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

public class Exp2 extends UnaryOperator {

    public Exp2(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        return Math.pow(2.0, term.getNumber().doubleValue());
    }

    @Override
    public String getLexeme() {
        return "EXP2 ";
    }

}
