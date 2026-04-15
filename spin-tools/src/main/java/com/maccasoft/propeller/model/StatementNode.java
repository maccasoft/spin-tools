/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

public class StatementNode extends Node {

    public StatementNode(Node parent) {
        super(parent);
    }

    public StatementNode(Node parent, Token start) {
        super(parent);
        addToken(start);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        if (visitor.visitStatement(this)) {
            super.accept(visitor);
        }
    }

    @Override
    public int getStartIndex() {
        return !tokens.isEmpty() ? tokens.getFirst().start - tokens.getFirst().column : -1;
    }

    public String getText() {
        if (tokens.isEmpty()) {
            return "";
        }
        if (tokens.size() == 1 && tokens.getFirst().type == Token.EOF) {
            return "<EOF>";
        }
        int s = tokens.getFirst().start - tokens.getFirst().column;
        int e = tokens.getLast().stop;
        TokenStream stream = getStartToken().getStream();
        return stream.getSource(s, e);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getClass().getSimpleName());
        sb.append(dumpTokens());

        return sb.toString();
    }

}
