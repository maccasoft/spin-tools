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
import java.util.Map;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.SpinObject;
import com.maccasoft.propeller.SpinObject.LinkDataObject;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin1.Spin1Compiler;
import com.maccasoft.propeller.spin1.Spin1Object;
import com.maccasoft.propeller.spin1.Spin1ObjectCompiler;

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

        public Spin1CObjectCompilerProxy(File file) {
            super(Spin1CCompiler.this, file);
        }

        public Spin1CObjectCompilerProxy(ObjectCompiler parent, File file) {
            super(Spin1CCompiler.this, parent, file);
        }

        @Override
        protected byte[] getBinaryFile(String fileName) {
            return Spin1CCompiler.this.getBinaryFile(fileName);
        }

        @Override
        protected void logMessage(CompilerException message) {
            message.fileName = getFile().getName();
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

        public Spin1ObjectCompilerProxy(File file) {
            super(Spin1CCompiler.this, file);
        }

        public Spin1ObjectCompilerProxy(ObjectCompiler parent, File file) {
            super(Spin1CCompiler.this, parent, file);
        }

        @Override
        protected byte[] getBinaryFile(String fileName) {
            return Spin1CCompiler.this.getBinaryFile(fileName);
        }

        @Override
        protected void logMessage(CompilerException message) {
            message.fileName = getFile().getName();
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

        Spin1CObjectCompiler objectCompiler = new Spin1CObjectCompilerProxy(rootFile);
        objectCompiler.compileObject(root);

        objectCompiler.compileStep2();
        for (ObjectInfo info : childObjects) {
            info.compiler.compileStep2();
        }

        Spin1Object object = objectCompiler.generateObject(memoryOffset);
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
                        linkData.setText(String.format("Object \"%s\" @ $%04X (variables @ $%04X)", info2.file.getName(), linkData.getOffset(), linkData.getVarOffset()));
                        break;
                    }
                }
            }
        }

        for (LinkDataObject linkData : objectCompiler.getObjectLinks()) {
            for (ObjectInfo info : childObjects) {
                if (linkData.isObject(info.compiler)) {
                    linkData.setOffset(info.offset);
                    linkData.setText(String.format("Object \"%s\" @ $%04X (variables @ $%04X)", info.file.getName(), linkData.getOffset(), linkData.getVarOffset()));
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

        tree = buildFrom(objectCompiler);

        return object;

    }

    @Override
    public ObjectInfo getObjectInfo(ObjectCompiler parent, File file, Map<String, Expression> parameters) throws Exception {
        Node objectRoot = getParsedSource(file);

        ObjectCompiler objectCompiler;
        if (file.getName().toLowerCase().endsWith(".spin")) {
            objectCompiler = new Spin1ObjectCompilerProxy(parent, file);
        }
        else {
            objectCompiler = new Spin1CObjectCompilerProxy(parent, file);
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
        objectCompiler.compileObject(objectRoot);

        return info;
    }

}
