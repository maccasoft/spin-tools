/*
 * Copyright (c) 2021 Marco Maccaferri and others.
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
import com.maccasoft.propeller.spin2.Spin2Context;
import com.maccasoft.propeller.spin2.Spin2InstructionObject;
import com.maccasoft.propeller.spin2.Spin2PAsmExpression;
import com.maccasoft.propeller.spin2.Spin2PAsmInstructionFactory;
import com.maccasoft.propeller.spin2.Spin2PAsmSchema;

public class Wrlong extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.LD_S.check(arguments, effect)) {
            return new Wrlong_(context, condition, arguments.get(0), arguments.get(1));
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * WRLONG  {#}D,{#}S/P
     */
    public class Wrlong_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;
        String effect;

        public Wrlong_(Spin2Context context, String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.src = src;
        }

        @Override
        public int getSize() {
            if (dst.isLongLiteral() && src.isLongLiteral()) {
                return 12;
            }
            if (dst.isLongLiteral() || src.isLongLiteral()) {
                return 8;
            }
            return 4;
        }

        // EEEE 1100011 0LI DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1100011);
            value = c.setValue(value, 0);
            value = l.setBoolean(value, dst.isLiteral());
            if (isPtr(src)) {
                value = i.setValue(value, 1);
                value = s.setValue(value, encodePtr(src));
            }
            else {
                if ((src.isLiteral() && !src.isLongLiteral()) && src.getInteger() > 0xFF) {
                    throw new CompilerException("Source constant cannot exceed $FF", src.getExpression().getData());
                }
                value = i.setBoolean(value, src.isLiteral());
                value = s.setValue(value, src.getInteger());
            }
            if (!dst.isLongLiteral() && dst.getInteger() > 0x1FF) {
                throw new CompilerException("Destination register/constant cannot exceed $1FF", dst.getExpression().getData());
            }
            value = d.setValue(value, dst.getInteger());
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
