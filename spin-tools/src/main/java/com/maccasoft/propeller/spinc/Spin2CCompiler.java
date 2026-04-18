/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.SpinObject.LinkDataObject;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.spin2.Spin2Compiler;
import com.maccasoft.propeller.spin2.Spin2Debugger;
import com.maccasoft.propeller.spin2.Spin2Interpreter;
import com.maccasoft.propeller.spin2.Spin2Object;
import com.maccasoft.propeller.spin2.Spin2ObjectCompiler;

public class Spin2CCompiler extends Spin2Compiler {

    Spin2CObjectCompiler objectCompiler;

    public Spin2CCompiler() {

    }

    public Spin2CCompiler(boolean caseSensitive) {
        super(caseSensitive);
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
        obj.setCompress(isCompress());

        return obj;
    }

    Spin2Object compileObject(File file, String text) {
        objectCompiler = new Spin2CObjectCompiler(this, file);
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

        for (ObjectInfo info : childObjects) {
            info.offset = object.getSize();
            info.object = info.compiler.generateObject(memoryOffset + object.getSize());
            object.writeObject(info.object);
        }

        for (ObjectInfo info : childObjects) {
            for (LinkDataObject linkData : info.object.getObjectLinks()) {
                for (ObjectInfo info2 : childObjects) {
                    if (linkData.isObjectCompiler(info2.compiler)) {
                        linkData.setOffset(info2.offset - info.offset);
                        info.object.addChildObject(info2.object);
                        break;
                    }
                }
            }
        }

        for (LinkDataObject linkData : object.getObjectLinks()) {
            for (ObjectInfo info : childObjects) {
                if (linkData.isObjectCompiler(info.compiler)) {
                    linkData.setOffset(info.offset);
                    object.addChildObject(info.object);
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
            interpreter.setVBase((interpreter.getPBase() + object.getSize()) | (object.getObjectLinks().size() << 21));
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

        return object;

    }

    @Override
    public Context getContext() {
        return objectCompiler.getScope();
    }

    @Override
    public ObjectInfo getObjectInfo(ObjectCompiler parent, File file, Map<String, Expression> parameters) throws Exception {
        ObjectCompiler objectCompiler;
        if (file.getName().toLowerCase().endsWith(".spin2")) {
            objectCompiler = new Spin2ObjectCompiler(this, parent, file, Collections.emptyMap());
        }
        else {
            objectCompiler = new Spin2CObjectCompiler(this, parent, file);
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

}
