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

public class Log2 extends UnaryOperator {

    public Log2(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        if ((term.getNumber() instanceof Long)) {
            return (long) (Math.log(term.getNumber().longValue()) / Math.log(2.0));
        }
        return Math.log(term.getNumber().doubleValue()) / Math.log(2.0);
    }

    @Override
    public String getLexeme() {
        return "LOG2 ";
    }

}
