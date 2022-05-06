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

    int conditionColumn;
    int instructionColumn;
    int argumentsColumn;
    int effectsColumn;
    int commentsColumn;

    int inlineConditionColumn;
    int inlineInstructionColumn;
    int inlineArgumentsColumn;
    int inlineEffectsColumn;
    int inlineCommentsColumn;

    boolean isolateLargeLabels;
    boolean keepBlankLines;
    boolean adjustPAsmColumns;

    class FormatterStringBuilder {
        int line = 0;
        int column = 0;
        StringBuilder sb = new StringBuilder();

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
            int next = ((column + 3) / 4) * 4;
            while (column < next) {
                sb.append(" ");
                column++;
            }
        }

        public void alignToColumn(int c) {
            while (column < c) {
                sb.append(" ");
                column++;
            }
        }

        @Override
        public String toString() {
            return sb.toString();
        }

    }

    FormatterStringBuilder sb = new FormatterStringBuilder();

    private static final Set<String> sections = new HashSet<String>(Arrays.asList(new String[] {
        "CON", "VAR", "OBJ", "PUB", "PRI", "DAT"
    }));
    private static final Set<String> blockStart = new HashSet<String>(Arrays.asList(new String[] {
        "REPEAT", "IF", "IFNOT", "ELSE", "ELSEIF", "ELSEIFNOT", "CASE", "CASE_FAST"
    }));
    private static final Set<String> blockEnd = new HashSet<String>(Arrays.asList(new String[] {
        "WHILE", "UNTIL"
    }));

    public Formatter() {
        conditionColumn = 8;
        instructionColumn = 16;
        argumentsColumn = 24;
        effectsColumn = 36;
        commentsColumn = 52;

        inlineConditionColumn = 8;
        inlineInstructionColumn = 16;
        inlineArgumentsColumn = 24;
        inlineEffectsColumn = 36;
        inlineCommentsColumn = 52;
    }

    public void setIsolateLargeLabels(boolean isolateLargeLabels) {
        this.isolateLargeLabels = isolateLargeLabels;
    }

    public void setKeepBlankLines(boolean keepBlankLines) {
        this.keepBlankLines = keepBlankLines;
    }

    public void setPAsmColumns(int condition, int instruction, int arguments, int effects, int comments) {
        conditionColumn = condition;
        instructionColumn = instruction;
        argumentsColumn = arguments;
        effectsColumn = effects;
        commentsColumn = comments;
    }

    public void setInlinePAsmColumns(int condition, int instruction, int arguments, int effects, int comments) {
        inlineConditionColumn = condition;
        inlineInstructionColumn = instruction;
        inlineArgumentsColumn = arguments;
        inlineEffectsColumn = effects;
        inlineCommentsColumn = comments;
    }

    public void setAdjustPAsmColumns(boolean adjustPAsmColumns) {
        this.adjustPAsmColumns = adjustPAsmColumns;
    }

    public abstract String format(String text);

    protected String format(TokenStream stream) {
        boolean blockSeparator = false;

        if (adjustPAsmColumns) {
            computeDatColumns(stream);
        }

        stream.reset();
        stream.skipComments(false);

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
            }
            else if (token.type == Token.COMMENT) {
                sb.alignToColumn(token.column);
                sb.append(stream.nextToken());
                sb.append(System.lineSeparator());
                if (keepBlankLines && stream.peekNext().type == Token.NL) {
                    stream.nextToken();
                }
            }
            else if (token.type == Token.BLOCK_COMMENT) {
                sb.alignToColumn(token.column);
                sb.append(stream.nextToken());
                if (stream.peekNext().type == Token.NL) {
                    sb.append(System.lineSeparator());
                    if (keepBlankLines) {
                        stream.nextToken();
                    }
                }
            }
            else {
                if (blockSeparator) {
                    if (!keepBlankLines) {
                        sb.append(System.lineSeparator());
                    }
                    blockSeparator = false;
                }
                if ("PUB".equalsIgnoreCase(token.getText()) || "PRI".equalsIgnoreCase(token.getText())) {
                    sb.append(stream.nextToken().getText().toUpperCase());
                    sb.append(" ");

                    formatTokens(stream);
                    sb.append(System.lineSeparator());
                    if (keepBlankLines && stream.peekNext().type == Token.NL) {
                        stream.nextToken();
                    }

                    while ((token = stream.peekNext()).type != Token.EOF) {
                        if (token.type == Token.NL) {
                            if (keepBlankLines) {
                                sb.append(System.lineSeparator());
                            }
                            stream.nextToken();
                        }
                        else {
                            if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                                break;
                            }
                            formatStatement(1, stream, token.column);
                        }
                    }
                }
                else if ("DAT".equalsIgnoreCase(token.getText())) {
                    sb.append(stream.nextToken().getText().toUpperCase());

                    token = stream.peekNext();
                    while (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                        sb.append(" ");
                        if (token.type == Token.COMMENT) {
                            sb.append(stream.nextToken());
                            break;
                        }
                        sb.append(stream.nextToken());
                        token = stream.peekNext();
                    }
                    formatTokens(stream);
                    sb.append(System.lineSeparator());
                    if (keepBlankLines && stream.peekNext().type == Token.NL) {
                        stream.nextToken();
                    }

                    while (true) {
                        token = stream.peekNext();
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
                        else if (token.type == Token.COMMENT) {
                            sb.alignToColumn(token.column);
                            sb.append(stream.nextToken());
                        }
                        else {
                            Token label = null;
                            Token condition = null;
                            Token instruction = null;

                            if (pasmLabel(token)) {
                                label = stream.nextToken();
                                if (":".equals(token.getText()) || ".".equals(token.getText())) {
                                    token = stream.peekNext();
                                    if (token.type != Token.NL && token.type != Token.EOF) {
                                        label = label.merge(stream.nextToken());
                                    }
                                }
                                token = stream.peekNext();
                            }

                            if (pasmCondition(token)) {
                                condition = stream.nextToken();
                                token = stream.peekNext();
                            }

                            if (token.type == 0) {
                                instruction = stream.nextToken();
                                token = stream.peekNext();
                            }

                            if (instruction == null) {
                                if (label != null) {
                                    sb.append(label);
                                    while ((token = stream.peekNext()).type != Token.EOF) {
                                        if (token.type == Token.NL) {
                                            break;
                                        }
                                        if (token.type == Token.COMMENT) {
                                            sb.alignToColumn(commentsColumn);
                                            sb.append(stream.nextToken());
                                            break;
                                        }
                                        else {
                                            sb.append(stream.nextToken());
                                        }
                                    }
                                }
                            }
                            else {
                                if (label != null) {
                                    sb.append(label);
                                    if (isolateLargeLabels) {
                                        if (condition == null && label.getText().length() >= instructionColumn) {
                                            sb.append(System.lineSeparator());
                                        }
                                        else if (condition != null && label.getText().length() >= conditionColumn) {
                                            sb.append(System.lineSeparator());
                                        }
                                    }
                                }

                                if (condition != null) {
                                    sb.alignToColumn(conditionColumn);
                                    sb.append(condition.getText().toLowerCase());
                                }

                                sb.alignToColumn(instructionColumn);
                                sb.append(instruction.getText().toLowerCase());

                                if (token.type != Token.NL && token.type != Token.EOF) {
                                    if (!"debug".equalsIgnoreCase(instruction.getText())) {
                                        sb.alignToColumn(argumentsColumn);
                                    }
                                    formatPAsmTokens(stream, effectsColumn);
                                }
                            }
                            sb.append(System.lineSeparator());
                            if (keepBlankLines && stream.peekNext().type == Token.NL) {
                                stream.nextToken();
                            }
                        }
                    }
                }
                else {
                    if (sections.contains(token.getText().toUpperCase())) {
                        sb.append(stream.nextToken().getText().toUpperCase());
                        token = stream.peekNext();
                        while (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT) {
                            sb.append(" ");
                            if (token.type == Token.COMMENT) {
                                sb.append(stream.nextToken());
                                break;
                            }
                            sb.append(stream.nextToken());
                            token = stream.peekNext();
                        }
                        sb.append(System.lineSeparator());
                        if (keepBlankLines && stream.peekNext().type == Token.NL) {
                            stream.nextToken();
                        }
                    }
                    while (true) {
                        token = stream.peekNext();
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
                        else if (token.type == Token.COMMENT) {
                            sb.alignToColumn(token.column);
                            sb.append(stream.nextToken());
                            sb.append(System.lineSeparator());
                            if (keepBlankLines && stream.peekNext().type == Token.NL) {
                                stream.nextToken();
                                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                                    break;
                                }
                            }
                        }
                        else if (token.type == Token.BLOCK_COMMENT) {
                            sb.append(stream.nextToken());
                            if (stream.peekNext().type == Token.NL) {
                                sb.append(System.lineSeparator());
                                if (keepBlankLines) {
                                    stream.nextToken();
                                }
                            }
                        }
                        else {
                            sb.append("    ");
                            formatTokens(stream);
                            sb.append(System.lineSeparator());
                            if (keepBlankLines && stream.peekNext().type == Token.NL) {
                                stream.nextToken();
                                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                                    break;
                                }
                            }
                        }
                    }
                }
                blockSeparator = true;
            }
        }

        return sb.toString();
    }

    protected abstract boolean pasmLabel(Token token);

    protected abstract boolean pasmCondition(Token token);

    protected abstract boolean pasmInstruction(Token token);

    protected abstract boolean pasmModifier(Token token);

    void computeDatColumns(TokenStream stream) {
        int labelWidth = conditionColumn;
        int inlineLabelWidth = inlineConditionColumn;
        int conditionWidth = instructionColumn - conditionColumn;
        int inlineConditionWidth = inlineInstructionColumn - inlineConditionColumn;

        stream.reset();
        stream.skipComments(true);

        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF) {
                break;
            }
            if (token.type != Token.NL) {
                if ("DAT".equalsIgnoreCase(token.getText())) {
                    while ((token = stream.nextToken()).type != Token.EOF) {
                        if (token.type == Token.NL) {
                            break;
                        }
                    }

                    while ((token = stream.nextToken()).type != Token.EOF) {
                        if (token.type == Token.NL) {
                            token = stream.peekNext();
                            if (sections.contains(token.getText().toUpperCase())) {
                                break;
                            }
                        }
                        else {
                            if (pasmLabel(token)) {
                                Token label = token;
                                if (":".equals(token.getText()) || ".".equals(token.getText())) {
                                    token = stream.nextToken();
                                    if (token.type == 0 && label.isAdjacent(token)) {
                                        label = label.merge(stream.nextToken());
                                        token = stream.nextToken();
                                    }
                                }
                                if (token.type == Token.NL || token.type == Token.EOF) {
                                    break;
                                }
                                if (!isolateLargeLabels) {
                                    labelWidth = Math.max(labelWidth, label.getText().length() + 1);
                                }
                            }

                            if (pasmCondition(token)) {
                                conditionWidth = Math.max(conditionWidth, token.getText().length() + 1);
                                token = stream.nextToken();
                                if (token.type == Token.NL || token.type == Token.EOF) {
                                    break;
                                }
                            }

                            while (token.type != Token.EOF) {
                                if (token.type == Token.NL) {
                                    break;
                                }
                                token = stream.nextToken();
                            }
                        }
                    }
                }
                else if ("PUB".equalsIgnoreCase(token.getText()) || "PRI".equalsIgnoreCase(token.getText())) {
                    while (true) {
                        token = stream.nextToken();
                        if (token.type == Token.EOF) {
                            break;
                        }
                        if (token.type == Token.NL) {
                            token = stream.peekNext();
                            if (sections.contains(token.getText().toUpperCase())) {
                                break;
                            }
                            if ("ORG".equalsIgnoreCase(token.getText().toUpperCase())) {
                                while (true) {
                                    token = stream.nextToken();
                                    if (token.type == Token.EOF) {
                                        break;
                                    }
                                    if (token.type == Token.NL) {
                                        token = stream.peekNext();
                                        if ("END".equalsIgnoreCase(token.getText().toUpperCase())) {
                                            break;
                                        }
                                    }
                                    else {
                                        if (pasmLabel(token)) {
                                            if (!isolateLargeLabels) {
                                                inlineLabelWidth = Math.max(inlineLabelWidth, token.getText().length() + 1);
                                            }
                                            token = stream.nextToken();
                                            if (token.type == Token.NL || token.type == Token.EOF) {
                                                break;
                                            }
                                        }

                                        if (pasmCondition(token)) {
                                            inlineConditionWidth = Math.max(inlineConditionWidth, token.getText().length() + 1);
                                            token = stream.nextToken();
                                            if (token.type == Token.NL || token.type == Token.EOF) {
                                                break;
                                            }
                                        }

                                        while ((token = stream.peekNext()).type != Token.NL) {
                                            if (token.type == Token.EOF) {
                                                break;
                                            }
                                            stream.nextToken();
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
                else {
                    while ((token = stream.peekNext()).type != Token.NL) {
                        if (token.type == Token.EOF) {
                            break;
                        }
                        stream.nextToken();
                    }
                }
            }
        }

        stream.reset();

        int diff = instructionColumn;
        conditionColumn = ((labelWidth + 3) / 4) * 4;
        instructionColumn = ((conditionColumn + conditionWidth + 3) / 4) * 4;
        argumentsColumn = instructionColumn + 8;
        diff = instructionColumn > diff ? instructionColumn - diff : 0;
        effectsColumn = ((effectsColumn + diff + 3) / 4) * 4;
        commentsColumn = ((commentsColumn + diff + 3) / 4) * 4;

        diff = inlineInstructionColumn;
        inlineConditionColumn = ((inlineLabelWidth + 3) / 4) * 4;
        inlineInstructionColumn = ((inlineConditionColumn + inlineConditionWidth + 3) / 4) * 4;
        inlineArgumentsColumn = inlineInstructionColumn + 8;
        diff = inlineInstructionColumn > diff ? inlineInstructionColumn - diff : 0;
        inlineEffectsColumn = ((inlineEffectsColumn + diff + 3) / 4) * 4;
        inlineCommentsColumn = ((inlineCommentsColumn + diff + 3) / 4) * 4;
    }

    void formatStatement(int indent, TokenStream stream, int column) {
        Token token;
        boolean indentNext = false;

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type == Token.NL) {
                if (keepBlankLines) {
                    sb.append(System.lineSeparator());
                }
                stream.nextToken();
            }
            else if (token.type == Token.COMMENT) {
                if (token.column != 0) {
                    sb.alignToColumn((indentNext ? (indent + 1) : indent) * 4);
                }
                sb.append(stream.nextToken());
                sb.append(System.lineSeparator());
                if (keepBlankLines && stream.peekNext().type == Token.NL) {
                    stream.nextToken();
                }
            }
            else if (token.type == Token.BLOCK_COMMENT) {
                if (token.column != 0) {
                    sb.alignToColumn(indent * 4);
                }
                sb.append(stream.nextToken());
                if (stream.peekNext().type == Token.NL) {
                    sb.append(System.lineSeparator());
                    if (keepBlankLines) {
                        stream.nextToken();
                    }
                }
            }
            else {
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    break;
                }
                if ("ORG".equalsIgnoreCase(token.getText().toUpperCase())) {

                    for (int i = 0; i < indent; i++) {
                        sb.append("    ");
                    }
                    formatTokens(stream);
                    sb.append(System.lineSeparator());
                    if (keepBlankLines && stream.peekNext().type == Token.NL) {
                        stream.nextToken();
                    }

                    formatInlinePAsm(stream);
                }
                else {
                    if (blockEnd.contains(token.getText().toUpperCase())) {
                        indentNext = false;
                    }
                    if (token.column > column && indentNext) {
                        formatStatement(indent + 1, stream, token.column);
                        indentNext = false;
                    }
                    else if (token.column < column) {
                        break;
                    }
                    else {
                        indentNext = blockStart.contains(token.getText().toUpperCase());

                        for (int i = 0; i < indent; i++) {
                            sb.append("    ");
                        }
                        formatTokens(stream);
                        sb.append(System.lineSeparator());
                        if (keepBlankLines && stream.peekNext().type == Token.NL) {
                            stream.nextToken();
                        }
                    }
                }
            }
        }
    }

    void formatInlinePAsm(TokenStream stream) {
        Token token;

        while (true) {
            token = stream.peekNext();
            if (token.type == Token.EOF) {
                break;
            }
            if ("END".equalsIgnoreCase(stream.peekNext().getText())) {
                break;
            }
            if (token.type == Token.NL) {
                if (keepBlankLines) {
                    sb.append(System.lineSeparator());
                }
                stream.nextToken();
            }
            else if (token.type == Token.COMMENT) {
                sb.alignToColumn(token.column);
                sb.append(stream.nextToken());
                sb.append(System.lineSeparator());
                if (keepBlankLines && stream.peekNext().type == Token.NL) {
                    stream.nextToken();
                    if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                        break;
                    }
                }
            }
            else if (token.type == Token.BLOCK_COMMENT) {
                sb.append(stream.nextToken());
                if (stream.peekNext().type == Token.NL) {
                    sb.append(System.lineSeparator());
                    if (keepBlankLines) {
                        stream.nextToken();
                    }
                }
            }
            else {
                Token label = null;
                Token condition = null;
                Token instruction = null;

                token = stream.nextToken();
                if (pasmLabel(token)) {
                    label = token;
                    if (":".equals(token.getText()) || ".".equals(token.getText())) {
                        label.merge(stream.nextToken());
                    }
                    token = stream.nextToken();
                }

                if (pasmCondition(token)) {
                    condition = token;
                    token = stream.nextToken();
                }

                if (token.type != Token.NL && token.type != Token.EOF) {
                    instruction = token;
                }

                if (instruction == null) {
                    if (label != null) {
                        sb.append(label);
                    }
                }
                else {
                    if (label != null) {
                        sb.append(label);
                        if (isolateLargeLabels) {
                            if (condition == null && label.getText().length() >= instructionColumn) {
                                sb.append(System.lineSeparator());
                            }
                            else if (condition != null && label.getText().length() >= conditionColumn) {
                                sb.append(System.lineSeparator());
                            }
                        }
                    }

                    if (condition != null) {
                        sb.alignToColumn(inlineConditionColumn);
                        sb.append(condition);
                    }

                    sb.alignToColumn(inlineInstructionColumn);
                    sb.append(instruction);

                    token = stream.peekNext();
                    if (token.type != Token.NL && token.type != Token.EOF) {
                        if (!"debug".equalsIgnoreCase(instruction.getText())) {
                            sb.alignToColumn(argumentsColumn);
                        }
                        formatPAsmTokens(stream, inlineEffectsColumn);
                    }
                }
                sb.append(System.lineSeparator());
                if (keepBlankLines && stream.peekNext().type == Token.NL) {
                    stream.nextToken();
                }
            }
        }
    }

    void formatPAsmTokens(TokenStream stream, int effectsColumn) {
        Token token;
        int stop = 0;
        boolean addSpace = false;

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            token = stream.nextToken();
            if (pasmModifier(token)) {
                if (sb.column >= effectsColumn) {
                    sb.append(" ");
                }
                else {
                    sb.alignToColumn(effectsColumn);
                }
                sb.append(token);
                while ((token = stream.peekNext()).type != Token.EOF) {
                    if (token.type == Token.NL) {
                        break;
                    }
                    if (token.type == Token.COMMENT) {
                        sb.alignToColumn(commentsColumn);
                        sb.append(stream.nextToken());
                    }
                    else {
                        sb.append(stream.nextToken().getText().toLowerCase());
                    }
                }
                break;
            }
            else if (token.type == Token.BLOCK_COMMENT) {
                if (stream.peekNext().type == Token.NL) {
                    sb.alignToTabStop();
                    sb.append(token);
                }
                else {
                    if (token.start > stop + 1) {
                        sb.append(" ");
                    }
                    sb.append(stream.nextToken());
                    stop = token.stop;
                    if (stream.peekNext().start > stop + 1) {
                        sb.append(" ");
                    }
                }
            }
            else if (token.type == Token.COMMENT) {
                sb.alignToColumn(commentsColumn);
                sb.append(token);
            }
            else if (token.type == Token.OPERATOR) {
                addSpace = ")".equals(token.getText()) || "]".equals(token.getText());
                switch (token.getText()) {
                    case "(":
                    case "[":
                    case ")":
                    case "]":
                    case "#":
                    case "##":
                    case ".":
                    case "@":
                    case "\\":
                    case ":":
                        sb.append(token);
                        break;
                    case ",":
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
                if (addSpace) {
                    sb.append(" ");
                }
                if (token.type == Token.NUMBER) {
                    sb.append(token.getText().toUpperCase());
                    addSpace = true;
                }
                else {
                    sb.append(token);
                    if ("and".equalsIgnoreCase(token.getText()) || "or".equalsIgnoreCase(token.getText()) || "not".equalsIgnoreCase(token.getText())) {
                        sb.append(" ");
                        addSpace = false;
                    }
                    else {
                        addSpace = true;
                    }
                }
            }
            stop = token.stop;
        }
    }

    void formatTokens(TokenStream stream) {
        Token token;
        int stop = 0;
        boolean addSpace = false;

        while ((token = stream.peekNext()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            token = stream.nextToken();
            if (token.type == Token.BLOCK_COMMENT) {
                if (stream.peekNext().type == Token.NL) {
                    sb.alignToTabStop();
                    sb.append(token);
                }
                else {
                    if (token.start > stop + 1) {
                        sb.append(" ");
                    }
                    sb.append(token);
                    stop = token.stop;
                    if (stream.peekNext().start > stop + 1) {
                        sb.append(" ");
                    }
                }
            }
            else if (token.type == Token.COMMENT) {
                sb.alignToTabStop();
                sb.append(token);
            }
            else if (token.type == Token.OPERATOR) {
                if (addSpace) {
                    if ("@".equals(token.getText())) {
                        sb.append(" ");
                    }
                }
                addSpace = ")".equals(token.getText()) || "]".equals(token.getText());
                switch (token.getText()) {
                    case "(":
                    case "[":
                    case ")":
                    case "]":
                    case "#":
                    case "##":
                    case ".":
                    case "@":
                    case "\\":
                    case "--":
                    case "++":
                    case "~":
                    case "~~":
                        sb.append(token);
                        break;
                    case ",":
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
                if (addSpace) {
                    sb.append(" ");
                }
                if (token.type == Token.NUMBER) {
                    sb.append(token.getText().toUpperCase());
                    addSpace = true;
                }
                else {
                    sb.append(token);
                    if ("and".equalsIgnoreCase(token.getText()) || "or".equalsIgnoreCase(token.getText()) || "not".equalsIgnoreCase(token.getText())) {
                        sb.append(" ");
                        addSpace = false;
                    }
                    else {
                        addSpace = true;
                    }
                }
            }
            stop = token.stop;
        }
    }

}
