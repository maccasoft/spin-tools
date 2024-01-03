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

public class IfElse extends Passthrough {

    private final Expression condition;
    private final Expression trueTerm;
    private final Expression falseTerm;

    public IfElse(Expression condition, Expression trueTerm, Expression falseTerm) {
        this.condition = condition;
        this.trueTerm = trueTerm;
        this.falseTerm = falseTerm;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getTrueTerm() {
        return trueTerm;
    }

    public Expression getFalseTerm() {
        return falseTerm;
    }

    public boolean isTrue() {
        return condition.getNumber().longValue() != 0;
    }

    @Override
    public Expression resolve() {
        return isTrue() ? trueTerm : falseTerm;
    }

    @Override
    public boolean isNumber() {
        return trueTerm.isNumber() && falseTerm.isNumber() || super.isNumber();
    }

    @Override
    public boolean isString() {
        return trueTerm.isString() && falseTerm.isString() || super.isString();
    }

    @Override
    public String toString() {
        return "" + condition + " ? " + trueTerm + " : " + falseTerm;
    }

}
