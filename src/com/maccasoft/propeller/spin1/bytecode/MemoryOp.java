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

import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.spin1.Spin1BytecodeInstruction;
import com.maccasoft.propeller.spin1.Spin1Context;

public class MemoryOp extends Spin1BytecodeInstruction {

    static final BitField op_ss = new BitField(0b0_11_0_00_00);
    static final BitField op_i = new BitField(0b0_00_1_00_00);
    static final BitField op_bb = new BitField(0b0_00_0_11_00);
    static final BitField op_oo = new BitField(0b0_00_0_00_11);

    public enum Size {
        Byte, Word, Long
    };

    public enum Base {
        Pop, PBase, VBase, DBase
    };

    public enum Op {
        Read, Write, Assign, Address
    };

    public Size ss;
    public boolean i;
    public Base bb;
    public Op oo;
    public Expression expression;

    public MemoryOp(Spin1Context context, Size ss, boolean i, Base bb, Op oo, Expression expression) {
        super(context);
        this.ss = ss;
        this.i = i;
        this.bb = bb;
        this.oo = oo;
        this.expression = expression;
    }

    @Override
    public int getSize() {
        int value = expression.getNumber().intValue();
        if (bb == Base.PBase && (expression instanceof ContextLiteral)) {
            value = ((ContextLiteral) expression).getContext().getHubAddress();
        }
        return value < 127 ? 2 : 3;
    }

    @Override
    public byte[] getBytes() {
        int b0 = 0b1_00_0_00_00;
        b0 = op_ss.setValue(b0, ss.ordinal());
        b0 = op_i.setBoolean(b0, i);
        b0 = op_bb.setValue(b0, bb.ordinal());
        b0 = op_oo.setValue(b0, oo.ordinal());

        if (bb == Base.Pop) {
            return new byte[] {
                (byte) b0,
            };
        }

        int value = expression.getNumber().intValue();
        if (bb == Base.PBase && (expression instanceof ContextLiteral)) {
            value = ((ContextLiteral) expression).getContext().getHubAddress();
        }
        if (value < 127) {
            return new byte[] {
                (byte) b0,
                (byte) value,
            };
        }
        else {
            return new byte[] {
                (byte) b0,
                (byte) (0x80 | (value >> 8)),
                (byte) value,
            };
        }
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
                sb.append("ASSIGN");
                break;
            case Address:
                sb.append("ADDRESS");
                break;
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
        switch (bb) {
            case Pop:
                sb.append("POP");
                break;
            case PBase:
                sb.append("PBASE");
                break;
            case VBase:
                sb.append("VBASE");
                break;
            case DBase:
                sb.append("DBASE");
                break;
        }
        if (bb != Base.Pop) {
            sb.append("+");
            int value = expression.getNumber().intValue();
            if (bb == Base.PBase && (expression instanceof ContextLiteral)) {
                value = ((ContextLiteral) expression).getContext().getHubAddress();
            }
            sb.append(String.format("$%04X", value));
        }
        return sb.toString();
    }

}
