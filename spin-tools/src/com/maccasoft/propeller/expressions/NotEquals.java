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

public class NotEquals extends BinaryOperator {

    public NotEquals(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    protected Number internalGetNumber(Number term1, Number term2) {
        return term1.equals(term2) ? 0 : -1;
    }

    @Override
    public String getLexeme() {
        return "<>";
    }

}
