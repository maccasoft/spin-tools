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

public class LocalVariable extends Variable {

    Expression value;

    public LocalVariable(String type, String name, int size, int offset) {
        super(type, name, size, offset);
    }

    public LocalVariable(String type, String name, Expression value, int size, int offset) {
        super(type, name, size, offset);
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }

}
