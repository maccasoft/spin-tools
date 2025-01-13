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

public class Orgh extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (arguments.size() == 0) {
            return new Orgh_(context, null, null);
        }
        if (arguments.size() == 1) {
            return new Orgh_(context, arguments.get(0), null);
        }
        if (arguments.size() == 2) {
            return new Orgh_(context, arguments.get(0), arguments.get(1));
        }
        throw new RuntimeException("Invalid arguments");
    }

    public class Orgh_ extends Spin2InstructionObject {

        Spin2PAsmExpression arg0;
        Spin2PAsmExpression arg1;

        public Orgh_(Context context, Spin2PAsmExpression arg0, Spin2PAsmExpression arg1) {
            super(context);
            this.arg0 = arg0;
            this.arg1 = arg1;
        }

        @Override
        public int resolve(int address, boolean hubMode) {
            context.setAddress(hubMode ? address : address >> 2);
            return arg0 != null ? arg0.getInteger() : address;
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
