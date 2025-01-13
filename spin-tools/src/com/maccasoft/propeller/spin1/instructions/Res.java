/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1.instructions;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin1.Spin1InstructionObject;
import com.maccasoft.propeller.spin1.Spin1PAsmExpression;
import com.maccasoft.propeller.spin1.Spin1PAsmInstructionFactory;
import com.maccasoft.propeller.spin1.Spin1PAsmLine;

public class Res extends Spin1PAsmInstructionFactory {

    @Override
    public Spin1InstructionObject createObject(Spin1PAsmLine line) {
        if (line.getArgumentCount() == 0) {
            return new Res_(line.getScope());
        }
        if (line.getArgumentCount() == 1) {
            return new Res_(line.getScope(), line.getArgument(0));
        }
        throw new RuntimeException("expected 0 or 1 arguments, found " + line.getArgumentCount());
    }

    public static class Res_ extends Spin1InstructionObject {

        Spin1PAsmExpression argument;

        public Res_(Context context) {
            super(context);
        }

        public Res_(Context context, Spin1PAsmExpression argument) {
            super(context);
            this.argument = argument;
        }

        @Override
        public int resolve(int address, int memoryAddress) {
            while ((address % 4) != 0) {
                address++;
            }
            context.setAddress(address >> 2);
            context.setMemoryAddress(memoryAddress);
            return address + ((argument != null ? argument.getInteger() : 1) << 2);
        }

        @Override
        public int getSize() {
            return 0;
        }

        @Override
        public byte[] getBytes() {
            return new byte[0];
        }

    }
}
