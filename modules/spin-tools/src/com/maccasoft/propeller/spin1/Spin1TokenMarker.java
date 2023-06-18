/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.SourceTokenMarker;
import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.SourceProvider;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;

public class Spin1TokenMarker extends SourceTokenMarker {

    static Map<String, TokenId> keywords = new CaseInsensitiveMap<>();
    static {
        keywords.put("CON", TokenId.SECTION);
        keywords.put("VAR", TokenId.SECTION);
        keywords.put("OBJ", TokenId.SECTION);
        keywords.put("PUB", TokenId.SECTION);
        keywords.put("PRI", TokenId.SECTION);
        keywords.put("DAT", TokenId.SECTION);

        keywords.put("BYTE", TokenId.TYPE);
        keywords.put("WORD", TokenId.TYPE);
        keywords.put("LONG", TokenId.TYPE);
        keywords.put("@BYTE", TokenId.TYPE);
        keywords.put("@WORD", TokenId.TYPE);
        keywords.put("@LONG", TokenId.TYPE);
        keywords.put("@@BYTE", TokenId.TYPE);
        keywords.put("@@WORD", TokenId.TYPE);
        keywords.put("@@LONG", TokenId.TYPE);
        keywords.put("BYTEFIT", TokenId.TYPE);
        keywords.put("WORDFIT", TokenId.TYPE);

        keywords.put("ABORT", TokenId.KEYWORD);
        keywords.put("BYTEFILL", TokenId.FUNCTION);
        keywords.put("BYTEMOVE", TokenId.FUNCTION);
        keywords.put("CASE", TokenId.KEYWORD);
        keywords.put("OTHER", TokenId.KEYWORD);
        keywords.put("CHIPVER", TokenId.FUNCTION);
        keywords.put("CLKFREQ", TokenId.FUNCTION);
        keywords.put("_CLKFREQ", TokenId.CONSTANT);
        keywords.put("CLKMODE", TokenId.FUNCTION);
        keywords.put("_CLKMODE", TokenId.CONSTANT);
        keywords.put("CLKSET", TokenId.FUNCTION);
        keywords.put("COGID", TokenId.FUNCTION);
        keywords.put("COGINIT", TokenId.FUNCTION);
        keywords.put("COGNEW", TokenId.FUNCTION);
        keywords.put("COGSTOP", TokenId.FUNCTION);
        keywords.put("CONSTANT", TokenId.FUNCTION);
        keywords.put("FLOAT", TokenId.FUNCTION);
        keywords.put("FILE", TokenId.KEYWORD);
        keywords.put("FLOAT", TokenId.FUNCTION);
        keywords.put("IF", TokenId.KEYWORD);
        keywords.put("IFNOT", TokenId.KEYWORD);
        keywords.put("ELSEIF", TokenId.KEYWORD);
        keywords.put("ELSEIFNOT", TokenId.KEYWORD);
        keywords.put("ELSE", TokenId.KEYWORD);
        keywords.put("LOCKCLR", TokenId.FUNCTION);
        keywords.put("LOCKNEW", TokenId.FUNCTION);
        keywords.put("LOCKRET", TokenId.FUNCTION);
        keywords.put("LOCKSET", TokenId.FUNCTION);
        keywords.put("LONGFILL", TokenId.FUNCTION);
        keywords.put("LONGMOVE", TokenId.FUNCTION);
        keywords.put("LOOKDOWN", TokenId.FUNCTION);
        keywords.put("LOOKDOWNZ", TokenId.FUNCTION);
        keywords.put("LOOKUP", TokenId.FUNCTION);
        keywords.put("LOOKUPZ", TokenId.FUNCTION);
        keywords.put("NEXT", TokenId.KEYWORD);
        keywords.put("QUIT", TokenId.KEYWORD);
        keywords.put("REBOOT", TokenId.KEYWORD);
        keywords.put("REPEAT", TokenId.KEYWORD);
        keywords.put("FROM", TokenId.KEYWORD);
        keywords.put("TO", TokenId.KEYWORD);
        keywords.put("STEP", TokenId.KEYWORD);
        keywords.put("WHILE", TokenId.KEYWORD);
        keywords.put("UNTIL", TokenId.KEYWORD);
        keywords.put("RESULT", TokenId.METHOD_RETURN);
        keywords.put("@RESULT", TokenId.METHOD_RETURN);
        keywords.put("RETURN", TokenId.KEYWORD);
        keywords.put("STRCOMP", TokenId.FUNCTION);
        keywords.put("STRING", TokenId.FUNCTION);
        keywords.put("STRSIZE", TokenId.FUNCTION);
        keywords.put("TRUNC", TokenId.FUNCTION);
        keywords.put("WAITCNT", TokenId.FUNCTION);
        keywords.put("WAITPEQ", TokenId.FUNCTION);
        keywords.put("WAITPNE", TokenId.FUNCTION);
        keywords.put("WAITVID", TokenId.FUNCTION);
        keywords.put("WORDFILL", TokenId.FUNCTION);
        keywords.put("WORDMOVE", TokenId.FUNCTION);

        keywords.put("NOT", TokenId.KEYWORD);
        keywords.put("OR", TokenId.KEYWORD);
        keywords.put("AND", TokenId.KEYWORD);
        keywords.put("XOR", TokenId.KEYWORD);

        keywords.put("_CLKFREQ", TokenId.CONSTANT);
        keywords.put("_CLKMODE", TokenId.CONSTANT);
        keywords.put("_XINFREQ", TokenId.CONSTANT);
        keywords.put("_FREE", TokenId.CONSTANT);
        keywords.put("_STACK", TokenId.CONSTANT);

        keywords.put("RCFAST", TokenId.CONSTANT);
        keywords.put("RCSLOW", TokenId.CONSTANT);
        keywords.put("XINPUT", TokenId.CONSTANT);
        keywords.put("XTAL1", TokenId.CONSTANT);
        keywords.put("XTAL2", TokenId.CONSTANT);
        keywords.put("XTAL3", TokenId.CONSTANT);
        keywords.put("PLL1X", TokenId.CONSTANT);
        keywords.put("PLL2X", TokenId.CONSTANT);
        keywords.put("PLL4X", TokenId.CONSTANT);
        keywords.put("PLL8X", TokenId.CONSTANT);
        keywords.put("PLL16X", TokenId.CONSTANT);

        keywords.put("TRUE", TokenId.CONSTANT);
        keywords.put("FALSE", TokenId.CONSTANT);
        keywords.put("POSX", TokenId.CONSTANT);
        keywords.put("NEGX", TokenId.CONSTANT);
        keywords.put("PI", TokenId.CONSTANT);

        keywords.put("DIRA", TokenId.KEYWORD);
        keywords.put("DIRB", TokenId.KEYWORD);
        keywords.put("INA", TokenId.KEYWORD);
        keywords.put("INB", TokenId.KEYWORD);
        keywords.put("OUTA", TokenId.KEYWORD);
        keywords.put("OUTB", TokenId.KEYWORD);
        keywords.put("CNT", TokenId.KEYWORD);
        keywords.put("CTRA", TokenId.KEYWORD);
        keywords.put("CTRB", TokenId.KEYWORD);
        keywords.put("FRQA", TokenId.KEYWORD);
        keywords.put("FRQB", TokenId.KEYWORD);
        keywords.put("PHSA", TokenId.KEYWORD);
        keywords.put("PHSB", TokenId.KEYWORD);
        keywords.put("VCFG", TokenId.KEYWORD);
        keywords.put("VSCL", TokenId.KEYWORD);
        keywords.put("PAR", TokenId.KEYWORD);
        keywords.put("SPR", TokenId.KEYWORD);
    }

