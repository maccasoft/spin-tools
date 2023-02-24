/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
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

/*
 * CALLPA  {#}D,{#}S
 */
public class Callpa extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if ("_ret_".equalsIgnoreCase(condition)) {
            throw new RuntimeException("_ret_ has no effect for branching instructions");
        }
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
        public int getSize() {
            if (dst.isLongLiteral() && src.isLongLiteral()) {
                return 12;
            }
            if (dst.isLongLiteral() || src.isLongLiteral()) {
                return 8;
            }
            return 4;
        }

        // EEEE 1011010 0LI DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1011010);
            value = c.setValue(value, 0);
            value = l.setBoolean(value, dst.isLiteral());
            value = i.setBoolean(value, src.isLiteral());

            if (!dst.isLongLiteral() && dst.getInteger() > 0x1FF) {
                throw new CompilerException("destination register cannot exceed $1FF", dst.getExpression().getData());
            }
            value = d.setValue(value, dst.getInteger());

            if (src.isLiteral()) {
                int addr = src.getInteger();
                int ours = context.getSymbol("$").getNumber().intValue();
                if ((ours < 0x400 && addr >= 0x400) || (ours >= 0x400 && addr < 0x400)) {
                    throw new CompilerException("relative addresses cann't cross between cog and hub domains", src.getExpression().getData());
                }
                int offset = addr - ours - 1;
                if (!src.isLongLiteral() && (offset < -256 || offset > 255)) {
                    throw new CompilerException("relative offset out of range", src.getExpression().getData());
                }
                value = s.setValue(value, offset);
                if (dst.isLongLiteral() && src.isLongLiteral()) {
                    return getBytes(encodeAugd(condition, dst.getInteger()), encodeAugs(condition, offset), value);
                }
                if (src.isLongLiteral()) {
                    return getBytes(encodeAugs(condition, offset), value);
                }
            }
            else {
                if (src.getInteger() > 0x1FF) {
                    throw new CompilerException("source register cannot exceed $1FF", src.getExpression().getData());
                }
                value = s.setValue(value, src.getInteger());
                if (dst.isLongLiteral() && src.isLongLiteral()) {
                    return getBytes(encodeAugd(condition, dst.getInteger()), encodeAugs(condition, src.getInteger()), value);
                }
                if (src.isLongLiteral()) {
                    return getBytes(encodeAugs(condition, src.getInteger()), value);
                }
            }

            if (dst.isLongLiteral()) {
                return getBytes(encodeAugd(condition, dst.getInteger()), value);
            }
            return getBytes(value);
        }

    }
}
