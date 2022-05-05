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
    public Token nextToken(boolean skipComments) {
        int nested = 0;
        int state = Token.START;
        boolean escape = false;
        Token token = EOF_TOKEN;

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
                        token.type = Token.OPERATOR;
                        if (ch == '@' || ch == '(' || ch == ')' || ch == '[' || ch == ']' || ch == ',' || ch == ';' || ch == '\\') {
                            index++;
                            column++;
                            state = Token.START;
                            return token;
                        }
                        state = Token.OPERATOR;
                    }
                    break;
                case Token.COMMENT:
                    if (ch == '\r' || ch == '\n') {
                        state = Token.START;
                        hiddenTokens.add(token);
                        if (!skipComments) {
                            return token;
                        }
                        index--;
                        token = EOF_TOKEN;
                        break;
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
                            state = Token.START;
                            hiddenTokens.add(token);
                            if (!skipComments) {
                                index++;
                                column++;
                                return token;
                            }
                            token = EOF_TOKEN;
                            break;
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

                    state = Token.START;
                    return token;
                case Token.STRING:
                    token.stop++;
                    if (escape) {
                        escape = false;
                        break;
                    }
                    if (ch == '"') {
                        index++;
                        column++;
                        state = Token.START;
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
                            if ((ch1 >= '0' && ch1 <= '9') || (ch1 >= 'A' && ch1 <= 'Z') || (ch1 >= 'a' && ch1 <= 'z') || ch1 == '=') {
                                token.stop++;
                                break;
                            }
                        }
                        state = Token.START;
                        return token;
                    }

                    if (ch == '=') {
                        if (index + 1 < text.length()) {
                            char ch1 = text.charAt(index + 1);
                            if ((ch1 >= '0' && ch1 <= '9') || (ch1 >= 'A' && ch1 <= 'Z') || (ch1 >= 'a' && ch1 <= 'z') || ch1 == '=') {
                                state = Token.START;
                                return token;
                            }
                        }
                        token.stop++;
                        index++;
                        column++;
                    }

                    state = Token.START;
                    return token;
                case Token.OPERATOR:
                    if (text.charAt(index - 1) == '#') {
                        if (ch != '>' && ch != '=' && ch != '#') {
                            state = Token.START;
                            return token;
                        }
                    }
                    if (ch == '|' || ch == '!' || ch == '=' || ch == '^' || ch == '+' || ch == '*' || ch == '-' || ch == '/' || ch == '#' || ch == ':' || ch == '>' || ch == '<' || ch == '.'
                        || ch == '~' || ch == '&' || ch == '?') {
                        token.stop++;
                        break;
                    }
                    state = Token.START;
                    return token;
                case Token.DEBUG:
                    if (ch == '(') {
                        nested++;
                    }
                    else if (ch == ')') {
                        if (nested == 0) {
                            state = Token.START;
                            return token;
                        }
                        if (nested > 0) {
                            nested--;
                        }
                    }
                    if (nested == 0 && ch == '`') {
                        state = Token.START;
                        return token;
                    }
                    token.stop++;
                    break;
            }
        }

        if (state == Token.COMMENT || state == Token.BLOCK_COMMENT) {
            hiddenTokens.add(token);
            return !skipComments ? token : EOF_TOKEN;
        }

        if (token == EOF_TOKEN) {
            token.start = token.stop = text.length() - 1;
        }

        return token;
    }

}
