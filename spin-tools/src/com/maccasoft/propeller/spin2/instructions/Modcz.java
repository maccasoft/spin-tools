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

/*
 * MODCZ   c,z      {WC/WZ/WCZ}
 */
public class Modcz extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.D_S_WC_WZ_WCZ.check(arguments, effect)) {
            return new Modcz_(context, condition, arguments.get(0), arguments.get(1), effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    public class Modcz_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;
        String effect;

        public Modcz_(Context context, String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src, String effect) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.src = src;
            this.effect = effect;
        }

        // EEEE 1101011 CZ1 0cccczzzz 001101111

        @Override
        public byte[] getBytes() {
            Integer cval = mod.get(dst.getExpression().toString().toLowerCase());
            if (cval == null) {
                cval = dst.getInteger() & 0b1111;
            }
            Integer zval = mod.get(src.getExpression().toString().toLowerCase());
            if (zval == null) {
                zval = src.getInteger() & 0b1111;
            }

            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1101011);
            value = cz.setValue(value, encodeEffect(effect));
            value = i.setValue(value, 1);
            value = d.setValue(value, (cval << 4) | zval);
            value = s.setValue(value, 0b001101111);
            return getBytes(value);
        }

    }
}
