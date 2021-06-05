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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.maccasoft.propeller.spin.Spin2Parser.ConstantAssignEnumNode;
import com.maccasoft.propeller.spin.Spin2Parser.ConstantAssignNode;
import com.maccasoft.propeller.spin.Spin2Parser.ConstantsNode;
import com.maccasoft.propeller.spin.Spin2Parser.DataLineNode;
import com.maccasoft.propeller.spin.Spin2Parser.DataNode;
import com.maccasoft.propeller.spin.Spin2Parser.ErrorNode;
import com.maccasoft.propeller.spin.Spin2Parser.ExpressionNode;
import com.maccasoft.propeller.spin.Spin2Parser.LocalVariableNode;
import com.maccasoft.propeller.spin.Spin2Parser.MethodNode;
import com.maccasoft.propeller.spin.Spin2Parser.Node;
import com.maccasoft.propeller.spin.Spin2Parser.ObjectsNode;
import com.maccasoft.propeller.spin.Spin2Parser.ParameterNode;
import com.maccasoft.propeller.spin.Spin2Parser.StatementNode;
import com.maccasoft.propeller.spin.Spin2Parser.VariableNode;
import com.maccasoft.propeller.spin.Spin2Parser.VariablesNode;
import com.maccasoft.propeller.spin.Spin2TokenStream.Token;

public class Spin2TokenMarker {

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

        keywords.put("HUBSET", TokenId.FUNCTION);
        keywords.put("CLKSET", TokenId.FUNCTION);
        keywords.put("COGSPIN", TokenId.FUNCTION);
        keywords.put("COGINIT", TokenId.FUNCTION);
        keywords.put("COGSTOP", TokenId.FUNCTION);
        keywords.put("COGID", TokenId.FUNCTION);
        keywords.put("COGCHK", TokenId.FUNCTION);
        keywords.put("LOCKNEW", TokenId.FUNCTION);
        keywords.put("LOCKRET", TokenId.FUNCTION);
        keywords.put("LOCKTRY", TokenId.FUNCTION);
        keywords.put("LOCKREL", TokenId.FUNCTION);
        keywords.put("LOCKCHK", TokenId.FUNCTION);
        keywords.put("COGATN", TokenId.FUNCTION);
        keywords.put("POLLATN", TokenId.FUNCTION);
        keywords.put("WAITATN", TokenId.FUNCTION);

        keywords.put("PINW", TokenId.FUNCTION);
        keywords.put("PINWRITE", TokenId.FUNCTION);
        keywords.put("PINL", TokenId.FUNCTION);
        keywords.put("PINLOW", TokenId.FUNCTION);
        keywords.put("PINH", TokenId.FUNCTION);
        keywords.put("PINHIGH", TokenId.FUNCTION);
        keywords.put("PINT", TokenId.FUNCTION);
        keywords.put("PINTOGGLE", TokenId.FUNCTION);
        keywords.put("PINF", TokenId.FUNCTION);
        keywords.put("PINFLOAT", TokenId.FUNCTION);
        keywords.put("PINR", TokenId.FUNCTION);
        keywords.put("PINREAD", TokenId.FUNCTION);
        keywords.put("PINSTART", TokenId.FUNCTION);
        keywords.put("PINCLEAR", TokenId.FUNCTION);
        keywords.put("WRPIN", TokenId.FUNCTION);
        keywords.put("WXPIN", TokenId.FUNCTION);
        keywords.put("WYPIN", TokenId.FUNCTION);
        keywords.put("AKPIN", TokenId.FUNCTION);
        keywords.put("RDPIN", TokenId.FUNCTION);
        keywords.put("RQPIN", TokenId.FUNCTION);

        keywords.put("GETCT", TokenId.FUNCTION);
        keywords.put("POLLCT", TokenId.FUNCTION);
        keywords.put("WAITCT", TokenId.FUNCTION);
        keywords.put("WAITUS", TokenId.FUNCTION);
        keywords.put("WAITMS", TokenId.FUNCTION);
        keywords.put("GETSEC", TokenId.FUNCTION);
        keywords.put("GETMS", TokenId.FUNCTION);

        keywords.put("CALL", TokenId.FUNCTION);
        keywords.put("REGEXEC", TokenId.FUNCTION);
        keywords.put("REGLOAD", TokenId.FUNCTION);

