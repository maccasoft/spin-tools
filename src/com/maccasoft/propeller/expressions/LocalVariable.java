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

public class LocalVariable extends Literal {

    String name;
    int offset;

    public LocalVariable(String name, int offset) {
        this.name = name;
        this.offset = offset;
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
        return new Long(offset);
    }

    @Override
    public String toString() {
        return name;
    }

}
