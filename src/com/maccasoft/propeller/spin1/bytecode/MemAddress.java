/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1.bytecode;

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.spin1.Spin1BytecodeFactory;
import com.maccasoft.propeller.spin1.Spin1BytecodeObject;
import com.maccasoft.propeller.spin1.Spin1Context;

public class MemAddress extends Spin1BytecodeFactory {

    @Override
    public Spin1BytecodeObject createObject(Spin1Context context, Expression expression) {
        return new MemAddress_(context, expression);
    }

    class MemAddress_ extends Spin1BytecodeObject {

        Expression expression;

        public MemAddress_(Spin1Context context, Expression expression) {
            super(context);
            this.expression = expression;
        }

        @Override
        public int getSize() {
            return 2;
        }

        @Override
        public byte[] getBytes() {
            int addr = expression.getNumber().intValue() - 0x0010;

            //           +------------- memory op
            //           | ++---------- 00 byte, 01 word, 10 long
            //           | || +-------- 0 none, 1 pop
            //           | || | ++----- 00 pop, 01 pbase, 10 vbase, 11 dbase
            //           | || | || ++-- 00 read, 01 write, 10 assign, 11 address
            //           | || | || ||
            if (addr < 127) {
                return new byte[] {
                    (byte) 0b1_10_0_01_11,
                    (byte) addr,
                };
            }
            else {
                return new byte[] {
                    (byte) 0b1_10_0_01_11,
                    (byte) (0x80 | (addr >> 8)),
                    (byte) addr,
                };
            }
        }

        @Override
        public String toString() {
            return String.format("MEM_ADDRESS LONG PBASE+$%04X", expression.getNumber().intValue() - 0x0010);
        }

    }

}
