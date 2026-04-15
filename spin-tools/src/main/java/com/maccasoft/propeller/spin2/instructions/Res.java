/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.instructions;

import java.util.List;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin2.Spin2InstructionObject;
import com.maccasoft.propeller.spin2.Spin2PAsmExpression;
import com.maccasoft.propeller.spin2.Spin2PAsmInstructionFactory;

public class Res extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (arguments.size() == 0) {
            return new Empty_(context);
        }
        if (arguments.size() == 1) {
            return new Empty_(context, arguments.get(0));
        }
        throw new RuntimeException("expected 0 or 1 arguments, found " + arguments.size());
    }

    public static class Empty_ extends Spin2InstructionObject {

        Spin2PAsmExpression argument;

        public Empty_(Context context) {
            super(context);
        }

        public Empty_(Context context, Spin2PAsmExpression argument) {
            super(context);
            this.argument = argument;
        }

        @Override
        public int resolve(int address, boolean hubMode) {
            context.setAddress(hubMode ? address : address >> 2);
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
