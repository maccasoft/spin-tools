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

public class DataVariable extends ContextLiteral {

    private final int size;

    public DataVariable(Context context, int size) {
        super(context);
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public int getAddress() {
        return context.getHubAddress();
    }
}
