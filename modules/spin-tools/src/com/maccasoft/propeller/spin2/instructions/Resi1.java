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

public class Resi1 extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.NONE.check(arguments, effect)) {
            return new Resi1_(context, condition);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * RESI1
     */
    public class Resi1_ extends Spin2InstructionObject {

        String condition;

        public Resi1_(Context context, String condition) {
            super(context);
            this.condition = condition;
        }

        // EEEE 1011001 110 111110100 111110101

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1011001);
            value = czi.setValue(value, 0b110);
            value = d.setValue(value, 0b111110100);
            value = s.setValue(value, 0b111110101);
            return getBytes(value);
        }

    }
}
