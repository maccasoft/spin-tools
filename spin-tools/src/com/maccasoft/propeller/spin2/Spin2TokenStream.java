/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenStream;
import com.maccasoft.propeller.spin2.bytecode.MathOp;

public class Spin2TokenStream extends TokenStream {

    int backtickState;
    int backtickNestedParens;
    Token eofToken;

    public Spin2TokenStream(String text) {
        super(text);
        backtickState = 0;
        backtickNestedParens = 0;
        eofToken = new Token(this, text.length() - 1, Token.EOF);
    }

    @Override
    public Token nextToken() {
        if (backtickState == 1) {
            return parseBacktickString();
        }
        if (backtickState == 2) {
            return parseBacktickKeyword();
        }
        if (backtickState == 3) {
            return parseBacktickNext();
        }
        return parseNext();
    }

    Token parseNext() {
        while (index < text.length()) {
            char ch = Character.toUpperCase(text.charAt(index));
            if (ch == ' ') {
                index++;
                column++;
            }
            else if (ch == '\t') {
                index++;
                column = ((column + 7) & 7) - 1;
            }
            else if (ch == '\r') {
                int startIndex = index++;
                int startColumn = column;
                int startLine = line;

                if (index < text.length() && text.charAt(index) == '\n') {
                    index++;
                }
                column = 0;
                line++;

                return new Token(this, startIndex, startLine, startColumn, Token.NL);
            }
            else if (ch == '\n') {
                int startIndex = index++;
                int startColumn = column;
                int startLine = line;

                column = 0;
                line++;

                return new Token(this, startIndex, startLine, startColumn, Token.NL);
            }
            else if (ch == '\'') {
                return parseComment();
            }
            else if (ch == '{') {
                return parseBlockComment();
            }
            else if (ch == '"') {
                return parseString();
            }
            else if (ch == '$') {
                return parseHexNumber();
            }
            else if (ch == '%') {
                return parseBinQuadNumber();
            }
            else if ((ch >= '0' && ch <= '9')) {
                return parseNumber();
            }
            else if ((ch >= 'A' && ch <= 'Z') || ch == '_') {
                return parseKeyword();
            }
            else if (ch == '`') {
                return parseBacktickString();
            }
            else {
                /*if (ch == '.' && index + 1 < text.length()) {
                    char ch1 = Character.toUpperCase(text.charAt(index + 1));
                    if ((ch1 >= 'A' && ch1 <= 'Z') || ch1 == '_') {
                        return parseKeyword();
                    }
                }*/
                return parseOperator();
            }
        }

        return eofToken;
    }

    Token parseComment() {
        int startIndex = index++;
        int startColumn = column++;

        while (index < text.length()) {
            char ch = text.charAt(index);
            if (ch == '\r' || ch == '\n') {
                break;
            }
            index++;
            column++;
        }

        return new Token(this, startIndex, line, startColumn, Token.COMMENT, text.substring(startIndex, index));
    }

    Token parseBlockComment() {
        int startIndex = index++;
        int startLine = line;
        int startColumn = column++;
        int nested = 0;

        while (index < text.length()) {
            char ch = text.charAt(index);
            index++;
            column++;
            if (ch == '{') {
                nested++;
            }
            else if (ch == '}') {
                if (nested == 0) {
                    break;
                }
                nested--;
            }
            if (ch == '\r' || ch == '\n') {
                line++;
                column = 0;
                if (ch == '\r' && index < text.length() && text.charAt(index) == '\n') {
                    index++;
                }
            }
        }

        return new Token(this, startIndex, startLine, startColumn, Token.BLOCK_COMMENT, text.substring(startIndex, index));
    }

    Token parseString() {
        int startIndex = index++;
        int startColumn = column++;

        while (index < text.length()) {
            char ch = text.charAt(index);
            if (ch == '\n' || ch == '\n') {
                break;
            }
            index++;
            column++;
            if (ch == '"') {
                break;
            }
        }

        return new Token(this, startIndex, line, startColumn, Token.STRING, text.substring(startIndex, index));
    }

