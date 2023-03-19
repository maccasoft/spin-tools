/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.instructions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin2.Spin2Compiler;
import com.maccasoft.propeller.spin2.Spin2Object;
import com.maccasoft.propeller.spin2.Spin2ObjectCompiler;
import com.maccasoft.propeller.spin2.Spin2Parser;
import com.maccasoft.propeller.spin2.Spin2TokenStream;

class DataTypeTest {

    @Test
    void testByte() throws Exception {
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04
        }, compile("DAT\n  byte 1,2,3,4\n"));
    }

    @Test
    void testWord() throws Exception {
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0x00,
            (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x00
        }, compile("DAT\n  word 1,2,3,4\n"));
    }

    @Test
    void testLong() throws Exception {
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        }, compile("DAT\n  long 1,2,3,4\n"));
    }

    @Test
    void testByteSize() throws Exception {
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x01, (byte) 0x01, (byte) 0x03, (byte) 0x03,
            (byte) 0x03, (byte) 0x03,
        }, compile("DAT\n  byte 1[2],3[4]\n"));
    }

    @Test
    void testWordSize() throws Exception {
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00,
            (byte) 0x03, (byte) 0x00, (byte) 0x03, (byte) 0x00,
            (byte) 0x03, (byte) 0x00, (byte) 0x03, (byte) 0x00,
        }, compile("DAT\n  word 1[2],3[4]\n"));
    }

    @Test
    void testLongSize() throws Exception {
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        }, compile("DAT\n  long 1[2],3[4]\n"));
    }

    byte[] compile(String text) throws Exception {
        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser subject = new Spin2Parser(stream);
        Node root = subject.parse();

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2Compiler(), new ArrayList<>());
        Spin2Object obj = compiler.compileObject(root);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateBinary(os);
        return os.toByteArray();
    }

}
