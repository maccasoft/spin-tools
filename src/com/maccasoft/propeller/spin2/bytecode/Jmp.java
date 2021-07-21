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
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.spin2.Spin2Bytecode;
import com.maccasoft.propeller.spin2.Spin2Context;

public class Jmp extends Spin2Bytecode {

    int code;
    Expression expression;

    public Jmp(Spin2Context context, Expression expression) {
        super(context);
        this.code = 0x12;
        this.expression = expression;
    }

    protected Jmp(Spin2Context context, int code, Expression expression) {
        super(context);
        this.code = code;
        this.expression = expression;
    }

    @Override
    public int getSize() {
        if (expression instanceof ContextLiteral) {
            if (!((ContextLiteral) expression).getContext().isAddressSet()) {
                return 3;
            }
        }
        else if (expression instanceof Identifier) {
            if (!((Identifier) expression).getContext().isAddressSet()) {
                return 3;
            }
        }
        return getBytes().length;
    }

    @Override
    public byte[] getBytes() {
        int address = expression.getNumber().intValue();
        int value = address - (context.getAddress() + 1);
        byte[] v = Constant.wrVars(value);

        byte[] b = new byte[v.length + 1];
        b[0] = (byte) code;
        for (int i = 0; i < v.length; i++) {
            b[i + 1] = v[i];
        }

        return b;
    }

    public String toString(String prefix) {
        int address = expression.getNumber().intValue();
        int value = address - (context.getAddress() + 1);
        return prefix + String.format(" $%05X (%d)", address, value);
    }

    @Override
    public String toString() {
        return toString("JMP");
    }

}
