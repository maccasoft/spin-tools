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
import com.maccasoft.propeller.spin2.Spin2Bytecode;
import com.maccasoft.propeller.expressions.Context;

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
                0x44, (byte) value
            };
        }
        if ((value & 0xFFFF0000L) == 0) {
            return new byte[] {
                0x46, (byte) value, (byte) (value >> 8)
            };
        }
        if ((value & 0xFFFF0000L) == 0xFFFF0000L) {
            value ^= 0xFFFF;
            return new byte[] {
                0x47, (byte) value, (byte) (value >> 8)
            };
        }

        return new byte[] {
            0x48, (byte) value, (byte) (value >> 8), (byte) (value >> 16), (byte) (value >> 24)
        };
    }

    @Override
    public String toString() {
        return "ADDRESS (" + String.format("$%05X", expression.getNumber().intValue()) + ")";
    }

}
