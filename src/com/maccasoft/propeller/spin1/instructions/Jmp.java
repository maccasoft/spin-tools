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

import com.maccasoft.propeller.CompilerMessage;
import com.maccasoft.propeller.spin1.Spin1Context;
import com.maccasoft.propeller.spin1.Spin1InstructionObject;
import com.maccasoft.propeller.spin1.Spin1PAsmExpression;
import com.maccasoft.propeller.spin1.Spin1PAsmInstructionFactory;
import com.maccasoft.propeller.spin1.Spin1PAsmSchema;

public class Jmp extends Spin1PAsmInstructionFactory {

    @Override
    public Spin1InstructionObject createObject(Spin1Context context, String condition, List<Spin1PAsmExpression> arguments, String effect) {
        if (Spin1PAsmSchema.S.check(arguments, effect)) {
            return new Jmp_(context, condition, arguments.get(0), effect);
        }
        throw new RuntimeException("error: invalid arguments");
    }

    /*
     * JMP     {#}S
     */
    public class Jmp_ extends Spin1InstructionObject {

        String condition;
        Spin1PAsmExpression src;
        String effect;

        public Jmp_(Spin1Context context, String condition, Spin1PAsmExpression src, String effect) {
            super(context);
            this.condition = condition;
            this.src = src;
            this.effect = effect;
        }

        // 010111_000i_1111_ddddddddd_sssssssss

        @Override
        public byte[] getBytes() {
            int value = instr.setValue(0, 0b010111);
            value = con.setValue(value, encodeCondition(condition));
            value = zcr.setValue(value, encodeEffect(0b000, effect));
            value = i.setBoolean(value, src.isLiteral());
            if (src.getInteger() > 0x1FF) {
                throw new CompilerMessage("Source register/constant cannot exceed $1FF", src.getExpression().getData());
            }
            value = s.setValue(value, src.getInteger());
            return getBytes(value);
        }

    }
}
