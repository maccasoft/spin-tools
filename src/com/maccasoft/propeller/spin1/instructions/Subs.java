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

import com.maccasoft.propeller.spin1.Spin1Context;
import com.maccasoft.propeller.spin1.Spin1InstructionObject;
import com.maccasoft.propeller.spin1.Spin1PAsmExpression;
import com.maccasoft.propeller.spin1.Spin1PAsmInstructionFactory;
import com.maccasoft.propeller.spin1.Spin1PAsmSchema;

public class Subs extends Spin1PAsmInstructionFactory {

    @Override
    public Spin1InstructionObject createObject(Spin1Context context, String condition, List<Spin1PAsmExpression> arguments, String effect) {
        if (Spin1PAsmSchema.D_S.check(arguments, effect)) {
            return new Subs_(context, condition, arguments.get(0), arguments.get(1), effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * SUBS    D,{#}S   {WC/WZ/NR/WR}
     */
    public class Subs_ extends Spin1InstructionObject {

        String condition;
        Spin1PAsmExpression dst;
        Spin1PAsmExpression src;
        String effect;

        public Subs_(Spin1Context context, String condition, Spin1PAsmExpression dst, Spin1PAsmExpression src, String effect) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.src = src;
            this.effect = effect;
        }

        // 110101_001i_1111_ddddddddd_sssssssss

        @Override
        public byte[] getBytes() {
            int value = instr.setValue(0, 0b110101);
            value = con.setValue(value, encodeCondition(condition));
            value = zcr.setValue(value, encodeEffect(effect));
            value = i.setBoolean(value, src.isLiteral());
            value = d.setValue(value, dst.getInteger());
            value = s.setValue(value, src.getInteger());
            return getBytes(value);
        }

    }
}
