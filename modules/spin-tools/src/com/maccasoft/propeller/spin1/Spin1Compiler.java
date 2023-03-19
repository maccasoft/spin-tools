/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

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
import com.maccasoft.propeller.SpinObject.LongDataObject;
import com.maccasoft.propeller.SpinObject.WordDataObject;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin1.Spin1Object.LinkDataObject;

public class Spin1Compiler extends Compiler {

    public static class ObjectInfo {

        Spin1ObjectCompiler compiler;

        long offset;
        Expression count;

        public ObjectInfo(Spin1ObjectCompiler compiler) {
            this.compiler = compiler;
        }

        public ObjectInfo(Spin1ObjectCompiler compiler, Expression count) {
            this.compiler = compiler;
            this.count = count;
        }

        public boolean hasErrors() {
            return compiler.hasErrors();
        }

    }

    boolean removeUnusedMethods;
    boolean openspinCompatible;

    Spin1Preprocessor preprocessor;
    Map<File, ObjectInfo> childObjects = new HashMap<>();

    boolean errors;
    List<CompilerException> messages = new ArrayList<CompilerException>();

    public Spin1Compiler() {

    }

    @Override
    public void setRemoveUnusedMethods(boolean removeUnusedMethods) {
        this.removeUnusedMethods = removeUnusedMethods;
    }

    public void setOpenspinCompatible(boolean openspinCompatible) {
        this.openspinCompatible = openspinCompatible;
    }

    @Override
    public void compile(File file, OutputStream binary, PrintStream listing) throws Exception {
        String text = getSource(file.getAbsolutePath());
        if (text == null) {
            throw new FileNotFoundException();
        }
        Spin1TokenStream stream = new Spin1TokenStream(text);
        Spin1Parser parser = new Spin1Parser(stream);
        Spin1Object object = compile(file, parser.parse());

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
    public Spin1Object compile(File rootFile, Node root) {
        Spin1Object obj = compileObject(rootFile, root);

        Spin1Object object = new Spin1Object();
        object.setClkFreq(obj.getClkFreq());
        object.setClkMode(obj.getClkMode());

        object.writeLong(object.getClkFreq(), "CLKFREQ");
        object.writeByte(object.getClkMode(), "CLKMODE");
        object.writeByte(0, "Checksum");

        WordDataObject pbase = object.writeWord(0, "PBASE");
        WordDataObject vbase = object.writeWord(0, "VBASE");
        WordDataObject dbase = object.writeWord(0, "DBASE");

        WordDataObject pcurr = object.writeWord(0, "PCURR");
        WordDataObject dcurr = object.writeWord(0, "DCURR");

        pbase.setValue(object.getSize());

        object.writeObject(obj);

        vbase.setValue(object.getSize());

        int offset = obj.getVarSize() + 8;
        dbase.setValue(object.getSize() + offset);

        if (!(obj.getObject(4) instanceof LongDataObject)) {
            logMessage(new CompilerException(CompilerException.ERROR, rootFile.getName(), "No PUB routines found", null));
            return null;
        }
        pcurr.setValue((int) (pbase.getValue() + (((LongDataObject) obj.getObject(4)).getValue() & 0xFFFF)));

        offset = 4 + obj.getDcurr();
        dcurr.setValue(dbase.getValue() + offset);

        return object;
    }

    class Spin1ObjectCompilerProxy extends Spin1ObjectCompiler {

        String fileName;

        public Spin1ObjectCompilerProxy(String fileName) {
            super(Spin1Compiler.this);
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
            Node node = Spin1Compiler.this.getParsedObject(fileName);
            if (node == null) {
                node = Spin1Compiler.this.getParsedObject(fileName + ".spin");
            }
            return node;
        }

        @Override
        protected byte[] getBinaryFile(String fileName) {
            return Spin1Compiler.this.getBinaryFile(fileName);
        }

        @Override
        protected void logMessage(CompilerException message) {
            message.fileName = fileName;
            if (message.hasChilds()) {
                for (CompilerException msg : message.getChilds()) {
                    msg.fileName = fileName;
                    Spin1Compiler.this.logMessage(msg);
                }
            }
            else {
                Spin1Compiler.this.logMessage(message);
            }
            super.logMessage(message);
        }

    }

    Spin1Object compileObject(File rootFile, Node root) {
        int memoryOffset = 16;

        preprocessor = new Spin1Preprocessor(this);
        preprocessor.process(rootFile, root);
        preprocessor.removeUnusedMethods();

        ListOrderedMap<File, Node> objects = preprocessor.getObjects();

        for (Entry<File, Node> entry : objects.entrySet()) {
            Spin1ObjectCompiler objectCompiler = new Spin1ObjectCompilerProxy(entry.getKey().getName());
            objectCompiler.setOpenspinCompatibile(openspinCompatible);
            objectCompiler.compile(entry.getValue());
            childObjects.put(entry.getKey(), new ObjectInfo(objectCompiler));
        }

        Spin1ObjectCompiler objectCompiler = new Spin1ObjectCompilerProxy(rootFile.getName());
        objectCompiler.setOpenspinCompatibile(openspinCompatible);
        objectCompiler.compile(root);

        Spin1Object object = objectCompiler.generateObject(memoryOffset);
        memoryOffset += object.getSize();

        for (int i = objects.size() - 1; i >= 0; i--) {
            File file = objects.get(i);
            ObjectInfo info = childObjects.get(file);
            info.offset = object.getSize();
            Spin1Object linkedObject = info.compiler.generateObject(memoryOffset);
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
                        linkData.setText(String.format("Object \"%s\" @ $%04X (variables @ $%04X)", entry.getKey().getName(), linkData.getOffset(), linkData.getVarOffset()));
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

    public ObjectInfo getObjectInfo(String fileName) {
        File file = getFile(fileName);
        if (file == null) {
            file = getFile(fileName + ".spin");
        }
        return childObjects.get(file);
    }

    protected Node getParsedObject(String fileName) {
        Node node = getParsedSource(fileName);
        if (node == null) {
            String text = getSource(fileName);
            if (text != null) {
                Spin1TokenStream stream = new Spin1TokenStream(text);
                Spin1Parser parser = new Spin1Parser(stream);
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
