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

public class Group extends Passthrough {

    private final Expression term;

    public Group(Expression term) {
        this.term = term;
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
        return "(" + term + ")";
    }

}
