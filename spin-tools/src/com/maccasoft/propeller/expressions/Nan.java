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

public class Nan extends Function {

    public Nan(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        return Double.isNaN(term.getNumber().doubleValue()) ? -1L : 0L;
    }

    @Override
    public String getLexeme() {
        return "nan";
    }

}
