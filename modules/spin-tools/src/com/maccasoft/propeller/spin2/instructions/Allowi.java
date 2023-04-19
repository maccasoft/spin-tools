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
import com.maccasoft.propeller.spin2.Spin2PAsmSchema;

public class Allowi extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.NONE.check(arguments, effect)) {
            return new Allowi_(context, condition);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * ALLOWI
     */
    public class Allowi_ extends Spin2InstructionObject {

        String condition;

        public Allowi_(Context context, String condition) {
            super(context);
            this.condition = condition;
        }

        // EEEE 1101011 000 000100000 000100100

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1101011);
            value = czi.setValue(value, 0b000);
            value = d.setValue(value, 0b000100000);
            value = s.setValue(value, 0b000100100);
            return getBytes(value);
        }

    }
}
