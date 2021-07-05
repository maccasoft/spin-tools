/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.expressions;

public class Variable extends Expression {

    String type;
    String name;
    Expression size;
    int offset;

    public Variable(String type, String name, Expression size, int offset) {
        this.type = type;
        this.name = name;
        this.size = size;
        this.offset = offset;
    }

    public String getType() {
        return type;
    }

    public Expression getSize() {
        return size;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return name;
    }

}
