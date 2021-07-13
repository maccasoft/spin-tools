/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;

public abstract class EditorTokenMarker {

    public static enum TokenId {
        NULL,
        COMMENT,
        SECTION,
        NUMBER,
        STRING,
        KEYWORD,
        FUNCTION,
        OPERATOR,
        TYPE,

        CONSTANT,
        VARIABLE,
        OBJECT,

        METHOD_PUB,
        METHOD_PRI,
        METHOD_LOCAL,
        METHOD_RETURN,
        METHOD_PARAMETER,

        PASM_LABEL,
        PASM_LOCAL_LABEL,
        PASM_CONDITION,
        PASM_TYPE,
        PASM_DIRECTIVE,
        PASM_INSTRUCTION,
        PASM_MODIFIER,

        WARNING,
        ERROR
    }

    public static class TokenMarker implements Comparable<TokenMarker> {

        int start;
        int stop;
        TokenId id;

        String error;

        public TokenMarker(Token token, TokenId id) {
            this.start = token.start;
            this.stop = token.stop;
            this.id = id;
        }

        public TokenMarker(Node node, TokenId id) {
            this.start = node.getStartIndex();
            this.stop = node.getStopIndex();
            this.id = id;
        }

        public TokenMarker(Token startToken, Token stopToken, TokenId id) {
            this.start = startToken.start;
            this.stop = stopToken.stop;
            this.id = id;
        }

        public TokenMarker(int start, int stop, TokenId id) {
            this.start = start;
            this.stop = stop;
            this.id = id;
        }

        public TokenMarker(int start) {
            this.start = start;
            this.stop = start;
        }

        public int getStart() {
            return start;
        }

        public int getStop() {
            return stop;
        }

        public TokenId getId() {
            return id;
        }

        @Override
        public int compareTo(TokenMarker o) {
            return Integer.compare(start, o.start);
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

    }

    protected Node root;
    protected TreeSet<TokenMarker> tokens = new TreeSet<TokenMarker>();
    protected TreeSet<TokenMarker> compilerTokens = new TreeSet<TokenMarker>();

    protected Map<String, TokenId> symbols = new HashMap<String, TokenId>();

    public abstract void refreshTokens(String text);

    public Node getRoot() {
        return root;
    }

    public void refreshCompilerTokens(List<CompilerMessage> messages) {
        TreeSet<TokenMarker> tokens = new TreeSet<TokenMarker>();
        for (CompilerMessage message : messages) {
            TokenId id = message.type == CompilerMessage.ERROR ? TokenId.ERROR : TokenId.WARNING;
            TokenMarker marker = new TokenMarker(message.getStartToken(), message.getStopToken(), id);
            marker.setError(message.getMessage());
            tokens.add(marker);
        }
        compilerTokens = tokens;
    }

    public TreeSet<TokenMarker> getCompilerTokens() {
        return compilerTokens;
    }

    public Set<TokenMarker> getLineTokens(int lineStart, String lineText) {
        return getLineTokens(lineStart, lineStart + lineText.length());
    }

    public TokenMarker getMarkerAtOffset(int offset) {
        for (TokenMarker marker : compilerTokens) {
            if (offset >= marker.start && offset <= marker.stop) {
                return marker;
            }
        }
        for (TokenMarker marker : tokens) {
            if (offset >= marker.start && offset <= marker.stop) {
                return marker;
            }
        }
        return null;
    }

    public Set<TokenMarker> getLineTokens(int lineStart, int lineStop) {
        Set<TokenMarker> result = new TreeSet<TokenMarker>();

        if (tokens.size() != 0) {
            TokenMarker firstMarker = tokens.floor(new TokenMarker(lineStart, lineStop, null));
            if (firstMarker == null) {
                firstMarker = tokens.first();
            }
            for (TokenMarker entry : tokens.tailSet(firstMarker)) {
                int start = entry.getStart();
                int stop = entry.getStop();
                if ((lineStart >= start && lineStart <= stop) || (lineStop >= start && lineStop <= stop)) {
                    result.add(entry);
                }
                else if (stop >= lineStart && stop <= lineStop) {
                    result.add(entry);
                }
                if (start >= lineStop) {
                    break;
                }
            }
        }

        return result;
    }

    public Token getTokenAt(int index) {
        if (root == null) {
            return null;
        }
        return getTokenAt(root, index);
    }

    public Token getTokenAt(Node node, int index) {
        for (Token token : node.getTokens()) {
            if (index >= token.start && index <= token.stop) {
                return token;
            }
        }
        for (Node child : node.getChilds()) {
            Token token = getTokenAt(child, index);
            if (token != null) {
                return token;
            }
        }

        return null;
    }

    public Node getContextAt(int index) {
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
        if (allNodes.size() != 0) {
            return allNodes.get(allNodes.size() - 1);
        }
        return null;
    }

