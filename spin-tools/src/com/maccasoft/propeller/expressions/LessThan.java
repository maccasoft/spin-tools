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

public class LessThan extends BinaryOperator {

    public LessThan(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        if (term1.getNumber() instanceof Long && term2.getNumber() instanceof Long) {
            return term1.getNumber().longValue() < term2.getNumber().longValue() ? -1 : 0;
        }
        return term1.getNumber().doubleValue() < term2.getNumber().doubleValue() ? -1 : 0;
    }

    @Override
    public String getLexeme() {
        return "<";
    }

}
