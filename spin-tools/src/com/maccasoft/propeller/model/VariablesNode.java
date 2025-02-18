/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

public class VariablesNode extends Node {

    public VariablesNode(Node parent) {
        super(parent);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        if (visitor.visitVariables(this)) {
            super.accept(visitor);
        }
    }

    @Override
    public String getPath() {
        StringBuilder sb = new StringBuilder();

        if (parent instanceof VariablesNode) {
            sb.append(parent.getPath());
            sb.append("/");
        }
        else {
            sb.append("/VAR");
        }
        sb.append(parent.indexOf(this));

        return sb.toString();
    }

}