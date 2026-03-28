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

public class SourceLine {

    int index;
    List<Token> tokens;

    public SourceLine(List<Token> tokens) {
        this.tokens = new ArrayList<>(tokens);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Token getNextToken() {
        if (index >= tokens.size()) {
            return null;
        }
        return tokens.get(index++);
    }

    public Token peekNextToken() {
        if (index >= tokens.size()) {
            return null;
        }
        return tokens.get(index);
    }

    public Token peekNextToken(int offset) {
        if (index + offset >= tokens.size()) {
            return null;
        }
        return tokens.get(index + offset);
    }

    public Token skipCommentsAndGetNextToken() {
        while (index < tokens.size()) {
            Token token = tokens.get(index);
            if (token.type == Token.NL) {
                return null;
            }
            index++;
            if (token.type != Token.COMMENT && token.type != Token.BLOCK_COMMENT && token.type != Token.NEXT_LINE) {
                return token;
            }
        }
        return null;
    }

    public Token skipCommentsAndPeekNextToken() {
        int idx = index;
        while (idx < tokens.size()) {
            Token token = tokens.get(idx++);
            if (token.type == Token.NL) {
                return null;
            }
            if (token.type != Token.COMMENT && token.type != Token.BLOCK_COMMENT && token.type != Token.NEXT_LINE) {
                return token;
            }
        }
        return null;
    }

    public boolean hasMoreTokens() {
        int idx = index;
        while (idx < tokens.size()) {
            Token token = tokens.get(idx++);
            if (token.type == Token.NL) {
                return false;
            }
            if (token.type != Token.COMMENT && token.type != Token.BLOCK_COMMENT && token.type != Token.NEXT_LINE) {
                return true;
            }
        }
        return false;
    }

    public String getText() {
        StringBuilder sb = new StringBuilder();

        int index = 0;
        for (Token token : tokens) {
            if (token.start > index) {
                sb.append(" ".repeat(token.start - index));
            }
            sb.append(token.getText());
            index = token.stop + 1;
        }
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    public Token getAsToken(int type) {
        Token firstToken = tokens.getFirst();
        Token lastToken = tokens.getLast();
        TokenStream stream = firstToken.getStream();
        String text = stream.getSource(firstToken.start - firstToken.column, lastToken.stop);
        return new Token(stream, firstToken.start - firstToken.column, firstToken.line, 0, type, text);
    }

}
