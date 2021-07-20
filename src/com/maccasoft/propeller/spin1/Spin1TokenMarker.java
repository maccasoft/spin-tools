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

import java.util.HashMap;
import java.util.Map;

import com.maccasoft.propeller.EditorTokenMarker;
import com.maccasoft.propeller.model.ConstantAssignEnumNode;
import com.maccasoft.propeller.model.ConstantAssignNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.ErrorNode;
import com.maccasoft.propeller.model.ExpressionNode;
import com.maccasoft.propeller.model.LocalVariableNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.ParameterNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;

public class Spin1TokenMarker extends EditorTokenMarker {

    static Map<String, TokenId> keywords = new HashMap<String, TokenId>();
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

        keywords.put("ABORT", TokenId.KEYWORD);
        keywords.put("BYTEFILL", TokenId.FUNCTION);
        keywords.put("BYTEMOVE", TokenId.FUNCTION);
        keywords.put("CASE", TokenId.KEYWORD);
        keywords.put("OTHER", TokenId.KEYWORD);
        keywords.put("CHIPVER", TokenId.CONSTANT);
        keywords.put("CHIPFREQ", TokenId.CONSTANT);
        keywords.put("CLKFREQ", TokenId.CONSTANT);
        keywords.put("CLKMODE", TokenId.CONSTANT);
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

    static Map<String, TokenId> pasmKeywords = new HashMap<String, TokenId>();
    static {
        pasmKeywords.put("DIRA", TokenId.KEYWORD);
        pasmKeywords.put("DIRB", TokenId.KEYWORD);
        pasmKeywords.put("INA", TokenId.KEYWORD);
        pasmKeywords.put("INB", TokenId.KEYWORD);
        pasmKeywords.put("OUTA", TokenId.KEYWORD);
        pasmKeywords.put("OUTB", TokenId.KEYWORD);
        pasmKeywords.put("CNT", TokenId.KEYWORD);
        pasmKeywords.put("CTRA", TokenId.KEYWORD);
        pasmKeywords.put("CTRB", TokenId.KEYWORD);
        pasmKeywords.put("FRQA", TokenId.KEYWORD);
        pasmKeywords.put("FRQB", TokenId.KEYWORD);
        pasmKeywords.put("PHSA", TokenId.KEYWORD);
        pasmKeywords.put("PHSB", TokenId.KEYWORD);
        pasmKeywords.put("VCFG", TokenId.KEYWORD);
        pasmKeywords.put("VSCL", TokenId.KEYWORD);
        pasmKeywords.put("PAR", TokenId.KEYWORD);
        pasmKeywords.put("SPR", TokenId.KEYWORD);
    }

