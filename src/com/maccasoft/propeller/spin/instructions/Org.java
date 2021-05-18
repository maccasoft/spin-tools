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

public class Org extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (arguments.size() == 0) {
            return new Pins_(context, null, null);
        }
        if (arguments.size() == 1) {
            return new Pins_(context, arguments.get(0), null);
        }
        if (arguments.size() == 2) {
            return new Pins_(context, arguments.get(0), arguments.get(1));
        }
        throw new RuntimeException("Invalid arguments");
    }

    public class Pins_ extends Spin2InstructionObject {

        Spin2PAsmExpression arg0;
        Spin2PAsmExpression arg1;

        public Pins_(Spin2Context context, Spin2PAsmExpression arg0, Spin2PAsmExpression arg1) {
            super(context);
            this.arg0 = arg0;
            this.arg1 = arg1;
        }

        @Override
        public int resolve(int address) {
            return arg0 != null ? arg0.getInteger() : 0x000;
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
