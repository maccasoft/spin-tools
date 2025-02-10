/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

public abstract class TokenStream {

    public static class Position {

        final TokenStream stream;
        final int index;
        final int line;
        final int column;

        public Position(TokenStream stream) {
            this.stream = stream;
            this.index = stream.index;
            this.line = stream.line;
            this.column = stream.column;
        }

        public void restore() {
            stream.index = this.index;
            stream.line = this.line;
            stream.column = this.column;
        }

    }

    protected final String text;

    protected int index = 0;
    protected int line = 0;
    protected int column = 0;

    public TokenStream(String text) {
        this.text = text;
        if (text == null) {
            throw new NullPointerException();
        }
    }

    public Token peekNext() {
        Position pos = new Position(this);
        try {
            return nextToken();
        } finally {
            pos.restore();
        }
    }

    public abstract Token nextToken();

    public String getSource(int start, int stop) {
        return text.substring(start, stop + 1);
    }

    public void reset() {
        index = column = line = 0;
    }

}
