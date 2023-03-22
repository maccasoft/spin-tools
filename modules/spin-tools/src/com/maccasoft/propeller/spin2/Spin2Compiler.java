/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
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
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.SpinObject;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin2.Spin2Object.LinkDataObject;

public class Spin2Compiler extends Compiler {

    public static class ObjectInfo {

        public ObjectCompiler compiler;

        public long offset;
        public Expression count;

        public ObjectInfo(ObjectCompiler compiler) {
            this.compiler = compiler;
        }

        public ObjectInfo(ObjectCompiler compiler, Expression count) {
            this.compiler = compiler;
            this.count = count;
        }

        public boolean hasErrors() {
            return compiler.hasErrors();
        }

    }

    protected boolean removeUnusedMethods;

    protected boolean debugEnabled;
    protected List<Object> debugStatements = new ArrayList<Object>();

    protected Spin2Interpreter interpreter;

    Spin2Preprocessor preprocessor;
    protected Map<File, ObjectInfo> childObjects = new HashMap<>();

    boolean errors;
    List<CompilerException> messages = new ArrayList<CompilerException>();

    public Spin2Compiler() {

    }

    @Override
    public void setRemoveUnusedMethods(boolean removeUnusedMethods) {
        this.removeUnusedMethods = removeUnusedMethods;
    }

    public boolean isRemoveUnusedMethods() {
        return removeUnusedMethods;
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
        Spin2Object object = compile(file, parser.parse());

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
    public Spin2Object compile(File rootFile, Node root) {
        Spin2Object obj = compileObject(rootFile, root);

        if (interpreter != null) {
            obj.setInterpreter(interpreter);
        }

        if (debugEnabled) {
            obj.setDebugger(new Spin2Debugger());
        }

        return obj;
    }

    class Spin2ObjectCompilerProxy extends Spin2ObjectCompiler {

        String fileName;

        public Spin2ObjectCompilerProxy(String fileName, List<Object> debugStatements) {
            super(Spin2Compiler.this, debugStatements);
            this.fileName = fileName;
        }

        @Override
        protected Node getParsedSource(String fileName) {
            Node node = Spin2Compiler.this.getParsedObject(fileName);
            if (node == null) {
                node = Spin2Compiler.this.getParsedObject(fileName + ".spin2");
            }
            return node;
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

    protected Spin2Object compileObject(File rootFile, Node root) {
        preprocessor = new Spin2Preprocessor(this);
        preprocessor.process(rootFile, root);

        ListOrderedMap<File, Node> objects = preprocessor.getObjects();

        for (Entry<File, Node> entry : objects.entrySet()) {
            Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompilerProxy(entry.getKey().getName(), debugStatements);
            objectCompiler.compile(entry.getValue());
            childObjects.put(entry.getKey(), new ObjectInfo(objectCompiler));
        }

        Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompilerProxy(rootFile.getName(), debugStatements);
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
                    if (info2.compiler == linkData.object) {
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
                if (info.compiler == linkData.object) {
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

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public ObjectInfo getObjectInfo(String fileName) {
        File file = getFile(fileName);
        if (file == null) {
            file = getFile(fileName + ".spin2");
        }
        return childObjects.get(file);
    }

    public Node getParsedObject(String fileName) {
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

}
