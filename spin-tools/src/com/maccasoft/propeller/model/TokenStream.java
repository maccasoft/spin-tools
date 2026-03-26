/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

import java.util.ArrayList;
import java.util.List;

public abstract class TokenStream {

    public static class Position {

        final int index;
        final int line;
        final int column;

        public Position(TokenStream stream) {
            this.index = stream.index;
            this.line = stream.line;
            this.column = stream.column;
        }

        public void restore(TokenStream stream) {
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

    public SourceLine[] parseSourceLines() {
        Token token;
        List<SourceLine> sourceLines = new ArrayList<>();
        List<Token> tokens = new ArrayList<>();

        boolean allComments = true;
        while ((token = nextToken()).type != Token.EOF) {
            tokens.add(token);
            if (token.type == Token.NL) {
                if (allComments && !sourceLines.isEmpty()) {
                    sourceLines.getLast().tokens.addAll(tokens);
                }
                else {
                    sourceLines.add(new SourceLine(tokens));
                }
                tokens.clear();
                allComments = true;
            }
            else if (token.type != Token.COMMENT && token.type != Token.BLOCK_COMMENT && token.type != Token.NEXT_LINE) {
                allComments = false;
            }
        }

        if (!tokens.isEmpty()) {
            if (allComments && !sourceLines.isEmpty()) {
                sourceLines.getLast().tokens.addAll(tokens);
            }
            else {
                sourceLines.add(new SourceLine(tokens));
            }
        }

        return sourceLines.toArray(new SourceLine[0]);
    }

    public Token peekNext() {
        Position pos = mark();
        try {
            return nextToken();
        } finally {
            restore(pos);
        }
    }

    public abstract Token nextToken();

    public String getSource(int start, int stop) {
        return text.substring(start, stop + 1);
    }

    public void reset() {
        index = column = line = 0;
    }

    public Position mark() {
        return new Position(this);
    }

    public void restore(Position position) {
        position.restore(this);
    }

}
