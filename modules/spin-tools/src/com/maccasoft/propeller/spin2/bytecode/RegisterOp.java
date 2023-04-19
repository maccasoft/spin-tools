/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
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

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.spin2.Spin2Bytecode;
import com.maccasoft.propeller.expressions.Context;

public class RegisterOp extends Spin2Bytecode {

    public static enum Op {
        Read, Write, Setup, Field
    }

    public Op op;
    public boolean indexed;
    public Expression expression;
    public int index;

    public RegisterOp(Context context, Op op, boolean indexed, Expression expression, int index) {
        super(context);
        this.op = op;
        this.indexed = indexed;
        this.expression = expression;
        this.index = index;
    }

    @Override
    public int getSize() {
        return getBytes().length;
    }

    @Override
    public byte[] getBytes() {
        int value = expression.getNumber().intValue() + index;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            if (!indexed && index == 0 && value >= 0x1D8 && value <= 0x1DF) {
                os.write(0xB0 + (value - 0x1D8));
            }
            else if (!indexed && index == 0 && value >= 0x1F8 && value <= 0x1FF) {
                os.write(0xB0 + (value - 0x1F8) + 8);
            }
            else {
                os.write(indexed ? 0x50 : 0x4F);
                os.write(Constant.wrVars(value >= 0x100 ? (value - 0x200) : value));
            }

            if (op == Op.Field) {
                os.write(0x7E);
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
        StringBuilder sb = new StringBuilder("REG_");
        if (op == Op.Read) {
            sb.append("READ");
        }
        else if (op == Op.Write) {
            sb.append("WRITE");
        }
        else if (op == Op.Setup) {
            sb.append("SETUP");
        }
        else if (op == Op.Field) {
            sb.append("BITFIELD_PTR");
        }

        if (indexed) {
            sb.append("_INDEXED");
        }

        int value = expression.getNumber().intValue() + index;
        sb.append(String.format(" +$%03X", value));

        if (!indexed && index == 0 && value >= 0x1D8 && value <= 0x1DF) {
            sb.append(" (short)");
        }
        else if (!indexed && index == 0 && value >= 0x1F8 && value <= 0x1FF) {
            sb.append(" (short)");
        }

        return sb.toString();
    }

}
