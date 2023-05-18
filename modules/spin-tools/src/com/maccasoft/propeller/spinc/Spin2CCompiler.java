/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.SpinObject;
import com.maccasoft.propeller.SpinObject.DataObject;
import com.maccasoft.propeller.SpinObject.LinkDataObject;
import com.maccasoft.propeller.SpinObject.WordDataObject;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin2.Spin2Compiler;
import com.maccasoft.propeller.spin2.Spin2Interpreter;
import com.maccasoft.propeller.spin2.Spin2Object;
import com.maccasoft.propeller.spin2.Spin2ObjectCompiler;
import com.maccasoft.propeller.spin2.Spin2PAsmDebugLine;
import com.maccasoft.propeller.spin2.Spin2Preprocessor;
import com.maccasoft.propeller.spin2.Spin2StatementNode;

public class Spin2CCompiler extends Spin2Compiler {

    public Spin2CCompiler() {

    }

    public Spin2CCompiler(boolean caseSensitive) {
        super(caseSensitive);
    }

    @Override
    public void compile(File file, OutputStream binary, PrintStream listing) throws Exception {
        String text = getSource(file.getAbsolutePath());
        if (text == null) {
            throw new FileNotFoundException();
        }
        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Spin2Object object = compile(file, parser.parse());

        if (hasErrors()) {
            throw new CompilerException(getMessages());
        }

        if (listing != null) {
            object.generateListing(listing);
        }
        if (binary != null) {
            object.generateBinary(binary);
        }
    }

    class Spin2CObjectCompilerProxy extends Spin2CObjectCompiler {

        String fileName;

        public Spin2CObjectCompilerProxy(String fileName) {
            super(Spin2CCompiler.this);
            this.fileName = fileName;
        }

        @Override
        protected byte[] getBinaryFile(String fileName) {
            return Spin2CCompiler.this.getBinaryFile(fileName);
        }

        @Override
        protected void logMessage(CompilerException message) {
            message.fileName = fileName;
            if (message.hasChilds()) {
                for (CompilerException msg : message.getChilds()) {
                    logMessage(msg);
                }
            }
            else {
                Spin2CCompiler.this.logMessage(message);
                super.logMessage(message);
            }
        }

    }

    class Spin2ObjectCompilerProxy extends Spin2ObjectCompiler {

        String fileName;

        public Spin2ObjectCompilerProxy(String fileName) {
            super(Spin2CCompiler.this);
            this.fileName = fileName;
        }

        @Override
        protected byte[] getBinaryFile(String fileName) {
            return Spin2CCompiler.this.getBinaryFile(fileName);
        }

        @Override
        protected void logMessage(CompilerException message) {
            message.fileName = fileName;
            if (message.hasChilds()) {
                for (CompilerException msg : message.getChilds()) {
                    logMessage(msg);
                }
            }
            else {
                Spin2CCompiler.this.logMessage(message);
                super.logMessage(message);
            }
        }

    }

