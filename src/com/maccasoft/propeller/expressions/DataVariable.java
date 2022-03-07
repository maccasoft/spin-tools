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

    private final String type;

    public DataVariable(Context context, String type) {
        super(context);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public long getAddress() {
        return context.getHubAddress();
    }
}
