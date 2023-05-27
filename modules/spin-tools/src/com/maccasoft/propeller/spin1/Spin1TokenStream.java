/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenStream;

public class Spin1TokenStream extends TokenStream {

    public Spin1TokenStream(String text) {
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

                    if ((ch == '.' || ch == '#') && index + 1 < text.length()) {
                        char ch1 = text.charAt(index + 1);
                        if ((ch1 >= '0' && ch1 <= '9') || (ch1 >= 'A' && ch1 <= 'Z') || (ch1 >= 'a' && ch1 <= 'z') || ch1 == '_') {
                            token.stop++;
                            break;
                        }
                    }

                    if (ch == '=') {
                        if ((token.stop - token.start) == 2) {
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
                            if (Character.toUpperCase(text.charAt(token.start)) == 'N') {
                                if (Character.toUpperCase(text.charAt(token.start + 1)) == 'O') {
                                    if (Character.toUpperCase(text.charAt(token.start + 2)) == 'T') {
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
                        if (ch0 == '#' && (ch == '>')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '%' && (ch == '%')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '&' && (ch == '=')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '*' && (ch == '*' || ch == '=')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '+' && (ch == '+' || ch == '=')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '-' && (ch == '-' || ch == '=' || ch == '>')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '.' && (ch == '.')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '/' && (ch == '/' || ch == '=')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == ':' && (ch == '=')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '<' && (ch == '#' || ch == '-' || ch == '<' || ch == '=' || ch == '>')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '=' && (ch == '<' || ch == '=' || ch == '>')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '>' && (ch == '<' || ch == '=' || ch == '>' || ch == '|')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '@' && (ch == '@')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '^' && (ch == '=' || ch == '^')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '|' && (ch == '<' || ch == '=' || ch == '|')) {
                            token.stop++;
                            break;
                        }
                        if (ch0 == '~' && (ch == '>' || ch == '~')) {
                            token.stop++;
                            break;
                        }
                    }
                    else if ((token.stop - token.start + 1) == 2) {
                        if (ch == '=') {
                            token.stop++;
                            index++;
                            column++;
                        }
                    }
                    return token;
            }
        }

        if (token == null) {
            token = new Token(this, text.length() - 1, Token.EOF);
        }

        return token;
    }

}
