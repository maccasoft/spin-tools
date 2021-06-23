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

public class Identifier extends Spin1BytecodeInstructionFactory {

    @Override
    public Spin1BytecodeInstructionObject createObject(Spin1BytecodeLine line) {
        return new Identifier_(line.getScope(), new com.maccasoft.propeller.expressions.Identifier(line.getMnemonic(), line.getScope()));
    }

    class Identifier_ extends Spin1BytecodeInstructionObject {

        Expression expression;

        public Identifier_(Spin1Context context, Expression expression) {
            super(context);
            this.expression = expression;
        }

        @Override
        public byte[] getBytes() {
            return new byte[0];
        }

        @Override
        public String toString() {
            return "CONSTANT " + expression;
        }
    }

}
