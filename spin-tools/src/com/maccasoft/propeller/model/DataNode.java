/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller.model;

public class DataNode extends Node {

    public Token name;

    public DataNode(Node parent) {
        super(parent);
    }

    public String getName() {
        return name != null ? name.getText() : null;
    }

    public void setName(Token name) {
        this.name = name;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        if (visitor.visitData(this)) {
            super.accept(visitor);
        }
    }

}