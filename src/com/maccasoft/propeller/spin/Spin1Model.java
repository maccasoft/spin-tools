/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;

import com.maccasoft.propeller.model.ConstantAssignEnumNode;
import com.maccasoft.propeller.model.ConstantAssignNode;
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

public class Spin1Model {

    static Set<String> instructions = new HashSet<String>(Arrays.asList(new String[] {
        "ORG", "FIT",
        "ABS", "ABSNEG", "ADD", "ADDABS", "ADDS", "ADDSX", "ADDX", "AND", "ANDN", "CALL", "CLKSET", "CMP", "CMPS", "CMPSUB",
        "CMPSX", "CMPX", "COGID", "COGINIT", "COGSTOP", "DJNZ", "HUBOP", "JMP", "JMPRET", "LOCKCLR", "LOCKNEW", "LOCKRET",
        "LOCKSET", "MAX", "MAXS", "MIN", "MINS", "MOV", "MOVD", "MOVI", "MOVS", "MUXC", "MUXNC", "MUXNZ", "MUXZ", "NEG", "NEGC",
        "NEGNC", "NEGNZ", "NEGZ", "NOP", "OR", "RCL", "RCR", "RDBYTE", "RDLONG", "RDWORD", "RET", "REV", "ROL", "ROR", "SAR",
        "SHL", "SHR", "SUB", "SUBABS", "SUBS", "SUBSX", "SUBX", "SUMC", "SUMNC", "SUMNZ", "SUMZ", "TEST", "TESTN", "TJNZ", "TJZ",
        "WAITCNT", "WAITPEQ", "WAITPNE", "WAITVID", "WRBYTE", "WRLONG", "WRWORD", "XOR",
    }));

    static Set<String> conditions = new HashSet<String>(Arrays.asList(new String[] {
        "IF_ALWAYS", "IF_NEVER", "IF_E", "IF_NE", "IF_A", "IF_B", "IF_AE", "IF_BE", "IF_C", "IF_NC", "IF_Z", "IF_NZ", "IF_C_EQ_Z",
        "IF_C_NE_Z", "IF_C_AND_Z", "IF_C_AND_NZ", "IF_NC_AND_Z", "IF_NC_AND_NZ", "IF_C_OR_Z", "IF_C_OR_NZ", "IF_NC_OR_Z",
        "IF_NC_OR_NZ", "IF_Z_EQ_C", "IF_Z_NE_C", "IF_Z_AND_C", "IF_Z_AND_NC", "IF_NZ_AND_C", "IF_NZ_AND_NC", "IF_Z_OR_C",
        "IF_Z_OR_NC", "IF_NZ_OR_C", "IF_NZ_OR_NC",
    }));

    static Set<String> modifiers = new HashSet<String>(Arrays.asList(new String[] {
        "WZ", "WC", "WR", "NR",
    }));

    static Set<String> types = new HashSet<String>(Arrays.asList(new String[] {
        "LONG", "WORD", "BYTE",
    }));

    Node root;
    Map<String, Spin1Model> objects = new HashMap<String, Spin1Model>();

    public static boolean isInstruction(String token) {
        return instructions.contains(token.toUpperCase());
    }

    public static boolean isCondition(String token) {
        return conditions.contains(token.toUpperCase());
    }

    public static boolean isModifier(String token) {
        return modifiers.contains(token.toUpperCase());
    }

    public static boolean isType(String token) {
        return types.contains(token.toUpperCase());
    }

    public Spin1Model(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    public void setObject(String name, Spin1Model model) {
        objects.put(name, model);
    }

    public Spin1Model getObject(String name) {
        return objects.get(name);
    }

    public Node getNodeAt(int index) {
        if (root == null) {
            return null;
        }

        List<Node> allNodes = new ArrayList<Node>();
        root.accept(new NodeVisitor() {

            @Override
            public void visitConstants(ConstantsNode node) {
                allNodes.add(node);
            }

            @Override
            public void visitVariables(VariablesNode node) {
                allNodes.add(node);
            }

            @Override
            public void visitObjects(ObjectsNode node) {
                allNodes.add(node);
            }

            @Override
            public void visitStatement(StatementNode node) {
                allNodes.add(node);
            }

            @Override
            public void visitData(DataNode node) {
                allNodes.add(node);
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
                public void visitConstantAssign(ConstantAssignNode node) {
                    if (node.identifier != null) {
                        String text = node.identifier.getText();
                        if (StringUtils.containsIgnoreCase(text, token)) {
                            proposals.add(new ContentProposal(text, text, null));
                        }
                    }
                }

                @Override
                public void visitConstantAssignEnum(ConstantAssignEnumNode node) {
                    if (node.identifier != null) {
                        String text = node.identifier.getText();
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
                public void visitMethod(MethodNode node) {
                    if (node != currentMethod && node.getName() != null) {
                        String text = node.getName().getText();
                        if (StringUtils.containsIgnoreCase(text, token)) {
                            proposals.add(new ContentProposal(text, text, null));
                        }
                    }
                }

                @Override
                public void visitData(DataNode node) {
                    for (Node child : node.getChilds()) {
                        DataLineNode lineNode = (DataLineNode) child;
                        if (lineNode.label != null) {
                            String text = lineNode.label.getText();
                            if (!text.startsWith(".") && StringUtils.containsIgnoreCase(text, token)) {
                                proposals.add(new ContentProposal(text, text, null));
                            }
                        }
                    }
                }

            });
        }

        return proposals;
    }

}
