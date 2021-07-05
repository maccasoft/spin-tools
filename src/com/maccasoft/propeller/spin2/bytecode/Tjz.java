/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.bytecode;

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.spin2.Spin2Context;

public class Tjz extends Jmp {

    public Tjz(Spin2Context context, Expression expression) {
        super(context, 0x15, expression);
    }

    @Override
    public String toString() {
        return toString("TJZ");
    }

}
