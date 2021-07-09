/*
 * Copyright (c) 2021 Marco Maccaferri and others.
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
    public static final int NUMBER = 4;
    public static final int KEYWORD = 5;
    public static final int OPERATOR = 6;
    public static final int FUNCTION = 7;
    public static final int DEBUG = 8;
    public static final int NL = 9;
    public static final int EOF = -1;

    public int type;
    public int start;
    public int stop;
    public int line;
    public int column;

    private TokenStream stream;
    private String text;

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

    public String getText() {
        if (type == EOF) {
            return "<EOF>";
        }
        if (text == null) {
            text = stream.getSource(start, stop);
        }
        return text;
    }

    public TokenStream getStream() {
        return stream;
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
        return String.format("type=%d, start=%d, stop=%d, line=%d, column=%d, text='%s'", type, start, stop, line, column, getText().replaceAll("\n", "\\\\n"));
    }

}
