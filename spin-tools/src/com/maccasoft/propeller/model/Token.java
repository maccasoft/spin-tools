/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

import java.util.Objects;

public class Token {

    public static final int START = 0;
    public static final int COMMENT = 1;
    public static final int BLOCK_COMMENT = 2;
    public static final int STRING = 3;
    public static final int CHAR = 4;
    public static final int NUMBER = 5;
    public static final int KEYWORD = 6;
    public static final int OPERATOR = 7;
    public static final int FUNCTION = 8;
    public static final int DEBUG = 9;
    public static final int NL = 10;
    public static final int NEXT_LINE = 11;
    public static final int EOF = -1;

    public int type;
    public int start;
    public int stop;
    public int line;
    public int column;

    private TokenStream stream;
    private String text;

    private Token() {

    }

    public Token(int type, String text) {
        this.type = type;
        this.text = text;
    }

    public Token(TokenStream stream, int start) {
        this.start = start;
        this.stop = start;
        this.stream = stream;
    }

    public Token(TokenStream stream, int start, int type) {
        this.start = start;
        this.stop = start;
        this.type = type;
        this.stream = stream;
    }

    public Token(TokenStream stream, int start, int line, int column, int type) {
        this.stream = stream;
        this.start = start;
        this.stop = start;
        this.line = line;
        this.column = column;
        this.type = type;
    }

    public Token(TokenStream stream, int start, int line, int column, int type, String text) {
        this.stream = stream;
        this.start = start;
        this.stop = start + text.length() - 1;
        this.line = line;
        this.column = column;
        this.type = type;
        this.text = text;
    }

    public String getText() {
        if (type == EOF || type == NL) {
            return "";
        }
        if (text == null) {
            text = stream.getSource(start, stop);
        }
        return text;
    }

    public TokenStream getStream() {
        return stream;
    }

    public boolean isAdjacent(Token token) {
        return (stop + 1) == token.start;
    }

    public Token merge(Token token) {
        Token result = new Token();
        result.stream = stream;
        result.line = line;
        result.column = column;
        result.start = start < token.start ? start : token.start;
        result.stop = stop > token.stop ? stop : token.stop;
        result.type = type == token.type ? type : 0;
        return result;
    }

    public Token substring(int start) {
        Token result = new Token();
        result.stream = stream;
        result.line = line;
        result.column = column + start;
        result.start = this.start + start;
        result.stop = this.stop;
        result.type = this.type;
        return result;
    }

    public Token substring(int start, int stop) {
        Token result = new Token();
        result.stream = stream;
        result.line = line;
        result.column = column + start;
        result.start = this.start + start;
        result.stop = this.start + stop;
        result.type = this.type;
        return result;
    }

    public boolean equals(String text, boolean caseSensitive) {
        return caseSensitive ? getText().equals(text) : getText().equalsIgnoreCase(text);
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
        if (type == EOF) {
            return "<EOF>";
        }
        if (type == NL) {
            return "<NL>";
        }
        return getText();
    }

}
