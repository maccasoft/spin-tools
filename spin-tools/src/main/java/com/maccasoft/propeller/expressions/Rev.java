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

public class Rev extends BinaryOperator {

    public Rev(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    protected Number internalGetNumber(Number term1, Number term2) {
        long result = 0;
        long value1 = term1.longValue();

        for (int i = 0; i <= term2.longValue(); i++) {
            result <<= 1;
            result |= (value1 & 0x01);
            value1 >>= 1;
        }

        return result & 0xFFFFFFFFL;
    }

    @Override
    public String getLexeme() {
        return "rev";
    }

}
