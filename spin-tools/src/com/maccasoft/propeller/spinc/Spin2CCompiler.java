/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.SpinObject;
import com.maccasoft.propeller.SpinObject.CommentDataObject;
import com.maccasoft.propeller.SpinObject.DataObject;
import com.maccasoft.propeller.SpinObject.LinkDataObject;
import com.maccasoft.propeller.SpinObject.WordDataObject;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin2.Spin2Compiler;
import com.maccasoft.propeller.spin2.Spin2Debugger;
import com.maccasoft.propeller.spin2.Spin2Interpreter;
import com.maccasoft.propeller.spin2.Spin2Object;
import com.maccasoft.propeller.spin2.Spin2ObjectCompiler;
import com.maccasoft.propeller.spin2.Spin2PAsmDebugLine;
import com.maccasoft.propeller.spin2.Spin2StatementNode;

public class Spin2CCompiler extends Spin2Compiler {

    public Spin2CCompiler() {

    }

    public Spin2CCompiler(boolean caseSensitive) {
        super(caseSensitive);
    }

    @Override
    public Spin2Object compile(File file) throws Exception {
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

        return object;
    }

    @Override
    protected Spin2Object compileObject(File rootFile, Node root) {
        Spin2CObjectCompiler objectCompiler = new Spin2CObjectCompiler(this, rootFile);
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
    public Spin2Object generateDebugData() {
        Spin2Object object = new Spin2Object();
        object.writeComment("Debug data");
        WordDataObject sizeWord = object.writeWord(2);

        int index = 1;
        int pos = (debugStatements.size() + 1) * 2;
        List<DataObject> l = new ArrayList<DataObject>();
        for (Object node : debugStatements) {
            try {
                List<DataObject> list = null;
                if (node instanceof Spin2StatementNode) {
                    list = debug.compileDebugStatement((Spin2StatementNode) node);
                }
                else if (node instanceof Spin2PAsmDebugLine) {
                    list = debug.compilePAsmDebugStatement((Spin2PAsmDebugLine) node);
                }
                if (list != null) {
                    l.add(new CommentDataObject(String.format("#%d", index++)));
                    object.writeWord(pos);
                    for (DataObject o : list) {
                        l.add(o);
                        pos += o.size();
                    }
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
    public ObjectInfo getObjectInfo(ObjectCompiler parent, File file, Map<String, Expression> parameters) throws Exception {
        Node objectRoot = getParsedSource(file);

        ObjectCompiler objectCompiler;
        if (file.getName().toLowerCase().endsWith(".spin2")) {
            objectCompiler = new Spin2ObjectCompiler(this, parent, file);
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

        int index = childObjects.indexOf(info);
        if (index != -1) {
            info = childObjects.remove(index);
        }
        childObjects.add(info);
        objectCompiler.compileStep1(objectRoot);

        return info;
    }

}
