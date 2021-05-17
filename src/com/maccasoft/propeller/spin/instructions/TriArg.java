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

/*
 * OPCODE  D,{#}S,#N
 */
public class TriArg extends Spin2PAsmInstructionFactory {

    int opcode;
    boolean s_only;

    public TriArg(int opcode, boolean s_only) {
        this.opcode = opcode;
        this.s_only = s_only;
    }

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, List<Spin2PAsmExpression> arguments, String effect) {
        if (arguments.size() == 3 && effect == null) {
            return new TriArg_(context, arguments.get(0), arguments.get(1), arguments.get(2));
        }
        if (arguments.size() == 1 && effect == null) {
            if (s_only) {
                return new TriArg_S_(context, arguments.get(0));
            }
            return new TriArg_D_(context, arguments.get(0));
        }
        throw new RuntimeException("Invalid arguments");
    }

    public class TriArg_ extends Spin2InstructionObject {

        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;
        Spin2PAsmExpression idx;

        public TriArg_(Spin2Context context, Spin2PAsmExpression dst, Spin2PAsmExpression src, Spin2PAsmExpression idx) {
            super(context);
        }

        // EEEE xxxxxxN NNI DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = n.setValue(l.setBoolean(s.setValue(d.setValue(opcode, dst.getInteger()), src.getInteger()), src.isLiteral()), idx.getInteger());
            if (src.isLongLiteral()) {
                byte[] prefix = new Augs.Augs_(context, src).getBytes();
                return getBytes(prefix, value);
            }
            return getBytes(value);
        }

    }

    public class TriArg_D_ extends Spin2InstructionObject {

        Spin2PAsmExpression dst;

        public TriArg_D_(Spin2Context context, Spin2PAsmExpression dst) {
            super(context);
            this.dst = dst;
        }

        // EEEE xxxxxx0 000 DDDDDDDDD 000000000

        @Override
        public byte[] getBytes() {
            return getBytes(d.setValue(opcode, dst.getInteger()));
        }

    }

    public class TriArg_S_ extends Spin2InstructionObject {

        Spin2PAsmExpression src;

        public TriArg_S_(Spin2Context context, Spin2PAsmExpression src) {
            super(context);
        }

        // EEEE xxxxxx0 00I 000000000 SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = l.setBoolean(s.setValue(opcode, src.getInteger()), src.isLiteral());
            if (src.isLongLiteral()) {
                byte[] prefix = new Augs.Augs_(context, src).getBytes();
                return getBytes(prefix, value);
            }
            return getBytes(value);
        }

    }
}
