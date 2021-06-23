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
import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionFactory;
import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionObject;
import com.maccasoft.propeller.spin1.Spin1BytecodeLine;
import com.maccasoft.propeller.spin1.Spin1Context;

public class MemAddress extends Spin1BytecodeInstructionFactory {

    @Override
    public Spin1BytecodeInstructionObject createObject(Spin1BytecodeLine line) {
        return new MemAddress_(line.getScope(), new com.maccasoft.propeller.expressions.Identifier(line.getMnemonic(), line.getScope()));
    }

    class MemAddress_ extends Spin1BytecodeInstructionObject {

        Expression expression;

        public MemAddress_(Spin1Context context, Expression expression) {
            super(context);
            this.expression = expression;
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
                    (byte) 0b1_10_0_01_00,
                    (byte) addr,
                };
            }
            else {
                return new byte[] {
                    (byte) 0b1_10_0_01_00,
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