        keywords.put("ROTXY", TokenId.FUNCTION);
        keywords.put("POLXY", TokenId.FUNCTION);
        keywords.put("XYPOL", TokenId.FUNCTION);
        keywords.put("QSIN", TokenId.FUNCTION);
        keywords.put("QCOS", TokenId.FUNCTION);
        keywords.put("MULDIV64", TokenId.FUNCTION);

        keywords.put("GETREGS", TokenId.FUNCTION);
        keywords.put("SETREGS", TokenId.FUNCTION);
        keywords.put("BYTEMOVE", TokenId.FUNCTION);
        keywords.put("WORDMOVE", TokenId.FUNCTION);
        keywords.put("LONGMOVE", TokenId.FUNCTION);
        keywords.put("BYTEFILL", TokenId.FUNCTION);
        keywords.put("WORDFILL", TokenId.FUNCTION);
        keywords.put("LONGFILL", TokenId.FUNCTION);

        keywords.put("STRSIZE", TokenId.FUNCTION);
        keywords.put("STRCOMP", TokenId.FUNCTION);
        keywords.put("STRING", TokenId.FUNCTION);

        keywords.put("LOOKUP", TokenId.FUNCTION);
        keywords.put("LOOKUPZ", TokenId.FUNCTION);
        keywords.put("LOOKDOWN", TokenId.FUNCTION);
        keywords.put("LOOKDOWNZ", TokenId.FUNCTION);

        keywords.put("ABORT", TokenId.KEYWORD);
        keywords.put("SEND", TokenId.KEYWORD);
        keywords.put("RECV", TokenId.KEYWORD);
        keywords.put("IF", TokenId.KEYWORD);
        keywords.put("IFNOT", TokenId.KEYWORD);
        keywords.put("ELSEIF", TokenId.KEYWORD);
        keywords.put("ELSEIFNOT", TokenId.KEYWORD);
        keywords.put("ELSE", TokenId.KEYWORD);
        keywords.put("CASE", TokenId.KEYWORD);
        keywords.put("CASE_FAST", TokenId.KEYWORD);
        keywords.put("OTHER", TokenId.KEYWORD);
        keywords.put("REPEAT", TokenId.KEYWORD);
        keywords.put("FROM", TokenId.KEYWORD);
        keywords.put("TO", TokenId.KEYWORD);
        keywords.put("STEP", TokenId.KEYWORD);
        keywords.put("WHILE", TokenId.KEYWORD);
        keywords.put("UNTIL", TokenId.KEYWORD);
        keywords.put("NEXT", TokenId.KEYWORD);
        keywords.put("QUIT", TokenId.KEYWORD);

        keywords.put("END", TokenId.KEYWORD);

        keywords.put("ROUND", TokenId.KEYWORD);
        keywords.put("ADDPINS", TokenId.KEYWORD);
        keywords.put("ADDBITS", TokenId.KEYWORD);

        keywords.put("_CLKFREQ", TokenId.CONSTANT);
        keywords.put("_CLKMODE", TokenId.CONSTANT);
        keywords.put("CLKFREQ", TokenId.CONSTANT);
        keywords.put("CLKMODE", TokenId.CONSTANT);
        keywords.put("VARBASE", TokenId.CONSTANT);

        keywords.put("TRUE", TokenId.CONSTANT);
        keywords.put("FALSE", TokenId.CONSTANT);
        keywords.put("POSX", TokenId.CONSTANT);
        keywords.put("NEGX", TokenId.CONSTANT);
        keywords.put("PI", TokenId.CONSTANT);

