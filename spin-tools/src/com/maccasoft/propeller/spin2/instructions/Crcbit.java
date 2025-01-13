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

public class Crcbit extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.D_S.check(arguments, effect)) {
            return new Crcbit_(context, condition, arguments.get(0), arguments.get(1));
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * CRCBIT  D,{#}S
     */
    public class Crcbit_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;

        public Crcbit_(Context context, String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.src = src;
        }

        @Override
        public int getSize() {
            return src.isLongLiteral() ? 8 : 4;
        }

        // EEEE 1001110 10I DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = o.setValue(encodeInstructionParameters(condition, dst, src, null), 0b1001110);
            value = cz.setValue(value, 0b10);
            return src.isLongLiteral() ? getBytes(encodeAugs(condition, src.getInteger()), value) : getBytes(value);
        }

    }
}
