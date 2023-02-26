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
import com.maccasoft.propeller.ObjectTree;
import com.maccasoft.propeller.SpinObject.LongDataObject;
import com.maccasoft.propeller.SpinObject.WordDataObject;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.spin1.Spin1Object.LinkDataObject;
import com.maccasoft.propeller.spin1.Spin1ObjectCompiler.ObjectInfo;

public class Spin1Compiler extends Compiler {

    Spin1Context scope;
    Map<String, ObjectInfo> childObjects = new HashMap<String, ObjectInfo>();

    boolean errors;
    List<CompilerException> messages = new ArrayList<CompilerException>();

    boolean spenspinCompatible;
    boolean removeUnusedMethods;
    Spin1Preprocessor preprocessor;

    public Spin1Compiler() {
        scope = new Spin1GlobalContext();
    }

    public Spin1Compiler(boolean caseSensitive) {
        scope = new Spin1GlobalContext(caseSensitive);
    }

    public void setOpenspinCompatible(boolean openspinCompatible) {
        this.spenspinCompatible = openspinCompatible;
    }

    @Override
    public void setRemoveUnusedMethods(boolean removeUnusedMethods) {
        this.removeUnusedMethods = removeUnusedMethods;
    }

    @Override
    public void compile(File file, OutputStream binary, PrintStream listing) throws Exception {
        String text = getSource(file.getAbsolutePath());
        if (text == null) {
            throw new FileNotFoundException();
        }
        Spin1TokenStream stream = new Spin1TokenStream(text);
        Spin1Parser parser = new Spin1Parser(stream);
        Spin1Object object = compile(file, file.getName(), parser.parse());

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
    public Spin1Object compile(File rootFile, String rootFileName, Node root) {
        Spin1Object obj = compileObject(rootFile, rootFileName, root);

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
            logMessage(new CompilerException(CompilerException.ERROR, rootFileName, "No PUB routines found", null));
            return null;
        }
        pcurr.setValue((int) (pbase.getValue() + (((LongDataObject) obj.getObject(4)).getValue() & 0xFFFF)));

        offset = 4 + obj.getDcurr();
        dcurr.setValue(dbase.getValue() + offset);

        int stackRequired = 16;
        if (scope.hasSymbol("_STACK")) {
            stackRequired = scope.getLocalSymbol("_STACK").getNumber().intValue();
        }
        else if (scope.hasSymbol("_stack")) {
            stackRequired = scope.getLocalSymbol("_stack").getNumber().intValue();
        }
        if (scope.hasSymbol("_FREE")) {
            stackRequired += scope.getLocalSymbol("_FREE").getNumber().intValue();
        }
        else if (scope.hasSymbol("_free")) {
            stackRequired += scope.getLocalSymbol("_free").getNumber().intValue();
        }

        if (stackRequired > 0x2000) {
            logMessage(new CompilerException(rootFileName, "_STACK and _FREE must sum to under 8k longs."));
        }
        else {
            int requiredSize = object.getSize() + obj.getVarSize() + (stackRequired << 2);
            if (requiredSize >= 0x8000) {
                logMessage(new CompilerException(rootFileName, "object exceeds runtime memory limit by " + ((requiredSize - 0x8000) >> 2) + " longs."));
            }
        }

        return object;
    }

    class ObjectTreeVisitor extends NodeVisitor {

        String fileName;
        ObjectTreeVisitor parent;
        ListOrderedMap<String, Node> list;
        ObjectTree objectTree;

        public ObjectTreeVisitor(File file, String fileName) {
            this.fileName = fileName;
            this.objectTree = new ObjectTree(file, fileName);
            this.list = ListOrderedMap.listOrderedMap(new HashMap<>());
        }

        public ObjectTreeVisitor(ObjectTreeVisitor parent, File file, String fileName, ListOrderedMap<String, Node> list) {
            this.parent = parent;
            this.fileName = fileName;
            this.objectTree = new ObjectTree(file, fileName);
            this.list = list;

            parent.objectTree.add(objectTree);
        }

