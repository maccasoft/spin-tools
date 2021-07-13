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
import com.maccasoft.propeller.spin1.Spin1Context;

public class RegisterBitOp extends Spin1Bytecode {

    static final BitField op_oo = new BitField(0b0_11_00000);
    static final BitField op_xxxxx = new BitField(0b0_00_11111);

    public static enum Op {
        Read, Write, Assign
    };

    public Op oo;
    public int value;
    public int mathOp;

    public RegisterBitOp(Spin1Context context, Op oo, int value) {
        super(context);
        this.oo = oo;
        this.value = value;
    }

    public RegisterBitOp(Spin1Context context, Op oo, int value, int mathOp) {
        super(context);
        this.oo = oo;
        this.value = value;
        this.mathOp = mathOp;
    }

    @Override
    public int getSize() {
        return mathOp != 0 ? 3 : 2;
    }

    @Override
    public byte[] getBytes() {
        int b1 = 0b1_00_00000;
        b1 = op_oo.setValue(b1, oo.ordinal());
        b1 = op_xxxxx.setValue(b1, value - 0x1E0);

        if (mathOp != 0) {
            return new byte[] {
                0b00111101, (byte) b1, (byte) mathOp
            };
        }

        return new byte[] {
            0b00111101, (byte) b1
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("REGBIT_");
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

        if (mathOp != 0) {
            //sb.append(" ");
            //sb.append(VariableOp.mathText[mathOp & 0x1F]);
            //sb.append(" ");
            //sb.append("ASSIGN");
            //if ((mathOp & 0b100_00000) != 0) {
            //    sb.append(" (push)");
            //}
        }
        return sb.toString();
    }

}
