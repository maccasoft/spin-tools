/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.File;
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
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenStream;
import com.maccasoft.propeller.spin2.Spin2Debug.DebugDataObject;
import com.maccasoft.propeller.spinc.Spin2CObjectCompiler;

public class Spin2Compiler extends Compiler {

    protected List<DebugDataObject> debugStatements = new ArrayList<>();
    protected Spin2Debug debug = new Spin2Debug();

    protected Spin2Interpreter interpreter;
    protected Spin2Debugger debugger;

    protected List<ObjectInfo> childObjects = new ArrayList<>();

    boolean compress;
    Spin2ObjectCompiler objectCompiler;

    public Spin2Compiler() {

    }

    public Spin2Compiler(boolean caseSensitive) {
        super(caseSensitive);
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
    public Spin2Object compile(File file, String text) {
        Spin2Object obj = compileObject(file, text);

        if (interpreter != null) {
            obj.setInterpreter(interpreter);
        }
        if (debugger != null) {
            obj.setDebugger(debugger);
        }
        obj.setCompress(compress);

        return obj;
    }

    Spin2Object compileObject(File file, String text) {
        objectCompiler = new Spin2ObjectCompiler(this, file);
        root = objectCompiler.compileStep1(text);

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
        object.getObject(0).setText("Object \"" + file.getName() + "\" header (var size " + object.getVarSize() + ")");
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

        messages.addAll(objectCompiler.getMessages());
        for (ObjectInfo info : childObjects) {
            messages.addAll(info.compiler.getMessages());
        }

        errors = objectCompiler.hasErrors();
        for (ObjectInfo info : childObjects) {
            errors |= info.compiler.hasErrors();
        }

        int stackFree = 512 * 1024;

        if (interpreter != null) {
            interpreter.setVBase((interpreter.getPBase() + object.getSize()) | (objectCompiler.getObjectLinks().size() << 21));
            interpreter.setDBase(interpreter.getPBase() + object.getSize() + object.getVarSize());
            interpreter.setClearLongs(255 + ((object.getVarSize() + 3) / 4));
            stackFree -= interpreter.getDBase();
        }

        if (isDebugEnabled()) {
            debugger = new Spin2Debugger();
            try {
                Spin2Object debugObject = generateDebugData();
                object.setDebugData(debugObject);
                stackFree -= debugger.getSize() + debugObject.getSize();
            } catch (Exception e) {
                logMessage(new CompilerException(file, e.getMessage(), null));
            }
        }

        if (stackFree < 0) {
            logMessage(new CompilerException(file, "program exceeds runtime memory limit by " + Math.abs(stackFree) + " bytes.", null));
        }

        tree = buildFrom(objectCompiler);

        return object;

    }

    @Override
    public Context getContext() {
        return objectCompiler.getScope();
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
            throw new RuntimeException("debug data exceeds memory limit by " + (object.getSize() - 16384) + " bytes");
        }

        return object;
    }

    @Override
    public ObjectInfo getObjectInfo(ObjectCompiler parent, File file, Map<String, Expression> parameters) throws Exception {
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
        info.text = getSource(file);

        int index = childObjects.indexOf(info);
        if (index != -1) {
            info = childObjects.remove(index);
        }
        childObjects.add(info);

        info.root = objectCompiler.compileStep1(info.text);

        return info;
    }

    public ObjectInfo getObjectInclude(String fileName, Map<String, Expression> parameters) {
        File objectFile = getFile(fileName, ".c", ".spin2");
        if (objectFile != null) {
            String text = getSource(objectFile);
            if (text != null) {
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
                objectCompiler.compileStep1(text);
                return info;
            }
        }
        return null;
    }

}
