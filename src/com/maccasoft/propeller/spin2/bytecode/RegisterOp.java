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

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.spin2.Spin2Bytecode;
import com.maccasoft.propeller.spin2.Spin2Context;

public class RegisterOp extends Spin2Bytecode {

    public static enum Op {
        Read, Write, Setup
    }

    public Op op;
    public boolean indexed;
    public Expression expression;
    public int index;

    public RegisterOp(Spin2Context context, Op op, boolean indexed, Expression expression, int index) {
        super(context);
        this.op = op;
        this.indexed = indexed;
        this.expression = expression;
        this.index = index;
    }

    @Override
    public int getSize() {
        int value = expression.getNumber().intValue() + index;
        if (!indexed && index == 0) {
            if (value >= 0x1D8 && value <= 0x1DF) {
                return 2;
            }
            else if (value >= 0x1F8 && value <= 0x1FF) {
                return 2;
            }
        }
        return Constant.wrVarsSize(value - 200) + 2;
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
                os.write(indexed ? 0x4F : 0x4E);
                os.write(Constant.wrVars(value - 0x200));
            }

            if (op == Op.Read) {
                os.write(0x80);
            }
            else if (op == Op.Write) {
                os.write(0x81);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
