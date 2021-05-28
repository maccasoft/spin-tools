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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.maccasoft.propeller.spin.Spin2Parser.ConstantAssignContext;
import com.maccasoft.propeller.spin.Spin2Parser.ConstantsSectionContext;
import com.maccasoft.propeller.spin.Spin2Parser.DataContext;
import com.maccasoft.propeller.spin.Spin2Parser.DataLineContext;
import com.maccasoft.propeller.spin.Spin2Parser.FunctionContext;
import com.maccasoft.propeller.spin.Spin2Parser.IdentifierContext;
import com.maccasoft.propeller.spin.Spin2Parser.LocalvarContext;
import com.maccasoft.propeller.spin.Spin2Parser.MethodContext;
import com.maccasoft.propeller.spin.Spin2Parser.ObjectContext;
import com.maccasoft.propeller.spin.Spin2Parser.ObjectsSectionContext;
import com.maccasoft.propeller.spin.Spin2Parser.ParametersContext;
import com.maccasoft.propeller.spin.Spin2Parser.ProgContext;
import com.maccasoft.propeller.spin.Spin2Parser.ResultContext;
import com.maccasoft.propeller.spin.Spin2Parser.VariableContext;
import com.maccasoft.propeller.spin.Spin2Parser.VariablesSectionContext;

@SuppressWarnings({
    "unchecked", "rawtypes"
})
public class Spin2TokenMarker {

    public static enum TokenId {
        NULL,
        COMMENT,
        SECTION,
        NUMBER,
        STRING,

        KEYWORD,

        CONSTANT,
        VARIABLE,

        OBJECT,
        PUB_METHOD,
        PRI_METHOD,
        LOCAL_VAR,
        METHOD_PARAM,

        LABEL,

        WARNING,
        ERROR
    }

    static Set<Integer> commentSet = new HashSet<Integer>(Arrays.asList(new Integer[] {
        Spin2Lexer.COMMENT, Spin2Lexer.BLOCK_COMMENT
    }));
    static Set<Integer> numberSet = new HashSet<Integer>(Arrays.asList(new Integer[] {
        Spin2Lexer.BIN, Spin2Lexer.HEX, Spin2Lexer.NUMBER, Spin2Lexer.QUAD
    }));
    static Set<Integer> keywordSet = new HashSet<Integer>(Arrays.asList(new Integer[] {
        Spin2Lexer.REPEAT, Spin2Lexer.WHILE, Spin2Lexer.UNTIL, Spin2Lexer.FROM, Spin2Lexer.TO, Spin2Lexer.STEP,
        Spin2Lexer.IF, Spin2Lexer.IFNOT, Spin2Lexer.ELSE, Spin2Lexer.ELSEIF, Spin2Lexer.ELSEIFNOT,
        Spin2Lexer.CASE, Spin2Lexer.OTHER,
    }));

    static Map<String, TokenId> keywords = new HashMap<String, TokenId>();
    static {
        keywords.put("HUBSET", TokenId.KEYWORD);
        keywords.put("CLKSET", TokenId.KEYWORD);
        keywords.put("COGSPIN", TokenId.KEYWORD);
        keywords.put("WAITUS", TokenId.KEYWORD);
        keywords.put("WAITMS", TokenId.KEYWORD);
        keywords.put("WAITCT", TokenId.KEYWORD);
        keywords.put("GETSEC", TokenId.KEYWORD);
        keywords.put("GETMS", TokenId.KEYWORD);
        keywords.put("REGEXEC", TokenId.KEYWORD);
        keywords.put("REGLOAD", TokenId.KEYWORD);
        keywords.put("ROTXY", TokenId.KEYWORD);
        keywords.put("POLXY", TokenId.KEYWORD);
        keywords.put("XYPOL", TokenId.KEYWORD);
        keywords.put("QSIN", TokenId.KEYWORD);
        keywords.put("QCOS", TokenId.KEYWORD);
        keywords.put("MULDIV64", TokenId.KEYWORD);
        keywords.put("GETREGS", TokenId.KEYWORD);
        keywords.put("SETREGS", TokenId.KEYWORD);
        keywords.put("BYTEMOVE", TokenId.KEYWORD);
        keywords.put("WORDMOVE", TokenId.KEYWORD);
        keywords.put("LONGMOVE", TokenId.KEYWORD);
        keywords.put("BYTEFILL", TokenId.KEYWORD);
        keywords.put("WORDFILL", TokenId.KEYWORD);
        keywords.put("LONGFILL", TokenId.KEYWORD);
        keywords.put("STRSIZE", TokenId.KEYWORD);
        keywords.put("STRCOMP", TokenId.KEYWORD);
        keywords.put("STRING", TokenId.KEYWORD);
        keywords.put("LOOKUP", TokenId.KEYWORD);
        keywords.put("LOOKUPZ", TokenId.KEYWORD);
        keywords.put("LOOKDOWN", TokenId.KEYWORD);
        keywords.put("LOOKDOWNZ", TokenId.KEYWORD);
        keywords.put("PINT", TokenId.KEYWORD);

        keywords.put("ABORT", TokenId.KEYWORD);
        keywords.put("SEND", TokenId.KEYWORD);
        keywords.put("RECV", TokenId.KEYWORD);
        keywords.put("IF", TokenId.KEYWORD);
        keywords.put("CASE", TokenId.KEYWORD);
        keywords.put("CASE_FAST", TokenId.KEYWORD);
        keywords.put("OTHER", TokenId.KEYWORD);
        keywords.put("IFNOT", TokenId.KEYWORD);
        keywords.put("ELSEIF", TokenId.KEYWORD);
        keywords.put("ELSEIFNOT", TokenId.KEYWORD);
        keywords.put("ELSE", TokenId.KEYWORD);
        keywords.put("REPEAT", TokenId.KEYWORD);
        keywords.put("FROM", TokenId.KEYWORD);
        keywords.put("TO", TokenId.KEYWORD);
        keywords.put("STEP", TokenId.KEYWORD);
        keywords.put("WHILE", TokenId.KEYWORD);
        keywords.put("UNTIL", TokenId.KEYWORD);
        keywords.put("QUIT", TokenId.KEYWORD);
        keywords.put("END", TokenId.KEYWORD);
    }

