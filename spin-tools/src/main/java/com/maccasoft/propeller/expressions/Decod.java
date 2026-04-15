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

public class Decod extends UnaryOperator {

    boolean p2 = false;

    public Decod(Expression term, boolean p2) {
        super(term);
        this.p2 = p2;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public Number getNumber() {
        return 1L << (term.getNumber().longValue() & 0x1F);
    }

    @Override
    public String getLexeme() {
        return p2 ? "decod " : "|<";
    }

}
