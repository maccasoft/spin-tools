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

public class Getword extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.D_S_N.check(arguments, effect)) {
            return new Getword_(context, condition, arguments.get(0), arguments.get(1), arguments.get(2));
        }
        if (Spin2PAsmSchema.D.check(arguments, effect)) {
            return new Getword_D_(context, condition, arguments.get(0));
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * GETWORD D,{#}S,#N
     */
    public class Getword_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;
        Spin2PAsmExpression n;
        String effect;

        public Getword_(Spin2Context context, String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src, Spin2PAsmExpression n) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.src = src;
            this.n = n;
        }

        @Override
        public int resolve(int address) {
            super.resolve(address);
            return address + (src.isLongLiteral() ? 2 : 1);
        }

        @Override
        public int getSize() {
            return src.isLongLiteral() ? 8 : 4;
        }

        // EEEE 1001001 1NI DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : context.getInteger(condition));
            value = o.setValue(value, 0b1001001);
            value = c.setValue(value, 1);
            value = z.setValue(value, n.getInteger());
            value = i.setBoolean(value, src.isLiteral());
            value = d.setValue(value, dst.getInteger());
            value = s.setValue(value, src.getInteger());
            return src.isLongLiteral() ? getBytes(encodeAugs(condition, src.getInteger()), value) : getBytes(value);
        }

    }

    /*
     * GETWORD D
     */
    public class Getword_D_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;

        public Getword_D_(Spin2Context context, String condition, Spin2PAsmExpression dst) {
            super(context);
            this.condition = condition;
            this.dst = dst;
        }

        // EEEE 1001001 100 DDDDDDDDD 000000000

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : context.getInteger(condition));
            value = o.setValue(value, 0b1001001);
            value = czi.setValue(value, 0b100);
            value = d.setValue(value, dst.getInteger());
            value = s.setValue(value, 0b000000000);
            return getBytes(value);
        }

    }
}
