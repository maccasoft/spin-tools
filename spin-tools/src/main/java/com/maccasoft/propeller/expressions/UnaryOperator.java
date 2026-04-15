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

public abstract class UnaryOperator extends Expression {

    protected final Expression term;

    public abstract String getLexeme();

    public UnaryOperator(Expression term) {
        this.term = term;
    }

    public Expression getTerm() {
        return term;
    }

    @Override
    public boolean isConstant() {
        return term.isConstant();
    }

    @Override
    public boolean isNumber() {
        return term.isNumber();
    }

    @Override
    public String toString() {
        return getLexeme() + term;
    }

}
