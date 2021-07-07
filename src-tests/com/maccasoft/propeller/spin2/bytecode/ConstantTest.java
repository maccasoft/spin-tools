/*
 * Copyright (c) 2021 Marco Maccaferri and others.
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

import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.spin2.Spin2Context;

class ConstantTest {

    @Test
    void testSmallNumbers() {
        Constant subject = new Constant(new Spin2Context(), new NumberLiteral(-1));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0xA0
        }, subject.getBytes());

        subject = new Constant(new Spin2Context(), new NumberLiteral(0));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0xA1
        }, subject.getBytes());

        subject = new Constant(new Spin2Context(), new NumberLiteral(1));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0xA2
        }, subject.getBytes());

        subject = new Constant(new Spin2Context(), new NumberLiteral(2));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0xA3
        }, subject.getBytes());

        subject = new Constant(new Spin2Context(), new NumberLiteral(3));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0xA4
        }, subject.getBytes());

        subject = new Constant(new Spin2Context(), new NumberLiteral(4));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0xA5
        }, subject.getBytes());

        subject = new Constant(new Spin2Context(), new NumberLiteral(5));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0xA6
        }, subject.getBytes());

        subject = new Constant(new Spin2Context(), new NumberLiteral(6));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0xA7
        }, subject.getBytes());

        subject = new Constant(new Spin2Context(), new NumberLiteral(7));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0xA8
        }, subject.getBytes());

        subject = new Constant(new Spin2Context(), new NumberLiteral(8));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0xA9
        }, subject.getBytes());

        subject = new Constant(new Spin2Context(), new NumberLiteral(9));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0xAA
        }, subject.getBytes());

        subject = new Constant(new Spin2Context(), new NumberLiteral(10));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0xAB
        }, subject.getBytes());

        subject = new Constant(new Spin2Context(), new NumberLiteral(11));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0xAC
        }, subject.getBytes());

        subject = new Constant(new Spin2Context(), new NumberLiteral(12));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0xAD
        }, subject.getBytes());

        subject = new Constant(new Spin2Context(), new NumberLiteral(13));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0xAE
        }, subject.getBytes());

        subject = new Constant(new Spin2Context(), new NumberLiteral(14));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0xAF
        }, subject.getBytes());
    }

    @Test
    void testByteConstant() {
        Constant subject = new Constant(new Spin2Context(), new NumberLiteral(0x12));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x45, (byte) 0x12
        }, subject.getBytes());

        subject = new Constant(new Spin2Context(), new NumberLiteral(0x10));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x45, (byte) 0x10
        }, subject.getBytes());
    }

    @Test
    void testWordConstant() {
        Constant subject = new Constant(new Spin2Context(), new NumberLiteral(0x1234));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x47, (byte) 0x34, (byte) 0x12
        }, subject.getBytes());
    }

    @Test
    void testLongConstant() {
        Constant subject = new Constant(new Spin2Context(), new NumberLiteral(0x12345678));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x49, (byte) 0x78, (byte) 0x56, (byte) 0x34, (byte) 0x12
        }, subject.getBytes());
    }

    @Test
    void testDecodConstant() {
        Constant subject = new Constant(new Spin2Context(), new NumberLiteral(0x10000));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x4A, (byte) 0x10
        }, subject.getBytes());
    }

    @Test
    void testDecodConstantNot() {
        Constant subject = new Constant(new Spin2Context(), new NumberLiteral(~0x10000));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x4B, (byte) 0x10
        }, subject.getBytes());
    }

    @Test
    void testBMaskConstant() {
        Constant subject = new Constant(new Spin2Context(), new NumberLiteral(0x1FF));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x4C, (byte) 0x08
        }, subject.getBytes());
    }

    @Test
    void testBMaskConstantNot() {
        Constant subject = new Constant(new Spin2Context(), new NumberLiteral(~0x1FF));
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x4D, (byte) 0x08
        }, subject.getBytes());
    }

}
