/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.instructions;

import java.util.List;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin2.Spin2InstructionObject;
import com.maccasoft.propeller.spin2.Spin2PAsmExpression;
import com.maccasoft.propeller.spin2.Spin2PAsmInstructionFactory;
import com.maccasoft.propeller.spin2.Spin2PAsmSchema;

public class Akpin extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.S.check(arguments, effect)) {
            return new Akpin_(context, condition, arguments.get(0));
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * AKPIN   {#}S
     */
    public class Akpin_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression src;

        public Akpin_(Context context, String condition, Spin2PAsmExpression src) {
            super(context);
            this.condition = condition;
            this.src = src;
        }

        // EEEE 1100000 01I 000000001 SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1100000);
            value = cz.setValue(value, 0b01);
            value = d.setValue(value, 0b000000001);
            try {
                value = i.setBoolean(value, src.isLiteral());
                if (src.getInteger() > 0x1FF) {
                    throw new CompilerException("source register/constant cannot exceed $1FF", src.getExpression().getData());
                }
                value = s.setValue(value, src.getInteger());
            } catch (Exception e) {
                throw new CompilerException(e.getMessage(), src.getExpression().getData());
            }
            return getBytes(value);
        }

    }
}
