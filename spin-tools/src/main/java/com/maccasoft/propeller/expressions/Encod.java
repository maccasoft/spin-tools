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

public class Encod extends UnaryOperator {

    boolean p2 = false;

    public Encod(Expression term, boolean p2) {
        super(term);
        this.p2 = p2;
    }

    @Override
    public Number getNumber() {
        long v = term.getNumber().longValue();
        for (long l = 31, b = 1L << 31; l >= 0; l--, b >>= 1) {
            if ((v & b) != 0) {
                return l;
            }
        }
        return 0;
    }

    @Override
    public String getLexeme() {
        return p2 ? "encod " : ">|";
    }

}
