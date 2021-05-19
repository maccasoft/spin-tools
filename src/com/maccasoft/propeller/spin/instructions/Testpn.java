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

public class Testpn extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.TEST_OP2.check(arguments, effect)) {
            return new Testpn_(context, condition, arguments.get(0), effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * TESTPN  {#}D           WC/WZ
     * TESTPN  {#}D       ANDC/ANDZ
     * TESTPN  {#}D         ORC/ORZ
     * TESTPN  {#}D       XORC/XORZ
     */
    public class Testpn_ extends Spin2InstructionObject {

        String condition;
        Spin2PAsmExpression dst;
        String effect;

        public Testpn_(Spin2Context context, String condition, Spin2PAsmExpression dst, String effect) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.effect = effect;
        }

        // EEEE 1101011 CZL DDDDDDDDD 001000001
        // EEEE 1101011 CZL DDDDDDDDD 001000011
        // EEEE 1101011 CZL DDDDDDDDD 001000101
        // EEEE 1101011 CZL DDDDDDDDD 001000111

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : context.getInteger(condition));
            value = o.setValue(value, 0b1101011);
            value = cz.setValue(value, encodeEffect());
            value = i.setBoolean(value, dst.isLiteral());
            value = d.setValue(value, dst.getInteger());
            value = s.setValue(value, 0b001000001 | encodeOpcodeEffect());
            return getBytes(value);
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
