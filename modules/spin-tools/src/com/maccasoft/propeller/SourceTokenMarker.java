/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;

import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenStream;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;

public abstract class SourceTokenMarker {

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
            return start - o.start;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

    }

    protected Node root;
    protected TreeSet<TokenMarker> tokens = new TreeSet<>();
    protected TreeSet<TokenMarker> compilerTokens = new TreeSet<>();

    protected boolean caseSensitive;
    protected String constantSeparator;

    protected Map<String, TokenId> symbols = new CaseInsensitiveMap<>();
    protected Map<String, TokenId> compilerSymbols = new CaseInsensitiveMap<>();
    protected Map<String, TokenId> locals = new CaseInsensitiveMap<>();

    public SourceTokenMarker() {

    }

    public void setSourceRoot(Node root) {
        this.root = root;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        symbols = caseSensitive ? new HashMap<>() : new CaseInsensitiveMap<>();
        compilerSymbols = caseSensitive ? new HashMap<>() : new CaseInsensitiveMap<>();
        locals = caseSensitive ? new HashMap<>() : new CaseInsensitiveMap<>();
    }

    public abstract void refreshTokens(String text);

    public Node getRoot() {
        return root;
    }

    public void refreshCompilerTokens(List<CompilerException> messages) {
        TreeSet<TokenMarker> tokens = new TreeSet<TokenMarker>();
        for (CompilerException message : messages) {
            if (message.hasChilds()) {
                for (CompilerException childMessage : message.getChilds()) {
                    TokenId id = childMessage.type == CompilerException.ERROR ? TokenId.ERROR : TokenId.WARNING;
                    TokenMarker marker = childMessage.getStartToken() != null ? new TokenMarker(childMessage.getStartToken(), childMessage.getStopToken(), id) : new TokenMarker(0, 0, id);
                    marker.setError(childMessage.getMessage());
                    tokens.add(marker);
                }
            }
            else {
                TokenId id = message.type == CompilerException.ERROR ? TokenId.ERROR : TokenId.WARNING;
                TokenMarker marker = message.getStartToken() != null ? new TokenMarker(message.getStartToken(), message.getStopToken(), id) : new TokenMarker(0, 0, id);
                marker.setError(message.getMessage());
                tokens.add(marker);
            }
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
            TokenMarker firstMarker = tokens.floor(new TokenMarker(lineStart, lineStart, null));
            if (firstMarker == null) {
                firstMarker = tokens.first();
            }
            TokenMarker lastMarker = tokens.ceiling(new TokenMarker(lineStop, lineStop, null));
            if (lastMarker == null) {
                lastMarker = tokens.last();
            }
            for (TokenMarker entry : tokens.subSet(firstMarker, lastMarker)) {
                int start = entry.getStart();
                int stop = entry.getStop();
                if (stop >= lineStart && start <= lineStop) {
                    if (start < lineStart) {
                        start = lineStart;
                    }
                    if (stop > lineStop) {
                        stop = lineStop;
                    }
                    result.add(new TokenMarker(start, stop, entry.getId()));
                }
            }
            int start = lastMarker.getStart();
            int stop = lastMarker.getStop();
            if (stop >= lineStart && start <= lineStop) {
                if (start < lineStart) {
                    start = lineStart;
                }
                if (stop > lineStop) {
                    stop = lineStop;
                }
                result.add(new TokenMarker(start, stop, lastMarker.getId()));
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

    public Node getContextAtLine(int lineIndex) {
        if (root == null) {
            return null;
        }

        AtomicReference<Node> result = new AtomicReference<Node>();
        root.accept(new NodeVisitor() {

            @Override
            public boolean visitConstants(ConstantsNode node) {
                if (lineIndex >= node.getStartToken().line) {
                    result.set(node);
                }
                return false;
            }

            @Override
            public boolean visitVariables(VariablesNode node) {
                if (lineIndex >= node.getStartToken().line) {
                    result.set(node);
                }
                return false;
            }

            @Override
            public boolean visitObjects(ObjectsNode node) {
                if (lineIndex >= node.getStartToken().line) {
                    result.set(node);
                }
                return true;
            }

            @Override
            public void visitObject(ObjectNode node) {
                if (lineIndex >= node.getStartToken().line) {
                    result.set(node);
                }
            }

            @Override
            public boolean visitMethod(MethodNode node) {
                if (lineIndex >= node.getStartToken().line) {
                    result.set(node);
                }
                return true;
            }

            @Override
            public boolean visitStatement(StatementNode node) {
                if (lineIndex >= node.getStartToken().line) {
                    result.set(node);
                }
                return true;
            }

            @Override
            public boolean visitData(DataNode node) {
                if (lineIndex >= node.getStartToken().line) {
                    result.set(node);
                }
                return true;
            }

            @Override
            public void visitDataLine(DataLineNode node) {
                if (lineIndex >= node.getStartToken().line) {
                    result.set(node);
                }
            }

        });

        return result.get();
    }

    public String getMethod(String symbol) {
        StringBuilder sb = new StringBuilder();

        if (symbol.indexOf('.') != -1) {
            String[] s = symbol.split("[\\.]");
            if (s.length != 2) {
                return null;
            }
            root.accept(new NodeVisitor() {

                @Override
                public void visitObject(ObjectNode node) {
                    if (node.name == null || node.file == null) {
                        return;
                    }
                    if (!node.name.getText().equalsIgnoreCase(s[0])) {
                        return;
                    }
                    String fileName = node.file.getText().substring(1, node.file.getText().length() - 1);
                    Node objectRoot = getObjectTree(fileName);
                    if (objectRoot != null) {
                        objectRoot.accept(new NodeVisitor() {

                            @Override
                            public boolean visitConstant(ConstantNode node) {
                                if (node.getIdentifier() != null) {
                                    if (s[1].equals(node.getIdentifier().getText())) {
                                        sb.append("<b><code>");
                                        sb.append(getHtmlSafeString(node.getText()));
                                        sb.append("</code></b>");
                                    }
                                }
                                return false;
                            }

                            @Override
                            public boolean visitMethod(MethodNode node) {
                                if (node.getName() != null) {
                                    if (s[1].equals(node.getName().getText())) {
                                        sb.append(getMethodDocument(node));
                                    }
                                }
                                return false;
                            }

                        });
                    }
                }

            });
        }
        else {
            root.accept(new NodeVisitor() {

                @Override
                public boolean visitConstant(ConstantNode node) {
                    if (node.getIdentifier() != null) {
                        if (symbol.equals(node.getIdentifier().getText())) {
                            sb.append("<b><code>");
                            sb.append(getHtmlSafeString(node.getText()));
                            sb.append("</b></code>");
                        }
                    }
                    return false;
                }

                @Override
                public boolean visitMethod(MethodNode node) {
                    if (node.getName() != null) {
                        if (symbol.equals(node.getName().getText())) {
                            sb.append(getMethodDocument(node));
                        }
                    }
                    return false;
                }

            });
        }

        return sb.length() != 0 ? sb.toString() : null;
    }

    public List<IContentProposal> getMethodProposals(Node context, String textToken) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        if (root == null) {
            return proposals;
        }

        while (!(context instanceof MethodNode) && context.getParent() != null) {
            context = context.getParent();
        }

        String contextMethodName;
        if ((context instanceof MethodNode) && ((MethodNode) context).getName() != null) {
            contextMethodName = ((MethodNode) context).getName().getText();
        }
        else {
            contextMethodName = "";
        }

        boolean address = textToken.startsWith("@");
        String token = address ? textToken.substring(1) : textToken;

        root.accept(new NodeVisitor() {

            @Override
            public boolean visitMethod(MethodNode node) {
                if (node.getName() != null && !contextMethodName.equals(node.getName().getText())) {
                    String text = node.getName().getText();
                    if (text.toUpperCase().contains(token)) {
                        String content = getMethodInsert(node);
                        int cursorPosition = content.endsWith("()") ? content.length() : (content.indexOf('(') + 1);
                        proposals.add(new ContentProposal(content, text, getMethodDocument(node), cursorPosition));
                    }
                }
                return false;
            }

        });

        root.accept(new NodeVisitor() {

            @Override
            public void visitObject(ObjectNode node) {
                if (node.name == null || node.file == null) {
                    return;
                }
                String text = node.name.getText();
                if (text.toUpperCase().contains(token)) {
                    if (node.count != null) {
                        proposals.add(new ContentProposal(text + "[0]", text, ""));
                    }
                    else {
                        proposals.add(new ContentProposal(text, text, ""));
                    }
                }
                return;
            }

        });

        int dot = token.indexOf('.');
        if (dot != -1) {
            String objectName = token.substring(0, dot);
            if (objectName.indexOf('[') != -1) {
                objectName = objectName.substring(0, objectName.indexOf('['));
            }
            String refName = objectName + token.substring(dot);
            root.accept(new NodeVisitor() {

                @Override
                public void visitObject(ObjectNode objectNode) {
                    if (objectNode.name == null || objectNode.file == null) {
                        return;
                    }
                    String fileName = objectNode.file.getText().substring(1, objectNode.file.getText().length() - 1);
                    Node objectRoot = getObjectTree(fileName);
                    if (objectRoot == null) {
                        return;
                    }
                    objectRoot.accept(new NodeVisitor() {

                        @Override
                        public boolean visitMethod(MethodNode node) {
                            if (node.getType() == null || node.getName() == null) {
                                return false;
                            }
                            if ("PUB".equalsIgnoreCase(node.getType().getText())) {
                                String text = objectNode.name.getText() + "." + node.getName().getText();
                                if (text.toUpperCase().contains(refName)) {
                                    proposals.add(new ContentProposal(getMethodInsert(node), text, getMethodDocument(node)));
                                }
                            }
                            return false;
                        }

                    });
                }
            });
        }

        context.accept(new NodeVisitor() {

            @Override
            public boolean visitMethod(MethodNode node) {
                for (Node child : node.getParameters()) {
                    String text = child.getText();
                    if (text.toUpperCase().contains(token)) {
                        proposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                    }
                }

                for (Node child : node.getReturnVariables()) {
                    String text = child.getText();
                    if (text.toUpperCase().contains(token)) {
                        proposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                    }
                }

                for (Node child : node.getLocalVariables()) {
                    String text = child.getText();
                    if (text.toUpperCase().contains(token)) {
                        proposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                    }
                }

                return false;
            }

        });

        root.accept(new NodeVisitor() {

            @Override
            public boolean visitConstant(ConstantNode node) {
                if (node.identifier != null) {
                    String text = node.identifier.getText();
                    if (text.toUpperCase().contains(token)) {
                        proposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                    }
                }
                return false;
            }

            @Override
            public void visitVariable(VariableNode node) {
                if (node.getIdentifier() != null) {
                    String text = node.getIdentifier().getText();
                    if (!text.startsWith(".") && text.toUpperCase().contains(token)) {
                        proposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                    }
                }
            }

        });

        if (token.indexOf(constantSeparator) != -1) {
            root.accept(new NodeVisitor() {

                @Override
                public void visitObject(ObjectNode objectNode) {
                    if (objectNode.name == null || objectNode.file == null) {
                        return;
                    }
                    String fileName = objectNode.file.getText().substring(1, objectNode.file.getText().length() - 1);
                    Node objectRoot = getObjectTree(fileName);
                    if (objectRoot == null) {
                        return;
                    }
                    objectRoot.accept(new NodeVisitor() {

                        @Override
                        public boolean visitConstant(ConstantNode node) {
                            if (node.identifier != null) {
                                String text = objectNode.name.getText() + constantSeparator + node.identifier.getText();
                                if (text.toUpperCase().contains(token)) {
                                    proposals.add(new ContentProposal(node.identifier.getText(), text, "<b>" + node.getText() + "</b>"));
                                }
                            }
                            return false;
                        }

                    });
                }
            });
        }

        root.accept(new NodeVisitor() {

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

        return proposals;
    }

    String getMethodInsert(Node node) {
        Iterator<Token> iter = node.getTokens().iterator();
        Token token = iter.next();
        TokenStream stream = token.getStream();

        if (iter.hasNext()) {
            Token start = iter.next();
            Token stop = start;
            while (iter.hasNext()) {
                Token next = iter.next();
                if (")".equals(next.getText())) {
                    stop = next;
                    break;
                }
                if (":".equals(next.getText()) || "|".equals(next.getText())) {
                    break;
                }
            }
            return stream.getSource(start.start, stop.stop).trim();
        }

        return null;
    }

    public List<IContentProposal> getConstantsProposals(Node context, String token) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        if (root == null) {
            return proposals;
        }

        root.accept(new NodeVisitor() {

            @Override
            public boolean visitConstant(ConstantNode node) {
                if (node.identifier != null) {
                    String text = node.identifier.getText();
                    if (text.toUpperCase().contains(token)) {
                        proposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                    }
                }
                return false;
            }

        });
        root.accept(new NodeVisitor() {

            @Override
            public void visitObject(ObjectNode objectNode) {
                if (objectNode.name == null || objectNode.file == null) {
                    return;
                }
                String fileName = objectNode.file.getText().substring(1, objectNode.file.getText().length() - 1);
                Node objectRoot = getObjectTree(fileName);
                if (objectRoot == null) {
                    return;
                }
                objectRoot.accept(new NodeVisitor() {

                    @Override
                    public boolean visitConstant(ConstantNode node) {
                        if (node.identifier != null) {
                            String text = objectNode.name.getText() + "." + node.identifier.getText();
                            if (text.toUpperCase().contains(token)) {
                                proposals.add(new ContentProposal(node.identifier.getText(), text, "<b>" + node.getText() + "</b>"));
                            }
                        }
                        return false;
                    }

                });
            }
        });

        return proposals;
    }

    String getMethodDocument(MethodNode node) {
        Iterator<Token> iter = node.getTokens().iterator();
        Token start = iter.next();
        Token stop = start;
        TokenStream stream = start.getStream();

        while (iter.hasNext()) {
            stop = iter.next();
            if (")".equals(stop.getText())) {
                break;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<pre><b><code>");
        sb.append(getHtmlSafeString(stream.getSource(start.start, stop.stop).trim()));
        sb.append("</code></b>");
        sb.append(System.lineSeparator());

        iter = node.getDocument().iterator();
        if (iter.hasNext()) {
            sb.append(System.lineSeparator());
            sb.append("<code>");
            while (iter.hasNext()) {
                Token token = iter.next();
                if (token.type == Token.COMMENT) {
                    String s = token.getText().substring(2);
                    sb.append(getHtmlSafeString(s));
                    sb.append(System.lineSeparator());
                }
                else if (token.type == Token.BLOCK_COMMENT) {
                    String s = token.getText().substring(2);
                    s = s.substring(0, s.length() - 2);
                    s = getHtmlSafeString(s);
                    s = s.replaceAll("[\\r\\n|\\r|\\n]", System.lineSeparator());
                    while (s.startsWith(System.lineSeparator())) {
                        s = s.substring(System.lineSeparator().length());
                    }
                    while (s.endsWith(System.lineSeparator())) {
                        s = s.substring(0, s.length() - System.lineSeparator().length());
                    }
                    sb.append(s);
                }
            }
            sb.append("</code>");
        }
        sb.append("</pre>");

        return sb.toString();
    }

    String getHtmlSafeString(String s) {
        s = s.replaceAll("&", "&amp;");
        s = s.replaceAll("<", "&lt;");
        s = s.replaceAll(">", "&gt;");
        return s;
    }

    protected Node getObjectTree(String fileName) {
        return null;
    }

    public List<IContentProposal> getPAsmProposals(Node ref, String token) {
        List<String> pasmLabels = new ArrayList<String>();
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();

        if (root != null) {
            root.accept(new NodeVisitor() {

                @Override
                public boolean visitConstant(ConstantNode node) {
                    if (node.identifier != null) {
                        String text = node.identifier.getText();
                        if (text.toUpperCase().contains(token)) {
                            proposals.add(new ContentProposal(text, text, null));
                        }
                    }
                    return false;
                }

                @Override
                public boolean visitData(DataNode node) {
                    String lastLabel = "";

                    for (Node child : node.getChilds()) {
                        DataLineNode lineNode = (DataLineNode) child;
                        if (lineNode.label != null) {
                            String text = lineNode.label.getText();
                            if (text.startsWith(".")) {
                                pasmLabels.add(lastLabel + text);
                            }
                            else {
                                lastLabel = text;
                            }
                        }
                    }

                    return true;
                }

            });
        }

        for (Node node : root.getChilds()) {
            if (node instanceof DataNode) {
                String lastLabel = "";
                for (Node child : node.getChilds()) {
                    DataLineNode lineNode = (DataLineNode) child;
                    if (lineNode.label != null) {
                        String text = lineNode.label.getText();
                        if (!text.startsWith(".")) {
                            lastLabel = text;
                        }
                    }
                    if (lineNode == ref) {
                        String s1 = lastLabel + ".";
                        for (String s : pasmLabels) {
                            if (s.startsWith(s1)) {
                                if (s.substring(s1.length() - 1).toUpperCase().contains(token)) {
                                    String text = s.substring(s1.length() - 1);
                                    if (text.toUpperCase().contains(token)) {
                                        proposals.add(new ContentProposal(text, text, null));
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }

        for (Node node : root.getChilds()) {
            if (node instanceof DataNode) {
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
        }

        return proposals;
    }

    public TreeSet<TokenMarker> getTokens() {
        return tokens;
    }

    public Map<String, TokenId> getSymbols() {
        return symbols;
    }

}
