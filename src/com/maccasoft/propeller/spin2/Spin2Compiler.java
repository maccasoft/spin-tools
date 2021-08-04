/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.maccasoft.propeller.CompilerMessage;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.spin2.Spin2Object.LinkDataObject;
import com.maccasoft.propeller.spin2.Spin2ObjectCompiler.ObjectInfo;

public class Spin2Compiler {

    Spin2Context scope = new Spin2GlobalContext();
    Map<String, ObjectInfo> childObjects = new HashMap<String, ObjectInfo>();

    boolean errors;
    List<CompilerMessage> messages = new ArrayList<CompilerMessage>();

    Spin2Object object = new Spin2Object();

    public Spin2Compiler() {

    }

    public Spin2Object compile(Node root) {
        Spin2Object obj = compileObject(root);

        for (Entry<String, Expression> entry : obj.getSymbols().entrySet()) {
            if (entry.getValue() instanceof Method) {
                Spin2Interpreter interpreter = new Spin2Interpreter();
                interpreter.setVBase((interpreter.getPBase() + obj.getSize()) | (obj.links.size() << 21));
                interpreter.setDBase(interpreter.getPBase() + obj.getSize() + obj.getVarSize());
                interpreter.setClearLongs(255 + ((obj.getVarSize() + 3) / 4));
                obj.setInterpreter(interpreter);
                break;
            }
        }

        return obj;
    }

    public Spin2Object compileObject(Node root) {
        List<String> list = new ArrayList<String>();
        Map<String, Node> map = new HashMap<String, Node>();
        NodeVisitor objectNodeVisitor = new NodeVisitor() {

            @Override
            public void visitObject(ObjectNode node) {
                if (node.name == null || node.file == null) {
                    return;
                }
                String fileName = node.file.getText().substring(0, node.file.getText().length() - 1).substring(1);
                list.remove(fileName);
                list.add(0, fileName);

                Node objectRoot = map.get(fileName);
                if (objectRoot == null) {
                    objectRoot = getParsedObject(fileName);
                }
                if (objectRoot == null) {
                    logMessage(new CompilerMessage("Object file " + fileName + " not found", node));
                    return;
                }
                map.put(fileName, objectRoot);
                objectRoot.accept(this);
            }

        };
        root.accept(objectNodeVisitor);

        for (String fileName : list) {
            Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompiler(scope, childObjects) {

                @Override
                protected byte[] getBinaryFile(String fileName) {
                    return Spin2Compiler.this.getBinaryFile(fileName);
                }

            };
            Spin2Object object = objectCompiler.compileObject(map.get(fileName));
            childObjects.put(fileName, new ObjectInfo(fileName, object));
        }

        Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompiler(scope, childObjects) {

            @Override
            protected byte[] getBinaryFile(String fileName) {
                return Spin2Compiler.this.getBinaryFile(fileName);
            }

        };
        Spin2Object object = objectCompiler.compileObject(root);

        for (int i = list.size() - 1; i >= 0; i--) {
            String fileName = list.get(i);
            ObjectInfo info = childObjects.get(fileName);
            info.offset = object.getSize();
            info.object.getObject(0).setText("Object \"" + fileName + "\" header (var size " + info.object.getVarSize() + ")");
            object.writeObject(info.object);
        }

        for (ObjectInfo info : childObjects.values()) {
            for (LinkDataObject linkData : info.object.links) {
                for (ObjectInfo info2 : childObjects.values()) {
                    if (info2.object == linkData.object) {
                        linkData.setOffset(info2.offset - info.offset);
                        linkData.setText(String.format("Object \"%s\" @ $%05X", info2.fileName, linkData.getOffset()));
                        break;
                    }
                }
            }
        }

        for (LinkDataObject linkData : object.links) {
            for (ObjectInfo info : childObjects.values()) {
                if (info.object == linkData.object) {
                    linkData.setOffset(info.offset);
                    linkData.setText(String.format("Object \"%s\" @ $%05X", info.fileName, linkData.getOffset()));
                    break;
                }
            }
        }

        return object;

    }

    protected Node getParsedObject(String fileName) {
        return null;
    }

    protected byte[] getBinaryFile(String fileName) {
        return null;
    }

    protected void logMessage(CompilerMessage message) {
        if (message.type == CompilerMessage.ERROR) {
            errors = true;
        }
        messages.add(message);
    }

    public boolean hasErrors() {
        return errors;
    }

    public List<CompilerMessage> getMessages() {
        return messages;
    }

}
