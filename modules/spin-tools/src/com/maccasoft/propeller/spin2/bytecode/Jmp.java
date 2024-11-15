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
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.spin2.Spin2Bytecode;

public class Jmp extends Spin2Bytecode {

    int code;
    Expression expression;

    public Jmp(Context context, Expression expression) {
        super(context);
        this.code = Spin2Bytecode.bc_jmp;
        this.expression = expression;
    }

    protected Jmp(Context context, int code, Expression expression) {
        super(context);
        this.code = code;
        this.expression = expression;
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
        int target = expression.getNumber().intValue();
        int our = context.getAddress();
        int value = target - (our + 1);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            os.write(code);
            os.write(Constant.wrVars(value));
        } catch (IOException e) {
            // Do nothing
        }

        return os.toByteArray();
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
