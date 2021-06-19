/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1.instructions;

import java.util.List;

import com.maccasoft.propeller.spin1.Spin1Context;
import com.maccasoft.propeller.spin1.Spin1InstructionObject;
import com.maccasoft.propeller.spin1.Spin1PAsmExpression;
import com.maccasoft.propeller.spin1.Spin1PAsmInstructionFactory;
import com.maccasoft.propeller.spin1.Spin1PAsmSchema;

public class Lockret extends Spin1PAsmInstructionFactory {

    @Override
    public Spin1InstructionObject createObject(Spin1Context context, String condition, List<Spin1PAsmExpression> arguments, String effect) {
        if (Spin1PAsmSchema.D_WC_WZ.check(arguments, effect)) {
            return new Lockret_(context, condition, arguments.get(0), effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * LOCKRET D
     */
    public class Lockret_ extends Spin1InstructionObject {

        String condition;
        Spin1PAsmExpression dst;
        String effect;

        public Lockret_(Spin1Context context, String condition, Spin1PAsmExpression dst, String effect) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.effect = effect;
        }

        // 000011_0001_1111_ddddddddd_xxxxxx101

        @Override
        public byte[] getBytes() {
            int value = instr.setValue(0, 0b000011);
            value = con.setValue(value, condition == null ? 0b1111 : conditions.get(condition));
            value = zcr.setValue(value, encodeEffect(0b000, effect));
            value = i.setBoolean(value, true);
            value = d.setValue(value, dst.getInteger());
            value = s.setValue(value, 0b101);
            return getBytes(value);
        }

    }
}
