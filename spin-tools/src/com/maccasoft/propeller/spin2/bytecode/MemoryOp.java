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
        Read, Write, Setup, Address, Field, WritePush
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
        this.index = index;
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
                        os.write(pop ? Spin2Bytecode.bc_setup_byte_pbase_pi : Spin2Bytecode.bc_setup_byte_pbase);
                    }
                    else if (ss == Size.Word) {
                        os.write(pop ? Spin2Bytecode.bc_setup_word_pbase_pi : Spin2Bytecode.bc_setup_word_pbase);
                    }
                    else {
                        os.write(pop ? Spin2Bytecode.bc_setup_long_pbase_pi : Spin2Bytecode.bc_setup_long_pbase);
                    }
                    break;
                case VBase:
                    if (ss == Size.Byte) {
                        os.write(pop ? Spin2Bytecode.bc_setup_byte_vbase_pi : Spin2Bytecode.bc_setup_byte_vbase);
                    }
                    else if (ss == Size.Word) {
                        os.write(pop ? Spin2Bytecode.bc_setup_word_vbase_pi : Spin2Bytecode.bc_setup_word_vbase);
                    }
                    else {
                        os.write(pop ? Spin2Bytecode.bc_setup_long_vbase_pi : Spin2Bytecode.bc_setup_long_vbase);
                    }
                    break;
                case DBase:
                    if (ss == Size.Byte) {
                        os.write(pop ? Spin2Bytecode.bc_setup_byte_dbase_pi : Spin2Bytecode.bc_setup_byte_dbase);
                    }
                    else if (ss == Size.Word) {
                        os.write(pop ? Spin2Bytecode.bc_setup_word_dbase_pi : Spin2Bytecode.bc_setup_word_dbase);
                    }
                    else {
                        os.write(pop ? Spin2Bytecode.bc_setup_long_dbase_pi : Spin2Bytecode.bc_setup_long_dbase);
                    }
                    break;
                case Pop:
                    if (ss == Size.Byte) {
                        os.write(pop ? Spin2Bytecode.bc_setup_byte_pb_pi : Spin2Bytecode.bc_setup_byte_pa);
                    }
                    else if (ss == Size.Word) {
                        os.write(pop ? Spin2Bytecode.bc_setup_word_pb_pi : Spin2Bytecode.bc_setup_word_pa);
                    }
                    else {
                        os.write(pop ? Spin2Bytecode.bc_setup_long_pb_pi : Spin2Bytecode.bc_setup_long_pa);
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
                os.write(Spin2Bytecode.bc_get_field);
            }
            else if (op == Op.Address) {
                os.write(Spin2Bytecode.bc_get_addr);
            }
            else if (op == Op.Read) {
                os.write(Spin2Bytecode.bc_read);
            }
            else if (op == Op.Write) {
                os.write(Spin2Bytecode.bc_write);
            }
            else if (op == Op.WritePush) {
                os.write(Spin2Bytecode.bc_write_push);
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
        else if (op == Op.Write || op == Op.WritePush) {
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
