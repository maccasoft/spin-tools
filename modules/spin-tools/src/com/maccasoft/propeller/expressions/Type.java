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

public class Type extends Passthrough {

    private final String type;
    private final Expression term;

    public Type(String type, Expression term) {
        this.type = type;
        this.term = term;
    }

    public String getType() {
        return type;
    }

    public Expression getTerm() {
        return term;
    }

    @Override
    public Expression resolve() {
        return term;
    }

    @Override
    public boolean isGroup() {
        return true;
    }

    @Override
    public String toString() {
        return type + " " + term;
    }

}
