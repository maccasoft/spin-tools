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

public class Jnct1 extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.S.check(arguments, effect)) {
            return new Jnct1_(context, condition, arguments.get(0));
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * JNCT1   {#}S
     */
    public class Jnct1_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression src;

        public Jnct1_(Context context, String condition, Spin2PAsmExpression src) {
            super(context);
            this.condition = condition;
            this.src = src;
        }

        @Override
        public int getSize() {
            return src.isLongLiteral() ? 8 : 4;
        }

        // EEEE 1011110 01I 000010001 SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1011110);
            value = cz.setValue(value, 0b01);
            value = i.setBoolean(value, src.isLiteral());
            value = d.setValue(value, 0b000010001);

            int offset = src.getInteger();
            if (src.isLiteral()) {
                offset -= context.getInteger("$");
                if (src.getInteger() >= 0x400) {
                    offset /= 4;
                }
                offset--;
            }
            value = s.setValue(value, offset);

            return src.isLongLiteral() ? getBytes(encodeAugs(condition, offset), value) : getBytes(value);
        }

    }
}
