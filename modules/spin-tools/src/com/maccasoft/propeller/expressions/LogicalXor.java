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

public class LogicalXor extends BinaryOperator {

    public LogicalXor(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        long value1 = term1.getNumber().longValue();
        long value2 = term2.getNumber().longValue();
        return (value1 != 0 && value2 == 0) || (value1 == 0 && value2 != 0) ? -1 : 0;
    }

    @Override
    public String getLexeme() {
        return "xor";
    }

}
