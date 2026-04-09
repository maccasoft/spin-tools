/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.maccasoft.propeller.SourceTokenMarker;
import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.RootNode;
import com.maccasoft.propeller.model.SourceProvider;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenStream;
import com.maccasoft.propeller.model.TokenStream.Position;
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
        keywords.put("BYTEFIT", TokenId.TYPE);
        keywords.put("WORDFIT", TokenId.TYPE);

        keywords.put("ABORT", TokenId.KEYWORD);
        keywords.put("BYTECODE", TokenId.FUNCTION);
        keywords.put("BYTEFILL", TokenId.FUNCTION);
        keywords.put("BYTEMOVE", TokenId.FUNCTION);
        keywords.put("CASE", TokenId.KEYWORD);
        keywords.put("OTHER", TokenId.KEYWORD);
        keywords.put("CHIPVER", TokenId.FUNCTION);
        keywords.put("CLKFREQ", TokenId.FUNCTION);
        keywords.put("CLKMODE", TokenId.FUNCTION);
        keywords.put("CLKSET", TokenId.FUNCTION);
        keywords.put("COGID", TokenId.FUNCTION);
        keywords.put("COGINIT", TokenId.FUNCTION);
        keywords.put("COGNEW", TokenId.FUNCTION);
        keywords.put("COGSTOP", TokenId.FUNCTION);
        keywords.put("CONSTANT", TokenId.FUNCTION);
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
        keywords.put("ROUND", TokenId.FUNCTION);
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

        keywords.put("defined", TokenId.DIRECTIVE);

        keywords.put("__DATE__", TokenId.CONSTANT);
        keywords.put("__TIME__", TokenId.CONSTANT);
        keywords.put("__FILE__", TokenId.CONSTANT);
        keywords.put("__P1__", TokenId.CONSTANT);
        keywords.put("__SPINTOOLS__", TokenId.CONSTANT);
        keywords.put("__VERSION__", TokenId.CONSTANT);
        keywords.put("__DEBUG__", TokenId.CONSTANT);
        keywords.put("__propeller__", TokenId.CONSTANT);
        keywords.put("__propeller1__", TokenId.CONSTANT);
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

        pasmKeywords.put("END", TokenId.PASM_INSTRUCTION);
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

    private static final Set<String> preprocessor = new HashSet<>(Arrays.asList(new String[] {
        "define", "ifdef", "elifdef", "elseifdef", "ifndef", "elifndef", "elseifndef", "else", "if", "elif", "elseif", "endif",
        "error", "warning", "pragma", "undef"
    }));

    public Spin1TokenMarker(SourceProvider sourceProvider) {
        super(sourceProvider);
        this.constantSeparator = "#";
        this.localLabelPrefix = ":";
    }

    @Override
    public void refreshTokens(String text) {
        Spin1Parser parser = new Spin1Parser(text);

        RootNode root = parser.parse();
        if (this.root == null || this.root.getChildCount() == 0) {
            comments.addAll(root.getComments());
        }
        this.root = root;

        collectTokens(root);
    }

    @Override
    public void setRoot(RootNode root) {
        symbols.clear();
        locals.clear();
        excludedPaths.clear();

        comments.clear();
        comments.addAll(root.getComments());
        collectTokens(root);

        super.setRoot(root);
    }

    void collectTokens(Node root) {
        for (Node child : root.getChilds()) {
            if (child.isExclude()) {
                excludedPaths.add(child.getPath());
            }
            switch (child) {
                case DirectiveNode.DefineNode node -> {
                    if (!isExcludedNode(node) && node.identifier != null) {
                        symbols.put(node.identifier.getText(), TokenId.CONSTANT);
                    }
                }
                case ConstantsNode node -> {
                    collectTokens(node);
                }
                case ConstantNode node -> {
                    if (node.identifier != null) {
                        symbols.put(node.identifier.getText(), TokenId.CONSTANT);
                    }
                }
                case VariablesNode node -> {
                    collectTokens(node);
                }
                case VariableNode node -> {
                    if (node.identifier != null) {
                        symbols.put(node.identifier.getText(), TokenId.VARIABLE);
                    }
                }
                case ObjectsNode node -> {
                    collectTokens(node);
                }
                case ObjectNode node -> {
                    if (node.name != null) {
                        String name = node.name.getText();
                        symbols.put(name, TokenId.OBJECT);
                        RootNode objectRoot = node.getRoot().getObjectRoot(name);
                        if (objectRoot != null) {
                            collectObjectTokens(name, objectRoot);
                        }
                    }
                }
                case MethodNode node -> {
                    if (node.name != null) {
                        symbols.put(node.name.getText(), node.isPublic() ? TokenId.METHOD_PUB : TokenId.METHOD_PRI);
                        Map<String, TokenId> methodLocals = locals.computeIfAbsent(node.name.getText(), k -> isCaseSensitive() ? new HashMap<>() : new CaseInsensitiveMap<>());
                        for (MethodNode.ParameterNode var : node.getParameters()) {
                            if (var.identifier != null) {
                                methodLocals.put(var.identifier.getText(), TokenId.METHOD_PARAMETER);
                            }
                        }
                        for (MethodNode.ReturnNode var : node.getReturnVariables()) {
                            if (var.identifier != null) {
                                methodLocals.put(var.identifier.getText(), TokenId.METHOD_RETURN);
                            }
                        }
                        for (MethodNode.LocalVariableNode var : node.getLocalVariables()) {
                            if (var.identifier != null) {
                                methodLocals.put(var.identifier.getText(), TokenId.METHOD_LOCAL);
                            }
                        }
                    }
                }
                case DataNode node -> {
                    collectTokens(node);
                }
                case DataLineNode node -> {
                    if (node.label != null && !node.label.getText().startsWith(":")) {
                        symbols.put(node.label.getText(), TokenId.PASM_LABEL);
                    }
                }
                default -> {
                }
            }
        }
    }

    void collectObjectTokens(String qualifier, Node root) {
        for (Node child : root.getChilds()) {
            if (child.isExclude()) {
                excludedPaths.add(child.getPath());
            }
            switch (child) {
                case ConstantsNode node -> {
                    collectObjectTokens(qualifier, node);
                }
                case ConstantNode node -> {
                    if (node.identifier != null) {
                        symbols.put(qualifier + "#" + node.identifier.getText(), TokenId.CONSTANT);
                    }
                }
                case MethodNode node -> {
                    if (node.name != null && node.isPublic()) {
                        symbols.put(qualifier + "." + node.name.getText(), TokenId.METHOD_PUB);
                    }
                }
                default -> {
                }
            }
        }
    }

    @Override
    public Collection<TokenMarker> getTokens(int lineIndex, int lineOffset, String lineText) {
        Token token;
        int startIndex = 0;
        List<TokenMarker> markers = new ArrayList<>();

        for (Token blockCommentToken : comments) {
            if (lineOffset >= blockCommentToken.start && lineOffset <= blockCommentToken.stop) {
                int index = blockCommentToken.stop - lineOffset + 1;
                if (index >= lineText.length()) {
                    markers.add(new TokenMarker(0, lineText.length(), TokenId.COMMENT));
                    return markers;
                }
                markers.add(new TokenMarker(0, index, TokenId.COMMENT));
                startIndex = index;
                break;
            }
        }

        Node contextNode = null;
        for (Node node : root.getChilds()) {
            if (node.getTokenCount() != 0 && lineIndex >= node.getStartToken().line) {
                contextNode = node;
            }
        }

        Spin1TokenStream stream = new Spin1TokenStream(lineText, startIndex);

        while ((token = stream.peekNext()).type != Token.EOF) {
            if ("#".equals(token.getText())) {
                Position pos = stream.mark();
                Token preprocessorToken = stream.nextToken(); // #
                Token directiveToken = stream.nextToken();
                if (preprocessor.contains(directiveToken.getText())) {
                    while ((token = stream.nextToken()).type != Token.EOF) {
                        if (token.type == Token.NL) {
                            break;
                        }
                    }
                    markers.add(new TokenMarker(preprocessorToken.start, token.stop, TokenId.DIRECTIVE));
                    continue;
                }
                stream.restore(pos);
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                markers.add(new TokenMarker(stream.nextToken(), TokenId.COMMENT));
            }
            else if (token.type == Token.NUMBER) {
                markers.add(new TokenMarker(stream.nextToken(), TokenId.NUMBER));
            }
            else if (token.type == Token.STRING) {
                markers.add(new TokenMarker(stream.nextToken(), TokenId.STRING));
            }
            else if ("CON".equalsIgnoreCase(token.getText())) {
                markers.add(new TokenMarker(stream.nextToken(), TokenId.SECTION));
                parseConstant(stream, markers);
            }
            else if ("VAR".equalsIgnoreCase(token.getText())) {
                markers.add(new TokenMarker(stream.nextToken(), TokenId.SECTION));
                parseVariable(stream, contextNode, markers);
            }
            else if ("OBJ".equalsIgnoreCase(token.getText())) {
                markers.add(new TokenMarker(stream.nextToken(), TokenId.SECTION));
                parseObject(stream, markers);
            }
            else if ("PUB".equalsIgnoreCase(token.getText())) {
                markers.add(new TokenMarker(stream.nextToken(), TokenId.METHOD_PUB));
                parseMethod(TokenId.METHOD_PUB, stream, markers);
            }
            else if ("PRI".equalsIgnoreCase(token.getText())) {
                markers.add(new TokenMarker(stream.nextToken(), TokenId.METHOD_PRI));
                parseMethod(TokenId.METHOD_PRI, stream, markers);
            }
            else if ("DAT".equalsIgnoreCase(token.getText())) {
                markers.add(new TokenMarker(stream.nextToken(), TokenId.SECTION));
                parseDatLine(stream, "-", markers);
            }
            else {
                if (contextNode instanceof VariablesNode) {
                    parseVariable(stream, contextNode, markers);
                }
                else if (contextNode instanceof ObjectsNode) {
                    parseObject(stream, markers);
                }
                else if (contextNode instanceof MethodNode methodNode) {
                    markTokens(contextNode, stream, markers, null);
                }
                else if (contextNode instanceof DataNode) {
                    String lastLabel = "-";

                    for (Node node : contextNode.getChilds()) {
                        if (node.getStartIndex() > lineOffset + lineText.length()) {
                            break;
                        }
                        if (node instanceof DataLineNode dataLineNode) {
                            if (dataLineNode.label != null) {
                                String s = dataLineNode.label.getText();
                                if (!s.startsWith(":") && !s.startsWith(".")) {
                                    lastLabel = s;
                                }
                            }
                        }
                    }

                    parseDatLine(stream, lastLabel, markers);
                }
                else {
                    parseConstant(stream, markers);
                }
            }
        }

        return markers;
    }

    void parseConstant(TokenStream stream, List<TokenMarker> markers) {
        Token token;
        int state = 1;

        while ((token = stream.nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                markers.add(new TokenMarker(token, TokenId.COMMENT));
                continue;
            }
            switch (state) {
                case 0, 5:
                    if (",".equals(token.getText())) {
                        state = 1;
                        break;
                    }
                    markConstantToken(token, markers);
                    break;
                case 1:
                    if ("#".equals(token.getText())) {
                        state = 2;
                        break;
                    }
                    markers.add(new TokenMarker(token, TokenId.CONSTANT));
                    state = 4;
                    break;
                case 2:
                    if (",".equals(token.getText())) {
                        state = 1;
                        break;
                    }
                    if ("[".equals(token.getText())) {
                        state = 3;
                        break;
                    }
                    markConstantToken(token, markers);
                    break;
                case 3, 6:
                    if ("]".equals(token.getText())) {
                        state = 0;
                        break;
                    }
                    markConstantToken(token, markers);
                    break;
                case 4:
                    if (",".equals(token.getText())) {
                        state = 1;
                        break;
                    }
                    if ("=".equals(token.getText())) {
                        state = 5;
                    }
                    if ("[".equals(token.getText())) {
                        state = 6;
                        break;
                    }
                    markConstantToken(token, markers);
                    break;
            }
        }
    }

    void parseVariable(TokenStream stream, Node contextNode, List<TokenMarker> markers) {
        Token token;
        int state = 1;

        while ((token = stream.nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                markers.add(new TokenMarker(token, TokenId.COMMENT));
                continue;
            }

            switch (state) {
                case 1:
                    if (Spin1Model.isType(token.getText())) {
                        markers.add(new TokenMarker(token, TokenId.TYPE));
                        state = 2;
                        break;
                    }
                    // fall-through
                case 2:
                    markers.add(new TokenMarker(token, TokenId.VARIABLE));
                    state = 3;
                    break;

                case 3:
                    if (",".equals(token.getText())) {
                        state = 1;
                        break;
                    }
                    if ("[".equals(token.getText())) {
                        markTokens(contextNode, stream, markers, "]");
                        break;
                    }
                    break;
            }
        }
    }

    void parseObject(TokenStream stream, List<TokenMarker> markers) {
        Token token;
        int state = 1;

        while ((token = stream.nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                markers.add(new TokenMarker(token, TokenId.COMMENT));
                continue;
            }

            TokenId id = null;
            if (state == 1) {
                if (token.type == Token.KEYWORD) {
                    id = TokenId.OBJECT;
                }
                state = 0;
            }
            else {
                if (token.type == Token.NUMBER) {
                    id = TokenId.NUMBER;
                }
                else if (token.type == Token.STRING) {
                    id = token.getText().length() > 3 ? TokenId.STRING : TokenId.NUMBER;
                }
                else {
                    id = symbols.get(token.getText());
                    if (id == null) {
                        id = keywords.get(token.getText());
                        if (id == null) {
                            id = spinKeywords.get(token.getText());
                        }
                    }
                }
            }
            if (id != null) {
                markers.add(new TokenMarker(token, id));
            }
        }
    }

    void parseMethod(TokenId type, TokenStream stream, List<TokenMarker> markers) {
        Token token;
        int state = 1;

        while ((token = stream.nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                markers.add(new TokenMarker(token, TokenId.COMMENT));
                continue;
            }

            if (state == 4 || state == 7 || state == 9) {
                if ("^".equals(token.getText())) {
                    Token nextToken = stream.peekNext();
                    if (nextToken != null && token.isAdjacent(nextToken) && nextToken.type == Token.KEYWORD) {
                        token = token.merge(stream.nextToken());
                        token.type = Token.KEYWORD;
                    }
                }
            }

            TokenId id = null;

            switch (state) {
                case 1:
                    id = type;
                    state = 2;
                    break;
                case 2:
                    if ("(".equals(token.getText())) {
                        state = 4;
                        break;
                    }
                    if (":".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    if ("|".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    break;

                case 4:
                    if (",".equals(token.getText())) {
                        break;
                    }
                    if (")".equals(token.getText())) {
                        state = 6;
                        break;
                    }
                    if (Spin1Model.isType(token.getText())) {
                        id = TokenId.TYPE;
                        break;
                    }
                    if (token.type == Token.KEYWORD) {
                        Token next = stream.peekNext();
                        if (next.type == Token.KEYWORD) {
                            id = TokenId.TYPE;
                            break;
                        }
                        id = TokenId.METHOD_PARAMETER;
                        break;
                    }
                    if ("=".equals(token.getText())) {
                        state = 5;
                    }
                    break;
                case 5:
                    if (",".equals(token.getText())) {
                        state = 4;
                        break;
                    }
                    if (")".equals(token.getText())) {
                        state = 6;
                        break;
                    }
                    break;

                case 6:
                    if (":".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    if ("|".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    break;

                case 7:
                    id = TokenId.METHOD_RETURN;
                    state = 8;
                    break;
                case 8:
                    if (",".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    else if ("|".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    break;

                case 9:
                    if ("alignw".equalsIgnoreCase(token.getText()) || "alignl".equalsIgnoreCase(token.getText())) {
                        id = TokenId.KEYWORD;
                        break;
                    }
                    if (Spin1Model.isType(token.getText())) {
                        id = TokenId.TYPE;
                        state = 10;
                        break;
                    }
                    if (token.type == Token.KEYWORD) {
                        Token next = stream.peekNext();
                        if (next != null && next.type == Token.KEYWORD) {
                            id = TokenId.TYPE;
                            state = 10;
                            break;
                        }
                    }
                    // fall-through
                case 10:
                    id = TokenId.METHOD_LOCAL;
                    state = 11;
                    break;
                case 11:
                    if (",".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    if (":".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    if ("[".equals(token.getText())) {
                        state = 12;
                        break;
                    }
                    break;
                case 12:
                    if ("]".equals(token.getText())) {
                        state = 11;
                        break;
                    }
                    break;
            }

            if (token.type == Token.NUMBER) {
                id = TokenId.NUMBER;
            }
            else if (token.type == Token.STRING) {
                id = token.getText().length() > 3 ? TokenId.STRING : TokenId.NUMBER;
            }
            else if (id == null) {
                id = symbols.get(token.getText());
                if (id == null) {
                    id = keywords.get(token.getText());
                    if (id == null) {
                        id = spinKeywords.get(token.getText());
                    }
                }
            }
            if (id != null) {
                markers.add(new TokenMarker(token, id));
            }
        }
    }

    void parseDatLine(TokenStream stream, String lastLabel, List<TokenMarker> markers) {
        Token token;
        int state = 1;

        while ((token = stream.nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                markers.add(new TokenMarker(token, TokenId.COMMENT));
                continue;
            }

            TokenId id = null;

            if (state == 4) {
                if ("@".equals(token.getText()) || "@@".equals(token.getText()) || "@@@".equals(token.getText())) {
                    Token nextToken = stream.peekNext();
                    if (":".equals(nextToken.getText()) && token.isAdjacent(nextToken)) {
                        token = token.merge(stream.nextToken());
                        nextToken = stream.peekNext();
                    }
                    if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                        token = token.merge(stream.nextToken());
                    }
                }
            }
            if (state == 1 || state == 4) {
                if (":".equals(token.getText())) {
                    Token nextToken = stream.peekNext();
                    if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                        token = token.merge(stream.nextToken());
                    }
                }
            }

            switch (state) {
                case 1:
                    if (Spin1Model.isPAsmCondition(token.getText())) {
                        id = TokenId.PASM_CONDITION;
                        state = 3;
                        break;
                    }
                    if (Spin1Model.isPAsmInstruction(token.getText())) {
                        id = TokenId.PASM_INSTRUCTION;
                        state = 4;
                        break;
                    }
                    id = token.getText().startsWith(":") || token.getText().startsWith(".") ? TokenId.PASM_LOCAL_LABEL : TokenId.PASM_LABEL;
                    state = 2;
                    break;
                case 2:
                    if (Spin1Model.isPAsmCondition(token.getText())) {
                        id = TokenId.PASM_CONDITION;
                        state = 3;
                        break;
                    }
                    // fall-through
                case 3:
                    if (Spin1Model.isPAsmInstruction(token.getText())) {
                        id = TokenId.PASM_INSTRUCTION;
                    }
                    state = 4;
                    break;
                case 4:
                    if (Spin1Model.isPAsmModifier(token.getText())) {
                        id = TokenId.PASM_MODIFIER;
                        state = 6;
                        break;
                    }
                    break;
            }

            if (id == null) {
                if (token.type == Token.NUMBER) {
                    id = TokenId.NUMBER;
                }
                else if (token.type == Token.STRING) {
                    id = token.getText().length() > 3 ? TokenId.STRING : TokenId.NUMBER;
                }
                else if (token.getText().startsWith(":")) {
                    id = TokenId.PASM_LOCAL_LABEL;
                }
                else {
                    id = symbols.get(token.getText());
                    if (id == null) {
                        int index = token.getText().indexOf('.');
                        if (index != -1) {
                            String left = token.getText().substring(0, index);
                            id = symbols.get(left);
                            if (id == TokenId.OBJECT) {
                                markers.add(new TokenMarker(token.start, token.start + index - 1, id));
                                //markers.add(new TokenMarker(token.start + index + 1, token.stop, TokenId.CONSTANT));
                            }
                        }
                    }
                    if (id == null) {
                        id = keywords.get(token.getText());
                        if (id == null) {
                            id = spinKeywords.get(token.getText());
                        }
                    }
                }
            }
            if (id != null) {
                markers.add(new TokenMarker(token, id));
            }
        }
    }

    void markConstantToken(Token token, List<TokenMarker> markers) {
        if (token.type == Token.NUMBER) {
            markers.add(new TokenMarker(token, TokenId.NUMBER));
        }
        else if (token.type == Token.STRING) {
            markers.add(new TokenMarker(token, token.getText().length() > 3 ? TokenId.STRING : TokenId.NUMBER));
        }
        else {
            TokenId id = symbols.get(token.getText());
            if (id == null) {
                id = keywords.get(token.getText());
            }
            if (id == null && token.type == Token.KEYWORD) {
                int index = token.getText().indexOf('#');
                if (index > 0) {
                    String left = token.getText().substring(0, index);
                    id = symbols.get(left);
                    if (id == TokenId.OBJECT) {
                        markers.add(new TokenMarker(token.start, token.start + index - 1, id));
                        //markers.add(new TokenMarker(token.start + index + 1, token.stop, TokenId.CONSTANT));
                        return;
                    }
                }
            }
            if (id != null) {
                markers.add(new TokenMarker(token, id));
            }
        }
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

    void markTokens(Node contextNode, TokenStream stream, List<TokenMarker> markers, String endMarker) {
        Token token;

        while ((token = stream.nextToken()).type != Token.EOF) {
            String tokenText = token.getText();
            if (tokenText.equals(endMarker)) {
                return;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                markers.add(new TokenMarker(token, TokenId.COMMENT));
            }
            else if (token.type == Token.NUMBER) {
                markers.add(new TokenMarker(token, TokenId.NUMBER));
            }
            else if (token.type == Token.STRING) {
                markers.add(new TokenMarker(token, token.getText().length() > 3 ? TokenId.STRING : TokenId.NUMBER));
            }
            else if (token.type != Token.OPERATOR) {
                TokenId id = symbols.get(tokenText);
                if (id == TokenId.OBJECT) {
                    String qualifier = token.getText();
                    markers.add(new TokenMarker(token, id));
                    token = stream.nextToken();
                    if ("[".equals(token.getText())) {
                        markTokens(contextNode, stream, markers, "]");
                        token = stream.nextToken();
                        if (".".equals(token.getText())) {
                            Token nextToken = stream.peekNext();
                            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                                token = stream.nextToken();
                                id = symbols.get(qualifier + "." + token.getText());
                                if (id != null) {
                                    markers.add(new TokenMarker(token, id));
                                }
                                continue;
                            }
                        }
                    }
                    id = null;
                }
                else if (id == TokenId.METHOD_PUB || id == TokenId.CONSTANT) {
                    int dot = tokenText.indexOf('.');
                    if (dot == -1) {
                        dot = tokenText.indexOf('#');
                    }
                    if (dot != -1) {
                        markers.add(new TokenMarker(token.start, token.start + dot - 1, TokenId.OBJECT));
                        markers.add(new TokenMarker(token.start + dot + 1, token.stop, id));
                        continue;
                    }
                }
                if (id == null) {
                    if (contextNode instanceof MethodNode methodNode) {
                        if (methodNode.name != null) {
                            Map<String, TokenId> localSymbols = locals.get(methodNode.name.getText());
                            if (localSymbols != null) {
                                id = localSymbols.get(tokenText);
                            }
                        }
                    }
                    if (id == null) {
                        id = keywords.get(tokenText);
                        if (id == null) {
                            id = spinKeywords.get(tokenText);
                        }
                    }
                }
                if (id != null) {
                    markers.add(new TokenMarker(token, id));
                }
            }
            else if ("[".equals(tokenText)) {
                markTokens(contextNode, stream, markers, "]");
            }
        }
    }

}
