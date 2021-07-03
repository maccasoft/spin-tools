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

public class Jz extends Jmp {

    public Jz(Spin2Context context, String label, Expression expression) {
        super(context, label, 0x13, expression);
    }

    @Override
    public String toString() {
        return toString("JZ");
    }

}
