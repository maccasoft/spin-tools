/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
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

    private static final Set<String> sections = new HashSet<String>(Arrays.asList(new String[] {
        "CON", "VAR", "OBJ", "PUB", "PRI", "DAT"
    }));

    public static Set<String> types = new HashSet<String>(Arrays.asList(new String[] {
        "LONG", "WORD", "BYTE",
    }));

    class FormatterStringBuilder {
        int column = 0;
        StringBuilder sb = new StringBuilder();

        int[] tabstops;
        int tabspaces = 4;

        public FormatterStringBuilder(int tabspaces, int[] tabstops) {
            this.tabspaces = tabspaces;
            this.tabstops = tabstops;
        }

        public void append(String s) {
            sb.append(s);
            if (s.equals(System.lineSeparator())) {
                column = 0;
            }
            else {
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

    protected TokenStream stream;

    Map<String, int[]> sectionTabStops;

    public Formatter() {
        this.sectionTabStops = new HashMap<>();
    }

    public void setSectionTabStop(String section, int[] tabstops) {
        sectionTabStops.put(section, tabstops);
    }

    public abstract String format(String text);

    protected String format(TokenStream stream) {
        Token token;

        this.stream = stream;

        FormatterStringBuilder sb = new FormatterStringBuilder(4, sectionTabStops.get("con"));

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type == Token.EOF) {
                break;
            }
            if (token.type == Token.NL) {
                sb.append(System.lineSeparator());
                stream.nextToken();
            }
            else if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                sb.alignToColumn(token.column);
                if (sb.column > 0 && sb.lastChar() != ' ') {
                    sb.append(" ");
                }
                sb.append(stream.nextToken());
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
        FormatterStringBuilder sb = new FormatterStringBuilder(4, sectionTabStops.get("con"));

        appendSectionHeader(sb, stream.nextToken());
        if (stream.peekNext().type == Token.NL) {
            stream.nextToken();
        }
        else {
            formatConstant(sb);
        }
        if (sections.contains(stream.peekNext().getText().toUpperCase())) {
            return sb;
        }

        while (stream.peekNext().type != Token.EOF) {
            formatConstant(sb);
            if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                break;
            }
        }

        return sb;
    }

    void formatConstant(FormatterStringBuilder sb) {
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
                    if (token.type == Token.KEYWORD && sb.lastChar() != ' ') {
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
                    sb.append(token);
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
        FormatterStringBuilder sb = new FormatterStringBuilder(4, sectionTabStops.get("var"));

        appendSectionHeader(sb, stream.nextToken());
        if (stream.peekNext().type == Token.NL) {
            stream.nextToken();
        }
        else {
            formatVariable(sb);
        }
        if (sections.contains(stream.peekNext().getText().toUpperCase())) {
            return sb;
        }

        while (stream.peekNext().type != Token.EOF) {
            formatVariable(sb);
            if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                break;
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
                    appendExpressionToken(sb, token);
                    break;
            }
        }

        sb.append(System.lineSeparator());
    }

    FormatterStringBuilder formatObjects() {
        FormatterStringBuilder sb = new FormatterStringBuilder(4, sectionTabStops.get("obj"));

        appendSectionHeader(sb, stream.nextToken());
        if (stream.peekNext().type == Token.NL) {
            stream.nextToken();
        }
        else {
            formatObject(sb);
        }
        if (sections.contains(stream.peekNext().getText().toUpperCase())) {
            return sb;
        }

        while (stream.peekNext().type != Token.EOF) {
            formatObject(sb);
            if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                break;
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

        sb.append(stream.nextToken().getText().toUpperCase());

        Token token;
        while ((token = nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    return sb;
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
                    appendExpressionToken(sb, token);
                    break;
            }
        }

        sb.append(System.lineSeparator());

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type == Token.NL) {
                sb.append(System.lineSeparator());
                stream.nextToken();
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    break;
                }
            }
            formatStatement(sb, 0, sb.getNextTabStop(1));
            if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                break;
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
                sb.alignToColumn(token.column);
                if (sb.column > 0 && sb.lastChar() != ' ') {
                    sb.append(" ");
                }
                sb.append(stream.nextToken());
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
        FormatterStringBuilder sb = new FormatterStringBuilder(4, sectionTabStops.get("dat"));

        appendSectionHeader(sb, stream.nextToken());
        if (stream.peekNext().type == Token.NL) {
            stream.nextToken();
        }
        else {
            formatDatLine(sb);
        }
        if (sections.contains(stream.peekNext().getText().toUpperCase())) {
            return sb;
        }

        while (stream.peekNext().type != Token.EOF) {
            formatDatLine(sb);
            if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                break;
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

    protected boolean pasmCondition(Token token) {
        return false;
    }

    protected boolean pasmInstruction(Token token) {
        return false;
    }

    protected boolean pasmModifier(Token token) {
        return false;
    }

    void appendSectionHeader(FormatterStringBuilder sb, Token token) {
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
            if (lc != ' ' && lc != '(' && lc != '[' && lc != ']' && lc != '#' && lc != '.' && lc != '\\' && lc != '+' && lc != '-' && lc != '~' && lc != '!') {
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
        else if ("^".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (token.isAdjacent(nextToken) && nextToken.type == Token.KEYWORD) {
                token = token.merge(stream.nextToken());
                token.type = 0;
            }
        }
        return token;
    }

    protected abstract Token nextPAsmToken();

}
