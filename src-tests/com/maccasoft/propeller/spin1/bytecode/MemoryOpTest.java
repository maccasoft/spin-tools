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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.spin1.Spin1Context;

class MemoryOpTest {

    @Test
    void testByteMemoryOp() {
        MemoryOp op = new MemoryOp(new Spin1Context(), MemoryOp.Size.Byte, true, MemoryOp.Base.Pop, MemoryOp.Op.Read, null);

        Assertions.assertEquals("90", toString(op.getBytes()));
        Assertions.assertEquals("MEM_READ BYTE POP", op.toString());
    }

    @Test
    void testWordMemoryOp() {
        MemoryOp op = new MemoryOp(new Spin1Context(), MemoryOp.Size.Word, true, MemoryOp.Base.Pop, MemoryOp.Op.Read, null);

        Assertions.assertEquals("B0", toString(op.getBytes()));
        Assertions.assertEquals("MEM_READ WORD POP", op.toString());
    }

    @Test
    void testLongMemoryOp() {
        MemoryOp op = new MemoryOp(new Spin1Context(), MemoryOp.Size.Long, true, MemoryOp.Base.Pop, MemoryOp.Op.Read, null);

        Assertions.assertEquals("D0", toString(op.getBytes()));
        Assertions.assertEquals("MEM_READ LONG POP", op.toString());
    }

    String toString(byte[] b) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < b.length; i++) {
            if (i != 0) {
                sb.append(" ");
            }
            sb.append(String.format("%02X", b[i] & 0xFF));
        }

        return sb.toString();
    }
}
