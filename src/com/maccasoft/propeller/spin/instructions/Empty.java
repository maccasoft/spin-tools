/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin.instructions;

import java.util.List;

import com.maccasoft.propeller.spin.Spin2Context;
import com.maccasoft.propeller.spin.Spin2InstructionObject;
import com.maccasoft.propeller.spin.Spin2PAsmExpression;
import com.maccasoft.propeller.spin.Spin2PAsmInstructionFactory;

public class Empty extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, List<Spin2PAsmExpression> arguments) {
        return new Empty_(context);
    }

    public static class Empty_ extends Spin2InstructionObject {

        public Empty_(Spin2Context context) {
            super(context);
        }

        @Override
        public int resolve(int address) {
            return address;
        }

        @Override
        public byte[] getBytes() {
            return new byte[0];
        }

    }
}
