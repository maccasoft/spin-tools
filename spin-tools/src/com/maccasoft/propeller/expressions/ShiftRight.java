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

public class ShiftRight extends BinaryOperator {

    public ShiftRight(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        long value = term1.getNumber().longValue() & 0xFFFFFFFFL;
        return value >> (term2.getNumber().longValue() & 0x1F);
    }

    @Override
    public String getLexeme() {
        return ">>";
    }

}
