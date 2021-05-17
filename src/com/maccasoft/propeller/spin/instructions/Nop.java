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

public class Nop extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, List<Spin2PAsmExpression> arguments, String effect) {
        if (arguments.size() == 0 && effect == null) {
            return new Nop_(context);
        }
        throw new RuntimeException("Invalid arguments");
    }

    public static class Nop_ extends Spin2InstructionObject {

        // 0000 0000000 000 000000000 000000000

        public Nop_(Spin2Context context) {
            super(context);
        }

        @Override
        public byte[] getBytes() {
            return getBytes(encode(0b0000000, 0b00, false, 0b00000000, 0b00000000));
        }

    }
}
