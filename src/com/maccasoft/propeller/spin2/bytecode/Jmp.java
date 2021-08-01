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
