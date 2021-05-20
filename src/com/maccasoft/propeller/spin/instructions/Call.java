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

public class Call extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
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

        public Call_(Spin2Context context, String condition, Spin2PAsmExpression dst) {
            super(context);
            this.condition = condition;
            this.dst = dst;
        }

        // EEEE 1101101 RAA AAAAAAAAA AAAAAAAAA

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition));
            value = o.setValue(value, 0b1101011);
            value = r.setBoolean(value, !dst.getPrefix().endsWith("\\"));
            value = a.setValue(value, dst.getPrefix().endsWith("\\") ? dst.getInteger() : (dst.getInteger() - context.getSymbol("$").getNumber().intValue() - 1) * 4);
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

        public Call_D_(Spin2Context context, String condition, Spin2PAsmExpression dst, String effect) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.effect = effect;
        }

        // EEEE 1101011 CZ0 DDDDDDDDD 000101101

        @Override
        public byte[] getBytes() {
            int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition));
            value = o.setValue(value, 0b1101011);
            value = cz.setValue(value, encodeEffect(effect));
            value = d.setValue(value, dst.getInteger());
            value = s.setValue(value, 0b000101101);
            return getBytes(value);
        }

    }
}