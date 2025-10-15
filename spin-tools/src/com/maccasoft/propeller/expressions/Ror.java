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

public class Ror extends BinaryOperator {

    public Ror(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    protected Number internalGetNumber(Number term1, Number term2) {
        long value1 = term1.longValue() & 0xFFFFFFFFL;
        long value2 = term2.longValue() & 0x1FL;
        long result = ((value1 >> value2) & 0xFFFFFFFFL) | ((value1 << (32 - value2)) & 0xFFFFFFFFL);
        return result & 0xFFFFFFFFL;
    }

    @Override
    public String getLexeme() {
        return "ror";
    }

}
