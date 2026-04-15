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

import com.maccasoft.propeller.CompilerException;

public abstract class BinaryOperator extends Expression {

    protected final Expression term1;
    protected final Expression term2;

    public abstract String getLexeme();

    public BinaryOperator(Expression term1, Expression term2) {
        this.term1 = term1;
        this.term2 = term2;
    }

    public Expression getTerm1() {
        return term1;
    }

    public Expression getTerm2() {
        return term2;
    }

    @Override
    public boolean isConstant() {
        boolean t1 = false, t2 = false;
        CompilerException errors = new CompilerException();

        try {
            t1 = term1.isConstant();
        } catch (CompilerException e) {
            errors.addMessage(e);
        } catch (Exception e) {
            errors.addMessage((new CompilerException(e, term1.getData())));
        }
        try {
            t2 = term2.isConstant();
        } catch (CompilerException e) {
            errors.addMessage(e);
        } catch (Exception e) {
            errors.addMessage((new CompilerException(e, term1.getData())));
        }
        if (errors.hasChilds()) {
            throw errors;
        }
        return t1 && t2;
    }

    @Override
    public final Number getNumber() {
        Number t1 = null, t2 = null;
        CompilerException errors = new CompilerException();

        try {
            t1 = term1.getNumber();
        } catch (CompilerException e) {
            errors.addMessage(e);
        } catch (Exception e) {
            errors.addMessage((new CompilerException(e, term1.getData())));
        }
        try {
            t2 = term2.getNumber();
        } catch (CompilerException e) {
            errors.addMessage(e);
        } catch (Exception e) {
            errors.addMessage((new CompilerException(e, term1.getData())));
        }
        if (errors.hasChilds()) {
            throw errors;
        }

        return internalGetNumber(t1, t2);
    }

    protected abstract Number internalGetNumber(Number term1, Number term2);

    @Override
    public boolean isNumber() {
        return term1.isNumber() && term2.isNumber();
    }

    @Override
    public boolean isString() {
        return term1.isString() || term2.isString();
    }

    @Override
    public String getString() {
        if (term1.isString()) {
            return term1.getString();
        }
        if (term2.isString()) {
            return term2.getString();
        }
        return super.getString();
    }

    @Override
    public String toString() {
        return term1 + " " + getLexeme() + " " + term2;
    }

}
