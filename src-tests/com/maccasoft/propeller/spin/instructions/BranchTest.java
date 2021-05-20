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
class BranchTest {

    @Test
    void testCalld() throws Exception {
        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x23, (byte) 0x02, (byte) 0x20, (byte) 0xFB
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10  calld $1,$23\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x12, (byte) 0x02, (byte) 0x24, (byte) 0xFB
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10\n    calld $1,#$23\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x48, (byte) 0x04, (byte) 0x10, (byte) 0xFE
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10\n    calld pa,#$123\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x48, (byte) 0x04, (byte) 0x30, (byte) 0xFE
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10\n    calld pb,#$123\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x48, (byte) 0x04, (byte) 0x50, (byte) 0xFE
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10\n    calld ptra,#$123\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x48, (byte) 0x04, (byte) 0x70, (byte) 0xFE
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10\n    calld ptrb,#$123\n")));
    }

    @Test
    void testCallpa() throws Exception {
        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x23, (byte) 0x02, (byte) 0x40, (byte) 0xFB
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10\n    callpa $1,$23\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x12, (byte) 0x02, (byte) 0x44, (byte) 0xFB
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10\n    callpa $1,#$23\n")));
    }

    @Test
    void testCallpb() throws Exception {
        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x23, (byte) 0x02, (byte) 0x50, (byte) 0xFB
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10\n    callpb $1,$23\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x12, (byte) 0x02, (byte) 0x54, (byte) 0xFB
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10\n    callpb $1,#$23\n")));
    }

    @Test
    void testJmp() throws Exception {
        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x04, (byte) 0x00, (byte) 0x90, (byte) 0xFD
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10\n    jmp #$12\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x12, (byte) 0x00, (byte) 0x80, (byte) 0xFD
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10\n    jmp #\\$12\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0xC0, (byte) 0xFF, (byte) 0x9F, (byte) 0xFD
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10\n    jmp #$1\n")));
    }

    @Test
    void testJmprel() throws Exception {
        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x30, (byte) 0x24, (byte) 0x60, (byte) 0xFD
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10\n    jmprel $12\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x30, (byte) 0x24, (byte) 0x64, (byte) 0xFD
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10\n    jmprel #$12\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x30, (byte) 0x02, (byte) 0x64, (byte) 0xFD
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  org $10\n    jmprel #$1\n")));
    }

    byte[] compile(String text) throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Lexer lexer = new Spin2Lexer(CharStreams.fromString(text));
        lexer.removeErrorListeners();
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Spin2Parser parser = new Spin2Parser(tokens);
        parser.removeErrorListeners();

        parser.prog().accept(compiler);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        compiler.generateObjectCode(os);
        return os.toByteArray();
    }

}