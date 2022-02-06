/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.instructions;

import java.util.List;

import com.maccasoft.propeller.spin2.Spin2Context;
import com.maccasoft.propeller.spin2.Spin2InstructionObject;
import com.maccasoft.propeller.spin2.Spin2PAsmExpression;
import com.maccasoft.propeller.spin2.Spin2PAsmInstructionFactory;

public class Fit extends Spin2PAsmInstructionFactory {

    int defaultLimit = 0x1F0;

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (arguments.size() == 0) {
            return new Fit_(context);
        }
        if (arguments.size() == 1) {
            return new Fit_(context, arguments.get(0));
        }
        throw new RuntimeException("Invalid arguments");
    }

    public void setDefaultLimit(int defaultLimit) {
        this.defaultLimit = defaultLimit;
    }

    public class Fit_ extends Spin2InstructionObject {

        Spin2PAsmExpression arg0;

        public Fit_(Spin2Context context) {
            super(context);
        }

        public Fit_(Spin2Context context, Spin2PAsmExpression arg0) {
            super(context);
            this.arg0 = arg0;
        }

        @Override
        public int resolve(int address, boolean hubMode) {
            context.setAddress(hubMode ? address : address >> 2);
            int limit = (arg0 != null ? arg0.getInteger() : defaultLimit) << 2;
            if (address > limit) {
                throw new RuntimeException("error: fit limit exceeded by " + ((address - limit) >> 2) + " long(s)");
            }
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
