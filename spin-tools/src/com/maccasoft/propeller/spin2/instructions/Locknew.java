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
import com.maccasoft.propeller.spin2.Spin2PAsmSchema;

public class Locknew extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.D_WC.check(arguments, effect)) {
            return new Locknew_D_(context, condition, arguments.get(0), effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * LOCKNEW D               {WC}
     */
    public class Locknew_D_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        String effect;

        public Locknew_D_(Context context, String condition, Spin2PAsmExpression dst, String effect) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.effect = effect;
        }

        // EEEE 1101011 C00 DDDDDDDDD 000000100

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1101011);
            value = c.setBoolean(value, "wc".equalsIgnoreCase(effect));
            value = d.setValue(value, dst.getInteger());
            value = s.setValue(value, 0b000000100);
            return getBytes(value);
        }

    }
}
