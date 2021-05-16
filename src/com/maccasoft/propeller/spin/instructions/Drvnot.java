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
import com.maccasoft.propeller.spin.Spin2PAsmLine;

public class Drvnot extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, List<Spin2PAsmExpression> arguments) {
        if (arguments.size() == 1) {
            return new Drvnot_(context, arguments.get(0));
        }
        throw new RuntimeException("Invalid arguments");
    }

    @Override
    public List<Spin2PAsmLine> expand(Spin2PAsmLine line) {
        return augdExpand(line, line.getArguments().get(0));
    }

    public static class Drvnot_ extends Spin2InstructionObject {

        Spin2PAsmExpression argument;

        public Drvnot_(Spin2Context context, Spin2PAsmExpression argument) {
            super(context);
            this.argument = argument;
        }

        // EEEE 1101011 CZL DDDDDDDDD 001011111

        @Override
        public byte[] getBytes() {
            int value = encode(0b1101011, false, false, argument.isLiteral(), 0b000000000, 0b001011111);
            return getBytes(d.setValue(value, argument.getInteger()));
        }

    }
}
