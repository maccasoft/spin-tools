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

public class Loc extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.D_S.check(arguments, effect)) {
            return new Loc_(context, condition, arguments.get(0), arguments.get(1));
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * LOC     PA/PB/PTRA/PTRB,#{\}A
     */
    public class Loc_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;

        public Loc_(Context context, String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.src = src;
        }

        // EEEE 11101WW RAA AAAAAAAAA AAAAAAAAA

        @Override
        public byte[] getBytes() {
            CompilerException msgs = new CompilerException();

            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            try {
                value = o.setValue(value, 0b1110100 | encodeDst(dst.getExpression().toString()));
            } catch (Exception e) {
                msgs.addMessage(new CompilerException(e.getMessage(), dst.getExpression().getData()));
            }

            try {
                int addr = src.getInteger();
                int ours = context.getSymbol("$").getNumber().intValue();
                if ((ours < 0x400 && addr >= 0x400) || (ours >= 0x400 && addr < 0x400)) {
                    value = r.setBoolean(value, false);
                    value = a.setValue(value, addr);
                }
                else {
                    value = r.setBoolean(value, !src.isAbsolute());
                    if (src.isAbsolute()) {
                        value = a.setValue(value, addr);
                    }
                    else {
                        value = a.setValue(value, addr < 0x400 ? (addr - ours - 1) : addr - ours - 4);
                    }
                }
            } catch (Exception e) {
                msgs.addMessage(new CompilerException(e.getMessage(), dst.getExpression().getData()));
            }

            if (msgs.hasChilds()) {
                throw msgs;
            }

            return getBytes(value);
        }

        int encodeDst(String dst) throws Exception {
            if ("pa".equalsIgnoreCase(dst)) {
                return 0b00;
            }
            if ("pb".equalsIgnoreCase(dst)) {
                return 0b01;
            }
            if ("ptra".equalsIgnoreCase(dst)) {
                return 0b10;
            }
            if ("ptrb".equalsIgnoreCase(dst)) {
                return 0b11;
            }
            throw new Exception("destination register must be PA, PB, PTRA or PTRB");
        }

    }
}
