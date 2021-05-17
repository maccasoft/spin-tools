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

public class Branch_D_A extends Spin2PAsmInstructionFactory {

    int opcode1;
    int opcode2;

    public Branch_D_A(int opcode1, int opcode2) {
        this.opcode1 = opcode1;
        this.opcode2 = opcode2;
    }

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, List<Spin2PAsmExpression> arguments, String effect) {
        if (arguments.size() == 1) {
            if (arguments.get(0).isLiteral() && effect == null) {
                return new Branch_A_(context, arguments.get(0));
            }
            if (!arguments.get(0).isLiteral() && (effect == null || Spin2PAsmSchema.E_WC_WZ_WCZ.contains(effect.toLowerCase()))) {
                return new Branch_D_(context, arguments.get(0), effect);
            }
        }
        throw new RuntimeException("Invalid arguments");
    }

    public class Branch_A_ extends Spin2InstructionObject {

        Spin2PAsmExpression argument;

        public Branch_A_(Spin2Context context, Spin2PAsmExpression argument) {
            super(context);
            this.argument = argument;
        }

        // EEEE xxxxxxx RAA AAAAAAAAA AAAAAAAAA

        @Override
        public byte[] getBytes() {
            if (argument.getPrefix().endsWith("\\")) {
                return getBytes(encodeAddress(opcode1, false, argument.getInteger()));
            }
            return getBytes(encodeAddress(opcode1, true, (argument.getInteger() - context.getSymbol("$").getNumber().intValue() - 1) * 4));
        }

    }

    public class Branch_D_ extends Spin2InstructionObject {

        Spin2PAsmExpression argument;
        String effect;

        public Branch_D_(Spin2Context context, Spin2PAsmExpression argument, String effect) {
            super(context);
            this.argument = argument;
            this.effect = effect;
        }

        // EEEE xxxxxxx CZ0 DDDDDDDDD xxxxxxxxx

        @Override
        public byte[] getBytes() {
            return getBytes(cz.setValue(d.setValue(opcode2, argument.getInteger()), encodeEffect(effect)));
        }

    }
}
