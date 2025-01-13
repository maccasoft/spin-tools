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

public class Scl extends BinaryOperator {

    public Scl(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        long value1 = term1.getNumber().longValue();
        long value2 = term1.getNumber().longValue();
        long a = (value1 >> 16) & 0xFFFF;
        long b = value1 & 0xFFFF;
        long c = (value2 >> 16) & 0xFFFF;
        long d = value2 & 0xFFFF;
        long x = a * d + c * b;
        long y = (((b * d) >> 16) & 0xFFFF) + x;
        long result = ((y >> 16) & 0xFFFF) + (a * c);
        return result & 0xFFFFFFFFL;
    }

    @Override
    public String getLexeme() {
        return "scl";
    }

}
