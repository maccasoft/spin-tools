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

public class Testbn extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.TEST_OP.check(arguments, effect)) {
            return new Testbn_(context, condition, arguments.get(0), arguments.get(1), effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * TESTBN  D,{#}S         WC/WZ
     * TESTBN  D,{#}S     ANDC/ANDZ
     * TESTBN  D,{#}S       ORC/ORZ
     * TESTBN  D,{#}S     XORC/XORZ
     */
    public class Testbn_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;
        String effect;

        public Testbn_(Spin2Context context, String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src, String effect) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.src = src;
            this.effect = effect;
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

        // EEEE 0100001 CZI DDDDDDDDD SSSSSSSSS
        // EEEE 0100011 CZI DDDDDDDDD SSSSSSSSS
        // EEEE 0100101 CZI DDDDDDDDD SSSSSSSSS
        // EEEE 0100111 CZI DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : context.getInteger(condition));
            value = o.setValue(value, 0b0100001 | encodeOpcodeEffect());
            value = cz.setValue(value, encodeEffect());
            value = i.setBoolean(value, src.isLiteral());
            value = d.setValue(value, dst.getInteger());
            value = s.setValue(value, src.getInteger());
            return src.isLongLiteral() ? getBytes(encodeAugs(condition, src.getInteger()), value) : getBytes(value);
        }

        int encodeOpcodeEffect() {
            if ("andc".equalsIgnoreCase(effect) || "andz".equalsIgnoreCase(effect)) {
                return 0b010;
            }
            if ("orc".equalsIgnoreCase(effect) || "orz".equalsIgnoreCase(effect)) {
                return 0b100;
            }
            if ("xorc".equalsIgnoreCase(effect) || "xorz".equalsIgnoreCase(effect)) {
                return 0b110;
            }
            return 0b000;
        }

        int encodeEffect() {
            if ("wc".equalsIgnoreCase(effect) || "andc".equalsIgnoreCase(effect) || "orc".equalsIgnoreCase(effect) || "xorc".equalsIgnoreCase(effect)) {
                return 0b10;
            }
            if ("wz".equalsIgnoreCase(effect) || "andz".equalsIgnoreCase(effect) || "orz".equalsIgnoreCase(effect) || "xorz".equalsIgnoreCase(effect)) {
                return 0b01;
            }
            return 0b00;
        }

    }
}
