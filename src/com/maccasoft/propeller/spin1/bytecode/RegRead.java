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
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionFactory;
import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionObject;
import com.maccasoft.propeller.spin1.Spin1BytecodeLine;
import com.maccasoft.propeller.spin1.Spin1Context;

public class RegRead extends Spin1BytecodeInstructionFactory {

    @Override
    public Spin1BytecodeInstructionObject createObject(Spin1BytecodeLine line) {
        if (line.getScope().hasSymbol(line.getMnemonic())) {
            Expression expression = line.getScope().getSymbol(line.getMnemonic());
            return new RegRead_(line.getScope(), expression);
        }
        return new RegRead_(line.getScope(), new NumberLiteral(line.getMnemonic()));
    }

    public static class RegRead_ extends Spin1BytecodeInstructionObject {

        Expression expression;

        public RegRead_(Spin1Context context, Expression expression) {
            super(context);
            this.expression = expression;
        }

        @Override
        public byte[] getBytes() {
            //                                                            ++-------- 00 read, 01 write, 10 assign
            //                                                            || +++++-- register offset from 0x1E0
            //                                                            || |||||
            return new byte[] {
                (byte) 0x3F,
                (byte) ((expression.getNumber().intValue() - 0x1E0) | 0b1_00_00000),
            };
        }

        @Override
        public String toString() {
            return String.format("REG_READ %03X", expression.getNumber().intValue());
        }

    }

}
