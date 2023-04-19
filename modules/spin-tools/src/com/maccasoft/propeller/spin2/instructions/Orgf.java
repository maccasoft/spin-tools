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

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin2.Spin2InstructionObject;
import com.maccasoft.propeller.spin2.Spin2PAsmExpression;
import com.maccasoft.propeller.spin2.Spin2PAsmInstructionFactory;

public class Orgf extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (arguments.size() == 1) {
            return new Orgf_(context, arguments.get(0));
        }
        throw new RuntimeException("Invalid arguments");
    }

    public class Orgf_ extends Spin2InstructionObject {

        Spin2PAsmExpression arg0;
        int size;

        public Orgf_(Context context, Spin2PAsmExpression arg0) {
            super(context);
            this.arg0 = arg0;
        }

        @Override
        public int resolve(int address, boolean hubMode) {
            context.setAddress(hubMode ? address : address >> 2);
            int newAddress = arg0.getInteger() << 2;
            if (address > newAddress) {
                newAddress = address;
            }
            size = newAddress - address;
            return newAddress;
        }

        @Override
        public int getSize() {
            return size;
        }

        @Override
        public byte[] getBytes() {
            return new byte[size];
        }

    }
}
