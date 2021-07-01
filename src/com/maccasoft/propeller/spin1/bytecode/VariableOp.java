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

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.spin1.Spin1BytecodeInstruction;
import com.maccasoft.propeller.spin1.Spin1Context;

public class VariableOp extends Spin1BytecodeInstruction {

    static final BitField op_b = new BitField(0b00_1_000_00);
    static final BitField op_oo = new BitField(0b00_0_000_11);
    static final BitField op_xxx = new BitField(0b00_0_111_00);

    public static enum Base {
        VBase, DBase
    };

    public static enum Op {
        Read, Write, Assign, Address
    };

    static String[] mathText = new String[] {
        "ROTATE_RIGHT",
        "ROTATE_LEFT",
        "SHIFT_RIGHT",
        "SHIFT_LEFT",
        "LIMIT_MINIMUM",
        "LIMIT_MAXIMUM",
        "NEGATE",
        "BITNOT",
        "BITAND",
        "ABS",
        "BITOR",
        "BITXOR",
        "ADD",
        "SUBTRACT",
        "SAR",
        "REV",
        "BOOLEAN_AND",
        "ENCODE",
        "BOOLEAN_OR",
        "DECODE",
        "MULTIPLY",
        "MULTIPLY_UPPER",
        "DIVIDE",
        "REMAINDER",
        "SQR",
        "TEST_BELOW",
        "TEST_ABOVE",
        "TEST_NOT_EQUAL",
        "TEST_EQUAL",
        "TEST_BELOW_OR_EQUAL",
        "TEST_ABOVE_OR_EQUAL",
        "BOOLEAN_NOT"
    };

    public Base b;
    public Op oo;
    public Expression value;
    public int mathOp;

    VariableOp(Spin1Context context) {
        super(context);
        b = Base.DBase;
        oo = Op.Read;
    }

    public VariableOp(Spin1Context context, Base b, Op oo, Expression value) {
        super(context);
        this.b = b;
        this.oo = oo;
        this.value = value;
    }

    public VariableOp(Spin1Context context, Base b, Op oo, Expression value, int mathOp) {
        super(context);
        this.b = b;
        this.oo = oo;
        this.value = value;
        this.mathOp = mathOp;
    }

    @Override
    public int getSize() {
        return oo == Op.Assign ? 2 : 1;
    }

    @Override
    public byte[] getBytes() {
        int b0 = 0b01_0_000_00;
        b0 = op_b.setValue(b0, b.ordinal());
        b0 = op_oo.setValue(b0, oo.ordinal());
        b0 = op_xxx.setValue(b0, value.getNumber().intValue() / 4);

        if (oo == Op.Assign) {
            return new byte[] {
                (byte) b0,
                (byte) mathOp
            };
        }

        return new byte[] {
            (byte) b0,
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MEM_");
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
            case Address:
                sb.append("ADDRESS");
                break;
        }
        sb.append(" ");
        sb.append("LONG");
        sb.append(" ");
        switch (b) {
            case VBase:
                sb.append("VBASE");
                break;
            case DBase:
                sb.append("DBASE");
                break;
            default:
                throw new RuntimeException("Invalid base");
        }
        sb.append("+");
        sb.append(String.format("$%04X", value.getNumber().intValue()));
        sb.append(" (short)");

        if (oo == Op.Assign) {
            sb.append(" ");
            sb.append(mathText[mathOp & 0x1F]);
            sb.append(" ");
            sb.append("ASSIGN");
            if ((mathOp & 0b100_00000) != 0) {
                sb.append(" (push)");
            }
        }
        return sb.toString();
    }

}
