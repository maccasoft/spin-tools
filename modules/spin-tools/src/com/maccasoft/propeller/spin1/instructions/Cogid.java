/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1.instructions;

import java.util.List;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.spin1.Spin1Context;
import com.maccasoft.propeller.spin1.Spin1InstructionObject;
import com.maccasoft.propeller.spin1.Spin1PAsmExpression;
import com.maccasoft.propeller.spin1.Spin1PAsmInstructionFactory;
import com.maccasoft.propeller.spin1.Spin1PAsmSchema;

public class Cogid extends Spin1PAsmInstructionFactory {

    @Override
    public Spin1InstructionObject createObject(Spin1Context context, String condition, List<Spin1PAsmExpression> arguments, String effect) {
        if (Spin1PAsmSchema.D.check(arguments, effect)) {
            return new Cogid_(context, condition, arguments.get(0));
        }
        throw new RuntimeException("error: invalid arguments");
    }

    /*
     * COGID   D
     */
    public class Cogid_ extends Spin1InstructionObject {

        String condition;
        Spin1PAsmExpression dst;

        public Cogid_(Spin1Context context, String condition, Spin1PAsmExpression dst) {
            super(context);
            this.condition = condition;
            this.dst = dst;
        }

        // 000011_0011_1111_ddddddddd_xxxxxx001

        @Override
        public byte[] getBytes() {
            int value = instr.setValue(0, 0b000011);
            value = con.setValue(value, encodeCondition(condition));
            value = zcr.setValue(value, 0b001);
            value = i.setBoolean(value, true);
            if (dst.getInteger() > 0x1FF) {
                throw new CompilerException("Destination register cannot exceed $1FF", dst.getExpression().getData());
            }
            value = d.setValue(value, dst.getInteger());
            value = s.setValue(value, 0b001);
            return getBytes(value);
        }

    }
}
