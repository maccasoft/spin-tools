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

public class Log extends UnaryOperator {

    public Log(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        if ((term.getNumber() instanceof Long)) {
            return Double.valueOf(Math.log(term.getNumber().longValue())).longValue();
        }
        return Math.log(term.getNumber().doubleValue());
    }

    @Override
    public String getLexeme() {
        return "LOG ";
    }

}
