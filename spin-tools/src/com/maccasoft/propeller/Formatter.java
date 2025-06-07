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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenStream;

public abstract class Formatter {

    private static final Set<String> sections = new HashSet<>(Arrays.asList(
        "CON", "VAR", "OBJ", "PUB", "PRI", "DAT"
    ));

    private static final Set<String> preprocessor = new HashSet<>(Arrays.asList(
        "define", "ifdef", "elifdef", "elseifdef", "ifndef", "elifndef", "elseifndef", "else", "if", "elif", "elseif", "endif",
        "error", "warning"
    ));

    public static Set<String> types = new HashSet<String>(Arrays.asList(new String[] {
        "LONG", "WORD", "BYTE",
    }));

    static class FormatterStringBuilder {
        int column = 0;
        StringBuilder sb = new StringBuilder();

        int[] tabstops;
        int tabspaces = 4;

        int previousLineIndent;

        public FormatterStringBuilder(int tabspaces, int[] tabstops) {
            this.tabspaces = tabspaces;
            this.tabstops = tabstops;
        }

        public void append(String s) {
            if (s.equals(System.lineSeparator())) {
                int idx = sb.length() - column;
                previousLineIndent = 0;
                while (idx > 0 && idx < sb.length()) {
                    char c = sb.charAt(idx);
                    if (c != ' ' && c != '\t') {
                        break;
                    }
                    previousLineIndent++;
                    idx++;
                }
                sb.append(s);
                column = 0;
            }
            else {
                sb.append(s);
                column += s.length();
            }
        }

        public boolean endsWith(String s) {
            if (sb.length() < s.length()) {
                return false;
            }
            return s.equals(sb.substring(sb.length() - s.length()));
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

    public static enum Case {
        None, Upper, Lower
    }

    public static enum Align {
        None, TabStop, Fixed
    }

    protected TokenStream stream;

    Case sectionCase;
    Case builtInConstantsCase;
    Case pasmInstructionsCase;

    Align lineCommentAlign;
    int lineCommentColumn;

    boolean blockCommentIndentAlign;

    Map<String, int[]> sectionTabStops;

    public Formatter() {
        this.sectionCase = Case.None;
        this.builtInConstantsCase = Case.None;
        this.pasmInstructionsCase = Case.None;

        this.lineCommentAlign = Align.None;
        this.lineCommentColumn = 64;

        this.blockCommentIndentAlign = false;

        this.sectionTabStops = new HashMap<>();
    }

    public void setSectionTabStop(String section, int[] tabstops) {
        sectionTabStops.put(section, tabstops);
    }

    public void setSectionCase(Case sectionCase) {
        this.sectionCase = sectionCase;
    }

    public void setBuiltInConstantsCase(Case builtInConstantsCase) {
        this.builtInConstantsCase = builtInConstantsCase;
    }

    public void setPasmInstructionsCase(Case pasmInstructionsCase) {
        this.pasmInstructionsCase = pasmInstructionsCase;
    }

    public void setLineCommentAlign(Align lineCommentAlign) {
        this.lineCommentAlign = lineCommentAlign;
    }

    public void setLineCommentColumn(int lineCommentColumn) {
        this.lineCommentColumn = lineCommentColumn;
    }

    public void setBlockCommentIndentAlign(boolean blockCommentIndentAlign) {
        this.blockCommentIndentAlign = blockCommentIndentAlign;
    }

    public abstract String format(String text);

    protected String format(TokenStream stream) {
        Token token;

        this.stream = stream;

        FormatterStringBuilder sb = new FormatterStringBuilder(4, sectionTabStops.get("con"));

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type == Token.NL) {
                sb.append(System.lineSeparator());
                stream.nextToken();
            }
            else if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                appendComment(sb, stream.nextToken());
            }
            else {
                if ("VAR".equalsIgnoreCase(token.getText())) {
                    sb.append(formatVariables());
                }
                else if ("OBJ".equalsIgnoreCase(token.getText())) {
                    sb.append(formatObjects());
                }
                else if ("PUB".equalsIgnoreCase(token.getText()) || "PRI".equalsIgnoreCase(token.getText())) {
                    sb.append(formatMethod());
                }
                else if ("DAT".equalsIgnoreCase(token.getText())) {
                    sb.append(formatDat());
                }
                else if ("CON".equalsIgnoreCase(token.getText())) {
                    sb.append(formatConstants());
                }
                else {
                    formatConstant(sb);
                }
            }
        }

