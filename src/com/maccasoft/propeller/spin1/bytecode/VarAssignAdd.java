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

public class VarAssignAdd extends Spin1BytecodeFactory {

    @Override
    public Spin1BytecodeObject createObject(Spin1Context context, Expression expression) {
        return new VarAssignAdd_(expression);
    }

    class VarAssignAdd_ extends Spin1BytecodeObject {

        Expression expression;

        public VarAssignAdd_(Expression expression) {
            this.expression = expression;
        }

        @Override
        public int getSize() {
            return 2;
        }

        @Override
        public byte[] getBytes() {
            int value = xxx.setValue(0b01_1_000_10, expression.getNumber().intValue() / 4);
            return new byte[] {
                (byte) value, (byte) 0b110_01100
            };
        }
    }
}
