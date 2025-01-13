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

public class SpinObject extends Expression {

    String name;
    int count;

    public SpinObject(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
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
