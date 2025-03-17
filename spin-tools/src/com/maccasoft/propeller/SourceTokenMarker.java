/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;

import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.FunctionNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.RootNode;
import com.maccasoft.propeller.model.SourceProvider;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenStream;
import com.maccasoft.propeller.model.TypeDefinitionNode;
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
        DIRECTIVE,
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

        CON,
        VAR,
        OBJ,
        PUB,
        PRI,
        DAT,

        CON_ALT,
        VAR_ALT,
        OBJ_ALT,
        PUB_ALT,
        PRI_ALT,
        DAT_ALT,

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
            if (start != o.start) {
                return start - o.start;
            }
            return stop - o.stop;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        @Override
        public String toString() {
            return "TokenMarker [start=" + start + ", stop=" + stop + ", id=" + id;
        }

    }

    protected SourceProvider sourceProvider;

    protected RootNode root;
    protected TreeSet<TokenMarker> tokens = new TreeSet<>();
    protected TreeSet<TokenMarker> compilerTokens = new TreeSet<>();
    protected TreeSet<TokenMarker> excludedNodes = new TreeSet<>();

    protected boolean caseSensitive;
    protected String constantSeparator;
    protected String localLabelPrefix;

    protected Map<String, TokenId> externals = new CaseInsensitiveMap<>();
    protected Map<String, TokenId> symbols = new CaseInsensitiveMap<>();
    protected Map<String, TokenId> locals = new CaseInsensitiveMap<>();

    protected Map<File, RootNode> cache = new HashMap<>();
    protected Set<String> excludedPaths = new HashSet<>();

    public SourceTokenMarker(SourceProvider sourceProvider) {
        this.sourceProvider = sourceProvider;
    }

    public void setSourceRoot(RootNode root) {
        this.root = root;
        this.constantSeparator = "";
        this.localLabelPrefix = "";
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        symbols = caseSensitive ? new HashMap<>() : new CaseInsensitiveMap<>();
        locals = caseSensitive ? new HashMap<>() : new CaseInsensitiveMap<>();
    }

    public abstract void refreshTokens(String text);

    public RootNode getRoot() {
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

        excludedPaths.clear();
        excludedNodes.clear();

        root.accept(new NodeVisitor() {

            @Override
            public void visitDirective(DirectiveNode node) {
                if (node.isExclude()) {
                    addToExcluded(node);
                }
            }

            @Override
            public boolean visitConstants(ConstantsNode node) {
                if (node.isExclude()) {
                    addToExcluded(node);
                }
                return true;
            }

            @Override
            public void visitConstant(ConstantNode node) {
                if (node.isExclude()) {
                    addToExcluded(node);
                }
            }

            @Override
            public boolean visitVariables(VariablesNode node) {
                if (node.isExclude()) {
                    addToExcluded(node);
                }
                return true;
            }

            @Override
            public boolean visitObjects(ObjectsNode node) {
                if (node.isExclude()) {
                    addToExcluded(node);
                }
                return true;
            }

            @Override
            public void visitObject(ObjectNode node) {
                if (node.isExclude()) {
                    addToExcluded(node);
                }
            }

            @Override
            public boolean visitMethod(MethodNode node) {
                if (node.isExclude()) {
                    addToExcluded(node);
                }
                return true;
            }

            @Override
            public boolean visitFunction(FunctionNode node) {
                if (node.isExclude()) {
                    addToExcluded(node);
                }
                return true;
            }

            @Override
            public boolean visitStatement(StatementNode node) {
                if (node.isExclude()) {
                    addToExcluded(node);
                }
                return true;
            }

            @Override
            public boolean visitData(DataNode node) {
                if (node.isExclude()) {
                    addToExcluded(node);
                }
                return true;
            }

            @Override
            public void visitDataLine(DataLineNode node) {
                super.visitDataLine(node);
            }

        });

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
            if (marker.id == TokenId.ERROR) {
                if (offset >= marker.start && offset <= marker.stop) {
                    return marker;
                }
            }
        }
        for (TokenMarker marker : compilerTokens) {
            if (marker.id == TokenId.WARNING) {
                if (offset >= marker.start && offset <= marker.stop) {
                    return marker;
                }
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
        Set<TokenMarker> result = new TreeSet<>();

        if (tokens.size() != 0) {
            TokenMarker firstMarker = tokens.floor(new TokenMarker(lineStart, lineStart, null));
            if (firstMarker == null) {
                firstMarker = tokens.first();
            }
            if (firstMarker.start <= lineStart && firstMarker.stop >= lineStop) {
                result.add(new TokenMarker(lineStart, lineStop, firstMarker.getId()));
                return result;
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
                    lineStart = stop + 1;
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

    public Node getSectionAtLine(int lineIndex) {
        Node result = null;

        if (root != null) {
            for (Node node : root.getChilds()) {
                if (node.getTokenCount() != 0) {
                    if (lineIndex < node.getStartToken().line) {
                        break;
                    }
                    result = node;
                }
            }
        }

        return result;
    }

    public Node getContextAtLine(int lineIndex) {
        if (root == null) {
            return null;
        }

        AtomicReference<Node> result = new AtomicReference<Node>();
        root.accept(new NodeVisitor() {

            @Override
            public boolean visitConstants(ConstantsNode node) {
                if (node.getTokenCount() != 0 && lineIndex >= node.getStartToken().line) {
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
            public void visitVariable(VariableNode node) {
                if (lineIndex >= node.getStartToken().line) {
                    result.set(node);
                }
            }

            @Override
            public boolean visitObjects(ObjectsNode node) {
                if (lineIndex >= node.getStartToken().line) {
                    result.set(node);
                    return true;
                }
                return false;
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
                    return true;
                }
                return false;
            }

            @Override
            public boolean visitFunction(FunctionNode node) {
                if (lineIndex >= node.getStartToken().line) {
                    result.set(node);
                    return true;
                }
                return false;
            }

            @Override
            public boolean visitStatement(StatementNode node) {
                if (node.getStartToken() == null) {
                    return true;
                }
                if (lineIndex >= node.getStartToken().line) {
                    result.set(node);
                    return true;
                }
                return false;
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
                public void visitVariable(VariableNode node) {
                    if (node.getType() == null || node.getIdentifier() == null) {
                        return;
                    }
                    Node objectRoot = getObjectTree(node.getType().getText());
                    if (objectRoot != null) {
                        String name = node.getIdentifier().getText();
                        if (!name.equalsIgnoreCase(s[0])) {
                            return;
                        }
                        objectRoot.accept(new NodeVisitor() {

                            @Override
                            public boolean visitFunction(FunctionNode node) {
                                if (node.getIdentifier() != null) {
                                    if (s[1].equals(node.getIdentifier().getText())) {
                                        sb.append(getMethodDocument(node));
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

                @Override
                public void visitObject(ObjectNode node) {
                    if (node.name == null || node.file == null) {
                        return;
                    }
                    if (!node.name.getText().equalsIgnoreCase(s[0])) {
                        return;
                    }
                    String fileName = node.getFileName();
                    Node objectRoot = getObjectTree(fileName);
                    if (objectRoot != null) {
                        objectRoot.accept(new NodeVisitor() {

                            @Override
                            public void visitConstant(ConstantNode node) {
                                if (node.getIdentifier() != null) {
                                    if (s[1].equals(node.getIdentifier().getText())) {
                                        sb.append("<b><code>");
                                        sb.append(getHtmlSafeString(node.getText()));
                                        sb.append("</code></b>");
                                    }
                                }
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
                public void visitConstant(ConstantNode node) {
                    if (node.getIdentifier() != null) {
                        if (symbol.equals(node.getIdentifier().getText())) {
                            sb.append("<b><code>");
                            sb.append(getHtmlSafeString(node.getText()));
                            sb.append("</b></code>");
                        }
                    }
                }

                @Override
                public boolean visitFunction(FunctionNode node) {
                    if (node.getIdentifier() != null) {
                        if (symbol.equals(node.getIdentifier().getText())) {
                            sb.append(getMethodDocument(node));
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

        while (!(context instanceof MethodNode) && !(context instanceof FunctionNode) && context.getParent() != null) {
            context = context.getParent();
        }

        String contextMethodName;
        if ((context instanceof MethodNode) && ((MethodNode) context).getName() != null) {
            contextMethodName = ((MethodNode) context).getName().getText();
        }
        else if ((context instanceof FunctionNode) && ((FunctionNode) context).getIdentifier() != null) {
            contextMethodName = ((FunctionNode) context).getIdentifier().getText();
        }
        else {
            contextMethodName = "";
        }

        boolean address = textToken.startsWith("@");
        String filterText = address ? textToken.substring(1) : textToken;

        int dot = filterText.indexOf('.');
        String objectName = "";
        if (dot != -1) {
            objectName = filterText.substring(0, dot);
            if (objectName.indexOf('[') != -1) {
                objectName = objectName.substring(0, objectName.indexOf('['));
            }
        }
        String refName = dot != -1 ? filterText.substring(dot + 1) : filterText;
        String refObject = objectName;

        Map<String, TypeDefinitionNode> structs = new HashMap<>();

        root.accept(new NodeVisitor() {

            @Override
            public void visitTypeDefinition(TypeDefinitionNode node) {
                if (!node.isExclude() && node.identifier != null) {
                    structs.put(node.identifier.getText(), node);
                }
            }

        });

        context.accept(new NodeVisitor() {

            @Override
            public boolean visitMethod(MethodNode node) {
                for (MethodNode.ParameterNode child : node.getParameters()) {
                    String text = child.getIdentifier().getText();
                    if (dot == -1) {
                        if (StringUtils.containsIgnoreCase(text, filterText)) {
                            proposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                        }
                    }
                    else if (child.getType() != null) {
                        if (refObject.equalsIgnoreCase(child.getIdentifier().getText())) {
                            visitStructureMembers(child.getIdentifier().getText(), child.getType().getText());
                        }
                    }
                }

                for (MethodNode.LocalVariableNode child : node.getLocalVariables()) {
                    String text = child.getIdentifier().getText();
                    if (dot == -1) {
                        if (StringUtils.containsIgnoreCase(text, filterText)) {
                            proposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                        }
                    }
                    else if (child.getType() != null) {
                        if (refObject.equalsIgnoreCase(child.getIdentifier().getText())) {
                            visitStructureMembers(child.getIdentifier().getText(), child.getType().getText());
                        }
                    }
                }

                for (MethodNode.ReturnNode child : node.getReturnVariables()) {
                    String text = child.getIdentifier().getText();
                    if (dot == -1) {
                        if (StringUtils.containsIgnoreCase(text, filterText)) {
                            proposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                        }
                    }
                    else if (child.getType() != null) {
                        if (refObject.equalsIgnoreCase(child.getIdentifier().getText())) {
                            visitStructureMembers(child.getIdentifier().getText(), child.getType().getText());
                        }
                    }
                }

                return false;
            }

            void visitStructureMembers(String identifier, String type) {
                TypeDefinitionNode typeNode = structs.get(type);
                if (typeNode != null) {
                    for (Node n : typeNode.getChilds()) {
                        if (!(n instanceof TypeDefinitionNode.Definition)) {
                            continue;
                        }
                        TypeDefinitionNode.Definition member = (TypeDefinitionNode.Definition) n;
                        String text = member.getIdentifier().getText();
                        if (StringUtils.containsIgnoreCase(text, refName)) {
                            proposals.add(new ContentProposal(text, text, "<b>" + typeNode.getText() + "</b>"));
                        }
                    }
                }
            }

        });

        root.accept(new NodeVisitor() {

            @Override
            public void visitVariable(VariableNode node) {
                if (node.getIdentifier() != null && !contextMethodName.equals(node.getIdentifier().getText())) {
                    String text = node.getIdentifier().getText();
                    if (dot == -1) {
                        if (StringUtils.containsIgnoreCase(text, filterText)) {
                            proposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                        }
                    }
                    else if (node.getType() != null) {
                        if (refObject.equalsIgnoreCase(node.getIdentifier().getText())) {
                            visitStructureMembers(node.getIdentifier().getText(), node.getType().getText());
                        }
                    }
                }
            }

            void visitStructureMembers(String identifier, String type) {
                TypeDefinitionNode typeNode = structs.get(type);
                if (typeNode != null) {
                    for (Node n : typeNode.getChilds()) {
                        if (!(n instanceof TypeDefinitionNode.Definition)) {
                            continue;
                        }
                        TypeDefinitionNode.Definition member = (TypeDefinitionNode.Definition) n;
                        String text = member.getIdentifier().getText();
                        if (StringUtils.containsIgnoreCase(text, refName)) {
                            proposals.add(new ContentProposal(text, text, "<b>" + typeNode.getText() + "</b>"));
                        }
                    }
                }
            }

        });

        root.accept(new NodeVisitor() {

            @Override
            public boolean visitMethod(MethodNode node) {
                if (node.getName() != null && !contextMethodName.equals(node.getName().getText())) {
                    String text = node.getName().getText();
                    if (StringUtils.containsIgnoreCase(text, filterText)) {
                        String content = getMethodInsert(node);
                        int cursorPosition = content.endsWith("()") ? content.length() : (content.indexOf('(') + 1);
                        proposals.add(new ContentProposal(content, text, getMethodDocument(node), cursorPosition));
                    }
                }
                return false;
            }

            @Override
            public boolean visitFunction(FunctionNode node) {
                if (node.getIdentifier() != null && !contextMethodName.equals(node.getIdentifier().getText())) {
                    String text = node.getIdentifier().getText();
                    if (StringUtils.containsIgnoreCase(text, filterText)) {
                        String content = getMethodInsert(node);
                        int cursorPosition = content.endsWith("()") ? content.length() : (content.indexOf('(') + 1);
                        proposals.add(new ContentProposal(content, text, "", cursorPosition));
                    }
                }
                return false;
            }

        });

        root.accept(new NodeVisitor() {

            @Override
            public void visitDirective(DirectiveNode node) {
                Iterator<Token> iter = node.getTokens().iterator();
                if (iter.hasNext()) {
                    iter.next();
                }
                if (iter.hasNext()) {
                    Token directive = iter.next();
                    if ("include".equals(directive.getText())) {
                        if (iter.hasNext()) {
                            Token file = iter.next();
                            String name = file.getText().substring(1, file.getText().length() - 1);
                            if (StringUtils.containsIgnoreCase(name, filterText)) {
                                proposals.add(new ContentProposal(name, name, ""));
                            }
                        }
                    }
                }
            }

            @Override
            public void visitObject(ObjectNode node) {
                if (node.name == null || node.file == null) {
                    return;
                }
                String text = node.name.getText();
                if (StringUtils.containsIgnoreCase(text, filterText)) {
                    proposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                }
            }

        });

        if (dot != -1) {
            List<IContentProposal> secondary = new ArrayList<>();

            root.accept(new NodeVisitor() {

                @Override
                public void visitVariable(VariableNode node) {
                    if (node.getType() == null || node.getIdentifier() == null) {
                        return;
                    }
                    String name = node.getIdentifier().getText();
                    if (!refObject.equalsIgnoreCase(name)) {
                        return;
                    }

                    Node objectRoot = getObjectTree(node.getType().getText());
                    if (objectRoot != null) {
                        objectRoot.accept(new NodeVisitor() {

                            @Override
                            public boolean visitFunction(FunctionNode node) {
                                if (node.getIdentifier() != null) {
                                    String text = node.getIdentifier().getText();
                                    if (StringUtils.startsWithIgnoreCase(text, refName)) {
                                        proposals.add(new ContentProposal(getMethodInsert(node), text, getMethodDocument(node)));
                                    }
                                    else if (StringUtils.containsIgnoreCase(text, refName)) {
                                        secondary.add(new ContentProposal(getMethodInsert(node), text, getMethodDocument(node)));
                                    }
                                }
                                return false;
                            }

                            @Override
                            public boolean visitMethod(MethodNode node) {
                                if (node.getType() == null || node.getName() == null) {
                                    return false;
                                }
                                if ("PUB".equalsIgnoreCase(node.getType().getText())) {
                                    String text = node.getName().getText();
                                    if (StringUtils.startsWithIgnoreCase(text, refName)) {
                                        proposals.add(new ContentProposal(getMethodInsert(node), text, getMethodDocument(node)));
                                    }
                                    else if (StringUtils.containsIgnoreCase(text, refName)) {
                                        secondary.add(new ContentProposal(getMethodInsert(node), text, getMethodDocument(node)));
                                    }
                                }
                                return false;
                            }

                        });
                    }
                }

                @Override
                public void visitObject(ObjectNode objectNode) {
                    if (objectNode.name == null || objectNode.file == null) {
                        return;
                    }
                    if (!refObject.equalsIgnoreCase(objectNode.name.getText())) {
                        return;
                    }
                    String fileName = objectNode.getFileName();
                    Node objectRoot = getObjectTree(fileName);
                    if (objectRoot == null) {
                        return;
                    }
                    objectRoot.accept(new NodeVisitor() {

                        @Override
                        public boolean visitFunction(FunctionNode node) {
                            if (node.getIdentifier() != null) {
                                String text = node.getIdentifier().getText();
                                if (StringUtils.startsWithIgnoreCase(text, refName)) {
                                    proposals.add(new ContentProposal(getMethodInsert(node), text, ""));
                                }
                                else if (StringUtils.containsIgnoreCase(text, refName)) {
                                    secondary.add(new ContentProposal(getMethodInsert(node), text, ""));
                                }
                            }
                            return false;
                        }

                        @Override
                        public boolean visitMethod(MethodNode node) {
                            if (node.getType() == null || node.getName() == null) {
                                return false;
                            }
                            if ("PUB".equalsIgnoreCase(node.getType().getText())) {
                                String text = node.getName().getText();
                                if (StringUtils.startsWithIgnoreCase(text, refName)) {
                                    proposals.add(new ContentProposal(getMethodInsert(node), text, getMethodDocument(node)));
                                }
                                else if (StringUtils.containsIgnoreCase(text, refName)) {
                                    secondary.add(new ContentProposal(getMethodInsert(node), text, getMethodDocument(node)));
                                }
                            }
                            return false;
                        }

                    });
                }
            });

            proposals.addAll(secondary);
        }

        return proposals;
    }

    public List<IContentProposal> getInlinePAsmProposals(Node context, String filterText) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        if (root == null) {
            return proposals;
        }

        Node method = context;
        while (!(method instanceof MethodNode) && !(method instanceof FunctionNode) && method.getParent() != null) {
            method = method.getParent();
        }
        for (Node node : method.getChilds()) {
            if (node instanceof MethodNode.ParameterNode) {
                MethodNode.ParameterNode child = (MethodNode.ParameterNode) node;
                String text = child.getIdentifier().getText();
                if (StringUtils.containsIgnoreCase(text, filterText)) {
                    proposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                }
            }
            else if (node instanceof MethodNode.LocalVariableNode) {
                MethodNode.LocalVariableNode child = (MethodNode.LocalVariableNode) node;
                String text = child.getIdentifier().getText();
                if (StringUtils.containsIgnoreCase(text, filterText)) {
                    proposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                }
            }
            else if (node instanceof MethodNode.ReturnNode) {
                MethodNode.ReturnNode child = (MethodNode.ReturnNode) node;
                String text = child.getIdentifier().getText();
                if (StringUtils.containsIgnoreCase(text, filterText)) {
                    proposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                }
            }
        }

        List<String> pasmLabels = new ArrayList<String>();

        String lastLabel = "";

        for (Node child : context.getParent().getChilds()) {
            if (!(child instanceof DataLineNode)) {
                continue;
            }
            DataLineNode lineNode = (DataLineNode) child;
            if (lineNode.label != null) {
                String text = lineNode.label.getText();
                if (text.startsWith(localLabelPrefix)) {
                    pasmLabels.add(lastLabel + text);
                }
                else {
                    lastLabel = text;
                }
            }
        }

        lastLabel = "";
        for (Node child : context.getParent().getChilds()) {
            if (!(child instanceof DataLineNode)) {
                continue;
            }
            DataLineNode lineNode = (DataLineNode) child;
            if (lineNode.label != null) {
                String text = lineNode.label.getText();
                if (!text.startsWith(localLabelPrefix)) {
                    lastLabel = text;
                }
            }
            if (lineNode == context) {
                String s1 = lastLabel + localLabelPrefix;
                for (String s : pasmLabels) {
                    if (StringUtils.startsWithIgnoreCase(s, s1)) {
                        if (StringUtils.containsIgnoreCase(s.substring(s1.length() - 1), filterText)) {
                            String text = s.substring(s1.length() - 1);
                            if (StringUtils.containsIgnoreCase(text, filterText)) {
                                proposals.add(new ContentProposal(text, text, null));
                            }
                        }
                    }
                }
                break;
            }
        }

        for (Node child : context.getParent().getChilds()) {
            if (!(child instanceof DataLineNode)) {
                continue;
            }
            DataLineNode lineNode = (DataLineNode) child;
            if (lineNode.label != null) {
                String text = lineNode.label.getText();
                if (!text.startsWith(localLabelPrefix) && StringUtils.containsIgnoreCase(text, filterText)) {
                    proposals.add(new ContentProposal(text, text, null));
                }
            }
        }

        return proposals;
    }

    public List<IContentProposal> getObjectsProposals(Node context, String textToken) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        if (root == null) {
            return proposals;
        }

        textToken = textToken.startsWith("@") ? textToken.substring(1) : textToken;

        int dot = textToken.indexOf('.');
        if (dot != -1) {
            String objectName = textToken.substring(0, dot);
            if (objectName.indexOf('[') != -1) {
                objectName = objectName.substring(0, objectName.indexOf('['));
            }
            textToken = objectName + textToken.substring(dot);
        }

        String filterText = textToken;

        root.accept(new NodeVisitor() {

            @Override
            public void visitVariable(VariableNode node) {
                if (node.getType() == null || node.getIdentifier() == null) {
                    return;
                }
                Node objectRoot = getObjectTree(node.getType().getText());
                if (objectRoot != null) {
                    String name = node.getIdentifier().getText();
                    if (StringUtils.containsIgnoreCase(name, filterText)) {
                        proposals.add(new ContentProposal(name, name, ""));
                    }
                    if (dot != -1) {
                        objectRoot.accept(new NodeVisitor() {

                            @Override
                            public boolean visitFunction(FunctionNode node) {
                                if (node.getIdentifier() != null) {
                                    String text = name + "." + node.getIdentifier().getText();
                                    if (StringUtils.containsIgnoreCase(text, filterText)) {
                                        proposals.add(new ContentProposal(getMethodInsert(node), text, getMethodDocument(node)));
                                    }
                                }
                                return false;
                            }

                            @Override
                            public boolean visitMethod(MethodNode node) {
                                if (node.getType() == null || node.getName() == null) {
                                    return false;
                                }
                                if ("PUB".equalsIgnoreCase(node.getType().getText())) {
                                    String text = name + "." + node.getName().getText();
                                    if (StringUtils.containsIgnoreCase(text, filterText)) {
                                        proposals.add(new ContentProposal(getMethodInsert(node), text, getMethodDocument(node)));
                                    }
                                }
                                return false;
                            }

                        });
                    }
                }
            }

            @Override
            public void visitObject(ObjectNode objectNode) {
                if (objectNode.name == null || objectNode.file == null) {
                    return;
                }
                String name = objectNode.name.getText();
                if (StringUtils.containsIgnoreCase(name, filterText)) {
                    proposals.add(new ContentProposal(name, name, ""));
                }
                if (dot != -1) {
                    String fileName = objectNode.getFileName();
                    Node objectRoot = getObjectTree(fileName);
                    if (objectRoot == null) {
                        return;
                    }
                    objectRoot.accept(new NodeVisitor() {

                        @Override
                        public boolean visitFunction(FunctionNode node) {
                            if (node.getIdentifier() != null) {
                                String text = name + "." + node.getIdentifier().getText();
                                if (StringUtils.containsIgnoreCase(text, filterText)) {
                                    proposals.add(new ContentProposal(getMethodInsert(node), text, ""));
                                }
                            }
                            return false;
                        }

                        @Override
                        public boolean visitMethod(MethodNode node) {
                            if (node.getType() == null || node.getName() == null) {
                                return false;
                            }
                            if ("PUB".equalsIgnoreCase(node.getType().getText())) {
                                String text = name + "." + node.getName().getText();
                                if (StringUtils.containsIgnoreCase(text, filterText)) {
                                    proposals.add(new ContentProposal(getMethodInsert(node), text, getMethodDocument(node)));
                                }
                            }
                            return false;
                        }

                    });
                }
            }

        });

        return proposals;
    }

    protected String getMethodInsert(MethodNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(node.name.getText());
        sb.append("(");

        int i = 0;
        while (i < node.getParametersCount()) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(node.getParameter(i).identifier.getText());
            i++;
        }

        sb.append(")");

        return sb.toString();
    }

    protected String getMethodInsert(FunctionNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(node.identifier.getText());
        sb.append("(");

        int i = 0;
        while (i < node.getParametersCount()) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(node.getParameter(i).identifier.getText());
            i++;
        }

        sb.append(")");

        return sb.toString();
    }

    public List<IContentProposal> getTypeProposals(Node context, String filterText) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        if (root == null) {
            return proposals;
        }

        List<IContentProposal> localProposals = new ArrayList<IContentProposal>();
        root.accept(new NodeVisitor() {

            @Override
            public void visitTypeDefinition(TypeDefinitionNode node) {
                if (node.getIdentifier() != null) {
                    String text = node.getIdentifier().getText();
                    if (StringUtils.containsIgnoreCase(text, filterText)) {
                        localProposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                    }
                }
            }

        });
        Collections.sort(localProposals, new Comparator<IContentProposal>() {

            @Override
            public int compare(IContentProposal o1, IContentProposal o2) {
                return o1.getLabel().compareToIgnoreCase(o2.getLabel());
            }

        });
        proposals.addAll(localProposals);

        List<IContentProposal> objectProposals = new ArrayList<IContentProposal>();
        root.accept(new NodeVisitor() {

            @Override
            public void visitObject(ObjectNode objectNode) {
                if (objectNode.name == null || objectNode.file == null) {
                    return;
                }
                String fileName = objectNode.getFileName();
                Node objectRoot = getObjectTree(fileName);
                if (objectRoot == null) {
                    return;
                }
                objectRoot.accept(new NodeVisitor() {

                    @Override
                    public void visitTypeDefinition(TypeDefinitionNode node) {
                        if (node.getIdentifier() != null) {
                            String text = objectNode.name.getText() + "." + node.getIdentifier().getText();
                            if (StringUtils.containsIgnoreCase(text, filterText)) {
                                objectProposals.add(new ContentProposal(node.getIdentifier().getText(), text, "<b>" + node.getText() + "</b>"));
                            }
                        }
                    }

                });
            }

        });
        Collections.sort(objectProposals, new Comparator<IContentProposal>() {

            @Override
            public int compare(IContentProposal o1, IContentProposal o2) {
                return o1.getLabel().compareToIgnoreCase(o2.getLabel());
            }

        });
        proposals.addAll(objectProposals);

        return proposals;
    }

    public List<IContentProposal> getConstantsProposals(Node context, String filterText) {
        List<IContentProposal> proposals = new ArrayList<>();
        if (root == null) {
            return proposals;
        }

        int dot = filterText.indexOf(constantSeparator);

        root.accept(new NodeVisitor() {

            @Override
            public void visitDirective(DirectiveNode node) {
                if (node instanceof DirectiveNode.DefineNode) {
                    DirectiveNode.DefineNode define = (DirectiveNode.DefineNode) node;
                    if (define.getIdentifier() != null) {
                        String text = define.getIdentifier().getText();
                        if (StringUtils.containsIgnoreCase(text, filterText)) {
                            proposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                        }
                    }
                }
            }

            @Override
            public void visitConstant(ConstantNode node) {
                if (node.identifier != null) {
                    String text = node.identifier.getText();
                    if (StringUtils.containsIgnoreCase(text, filterText)) {
                        proposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                    }
                }
            }

        });

        if (dot != -1) {
            String refObject = dot != -1 ? filterText.substring(0, dot) : "";
            String refName = dot != -1 ? filterText.substring(dot + 1) : filterText;
            List<IContentProposal> secondary = new ArrayList<>();

            root.accept(new NodeVisitor() {

                @Override
                public void visitDirective(DirectiveNode node) {
                    if (node instanceof DirectiveNode.IncludeNode) {
                        DirectiveNode.IncludeNode include = (DirectiveNode.IncludeNode) node;
                        if (include.getFile() == null) {
                            return;
                        }

                        Node objectRoot = getObjectTree(include.getFileName());
                        if (objectRoot != null) {
                            objectRoot.accept(new NodeVisitor() {

                                @Override
                                public void visitDirective(DirectiveNode node) {
                                    if (node instanceof DirectiveNode.DefineNode) {
                                        DirectiveNode.DefineNode define = (DirectiveNode.DefineNode) node;
                                        if (define.getIdentifier() != null) {
                                            String text = define.getIdentifier().getText();
                                            if (StringUtils.containsIgnoreCase(text, filterText)) {
                                                proposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void visitConstant(ConstantNode node) {
                                    if (node.identifier != null) {
                                        String text = node.identifier.getText();
                                        if (StringUtils.containsIgnoreCase(text, filterText)) {
                                            proposals.add(new ContentProposal(node.identifier.getText(), text, "<b>" + node.getText() + "</b>"));
                                        }
                                    }
                                }

                            });
                        }
                    }
                }

                @Override
                public void visitObject(ObjectNode objectNode) {
                    if (objectNode.name == null || objectNode.file == null) {
                        return;
                    }
                    if (!refObject.equalsIgnoreCase(objectNode.name.getText())) {
                        return;
                    }
                    String fileName = objectNode.getFileName();
                    Node objectRoot = getObjectTree(fileName);
                    if (objectRoot == null) {
                        return;
                    }
                    objectRoot.accept(new NodeVisitor() {

                        @Override
                        public void visitConstant(ConstantNode node) {
                            if (node.identifier != null) {
                                String text = node.identifier.getText();
                                if (StringUtils.startsWithIgnoreCase(text, refName)) {
                                    proposals.add(new ContentProposal(node.identifier.getText(), text, "<b>" + node.getText() + "</b>"));
                                }
                                else if (StringUtils.containsIgnoreCase(text, refName)) {
                                    secondary.add(new ContentProposal(node.identifier.getText(), text, "<b>" + node.getText() + "</b>"));
                                }
                            }
                        }

                    });
                }
            });

            proposals.addAll(secondary);
        }

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

    String getMethodDocument(FunctionNode node) {
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

    protected RootNode getObjectTree(String fileName) {
        RootNode root = null;

        File file = sourceProvider.getFile(fileName);
        if (file != null) {
            root = cache.get(file);
            if (root == null) {
                root = sourceProvider.getParsedSource(file);
                if (root != null) {
                    cache.put(file, root);
                }
            }
        }

        return root;
    }

    public List<IContentProposal> getPAsmProposals(Node ref, String filterText) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        if (root == null) {
            return proposals;
        }

        Node parent = ref;
        if (parent instanceof DataLineNode) {
            parent = ref.getParent();
        }

        int dot = filterText.indexOf('.');

        List<IContentProposal> list = new ArrayList<IContentProposal>();
        int index = parent.getChilds().indexOf(ref);
        while (index >= 0) {
            if (parent.getChild(index) instanceof DataLineNode) {
                DataLineNode lineNode = (DataLineNode) parent.getChild(index);
                if (lineNode.instruction != null && "NAMESP".equalsIgnoreCase(lineNode.instruction.getText())) {
                    break;
                }
                if (lineNode.label != null) {
                    if (!lineNode.label.getText().startsWith(localLabelPrefix)) {
                        break;
                    }
                    if (StringUtils.containsIgnoreCase(lineNode.label.getText(), filterText)) {
                        list.add(0, new ContentProposal(lineNode.label.getText(), lineNode.label.getText(), null));
                    }
                }
            }
            index--;
        }
        index = parent.getChilds().indexOf(ref) + 1;
        while (index < parent.getChildCount()) {
            if (parent.getChild(index) instanceof DataLineNode) {
                DataLineNode lineNode = (DataLineNode) parent.getChild(index);
                if (lineNode.instruction != null && "NAMESP".equalsIgnoreCase(lineNode.instruction.getText())) {
                    break;
                }
                if (lineNode.label != null) {
                    if (!lineNode.label.getText().startsWith(localLabelPrefix)) {
                        break;
                    }
                    if (StringUtils.containsIgnoreCase(lineNode.label.getText(), filterText)) {
                        list.add(new ContentProposal(lineNode.label.getText(), lineNode.label.getText(), null));
                    }
                }
            }
            index++;
        }
        proposals.addAll(list);

        List<Node> dataLineNodes = new ArrayList<>();
        for (Node node : root.getChilds()) {
            if (node instanceof DataNode) {
                dataLineNodes.addAll(node.getChilds());
            }
        }

        String refNamespace = "";

        index = dataLineNodes.indexOf(ref);
        while (index >= 0) {
            if (dataLineNodes.get(index) instanceof DataLineNode) {
                DataLineNode lineNode = (DataLineNode) dataLineNodes.get(index);
                if (lineNode.instruction != null && "NAMESP".equalsIgnoreCase(lineNode.instruction.getText())) {
                    refNamespace = lineNode.parameters.size() != 0 ? lineNode.parameters.get(0).getText() + "." : "";
                    break;
                }
            }
            index--;
        }

        String namespace = "";

        if (dot == -1) {
            for (Node child : dataLineNodes) {
                if (!(child instanceof DataLineNode)) {
                    continue;
                }
                DataLineNode lineNode = (DataLineNode) child;
                if (lineNode.instruction != null && "NAMESP".equalsIgnoreCase(lineNode.instruction.getText())) {
                    namespace = lineNode.parameters.size() != 0 ? lineNode.parameters.get(0).getText() + "." : "";
                }
                if (lineNode.label != null && !lineNode.label.getText().startsWith(localLabelPrefix)) {
                    if (namespace.isBlank() || namespace.equalsIgnoreCase(refNamespace)) {
                        if (StringUtils.containsIgnoreCase(lineNode.label.getText(), filterText)) {
                            proposals.add(new ContentProposal(lineNode.label.getText(), lineNode.label.getText(), null));
                        }
                    }
                }
            }

            Set<String> ns = new HashSet<>();
            for (Node child : dataLineNodes) {
                if (!(child instanceof DataLineNode)) {
                    continue;
                }
                DataLineNode lineNode = (DataLineNode) child;
                if (lineNode.instruction != null && "NAMESP".equalsIgnoreCase(lineNode.instruction.getText())) {
                    if (lineNode.parameters.size() != 0) {
                        namespace = lineNode.parameters.get(0).getText() + ".";
                        if (!namespace.equalsIgnoreCase(refNamespace) && ns.add(namespace.toLowerCase())) {
                            String text = lineNode.parameters.get(0).getText();
                            if (StringUtils.containsIgnoreCase(text, filterText)) {
                                proposals.add(new ContentProposal(text, text, null));
                            }
                        }
                    }
                }
            }
        }
        else {
            for (Node child : dataLineNodes) {
                if (!(child instanceof DataLineNode)) {
                    continue;
                }
                DataLineNode lineNode = (DataLineNode) child;
                if (lineNode.instruction != null && "NAMESP".equalsIgnoreCase(lineNode.instruction.getText())) {
                    namespace = lineNode.parameters.size() != 0 ? lineNode.parameters.get(0).getText() + "." : "";
                }
                if (namespace.equalsIgnoreCase(refNamespace)) {
                    continue;
                }
                if (lineNode.label != null && !lineNode.label.getText().startsWith(localLabelPrefix)) {
                    String text = namespace + lineNode.label.getText();
                    if (StringUtils.containsIgnoreCase(text, filterText)) {
                        proposals.add(new ContentProposal(lineNode.label.getText(), lineNode.label.getText(), null));
                    }
                }
            }
        }

        root.accept(new NodeVisitor() {

            @Override
            public void visitConstant(ConstantNode node) {
                if (node.identifier != null) {
                    String text = node.identifier.getText();
                    if (StringUtils.containsIgnoreCase(text, filterText)) {
                        proposals.add(new ContentProposal(text, text, null));
                    }
                }
            }

        });

        root.accept(new NodeVisitor() {

            @Override
            public void visitDirective(DirectiveNode node) {
                if (node instanceof DirectiveNode.IncludeNode) {
                    DirectiveNode.IncludeNode include = (DirectiveNode.IncludeNode) node;
                    if (include.getFile() == null) {
                        return;
                    }

                    Node objectRoot = getObjectTree(include.getFileName());
                    if (objectRoot != null && dot != -1) {
                        objectRoot.accept(new NodeVisitor() {

                            @Override
                            public void visitDirective(DirectiveNode node) {
                                if (node instanceof DirectiveNode.DefineNode) {
                                    DirectiveNode.DefineNode define = (DirectiveNode.DefineNode) node;
                                    if (define.getIdentifier() != null) {
                                        String text = define.getIdentifier().getText();
                                        if (StringUtils.containsIgnoreCase(text, filterText)) {
                                            proposals.add(new ContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                                        }
                                    }
                                }
                            }

                            @Override
                            public void visitConstant(ConstantNode node) {
                                if (node.identifier != null) {
                                    String text = node.identifier.getText();
                                    if (StringUtils.containsIgnoreCase(text, filterText)) {
                                        proposals.add(new ContentProposal(node.identifier.getText(), text, "<b>" + node.getText() + "</b>"));
                                    }
                                }
                            }

                        });
                    }
                }
            }

            @Override
            public void visitObject(ObjectNode node) {
                if (node.name == null || node.file == null) {
                    return;
                }
                String name = node.name.getText();
                if (StringUtils.containsIgnoreCase(name, filterText)) {
                    proposals.add(new ContentProposal(name, name, ""));
                }
                if (dot != -1) {
                    String fileName = node.getFileName();
                    Node objectRoot = getObjectTree(fileName);
                    if (objectRoot == null) {
                        return;
                    }
                    objectRoot.accept(new NodeVisitor() {

                        @Override
                        public void visitConstant(ConstantNode node) {
                            if (node.identifier != null) {
                                String text = name + constantSeparator + node.identifier.getText();
                                if (StringUtils.containsIgnoreCase(text, filterText)) {
                                    proposals.add(new ContentProposal(node.identifier.getText(), text, "<b>" + node.getText() + "</b>"));
                                }
                            }
                        }

                    });
                }
            }
        });

        return proposals;
    }

    public List<IContentProposal> getPAsmLabelProposals(Node ref, String filterText) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        if (root == null) {
            return proposals;
        }

        String namespace = "";
        int dot = filterText.indexOf('.');

        Map<String, TypeDefinitionNode> structs = new HashMap<>();

        root.accept(new NodeVisitor() {

            @Override
            public void visitTypeDefinition(TypeDefinitionNode node) {
                if (!node.isExclude() && node.identifier != null) {
                    structs.put(node.identifier.getText(), node);
                }
            }

        });

        for (Node node : root.getChilds()) {
            if (node instanceof DataNode) {
                for (Node child : node.getChilds()) {
                    if (!(child instanceof DataLineNode)) {
                        continue;
                    }
                    DataLineNode lineNode = (DataLineNode) child;
                    if (lineNode.instruction != null && "NAMESP".equalsIgnoreCase(lineNode.instruction.getText())) {
                        if (lineNode.parameters.size() == 1) {
                            String text = lineNode.parameters.get(0).getText();
                            if (StringUtils.containsIgnoreCase(text, filterText)) {
                                proposals.add(new ContentProposal(text, text, ""));
                            }
                        }
                        namespace = lineNode.parameters.size() != 0 ? lineNode.parameters.get(0).getText() + "." : "";
                    }
                    if (lineNode.label != null && !lineNode.label.getText().startsWith(localLabelPrefix)) {
                        if (dot != -1 || namespace.isBlank()) {
                            String text = namespace + lineNode.label.getText();
                            if (StringUtils.containsIgnoreCase(text, filterText)) {
                                String content = dot == -1 ? text : lineNode.label.getText();
                                String description = "";
                                if (lineNode.instruction != null) {
                                    TypeDefinitionNode typeNode = structs.get(lineNode.instruction.getText());
                                    if (typeNode != null) {
                                        description = "<b>" + typeNode.getText() + "</b>";
                                    }
                                }
                                proposals.add(new ContentProposal(content, text, description));
                            }
                        }
                        if (dot != -1 && lineNode.instruction != null) {
                            TypeDefinitionNode typeNode = structs.get(lineNode.instruction.getText());
                            if (typeNode != null) {
                                String identifier = namespace + lineNode.label.getText();
                                for (Node n : typeNode.getChilds()) {
                                    if (!(n instanceof TypeDefinitionNode.Definition)) {
                                        continue;
                                    }
                                    TypeDefinitionNode.Definition member = (TypeDefinitionNode.Definition) n;
                                    String text = identifier + "." + member.getIdentifier().getText();
                                    if (StringUtils.containsIgnoreCase(text, filterText)) {
                                        proposals.add(new ContentProposal(member.getIdentifier().getText(), member.getIdentifier().getText(), "<b>" + typeNode.getText() + "</b>"));
                                    }
                                }
                            }
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

    public Collection<TokenMarker> getExcludedNodes() {
        return excludedNodes;
    }

    protected void addToExcluded(Node node) {
        Token startToken = node.getStartToken();
        if (startToken != null) {
            TokenMarker marker = new TokenMarker(startToken.start - startToken.column, node.getStopIndex(), TokenId.COMMENT);
            tokens.add(marker);
            excludedNodes.add(marker);
        }
        excludedPaths.add(node.getPath());
    }

    protected boolean isExcludedNode(Node node) {
        if (excludedPaths.contains(node.getPath())) {
            return true;
        }
        return node.isExclude();
    }

    public TokenId getLineBackgroundId(Node root, int lineOffset) {
        TokenId color = getSectionBackgroundId(null);

        if (root != null) {
            for (Node child : root.getChilds()) {
                if (lineOffset < child.getStartIndex()) {
                    break;
                }
                if (!isExcludedNode(child)) {
                    color = getSectionBackgroundId(child);
                }
            }
        }

        return color;
    }

    boolean[] blockToggle = new boolean[6];

    TokenId getSectionBackgroundId(Node node) {
        TokenId result = null;

        if (node == null) {
            blockToggle[0] = blockToggle[1] = blockToggle[2] = blockToggle[3] = blockToggle[4] = blockToggle[5] = false;
        }

        if (node instanceof VariablesNode) {
            result = blockToggle[1] ? TokenId.VAR_ALT : TokenId.VAR;
            blockToggle[1] = !blockToggle[1];
            blockToggle[0] = blockToggle[2] = blockToggle[3] = blockToggle[4] = blockToggle[5] = false;
        }
        else if (node instanceof ObjectsNode) {
            result = blockToggle[2] ? TokenId.OBJ_ALT : TokenId.OBJ;
            blockToggle[2] = !blockToggle[2];
            blockToggle[0] = blockToggle[1] = blockToggle[3] = blockToggle[4] = blockToggle[5] = false;
        }
        else if (node instanceof MethodNode) {
            if (((MethodNode) node).isPublic()) {
                result = blockToggle[3] ? TokenId.PUB_ALT : TokenId.PUB;
                blockToggle[3] = !blockToggle[3];
                blockToggle[0] = blockToggle[1] = blockToggle[2] = blockToggle[4] = blockToggle[5] = false;
            }
            else {
                result = blockToggle[4] ? TokenId.PRI_ALT : TokenId.PRI;
                blockToggle[4] = !blockToggle[4];
                blockToggle[0] = blockToggle[1] = blockToggle[2] = blockToggle[3] = blockToggle[5] = false;
            }
        }
        else if (node instanceof DataNode) {
            result = blockToggle[5] ? TokenId.DAT_ALT : TokenId.DAT;
            blockToggle[5] = !blockToggle[5];
            blockToggle[0] = blockToggle[1] = blockToggle[2] = blockToggle[3] = blockToggle[4] = false;
        }
        else {
            result = blockToggle[0] ? TokenId.CON_ALT : TokenId.CON;
            if (node != null && node.getTokenCount() != 0) {
                blockToggle[0] = !blockToggle[0];
                blockToggle[1] = blockToggle[2] = blockToggle[3] = blockToggle[4] = blockToggle[5] = false;
            }
        }

        return result;
    }

}
