/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1.instructions;

import com.maccasoft.propeller.spin1.Spin1Context;
import com.maccasoft.propeller.spin1.Spin1InstructionObject;
import com.maccasoft.propeller.spin1.Spin1PAsmExpression;
import com.maccasoft.propeller.spin1.Spin1PAsmInstructionFactory;
import com.maccasoft.propeller.spin1.Spin1PAsmLine;

public class Fit extends Spin1PAsmInstructionFactory {

    @Override
    public Spin1InstructionObject createObject(Spin1PAsmLine line) {
        if (line.getArgumentCount() == 0) {
            return new Fit_(line.getScope());
        }
        if (line.getArgumentCount() == 1) {
            return new Fit_(line.getScope(), line.getArgument(0));
        }
        throw new RuntimeException("expected 0 or 1 arguments, found " + line.getArgumentCount());
    }

    public class Fit_ extends Spin1InstructionObject {

        Spin1PAsmExpression arg0;

        public Fit_(Spin1Context context) {
            super(context);
        }

        public Fit_(Spin1Context context, Spin1PAsmExpression arg0) {
            super(context);
            this.arg0 = arg0;
        }

        @Override
        public int resolve(int address, int memoryAddress) {
            context.setAddress(address >> 2);
            context.setMemoryAddress(memoryAddress);
            int limit = arg0 != null ? arg0.getInteger() : 0x1F0;
            if ((address >> 2) > limit) {
                throw new RuntimeException("error: fit limit exceeded by " + ((address >> 2) - limit) + " long(s)");
            }
            return address;
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
