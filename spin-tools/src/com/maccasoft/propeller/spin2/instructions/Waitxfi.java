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

public class Waitxfi extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.WC_WZ_WCZ.check(arguments, effect)) {
            return new Waitxfi_(context, condition, effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * WAITXFI          {WC/WZ/WCZ}
     */
    public class Waitxfi_ extends Spin2InstructionObject {

        String condition;
        String effect;

        public Waitxfi_(Context context, String condition, String effect) {
            super(context);
            this.condition = condition;
            this.effect = effect;
        }

        // EEEE 1101011 CZ0 000011011 000100100

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1101011);
            value = cz.setValue(value, encodeEffect(effect));
            value = d.setValue(value, 0b000011011);
            value = s.setValue(value, 0b000100100);
            return getBytes(value);
        }

    }
}
