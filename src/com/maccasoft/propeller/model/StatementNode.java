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

public class StatementNode extends Node {

    public StatementNode(Node parent) {
        super(parent);
    }

    @Override
    public void addToken(Token token) {
        tokens.add(token);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        if (visitor.visitStatement(this)) {
            super.accept(visitor);
        }
    }

    @Override
    public int getStartIndex() {
        return tokens.size() != 0 ? tokens.get(0).start - tokens.get(0).column : -1;
    }

}