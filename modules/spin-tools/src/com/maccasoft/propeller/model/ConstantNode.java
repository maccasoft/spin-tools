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

public class ConstantNode extends Node {

    public Token identifier;
    public ExpressionNode start;
    public ExpressionNode step;
    public ExpressionNode expression;
    public ExpressionNode multiplier;

    public ConstantNode(Node parent) {
        super(parent);
    }

    public ConstantNode(Node parent, Token token) {
        super(parent);
        this.identifier = token;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        if (visitor.visitConstant(this)) {
            super.accept(visitor);
        }
    }

    public Token getIdentifier() {
        return identifier;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    public ExpressionNode getStart() {
        return start;
    }

    public ExpressionNode getStep() {
        return step;
    }

    public ExpressionNode getMultiplier() {
        return multiplier;
    }

}