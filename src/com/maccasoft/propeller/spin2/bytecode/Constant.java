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

public class Constant extends Spin2Bytecode {

    public static byte[] wrVar(long value) {
        value = Math.abs(value);
        if (value < 0x80) {
            return new byte[] {
                (byte) (value & 0x7F)
            };
        }
        if (value < 0x4000) {
            return new byte[] {
                (byte) ((value & 0x7F) | 0x80),
                (byte) ((value >> 7) & 0x7F)
            };
        }
        if (value < 0x200000) {
            return new byte[] {
                (byte) ((value & 0x7F) | 0x80),
                (byte) (((value >> 7) & 0x7F) | 0x80),
                (byte) ((value >> 14) & 0x7F)
            };
        }
        return new byte[] {
            (byte) ((value & 0x7F) | 0x80),
            (byte) (((value >> 7) & 0x7F) | 0x80),
            (byte) (((value >> 14) & 0x7F) | 0x80),
            (byte) ((value >> 21) & 0x7F)
        };
    }

    public static int wrVarSize(long value) {
        value = Math.abs(value);
        if (value < 0x80) {
            return 1;
        }
        if (value < 0x4000) {
            return 2;
        }
        if (value < 0x200000) {
            return 3;
        }
        return 4;
    }

    public static byte[] wrVars(long value) {
        if (Math.abs(value) < 0x40) {
            return new byte[] {
                (byte) (value & 0x7F)
            };
        }
        if (Math.abs(value) < 0x2000) {
            return new byte[] {
                (byte) ((value & 0x7F) | 0x80),
                (byte) ((value >> 7) & 0x7F)
            };
        }
        if (Math.abs(value) < 0x100000) {
            return new byte[] {
                (byte) ((value & 0x7F) | 0x80),
                (byte) (((value >> 7) & 0x7F) | 0x80),
                (byte) ((value >> 14) & 0x7F)
            };
        }
        return new byte[] {
            (byte) ((value & 0x7F) | 0x80),
            (byte) (((value >> 7) & 0x7F) | 0x80),
            (byte) (((value >> 14) & 0x7F) | 0x80),
            (byte) ((value >> 21) & 0x7F)
        };
    }

    public static int wrVarsSize(long value) {
        if (Math.abs(value) < 0x40) {
            return 1;
        }
        if (Math.abs(value) < 0x2000) {
            return 2;
        }
        if (Math.abs(value) < 0x100000) {
            return 3;
        }
        return 4;
    }

    public Expression expression;

    public Constant(Spin2Context context, String label, Expression expression) {
        super(context, label);
        this.expression = expression;
    }

    @Override
    public int getSize() {
        int value = expression.getNumber().intValue();

        if (value >= -1 && value <= 14) {
            return 1;
        }

        // 1 to 4 byte constant
        if ((value & 0xFFFF0000) != 0) {
            return 5;
        }
        else if ((value & 0x0000FF00) != 0) {
            return 3;
        }

        return 2;
    }

    @Override
    public byte[] getBytes() {
        int value = expression.getNumber().intValue();

        if (value >= -1 && value <= 14) {
            return new byte[] {
                (byte) (0xA0 + value + 1)
            };
        }

        // 1 to 4 byte constant
        if ((value & 0xFFFF0000) != 0) {
            return new byte[] {
                0x49, (byte) value, (byte) (value >> 8), (byte) (value >> 16), (byte) (value >> 24)
            };
        }
        else if ((value & 0x0000FF00) != 0) {
            return new byte[] {
                0x47, (byte) value, (byte) (value >> 8)
            };
        }

        return new byte[] {
            0x45, (byte) value
        };
    }

    @Override
    public String toString() {
        return "CONSTANT (" + expression + ")";
    }

}
