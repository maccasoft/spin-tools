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
    int returnLongs;

    public Method(String name, int arguments, int returnLongs) {
        this.name = name;
        this.minArguments = arguments;
        this.arguments = arguments;
        this.returnLongs = returnLongs;
    }

    public Method(String name, int minArguments, int maxArguments, int returnLongs) {
        this.name = name;
        this.minArguments = minArguments;
        this.arguments = maxArguments;
        this.returnLongs = returnLongs;
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

    public int getReturnLongs() {
        return returnLongs;
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
