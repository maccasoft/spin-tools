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

public class Getbyte extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.D_S_N.check(arguments, effect)) {
            return new Getbyte_(context, condition, arguments.get(0), arguments.get(1), arguments.get(2));
        }
        if (Spin2PAsmSchema.D.check(arguments, effect)) {
            return new Getbyte_D_(context, condition, arguments.get(0));
        }
        if (arguments.size() == 0 || arguments.size() == 2) {
            throw new RuntimeException("Expected 1 or 3 operands, found " + arguments.size());
        }
        if (arguments.get(0).isLiteral()) {
            throw new RuntimeException("Bad use of immediate for first operand");
        }
        if (arguments.size() == 3 && !arguments.get(2).isLiteral()) {
            throw new RuntimeException("Third operand must be an immediate");
        }
        if (effect != null) {
            throw new RuntimeException("Modifiers not allowed for this instruction");
        }
        throw new RuntimeException("Syntax error");
    }

    /*
     * GETBYTE D,{#}S,#N
     */
    public class Getbyte_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;
        Spin2PAsmExpression n;
        String effect;

        public Getbyte_(Spin2Context context, String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src, Spin2PAsmExpression n) {
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

        // EEEE 1000111 NNI DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition));
            value = o.setValue(value, 0b1000111);
            value = cz.setValue(value, n.getInteger());
            value = i.setBoolean(value, src.isLiteral());
            value = d.setValue(value, dst.getInteger());
            value = s.setValue(value, src.getInteger());
            return src.isLongLiteral() ? getBytes(encodeAugs(condition, src.getInteger()), value) : getBytes(value);
        }

    }

    /*
     * GETBYTE D
     */
    public class Getbyte_D_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;

        public Getbyte_D_(Spin2Context context, String condition, Spin2PAsmExpression dst) {
            super(context);
            this.condition = condition;
            this.dst = dst;
        }

        // EEEE 1000111 000 DDDDDDDDD 000000000

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition));
            value = o.setValue(value, 0b1000111);
            value = d.setValue(value, dst.getInteger());
            value = s.setValue(value, 0);
            return getBytes(value);
        }

    }
}
