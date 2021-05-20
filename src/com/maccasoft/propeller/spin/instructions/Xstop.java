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

public class Xstop extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.NONE.check(arguments, effect)) {
            return new Xstop_(context, condition);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * XSTOP
     */
    public class Xstop_ extends Spin2InstructionObject {

        String condition;

        public Xstop_(Spin2Context context, String condition) {
            super(context);
            this.condition = condition;
        }

        // EEEE 1100101 011 000000000 000000000

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition));
            value = o.setValue(value, 0b1100101);
            value = czi.setValue(value, 0b011);
            value = d.setValue(value, 0b000000000);
            value = s.setValue(value, 0b000000000);
            return getBytes(value);
        }

    }
}
