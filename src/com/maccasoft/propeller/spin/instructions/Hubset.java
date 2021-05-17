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

public class Hubset extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, List<Spin2PAsmExpression> arguments, String effect) {
        if (arguments.size() == 1 && effect == null) {
            return new Hubset_(context, arguments.get(0));
        }
        throw new RuntimeException("Invalid arguments");
    }

    public static class Hubset_ extends Spin2InstructionObject {

        Spin2PAsmExpression argument;

        public Hubset_(Spin2Context context, Spin2PAsmExpression argument) {
            super(context);
            this.argument = argument;
        }

        @Override
        public int resolve(int address) {
            super.resolve(address);
            return address + (argument.isLongLiteral() ? 2 : 1);
        }

        @Override
        public int getSize() {
            return argument.isLongLiteral() ? 8 : 4;
        }

        // EEEE 1101011 00L DDDDDDDDD 000000000

        @Override
        public byte[] getBytes() {
            int value = encode(0b1101011, 0b00, argument.isLiteral(), argument.getInteger(), 0b000000000);
            if (argument.isLongLiteral()) {
                byte[] prefix = new Augd.Augd_(context, argument).getBytes();
                return getBytes(prefix, value);
            }
            return getBytes(value);
        }

    }
}
