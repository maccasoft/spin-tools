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

import java.util.List;

import com.maccasoft.propeller.spin1.Spin1Context;
import com.maccasoft.propeller.spin1.Spin1InstructionObject;
import com.maccasoft.propeller.spin1.Spin1PAsmExpression;
import com.maccasoft.propeller.spin1.Spin1PAsmInstructionFactory;
import com.maccasoft.propeller.spin1.Spin1PAsmSchema;

public class Nop extends Spin1PAsmInstructionFactory {

    @Override
    public Spin1InstructionObject createObject(Spin1Context context, String condition, List<Spin1PAsmExpression> arguments, String effect) {
        if (Spin1PAsmSchema.NONE.check(arguments, effect)) {
            return new Nop_(context);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * NOP
     */
    public class Nop_ extends Spin1InstructionObject {

        public Nop_(Spin1Context context) {
            super(context);
        }

        // 000000_0000_0000_000000000_000000000

        @Override
        public byte[] getBytes() {
            return getBytes(0);
        }

    }
}
