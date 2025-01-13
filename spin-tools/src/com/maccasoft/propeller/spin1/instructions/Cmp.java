/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
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
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin1.Spin1InstructionObject;
import com.maccasoft.propeller.spin1.Spin1PAsmExpression;
import com.maccasoft.propeller.spin1.Spin1PAsmInstructionFactory;
import com.maccasoft.propeller.spin1.Spin1PAsmLine;
import com.maccasoft.propeller.spin1.Spin1PAsmSchema;

public class Cmp extends Spin1PAsmInstructionFactory {

    @Override
    public Spin1InstructionObject createObject(Spin1PAsmLine line) {
        if (Spin1PAsmSchema.D_S_WC_WZ.check(line.getArguments(), line.getEffect())) {
            if (line.getEffect() == null) {
                line.addAnnotation(new CompilerException(CompilerException.WARNING, "instruction " + line.getMnemonic() + " used without flags being set", line.getData()));
            }
            return new Cmp_(line);
        }
        throw new RuntimeException("Invalid arguments");
    }

    @Override
    public Spin1InstructionObject createObject(Context context, String condition, List<Spin1PAsmExpression> arguments, String effect) {
        if (Spin1PAsmSchema.D_S_WC_WZ.check(arguments, effect)) {
            return new Cmp_(context, condition, arguments.get(0), arguments.get(1), effect);
        }
        throw new RuntimeException("Invalid arguments");
    }

    /*
     * CMP     D,{#}S   {WC/WZ/NR/WR}
     */
    public class Cmp_ extends Spin1InstructionObject {

        String condition;
        Spin1PAsmExpression dst;
        Spin1PAsmExpression src;
        String effect;

        public Cmp_(Spin1PAsmLine line) {
            super(line.getScope());
            this.condition = line.getCondition();
            this.dst = line.getArguments().get(0);
            this.src = line.getArguments().get(1);
            this.effect = line.getEffect();
        }

        public Cmp_(Context context, String condition, Spin1PAsmExpression dst, Spin1PAsmExpression src, String effect) {
            super(context);
            this.condition = condition;
            this.dst = dst;
            this.src = src;
            this.effect = effect;
        }

        // 100001_000i_1111_ddddddddd_sssssssss

        @Override
        public byte[] getBytes() {
            int value = instr.setValue(encodeInstructionParameters(condition, dst, src, null), 0b100001);
            value = zcr.setValue(value, encodeEffect(0b000, effect));
            return getBytes(value);
        }

    }
}
