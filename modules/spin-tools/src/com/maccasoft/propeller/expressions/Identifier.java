/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.expressions;

public class Identifier extends Passthrough {

    String name;
    Context context;

    public Identifier(String name, Context context) {
        this.name = name;
        this.context = context;
    }

    public String getName() {
        return name;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public Expression resolve() {
        return context.getSymbol(name);
    }

    @Override
    public String toString() {
        return name;
    }

}
