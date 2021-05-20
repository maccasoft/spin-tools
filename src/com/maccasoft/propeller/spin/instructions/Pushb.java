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

public class Pushb extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.LD.check(arguments, effect)) {
            return new Pushb_(context, condition, arguments.get(0));
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * PUSHB   {#}D
     */
    public class Pushb_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;

        public Pushb_(Spin2Context context, String condition, Spin2PAsmExpression dst) {
            super(context);
            this.condition = condition;
            this.dst = dst;
        }

        @Override
        public int getSize() {
            return dst.isLongLiteral() ? 8 : 4;
        }

        // EEEE 1100011 0L1 DDDDDDDDD 111100001

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition));
            value = o.setValue(value, 0b1100011);
            value = c.setValue(value, 0);
            value = l.setBoolean(value, dst.isLiteral());
            value = i.setValue(value, 1);
            value = d.setValue(value, dst.getInteger());
            value = s.setValue(value, 0b111100001);
            return dst.isLongLiteral() ? getBytes(encodeAugd(condition, dst.getInteger()), value) : getBytes(value);
        }

    }
}