    Comparator<Token> tokenComparator = new Comparator<Token>() {

        @Override
        public int compare(Token o1, Token o2) {
            return o1.getStartIndex() - o2.getStartIndex();
        }

    };

    Map<Token, TokenId> tokens = new TreeMap<Token, TokenId>(tokenComparator);

    Map<String, TokenId> symbols = new HashMap<String, TokenId>();

    final Spin2ParserBaseListener parserListener = new Spin2ParserBaseListener() {

        @Override
        public void enterConstantsSection(ConstantsSectionContext ctx) {
            tokens.put(ctx.getStart(), TokenId.SECTION);
        }

        @Override
        public void exitConstantAssign(ConstantAssignContext ctx) {
            if (ctx.name != null) {
                if (!symbols.containsKey(ctx.name.getText())) {
                    symbols.put(ctx.name.getText(), TokenId.CONSTANT);
                    tokens.put(ctx.name, TokenId.CONSTANT);
                }
                else {
                    tokens.put(ctx.name, TokenId.ERROR);
                }
            }
        }

        @Override
        public void enterVariablesSection(VariablesSectionContext ctx) {
            tokens.put(ctx.getStart(), TokenId.SECTION);
        }

        @Override
        public void exitVariable(VariableContext ctx) {
            if (ctx.name != null) {
                if (!symbols.containsKey(ctx.name.getText())) {
                    symbols.put(ctx.name.getText(), TokenId.VARIABLE);
                    tokens.put(ctx.name, TokenId.VARIABLE);
                }
                else {
                    tokens.put(ctx.name, TokenId.ERROR);
                }
            }
        }

        @Override
        public void enterObjectsSection(ObjectsSectionContext ctx) {
            tokens.put(ctx.getStart(), TokenId.SECTION);
        }

        @Override
        public void exitObject(ObjectContext ctx) {
            if (ctx.name != null) {
                if (!symbols.containsKey(ctx.name.getText())) {
                    symbols.put(ctx.name.getText(), TokenId.OBJECT);
                    tokens.put(ctx.name, TokenId.OBJECT);
                }
                else {
                    tokens.put(ctx.name, TokenId.ERROR);
                }
            }
        }

        @Override
        public void exitMethod(MethodContext ctx) {
            tokens.put(ctx.getStart(), TokenId.SECTION);
            if (ctx.name != null) {
                if (symbols.containsKey(ctx.name.getText())) {
                    tokens.put(ctx.name, TokenId.ERROR);
                }
                else if (ctx.PRI_START() != null) {
                    symbols.put(ctx.name.getText(), TokenId.PRI_METHOD);
                    tokens.put(ctx.name, TokenId.PRI_METHOD);
                }
                else {
                    symbols.put(ctx.name.getText(), TokenId.PUB_METHOD);
                    tokens.put(ctx.name, TokenId.PUB_METHOD);
                }
            }
        }

        @Override
        public void enterData(DataContext ctx) {
            tokens.put(ctx.getStart(), TokenId.SECTION);
        }

        @Override
        public void exitDataLine(DataLineContext ctx) {
            if (ctx.label() != null) {
                String text = ctx.label().getText();
                if (!text.startsWith(".")) {
                    if (!symbols.containsKey(text)) {
                        symbols.put(text, TokenId.LABEL);
                        tokens.put(ctx.label().getStart(), TokenId.LABEL);
                        tokens.put(ctx.label().getStop(), TokenId.LABEL);
                    }
                }
            }
        }

        @Override
        public void visitTerminal(TerminalNode node) {
            int type = node.getSymbol().getType();
            if (numberSet.contains(type)) {
                tokens.put(node.getSymbol(), TokenId.NUMBER);
            }
            else if (type == Spin2Lexer.STRING) {
                tokens.put(node.getSymbol(), TokenId.STRING);
            }
            else if (keywordSet.contains(type) || keywords.containsKey(node.getSymbol().getText().toUpperCase())) {
                tokens.put(node.getSymbol(), TokenId.KEYWORD);
            }
        }

    };

