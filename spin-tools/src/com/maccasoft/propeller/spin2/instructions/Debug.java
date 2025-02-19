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

public class Debug extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.LD.check(arguments, effect)) {
            return new Debug_(context, condition, arguments.get(0));
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * BRK     #D
     */
    public class Debug_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;

        public Debug_(Context context, String condition, Spin2PAsmExpression dst) {
            super(context);
            this.condition = condition;
            this.dst = dst;
        }

        // EEEE 1101011 00L DDDDDDDDD 000110110

        @Override
        public int getSize() {
            if (condition != null && !"__ret__".equalsIgnoreCase(condition)) {
                return 8;
            }
            return 4;
        }

        @Override
        public byte[] getBytes() {
            int value = 0;
            if (condition == null || "__ret__".equalsIgnoreCase(condition)) {
                value = e.setValue(value, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            }
            else {
                value = e.setValue(value, 0b1111);
            }
            value = o.setValue(value, 0b1101011);
            value = cz.setValue(value, 0b00);
            value = i.setBoolean(value, true);
            value = d.setValue(value, dst.getInteger());
            value = s.setValue(value, 0b000110110);
            if (condition != null && !"__ret__".equalsIgnoreCase(condition)) {
                return getBytes(encodeSkip(), value);
            }
            return getBytes(value);
        }

        int encodeSkip() {
            int value = e.setValue(0, conditions.get(condition.toLowerCase()) ^ 0b1111);
            value = o.setValue(value, 0b1101011);
            value = cz.setValue(value, 0b00);
            value = i.setBoolean(value, true);
            value = d.setValue(value, 1);
            value = s.setValue(value, 0b000110001);
            return value;
        }

    }
}
