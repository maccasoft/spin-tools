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

public class Fltrnd extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.LD_WCZ.check(arguments, effect)) {
            return new Fltrnd_(context, condition, arguments.get(0), effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * FLTRND  {#}D           {WCZ}
     */
    public class Fltrnd_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        String effect;

        public Fltrnd_(Context context, String condition, Spin2PAsmExpression dst, String effect) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.effect = effect;
        }

        @Override
        public int getSize() {
            return dst.isLongLiteral() ? 8 : 4;
        }

        // EEEE 1101011 CZL DDDDDDDDD 001010110

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1101011);
            value = cz.setValue(value, encodeEffect(effect));
            value = i.setBoolean(value, dst.isLiteral());
            if (dst.getInteger() > 0x1FF) {
                throw new CompilerException("destination register/constant cannot exceed $1FF", dst.getExpression().getData());
            }
            value = d.setValue(value, dst.getInteger());
            value = s.setValue(value, 0b001010110);
            return dst.isLongLiteral() ? getBytes(encodeAugd(condition, dst.getInteger()), value) : getBytes(value);
        }

    }
}
