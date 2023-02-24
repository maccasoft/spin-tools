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
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin2.Spin2GlobalContext;
import com.maccasoft.propeller.spin2.Spin2Object;
import com.maccasoft.propeller.spin2.Spin2ObjectCompiler;
import com.maccasoft.propeller.spin2.Spin2Parser;
import com.maccasoft.propeller.spin2.Spin2TokenStream;

class InstructionsTest {

    @Test
    void testGetbyte() throws Exception {
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0xF8, (byte) 0xF8
        }, compile("DAT\n  getbyte 1,2,#3\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0xFC, (byte) 0xF8
        }, compile("DAT\n  getbyte 1,#2,#3\n"));
    }

    @Test
    void testMov() throws Exception {
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x00, (byte) 0xF6
        }, compile("DAT\n  mov 1,2\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x10, (byte) 0xF6
        }, compile("DAT\n  mov 1,2 wc\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x08, (byte) 0xF6
        }, compile("DAT\n  mov 1,2 wz\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x18, (byte) 0xF6
        }, compile("DAT\n  mov 1,2 wcz\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x04, (byte) 0xF6
        }, compile("DAT\n  mov 1,#2\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x14, (byte) 0xF6
        }, compile("DAT\n  mov 1,#2 wc\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x0C, (byte) 0xF6
        }, compile("DAT\n  mov 1,#2 wz\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x1C, (byte) 0xF6
        }, compile("DAT\n  mov 1,#2 wcz\n"));
    }

    @Test
    void testTestb() throws Exception {
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x10, (byte) 0xF4
        }, compile("DAT\n  testb 1,2 wc\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x50, (byte) 0xF4
        }, compile("DAT\n  testb 1,2 andc\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x90, (byte) 0xF4
        }, compile("DAT\n  testb 1,2 orc\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0xD0, (byte) 0xF4
        }, compile("DAT\n  testb 1,2 xorc\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x08, (byte) 0xF4
        }, compile("DAT\n  testb 1,2 wz\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x48, (byte) 0xF4
        }, compile("DAT\n  testb 1,2 andz\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x88, (byte) 0xF4
        }, compile("DAT\n  testb 1,2 orz\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0xC8, (byte) 0xF4
        }, compile("DAT\n  testb 1,2 xorz\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x14, (byte) 0xF4
        }, compile("DAT\n  testb 1,#2 wc\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x54, (byte) 0xF4
        }, compile("DAT\n  testb 1,#2 andc\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x94, (byte) 0xF4
        }, compile("DAT\n  testb 1,#2 orc\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0xD4, (byte) 0xF4
        }, compile("DAT\n  testb 1,#2 xorc\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x0C, (byte) 0xF4
        }, compile("DAT\n  testb 1,#2 wz\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x4C, (byte) 0xF4
        }, compile("DAT\n  testb 1,#2 andz\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0x8C, (byte) 0xF4
        }, compile("DAT\n  testb 1,#2 orz\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x02, (byte) 0x02, (byte) 0xCC, (byte) 0xF4
        }, compile("DAT\n  testb 1,#2 xorz\n"));
    }

    @Test
    void testMod() throws Exception {
        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x6F, (byte) 0xA0, (byte) 0x64, (byte) 0xFD
        }, compile("DAT\n  modc _nz\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x6F, (byte) 0x06, (byte) 0x64, (byte) 0xFD
        }, compile("DAT\n  modz _nc\n"));

        Assertions.assertArrayEquals(new byte[] {
            (byte) 0x6F, (byte) 0xA6, (byte) 0x64, (byte) 0xFD
        }, compile("DAT\n  modcz _nz,_nc\n"));
    }

    byte[] compile(String text) throws Exception {
        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser subject = new Spin2Parser(stream);
        Node root = subject.parse();

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2GlobalContext(), Collections.emptyMap());
        Spin2Object obj = compiler.compileObject(root);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateBinary(os);
        return os.toByteArray();
    }

}
