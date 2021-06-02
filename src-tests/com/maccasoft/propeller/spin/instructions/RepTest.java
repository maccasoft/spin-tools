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

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.spin.Spin2Compiler;
import com.maccasoft.propeller.spin.Spin2InstructionObject;
import com.maccasoft.propeller.spin.Spin2Parser;
import com.maccasoft.propeller.spin.Spin2Parser.Node;
import com.maccasoft.propeller.spin.Spin2TokenStream;

class RepTest {

    @Test
    void testLiteral() throws Exception {
        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x10, (byte) 0x30, (byte) 0xDC, (byte) 0xFC
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10\n    rep #$18,#$10\n")));
    }

    @Test
    void testEndAddressLiteral() throws Exception {
        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x10, (byte) 0x10, (byte) 0xDC, (byte) 0xFC
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10\n    rep #@l18,#$10\n    org $18\nl18\n")));
    }

    @Test
    void testEndAddress() throws Exception {
        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x10, (byte) 0x0E, (byte) 0xDC, (byte) 0xFC
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10\n    rep @l18,#$10\n    org $18\nl18\n")));
    }

    byte[] compile(String text) throws Exception {
        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser subject = new Spin2Parser(stream);
        Node root = subject.parse();

        Spin2Compiler compiler = new Spin2Compiler();
        compiler.compile(root);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        compiler.generateObjectCode(os);
        return os.toByteArray();
    }

}
