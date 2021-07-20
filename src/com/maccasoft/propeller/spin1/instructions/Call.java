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

public class Call extends Spin1PAsmInstructionFactory {

    @Override
    public Spin1InstructionObject createObject(Spin1Context context, String condition, List<Spin1PAsmExpression> arguments, String effect) {
        if (Spin1PAsmSchema.S.check(arguments, effect)) {
            return new Call_(context, condition, arguments.get(0), effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * CALL    #S       {WC/WZ}
     */
    public class Call_ extends Spin1InstructionObject {

        String condition;
        Spin1PAsmExpression src;
        String effect;

        public Call_(Spin1Context context, String condition, Spin1PAsmExpression src, String effect) {
            super(context);
            this.condition = condition;
            this.src = src;
            this.effect = effect;
        }

        // 010111_0011_1111_ddddddddd_sssssssss

        @Override
        public byte[] getBytes() {
            int value = instr.setValue(0, 0b010111);
            value = con.setValue(value, encodeCondition(condition));
            value = zcr.setValue(value, encodeEffect(effect));
            value = i.setBoolean(value, true);
            value = d.setValue(value, context.getInteger(src.getExpression().toString() + "_ret"));
            value = s.setValue(value, src.getInteger());
            return getBytes(value);
        }

    }
}
