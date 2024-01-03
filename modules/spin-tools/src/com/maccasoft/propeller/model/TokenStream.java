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

public abstract class TokenStream {

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
        int saveIndex = index;
        int saveLine = line;
        int saveColumn = column;

        Token token = nextToken();

        index = saveIndex;
        line = saveLine;
        column = saveColumn;

        return token;
    }

    public abstract Token nextToken();

    public String getSource(int start, int stop) {
        return text.substring(start, stop + 1);
    }

    public void reset() {
        index = column = line = 0;
    }

}
