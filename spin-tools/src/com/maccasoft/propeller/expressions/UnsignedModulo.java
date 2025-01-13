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

public class UnsignedModulo extends BinaryOperator {

    public UnsignedModulo(Expression dividend, Expression divisor) {
        super(dividend, divisor);
    }

    public Expression getDividend() {
        return term1;
    }

    public Expression getDivisor() {
        return term2;
    }

    @Override
    public Number getNumber() {
        long dividend = term1.getNumber().longValue() & 0xFFFFFFFFL;
        long divisor = term2.getNumber().longValue() & 0xFFFFFFFFL;
        if (divisor == 0) {
            throw new EvaluationException("Division by zero.");
        }
        return dividend % divisor;
    }

    @Override
    public String getLexeme() {
        return "+//";
    }

}
