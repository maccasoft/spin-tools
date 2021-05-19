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
import com.maccasoft.propeller.spin.Spin2Context;
import com.maccasoft.propeller.spin.Spin2InstructionObject;
import com.maccasoft.propeller.spin.Spin2Lexer;
import com.maccasoft.propeller.spin.Spin2PAsmExpression;
import com.maccasoft.propeller.spin.Spin2Parser;

@SuppressWarnings("unchecked")
class PtrTest {

    @Test
    void testEncodePtr() throws Exception {
        Assertions.assertEquals(0b100000000, encodePtr("ptra"));
        Assertions.assertEquals(0b110000000, encodePtr("ptrb"));
        Assertions.assertEquals(0b100000011, encodePtr("ptra[3]"));
        Assertions.assertEquals(0b110000011, encodePtr("ptrb[3]"));
        Assertions.assertEquals(0b100111101, encodePtr("ptra[-3]"));
        Assertions.assertEquals(0b110111101, encodePtr("ptrb[-3]"));

        Assertions.assertEquals(0b101100001, encodePtr("ptra++"));
        Assertions.assertEquals(0b111100001, encodePtr("ptrb++"));
        Assertions.assertEquals(0b101111111, encodePtr("ptra--"));
        Assertions.assertEquals(0b111111111, encodePtr("ptrb--"));
        Assertions.assertEquals(0b101000001, encodePtr("++ptra"));
        Assertions.assertEquals(0b111000001, encodePtr("++ptrb"));
        Assertions.assertEquals(0b101011111, encodePtr("--ptra"));
        Assertions.assertEquals(0b111011111, encodePtr("--ptrb"));

        Assertions.assertEquals(0b101100011, encodePtr("ptra++[3]"));
        Assertions.assertEquals(0b111100011, encodePtr("ptrb++[3]"));
        Assertions.assertEquals(0b101111101, encodePtr("ptra--[3]"));
        Assertions.assertEquals(0b111111101, encodePtr("ptrb--[3]"));

        Assertions.assertEquals(0b101000011, encodePtr("++ptra[3]"));
        Assertions.assertEquals(0b111000011, encodePtr("++ptrb[3]"));
        Assertions.assertEquals(0b101011101, encodePtr("--ptra[3]"));
        Assertions.assertEquals(0b111011101, encodePtr("--ptrb[3]"));
    }

    int encodePtr(String str) {
        return new Spin2InstructionObjectMock().encodePtr(new Spin2PAsmExpression(null, null, null) {

            @Override
            public String toString() {
                return str;
            }

        });
    }

    class Spin2InstructionObjectMock extends Spin2InstructionObject {

        public Spin2InstructionObjectMock() {
            super(new Spin2Context());
        }

        @Override
        public int encodePtr(Spin2PAsmExpression expression) {
            return super.encodePtr(expression);
        }

        @Override
        public byte[] getBytes() {
            return null;
        }

    }

    @Test
    void testRdbyte() throws Exception {
        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x34, (byte) 0x24, (byte) 0xC0, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdbyte $12,$34\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x00, (byte) 0x25, (byte) 0xC4, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdbyte $12,ptra\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x61, (byte) 0x25, (byte) 0xC4, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdbyte $12,ptra++\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x7F, (byte) 0x25, (byte) 0xC4, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdbyte $12,ptra--\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x41, (byte) 0x25, (byte) 0xC4, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdbyte $12,++ptra\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x5F, (byte) 0x25, (byte) 0xC4, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdbyte $12,--ptra\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x03, (byte) 0x25, (byte) 0xC4, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdbyte $12,ptra[3]\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x3D, (byte) 0x25, (byte) 0xC4, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdbyte $12,ptra[-3]\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x63, (byte) 0x25, (byte) 0xC4, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdbyte $12,ptra[3]++\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x7D, (byte) 0x25, (byte) 0xC4, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdbyte $12,ptra[3]--\n")));
    }

    @Test
    void testRdword() throws Exception {
        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x34, (byte) 0x24, (byte) 0xE0, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdword $12,$34\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x00, (byte) 0x25, (byte) 0xE4, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdword $12,ptra\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x61, (byte) 0x25, (byte) 0xE4, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdword $12,ptra++\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x7F, (byte) 0x25, (byte) 0xE4, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdword $12,ptra--\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x41, (byte) 0x25, (byte) 0xE4, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdword $12,++ptra\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x5F, (byte) 0x25, (byte) 0xE4, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdword $12,--ptra\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x03, (byte) 0x25, (byte) 0xE4, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdword $12,ptra[3]\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x3D, (byte) 0x25, (byte) 0xE4, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdword $12,ptra[-3]\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x63, (byte) 0x25, (byte) 0xE4, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdword $12,ptra[3]++\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x7D, (byte) 0x25, (byte) 0xE4, (byte) 0xFA
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdword $12,ptra[3]--\n")));
    }

    @Test
    void testRdlong() throws Exception {
        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x34, (byte) 0x24, (byte) 0x00, (byte) 0xFB
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdlong $12,$34\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x00, (byte) 0x25, (byte) 0x04, (byte) 0xFB
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdlong $12,ptra\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x61, (byte) 0x25, (byte) 0x04, (byte) 0xFB
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdlong $12,ptra++\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x7F, (byte) 0x25, (byte) 0x04, (byte) 0xFB
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdlong $12,ptra--\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x41, (byte) 0x25, (byte) 0x04, (byte) 0xFB
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdlong $12,++ptra\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x5F, (byte) 0x25, (byte) 0x04, (byte) 0xFB
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdlong $12,--ptra\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x03, (byte) 0x25, (byte) 0x04, (byte) 0xFB
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdlong $12,ptra[3]\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x3D, (byte) 0x25, (byte) 0x04, (byte) 0xFB
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdlong $12,ptra[-3]\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x63, (byte) 0x25, (byte) 0x04, (byte) 0xFB
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdlong $12,ptra[3]++\n")));

        Assertions.assertEquals(Spin2InstructionObject.decodeToString(new byte[] {
            (byte) 0x7D, (byte) 0x25, (byte) 0x04, (byte) 0xFB
        }), Spin2InstructionObject.decodeToString(compile("DAT\n  rdlong $12,ptra[3]--\n")));
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
