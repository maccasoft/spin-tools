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
import com.maccasoft.propeller.spin2.Spin2Bytecode;
import com.maccasoft.propeller.spin2.Spin2Context;

public class Jmp extends Spin2Bytecode {

    Expression expression;

    public Jmp(Spin2Context context, String label, Expression expression) {
        super(context, label);
        this.expression = expression;
    }

    @Override
    public byte[] getBytes() {
        int address = expression.getNumber().intValue();
        int value = address - (getContext().getAddress() + 1);
        byte[] v = Constant.wrVars(value);

        byte[] b = new byte[v.length + 1];
        b[0] = 0x12;
        for (int i = 0; i < v.length; i++) {
            b[i + 1] = v[i];
        }

        return b;
    }

    @Override
    public String toString() {
        int address = expression.getNumber().intValue();
        int value = address - (getContext().getAddress() + 1);
        return "JMP " + String.format("$%04X", expression.getNumber().intValue()) + " (" + String.valueOf(value) + ")";
    }

}