    static Map<String, TokenId> pasmKeywords = new CaseInsensitiveMap<>();
    static {
        pasmKeywords.put("CLKFREQ", TokenId.CONSTANT);
        pasmKeywords.put("CLKMODE", TokenId.CONSTANT);

        pasmKeywords.put("DIRA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DIRB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("INA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("INB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("OUTA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("OUTB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CNT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CTRA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("CTRB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("FRQA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("FRQB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PHSA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PHSB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("VCFG", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("VSCL", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PAR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("SPR", TokenId.PASM_INSTRUCTION);
    }

    static Map<String, TokenId> spinKeywords = new CaseInsensitiveMap<>();
    static {
        spinKeywords.put("@BYTE", TokenId.TYPE);
        spinKeywords.put("@WORD", TokenId.TYPE);
        spinKeywords.put("@LONG", TokenId.TYPE);
        spinKeywords.put("@@BYTE", TokenId.TYPE);
        spinKeywords.put("@@WORD", TokenId.TYPE);
        spinKeywords.put("@@LONG", TokenId.TYPE);
    }

    public Spin1TokenMarker(SourceProvider sourceProvider) {
        super(sourceProvider);
        this.constantSeparator = "#";
        this.localLabelPrefix = ":";
    }

    @Override
    public void refreshTokens(String text) {
        tokens.clear();
        symbols.clear();

        Spin1TokenStream stream = new Spin1TokenStream(text);
        Spin1Parser subject = new Spin1Parser(stream);
        root = subject.parse();

        for (Token token : root.getComments()) {
            tokens.add(new TokenMarker(token, TokenId.COMMENT));
        }
        collectKeywords(root, tokens);
        updateReferences(root, tokens);
    }

    @Override
    public void refreshCompilerTokens(List<CompilerException> messages) {
        tokens.clear();
        externals.clear();
        cache.clear();

        for (Token token : root.getComments()) {
            tokens.add(new TokenMarker(token, TokenId.COMMENT));
        }
        collectKeywords(root, tokens);
        root.accept(new NodeVisitor() {

            @Override
            public void visitObject(ObjectNode objectNode) {
                if (objectNode.name == null || objectNode.file == null) {
                    return;
                }

                String fileName = objectNode.getFileName();
                Node objectRoot = getObjectTree(fileName);
                if (objectRoot != null) {
                    objectRoot.accept(new NodeVisitor() {

                        @Override
                        public void visitConstant(ConstantNode node) {
                            if (node.getIdentifier() != null) {
                                externals.put(objectNode.name.getText() + constantSeparator + node.getIdentifier().getText(), TokenId.CONSTANT);
                            }
                        }

                        @Override
                        public boolean visitMethod(MethodNode node) {
                            if ("PUB".equalsIgnoreCase(node.type.getText())) {
                                if (node.name != null) {
                                    externals.put(objectNode.name.getText() + "." + node.name.getText(), TokenId.METHOD_PUB);
                                }
                            }
                            return false;
                        }

                    });
                }
            }

        });
        updateReferences(root, tokens);

        super.refreshCompilerTokens(messages);
    }

    void collectKeywords(Node root, TreeSet<TokenMarker> tokens) {
        root.accept(new NodeVisitor() {

            String lastLabel = "";

            @Override
            public void visitDirective(DirectiveNode node) {
                int index = 0;
                if (index < node.getTokenCount()) {
                    tokens.add(new TokenMarker(node.getToken(index++), TokenId.DIRECTIVE));
                }
                if (index < node.getTokenCount()) {
                    tokens.add(new TokenMarker(node.getToken(index++), TokenId.DIRECTIVE));
                }

                if (node instanceof DirectiveNode.DefineNode) {
                    Token identifier = ((DirectiveNode.DefineNode) node).getIdentifier();
                    if (identifier != null) {
                        symbols.put(identifier.getText(), TokenId.CONSTANT);
                        tokens.add(new TokenMarker(identifier, TokenId.CONSTANT));
                    }
                }
            }

            @Override
            public boolean visitConstants(ConstantsNode node) {
                if (node.getTextToken() != null) {
                    tokens.add(new TokenMarker(node.getTextToken(), TokenId.SECTION));
                }
                return true;
            }

            @Override
            public void visitConstant(ConstantNode node) {
                if (node.getIdentifier() != null) {
                    symbols.put(node.getIdentifier().getText(), TokenId.CONSTANT);
                    tokens.add(new TokenMarker(node.getIdentifier(), TokenId.CONSTANT));
                }
            }

            @Override
            public boolean visitVariables(VariablesNode node) {
                tokens.add(new TokenMarker(node.getTokens().get(0), TokenId.SECTION));
                return true;
            }

            @Override
            public void visitVariable(VariableNode node) {
                if (node.getType() != null) {
                    tokens.add(new TokenMarker(node.getType(), TokenId.TYPE));
                }

                if (node.getIdentifier() != null) {
                    String identifier = node.getIdentifier().getText();
                    symbols.put(identifier, TokenId.VARIABLE);
                    symbols.put("@" + identifier, TokenId.VARIABLE);
                    symbols.put("@@" + identifier, TokenId.VARIABLE);
                    tokens.add(new TokenMarker(node.getIdentifier(), TokenId.VARIABLE));
                }
            }

            @Override
            public boolean visitObjects(ObjectsNode node) {
                tokens.add(new TokenMarker(node.getTokens().get(0), TokenId.SECTION));
                return true;
            }

            @Override
            public void visitObject(ObjectNode node) {
                if (node.name == null || node.file == null) {
                    return;
                }

                symbols.put(node.name.getText(), TokenId.OBJECT);
                tokens.add(new TokenMarker(node.name, TokenId.OBJECT));
                if (node.file != null) {
                    tokens.add(new TokenMarker(node.file, TokenId.STRING));
                }
            }

            @Override
            public boolean visitMethod(MethodNode node) {
                TokenId id = TokenId.METHOD_PUB;
                if ("PRI".equalsIgnoreCase(node.getType().getText())) {
                    id = TokenId.METHOD_PRI;
                }
                tokens.add(new TokenMarker(node.getType(), id));

                if (node.getName() != null) {
                    symbols.put(node.getName().getText(), id);
                    tokens.add(new TokenMarker(node.getName(), id));
                }

                for (MethodNode.ParameterNode child : node.getParameters()) {
                    tokens.add(new TokenMarker(child.getIdentifier(), TokenId.METHOD_LOCAL));
                }

                for (MethodNode.ReturnNode child : node.getReturnVariables()) {
                    tokens.add(new TokenMarker(child.getIdentifier(), TokenId.METHOD_RETURN));
                }

                return true;
            }

            @Override
            public boolean visitData(DataNode node) {
                lastLabel = "";
                tokens.add(new TokenMarker(node.getTokens().get(0), TokenId.SECTION));
                return true;
            }

            @Override
            public void visitDataLine(DataLineNode node) {
                if (node.label != null) {
                    String s = node.label.getText();
                    if (s.startsWith(":")) {
                        symbols.put(lastLabel + s, TokenId.PASM_LOCAL_LABEL);
                        symbols.put(lastLabel + "@" + s, TokenId.PASM_LOCAL_LABEL);
                        symbols.put(lastLabel + "@@" + s, TokenId.PASM_LOCAL_LABEL);
                        tokens.add(new TokenMarker(node.label, TokenId.PASM_LOCAL_LABEL));
                    }
                    else {
                        symbols.put(s, TokenId.PASM_LABEL);
                        symbols.put("@" + s, TokenId.PASM_LABEL);
                        symbols.put("@@" + s, TokenId.PASM_LABEL);
                        tokens.add(new TokenMarker(node.label, TokenId.PASM_LABEL));
                        lastLabel = s;
                    }
                }
                if (node.condition != null) {
                    tokens.add(new TokenMarker(node.condition, TokenId.PASM_CONDITION));
                }
                if (node.instruction != null) {
                    if (Spin1Model.isPAsmInstruction(node.instruction.getText())) {
                        tokens.add(new TokenMarker(node.instruction, TokenId.PASM_INSTRUCTION));
                    }
                }
                if (node.modifier != null) {
                    tokens.add(new TokenMarker(node.modifier, TokenId.PASM_MODIFIER));
                }
            }

        });
    }

    void updateReferences(Node root, TreeSet<TokenMarker> tokens) {
        root.accept(new NodeVisitor() {

            String lastLabel = "";

            @Override
            public void visitConstant(ConstantNode node) {
                if (node.getStart() != null) {
                    markTokens(node.getStart(), 0, null);
                }
                if (node.getStep() != null) {
                    markTokens(node.getStep(), 0, null);
                }
                if (node.getExpression() != null) {
                    markTokens(node.getExpression(), 0, null);
                }
                if (node.getMultiplier() != null) {
                    markTokens(node.getMultiplier(), 0, null);
                }
            }

            @Override
            public void visitVariable(VariableNode node) {
                if (node.getSize() != null) {
                    markTokens(node.getSize(), 0, null);
                }
            }

            @Override
            public void visitDirective(DirectiveNode node) {
                markTokens(node, 2, null);
            }

            @Override
            public void visitObject(ObjectNode node) {
                if (node.count != null) {
                    markTokens(node.count, 0, null);
                }
            }

            @Override
            public boolean visitMethod(MethodNode node) {
                locals.clear();

                for (Node child : node.getParameters()) {
                    locals.put(child.getText(), TokenId.METHOD_LOCAL);
                    locals.put("@" + child.getText(), TokenId.METHOD_LOCAL);
                    locals.put("@@" + child.getText(), TokenId.METHOD_LOCAL);
                }
                for (Node child : node.getReturnVariables()) {
                    locals.put(child.getText(), TokenId.METHOD_RETURN);
                    locals.put("@" + child.getText(), TokenId.METHOD_RETURN);
                    locals.put("@@" + child.getText(), TokenId.METHOD_RETURN);
                }

                for (MethodNode.ParameterNode child : node.getParameters()) {
                    if (child.defaultValue != null) {
                        markTokens(child, 1, null);
                    }
                }

                for (MethodNode.LocalVariableNode child : node.getLocalVariables()) {
                    if (child.type != null) {
                        tokens.add(new TokenMarker(child.type, TokenId.TYPE));
                    }
                    if (child.identifier != null) {
                        locals.put(child.identifier.getText(), TokenId.METHOD_LOCAL);
                        locals.put("@" + child.identifier.getText(), TokenId.METHOD_LOCAL);
                        locals.put("@@" + child.identifier.getText(), TokenId.METHOD_LOCAL);
                        tokens.add(new TokenMarker(child.identifier, TokenId.METHOD_LOCAL));
                    }
                    if (child.size != null) {
                        markTokens(child.size, 0, null);
                    }
                }

                return true;
            }

            @Override
            public boolean visitStatement(StatementNode node) {
                markTokens(node, 0, null);
                return true;
            }

            int markTokens(Node node, int i, String endMarker) {
                List<Token> list = node.getTokens();

                while (i < list.size()) {
                    Token token = list.get(i++);
                    if (token.type == Token.NUMBER) {
                        tokens.add(new TokenMarker(token, TokenId.NUMBER));
                    }
                    else if (token.type == Token.OPERATOR) {
                        tokens.add(new TokenMarker(token, TokenId.OPERATOR));
                        if (token.getText().equals(endMarker)) {
                            return i;
                        }
                    }
                    else if (token.type == Token.STRING) {
                        tokens.add(new TokenMarker(token, TokenId.STRING));
                    }
                    else {
                        int dot = token.getText().indexOf('.');
                        TokenId id = locals.get(token.getText());
                        if (id == null) {
                            id = symbols.get(token.getText());
                        }
                        if (id == null) {
                            id = externals.get(token.getText());
                        }
                        if (id == null) {
                            id = keywords.get(token.getText());
                            if (id == null) {
                                id = spinKeywords.get(token.getText());
                            }
                        }
                        if (id == null && dot != -1) {
                            String left = token.getText().substring(0, dot);
                            TokenId leftId = locals.get(left);
                            if (leftId == null && left.startsWith("@")) {
                                leftId = locals.get(left.substring(1));
                            }
                            if (leftId == null) {
                                leftId = symbols.get(left);
                                if (leftId == null && left.startsWith("@")) {
                                    leftId = symbols.get(left.substring(1));
                                }
                            }
                            if (leftId != null) {
                                tokens.add(new TokenMarker(token.start, token.start + dot, leftId));
                            }

                            switch (token.getText().substring(dot + 1).toUpperCase()) {
                                case "LONG":
                                case "WORD":
                                case "BYTE":
                                    tokens.add(new TokenMarker(token.start + dot + 1, token.stop, TokenId.TYPE));
                                    break;
                            }
                        }
                        if (id != null) {
                            if (id == TokenId.METHOD_PUB && dot != -1) {
                                tokens.add(new TokenMarker(token.start, token.start + dot - 1, TokenId.OBJECT));
                                tokens.add(new TokenMarker(token.start + dot + 1, token.stop, id));
                            }
                            else if (id == TokenId.CONSTANT && token.getText().contains("#")) {
                                int pound = token.getText().indexOf('#');
                                tokens.add(new TokenMarker(token.start, token.start + pound - 1, TokenId.OBJECT));
                                tokens.add(new TokenMarker(token.start + pound + 1, token.stop, id));
                            }
                            else {
                                tokens.add(new TokenMarker(token, id));
                                if (id == TokenId.OBJECT && i < list.size()) {
                                    Token objToken = token;
                                    token = list.get(i);
                                    if (token.getText().equals("[")) {
                                        i = markTokens(node, i, "]");
                                        if (i < list.size()) {
                                            token = list.get(i);
                                        }
                                    }
                                    if (token.getText().startsWith(".") || token.getText().startsWith("#")) {
                                        String qualifiedName = objToken.getText() + token.getText();
                                        id = symbols.get(qualifiedName);
                                        if (id == null) {
                                            id = externals.get(qualifiedName);
                                        }
                                        if (id != null) {
                                            tokens.add(new TokenMarker(token, id));
                                        }
                                        i++;
                                    }
                                }
                            }
                        }
                    }
                }

                for (Node child : node.getChilds()) {
                    if (child instanceof DirectiveNode) {
                        visitDirective((DirectiveNode) child);
                    }
                    else if (child instanceof StatementNode) {
                        markTokens(child, 0, null);
                    }
                }

                return i;
            }

            @Override
            public boolean visitData(DataNode node) {
                lastLabel = "";
                return true;
            }

            @Override
            public void visitDataLine(DataLineNode node) {
                if (node.label != null) {
                    String s = node.label.getText();
                    if (!s.startsWith(":")) {
                        lastLabel = s;
                    }
                }

                for (DataLineNode.ParameterNode parameter : node.parameters) {
                    for (Token token : parameter.getTokens()) {
                        TokenId id = null;
                        if (token.type == Token.NUMBER) {
                            tokens.add(new TokenMarker(token, TokenId.NUMBER));
                        }
                        else if (token.type == Token.OPERATOR) {
                            tokens.add(new TokenMarker(token, TokenId.OPERATOR));
                        }
                        else if (token.type == Token.STRING) {
                            tokens.add(new TokenMarker(token, TokenId.STRING));
                        }
                        else {
                            String s = token.getText();
                            if (s.startsWith(":") || s.startsWith("@:") || s.startsWith("@@:")) {
                                s = lastLabel + s;
                            }
                            id = symbols.get(s);
                            if (id == null) {
                                id = pasmKeywords.get(token.getText());
                            }
                            if (id == null) {
                                id = keywords.get(token.getText());
                            }
                            if (id != null) {
                                if (id == TokenId.CONSTANT && token.getText().contains("#")) {
                                    int dot = token.getText().indexOf('#');
                                    tokens.add(new TokenMarker(token.start, token.start + dot - 1, TokenId.OBJECT));
                                    tokens.add(new TokenMarker(token.start + dot + 1, token.stop, id));
                                }
                                else {
                                    tokens.add(new TokenMarker(token, id));
                                }
                            }
                        }
                    }
                }
            }

        });
    }

    @Override
    protected String getMethodInsert(MethodNode node) {
        StringBuilder sb = new StringBuilder();

        sb.append(node.name.getText());
        if (node.getParametersCount() != 0) {
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
        }

        return sb.toString();
    }

}
