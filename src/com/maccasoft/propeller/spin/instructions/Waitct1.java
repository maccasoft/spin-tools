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

public class Waitct1 extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, List<Spin2PAsmExpression> arguments) {
        if (arguments.size() == 0) {
            return new Waitct1_(context);
        }
        throw new RuntimeException("Invalid arguments");
    }

    public static class Waitct1_ extends Spin2InstructionObject {

        // EEEE 1101011 CZ0 000010001 000100100

        public Waitct1_(Spin2Context context) {
            super(context);
        }

        @Override
        public byte[] getBytes() {
            return getBytes(encode(0b1101011, false, false, false, 0b000010001, 0b000100100));
        }

    }
}
