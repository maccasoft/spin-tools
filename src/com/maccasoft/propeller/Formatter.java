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
import com.maccasoft.propeller.spin2.Spin2Model;
import com.maccasoft.propeller.spin2.Spin2TokenStream;

public class Formatter {

    int conditionColumn;
    int instructionColumn;
    int argumentsColumn;
    int effectsColumn;

    int inlineConditionColumn;
    int inlineInstructionColumn;
    int inlineArgumentsColumn;
    int inlineEffectsColumn;

    boolean isolateLargeLabels;
    boolean alignCommentsToTab;
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

        inlineConditionColumn = 8;
        inlineInstructionColumn = 16;
        inlineArgumentsColumn = 24;
        inlineEffectsColumn = 36;
    }

    public void setIsolateLargeLabels(boolean isolateLargeLabels) {
        this.isolateLargeLabels = isolateLargeLabels;
    }

    public void setAlignCommentsToTab(boolean alignCommentsToTab) {
        this.alignCommentsToTab = alignCommentsToTab;
    }

    public void setKeepBlankLines(boolean keepBlankLines) {
        this.keepBlankLines = keepBlankLines;
    }

    public void setPAsmColumns(int condition, int instruction, int arguments, int effects) {
        conditionColumn = condition;
        instructionColumn = instruction;
        argumentsColumn = arguments;
        effectsColumn = effects;
    }

    public void setAdjustPAsmColumns(boolean adjustPAsmColumns) {
        this.adjustPAsmColumns = adjustPAsmColumns;
    }

    public String format(Spin2TokenStream stream) {
        boolean blockSeparator = false;

        stream.setComments(true);

        if (adjustPAsmColumns) {
            computeDatColumns(stream);
        }

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
                appendLineComment(stream.nextToken());
                sb.append(System.lineSeparator());
                if (keepBlankLines && stream.peekNext().type == Token.NL) {
                    stream.nextToken();
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

                            if (Spin2Model.isCondition(token.getText())) {
                                condition = stream.nextToken();
                                token = stream.peekNext();
                            }

                            if (token.type != Token.NL && token.type != Token.EOF) {
                                instruction = stream.nextToken();
                                token = stream.peekNext();
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
                                        if (condition == null && label.getText().length() > instructionColumn) {
                                            sb.append(System.lineSeparator());
                                        }
                                        else if (condition != null && label.getText().length() > conditionColumn) {
                                            sb.append(System.lineSeparator());
                                        }
                                    }
                                }

                                if (condition != null) {
                                    sb.alignToColumn(conditionColumn);
                                    sb.append(condition);
                                }

                                sb.alignToColumn(instructionColumn);
                                sb.append(instruction);

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
                            if (token.type == Token.COMMENT) {
                                appendLineComment(stream.nextToken());
                                break;
                            }
                            sb.append(" ");
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
                            appendLineComment(stream.nextToken());
                            sb.append(System.lineSeparator());
                            if (keepBlankLines && stream.peekNext().type == Token.NL) {
                                stream.nextToken();
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
                            if (keepBlankLines && stream.peekNext().type == Token.NL) {
                                stream.nextToken();
                            }
                            sb.append(System.lineSeparator());
                        }
                    }
                }
                blockSeparator = true;
            }
        }

        return sb.toString();
    }

    boolean pasmLabel(Token token) {
        if (Spin2Model.isCondition(token.getText())) {
            return false;
        }
        if (Spin2Model.isInstruction(token.getText())) {
            return false;
        }
        if (Spin2Model.isPAsmType(token.getText())) {
            return false;
        }
        if ("debug".equalsIgnoreCase(token.getText())) {
            return false;
        }
        return true;
    }

    boolean pasmInstruction(Token token) {
        if (Spin2Model.isInstruction(token.getText())) {
            return true;
        }
        if (Spin2Model.isPAsmType(token.getText())) {
            return true;
        }
        if ("debug".equalsIgnoreCase(token.getText())) {
            return true;
        }
        return false;
    }

    void appendLineComment(Token token) {
        if (token.column != 0) {
            sb.append(" ");
        }
        if (alignCommentsToTab) {
            sb.alignToTabStop();
        }
        sb.append(token);
    }

    void computeDatColumns(Spin2TokenStream stream) {
        int labelWidth = conditionColumn;
        int inlineLabelWidth = inlineConditionColumn;
        int conditionWidth = instructionColumn - conditionColumn;
        int inlineConditionWidth = inlineInstructionColumn - inlineConditionColumn;

        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF) {
                break;
            }
            if (token.type != Token.NL) {
                if ("DAT".equalsIgnoreCase(token.getText())) {

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
                        }
                        else {
                            if (pasmLabel(token)) {
                                Token label = token;
                                if (":".equals(token.getText()) || ".".equals(token.getText())) {
                                    token = stream.peekNext();
                                    if (token.type != Token.NL && token.type != Token.EOF) {
                                        label = label.merge(stream.nextToken());
                                    }
                                }
                                if (!isolateLargeLabels) {
                                    labelWidth = Math.max(labelWidth, label.getText().length() + 1);
                                }
                                if (token.type == Token.NL || token.type == Token.EOF) {
                                    break;
                                }
                            }

                            if (Spin2Model.isCondition(token.getText())) {
                                conditionWidth = Math.max(conditionWidth, token.getText().length() + 1);
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
                                        if (!Spin2Model.isCondition(token.getText()) && !Spin2Model.isInstruction(token.getText())) {
                                            if (!isolateLargeLabels) {
                                                inlineLabelWidth = Math.max(inlineLabelWidth, token.getText().length() + 1);
                                            }
                                            token = stream.nextToken();
                                            if (token.type == Token.NL || token.type == Token.EOF) {
                                                break;
                                            }
                                        }

                                        if (Spin2Model.isCondition(token.getText())) {
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

        //diff = inlineInstructionColumn;
        inlineConditionColumn = ((inlineLabelWidth + 3) / 4) * 4;
        inlineInstructionColumn = ((inlineConditionColumn + inlineConditionWidth + 3) / 4) * 4;
        inlineArgumentsColumn = inlineInstructionColumn + 8;
        diff = inlineInstructionColumn > diff ? inlineInstructionColumn - diff : 0;
        inlineEffectsColumn = ((inlineEffectsColumn + diff + 3) / 4) * 4;
    }

    void formatStatement(int indent, Spin2TokenStream stream, int column) {
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
                appendLineComment(stream.nextToken());
                sb.append(System.lineSeparator());
                if (keepBlankLines && stream.peekNext().type == Token.NL) {
                    stream.nextToken();
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
                if (sections.contains(stream.peekNext().getText().toUpperCase())) {
                    break;
                }
                if ("ORG".equalsIgnoreCase(token.getText().toUpperCase())) {

                    for (int i = 0; i < indent; i++) {
                        sb.append("    ");
                    }
                    formatTokens(stream);
                    sb.append(System.lineSeparator());
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

    void formatInlinePAsm(Spin2TokenStream stream) {
        Token token;

        while (true) {
            token = stream.peekNext();
            if (token.type == Token.EOF) {
                break;
            }
            if (token.type == Token.NL) {
                stream.nextToken();
                if ("END".equalsIgnoreCase(stream.peekNext().getText())) {
                    break;
                }
            }
            else {
                Token label = null;
                Token condition = null;
                Token instruction = null;

                token = stream.nextToken();
                if (!Spin2Model.isCondition(token.getText()) && !Spin2Model.isInstruction(token.getText())) {
                    label = token;
                    if (":".equals(token.getText()) || ".".equals(token.getText())) {
                        label.merge(stream.nextToken());
                    }
                    token = stream.nextToken();
                }

                if (Spin2Model.isCondition(token.getText())) {
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
                            if (condition == null && label.getText().length() > instructionColumn) {
                                sb.append(System.lineSeparator());
                            }
                            else if (condition != null && label.getText().length() > conditionColumn) {
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
            }
        }
    }

    void formatPAsmTokens(Spin2TokenStream stream, int effectsColumn) {
        Token token;
        int stop = 0;
        boolean addSpace = false;

        while ((token = stream.peekNext()).type != Token.NL) {
            if (token.type == Token.EOF) {
                break;
            }
            if (Spin2Model.isModifier(token.getText())) {
                if (sb.column >= effectsColumn) {
                    sb.append(" ");
                }
                else {
                    sb.alignToColumn(effectsColumn);
                }
                while ((token = stream.peekNext()).type != Token.NL) {
                    sb.append(stream.nextToken());
                    if (",".equals(token.getText())) {
                        sb.append(" ");
                    }
                    if (token.type == Token.EOF) {
                        break;
                    }
                }
                break;
            }
            else if (token.type == Token.BLOCK_COMMENT) {
                if (token.start > stop + 1) {
                    sb.append(" ");
                }
                sb.append(stream.nextToken());
                stop = token.stop;
                if (stream.peekNext().start > stop + 1) {
                    sb.append(" ");
                }
            }
            else {
                if (token.type == Token.COMMENT) {
                    appendLineComment(token);
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
                    }
                    else {
                        sb.append(token);
                    }
                    addSpace = true;
                }
                stop = token.stop;
                stream.nextToken();
            }
        }
    }

    void formatTokens(Spin2TokenStream stream) {
        Token token;
        int stop = 0;
        boolean addSpace = false;

        while ((token = stream.peekNext()).type != Token.NL) {
            if (token.type == Token.EOF) {
                break;
            }
            if (token.type == Token.BLOCK_COMMENT) {
                if (token.start > stop + 1) {
                    sb.append(" ");
                }
                sb.append(stream.nextToken());
                stop = token.stop;
                if (stream.peekNext().start > stop + 1) {
                    sb.append(" ");
                }
            }
            else {
                if (token.type == Token.COMMENT) {
                    appendLineComment(token);
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
                    }
                    else {
                        sb.append(token);
                    }
                    addSpace = true;
                }
                stop = token.stop;
                stream.nextToken();
            }
        }
    }

}
