/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin;

import java.io.ByteArrayOutputStream;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings({
    "unchecked"
})
class Spin2CompilerFunctionalTest {

    @Test
    void testCompileBlink() throws Exception {
        String text = ""
            + "CON\n"
            + "\n"
            + "    _clkfreq = 160_000_000\n"
            + "    delay    = _clkfreq / 2\n"
            + "\n"
            + "DAT\n"
            + "\n"
            + "                org   $000\n"
            + "\n"
            + "start\n"
            + "                asmclk                      ' set clock\n"
            + "\n"
            + "                getct   ct                  ' get current timer\n"
            + ".loop           drvnot  #56                 ' toggle output\n"
            + "                addct1  ct, ##delay         ' set delay to timer 1\n"
            + "                waitct1                     ' wait for timer 1 expire\n"
            + "                jmp     #\\.loop\n"
            + "\n"
            + "ct              res     1\n";

        byte[] expected = new byte[] {
            /* 000000 000 */ // |                     org     0
            /* 000000 000 */ // | start
            /* 000000 000 */ (byte) 0x03, (byte) 0x80, (byte) 0x80, (byte) 0xFF, // |                     hubset  ##clkmode_ & !%11
            /* 000004 001 */ (byte) 0x00, (byte) 0xF0, (byte) 0x67, (byte) 0xFD, // |
            /* 000008 002 */ (byte) 0x86, (byte) 0x01, (byte) 0x80, (byte) 0xFF, // |                     waitx   ##20000000 / 100
            /* 00000C 003 */ (byte) 0x1F, (byte) 0x80, (byte) 0x66, (byte) 0xFD, // |
            /* 000010 004 */ (byte) 0x03, (byte) 0x80, (byte) 0x80, (byte) 0xFF, // |                     hubset  ##clkmode_
            /* 000014 005 */ (byte) 0x00, (byte) 0xF6, (byte) 0x67, (byte) 0xFD, // |
            /* 000018 006 */ (byte) 0x1A, (byte) 0x18, (byte) 0x60, (byte) 0xFD, // |                     getct   ct
            /* 00001C 007 */ (byte) 0x5F, (byte) 0x70, (byte) 0x64, (byte) 0xFD, // | .loop               drvnot  #56
            /* 000020 008 */ (byte) 0x5A, (byte) 0x62, (byte) 0x02, (byte) 0xFF, // |
            /* 000024 009 */ (byte) 0x00, (byte) 0x18, (byte) 0x64, (byte) 0xFA, // |                     addct1  ct, ##delay
            /* 000028 00A */ (byte) 0x24, (byte) 0x22, (byte) 0x60, (byte) 0xFD, // |                     waitct1
            /* 00002C 00B */ (byte) 0x07, (byte) 0x00, (byte) 0x80, (byte) 0xFD, // |                     jmp     #\.loop
            /* 000030 00C */ // | ct                  res     1
        };

        byte[] result = compile(text);
        Assertions.assertArrayEquals(expected, result);
    }

    @Test
    void testCompileBlinkHubexec() throws Exception {
        String text = ""
            + "CON\n"
            + "\n"
            + "    _clkfreq = 160_000_000\n"
            + "    delay    = _clkfreq / 2\n"
            + "\n"
            + "DAT\n"
            + "\n"
            + "                org   $000\n"
            + "\n"
            + "start\n"
            + "                asmclk                      ' set clock\n"
            + "                jmp     #@main              ' jump to hub program\n"
            + "\n"
            + "ct              res     1\n"
            + "\n"
            + "' HUB Program\n"
            + "\n"
            + "                orgh    $400\n"
            + "\n"
            + "main\n"
            + "\n"
            + "                getct   ct                  ' get current timer\n"
            + ".loop           drvnot  #56                 ' toggle output\n"
            + "                addct1  ct, ##delay         ' set delay to timer 1\n"
            + "                waitct1                     ' wait for timer 1 expire\n"
            + "                jmp     #.loop\n"
            + "\n";

        byte[] expected = new byte[] {
            /* 000000 000 */ // |                     org     0
            /* 000000 000 */ // | start
            /* 000000 000 */ (byte) 0x03, (byte) 0x80, (byte) 0x80, (byte) 0xFF, // |                     hubset  ##clkmode_ & !%11
            /* 000004 001 */ (byte) 0x00, (byte) 0xF0, (byte) 0x67, (byte) 0xFD, // |
            /* 000008 002 */ (byte) 0x86, (byte) 0x01, (byte) 0x80, (byte) 0xFF, // |                     waitx   ##20000000 / 100
            /* 00000C 003 */ (byte) 0x1F, (byte) 0x80, (byte) 0x66, (byte) 0xFD, // |
            /* 000010 004 */ (byte) 0x03, (byte) 0x80, (byte) 0x80, (byte) 0xFF, // |                     hubset  ##clkmode_
            /* 000014 005 */ (byte) 0x00, (byte) 0xF6, (byte) 0x67, (byte) 0xFD, // |
            /* 000018 006 */ (byte) 0x00, (byte) 0x04, (byte) 0x80, (byte) 0xFD, // |                     jmp     #@main
            /* 00001C 007 */ // | ct                  res     1
            /* 00001C 000 */ // |                     orgh    1024
            /* 00001C 400 */ // | main
            /* 00001C 400 */ (byte) 0x1A, (byte) 0x0E, (byte) 0x60, (byte) 0xFD, // |                     getct   ct
            /* 000020 401 */ (byte) 0x5F, (byte) 0x70, (byte) 0x64, (byte) 0xFD, // | .loop               drvnot  #56
            /* 000024 402 */ (byte) 0x5A, (byte) 0x62, (byte) 0x02, (byte) 0xFF, // |                     addct1  ct, ##delay
            /* 000028 403 */ (byte) 0x00, (byte) 0x0E, (byte) 0x64, (byte) 0xFA, // |
            /* 00002C 404 */ (byte) 0x24, (byte) 0x22, (byte) 0x60, (byte) 0xFD, // |                     waitct1
            /* 000030 405 */ (byte) 0xEC, (byte) 0xFF, (byte) 0x9F, (byte) 0xFD, // |                     jmp     #.loop
        };

        byte[] result = compile(text);
        Assertions.assertArrayEquals(expected, result);
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
        for (Spin2PAsmLine line : compiler.source) {
            line.generateObjectCode(os);
        }
        return os.toByteArray();
    }
}
