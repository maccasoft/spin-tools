/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.expressions;

public abstract class Passthrough extends Expression {

    @Override
    public boolean isConstant() {
        return resolve().isConstant();
    }

    @Override
    public boolean isNumber() {
        return resolve().isNumber();
    }

    @Override
    public Number getNumber() {
        return resolve().getNumber();
    }

    @Override
    public boolean isString() {
        return resolve().isString();
    }

    @Override
    public String getString() {
        return resolve().getString();
    }

    @Override
    public boolean isGroup() {
        return resolve().isGroup();
    }

}
