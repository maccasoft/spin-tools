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

import com.maccasoft.propeller.CompilerException;

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
    protected Number internalGetNumber(Number term1, Number term2) {
        if ((term1 instanceof Long) && (term2 instanceof Long)) {
            if (term2.longValue() == 0) {
                throw new CompilerException("division by zero.", getDivisor().getData());
            }
            return term1.longValue() / term2.longValue();
        }
        if (term2.doubleValue() == 0) {
            throw new CompilerException("division by zero.", getDivisor().getData());
        }
        return term1.doubleValue() / term2.doubleValue();
    }

    @Override
    public String getLexeme() {
        return "/";
    }

}
