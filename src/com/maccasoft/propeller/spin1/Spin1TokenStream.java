/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.util.ArrayList;
import java.util.List;

import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenStream;

public class Spin1TokenStream extends TokenStream {

    private final Token EOF_TOKEN = new Token(null, 0, Token.EOF);

    final String text;

    int index = 0;
    int line = 0;
    int column = 0;
    int state = Token.START;
    int nested = 0;
    boolean escape = false;

    List<Token> hiddenTokens = new ArrayList<Token>();

    public Spin1TokenStream(String text) {
        this.text = text;
    }

    @Override
    public Token nextToken() {
        return nextToken(false);
    }

    public Token nextToken(boolean comments) {
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
                    if (ch == '\r' || ch == '\n') {
                        state = token.type = Token.NL;
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
                        nested = 1;
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
                        if ((ch == '.' || ch == ':') && index + 1 < text.length()) {
                            char ch1 = text.charAt(index + 1);
                            if ((ch1 >= 'a' && ch1 <= 'z') || (ch1 >= 'A' && ch1 <= 'Z') || ch1 == '_') { // Local label
                                state = Token.KEYWORD;
                                break;
                            }
                        }
                        if (ch == '@' && index + 1 < text.length()) {
                            char ch1 = text.charAt(index + 1);
                            if ((ch1 >= 'a' && ch1 <= 'z') || (ch1 >= 'A' && ch1 <= 'Z') || ch1 == '_') { // Keyword
                                state = Token.KEYWORD;
                                break;
                            }
                            if (ch1 == '.') {
                                token.stop++;
                                index++;
                                state = Token.KEYWORD;
                                break;
                            }
                        }
                        token.type = Token.OPERATOR;
                        if (ch == '(' || ch == ')' || ch == '[' || ch == ']' || ch == ',' || ch == ';') {
                            index++;
                            state = Token.START;
                            return token;
                        }
                        state = Token.OPERATOR;
                    }
                    break;
                case Token.NL:
                    column = 0;
                    line++;
                    if (ch != '\r' && ch != '\n') {
                        state = Token.START;
                        return token;
                    }
                    token.stop++;
                    break;
                case Token.COMMENT:
                    if (ch == '\r' || ch == '\n') {
                        state = Token.START;
                        hiddenTokens.add(token);
                        if (comments) {
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
                            index++;
                            state = Token.START;
                            hiddenTokens.add(token);
                            if (comments) {
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
                        state = Token.START;
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
                        token.stop++;
                        index++;
                    }

                    state = Token.START;
                    return token;
                case Token.OPERATOR:
                    if (ch == '|' || ch == '!' || ch == '=' || ch == '^' || ch == '+' || ch == '*' || ch == '-' || ch == '/' || ch == '#' || ch == ':' || ch == '>' || ch == '<' || ch == '.'
                        || ch == '~') {
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
                        if (nested > 0) {
                            nested--;
                        }
                        if (nested == 0) {
                            state = Token.START;
                            return token;
                        }
                    }
                    token.stop++;
                    break;
            }
        }

        if (state == Token.COMMENT || state == Token.BLOCK_COMMENT) {
            hiddenTokens.add(token);
            return comments ? token : EOF_TOKEN;
        }

        if (token == EOF_TOKEN) {
            token.start = token.stop = text.length() - 1;
        }

        return token;
    }

    @Override
    public String getSource(int start, int stop) {
        return text.substring(start, stop + 1);
    }

    @Override
    public List<Token> getHiddenTokens() {
        return hiddenTokens;
    }

}
