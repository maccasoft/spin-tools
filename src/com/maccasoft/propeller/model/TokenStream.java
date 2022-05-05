/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

import java.util.ArrayList;
import java.util.List;

public abstract class TokenStream {

    protected final Token EOF_TOKEN = new Token(null, 0, Token.EOF);

    protected final String text;

    protected int index = 0;
    protected int line = 0;
    protected int column = 0;

    protected boolean skipComments = true;

    protected List<Token> hiddenTokens = new ArrayList<Token>();

    public TokenStream(String text) {
        this.text = text;
        if (text == null) {
            throw new NullPointerException();
        }
    }

    public void skipComments(boolean skip) {
        this.skipComments = skip;
    }

    public Token peekNext() {
        return peekNext(skipComments);
    }

    public Token peekNext(boolean skipComments) {
        int saveIndex = index;
        int saveLine = line;
        int saveColumn = column;

        Token token = nextToken(skipComments);

        index = saveIndex;
        line = saveLine;
        column = saveColumn;

        return token;
    }

    public Token nextToken() {
        return nextToken(skipComments);
    }

    public abstract Token nextToken(boolean skipComments);

    public String getSource(int start, int stop) {
        return text.substring(start, stop + 1);
    }

    public List<Token> getHiddenTokens() {
        return hiddenTokens;
    }

    public void reset() {
        index = column = line = 0;
        hiddenTokens.clear();
    }

}
