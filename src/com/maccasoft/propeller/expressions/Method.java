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

public class Method extends Expression {

    String name;
    int arguments;
    int returns;
    int object;
    int offset;

    public Method(String name, int arguments, int returns, int offset) {
        this.name = name;
        this.arguments = arguments;
        this.returns = returns;
        this.offset = offset;
    }

    public Method copy() {
        return new Method(name, arguments, returns, offset);
    }

    public int getArgumentsCount() {
        return arguments;
    }

    public int getReturnsCount() {
        return returns;
    }

    public int getObject() {
        return object;
    }

    public void setObject(int object) {
        this.object = object;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return name;
    }

}