        return sb.toString();
    }

    FormatterStringBuilder formatConstants() {
        Token token;
        FormatterStringBuilder sb = new FormatterStringBuilder(4, sectionTabStops.get("con"));

        appendSectionHeader(sb, stream.nextToken());
        if (stream.peekNext().type == Token.NL) {
            stream.nextToken();
        }
        else {
            formatConstant(sb);
        }

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (sections.contains(token.getText().toUpperCase())) {
                break;
            }
            formatConstant(sb);
        }

        return sb;
    }

    void formatConstant(FormatterStringBuilder sb) {
        int state = 0;

        Token token;
        while ((token = nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                appendComment(sb, token);
                continue;
            }
            switch (state) {
                case 0:
                    if ("#".equals(token.getText())) {
                        Token next = stream.peekNext();
                        if (preprocessor.contains(next.getText().toLowerCase())) {
                            sb.append(token);
                            sb.append(stream.nextToken());
                            sb.append(" ");
                            while ((token = nextToken()).type != Token.EOF) {
                                if (token.type == Token.NL) {
                                    break;
                                }
                                appendExpressionToken(sb, token);
                            }
                            sb.append(System.lineSeparator());
                            return;
                        }
                    }
                    state = 1;
                    // Fall-through
                case 1:
                    sb.alignToColumn(sb.tabspaces);
                    if (token.type == Token.KEYWORD) {
                        if (sb.lastChar() != ' ') {
                            sb.append(" ");
                        }
                        sb.append(isBuiltInConstant(token) ? convertCase(token, builtInConstantsCase) : token);
                    }
                    else {
                        sb.append(token);
                    }
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
                    sb.append(isBuiltInConstant(token) ? convertCase(token, builtInConstantsCase) : token);
                    break;
                case 3:
                    sb.append(isBuiltInConstant(token) ? convertCase(token, builtInConstantsCase) : token);
                    if ("]".equals(token.getText())) {
                        state = 2;
                        break;
                    }
                    break;
                case 4:
                    if ("(".equals(token.getText()) || ")".equals(token.getText())) {
                        sb.append(token);
                        break;
                    }
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
                    if (sb.lastChar() != ' ' && sb.lastChar() != '(') {
                        sb.append(" ");
                    }
                    sb.append(isBuiltInConstant(token) ? convertCase(token, builtInConstantsCase) : token);
                    break;
                case 5:
                    if (",".equals(token.getText())) {
                        sb.append(token);
                        state = 1;
                        break;
                    }
                    appendExpressionToken(sb, token);
                    break;
                case 6:
                    if ("]".equals(token.getText())) {
                        sb.append(token);
                        state = 1;
                        break;
                    }
                    appendExpressionToken(sb, token);
                    break;
            }
        }

        sb.append(System.lineSeparator());
    }

    FormatterStringBuilder formatVariables() {
        Token token;
        FormatterStringBuilder sb = new FormatterStringBuilder(4, sectionTabStops.get("var"));

        appendSectionHeader(sb, stream.nextToken());
        if (stream.peekNext().type == Token.NL) {
            stream.nextToken();
        }
        else {
            formatVariable(sb);
        }

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (sections.contains(token.getText().toUpperCase())) {
                break;
            }
            if ("#".equals(token.getText())) {
                sb.append(stream.nextToken());
                sb.append(stream.nextToken());
                sb.append(" ");
                while ((token = nextToken()).type != Token.EOF) {
                    if (token.type == Token.NL) {
                        break;
                    }
                    appendExpressionToken(sb, token);
                }
                sb.append(System.lineSeparator());
            }
            else {
                formatVariable(sb);
            }
        }

        return sb;
    }

    void formatVariable(FormatterStringBuilder sb) {
        int state = 1;

        Token token;
        while ((token = nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                appendComment(sb, token);
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
                    appendExpressionToken(sb, token);
                    break;
            }
        }

        sb.append(System.lineSeparator());
    }

    FormatterStringBuilder formatObjects() {
        Token token;
        FormatterStringBuilder sb = new FormatterStringBuilder(4, sectionTabStops.get("obj"));

        appendSectionHeader(sb, stream.nextToken());
        if (stream.peekNext().type == Token.NL) {
            stream.nextToken();
        }
        else {
            formatObject(sb);
        }

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (sections.contains(token.getText().toUpperCase())) {
                break;
            }
            if ("#".equals(token.getText())) {
                sb.append(stream.nextToken());
                sb.append(stream.nextToken());
                sb.append(" ");
                while ((token = nextToken()).type != Token.EOF) {
                    if (token.type == Token.NL) {
                        break;
                    }
                    appendExpressionToken(sb, token);
                }
                sb.append(System.lineSeparator());
            }
            else {
                formatObject(sb);
            }
        }

        return sb;
    }

    void formatObject(FormatterStringBuilder sb) {
        int state = 1;

        Token token;
        while ((token = nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                appendComment(sb, token);
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
                    appendExpressionToken(sb, token);
                    break;

                case 8:
                    sb.append(" ");
                    sb.append(token);
                    break;
            }
        }

        sb.append(System.lineSeparator());
    }

    FormatterStringBuilder formatMethod() {
        int state = 1;
        FormatterStringBuilder sb = new FormatterStringBuilder(4, sectionTabStops.get("pub"));

        sb.append(convertCase(stream.nextToken(), sectionCase));

        Token token;
        while ((token = nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    sb.append(System.lineSeparator());
                    return sb;
                }
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                appendComment(sb, token);
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
                    appendExpressionToken(sb, token);
                    break;
            }
        }

        sb.append(System.lineSeparator());

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                break;
            }
            if ("#".equals(token.getText())) {
                sb.append(stream.nextToken());
                sb.append(stream.nextToken());
                sb.append(" ");
                while ((token = nextToken()).type != Token.EOF) {
                    if (token.type == Token.NL) {
                        break;
                    }
                    appendExpressionToken(sb, token);
                }
                sb.append(System.lineSeparator());
            }
            else {
                formatStatement(sb, 0, sb.getNextTabStop(1));
            }
        }

        return sb;
    }

    void formatStatement(FormatterStringBuilder sb, int column, int indent) {
        Token token;

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type == Token.NL) {
                sb.append(System.lineSeparator());
                stream.nextToken();
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    return;
                }
            }
            else if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                appendComment(sb, stream.nextToken());
            }
            else if ("#".equals(token.getText())) {
                sb.append(stream.nextToken());
                sb.append(stream.nextToken());
                sb.append(" ");
                while ((token = nextToken()).type != Token.EOF) {
                    if (token.type == Token.NL) {
                        break;
                    }
                    appendExpressionToken(sb, token);
                }
                sb.append(System.lineSeparator());
            }
            else if (token.column < column) {
                break;
            }
            else {
                Token startToken = nextToken();

                sb.alignToColumn(indent);
                sb.append(startToken);
                if (isBlockStart(startToken)) {
                    token = stream.peekNext();
                    if (token.type != Token.EOF && token.type != Token.NL) {
                        sb.append(" ");
                    }
                }

                while ((token = nextToken()).type != Token.EOF) {
                    if (token.type == Token.NL) {
                        break;
                    }
                    if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                        appendComment(sb, token);
                        continue;
                    }
                    appendExpressionToken(sb, token);
                }

                sb.append(System.lineSeparator());

                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    sb.append(System.lineSeparator());
                    return;
                }

                if ("ORG".equalsIgnoreCase(startToken.getText())) {
                    sb.append(formatInlineCode(indent));
                    if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                        return;
                    }
                }
                if ("CASE".equalsIgnoreCase(startToken.getText()) || "CASE_FAST".equalsIgnoreCase(startToken.getText())) {
                    formatCaseStatement(sb, startToken.column + 1, sb.getNextTabStop(indent + 1));
                    if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                        return;
                    }
                }
                else if (isBlockStart(startToken)) {
                    formatStatement(sb, startToken.column + 1, sb.getNextTabStop(indent + 1));
                    if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                        return;
                    }
                }
            }
        }
    }

    void formatCaseStatement(FormatterStringBuilder sb, int column, int indent) {
        Token token;

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type == Token.NL) {
                sb.append(System.lineSeparator());
                stream.nextToken();
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    return;
                }
            }
            else if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                appendComment(sb, token);
            }
            else if ("#".equals(token.getText())) {
                sb.append(stream.nextToken());
                sb.append(stream.nextToken());
                sb.append(" ");
                while ((token = nextToken()).type != Token.EOF) {
                    if (token.type == Token.NL) {
                        break;
                    }
                    appendExpressionToken(sb, token);
                }
                sb.append(System.lineSeparator());
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
                        appendComment(sb, token);
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
                    appendExpressionToken(sb, token);
                }

                formatStatement(sb, startToken.column + 1, indent + sb.tabspaces);
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    return;
                }
            }
        }
    }

    FormatterStringBuilder formatInlineCode(int indent) {
        FormatterStringBuilder sb = new FormatterStringBuilder(4, sectionTabStops.get("dat"));

        while (true) {
            Token token = stream.peekNext();
            if (token.type == Token.EOF) {
                break;
            }
            if (token.type == Token.NL) {
                sb.append(System.lineSeparator());
                stream.nextToken();
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    break;
                }
            }
            else if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                appendComment(sb, stream.nextToken());
            }
            else if ("#".equals(token.getText())) {
                sb.append(stream.nextToken());
                sb.append(stream.nextToken());
                sb.append(" ");
                while ((token = nextToken()).type != Token.EOF) {
                    if (token.type == Token.NL) {
                        break;
                    }
                    appendExpressionToken(sb, token);
                }
                sb.append(System.lineSeparator());
            }
            else if ("END".equalsIgnoreCase(token.getText())) {
                sb.alignToColumn(indent);
                sb.append(stream.nextToken());
                break;
            }
            else {
                formatDatLine(sb);
            }
        }

        return sb;
    }

    protected boolean isBlockStart(Token token) {
        return false;
    }

    FormatterStringBuilder formatDat() {
        Token token;
        FormatterStringBuilder sb = new FormatterStringBuilder(4, sectionTabStops.get("dat"));

        appendSectionHeader(sb, stream.nextToken());
        if (stream.peekNext().type == Token.NL) {
            stream.nextToken();
        }
        else {
            formatDatLine(sb);
        }

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (sections.contains(token.getText().toUpperCase())) {
                break;
            }
            if ("#".equals(token.getText())) {
                sb.append(stream.nextToken());
                sb.append(stream.nextToken());
                sb.append(" ");
                while ((token = nextToken()).type != Token.EOF) {
                    if (token.type == Token.NL) {
                        break;
                    }
                    appendExpressionToken(sb, token);
                }
                sb.append(System.lineSeparator());
            }
            else {
                formatDatLine(sb);
            }
        }

        return sb;
    }

    void formatDatLine(FormatterStringBuilder sb) {
        int state = 1;
        int conditionColumn = sb.tabstops != null && sb.tabstops.length >= 1 ? sb.tabstops[0] : 8;
        int instructionColumn = sb.tabstops != null && sb.tabstops.length >= 2 ? sb.tabstops[1] : 16;
        int argumentsColumn = sb.tabstops != null && sb.tabstops.length >= 3 ? sb.tabstops[2] : 24;
        int modifiersColumn = sb.tabstops != null && sb.tabstops.length >= 4 ? sb.tabstops[3] : 36;

        Token token;
        while ((token = nextPAsmToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                appendComment(sb, token);
                continue;
            }
            switch (state) {
                case 1:
                    if ("debug".equalsIgnoreCase(token.getText())) {
                        sb.alignToColumn(instructionColumn);
                        sb.append(convertCase(token, pasmInstructionsCase));
                        state = 9;
                        break;
                    }
                    if (pasmCondition(token)) {
                        sb.alignToColumn(conditionColumn);
                        sb.append(convertCase(token, pasmInstructionsCase));
                        state = 3;
                        break;
                    }
                    if (pasmInstruction(token)) {
                        sb.alignToColumn(instructionColumn);
                        sb.append(convertCase(token, pasmInstructionsCase));
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
                        sb.append(convertCase(token, pasmInstructionsCase));
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
                    sb.append(convertCase(token, pasmInstructionsCase));
                    state = "debug".equalsIgnoreCase(token.getText()) ? 9 : 4;
                    break;
                case 4:
                    if (pasmModifier(token)) {
                        sb.alignToColumn(modifiersColumn);
                        if (sb.lastChar() != ' ') {
                            sb.append(" ");
                            sb.alignToTabStop();
                        }
                        sb.append(convertCase(token, pasmInstructionsCase));
                        state = 6;
                        break;
                    }
                    sb.alignToColumn(argumentsColumn);
                    if ("\\".equals(token.getText()) || "#".equals(token.getText()) || "##".equals(token.getText()) || "@".equals(token.getText())) {
                        sb.append(token);
                    }
                    else {
                        appendExpressionToken(sb, token);
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
                        sb.append(convertCase(token, pasmInstructionsCase));
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
                    if (sb.lastChar() == '\\' || sb.lastChar() == '#' || sb.lastChar() == '@') {
                        sb.append(token);
                        break;
                    }
                    appendExpressionToken(sb, token);
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
                    appendExpressionToken(sb, token);
                    break;
                case 9:
                    if ("(".equals(token.getText())) {
                        sb.append(token);
                        break;
                    }
                    appendExpressionToken(sb, token);
                    break;
            }
        }

        sb.append(System.lineSeparator());
    }

    String convertCase(Token token, Case to) {
        if (to == Case.Upper) {
            return token.getText().toUpperCase();
        }
        if (to == Case.Lower) {
            return token.getText().toLowerCase();
        }
        return token.getText();
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

    protected boolean isBuiltInConstant(Token token) {
        return false;
    }

    void appendSectionHeader(FormatterStringBuilder sb, Token token) {
        sb.append(convertCase(token, sectionCase));

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type != Token.COMMENT && token.type != Token.BLOCK_COMMENT) {
                break;
            }
            appendComment(sb, stream.nextToken());
        }
        sb.append(System.lineSeparator());
    }

    void appendExpressionToken(FormatterStringBuilder sb, Token token) {
        char lc = sb.lastChar();

        if (token.type == Token.OPERATOR) {
            switch (token.getText()) {
                case "(":
                case ")":
                case "[":
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
                    if (lc == ',') {
                        sb.append(" ");
                    }
                    sb.append(token);
                    break;
                case "\\":
                    if (lc != ' ' && lc != '#') {
                        sb.append(" ");
                    }
                    sb.append(token);
                    break;
                case "+":
                case "-":
                    if (lc == ' ' || lc == '(' || lc == '[') {
                        sb.append(token);
                        break;
                    }
                    // fall-through
                default:
                    if (lc != ' ') {
                        sb.append(" ");
                    }
                    sb.append(token);
                    sb.append(" ");
                    break;
            }
        }
        else {
            if (lc == ']' && !token.getText().startsWith(".")) {
                sb.append(" ");
            }
            else if (lc != ' ' && lc != '(' && lc != '[' && lc != ']' && lc != '#' && lc != '.' && lc != '\\' && lc != '+' && lc != '-' && lc != '~' && lc != '!') {
                sb.append(" ");
            }
            sb.append(isBuiltInConstant(token) ? convertCase(token, builtInConstantsCase) : token);
        }
    }

    void appendComment(FormatterStringBuilder sb, Token token) {
        if (token.type == Token.COMMENT) {
            if (lineCommentAlign == Align.None) {
                sb.alignToColumn(token.column);
            }
            else if (lineCommentAlign == Align.TabStop) {
                sb.alignToTabStop();
            }
            else {
                sb.alignToColumn(lineCommentColumn);
            }
            if (sb.column > 0 && sb.lastChar() != ' ') {
                sb.append(" ");
            }
            sb.append(token);
        }
        else if (token.type == Token.BLOCK_COMMENT) {
            if (blockCommentIndentAlign) {
                sb.alignToColumn(sb.previousLineIndent);
            }
            else {
                sb.alignToColumn(token.column);
            }
            if (sb.column > 0 && sb.lastChar() != ' ') {
                sb.append(" ");
            }
            sb.append(alignBlockComment(token, sb.column));
        }
    }

    String alignBlockComment(Token token, int column) {
        StringBuilder sb = new StringBuilder();

        int displ = column - token.column;
        String[] lines = token.getText().split("\r\n|\n|\r");

        int i = 0;
        sb.append(lines[i]);
        if (lines.length > 1) {
            i++;
            sb.append(System.lineSeparator());
            while (i < lines.length - 1) {
                sb.append(adjustIndent(lines[i], displ));
                sb.append(System.lineSeparator());
                i++;
            }
            sb.append(adjustIndent(lines[i], displ));
        }

        return sb.toString();
    }

    String adjustIndent(String text, int spaces) {
        if (spaces >= 0) {
            return " ".repeat(spaces) + text;
        }
        else {
            int index = 0;
            while (index < text.length() && text.charAt(index) == ' ') {
                index++;
                if (index >= -spaces) {
                    break;
                }
            }
            return text.substring(index);
        }
    }

    protected Token nextToken() {
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
        else if ("^".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (token.isAdjacent(nextToken) && nextToken.type == Token.KEYWORD) {
                token = token.merge(stream.nextToken());
                token.type = 0;
            }
        }
        return token;
    }

    protected Token nextPAsmToken() {
        return stream.nextToken();
    }

}
