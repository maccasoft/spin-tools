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

/*
 * OPCODE  {#}D           {WCZ}
 */
public class Pins extends Spin2PAsmInstructionFactory {

    int opcode;

    public Pins(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, List<Spin2PAsmExpression> arguments, String effect) {
        if (arguments.size() == 1 && (effect == null || Spin2PAsmSchema.E_WCZ.contains(effect.toLowerCase()))) {
            return new Pins_(context, arguments.get(0), effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    public class Pins_ extends Spin2InstructionObject {

        Spin2PAsmExpression dst;
        String effect;

        public Pins_(Spin2Context context, Spin2PAsmExpression dst, String effect) {
            super(context);
            this.dst = dst;
            this.effect = effect;
        }

        // EEEE xxxxxxx CZL DDDDDDDDD xxxxxxxxx

        @Override
        public byte[] getBytes() {
            int value = cz.setValue(l.setBoolean(d.setValue(opcode, dst.getInteger()), dst.isLiteral()), encodeEffect(effect));
            if (dst.isLongLiteral()) {
                byte[] prefix = new Augd.Augd_(context, dst).getBytes();
                return getBytes(prefix, value);
            }
            return getBytes(value);
        }

    }
}
