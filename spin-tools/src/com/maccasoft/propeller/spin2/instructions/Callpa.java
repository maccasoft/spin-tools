/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.instructions;

import java.util.List;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin2.Spin2InstructionObject;
import com.maccasoft.propeller.spin2.Spin2PAsmExpression;
import com.maccasoft.propeller.spin2.Spin2PAsmInstructionFactory;
import com.maccasoft.propeller.spin2.Spin2PAsmSchema;

/*
 * CALLPA  {#}D,{#}S
 */
public class Callpa extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
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

        public Callpa_(Context context, String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.src = src;
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

        // EEEE 1011010 0LI DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1011010);
            value = c.setValue(value, 0);
            value = i.setBoolean(value, src.isLiteral());

            CompilerException msgs = new CompilerException();

            try {
                value = l.setBoolean(value, dst.isLiteral());
                if (!dst.isLongLiteral() && dst.getInteger() > 0x1FF) {
                    throw new Exception("destination register cannot exceed $1FF");
                }
                value = d.setValue(value, dst.getInteger());
            } catch (CompilerException e) {
                msgs.addMessage(e);
            } catch (Exception e) {
                msgs.addMessage(new CompilerException(e.getMessage(), dst.getData()));
            }

            if (src.isLiteral()) {
                int offset = 0;
                try {
                    int addr = src.getInteger();
                    int ours = context.getSymbol("$").getNumber().intValue();
                    if ((ours < 0x400 && addr >= 0x400) || (ours >= 0x400 && addr < 0x400)) {
                        throw new Exception("relative addresses cann't cross between cog and hub domains");
                    }
                    offset = (addr < 0x400 ? (addr - ours) : (addr - ours) / 4) - 1;
                    if (!src.isLongLiteral() && (offset < -256 || offset > 255)) {
                        throw new Exception("relative offset out of range");
                    }
                    value = s.setValue(value, offset);
                } catch (CompilerException e) {
                    msgs.addMessage(e);
                } catch (Exception e) {
                    msgs.addMessage(new CompilerException(e.getMessage(), src.getData()));
                }
                if (msgs.hasChilds()) {
                    throw msgs;
                }
                if (dst.isLongLiteral() && src.isLongLiteral()) {
                    return getBytes(encodeAugd(condition, dst.getInteger()), encodeAugs(condition, offset), value);
                }
                if (src.isLongLiteral()) {
                    return getBytes(encodeAugs(condition, offset), value);
                }
            }
            else {
                try {
                    if (src.getInteger() > 0x1FF) {
                        throw new Exception("source register cannot exceed $1FF");
                    }
                    value = s.setValue(value, src.getInteger());
                } catch (CompilerException e) {
                    msgs.addMessage(e);
                } catch (Exception e) {
                    msgs.addMessage(new CompilerException(e.getMessage(), src.getData()));
                }
                if (msgs.hasChilds()) {
                    throw msgs;
                }
                if (dst.isLongLiteral() && src.isLongLiteral()) {
                    return getBytes(encodeAugd(condition, dst.getInteger()), encodeAugs(condition, src.getInteger()), value);
                }
                if (src.isLongLiteral()) {
                    return getBytes(encodeAugs(condition, src.getInteger()), value);
                }
            }

            if (msgs.hasChilds()) {
                throw msgs;
            }

            if (dst.isLongLiteral()) {
                return getBytes(encodeAugd(condition, dst.getInteger()), value);
            }
            return getBytes(value);
        }

    }
}
