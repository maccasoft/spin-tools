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
import com.maccasoft.propeller.spin1.Spin1ObjectCompiler;

public class Constant extends Spin1Bytecode {

    public Expression expression;

    public Constant(Spin1Context context, Expression expression) {
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
        if (expression.getNumber() instanceof Double) {
            int value = Float.floatToIntBits(expression.getNumber().floatValue());
            return new byte[] {
                0x37 + 4, (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value
            };
        }

        int value = expression.getNumber().intValue();

        if (value == -1 || value == 0 || value == 1) {
            return new byte[] {
                (byte) ((value + 1) | 0x34)
            };
        }

        if (Spin1ObjectCompiler.OPENSPIN_COMPATIBILITY || (value & 0xFFFFFF00) != 0) {
            for (int i = 0; i < 128; i++) {
                int testVal = 2;
                testVal <<= (i & 0x1F); // mask i, so that we only actually shift 0 to 31

                if ((i & 0x20) != 0) {// i in range 32 to 63 or 96 to 127
                    testVal--;
                }
                if ((i & 0x40) != 0) {// i in range 64 to 127
                    testVal = ~testVal;
                }

                if (testVal == value) {
                    return new byte[] {
                        0x37, (byte) i
                    };
                }
            }
        }

        if ((value & 0xFFFFFF00) == 0xFFFFFF00) {
            return new byte[] {
                0x37 + 1, (byte) (value ^ 0xFF), (byte) 0xE7
            };
        }

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
        return "CONSTANT (" + expression + ")";
    }

}
