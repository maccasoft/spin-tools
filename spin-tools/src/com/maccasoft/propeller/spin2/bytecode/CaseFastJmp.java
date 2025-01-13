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

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.spin2.Spin2Bytecode;
import com.maccasoft.propeller.expressions.Context;

public class CaseFastJmp extends Spin2Bytecode {

    Context ref;
    Expression expression;

    public CaseFastJmp(Context context, Expression expression) {
        super(context);
        this.ref = context;
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
        int our = ref.getAddress();
        int value = target - (our + 2);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            os.write(Constant.wrWord(value));
        } catch (IOException e) {
            // Do nothing
        }

        return os.toByteArray();
    }

    public String toString(String prefix) {
        int address = expression.getNumber().intValue();
        int value = address - (ref.getAddress() + 2);
        if (value > 65535) {
            throw new RuntimeException("block exceeds 64KB");
        }
        return prefix + String.format(" $%05X (%d)", address, value) + String.format(" <$%05X>", ref.getAddress());
    }

    @Override
    public String toString() {
        return toString("CASE_FAST_JMP");
    }

}
