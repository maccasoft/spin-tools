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

public class Setnib extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.D_S_N.check(arguments, effect)) {
            return new Setnib_(context, condition, arguments.get(0), arguments.get(1), arguments.get(2));
        }
        if (Spin2PAsmSchema.S.check(arguments, effect)) {
            return new Setnib_S_(context, condition, arguments.get(0));
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * SETNIB  D,{#}S,#N
     */
    public class Setnib_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;
        Spin2PAsmExpression n;

        public Setnib_(Context context, String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src, Spin2PAsmExpression n) {
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

        // EEEE 100000N NNI DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            CompilerException msgs = new CompilerException();

            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1000000);
            try {
                if (dst.getInteger() > 0x1FF) {
                    msgs.addMessage(new CompilerException("destination register cannot exceed $1FF", dst.getExpression().getData()));
                }
                value = d.setValue(value, dst.getInteger());
            } catch (Exception e) {
                msgs.addMessage(new CompilerException(e.getMessage(), dst.getExpression().getData()));
            }
            try {
                value = i.setBoolean(value, src.isLiteral());
                if (!src.isLongLiteral() && src.getInteger() > 0x1FF) {
                    msgs.addMessage(new CompilerException("source register/constant cannot exceed $1FF", src.getExpression().getData()));
                }
                value = s.setValue(value, src.getInteger());
            } catch (Exception e) {
                msgs.addMessage(new CompilerException(e.getMessage(), src.getExpression().getData()));
            }
            try {
                if (n.getInteger() < 0 || n.getInteger() > 7) {
                    msgs.addMessage(new CompilerException("selector must be 0 to 7", n.getExpression().getData()));
                }
                value = nnn.setValue(value, n.getInteger());
            } catch (Exception e) {
                msgs.addMessage(new CompilerException(e.getMessage(), n.getExpression().getData()));
            }
            if (msgs.hasChilds()) {
                throw msgs;
            }

            return src.isLongLiteral() ? getBytes(encodeAugs(condition, src.getInteger()), value) : getBytes(value);
        }

    }

    /*
     * SETNIB  {#}S
     */
    public class Setnib_S_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression src;

        public Setnib_S_(Context context, String condition, Spin2PAsmExpression dst) {
            super(context);
            this.condition = condition;
            this.src = dst;
        }

        // EEEE 1000000 00I 000000000 SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1000000);
            value = d.setValue(value, 0b000000000);
            try {
                value = i.setBoolean(value, src.isLiteral());
                if (src.getInteger() > 0x1FF) {
                    throw new CompilerException("source register/constant cannot exceed $1FF", src.getExpression().getData());
                }
                value = s.setValue(value, src.getInteger());
            } catch (Exception e) {
                throw new CompilerException(e.getMessage(), src.getExpression().getData());
            }
            return getBytes(value);
        }

    }
}
