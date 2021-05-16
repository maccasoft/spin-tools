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

public class Addct1 extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, List<Spin2PAsmExpression> arguments) {
        if (arguments.size() == 2) {
            return new Addct1_(context, arguments.get(0), arguments.get(1));
        }
        throw new RuntimeException("Invalid arguments");
    }

    public static class Addct1_ extends Spin2InstructionObject {

        Spin2PAsmExpression dst;
        Spin2PAsmExpression src;

        public Addct1_(Spin2Context context, Spin2PAsmExpression dst, Spin2PAsmExpression src) {
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

        // EEEE 1010011 00I DDDDDDDDD SSSSSSSSS

        @Override
        public byte[] getBytes() {
            int value = encode(0b1010011, false, false, src.isLiteral(), dst.getInteger(), src.getInteger());
            if (src.isLongLiteral()) {
                byte[] prefix = new Augs.Augs_(context, src).getBytes();
                return getBytes(prefix, value);
            }
            return getBytes(value);
        }

    }
}
