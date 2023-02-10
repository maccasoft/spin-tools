/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.expressions;

public class RegisterAddress extends DataVariable {

    public RegisterAddress(Context context, String type) {
        super(context, type);
    }

    @Override
    public boolean isConstant() {
        return true;
    }
}
