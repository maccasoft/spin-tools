/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.bytecode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.spin2.Spin2Bytecode;

class ConstantTest {

    @Test
    void testSmallNumbers() {
        byte expect = (byte) Spin2Bytecode.bc_con_n;

        for (int i = -1; i <= 14; i++) {
            Constant subject = new Constant(new Context(), new NumberLiteral(i));
            Assertions.assertArrayEquals(new byte[] {
                expect
            }, subject.getBytes(), String.format("i=%d, bc=$%02X", i, expect));
            expect++;
        }
    }

    @Test
    void testByteConstant() {
        Constant subject = new Constant(new Context(), new NumberLiteral(0x12));
        Assertions.assertArrayEquals(new byte[] {
            (byte) Spin2Bytecode.bc_con_rfbyte, (byte) 0x12
        }, subject.getBytes());

        subject = new Constant(new Context(), new NumberLiteral(0x10));
        Assertions.assertArrayEquals(new byte[] {
            (byte) Spin2Bytecode.bc_con_rfbyte, (byte) 0x10
        }, subject.getBytes());
    }

    @Test
    void testByteConstantNot() {
        Constant subject = new Constant(new Context(), new NumberLiteral(0xFFFFFF12));
        Assertions.assertArrayEquals(new byte[] {
            (byte) Spin2Bytecode.bc_con_rfbyte_not, (byte) (0x12 ^ 0xFF)
        }, subject.getBytes());
    }

    @Test
    void testWordConstant() {
        Constant subject = new Constant(new Context(), new NumberLiteral(0x1234));
        Assertions.assertArrayEquals(new byte[] {
            (byte) Spin2Bytecode.bc_con_rfword, (byte) 0x34, (byte) 0x12
        }, subject.getBytes());
    }

    @Test
    void testWordConstantNot() {
        Constant subject = new Constant(new Context(), new NumberLiteral(0xFFFF1234));
        Assertions.assertArrayEquals(new byte[] {
            (byte) Spin2Bytecode.bc_con_rfword_not, (byte) (0x34 ^ 0xFF), (byte) (0x12 ^ 0xFF)
        }, subject.getBytes());
    }

    @Test
    void testLongConstant() {
        Constant subject = new Constant(new Context(), new NumberLiteral(0x12345678));
        Assertions.assertArrayEquals(new byte[] {
            (byte) Spin2Bytecode.bc_con_rflong, (byte) 0x78, (byte) 0x56, (byte) 0x34, (byte) 0x12
        }, subject.getBytes());
    }

    @Test
    void testDecodByteConstant() {
        Constant subject = new Constant(new Context(), new NumberLiteral(0x10000));
        Assertions.assertArrayEquals(new byte[] {
            (byte) Spin2Bytecode.bc_con_rfbyte_decod, (byte) 0x10
        }, subject.getBytes());
    }

    @Test
    void testDecodByteConstantNot() {
        Constant subject = new Constant(new Context(), new NumberLiteral(~0x10000));
        Assertions.assertArrayEquals(new byte[] {
            (byte) Spin2Bytecode.bc_con_rfbyte_decod_not, (byte) 0x10
        }, subject.getBytes());
    }

    @Test
    void testBMaskConstant() {
        Constant subject = new Constant(new Context(), new NumberLiteral(0x1FF));
        Assertions.assertArrayEquals(new byte[] {
            (byte) Spin2Bytecode.bc_con_rfbyte_bmask, (byte) 0x08
        }, subject.getBytes());
    }

    @Test
    void testBMaskConstantNot() {
        Constant subject = new Constant(new Context(), new NumberLiteral(~0x1FF));
        Assertions.assertArrayEquals(new byte[] {
            (byte) Spin2Bytecode.bc_con_rfbyte_bmask_not, (byte) 0x08
        }, subject.getBytes());
    }

}
