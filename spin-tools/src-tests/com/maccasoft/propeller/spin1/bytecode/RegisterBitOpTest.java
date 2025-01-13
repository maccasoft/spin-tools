/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
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

import com.maccasoft.propeller.expressions.Context;

class RegisterBitOpTest {

    @Test
    void testRead() {
        RegisterBitOp op = new RegisterBitOp(new Context(), RegisterBitOp.Op.Read, false, 0x1F6);
        Assertions.assertEquals("3D 96", toString(op.getBytes()));
        Assertions.assertEquals("REGBIT_READ $1F6", op.toString());
    }

    @Test
    void testWrite() {
        RegisterBitOp op = new RegisterBitOp(new Context(), RegisterBitOp.Op.Write, false, 0x1F6);
        Assertions.assertEquals("3D B6", toString(op.getBytes()));
        Assertions.assertEquals("REGBIT_WRITE $1F6", op.toString());
    }

    @Test
    void testAssign() {
        RegisterBitOp op = new RegisterBitOp(new Context(), RegisterBitOp.Op.Assign, false, 0x1F6);
        Assertions.assertEquals("3D D6", toString(op.getBytes()));
        Assertions.assertEquals("REGBIT_MODIFY $1F6", op.toString());
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