    @Override
    protected Spin2Object compileObject(File rootFile, Node root) {
        preprocessor = new Spin2Preprocessor(this);
        preprocessor.process(rootFile, root);

        Spin2CObjectCompiler objectCompiler = new Spin2CObjectCompilerProxy(rootFile.getName());
        objectCompiler.compileObject(root);

        objectCompiler.compilePass2();
        for (ObjectInfo info : childObjects) {
            info.compiler.compilePass2();
        }

        int memoryOffset = 0;
        for (Entry<String, Expression> entry : objectCompiler.getPublicSymbols().entrySet()) {
            if (entry.getValue() instanceof Method) {
                interpreter = new Spin2Interpreter();
                memoryOffset = interpreter.getSize();
                break;
            }
        }

        Spin2Object object = objectCompiler.generateObject(memoryOffset);
        memoryOffset += object.getSize();

        for (ObjectInfo info : childObjects) {
            info.offset = object.getSize();
            SpinObject linkedObject = info.compiler.generateObject(memoryOffset);
            memoryOffset += linkedObject.getSize();
            linkedObject.getObject(0).setText("Object \"" + info.file.getName() + "\" header (var size " + linkedObject.getVarSize() + ")");
            object.writeObject(linkedObject);
        }

        for (ObjectInfo info : childObjects) {
            for (LinkDataObject linkData : info.compiler.getObjectLinks()) {
                for (ObjectInfo info2 : childObjects) {
                    if (linkData.isObject(info2.compiler)) {
                        linkData.setOffset(info2.offset - info.offset);
                        linkData.setText(String.format("Object \"%s\" @ $%05X", info2.file.getName(), linkData.getOffset()));
                        break;
                    }
                }
            }
        }

        for (LinkDataObject linkData : objectCompiler.getObjectLinks()) {
            for (ObjectInfo info : childObjects) {
                if (linkData.isObject(info.compiler)) {
                    linkData.setOffset(info.offset);
                    linkData.setText(String.format("Object \"%s\" @ $%05X", info.file.getName(), linkData.getOffset()));
                    break;
                }
            }
        }

        if (interpreter != null) {
            interpreter.setVBase((interpreter.getPBase() + object.getSize()) | (objectCompiler.getObjectLinks().size() << 21));
            interpreter.setDBase(interpreter.getPBase() + object.getSize() + object.getVarSize());
            interpreter.setClearLongs(255 + ((object.getVarSize() + 3) / 4));
        }

        if (debugEnabled) {
            Spin2Object debugObject = generateDebugData();
            object.setDebugData(debugObject);
        }

        tree = preprocessor.getObjectTree();

        return object;

    }

    @Override
    public Spin2Object generateDebugData() {
        Spin2Object object = new Spin2Object();
        object.writeComment("Debug data");
        WordDataObject sizeWord = object.writeWord(2);

        int pos = (debugStatements.size() + 1) * 2;
        List<DataObject> l = new ArrayList<DataObject>();
        for (Object node : debugStatements) {
            try {
                if (node instanceof Spin2StatementNode) {
                    byte[] data = debug.compileDebugStatement((Spin2StatementNode) node);
                    l.add(new DataObject(data));
                    object.writeWord(pos);
                    pos += data.length;
                }
                else if (node instanceof Spin2PAsmDebugLine) {
                    byte[] data = debug.compilePAsmDebugStatement((Spin2PAsmDebugLine) node);
                    l.add(new DataObject(data));
                    object.writeWord(pos);
                    pos += data.length;
                }
            } catch (CompilerException e) {
                logMessage(e);
            } catch (Exception e) {
                logMessage(new CompilerException(e, node));
            }
        }
        for (DataObject data : l) {
            object.write(data);
        }
        sizeWord.setValue(object.getSize());

        if (object.getSize() > 16384) {
            throw new CompilerException("debug data is too long", null);
        }

        return object;
    }

    @Override
    public ObjectInfo getObjectInfo(String fileName, Map<String, Expression> parameters) {
        File objectFile = getFile(fileName, ".c", ".spin2");
        if (objectFile != null) {
            Node objectRoot = getParsedObject(objectFile.getName(), ".c", ".spin2");
            if (objectRoot != null) {
                ObjectCompiler objectCompiler;
                if (objectFile.getName().toLowerCase().endsWith(".c")) {
                    objectCompiler = new Spin2CObjectCompilerProxy(fileName);
                }
                else {
                    objectCompiler = new Spin2ObjectCompilerProxy(fileName);
                }
                ObjectInfo info = new ObjectInfo(objectFile, objectCompiler, parameters);
                int index = childObjects.indexOf(info);
                if (index != -1) {
                    info = childObjects.remove(index);
                }
                childObjects.add(info);
                objectCompiler.compileObject(objectRoot);
                return info;
            }
        }
        return null;
    }

}