        keywords.put("PR0", TokenId.PASM_INSTRUCTION);
        keywords.put("PR1", TokenId.PASM_INSTRUCTION);
        keywords.put("PR2", TokenId.PASM_INSTRUCTION);
        keywords.put("PR3", TokenId.PASM_INSTRUCTION);
        keywords.put("PR4", TokenId.PASM_INSTRUCTION);
        keywords.put("PR5", TokenId.PASM_INSTRUCTION);
        keywords.put("PR6", TokenId.PASM_INSTRUCTION);
        keywords.put("PR7", TokenId.PASM_INSTRUCTION);
    }

    static Map<String, TokenId> pasmKeywords = new HashMap<String, TokenId>();
    static {
        pasmKeywords.put("IJMP3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("IRET3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("IJMP2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("IRET2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("IJMP1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("IRET1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PTRA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PTRB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DIRA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DIRB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("OUTA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("OUTB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("INA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("INB", TokenId.PASM_INSTRUCTION);

        pasmKeywords.put("_CLR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NC_AND_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z_AND_NC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_GT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NC_AND_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z_AND_NC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_GE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_AND_NZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NZ_AND_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_NE_NZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NZ_NE_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_OR_NZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NZ_OR_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_AND_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z_AND_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_EQ_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z_EQ_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_E", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NC_OR_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z_OR_NC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_LT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_OR_NZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NZ_OR_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_OR_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z_OR_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_LE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_SET", TokenId.PASM_INSTRUCTION);
    }

    public static class TokenMarker implements Comparable<TokenMarker> {

        int start;
        int stop;
        TokenId id;

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

    }

    TreeSet<TokenMarker> tokens = new TreeSet<TokenMarker>();

    Map<String, TokenId> symbols = new HashMap<String, TokenId>();

    final Spin2ParserVisitor collectKeywordsVisitor = new Spin2ParserVisitor() {

        String lastLabel = "";

        @Override
        public void visitConstants(ConstantsNode node) {
            tokens.add(new TokenMarker(node.getTokens().get(0), TokenId.SECTION));
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

            if (symbols.containsKey(node.getIdentifier().getText())) {
                tokens.add(new TokenMarker(node.getIdentifier(), TokenId.ERROR));
            }
            else {
                symbols.put(node.getIdentifier().getText(), TokenId.VARIABLE);
                tokens.add(new TokenMarker(node.getIdentifier(), TokenId.VARIABLE));
            }
        }

        @Override
        public void visitObjects(ObjectsNode node) {
            tokens.add(new TokenMarker(node.getTokens().get(0), TokenId.SECTION));
        }

        @Override
        public void visitMethod(MethodNode node) {
            if ("PRI".equalsIgnoreCase(node.getType().getText())) {
                tokens.add(new TokenMarker(node.getType(), TokenId.METHOD_PRI));
                if (node.getName() != null) {
                    tokens.add(new TokenMarker(node.getName(), TokenId.METHOD_PRI));
                }
            }
            else {
                tokens.add(new TokenMarker(node.getType(), TokenId.METHOD_PUB));
                if (node.getName() != null) {
                    tokens.add(new TokenMarker(node.getName(), TokenId.METHOD_PUB));
                }
            }

            for (Node child : node.getParameters()) {
                tokens.add(new TokenMarker(child, TokenId.METHOD_LOCAL));
            }
            for (Node child : node.getReturnVariables()) {
                tokens.add(new TokenMarker(child, TokenId.METHOD_RETURN));
            }
            for (LocalVariableNode child : node.getLocalVariables()) {
                if (child.type != null) {
                    tokens.add(new TokenMarker(child.type, TokenId.TYPE));
                }
                tokens.add(new TokenMarker(child.identifier, TokenId.METHOD_LOCAL));
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
                if (s.startsWith(".")) {
                    if (symbols.containsKey(lastLabel + s)) {
                        tokens.add(new TokenMarker(node.label, TokenId.ERROR));
                    }
                    else {
                        symbols.put(lastLabel + s, TokenId.PASM_LOCAL_LABEL);
                        symbols.put(lastLabel + "@" + s, TokenId.PASM_LOCAL_LABEL);
                        tokens.add(new TokenMarker(node.label, TokenId.PASM_LOCAL_LABEL));
                    }
                }
                else {
                    TokenId id = symbols.get(s);
                    if (id == null) {
                        id = pasmKeywords.get(s);
                    }
                    if (id != null) {
                        tokens.add(new TokenMarker(node.label, TokenId.ERROR));
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
                tokens.add(new TokenMarker(node.instruction, TokenId.PASM_INSTRUCTION));
            }
            if (node.modifier != null) {
                tokens.add(new TokenMarker(node.modifier, TokenId.PASM_MODIFIER));
            }
        }

    };

    final Spin2ParserVisitor updateReferencesVisitor = new Spin2ParserVisitor() {

        String lastLabel = "";
        Map<String, TokenId> locals = new HashMap<String, TokenId>();

        @Override
        public void visitMethod(MethodNode node) {
            locals.clear();

            for (Node child : node.getParameters()) {
                locals.put(child.getText(), TokenId.METHOD_LOCAL);
            }
            for (Node child : node.getReturnVariables()) {
                locals.put(child.getText(), TokenId.METHOD_RETURN);
            }
            for (LocalVariableNode child : node.getLocalVariables()) {
                locals.put(child.identifier.getText(), TokenId.METHOD_LOCAL);
            }
            for (Node child : node.childs) {
                if (child instanceof StatementNode) {
                    markTokens(child);
                }
            }
        }

        void markTokens(Node node) {
            for (Token token : node.getTokens()) {
                TokenId id = null;
                if (token.type == Spin2TokenStream.NUMBER) {
                    id = TokenId.NUMBER;
                }
                else if (token.type == Spin2TokenStream.OPERATOR) {
                    id = TokenId.OPERATOR;
                }
                else if (token.type == Spin2TokenStream.STRING) {
                    id = TokenId.STRING;
                }
                if (id == null) {
                    id = keywords.get(token.getText().toUpperCase());
                }
                if (id == null) {
                    id = locals.get(token.getText());
                }
                if (id == null) {
                    id = symbols.get(token.getText());
                }
                if (id != null) {
                    tokens.add(new TokenMarker(token, id));
                }
            }
            for (Node child : node.childs) {
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
                if (!s.startsWith(".")) {
                    lastLabel = s;
                }
            }

            for (ParameterNode parameter : node.parameters) {
                for (Token token : parameter.getTokens()) {
                    TokenId id = null;
                    if (token.type == Spin2TokenStream.NUMBER) {
                        id = TokenId.NUMBER;
                    }
                    else if (token.type == Spin2TokenStream.OPERATOR) {
                        id = TokenId.OPERATOR;
                    }
                    else if (token.type == Spin2TokenStream.STRING) {
                        id = TokenId.STRING;
                    }
                    else {
                        String s = token.getText();
                        if (s.startsWith(".") || s.startsWith("@.")) {
                            s = lastLabel + s;
                        }
                        id = symbols.get(s);
                    }
                    if (id == null) {
                        id = pasmKeywords.get(token.getText().toUpperCase());
                    }
                    if (id == null) {
                        id = TokenId.ERROR;
                    }
                    tokens.add(new TokenMarker(token, id));
                }
            }
        }

        @Override
        public void visitExpression(ExpressionNode node) {
            for (Token token : node.getTokens()) {
                TokenId id = null;
                if (token.type == Spin2TokenStream.NUMBER) {
                    id = TokenId.NUMBER;
                }
                else if (token.type == Spin2TokenStream.OPERATOR) {
                    id = TokenId.OPERATOR;
                }
                else if (token.type == Spin2TokenStream.STRING) {
                    id = TokenId.STRING;
                }
                else {
                    id = symbols.get(token.getText());
                }
                if (id == null) {
                    id = keywords.get(token.getText().toUpperCase());
                }
                if (id == null) {
                    id = TokenId.ERROR;
                }
                tokens.add(new TokenMarker(token, id));
            }
        }

        @Override
        public void visitError(ErrorNode node) {
            for (Token token : node.getTokens()) {
                tokens.add(new TokenMarker(token, TokenId.ERROR));
            }
        }

    };

    public Spin2TokenMarker() {

    }

    public void refreshTokens(String text) {
        tokens.clear();
        symbols.clear();

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser subject = new Spin2Parser(stream);
        Node root = subject.parse();

        // Comments are hidden from the parser
        for (Token token : stream.getHiddenTokens()) {
            tokens.add(new TokenMarker(token, TokenId.COMMENT));
        }

        // Collect known keywords and symbols
        root.accept(collectKeywordsVisitor);

        // Update symbols references from expressions
        root.accept(updateReferencesVisitor);
    }

    public Set<TokenMarker> getLineTokens(int lineStart, String lineText) {
        return getLineTokens(lineStart, lineStart + lineText.length());
    }

    public Set<TokenMarker> getLineTokens(int lineStart, int lineStop) {
        Set<TokenMarker> result = new TreeSet<TokenMarker>();

        TokenMarker firstMarker = tokens.floor(new TokenMarker(lineStart, lineStop, null));
        if (firstMarker == null) {
            return new TreeSet<TokenMarker>();
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

        return result;
    }

}
