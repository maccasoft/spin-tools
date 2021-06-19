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

public class Fit extends Spin1PAsmInstructionFactory {

    @Override
    public Spin1InstructionObject createObject(Spin1Context context, String condition, List<Spin1PAsmExpression> arguments, String effect) {
        if (arguments.size() == 0) {
            return new Fit_(context);
        }
        if (arguments.size() == 1) {
            return new Fit_(context, arguments.get(0));
        }
        throw new RuntimeException("Invalid arguments");
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
        public int resolve(int address) {
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
