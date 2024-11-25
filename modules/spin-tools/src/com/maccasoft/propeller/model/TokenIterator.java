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

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class TokenIterator {

    int index;
    Token[] tokens;

    public TokenIterator(List<Token> list) {
        this.index = 0;
        this.tokens = list.toArray(new Token[0]);
    }

    public boolean hasNext() {
        int index = this.index;
        while (index < tokens.length) {
            if (tokens[index].type != Token.COMMENT && tokens[index].type != Token.BLOCK_COMMENT) {
                break;
            }
            index++;
        }
        return index < tokens.length;
    }

    public Token peekNext() {
        int index = this.index;
        while (index < tokens.length) {
            if (tokens[index].type != Token.COMMENT && tokens[index].type != Token.BLOCK_COMMENT) {
                break;
            }
            index++;
        }
        return tokens[index];
    }

    public Token next() {
        while (index < tokens.length) {
            if (tokens[index].type != Token.COMMENT && tokens[index].type != Token.BLOCK_COMMENT) {
                break;
            }
            index++;
        }
        return tokens[index++];
    }

    public void forEachRemaining(Consumer<Token> action) {
        Objects.requireNonNull(action);
        while (hasNext()) {
            action.accept(next());
        }
    }

}
