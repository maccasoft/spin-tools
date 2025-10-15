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

public class LogicalAnd extends BinaryOperator {

    public LogicalAnd(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    protected Number internalGetNumber(Number term1, Number term2) {
        long value1 = term1.longValue();
        long value2 = term2.longValue();
        return (value1 != 0 && value2 != 0) ? -1 : 0;
    }

    @Override
    public String getLexeme() {
        return "and";
    }

}
