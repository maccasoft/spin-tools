/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.maccasoft.propeller.Compiler;
import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.SpinObject;
import com.maccasoft.propeller.SpinObject.CommentDataObject;
import com.maccasoft.propeller.SpinObject.DataObject;
import com.maccasoft.propeller.SpinObject.LinkDataObject;
import com.maccasoft.propeller.SpinObject.WordDataObject;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.model.RootNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenStream;
import com.maccasoft.propeller.spin2.Spin2Debug.DebugDataObject;
import com.maccasoft.propeller.spinc.Spin2CObjectCompiler;

public class Spin2Compiler extends Compiler {

    protected boolean debugEnabled;
    protected List<DebugDataObject> debugStatements = new ArrayList<>();
    protected Spin2Debug debug = new Spin2Debug();

    protected Spin2Interpreter interpreter;
    protected Spin2Debugger debugger;

    protected List<ObjectInfo> childObjects = new ArrayList<>();

    protected boolean errors;
    protected List<CompilerException> messages = new ArrayList<CompilerException>();

    boolean compress;

    public Spin2Compiler() {

    }

    public Spin2Compiler(boolean caseSensitive) {
        super(caseSensitive);
    }

    @Override
    public void setDebugEnabled(boolean enabled) {
        this.debugEnabled = enabled;
    }

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    @Override
    public void addDefine(String identifier, String value) {
        Token token;

        List<Token> list = new ArrayList<>();
        TokenStream stream = new Spin2TokenStream(value);
        while ((token = stream.nextToken()).type != Token.EOF) {
            list.add(token);
        }
        addDefine(identifier, list);
    }

    @Override
    public Spin2Object compile(File file) throws Exception {
        String text = getSource(file.getAbsolutePath());
        if (text == null) {
            throw new FileNotFoundException();
        }
        Spin2Parser parser = new Spin2Parser(text);
        Spin2Object object = compile(file, parser.parse());

        if (hasErrors()) {
            throw new CompilerException(messages);
        }

        return object;
    }

    @Override
    public Spin2Object compile(File rootFile, RootNode root) {
        Spin2Object obj = compileObject(rootFile, root);

        if (interpreter != null) {
            obj.setInterpreter(interpreter);
        }
        if (debugger != null) {
            obj.setDebugger(debugger);
        }
        obj.setCompress(compress);

        return obj;
    }

    protected Spin2Object compileObject(File rootFile, RootNode root) {
        Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompiler(this, rootFile);
        objectCompiler.compileStep1(root);

        objectCompiler.compileStep2(true);
        for (ObjectInfo info : childObjects) {
            info.compiler.compileStep2(false);
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
        object.getObject(0).setText("Object \"" + rootFile.getName() + "\" header (var size " + object.getVarSize() + ")");
        memoryOffset += object.getSize();

        for (ObjectInfo info : childObjects) {
            info.offset = object.getSize();
            SpinObject linkedObject = info.compiler.generateObject(memoryOffset);
            linkedObject.getObject(0).setText("Object \"" + info.file.getName() + "\" header (var size " + linkedObject.getVarSize() + ")");
            object.writeObject(linkedObject);
            memoryOffset += linkedObject.getSize();
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

        Spin2Object debugObject = generateDebugData();

        int stackFree = 512 * 1024;

        if (interpreter != null) {
            interpreter.setVBase((interpreter.getPBase() + object.getSize()) | (objectCompiler.getObjectLinks().size() << 21));
            interpreter.setDBase(interpreter.getPBase() + object.getSize() + object.getVarSize());
            interpreter.setClearLongs(255 + ((object.getVarSize() + 3) / 4));
            stackFree -= interpreter.getDBase();
        }

        if (debugEnabled) {
            debugger = new Spin2Debugger();
            object.setDebugData(debugObject);
            stackFree -= debugger.getSize() + debugObject.getSize();
        }

        tree = buildFrom(objectCompiler);

        errors = objectCompiler.hasErrors();
        if (stackFree < 0) {
            logMessage(new CompilerException(rootFile.getName(), "program exceeds runtime memory limit by " + Math.abs(stackFree) + " longs.", null));
        }

        messages.addAll(objectCompiler.getMessages());
        for (ObjectInfo info : childObjects) {
            messages.addAll(info.compiler.getMessages());
        }

        return object;

    }

    @Override
    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void addDebugStatement(DebugDataObject statement) {
        if (!debugStatements.contains(statement)) {
            debugStatements.add(statement);
        }
    }

    public int getDebugStatementIndex(DebugDataObject statement) {
        return debugStatements.indexOf(statement) + 1;
    }

    public Spin2Object generateDebugData() {
        Spin2Object object = new Spin2Object();
        object.writeComment("Debug data");

        WordDataObject sizeWord = object.writeWord(2);

        int index = 1;
        int pos = (debugStatements.size() + 1) * 2;

        for (DebugDataObject data : debugStatements) {
            object.writeWord(pos, String.format("#%d@%04X", index++, pos));
            pos += data.getSize();
        }

        index = 1;
        for (DebugDataObject data : debugStatements) {
            object.write(new CommentDataObject(String.format("#%d", index++)));
            for (DataObject d : data.getDataObjects()) {
                object.write(d);
            }
        }

        sizeWord.setValue(object.getSize());

        if (object.getSize() > 16384) {
            throw new CompilerException("debug data is too long", null);
        }

        return object;
    }

    @Override
    public ObjectInfo getObjectInfo(ObjectCompiler parent, File file, Map<String, Expression> parameters) throws Exception {
        RootNode objectRoot = getParsedSource(file);

        ObjectCompiler objectCompiler;
        if (file.getName().toLowerCase().endsWith(".c")) {
            objectCompiler = new Spin2CObjectCompiler(this, parent, file);
        }
        else {
            objectCompiler = new Spin2ObjectCompiler(this, parent, file, parameters);
        }

        while (parent != null) {
            if (file.equals(parent.getFile())) {
                throw new Exception("illegal circular reference");
            }
            parent = parent.getParent();
        }

        ObjectInfo info = new ObjectInfo(file, objectCompiler, parameters);
        info.root = objectRoot;

        int index = childObjects.indexOf(info);

        if (index != -1) {
            info = childObjects.remove(index);
        }
        childObjects.add(info);
        objectCompiler.compileStep1(objectRoot);

        return info;
    }

    public ObjectInfo getObjectInclude(String fileName, Map<String, Expression> parameters) {
        File objectFile = getFile(fileName, ".c", ".spin2");
        if (objectFile != null) {
            RootNode objectRoot = getParsedSource(objectFile);
            if (objectRoot != null) {
                ObjectCompiler objectCompiler;
                if (objectFile.getName().toLowerCase().endsWith(".c")) {
                    objectCompiler = new Spin2CObjectCompiler(this, objectFile);
                }
                else {
                    objectCompiler = new Spin2ObjectCompiler(this, objectFile);
                }
                ObjectInfo info = new ObjectInfo(objectFile, objectCompiler, parameters);
                int index = childObjects.indexOf(info);
                if (index != -1) {
                    return childObjects.get(index);
                }
                objectCompiler.compileStep1(objectRoot);
                return info;
            }
        }
        return null;
    }

    protected byte[] getBinaryFile(String fileName) {
        return getResource(fileName);
    }

    protected void logMessage(CompilerException message) {
        if (message.type == CompilerException.ERROR) {
            errors = true;
        }
        messages.add(message);
    }

    @Override
    public boolean hasErrors() {
        return errors;
    }

    @Override
    public List<CompilerException> getMessages() {
        return messages;
    }

}