    Token parseNumber() {
        int startIndex = index;
        int startColumn = column;

        index++;
        column++;

        while (index < text.length()) {
            char ch = Character.toUpperCase(text.charAt(index));
            if (ch == '.') {
                if (index + 1 < text.length()) {
                    char ch1 = text.charAt(index + 1);
                    if (!((ch1 >= '0' && ch1 <= '9'))) {
                        break;
                    }
                }
            }
            else if (ch == 'E') {
                if (index + 1 < text.length()) {
                    if (text.charAt(index + 1) == '+' || text.charAt(index + 1) == '-') {
                        index++;
                        column++;
                    }
                }
            }
            else if (!((ch >= '0' && ch <= '9') || ch == '_')) {
                break;
            }
            index++;
            column++;
        }

        return new Token(this, startIndex, line, startColumn, Token.NUMBER, text.substring(startIndex, index));
    }

    Token parseHexNumber() {
        int startIndex = index;
        int startColumn = column;
        char ch = text.charAt(index);

        index++;
        column++;

        while (index < text.length()) {
            ch = Character.toUpperCase(text.charAt(index));
            if (!((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') || ch == '_')) {
                break;
            }
            index++;
            column++;
        }

        return new Token(this, startIndex, line, startColumn, Token.NUMBER, text.substring(startIndex, index));
    }

    Token parseBinQuadNumber() {
        int startIndex = index;
        int startColumn = column;
        char ch = text.charAt(index);

        index++;
        column++;

        if (ch == '%' && index < text.length()) {
            if (text.charAt(index) == '%') {
                index++;
                column++;

                while (index < text.length()) {
                    ch = Character.toUpperCase(text.charAt(index));
                    if (!((ch >= '0' && ch <= '3') || ch == '_')) {
                        break;
                    }
                    index++;
                    column++;
                }

                return new Token(this, startIndex, line, startColumn, Token.NUMBER, text.substring(startIndex, index));
            }
        }

        while (index < text.length()) {
            ch = Character.toUpperCase(text.charAt(index));
            if (!((ch >= '0' && ch <= '1') || ch == '_')) {
                break;
            }
            index++;
            column++;
        }

        return new Token(this, startIndex, line, startColumn, Token.NUMBER, text.substring(startIndex, index));
    }

    Token parseKeyword() {
        int startIndex = index++;
        int startColumn = column++;

        while (index < text.length()) {
            char ch = Character.toUpperCase(text.charAt(index));
            if (ch == '.' && index + 1 < text.length()) {
                ch = Character.toUpperCase(text.charAt(index + 1));
                if (!((ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '_')) {
                    break;
                }
            }
            else if (!((ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '_')) {
                break;
            }
            index++;
            column++;
        }

        String s = text.substring(startIndex, index);
        if (index < text.length() && text.charAt(index) == '=') {
            if (MathOp.isAssignMathOp(s + "=")) {
                index++;
                column++;
                return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
            }
        }

        return new Token(this, startIndex, line, startColumn, 0, text.substring(startIndex, index));
    }

    Token parseOperator() {
        int startIndex = index;
        int startColumn = column;

        char ch0 = text.charAt(index);
        char ch1 = index + 1 < text.length() ? text.charAt(index + 1) : 0;
        char ch2 = index + 2 < text.length() ? text.charAt(index + 2) : 0;
        char ch3 = index + 3 < text.length() ? text.charAt(index + 3) : 0;

        if (ch0 == '+' && ch1 == '/' && ch2 == '/') {
            index += 3 + (ch3 == '=' ? 1 : 0);
            column += 3 + (ch3 == '=' ? 1 : 0);
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '+' && ch1 == '>' && ch2 == '=') {
            index += 3;
            column += 3;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '<' && ch1 == '=' && ch2 == '>') {
            index += 3;
            column += 3;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == ':' && ch1 == '=' && ch2 == ':') {
            index += 3;
            column += 3;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '.' && ch1 == '.' && ch2 == '.') {
            return skipToNextLine();
        }

        if (ch0 == '+' && ch1 == '+') {
            index += 2;
            column += 2;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '-' && ch1 == '-') {
            index += 2;
            column += 2;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '?' && ch1 == '?') {
            index += 2;
            column += 2;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }

        if (ch0 == '!' && ch1 == '!') {
            index += 2 + (ch2 == '=' ? 1 : 0);
            column += 2 + (ch2 == '=' ? 1 : 0);
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '~' && ch1 == '~') {
            index += 2;
            column += 2;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }

        if (ch0 == '^' && ch1 == '@') {
            index += 2;
            column += 2;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '@' && (ch1 == '@' || ch1 == '\\')) {
            index += 2;
            column += 2;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }

        if (ch0 == '-' && ch1 == '.') {
            index += 2;
            column += 2;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '>' && ch1 == '>') {
            index += 2 + (ch2 == '=' ? 1 : 0);
            column += 2 + (ch2 == '=' ? 1 : 0);
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '<' && ch1 == '<') {
            index += 2 + (ch2 == '=' ? 1 : 0);
            column += 2 + (ch2 == '=' ? 1 : 0);
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '*' && ch1 == '.') {
            index += 2;
            column += 2;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '/' && ch1 == '.') {
            index += 2;
            column += 2;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '+' && ch1 == '/') {
            index += 2 + (ch2 == '=' ? 1 : 0);
            column += 2 + (ch2 == '=' ? 1 : 0);
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '/' && ch1 == '/') {
            index += 2 + (ch2 == '=' ? 1 : 0);
            column += 2 + (ch2 == '=' ? 1 : 0);
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '+' && ch1 == '.') {
            index += 2;
            column += 2;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '#' && ch1 == '>') {
            index += 2 + (ch2 == '=' ? 1 : 0);
            column += 2 + (ch2 == '=' ? 1 : 0);
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '<' && ch1 == '#') {
            index += 2 + (ch2 == '=' ? 1 : 0);
            column += 2 + (ch2 == '=' ? 1 : 0);
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '+' && ch1 == '<') {
            index += 2 + (ch2 == '=' ? 1 : 0);
            column += 2 + (ch2 == '=' ? 1 : 0);
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '<' && ch1 == '.') {
            index += 2;
            column += 2;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '<' && ch1 == '=') {
            index += 2 + (ch2 == '.' ? 1 : 0);
            column += 2 + (ch2 == '.' ? 1 : 0);
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '=' && ch1 == '=') {
            index += 2 + (ch2 == '.' ? 1 : 0);
            column += 2 + (ch2 == '.' ? 1 : 0);
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '<' && ch1 == '>') {
            index += 2 + (ch2 == '.' ? 1 : 0);
            column += 2 + (ch2 == '.' ? 1 : 0);
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '>' && ch1 == '=') {
            index += 2 + (ch2 == '.' ? 1 : 0);
            column += 2 + (ch2 == '.' ? 1 : 0);
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '+' && ch1 == '>') {
            index += 2;
            column += 2;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '>' && ch1 == '.') {
            index += 2;
            column += 2;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '&' && ch1 == '&') {
            index += 2 + (ch2 == '=' ? 1 : 0);
            column += 2 + (ch2 == '=' ? 1 : 0);
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '^' && ch1 == '^') {
            index += 2 + (ch2 == '=' ? 1 : 0);
            column += 2 + (ch2 == '=' ? 1 : 0);
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '|' && ch1 == '|') {
            index += 2 + (ch2 == '=' ? 1 : 0);
            column += 2 + (ch2 == '=' ? 1 : 0);
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == ':' && ch1 == '=') {
            index += 2;
            column += 2;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '.' && ch1 == '.') {
            index += 2;
            column += 2;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }
        if (ch0 == '#' && ch1 == '#') {
            index += 2;
            column += 2;
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }

        if (ch0 == '!' || ch0 == '&' || ch0 == '^' || ch0 == '|' || ch0 == '*' || ch0 == '/' || ch0 == '+' || ch0 == '-') {
            index += 1 + (ch1 == '=' ? 1 : 0);
            column += 1 + (ch1 == '=' ? 1 : 0);
            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
        }

        index++;
        column++;

        return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
    }

    Token skipToNextLine() {
        int startIndex = index++;
        int startLine = line;
        int startColumn = column++;
        int nested = 0;

        while (index < text.length()) {
            char ch = text.charAt(index);
            if (ch == '\n') {
                column = 0;
                line++;
                index++;
                if (nested == 0) {
                    break;
                }
            }
            else if (ch == '\r') {
                if (index + 1 < text.length()) {
                    if (text.charAt(index + 1) == '\n') {
                        index++;
                    }
                }
                column = 0;
                line++;
                index++;
                if (nested == 0) {
                    break;
                }
            }
            else {
                if (ch == '{') {
                    nested++;
                }
                else if (ch == '}') {
                    if (nested > 0) {
                        nested--;
                    }
                }
                index++;
                column++;
            }
        }

        return new Token(this, startIndex, startLine, startColumn, Token.NEXT_LINE, text.substring(startIndex, index));
    }

    Token parseBacktickString() {
        char ch;
        int nested = 0;
        int startIndex = index;
        int startColumn = column;

        if ((ch = text.charAt(index)) == '`') {
            index++;
            column++;
        }
        backtickState = 1;

        while (index < text.length()) {
            ch = text.charAt(index);
            if (ch == '\t') {
                index++;
                column = ((column + 7) & 7) - 1;
            }
            else if (ch == '\r' || ch == '\n') {
                break;
            }
            else if (ch == '`') {
                backtickState = 2;
                return new Token(this, startIndex, line, startColumn, Token.STRING, text.substring(startIndex, index));
            }
            else {
                if (ch == '(') {
                    nested++;
                }
                if (ch == ')') {
                    if (nested == 0) {
                        if (index == startIndex) {
                            index++;
                            column++;
                            backtickState = 0;
                            return new Token(this, startIndex, line, startColumn, Token.OPERATOR, text.substring(startIndex, index));
                        }
                        break;
                    }
                    nested--;
                }
                index++;
                column++;
            }
        }

        backtickState = 0;

        return new Token(this, startIndex, line, startColumn, Token.STRING, text.substring(startIndex, index));
    }

    Token parseBacktickKeyword() {
        char ch;
        int startIndex = index;
        int startColumn = column;

        ch = text.charAt(index);
        index++;
        column++;

        if (ch == '`') {
            if (index < text.length()) {
                ch = text.charAt(index);
                if (ch == '$' || ch == '%' || ch == '.' || ch == '#' || ch == '?') {
                    index++;
                    column++;
                }
            }
            if (index < text.length()) {
                ch = text.charAt(index);
                if (ch == '(') {
                    backtickState = 3;
                    return new Token(this, startIndex, line, startColumn, 0, text.substring(startIndex, index));
                }
            }
        }

        while (index < text.length()) {
            ch = Character.toUpperCase(text.charAt(index));
            if (!((ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '_')) {
                break;
            }
            index++;
            column++;
        }

        backtickState = 3;

        return new Token(this, startIndex, line, startColumn, 0, text.substring(startIndex, index));
    }

    Token parseBacktickNext() {
        char ch;

        while (index < text.length()) {
            ch = Character.toUpperCase(text.charAt(index));
            if (ch == ' ') {
                index++;
                column++;
            }
            else if (ch == '\t') {
                index++;
                column = ((column + 7) & 7) - 1;
            }
            else if (ch == '\r' || ch == '\n') {
                break;
            }
            else if (ch == '{') {
                return parseBlockComment();
            }
            else if (ch == '$') {
                return parseHexNumber();
            }
            else if (ch == '%') {
                return parseBinQuadNumber();
            }
            else if ((ch >= '0' && ch <= '9')) {
                return parseNumber();
            }
            else if ((ch >= 'A' && ch <= 'Z') || ch == '_') {
                return parseKeyword();
            }
            else {
                if (ch == '(') {
                    backtickNestedParens++;
                }
                if (ch == ')') {
                    if (backtickNestedParens > 0) {
                        backtickNestedParens--;
                    }
                    if (backtickNestedParens == 0) {
                        backtickState = 1;
                    }
                }
                return parseOperator();
            }
        }

        return eofToken;
    }

}
