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

public class Ones extends UnaryOperator {

    public Ones(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        int count = 0;

        long value = term.getNumber().longValue();
        for (int i = 0, mask = 1; i < 32; i++, mask <<= 1) {
            if ((value & mask) != 0) {
                count++;
            }
        }

        return count;
    }

    @Override
    public String getLexeme() {
        return "ones";
    }

}
