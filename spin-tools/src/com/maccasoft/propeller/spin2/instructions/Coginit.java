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

public class Coginit extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.LD_IS_WC.check(arguments, effect)) {
            return new Coginit_(context, condition, arguments.get(0), arguments.get(1), effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * COGINIT {#}D,{#}S       {WC}
     */
    public class Coginit_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;
        String effect;

        public Coginit_(Context context, String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src, String effect) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.src = src;
            this.effect = effect;
        }

        @Override
        public int getSize() {
            int size = 4;
            if (dst.isLongLiteral()) {
                size += 4;
            }
            if (src.isLongLiteral()) {
                size += 4;
            }
            return size;
        }

        // EEEE 1100111 CLI DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = o.setValue(encodeInstructionParameters(condition, dst, src), 0b1100111);
            value = c.setBoolean(value, "wc".equalsIgnoreCase(effect));
            if (dst.isLongLiteral() && src.isLongLiteral()) {
                return getBytes(encodeAugd(condition, dst.getInteger()), encodeAugs(condition, src.getInteger()), value);
            }
            if (dst.isLongLiteral()) {
                return getBytes(encodeAugd(condition, dst.getInteger()), value);
            }
            if (src.isLongLiteral()) {
                return getBytes(encodeAugs(condition, src.getInteger()), value);
            }
            return getBytes(value);
        }

    }
}
