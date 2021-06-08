/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Spin2TokenStream {

    static final int START = 0;
    static final int COMMENT = 1;
    static final int BLOCK_COMMENT = 2;
    static final int STRING = 3;
    static final int NUMBER = 4;
    static final int KEYWORD = 5;
    static final int OPERATOR = 6;
    static final int DEBUG = 7;
    static final int NL = 8;
    static final int EOF = -1;

    final String text;
    final Token eofToken = new Token(EOF, 0, 0);

    int index = 0;
    int line = 0;
    int column = 0;
    int state = START;
    int nested = 0;
    boolean escape = false;

    List<Token> tokens = new ArrayList<Token>();
    List<Token> hiddenTokens = new ArrayList<Token>();

    public class Token {
        public int type;
        public int start;
        public int stop;
        public int line;
        public int column;

        private String text;

        public Token(int start) {
            this.start = start;
            this.stop = start;
        }

        public Token(int type, int start, int stop) {
            this.type = type;
            this.start = start;
            this.stop = stop;
        }

        public Token(Token token0, Token token1) {
            this.start = token0.start;
            this.line = token0.line;
            this.column = token0.column;
            this.stop = token1.stop;
        }

        public String getText() {
            if (this.text == null) {
                this.text = Spin2TokenStream.this.text.substring(start, stop + 1);
            }
            return this.text;
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, start, stop);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Token)) {
                return false;
            }
            Token other = (Token) obj;
            return type == other.type && start == other.start && stop == other.stop;
        }

        @Override
        public String toString() {
            return String.format("%s [type=%d, start=%d, stop=%d, line=%d, column=%d, text='%s']", getClass().getSimpleName(), type, start, stop, line, column, getText());
        }
    }

    public Spin2TokenStream(String text) {
        this.text = text;
    }

    public Token nextToken() {
        Token token = eofToken;

        for (; index < text.length(); index++, column++) {
            char ch = text.charAt(index);
            switch (state) {
                case START:
                    if (ch == ' ' || ch == '\t') { // Skip white spaces
                        if (ch == '\t') {
                            column = ((column + 7) & 7) - 1;
                        }
                        break;
                    }
                    token = new Token(index);
                    token.column = column;
                    token.line = line;
                    if (ch == '\r' || ch == '\n') {
                        state = token.type = NL;
                    }
                    else if (ch == '\'') { // Comment
                        state = token.type = COMMENT;
                    }
                    else if (ch == '{') { // Block comment
                        nested = 0;
                        state = token.type = BLOCK_COMMENT;
                    }
                    else if (ch == '"') { // String
                        state = token.type = STRING;
                    }
                    else if (ch == '`') { // Debug command
                        token.type = STRING;
                        nested = 1;
                        state = DEBUG;
                    }
                    else if (ch == '$') { // Hex number
                        state = token.type = NUMBER;
                    }
                    else if (ch == '%') { // Bin/Quad number
                        if ((index + 1) < text.length()) {
                            if (text.charAt(index + 1) == '%') {
                                token.stop++;
                                index++;
                            }
                        }
                        state = token.type = NUMBER;
                    }
                    else if (ch >= '0' && ch <= '9') { // Decimal
                        state = token.type = NUMBER;
                    }
                    else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_') { // Keyword
                        state = KEYWORD;
                    }
                    else { // operator
                        if (ch == '.' && index + 1 < text.length()) {
                            char ch1 = text.charAt(index + 1);
                            if ((ch1 >= 'a' && ch1 <= 'z') || (ch1 >= 'A' && ch1 <= 'Z') || ch1 == '_') { // Keyword
                                state = KEYWORD;
                                break;
                            }
                        }
                        if (ch == '@' && index + 1 < text.length()) {
                            char ch1 = text.charAt(index + 1);
                            if ((ch1 >= 'a' && ch1 <= 'z') || (ch1 >= 'A' && ch1 <= 'Z') || ch1 == '_') { // Keyword
                                state = KEYWORD;
                                break;
                            }
                            if (ch1 == '.') {
                                token.stop++;
                                index++;
                                state = KEYWORD;
                                break;
                            }
                        }
                        token.type = OPERATOR;
                        if (ch == '(' || ch == ')' || ch == '[' || ch == ']' || ch == ',' || ch == ';') {
                            index++;
                            tokens.add(token);
                            state = START;
                            return token;
                        }
                        state = OPERATOR;
                    }
                    break;
                case NL:
                    if (ch != '\r' && ch != '\n') {
                        column = 0;
                        line++;
                        tokens.add(token);
                        state = START;
                        return token;
                    }
                    token.stop++;
                    break;
                case COMMENT:
                    if (ch == '\r' || ch == '\n') {
                        index--;
                        state = START;
                        hiddenTokens.add(token);
                        token = eofToken;
                        break;
                    }
                    token.stop++;
                    break;
                case BLOCK_COMMENT:
                    token.stop++;
                    if (ch == '{') {
                        nested++;
                    }
                    else if (ch == '}') {
                        if (nested == 0) {
                            index++;
                            state = START;
                            hiddenTokens.add(token);
                            token = eofToken;
                            break;
                        }
                        nested--;
                    }
                    break;
                case NUMBER:
                    if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || ch == '_' || ch == '.') {
                        token.stop++;
                    }
                    else {
                        tokens.add(token);
                        state = START;
                        return token;
                    }
                    break;
                case STRING:
                    token.stop++;
                    if (escape) {
                        escape = false;
                        break;
                    }
                    if (ch == '"') {
                        index++;
                        tokens.add(token);
                        state = START;
                        return token;
                    }
                    break;
                case KEYWORD:
                    if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_') {
                        token.stop++;
                        break;
                    }

                    tokens.add(token);
                    state = START;
                    return token;
                case OPERATOR: {
                    char ch0 = text.charAt(index - 1);
                    if (ch == ch0) {
                        token.stop++;
                        index++;
                        if (index < text.length()) {
                            ch = text.charAt(index);
                            if (ch == '=') {
                                token.stop++;
                                index++;
                            }
                        }
                    }
                    else if (ch0 == '+' && ch == '/') {
                        token.stop++;
                        index++;
                        if (index < text.length()) {
                            ch = text.charAt(index);
                            if (ch == '/') {
                                token.stop++;
                                index++;
                            }
                        }
                    }
                    else if (ch0 == '+' && (ch == '<' || ch == '>')) {
                        token.stop++;
                        index++;
                        if (index < text.length()) {
                            ch = text.charAt(index);
                            if (ch == '=') {
                                token.stop++;
                                index++;
                            }
                        }
                    }
                    else if (ch0 == '<' && ch == '>') {
                        token.stop++;
                        index++;
                    }
                    else if (ch0 == '<' && ch == '=' && (index + 1 < text.length() && text.charAt(index + 1) == '>')) {
                        token.stop += 2;
                        index += 2;
                    }
                    else if (ch == '=') {
                        token.stop++;
                        index++;
                    }
                    tokens.add(token);
                    state = START;
                    return token;
                }
                case DEBUG:
                    if (ch == '(') {
                        nested++;
                    }
                    else if (ch == ')') {
                        if (nested > 0) {
                            nested--;
                        }
                        if (nested == 0) {
                            state = START;
                            return token;
                        }
                    }
                    token.stop++;
                    break;
            }
        }

        return token;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public List<Token> getHiddenTokens() {
        return hiddenTokens;
    }

}
