/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
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

public class Spin2TokenStream extends TokenStream {

    public Spin2TokenStream(String text) {
        super(text);
    }

    @Override
    public Token nextToken() {
        int nested = 0;
        int state = Token.START;
        boolean escape = false;
        Token token = null;

        for (; index < text.length(); index++, column++) {
            char ch = text.charAt(index);
            switch (state) {
                case Token.START:
                    if (ch == ' ' || ch == '\t') { // Skip white spaces
                        if (ch == '\t') {
                            column = ((column + 7) & 7) - 1;
                        }
                        break;
                    }
                    token = new Token(this, index);
                    token.column = column;
                    token.line = line;
                    if (ch == '\r') {
                        column = 0;
                        line++;
                        index++;
                        if (index < text.length()) {
                            if (text.charAt(index) == '\n') {
                                index++;
                            }
                        }
                        token.type = Token.NL;
                        return token;
                    }
                    else if (ch == '\n') {
                        column = 0;
                        line++;
                        index++;
                        token.type = Token.NL;
                        return token;
                    }
                    else if (ch == '\'') { // Comment
                        state = token.type = Token.COMMENT;
                    }
                    else if (ch == '{') { // Block comment
                        nested = 0;
                        state = token.type = Token.BLOCK_COMMENT;
                    }
                    else if (ch == '"') { // String
                        state = token.type = Token.STRING;
                    }
                    else if (ch == '`') { // Debug command
                        token.type = Token.STRING;
                        //nested = 1;
                        state = Token.DEBUG;
                    }
                    else if (ch == '$') { // Hex number
                        state = token.type = Token.NUMBER;
                    }
                    else if (ch == '%') { // Bin/Quad number
                        if ((index + 1) < text.length()) {
                            if (text.charAt(index + 1) == '%') {
                                token.stop++;
                                index++;
                                column++;
                            }
                        }
                        state = token.type = Token.NUMBER;
                    }
                    else if (ch >= '0' && ch <= '9') { // Decimal
                        state = token.type = Token.NUMBER;
                    }
                    else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_') { // Keyword
                        state = Token.KEYWORD;
                    }
                    else { // operator
                        state = token.type = Token.OPERATOR;
                    }
                    break;
                case Token.COMMENT:
                    if (ch == '\r' || ch == '\n') {
                        return token;
                    }
                    token.stop++;
                    break;
                case Token.BLOCK_COMMENT:
                    token.stop++;
                    if (ch == '\r' && index + 1 < text.length()) {
                        if (text.charAt(index + 1) == '\n') {
                            token.stop++;
                            index++;
                        }
                    }
                    if (ch == '\r' || ch == '\n') {
                        column = 0;
                        line++;
                    }
                    else if (ch == '{') {
                        nested++;
                    }
                    else if (ch == '}') {
                        if (nested == 0) {
                            index++;
                            column++;
                            return token;
                        }
                        nested--;
                    }
                    break;
                case Token.NUMBER:
                    if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || ch == '_') {
                        token.stop++;
                        break;
                    }

                    if ((ch == '+' || ch == '-') && (text.charAt(index - 1) == 'e' || text.charAt(index - 1) == 'E')) {
                        if (text.charAt(token.start) == '$' || text.charAt(token.start) == '%') {
                            return token;
                        }
                        token.stop++;
                        break;
                    }

                    if (ch == '.' && index + 1 < text.length()) {
                        char ch1 = text.charAt(index + 1);
                        if ((ch1 >= '0' && ch1 <= '9') || (ch1 >= 'A' && ch1 <= 'Z') || (ch1 >= 'a' && ch1 <= 'z') || ch1 == '_') {
                            token.stop++;
                            break;
                        }
                    }

                    return token;
                case Token.STRING:
                    if (ch == '\r' || ch == '\n') {
                        return token;
                    }
                    token.stop++;
                    if (escape) {
                        escape = false;
                        break;
                    }
                    if (ch == '"') {
                        index++;
                        column++;
                        return token;
                    }
                    break;
                case Token.KEYWORD:
                    if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_') {
                        token.stop++;
                        break;
                    }