    final Spin2ParserBaseVisitor parserVisitor = new Spin2ParserBaseVisitor() {

        @Override
        public Object visitMethod(MethodContext ctx) {
            ctx.accept(new Spin2ParserBaseVisitor() {

                Map<String, TokenId> locals = new HashMap<String, TokenId>();

                @Override
                public Object visitParameters(ParametersContext ctx) {
                    for (TerminalNode node : ctx.IDENTIFIER()) {
                        String text = node.getSymbol().getText();
                        if (!symbols.containsKey(text) && !locals.containsKey(text)) {
                            locals.put(text, TokenId.METHOD_PARAM);
                            tokens.put(node.getSymbol(), TokenId.METHOD_PARAM);
                        }
                        else {
                            tokens.put(node.getSymbol(), TokenId.ERROR);
                        }
                    }
                    return null;
                }

                @Override
                public Object visitResult(ResultContext ctx) {
                    for (TerminalNode node : ctx.IDENTIFIER()) {
                        String text = node.getSymbol().getText();
                        if (!symbols.containsKey(text) && !locals.containsKey(text)) {
                            locals.put(text, TokenId.LOCAL_VAR);
                            tokens.put(node.getSymbol(), TokenId.LOCAL_VAR);
                        }
                        else {
                            tokens.put(node.getSymbol(), TokenId.ERROR);
                        }
                    }
                    return null;
                }

                @Override
                public Object visitLocalvar(LocalvarContext ctx) {
                    if (ctx.name != null) {
                        String text = ctx.name.getText();
                        if (!symbols.containsKey(text) && !locals.containsKey(text)) {
                            locals.put(ctx.name.getText(), TokenId.LOCAL_VAR);
                            tokens.put(ctx.name, TokenId.LOCAL_VAR);
                        }
                        else {
                            tokens.put(ctx.name, TokenId.ERROR);
                        }
                    }
                    return null;
                }

                @Override
                public Object visitIdentifier(IdentifierContext ctx) {
                    if (ctx.name != null) {
                        TokenId id = symbols.get(ctx.name.getText());
                        if (id == null) {
                            id = locals.get(ctx.name.getText());
                        }
                        if (id == null) {
                            id = TokenId.ERROR;
                        }
                        tokens.put(ctx.name, id);
                    }
                    return null;
                }

                @Override
                public Object visitFunction(FunctionContext ctx) {
                    if (ctx.obj != null) {
                        TokenId id = symbols.get(ctx.obj.getText());
                        if (id == null) {
                            id = TokenId.ERROR;
                        }
                        tokens.put(ctx.obj, id);
                    }
                    if (ctx.name != null) {
                        TokenId id = symbols.get(ctx.name.getText());
                        if (id == null) {
                            id = TokenId.ERROR;
                        }
                        tokens.put(ctx.name, id);
                    }
                    return super.visitFunction(ctx);
                }

            });
            return null;
        }

        @Override
        public Object visitIdentifier(IdentifierContext ctx) {
            if (ctx.name != null) {
                TokenId id = symbols.get(ctx.name.getText());
                if (id == null) {
                    id = TokenId.ERROR;
                }
                tokens.put(ctx.name, id);
            }
            return null;
        }

    };

    public Spin2TokenMarker() {

    }

    public void refreshTokens(String text) {
        tokens.clear();
        symbols.clear();

        Spin2Lexer lexer = new Spin2Lexer(CharStreams.fromString(text));
        lexer.removeErrorListeners();

        CommonTokenStream stream = new CommonTokenStream(lexer);
        Spin2Parser parser = new Spin2Parser(stream);
        parser.removeErrorListeners();
        parser.addParseListener(parserListener);

        ProgContext context = parser.prog();

        context.accept(parserVisitor);

        List<Token> list = stream.getTokens(0, stream.size() - 1, commentSet);
        if (list != null) {
            for (Token token : list) {
                tokens.put(token, TokenId.COMMENT);
            }
        }
    }

    public Map<Token, TokenId> getLineTokens(int lineStart, int lineStop) {
        Map<Token, TokenId> result = new TreeMap<Token, TokenId>(tokenComparator);

        for (Entry<Token, TokenId> entry : tokens.entrySet()) {
            int start = entry.getKey().getStartIndex();
            int stop = entry.getKey().getStopIndex();
            if ((lineStart >= start && lineStart <= stop) || (lineStop >= start && lineStop <= stop)) {
                result.put(entry.getKey(), entry.getValue());
            }
            else if (stop >= lineStart && stop <= lineStop) {
                result.put(entry.getKey(), entry.getValue());
            }
            if (start >= lineStop) {
                break;
            }
        }

        return result;
    }
}
