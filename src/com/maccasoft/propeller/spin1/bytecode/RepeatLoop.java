/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1.bytecode;

import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.spin1.Spin1Context;

public class RepeatLoop extends VariableOp {

    public Expression expression;

    public Base b;
    public Op oo;
    boolean step;

    public RepeatLoop(Spin1Context context, Variable value, boolean step, Expression expression) {
        super(context, Op.Assign, value);
        this.step = step;
        this.expression = expression;
    }

    @Override
    public int getSize() {
        if (expression instanceof ContextLiteral) {
            if (!((ContextLiteral) expression).getContext().isAddressSet()) {
                return 4;
            }
        }
        else if (expression instanceof Identifier) {
            if (!((Identifier) expression).getContext().isAddressSet()) {
                return 4;
            }
        }
        int address = expression.getNumber().intValue();
        int value = address - context.getAddress() - 3;
        if (Math.abs(value) >= 0x40) {
            value--;
        }
        return Math.abs(value) < 0x40 ? 3 : 4;
    }

    @Override
    public byte[] getBytes() {
        byte[] b0 = super.getBytes();

        int address = expression.getNumber().intValue();
        int value = address - context.getAddress() - 3;
        if (Math.abs(value) < 0x40) {
            return new byte[] {
                b0[0],
                (byte) (step ? 0x06 : 0x02),
                (byte) (value & 0x7F)
            };
        }
        else {
            value--;
            return new byte[] {
                b0[0],
                (byte) (step ? 0x06 : 0x02),
                (byte) ((value & 0x7F) | 0x80),
                (byte) ((value >> 7) & 0x7F)
            };
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());

        int address = expression.getNumber().intValue();
        int value = address - (context.getAddress() + getSize());
        sb.append(" REPEAT-JMP");
        sb.append(String.format(" $%05X (%d)", address, value));

        return sb.toString();
    }

}
