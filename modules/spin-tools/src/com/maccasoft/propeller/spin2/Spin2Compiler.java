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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.collections4.map.ListOrderedMap;

import com.maccasoft.propeller.Compiler;
import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin2.Spin2Object.LinkDataObject;
import com.maccasoft.propeller.spin2.Spin2ObjectCompiler.ObjectInfo;

public class Spin2Compiler extends Compiler {

    final Spin2Context scope;
    Map<String, ObjectInfo> childObjects = new CaseInsensitiveMap<>();

    boolean errors;
    List<CompilerException> messages = new ArrayList<CompilerException>();

    boolean debugEnabled;
    List<Object> debugStatements = new ArrayList<Object>();

    boolean removeUnusedMethods;
    Spin2Preprocessor preprocessor;

    Spin2Interpreter interpreter;

    public Spin2Compiler() {
        scope = new Spin2GlobalContext();
    }

    public Spin2Compiler(boolean caseSensitive) {
        scope = new Spin2GlobalContext(caseSensitive);
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

    Spin2Object compileObject(File rootFile, Node root) {
        preprocessor = new Spin2Preprocessor() {

            @Override
            protected File getFile(String name) {
                return Spin2Compiler.this.getFile(name);
            }

            @Override
            protected Node getParsedObject(String fileName) {
                return Spin2Compiler.this.getParsedObject(fileName);
            }

        };
        preprocessor.process(rootFile, root);
        preprocessor.removeUnusedMethods();

        ListOrderedMap<String, Node> objects = preprocessor.getObjects();

        for (Entry<String, Node> entry : objects.entrySet()) {
            Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompilerProxy(entry.getKey(), scope, childObjects, debugEnabled, debugStatements);
            objectCompiler.compile(entry.getValue());
            childObjects.put(entry.getKey(), new ObjectInfo(entry.getKey(), objectCompiler));
        }

        Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompilerProxy(rootFile.getName(), scope, childObjects, debugEnabled, debugStatements);
        objectCompiler.compile(root);

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
                    if (info2.compiler == linkData.object) {
                        linkData.setOffset(info2.offset - info.offset);
                        linkData.setText(String.format("Object \"%s\" @ $%05X", info2.fileName, linkData.getOffset()));
                        break;
                    }
                }
            }
        }

        for (LinkDataObject linkData : objectCompiler.getObjectLinks()) {
            for (ObjectInfo info : childObjects.values()) {
                if (info.compiler == linkData.object) {
                    linkData.setOffset(info.offset);
                    linkData.setText(String.format("Object \"%s\" @ $%05X", info.fileName, linkData.getOffset()));
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

}
