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

import com.maccasoft.propeller.spin.Spin2ModelVisitor;
import com.maccasoft.propeller.spin.Spin2TokenStream.Token;

public class ConstantSetEnumNode extends Node {

    public ExpressionNode start;
    public ExpressionNode step;

    public ConstantSetEnumNode(Node parent, List<Token> tokens) {
        super(parent);
        this.start = new ExpressionNode(this);

        int i = 1;
        while (i < tokens.size()) {
            Token token = tokens.get(i++);
            if ("[".equals(token.getText())) {
                break;
            }
            this.start.addToken(token);
        }
        if (i < tokens.size()) {
            this.step = new ExpressionNode(this);
            this.step.addAllTokens(tokens.subList(i, tokens.size() - 1));
        }
    }

    @Override
    public void accept(Spin2ModelVisitor visitor) {
        visitor.visitConstantSetEnum(this);
        super.accept(visitor);
    }

}