    final NodeVisitor collectKeywordsVisitor = new NodeVisitor() {

        String lastLabel = "";

        @Override
        public void visitConstants(ConstantsNode node) {
            if (node.getTextToken() != null) {
                tokens.add(new TokenMarker(node.getTextToken(), TokenId.SECTION));
            }
        }

        @Override
        public void visitConstantAssign(ConstantAssignNode node) {
            if (symbols.containsKey(node.getIdentifier().getText())) {
                tokens.add(new TokenMarker(node.getIdentifier(), TokenId.ERROR));
            }
            else {
                symbols.put(node.getIdentifier().getText(), TokenId.CONSTANT);
                tokens.add(new TokenMarker(node.getIdentifier(), TokenId.CONSTANT));
            }
        }

        @Override
        public void visitConstantAssignEnum(ConstantAssignEnumNode node) {
            if (symbols.containsKey(node.getIdentifier().getText())) {
                tokens.add(new TokenMarker(node.getIdentifier(), TokenId.ERROR));
            }
            else {
                symbols.put(node.getIdentifier().getText(), TokenId.CONSTANT);
                tokens.add(new TokenMarker(node.getIdentifier(), TokenId.CONSTANT));
            }
        }

        @Override
        public void visitVariables(VariablesNode node) {
            tokens.add(new TokenMarker(node.getTokens().get(0), TokenId.SECTION));
        }

        @Override
        public void visitVariable(VariableNode node) {
            if (node.getType() != null) {
                tokens.add(new TokenMarker(node.getType(), TokenId.TYPE));
            }

            String identifier = node.getIdentifier().getText();
            if (symbols.containsKey(identifier)) {
                TokenMarker marker = new TokenMarker(node.getIdentifier(), TokenId.ERROR);
                marker.setError("Symbol already defined");
                tokens.add(marker);
            }
            else {
                symbols.put(identifier, TokenId.VARIABLE);
                symbols.put("@" + identifier, TokenId.VARIABLE);
                tokens.add(new TokenMarker(node.getIdentifier(), TokenId.VARIABLE));
            }
        }

        @Override
        public void visitObjects(ObjectsNode node) {
            tokens.add(new TokenMarker(node.getTokens().get(0), TokenId.SECTION));
            for (Node child : node.getChilds()) {
                ObjectNode obj = (ObjectNode) child;
                if (obj.name != null) {
                    symbols.put(obj.name.getText(), TokenId.OBJECT);
                    tokens.add(new TokenMarker(obj.name, TokenId.OBJECT));
                    if (obj.file != null) {
                        tokens.add(new TokenMarker(obj.file, TokenId.STRING));
                    }
                }
            }
        }

        @Override
        public void visitMethod(MethodNode node) {
            TokenId id = TokenId.METHOD_PUB;
            if ("PRI".equalsIgnoreCase(node.getType().getText())) {
                id = TokenId.METHOD_PRI;
            }
            tokens.add(new TokenMarker(node.getType(), id));

            if (node.getName() != null) {
                if (symbols.containsKey(node.getName().getText())) {
                    TokenMarker marker = new TokenMarker(node.getName(), TokenId.ERROR);
                    marker.setError("Symbol already defined");
                    tokens.add(marker);
                }
                else {
                    symbols.put(node.getName().getText(), id);
                    tokens.add(new TokenMarker(node.getName(), id));
                }
            }

            for (Node child : node.getParameters()) {
                tokens.add(new TokenMarker(child, TokenId.METHOD_LOCAL));
            }

            for (Node child : node.getReturnVariables()) {
                tokens.add(new TokenMarker(child, TokenId.METHOD_RETURN));
            }
        }

        @Override
        public void visitData(DataNode node) {
            lastLabel = "";
            tokens.add(new TokenMarker(node.getTokens().get(0), TokenId.SECTION));
        }

        @Override
        public void visitDataLine(DataLineNode node) {
            if (node.label != null) {
                String s = node.label.getText();
                if (s.startsWith(":")) {
                    if (symbols.containsKey(lastLabel + s)) {
                        TokenMarker marker = new TokenMarker(node.label, TokenId.ERROR);
                        marker.setError("Symbol already defined");
                        tokens.add(marker);
                    }
                    else {
                        symbols.put(lastLabel + s, TokenId.PASM_LOCAL_LABEL);
                        symbols.put(lastLabel + "@" + s, TokenId.PASM_LOCAL_LABEL);
                        tokens.add(new TokenMarker(node.label, TokenId.PASM_LOCAL_LABEL));
                    }
                }
                else {
                    TokenId id = symbols.get(s);
                    if (id != null) {
                        TokenMarker marker = new TokenMarker(node.label, TokenId.ERROR);
                        marker.setError("Symbol already defined");
                        tokens.add(marker);
                    }
                    else {
                        symbols.put(s, TokenId.PASM_LABEL);
                        symbols.put("@" + s, TokenId.PASM_LABEL);
                        tokens.add(new TokenMarker(node.label, TokenId.PASM_LABEL));
                    }
                    lastLabel = s;
                }
            }
            if (node.condition != null) {
                tokens.add(new TokenMarker(node.condition, TokenId.PASM_CONDITION));
            }
            if (node.instruction != null) {
                TokenId id = keywords.get(node.instruction.getText().toUpperCase());
                if (id == null || id != TokenId.TYPE) {
                    id = TokenId.PASM_INSTRUCTION;
                }
                tokens.add(new TokenMarker(node.instruction, id));
            }
            if (node.modifier != null) {
                tokens.add(new TokenMarker(node.modifier, TokenId.PASM_MODIFIER));
            }
        }

    };

