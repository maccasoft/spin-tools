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

/*
 * RET              {WC/WZ/WCZ}
 */
public class Ret extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.WC_WZ_WCZ.check(arguments, effect)) {
            return new Ret_(context, condition, effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    public class Ret_ extends Spin2InstructionObject {

        String condition;
        String effect;

        public Ret_(Context context, String condition, String effect) {
            super(context);
            this.condition = condition;
            this.effect = effect;
        }

        // EEEE 1101011 CZ1 000000000 000101101

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1101011);
            value = cz.setValue(value, encodeEffect(effect));
            value = i.setValue(value, 1);
            value = d.setValue(value, 0b000000000);
            value = s.setValue(value, 0b000101101);
            return getBytes(value);
        }

    }
}
