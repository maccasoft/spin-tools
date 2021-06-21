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
import com.maccasoft.propeller.spin1.Spin1BytecodeFactory;
import com.maccasoft.propeller.spin1.Spin1BytecodeObject;
import com.maccasoft.propeller.spin1.Spin1Context;

public class RegRead extends Spin1BytecodeFactory {

    @Override
    public Spin1BytecodeObject createObject(Spin1Context context, Expression expression) {
        return new RegRead_(context, expression);
    }

    public static class RegRead_ extends Spin1BytecodeObject {

        Expression expression;

        public RegRead_(Spin1Context context, Expression expression) {
            super(context);
            this.expression = expression;
        }

        @Override
        public int getSize() {
            return 2;
        }

        @Override
        public byte[] getBytes() {
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
