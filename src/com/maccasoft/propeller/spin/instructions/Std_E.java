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
 * OPCODE D, {#}S {WC/WZ/WCZ}
 * OPCODE D       {WC/WZ/WCZ}
 */
public class Std_E extends Spin2PAsmInstructionFactory {

    int opcode;
    boolean s_opt;

    public Std_E(int opcode, boolean s_opt) {
        this.opcode = opcode;
        this.s_opt = s_opt;
    }

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, List<Spin2PAsmExpression> arguments, String effect) {
        if (Spin2PAsmSchema.D_S_WC_WZ_WCZ.check(arguments, effect)) {
            return new Std_D_S_(context, arguments.get(0), arguments.get(1), effect);
        }
        if (s_opt) {
            if (Spin2PAsmSchema.D_WC_WZ_WCZ.check(arguments, effect)) {
                return new Std_D_(context, arguments.get(0), effect);
            }
        }
        throw new RuntimeException("Invalid arguments");
    }

    public class Std_D_S_ extends Spin2InstructionObject {

        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;
        String effect;

        public Std_D_S_(Spin2Context context, Spin2PAsmExpression dst, Spin2PAsmExpression src, String effect) {
            super(context);
            this.dst = dst;
            this.src = src;
            this.effect = effect;
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

        // EEEE xxxxxxx CZL DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = cz.setValue(l.setBoolean(s.setValue(d.setValue(opcode, dst.getInteger()), src.getInteger()), src.isLiteral()), encodeEffect(effect));
            if (src.isLongLiteral()) {
                byte[] prefix = new Augs.Augs_(context, src).getBytes();
                return getBytes(prefix, value);
            }
            return getBytes(value);
        }

    }

    public class Std_D_ extends Spin2InstructionObject {

        Spin2PAsmExpression dst;
        String effect;

        public Std_D_(Spin2Context context, Spin2PAsmExpression dst, String effect) {
            super(context);
            this.dst = dst;
            this.effect = effect;
        }

        // EEEE xxxxxxx CZ0 DDDDDDDDD DDDDDDDDD

        @Override
        public byte[] getBytes() {
            return getBytes(cz.setValue(s.setValue(d.setValue(opcode, dst.getInteger()), dst.getInteger()), encodeEffect(effect)));
        }

    }
}
