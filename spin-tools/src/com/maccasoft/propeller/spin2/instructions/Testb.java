/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.instructions;

import java.util.List;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin2.Spin2InstructionObject;
import com.maccasoft.propeller.spin2.Spin2PAsmExpression;
import com.maccasoft.propeller.spin2.Spin2PAsmInstructionFactory;
import com.maccasoft.propeller.spin2.Spin2PAsmSchema;

public class Testb extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.TEST_OP.check(arguments, effect)) {
            return new Testb_(context, condition, arguments.get(0), arguments.get(1), effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * TESTB   D,{#}S         WC/WZ
     * TESTB   D,{#}S     ANDC/ANDZ
     * TESTB   D,{#}S       ORC/ORZ
     * TESTB   D,{#}S     XORC/XORZ
     */
    public class Testb_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;
        String effect;

        public Testb_(Context context, String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src, String effect) {
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

        // EEEE 0100000 CZI DDDDDDDDD SSSSSSSSS
        // EEEE 0100010 CZI DDDDDDDDD SSSSSSSSS
        // EEEE 0100100 CZI DDDDDDDDD SSSSSSSSS
        // EEEE 0100110 CZI DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = o.setValue(encodeInstructionParameters(condition, dst, src, null), 0b0100000 | encodeOpcodeEffect());
            value = cz.setValue(value, encodeEffect());
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
