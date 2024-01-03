/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
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
    int minArguments;
    int arguments;
    int returns;

    public Method(String name, int arguments, int returns) {
        this.name = name;
        this.minArguments = arguments;
        this.arguments = arguments;
        this.returns = returns;
    }

    public Method(String name, int minArguments, int maxArguments, int returns) {
        this.name = name;
        this.minArguments = minArguments;
        this.arguments = maxArguments;
        this.returns = returns;
    }

    public String getName() {
        return name;
    }

    public int getMinArgumentsCount() {
        return minArguments;
    }

    public int getArgumentsCount() {
        return arguments;
    }

    public int getReturnsCount() {
        return returns;
    }

    public int getObjectIndex() {
        return -1;
    }

    public int getIndex() {
        return -1;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

}
