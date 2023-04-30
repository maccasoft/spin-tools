/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;

import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;

public class SpinModel {

    Node root;
    Map<String, SpinModel> objects = new HashMap<String, SpinModel>();

    public SpinModel(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    public void setObject(String name, SpinModel model) {
        objects.put(name, model);
    }

    public SpinModel getObject(String name) {
        return objects.get(name);
    }

    public Node getNodeAt(int index) {
        if (root == null) {
            return null;
        }

        List<Node> allNodes = new ArrayList<Node>();
        root.accept(new NodeVisitor() {

            @Override
            public boolean visitConstants(ConstantsNode node) {
                allNodes.add(node);
                return false;
            }

            @Override
            public boolean visitVariables(VariablesNode node) {
                allNodes.add(node);
                return false;
            }

            @Override
            public boolean visitObjects(ObjectsNode node) {
                allNodes.add(node);
                return false;
            }

            @Override
            public boolean visitMethod(MethodNode node) {
                allNodes.add(node);
                return true;
            }

            @Override
            public boolean visitStatement(StatementNode node) {
                allNodes.add(node);
                return true;
            }

            @Override
            public boolean visitData(DataNode node) {
                allNodes.add(node);
                return true;
            }

            @Override
            public void visitDataLine(DataLineNode node) {
                allNodes.add(node);
            }

        });
        for (int i = 0; i < allNodes.size() - 1; i++) {
            int nodeStart = allNodes.get(i).getStartIndex();
            int nodeStop = allNodes.get(i + 1).getStartIndex();
            if (index >= nodeStart && index < nodeStop) {
                return allNodes.get(i);
            }
        }

        for (int i = 0; i < root.getChilds().size() - 1; i++) {
            int nodeStart = root.getChild(i).getStartIndex();
            int nodeStop = root.getChild(i + 1).getStartIndex();
            if (index >= nodeStart && index < nodeStop) {
                return root.getChild(i);
            }
        }

        if (root.getChilds().size() != 0) {
            Node last = root.getChild(root.getChilds().size() - 1);
            if (index >= last.getStartIndex()) {
                return last;
            }
        }

        return null;
    }

    public List<IContentProposal> getMethodProposals(Node context, String token) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();

        if (context != null) {
            while (!(context instanceof MethodNode) && context.getParent() != null) {
                context = context.getParent();
            }
            if (context instanceof MethodNode) {
                MethodNode node = (MethodNode) context;
                for (Node child : node.getParameters()) {
                    String text = child.getText();
                    if (StringUtils.containsIgnoreCase(text, token)) {
                        proposals.add(new ContentProposal(text, text, null));
                    }
                }

                for (Node child : node.getReturnVariables()) {
                    String text = child.getText();
                    if (StringUtils.containsIgnoreCase(text, token)) {
                        proposals.add(new ContentProposal(text, text, null));
                    }
                }

                for (Node child : node.getLocalVariables()) {
                    String text = child.getText();
                    if (StringUtils.containsIgnoreCase(text, token)) {
                        proposals.add(new ContentProposal(text, text, null));
                    }
                }
            }
        }

        if (root != null) {
            final Node currentMethod = context;
            root.accept(new NodeVisitor() {

                @Override
                public void visitConstant(ConstantNode node) {
                    if (node.getIdentifier() != null) {
                        String text = node.getIdentifier().getText();
                        if (StringUtils.containsIgnoreCase(text, token)) {
                            proposals.add(new ContentProposal(text, text, null));
                        }
                    }
                }

                @Override
                public void visitVariable(VariableNode node) {
                    if (node.getIdentifier() != null) {
                        String text = node.getIdentifier().getText();
                        if (StringUtils.containsIgnoreCase(text, token)) {
                            proposals.add(new ContentProposal(text, text, null));
                        }
                    }
                }

                @Override
                public boolean visitMethod(MethodNode node) {
                    if (node != currentMethod && node.getName() != null) {
                        String text = node.getName().getText();
                        if (StringUtils.containsIgnoreCase(text, token)) {
                            proposals.add(new ContentProposal(text, text, null));
                        }
                    }
                    return false;
                }

                @Override
                public boolean visitData(DataNode node) {
                    for (Node child : node.getChilds()) {
                        DataLineNode lineNode = (DataLineNode) child;
                        if (lineNode.label != null) {
                            String text = lineNode.label.getText();
                            if (!text.startsWith(".") && StringUtils.containsIgnoreCase(text, token)) {
                                proposals.add(new ContentProposal(text, text, null));
                            }
                        }
                    }
                    return false;
                }

            });
        }

        return proposals;
    }

}
