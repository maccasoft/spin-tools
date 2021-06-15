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

public class VariableNode extends Node {

    public Token type;
    public Token identifier;
    public ExpressionNode size;

    public VariableNode(Node parent) {
        super(parent);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitVariable(this);
    }

    public Token getType() {
        return type;
    }

    public Token getIdentifier() {
        return identifier;
    }

    public ExpressionNode getSize() {
        return size;
    }

}