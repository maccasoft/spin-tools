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
import java.util.Map.Entry;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.SpinObject;
import com.maccasoft.propeller.SpinObject.LinkDataObject;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin1.Spin1Compiler;
import com.maccasoft.propeller.spin1.Spin1Object;
import com.maccasoft.propeller.spin1.Spin1ObjectCompiler;
import com.maccasoft.propeller.spin1.Spin1Preprocessor;

public class Spin1CCompiler extends Spin1Compiler {

    public Spin1CCompiler() {

    }

    public Spin1CCompiler(boolean caseSensitive) {
        super(caseSensitive);
    }

    @Override
    public void compile(File file, OutputStream binary, PrintStream listing) throws Exception {
        String text = getSource(file.getAbsolutePath());
        if (text == null) {
            throw new FileNotFoundException();
        }
        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Spin1Object object = compile(file, parser.parse());

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

    class Spin1CObjectCompilerProxy extends Spin1CObjectCompiler {

        String fileName;

        public Spin1CObjectCompilerProxy(String fileName) {
            super(Spin1CCompiler.this);
            this.fileName = fileName;
        }

        @Override
        protected byte[] getBinaryFile(String fileName) {
            return Spin1CCompiler.this.getBinaryFile(fileName);
        }

        @Override
        protected void logMessage(CompilerException message) {
            message.fileName = fileName;
            if (message.hasChilds()) {
                for (CompilerException msg : message.getChilds()) {
                    logMessage(msg);
                }
            }
            else {
                Spin1CCompiler.this.logMessage(message);
                super.logMessage(message);
            }
        }

    }

    class Spin1ObjectCompilerProxy extends Spin1ObjectCompiler {

        String fileName;

        public Spin1ObjectCompilerProxy(String fileName) {
            super(Spin1CCompiler.this);
            this.fileName = fileName;
        }

        @Override
        protected byte[] getBinaryFile(String fileName) {
            return Spin1CCompiler.this.getBinaryFile(fileName);
        }

        @Override
        protected void logMessage(CompilerException message) {
            message.fileName = fileName;
            if (message.hasChilds()) {
                for (CompilerException msg : message.getChilds()) {
                    logMessage(msg);
                }
            }
            else {
                Spin1CCompiler.this.logMessage(message);
                super.logMessage(message);
            }
        }

    }

    @Override
    public Spin1Object compileObject(File rootFile, Node root) {
        int memoryOffset = 16;

        preprocessor = new Spin1Preprocessor(this);
        preprocessor.process(rootFile, root);

        ListOrderedMap<File, Node> objects = preprocessor.getObjects();

        for (Entry<File, Node> entry : objects.entrySet()) {
            String fileName = entry.getKey().getName();

            ObjectCompiler objectCompiler;
            if (fileName.toLowerCase().endsWith(".spin")) {
                objectCompiler = new Spin1ObjectCompilerProxy(fileName);
            }
            else {
                objectCompiler = new Spin1CObjectCompilerProxy(fileName);
            }
            objectCompiler.compileObject(entry.getValue());
            childObjects.put(entry.getKey(), new ObjectInfo(objectCompiler));
        }

        for (Entry<File, Node> entry : preprocessor.getIncludedObjects().entrySet()) {
            if (!childObjects.containsKey(entry.getKey())) {
                String fileName = entry.getKey().getName();

                ObjectCompiler objectCompiler;
                if (fileName.toLowerCase().endsWith(".spin")) {
                    objectCompiler = new Spin1ObjectCompilerProxy(fileName);
                }
                else {
                    objectCompiler = new Spin1CObjectCompilerProxy(fileName);
                }
                objectCompiler.compileObject(entry.getValue());
                childObjects.put(entry.getKey(), new ObjectInfo(objectCompiler));
            }
        }

        Spin1CObjectCompiler objectCompiler = new Spin1CObjectCompilerProxy(rootFile.getName());
        objectCompiler.compileObject(root);

        objectCompiler.compilePass2();
        for (int i = objects.size() - 1; i >= 0; i--) {
            File file = objects.get(i);
            ObjectInfo info = childObjects.get(file);
            info.compiler.compilePass2();
        }

        Spin1Object object = objectCompiler.generateObject(0);
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
                        linkData.setText(String.format("Object \"%s\" @ $%04X (variables @ $%04X)", entry.getKey().getName(), linkData.getOffset(), linkData.getVarOffset()));
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
                    linkData.setText(String.format("Object \"%s\" @ $%04X (variables @ $%04X)", entry.getKey().getName(), linkData.getOffset(), linkData.getVarOffset()));
                    break;
                }
            }
        }

        int stackRequired = 16;
        if (objectCompiler.getScope().hasSymbol("_STACK")) {
            stackRequired = objectCompiler.getScope().getLocalSymbol("_STACK").getNumber().intValue();
        }
        if (objectCompiler.getScope().hasSymbol("_FREE")) {
            stackRequired += objectCompiler.getScope().getLocalSymbol("_FREE").getNumber().intValue();
        }

        if (stackRequired > 0x2000) {
            logMessage(new CompilerException(rootFile.getName(), "_STACK and _FREE must sum to under 8k longs."));
        }
        else {
            int requiredSize = object.getSize() + object.getVarSize() + (stackRequired << 2);
            if (requiredSize >= 0x8000) {
                logMessage(new CompilerException(rootFile.getName(), "object exceeds runtime memory limit by " + ((requiredSize - 0x8000) >> 2) + " longs."));
            }
        }

        tree = preprocessor.getObjectTree();

        return object;

    }

    @Override
    public ObjectInfo getObjectInfo(String fileName) {
        File file = getFile(fileName + ".c");
        if (file == null) {
            file = getFile(fileName + ".spin");
        }
        if (file == null) {
            file = getFile(fileName);
        }
        return childObjects.get(file);
    }

}
