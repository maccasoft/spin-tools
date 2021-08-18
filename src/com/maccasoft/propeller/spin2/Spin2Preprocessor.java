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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;

public class Spin2Preprocessor {

    class MethodReference {
        int count;
        MethodNode node;
        List<MethodNode> references = new ArrayList<MethodNode>();

        public MethodReference(MethodNode node) {
            this.node = node;
        }

    }

    final Node root;
    final Map<String, Node> childObjects;

    Map<MethodNode, MethodReference> referencedMethods = new HashMap<MethodNode, MethodReference>();

    public Spin2Preprocessor(Node root, Map<String, Node> childObjects) {
        this.root = root;
        this.childObjects = childObjects;
    }

    public void collectReferencedMethods() {
        root.accept(new NodeVisitor() {

            @Override
            public void visitMethod(MethodNode node) {
                referencedMethods.put(node, new MethodReference(node));
            }
        });
        for (Entry<String, Node> entry : childObjects.entrySet()) {
            entry.getValue().accept(new NodeVisitor() {

                @Override
                public void visitMethod(MethodNode node) {
                    referencedMethods.put(node, new MethodReference(node));
                }
            });
        }

        countMethodReferences(true, root);
        for (Node node : childObjects.values()) {
            countMethodReferences(false, node);
        }
    }

    void countMethodReferences(boolean keepFirst, Node root) {
        Map<String, MethodNode> symbols = new HashMap<String, MethodNode>();

        root.accept(new NodeVisitor() {

            @Override
            public void visitObject(ObjectNode node) {
                if (node.file == null) {
                    return;
                }
                String fileName = node.file.getText().substring(1, node.file.getText().length() - 1) + ".spin2";
                Node objectRoot = childObjects.get(fileName);
                if (objectRoot != null) {
                    String prefix = node.name.getText() + ".";
                    objectRoot.accept(new NodeVisitor() {

                        @Override
                        public void visitMethod(MethodNode node) {
                            if (node.getName() != null) {
                                symbols.put(prefix + node.getName().getText(), node);
                            }
                        }

                    });
                }
            }

            @Override
            public void visitMethod(MethodNode node) {
                if (node.getName() != null) {
                    symbols.put(node.getName().getText(), node);
                }
            }

        });

        root.accept(new NodeVisitor() {

            boolean first = keepFirst;

            @Override
            public void visitMethod(MethodNode node) {
                MethodReference parent = referencedMethods.get(node);
                if (first) {
                    parent.count++;
                    first = false;
                }
                for (Node child : node.getChilds()) {
                    if (child instanceof StatementNode) {
                        markTokens(parent, child);
                    }
                }
            }

            void markTokens(MethodReference parent, Node node) {
                for (Token token : node.getTokens()) {
                    MethodNode methodNode = symbols.get(token.getText());
                    if (methodNode != null && methodNode != parent.node) {
                        MethodReference ref = referencedMethods.get(methodNode);
                        ref.count++;
                        parent.references.add(methodNode);
                    }
                }
                for (Node child : node.getChilds()) {
                    if (child instanceof StatementNode) {
                        markTokens(parent, child);
                    }
                }
            }

        });
    }

    public void removeUnusedMethods() {
        boolean repeat;

        do {
            repeat = false;
            Iterator<Entry<MethodNode, MethodReference>> iter = referencedMethods.entrySet().iterator();
            while (iter.hasNext()) {
                MethodReference ref = iter.next().getValue();
                if (ref.count == 0) {
                    for (MethodNode child : ref.references) {
                        MethodReference childRef = referencedMethods.get(child);
                        childRef.count--;
                    }
                    iter.remove();
                    repeat = true;
                }
            }
        } while (repeat);
    }

    public boolean isReferenced(Node node) {
        return referencedMethods.containsKey(node);
    }

}
