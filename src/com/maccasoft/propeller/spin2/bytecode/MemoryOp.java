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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.spin2.Spin2Bytecode;
import com.maccasoft.propeller.spin2.Spin2Context;

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
    public boolean pop;
    public Expression expression;
    public int index;

    public MemoryOp(Spin2Context context, Size ss, Base bb, Op op, Expression expression) {
        super(context);
        this.ss = ss;
        this.base = bb;
        this.op = op;
        this.expression = expression;
    }

    public MemoryOp(Spin2Context context, Size ss, Base bb, Op op, boolean pop, Expression expression) {
        super(context);
        this.ss = ss;
        this.base = bb;
        this.op = op;
        this.pop = pop;
        this.expression = expression;
    }

    public MemoryOp(Spin2Context context, Size ss, Base bb, Op op, Expression expression, int index) {
        super(context);
        this.ss = ss;
        this.base = bb;
        this.op = op;
        this.expression = expression;
        if (this.ss == Size.Long) {
            this.index = index * 4;
        }
        else if (this.ss == Size.Word) {
            this.index = index * 2;
        }
        else {
            this.index = index;
        }
    }

    public MemoryOp(Spin2Context context, Size ss, Base bb, Op op, boolean pop, Expression expression, int index) {
        super(context);
        this.ss = ss;
        this.base = bb;
        this.op = op;
        this.pop = pop;
        this.expression = expression;
        if (this.ss == Size.Long) {
            this.index = index * 4;
        }
        else if (this.ss == Size.Word) {
            this.index = index * 2;
        }
        else {
            this.index = index;
        }
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
        return getBytes().length;
    }

    @Override
    public byte[] getBytes() {
        int offset;
        if (expression instanceof ContextLiteral) {
            offset = ((ContextLiteral) expression).getContext().getHubAddress();
        }
        else if (expression instanceof Variable) {
            offset = ((Variable) expression).getOffset();
        }
        else {
            offset = expression.getNumber().intValue();
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        switch (base) {
            case PBase:
                if (ss == Size.Byte) {
                    os.write(pop ? 0x53 : 0x50);
                }
                else if (ss == Size.Word) {
                    os.write(pop ? 0x59 : 0x56);
                }
                else {
                    os.write(pop ? 0x5F : 0x5C);
                }
                break;
            case VBase:
                if (ss == Size.Byte) {
                    os.write(pop ? 0x54 : 0x51);
                }
                else if (ss == Size.Word) {
                    os.write(pop ? 0x5A : 0x57);
                }
                else {
                    os.write(pop ? 0x60 : 0x5D);
                }
                break;
            case DBase:
                if (ss == Size.Byte) {
                    os.write(pop ? 0x55 : 0x52);
                }
                else if (ss == Size.Word) {
                    os.write(pop ? 0x5B : 0x58);
                }
                else {
                    os.write(pop ? 0x61 : 0x5E);
                }
                break;
        }

        try {
            os.write(Constant.wrVar(offset + index));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (op == Op.Address) {
            os.write(0x7F);
        }
        else if (op == Op.Read) {
            os.write(0x80);
        }
        else if (op == Op.Write) {
            os.write(0x81);
        }

        return os.toByteArray();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MEM_");

        if (op == Op.Read) {
            sb.append("READ");
        }
        else if (op == Op.Write) {
            sb.append("WRITE");
        }
        else if (op == Op.Setup) {
            sb.append("SETUP");
        }
        else if (op == Op.Address) {
            sb.append("ADDRESS");
        }

        if (op != Op.Address) {
            switch (ss) {
                case Byte:
                    sb.append(" BYTE");
                    break;
                case Word:
                    sb.append(" WORD");
                    break;
                case Long:
                    sb.append(" LONG");
                    break;
            }
        }

        if (pop) {
            sb.append(" INDEXED");
        }

        switch (base) {
            case PBase:
                sb.append(" PBASE");
                break;
            case VBase:
                sb.append(" VBASE");
                break;
            case DBase:
                sb.append(" DBASE");
                break;
        }

        int offset;
        if (expression instanceof ContextLiteral) {
            offset = ((ContextLiteral) expression).getContext().getHubAddress();
        }
        else if (expression instanceof Variable) {
            offset = ((Variable) expression).getOffset();
        }
        else {
            offset = expression.getNumber().intValue();
        }
        sb.append(String.format("+$%05X", offset + index));

        return sb.toString();
    }

}