    final NodeVisitor updateReferencesVisitor = new NodeVisitor() {

        String lastLabel = "";
        Map<String, TokenId> locals = new HashMap<String, TokenId>();

        @Override
        public void visitObjects(ObjectsNode node) {
            tokens.add(new TokenMarker(node.getTokens().get(0), TokenId.SECTION));
            for (Node child : node.getChilds()) {
                ObjectNode obj = (ObjectNode) child;
                if (obj.count != null) {
                    markTokens(obj.count);
                }
            }
        }

        @Override
        public void visitMethod(MethodNode node) {
            locals.clear();

            for (Node child : node.getParameters()) {
                locals.put(child.getText(), TokenId.METHOD_LOCAL);
                locals.put("@" + child.getText(), TokenId.METHOD_LOCAL);
            }
            for (Node child : node.getReturnVariables()) {
                locals.put(child.getText(), TokenId.METHOD_RETURN);
                locals.put("@" + child.getText(), TokenId.METHOD_RETURN);
            }

            for (LocalVariableNode child : node.getLocalVariables()) {
                if (child.type != null) {
                    tokens.add(new TokenMarker(child.type, TokenId.TYPE));
                }
                locals.put(child.identifier.getText(), TokenId.METHOD_LOCAL);
                locals.put("@" + child.identifier.getText(), TokenId.METHOD_LOCAL);
                if (symbols.containsKey(child.identifier.getText())) {
                    TokenMarker marker = new TokenMarker(child.identifier, TokenId.ERROR);
                    marker.setError("Symbol already defined");
                    tokens.add(marker);
                }
                else {
                    tokens.add(new TokenMarker(child.identifier, TokenId.METHOD_LOCAL));
                }
            }

            for (Node child : node.getChilds()) {
                if (child instanceof StatementNode) {
                    markTokens(child);
                }
            }
        }

        void markTokens(Node node) {
            for (Token token : node.getTokens()) {
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
                    TokenId id = keywords.get(token.getText().toUpperCase());
                    if (id == null) {
                        id = locals.get(token.getText());
                    }
                    if (id == null) {
                        id = symbols.get(token.getText());
                    }
                    if (id == null) {
                        if (token.getText().indexOf('.') <= 0) {
                            TokenMarker marker = new TokenMarker(token, TokenId.ERROR);
                            marker.setError("Symbol is undefined");
                            tokens.add(marker);
                        }
                    }
                    else {
                        tokens.add(new TokenMarker(token, id));
                    }
                }
            }
            for (Node child : node.getChilds()) {
                markTokens(child);
            }
        }

        @Override
        public void visitData(DataNode node) {
            lastLabel = "";
        }

        @Override
        public void visitDataLine(DataLineNode node) {
            if (node.label != null) {
                String s = node.label.getText();
                if (!s.startsWith(":")) {
                    lastLabel = s;
                }
            }

            for (ParameterNode parameter : node.parameters) {
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
                        if (s.startsWith(":") || s.startsWith("@:")) {
                            s = lastLabel + s;
                        }
                        id = symbols.get(s);
                        if (id == null) {
                            id = pasmKeywords.get(token.getText().toUpperCase());
                        }
                        if (id == null) {
                            TokenMarker marker = new TokenMarker(token, TokenId.ERROR);
                            marker.setError("Symbol is undefined");
                            tokens.add(marker);
                        }
                        else {
                            tokens.add(new TokenMarker(token, id));
                        }
                    }
                }
            }
        }

        @Override
        public void visitExpression(ExpressionNode node) {
            for (Token token : node.getTokens()) {
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
                    TokenId id = keywords.get(token.getText().toUpperCase());
                    if (id == null) {
                        id = symbols.get(token.getText());
                    }
                    if (id == null) {
                        TokenMarker marker = new TokenMarker(token, TokenId.ERROR);
                        marker.setError("Symbol is undefined");
                        tokens.add(marker);
                    }
                    else {
                        tokens.add(new TokenMarker(token, id));
                    }
                }
            }
        }

        @Override
        public void visitError(ErrorNode node) {
            TokenMarker marker = new TokenMarker(node.getStartToken(), node.getStopToken(), TokenId.ERROR);
            if (node.getDescription() != null) {
                marker.setError(node.getDescription());
            }
            tokens.add(marker);
        }

    };

    public Spin1TokenMarker() {

    }

    @Override
    public void refreshTokens(String text) {
        tokens.clear();
        symbols.clear();
        compilerTokens.clear();

        Spin1TokenStream stream = new Spin1TokenStream(text);
        Spin1Parser subject = new Spin1Parser(stream);
        root = subject.parse();

        // Comments are hidden from the parser
        for (Token token : stream.getHiddenTokens()) {
            tokens.add(new TokenMarker(token, TokenId.COMMENT));
        }

        // Collect known keywords and symbols
        root.accept(collectKeywordsVisitor);

        // Update symbols references from expressions
        root.accept(updateReferencesVisitor);
    }

}
