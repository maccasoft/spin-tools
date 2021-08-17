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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;

public class Spin2Preprocessor {

    final Node root;
    final Map<String, Node> childObjects;

    Map<MethodNode, String> unusedMethods = new HashMap<MethodNode, String>();

    public Spin2Preprocessor(Node root, Map<String, Node> childObjects) {
        this.root = root;
        this.childObjects = childObjects;
    }

    public void removeUnusedMethods() {
        root.accept(new NodeVisitor() {

            @Override
            public void visitMethod(MethodNode node) {
                unusedMethods.put(node, "");
            }
        });
        for (Entry<String, Node> entry : childObjects.entrySet()) {
            entry.getValue().accept(new NodeVisitor() {

                @Override
                public void visitMethod(MethodNode node) {
                    unusedMethods.put(node, entry.getKey());
                }
            });
        }

        removeUnusedMethods(true, root);
        for (Node node : childObjects.values()) {
            removeUnusedMethods(false, node);
        }

        for (Entry<MethodNode, String> entry : unusedMethods.entrySet()) {
            MethodNode node = entry.getKey();
            node.getParent().getChilds().remove(node);
            //System.out.println(entry.getValue() + ": " + node.getName());
        }
    }

    void removeUnusedMethods(boolean keepFirst, Node root) {
        Map<String, MethodNode> symbols = new HashMap<String, MethodNode>();

        root.accept(new NodeVisitor() {

            String prefix = "";

            @Override
            public void visitObject(ObjectNode node) {
                String fileName = node.file.getText().substring(1, node.file.getText().length() - 1) + ".spin";
                Node objectRoot = childObjects.get(fileName);
                if (objectRoot != null) {
                    String oldPrefix = prefix;

                    prefix = node.name.getText() + ".";
                    objectRoot.accept(this);

                    prefix = oldPrefix;
                }
            }

            @Override
            public void visitMethod(MethodNode node) {
                symbols.put(prefix + node.getName().getText(), node);
            }

        });

        root.accept(new NodeVisitor() {

            boolean first = keepFirst;

            @Override
            public void visitMethod(MethodNode node) {
                if (first) {
                    unusedMethods.remove(node);
                    first = false;
                }
                for (Node child : node.getChilds()) {
                    if (child instanceof StatementNode) {
                        markTokens(child);
                    }
                }
            }

            void markTokens(Node node) {
                for (Token token : node.getTokens()) {
                    MethodNode methodNode = symbols.get(token.getText());
                    unusedMethods.remove(methodNode);
                }
                for (Node child : node.getChilds()) {
                    if (child instanceof StatementNode) {
                        markTokens(child);
                    }
                }
            }

        });
    }

}
