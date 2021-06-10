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

import com.maccasoft.propeller.spin.Spin2Parser;
import com.maccasoft.propeller.spin.Spin2TokenStream.Token;

public class VariableNode extends Node {

    public Node type;
    public Node identifier;
    public ExpressionNode size;

    public VariableNode(Node parent, List<Token> tokens) {
        super(parent);

        int i = 0;
        if (Spin2Parser.types.contains(tokens.get(i).getText().toUpperCase())) {
            this.type = new Node(this);
            this.type.addToken(tokens.get(i));
            i++;
        }

        if (i < tokens.size()) {
            this.identifier = new Node(this);
            this.identifier.addToken(tokens.get(i++));
        }

        if (i < tokens.size() && "[".equals(tokens.get(i).getText())) {
            parent.addToken(tokens.get(i));
            this.size = new ExpressionNode(this);
            this.size.addAllTokens(tokens.subList(i + 1, tokens.size() - 1));
            parent.addToken(tokens.get(tokens.size() - 1));
        }

        this.tokens.addAll(tokens);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitVariable(this);
    }

    public Node getType() {
        return type;
    }

    public Node getIdentifier() {
        return identifier;
    }

    public ExpressionNode getSize() {
        return size;
    }

}