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

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.spin1.Spin1BytecodeInstruction;
import com.maccasoft.propeller.spin1.Spin1Context;

public class Jmp extends Spin1BytecodeInstruction {

    public Expression expression;

    public Jmp(Spin1Context context, Expression expression) {
        super(context);
        this.expression = expression;
    }

    @Override
    public int getSize() {
        int value = expression.getNumber().intValue() - getContext().getAddress() - 2;
        if (Math.abs(value) >= 0x40) {
            value--;
        }
        return Math.abs(value) < 0x40 ? 2 : 3;
    }

    @Override
    public byte[] getBytes() {
        int value = expression.getNumber().intValue() - getContext().getAddress() - getSize();
        if (Math.abs(value) < 0x40) {
            return new byte[] {
                (byte) 0b00000100,
                (byte) (value & 0x7F)
            };
        }
        else {
            return new byte[] {
                (byte) 0b00000100,
                (byte) ((value & 0x7F) | 0x80),
                (byte) ((value >> 7) & 0x7F)
            };
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("JMP");

        int value = expression.getNumber().intValue() - getContext().getAddress() - getSize();
        sb.append(String.format(" %d", value));

        return sb.toString();
    }

}
