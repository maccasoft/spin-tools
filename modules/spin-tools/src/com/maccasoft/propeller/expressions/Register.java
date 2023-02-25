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

public class Register extends Literal {

    int address;

    public Register(int address) {
        this.address = address;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public Number getNumber() {
        return Long.valueOf(address);
    }

    @Override
    public String toString() {
        return String.format("$%03X", address);
    }

}
