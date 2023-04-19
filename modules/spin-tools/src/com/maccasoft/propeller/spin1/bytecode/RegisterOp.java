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

import org.apache.commons.lang3.BitField;

import com.maccasoft.propeller.spin1.Spin1Bytecode;
import com.maccasoft.propeller.expressions.Context;

public class RegisterOp extends Spin1Bytecode {

    static final BitField op_oo = new BitField(0b0_11_00000);
    static final BitField op_xxxxx = new BitField(0b0_00_11111);

    public static enum Op {
        Read, Write, Assign
    };

    public Op oo;
    public int value;

    public RegisterOp(Context context, Op oo, int value) {
        super(context);
        this.oo = oo;
        this.value = value;
    }

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public byte[] getBytes() {
        int b1 = 0b1_00_00000;
        b1 = op_oo.setValue(b1, oo.ordinal());
        b1 = op_xxxxx.setValue(b1, value - 0x1E0);

        return new byte[] {
            (byte) 0b00111111,
            (byte) b1
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("REG_");
        switch (oo) {
            case Read:
                sb.append("READ");
                break;
            case Write:
                sb.append("WRITE");
                break;
            case Assign:
                sb.append("MODIFY");
                break;
        }
        sb.append(" ");
        sb.append(String.format("$%03X", value));
        return sb.toString();
    }

}
