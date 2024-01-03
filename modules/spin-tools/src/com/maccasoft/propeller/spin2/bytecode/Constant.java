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
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.spin2.Spin2Bytecode;

public class Constant extends Spin2Bytecode {

    public static byte[] wrLong(long value) {
        return new byte[] {
            (byte) (value & 0xFF),
            (byte) ((value >> 8) & 0xFF),
            (byte) ((value >> 16) & 0xFF),
            (byte) ((value >> 24) & 0xFF)
        };
    }

    public static byte[] wrWord(long value) {
        return new byte[] {
            (byte) (value & 0xFF),
            (byte) ((value >> 8) & 0xFF)
        };
    }

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
            (byte) (value >> 21)
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
        if (value >= -64 && value < 64) {
            return new byte[] {
                (byte) (value & 0x7F)
            };
        }
        if (value >= -8291 && value < 8291) {
            return new byte[] {
                (byte) ((value & 0x7F) | 0x80),
                (byte) ((value >> 7) & 0x7F)
            };
        }
        if (value >= -1048576 && value < 1048576) {
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
            (byte) (value >> 21)
        };
    }

    public static int wrVarsSize(long value) {
        if (value >= -64 && value < 64) {
            return 1;
        }
        if (value >= -8291 && value < 8291) {
            return 2;
        }
        if (value >= -1048576 && value < 1048576) {
            return 3;
        }
        return 4;
    }

    public Expression expression;

    public Constant(Context context, Expression expression) {
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
                0x48, (byte) value, (byte) (value >> 8), (byte) (value >> 16), (byte) (value >> 24)
            };
        }

        long value = expression.getNumber().longValue();

        if (value >= -1 && value <= 14) {
            return new byte[] {
                (byte) (0xA0 + value + 1)
            };
        }

        if ((value & 0xFFFFFF00L) == 0) {
            return new byte[] {
                0x44, (byte) value
            };
        }
        if ((value & 0xFFFFFF00L) == 0xFFFFFF00L) {
            return new byte[] {
                0x45, (byte) (value ^ 0xFF)
            };
        }

        value &= 0xFFFFFFFFL;

        for (long i = 31, b = 0xFFFFFFFFL; i >= 0; i--, b >>= 1) {
            if (value == b) {
                return new byte[] {
                    (byte) 0x4B,
                    (byte) i
                };
            }
            if (value == (b ^ 0xFFFFFFFFL)) {
                return new byte[] {
                    (byte) 0x4C,
                    (byte) i
                };
            }
        }

        for (long i = 0, b = 1; i < 32; i++, b <<= 1) {
            if (value == b) {
                return new byte[] {
                    (byte) 0x49,
                    (byte) i
                };
            }
            if (value == (b ^ 0xFFFFFFFFL)) {
                return new byte[] {
                    (byte) 0x4A,
                    (byte) i
                };
            }
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
        if ((expression instanceof ContextLiteral)) {
            return "CONSTANT (" + String.format("$%05X", expression.getNumber().intValue()) + ")";
        }
        return "CONSTANT (" + expression + ")";
    }

}
