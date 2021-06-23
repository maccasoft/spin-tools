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

import java.util.Collections;
import java.util.List;

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionFactory;
import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionObject;
import com.maccasoft.propeller.spin1.Spin1BytecodeLine;
import com.maccasoft.propeller.spin1.Spin1Context;

public class Constant extends Spin1BytecodeInstructionFactory {

    public static byte[] compileConstant(int value) {

        if (value == -1 || value == 0 || value == 1) {
            return new byte[] {
                (byte) ((value + 1) | 0x34)
            };
        }

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

        if ((value & 0xFFFFFF00) == 0xFFFFFF00) {
            return new byte[] {
                0x38, (byte) ~(value & 0xFF), (byte) 0xE7
            };
        }
        else if ((value & 0xFFFF0000) == 0xFFFF0000) {
            return new byte[] {
                0x39, (byte) ~((value >> 8) & 0xFF), (byte) ~(value & 0xFF), (byte) 0xE7
            };
        }

        // 1 to 4 byte constant
        if ((value & 0xFF000000) != 0) {
            return new byte[] {
                0x37 + 4, (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value
            };
        }
        else if ((value & 0x00FF0000) != 0) {
            return new byte[] {
                0x37 + 3, (byte) (value >> 16), (byte) (value >> 8), (byte) value
            };
        }
        else if ((value & 0x0000FF00) != 0) {
            return new byte[] {
                0x37 + 2, (byte) (value >> 8), (byte) value
            };
        }
        else {
            return new byte[] {
                0x37 + 1, (byte) value
            };
        }

    }

    @Override
    public Spin1BytecodeInstructionObject createObject(Spin1BytecodeLine line) {
        try {
            return new Constant_(line.getScope(), new NumberLiteral(line.getMnemonic()));
        } catch (Exception e) {
            return new Constant_(line.getScope(), new com.maccasoft.propeller.expressions.Identifier(line.getMnemonic(), line.getScope()));
        }
    }

    @Override
    public List<Spin1BytecodeLine> expand(Spin1BytecodeLine line) {
        return Collections.singletonList(line);
    }

    class Constant_ extends Spin1BytecodeInstructionObject {

        Expression expression;

        public Constant_(Spin1Context context, Expression expression) {
            super(context);
            this.expression = expression;
        }

        @Override
        public byte[] getBytes() {
            int value = expression.getNumber().intValue();
            return compileConstant(value);
        }

        @Override
        public String toString() {
            return "CONSTANT " + expression;
        }
    }

}
