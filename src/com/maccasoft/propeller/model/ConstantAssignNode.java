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

import java.util.List;

public class ConstantAssignNode extends ConstantStatement {

    public ConstantAssignNode(Node parent, Token identifier) {
        super(parent);
        this.identifier = identifier;
    }

    public ConstantAssignNode(Node parent, List<Token> childs) {
        super(parent);
        this.identifier = childs.get(0);
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

}