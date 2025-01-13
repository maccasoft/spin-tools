/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller.model;

public class ConstantsNode extends Node {

    Token text;

    public ConstantsNode(Node parent) {
        super(parent);
    }

    public ConstantsNode(Node parent, Token text) {
        super(parent);
        this.text = text;
        addToken(text);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        if (visitor.visitConstants(this)) {
            super.accept(visitor);
        }
    }

    public Token getTextToken() {
        return text;
    }

}