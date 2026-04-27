/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1.bytecode;

import org.apache.commons.lang3.BitField;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.spin1.Spin1Bytecode;
import com.maccasoft.propeller.spin1.bytecode.Bytecode.Base;
import com.maccasoft.propeller.spin1.bytecode.Bytecode.Op;
import com.maccasoft.propeller.spin1.bytecode.Bytecode.Size;

public class VariableOp extends Spin1Bytecode {

    static final BitField vop_b = new BitField(0b00_1_000_00);
    static final BitField vop_oo = new BitField(0b00_0_000_11);
    static final BitField vop_xxx = new BitField(0b00_0_111_00);

    static final BitField mop_ss = new BitField(0b0_11_0_00_00);
    static final BitField mop_i = new BitField(0b0_00_1_00_00);
    static final BitField mop_bb = new BitField(0b0_00_0_11_00);
    static final BitField mop_oo = new BitField(0b0_00_0_00_11);

    public Size ss;
    public Base b;
    public boolean i;
    public Op oo;
    public Variable value;

    public VariableOp(Context context, Op oo, Variable value) {
        this(context, oo, false, value);
    }

    public VariableOp(Context context, Op oo, boolean i, Variable value) {
        super(context);
        this.b = value instanceof LocalVariable ? Bytecode.Base.DBase : Bytecode.Base.VBase;
        this.i = i;
        this.oo = oo;
        this.value = value;

        this.ss = Bytecode.Size.Long;
        if (!value.isPointer()) {
            if ("BYTE".equalsIgnoreCase(value.getType())) {
                this.ss = Bytecode.Size.Byte;
            }
            else if ("WORD".equalsIgnoreCase(value.getType())) {
                this.ss = Bytecode.Size.Word;
            }
        }
    }

    @Override
    public int getSize() {
        try {
            return getBytes().length;
        } catch (Exception e) {
            // Do nothing
        }
        return 3;
    }

    @Override
    public byte[] getBytes() {
        if (ss == Bytecode.Size.Long && (value.getOffset() / 4) < 8 && !i) {
            int b0 = 0b01_0_000_00;
            switch (b) {
                case VBase -> b0 = vop_b.setValue(b0, 0);
                case DBase -> b0 = vop_b.setValue(b0, 1);
            }
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
            switch (b) {
                case VBase -> b0 = mop_bb.setValue(b0, 2);
                case DBase -> b0 = mop_bb.setValue(b0, 3);
            }
            b0 = mop_oo.setValue(b0, oo.ordinal());

            if (value.getOffset() <= 127) {
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
        if (oo != Bytecode.Op.Address) {
            sb.append(" ");
            sb.append(value.getType().toUpperCase());
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
        if (ss == Bytecode.Size.Long && (value.getOffset() / 4) < 8) {
            sb.append(" (short)");
        }
        return sb.toString();
    }

}
