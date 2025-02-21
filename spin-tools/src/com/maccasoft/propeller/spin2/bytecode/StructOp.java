/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
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
import java.util.List;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.spin2.Spin2Bytecode;
import com.maccasoft.propeller.spin2.Spin2Struct;

public class StructOp extends Spin2Bytecode {

    MemoryOp.Op op;
    MemoryOp.Base base;
    MemoryOp.Size size;
    Spin2Struct struct;
    Expression expression;
    int offset;
    List<Integer> multipliers;
    boolean push;

    int packedOffset;

    public StructOp(Context context, MemoryOp.Op op, MemoryOp.Base base, MemoryOp.Size ss, Spin2Struct struct, Expression expression, int offset, List<Integer> multipliers, boolean push) {
        super(context);
        this.op = op;
        this.base = base;
        this.size = ss;
        this.struct = struct;
        this.expression = expression;
        this.offset = offset;
        this.multipliers = multipliers;
        this.push = push;
    }

    public Spin2Struct getStruct() {
        return struct;
    }

    @Override
    public int getSize() {
        return getBytes().length;
    }

    @Override
    public byte[] getBytes() {
        boolean pointer;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            if (expression instanceof Variable) {
                pointer = ((Variable) expression).isPointer();
            }
            else {
                pointer = false;
            }

            packedOffset = 0;

            if (pointer) {
                os.write(Spin2Bytecode.bc_setup_struct_pop);
            }
            else {
                if (base == MemoryOp.Base.Pop) {
                    os.write(Spin2Bytecode.bc_setup_struct_pop);
                }
                else if (base == MemoryOp.Base.VBase) {
                    os.write(Spin2Bytecode.bc_setup_struct_vbase);
                }
                else if (base == MemoryOp.Base.DBase) {
                    os.write(Spin2Bytecode.bc_setup_struct_dbase);
                }
                else if (base == MemoryOp.Base.PBase) {
                    os.write(Spin2Bytecode.bc_setup_struct_pbase);
                }

                if (expression instanceof ContextLiteral) {
                    packedOffset = ((ContextLiteral) expression).getContext().getObjectAddress();
                }
                else if (expression instanceof Variable) {
                    packedOffset = ((Variable) expression).getOffset();
                }
                else {
                    packedOffset = expression.getNumber().intValue();
                }
            }
            packedOffset = ((packedOffset + offset) << 4) | multipliers.size();
            if (struct == null) {
                packedOffset |= ((size.ordinal() + 1) << 2);
            }
            os.write(Constant.wrVar(packedOffset));

            for (Integer multiplier : multipliers) {
                os.write(Constant.wrVar(multiplier.intValue()));
            }
            if (op == MemoryOp.Op.Address) {
                os.write(Constant.wrVar(0));
            }

            if (struct == null) {
                if (op == MemoryOp.Op.Read) {
                    os.write(Spin2Bytecode.bc_read);
                }
                else if (op == MemoryOp.Op.Write) {
                    os.write(push ? Spin2Bytecode.bc_write_push : Spin2Bytecode.bc_write);
                }
            }
            else {
                if (op == MemoryOp.Op.Read) {
                    os.write(0x80 | struct.getTypeSize());
                }
                else if (op == MemoryOp.Op.Write) {
                    os.write(struct.getTypeSize());
                }
            }

        } catch (IOException e) {
            // Do nothing
        }
        return os.toByteArray();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("STRUCT_");

        if (op == MemoryOp.Op.Read) {
            sb.append("READ");
        }
        else if (op == MemoryOp.Op.Write) {
            sb.append("WRITE");
        }
        else if (op == MemoryOp.Op.Setup) {
            sb.append("SETUP");
        }
        else if (op == MemoryOp.Op.Address) {
            sb.append("ADDRESS");
        }

        switch (size) {
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
                sb.append(" POP");
                break;
        }
        sb.append(String.format("+$%05X (indexed)", packedOffset >> 4));

        if (op == MemoryOp.Op.Write && push) {
            sb.append(" (push)");
        }

        return sb.toString();
    }

}
