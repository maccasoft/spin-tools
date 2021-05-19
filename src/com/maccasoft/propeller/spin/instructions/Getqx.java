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
import com.maccasoft.propeller.spin.Spin2PAsmSchema;

public class Getqx extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.D_WC_WZ_WCZ.check(arguments, effect)) {
            return new Getqx_D_(context, condition, arguments.get(0), effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * GETQX   D        {WC/WZ/WCZ}
     */
    public class Getqx_D_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        String effect;

        public Getqx_D_(Spin2Context context, String condition, Spin2PAsmExpression dst, String effect) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.effect = effect;
        }

        // EEEE 1101011 CZ0 DDDDDDDDD 000011000

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : context.getInteger(condition));
            value = o.setValue(value, 0b1101011);
            value = cz.setValue(value, encodeEffect(effect));
            value = d.setValue(value, dst.getInteger());
            value = s.setValue(value, 0b000011000);
            return getBytes(value);
        }

    }
}
