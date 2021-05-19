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

public class Loc extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
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

        public Loc_(Spin2Context context, String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.src = src;
        }

        // EEEE 11101WW RAA AAAAAAAAA AAAAAAAAA

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : context.getInteger(condition));
            value = o.setValue(value, 0b1110100 | encodeDst(dst.getExpression().toString()));
            value = r.setBoolean(value, !dst.getPrefix().endsWith("\\"));
            value = a.setValue(value, dst.getPrefix().endsWith("\\") ? dst.getInteger() : (dst.getInteger() - context.getSymbol("$").getNumber().intValue() - 1) * 4);
            return getBytes(value);
        }

        int encodeDst(String dst) {
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
            return 0b00;
        }

    }
}
