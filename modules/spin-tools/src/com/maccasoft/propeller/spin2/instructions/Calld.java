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
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin2.Spin2InstructionObject;
import com.maccasoft.propeller.spin2.Spin2PAsmExpression;
import com.maccasoft.propeller.spin2.Spin2PAsmInstructionFactory;
import com.maccasoft.propeller.spin2.Spin2PAsmSchema;

public class Calld extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if ("_ret_".equalsIgnoreCase(condition)) {
            throw new RuntimeException("_ret_ has no effect for branching instructions");
        }
        if (Calld_P_A_.check(arguments, effect)) {
            return new Calld_P_A_(context, condition, arguments.get(0), arguments.get(1));
        }
        if (Spin2PAsmSchema.D_S_WC_WZ_WCZ.check(arguments, effect)) {
            return new Calld_(context, condition, arguments.get(0), arguments.get(1), effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * CALLD   PA/PB/PTRA/PTRB,#{\}A
     */
    public static class Calld_P_A_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;

        public Calld_P_A_(Context context, String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.src = src;
        }

        public static boolean check(List<Spin2PAsmExpression> arguments, String effect) {
            if (arguments.size() != 2 || effect != null) {
                return false;
            }
            String dst = arguments.get(0).toString();
            if ("pa".equalsIgnoreCase(dst)) {
                return true;
            }
            if ("pb".equalsIgnoreCase(dst)) {
                return true;
            }
            if ("ptra".equalsIgnoreCase(dst)) {
                return true;
            }
            if ("ptrb".equalsIgnoreCase(dst)) {
                return true;
            }
            return false;
        }

        // EEEE 11100WW RAA AAAAAAAAA AAAAAAAAA

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1110000 | encodeDst(dst.getExpression().toString()));
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
                    value = a.setValue(value, addr < 0x400 ? (addr - ours - 1) * 4 : addr - ours - 4);
                }
            }
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

    /*
     * CALLD   D,{#}S   {WC/WZ/WCZ}
     */
    public static class Calld_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;
        String effect;

        public Calld_(Context context, String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src, String effect) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.src = src;
            this.effect = effect;
        }

        @Override
        public int getSize() {
            return src.isLongLiteral() ? 8 : 4;
        }

        // EEEE 1011001 CZI DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
            value = o.setValue(value, 0b1011001);
            value = cz.setValue(value, encodeEffect(effect));
            value = i.setBoolean(value, src.isLiteral());
            if (dst.getInteger() > 0x1FF) {
                throw new CompilerException("Destination register cannot exceed $1FF", dst.getExpression().getData());
            }
            value = d.setValue(value, dst.getInteger());
            value = s.setValue(value, src.isLiteral() ? (src.getInteger() - context.getSymbol("$").getNumber().intValue() - 1) : src.getInteger());
            return src.isLongLiteral() ? getBytes(encodeAugs(condition, src.getInteger()), value) : getBytes(value);
        }

    }
}
