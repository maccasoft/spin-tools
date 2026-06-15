/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.Strings;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.FunctionNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.RootNode;
import com.maccasoft.propeller.model.SourceProvider;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.StructNode;
import com.maccasoft.propeller.model.StructNode.Member;
import com.maccasoft.propeller.model.Token;
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

    public static class TokenMarker {

        int start;
        int stop;
        TokenId id;

        public TokenMarker(Token token, TokenId id) {
            this.start = token.start;
            this.stop = token.stop;
            this.id = id;
        }

        public TokenMarker(int start, int stop, TokenId id) {
            this.start = start;
            this.stop = stop;
            this.id = id;
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
        public String toString() {
            return "TokenMarker [start=" + start + ", stop=" + stop + ", id=" + id;
        }

    }

    public static class SourceContentProposal extends ContentProposal {

        public SourceContentProposal(String content, String label, String description) {
            super(content, label, description);
        }

        public SourceContentProposal(String content, String label, String description, int cursorPosition) {
            super(content, label, description, cursorPosition);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SourceContentProposal that = (SourceContentProposal) o;
            return getLabel().equals(that.getLabel());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getLabel());
        }

    }

    protected static class SectionBackground {

        int lineOffset;
        final TokenId backgroundId;

        public SectionBackground(int lineStart, TokenId backgroundId) {
            this.lineOffset = lineStart;
            this.backgroundId = backgroundId;
        }

    }

    protected SourceProvider sourceProvider;

    protected RootNode root;
    protected List<SectionBackground> sectionBackgrounds = new ArrayList<>();

    protected boolean caseSensitive;
    protected String constantSeparator;
    protected String localLabelPrefix;

    protected List<Token> comments = new ArrayList<>();
    protected Map<String, RootNode> rootNodes = new CaseInsensitiveMap<>();
    protected Map<String, TokenId> symbols = new CaseInsensitiveMap<>();
    protected Map<String, Map<String, TokenId>> locals = new HashMap<>();

    protected Context context;

    public SourceTokenMarker(SourceProvider sourceProvider) {
        this.sourceProvider = sourceProvider;
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

    public void setContext(Context context) {
        this.context = context;
    }

    public RootNode getRoot() {
        return root;
    }

    public void setRoot(RootNode root) {
        this.root = root;
        refreshBackgrounds(root);
    }

    public void refreshCompilerTokens(List<CompilerException> messages) {

    }

    public void adjustTokens(int start, int newCharCount, int replaceCharCount) {
        for (Token token : comments) {
            if (newCharCount != 0) {
                if (start <= token.start) {
                    token.start += newCharCount;
                    token.stop += newCharCount;
                }
                else if (start <= token.stop) {
                    token.stop += newCharCount;
                }
            }
            if (replaceCharCount != 0) {
                if (start + replaceCharCount <= token.start) {
                    token.start -= replaceCharCount;
                    token.stop -= replaceCharCount;
                }
                else if (start >= token.start && start <= token.stop) {
                    if (start + replaceCharCount > token.stop) {
                        token.stop -= token.stop - start;
                    }
                    else {
                        token.stop -= replaceCharCount;
                    }
                }
                else if (start < token.start) {
                    if (start + replaceCharCount <= token.stop) {
                        token.start -= token.start - start;
                    }
                    else {
                        token.stop = token.start;
                    }
                }
            }
        }
        for (SectionBackground background : sectionBackgrounds) {
            if (newCharCount != 0) {
                if (start < background.lineOffset) {
                    background.lineOffset += newCharCount;
                }
            }
            if (replaceCharCount != 0) {
                if (start < background.lineOffset) {
                    background.lineOffset -= Math.min(replaceCharCount, background.lineOffset - start);
                }
            }
        }
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

        if (root == null) {
            return result;
        }

        for (Node node : root.getChilds()) {
            if (node.getTokenCount() != 0) {
                if (lineIndex < node.getStartToken().line) {
                    break;
                }
                result = node;
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
                return true;
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
        int dot = symbol.indexOf('.');
        if (dot != -1) {
            String refObject = symbol.substring(0, dot);
            String refName = symbol.substring(dot + 1);

            RootNode objectRoot = rootNodes.get(refObject);
            if (objectRoot != null) {
                for (MethodNode node : objectRoot.getMethods()) {
                    if (node.isPublic() && refName.equalsIgnoreCase(node.getName().getText())) {
                        return getMethodDocument(node);
                    }
                }
                for (FunctionNode node : objectRoot.getFunctions()) {
                    if (node.isPublic() && refName.equalsIgnoreCase((node.getIdentifier().getText()))) {
                        return getMethodDocument(node);
                    }
                }
            }
        }

        for (MethodNode node : root.getMethods()) {
            if (symbol.equals(node.getName().getText())) {
                return getMethodDocument(node);
            }
        }
        for (FunctionNode node : root.getFunctions()) {
            if (symbol.equals(node.getIdentifier().getText())) {
                return getMethodDocument(node);
            }
        }

        return null;
    }

    public String getConstant(String symbol) {
        if (!constantSeparator.isBlank()) {
            int dot = symbol.indexOf(constantSeparator);
            if (dot > 0) {
                String refObject = symbol.substring(0, dot);
                String refName = symbol.substring(dot + 1);

                RootNode objectRoot = rootNodes.get(refObject);
                if (objectRoot != null) {
                    for (ConstantNode node : objectRoot.getConstants()) {
                        if (node.getIdentifier() == null) {
                            continue;
                        }
                        String identifier = node.getIdentifier().getText();
                        if (refName.equals(identifier)) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("<div class=\"header\">").append(getHtmlSafeString(node.getText())).append("</div>");
                            if (context != null) {
                                appendValue(sb, symbol);
                            }
                            return sb.toString();
                        }
                    }
                }
            }
        }

        for (ConstantNode node : root.getConstants()) {
            if (node.getIdentifier() == null) {
                continue;
            }
            String identifier = node.getIdentifier().getText();
            if (symbol.equals(identifier)) {
                StringBuilder sb = new StringBuilder();
                sb.append("<div class=\"header\">").append(getHtmlSafeString(node.getText())).append("</div>");
                if (context != null) {
                    appendValue(sb, symbol);
                }
                return sb.toString();
            }
        }

        return null;
    }

    void appendValue(StringBuilder sb, String identifier) {
        Expression expression = context.getLocalSymbol(identifier);
        if (expression != null) {
            sb.append("<p class=\"code\">Value: ");
            try {
                if (expression.isString()) {
                    sb.append(expression.getString());
                }
                else {
                    Number number = expression.getNumber();
                    sb.append(number).append("<br/>");
                    if (number instanceof Double) {
                        long value = Float.floatToRawIntBits(number.floatValue()) & 0xFFFFFFFFL;
                        sb.repeat(" ", 7).append(String.format("$%08X<br/>", Float.floatToRawIntBits(value)));
                        String bs = Long.toBinaryString(Float.floatToRawIntBits(value));
                        sb.repeat(" ", 7).append("%").repeat("0", 32 - bs.length()).append(bs);
                    }
                    else {
                        long value = number.longValue() & 0xFFFFFFFFL;
                        sb.repeat(" ", 7).append(String.format("$%08X<br/>", value));
                        String bs = Long.toBinaryString(value);
                        sb.repeat(" ", 7).append("%").repeat("0", 32 - bs.length()).append(bs);
                    }
                }
            } catch (Exception e) {
                sb.append("Evaluation error");
            }
            sb.append("</p>");
        }
        else {
            sb.append("<p class=\"code\">No Value</p>");
        }
    }

    public List<IContentProposal> getMethodProposals(Node context, String filterText, boolean startsOnly) {
        List<IContentProposal> proposals = new ArrayList<>();
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

        boolean address = filterText.startsWith("@");
        if (address) {
            filterText = filterText.substring(1);
        }

        Map<String, StructNode> structs = new CaseInsensitiveMap<>();
        for (StructNode node : root.getStructs()) {
            structs.put(node.identifier.getText(), node);
            structs.put("^" + node.identifier.getText(), node);
        }

        int dot = filterText.indexOf('.');
        if (dot > 0) {
            String objectName = filterText.substring(0, dot);
            if (objectName.indexOf('[') != -1) {
                objectName = objectName.substring(0, objectName.indexOf('['));
            }
            String memberName = filterText.substring(dot + 1);

            if (context instanceof MethodNode methodNode) {
                for (MethodNode.ParameterNode var : methodNode.getParameters()) {
                    if (var.getType() == null) {
                        continue;
                    }
                    if (objectName.equalsIgnoreCase(var.getIdentifier().getText())) {
                        StructNode structNode = structs.get(var.getType().getText());
                        if (structNode != null) {
                            proposals.addAll(getMembersProposals(structs, structNode, memberName, startsOnly));
                            return proposals;
                        }
                        break;
                    }
                }
                for (MethodNode.LocalVariableNode var : methodNode.getLocalVariables()) {
                    if (var.getType() == null) {
                        continue;
                    }
                    if (objectName.equalsIgnoreCase(var.getIdentifier().getText())) {
                        StructNode structNode = structs.get(var.getType().getText());
                        if (structNode != null) {
                            proposals.addAll(getMembersProposals(structs, structNode, memberName, startsOnly));
                            return proposals;
                        }
                        break;
                    }
                }
                for (MethodNode.ReturnNode var : methodNode.getReturnVariables()) {
                    if (var.getType() == null) {
                        continue;
                    }
                    if (objectName.equalsIgnoreCase(var.getIdentifier().getText())) {
                        StructNode structNode = structs.get(var.getType().getText());
                        if (structNode != null) {
                            proposals.addAll(getMembersProposals(structs, structNode, memberName, startsOnly));
                            return proposals;
                        }
                        break;
                    }
                }
            }

            for (VariableNode variableNode : root.getVariables()) {
                if (variableNode.getType() == null) {
                    continue;
                }
                if (objectName.equalsIgnoreCase(variableNode.getIdentifier().getText())) {
                    StructNode structNode = structs.get(variableNode.getType().getText());
                    if (structNode != null) {
                        proposals.addAll(getMembersProposals(structs, structNode, memberName, startsOnly));
                        return proposals;
                    }
                    break;
                }
            }

            RootNode objectRoot = rootNodes.get(objectName);
            if (objectRoot != null) {
                boolean first = true;
                for (MethodNode node : objectRoot.getMethods()) {
                    if (node.isPublic()) {
                        String text = node.getName().getText();
                        if (first && "null".equalsIgnoreCase(text)) {
                            continue;
                        }
                        int foundAt = Strings.CI.indexOf(text, memberName);
                        if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                            String content = getMethodInsert(node);
                            int cursorPosition = content.endsWith("()") ? content.length() : (content.indexOf('(') + 1);
                            proposals.add(new SourceContentProposal(content, text, getMethodDocument(node), cursorPosition));
                        }
                        first = false;
                    }
                }
                for (FunctionNode node : objectRoot.getFunctions()) {
                    if (node.isPublic()) {
                        String text = node.getIdentifier().getText();
                        int foundAt = Strings.CI.indexOf(text, memberName);
                        if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                            String content = getMethodInsert(node);
                            int cursorPosition = content.endsWith("()") ? content.length() : (content.indexOf('(') + 1);
                            proposals.add(new SourceContentProposal(content, text, "", cursorPosition));
                        }
                    }
                }
            }
        }
        else {
            if (context instanceof MethodNode node) {
                for (MethodNode.ParameterNode child : node.getParameters()) {
                    String text = child.getIdentifier().getText();
                    int foundAt = Strings.CI.indexOf(text, filterText);
                    if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                        proposals.add(new SourceContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                    }
                }

                for (MethodNode.LocalVariableNode child : node.getLocalVariables()) {
                    String text = child.getIdentifier().getText();
                    int foundAt = Strings.CI.indexOf(text, filterText);
                    if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                        proposals.add(new SourceContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                    }
                }

                for (MethodNode.ReturnNode child : node.getReturnVariables()) {
                    String text = child.getIdentifier().getText();
                    int foundAt = Strings.CI.indexOf(text, filterText);
                    if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                        proposals.add(new SourceContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                    }
                }
            }

            for (VariableNode node : root.getVariables()) {
                String text = node.getIdentifier().getText();
                int foundAt = Strings.CI.indexOf(text, filterText);
                if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                    proposals.add(new SourceContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                }
            }

            for (MethodNode node : root.getMethods()) {
                String text = node.getName().getText();
                if (!contextMethodName.equals(text)) {
                    int foundAt = Strings.CI.indexOf(text, filterText);
                    if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                        String content = getMethodInsert(node);
                        int cursorPosition = content.endsWith("()") ? content.length() : (content.indexOf('(') + 1);
                        proposals.add(new SourceContentProposal(content, text, getMethodDocument(node), cursorPosition));
                    }
                }
            }
            for (FunctionNode node : root.getFunctions()) {
                String text = node.getIdentifier().getText();
                if (!contextMethodName.equals(text)) {
                    int foundAt = Strings.CI.indexOf(text, filterText);
                    if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                        String content = getMethodInsert(node);
                        int cursorPosition = content.endsWith("()") ? content.length() : (content.indexOf('(') + 1);
                        proposals.add(new SourceContentProposal(content, text, "", cursorPosition));
                    }
                }
            }

            for (ObjectNode node : root.getObjects()) {
                String text = node.name.getText();
                int foundAt = Strings.CI.indexOf(text, filterText);
                if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                    proposals.add(new SourceContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                }
            }
        }

        return proposals;
    }

    List<IContentProposal> getMembersProposals(Map<String, StructNode> structs, StructNode typeNode, String refName, boolean startsOnly) {
        List<IContentProposal> proposals = new ArrayList<>();

        if (refName.contains(".")) {
            String[] ar = refName.split("[.]");
            for (Node n : typeNode.getChilds()) {
                if (!(n instanceof StructNode.Member member)) {
                    continue;
                }
                if (ar[0].equalsIgnoreCase(member.getIdentifier().getText())) {
                    if (member.getType() != null) {
                        typeNode = structs.get(member.getType().getText());
                        if (typeNode == null) {
                            return proposals;
                        }
                        return getMembersProposals(structs, typeNode, refName.substring(refName.indexOf(".") + 1), startsOnly);
                    }
                }
            }
        }
        for (Node n : typeNode.getChilds()) {
            if (!(n instanceof Member member)) {
                continue;
            }
            String text = member.getIdentifier().getText();
            int foundAt = Strings.CI.indexOf(text, refName);
            if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                proposals.add(new SourceContentProposal(text, text, "<b>" + typeNode.getText() + "</b>"));
            }
        }

        return proposals;
    }

    public List<IContentProposal> getInlinePAsmProposals(Node context, String filterText) {
        List<IContentProposal> proposals = new ArrayList<>();
        if (root == null) {
            return proposals;
        }

        Node method = context;
        while (!(method instanceof MethodNode) && !(method instanceof FunctionNode) && method.getParent() != null) {
            method = method.getParent();
        }
        for (Node node : method.getChilds()) {
            if (node instanceof MethodNode.ParameterNode child) {
                String text = child.getIdentifier().getText();
                if (Strings.CI.contains(text, filterText)) {
                    proposals.add(new SourceContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                }
            }
            else if (node instanceof MethodNode.LocalVariableNode child) {
                String text = child.getIdentifier().getText();
                if (Strings.CI.contains(text, filterText)) {
                    proposals.add(new SourceContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                }
            }
            else if (node instanceof MethodNode.ReturnNode child) {
                String text = child.getIdentifier().getText();
                if (Strings.CI.contains(text, filterText)) {
                    proposals.add(new SourceContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                }
            }
        }

        List<String> pasmLabels = new ArrayList<>();

        String lastLabel = "";

        for (Node child : context.getParent().getChilds()) {
            if (!(child instanceof DataLineNode lineNode)) {
                continue;
            }
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
            if (!(child instanceof DataLineNode lineNode)) {
                continue;
            }
            if (lineNode.label != null) {
                String text = lineNode.label.getText();
                if (!text.startsWith(localLabelPrefix)) {
                    lastLabel = text;
                }
            }
            if (lineNode == context) {
                String s1 = lastLabel + localLabelPrefix;
                for (String s : pasmLabels) {
                    if (Strings.CI.startsWith(s, s1)) {
                        if (Strings.CI.contains(s.substring(s1.length() - 1), filterText)) {
                            String text = s.substring(s1.length() - 1);
                            if (Strings.CI.contains(text, filterText)) {
                                proposals.add(new SourceContentProposal(text, text, null));
                            }
                        }
                    }
                }
                break;
            }
        }

        for (Node child : context.getParent().getChilds()) {
            if (!(child instanceof DataLineNode lineNode)) {
                continue;
            }
            if (lineNode.label != null) {
                String text = lineNode.label.getText();
                if (!text.startsWith(localLabelPrefix) && Strings.CI.contains(text, filterText)) {
                    proposals.add(new SourceContentProposal(text, text, null));
                }
            }
        }

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
        List<IContentProposal> proposals = new ArrayList<>();

        String typeText = "long";
        if (Strings.CI.contains(typeText, filterText)) {
            proposals.add(new SourceContentProposal(typeText, typeText, "<b>" + typeText + "</b>"));
        }
        typeText = "word";
        if (Strings.CI.contains(typeText, filterText)) {
            proposals.add(new SourceContentProposal(typeText, typeText, "<b>" + typeText + "</b>"));
        }
        typeText = "byte";
        if (Strings.CI.contains(typeText, filterText)) {
            proposals.add(new SourceContentProposal(typeText, typeText, "<b>" + typeText + "</b>"));
        }

        if (root != null) {
            List<IContentProposal> localProposals = new ArrayList<>();
            for (StructNode node : root.getStructs()) {
                String text = node.getIdentifier().getText();
                if (Strings.CI.contains(text, filterText)) {
                    localProposals.add(new SourceContentProposal(text, text, "<b>" + node.getText() + "</b>"));
                }
            }
            localProposals.sort((o1, o2) -> o1.getLabel().compareToIgnoreCase(o2.getLabel()));
            proposals.addAll(localProposals);

            List<IContentProposal> objectProposals = new ArrayList<>();
            for (ObjectNode objectNode : root.getObjects()) {
                RootNode objectRoot = rootNodes.get(objectNode.name.getText());
                if (objectRoot != null) {
                    for (StructNode node : objectRoot.getStructs()) {
                        String text = objectNode.name.getText() + "." + node.getIdentifier().getText();
                        if (Strings.CI.contains(text, filterText)) {
                            objectProposals.add(new SourceContentProposal(node.getIdentifier().getText(), text, "<b>" + node.getText() + "</b>"));
                        }
                    }
                }
            }
            objectProposals.sort((o1, o2) -> o1.getLabel().compareToIgnoreCase(o2.getLabel()));
            proposals.addAll(objectProposals);
        }

        return proposals;
    }

    public List<IContentProposal> getConstantsProposals(String filterText, boolean startsOnly) {
        List<IContentProposal> proposals = new ArrayList<>();

        if (root != null) {
            int dot = constantSeparator.isBlank() ? -1 : filterText.indexOf(constantSeparator);
            if (dot > 0) {
                String objectName = filterText.substring(0, dot);
                String memberName = filterText.substring(dot + 1);

                RootNode objectRoot = rootNodes.get(objectName);
                if (objectRoot != null) {
                    for (ConstantNode node : objectRoot.getConstants()) {
                        String text = node.getIdentifier().getText();
                        int foundAt = Strings.CI.indexOf(text, memberName);
                        if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("<div class=\"header\">").append(getHtmlSafeString(node.getText())).append("</div>");
                            if (context != null) {
                                appendValue(sb, objectName + constantSeparator + node.getIdentifier().getText());
                            }
                            proposals.add(new SourceContentProposal(text, text, sb.toString()));
                        }
                    }
                }
            }
            else {
                for (ConstantNode node : root.getConstants()) {
                    String text = node.getIdentifier().getText();
                    int foundAt = Strings.CI.indexOf(text, filterText);
                    if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("<div class=\"header\">").append(getHtmlSafeString(node.getText())).append("</div>");
                        if (context != null) {
                            appendValue(sb, node.getIdentifier().getText());
                        }
                        proposals.add(new SourceContentProposal(text, text, sb.toString()));
                    }
                }
            }
        }

        return proposals;
    }

    String getMethodDocument(MethodNode node) {
        int index;

        StringBuilder title = new StringBuilder();
        title.append(node.getStartToken().getText()).append(" ");
        title.append(node.getName().getText());
        title.append("(");
        for (MethodNode.ParameterNode param : node.getParameters()) {
            if (title.charAt(title.length() - 1) != '(') {
                title.append(", ");
            }
            if (param.getType() != null) {
                title.append(param.getType().getText()).append(" ");
            }
            title.append(param.getIdentifier().getText());
        }
        title.append(")");
        if (!node.getReturnVariables().isEmpty()) {
            title.append(" : ");
            for (MethodNode.ReturnNode param : node.getReturnVariables()) {
                if (title.charAt(title.length() - 1) != ' ') {
                    title.append(", ");
                }
                if (param.getType() != null) {
                    title.append(param.getType().getText()).append(" ");
                }
                title.append(param.getIdentifier().getText());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"header\">");
        sb.append(getHtmlSafeString(title.toString()));
        sb.append("</div>");

        Iterator<Token> iter = node.getDocument().iterator();
        if (iter.hasNext()) {
            boolean hasParameters = false;
            boolean hasReturns = false;
            boolean closeParagraph = false;

            sb.append("<div class=\"code\">");
            while (iter.hasNext()) {
                Token token = iter.next();
                String tokenText = token.getText();
                if (tokenText.startsWith(("''"))) {
                    tokenText = tokenText.substring(2);
                    tokenText = getHtmlSafeString(tokenText.trim());

                    int idx = tokenText.indexOf("@param");
                    if (idx != -1) {
                        if (!hasParameters) {
                            if (closeParagraph) {
                                sb.append("</p>");
                                closeParagraph = false;
                            }
                            sb.append("<p class=\"subtitle\">Parameters</p>");
                            hasParameters = true;
                        }
                    }
                    else if ((idx = tokenText.indexOf("@returns")) != -1) {
                        if (!hasReturns) {
                            if (closeParagraph) {
                                sb.append("</p>");
                                closeParagraph = false;
                            }
                            sb.append("<p class=\"subtitle\">Returns</p>");
                            hasReturns = true;
                        }
                    }
                    if (idx != -1) {
                        sb.append("<div>");
                        int start = tokenText.indexOf(' ', idx + 1);
                        if (start != -1) {
                            String className = tokenText.substring(1, start).trim();
                            int stop = tokenText.indexOf(" -", start + 1);
                            if (stop > start) {
                                String name = tokenText.substring(start, stop).trim();
                                tokenText = "<span class=\"" + className + "\">" + name + "</span>" + tokenText.substring(stop);
                            }
                        }
                        sb.append(tokenText);
                        sb.append("</div>");
                    }
                    else if (!tokenText.trim().isEmpty()) {
                        if (!closeParagraph) {
                            sb.append("<p>");
                            closeParagraph = true;
                        }
                        sb.append(tokenText).append(System.lineSeparator());
                    }
                }
                else if (tokenText.startsWith("{{")) {
                    tokenText = tokenText.substring(2);
                    if (tokenText.endsWith("}}")) {
                        tokenText = tokenText.substring(0, tokenText.length() - 2);
                    }
                    tokenText = getHtmlSafeString(tokenText.trim());

                    String[] textLines = tokenText.split("\\r\\n|\\r|\\n");
                    for (String line : textLines) {
                        line = getHtmlSafeString(line.trim());

                        int idx = line.indexOf("@param");
                        if (idx != -1) {
                            if (!hasParameters) {
                                if (closeParagraph) {
                                    sb.append("</p>");
                                    closeParagraph = false;
                                }
                                sb.append("<p class=\"subtitle\">Parameters</p>");
                                hasParameters = true;
                            }
                        }
                        else if ((idx = line.indexOf("@returns")) != -1) {
                            if (!hasReturns) {
                                if (closeParagraph) {
                                    sb.append("</p>");
                                    closeParagraph = false;
                                }
                                sb.append("<p class=\"subtitle\">Returns</p>");
                                hasReturns = true;
                            }
                        }
                        if (idx != -1) {
                            sb.append("<div>");
                            int start = line.indexOf(' ', idx + 1);
                            if (start != -1) {
                                String className = line.substring(1, start).trim();
                                int stop = line.indexOf(" -", start + 1);
                                if (stop > start) {
                                    String name = line.substring(start, stop).trim();
                                    line = "<span class=\"" + className + "\">" + name + "</span>" + line.substring(stop);
                                }
                            }
                            sb.append(line);
                            sb.append("</div>");
                        }
                        else if (!line.trim().isEmpty()) {
                            if (!closeParagraph) {
                                sb.append("<p>");
                                closeParagraph = true;
                            }
                            sb.append(line).append(System.lineSeparator());
                        }
                    }
                }
            }
            if (closeParagraph) {
                sb.append("</p>");
            }
            sb.append("</div>");
        }

        return sb.toString();
    }

    String getMethodDocument(FunctionNode node) {
        StringBuilder title = new StringBuilder();
        if (node.getModifier() != null) {
            title.append(node.getModifier().getText()).append(" ");
        }
        title.append(node.getType().getText()).append(" ");
        title.append(node.getIdentifier().getText());
        title.append("(");
        for (FunctionNode.ParameterNode param : node.getParameters()) {
            if (title.charAt(title.length() - 1) != '(') {
                title.append(", ");
            }
            if (param.getType() != null) {
                title.append(param.getType().getText()).append(" ");
            }
            title.append(param.getIdentifier().getText());
        }
        title.append(")");

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"header\">");
        sb.append(getHtmlSafeString(title.toString()));
        sb.append("</div>");

        Iterator<Token> iter = node.getDocument().iterator();
        if (iter.hasNext()) {
            sb.append("<div class=\"code\">");
            while (iter.hasNext()) {
                Token token = iter.next();
                String tokenText = token.getText();
                if (tokenText.startsWith("/**")) {
                    if (tokenText.startsWith(("/** "))) {
                        tokenText = tokenText.substring(4);
                    }
                    else {
                        tokenText = tokenText.substring(3);
                    }
                    if (tokenText.endsWith("*/")) {
                        tokenText = tokenText.substring(0, tokenText.length() - 2);
                    }
                    tokenText = getHtmlSafeString(tokenText);
                    tokenText = tokenText.replaceAll("\\r\\n|\\r|\\n", System.lineSeparator());
                    while (tokenText.startsWith(System.lineSeparator())) {
                        tokenText = tokenText.substring(System.lineSeparator().length());
                    }
                    while (tokenText.endsWith(System.lineSeparator())) {
                        tokenText = tokenText.substring(0, tokenText.length() - System.lineSeparator().length());
                    }
                    sb.append(tokenText);
                }
            }
            sb.append("</div>");
        }

        return sb.toString();
    }

    String getHtmlSafeString(String s) {
        s = s.replace("&", "&amp;");
        s = s.replace("<", "&lt;");
        s = s.replace(">", "&gt;");
        return s;
    }

    public List<IContentProposal> getPAsmProposals(Node ref, String filterText, boolean startsOnly) {
        int index;

        List<IContentProposal> proposals = new ArrayList<>();
        if (root == null) {
            return proposals;
        }

        Node parent = ref;
        if (parent instanceof DataLineNode) {
            parent = ref.getParent();
        }

        List<Node> dataLineNodes = new ArrayList<>();
        for (Node node : root.getChilds()) {
            if (node instanceof DataNode) {
                dataLineNodes.addAll(node.getChilds());
            }
        }

        String currentNamespace = "";

        index = dataLineNodes.indexOf(ref);
        while (index >= 0) {
            if (dataLineNodes.get(index) instanceof DataLineNode lineNode) {
                if (lineNode.instruction != null && "NAMESP".equalsIgnoreCase(lineNode.instruction.getText())) {
                    currentNamespace = !lineNode.parameters.isEmpty() ? lineNode.parameters.getFirst().getText() : "";
                    break;
                }
            }
            index--;
        }

        int dot = filterText.indexOf('.');
        if (dot > 0) {
            String objectName = filterText.substring(0, dot);
            String refName = filterText.substring(dot + 1);
            String namespace = "";

            for (Node node : root.getChilds()) {
                if (node instanceof DataNode) {
                    for (Node child : node.getChilds()) {
                        if (child instanceof DataLineNode dataLine) {
                            if (namespace.equalsIgnoreCase(currentNamespace)) {
                                continue;
                            }
                            if (dataLine.instruction != null && "NAMESP".equalsIgnoreCase(dataLine.instruction.getText())) {
                                namespace = !dataLine.parameters.isEmpty() ? dataLine.parameters.getFirst().getText() : "";
                            }
                            else if (dataLine.label != null && !dataLine.label.getText().startsWith(localLabelPrefix) && objectName.equalsIgnoreCase(namespace)) {
                                String text = dataLine.label.getText();
                                int foundAt = Strings.CI.indexOf(text, refName);
                                if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                                    proposals.add(new SourceContentProposal(text, text, null));
                                }
                            }
                        }
                    }
                }
            }
        }
        else {
            List<IContentProposal> localProposals = new ArrayList<>();

            index = parent.getChilds().indexOf(ref);
            while (index >= 0) {
                if (parent.getChild(index) instanceof DataLineNode lineNode) {
                    if (lineNode.instruction != null && "NAMESP".equalsIgnoreCase(lineNode.instruction.getText())) {
                        break;
                    }
                    if (lineNode.label != null) {
                        if (!lineNode.label.getText().startsWith(localLabelPrefix)) {
                            break;
                        }
                        String text = lineNode.label.getText();
                        int foundAt = Strings.CI.indexOf(text, filterText);
                        if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                            localProposals.addFirst(new SourceContentProposal(text, text, null));
                        }
                    }
                }
                index--;
            }

            index = parent.getChilds().indexOf(ref) + 1;
            while (index < parent.getChildCount()) {
                if (parent.getChild(index) instanceof DataLineNode lineNode) {
                    if (lineNode.instruction != null && "NAMESP".equalsIgnoreCase(lineNode.instruction.getText())) {
                        break;
                    }
                    if (lineNode.label != null) {
                        if (!lineNode.label.getText().startsWith(localLabelPrefix)) {
                            break;
                        }
                        String text = lineNode.label.getText();
                        int foundAt = Strings.CI.indexOf(text, filterText);
                        if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                            localProposals.addFirst(new SourceContentProposal(text, text, null));
                        }
                    }
                }
                index++;
            }

            localProposals.sort((o1, o2) -> o1.getLabel().compareToIgnoreCase(o2.getLabel()));
            proposals.addAll(localProposals);

            String namespace = "";
            List<IContentProposal> labelProposals = new ArrayList<>();

            for (Node node : dataLineNodes) {
                if (node instanceof DataLineNode dataLine) {
                    if (dataLine.instruction != null && "NAMESP".equalsIgnoreCase(dataLine.instruction.getText())) {
                        namespace = !dataLine.parameters.isEmpty() ? dataLine.parameters.getFirst().getText() : "";
                    }
                    else if (dataLine.label != null && !dataLine.label.getText().startsWith(localLabelPrefix) && namespace.equalsIgnoreCase(currentNamespace)) {
                        String text = dataLine.label.getText();
                        int foundAt = Strings.CI.indexOf(text, filterText);
                        if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                            labelProposals.addFirst(new SourceContentProposal(text, text, null));
                        }
                    }
                }
            }

            labelProposals.sort((o1, o2) -> o1.getLabel().compareToIgnoreCase(o2.getLabel()));
            proposals.addAll(labelProposals);

            Set<String> ns = new HashSet<>();
            List<IContentProposal> namespaceProposals = new ArrayList<>();

            for (Node node : dataLineNodes) {
                if (node instanceof DataLineNode dataLine) {
                    if (dataLine.instruction != null && "NAMESP".equalsIgnoreCase(dataLine.instruction.getText())) {
                        if (!dataLine.parameters.isEmpty()) {
                            String text = dataLine.parameters.getFirst().getText();
                            if (ns.add(text.toLowerCase()) && !text.equalsIgnoreCase(currentNamespace)) {
                                int foundAt = Strings.CI.indexOf(text, filterText);
                                if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                                    namespaceProposals.add(new SourceContentProposal(text, text, null));
                                }
                            }
                        }
                    }
                }
            }

            namespaceProposals.sort((o1, o2) -> o1.getLabel().compareToIgnoreCase(o2.getLabel()));
            proposals.addAll(namespaceProposals);
        }

        return proposals;
    }

    public List<IContentProposal> getPAsmLabelProposals(Node ref, String filterText, boolean startsOnly) {
        List<IContentProposal> proposals = new ArrayList<>();
        if (root == null) {
            return proposals;
        }

        Map<String, StructNode> structs = new CaseInsensitiveMap<>();
        for (StructNode node : root.getStructs()) {
            structs.put(node.identifier.getText(), node);
            structs.put("^" + node.identifier.getText(), node);
        }

        String namespace = "";

        int dot = filterText.indexOf('.');
        if (dot > 0) {
            String objectName = filterText.substring(0, dot);
            String memberName = filterText.substring(dot + 1);

            for (Node mainNode : root.getChilds()) {
                if (mainNode instanceof DataNode dataNode) {
                    for (Node childNode : dataNode.getChilds()) {
                        if (!(childNode instanceof DataLineNode node)) {
                            continue;
                        }
                        if (node.instruction != null && "NAMESP".equalsIgnoreCase(node.instruction.getText())) {
                            namespace = !node.parameters.isEmpty() ? node.parameters.getFirst().getText() : "";
                        }
                        if (node.label != null && objectName.equalsIgnoreCase(namespace)) {
                            if (!node.label.getText().startsWith(".") && !node.label.getText().startsWith(":")) {
                                String text = node.label.getText();
                                int foundAt = Strings.CI.indexOf(text, memberName);
                                if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                                    proposals.add(new SourceContentProposal(text, text, null));
                                }
                            }
                        }
                    }
                }
            }

            for (Node mainNode : root.getChilds()) {
                if (mainNode instanceof DataNode dataNode) {
                    for (Node childNode : dataNode.getChilds()) {
                        if (!(childNode instanceof DataLineNode node)) {
                            continue;
                        }
                        if (node.instruction != null) {
                            if ("NAMESP".equalsIgnoreCase(node.instruction.getText())) {
                                namespace = !node.parameters.isEmpty() ? node.parameters.getFirst().getText() : "";
                            }
                            if (node.label != null && namespace.isEmpty() && objectName.equalsIgnoreCase(node.label.getText())) {
                                StructNode structNode = structs.get(node.instruction.getText());
                                if (structNode != null) {
                                    for (Node child : structNode.getChilds()) {
                                        if (child instanceof StructNode.Member member && member.getIdentifier() != null) {
                                            String text = member.getIdentifier().getText();
                                            int foundAt = Strings.CI.indexOf(text, memberName);
                                            if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                                                proposals.add(new SourceContentProposal(text, text, null));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else {
            for (Node mainNode : root.getChilds()) {
                if (mainNode instanceof DataNode dataNode) {
                    for (Node childNode : dataNode.getChilds()) {
                        if (!(childNode instanceof DataLineNode node)) {
                            continue;
                        }
                        if (node.instruction != null && "NAMESP".equalsIgnoreCase(node.instruction.getText())) {
                            namespace = !node.parameters.isEmpty() ? node.parameters.getFirst().getText() : "";
                        }
                        if (node.label != null && namespace.isEmpty()) {
                            if (!node.label.getText().startsWith(".") && !node.label.getText().startsWith(":")) {
                                String text = node.label.getText();
                                int foundAt = Strings.CI.indexOf(text, filterText);
                                if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                                    proposals.add(new SourceContentProposal(text, text, null));
                                }
                            }
                        }
                    }
                }
            }

            for (Node mainNode : root.getChilds()) {
                if (mainNode instanceof DataNode dataNode) {
                    for (Node childNode : dataNode.getChilds()) {
                        if (!(childNode instanceof DataLineNode node)) {
                            continue;
                        }
                        if (node.instruction != null && "NAMESP".equalsIgnoreCase(node.instruction.getText()) && !node.parameters.isEmpty()) {
                            String text = node.parameters.getFirst().getText();
                            int foundAt = Strings.CI.indexOf(text, filterText);
                            if ((startsOnly && foundAt == 0) || (!startsOnly && foundAt > 0)) {
                                proposals.add(new SourceContentProposal(text, text, null));
                            }
                        }
                    }
                }
            }
        }

        return proposals;
    }

    public SourceTokenMarker.TokenId getLineBackgroundId(int lineOffset) {
        SourceTokenMarker.TokenId color = TokenId.CON_ALT;

        for (SectionBackground background : sectionBackgrounds) {
            if (lineOffset < background.lineOffset) {
                break;
            }
            color = background.backgroundId;
        }

        return color;
    }

    protected void refreshBackgrounds(RootNode root) {
        boolean[] blockToggle = new boolean[6];

        sectionBackgrounds.clear();

        for (Node node : root.getChilds()) {
            Token startToken = node.getStartToken();
            int lineOffset = startToken != null ? startToken.start - startToken.column : 0;
            switch (node) {
                case VariablesNode ignored -> {
                    sectionBackgrounds.add(new SectionBackground(lineOffset, blockToggle[1] ? TokenId.VAR_ALT : TokenId.VAR));
                    blockToggle[1] = !blockToggle[1];
                    blockToggle[0] = blockToggle[2] = blockToggle[3] = blockToggle[4] = blockToggle[5] = false;
                }
                case ObjectsNode ignored -> {
                    sectionBackgrounds.add(new SectionBackground(lineOffset, blockToggle[2] ? TokenId.OBJ_ALT : TokenId.OBJ));
                    blockToggle[2] = !blockToggle[2];
                    blockToggle[0] = blockToggle[1] = blockToggle[3] = blockToggle[4] = blockToggle[5] = false;
                }
                case MethodNode methodNode -> {
                    if (methodNode.isPublic()) {
                        sectionBackgrounds.add(new SectionBackground(lineOffset, blockToggle[3] ? TokenId.PUB_ALT : TokenId.PUB));
                        blockToggle[3] = !blockToggle[3];
                        blockToggle[0] = blockToggle[1] = blockToggle[2] = blockToggle[4] = blockToggle[5] = false;
                    }
                    else {
                        sectionBackgrounds.add(new SectionBackground(lineOffset, blockToggle[4] ? TokenId.PRI_ALT : TokenId.PRI));
                        blockToggle[4] = !blockToggle[4];
                        blockToggle[0] = blockToggle[1] = blockToggle[2] = blockToggle[3] = blockToggle[5] = false;
                    }
                }
                case FunctionNode functionNode -> {
                    if (functionNode.isPublic()) {
                        sectionBackgrounds.add(new SectionBackground(lineOffset, blockToggle[3] ? TokenId.PUB_ALT : TokenId.PUB));
                        blockToggle[3] = !blockToggle[3];
                        blockToggle[0] = blockToggle[1] = blockToggle[2] = blockToggle[4] = blockToggle[5] = false;
                    }
                    else {
                        sectionBackgrounds.add(new SectionBackground(lineOffset, blockToggle[4] ? TokenId.PRI_ALT : TokenId.PRI));
                        blockToggle[4] = !blockToggle[4];
                        blockToggle[0] = blockToggle[1] = blockToggle[2] = blockToggle[3] = blockToggle[5] = false;
                    }
                }
                case DataNode ignored -> {
                    sectionBackgrounds.add(new SectionBackground(lineOffset, blockToggle[5] ? TokenId.DAT_ALT : TokenId.DAT));
                    blockToggle[5] = !blockToggle[5];
                    blockToggle[0] = blockToggle[1] = blockToggle[2] = blockToggle[3] = blockToggle[4] = false;
                }
                default -> {
                    sectionBackgrounds.add(new SectionBackground(lineOffset, blockToggle[0] ? TokenId.CON_ALT : TokenId.CON));
                    blockToggle[0] = !blockToggle[0];
                    blockToggle[1] = blockToggle[2] = blockToggle[3] = blockToggle[4] = blockToggle[5] = false;
                }
            }
        }
    }

    public Collection<SourceTokenMarker.TokenMarker> getTokens(int lineIndex, int lineOffset, String lineText) {
        return new ArrayList<>();
    }

    public boolean hasLineContinuation(int lineIndex, int lineOffset, String lineText) {
        return false;
    }

}
