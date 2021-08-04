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

import com.maccasoft.propeller.CompilerMessage;
import com.maccasoft.propeller.SpinObject.LongDataObject;
import com.maccasoft.propeller.SpinObject.WordDataObject;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin1.Spin1Object.LinkDataObject;
import com.maccasoft.propeller.spin1.Spin1ObjectCompiler.ObjectInfo;

public class Spin1Compiler {

    Spin1Context scope = new Spin1GlobalContext();
    Map<String, ObjectInfo> childObjects = new HashMap<String, ObjectInfo>();

    boolean errors;
    List<CompilerMessage> messages = new ArrayList<CompilerMessage>();

    public Spin1Compiler() {

    }

    public Spin1Object compile(Node root) {
        Spin1Object obj = compileObject(root);

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

        return object;
    }

    public Spin1Object compileObject(Node root) {
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
            Spin1ObjectCompiler objectCompiler = new Spin1ObjectCompiler(scope, childObjects);
            Spin1Object object = objectCompiler.compileObject(map.get(fileName));
            childObjects.put(fileName, new ObjectInfo(fileName, object));
        }

        Spin1ObjectCompiler objectCompiler = new Spin1ObjectCompiler(scope, childObjects);
        Spin1Object object = objectCompiler.compileObject(root);

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

    Expression buildExpression(Node node, Spin1Context scope) {
        return buildExpression(node.getTokens(), scope);
    }

    Expression buildExpression(List<Token> tokens, Spin1Context scope) {
        return new Spin1ExpressionBuilder(scope, tokens).getExpression();
    }

}
