/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

public class VariableNode extends Node {

    public Token modifier;
    public Token type;
    public Token identifier;
    public ExpressionNode size;

    public VariableNode(Node parent) {
        super(parent);
    }

    public VariableNode(Node parent, Token type, Token identifier) {
        super(parent);
        this.type = type;
        this.identifier = identifier;
        if (type != null) {
            tokens.add(type);
        }
        if (identifier != null) {
            tokens.add(identifier);
        }
    }

    public VariableNode(Node parent, Token modifier, Token type, Token identifier) {
        super(parent);
        this.modifier = modifier;
        this.type = type;
        this.identifier = identifier;
        if (modifier != null) {
            tokens.add(modifier);
        }
        if (type != null) {
            tokens.add(type);
        }
        if (identifier != null) {
            tokens.add(identifier);
        }
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitVariable(this);
    }

    public Token getModifier() {
        return modifier;
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