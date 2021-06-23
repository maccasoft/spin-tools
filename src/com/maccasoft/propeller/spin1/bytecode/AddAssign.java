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
import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionFactory;
import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionObject;
import com.maccasoft.propeller.spin1.Spin1BytecodeLine;
import com.maccasoft.propeller.spin1.Spin1Context;

public class AddAssign extends Spin1BytecodeInstructionFactory {

    @Override
    public Spin1BytecodeInstructionObject createObject(Spin1BytecodeLine line) {
        return new AddAssign_(line.getScope(), new com.maccasoft.propeller.expressions.Identifier(line.getMnemonic(), line.getScope()));
    }

    class AddAssign_ extends Spin1BytecodeInstructionObject {

        Expression expression;

        public AddAssign_(Spin1Context context, Expression expression) {
            super(context);
            this.expression = expression;
        }

        @Override
        public byte[] getBytes() {
            int value = xxx.setValue(0b01_1_000_10, expression.getNumber().intValue() / 4);
            return new byte[] {
                (byte) value, (byte) 0b110_01100
            };
        }

        @Override
        public String toString() {
            return "ADD_ASSIGN";
        }
    }
}
