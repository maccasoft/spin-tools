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

import com.maccasoft.propeller.CompilerMessage;
import com.maccasoft.propeller.spin2.Spin2Context;
import com.maccasoft.propeller.spin2.Spin2InstructionObject;
import com.maccasoft.propeller.spin2.Spin2PAsmExpression;
import com.maccasoft.propeller.spin2.Spin2PAsmInstructionFactory;
import com.maccasoft.propeller.spin2.Spin2PAsmSchema;

public class Setword extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.D_S_N.check(arguments, effect)) {
            return new Setword_(context, condition, arguments.get(0), arguments.get(1), arguments.get(2));
        }
        if (Spin2PAsmSchema.S.check(arguments, effect)) {
            return new Setword_S_(context, condition, arguments.get(0));
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * SETWORD D,{#}S,#N
     */
    public class Setword_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;
        Spin2PAsmExpression n;

        public Setword_(Spin2Context context, String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src, Spin2PAsmExpression n) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.src = src;
            this.n = n;
        }

        @Override
        public int getSize() {
            return src.isLongLiteral() ? 8 : 4;
        }

        // EEEE 1001001 0NI DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition));
            value = o.setValue(value, 0b1001001);
            value = c.setValue(value, 0);
            value = z.setValue(value, n.getInteger());
            value = i.setBoolean(value, src.isLiteral());
            if (dst.getInteger() > 0x1FF) {
                throw new CompilerMessage("Destination register cannot exceed $1FF", dst.getExpression().getData());
            }
            value = d.setValue(value, dst.getInteger());
            if (src.isLongLiteral() && src.getInteger() > 0x1FF) {
                throw new CompilerMessage("Source register cannot exceed $1FF", dst.getExpression().getData());
            }
            value = s.setValue(value, src.getInteger());
            if (n.getInteger() < 0 || n.getInteger() > 1) {
                throw new CompilerMessage("Word number can be 0 or 1", n.getExpression().getData());
            }
            return src.isLongLiteral() ? getBytes(encodeAugs(condition, src.getInteger()), value) : getBytes(value);
        }

    }

    /*
     * SETWORD {#}S
     */
    public class Setword_S_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression src;

        public Setword_S_(Spin2Context context, String condition, Spin2PAsmExpression src) {
            super(context);
            this.condition = condition;
            this.src = src;
        }

        // EEEE 1001001 00I 000000000 SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition));
            value = o.setValue(value, 0b1001001);
            value = cz.setValue(value, 0b00);
            value = i.setBoolean(value, src.isLiteral());
            value = d.setValue(value, 0);
            value = s.setValue(value, src.getInteger());
            return getBytes(value);
        }

    }
}
