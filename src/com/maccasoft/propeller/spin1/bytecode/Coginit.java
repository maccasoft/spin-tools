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

import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionFactory;
import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionObject;
import com.maccasoft.propeller.spin1.Spin1BytecodeLine;
import com.maccasoft.propeller.spin1.Spin1Context;

public class Coginit extends Spin1BytecodeInstructionFactory {

    @Override
    public Spin1BytecodeInstructionObject createObject(Spin1BytecodeLine line) {
        if (line.getArgumentCount() == 3) {
            return new Coginit_(line.getScope());
        }
        throw new RuntimeException("error: expected 3 arguments, found " + line.getArgumentCount());
    }

    class Coginit_ extends Spin1BytecodeInstructionObject {

        public Coginit_(Spin1Context context) {
            super(context);
        }

        @Override
        public byte[] getBytes() {
            return new byte[] {
                (byte) 0x2C,
            };
        }

        @Override
        public String toString() {
            return "COGINIT";
        }

    }

}
