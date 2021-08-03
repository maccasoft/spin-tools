/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1.bytecode;

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.spin1.Spin1Bytecode;
import com.maccasoft.propeller.spin1.Spin1Context;

public class Address extends Spin1Bytecode {

    public Expression expression;

    public Address(Spin1Context context, Expression expression) {
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

        if ((value & 0xFF000000) != 0) {
            return new byte[] {
                0x37 + 4, (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value
            };
        }
        if ((value & 0x00FF0000) != 0) {
            return new byte[] {
                0x37 + 3, (byte) (value >> 16), (byte) (value >> 8), (byte) value
            };
        }
        if ((value & 0x0000FF00) != 0) {
            return new byte[] {
                0x37 + 2, (byte) (value >> 8), (byte) value
            };
        }

        return new byte[] {
            0x37 + 1, (byte) value
        };
    }

    @Override
    public String toString() {
        return String.format("ADDRESS ($%04X)", expression.getNumber().intValue());
    }

}