    public String getMethod(String symbol) {
        StringBuilder sb = new StringBuilder();

        root.accept(new NodeVisitor() {

            @Override
            public void visitConstantAssign(ConstantAssignNode node) {
                if (node.getIdentifier() == null) {
                    return;
                }
                if (!symbol.equals(node.getIdentifier().getText())) {
                    return;
                }
                sb.append("<b>");
                sb.append(node.getText());
                sb.append("</b>");
            }

            @Override
            public void visitMethod(MethodNode node) {
                if (node.getName() == null) {
                    return;
                }
                if (!symbol.equals(node.getName().getText())) {
                    return;
                }

                sb.append("<b>");
                sb.append(node.getType().getText());
                sb.append(" ");
                if (node.getName() != null) {
                    sb.append(node.getName().getText());
                }

                sb.append("(");
                for (Node child : node.getParameters()) {
                    if (sb.charAt(sb.length() - 1) != '(') {
                        sb.append(", ");
                    }
                    sb.append(child.getText());
                    tokens.add(new TokenMarker(child, TokenId.METHOD_LOCAL));
                }
                sb.append(")");

                if (node.getReturnVariables().size() != 0) {
                    sb.append(" : ");
                    for (Node child : node.getReturnVariables()) {
                        if (sb.charAt(sb.length() - 2) != ':') {
                            sb.append(", ");
                        }
                        sb.append(child.getText());
                        tokens.add(new TokenMarker(child, TokenId.METHOD_RETURN));
                    }
                }
                sb.append("</b>");
            }

        });

        return sb.length() != 0 ? sb.toString() : null;
    }

    public List<IContentProposal> getMethodProposals(Node context, String token) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();

        if (root != null) {
            while (!(context instanceof MethodNode) && context.getParent() != null) {
                context = context.getParent();
            }
            context.accept(new NodeVisitor() {

                @Override
                public void visitMethod(MethodNode node) {
                    for (Node child : node.getParameters()) {
                        String text = child.getText();
                        if (!text.startsWith(".") && text.toUpperCase().contains(token)) {
                            proposals.add(new ContentProposal(text, text, null));
                        }
                    }

                    for (Node child : node.getReturnVariables()) {
                        String text = child.getText();
                        if (!text.startsWith(".") && text.toUpperCase().contains(token)) {
                            proposals.add(new ContentProposal(text, text, null));
                        }
                    }

                    for (Node child : node.getLocalVariables()) {
                        String text = child.getText();
                        if (!text.startsWith(".") && text.toUpperCase().contains(token)) {
                            proposals.add(new ContentProposal(text, text, null));
                        }
                    }
                }

                @Override
                public void visitDataLine(DataLineNode node) {
                    if (node.label != null) {
                        String text = node.label.getText();
                        if (!text.startsWith(".") && text.toUpperCase().contains(token)) {
                            proposals.add(new ContentProposal(text, text, null));
                        }
                    }
                }

            });

            root.accept(new NodeVisitor() {

                @Override
                public void visitConstantAssign(ConstantAssignNode node) {
                    if (node.identifier != null) {
                        String text = node.identifier.getText();
                        if (text.toUpperCase().contains(token)) {
                            proposals.add(new ContentProposal(text, text, null));
                        }
                    }
                }

                @Override
                public void visitConstantAssignEnum(ConstantAssignEnumNode node) {
                    if (node.identifier != null) {
                        String text = node.identifier.getText();
                        if (text.toUpperCase().contains(token)) {
                            proposals.add(new ContentProposal(text, text, null));
                        }
                    }
                }

                @Override
                public void visitVariable(VariableNode node) {
                    if (node.getIdentifier() != null) {
                        String text = node.getIdentifier().getText();
                        if (!text.startsWith(".") && text.toUpperCase().contains(token)) {
                            proposals.add(new ContentProposal(text, text, null));
                        }
                    }
                }

                @Override
                public void visitMethod(MethodNode node) {
                    if (node.getName() != null) {
                        String text = node.getName().getText();
                        if (!text.startsWith(".") && text.toUpperCase().contains(token)) {
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
                            if (!text.startsWith(".") && text.toUpperCase().contains(token)) {
                                proposals.add(new ContentProposal(text, text, null));
                            }
                        }
                    }
                }

            });
        }

        Collections.sort(proposals, new Comparator<IContentProposal>() {

            @Override
            public int compare(IContentProposal o1, IContentProposal o2) {
                return o1.getLabel().compareToIgnoreCase(o2.getLabel());
            }

        });

        return proposals;
    }

    public List<IContentProposal> getPAsmProposals(String token) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();

        if (root != null) {
            root.accept(new NodeVisitor() {

                @Override
                public void visitConstantAssign(ConstantAssignNode node) {
                    if (node.identifier != null) {
                        String text = node.identifier.getText();
                        if (text.toUpperCase().contains(token)) {
                            proposals.add(new ContentProposal(text, text, null));
                        }
                    }
                }

                @Override
                public void visitConstantAssignEnum(ConstantAssignEnumNode node) {
                    if (node.identifier != null) {
                        String text = node.identifier.getText();
                        if (text.toUpperCase().contains(token)) {
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
                            if (!text.startsWith(".") && text.toUpperCase().contains(token)) {
                                proposals.add(new ContentProposal(text, text, null));
                            }
                        }
                    }
                }

            });
        }

        Collections.sort(proposals, new Comparator<IContentProposal>() {

            @Override
            public int compare(IContentProposal o1, IContentProposal o2) {
                return o1.getLabel().compareToIgnoreCase(o2.getLabel());
            }

        });

        return proposals;
    }

    public TreeSet<TokenMarker> getTokens() {
        return tokens;
    }

    public Map<String, TokenId> getSymbols() {
        return symbols;
    }

}
