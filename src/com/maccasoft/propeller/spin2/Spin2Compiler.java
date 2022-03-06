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

import org.apache.commons.collections4.map.ListOrderedMap;

import com.maccasoft.propeller.CompilerMessage;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.model.MethodNode;
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

    boolean removeUnusedMethods;
    Spin2Preprocessor preprocessor;

    Spin2Object object = new Spin2Object();

    public Spin2Compiler() {

    }

    public void setRemoveUnusedMethods(boolean removeUnusedMethods) {
        this.removeUnusedMethods = removeUnusedMethods;
    }

    public Spin2Object compile(String rootFileName, Node root) {
        Spin2Object obj = compileObject(rootFileName, root);

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

    class ObjectNodeVisitor extends NodeVisitor {

        ObjectNodeVisitor parent;
        String fileName;
        ListOrderedMap<String, Node> list;

        public ObjectNodeVisitor(String fileName, ListOrderedMap<String, Node> list) {
            this.fileName = fileName;
            this.list = list;
        }

        public ObjectNodeVisitor(ObjectNodeVisitor parent, String fileName, ListOrderedMap<String, Node> list) {
            this.parent = parent;
            this.fileName = fileName;
            this.list = list;
        }

        @Override
        public void visitObject(ObjectNode node) {
            if (node.name == null || node.file == null) {
                return;
            }
            String objectName = node.file.getText().substring(1, node.file.getText().length() - 1);
            String objectFileName = objectName + ".spin2";

            ObjectNodeVisitor p = parent;
            while (p != null) {
                if (p.fileName.equals(objectFileName)) {
                    throw new CompilerMessage(fileName, "\"" + objectFileName + "\" illegal circular reference", node.file);
                }
                p = p.parent;
            }

            Node objectRoot = list.get(objectFileName);
            if (objectRoot == null) {
                objectRoot = getParsedObject(objectFileName);
            }
            if (objectRoot == null) {
                //logMessage(new CompilerMessage(fileName, "object \"" + objectName + "\" not found", node.file));
                return;
            }

            list.remove(objectFileName);
            list.put(0, objectFileName, objectRoot);

            objectRoot.accept(new ObjectNodeVisitor(this, objectFileName, list));
        }

    }

    class Spin2ObjectCompilerProxy extends Spin2ObjectCompiler {

        String fileName;

        public Spin2ObjectCompilerProxy(String fileName, Spin2Context scope, Map<String, ObjectInfo> childObjects) {
            super(scope, childObjects);
            this.fileName = fileName;
        }

        @Override
        Spin2Method compileMethod(MethodNode node) {
            if (!preprocessor.isReferenced(node)) {
                if ("PRI".equalsIgnoreCase(node.type.getText())) {
                    logMessage(new CompilerMessage(CompilerMessage.WARNING, "function \"" + node.name.getText() + "\" is not used", node.name));
                }
                if (removeUnusedMethods) {
                    return null;
                }
            }
            return super.compileMethod(node);
        }

        @Override
        protected byte[] getBinaryFile(String fileName) {
            return Spin2Compiler.this.getBinaryFile(fileName);
        }

        @Override
        protected void logMessage(CompilerMessage message) {
            message.fileName = fileName;
            Spin2Compiler.this.logMessage(message);
        }

    }

    public Spin2Object compileObject(String rootFileName, Node root) {
        ListOrderedMap<String, Node> objects = ListOrderedMap.listOrderedMap(new HashMap<String, Node>());

        root.accept(new ObjectNodeVisitor(rootFileName, objects));

        preprocessor = new Spin2Preprocessor(root, objects);
        preprocessor.collectReferencedMethods();
        preprocessor.removeUnusedMethods();

        for (Entry<String, Node> entry : objects.entrySet()) {
            Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompilerProxy(entry.getKey(), scope, childObjects);
            Spin2Object object = objectCompiler.compileObject(entry.getValue());
            childObjects.put(entry.getKey(), new ObjectInfo(entry.getKey(), object));
        }

        Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompilerProxy(rootFileName, scope, childObjects);
        Spin2Object object = objectCompiler.compileObject(root);

        for (int i = objects.size() - 1; i >= 0; i--) {
            String fileName = objects.get(i);
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
