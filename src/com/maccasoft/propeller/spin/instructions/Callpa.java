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

/*
 * CALLPA  {#}D,{#}S
 */
public class Callpa extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.LD_S.check(arguments, effect)) {
            return new Callpa_(context, condition, arguments.get(0), arguments.get(1));
        }
        throw new RuntimeException("Invalid arguments");
    }

    public class Callpa_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;
        String effect;

        public Callpa_(Spin2Context context, String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.src = src;
        }

        @Override
        public int resolve(int address) {
            super.resolve(address);
            if (dst.isLongLiteral() && src.isLongLiteral()) {
                return address + 3;
            }
            if (dst.isLongLiteral() || src.isLongLiteral()) {
                return address + 2;
            }
            return address + 1;
        }

        @Override
        public int getSize() {
            return src.isLongLiteral() ? 8 : 4;
        }

        // EEEE 1011010 0LI DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : context.getInteger(condition));
            value = o.setValue(value, 0b1011010);
            value = c.setValue(value, 0);
            value = l.setBoolean(value, dst.isLiteral());
            value = i.setBoolean(value, src.isLiteral());
            value = d.setValue(value, dst.getInteger());
            int offset = src.isLiteral() ? (src.getInteger() - context.getSymbol("$").getNumber().intValue() - 1) : src.getInteger();
            value = s.setValue(value, offset);
            if (dst.isLongLiteral() && src.isLongLiteral()) {
                return getBytes(encodeAugd(condition, dst.getInteger()), encodeAugs(condition, offset), value);
            }
            if (dst.isLongLiteral()) {
                return getBytes(encodeAugd(condition, dst.getInteger()), value);
            }
            if (src.isLongLiteral()) {
                return getBytes(encodeAugs(condition, offset), value);
            }
            return getBytes(value);
        }

    }
}
