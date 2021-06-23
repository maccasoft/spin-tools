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

import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionFactory;
import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionObject;
import com.maccasoft.propeller.spin1.Spin1BytecodeLine;
import com.maccasoft.propeller.spin1.Spin1Context;
import com.maccasoft.propeller.spin1.bytecode.RegRead.RegRead_;

public class Cogid extends Spin1BytecodeInstructionFactory {

    @Override
    public Spin1BytecodeInstructionObject createObject(Spin1BytecodeLine line) {
        return new Cogid_(line.getScope());
    }

    public static class Cogid_ extends RegRead_ {

        public Cogid_(Spin1Context context) {
            super(context, new NumberLiteral(0x1E9));
        }

    }

}