        @Override
        public void visitObject(ObjectNode node) {
            if (node.name == null || node.file == null) {
                return;
            }

            String objectFileName = node.file.getText().substring(1, node.file.getText().length() - 1) + ".spin";

            ObjectTreeVisitor p = parent;
            while (p != null) {
                if (p.fileName.equals(objectFileName)) {
                    throw new CompilerException(fileName, "\"" + objectFileName + "\" illegal circular reference", node.file);
                }
                p = p.parent;
            }

            File objectFile = getFile(objectFileName);

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

            objectRoot.accept(new ObjectTreeVisitor(this, objectFile, objectFileName, list));
        }

        public ListOrderedMap<String, Node> getList() {
            return list;
        }

        public ObjectTree getObjectTree() {
            return objectTree;
        }

    }

    class Spin1ObjectCompilerProxy extends Spin1ObjectCompiler {

        String fileName;

        public Spin1ObjectCompilerProxy(String fileName, Spin1Context scope, Map<String, ObjectInfo> childObjects) {
            super(scope, childObjects);
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

    Spin1Object compileObject(File rootFile, String rootFileName, Node root) {
        int memoryOffset = 16;

        ObjectTreeVisitor visitor = new ObjectTreeVisitor(rootFile, rootFileName);

        root.accept(visitor);

        tree = visitor.getObjectTree();
        ListOrderedMap<String, Node> objects = visitor.getList();

        preprocessor = new Spin1Preprocessor(root, objects);
        preprocessor.collectReferencedMethods();
        preprocessor.removeUnusedMethods();

        for (Entry<String, Node> entry : objects.entrySet()) {
            Spin1ObjectCompiler objectCompiler = new Spin1ObjectCompilerProxy(entry.getKey(), scope, childObjects);
            objectCompiler.setOpenspinCompatibile(spenspinCompatible);
            objectCompiler.compile(entry.getValue());
            childObjects.put(entry.getKey(), new ObjectInfo(entry.getKey(), objectCompiler));
        }

        Spin1ObjectCompiler objectCompiler = new Spin1ObjectCompilerProxy(rootFileName, scope, childObjects);
        objectCompiler.setOpenspinCompatibile(spenspinCompatible);
        objectCompiler.compile(root);

        Spin1Object object = objectCompiler.generateObject(memoryOffset);
        memoryOffset += object.getSize();

        for (int i = objects.size() - 1; i >= 0; i--) {
            String fileName = objects.get(i);
            ObjectInfo info = childObjects.get(fileName);
            info.offset = object.getSize();
            Spin1Object linkedObject = info.compiler.generateObject(memoryOffset);
            memoryOffset += linkedObject.getSize();
            linkedObject.getObject(0).setText("Object \"" + fileName + "\" header (var size " + linkedObject.getVarSize() + ")");
            object.writeObject(linkedObject);
        }

        for (ObjectInfo info : childObjects.values()) {
            for (LinkDataObject linkData : info.compiler.getObjectLinks()) {
                for (ObjectInfo info2 : childObjects.values()) {
                    if (info2.compiler == linkData.object.compiler) {
                        linkData.setOffset(info2.offset - info.offset);
                        linkData.setText(String.format("Object \"%s\" @ $%04X (variables @ $%04X)", info2.fileName, linkData.getOffset(), linkData.getVarOffset()));
                        break;
                    }
                }
            }
        }

        for (LinkDataObject linkData : object.links) {
            for (ObjectInfo info : childObjects.values()) {
                if (info.compiler == linkData.object.compiler) {
                    linkData.setOffset(info.offset);
                    linkData.setText(String.format("Object \"%s\" @ $%04X (variables @ $%04X)", info.fileName, linkData.getOffset(), linkData.getVarOffset()));
                    break;
                }
            }
        }

        return object;

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
