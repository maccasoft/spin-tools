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

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.spin2.Spin2Bytecode;
import com.maccasoft.propeller.spin2.Spin2Context;

public class RegisterOp extends Spin2Bytecode {

    public static enum Op {
        Read, Write
    }

    public Op op;
    public Expression expression;

    public RegisterOp(Spin2Context context, Op op, Expression expression) {
        super(context);
        this.op = op;
        this.expression = expression;
    }

    @Override
    public int getSize() {
        int value = expression.getNumber().intValue() - 0x200;
        return Constant.wrVarsSize(value) + 2;
    }

    @Override
    public byte[] getBytes() {
        int value = expression.getNumber().intValue() - 0x200;

        byte[] c = Constant.wrVars(value);
        byte[] code = new byte[c.length + 2];

        int index = 0;
        code[index++] = (byte) 0x4E;
        for (int i = 0; i < c.length; i++) {
            code[index++] = c[i];
        }
        if (op == Op.Read) {
            code[index] = (byte) 0x80;
        }
        else if (op == Op.Write) {
            code[index] = (byte) 0x81;
        }

        return code;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("REG_");
        if (op == Op.Read) {
            sb.append("READ ");
        }
        else if (op == Op.Write) {
            sb.append("WRITE ");
        }

        int value = expression.getNumber().intValue();
        sb.append(String.format("+$%03X", value));

        return sb.toString();
    }

}
