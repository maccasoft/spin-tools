/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
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

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.spin2.Spin2Bytecode;

public class MemoryOp extends Spin2Bytecode {

    public enum Size {
        Byte, Word, Long
    };

    public static enum Base {
        PBase, VBase, DBase, Pop
    }

    public static enum Op {
        Read, Write, Setup, Address, Field
    }

    public Size ss;
    public Base base;
    public Op op;
    public boolean pop;
    public Expression expression;
    public int index;

    public MemoryOp(Context context, Size ss, Base bb, Op op) {
        super(context);
        this.ss = ss;
        this.base = bb;
        this.op = op;
    }

    public MemoryOp(Context context, Size ss, Base bb, Op op, Expression expression) {
        super(context);
        this.ss = ss;
        this.base = bb;
        this.op = op;
        this.expression = expression;
    }

    public MemoryOp(Context context, Size ss, Base bb, Op op, boolean pop) {
        super(context);
        this.ss = ss;
        this.base = bb;
        this.op = op;
        this.pop = pop;
    }

    public MemoryOp(Context context, Size ss, Base bb, Op op, boolean pop, Expression expression) {
        super(context);
        this.ss = ss;
        this.base = bb;
        this.op = op;
        this.pop = pop;
        this.expression = expression;
    }

    public MemoryOp(Context context, Size ss, Base bb, Op op, Expression expression, int index) {
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

    public MemoryOp(Context context, Size ss, Base bb, Op op, boolean pop, Expression expression, int index) {
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
        try {
            return getBytes().length;
        } catch (Exception e) {
            // Do nothing
        }
        return 5;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            switch (base) {
                case PBase:
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
                case VBase:
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
                case DBase:
                    if (ss == Size.Byte) {
                        os.write(pop ? 0x56 : 0x53);
                    }
                    else if (ss == Size.Word) {
                        os.write(pop ? 0x5C : 0x59);
                    }
                    else {
                        os.write(pop ? 0x62 : 0x5F);
                    }
                    break;
                case Pop:
                    if (ss == Size.Byte) {
                        os.write(pop ? 0x63 : 0x66);
                    }
                    else if (ss == Size.Word) {
                        os.write(pop ? 0x64 : 0x67);
                    }
                    else {
                        os.write(pop ? 0x65 : 0x68);
                    }
                    break;
            }

            if (base != Base.Pop) {
                int offset;
                if (expression instanceof ContextLiteral) {
                    offset = ((ContextLiteral) expression).getContext().getObjectAddress();
                }
                else if (expression instanceof Variable) {
                    offset = ((Variable) expression).getOffset();
                }
                else {
                    offset = expression.getNumber().intValue();
                }
                os.write(Constant.wrVar(offset + index));
            }

            if (op == Op.Field) {
                os.write(0x7E);
            }
            else if (op == Op.Address) {
                os.write(0x7F);
            }
            else if (op == Op.Read) {
                os.write(0x80);
            }
            else if (op == Op.Write) {
                os.write(0x81);
            }
        } catch (IOException e) {
            // Do nothing
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
        else if (op == Op.Field) {
            sb.append("BITFIELD_PTR");
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
            case Pop:
                break;
        }

        if (base != Base.Pop) {
            int offset;
            if (expression instanceof ContextLiteral) {
                offset = ((ContextLiteral) expression).getContext().getObjectAddress();
            }
            else if (expression instanceof Variable) {
                offset = ((Variable) expression).getOffset();
            }
            else {
                offset = expression.getNumber().intValue();
            }
            sb.append(String.format("+$%05X", offset + index));
        }

        return sb.toString();
    }

}
