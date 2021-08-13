/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.maccasoft.propeller.CompilerMessage;
import com.maccasoft.propeller.SpinObject.LongDataObject;
import com.maccasoft.propeller.SpinObject.WordDataObject;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.spin1.Spin1Object.LinkDataObject;
import com.maccasoft.propeller.spin1.Spin1ObjectCompiler.ObjectInfo;

public class Spin1Compiler {

    Spin1Context scope = new Spin1GlobalContext();
    Map<String, ObjectInfo> childObjects = new HashMap<String, ObjectInfo>();

    boolean errors;
    List<CompilerMessage> messages = new ArrayList<CompilerMessage>();

    public Spin1Compiler() {

    }

    public Spin1Object compile(String rootFileName, Node root) {
        Spin1Object obj = compileObject(rootFileName, root);

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
            logMessage(new CompilerMessage(rootFileName, "_STACK and _FREE must sum to under 8k longs."));
        }
        else {
            int requiredSize = object.getSize() + obj.getVarSize() + (stackRequired << 2);
            if (requiredSize >= 0x8000) {
                logMessage(new CompilerMessage(rootFileName, "object exceeds runtime memory limit by " + ((requiredSize - 0x8000) >> 2) + " longs."));
            }
        }

        return object;
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
            String objectFileName = node.file.getText().substring(1, node.file.getText().length() - 1) + ".spin";

            ObjectNodeVisitor p = parent;
            while (p != null) {
                if (p.fileName.equals(objectFileName)) {
                    throw new CompilerMessage(fileName, "\"" + objectFileName + "\" illegal circular reference", node);
                }
                p = p.parent;
            }

            Node objectRoot = list.get(objectFileName);
            if (objectRoot == null) {
                objectRoot = getParsedObject(objectFileName);
            }
            if (objectRoot == null) {
                logMessage(new CompilerMessage(fileName, "object file \"" + objectFileName + "\" not found", node));
                return;
            }

            list.remove(objectFileName);
            list.put(0, objectFileName, objectRoot);

            objectRoot.accept(new ObjectNodeVisitor(this, objectFileName, list));
        }

    }

    class Spin1ObjectCompilerProxy extends Spin1ObjectCompiler {

        String fileName;

        public Spin1ObjectCompilerProxy(String fileName, Spin1Context scope, Map<String, ObjectInfo> childObjects) {
            super(scope, childObjects);
            this.fileName = fileName;
        }

        @Override
        protected byte[] getBinaryFile(String fileName) {
            return Spin1Compiler.this.getBinaryFile(fileName);
        }

        @Override
        protected void logMessage(CompilerMessage message) {
            message.fileName = fileName;
            Spin1Compiler.this.logMessage(message);
        }

    }

    Spin1Object compileObject(String rootFileName, Node root) {
        ListOrderedMap<String, Node> objects = ListOrderedMap.listOrderedMap(new HashMap<String, Node>());

        root.accept(new ObjectNodeVisitor(rootFileName, objects));

        for (Entry<String, Node> entry : objects.entrySet()) {
            Spin1ObjectCompiler objectCompiler = new Spin1ObjectCompilerProxy(entry.getKey(), scope, childObjects);
            Spin1Object object = objectCompiler.compileObject(entry.getValue());
            childObjects.put(entry.getKey(), new ObjectInfo(entry.getKey(), object));
        }

        Spin1ObjectCompiler objectCompiler = new Spin1ObjectCompilerProxy(rootFileName, scope, childObjects);
        Spin1Object object = objectCompiler.compileObject(root);

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
                        linkData.setText(String.format("Object \"%s\" @ $%04X (variables @ $%04X)", info2.fileName, linkData.getOffset(), linkData.getVarOffset()));
                        break;
                    }
                }
            }
        }

        for (LinkDataObject linkData : object.links) {
            for (ObjectInfo info : childObjects.values()) {
                if (info.object == linkData.object) {
                    linkData.setOffset(info.offset);
                    linkData.setText(String.format("Object \"%s\" @ $%04X (variables @ $%04X)", info.fileName, linkData.getOffset(), linkData.getVarOffset()));
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
