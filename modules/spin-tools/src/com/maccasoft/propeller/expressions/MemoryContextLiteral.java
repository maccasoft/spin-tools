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

public class MemoryContextLiteral extends Literal {

    private final Context context;
    private final String type;

    public MemoryContextLiteral(Context context) {
        this.context = context;
        this.type = "BYTE";
    }

    public MemoryContextLiteral(Context context, String type) {
        this.context = context;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public Number getNumber() {
        return Long.valueOf(context.getMemoryAddress());
    }

    @Override
    public String toString() {
        return Long.toString(getNumber().longValue());
    }

}
