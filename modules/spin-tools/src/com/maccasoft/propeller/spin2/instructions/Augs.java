/*
 * Copyright (c) 2021 Marco Maccaferri and others.
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

public class Augs extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (arguments.size() == 1 && "#".equals(arguments.get(0).getPrefix()) && effect == null) {
            return new Augs_(context, condition, arguments.get(0));
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * AUGS    #n
     */
    public static class Augs_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression argument;

        public Augs_(Context context, String condition, Spin2PAsmExpression argument) {
            super(context);
            this.condition = condition;
            this.argument = argument;
        }

        // EEEE 11110nn nnn nnnnnnnnn nnnnnnnnn

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1111000);
            return getBytes(x.setValue(value, argument.getInteger() >> 9));
        }

    }
}
