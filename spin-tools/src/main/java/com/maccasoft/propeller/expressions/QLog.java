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

public class QLog extends UnaryOperator {

    public QLog(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        long value = term.getNumber().longValue() & 0xFFFFFFFFL;
        return Double.valueOf(log2(value) * Math.pow(2, 27) + 0.5).longValue();
    }

    double log2(double v) {
        return Math.log(v) / Math.log(2);
    }

    @Override
    public String getLexeme() {
        return "QLOG ";
    }

}
