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

public class Pow extends BinaryOperator {

    public Pow(Expression multiplicand, Expression multiplier) {
        super(multiplicand, multiplier);
    }

    public Expression getMultiplicand() {
        return term1;
    }

    public Expression getMultiplier() {
        return term2;
    }

    @Override
    protected Number internalGetNumber(Number term1, Number term2) {
        if ((term1 instanceof Long) && (term2 instanceof Long)) {
            return (long) Math.pow(term1.longValue(), term2.longValue());
        }
        return Math.pow(term1.doubleValue(), term2.doubleValue());
    }

    @Override
    public String getLexeme() {
        return "POW";
    }

}
