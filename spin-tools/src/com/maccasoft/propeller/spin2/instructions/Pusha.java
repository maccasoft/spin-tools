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

public class Pusha extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.LD.check(arguments, effect)) {
            return new Pusha_(context, condition, arguments.get(0));
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * PUSHA   {#}D
     */
    public class Pusha_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;

        public Pusha_(Context context, String condition, Spin2PAsmExpression dst) {
            super(context);
            this.condition = condition;
            this.dst = dst;
        }

        @Override
        public int getSize() {
            return dst.isLongLiteral() ? 8 : 4;
        }

        // EEEE 1100011 0L1 DDDDDDDDD 101100001

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1100011);
            value = c.setValue(value, 0);
            value = l.setBoolean(value, dst.isLiteral());
            try {
                value = i.setValue(value, 1);
                if (dst.getInteger() > 0x1FF && !dst.isLongLiteral()) {
                    throw new Exception("destination register/constant cannot exceed $1FF");
                }
                value = d.setValue(value, dst.getInteger());
            } catch (Exception e) {
                throw new CompilerException(e.getMessage(), dst.getExpression().getData());
            }
            value = s.setValue(value, 0b101100001);
            return dst.isLongLiteral() ? getBytes(encodeAugd(condition, dst.getInteger()), value) : getBytes(value);
        }

    }
}
