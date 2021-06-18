/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;

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

        byte[] part0 = new byte[] {
            /* 000000 000 */ // |                     org     0
            /* 000000 000 */ // | start
            /* 000000 000 */ (byte) 0x03, (byte) 0x80, (byte) 0x80, (byte) 0xFF, // |                     hubset  ##clkmode_ & !%11
            /* 000004 001 */ (byte) 0x00, (byte) 0xF0, (byte) 0x67, (byte) 0xFD, // |
            /* 000008 002 */ (byte) 0x86, (byte) 0x01, (byte) 0x80, (byte) 0xFF, // |                     waitx   ##20000000 / 100
            /* 00000C 003 */ (byte) 0x1F, (byte) 0x80, (byte) 0x66, (byte) 0xFD, // |
            /* 000010 004 */ (byte) 0x03, (byte) 0x80, (byte) 0x80, (byte) 0xFF, // |                     hubset  ##clkmode_
            /* 000014 005 */ (byte) 0x00, (byte) 0xF6, (byte) 0x67, (byte) 0xFD, // |
            /* 000018 006 */ (byte) 0x00, (byte) 0x04, (byte) 0x80, (byte) 0xFD, // |                     jmp     #@main
        };
        byte[] part1 = new byte[] {
            /* 00001C 007 */ // | ct                  res     1
            /* 00001C 000 */ // |                     orgh    1024
            /* 000400     */ // | main
            /* 000400     */ (byte) 0x1A, (byte) 0x0E, (byte) 0x60, (byte) 0xFD, // |                     getct   ct
            /* 000404     */ (byte) 0x5F, (byte) 0x70, (byte) 0x64, (byte) 0xFD, // | .loop               drvnot  #56
            /* 000408     */ (byte) 0x5A, (byte) 0x62, (byte) 0x02, (byte) 0xFF, // |                     addct1  ct, ##delay
            /* 00040C     */ (byte) 0x00, (byte) 0x0E, (byte) 0x64, (byte) 0xFA, // |
            /* 000410     */ (byte) 0x24, (byte) 0x22, (byte) 0x60, (byte) 0xFD, // |                     waitct1
            /* 000414     */ (byte) 0xEC, (byte) 0xFF, (byte) 0x9F, (byte) 0xFD, // |                     jmp     #.loop
        };

        byte[] expected = new byte[0x400 + part1.length];
        System.arraycopy(part0, 0, expected, 0, part0.length);
        System.arraycopy(part1, 0, expected, 0x400, part1.length);

        byte[] result = compile(text);
        Assertions.assertArrayEquals(expected, result);
    }

    @Test
    void testCompileBlinkSpin() throws Exception {
        String text = ""
            + "CON\n"
            + "    _clkfreq = 160_000_000\n"
            + "\n"
            + "PUB main() | ct\n"
            + "\n"
            + "    ct := getct()                   ' get current timer\n"
            + "    repeat\n"
            + "        pint(56)                    ' toggle pin 56\n"
            + "        waitct(ct += _clkfreq / 2)  ' wait half second"
            + "\n";

        byte[] expected = new byte[] {};
        /* 88 10 00 00    PBASE $1088     */
        /* A4 10 00 00    VBASE $10A4     */
        /* A8 10 00 00    DBASE $10A8     */
        /* 00 01 00 00    CLEAR $0010     */

        /* 1088 - 08 00 00       CALL 0.0        */
        /* 108B - 80 19          3200?           */
        /* 108D - 00 00 00 04                    */
        /*                                       */ /* PUB main()                                              */
        /* 1091 - 33             GETCT           */ /*     ct := getct()                   ' get current timer */
        /* 1092 - F0             +-> ct          */ /*                                                         */
        /*                                       */ /*     repeat                                              */
        /* 1093 - 45 38          56              */ /*     |   pint(56)                    ' toggle pin 56     */
        /* 1095 - 39             PINT            */ /*     |                                                   */
        /* 1096 - 49 00 B4 C4 04 _CLKFREQ / 2    */ /*     |   waitct(ct += _clkfreq / 2)  ' wait half second  */
        /* 109B - D0             ct              */ /*     |                                                   */
        /* 109C - CA             +=              */ /*     |                                                   */
        /* 109D - 35             WAITCT          */ /*     |                                                   */
        /* 109E - 12 74          JMP -12         */ /*     +                                                   */
        /* 10A0 - 04 00 00 00                    */
        /* 10A4 - 00 00 00 00    VBASE           */
        /* DBASE (STACK)                         */
        /* 10A8 - 00 00 00 00    ct              */

        byte[] result = compile(text);
        Assertions.assertArrayEquals(expected, result);
    }

    @Test
    void testReference1() throws Exception {
        String text = getResourceAsString("Spin2_interpreter.spin2");
        byte[] expected = getResource("Spin2_interpreter.binary");

        compileAndCompare(text, expected);

        byte[] result = compile(text);
        Assertions.assertArrayEquals(expected, result);
    }

    @Test
    void testReference2() throws Exception {
        String text = getResourceAsString("m6502_apple1_cvbs.spin2");
        byte[] expected = getResource("m6502_apple1_cvbs.binary");

        Assertions.assertTrue(compileAndCompare(text, expected));

        byte[] result = compile(text);
        Assertions.assertArrayEquals(expected, result);
    }

    @Test
    void testReference3() throws Exception {
        String text = getResourceAsString("m6502_apple1_vga.spin2");
        byte[] expected = getResource("m6502_apple1_vga.binary");

        Assertions.assertTrue(compileAndCompare(text, expected));

        byte[] result = compile(text);
        Assertions.assertArrayEquals(expected, result);
    }

    String getResourceAsString(String name) throws Exception {
        InputStream is = getClass().getResourceAsStream(name);
        try {
            byte[] b = new byte[is.available()];
            is.read(b);
            return new String(b);
        } finally {
            is.close();
        }
    }

    byte[] getResource(String name) throws Exception {
        InputStream is = getClass().getResourceAsStream(name);
        try {
            byte[] b = new byte[is.available()];
            is.read(b);
            return b;
        } finally {
            is.close();
        }
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

    boolean compileAndCompare(String text, byte[] ref) throws Exception {
        byte[] refLong = new byte[4];

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser subject = new Spin2Parser(stream);
        Node root = subject.parse();

        Spin2Compiler compiler = new Spin2Compiler();
        compiler.compile(root);

        boolean result = true;

        for (Spin2PAsmLine line : compiler.getSource()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            line.generateObjectCode(os);
            byte[] code = os.toByteArray();

            int address = line.getScope().getHubAddress();
            int addr = line.getScope().getAddress();

            int index = 0;

            StringBuilder out = new StringBuilder();
            out.append(String.format("%06X ", address));
            out.append(addr < 0x400 ? String.format("%03X ", addr++) : "    ");

            refLong[0] = (address < ref.length) ? ref[address] : 0x00;
            refLong[1] = (address + 1 < ref.length) ? ref[address + 1] : 0x00;
            refLong[2] = (address + 2 < ref.length) ? ref[address + 2] : 0x00;
            refLong[3] = (address + 3 < ref.length) ? ref[address + 3] : 0x00;

            StringBuilder refbytes = new StringBuilder();
            refbytes.append((index < code.length && address < ref.length) ? String.format(" %02X", ref[address]) : "   ");
            refbytes.append((index + 1 < code.length && address + 1 < ref.length) ? String.format(" %02X", ref[address + 1]) : "   ");
            refbytes.append((index + 2 < code.length && address + 2 < ref.length) ? String.format(" %02X", ref[address + 2]) : "   ");
            refbytes.append((index + 3 < code.length && address + 3 < ref.length) ? String.format(" %02X", ref[address + 3]) : "   ");

            StringBuilder ourbytes = new StringBuilder();
            ourbytes.append((index < code.length) ? String.format(" %02X", code[index++]) : "   ");
            ourbytes.append((index < code.length) ? String.format(" %02X", code[index++]) : "   ");
            ourbytes.append((index < code.length) ? String.format(" %02X", code[index++]) : "   ");
            ourbytes.append((index < code.length) ? String.format(" %02X", code[index++]) : "   ");

            out.append(refbytes);
            out.append(refbytes.toString().equals(ourbytes.toString()) ? " | " : " * ");
            out.append(ourbytes);
            out.append(" | " + line);

            if (!refbytes.toString().equals(ourbytes.toString())) {
                if (code.length >= 4) {
                    Assertions.assertEquals(Spin2InstructionObject.decodeToString(refLong), Spin2InstructionObject.decodeToString(code), "\n" + out.toString() + "\n");
                }
                result = false;
            }

            while (index < code.length) {
                out = new StringBuilder();
                out.append(String.format("%06X ", address + index));
                out.append(addr < 0x400 ? String.format("%03X ", addr++) : "    ");

                refLong[0] = (address < ref.length) ? ref[address] : 0x00;
                refLong[1] = (address + 1 < ref.length) ? ref[address + 1] : 0x00;
                refLong[2] = (address + 2 < ref.length) ? ref[address + 2] : 0x00;
                refLong[3] = (address + 3 < ref.length) ? ref[address + 3] : 0x00;

                refbytes = new StringBuilder();
                refbytes.append((index < code.length && address + index < ref.length) ? String.format(" %02X", ref[address + index]) : "   ");
                refbytes.append((index + 1 < code.length && address + index + 1 < ref.length) ? String.format(" %02X", ref[address + index + 1]) : "   ");
                refbytes.append((index + 2 < code.length && address + index + 2 < ref.length) ? String.format(" %02X", ref[address + index + 2]) : "   ");
                refbytes.append((index + 3 < code.length && address + index + 3 < ref.length) ? String.format(" %02X", ref[address + index + 3]) : "   ");

                ourbytes = new StringBuilder();
                ourbytes.append((index < code.length) ? String.format(" %02X", code[index++]) : "   ");
                ourbytes.append((index < code.length) ? String.format(" %02X", code[index++]) : "   ");
                ourbytes.append((index < code.length) ? String.format(" %02X", code[index++]) : "   ");
                ourbytes.append((index < code.length) ? String.format(" %02X", code[index++]) : "   ");

                out.append(refbytes);
                out.append(refbytes.toString().equals(ourbytes.toString()) ? " | " : " * ");
                out.append(ourbytes);
                out.append(" | ");

                if (!refbytes.toString().equals(ourbytes.toString())) {
                    if (code.length >= 4) {
                        Assertions.assertEquals(Spin2InstructionObject.decodeToString(refLong), Spin2InstructionObject.decodeToString(code), "\n" + out.toString() + "\n");
                    }
                    result = false;
                }
            }
        }

        return result;
    }
}
