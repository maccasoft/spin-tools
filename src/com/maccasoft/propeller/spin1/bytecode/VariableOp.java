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

import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.spin1.Spin1Bytecode;
import com.maccasoft.propeller.spin1.Spin1Context;

public class VariableOp extends Spin1Bytecode {

    static final BitField vop_b = new BitField(0b00_1_000_00);
    static final BitField vop_oo = new BitField(0b00_0_000_11);
    static final BitField vop_xxx = new BitField(0b00_0_111_00);

    static final BitField mop_ss = new BitField(0b0_11_0_00_00);
    static final BitField mop_i = new BitField(0b0_00_1_00_00);
    static final BitField mop_bb = new BitField(0b0_00_0_11_00);
    static final BitField mop_oo = new BitField(0b0_00_0_00_11);

    public enum Size {
        Byte, Word, Long
    };

    public static enum Base {
        VBase, DBase
    };

    public static enum Op {
        Read, Write, Assign, Address
    };

    public Size ss;
    public Base b;
    public boolean i;
    public Op oo;
    public Variable value;

    public VariableOp(Spin1Context context, Op oo, Variable value) {
        this(context, oo, false, value);
    }

    public VariableOp(Spin1Context context, Op oo, boolean i, Variable value) {
        super(context);
        this.b = value instanceof LocalVariable ? Base.DBase : Base.VBase;
        this.i = i;
        this.oo = oo;
        this.value = value;

        if ("WORD".equalsIgnoreCase(value.getType())) {
            this.ss = Size.Word;
        }
        else if ("BYTE".equalsIgnoreCase(value.getType())) {
            this.ss = Size.Byte;
        }
        else {
            this.ss = Size.Long;
        }
    }

    @Override
    public int getSize() {
        if (ss == Size.Long && (value.getOffset() / 4) < 8 && !i) {
            return 1;
        }
        else {
            if (value.getOffset() < 127) {
                return 2;
            }
            else {
                return 3;
            }
        }
    }

    @Override
    public byte[] getBytes() {
        if (ss == Size.Long && (value.getOffset() / 4) < 8 && !i) {
            int b0 = 0b01_0_000_00;
            b0 = vop_b.setValue(b0, b.ordinal());
            b0 = vop_oo.setValue(b0, oo.ordinal());
            b0 = vop_xxx.setValue(b0, value.getOffset() / 4);

            return new byte[] {
                (byte) b0,
            };
        }
        else {
            int b0 = 0b1_00_0_00_00;
            b0 = mop_ss.setValue(b0, ss.ordinal());
            b0 = mop_i.setBoolean(b0, i);
            b0 = mop_bb.setValue(b0, b.ordinal() + 2);
            b0 = mop_oo.setValue(b0, oo.ordinal());

            if (value.getOffset() < 127) {
                return new byte[] {
                    (byte) b0,
                    (byte) value.getOffset(),
                };
            }
            else {
                return new byte[] {
                    (byte) b0,
                    (byte) (0x80 | (value.getOffset() >> 8)),
                    (byte) value.getOffset(),
                };
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("VAR_");
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
        if (i) {
            sb.append("_INDEXED");
        }
        sb.append(" ");
        switch (ss) {
            case Byte:
                sb.append("BYTE");
                break;
            case Word:
                sb.append("WORD");
                break;
            case Long:
                sb.append("LONG");
                break;
        }
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
        sb.append(String.format("$%04X", value.getOffset()));
        if (ss == Size.Long && (value.getOffset() / 4) < 8) {
            sb.append(" (short)");
        }
        return sb.toString();
    }

}
