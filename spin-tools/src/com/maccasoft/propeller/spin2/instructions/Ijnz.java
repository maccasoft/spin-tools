/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
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

public class Ijnz extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.D_S.check(arguments, effect)) {
            return new Ijnz_(context, condition, arguments.get(0), arguments.get(1));
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * IJNZ    D,{#}S
     */
    public class Ijnz_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;

        public Ijnz_(Context context, String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.src = src;
        }

        @Override
        public int getSize() {
            return src.isLongLiteral() ? 8 : 4;
        }

        // EEEE 1011100 01I DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1011100);
            value = cz.setValue(value, 0b01);
            value = i.setBoolean(value, src.isLiteral());
            value = d.setValue(value, dst.getInteger());
            return encodeRelativeJump(value, condition, src);
        }

    }
}
