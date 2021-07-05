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

import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.spin2.Spin2Bytecode;

public class MemoryOp extends Spin2Bytecode {

    public enum Size {
        Byte, Word, Long
    };

    public static enum Base {
        PBase, VBase, DBase
    }

    public static enum Op {
        Read, Write, Setup, Address
    }

    public Size ss;
    public Base base;
    public Op op;
    public Expression expression;

    public MemoryOp(Size ss, Base bb, Op op, Expression expression) {
        this.ss = ss;
        this.base = bb;
        this.op = op;
        this.expression = expression;
    }

    @Override
    public int getSize() {
        int value = expression.getNumber().intValue();
        if (expression instanceof ContextLiteral) {
            value = ((ContextLiteral) expression).getContext().getHubAddress();
        }
        return Constant.wrVarSize(value) + 2;
    }

    @Override
    public byte[] getBytes() {
        int value = expression.getNumber().intValue();
        if (expression instanceof ContextLiteral) {
            value = ((ContextLiteral) expression).getContext().getHubAddress();
        }

        byte[] v = Constant.wrVar(value);
        byte[] b = new byte[v.length + 2];

        b[0] = 0x50;
        if (base == Base.VBase) {
            b[0] = 0x51;
        }
        else if (base == Base.DBase) {
            b[0] = 0x52;
        }

        if (ss == Size.Word) {
            b[0] += 0x06;
        }
        else if (ss == Size.Long) {
            b[0] += 0x0C;
        }

        for (int i = 0; i < v.length; i++) {
            b[i + 1] = v[i];
        }
        if (op == Op.Address) {
            b[b.length - 1] = (byte) 0x7F;
        }
        else {
            b[b.length - 1] = (byte) (op == Op.Read ? 0x80 : 0x81);
        }

        return b;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MEM_");
        if (op == Op.Read) {
            sb.append("READ ");
        }
        else if (op == Op.Write) {
            sb.append("WRITE ");
        }
        else if (op == Op.Address) {
            sb.append("ADDRESS ");
        }

        if (base == Base.PBase) {
            sb.append("PBASE");
        }
        else if (base == Base.VBase) {
            sb.append("VBASE");
        }
        else if (base == Base.DBase) {
            sb.append("DBASE");
        }

        int value = expression.getNumber().intValue();
        if (expression instanceof ContextLiteral) {
            value = ((ContextLiteral) expression).getContext().getHubAddress();
        }
        sb.append(String.format("+$%05X", value));

        return sb.toString();
    }

}
