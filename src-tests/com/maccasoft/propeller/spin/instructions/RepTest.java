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

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.spin.Spin2Compiler;
import com.maccasoft.propeller.spin.Spin2InstructionObject;
import com.maccasoft.propeller.spin.Spin2Lexer;
import com.maccasoft.propeller.spin.Spin2Parser;

@SuppressWarnings("unchecked")
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
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Lexer lexer = new Spin2Lexer(CharStreams.fromString(text));
        //lexer.removeErrorListeners();
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Spin2Parser parser = new Spin2Parser(tokens);
        //parser.removeErrorListeners();

        parser.prog().accept(compiler);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        compiler.generateObjectCode(os);
        return os.toByteArray();
    }

}
