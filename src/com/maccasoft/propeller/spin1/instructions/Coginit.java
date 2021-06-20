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

public class Coginit extends Spin1PAsmInstructionFactory {

    @Override
    public Spin1InstructionObject createObject(Spin1Context context, String condition, List<Spin1PAsmExpression> arguments, String effect) {
        if (Spin1PAsmSchema.D.check(arguments, effect)) {
            return new Coginit_(context, condition, arguments.get(0));
        }
        throw new RuntimeException("error: invalid arguments");
    }

    /*
     * COGINIT D
     */
    public class Coginit_ extends Spin1InstructionObject {

        String condition;
        Spin1PAsmExpression dst;

        public Coginit_(Spin1Context context, String condition, Spin1PAsmExpression dst) {
            super(context);
            this.condition = condition;
            this.dst = dst;
        }

        // 000011_0001_1111_ddddddddd_xxxxxx010

        @Override
        public byte[] getBytes() {
            int value = instr.setValue(0, 0b000011);
            value = con.setValue(value, condition == null ? 0b1111 : conditions.get(condition));
            value = zcr.setValue(value, 0b000);
            value = i.setBoolean(value, true);
            value = d.setValue(value, dst.getInteger());
            value = s.setValue(value, 0b010);
            return getBytes(value);
        }

    }
}
