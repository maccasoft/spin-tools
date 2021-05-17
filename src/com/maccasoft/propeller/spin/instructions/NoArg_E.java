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
 * OPCODE         {WC/WZ/WCZ}
 */
public class NoArg_E extends Spin2PAsmInstructionFactory {

    int opcode;

    public NoArg_E(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.WC_WZ_WCZ.check(arguments, effect)) {
            return new NoArg_(context, effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    public class NoArg_ extends Spin2InstructionObject {

        String effect;

        public NoArg_(Spin2Context context, String effect) {
            super(context);
            this.effect = effect;
        }

        @Override
        public byte[] getBytes() {
            return getBytes(czi.setValue(opcode, encodeEffect(effect)));
        }

    }
}
