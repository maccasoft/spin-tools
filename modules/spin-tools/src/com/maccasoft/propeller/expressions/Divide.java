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

public class Divide extends BinaryOperator {

    public Divide(Expression dividend, Expression divisor) {
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
        Number divisor = term2.getNumber();
        if ((term1.getNumber() instanceof Long) && (divisor instanceof Long)) {
            if (divisor.longValue() == 0) {
                throw new EvaluationException("Division by zero.");
            }
            return term1.getNumber().longValue() / divisor.longValue();
        }
        if (divisor.doubleValue() == 0) {
            throw new EvaluationException("Division by zero.");
        }
        return term1.getNumber().doubleValue() / divisor.doubleValue();
    }

    @Override
    public String getLexeme() {
        return "/";
    }

}
