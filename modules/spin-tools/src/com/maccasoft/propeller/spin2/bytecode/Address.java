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

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.spin2.Spin2Bytecode;

public class Address extends Spin2Bytecode {

    public ContextLiteral expression;

    public Address(Context context, ContextLiteral expression) {
        super(context);
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
        int value = expression.getNumber().intValue();

        if ((value & 0xFFFFFF00L) == 0) {
            return new byte[] {
                Spin2Bytecode.bc_con_rfbyte, (byte) value
            };
        }
        if ((value & 0xFFFF0000L) == 0) {
            return new byte[] {
                Spin2Bytecode.bc_con_rfword, (byte) value, (byte) (value >> 8)
            };
        }
        if ((value & 0xFFFF0000L) == 0xFFFF0000L) {
            value ^= 0xFFFF;
            return new byte[] {
                Spin2Bytecode.bc_con_rfword_not, (byte) value, (byte) (value >> 8)
            };
        }

        return new byte[] {
            Spin2Bytecode.bc_con_rflong, (byte) value, (byte) (value >> 8), (byte) (value >> 16), (byte) (value >> 24)
        };
    }

    @Override
    public String toString() {
        return "ADDRESS (" + String.format("$%05X", expression.getNumber().intValue()) + ")";
    }

}
