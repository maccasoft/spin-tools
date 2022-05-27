/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
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
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.maccasoft.propeller.Compiler;
import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.spin2.Spin2Object.LinkDataObject;
import com.maccasoft.propeller.spin2.Spin2ObjectCompiler.ObjectInfo;

public class Spin2Compiler extends Compiler {

    Spin2Context scope = new Spin2GlobalContext();
    Map<String, ObjectInfo> childObjects = new HashMap<String, ObjectInfo>();

    boolean errors;
    List<CompilerException> messages = new ArrayList<CompilerException>();

    boolean debugEnabled;
    List<Object> debugStatements = new ArrayList<Object>();

    boolean removeUnusedMethods;
    Spin2Preprocessor preprocessor;

    List<String> tree = new ArrayList<String>();

    Spin2Interpreter interpreter = new Spin2Interpreter();

    public Spin2Compiler() {

    }

    @Override
    public void setRemoveUnusedMethods(boolean removeUnusedMethods) {
        this.removeUnusedMethods = removeUnusedMethods;
    }

    @Override
    public void setDebugEnabled(boolean enabled) {
        this.debugEnabled = enabled;
    }

    @Override
    public void compile(File file, OutputStream binary, PrintStream listing) throws Exception {
        String text = getSource(file.getAbsolutePath());
        if (text == null) {
            throw new FileNotFoundException();
        }
        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Spin2Object object = compile(file.getName(), parser.parse());

        if (hasErrors()) {
            throw new CompilerException(messages);
        }

        if (listing != null) {
            object.generateListing(listing);
        }
        if (binary != null) {
            object.generateBinary(binary);
        }
    }

    @Override
    public Spin2Object compile(String rootFileName, Node root) {
        Spin2Object obj = compileObject(rootFileName, root);

        for (Entry<String, Expression> entry : obj.getSymbols().entrySet()) {
            if (entry.getValue() instanceof Method) {
                interpreter.setVBase((interpreter.getPBase() + obj.getSize()) | (obj.links.size() << 21));
                interpreter.setDBase(interpreter.getPBase() + obj.getSize() + obj.getVarSize());
                interpreter.setClearLongs(255 + ((obj.getVarSize() + 3) / 4));
                obj.setInterpreter(interpreter);
                break;
            }
        }

        if (debugEnabled) {
            obj.setDebugger(new Spin2Debugger());
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
            tree.add(fileName);
        }

        public ObjectNodeVisitor(ObjectNodeVisitor parent, String fileName, ListOrderedMap<String, Node> list) {
            this.parent = parent;
            this.fileName = fileName;
            this.list = list;

            StringBuilder sb = new StringBuilder();

            ObjectNodeVisitor o = parent.parent;
            while (o != null) {
                o = o.parent;
                sb.append("    ");
            }
            for (int i = tree.size() - 1; i >= 0; i--) {
                char[] s = tree.get(i).toCharArray();
                if (s[sb.length()] != ' ') {
                    break;
                }
                s[sb.length()] = '|';
                tree.set(i, new String(s));
            }
            sb.append("+-- " + fileName);
            tree.add(sb.toString());
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
                    throw new CompilerException(fileName, "\"" + objectFileName + "\" illegal circular reference", node.file);
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

        public Spin2ObjectCompilerProxy(String fileName, Spin2Context scope, Map<String, ObjectInfo> childObjects, boolean debugEnabled, List<Object> debugStatements) {
            super(scope, childObjects, debugEnabled, debugStatements);
            this.fileName = fileName;
        }

        @Override
        protected boolean isReferenced(MethodNode node) {
            if (!preprocessor.isReferenced(node)) {
                if (removeUnusedMethods) {
                    return false;
                }
            }
            return true;
        }

        @Override
        protected byte[] getBinaryFile(String fileName) {
            return Spin2Compiler.this.getBinaryFile(fileName);
        }

        @Override
        protected void logMessage(CompilerException message) {
            message.fileName = fileName;
            if (message.hasChilds()) {
                for (CompilerException msg : message.getChilds()) {
                    msg.fileName = fileName;
                    Spin2Compiler.this.logMessage(msg);
                }
            }
            else {
                Spin2Compiler.this.logMessage(message);
            }
            super.logMessage(message);
        }

    }

    public Spin2Object compileObject(String rootFileName, Node root) {
        ListOrderedMap<String, Node> objects = ListOrderedMap.listOrderedMap(new HashMap<String, Node>());

        root.accept(new ObjectNodeVisitor(rootFileName, objects));

        preprocessor = new Spin2Preprocessor(root, objects);
        preprocessor.collectReferencedMethods();
        preprocessor.removeUnusedMethods();

        for (Entry<String, Node> entry : objects.entrySet()) {
            Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompilerProxy(entry.getKey(), scope, childObjects, debugEnabled, debugStatements);
            objectCompiler.compile(entry.getValue());
            childObjects.put(entry.getKey(), new ObjectInfo(entry.getKey(), objectCompiler));
        }

        Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompilerProxy(rootFileName, scope, childObjects, debugEnabled, debugStatements);
        objectCompiler.compile(root);

        int memoryOffset = 0;
        for (Entry<String, Expression> entry : objectCompiler.getPublicSymbols().entrySet()) {
            if (entry.getValue() instanceof Method) {
                memoryOffset = interpreter.getSize();
                break;
            }
        }

        Spin2Object object = objectCompiler.generateObject(memoryOffset);
        memoryOffset += object.getSize();

        for (int i = objects.size() - 1; i >= 0; i--) {
            String fileName = objects.get(i);
            ObjectInfo info = childObjects.get(fileName);
            info.offset = object.getSize();
            Spin2Object linkedObject = info.compiler.generateObject(memoryOffset);
            memoryOffset += linkedObject.getSize();
            linkedObject.getObject(0).setText("Object \"" + fileName + "\" header (var size " + linkedObject.getVarSize() + ")");
            object.writeObject(linkedObject);
        }

        for (ObjectInfo info : childObjects.values()) {
            for (LinkDataObject linkData : info.compiler.getObjectLinks()) {
                for (ObjectInfo info2 : childObjects.values()) {
                    if (info2.compiler == linkData.object.compiler) {
                        linkData.setOffset(info2.offset - info.offset);
                        linkData.setText(String.format("Object \"%s\" @ $%05X", info2.fileName, linkData.getOffset()));
                        break;
                    }
                }
            }
        }

        for (LinkDataObject linkData : object.links) {
            for (ObjectInfo info : childObjects.values()) {
                if (info.compiler == linkData.object.compiler) {
                    linkData.setOffset(info.offset);
                    linkData.setText(String.format("Object \"%s\" @ $%05X", info.fileName, linkData.getOffset()));
                    break;
                }
            }
        }

        if (debugEnabled) {
            Spin2Object debugObject = objectCompiler.generateDebugData();
            object.setDebugData(debugObject);
        }

        return object;

    }

    protected Node getParsedObject(String fileName) {
        Node node = getParsedSource(fileName);
        if (node == null) {
            String text = getSource(fileName);
            if (text != null) {
                Spin2TokenStream stream = new Spin2TokenStream(text);
                Spin2Parser parser = new Spin2Parser(stream);
                node = parser.parse();
            }
        }
        return node;
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

    @Override
    public String getObjectTree() {
        StringBuilder sb = new StringBuilder();
        tree.forEach((s) -> {
            if (sb.length() > 0) {
                sb.append(System.lineSeparator());
            }
            sb.append(s);
        });
        return sb.toString();
    }

}
