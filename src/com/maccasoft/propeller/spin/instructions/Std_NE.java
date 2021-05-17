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
 * OPCODE D, {#}S
 * OPCODE D
 */
public class Std_NE extends Spin2PAsmInstructionFactory {

    int opcode;
    boolean s_opt;

    public Std_NE(int opcode, boolean s_opt) {
        this.opcode = opcode;
        this.s_opt = s_opt;
    }

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.D_S.check(arguments, effect)) {
            return new Std_D_S_NE_(context, arguments.get(0), arguments.get(1));
        }
        if (s_opt) {
            if (Spin2PAsmSchema.D.check(arguments, effect)) {
                return new Std_D_NE_(context, arguments.get(0));
            }
        }
        throw new RuntimeException("Invalid arguments");
    }

    public class Std_D_S_NE_ extends Spin2InstructionObject {

        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;

        public Std_D_S_NE_(Spin2Context context, Spin2PAsmExpression dst, Spin2PAsmExpression src) {
            super(context);
            this.dst = dst;
            this.src = src;
        }

        @Override
        public int resolve(int address) {
            super.resolve(address);
            return address + (src.isLongLiteral() ? 2 : 1);
        }

        @Override
        public int getSize() {
            return src.isLongLiteral() ? 8 : 4;
        }

        // EEEE xxxxxxx xxI DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = i.setBoolean(s.setValue(d.setValue(opcode, dst.getInteger()), src.getInteger()), src.isLiteral());
            if (src.isLongLiteral()) {
                byte[] prefix = new Augs.Augs_(context, src).getBytes();
                return getBytes(prefix, value);
            }
            return getBytes(value);
        }

    }

    public class Std_D_NE_ extends Spin2InstructionObject {

        Spin2PAsmExpression dst;

        public Std_D_NE_(Spin2Context context, Spin2PAsmExpression dst) {
            super(context);
            this.dst = dst;
        }

        // EEEE xxxxxxx xx0 DDDDDDDDD DDDDDDDDD

        @Override
        public byte[] getBytes() {
            return getBytes(s.setValue(d.setValue(opcode, dst.getInteger()), dst.getInteger()));
        }

    }
}
