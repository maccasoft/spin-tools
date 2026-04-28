/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

public class ConstantNode extends Node {

    public Token identifier;
    public ExpressionNode start;
    public ExpressionNode step;
    public ExpressionNode expression;
    public ExpressionNode multiplier;
    public Node definition;

    public ConstantNode(Node parent) {
        super(parent);
    }

    public ConstantNode(Node parent, Token identifier) {
        super(parent);
        this.identifier = identifier;
        if (identifier != null) {
            tokens.add(identifier);
        }
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitConstant(this);
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

    @Override
    public String getPath() {
        StringBuilder sb = new StringBuilder();

        if (parent != null) {
            sb.append(parent.getPath());
        }
        sb.append("/");
        if (identifier != null) {
            sb.append(identifier.getText().toUpperCase());
        }
        else {
            sb.append(parent.indexOf(this));
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getClass().getSimpleName());
        if (identifier != null) {
            sb.append(" identifier=").append(identifier.getText());
        }
        sb.append(dumpTokens());

        return sb.toString();
    }

}
