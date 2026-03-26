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

}
