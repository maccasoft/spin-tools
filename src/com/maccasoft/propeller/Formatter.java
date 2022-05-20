/*
 * Copyright (c) 2022 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenStream;

public abstract class Formatter {

    private static final Set<String> sections = new HashSet<String>(Arrays.asList(new String[] {
        "CON", "VAR", "OBJ", "PUB", "PRI", "DAT"
    }));

    public static Set<String> types = new HashSet<String>(Arrays.asList(new String[] {
        "LONG", "WORD", "BYTE",
    }));

    class FormatterStringBuilder {
        int line = 0;
        int column = 0;
        StringBuilder sb = new StringBuilder();

        int[] tabstops;
        int tabspaces = 4;

        public void append(String s) {
            sb.append(s);
            if (s.equals(System.lineSeparator())) {
                column = 0;
                line++;
            }
            else {
                column += s.length();
            }
        }

        public void append(Object o) {
            append(o.toString());
        }

        public void alignToTabStop() {
            int next = getNextTabStop(column);
            while (column < next) {
                sb.append(" ");
                column++;
            }
        }

        public int getNextTabStop(int column) {
            int next = ((column + tabspaces - 1) / tabspaces) * tabspaces;
            if (tabstops != null) {
                int i = 0;
                while (i < tabstops.length) {
                    if (tabstops[i] >= column) {
                        next = tabstops[i];
                        break;
                    }
                    i++;
                }
            }
            return next;
        }

        public void alignToColumn(int c) {
            while (column < c) {
                sb.append(" ");
                column++;
            }
        }

        public char lastChar() {
            if (sb.length() == 0) {
                return '\0';
            }
            return sb.charAt(sb.length() - 1);
        }

        @Override
        public String toString() {
            return sb.toString();
        }

    }

    TokenStream stream;

    boolean keepBlankLines;

    int conditionColumn;
    int instructionColumn;
    int argumentsColumn;
    int modifiersColumn;
    int commentsColumn;

    FormatterStringBuilder sb = new FormatterStringBuilder();

    public Formatter() {
        this.keepBlankLines = true;

        this.conditionColumn = 8;
        this.instructionColumn = 16;
        this.argumentsColumn = 24;
        this.modifiersColumn = 36;
        this.commentsColumn = 52;
    }

    public void setKeepBlankLines(boolean keepBlankLines) {
        this.keepBlankLines = keepBlankLines;
    }

    public void setPAsmColumns(int condition, int instruction, int arguments, int modifiers, int comments) {
        conditionColumn = condition;
        instructionColumn = instruction;
        argumentsColumn = arguments;
        modifiersColumn = modifiers;
        commentsColumn = comments;
    }

    public abstract String format(String text);

    protected String format(TokenStream stream) {
        Token token;

        this.stream = stream;

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type == Token.EOF) {
                break;
            }
            if (token.type == Token.NL) {
                if (keepBlankLines) {
                    sb.append(System.lineSeparator());
                }
                stream.nextToken();
            }
            else if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                sb.alignToColumn(token.column);
                if (sb.column > 0 && sb.lastChar() != ' ') {
                    sb.append(" ");
                }
                sb.append(stream.nextToken());
                if (token.type == Token.COMMENT) {
                    if (!keepBlankLines) {
                        sb.append(System.lineSeparator());
                    }
                }
            }
            else {
                if ("VAR".equalsIgnoreCase(token.getText())) {
                    formatVariables();
                }
                else if ("OBJ".equalsIgnoreCase(token.getText())) {
                    formatObjects();
                }
                else if ("PUB".equalsIgnoreCase(token.getText()) || "PRI".equalsIgnoreCase(token.getText())) {
                    formatMethod();
                }
                else if ("DAT".equalsIgnoreCase(token.getText())) {
                    formatDat();
                }
                else if ("CON".equalsIgnoreCase(token.getText())) {
                    formatConstants();
                }
                else {
                    formatConstant();
                }
            }
        }

        return sb.toString();
    }

    void formatConstants() {
        appendSectionHeader(stream.nextToken());
        if (stream.peekNext().type == Token.NL) {
            stream.nextToken();
        }
        else {
            formatConstant();
        }
        if (sections.contains(stream.peekNext().getText().toUpperCase())) {
            return;
        }

        while (stream.peekNext().type != Token.EOF) {
            formatConstant();
            if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                break;
            }
        }
    }

    void formatConstant() {
        int state = 1;

        Token token;
        while ((token = nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                sb.alignToColumn(token.column);
                if (sb.column > 0 && sb.lastChar() != ' ') {
                    sb.append(" ");
                }
                sb.append(token);
                continue;
            }
            switch (state) {
                case 1:
                    sb.alignToColumn(sb.tabspaces);
                    if (token.type == 0 && sb.lastChar() != ' ') {
                        sb.append(" ");
                    }
                    sb.append(token);
                    if ("#".equals(token.getText())) {
                        state = 2;
                        break;
                    }
                    state = 4;
                    break;
                case 2:
                    if (",".equals(token.getText())) {
                        sb.append(token);
                        break;
                    }
                    if ("[".equals(token.getText())) {
                        sb.append(token);
                        state = 3;
                        break;
                    }
                    if (sb.lastChar() != ' ' && sb.lastChar() != '#') {
                        sb.append(" ");
                    }
                    sb.append(token);
                    break;
                case 3:
                    sb.append(token);
                    if ("]".equals(token.getText())) {
                        state = 2;
                        break;
                    }
                    break;
                case 4:
                    if ("=".equals(token.getText())) {
                        sb.append(" ");
                        sb.append(token);
                        state = 5;
                        break;
                    }
                    if (",".equals(token.getText())) {
                        sb.append(token);
                        state = 1;
                        break;
                    }
                    if ("[".equals(token.getText())) {
                        sb.append(token);
                        state = 6;
                        break;
                    }
                    if (sb.lastChar() != ' ') {
                        sb.append(" ");
                    }
                    sb.append(token);
                    break;
                case 5:
                    if (",".equals(token.getText())) {
                        sb.append(token);
                        state = 1;
                        break;
                    }
                    appendExpressionToken(token);
                    break;
                case 6:
                    if ("]".equals(token.getText())) {
                        sb.append(token);
                        state = 1;
                        break;
                    }
                    appendExpressionToken(token);
                    break;
            }
        }

        sb.append(System.lineSeparator());
    }

    void formatVariables() {
        appendSectionHeader(stream.nextToken());
        if (stream.peekNext().type == Token.NL) {
            stream.nextToken();
        }
        else {
            formatVariable();
        }
        if (sections.contains(stream.peekNext().getText().toUpperCase())) {
            return;
        }

        while (stream.peekNext().type != Token.EOF) {
            formatVariable();
            if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                break;
            }
        }
    }

    void formatVariable() {
        int state = 1;

        Token token;
        while ((token = nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                sb.alignToColumn(token.column);
                if (sb.column > 0 && sb.lastChar() != ' ') {
                    sb.append(" ");
                }
                sb.append(token);
                continue;
            }
            switch (state) {
                case 1:
                    sb.alignToColumn(sb.tabspaces);
                    if (types.contains(token.getText().toUpperCase())) {
                        if (sb.lastChar() != ' ') {
                            sb.append(" ");
                        }
                        sb.append(token);
                        state = 2;
                        break;
                    }
                    // fall-through
                case 2:
                    if (",".equals(token.getText())) {
                        sb.append(token);
                        state = 1;
                        break;
                    }
                    if ("[".equals(token.getText())) {
                        sb.append(token);
                        state = 3;
                        break;
                    }
                    if (sb.lastChar() != ' ') {
                        sb.append(" ");
                    }
                    sb.append(token);
                    break;

                case 3:
                    if ("]".equals(token.getText())) {
                        sb.append(token);
                        state = 2;
                        break;
                    }
                    appendExpressionToken(token);
                    break;
            }
        }

        sb.append(System.lineSeparator());
    }

    void formatObjects() {
        appendSectionHeader(stream.nextToken());
        if (stream.peekNext().type == Token.NL) {
            stream.nextToken();
        }
        else {
            formatObject();
        }
        if (sections.contains(stream.peekNext().getText().toUpperCase())) {
            return;
        }

        while (stream.peekNext().type != Token.EOF) {
            formatObject();
            if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                break;
            }
        }
    }

    void formatObject() {
        int state = 1;

        Token token;
        while ((token = nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                sb.alignToColumn(token.column);
                if (sb.column > 0 && sb.lastChar() != ' ') {
                    sb.append(" ");
                }
                sb.append(token);
                continue;
            }
            switch (state) {
                case 1:
                    sb.alignToColumn(sb.tabspaces);
                    sb.append(token);
                    state = 2;
                    break;
                case 2:
                    if ("[".equals(token.getText())) {
                        sb.append(token);
                        state = 5;
                        break;
                    }
                    sb.append(" ");
                    sb.append(token);
                    if (":".equals(token.getText())) {
                        state = 4;
                        break;
                    }
                case 4:
                    sb.append(" ");
                    sb.append(token);
                    state = 8;
                    break;

                case 5:
                    if ("]".equals(token.getText())) {
                        sb.append(token);
                        state = 2;
                        break;
                    }
                    appendExpressionToken(token);
                    break;

                case 8:
                    sb.append(" ");
                    sb.append(token);
                    break;
            }
        }

        sb.append(System.lineSeparator());
    }

    void formatMethod() {
        int state = 1;

        sb.append(stream.nextToken().getText().toUpperCase());

        Token token;
        while ((token = nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    return;
                }
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                sb.alignToColumn(token.column);
                if (sb.column > 0 && sb.lastChar() != ' ') {
                    sb.append(" ");
                }
                sb.append(token);
                continue;
            }
            switch (state) {
                case 1:
                    sb.append(" ");
                    sb.append(token);
                    state = 2;
                    break;
                case 2:
                    if ("(".equals(token.getText())) {
                        sb.append(token);
                        state = 4;
                        break;
                    }
                    sb.append(" ");
                    sb.append(token);
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
                        sb.append(token);
                        break;
                    }
                    if (")".equals(token.getText())) {
                        sb.append(token);
                        state = 6;
                        break;
                    }
                    if (sb.lastChar() != ' ' && sb.lastChar() != '(') {
                        sb.append(" ");
                    }
                    sb.append(token);
                    break;

                case 6:
                    sb.append(" ");
                    sb.append(token);
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
                    sb.append(" ");
                    sb.append(token);
                    state = 8;
                    break;
                case 8:
                    if (",".equals(token.getText())) {
                        sb.append(token);
                        state = 7;
                        break;
                    }
                    sb.append(" ");
                    sb.append(token);
                    if ("|".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    break;

                case 9:
                    if (types.contains(token.getText().toUpperCase())) {
                        sb.append(" ");
                        sb.append(token);
                        state = 10;
                        break;
                    }
                    // fall-through
                case 10:
                    sb.append(" ");
                    sb.append(token);
                    state = 11;
                    break;
                case 11:
                    if (",".equals(token.getText())) {
                        sb.append(token);
                        state = 9;
                        break;
                    }
                    if ("[".equals(token.getText())) {
                        sb.append(token);
                        state = 12;
                        break;
                    }
                    sb.append(" ");
                    sb.append(token);
                    if (":".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    break;
                case 12:
                    if ("]".equals(token.getText())) {
                        sb.append(token);
                        state = 11;
                        break;
                    }
                    appendExpressionToken(token);
                    break;
            }
        }

        sb.append(System.lineSeparator());

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type == Token.NL) {
                if (keepBlankLines) {
                    sb.append(System.lineSeparator());
                }
                stream.nextToken();
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    break;
                }
            }
            formatStatement(0, sb.tabspaces);
            if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                break;
            }
        }
    }

    void formatStatement(int column, int indent) {
        Token token;

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type == Token.NL) {
                if (keepBlankLines) {
                    sb.append(System.lineSeparator());
                }
                stream.nextToken();
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    return;
                }
            }
            else if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                sb.alignToColumn(token.column);
                if (sb.column > 0 && sb.lastChar() != ' ') {
                    sb.append(" ");
                }
                sb.append(stream.nextToken());
            }
            else if (token.column < column) {
                break;
            }
            else {
                Token startToken = nextToken();

                sb.alignToColumn(indent);
                sb.append(startToken);

                while ((token = nextToken()).type != Token.EOF) {
                    if (token.type == Token.NL) {
                        break;
                    }
                    if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                        sb.alignToColumn(token.column);
                        if (sb.column > 0 && sb.lastChar() != ' ') {
                            sb.append(" ");
                        }
                        sb.append(token);
                        continue;
                    }
                    appendExpressionToken(token);
                }

                sb.append(System.lineSeparator());

                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    sb.append(System.lineSeparator());
                    return;
                }

                if ("ORG".equalsIgnoreCase(startToken.getText())) {
                    formatInlineCode(indent);
                    if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                        return;
                    }
                }
                if ("CASE".equalsIgnoreCase(startToken.getText()) || "CASE_FAST".equalsIgnoreCase(startToken.getText())) {
                    formatCaseStatement(startToken.column + 1, indent + sb.tabspaces);
                    if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                        return;
                    }
                }
                else if (isBlockStart(startToken)) {
                    formatStatement(startToken.column + 1, indent + sb.tabspaces);
                    if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                        return;
                    }
                }
            }
        }
    }

    void formatCaseStatement(int column, int indent) {
        Token token;

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type == Token.NL) {
                if (keepBlankLines) {
                    sb.append(System.lineSeparator());
                }
                stream.nextToken();
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    return;
                }
            }
            else if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                sb.alignToColumn(token.column);
                if (sb.column > 0 && sb.lastChar() != ' ') {
                    sb.append(" ");
                }
                sb.append(token);
            }
            else if (token.column < column) {
                break;
            }
            else {
                Token startToken = nextToken();

                sb.alignToColumn(indent);
                sb.append(startToken);

                while ((token = nextToken()).type != Token.EOF) {
                    if (token.type == Token.NL) {
                        if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                            return;
                        }
                        break;
                    }
                    if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                        sb.alignToColumn(token.column);
                        if (sb.column > 0 && sb.lastChar() != ' ') {
                            sb.append(" ");
                        }
                        sb.append(token);
                        continue;
                    }
                    if (":".equals(token.getText())) {
                        sb.append(token);
                        while ((token = stream.peekNext()).type != Token.EOF) {
                            if (token.type != Token.COMMENT && token.type != Token.BLOCK_COMMENT) {
                                break;
                            }
                            sb.alignToColumn(token.column);
                            if (sb.column > 0 && sb.lastChar() != ' ') {
                                sb.append(" ");
                            }
                            sb.append(stream.nextToken());
                        }
                        sb.append(System.lineSeparator());
                        if (token.type == Token.NL) {
                            stream.nextToken();
                        }
                        break;
                    }
                    appendExpressionToken(token);
                }

                formatStatement(startToken.column + 1, indent + sb.tabspaces);
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    return;
                }
            }
        }
    }

    void formatInlineCode(int indent) {

        while (true) {
            Token token = stream.peekNext();
            if (token.type == Token.EOF) {
                break;
            }
            if (token.type == Token.NL) {
                if (keepBlankLines) {
                    sb.append(System.lineSeparator());
                }
                stream.nextToken();
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    break;
                }
            }
            else if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                sb.alignToColumn(token.column);
                if (sb.column > 0 && sb.lastChar() != ' ') {
                    sb.append(" ");
                }
                sb.append(token);
            }
            else if ("END".equalsIgnoreCase(token.getText())) {
                sb.alignToColumn(indent);
                sb.append(stream.nextToken());
                break;
            }
            else {
                formatDatLine();
            }
        }
    }

    protected boolean isBlockStart(Token token) {
        return false;
    }

    void formatDat() {
        appendSectionHeader(stream.nextToken());
        if (stream.peekNext().type == Token.NL) {
            stream.nextToken();
        }
        else {
            formatDatLine();
        }
        if (sections.contains(stream.peekNext().getText().toUpperCase())) {
            return;
        }

        while (stream.peekNext().type != Token.EOF) {
            formatDatLine();
            if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                break;
            }
        }
    }

    void formatDatLine() {
        int state = 1;

        Token token;
        while ((token = nextPAsmToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                sb.alignToColumn(token.column);
                if (sb.column > 0 && sb.lastChar() != ' ') {
                    sb.append(" ");
                }
                sb.append(token);
                continue;
            }
            switch (state) {
                case 1:
                    if ("debug".equalsIgnoreCase(token.getText())) {
                        sb.alignToColumn(instructionColumn);
                        sb.append(token);
                        state = 9;
                        break;
                    }
                    if (pasmCondition(token)) {
                        sb.alignToColumn(conditionColumn);
                        sb.append(token);
                        state = 3;
                        break;
                    }
                    if (pasmInstruction(token)) {
                        sb.alignToColumn(instructionColumn);
                        sb.append(token);
                        state = 4;
                        break;
                    }
                    sb.append(token);
                    state = 2;
                    break;
                case 2:
                    if (pasmCondition(token)) {
                        if (sb.column >= conditionColumn) {
                            sb.append(System.lineSeparator());
                        }
                        sb.alignToColumn(conditionColumn);
                        sb.append(token);
                        state = 3;
                        break;
                    }
                    if (sb.column >= instructionColumn) {
                        sb.append(System.lineSeparator());
                    }
                    // fall-through
                case 3:
                    sb.alignToColumn(instructionColumn);
                    if (sb.lastChar() != ' ') {
                        sb.append(" ");
                    }
                    sb.append(token);
                    state = "debug".equalsIgnoreCase(token.getText()) ? 9 : 4;
                    break;
                case 4:
                    if (pasmModifier(token)) {
                        sb.alignToColumn(modifiersColumn);
                        if (sb.lastChar() != ' ') {
                            sb.append(" ");
                            sb.alignToTabStop();
                        }
                        sb.append(token);
                        state = 6;
                        break;
                    }
                    sb.alignToColumn(argumentsColumn);
                    if ("\\".equals(token.getText()) || "#".equals(token.getText()) || "##".equals(token.getText())) {
                        sb.append(token);
                    }
                    else {
                        appendExpressionToken(token);
                    }
                    state = 5;
                    break;
                case 5:
                    if (",".equals(token.getText())) {
                        sb.append(token);
                        break;
                    }
                    if (pasmModifier(token)) {
                        sb.alignToColumn(modifiersColumn);
                        if (sb.lastChar() != ' ') {
                            sb.append(" ");
                            sb.alignToTabStop();
                        }
                        sb.append(token);
                        state = 6;
                        break;
                    }
                    if (sb.lastChar() == ',') {
                        sb.append(" ");
                    }
                    if ("[".equals(token.getText())) {
                        sb.append(token);
                        state = 7;
                        break;
                    }
                    if ("\\".equals(token.getText()) || "#".equals(token.getText()) || "##".equals(token.getText())) {
                        sb.append(token);
                        break;
                    }
                    if (sb.lastChar() == '\\' || sb.lastChar() == '#') {
                        sb.append(token);
                        break;
                    }
                    appendExpressionToken(token);
                    break;
                case 6:
                    sb.append(token);
                    break;
                case 7:
                    if ("]".equals(token.getText())) {
                        sb.append(token);
                        state = 5;
                        break;
                    }
                    appendExpressionToken(token);
                    break;
                case 9:
                    if ("(".equals(token.getText())) {
                        sb.append(token);
                        break;
                    }
                    appendExpressionToken(token);
                    break;
            }
        }

        sb.append(System.lineSeparator());
    }

    protected boolean pasmCondition(Token token) {
        return false;
    }

    protected boolean pasmInstruction(Token token) {
        return false;
    }

    protected boolean pasmModifier(Token token) {
        return false;
    }

    void appendSectionHeader(Token token) {
        sb.append(token.getText().toUpperCase());

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type != Token.COMMENT && token.type != Token.BLOCK_COMMENT) {
                break;
            }
            sb.alignToColumn(token.column);
            if (sb.column > 0 && sb.lastChar() != ' ') {
                sb.append(" ");
            }
            sb.append(stream.nextToken());
        }
        sb.append(System.lineSeparator());
    }

    void appendExpressionToken(Token token) {
        if (token.type == Token.OPERATOR) {
            switch (token.getText()) {
                case "(":
                case "[":
                case ")":
                case "]":
                case ",":
                case ".":
                case "..":
                case "!":
                case "!!":
                case "++":
                case "--":
                case "??":
                case "||":
                case "~":
                case "~~":
                    sb.append(token);
                    break;
                case "#":
                    if (sb.lastChar() == ',') {
                        sb.append(" ");
                    }
                    sb.append(token);
                    break;
                case "\\":
                    if (sb.lastChar() != ' ') {
                        sb.append(" ");
                    }
                    sb.append(token);
                    break;
                case "+":
                case "-":
                    if (sb.lastChar() != ' ' && sb.lastChar() != '(' && sb.lastChar() != '[') {
                        sb.append(" ");
                    }
                    sb.append(token);
                    sb.append(" ");
                    break;
                default:
                    sb.append(" ");
                    sb.append(token);
                    sb.append(" ");
                    break;
            }
        }
        else {
            if (sb.lastChar() != ' ' && sb.lastChar() != '(' && sb.lastChar() != '[' && sb.lastChar() != '#' && sb.lastChar() != '.') {
                sb.append(" ");
            }
            sb.append(token);
        }
    }

    Token nextToken() {
        Token token = stream.nextToken();
        if ("@".equals(token.getText()) || "@@".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken());
                if (nextToken.type == Token.STRING) {
                    token.type = Token.STRING;
                }
            }
        }
        else if (".".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken());
            }
        }
        return token;
    }

    Token nextPAsmToken() {
        Token token = stream.nextToken();
        if ("@".equals(token.getText()) || "@@".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (".".equals(nextToken.getText()) && token.isAdjacent(nextToken)) {
                token = token.merge(stream.nextToken());
                nextToken = stream.peekNext();
            }
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken());
            }
        }
        else if (".".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken());
            }
        }
        return token;
    }

}
