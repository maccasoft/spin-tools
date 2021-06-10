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

import java.util.List;

import com.maccasoft.propeller.spin.Spin2TokenStream.Token;

public class ConstantAssignNode extends Node {

    public Node identifier;
    public ExpressionNode expression;

    public ConstantAssignNode(Node parent, List<Token> childs) {
        super(parent);
        this.identifier = new Node(this);
        this.identifier.addToken(childs.get(0));
        if (this.parent != null) {
            this.parent.addToken(childs.get(1));
        }
        this.expression = new ExpressionNode(this);
        this.expression.addAllTokens(childs.subList(2, childs.size()));
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitConstantAssign(this);
        super.accept(visitor);
    }

    public Node getIdentifier() {
        return identifier;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

}