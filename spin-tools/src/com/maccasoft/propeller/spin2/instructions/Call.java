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

public class Call extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if ("_ret_".equalsIgnoreCase(condition)) {
            throw new RuntimeException("_ret_ has no effect for branching instructions");
        }
        if (arguments.size() == 1 && arguments.get(0).isLiteral() && effect == null) {
            return new Call_(context, condition, arguments.get(0));
        }
        if (Spin2PAsmSchema.D_WC_WZ_WCZ.check(arguments, effect)) {
            return new Call_D_(context, condition, arguments.get(0), effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * CALL    #{\}A
     */
    public static class Call_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;

        public Call_(Context context, String condition, Spin2PAsmExpression dst) {
            super(context);
            this.condition = condition;
            this.dst = dst;
        }

        // EEEE 1101101 RAA AAAAAAAAA AAAAAAAAA

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1101101);
            int addr = dst.getInteger();
            int ours = context.getSymbol("$").getNumber().intValue();
            if ((ours < 0x400 && addr >= 0x400) || (ours >= 0x400 && addr < 0x400)) {
                value = r.setBoolean(value, false);
                value = a.setValue(value, addr);
            }
            else {
                value = r.setBoolean(value, !dst.isAbsolute());
                if (dst.isAbsolute()) {
                    value = a.setValue(value, addr);
                }
                else {
                    value = a.setValue(value, addr < 0x400 ? (addr - ours - 1) * 4 : addr - ours - 4);
                }
            }
            return getBytes(value);
        }

    }

    /*
     * CALL    D        {WC/WZ/WCZ}
     */
    public static class Call_D_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        String effect;

        public Call_D_(Context context, String condition, Spin2PAsmExpression dst, String effect) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.effect = effect;
        }

        // EEEE 1101011 CZ0 DDDDDDDDD 000101101

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1101011);
            value = cz.setValue(value, encodeEffect(effect));
            try {
                if (dst.getInteger() > 0x1FF) {
                    throw new Exception("destination register cannot exceed $1FF");
                }
                value = d.setValue(value, dst.getInteger());
            } catch (Exception e) {
                throw new CompilerException(e.getMessage(), dst.getExpression().getData());
            }
            value = s.setValue(value, 0b000101101);
            return getBytes(value);
        }

    }
}
