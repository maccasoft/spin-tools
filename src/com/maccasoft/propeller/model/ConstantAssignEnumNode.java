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

public class ConstantAssignEnumNode extends ConstantStatement {

    public ConstantAssignEnumNode(Node parent, List<Token> tokens) {
        super(parent);

        int i = 0;
        this.identifier = tokens.get(i++);

        if (i < tokens.size() && "[".equals(tokens.get(i).getText())) {
            this.multiplier = new ExpressionNode(this);
            this.multiplier.addAllTokens(tokens.subList(i + 1, tokens.size() - 1));
        }
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitConstantAssignEnum(this);
        super.accept(visitor);
    }

}