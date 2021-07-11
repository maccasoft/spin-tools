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

    public Constant(Spin2Context context, Expression expression) {
        super(context);
        this.expression = expression;
    }

    @Override
    public int getSize() {
        if (expression instanceof ContextLiteral) {
            if (!((ContextLiteral) expression).getContext().isAddressSet()) {
                return 5;
            }
        }
        else if (expression instanceof Identifier) {
            if (!((Identifier) expression).getContext().isAddressSet()) {
                return 5;
            }
        }

        return getBytes().length;
    }

    @Override
    public byte[] getBytes() {
        long value = expression.getNumber().longValue();

        if (value >= -1 && value <= 14) {
            return new byte[] {
                (byte) (0xA0 + value + 1)
            };
        }

        if ((value & 0xFFFFFF00L) == 0) {
            return new byte[] {
                0x45, (byte) value
            };
        }

        value &= 0xFFFFFFFFL;

        for (long i = 0, b = 1; i < 32; i++, b <<= 1) {
            if (value == b) {
                return new byte[] {
                    (byte) 0x4A,
                    (byte) i
                };
            }
            if (value == (b ^ 0xFFFFFFFFL)) {
                return new byte[] {
                    (byte) 0x4B,
                    (byte) i
                };
            }
        }

        for (long i = 31, b = 0xFFFFFFFFL; i >= 0; i--, b >>= 1) {
            if (value == b) {
                return new byte[] {
                    (byte) 0x4C,
                    (byte) i
                };
            }
            if (value == (b ^ 0xFFFFFFFFL)) {
                return new byte[] {
                    (byte) 0x4D,
                    (byte) i
                };
            }
        }

        if ((value & 0xFFFF0000L) == 0) {
            return new byte[] {
                0x47, (byte) value, (byte) (value >> 8)
            };
        }

        return new byte[] {
            0x49, (byte) value, (byte) (value >> 8), (byte) (value >> 16), (byte) (value >> 24)
        };

    }

    @Override
    public String toString() {
        if ((expression instanceof ContextLiteral) || (expression instanceof Identifier)) {
            return "CONSTANT (" + String.format("$%05X", expression.getNumber().intValue()) + ")";
        }
        return "CONSTANT (" + expression + ")";
    }

}