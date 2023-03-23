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
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.SpinObject;
import com.maccasoft.propeller.SpinObject.LinkDataObject;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin2.Spin2Compiler;
import com.maccasoft.propeller.spin2.Spin2Interpreter;
import com.maccasoft.propeller.spin2.Spin2Object;
import com.maccasoft.propeller.spin2.Spin2ObjectCompiler;
import com.maccasoft.propeller.spin2.Spin2Preprocessor;

public class Spin2CCompiler extends Spin2Compiler {

    Spin2Preprocessor preprocessor;

    public Spin2CCompiler() {

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

        public Spin2CObjectCompilerProxy(String fileName, List<Object> debugStatements) {
            super(Spin2CCompiler.this, debugStatements);
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
                    msg.fileName = fileName;
                    Spin2CCompiler.this.logMessage(msg);
                }
            }
            else {
                Spin2CCompiler.this.logMessage(message);
            }
            super.logMessage(message);
        }

    }

    class Spin2ObjectCompilerProxy extends Spin2ObjectCompiler {

        String fileName;

        public Spin2ObjectCompilerProxy(String fileName, List<Object> debugStatements) {
            super(Spin2CCompiler.this, debugStatements);
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
                    msg.fileName = fileName;
                    Spin2CCompiler.this.logMessage(msg);
                }
            }
            else {
                Spin2CCompiler.this.logMessage(message);
            }
            super.logMessage(message);
        }

    }

    @Override
    protected Spin2Object compileObject(File rootFile, Node root) {
        preprocessor = new Spin2Preprocessor(this);
        preprocessor.process(rootFile, root);

        ListOrderedMap<File, Node> objects = preprocessor.getObjects();

        for (Entry<File, Node> entry : objects.entrySet()) {
            String fileName = entry.getKey().getName();

            ObjectCompiler objectCompiler;
            if (fileName.toLowerCase().endsWith(".spin2")) {
                objectCompiler = new Spin2ObjectCompilerProxy(fileName, debugStatements);
            }
            else {
                objectCompiler = new Spin2CObjectCompilerProxy(fileName, debugStatements);
            }
            objectCompiler.compile(entry.getValue());
            childObjects.put(entry.getKey(), new ObjectInfo(objectCompiler));
        }

        Spin2CObjectCompiler objectCompiler = new Spin2CObjectCompilerProxy(rootFile.getName(), debugStatements);
        objectCompiler.compile(root);

        objectCompiler.compilePass2();
        for (int i = objects.size() - 1; i >= 0; i--) {
            File file = objects.get(i);
            ObjectInfo info = childObjects.get(file);
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

        for (int i = objects.size() - 1; i >= 0; i--) {
            File file = objects.get(i);
            ObjectInfo info = childObjects.get(file);
            info.offset = object.getSize();
            SpinObject linkedObject = info.compiler.generateObject(memoryOffset);
            memoryOffset += linkedObject.getSize();
            linkedObject.getObject(0).setText("Object \"" + file.getName() + "\" header (var size " + linkedObject.getVarSize() + ")");
            object.writeObject(linkedObject);
        }

        for (ObjectInfo info : childObjects.values()) {
            for (LinkDataObject linkData : info.compiler.getObjectLinks()) {
                for (Entry<File, ObjectInfo> entry : childObjects.entrySet()) {
                    ObjectInfo info2 = entry.getValue();
                    if (linkData.isObject(info2.compiler)) {
                        linkData.setOffset(info2.offset - info.offset);
                        linkData.setText(String.format("Object \"%s\" @ $%05X", entry.getKey().getName(), linkData.getOffset()));
                        break;
                    }
                }
            }
        }

        for (LinkDataObject linkData : objectCompiler.getObjectLinks()) {
            for (Entry<File, ObjectInfo> entry : childObjects.entrySet()) {
                ObjectInfo info = entry.getValue();
                if (linkData.isObject(info.compiler)) {
                    linkData.setOffset(info.offset);
                    linkData.setText(String.format("Object \"%s\" @ $%05X", entry.getKey().getName(), linkData.getOffset()));
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
            Spin2Object debugObject = objectCompiler.generateDebugData();
            object.setDebugData(debugObject);
        }

        tree = preprocessor.getObjectTree();

        return object;

    }

    @Override
    public ObjectInfo getObjectInfo(String fileName) {
        File file = getFile(fileName + ".c");
        if (file == null) {
            file = getFile(fileName + ".spin2");
        }
        if (file == null) {
            file = getFile(fileName);
        }
        return childObjects.get(file);
    }

}
