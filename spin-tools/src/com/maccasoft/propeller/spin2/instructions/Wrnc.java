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

public class Wrnc extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.D.check(arguments, effect)) {
            return new Wrnc_(context, condition, arguments.get(0));
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * WRNC    D
     */
    public class Wrnc_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;

        public Wrnc_(Context context, String condition, Spin2PAsmExpression dst) {
            super(context);
            this.condition = condition;
            this.dst = dst;
        }

        // EEEE 1101011 000 DDDDDDDDD 001101101

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1101011);
            value = czi.setValue(value, 0b000);
            try {
                if (dst.getInteger() > 0x1FF) {
                    throw new Exception("destination register cannot exceed $1FF");
                }
                value = d.setValue(value, dst.getInteger());
            } catch (Exception e) {
                throw new CompilerException(e.getMessage(), dst.getExpression().getData());
            }
            value = s.setValue(value, 0b001101101);
            return getBytes(value);
        }

    }
}
