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

public class ContextLiteral extends Literal {

    protected final Context context;

    public ContextLiteral(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public Number getNumber() {
        return Long.valueOf(context.getAddress());
    }

    @Override
    public String toString() {
        return Long.toString(getNumber().longValue());
    }

}