                    if ((ch == '.' || ch == '#')) {
                        if (index + 1 < text.length()) {
                            char ch1 = text.charAt(index + 1);
                            if ((ch1 >= '0' && ch1 <= '9') || (ch1 >= 'A' && ch1 <= 'Z') || (ch1 >= 'a' && ch1 <= 'z') || ch1 == '_') {
                                token.stop++;
                                break;
                            }
                        }
                        return token;
                    }
                    if (ch == '=') {
                        if ((token.stop - token.start) == 6) {
                            if (Character.toUpperCase(text.charAt(token.start)) == 'A') {
                                if (Character.toUpperCase(text.charAt(token.start + 1)) == 'D') {
                                    if (Character.toUpperCase(text.charAt(token.start + 2)) == 'D') {
                                        if (Character.toUpperCase(text.charAt(token.start + 3)) == 'B') {
                                            if (Character.toUpperCase(text.charAt(token.start + 4)) == 'I') {
                                                if (Character.toUpperCase(text.charAt(token.start + 5)) == 'T') {
                                                    if (Character.toUpperCase(text.charAt(token.start + 6)) == 'S') {
                                                        token.stop++;
                                                        token.type = Token.OPERATOR;
                                                        index++;
                                                        column++;
                                                    }
                                                }
                                            }
                                        }
                                        if (Character.toUpperCase(text.charAt(token.start + 3)) == 'P') {
                                            if (Character.toUpperCase(text.charAt(token.start + 4)) == 'I') {
                                                if (Character.toUpperCase(text.charAt(token.start + 5)) == 'N') {
                                                    if (Character.toUpperCase(text.charAt(token.start + 6)) == 'S') {
                                                        token.stop++;
                                                        token.type = Token.OPERATOR;
                                                        index++;
                                                        column++;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else if ((token.stop - token.start) == 4) {
                            if (Character.toUpperCase(text.charAt(token.start)) == 'Z') {
                                if (Character.toUpperCase(text.charAt(token.start + 1)) == 'E') {
                                    if (Character.toUpperCase(text.charAt(token.start + 2)) == 'R') {
                                        if (Character.toUpperCase(text.charAt(token.start + 3)) == 'O') {
                                            if (Character.toUpperCase(text.charAt(token.start + 4)) == 'X') {
                                                token.stop++;
                                                token.type = Token.OPERATOR;
                                                index++;
                                                column++;
                                            }
                                        }
                                    }
                                }
                            }
                            if (Character.toUpperCase(text.charAt(token.start)) == 'S') {
                                if (Character.toUpperCase(text.charAt(token.start + 1)) == 'I') {
                                    if (Character.toUpperCase(text.charAt(token.start + 2)) == 'G') {
                                        if (Character.toUpperCase(text.charAt(token.start + 3)) == 'N') {
                                            if (Character.toUpperCase(text.charAt(token.start + 4)) == 'X') {
                                                token.stop++;
                                                token.type = Token.OPERATOR;
                                                index++;
                                                column++;
                                            }
                                        }
                                    }
                                }
                            }
                            if (Character.toUpperCase(text.charAt(token.start)) == 'T') {
                                if (Character.toUpperCase(text.charAt(token.start + 1)) == 'R') {
                                    if (Character.toUpperCase(text.charAt(token.start + 2)) == 'U') {
                                        if (Character.toUpperCase(text.charAt(token.start + 3)) == 'N') {
                                            if (Character.toUpperCase(text.charAt(token.start + 4)) == 'C') {
                                                token.stop++;
                                                token.type = Token.OPERATOR;
                                                index++;
                                                column++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else if ((token.stop - token.start) == 3) {
                            if (Character.toUpperCase(text.charAt(token.start)) == 'S') {
                                if (Character.toUpperCase(text.charAt(token.start + 1)) == 'C') {
                                    if (Character.toUpperCase(text.charAt(token.start + 2)) == 'A') {
                                        if (Character.toUpperCase(text.charAt(token.start + 3)) == 'S') {
                                            token.stop++;
                                            token.type = Token.OPERATOR;
                                            index++;
                                            column++;
                                        }
                                    }
                                }
                            }
                        }
                        else if ((token.stop - token.start) == 2) {
                            if (Character.toUpperCase(text.charAt(token.start)) == 'A') {
                                if (Character.toUpperCase(text.charAt(token.start + 1)) == 'N') {
                                    if (Character.toUpperCase(text.charAt(token.start + 2)) == 'D') {
                                        token.stop++;
                                        token.type = Token.OPERATOR;
                                        index++;
                                        column++;
                                    }
                                }
                            }
                            if (Character.toUpperCase(text.charAt(token.start)) == 'X') {
                                if (Character.toUpperCase(text.charAt(token.start + 1)) == 'O') {
                                    if (Character.toUpperCase(text.charAt(token.start + 2)) == 'R') {
                                        token.stop++;
                                        token.type = Token.OPERATOR;
                                        index++;
                                        column++;
                                    }
                                }
                            }
                            if (Character.toUpperCase(text.charAt(token.start)) == 'S') {
                                if (Character.toUpperCase(text.charAt(token.start + 1)) == 'A') {
                                    if (Character.toUpperCase(text.charAt(token.start + 2)) == 'R') {
                                        token.stop++;
                                        token.type = Token.OPERATOR;
                                        index++;
                                        column++;
                                    }
                                }
                                if (Character.toUpperCase(text.charAt(token.start + 1)) == 'C') {
                                    if (Character.toUpperCase(text.charAt(token.start + 2)) == 'A') {
                                        token.stop++;
                                        token.type = Token.OPERATOR;
                                        index++;
                                        column++;
                                    }
                                }
                            }
                            if (Character.toUpperCase(text.charAt(token.start)) == 'R') {
                                if (Character.toUpperCase(text.charAt(token.start + 1)) == 'O') {
                                    if (Character.toUpperCase(text.charAt(token.start + 2)) == 'R') {
                                        token.stop++;
                                        token.type = Token.OPERATOR;
                                        index++;
                                        column++;
                                    }
                                    if (Character.toUpperCase(text.charAt(token.start + 2)) == 'L') {
                                        token.stop++;
                                        token.type = Token.OPERATOR;
                                        index++;
                                        column++;
                                    }
                                }
                                if (Character.toUpperCase(text.charAt(token.start + 1)) == 'E') {
                                    if (Character.toUpperCase(text.charAt(token.start + 2)) == 'V') {
                                        token.stop++;
                                        token.type = Token.OPERATOR;
                                        index++;
                                        column++;
                                    }
                                }
                            }
                        }
                        else if ((token.stop - token.start) == 1) {
                            if (Character.toUpperCase(text.charAt(token.start)) == 'O') {
                                if (Character.toUpperCase(text.charAt(token.start + 1)) == 'R') {
                                    token.stop++;
                                    token.type = Token.OPERATOR;
                                    index++;
                                    column++;
                                }
                            }
                        }
                    }
                    return token;
                case Token.OPERATOR:
                    if ((token.stop - token.start + 1) == 1) {
                        char ch0 = text.charAt(token.start);
                        if (ch0 == '!' && (ch == '!' || ch == '=')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '.' && (ch == '.')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '#' && (ch == '>' || ch == '#')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '&' && (ch == '&' || ch == '=')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '*' && (ch == '=' || ch == '.')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '+' && (ch == '+' || ch == '/' || ch == '<' || ch == '=' || ch == '>' || ch == '.')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '-' && (ch == '-' || ch == '=' || ch == '.')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '/' && (ch == '/' || ch == '=' || ch == '.')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == ':' && (ch == '=')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '<' && (ch == '#' || ch == '<' || ch == '=' || ch == '>' || ch == '.')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '=' && (ch == '=')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '>' && (ch == '=' || ch == '>' || ch == '.')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '?' && (ch == '?')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '^' && (ch == '^' || ch == '=' || ch == '@')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '|' && (ch == '|' || ch == '=')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '~' && (ch == '~')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '@' && (ch == '@')) {
                            token.stop++;
                            break;
                        }
                    }
                    else if ((token.stop - token.start + 1) == 2) {
                        char ch0 = text.charAt(token.start);
                        char ch1 = text.charAt(token.start + 1);
                        if (ch0 == '+' && ch1 == '/' && ch == '/') {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '+' && ch1 == '<' && ch == '/') {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '<' && ch1 == '=' && (ch == '>' || ch == '.')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '>' && ch1 == '=' && (ch == '.')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '<' && ch1 == '>' && (ch == '.')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '=' && ch1 == '=' && (ch == '.')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '.' && ch1 == '.' && ch == '.') {
                            token.stop++;
                            token.type = state = Token.NEXT_LINE;
                            break;
                        }
                        if (ch == '=') {
                            token.stop++;
                            index++;
                            column++;
                        }
                    }
                    else if ((token.stop - token.start + 1) == 3) {
                        if (ch == '=') {
                            token.stop++;
                            index++;
                            column++;
                        }
                    }
                    return token;
                case Token.NEXT_LINE:
                    token.stop++;
                    if (ch == '\r' && index + 1 < text.length()) {
                        if (text.charAt(index + 1) == '\n') {
                            token.stop++;
                            index++;
                        }
                    }
                    if (ch == '\r' || ch == '\n') {
                        column = 0;
                        line++;
                        if (nested == 0) {
                            index++;
                            return token;
                        }
                    }
                    else if (ch == '{') {
                        nested++;
                    }
                    else if (ch == '}') {
                        if (nested == 0) {
                            index++;
                            column++;
                            return token;
                        }
                        nested--;
                    }
                    break;
                case Token.DEBUG:
                    if (ch == '(') {
                        nested++;
                    }
                    else if (ch == ')') {
                        if (nested == 0) {
                            return token;
                        }
                        if (nested > 0) {
                            nested--;
                        }
                    }
                    if (nested == 0 && ch == '`') {
                        index++;
                        column++;
                        return token;
                    }
                    token.stop++;
                    break;
            }
        }

        if (token == null) {
            token = new Token(this, text.length() - 1, Token.EOF);
        }

        return token;
    }

}
