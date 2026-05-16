/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.maccasoft.propeller.CompilerException;

class Spin2ObjectCompilerTest {

    @Test
    void testConstants() throws Exception {
        String text = """
            CON
               A = 1
               B = 2, C = 3
               D = A + B * C
            
            PUB main() | v
              v := A
              v := B
              v := C
              v := D
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       12 00 00 00    End
            ' PUB main() | v
            00008 00008       01             (stack size)
            '   v := A
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := B
            0000B 0000B       A3             CONSTANT (2)
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := C
            0000D 0000D       A4             CONSTANT (3)
            0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := D
            0000F 0000F       A8             CONSTANT (A + B * C)
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00011 00011       04             RETURN
            00012 00012       00 00          Padding
            """, compile(text));
    }

    @Test
    void testEnum() throws Exception {
        String text = """
            CON
               A, B, C
               #1, D, E[4], F
               #F, G, H, I[2], J
            
            PUB main() | v
              v := A
              v := B
              v := C
              v := D
              v := E
              v := F
              v := G
              v := H
              v := I
              v := J
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1E 00 00 00    End
            ' PUB main() | v
            00008 00008       01             (stack size)
            '   v := A
            00009 00009       A1             CONSTANT (0)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := B
            0000B 0000B       A2             CONSTANT (1)
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := C
            0000D 0000D       A3             CONSTANT (2)
            0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := D
            0000F 0000F       A2             CONSTANT (1)
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := E
            00011 00011       A3             CONSTANT (2)
            00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := F
            00013 00013       A7             CONSTANT (6)
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := G
            00015 00015       A7             CONSTANT (F)
            00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := H
            00017 00017       A8             CONSTANT (F + 1)
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := I
            00019 00019       A9             CONSTANT (F + 2)
            0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := J
            0001B 0001B       AB             CONSTANT (F + 4)
            0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001D 0001D       04             RETURN
            0001E 0001E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testEnumStep() throws Exception {
        String text = """
            CON
               #10[5], A, B, C
               D, E[2], F
               #1, G, H, I
            
            PUB main() | v
              v := A
              v := B
              v := C
              v := D
              v := E
              v := F
              v := G
              v := H
              v := I
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       21 00 00 00    End
            ' PUB main() | v
            00008 00008       01             (stack size)
            '   v := A
            00009 00009       AB             CONSTANT (10)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := B
            0000B 0000B       42 0F          CONSTANT (15)
            0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := C
            0000E 0000E       42 14          CONSTANT (20)
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := D
            00011 00011       42 19          CONSTANT (25)
            00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := E
            00014 00014       42 1E          CONSTANT (30)
            00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := F
            00017 00017       42 28          CONSTANT (40)
            00019 00019       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := G
            0001A 0001A       A2             CONSTANT (1)
            0001B 0001B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := H
            0001C 0001C       A3             CONSTANT (2)
            0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := I
            0001E 0001E       A4             CONSTANT (3)
            0001F 0001F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00020 00020       04             RETURN
            00021 00021       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testClockModeDefault() throws Exception {
        String text = "PUB main()\n";

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2Compiler(), new File("test.spin2"));
        compiler.compileObject(text);

        Assertions.assertEquals(0b00_00, compiler.getScope().getLocalSymbol("CLKMODE_").getNumber().intValue() & 0b11_11);
        Assertions.assertEquals(20_000_000, compiler.getScope().getLocalSymbol("CLKFREQ_").getNumber().intValue());
    }

    @Test
    void testClkFreqClockMode() throws Exception {
        String text = """
            _CLKFREQ = 250_000_000
            PUB main()
            """;

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2Compiler(), new File("test.spin2"));
        compiler.compileObject(text);

        Assertions.assertEquals(250_000_000, compiler.getScope().getLocalSymbol("CLKFREQ_").getNumber().intValue());
        Assertions.assertEquals(0b10_11, compiler.getScope().getLocalSymbol("CLKMODE_").getNumber().intValue() & 0b11_11);
    }

    @Test
    void testXtlFreq1() throws Exception {
        String text = """
            _XTLFREQ = 12_000_000
            _CLKFREQ = 148_500_000
            PUB main()
            """;

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2Compiler(), new File("test.spin2"));
        compiler.compileObject(text);

        Assertions.assertEquals(148_500_000, compiler.getScope().getLocalSymbol("CLKFREQ_").getNumber().intValue());
        Assertions.assertEquals("011C62FF", String.format("%08X", compiler.getScope().getLocalSymbol("CLKMODE_").getNumber().intValue()));
    }

    @Test
    void testXtlFreq2() throws Exception {
        String text = """
            _XTLFREQ = 20_000_000
            _CLKFREQ = 100_000_000
            PUB main()
            """;

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2Compiler(), new File("test.spin2"));
        compiler.compileObject(text);

        Assertions.assertEquals(100_000_000, compiler.getScope().getLocalSymbol("CLKFREQ_").getNumber().intValue());
        Assertions.assertEquals("0100090B", String.format("%08X", compiler.getScope().getLocalSymbol("CLKMODE_").getNumber().intValue()));
    }

    @Test
    void testXtlFreqClockMode() throws Exception {
        String text = """
            _XTLFREQ = 16_000_000
            PUB main()
            """;

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2Compiler(), new File("test.spin2"));
        compiler.compileObject(text);

        Assertions.assertEquals(16_000_000, compiler.getScope().getLocalSymbol("CLKFREQ_").getNumber().intValue());
        Assertions.assertEquals("0000000A", String.format("%08X", compiler.getScope().getLocalSymbol("CLKMODE_").getNumber().intValue()));
    }

    @Test
    void testXinFreq() throws Exception {
        String text = """
            _XINFREQ = 32_000_000
            _CLKFREQ = 297_500_000
            PUB main()
            """;

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2Compiler(), new File("test.spin2"));
        compiler.compileObject(text);

        Assertions.assertEquals(297_500_000, compiler.getScope().getLocalSymbol("CLKFREQ_").getNumber().intValue());
        Assertions.assertEquals("01FE52F7", String.format("%08X", compiler.getScope().getLocalSymbol("CLKMODE_").getNumber().intValue()));
    }

    @Test
    void testXinFreqClockMode() throws Exception {
        String text = """
            _XINFREQ = 16_000_000
            PUB main()
            """;

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2Compiler(), new File("test.spin2"));
        compiler.compileObject(text);

        Assertions.assertEquals(16_000_000, compiler.getScope().getLocalSymbol("CLKFREQ_").getNumber().intValue());
        Assertions.assertEquals("00000006", String.format("%08X", compiler.getScope().getLocalSymbol("CLKMODE_").getNumber().intValue()));
    }

    @Test
    void testRcSlow() throws Exception {
        String text = """
            _RCSLOW
            PUB main()
            """;

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2Compiler(), new File("test.spin2"));
        compiler.compileObject(text);

        Assertions.assertEquals(20_000, compiler.getScope().getLocalSymbol("CLKFREQ_").getNumber().intValue());
        Assertions.assertEquals("00000001", String.format("%08X", compiler.getScope().getLocalSymbol("CLKMODE_").getNumber().intValue()));
    }

    @Test
    void testRcFast() throws Exception {
        String text = """
            _RCFAST
            PUB main()
            """;

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2Compiler(), new File("test.spin2"));
        compiler.compileObject(text);

        Assertions.assertEquals(20_000_000, compiler.getScope().getLocalSymbol("CLKFREQ_").getNumber().intValue());
        Assertions.assertEquals("00000000", String.format("%08X", compiler.getScope().getLocalSymbol("CLKMODE_").getNumber().intValue()));
    }

    @Test
    void testEmptyMethod() throws Exception {
        String text = """
            PUB main()
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0A 00 00 00    End
            ' PUB main()
            00008 00008       00             (stack size)
            00009 00009       04             RETURN
            0000A 0000A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testLocalVarAssignment() throws Exception {
        String text = """
            PUB main() | a
            
                a := 1
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0C 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     a := 1
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0000B 0000B       04             RETURN
            """, compile(text));
    }

    @Test
    void testParameterVarAssignment() throws Exception {
        String text = """
            PUB main(a)
            
                a := 1
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 81    Method main @ $00008 (1 parameters, 0 returns)
            00004 00004       0C 00 00 00    End
            ' PUB main(a)
            00008 00008       00             (stack size)
            '     a := 1
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0000B 0000B       04             RETURN
            """, compile(text));
    }

    @Test
    void testGlobalVarAssignment() throws Exception {
        String text = """
            VAR
            
                long a
                byte b
                word c
                long d
            
            PUB main()
            
                a := 1
                b := 2
                c := 3
                d := 4
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 16)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       19 00 00 00    End
            ' PUB main()
            00008 00008       00             (stack size)
            '     a := 1
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       C1 1D          VAR_WRITE LONG VBASE+$00001 (short)
            '     b := 2
            0000C 0000C       A3             CONSTANT (2)
            0000D 0000D       50 08 1D       VAR_WRITE BYTE VBASE+$00008
            '     c := 3
            00010 00010       A4             CONSTANT (3)
            00011 00011       56 09 1D       VAR_WRITE WORD VBASE+$00009
            '     d := 4
            00014 00014       A5             CONSTANT (4)
            00015 00015       5C 0B 1D       VAR_WRITE LONG VBASE+$0000B
            00018 00018       04             RETURN
            00019 00019       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testExpressionAssignment() throws Exception {
        String text = """
            PUB main() | a, b
            
                a := 1 + b * 3
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       10 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     a := 1 + b * 3
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            0000B 0000B       A4             CONSTANT (3)
            0000C 0000C       96             MULTIPLY
            0000D 0000D       8A             ADD
            0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0000F 0000F       04             RETURN
            """, compile(text));
    }

    @Test
    void testConstantExpressionAssignment() throws Exception {
        String text = """
            PUB main() | a
            
                a := 1 + 2 * 3
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0C 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     a := 1 + 2 * 3
            00009 00009       A8             CONSTANT (1 + 2 * 3)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0000B 0000B       04             RETURN
            """, compile(text));
    }

    @Test
    void testCharacterAssigment() throws Exception {
        String text = """
            PUB main() | a
            
                a := "1"
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0D 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     a := "1"
            00009 00009       42 31          CONSTANT ("1")
            0000B 0000B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0000C 0000C       04             RETURN
            0000D 0000D       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testIfConditional() throws Exception {
        String text = """
            PUB main() | a
            
                if a == 0
                    a := 1
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       11 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     if a == 0
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A1             CONSTANT (0)
            0000B 0000B       70             EQUAL
            0000C 0000C       13 03          JZ $00010 (3)
            '         a := 1
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00010 00010       04             RETURN
            00011 00011       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testIfConditionExpression() throws Exception {
        String text = """
            PUB main() | a, b
            
                if (a := b) == 0
                    a := 1
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       13 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     if (a := b) == 0
            00009 00009       E1             VAR_READ LONG DBASE+$00001 (short)
            0000A 0000A       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000B 0000B       1E             WRITE (push)
            0000C 0000C       A1             CONSTANT (0)
            0000D 0000D       70             EQUAL
            0000E 0000E       13 03          JZ $00012 (3)
            '         a := 1
            00010 00010       A2             CONSTANT (1)
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00012 00012       04             RETURN
            00013 00013       00             Padding
            """, compile(text));
    }

    @Test
    void testIfElseConditional() throws Exception {
        String text = """
            PUB main() | a
            
                if a == 0
                    a := 1
                else
                    a := 2
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       15 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     if a == 0
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A1             CONSTANT (0)
            0000B 0000B       70             EQUAL
            0000C 0000C       13 05          JZ $00012 (5)
            '         a := 1
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00010 00010       12 03          JMP $00014 (3)
            '     else
            '         a := 2
            00012 00012       A3             CONSTANT (2)
            00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00014 00014       04             RETURN
            00015 00015       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testIfElseIfConditional() throws Exception {
        String text = """
            PUB main() | a
            
                if a == 0
                    a := 1
                elseif a == 1
                    a := 2
                elseif a == 2
                    a := 3
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       23 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     if a == 0
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A1             CONSTANT (0)
            0000B 0000B       70             EQUAL
            0000C 0000C       13 05          JZ $00012 (5)
            '         a := 1
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00010 00010       12 11          JMP $00022 (17)
            '     elseif a == 1
            00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)
            00013 00013       A2             CONSTANT (1)
            00014 00014       70             EQUAL
            00015 00015       13 05          JZ $0001B (5)
            '         a := 2
            00017 00017       A3             CONSTANT (2)
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00019 00019       12 08          JMP $00022 (8)
            '     elseif a == 2
            0001B 0001B       E0             VAR_READ LONG DBASE+$00000 (short)
            0001C 0001C       A3             CONSTANT (2)
            0001D 0001D       70             EQUAL
            0001E 0001E       13 03          JZ $00022 (3)
            '         a := 3
            00020 00020       A4             CONSTANT (3)
            00021 00021       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00022 00022       04             RETURN
            00023 00023       00             Padding
            """, compile(text));
    }

    @Test
    void testIfElseIfElseConditional() throws Exception {
        String text = """
            PUB main() | a
            
                if a == 0
                    a := 1
                elseif a == 1
                    a := 2
                elseif a == 2
                    a := 3
                else
                    a := 4
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       27 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     if a == 0
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A1             CONSTANT (0)
            0000B 0000B       70             EQUAL
            0000C 0000C       13 05          JZ $00012 (5)
            '         a := 1
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00010 00010       12 15          JMP $00026 (21)
            '     elseif a == 1
            00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)
            00013 00013       A2             CONSTANT (1)
            00014 00014       70             EQUAL
            00015 00015       13 05          JZ $0001B (5)
            '         a := 2
            00017 00017       A3             CONSTANT (2)
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00019 00019       12 0C          JMP $00026 (12)
            '     elseif a == 2
            0001B 0001B       E0             VAR_READ LONG DBASE+$00000 (short)
            0001C 0001C       A3             CONSTANT (2)
            0001D 0001D       70             EQUAL
            0001E 0001E       13 05          JZ $00024 (5)
            '         a := 3
            00020 00020       A4             CONSTANT (3)
            00021 00021       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00022 00022       12 03          JMP $00026 (3)
            '     else
            '         a := 4
            00024 00024       A5             CONSTANT (4)
            00025 00025       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00026 00026       04             RETURN
            00027 00027       00             Padding
            """, compile(text));
    }

    @Test
    void testRepeat() throws Exception {
        String text = """
            PUB main() | a
            
                repeat
                    a := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0E 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     repeat
            '         a := 1
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0000B 0000B       12 7D          JMP $00009 (-3)
            0000D 0000D       04             RETURN
            0000E 0000E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testRepeat0() throws Exception {
        String text = """
            PUB main() | a
            
                repeat 0
                    a := 1
                a := 2
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0C 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     repeat 0
            '     a := 2
            00009 00009       A3             CONSTANT (2)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0000B 0000B       04             RETURN
            """, compile(text));
    }

    @Test
    void testRepeatWith() throws Exception {
        String text = """
            PUB main() | a, b
            
                repeat 10 with b
                    a++
            
                repeat a with c
                    a++
            
                repeat c with b
                    a++
            
                repeat a+10 with b
                    a++
            DAT
            
            c   long 0
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       38 00 00 00    End
            00008 00008 00000 00 00 00 00    c                   long    0
            ' PUB main() | a, b
            0000C 0000C       02             (stack size)
            '     repeat 10 with b
            0000D 0000D       42 12          ADDRESS ($00012)
            0000F 0000F       AB             CONSTANT (10)
            00010 00010       D1             VAR_SETUP LONG DBASE+$00001 (short)
            00011 00011       16             REPEAT
            '         a++
            00012 00012       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00013 00013       1F             POST_INC
            00014 00014       D1             VAR_SETUP LONG DBASE+$00001 (short)
            00015 00015       19             REPEAT_LOOP
            '     repeat a with c
            00016 00016       42 1C          ADDRESS ($0001C)
            00018 00018       E0             VAR_READ LONG DBASE+$00000 (short)
            00019 00019       5B 08          MEM_SETUP LONG PBASE+$00008
            0001B 0001B       16             REPEAT
            '         a++
            0001C 0001C       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0001D 0001D       1F             POST_INC
            0001E 0001E       5B 08          MEM_SETUP LONG PBASE+$00008
            00020 00020       19             REPEAT_LOOP
            '     repeat c with b
            00021 00021       42 28          ADDRESS ($00028)
            00023 00023       5B 08 1C       MEM_READ LONG PBASE+$00008
            00026 00026       D1             VAR_SETUP LONG DBASE+$00001 (short)
            00027 00027       16             REPEAT
            '         a++
            00028 00028       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00029 00029       1F             POST_INC
            0002A 0002A       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0002B 0002B       19             REPEAT_LOOP
            '     repeat a+10 with b
            0002C 0002C       42 33          ADDRESS ($00033)
            0002E 0002E       E0             VAR_READ LONG DBASE+$00000 (short)
            0002F 0002F       AB             CONSTANT (10)
            00030 00030       8A             ADD
            00031 00031       D1             VAR_SETUP LONG DBASE+$00001 (short)
            00032 00032       16             REPEAT
            '         a++
            00033 00033       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00034 00034       1F             POST_INC
            00035 00035       D1             VAR_SETUP LONG DBASE+$00001 (short)
            00036 00036       19             REPEAT_LOOP
            00037 00037       04             RETURN
            """, compile(text));
    }

    @Test
    void testRepeatWithQuitAndNext() throws Exception {
        String text = """
            PUB main() | a, b
            
                repeat 10 with b
                    if a == 1
                        next
                    if a == 2
                        quit
                    a++
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       23 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     repeat 10 with b
            00009 00009       42 0E          ADDRESS ($0000E)
            0000B 0000B       AB             CONSTANT (10)
            0000C 0000C       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0000D 0000D       16             REPEAT
            '         if a == 1
            0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)
            0000F 0000F       A2             CONSTANT (1)
            00010 00010       70             EQUAL
            00011 00011       13 03          JZ $00015 (3)
            '             next
            00013 00013       12 0C          JMP $00020 (12)
            '         if a == 2
            00015 00015       E0             VAR_READ LONG DBASE+$00000 (short)
            00016 00016       A3             CONSTANT (2)
            00017 00017       70             EQUAL
            00018 00018       13 05          JZ $0001E (5)
            '             quit
            0001A 0001A       18 0C          POP 16
            0001C 0001C       12 05          JMP $00022 (5)
            '         a++
            0001E 0001E       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0001F 0001F       1F             POST_INC
            00020 00020       D1             VAR_SETUP LONG DBASE+$00001 (short)
            00021 00021       19             REPEAT_LOOP
            00022 00022       04             RETURN
            00023 00023       00             Padding
            """, compile(text));
    }

    @Test
    void testRepeatCount() throws Exception {
        String text = """
            PUB main() | a
            
                repeat 10
                    a := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0F 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     repeat 10
            00009 00009       AB             CONSTANT (10)
            '         a := 1
            0000A 0000A       A2             CONSTANT (1)
            0000B 0000B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0000C 0000C       16 7D          DJNZ $0000A (-3)
            0000E 0000E       04             RETURN
            0000F 0000F       00             Padding
            """, compile(text));
    }

    @Test
    void testRepeatVar() throws Exception {
        String text = """
            PUB main() | a
            
                repeat a
                    a := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       11 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     repeat a
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       15 05          TJZ $00010 (5)
            '         a := 1
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0000E 0000E       16 7D          DJNZ $0000C (-3)
            00010 00010       04             RETURN
            00011 00011       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testRepeatVarQuit() throws Exception {
        String text = """
            PUB main() | a
            
                repeat a
                    if a == 0
                        a := 1
                    else
                        quit
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1A 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     repeat a
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       15 0E          TJZ $00019 (14)
            '         if a == 0
            0000C 0000C       E0             VAR_READ LONG DBASE+$00000 (short)
            0000D 0000D       A1             CONSTANT (0)
            0000E 0000E       70             EQUAL
            0000F 0000F       13 05          JZ $00015 (5)
            '             a := 1
            00011 00011       A2             CONSTANT (1)
            00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00013 00013       12 03          JMP $00017 (3)
            '         else
            '             quit
            00015 00015       14 03          JNZ $00019 (3)
            00017 00017       16 74          DJNZ $0000C (-12)
            00019 00019       04             RETURN
            0001A 0001A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testRepeatQuit() throws Exception {
        String text = """
            PUB main() | a
            
                repeat
                    if a == 1
                        quit
                    a := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       15 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     repeat
            '         if a == 1
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A2             CONSTANT (1)
            0000B 0000B       70             EQUAL
            0000C 0000C       13 03          JZ $00010 (3)
            '             quit
            0000E 0000E       12 05          JMP $00014 (5)
            '         a := 1
            00010 00010       A2             CONSTANT (1)
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00012 00012       12 76          JMP $00009 (-10)
            00014 00014       04             RETURN
            00015 00015       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testRepeatNext() throws Exception {
        String text = """
            PUB main() | a
            
                repeat
                    if a == 1
                        next
                    a := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       15 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     repeat
            '         if a == 1
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A2             CONSTANT (1)
            0000B 0000B       70             EQUAL
            0000C 0000C       13 03          JZ $00010 (3)
            '             next
            0000E 0000E       12 7A          JMP $00009 (-6)
            '         a := 1
            00010 00010       A2             CONSTANT (1)
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00012 00012       12 76          JMP $00009 (-10)
            00014 00014       04             RETURN
            00015 00015       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testRepeatWhile() throws Exception {
        String text = """
            PUB main() | a
            
                repeat while a < 1
                    a := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       13 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     repeat while a < 1
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A2             CONSTANT (1)
            0000B 0000B       6C             LESS_THAN
            0000C 0000C       13 05          JZ $00012 (5)
            '         a := 1
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00010 00010       12 78          JMP $00009 (-8)
            00012 00012       04             RETURN
            00013 00013       00             Padding
            """, compile(text));
    }

    @Test
    void testRepeatUntil() throws Exception {
        String text = """
            PUB main() | a
            
                repeat until a < 1
                    a := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       13 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     repeat until a < 1
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A2             CONSTANT (1)
            0000B 0000B       6C             LESS_THAN
            0000C 0000C       14 05          JNZ $00012 (5)
            '         a := 1
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00010 00010       12 78          JMP $00009 (-8)
            00012 00012       04             RETURN
            00013 00013       00             Padding
            """, compile(text));
    }

    @Test
    void testRepeatWhileQuit() throws Exception {
        String text = """
            PUB main() | a
            
                repeat while a < 1
                    if a == 1
                        quit
                    a := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1A 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     repeat while a < 1
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A2             CONSTANT (1)
            0000B 0000B       6C             LESS_THAN
            0000C 0000C       13 0C          JZ $00019 (12)
            '         if a == 1
            0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)
            0000F 0000F       A2             CONSTANT (1)
            00010 00010       70             EQUAL
            00011 00011       13 03          JZ $00015 (3)
            '             quit
            00013 00013       12 05          JMP $00019 (5)
            '         a := 1
            00015 00015       A2             CONSTANT (1)
            00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00017 00017       12 71          JMP $00009 (-15)
            00019 00019       04             RETURN
            0001A 0001A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testRepeatPostWhile() throws Exception {
        String text = """
            PUB main() | a
            
                repeat
                    a := 1
                while a < 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       11 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     repeat
            '         a := 1
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     while a < 1
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       6C             LESS_THAN
            0000E 0000E       14 7A          JNZ $00009 (-6)
            00010 00010       04             RETURN
            00011 00011       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testRepeatPostUntil() throws Exception {
        String text = """
            PUB main() | a
            
                repeat
                    a := 1
                until a < 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       11 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     repeat
            '         a := 1
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     until a < 1
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       6C             LESS_THAN
            0000E 0000E       13 7A          JZ $00009 (-6)
            00010 00010       04             RETURN
            00011 00011       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testRepeatPostConditionQuit() throws Exception {
        String text = """
            PUB main() | a
            
                repeat
                    if a == 1
                        quit
                    a := 1
                while a < 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       18 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     repeat
            '         if a == 1
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A2             CONSTANT (1)
            0000B 0000B       70             EQUAL
            0000C 0000C       13 03          JZ $00010 (3)
            '             quit
            0000E 0000E       12 08          JMP $00017 (8)
            '         a := 1
            00010 00010       A2             CONSTANT (1)
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     while a < 1
            00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)
            00013 00013       A2             CONSTANT (1)
            00014 00014       6C             LESS_THAN
            00015 00015       14 73          JNZ $00009 (-13)
            00017 00017       04             RETURN
            """, compile(text));
    }

    @Test
    void testRepeatPostConditionNext() throws Exception {
        String text = """
            PUB main() | a
            
                repeat
                    if a == 1
                        next
                    a := 1
                until a < 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       18 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     repeat
            '         if a == 1
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A2             CONSTANT (1)
            0000B 0000B       70             EQUAL
            0000C 0000C       13 03          JZ $00010 (3)
            '             next
            0000E 0000E       12 03          JMP $00012 (3)
            '         a := 1
            00010 00010       A2             CONSTANT (1)
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     until a < 1
            00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)
            00013 00013       A2             CONSTANT (1)
            00014 00014       6C             LESS_THAN
            00015 00015       13 73          JZ $00009 (-13)
            00017 00017       04             RETURN
            """, compile(text));
    }

    @Test
    void testRepeatRange() throws Exception {
        String text = """
            PUB main() | a, b
            
                repeat a from 1 to 10
                    b := a + 1
                repeat long[a] from 1 to 10
                    b := a + 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       24 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     repeat a from 1 to 10
            00009 00009       42 0F          ADDRESS ($0000F)
            0000B 0000B       AB             CONSTANT (10)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000E 0000E       17             REPEAT
            '         b := a + 1
            0000F 0000F       E0             VAR_READ LONG DBASE+$00000 (short)
            00010 00010       A2             CONSTANT (1)
            00011 00011       8A             ADD
            00012 00012       F1             VAR_WRITE LONG DBASE+$00001 (short)
            00013 00013       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00014 00014       19             REPEAT_LOOP
            '     repeat long[a] from 1 to 10
            00015 00015       42 1C          ADDRESS ($0001C)
            00017 00017       AB             CONSTANT (10)
            00018 00018       A2             CONSTANT (1)
            00019 00019       E0             VAR_READ LONG DBASE+$00000 (short)
            0001A 0001A       63             MEM_SETUP LONG
            0001B 0001B       17             REPEAT
            '         b := a + 1
            0001C 0001C       E0             VAR_READ LONG DBASE+$00000 (short)
            0001D 0001D       A2             CONSTANT (1)
            0001E 0001E       8A             ADD
            0001F 0001F       F1             VAR_WRITE LONG DBASE+$00001 (short)
            00020 00020       E0             VAR_READ LONG DBASE+$00000 (short)
            00021 00021       63             MEM_SETUP LONG
            00022 00022       19             REPEAT_LOOP
            00023 00023       04             RETURN
            """, compile(text));
    }

    @Test
    void testRepeatRangeVariables() throws Exception {
        String text = """
            PUB main() | a, b, c, d
            
                repeat a from b to c
                    d := a + 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       16 00 00 00    End
            ' PUB main() | a, b, c, d
            00008 00008       04             (stack size)
            '     repeat a from b to c
            00009 00009       42 0F          ADDRESS ($0000F)
            0000B 0000B       E2             VAR_READ LONG DBASE+$00002 (short)
            0000C 0000C       E1             VAR_READ LONG DBASE+$00001 (short)
            0000D 0000D       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000E 0000E       17             REPEAT
            '         d := a + 1
            0000F 0000F       E0             VAR_READ LONG DBASE+$00000 (short)
            00010 00010       A2             CONSTANT (1)
            00011 00011       8A             ADD
            00012 00012       F3             VAR_WRITE LONG DBASE+$00003 (short)
            00013 00013       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00014 00014       19             REPEAT_LOOP
            00015 00015       04             RETURN
            00016 00016       00 00          Padding
            """, compile(text));
    }

    @Test
    void testRepeatRangeReverse() throws Exception {
        String text = """
            PUB main() | a, b
            
                repeat a from 10 to 1
                    b := a + 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       16 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     repeat a from 10 to 1
            00009 00009       42 0F          ADDRESS ($0000F)
            0000B 0000B       A2             CONSTANT (1)
            0000C 0000C       AB             CONSTANT (10)
            0000D 0000D       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000E 0000E       17             REPEAT
            '         b := a + 1
            0000F 0000F       E0             VAR_READ LONG DBASE+$00000 (short)
            00010 00010       A2             CONSTANT (1)
            00011 00011       8A             ADD
            00012 00012       F1             VAR_WRITE LONG DBASE+$00001 (short)
            00013 00013       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00014 00014       19             REPEAT_LOOP
            00015 00015       04             RETURN
            00016 00016       00 00          Padding
            """, compile(text));
    }

    @Test
    void testRepeatRangeStep() throws Exception {
        String text = """
            PUB main() | a, b
            
                repeat a from 1 to 10 step 5
                    b := a + 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       17 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     repeat a from 1 to 10 step 5
            00009 00009       42 10          ADDRESS ($00010)
            0000B 0000B       AB             CONSTANT (10)
            0000C 0000C       A6             CONSTANT (5)
            0000D 0000D       A2             CONSTANT (1)
            0000E 0000E       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000F 0000F       18             REPEAT
            '         b := a + 1
            00010 00010       E0             VAR_READ LONG DBASE+$00000 (short)
            00011 00011       A2             CONSTANT (1)
            00012 00012       8A             ADD
            00013 00013       F1             VAR_WRITE LONG DBASE+$00001 (short)
            00014 00014       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00015 00015       19             REPEAT_LOOP
            00016 00016       04             RETURN
            00017 00017       00             Padding
            """, compile(text));
    }

    @Test
    void testRepeatRangeNext() throws Exception {
        String text = """
            PUB main() | a, b
            
                repeat a from 1 to 10 step 5
                    if b > 5
                        next
                    b := a + 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1E 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     repeat a from 1 to 10 step 5
            00009 00009       42 10          ADDRESS ($00010)
            0000B 0000B       AB             CONSTANT (10)
            0000C 0000C       A6             CONSTANT (5)
            0000D 0000D       A2             CONSTANT (1)
            0000E 0000E       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000F 0000F       18             REPEAT
            '         if b > 5
            00010 00010       E1             VAR_READ LONG DBASE+$00001 (short)
            00011 00011       A6             CONSTANT (5)
            00012 00012       74             GREATER_THAN
            00013 00013       13 03          JZ $00017 (3)
            '             next
            00015 00015       12 05          JMP $0001B (5)
            '         b := a + 1
            00017 00017       E0             VAR_READ LONG DBASE+$00000 (short)
            00018 00018       A2             CONSTANT (1)
            00019 00019       8A             ADD
            0001A 0001A       F1             VAR_WRITE LONG DBASE+$00001 (short)
            0001B 0001B       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0001C 0001C       19             REPEAT_LOOP
            0001D 0001D       04             RETURN
            0001E 0001E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testRepeatRangeQuit() throws Exception {
        String text = """
            PUB main() | a, b
            
                repeat a from 1 to 10 step 5
                    if b > 5
                        quit
                    b := a + 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       20 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     repeat a from 1 to 10 step 5
            00009 00009       42 10          ADDRESS ($00010)
            0000B 0000B       AB             CONSTANT (10)
            0000C 0000C       A6             CONSTANT (5)
            0000D 0000D       A2             CONSTANT (1)
            0000E 0000E       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000F 0000F       18             REPEAT
            '         if b > 5
            00010 00010       E1             VAR_READ LONG DBASE+$00001 (short)
            00011 00011       A6             CONSTANT (5)
            00012 00012       74             GREATER_THAN
            00013 00013       13 05          JZ $00019 (5)
            '             quit
            00015 00015       18 0C          POP 16
            00017 00017       12 07          JMP $0001F (7)
            '         b := a + 1
            00019 00019       E0             VAR_READ LONG DBASE+$00000 (short)
            0001A 0001A       A2             CONSTANT (1)
            0001B 0001B       8A             ADD
            0001C 0001C       F1             VAR_WRITE LONG DBASE+$00001 (short)
            0001D 0001D       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0001E 0001E       19             REPEAT_LOOP
            0001F 0001F       04             RETURN
            """, compile(text));
    }

    @Test
    void testRepeatCaseNextQuit() throws Exception {
        String text = """
            PUB main() | a
            
                repeat
                    case a
                        1: next
                        5: quit
                    a++
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       22 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     repeat
            '         case a
            00009 00009       42 1D          ADDRESS ($0001D)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       1C 05          CASE_JMP $00013 (5)
            0000F 0000F       A6             CONSTANT (5)
            00010 00010       1C 07          CASE_JMP $00018 (7)
            00012 00012       1E             CASE_DONE
            '             1: next
            00013 00013       18 04          POP 8
            00015 00015       12 73          JMP $00009 (-13)
            00017 00017       1E             CASE_DONE
            '             5: quit
            00018 00018       18 04          POP 8
            0001A 0001A       12 06          JMP $00021 (6)
            0001C 0001C       1E             CASE_DONE
            '         a++
            0001D 0001D       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0001E 0001E       1F             POST_INC
            0001F 0001F       12 69          JMP $00009 (-23)
            00021 00021       04             RETURN
            00022 00022       00 00          Padding
            """, compile(text));
    }

    @Test
    void testRepeatCaseFastNextQuit() throws Exception {
        String text = """
            PUB main() | a
            
                repeat
                    case_fast a
                        1: next
                        5: quit
                    a++
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       2C 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     repeat
            '         case_fast a
            00009 00009       42 27          ADDRESS ($00027)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       1A             CASE_FAST
            0000D 0000D       01 00 00 00    FROM 1
            00011 00011       05 00          TO 5
            00013 00013       0C 00          CASE_FAST_JMP $0001F (12) <$00011>
            00015 00015       13 00          CASE_FAST_JMP $00026 (19) <$00011>
            00017 00017       13 00          CASE_FAST_JMP $00026 (19) <$00011>
            00019 00019       13 00          CASE_FAST_JMP $00026 (19) <$00011>
            0001B 0001B       10 00          CASE_FAST_JMP $00023 (16) <$00011>
            0001D 0001D       13 00          CASE_FAST_JMP $00026 (19) <$00011>
            '             1: next
            0001F 0001F       17             POP 4
            00020 00020       12 68          JMP $00009 (-24)
            00022 00022       1B             CASE_FAST_DONE
            '             5: quit
            00023 00023       17             POP 4
            00024 00024       12 06          JMP $0002B (6)
            00026 00026       1B             CASE_FAST_DONE
            '         a++
            00027 00027       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00028 00028       1F             POST_INC
            00029 00029       12 5F          JMP $00009 (-33)
            0002B 0002B       04             RETURN
            """, compile(text));
    }

    @Test
    void testRepeatRangeCaseQuit() throws Exception {
        String text = """
            PUB main() | a, b
            
                repeat a from 1 to 10 step 5
                    case a
                        1: quit
                    a := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       21 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     repeat a from 1 to 10 step 5
            00009 00009       42 10          ADDRESS ($00010)
            0000B 0000B       AB             CONSTANT (10)
            0000C 0000C       A6             CONSTANT (5)
            0000D 0000D       A2             CONSTANT (1)
            0000E 0000E       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000F 0000F       18             REPEAT
            '         case a
            00010 00010       42 1C          ADDRESS ($0001C)
            00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)
            00013 00013       A2             CONSTANT (1)
            00014 00014       1C 02          CASE_JMP $00017 (2)
            00016 00016       1E             CASE_DONE
            '             1: quit
            00017 00017       18 14          POP 24
            00019 00019       12 06          JMP $00020 (6)
            0001B 0001B       1E             CASE_DONE
            '         a := 1
            0001C 0001C       A2             CONSTANT (1)
            0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001E 0001E       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0001F 0001F       19             REPEAT_LOOP
            00020 00020       04             RETURN
            00021 00021       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testRepeatRangeCaseFastQuit() throws Exception {
        String text = """
            PUB main() | a, b
            
                repeat a from 1 to 10 step 5
                    case_fast a
                        1: quit
                    a := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       28 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     repeat a from 1 to 10 step 5
            00009 00009       42 10          ADDRESS ($00010)
            0000B 0000B       AB             CONSTANT (10)
            0000C 0000C       A6             CONSTANT (5)
            0000D 0000D       A2             CONSTANT (1)
            0000E 0000E       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000F 0000F       18             REPEAT
            '         case_fast a
            00010 00010       42 23          ADDRESS ($00023)
            00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)
            00013 00013       1A             CASE_FAST
            00014 00014       01 00 00 00    FROM 1
            00018 00018       01 00          TO 1
            0001A 0001A       04 00          CASE_FAST_JMP $0001E (4) <$00018>
            0001C 0001C       08 00          CASE_FAST_JMP $00022 (8) <$00018>
            '             1: quit
            0001E 0001E       18 10          POP 20
            00020 00020       12 06          JMP $00027 (6)
            00022 00022       1B             CASE_FAST_DONE
            '         a := 1
            00023 00023       A2             CONSTANT (1)
            00024 00024       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00025 00025       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00026 00026       19             REPEAT_LOOP
            00027 00027       04             RETURN
            """, compile(text));
    }

    @Test
    void testRepeatNestedCaseQuit() throws Exception {
        String text = """
            PUB main() | a, b
            
                repeat
                    case a
                        1: quit
                        2:
                            case a
                                1: quit
                    a := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       2A 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     repeat
            '         case a
            00009 00009       42 25          ADDRESS ($00025)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       1C 05          CASE_JMP $00013 (5)
            0000F 0000F       A3             CONSTANT (2)
            00010 00010       1C 07          CASE_JMP $00018 (7)
            00012 00012       1E             CASE_DONE
            '             1: quit
            00013 00013       18 04          POP 8
            00015 00015       12 13          JMP $00029 (19)
            00017 00017       1E             CASE_DONE
            '                 case a
            00018 00018       42 24          ADDRESS ($00024)
            0001A 0001A       E0             VAR_READ LONG DBASE+$00000 (short)
            0001B 0001B       A2             CONSTANT (1)
            0001C 0001C       1C 02          CASE_JMP $0001F (2)
            0001E 0001E       1E             CASE_DONE
            '                     1: quit
            0001F 0001F       18 0C          POP 16
            00021 00021       12 07          JMP $00029 (7)
            00023 00023       1E             CASE_DONE
            00024 00024       1E             CASE_DONE
            '         a := 1
            00025 00025       A2             CONSTANT (1)
            00026 00026       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00027 00027       12 61          JMP $00009 (-31)
            00029 00029       04             RETURN
            0002A 0002A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testRepeatNestedCaseFastQuit() throws Exception {
        String text = """
            PUB main() | a, b
            
                repeat
                    case_fast a
                        1: quit
                        2:
                            case_fast a
                                1: quit
                    a := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       36 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     repeat
            '         case_fast a
            00009 00009       42 31          ADDRESS ($00031)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       1A             CASE_FAST
            0000D 0000D       01 00 00 00    FROM 1
            00011 00011       02 00          TO 2
            00013 00013       06 00          CASE_FAST_JMP $00019 (6) <$00011>
            00015 00015       0A 00          CASE_FAST_JMP $0001D (10) <$00011>
            00017 00017       1D 00          CASE_FAST_JMP $00030 (29) <$00011>
            '             1: quit
            00019 00019       17             POP 4
            0001A 0001A       12 1A          JMP $00035 (26)
            0001C 0001C       1B             CASE_FAST_DONE
            '                 case_fast a
            0001D 0001D       42 30          ADDRESS ($00030)
            0001F 0001F       E0             VAR_READ LONG DBASE+$00000 (short)
            00020 00020       1A             CASE_FAST
            00021 00021       01 00 00 00    FROM 1
            00025 00025       01 00          TO 1
            00027 00027       04 00          CASE_FAST_JMP $0002B (4) <$00025>
            00029 00029       08 00          CASE_FAST_JMP $0002F (8) <$00025>
            '                     1: quit
            0002B 0002B       18 04          POP 8
            0002D 0002D       12 07          JMP $00035 (7)
            0002F 0002F       1B             CASE_FAST_DONE
            00030 00030       1B             CASE_FAST_DONE
            '         a := 1
            00031 00031       A2             CONSTANT (1)
            00032 00032       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00033 00033       12 55          JMP $00009 (-43)
            00035 00035       04             RETURN
            00036 00036       00 00          Padding
            """, compile(text));
    }

    @Test
    void testRepeatRangeNestedCaseQuit() throws Exception {
        String text = """
            PUB main() | a, b
            
                repeat a from 1 to 10 step 5
                    case a
                        1: quit
                        2:
                            case a
                                1: quit
                    a := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       31 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     repeat a from 1 to 10 step 5
            00009 00009       42 10          ADDRESS ($00010)
            0000B 0000B       AB             CONSTANT (10)
            0000C 0000C       A6             CONSTANT (5)
            0000D 0000D       A2             CONSTANT (1)
            0000E 0000E       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000F 0000F       18             REPEAT
            '         case a
            00010 00010       42 2C          ADDRESS ($0002C)
            00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)
            00013 00013       A2             CONSTANT (1)
            00014 00014       1C 05          CASE_JMP $0001A (5)
            00016 00016       A3             CONSTANT (2)
            00017 00017       1C 07          CASE_JMP $0001F (7)
            00019 00019       1E             CASE_DONE
            '             1: quit
            0001A 0001A       18 14          POP 24
            0001C 0001C       12 13          JMP $00030 (19)
            0001E 0001E       1E             CASE_DONE
            '                 case a
            0001F 0001F       42 2B          ADDRESS ($0002B)
            00021 00021       E0             VAR_READ LONG DBASE+$00000 (short)
            00022 00022       A2             CONSTANT (1)
            00023 00023       1C 02          CASE_JMP $00026 (2)
            00025 00025       1E             CASE_DONE
            '                     1: quit
            00026 00026       18 1C          POP 32
            00028 00028       12 07          JMP $00030 (7)
            0002A 0002A       1E             CASE_DONE
            0002B 0002B       1E             CASE_DONE
            '         a := 1
            0002C 0002C       A2             CONSTANT (1)
            0002D 0002D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0002E 0002E       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0002F 0002F       19             REPEAT_LOOP
            00030 00030       04             RETURN
            00031 00031       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testRepeatAddressRange() throws Exception {
        String text = """
            PUB main() | a, b
            
                repeat a from @c to @d
                    b := a + 1
            
            DAT
            
            c       long    1
            d       long    2
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            00004 00004       22 00 00 00    End
            00008 00008 00000 01 00 00 00    c                   long    1
            0000C 0000C 00004 02 00 00 00    d                   long    2
            ' PUB main() | a, b
            00010 00010       02             (stack size)
            '     repeat a from @c to @d
            00011 00011       42 1B          ADDRESS ($0001B)
            00013 00013       5B 0C 1B       MEM_ADDRESS PBASE+$0000C
            00016 00016       5B 08 1B       MEM_ADDRESS PBASE+$00008
            00019 00019       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0001A 0001A       17             REPEAT
            '         b := a + 1
            0001B 0001B       E0             VAR_READ LONG DBASE+$00000 (short)
            0001C 0001C       A2             CONSTANT (1)
            0001D 0001D       8A             ADD
            0001E 0001E       F1             VAR_WRITE LONG DBASE+$00001 (short)
            0001F 0001F       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00020 00020       19             REPEAT_LOOP
            00021 00021       04             RETURN
            00022 00022       00 00          Padding
            """, compile(text));
    }

    @Test
    void testMethodCall() throws Exception {
        String text = """
            PUB main()
            
                function()
                \\function()
            
            PUB function()
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       14 00 00 80    Method function @ $00014 (0 parameters, 0 returns)
            00008 00008       16 00 00 00    End
            ' PUB main()
            0000C 0000C       00             (stack size)
            '     function()
            0000D 0000D       00             ANCHOR
            0000E 0000E       0A 01          CALL_SUB (1)
            '     \\function()
            00010 00010       02             ANCHOR_TRAP
            00011 00011       0A 01          CALL_SUB (1)
            00013 00013       04             RETURN
            ' PUB function()
            00014 00014       00             (stack size)
            00015 00015       04             RETURN
            00016 00016       00 00          Padding
            """, compile(text));
    }

    @Test
    void testMethodArgumentsCall() throws Exception {
        String text = """
            PUB main()
            
                function(1, 2)
                \\function(1, 2)
            
            PUB function(a, b)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       18 00 00 82    Method function @ $00018 (2 parameters, 0 returns)
            00008 00008       1A 00 00 00    End
            ' PUB main()
            0000C 0000C       00             (stack size)
            '     function(1, 2)
            0000D 0000D       00             ANCHOR
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       A3             CONSTANT (2)
            00010 00010       0A 01          CALL_SUB (1)
            '     \\function(1, 2)
            00012 00012       02             ANCHOR_TRAP
            00013 00013       A2             CONSTANT (1)
            00014 00014       A3             CONSTANT (2)
            00015 00015       0A 01          CALL_SUB (1)
            00017 00017       04             RETURN
            ' PUB function(a, b)
            00018 00018       00             (stack size)
            00019 00019       04             RETURN
            0001A 0001A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testMethodDefaultArgument() throws Exception {
        String text = """
            PUB main()
            
                function(1)
                \\function(1)
            
            PUB function(a, b = 2)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       18 00 00 82    Method function @ $00018 (2 parameters, 0 returns)
            00008 00008       1A 00 00 00    End
            ' PUB main()
            0000C 0000C       00             (stack size)
            '     function(1)
            0000D 0000D       00             ANCHOR
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       A3             CONSTANT (2)
            00010 00010       0A 01          CALL_SUB (1)
            '     \\function(1)
            00012 00012       02             ANCHOR_TRAP
            00013 00013       A2             CONSTANT (1)
            00014 00014       A3             CONSTANT (2)
            00015 00015       0A 01          CALL_SUB (1)
            00017 00017       04             RETURN
            ' PUB function(a, b = 2)
            00018 00018       00             (stack size)
            00019 00019       04             RETURN
            0001A 0001A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testMethodDefaultArgumentOverride() throws Exception {
        String text = """
            PUB main()
            
                function(1, 3)
            
            PUB function(a, b = 2)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       13 00 00 82    Method function @ $00013 (2 parameters, 0 returns)
            00008 00008       15 00 00 00    End
            ' PUB main()
            0000C 0000C       00             (stack size)
            '     function(1, 3)
            0000D 0000D       00             ANCHOR
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       A4             CONSTANT (3)
            00010 00010       0A 01          CALL_SUB (1)
            00012 00012       04             RETURN
            ' PUB function(a, b = 2)
            00013 00013       00             (stack size)
            00014 00014       04             RETURN
            00015 00015       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testMethodCallReturn() throws Exception {
        String text = """
            PUB main() | a
            
                a := function()
            
            PUB function() : rc
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       12 00 10 80    Method function @ $00012 (0 parameters, 1 returns)
            00008 00008       14 00 00 00    End
            ' PUB main() | a
            0000C 0000C       01             (stack size)
            '     a := function()
            0000D 0000D       01             ANCHOR (push)
            0000E 0000E       0A 01          CALL_SUB (1)
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00011 00011       04             RETURN
            ' PUB function() : rc
            00012 00012       00             (stack size)
            00013 00013       04             RETURN
            """, compile(text));
    }

    @Test
    void testMethodCharacterArguments() throws Exception {
        String text = """
            PUB main()
            
                function("1")
            
            PUB function(a)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       13 00 00 81    Method function @ $00013 (1 parameters, 0 returns)
            00008 00008       15 00 00 00    End
            ' PUB main()
            0000C 0000C       00             (stack size)
            '     function("1")
            0000D 0000D       00             ANCHOR
            0000E 0000E       42 31          CONSTANT ("1")
            00010 00010       0A 01          CALL_SUB (1)
            00012 00012       04             RETURN
            ' PUB function(a)
            00013 00013       00             (stack size)
            00014 00014       04             RETURN
            00015 00015       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testMethodStringArgument() throws Exception {
        String text = """
            PUB main()
            
                function(string("1234"))
            
            PUB function(a)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       18 00 00 81    Method function @ $00018 (1 parameters, 0 returns)
            00008 00008       1A 00 00 00    End
            ' PUB main()
            0000C 0000C       00             (stack size)
            '     function(string("1234"))
            0000D 0000D       00             ANCHOR
            0000E 0000E       9E 05 31 32 33 STRING
            00013 00013       34 00
            00015 00015       0A 01          CALL_SUB (1)
            00017 00017       04             RETURN
            ' PUB function(a)
            00018 00018       00             (stack size)
            00019 00019       04             RETURN
            0001A 0001A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testMethodAutomaticStringArgument() throws Exception {
        String text = """
            PUB main()
            
                function("1234")
            
            PUB function(a)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       18 00 00 81    Method function @ $00018 (1 parameters, 0 returns)
            00008 00008       1A 00 00 00    End
            ' PUB main()
            0000C 0000C       00             (stack size)
            '     function("1234")
            0000D 0000D       00             ANCHOR
            0000E 0000E       9E 05 31 32 33 STRING
            00013 00013       34 00
            00015 00015       0A 01          CALL_SUB (1)
            00017 00017       04             RETURN
            ' PUB function(a)
            00018 00018       00             (stack size)
            00019 00019       04             RETURN
            0001A 0001A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testMethodMixedStringArgument() throws Exception {
        String text = """
            PUB main()
            
                function(string("1234", 13, 10))
            
            PUB function(a)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       1A 00 00 81    Method function @ $0001A (1 parameters, 0 returns)
            00008 00008       1C 00 00 00    End
            ' PUB main()
            0000C 0000C       00             (stack size)
            '     function(string("1234", 13, 10))
            0000D 0000D       00             ANCHOR
            0000E 0000E       9E 07 31 32 33 STRING
            00013 00013       34 0D 0A 00
            00017 00017       0A 01          CALL_SUB (1)
            00019 00019       04             RETURN
            ' PUB function(a)
            0001A 0001A       00             (stack size)
            0001B 0001B       04             RETURN
            """, compile(text));
    }

    @Test
    void testMethodOrder() throws Exception {
        String text = """
            PUB main()
            
            PRI function1()
            
            PUB function2()
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            00004 00004       12 00 00 80    Method function2 @ $00012 (0 parameters, 0 returns)
            00008 00008       14 00 00 80    Method function1 @ $00014 (0 parameters, 0 returns)
            0000C 0000C       16 00 00 00    End
            ' PUB main()
            00010 00010       00             (stack size)
            00011 00011       04             RETURN
            ' PUB function2()
            00012 00012       00             (stack size)
            00013 00013       04             RETURN
            ' PRI function1()
            00014 00014       00             (stack size)
            00015 00015       04             RETURN
            00016 00016       00 00          Padding
            """, compile(text));
    }

    @Test
    void testPriMethodCall() throws Exception {
        String text = """
            PUB main()
            
                function1()
            
            PRI function1()
            
            PUB function2()
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            00004 00004       15 00 00 80    Method function2 @ $00015 (0 parameters, 0 returns)
            00008 00008       17 00 00 80    Method function1 @ $00017 (0 parameters, 0 returns)
            0000C 0000C       19 00 00 00    End
            ' PUB main()
            00010 00010       00             (stack size)
            '     function1()
            00011 00011       00             ANCHOR
            00012 00012       0A 02          CALL_SUB (2)
            00014 00014       04             RETURN
            ' PUB function2()
            00015 00015       00             (stack size)
            00016 00016       04             RETURN
            ' PRI function1()
            00017 00017       00             (stack size)
            00018 00018       04             RETURN
            00019 00019       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testAbort() throws Exception {
        String text = """
            PUB main()
            
                abort
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0B 00 00 00    End
            ' PUB main()
            00008 00008       00             (stack size)
            '     abort
            00009 00009       06             ABORT
            0000A 0000A       04             RETURN
            0000B 0000B       00             Padding
            """, compile(text));
    }

    @Test
    void testAbortArgument() throws Exception {
        String text = """
            PUB main()
            
                abort 1
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0C 00 00 00    End
            ' PUB main()
            00008 00008       00             (stack size)
            '     abort 1
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       07             ABORT
            0000B 0000B       04             RETURN
            """, compile(text));
    }

    @Test
    void testDataPointer() throws Exception {
        String text = """
            PUB main() | a
            
                    a := @b
            
            DAT             org     $000
            
            b               long    1
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       12 00 00 00    End
            00008 00008   000                                    org     $000
            00008 00008   000 01 00 00 00    b                   long    1
            ' PUB main() | a
            0000C 0000C       01             (stack size)
            '         a := @b
            0000D 0000D       5B 08 1B       MEM_ADDRESS PBASE+$00008
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00011 00011       04             RETURN
            00012 00012       00 00          Padding
            """, compile(text));
    }

    @Test
    void testDataValueRead() throws Exception {
        String text = """
            PUB main() | a
            
                    a := b
            
            DAT             org     $000
            
            b               long    1
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       12 00 00 00    End
            00008 00008   000                                    org     $000
            00008 00008   000 01 00 00 00    b                   long    1
            ' PUB main() | a
            0000C 0000C       01             (stack size)
            '         a := b
            0000D 0000D       5B 08 1C       MEM_READ LONG PBASE+$00008
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00011 00011       04             RETURN
            00012 00012       00 00          Padding
            """, compile(text));
    }

    @Test
    void testDataValueAssign() throws Exception {
        String text = """
            PUB main() | a
            
                    b := a
            
            DAT             org     $000
            
            b               long    1
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       12 00 00 00    End
            00008 00008   000                                    org     $000
            00008 00008   000 01 00 00 00    b                   long    1
            ' PUB main() | a
            0000C 0000C       01             (stack size)
            '         b := a
            0000D 0000D       E0             VAR_READ LONG DBASE+$00000 (short)
            0000E 0000E       5B 08 1D       MEM_WRITE LONG PBASE+$00008
            00011 00011       04             RETURN
            00012 00012       00 00          Padding
            """, compile(text));
    }

    @Test
    void testVarAddress() throws Exception {
        String text = """
            VAR b[20], c
            
            PUB main() | a
            
                    a := @b
                    a := @c
                    a := @b[a]
                    a := @c[a]
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 88)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1B 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '         a := @b
            00009 00009       C1 1B          VAR_ADDRESS VBASE+$00001 (short)
            0000B 0000B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         a := @c
            0000C 0000C       5C 54 1B       VAR_ADDRESS VBASE+$00054
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         a := @b[a]
            00010 00010       E0             VAR_READ LONG DBASE+$00000 (short)
            00011 00011       5F 04 1B       VAR_ADDRESS_INDEXED VBASE+$00004
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         a := @c[a]
            00015 00015       E0             VAR_READ LONG DBASE+$00000 (short)
            00016 00016       5F 54 1B       VAR_ADDRESS_INDEXED VBASE+$00054
            00019 00019       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001A 0001A       04             RETURN
            0001B 0001B       00             Padding
            """, compile(text));
    }

    @Test
    void testVarAlign() throws Exception {
        String text = """
            VAR
                byte b
                alignl
                byte c
                alignw
                byte d
            
            PUB main() | a
            
                    a := @b
                    a := @c
                    a := @d
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 12)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       16 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '         a := @b
            00009 00009       50 04 1B       VAR_ADDRESS VBASE+$00004
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         a := @c
            0000D 0000D       50 08 1B       VAR_ADDRESS VBASE+$00008
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         a := @d
            00011 00011       50 0A 1B       VAR_ADDRESS VBASE+$0000A
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00015 00015       04             RETURN
            00016 00016       00 00          Padding
            """, compile(text));
    }

    @Test
    void testVarAbsoluteAddress() throws Exception {
        String text = """
            VAR b[20], c
            
            PUB main() | a
            
                    a := @@b
                    a := @@c
                    a := @@b[a]
                    a := @@c[a]
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 88)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1F 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '         a := @@b
            00009 00009       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            0000B 0000B       24             ADD PBASE
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         a := @@c
            0000D 0000D       5C 54 1C       VAR_READ LONG VBASE+$00054
            00010 00010       24             ADD PBASE
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         a := @@b[a]
            00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)
            00013 00013       5F 04 1C       VAR_READ_INDEXED LONG VBASE+$00004
            00016 00016       24             ADD PBASE
            00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         a := @@c[a]
            00018 00018       E0             VAR_READ LONG DBASE+$00000 (short)
            00019 00019       5F 54 1C       VAR_READ_INDEXED LONG VBASE+$00054
            0001C 0001C       24             ADD PBASE
            0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001E 0001E       04             RETURN
            0001F 0001F       00             Padding
            """, compile(text));
    }

    @Test
    void testLocalVarAddress() throws Exception {
        String text = """
            PUB main() | a, b[20], c
            
                    a := @b
                    a := @c
                    a := @b[a]
                    a := @c[a]
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1B 00 00 00    End
            ' PUB main() | a, b[20], c
            00008 00008       16             (stack size)
            '         a := @b
            00009 00009       D1 1B          VAR_ADDRESS DBASE+$00001 (short)
            0000B 0000B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         a := @c
            0000C 0000C       5D 54 1B       VAR_ADDRESS DBASE+$00054
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         a := @b[a]
            00010 00010       E0             VAR_READ LONG DBASE+$00000 (short)
            00011 00011       60 04 1B       VAR_ADDRESS_INDEXED DBASE+$00004
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         a := @c[a]
            00015 00015       E0             VAR_READ LONG DBASE+$00000 (short)
            00016 00016       60 54 1B       VAR_ADDRESS_INDEXED DBASE+$00054
            00019 00019       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001A 0001A       04             RETURN
            0001B 0001B       00             Padding
            """, compile(text));
    }

    @Test
    void testLocalVarAbsoluteAddress() throws Exception {
        String text = """
            PUB main() | a, b[20], c
            
                    a := @@b
                    a := @@c
                    a := @@b[a]
                    a := @@c[a]
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1E 00 00 00    End
            ' PUB main() | a, b[20], c
            00008 00008       16             (stack size)
            '         a := @@b
            00009 00009       E1             VAR_READ LONG DBASE+$00001 (short)
            0000A 0000A       24             ADD PBASE
            0000B 0000B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         a := @@c
            0000C 0000C       5D 54 1C       VAR_READ LONG DBASE+$00054
            0000F 0000F       24             ADD PBASE
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         a := @@b[a]
            00011 00011       E0             VAR_READ LONG DBASE+$00000 (short)
            00012 00012       60 04 1C       VAR_READ_INDEXED LONG DBASE+$00004
            00015 00015       24             ADD PBASE
            00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         a := @@c[a]
            00017 00017       E0             VAR_READ LONG DBASE+$00000 (short)
            00018 00018       60 54 1C       VAR_READ_INDEXED LONG DBASE+$00054
            0001B 0001B       24             ADD PBASE
            0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001D 0001D       04             RETURN
            0001E 0001E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testInternalFunction() throws Exception {
        String text = """
            PUB main() | a
            
                    a := muldiv64(1, 2, 3)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       10 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '         a := muldiv64(1, 2, 3)
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       A3             CONSTANT (2)
            0000B 0000B       A4             CONSTANT (3)
            0000C 0000C       19 92          MULDIV64
            0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0000F 0000F       04             RETURN
            """, compile(text));
    }

    @Test
    void testAddress() throws Exception {
        String text = """
            VAR
                long a
            
            PUB main() | ptr, b
            
                ptr := @a
                ptr := @@a
            
                ptr := @b
                ptr := @@b
            
                ptr := @table
                ptr := @@table
                ptr := @@table[a]
                ptr := @@@table
            
            DAT
                    orgh
            
            table   long    0
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 8)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       2D 00 00 00    End
            00008 00008 00400                                    orgh
            00008 00008 00400 00 00 00 00    table               long    0
            ' PUB main() | ptr, b
            0000C 0000C       02             (stack size)
            '     ptr := @a
            0000D 0000D       C1 1B          VAR_ADDRESS VBASE+$00001 (short)
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr := @@a
            00010 00010       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            00012 00012       24             ADD PBASE
            00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr := @b
            00014 00014       D1 1B          VAR_ADDRESS DBASE+$00001 (short)
            00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr := @@b
            00017 00017       E1             VAR_READ LONG DBASE+$00001 (short)
            00018 00018       24             ADD PBASE
            00019 00019       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr := @table
            0001A 0001A       5B 08 1B       MEM_ADDRESS PBASE+$00008
            0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr := @@table
            0001E 0001E       5B 08 1C       MEM_READ LONG PBASE+$00008
            00021 00021       24             ADD_PBASE
            00022 00022       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr := @@table[a]
            00023 00023       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            00025 00025       5E 08 1C       MEM_READ LONG INDEXED PBASE+$00008
            00028 00028       24             ADD_PBASE
            00029 00029       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr := @@@table
            0002A 0002A       A9             CONSTANT ($00008)
            0002B 0002B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0002C 0002C       04             RETURN
            0002D 0002D       00 00 00       Padding
            """, compile(text, false));
    }

    @Test
    void testDebug() throws Exception {
        String text = """
            PUB main() | a
            
                debug(udec(a))
                debug(udec(a + 1))
                debug(udec(1 + 2))
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       18 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     debug(udec(a))
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       41 04 01       DEBUG #1
            '     debug(udec(a + 1))
            0000D 0000D       E0             VAR_READ LONG DBASE+$00000 (short)
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       8A             ADD
            00010 00010       41 04 02       DEBUG #2
            '     debug(udec(1 + 2))
            00013 00013       A4             CONSTANT (1 + 2)
            00014 00014       41 04 03       DEBUG #3
            00017 00017       04             RETURN
            ' Debug data
            00B74 00000       1F 00        \s
            00B76 00002       08 00          #1@0008
            00B78 00004       0D 00          #2@000D
            00B7A 00006       16 00          #3@0016
            ' #1
            00B7C 00008       04             COGN
            00B7D 00009       41 61 00       UDEC(a)
            00B80 0000C       00             DONE
            ' #2
            00B81 0000D       04             COGN
            00B82 0000E       41 61 20 2B 20 UDEC(a + 1)
            00B87 00013       31 00
            00B89 00015       00             DONE
            ' #3
            00B8A 00016       04             COGN
            00B8B 00017       41 31 20 2B 20 UDEC(1 + 2)
            00B90 0001C       32 00
            00B92 0001E       00             DONE
            """, compile(text, true));
    }

    @Test
    void testIgnoreDebug() throws Exception {
        String text = """
            PUB main() | a
            
                debug(udec(a))
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0A 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     debug(udec(a))
            00009 00009       04             RETURN
            0000A 0000A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testThrowDebugException() throws Exception {
        String text = """
            PUB main()
            
                debug(udec(a))
            
            """;

        Assertions.assertThrows(CompilerException.class, () -> {
            compile(text);
        });
    }

    @Test
    void testModifyExpressions() throws Exception {
        String text = """
            PUB main() | a
            
                a += 1
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0D 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     a += 1
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000B 0000B       3F             ADD_ASSIGN
            0000C 0000C       04             RETURN
            0000D 0000D       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testCase() throws Exception {
        String text = """
            PUB main() | a
            
                case a
                    1: a := 4
                    2: a := 5
                    3: a := 6
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       20 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     case a
            00009 00009       42 1F          ADDRESS ($0001F)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       1C 08          CASE_JMP $00016 (8)
            0000F 0000F       A3             CONSTANT (2)
            00010 00010       1C 08          CASE_JMP $00019 (8)
            00012 00012       A4             CONSTANT (3)
            00013 00013       1C 08          CASE_JMP $0001C (8)
            00015 00015       1E             CASE_DONE
            '         1: a := 4
            00016 00016       A5             CONSTANT (4)
            00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00018 00018       1E             CASE_DONE
            '         2: a := 5
            00019 00019       A6             CONSTANT (5)
            0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001B 0001B       1E             CASE_DONE
            '         3: a := 6
            0001C 0001C       A7             CONSTANT (6)
            0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001E 0001E       1E             CASE_DONE
            0001F 0001F       04             RETURN
            """, compile(text));
    }

    @Test
    void testCaseFast() throws Exception {
        String text = """
            PUB main() | a
            
                case_fast a
                    1: a := 4
                    2: a := 5
                    3: a := 6
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       25 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     case_fast a
            00009 00009       42 24          ADDRESS ($00024)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       1A             CASE_FAST
            0000D 0000D       01 00 00 00    FROM 1
            00011 00011       03 00          TO 3
            00013 00013       08 00          CASE_FAST_JMP $0001B (8) <$00011>
            00015 00015       0B 00          CASE_FAST_JMP $0001E (11) <$00011>
            00017 00017       0E 00          CASE_FAST_JMP $00021 (14) <$00011>
            00019 00019       10 00          CASE_FAST_JMP $00023 (16) <$00011>
            '         1: a := 4
            0001B 0001B       A5             CONSTANT (4)
            0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001D 0001D       1B             CASE_FAST_DONE
            '         2: a := 5
            0001E 0001E       A6             CONSTANT (5)
            0001F 0001F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00020 00020       1B             CASE_FAST_DONE
            '         3: a := 6
            00021 00021       A7             CONSTANT (6)
            00022 00022       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00023 00023       1B             CASE_FAST_DONE
            00024 00024       04             RETURN
            00025 00025       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testCaseOther() throws Exception {
        String text = """
            PUB main() | a
            
                case a
                    1: a := 4
                    2: a := 5
                    3: a := 6
                    other: a := 7
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       22 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     case a
            00009 00009       42 21          ADDRESS ($00021)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       1C 0A          CASE_JMP $00018 (10)
            0000F 0000F       A3             CONSTANT (2)
            00010 00010       1C 0A          CASE_JMP $0001B (10)
            00012 00012       A4             CONSTANT (3)
            00013 00013       1C 0A          CASE_JMP $0001E (10)
            '         other: a := 7
            00015 00015       A8             CONSTANT (7)
            00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00017 00017       1E             CASE_DONE
            '         1: a := 4
            00018 00018       A5             CONSTANT (4)
            00019 00019       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001A 0001A       1E             CASE_DONE
            '         2: a := 5
            0001B 0001B       A6             CONSTANT (5)
            0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001D 0001D       1E             CASE_DONE
            '         3: a := 6
            0001E 0001E       A7             CONSTANT (6)
            0001F 0001F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00020 00020       1E             CASE_DONE
            00021 00021       04             RETURN
            00022 00022       00 00          Padding
            """, compile(text));
    }

    @Test
    void testCaseFastGaps() throws Exception {
        String text = """
            PUB main() | a
            
                case_fast a
                    1: a := 4
                    5: a := 5
                    7: a := 6
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       2D 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     case_fast a
            00009 00009       42 2C          ADDRESS ($0002C)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       1A             CASE_FAST
            0000D 0000D       01 00 00 00    FROM 1
            00011 00011       07 00          TO 7
            00013 00013       10 00          CASE_FAST_JMP $00023 (16) <$00011>
            00015 00015       18 00          CASE_FAST_JMP $0002B (24) <$00011>
            00017 00017       18 00          CASE_FAST_JMP $0002B (24) <$00011>
            00019 00019       18 00          CASE_FAST_JMP $0002B (24) <$00011>
            0001B 0001B       13 00          CASE_FAST_JMP $00026 (19) <$00011>
            0001D 0001D       18 00          CASE_FAST_JMP $0002B (24) <$00011>
            0001F 0001F       16 00          CASE_FAST_JMP $00029 (22) <$00011>
            00021 00021       18 00          CASE_FAST_JMP $0002B (24) <$00011>
            '         1: a := 4
            00023 00023       A5             CONSTANT (4)
            00024 00024       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00025 00025       1B             CASE_FAST_DONE
            '         5: a := 5
            00026 00026       A6             CONSTANT (5)
            00027 00027       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00028 00028       1B             CASE_FAST_DONE
            '         7: a := 6
            00029 00029       A7             CONSTANT (6)
            0002A 0002A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0002B 0002B       1B             CASE_FAST_DONE
            0002C 0002C       04             RETURN
            0002D 0002D       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testCaseFastOther() throws Exception {
        String text = """
            PUB main() | a
            
                case_fast a
                    1: a := 4
                    2: a := 5
                    3: a := 6
                    other: a := 7
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       28 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     case_fast a
            00009 00009       42 27          ADDRESS ($00027)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       1A             CASE_FAST
            0000D 0000D       01 00 00 00    FROM 1
            00011 00011       03 00          TO 3
            00013 00013       08 00          CASE_FAST_JMP $0001B (8) <$00011>
            00015 00015       0B 00          CASE_FAST_JMP $0001E (11) <$00011>
            00017 00017       0E 00          CASE_FAST_JMP $00021 (14) <$00011>
            00019 00019       11 00          CASE_FAST_JMP $00024 (17) <$00011>
            '         1: a := 4
            0001B 0001B       A5             CONSTANT (4)
            0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001D 0001D       1B             CASE_FAST_DONE
            '         2: a := 5
            0001E 0001E       A6             CONSTANT (5)
            0001F 0001F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00020 00020       1B             CASE_FAST_DONE
            '         3: a := 6
            00021 00021       A7             CONSTANT (6)
            00022 00022       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00023 00023       1B             CASE_FAST_DONE
            '         other: a := 7
            00024 00024       A8             CONSTANT (7)
            00025 00025       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00026 00026       1B             CASE_FAST_DONE
            00027 00027       04             RETURN
            """, compile(text));
    }

    @Test
    void testCaseFastWithGaps() throws Exception {
        String text = """
            PUB main() | a
            
                case_fast a
                    1: a := 4
                    5: a := 5
                    7: a := 6
                    other: a := 7
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       30 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     case_fast a
            00009 00009       42 2F          ADDRESS ($0002F)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       1A             CASE_FAST
            0000D 0000D       01 00 00 00    FROM 1
            00011 00011       07 00          TO 7
            00013 00013       10 00          CASE_FAST_JMP $00023 (16) <$00011>
            00015 00015       19 00          CASE_FAST_JMP $0002C (25) <$00011>
            00017 00017       19 00          CASE_FAST_JMP $0002C (25) <$00011>
            00019 00019       19 00          CASE_FAST_JMP $0002C (25) <$00011>
            0001B 0001B       13 00          CASE_FAST_JMP $00026 (19) <$00011>
            0001D 0001D       19 00          CASE_FAST_JMP $0002C (25) <$00011>
            0001F 0001F       16 00          CASE_FAST_JMP $00029 (22) <$00011>
            00021 00021       19 00          CASE_FAST_JMP $0002C (25) <$00011>
            '         1: a := 4
            00023 00023       A5             CONSTANT (4)
            00024 00024       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00025 00025       1B             CASE_FAST_DONE
            '         5: a := 5
            00026 00026       A6             CONSTANT (5)
            00027 00027       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00028 00028       1B             CASE_FAST_DONE
            '         7: a := 6
            00029 00029       A7             CONSTANT (6)
            0002A 0002A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0002B 0002B       1B             CASE_FAST_DONE
            '         other: a := 7
            0002C 0002C       A8             CONSTANT (7)
            0002D 0002D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0002E 0002E       1B             CASE_FAST_DONE
            0002F 0002F       04             RETURN
            """, compile(text));
    }

    @Test
    void testCaseRange() throws Exception {
        String text = """
            PUB main() | a
            
                case a
                    1..5: a := 6
                    other: a := 7
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       17 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     case a
            00009 00009       42 16          ADDRESS ($00016)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       A6             CONSTANT (5)
            0000E 0000E       1D 04          CASE_RANGE_JMP $00013 (4)
            '         other: a := 7
            00010 00010       A8             CONSTANT (7)
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00012 00012       1E             CASE_DONE
            '         1..5: a := 6
            00013 00013       A7             CONSTANT (6)
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00015 00015       1E             CASE_DONE
            00016 00016       04             RETURN
            00017 00017       00             Padding
            """, compile(text));
    }

    @Test
    void testCaseFastRange() throws Exception {
        String text = """
            PUB main() | a
            
                case_fast a
                    1..5: a := 6
                    other: a := 7
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       26 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     case_fast a
            00009 00009       42 25          ADDRESS ($00025)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       1A             CASE_FAST
            0000D 0000D       01 00 00 00    FROM 1
            00011 00011       05 00          TO 5
            00013 00013       0C 00          CASE_FAST_JMP $0001F (12) <$00011>
            00015 00015       0C 00          CASE_FAST_JMP $0001F (12) <$00011>
            00017 00017       0C 00          CASE_FAST_JMP $0001F (12) <$00011>
            00019 00019       0C 00          CASE_FAST_JMP $0001F (12) <$00011>
            0001B 0001B       0C 00          CASE_FAST_JMP $0001F (12) <$00011>
            0001D 0001D       0F 00          CASE_FAST_JMP $00022 (15) <$00011>
            '         1..5: a := 6
            0001F 0001F       A7             CONSTANT (6)
            00020 00020       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00021 00021       1B             CASE_FAST_DONE
            '         other: a := 7
            00022 00022       A8             CONSTANT (7)
            00023 00023       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00024 00024       1B             CASE_FAST_DONE
            00025 00025       04             RETURN
            00026 00026       00 00          Padding
            """, compile(text));
    }

    @Test
    void testCaseList() throws Exception {
        String text = """
            PUB main() | a
            
                case a
                    1, 2      : a := 14
                    3, 4, 5   : a := 15
                    6, 7, 8, 9: a := 16
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       34 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     case a
            00009 00009       42 33          ADDRESS ($00033)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       1C 1A          CASE_JMP $00028 (26)
            0000F 0000F       A3             CONSTANT (2)
            00010 00010       1C 17          CASE_JMP $00028 (23)
            00012 00012       A4             CONSTANT (3)
            00013 00013       1C 17          CASE_JMP $0002B (23)
            00015 00015       A5             CONSTANT (4)
            00016 00016       1C 14          CASE_JMP $0002B (20)
            00018 00018       A6             CONSTANT (5)
            00019 00019       1C 11          CASE_JMP $0002B (17)
            0001B 0001B       A7             CONSTANT (6)
            0001C 0001C       1C 12          CASE_JMP $0002F (18)
            0001E 0001E       A8             CONSTANT (7)
            0001F 0001F       1C 0F          CASE_JMP $0002F (15)
            00021 00021       A9             CONSTANT (8)
            00022 00022       1C 0C          CASE_JMP $0002F (12)
            00024 00024       AA             CONSTANT (9)
            00025 00025       1C 09          CASE_JMP $0002F (9)
            00027 00027       1E             CASE_DONE
            '         1, 2      : a := 14
            00028 00028       AF             CONSTANT (14)
            00029 00029       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0002A 0002A       1E             CASE_DONE
            '         3, 4, 5   : a := 15
            0002B 0002B       42 0F          CONSTANT (15)
            0002D 0002D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0002E 0002E       1E             CASE_DONE
            '         6, 7, 8, 9: a := 16
            0002F 0002F       42 10          CONSTANT (16)
            00031 00031       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00032 00032       1E             CASE_DONE
            00033 00033       04             RETURN
            """, compile(text));
    }

    @Test
    void testCaseFastList() throws Exception {
        String text = """
            PUB main() | a
            
                case_fast a
                    1, 2      : a := 14
                    3, 4, 5   : a := 15
                    6, 7, 8, 9: a := 16
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       33 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     case_fast a
            00009 00009       42 32          ADDRESS ($00032)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       1A             CASE_FAST
            0000D 0000D       01 00 00 00    FROM 1
            00011 00011       09 00          TO 9
            00013 00013       14 00          CASE_FAST_JMP $00027 (20) <$00011>
            00015 00015       14 00          CASE_FAST_JMP $00027 (20) <$00011>
            00017 00017       17 00          CASE_FAST_JMP $0002A (23) <$00011>
            00019 00019       17 00          CASE_FAST_JMP $0002A (23) <$00011>
            0001B 0001B       17 00          CASE_FAST_JMP $0002A (23) <$00011>
            0001D 0001D       1B 00          CASE_FAST_JMP $0002E (27) <$00011>
            0001F 0001F       1B 00          CASE_FAST_JMP $0002E (27) <$00011>
            00021 00021       1B 00          CASE_FAST_JMP $0002E (27) <$00011>
            00023 00023       1B 00          CASE_FAST_JMP $0002E (27) <$00011>
            00025 00025       1E 00          CASE_FAST_JMP $00031 (30) <$00011>
            '         1, 2      : a := 14
            00027 00027       AF             CONSTANT (14)
            00028 00028       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00029 00029       1B             CASE_FAST_DONE
            '         3, 4, 5   : a := 15
            0002A 0002A       42 0F          CONSTANT (15)
            0002C 0002C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0002D 0002D       1B             CASE_FAST_DONE
            '         6, 7, 8, 9: a := 16
            0002E 0002E       42 10          CONSTANT (16)
            00030 00030       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00031 00031       1B             CASE_FAST_DONE
            00032 00032       04             RETURN
            00033 00033       00             Padding
            """, compile(text));
    }

    @Test
    void testCaseFunction() throws Exception {
        String text = """
            PUB main() | a
            
                case peek()
                    1: a := 10\s
                    2: a := 20
                    other: a:= 100\s
            
            PRI peek() : rc
            
                rc := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       24 00 10 80    Method peek @ $00024 (0 parameters, 1 returns)
            00008 00008       28 00 00 00    End
            ' PUB main() | a
            0000C 0000C       01             (stack size)
            '     case peek()
            0000D 0000D       42 23          ADDRESS ($00023)
            0000F 0000F       01             ANCHOR (push)
            00010 00010       0A 01          CALL_SUB (1)
            00012 00012       A2             CONSTANT (1)
            00013 00013       1C 08          CASE_JMP $0001C (8)
            00015 00015       A3             CONSTANT (2)
            00016 00016       1C 08          CASE_JMP $0001F (8)
            '         other: a:= 100
            00018 00018       42 64          CONSTANT (100)
            0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001B 0001B       1E             CASE_DONE
            '         1: a := 10
            0001C 0001C       AB             CONSTANT (10)
            0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001E 0001E       1E             CASE_DONE
            '         2: a := 20
            0001F 0001F       42 14          CONSTANT (20)
            00021 00021       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00022 00022       1E             CASE_DONE
            00023 00023       04             RETURN
            ' PRI peek() : rc
            00024 00024       00             (stack size)
            '     rc := 1
            00025 00025       A2             CONSTANT (1)
            00026 00026       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00027 00027       04             RETURN
            """, compile(text));
    }

    @Test
    void testSendVariable() throws Exception {
        String text = """
            PUB main() | a
            
                SEND(a)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0C 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     SEND(a)
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       0D             SEND
            0000B 0000B       04             RETURN
            """, compile(text));
    }

    @Test
    void testSendBytes() throws Exception {
        String text = """
            PUB main() | a
            
                SEND(1,2,3)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0F 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     SEND(1,2,3)
            00009 00009       0E 03 01 02 03 SEND_BYTES
            0000E 0000E       04             RETURN
            0000F 0000F       00             Padding
            """, compile(text));
    }

    @Test
    void testSendMixed() throws Exception {
        String text = """
            PUB main() | a
            
                SEND(a,2,3)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       10 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     SEND(a,2,3)
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       0D             SEND
            0000B 0000B       0E 02 02 03    SEND_BYTES
            0000F 0000F       04             RETURN
            """, compile(text));
    }

    @Test
    void testSendMethods() throws Exception {
        String text = """
            PUB main() | ptr
            
                SEND(1, char_val(), text_append(), ptr(), ptr():1)
            
            PRI char_val() : r
            
            PRI text_append()
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            00004 00004       22 00 10 80    Method char_val @ $00022 (0 parameters, 1 returns)
            00008 00008       24 00 00 80    Method text_append @ $00024 (0 parameters, 0 returns)
            0000C 0000C       26 00 00 00    End
            ' PUB main() | ptr
            00010 00010       01             (stack size)
            '     SEND(1, char_val(), text_append(), ptr(), ptr():1)
            00011 00011       A2             CONSTANT (1)
            00012 00012       0D             SEND
            00013 00013       01             ANCHOR (push)
            00014 00014       0A 01          CALL_SUB (1)
            00016 00016       0D             SEND
            00017 00017       00             ANCHOR
            00018 00018       0A 02          CALL_SUB (2)
            0001A 0001A       00             ANCHOR
            0001B 0001B       E0             VAR_READ LONG DBASE+$00000 (short)
            0001C 0001C       0B             CALL_PTR
            0001D 0001D       01             ANCHOR (push)
            0001E 0001E       E0             VAR_READ LONG DBASE+$00000 (short)
            0001F 0001F       0B             CALL_PTR
            00020 00020       0D             SEND
            00021 00021       04             RETURN
            ' PRI char_val() : r
            00022 00022       00             (stack size)
            00023 00023       04             RETURN
            ' PRI text_append()
            00024 00024       00             (stack size)
            00025 00025       04             RETURN
            00026 00026       00 00          Padding
            """, compile(text));
    }

    @Test
    void testSendStrings() throws Exception {
        String text = """
            PUB main() | a
            
                SEND("Hello")
                SEND(@"Hello")
                SEND(@\\"Hello")
                SEND(%"4321")
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       29 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     SEND("Hello")
            00009 00009       0E 05 48 65 6C SEND_BYTES
            0000E 0000E       6C 6F
            '     SEND(@"Hello")
            00010 00010       9E 06 48 65 6C STRING
            00015 00015       6C 6F 00
            00018 00018       0D             SEND
            '     SEND(@\\"Hello")
            00019 00019       9E 06 48 65 6C STRING
            0001E 0001E       6C 6F 00
            00021 00021       0D             SEND
            '     SEND(%"4321")
            00022 00022       46 34 33 32 31 CONSTANT (%"4321")
            00027 00027       0D             SEND
            00028 00028       04             RETURN
            00029 00029       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testSendAssign() throws Exception {
        String text = """
            PUB main()
            
                SEND := @out
            
            PRI out()
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       13 00 00 80    Method out @ $00013 (0 parameters, 0 returns)
            00008 00008       15 00 00 00    End
            ' PUB main()
            0000C 0000C       00             (stack size)
            '     SEND := @out
            0000D 0000D       11 01          SUB_ADDRESS (1)
            0000F 0000F       4D 52 1D       REG_WRITE +$1D2
            00012 00012       04             RETURN
            ' PRI out()
            00013 00013       00             (stack size)
            00014 00014       04             RETURN
            00015 00015       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testRecv() throws Exception {
        String text = """
            PUB main() | a
            
                a := RECV()
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0C 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     a := RECV()
            00009 00009       0C             RECV
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0000B 0000B       04             RETURN
            """, compile(text));
    }

    @Test
    void testRecvAssign() throws Exception {
        String text = """
            PUB main()
            
                RECV := @in
            
            PRI in()
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       13 00 00 80    Method in @ $00013 (0 parameters, 0 returns)
            00008 00008       15 00 00 00    End
            ' PUB main()
            0000C 0000C       00             (stack size)
            '     RECV := @in
            0000D 0000D       11 01          SUB_ADDRESS (1)
            0000F 0000F       4D 51 1D       REG_WRITE +$1D1
            00012 00012       04             RETURN
            ' PRI in()
            00013 00013       00             (stack size)
            00014 00014       04             RETURN
            00015 00015       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testTypeExpression() throws Exception {
        String text = """
            PUB main() | a, b
            
                a := BYTE[@b]
                a := BYTE[@b][1]
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       15 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     a := BYTE[@b]
            00009 00009       D1 1B          VAR_ADDRESS DBASE+$00001 (short)
            0000B 0000B       61 1C          MEM_READ BYTE
            0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := BYTE[@b][1]
            0000E 0000E       D1 1B          VAR_ADDRESS DBASE+$00001 (short)
            00010 00010       A2             CONSTANT (1)
            00011 00011       64 1C          MEM_READ BYTE INDEXED
            00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00014 00014       04             RETURN
            00015 00015       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testTypeAssign() throws Exception {
        String text = """
            PUB main() | a, b
            
                BYTE[@b] := a
                BYTE[@b][1] := a
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       15 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     BYTE[@b] := a
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       D1 1B          VAR_ADDRESS DBASE+$00001 (short)
            0000C 0000C       61 1D          MEM_WRITE BYTE
            '     BYTE[@b][1] := a
            0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)
            0000F 0000F       D1 1B          VAR_ADDRESS DBASE+$00001 (short)
            00011 00011       A2             CONSTANT (1)
            00012 00012       64 1D          MEM_WRITE BYTE INDEXED
            00014 00014       04             RETURN
            00015 00015       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testTernaryExpression() throws Exception {
        String text = """
            PUB main() | a, b
            
                a := (b == 1) ? 2 : 3
                a := (c == 1) ? 2 : 3
                a := 1 ? 2 : 3
            
            DAT
            c       long    0
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       20 00 00 00    End
            00008 00008 00000 00 00 00 00    c                   long    0
            ' PUB main() | a, b
            0000C 0000C       02             (stack size)
            '     a := (b == 1) ? 2 : 3
            0000D 0000D       E1             VAR_READ LONG DBASE+$00001 (short)
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       70             EQUAL
            00010 00010       A3             CONSTANT (2)
            00011 00011       A4             CONSTANT (3)
            00012 00012       6B             TERNARY_IF_ELSE
            00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := (c == 1) ? 2 : 3
            00014 00014       5B 08 1C       MEM_READ LONG PBASE+$00008
            00017 00017       A2             CONSTANT (1)
            00018 00018       70             EQUAL
            00019 00019       A3             CONSTANT (2)
            0001A 0001A       A4             CONSTANT (3)
            0001B 0001B       6B             TERNARY_IF_ELSE
            0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := 1 ? 2 : 3
            0001D 0001D       A3             CONSTANT (1 ? 2 : 3)
            0001E 0001E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001F 0001F       04             RETURN
            """, compile(text));
    }

    @Test
    void testListAssignment() throws Exception {
        String text = """
            PUB start() : r | a, b
            
                r, a, b := b, a, r
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 10 80    Method start @ $00008 (0 parameters, 1 returns)
            00004 00004       10 00 00 00    End
            ' PUB start() : r | a, b
            00008 00008       02             (stack size)
            '     r, a, b := b, a, r
            00009 00009       E2             VAR_READ LONG DBASE+$00002 (short)
            0000A 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)
            0000D 0000D       F1             VAR_WRITE LONG DBASE+$00001 (short)
            0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0000F 0000F       04             RETURN
            """, compile(text));
    }

    @Test
    void testListReturn() throws Exception {
        String text = """
            PUB start() : a, b, c
            
                a, b, c := function()
            
            PUB function() : r1, r2, r3
            
                r1 := 1
                r2 := 2
                r3 := 3
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 30 80    Method start @ $0000C (0 parameters, 3 returns)
            00004 00004       14 00 30 80    Method function @ $00014 (0 parameters, 3 returns)
            00008 00008       1C 00 00 00    End
            ' PUB start() : a, b, c
            0000C 0000C       00             (stack size)
            '     a, b, c := function()
            0000D 0000D       01             ANCHOR (push)
            0000E 0000E       0A 01          CALL_SUB (1)
            00010 00010       F2             VAR_WRITE LONG DBASE+$00002 (short)
            00011 00011       F1             VAR_WRITE LONG DBASE+$00001 (short)
            00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00013 00013       04             RETURN
            ' PUB function() : r1, r2, r3
            00014 00014       00             (stack size)
            '     r1 := 1
            00015 00015       A2             CONSTANT (1)
            00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     r2 := 2
            00017 00017       A3             CONSTANT (2)
            00018 00018       F1             VAR_WRITE LONG DBASE+$00001 (short)
            '     r3 := 3
            00019 00019       A4             CONSTANT (3)
            0001A 0001A       F2             VAR_WRITE LONG DBASE+$00002 (short)
            0001B 0001B       04             RETURN
            """, compile(text));
    }

    @Test
    void testListSkipReturn() throws Exception {
        String text = """
            PUB start() : a, b, c
            
                a, _, c := function()
            
            PUB function() : r1, r2, r3
            
                r1 := 1
                r2 := 2
                r3 := 3
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 30 80    Method start @ $0000C (0 parameters, 3 returns)
            00004 00004       14 00 30 80    Method function @ $00014 (0 parameters, 3 returns)
            00008 00008       1C 00 00 00    End
            ' PUB start() : a, b, c
            0000C 0000C       00             (stack size)
            '     a, _, c := function()
            0000D 0000D       01             ANCHOR (push)
            0000E 0000E       0A 01          CALL_SUB (1)
            00010 00010       F2             VAR_WRITE LONG DBASE+$00002 (short)
            00011 00011       17             POP
            00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00013 00013       04             RETURN
            ' PUB function() : r1, r2, r3
            00014 00014       00             (stack size)
            '     r1 := 1
            00015 00015       A2             CONSTANT (1)
            00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     r2 := 2
            00017 00017       A3             CONSTANT (2)
            00018 00018       F1             VAR_WRITE LONG DBASE+$00001 (short)
            '     r3 := 3
            00019 00019       A4             CONSTANT (3)
            0001A 0001A       F2             VAR_WRITE LONG DBASE+$00002 (short)
            0001B 0001B       04             RETURN
            """, compile(text));
    }

    @Test
    void testListSkipReturnCount() throws Exception {
        String text = """
            PUB start() : a, b
            
                a, _[2] := function()
            
            PUB function() : r1, r2, r3
            
                r1 := 1
                r2 := 2
                r3 := 3
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 20 80    Method start @ $0000C (0 parameters, 2 returns)
            00004 00004       14 00 30 80    Method function @ $00014 (0 parameters, 3 returns)
            00008 00008       1C 00 00 00    End
            ' PUB start() : a, b
            0000C 0000C       00             (stack size)
            '     a, _[2] := function()
            0000D 0000D       01             ANCHOR (push)
            0000E 0000E       0A 01          CALL_SUB (1)
            00010 00010       18 04          POP 8
            00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00013 00013       04             RETURN
            ' PUB function() : r1, r2, r3
            00014 00014       00             (stack size)
            '     r1 := 1
            00015 00015       A2             CONSTANT (1)
            00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     r2 := 2
            00017 00017       A3             CONSTANT (2)
            00018 00018       F1             VAR_WRITE LONG DBASE+$00001 (short)
            '     r3 := 3
            00019 00019       A4             CONSTANT (3)
            0001A 0001A       F2             VAR_WRITE LONG DBASE+$00002 (short)
            0001B 0001B       04             RETURN
            """, compile(text));
    }

    @Test
    void testListSkipStructureReturnCount() throws Exception {
        String text = """
            CON
                S(byte a, word b, long c)
            
            
            PUB start() | a, b
            
                a, _[S] := function()
            
            PUB function() : r1, S r2
            
                r1 := 1
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method start @ $0000C (0 parameters, 0 returns)
            00004 00004       14 00 30 80    Method function @ $00014 (0 parameters, 3 returns)
            00008 00008       18 00 00 00    End
            ' PUB start() | a, b
            0000C 0000C       02             (stack size)
            '     a, _[S] := function()
            0000D 0000D       01             ANCHOR (push)
            0000E 0000E       0A 01          CALL_SUB (1)
            00010 00010       18 04          POP 8
            00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00013 00013       04             RETURN
            ' PUB function() : r1, S r2
            00014 00014       00             (stack size)
            '     r1 := 1
            00015 00015       A2             CONSTANT (1)
            00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00017 00017       04             RETURN
            """, compile(text));
    }

    @Test
    void testReturnListAsArguments() throws Exception {
        String text = """
            PUB start() | a
            
                function1(a, function2())
            
            PUB function1(p1, p2 , p3)
            
            PUB function2() : r1, r2
            
                r1 := 1
                r2 := 2
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       10 00 00 80    Method start @ $00010 (0 parameters, 0 returns)
            00004 00004       19 00 00 83    Method function1 @ $00019 (3 parameters, 0 returns)
            00008 00008       1B 00 20 80    Method function2 @ $0001B (0 parameters, 2 returns)
            0000C 0000C       21 00 00 00    End
            ' PUB start() | a
            00010 00010       01             (stack size)
            '     function1(a, function2())
            00011 00011       00             ANCHOR
            00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)
            00013 00013       01             ANCHOR (push)
            00014 00014       0A 02          CALL_SUB (2)
            00016 00016       0A 01          CALL_SUB (1)
            00018 00018       04             RETURN
            ' PUB function1(p1, p2 , p3)
            00019 00019       00             (stack size)
            0001A 0001A       04             RETURN
            ' PUB function2() : r1, r2
            0001B 0001B       00             (stack size)
            '     r1 := 1
            0001C 0001C       A2             CONSTANT (1)
            0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     r2 := 2
            0001E 0001E       A3             CONSTANT (2)
            0001F 0001F       F1             VAR_WRITE LONG DBASE+$00001 (short)
            00020 00020       04             RETURN
            00021 00021       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testPostEffects() throws Exception {
        String text = """
            PUB start() : r | a, b
            
                a++
                b--
                byte[a++] := 1
                byte[b--] := 2
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 10 80    Method start @ $00008 (0 parameters, 1 returns)
            00004 00004       18 00 00 00    End
            ' PUB start() : r | a, b
            00008 00008       02             (stack size)
            '     a++
            00009 00009       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0000A 0000A       1F             POST_INC
            '     b--
            0000B 0000B       D2             VAR_SETUP LONG DBASE+$00002 (short)
            0000C 0000C       20             POST_DEC
            '     byte[a++] := 1
            0000D 0000D       A2             CONSTANT (1)
            0000E 0000E       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0000F 0000F       23             POST_INC (push)
            00010 00010       61 1D          MEM_WRITE BYTE
            '     byte[b--] := 2
            00012 00012       A3             CONSTANT (2)
            00013 00013       D2             VAR_SETUP LONG DBASE+$00002 (short)
            00014 00014       24             POST_DEC (push)
            00015 00015       61 1D          MEM_WRITE BYTE
            00017 00017       04             RETURN
            """, compile(text));
    }

    @Test
    void testIndexPostEffects() throws Exception {
        String text = """
            PUB start() | a, b
            
                b[3]++
                b[3]~
                a := b[3]++
                a := b[3]~
                a := long[3]++
                a := long[3]--
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       22 00 00 00    End
            ' PUB start() | a, b
            00008 00008       02             (stack size)
            '     b[3]++
            00009 00009       5D 10          VAR_SETUP LONG DBASE+$00004 (short)
            0000B 0000B       1F             POST_INC
            '     b[3]~
            0000C 0000C       A1             CONSTANT (0)
            0000D 0000D       5D 10 1D       VAR_WRITE LONG DBASE+$00004 (short)
            '     a := b[3]++
            00010 00010       5D 10          VAR_SETUP LONG DBASE+$00004 (short)
            00012 00012       23             POST_INC (push)
            00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b[3]~
            00014 00014       A1             CONSTANT (0)
            00015 00015       5D 10          VAR_SETUP LONG DBASE+$00004 (short)
            00017 00017       29             SWAP
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := long[3]++
            00019 00019       A4             CONSTANT (3)
            0001A 0001A       63             MEM_SETUP LONG
            0001B 0001B       23             POST_INC (push)
            0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := long[3]--
            0001D 0001D       A4             CONSTANT (3)
            0001E 0001E       63             MEM_SETUP LONG
            0001F 0001F       24             POST_DEC (push)
            00020 00020       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00021 00021       04             RETURN
            00022 00022       00 00          Padding
            """, compile(text));
    }

    @Test
    void testPreEffects() throws Exception {
        String text = """
            PUB start() | a, b
            
                ++a
                --b
                byte[++a] := 1
                byte[--b] := 2
                b := ++byte[a]
                a := --byte[b]
                ++a[1]
                --b[2]
                ++a.byte[b]
                --b.byte[a]
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       2E 00 00 00    End
            ' PUB start() | a, b
            00008 00008       02             (stack size)
            '     ++a
            00009 00009       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000A 0000A       1F             PRE_INC
            '     --b
            0000B 0000B       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0000C 0000C       20             PRE_DEC
            '     byte[++a] := 1
            0000D 0000D       A2             CONSTANT (1)
            0000E 0000E       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000F 0000F       21             PRE_INC (push)
            00010 00010       61 1D          MEM_WRITE BYTE
            '     byte[--b] := 2
            00012 00012       A3             CONSTANT (2)
            00013 00013       D1             VAR_SETUP LONG DBASE+$00001 (short)
            00014 00014       22             PRE_DEC (push)
            00015 00015       61 1D          MEM_WRITE BYTE
            '     b := ++byte[a]
            00017 00017       E0             VAR_READ LONG DBASE+$00000 (short)
            00018 00018       61             MEM_SETUP BYTE
            00019 00019       21             PRE_INC (push)
            0001A 0001A       F1             VAR_WRITE LONG DBASE+$00001 (short)
            '     a := --byte[b]
            0001B 0001B       E1             VAR_READ LONG DBASE+$00001 (short)
            0001C 0001C       61             MEM_SETUP BYTE
            0001D 0001D       22             PRE_DEC (push)
            0001E 0001E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ++a[1]
            0001F 0001F       5D 04          VAR_SETUP LONG DBASE+$00001 (short)
            00021 00021       1F             PRE_INC
            '     --b[2]
            00022 00022       5D 0C          VAR_SETUP LONG DBASE+$00003 (short)
            00024 00024       20             PRE_DEC
            '     ++a.byte[b]
            00025 00025       E1             VAR_READ LONG DBASE+$00001 (short)
            00026 00026       54 00          MEM_SETUP BYTE INDEXED DBASE+$00000
            00028 00028       1F             PRE_INC
            '     --b.byte[a]
            00029 00029       E0             VAR_READ LONG DBASE+$00000 (short)
            0002A 0002A       54 04          MEM_SETUP BYTE INDEXED DBASE+$00004
            0002C 0002C       20             PRE_DEC
            0002D 0002D       04             RETURN
            0002E 0002E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testDatVariablePreEffects() throws Exception {
        String text = """
            PUB start()
            
                ++a
                --b
            
            DAT
            
            a       long    0
            b       long    0
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       10 00 00 80    Method start @ $00010 (0 parameters, 0 returns)
            00004 00004       18 00 00 00    End
            00008 00008 00000 00 00 00 00    a                   long    0
            0000C 0000C 00004 00 00 00 00    b                   long    0
            ' PUB start()
            00010 00010       00             (stack size)
            '     ++a
            00011 00011       5B 08          MEM_SETUP LONG PBASE+$00008
            00013 00013       1F             PRE_INC
            '     --b
            00014 00014       5B 0C          MEM_SETUP LONG PBASE+$0000C
            00016 00016       20             PRE_DEC
            00017 00017       04             RETURN
            """, compile(text));
    }

    @Test
    void testListAssignmentEffects() throws Exception {
        String text = """
            PUB start() : r | a, b
            
                byte[a++], byte[b--] := byte[b++], byte[a--]
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 10 80    Method start @ $00008 (0 parameters, 1 returns)
            00004 00004       1A 00 00 00    End
            ' PUB start() : r | a, b
            00008 00008       02             (stack size)
            '     byte[a++], byte[b--] := byte[b++], byte[a--]
            00009 00009       D2             VAR_SETUP LONG DBASE+$00002 (short)
            0000A 0000A       23             POST_INC (push)
            0000B 0000B       61 1C          MEM_READ BYTE
            0000D 0000D       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0000E 0000E       24             POST_DEC (push)
            0000F 0000F       61 1C          MEM_READ BYTE
            00011 00011       D2             VAR_SETUP LONG DBASE+$00002 (short)
            00012 00012       24             POST_DEC (push)
            00013 00013       61 1D          MEM_WRITE BYTE
            00015 00015       D1             VAR_SETUP LONG DBASE+$00001 (short)
            00016 00016       23             POST_INC (push)
            00017 00017       61 1D          MEM_WRITE BYTE
            00019 00019       04             RETURN
            0001A 0001A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testPostClear() throws Exception {
        String text = """
            PUB start() | a, b
            
                a~
                if (a~)
                    b := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       13 00 00 00    End
            ' PUB start() | a, b
            00008 00008       02             (stack size)
            '     a~
            00009 00009       A1             CONSTANT (0)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     if (a~)
            0000B 0000B       A1             CONSTANT (0)
            0000C 0000C       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000D 0000D       29             SWAP
            0000E 0000E       13 03          JZ $00012 (3)
            '         b := 1
            00010 00010       A2             CONSTANT (1)
            00011 00011       F1             VAR_WRITE LONG DBASE+$00001 (short)
            00012 00012       04             RETURN
            00013 00013       00             Padding
            """, compile(text));
    }

    @Test
    void testPostSet() throws Exception {
        String text = """
            PUB start() | a, b
            
                a~~
                if (a~~)
                    b := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       13 00 00 00    End
            ' PUB start() | a, b
            00008 00008       02             (stack size)
            '     a~~
            00009 00009       A0             CONSTANT (-1)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     if (a~~)
            0000B 0000B       A0             CONSTANT (-1)
            0000C 0000C       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000D 0000D       29             SWAP
            0000E 0000E       13 03          JZ $00012 (3)
            '         b := 1
            00010 00010       A2             CONSTANT (1)
            00011 00011       F1             VAR_WRITE LONG DBASE+$00001 (short)
            00012 00012       04             RETURN
            00013 00013       00             Padding
            """, compile(text));
    }

    @Test
    void testVariableTypeConstantIndex() throws Exception {
        String text = """
            PUB main() | a, b
            
                a := b.byte[1]
                b.byte[1] := a
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       12 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     a := b.byte[1]
            00009 00009       51 05 1C       MEM_READ BYTE DBASE+$00005
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     b.byte[1] := a
            0000D 0000D       E0             VAR_READ LONG DBASE+$00000 (short)
            0000E 0000E       51 05 1D       MEM_WRITE BYTE DBASE+$00005
            00011 00011       04             RETURN
            00012 00012       00 00          Padding
            """, compile(text));
    }

    @Test
    void testVariableTypeIndex() throws Exception {
        String text = """
            PUB main() | a, b, c
            
                a := b.byte[c]
                b.byte[c] := a
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       14 00 00 00    End
            ' PUB main() | a, b, c
            00008 00008       03             (stack size)
            '     a := b.byte[c]
            00009 00009       E2             VAR_READ LONG DBASE+$00002 (short)
            0000A 0000A       54 04 1C       MEM_READ BYTE INDEXED DBASE+$00004
            0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     b.byte[c] := a
            0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)
            0000F 0000F       E2             VAR_READ LONG DBASE+$00002 (short)
            00010 00010       54 04 1D       MEM_WRITE BYTE INDEXED DBASE+$00004
            00013 00013       04             RETURN
            """, compile(text));
    }

    @Test
    void testLocalVarChainedAssignments() throws Exception {
        String text = """
            PUB main() | a, b, c
            
                a := b := 1
                a := b := c := 1
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       14 00 00 00    End
            ' PUB main() | a, b, c
            00008 00008       03             (stack size)
            '     a := b := 1
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0000B 0000B       1E             WRITE (push)
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b := c := 1
            0000D 0000D       A2             CONSTANT (1)
            0000E 0000E       D2             VAR_SETUP LONG DBASE+$00002 (short)
            0000F 0000F       1E             WRITE (push)
            00010 00010       D1             VAR_SETUP LONG DBASE+$00001 (short)
            00011 00011       1E             WRITE (push)
            00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00013 00013       04             RETURN
            """, compile(text));
    }

    @Test
    void testGlobalVarChainedAssignments() throws Exception {
        String text = """
            VAR
            
                long a
                byte b
                word c
            
            PUB main()
            
                a := b := 1
                a := b := c := 1
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 12)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       19 00 00 00    End
            ' PUB main()
            00008 00008       00             (stack size)
            '     a := b := 1
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       50 08          VAR_SETUP BYTE VBASE+$00008
            0000C 0000C       1E             WRITE (push)
            0000D 0000D       C1 1D          VAR_WRITE LONG VBASE+$00001 (short)
            '     a := b := c := 1
            0000F 0000F       A2             CONSTANT (1)
            00010 00010       56 09          VAR_SETUP WORD VBASE+$00009
            00012 00012       1E             WRITE (push)
            00013 00013       50 08          VAR_SETUP BYTE VBASE+$00008
            00015 00015       1E             WRITE (push)
            00016 00016       C1 1D          VAR_WRITE LONG VBASE+$00001 (short)
            00018 00018       04             RETURN
            00019 00019       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testVariableIndex() throws Exception {
        String text = """
            PUB main() | a, b, c
            
                a := b[c]
                a := b[1]
                b[c] := a
                b[1] := a
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1C 00 00 00    End
            ' PUB main() | a, b, c
            00008 00008       03             (stack size)
            '     a := b[c]
            00009 00009       E2             VAR_READ LONG DBASE+$00002 (short)
            0000A 0000A       60 04 1C       VAR_READ_INDEXED LONG DBASE+$00004
            0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b[1]
            0000E 0000E       5D 08 1C       VAR_READ LONG DBASE+$00002 (short)
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     b[c] := a
            00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)
            00013 00013       E2             VAR_READ LONG DBASE+$00002 (short)
            00014 00014       60 04 1D       VAR_WRITE_INDEXED LONG DBASE+$00004
            '     b[1] := a
            00017 00017       E0             VAR_READ LONG DBASE+$00000 (short)
            00018 00018       5D 08 1D       VAR_WRITE LONG DBASE+$00002 (short)
            0001B 0001B       04             RETURN
            """, compile(text));
    }

    @Test
    void testBitField() throws Exception {
        String text = """
            PUB main() | a, b, c, d
            
                a := b.[1]
                a := b.[2..1]
                a := b.[1..2]
                a := b.[c]
                a := b.[d..c]
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       21 00 00 00    End
            ' PUB main() | a, b, c, d
            00008 00008       04             (stack size)
            '     a := b.[1]
            00009 00009       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0000A 0000A       C1             BITFIELD_READ (short)
            0000B 0000B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b.[2..1]
            0000C 0000C       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0000D 0000D       7E 21          BITFIELD_READ
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b.[1..2]
            00010 00010       D1             VAR_SETUP LONG DBASE+$00001 (short)
            00011 00011       7E E2 07       BITFIELD_READ
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b.[c]
            00015 00015       E2             VAR_READ LONG DBASE+$00002 (short)
            00016 00016       D1             VAR_SETUP LONG DBASE+$00001 (short)
            00017 00017       7B             BITFIELD_READ (pop)
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b.[d..c]
            00019 00019       E3             VAR_READ LONG DBASE+$00003 (short)
            0001A 0001A       E2             VAR_READ LONG DBASE+$00002 (short)
            0001B 0001B       9F 94          BITRANGE
            0001D 0001D       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0001E 0001E       7B             BITFIELD_READ (pop)
            0001F 0001F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00020 00020       04             RETURN
            00021 00021       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testBitFieldChainedAssignment() throws Exception {
        String text = """
            PUB main() | a, b, c
            
                a.[5..0] := b.[5..0] := 3
                a.[5..0] := b.[5..0] := c.[5..0] := 4
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       23 00 00 00    End
            ' PUB main() | a, b, c
            00008 00008       03             (stack size)
            '     a.[5..0] := b.[5..0] := 3
            00009 00009       A4             CONSTANT (3)
            0000A 0000A       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0000B 0000B       7D A0 01       BITFIELD_SETUP
            0000E 0000E       1E             WRITE (push)
            0000F 0000F       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00010 00010       7F A0 01       BITFIELD_WRITE
            '     a.[5..0] := b.[5..0] := c.[5..0] := 4
            00013 00013       A5             CONSTANT (4)
            00014 00014       D2             VAR_SETUP LONG DBASE+$00002 (short)
            00015 00015       7D A0 01       BITFIELD_SETUP
            00018 00018       1E             WRITE (push)
            00019 00019       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0001A 0001A       7D A0 01       BITFIELD_SETUP
            0001D 0001D       1E             WRITE (push)
            0001E 0001E       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0001F 0001F       7F A0 01       BITFIELD_WRITE
            00022 00022       04             RETURN
            00023 00023       00             Padding
            """, compile(text));
    }

    @Test
    void testBitFieldConstants() throws Exception {
        String text = """
            PUB main() | a, b, c, d
            
                a.[0] := 1
                a.[1] := 1
                a.[16] := 1
                a.[17] := 1
                a := a.[0]
                a := a.[1]
                a := a.[16]
                a := a.[17]
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       22 00 00 00    End
            ' PUB main() | a, b, c, d
            00008 00008       04             (stack size)
            '     a.[0] := 1
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000B 0000B       E0             BITFIELD_WRITE (short)
            '     a.[1] := 1
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000E 0000E       E1             BITFIELD_WRITE (short)
            '     a.[16] := 1
            0000F 0000F       A2             CONSTANT (1)
            00010 00010       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00011 00011       F0             BITFIELD_WRITE (short)
            '     a.[17] := 1
            00012 00012       A2             CONSTANT (1)
            00013 00013       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00014 00014       F1             BITFIELD_WRITE (short)
            '     a := a.[0]
            00015 00015       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00016 00016       C0             BITFIELD_READ (short)
            00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := a.[1]
            00018 00018       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00019 00019       C1             BITFIELD_READ (short)
            0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := a.[16]
            0001B 0001B       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0001C 0001C       D0             BITFIELD_READ (short)
            0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := a.[17]
            0001E 0001E       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0001F 0001F       D1             BITFIELD_READ (short)
            00020 00020       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00021 00021       04             RETURN
            00022 00022       00 00          Padding
            """, compile(text));
    }

    @Test
    void testBitFieldMemory() throws Exception {
        String text = """
            PUB main() | a, b, c, d
            
                long[a][b].[c] := d
                word[a][b].[c] := d
                byte[a][b].[c] := d
                d := long[a][b].[c]
                d := word[a][b].[c]
                d := byte[a][b].[c]
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       2E 00 00 00    End
            ' PUB main() | a, b, c, d
            00008 00008       04             (stack size)
            '     long[a][b].[c] := d
            00009 00009       E3             VAR_READ LONG DBASE+$00003 (short)
            0000A 0000A       E2             VAR_READ LONG DBASE+$00002 (short)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       E1             VAR_READ LONG DBASE+$00001 (short)
            0000D 0000D       66             MEM_SETUP LONG INDEXED
            0000E 0000E       7C             BITFIELD_WRITE (pop)
            '     word[a][b].[c] := d
            0000F 0000F       E3             VAR_READ LONG DBASE+$00003 (short)
            00010 00010       E2             VAR_READ LONG DBASE+$00002 (short)
            00011 00011       E0             VAR_READ LONG DBASE+$00000 (short)
            00012 00012       E1             VAR_READ LONG DBASE+$00001 (short)
            00013 00013       65             MEM_SETUP WORD INDEXED
            00014 00014       7C             BITFIELD_WRITE (pop)
            '     byte[a][b].[c] := d
            00015 00015       E3             VAR_READ LONG DBASE+$00003 (short)
            00016 00016       E2             VAR_READ LONG DBASE+$00002 (short)
            00017 00017       E0             VAR_READ LONG DBASE+$00000 (short)
            00018 00018       E1             VAR_READ LONG DBASE+$00001 (short)
            00019 00019       64             MEM_SETUP BYTE INDEXED
            0001A 0001A       7C             BITFIELD_WRITE (pop)
            '     d := long[a][b].[c]
            0001B 0001B       E2             VAR_READ LONG DBASE+$00002 (short)
            0001C 0001C       E0             VAR_READ LONG DBASE+$00000 (short)
            0001D 0001D       E1             VAR_READ LONG DBASE+$00001 (short)
            0001E 0001E       66             MEM_SETUP LONG INDEXED
            0001F 0001F       7B             BITFIELD_READ (pop)
            00020 00020       F3             VAR_WRITE LONG DBASE+$00003 (short)
            '     d := word[a][b].[c]
            00021 00021       E2             VAR_READ LONG DBASE+$00002 (short)
            00022 00022       E0             VAR_READ LONG DBASE+$00000 (short)
            00023 00023       E1             VAR_READ LONG DBASE+$00001 (short)
            00024 00024       65             MEM_SETUP WORD INDEXED
            00025 00025       7B             BITFIELD_READ (pop)
            00026 00026       F3             VAR_WRITE LONG DBASE+$00003 (short)
            '     d := byte[a][b].[c]
            00027 00027       E2             VAR_READ LONG DBASE+$00002 (short)
            00028 00028       E0             VAR_READ LONG DBASE+$00000 (short)
            00029 00029       E1             VAR_READ LONG DBASE+$00001 (short)
            0002A 0002A       64             MEM_SETUP BYTE INDEXED
            0002B 0002B       7B             BITFIELD_READ (pop)
            0002C 0002C       F3             VAR_WRITE LONG DBASE+$00003 (short)
            0002D 0002D       04             RETURN
            0002E 0002E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testBitFieldPostEffects() throws Exception {
        String text = """
            PUB main() | a, b, c, d
                a := b[3].[1]++
                a := b[3].[2..1]--
                a := b[a].[c]~
                a := b[a].[d..c]~~
                b[3].[1]++
                b[3].[2..1]--
                b[a].[c]~
                b[a].[d..c]~~
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       40 00 00 00    End
            ' PUB main() | a, b, c, d
            00008 00008       04             (stack size)
            '     a := b[3].[1]++
            00009 00009       5D 10          VAR_SETUP LONG DBASE+$00004 (short)
            0000B 0000B       A1             BITFIELD_SETUP (short)
            0000C 0000C       23             POST_INC (push)
            0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b[3].[2..1]--
            0000E 0000E       5D 10          VAR_SETUP LONG DBASE+$00004 (short)
            00010 00010       7D 21          BITFIELD_SETUP
            00012 00012       24             POST_DEC (push)
            00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b[a].[c]~
            00014 00014       A1             CONSTANT (0)
            00015 00015       E2             VAR_READ LONG DBASE+$00002 (short)
            00016 00016       E0             VAR_READ LONG DBASE+$00000 (short)
            00017 00017       60 04          VAR_SETUP_INDEXED LONG DBASE+$00004
            00019 00019       7A             BITFIELD_SETUP (pop)
            0001A 0001A       29             SWAP
            0001B 0001B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b[a].[d..c]~~
            0001C 0001C       A0             CONSTANT (-1)
            0001D 0001D       E3             VAR_READ LONG DBASE+$00003 (short)
            0001E 0001E       E2             VAR_READ LONG DBASE+$00002 (short)
            0001F 0001F       9F 94          BITRANGE
            00021 00021       E0             VAR_READ LONG DBASE+$00000 (short)
            00022 00022       60 04          VAR_SETUP_INDEXED LONG DBASE+$00004
            00024 00024       7A             BITFIELD_SETUP (pop)
            00025 00025       29             SWAP
            00026 00026       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     b[3].[1]++
            00027 00027       5D 10          VAR_SETUP LONG DBASE+$00004 (short)
            00029 00029       A1             BITFIELD_SETUP (short)
            0002A 0002A       1F             POST_INC
            '     b[3].[2..1]--
            0002B 0002B       5D 10          VAR_SETUP LONG DBASE+$00004 (short)
            0002D 0002D       7D 21          BITFIELD_SETUP
            0002F 0002F       20             POST_DEC
            '     b[a].[c]~
            00030 00030       A1             CONSTANT (0)
            00031 00031       E2             VAR_READ LONG DBASE+$00002 (short)
            00032 00032       E0             VAR_READ LONG DBASE+$00000 (short)
            00033 00033       60 04          VAR_SETUP_INDEXED LONG DBASE+$00004
            00035 00035       7C             BITFIELD_WRITE (pop)
            '     b[a].[d..c]~~
            00036 00036       A0             CONSTANT (-1)
            00037 00037       E3             VAR_READ LONG DBASE+$00003 (short)
            00038 00038       E2             VAR_READ LONG DBASE+$00002 (short)
            00039 00039       9F 94          BITRANGE
            0003B 0003B       E0             VAR_READ LONG DBASE+$00000 (short)
            0003C 0003C       60 04          VAR_SETUP_INDEXED LONG DBASE+$00004
            0003E 0003E       7C             BITFIELD_WRITE (pop)
            0003F 0003F       04             RETURN
            """, compile(text));
    }

    @Test
    void testTypedBitField() throws Exception {
        String text = """
            PUB main() | a, b, c, d, e
            
                a := b.byte[1]
                a := b.byte.[2..1]
                a := b.byte[3].[2..1]
                a := b.word[c]
                a := b.word.[d..c]
                a := b.word[e].[d..c]
            
                b.byte[1] := a
                b.byte.[2..1] := a
                b.byte[3].[2..1] := a
                b.word[c] := a
                b.word.[d..c] := a
                b.word[e].[d..c] := a
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       52 00 00 00    End
            ' PUB main() | a, b, c, d, e
            00008 00008       05             (stack size)
            '     a := b.byte[1]
            00009 00009       51 05 1C       MEM_READ BYTE DBASE+$00005
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b.byte.[2..1]
            0000D 0000D       51 04          MEM_SETUP BYTE DBASE+$00004
            0000F 0000F       7E 21          BITFIELD_READ
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b.byte[3].[2..1]
            00012 00012       51 07          MEM_SETUP BYTE DBASE+$00007
            00014 00014       7E 21          BITFIELD_READ
            00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b.word[c]
            00017 00017       E2             VAR_READ LONG DBASE+$00002 (short)
            00018 00018       5A 04 1C       MEM_READ WORD INDEXED DBASE+$00004
            0001B 0001B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b.word.[d..c]
            0001C 0001C       E3             VAR_READ LONG DBASE+$00003 (short)
            0001D 0001D       E2             VAR_READ LONG DBASE+$00002 (short)
            0001E 0001E       9F 94          BITRANGE
            00020 00020       57 04          MEM_SETUP WORD DBASE+$00004
            00022 00022       7B             BITFIELD_READ (pop)
            00023 00023       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b.word[e].[d..c]
            00024 00024       E3             VAR_READ LONG DBASE+$00003 (short)
            00025 00025       E2             VAR_READ LONG DBASE+$00002 (short)
            00026 00026       9F 94          BITRANGE
            00028 00028       E4             VAR_READ LONG DBASE+$00004 (short)
            00029 00029       5A 04          MEM_SETUP WORD INDEXED DBASE+$00004
            0002B 0002B       7B             BITFIELD_READ (pop)
            0002C 0002C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     b.byte[1] := a
            0002D 0002D       E0             VAR_READ LONG DBASE+$00000 (short)
            0002E 0002E       51 05 1D       MEM_WRITE BYTE DBASE+$00005
            '     b.byte.[2..1] := a
            00031 00031       E0             VAR_READ LONG DBASE+$00000 (short)
            00032 00032       51 04          MEM_SETUP BYTE DBASE+$00004
            00034 00034       7F 21          BITFIELD_WRITE
            '     b.byte[3].[2..1] := a
            00036 00036       E0             VAR_READ LONG DBASE+$00000 (short)
            00037 00037       51 07          MEM_SETUP BYTE DBASE+$00007
            00039 00039       7F 21          BITFIELD_WRITE
            '     b.word[c] := a
            0003B 0003B       E0             VAR_READ LONG DBASE+$00000 (short)
            0003C 0003C       E2             VAR_READ LONG DBASE+$00002 (short)
            0003D 0003D       5A 04 1D       MEM_WRITE WORD INDEXED DBASE+$00004
            '     b.word.[d..c] := a
            00040 00040       E0             VAR_READ LONG DBASE+$00000 (short)
            00041 00041       E3             VAR_READ LONG DBASE+$00003 (short)
            00042 00042       E2             VAR_READ LONG DBASE+$00002 (short)
            00043 00043       9F 94          BITRANGE
            00045 00045       57 04          MEM_SETUP WORD DBASE+$00004
            00047 00047       7C             BITFIELD_WRITE (pop)
            '     b.word[e].[d..c] := a
            00048 00048       E0             VAR_READ LONG DBASE+$00000 (short)
            00049 00049       E3             VAR_READ LONG DBASE+$00003 (short)
            0004A 0004A       E2             VAR_READ LONG DBASE+$00002 (short)
            0004B 0004B       9F 94          BITRANGE
            0004D 0004D       E4             VAR_READ LONG DBASE+$00004 (short)
            0004E 0004E       5A 04          MEM_SETUP WORD INDEXED DBASE+$00004
            00050 00050       7C             BITFIELD_WRITE (pop)
            00051 00051       04             RETURN
            00052 00052       00 00          Padding
            """, compile(text));
    }

    @Test
    void testIndexedBitField() throws Exception {
        String text = """
            PUB main() | a, b, c, d
            
                a := b[3].[1]
                a := b[3].[2..1]
                a := b[a].[c]
                a := b[a].[d..c]
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       22 00 00 00    End
            ' PUB main() | a, b, c, d
            00008 00008       04             (stack size)
            '     a := b[3].[1]
            00009 00009       5D 10          VAR_SETUP LONG DBASE+$00004 (short)
            0000B 0000B       C1             BITFIELD_READ (short)
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b[3].[2..1]
            0000D 0000D       5D 10          VAR_SETUP LONG DBASE+$00004 (short)
            0000F 0000F       7E 21          BITFIELD_READ
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b[a].[c]
            00012 00012       E2             VAR_READ LONG DBASE+$00002 (short)
            00013 00013       E0             VAR_READ LONG DBASE+$00000 (short)
            00014 00014       60 04          VAR_SETUP_INDEXED LONG DBASE+$00004
            00016 00016       7B             BITFIELD_READ (pop)
            00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b[a].[d..c]
            00018 00018       E3             VAR_READ LONG DBASE+$00003 (short)
            00019 00019       E2             VAR_READ LONG DBASE+$00002 (short)
            0001A 0001A       9F 94          BITRANGE
            0001C 0001C       E0             VAR_READ LONG DBASE+$00000 (short)
            0001D 0001D       60 04          VAR_SETUP_INDEXED LONG DBASE+$00004
            0001F 0001F       7B             BITFIELD_READ (pop)
            00020 00020       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00021 00021       04             RETURN
            00022 00022       00 00          Padding
            """, compile(text));
    }

    @Test
    void testMemoryBitFieldWrite() throws Exception {
        String text = """
            PUB main() | a
            
                byte[0].[3] := a
                word[1].[3] := a
                long[2].[3] := a
                byte[0][4].[3] := a
                word[1][4].[3] := a
                long[2][4].[3] := a
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       25 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     byte[0].[3] := a
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A1             CONSTANT (0)
            0000B 0000B       61             MEM_SETUP BYTE
            0000C 0000C       E3             BITFIELD_WRITE (short)
            '     word[1].[3] := a
            0000D 0000D       E0             VAR_READ LONG DBASE+$00000 (short)
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       62             MEM_SETUP WORD
            00010 00010       E3             BITFIELD_WRITE (short)
            '     long[2].[3] := a
            00011 00011       E0             VAR_READ LONG DBASE+$00000 (short)
            00012 00012       A3             CONSTANT (2)
            00013 00013       63             MEM_SETUP LONG
            00014 00014       E3             BITFIELD_WRITE (short)
            '     byte[0][4].[3] := a
            00015 00015       E0             VAR_READ LONG DBASE+$00000 (short)
            00016 00016       A1             CONSTANT (0)
            00017 00017       A5             CONSTANT (4)
            00018 00018       64             MEM_SETUP BYTE INDEXED
            00019 00019       E3             BITFIELD_WRITE (short)
            '     word[1][4].[3] := a
            0001A 0001A       E0             VAR_READ LONG DBASE+$00000 (short)
            0001B 0001B       A2             CONSTANT (1)
            0001C 0001C       A5             CONSTANT (4)
            0001D 0001D       65             MEM_SETUP WORD INDEXED
            0001E 0001E       E3             BITFIELD_WRITE (short)
            '     long[2][4].[3] := a
            0001F 0001F       E0             VAR_READ LONG DBASE+$00000 (short)
            00020 00020       A3             CONSTANT (2)
            00021 00021       A5             CONSTANT (4)
            00022 00022       66             MEM_SETUP LONG INDEXED
            00023 00023       E3             BITFIELD_WRITE (short)
            00024 00024       04             RETURN
            00025 00025       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testMemoryBitFieldRead() throws Exception {
        String text = """
            PUB main() | a
            
                a := byte[0].[3]
                a := word[1].[3]
                a := long[2].[3]
                a := byte[0][4].[3]
                a := word[1][4].[3]
                a := long[2][4].[3]
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       25 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     a := byte[0].[3]
            00009 00009       A1             CONSTANT (0)
            0000A 0000A       61             MEM_SETUP BYTE
            0000B 0000B       C3             BITFIELD_READ (short)
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := word[1].[3]
            0000D 0000D       A2             CONSTANT (1)
            0000E 0000E       62             MEM_SETUP WORD
            0000F 0000F       C3             BITFIELD_READ (short)
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := long[2].[3]
            00011 00011       A3             CONSTANT (2)
            00012 00012       63             MEM_SETUP LONG
            00013 00013       C3             BITFIELD_READ (short)
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := byte[0][4].[3]
            00015 00015       A1             CONSTANT (0)
            00016 00016       A5             CONSTANT (4)
            00017 00017       64             MEM_SETUP BYTE INDEXED
            00018 00018       C3             BITFIELD_READ (short)
            00019 00019       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := word[1][4].[3]
            0001A 0001A       A2             CONSTANT (1)
            0001B 0001B       A5             CONSTANT (4)
            0001C 0001C       65             MEM_SETUP WORD INDEXED
            0001D 0001D       C3             BITFIELD_READ (short)
            0001E 0001E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := long[2][4].[3]
            0001F 0001F       A3             CONSTANT (2)
            00020 00020       A5             CONSTANT (4)
            00021 00021       66             MEM_SETUP LONG INDEXED
            00022 00022       C3             BITFIELD_READ (short)
            00023 00023       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00024 00024       04             RETURN
            00025 00025       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testMemoryPostEffects() throws Exception {
        String text = """
            PUB main() | a
            
                a := byte[3]++
                a := word[3]--
                a := long[a]~
                a := long[a]~~
                byte[3]++
                word[3]--
                long[a]~
                long[a]~~
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       2A 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     a := byte[3]++
            00009 00009       A4             CONSTANT (3)
            0000A 0000A       61             MEM_SETUP BYTE
            0000B 0000B       23             POST_INC (push)
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := word[3]--
            0000D 0000D       A4             CONSTANT (3)
            0000E 0000E       62             MEM_SETUP WORD
            0000F 0000F       24             POST_DEC (push)
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := long[a]~
            00011 00011       A1             CONSTANT (0)
            00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)
            00013 00013       63             MEM_SETUP LONG
            00014 00014       29             SWAP
            00015 00015       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := long[a]~~
            00016 00016       A0             CONSTANT (-1)
            00017 00017       E0             VAR_READ LONG DBASE+$00000 (short)
            00018 00018       63             MEM_SETUP LONG
            00019 00019       29             SWAP
            0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     byte[3]++
            0001B 0001B       A4             CONSTANT (3)
            0001C 0001C       61             MEM_SETUP BYTE
            0001D 0001D       1F             POST_INC
            '     word[3]--
            0001E 0001E       A4             CONSTANT (3)
            0001F 0001F       62             MEM_SETUP WORD
            00020 00020       20             POST_DEC
            '     long[a]~
            00021 00021       A1             CONSTANT (0)
            00022 00022       E0             VAR_READ LONG DBASE+$00000 (short)
            00023 00023       63             MEM_SETUP LONG
            00024 00024       1D             WRITE
            '     long[a]~~
            00025 00025       A0             CONSTANT (-1)
            00026 00026       E0             VAR_READ LONG DBASE+$00000 (short)
            00027 00027       63             MEM_SETUP LONG
            00028 00028       1D             WRITE
            00029 00029       04             RETURN
            0002A 0002A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testMemoryBitFieldPostEffects() throws Exception {
        String text = """
            PUB main() | a, b, c, d
            
                a := byte[3].[1]++
                a := word[3].[2..1]--
                a := long[a].[c]~
                a := long[a].[d..c]~~
                byte[3].[1]++
                word[3].[2..1]--
                long[a].[c]~
                long[a].[d..c]~~
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       3C 00 00 00    End
            ' PUB main() | a, b, c, d
            00008 00008       04             (stack size)
            '     a := byte[3].[1]++
            00009 00009       A4             CONSTANT (3)
            0000A 0000A       61             MEM_SETUP BYTE
            0000B 0000B       A1             BITFIELD_SETUP (short)
            0000C 0000C       23             POST_INC (push)
            0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := word[3].[2..1]--
            0000E 0000E       A4             CONSTANT (3)
            0000F 0000F       62             MEM_SETUP WORD
            00010 00010       7D 21          BITFIELD_SETUP
            00012 00012       24             POST_DEC (push)
            00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := long[a].[c]~
            00014 00014       A1             CONSTANT (0)
            00015 00015       E2             VAR_READ LONG DBASE+$00002 (short)
            00016 00016       E0             VAR_READ LONG DBASE+$00000 (short)
            00017 00017       63             MEM_SETUP LONG
            00018 00018       7A             BITFIELD_SETUP (pop)
            00019 00019       29             SWAP
            0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := long[a].[d..c]~~
            0001B 0001B       A0             CONSTANT (-1)
            0001C 0001C       E3             VAR_READ LONG DBASE+$00003 (short)
            0001D 0001D       E2             VAR_READ LONG DBASE+$00002 (short)
            0001E 0001E       9F 94          BITRANGE
            00020 00020       E0             VAR_READ LONG DBASE+$00000 (short)
            00021 00021       63             MEM_SETUP LONG
            00022 00022       7A             BITFIELD_SETUP (pop)
            00023 00023       29             SWAP
            00024 00024       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     byte[3].[1]++
            00025 00025       A4             CONSTANT (3)
            00026 00026       61             MEM_SETUP BYTE
            00027 00027       A1             BITFIELD_SETUP (short)
            00028 00028       1F             POST_INC
            '     word[3].[2..1]--
            00029 00029       A4             CONSTANT (3)
            0002A 0002A       62             MEM_SETUP WORD
            0002B 0002B       7D 21          BITFIELD_SETUP
            0002D 0002D       20             POST_DEC
            '     long[a].[c]~
            0002E 0002E       A1             CONSTANT (0)
            0002F 0002F       E2             VAR_READ LONG DBASE+$00002 (short)
            00030 00030       E0             VAR_READ LONG DBASE+$00000 (short)
            00031 00031       63             MEM_SETUP LONG
            00032 00032       7C             BITFIELD_WRITE (pop)
            '     long[a].[d..c]~~
            00033 00033       A0             CONSTANT (-1)
            00034 00034       E3             VAR_READ LONG DBASE+$00003 (short)
            00035 00035       E2             VAR_READ LONG DBASE+$00002 (short)
            00036 00036       9F 94          BITRANGE
            00038 00038       E0             VAR_READ LONG DBASE+$00000 (short)
            00039 00039       63             MEM_SETUP LONG
            0003A 0003A       7C             BITFIELD_WRITE (pop)
            0003B 0003B       04             RETURN
            """, compile(text));
    }

    @Test
    void testMemoryBitfields() throws Exception {
        String text = """
            PUB main() | a, b, c
            
                word[@a][1].[2] ^= 3
                word[@a][b].[c] ^= 3
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       19 00 00 00    End
            ' PUB main() | a, b, c
            00008 00008       03             (stack size)
            '     word[@a][1].[2] ^= 3
            00009 00009       A4             CONSTANT (3)
            0000A 0000A       D0 1B          VAR_ADDRESS DBASE+$00000 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       65             MEM_SETUP WORD INDEXED
            0000E 0000E       A2             BITFIELD_SETUP (short)
            0000F 0000F       45             BITXOR_ASSIGN
            '     word[@a][b].[c] ^= 3
            00010 00010       A4             CONSTANT (3)
            00011 00011       E2             VAR_READ LONG DBASE+$00002 (short)
            00012 00012       D0 1B          VAR_ADDRESS DBASE+$00000 (short)
            00014 00014       E1             VAR_READ LONG DBASE+$00001 (short)
            00015 00015       65             MEM_SETUP WORD INDEXED
            00016 00016       7A             BITFIELD_SETUP (pop)
            00017 00017       45             BITXOR_ASSIGN
            00018 00018       04             RETURN
            00019 00019       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testInlineAssembly() throws Exception {
        String text = """
            PUB main(a)
            
                    org
                    mov     pr0, #0
            l1      add     pr0, a
                    djnz    a, #l1
                    end
            
                    orgh
                    mov     pr0, #0
            l1      add     pr0, a
                    djnz    a, #l1
                    end
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 81    Method main @ $00008 (1 parameters, 0 returns)
            00004 00004       34 00 00 00    End
            ' PUB main(a)
            00008 00008       00             (stack size)
            '         org
            00009 00009       19 5C 00 00 03 INLINE-EXEC ORG=$000, 4
            0000E 0000E       00
            0000F 0000F   000                                    org
            0000F 0000F   000 00 B0 07 F6                        mov     pr0, #0
            00013 00013   001 E0 B1 03 F1    l1                  add     pr0, a
            00017 00017   002 FE C1 6F FB                        djnz    a, #l1
            0001B 0001B   003 2D 00 64 FD                        ret
            '         orgh
            0001F 0001F       19 5E 04 00    INLINE-EXEC ORGH 4
            00023 00023   000                                    orgh
            00023 00023   000 00 B0 07 F6                        mov     pr0, #0
            00027 00027   004 E0 B1 03 F1    l1                  add     pr0, a
            0002B 0002B   008 FB C1 6F FB                        djnz    a, #l1
            0002F 0002F   00C 2D 00 64 FD                        ret
            00033 00033       04             RETURN
            """, compile(text));
    }

    @Test
    void testInlineAssemblyLocalLabel() throws Exception {
        String text = """
            PUB main(a)
            
                    org
                    mov     pr0, #0
            .l1    add     pr0, a
                    djnz    a, #.l1
                    end
            
                repeat a from 0 to 7
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 81    Method main @ $00008 (1 parameters, 0 returns)
            00004 00004       28 00 00 00    End
            ' PUB main(a)
            00008 00008       00             (stack size)
            '         org
            00009 00009       19 5C 00 00 03 INLINE-EXEC ORG=$000, 4
            0000E 0000E       00
            0000F 0000F   000                                    org
            0000F 0000F   000 00 B0 07 F6                        mov     pr0, #0
            00013 00013   001 E0 B1 03 F1    .l1                 add     pr0, a
            00017 00017   002 FE C1 6F FB                        djnz    a, #.l1
            0001B 0001B   003 2D 00 64 FD                        ret
            '     repeat a from 0 to 7
            0001F 0001F       42 25          ADDRESS ($00025)
            00021 00021       A8             CONSTANT (7)
            00022 00022       A1             CONSTANT (0)
            00023 00023       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00024 00024       17             REPEAT
            00025 00025       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00026 00026       19             REPEAT_LOOP
            00027 00027       04             RETURN
            """, compile(text));
    }

    @Test
    void testInlineAssemblyOrg() throws Exception {
        String text = """
            PUB main(a)
            
                    org $100
                    mov     pr0, #0
            l1      add     pr0, a
                    djnz    a, #l1
                    end
            
                repeat a from 0 to 7
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 81    Method main @ $00008 (1 parameters, 0 returns)
            00004 00004       28 00 00 00    End
            ' PUB main(a)
            00008 00008       00             (stack size)
            '         org $100
            00009 00009       19 5C 00 01 03 INLINE-EXEC ORG=$100, 4
            0000E 0000E       00
            0000F 0000F   000                                    org     $100
            0000F 0000F   100 00 B0 07 F6                        mov     pr0, #0
            00013 00013   101 E0 B1 03 F1    l1                  add     pr0, a
            00017 00017   102 FE C1 6F FB                        djnz    a, #l1
            0001B 0001B   103 2D 00 64 FD                        ret
            '     repeat a from 0 to 7
            0001F 0001F       42 25          ADDRESS ($00025)
            00021 00021       A8             CONSTANT (7)
            00022 00022       A1             CONSTANT (0)
            00023 00023       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00024 00024       17             REPEAT
            00025 00025       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00026 00026       19             REPEAT_LOOP
            00027 00027       04             RETURN
            """, compile(text));
    }

    @Test
    void testInlineAssemblyDebug() throws Exception {
        String text = """
            PUB main(a)
            
                    org
                    mov     pr0, #0
                    debug(uhex_long(a, pr0)
                    end
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 81    Method main @ $00008 (1 parameters, 0 returns)
            00004 00004       1C 00 00 00    End
            ' PUB main(a)
            00008 00008       00             (stack size)
            '         org
            00009 00009       19 5C 00 00 02 INLINE-EXEC ORG=$000, 3
            0000E 0000E       00
            0000F 0000F   000                                    org
            0000F 0000F   000 00 B0 07 F6                        mov     pr0, #0
            00013 00013   001 36 02 64 FD                        debug(uhex_long(a, pr0))
            00017 00017   002 2D 00 64 FD                        ret
            0001B 0001B       04             RETURN
            ' Debug data
            00B74 00000       13 00        \s
            00B76 00002       04 00          #1@0004
            ' #1
            00B78 00004       01             ASMMODE
            00B79 00005       04             COGN
            00B7A 00006       8D 61 00 81 E0 UHEX_LONG(a)
            00B7F 0000B       8C 70 72 30 00 UHEX_LONG(pr0)
            00B84 00010       81 D8
            00B86 00012       00             DONE
            """, compile(text, true));
    }

    @Test
    void testSkipInlineAssemblyDebug() throws Exception {
        String text = """
            PUB main(a)
            
                    org
                    mov     pr0, #0
                    debug(uhex_long(a, pr0)
                    end
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 81    Method main @ $00008 (1 parameters, 0 returns)
            00004 00004       18 00 00 00    End
            ' PUB main(a)
            00008 00008       00             (stack size)
            '         org
            00009 00009       19 5C 00 00 01 INLINE-EXEC ORG=$000, 2
            0000E 0000E       00
            0000F 0000F   000                                    org
            0000F 0000F   000 00 B0 07 F6                        mov     pr0, #0
            00013 00013   001                                    debug(uhex_long(a, pr0))
            00013 00013   001 2D 00 64 FD                        ret
            00017 00017       04             RETURN
            """, compile(text, false));
    }

    @Test
    void testAssemblyCall() throws Exception {
        String text = """
            PUB main()
                call(#pasm0)
                call(#pasm1)
            
            DAT
                    org
            pasm0 _ret_ drvnot  pr0
            pasm1 _ret_ drvl    pr0
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            00004 00004       18 00 00 00    End
            00008 00008   000                                    org
            00008 00008   000 5F B0 63 0D    pasm0   _ret_       drvnot  pr0
            0000C 0000C   001 58 B0 63 0D    pasm1   _ret_       drvl    pr0
            ' PUB main()
            00010 00010       00             (stack size)
            '     call(#pasm0)
            00011 00011       A1             CONSTANT ($00000)
            00012 00012       19 64          CALL
            '     call(#pasm1)
            00014 00014       A2             CONSTANT ($00001)
            00015 00015       19 64          CALL
            00017 00017       04             RETURN
            """, compile(text));
    }

    @Test
    void testAssemblyCallExpression() throws Exception {
        String text = """
            PUB main(a)
                call(#pasm + a)
            
            DAT
                    org
            pasm  _ret_ drvnot  pr0
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 81    Method main @ $0000C (1 parameters, 0 returns)
            00004 00004       13 00 00 00    End
            00008 00008   000                                    org
            00008 00008   000 5F B0 63 0D    pasm    _ret_       drvnot  pr0
            ' PUB main(a)
            0000C 0000C       00             (stack size)
            '     call(#pasm + a)
            0000D 0000D       A1             CONSTANT ($00000)
            0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)
            0000F 0000F       8A             ADD
            00010 00010       19 64          CALL
            00012 00012       04             RETURN
            00013 00013       00             Padding
            """, compile(text));
    }

    @Test
    void testRegisters() throws Exception {
        String text = """
            PUB main() | a
            
                DIRA := 1
                DIRA[1] := 2
                a := INA
                a := INA[1]
                REG[DIRA] := 1
                REG[DIRA][1] := 2
                a := REG[INA]
                a := REG[INA][1]
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       26 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     DIRA := 1
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       BA 1D          REG_WRITE +$1FA (short)
            '     DIRA[1] := 2
            0000C 0000C       A3             CONSTANT (2)
            0000D 0000D       4D 7B 1D       REG_WRITE +$1FB
            '     a := INA
            00010 00010       BE 1C          REG_READ +$1FE (short)
            00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := INA[1]
            00013 00013       4D 7F 1C       REG_READ +$1FF
            00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     REG[DIRA] := 1
            00017 00017       A2             CONSTANT (1)
            00018 00018       BA 1D          REG_WRITE +$1FA (short)
            '     REG[DIRA][1] := 2
            0001A 0001A       A3             CONSTANT (2)
            0001B 0001B       4D 7B 1D       REG_WRITE +$1FB
            '     a := REG[INA]
            0001E 0001E       BE 1C          REG_READ +$1FE (short)
            00020 00020       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := REG[INA][1]
            00021 00021       4D 7F 1C       REG_READ +$1FF
            00024 00024       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00025 00025       04             RETURN
            00026 00026       00 00          Padding
            """, compile(text));
    }

    @Test
    void testReg() throws Exception {
        String text = """
            PUB main() | a, b
            
                a := REG[1]
                a := REG[1][2]
                a := REG[2][b]
                a := REG[1].[5..0]
                a := REG[2][b].[5..0]
            
                REG[1] := a
                REG[1][2] := a
                REG[2][b] := a
                REG[1].[5..0] := a
                REG[2][b].[5..0] := a
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       3E 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     a := REG[1]
            00009 00009       4D 01 1C       REG_READ +$001
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := REG[1][2]
            0000D 0000D       4D 03 1C       REG_READ +$003
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := REG[2][b]
            00011 00011       E1             VAR_READ LONG DBASE+$00001 (short)
            00012 00012       4E 02 1C       REG_READ_INDEXED +$002
            00015 00015       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := REG[1].[5..0]
            00016 00016       4D 01          REG_SETUP +$001
            00018 00018       7E A0 01       BITFIELD_READ
            0001B 0001B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := REG[2][b].[5..0]
            0001C 0001C       E1             VAR_READ LONG DBASE+$00001 (short)
            0001D 0001D       4E 02          REG_SETUP_INDEXED +$002
            0001F 0001F       7E A0 01       BITFIELD_READ
            00022 00022       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     REG[1] := a
            00023 00023       E0             VAR_READ LONG DBASE+$00000 (short)
            00024 00024       4D 01 1D       REG_WRITE +$001
            '     REG[1][2] := a
            00027 00027       E0             VAR_READ LONG DBASE+$00000 (short)
            00028 00028       4D 03 1D       REG_WRITE +$003
            '     REG[2][b] := a
            0002B 0002B       E0             VAR_READ LONG DBASE+$00000 (short)
            0002C 0002C       E1             VAR_READ LONG DBASE+$00001 (short)
            0002D 0002D       4E 02 1D       REG_WRITE_INDEXED +$002
            '     REG[1].[5..0] := a
            00030 00030       E0             VAR_READ LONG DBASE+$00000 (short)
            00031 00031       4D 01          REG_SETUP +$001
            00033 00033       7F A0 01       BITFIELD_WRITE
            '     REG[2][b].[5..0] := a
            00036 00036       E0             VAR_READ LONG DBASE+$00000 (short)
            00037 00037       E1             VAR_READ LONG DBASE+$00001 (short)
            00038 00038       4E 02          REG_SETUP_INDEXED +$002
            0003A 0003A       7F A0 01       BITFIELD_WRITE
            0003D 0003D       04             RETURN
            0003E 0003E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testDatReg() throws Exception {
        String text = """
            PUB main() | a, b
            
                a := REG[reg100]
                a := REG[reg100][2]
                a := REG[reg100+1][b]
                a := REG[reg100].[5..0]
                a := REG[reg100+1][b].[5..0]
            
                REG[reg100] := a
                REG[reg100][2] := a
                REG[reg100+1][b] := a
                REG[reg100].[5..0] := a
                REG[reg100+1][b].[5..0] := a
            
            DAT      org $100
            
            reg100   long    1
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       4C 00 00 00    End
            00008 00008   000                                    org     $100
            00008 00008   100 01 00 00 00    reg100              long    1
            ' PUB main() | a, b
            0000C 0000C       02             (stack size)
            '     a := REG[reg100]
            0000D 0000D       4D 80 7E 1C    REG_READ +$100
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := REG[reg100][2]
            00012 00012       4D 82 7E 1C    REG_READ +$102
            00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := REG[reg100+1][b]
            00017 00017       E1             VAR_READ LONG DBASE+$00001 (short)
            00018 00018       4E 81 7E 1C    REG_READ_INDEXED +$101
            0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := REG[reg100].[5..0]
            0001D 0001D       4D 80 7E       REG_SETUP +$100
            00020 00020       7E A0 01       BITFIELD_READ
            00023 00023       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := REG[reg100+1][b].[5..0]
            00024 00024       E1             VAR_READ LONG DBASE+$00001 (short)
            00025 00025       4E 81 7E       REG_SETUP_INDEXED +$101
            00028 00028       7E A0 01       BITFIELD_READ
            0002B 0002B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     REG[reg100] := a
            0002C 0002C       E0             VAR_READ LONG DBASE+$00000 (short)
            0002D 0002D       4D 80 7E 1D    REG_WRITE +$100
            '     REG[reg100][2] := a
            00031 00031       E0             VAR_READ LONG DBASE+$00000 (short)
            00032 00032       4D 82 7E 1D    REG_WRITE +$102
            '     REG[reg100+1][b] := a
            00036 00036       E0             VAR_READ LONG DBASE+$00000 (short)
            00037 00037       E1             VAR_READ LONG DBASE+$00001 (short)
            00038 00038       4E 81 7E 1D    REG_WRITE_INDEXED +$101
            '     REG[reg100].[5..0] := a
            0003C 0003C       E0             VAR_READ LONG DBASE+$00000 (short)
            0003D 0003D       4D 80 7E       REG_SETUP +$100
            00040 00040       7F A0 01       BITFIELD_WRITE
            '     REG[reg100+1][b].[5..0] := a
            00043 00043       E0             VAR_READ LONG DBASE+$00000 (short)
            00044 00044       E1             VAR_READ LONG DBASE+$00001 (short)
            00045 00045       4E 81 7E       REG_SETUP_INDEXED +$101
            00048 00048       7F A0 01       BITFIELD_WRITE
            0004B 0004B       04             RETURN
            """, compile(text));
    }

    @Test
    void testString() throws Exception {
        String text = """
            PUB main() | a, b, c
            
                a := string("1234", 13, 10)
                b := "1234"
                c := @"1234"
                c := @\\"1234\\a\\b\\t\\n\\f\\r\\\\\\x01\\x02\\x03\\x04\\xA\\xFF"
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       39 00 00 00    End
            ' PUB main() | a, b, c
            00008 00008       03             (stack size)
            '     a := string("1234", 13, 10)
            00009 00009       9E 07 31 32 33 STRING
            0000E 0000E       34 0D 0A 00
            00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     b := "1234"
            00013 00013       9E 05 31 32 33 STRING
            00018 00018       34 00
            0001A 0001A       F1             VAR_WRITE LONG DBASE+$00001 (short)
            '     c := @"1234"
            0001B 0001B       9E 05 31 32 33 STRING
            00020 00020       34 00
            00022 00022       F2             VAR_WRITE LONG DBASE+$00002 (short)
            '     c := @\\"1234\\a\\b\\t\\n\\f\\r\\\\\\x01\\x02\\x03\\x04\\xA\\xFF"
            00023 00023       9E 12 31 32 33 STRING
            00028 00028       34 07 08 09 0A
            0002D 0002D       0C 0D 5C 01 02
            00032 00032       03 04 0A FF 00
            00037 00037       F2             VAR_WRITE LONG DBASE+$00002 (short)
            00038 00038       04             RETURN
            00039 00039       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testLString() throws Exception {
        String text = """
            PUB main() | a
            
                a := lstring("1234", 13, 10)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       14 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     a := lstring("1234", 13, 10)
            00009 00009       9E 07 06 31 32 LSTRING
            0000E 0000E       33 34 0D 0A
            00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00013 00013       04             RETURN
            """, compile(text));
    }

    @Test
    void testStringConstant() throws Exception {
        String text = """
            PUB main() | a
            
                a := %"ABCD"
                a := %"123"
                a := %"12"
                a := %"1"
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1D 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     a := %"ABCD"
            00009 00009       46 41 42 43 44 CONSTANT (%"ABCD")
            0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := %"123"
            0000F 0000F       46 31 32 33 00 CONSTANT (%"123")
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := %"12"
            00015 00015       44 31 32       CONSTANT (%"12")
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := %"1"
            00019 00019       42 31          CONSTANT (%"1")
            0001B 0001B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001C 0001C       04             RETURN
            0001D 0001D       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testByteWordLongFunction() throws Exception {
        String text = """
            PUB main() | a
            
                a := byte("1234", 13, 10)
                a := word("1234", 13, 10)
                a := long("1234", 13, 10)
            
                a := byte("1234", long 13, 10)
                a := word("1234", byte 13, 10)
                a := long("1234", word 13, 10)
            
                test(byte("1234", 13, 10))
                test(word("1234", 13, 10))
                test(long("1234", 13, 10))
            
                a := long(1, 2, 3.0, 4)
            
            PUB test(p)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       C0 00 00 81    Method test @ $000C0 (1 parameters, 0 returns)
            00008 00008       C2 00 00 00    End
            ' PUB main() | a
            0000C 0000C       01             (stack size)
            '     a := byte("1234", 13, 10)
            0000D 0000D       9E 06 31 32 33 BYTES
            00012 00012       34 0D 0A
            00015 00015       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := word("1234", 13, 10)
            00016 00016       9E 0C 31 00 32 WORDS
            0001B 0001B       00 33 00 34 00
            00020 00020       0D 00 0A 00
            00024 00024       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := long("1234", 13, 10)
            00025 00025       9E 18 31 00 00 LONGS
            0002A 0002A       00 32 00 00 00
            0002F 0002F       33 00 00 00 34
            00034 00034       00 00 00 0D 00
            00039 00039       00 00 0A 00 00
            0003E 0003E       00
            0003F 0003F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := byte("1234", long 13, 10)
            00040 00040       9E 09 31 32 33 BYTES
            00045 00045       34 0D 00 00 00
            0004A 0004A       0A
            0004B 0004B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := word("1234", byte 13, 10)
            0004C 0004C       9E 0B 31 00 32 WORDS
            00051 00051       00 33 00 34 00
            00056 00056       0D 0A 00
            00059 00059       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := long("1234", word 13, 10)
            0005A 0005A       9E 16 31 00 00 LONGS
            0005F 0005F       00 32 00 00 00
            00064 00064       33 00 00 00 34
            00069 00069       00 00 00 0D 00
            0006E 0006E       0A 00 00 00
            00072 00072       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     test(byte("1234", 13, 10))
            00073 00073       00             ANCHOR
            00074 00074       9E 06 31 32 33 BYTES
            00079 00079       34 0D 0A
            0007C 0007C       0A 01          CALL_SUB (1)
            '     test(word("1234", 13, 10))
            0007E 0007E       00             ANCHOR
            0007F 0007F       9E 0C 31 00 32 WORDS
            00084 00084       00 33 00 34 00
            00089 00089       0D 00 0A 00
            0008D 0008D       0A 01          CALL_SUB (1)
            '     test(long("1234", 13, 10))
            0008F 0008F       00             ANCHOR
            00090 00090       9E 18 31 00 00 LONGS
            00095 00095       00 32 00 00 00
            0009A 0009A       33 00 00 00 34
            0009F 0009F       00 00 00 0D 00
            000A4 000A4       00 00 0A 00 00
            000A9 000A9       00
            000AA 000AA       0A 01          CALL_SUB (1)
            '     a := long(1, 2, 3.0, 4)
            000AC 000AC       9E 10 01 00 00 LONGS
            000B1 000B1       00 02 00 00 00
            000B6 000B6       00 00 40 40 04
            000BB 000BB       00 00 00
            000BE 000BE       F0             VAR_WRITE LONG DBASE+$00000 (short)
            000BF 000BF       04             RETURN
            ' PUB test(p)
            000C0 000C0       00             (stack size)
            000C1 000C1       04             RETURN
            000C2 000C2       00 00          Padding
            """, compile(text));
    }

    @Test
    void testLookup() throws Exception {
        String text = """
            PUB main() | a, b
            
                a := lookup(b : 10, 20..30, 40)
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1A 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     a := lookup(b : 10, 20..30, 40)
            00009 00009       42 18          ADDRESS ($00018)
            0000B 0000B       E1             VAR_READ LONG DBASE+$00001 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       AB             CONSTANT (10)
            0000E 0000E       1F             LOOKUP
            0000F 0000F       42 14          CONSTANT (20)
            00011 00011       42 1E          CONSTANT (30)
            00013 00013       21             LOOKUP
            00014 00014       42 28          CONSTANT (40)
            00016 00016       1F             LOOKUP
            00017 00017       23             LOOKDONE
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00019 00019       04             RETURN
            0001A 0001A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testLookdownString() throws Exception {
        String text = """
            PUB main() | a, b
            
                a := lookdown(b : "abcdefgh")
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       28 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     a := lookdown(b : "abcdefgh")
            00009 00009       42 26          ADDRESS ($00026)
            0000B 0000B       E1             VAR_READ LONG DBASE+$00001 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       42 61          CONSTANT ("a")
            0000F 0000F       20             LOOKDOWN
            00010 00010       42 62          CONSTANT ("b")
            00012 00012       20             LOOKDOWN
            00013 00013       42 63          CONSTANT ("c")
            00015 00015       20             LOOKDOWN
            00016 00016       42 64          CONSTANT ("d")
            00018 00018       20             LOOKDOWN
            00019 00019       42 65          CONSTANT ("e")
            0001B 0001B       20             LOOKDOWN
            0001C 0001C       42 66          CONSTANT ("f")
            0001E 0001E       20             LOOKDOWN
            0001F 0001F       42 67          CONSTANT ("g")
            00021 00021       20             LOOKDOWN
            00022 00022       42 68          CONSTANT ("h")
            00024 00024       20             LOOKDOWN
            00025 00025       23             LOOKDONE
            00026 00026       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00027 00027       04             RETURN
            """, compile(text));
    }

    @Test
    void testFloatOp() throws Exception {
        String text = """
            PUB main() | a, b, c
            
                    a := b *. c
                    a := round(b +. c)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       16 00 00 00    End
            ' PUB main() | a, b, c
            00008 00008       03             (stack size)
            '         a := b *. c
            00009 00009       E1             VAR_READ LONG DBASE+$00001 (short)
            0000A 0000A       E2             VAR_READ LONG DBASE+$00002 (short)
            0000B 0000B       19 C2          FLOAT_MULTIPLY
            0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         a := round(b +. c)
            0000E 0000E       E1             VAR_READ LONG DBASE+$00001 (short)
            0000F 0000F       E2             VAR_READ LONG DBASE+$00002 (short)
            00010 00010       19 BE          FLOAT_ADD
            00012 00012       19 A8          ROUND
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00015 00015       04             RETURN
            00016 00016       00 00          Padding
            """, compile(text));
    }

    @Test
    void testPostAssign() throws Exception {
        String text = """
            PUB start() : a, b, c
            
                a := b\\c
                a := b[0]\\c
                a := b[0]\\c[1]
                a := field[b][0]\\c
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 30 80    Method start @ $00008 (0 parameters, 3 returns)
            00004 00004       20 00 00 00    End
            ' PUB start() : a, b, c
            00008 00008       00             (stack size)
            '     a := b\\c
            00009 00009       E2             VAR_READ LONG DBASE+$00002 (short)
            0000A 0000A       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0000B 0000B       29             SWAP
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b[0]\\c
            0000D 0000D       E2             VAR_READ LONG DBASE+$00002 (short)
            0000E 0000E       5D 04          VAR_SETUP LONG DBASE+$00001 (short)
            00010 00010       29             SWAP
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := b[0]\\c[1]
            00012 00012       5D 0C 1C       VAR_READ LONG DBASE+$00003 (short)
            00015 00015       5D 04          VAR_SETUP LONG DBASE+$00001 (short)
            00017 00017       29             SWAP
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := field[b][0]\\c
            00019 00019       E2             VAR_READ LONG DBASE+$00002 (short)
            0001A 0001A       E1             VAR_READ LONG DBASE+$00001 (short)
            0001B 0001B       A1             CONSTANT (0)
            0001C 0001C       4C             FIELD_SETUP
            0001D 0001D       29             SWAP
            0001E 0001E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001F 0001F       04             RETURN
            """, compile(text));
    }

    @Test
    void testMethodAddress() throws Exception {
        String text = """
            PUB main() | a
            
                a := @function
            
            PUB function()
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       11 00 00 80    Method function @ $00011 (0 parameters, 0 returns)
            00008 00008       13 00 00 00    End
            ' PUB main() | a
            0000C 0000C       01             (stack size)
            '     a := @function
            0000D 0000D       11 01          SUB_ADDRESS (1)
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00010 00010       04             RETURN
            ' PUB function()
            00011 00011       00             (stack size)
            00012 00012       04             RETURN
            00013 00013       00             Padding
            """, compile(text));
    }

    @Test
    void testMethodPointers() throws Exception {
        String text = """
            VAR\
                long _ptr1
            PUB method(x, y) | _ptr2
                _ptr1(x, y)
                _ptr2(x, y)
                _ptr3(x, y)
            
            DAT
            _ptr3           long  0
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 8)
            00000 00000       0C 00 00 82    Method method @ $0000C (2 parameters, 0 returns)
            00004 00004       20 00 00 00    End
            00008 00008 00000 00 00 00 00    _ptr3               long    0
            ' PUB method(x, y) | _ptr2
            0000C 0000C       01             (stack size)
            '     _ptr1(x, y)
            0000D 0000D       00             ANCHOR
            0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)
            0000F 0000F       E1             VAR_READ LONG DBASE+$00001 (short)
            00010 00010       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            00012 00012       0B             CALL_PTR
            '     _ptr2(x, y)
            00013 00013       00             ANCHOR
            00014 00014       E0             VAR_READ LONG DBASE+$00000 (short)
            00015 00015       E1             VAR_READ LONG DBASE+$00001 (short)
            00016 00016       E2             VAR_READ LONG DBASE+$00002 (short)
            00017 00017       0B             CALL_PTR
            '     _ptr3(x, y)
            00018 00018       00             ANCHOR
            00019 00019       E0             VAR_READ LONG DBASE+$00000 (short)
            0001A 0001A       E1             VAR_READ LONG DBASE+$00001 (short)
            0001B 0001B       5B 08 1C       MEM_READ LONG PBASE+$00008
            0001E 0001E       0B             CALL_PTR
            0001F 0001F       04             RETURN
            """, compile(text, false));
    }

    @Test
    void testMethodPointersTrap() throws Exception {
        String text = """
            VAR\
                long _ptr1
            PUB method(x, y) | _ptr2
                \\_ptr1(x, y)
                \\_ptr2(x, y)
                \\_ptr3(x, y)
            
            DAT
            _ptr3           long  0
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 8)
            00000 00000       0C 00 00 82    Method method @ $0000C (2 parameters, 0 returns)
            00004 00004       20 00 00 00    End
            00008 00008 00000 00 00 00 00    _ptr3               long    0
            ' PUB method(x, y) | _ptr2
            0000C 0000C       01             (stack size)
            '     \\_ptr1(x, y)
            0000D 0000D       02             ANCHOR_TRAP
            0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)
            0000F 0000F       E1             VAR_READ LONG DBASE+$00001 (short)
            00010 00010       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            00012 00012       0B             CALL_PTR
            '     \\_ptr2(x, y)
            00013 00013       02             ANCHOR_TRAP
            00014 00014       E0             VAR_READ LONG DBASE+$00000 (short)
            00015 00015       E1             VAR_READ LONG DBASE+$00001 (short)
            00016 00016       E2             VAR_READ LONG DBASE+$00002 (short)
            00017 00017       0B             CALL_PTR
            '     \\_ptr3(x, y)
            00018 00018       02             ANCHOR_TRAP
            00019 00019       E0             VAR_READ LONG DBASE+$00000 (short)
            0001A 0001A       E1             VAR_READ LONG DBASE+$00001 (short)
            0001B 0001B       5B 08 1C       MEM_READ LONG PBASE+$00008
            0001E 0001E       0B             CALL_PTR
            0001F 0001F       04             RETURN
            """, compile(text, false));
    }

    @Test
    void testMethodPointerReturn() throws Exception {
        String text = """
            VAR\
                long _ptr
            PUB main() | a, b
                a := _ptr():1
                a, b := _ptr():2
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 8)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       15 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     a := _ptr():1
            00009 00009       01             ANCHOR (push)
            0000A 0000A       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            0000C 0000C       0B             CALL_PTR
            0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a, b := _ptr():2
            0000E 0000E       01             ANCHOR (push)
            0000F 0000F       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            00011 00011       0B             CALL_PTR
            00012 00012       F1             VAR_WRITE LONG DBASE+$00001 (short)
            00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00014 00014       04             RETURN
            00015 00015       00 00 00       Padding
            """, compile(text, false));
    }

    @Test
    void testMethodPointerReturnTernary() throws Exception {
        String text = """
            VAR\
                long _ptr
            PUB main() | a, b
                a := b ? _ptr():1 : 0
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 8)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       12 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     a := b ? _ptr():1 : 0
            00009 00009       E1             VAR_READ LONG DBASE+$00001 (short)
            0000A 0000A       01             ANCHOR (push)
            0000B 0000B       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            0000D 0000D       0B             CALL_PTR
            0000E 0000E       A1             CONSTANT (0)
            0000F 0000F       6B             TERNARY_IF_ELSE
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00011 00011       04             RETURN
            00012 00012       00 00          Padding
            """, compile(text, false));
    }

    @Test
    void testMethodPointersArray() throws Exception {
        String text = """
            VAR\
                long _ptr1
            PUB method(x, y) | _ptr2
                _ptr1[0](x, y)
                _ptr2[1](x, y)
                _ptr3[2](x, y)
            
            DAT
            _ptr3           long  0
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 8)
            00000 00000       0C 00 00 82    Method method @ $0000C (2 parameters, 0 returns)
            00004 00004       23 00 00 00    End
            00008 00008 00000 00 00 00 00    _ptr3               long    0
            ' PUB method(x, y) | _ptr2
            0000C 0000C       01             (stack size)
            '     _ptr1[0](x, y)
            0000D 0000D       00             ANCHOR
            0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)
            0000F 0000F       E1             VAR_READ LONG DBASE+$00001 (short)
            00010 00010       5C 04 1C       VAR_READ LONG VBASE+$00001 (short)
            00013 00013       0B             CALL_PTR
            '     _ptr2[1](x, y)
            00014 00014       00             ANCHOR
            00015 00015       E0             VAR_READ LONG DBASE+$00000 (short)
            00016 00016       E1             VAR_READ LONG DBASE+$00001 (short)
            00017 00017       5D 0C 1C       VAR_READ LONG DBASE+$00003 (short)
            0001A 0001A       0B             CALL_PTR
            '     _ptr3[2](x, y)
            0001B 0001B       00             ANCHOR
            0001C 0001C       E0             VAR_READ LONG DBASE+$00000 (short)
            0001D 0001D       E1             VAR_READ LONG DBASE+$00001 (short)
            0001E 0001E       5B 10 1C       MEM_READ LONG PBASE+$00010
            00021 00021       0B             CALL_PTR
            00022 00022       04             RETURN
            00023 00023       00             Padding
            """, compile(text, false));
    }

    @Test
    void testMethodPointersArrayTrap() throws Exception {
        String text = """
            VAR\
                long _ptr1
            PUB method(x, y) | _ptr2, i
                \\_ptr1[i](x, y)
                \\_ptr2[i](x, y)
                \\_ptr3[i](x, y)
            
            DAT
            _ptr3           long  0
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 8)
            00000 00000       0C 00 00 82    Method method @ $0000C (2 parameters, 0 returns)
            00004 00004       26 00 00 00    End
            00008 00008 00000 00 00 00 00    _ptr3               long    0
            ' PUB method(x, y) | _ptr2, i
            0000C 0000C       02             (stack size)
            '     \\_ptr1[i](x, y)
            0000D 0000D       02             ANCHOR_TRAP
            0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)
            0000F 0000F       E1             VAR_READ LONG DBASE+$00001 (short)
            00010 00010       E3             VAR_READ LONG DBASE+$00003 (short)
            00011 00011       5F 04 1C       VAR_READ_INDEXED LONG VBASE+$00004
            00014 00014       0B             CALL_PTR
            '     \\_ptr2[i](x, y)
            00015 00015       02             ANCHOR_TRAP
            00016 00016       E0             VAR_READ LONG DBASE+$00000 (short)
            00017 00017       E1             VAR_READ LONG DBASE+$00001 (short)
            00018 00018       E3             VAR_READ LONG DBASE+$00003 (short)
            00019 00019       60 08 1C       VAR_READ_INDEXED LONG DBASE+$00008
            0001C 0001C       0B             CALL_PTR
            '     \\_ptr3[i](x, y)
            0001D 0001D       02             ANCHOR_TRAP
            0001E 0001E       E0             VAR_READ LONG DBASE+$00000 (short)
            0001F 0001F       E1             VAR_READ LONG DBASE+$00001 (short)
            00020 00020       E3             VAR_READ LONG DBASE+$00003 (short)
            00021 00021       5E 08 1C       MEM_READ LONG INDEXED PBASE+$00008
            00024 00024       0B             CALL_PTR
            00025 00025       04             RETURN
            00026 00026       00 00          Padding
            """, compile(text, false));
    }

    @Test
    void testMethodPointerAsArgument() throws Exception {
        String text = """
            PUB main()
            
                set(@method)
            
            PUB set(ptr) | a
            
                a := ptr
            
            PUB method(x, y)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            00004 00004       17 00 00 81    Method set @ $00017 (1 parameters, 0 returns)
            00008 00008       1B 00 00 82    Method method @ $0001B (2 parameters, 0 returns)
            0000C 0000C       1D 00 00 00    End
            ' PUB main()
            00010 00010       00             (stack size)
            '     set(@method)
            00011 00011       00             ANCHOR
            00012 00012       11 02          SUB_ADDRESS (2)
            00014 00014       0A 01          CALL_SUB (1)
            00016 00016       04             RETURN
            ' PUB set(ptr) | a
            00017 00017       01             (stack size)
            '     a := ptr
            00018 00018       E0             VAR_READ LONG DBASE+$00000 (short)
            00019 00019       F1             VAR_WRITE LONG DBASE+$00001 (short)
            0001A 0001A       04             RETURN
            ' PUB method(x, y)
            0001B 0001B       00             (stack size)
            0001C 0001C       04             RETURN
            0001D 0001D       00 00 00       Padding
            """, compile(text, false));
    }

    @Test
    void testTypedAddress() throws Exception {
        String text = """
            PUB main() | a, b, c
            
                a := @word[b]
                a := @word[b][c]
                a := @@word[b]
                a := @@word[b][c]
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1E 00 00 00    End
            ' PUB main() | a, b, c
            00008 00008       03             (stack size)
            '     a := @word[b]
            00009 00009       E1             VAR_READ LONG DBASE+$00001 (short)
            0000A 0000A       62 1B          MEM_ADDRESS
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := @word[b][c]
            0000D 0000D       E1             VAR_READ LONG DBASE+$00001 (short)
            0000E 0000E       E2             VAR_READ LONG DBASE+$00002 (short)
            0000F 0000F       65 1B          MEM_ADDRESS INDEXED
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := @@word[b]
            00012 00012       E1             VAR_READ LONG DBASE+$00001 (short)
            00013 00013       62 1C          MEM_READ WORD
            00015 00015       24             ADD_PBASE
            00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := @@word[b][c]
            00017 00017       E1             VAR_READ LONG DBASE+$00001 (short)
            00018 00018       E2             VAR_READ LONG DBASE+$00002 (short)
            00019 00019       65 1C          MEM_READ WORD INDEXED
            0001B 0001B       24             ADD_PBASE
            0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001D 0001D       04             RETURN
            0001E 0001E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testTypedAddressMethodCall() throws Exception {
        String text = """
            PUB main() | a, b, c, d, e
            
                long[a](c, d, e)
                long[a][b](c, d, e)
                \\long[a](c, d, e)
                \\long[a][b](c, d, e)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       2C 00 00 00    End
            ' PUB main() | a, b, c, d, e
            00008 00008       05             (stack size)
            '     long[a](c, d, e)
            00009 00009       00             ANCHOR
            0000A 0000A       E2             VAR_READ LONG DBASE+$00002 (short)
            0000B 0000B       E3             VAR_READ LONG DBASE+$00003 (short)
            0000C 0000C       E4             VAR_READ LONG DBASE+$00004 (short)
            0000D 0000D       E0             VAR_READ LONG DBASE+$00000 (short)
            0000E 0000E       63 1C          MEM_READ LONG
            00010 00010       0B             CALL_PTR
            '     long[a][b](c, d, e)
            00011 00011       00             ANCHOR
            00012 00012       E2             VAR_READ LONG DBASE+$00002 (short)
            00013 00013       E3             VAR_READ LONG DBASE+$00003 (short)
            00014 00014       E4             VAR_READ LONG DBASE+$00004 (short)
            00015 00015       E0             VAR_READ LONG DBASE+$00000 (short)
            00016 00016       E1             VAR_READ LONG DBASE+$00001 (short)
            00017 00017       66 1C          MEM_READ LONG INDEXED
            00019 00019       0B             CALL_PTR
            '     \\long[a](c, d, e)
            0001A 0001A       02             ANCHOR_TRAP
            0001B 0001B       E2             VAR_READ LONG DBASE+$00002 (short)
            0001C 0001C       E3             VAR_READ LONG DBASE+$00003 (short)
            0001D 0001D       E4             VAR_READ LONG DBASE+$00004 (short)
            0001E 0001E       E0             VAR_READ LONG DBASE+$00000 (short)
            0001F 0001F       63 1C          MEM_READ LONG
            00021 00021       0B             CALL_PTR
            '     \\long[a][b](c, d, e)
            00022 00022       02             ANCHOR_TRAP
            00023 00023       E2             VAR_READ LONG DBASE+$00002 (short)
            00024 00024       E3             VAR_READ LONG DBASE+$00003 (short)
            00025 00025       E4             VAR_READ LONG DBASE+$00004 (short)
            00026 00026       E0             VAR_READ LONG DBASE+$00000 (short)
            00027 00027       E1             VAR_READ LONG DBASE+$00001 (short)
            00028 00028       66 1C          MEM_READ LONG INDEXED
            0002A 0002A       0B             CALL_PTR
            0002B 0002B       04             RETURN
            """, compile(text));
    }

    @Test
    void testUnary() throws Exception {
        String text = """
            PUB main() | a, b
            
                -= b
                != b
                a := -= b
                a := != b
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       14 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     -= b
            00009 00009       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0000A 0000A       2E             NEGATE_ASSIGN
            '     != b
            0000B 0000B       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0000C 0000C       2D             BITNOT_ASSIGN
            '     a := -= b
            0000D 0000D       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0000E 0000E       55             NEGATE_ASSIGN (push)
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := != b
            00010 00010       D1             VAR_SETUP LONG DBASE+$00001 (short)
            00011 00011       54             BITNOT_ASSIGN (push)
            00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00013 00013       04             RETURN
            """, compile(text));
    }

    @Test
    void testUnaryFloat() throws Exception {
        String text = """
            PUB main() | a, b
            
                a := -. b
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0E 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     a := -. b
            00009 00009       E1             VAR_READ LONG DBASE+$00001 (short)
            0000A 0000A       19 AE          FLOAT_NEGATE
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0000D 0000D       04             RETURN
            0000E 0000E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testFieldPointer() throws Exception {
        String text = """
            VAR
            
                byte a
                word b
            
            PUB main() | c, p
            
                p := ^@a
                p := ^@b
                p := ^@c
            
                p := ^@a.[5..0]
                p := ^@b.[5..0]
                p := ^@c.[5..0]
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 8)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       29 00 00 00    End
            ' PUB main() | c, p
            00008 00008       02             (stack size)
            '     p := ^@a
            00009 00009       50 04 1A       VAR_BITFIELD_PTR BYTE VBASE+$00004
            0000C 0000C       F1             VAR_WRITE LONG DBASE+$00001 (short)
            '     p := ^@b
            0000D 0000D       56 05 1A       VAR_BITFIELD_PTR WORD VBASE+$00005
            00010 00010       F1             VAR_WRITE LONG DBASE+$00001 (short)
            '     p := ^@c
            00011 00011       D0 1A          VAR_BITFIELD_PTR LONG DBASE+$00000 (short)
            00013 00013       F1             VAR_WRITE LONG DBASE+$00001 (short)
            '     p := ^@a.[5..0]
            00014 00014       50 04          VAR_SETUP BYTE VBASE+$00004
            00016 00016       7D A0 01 1A    BITFIELD_PTR
            0001A 0001A       F1             VAR_WRITE LONG DBASE+$00001 (short)
            '     p := ^@b.[5..0]
            0001B 0001B       56 05          VAR_SETUP WORD VBASE+$00005
            0001D 0001D       7D A0 01 1A    BITFIELD_PTR
            00021 00021       F1             VAR_WRITE LONG DBASE+$00001 (short)
            '     p := ^@c.[5..0]
            00022 00022       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00023 00023       7D A0 01 1A    BITFIELD_PTR
            00027 00027       F1             VAR_WRITE LONG DBASE+$00001 (short)
            00028 00028       04             RETURN
            00029 00029       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testDatFieldPointer() throws Exception {
        String text = """
            PUB main() | p
            
                p := ^@a
                p := ^@b
                p := ^@c
            
                p := ^@a.[5..0]
                p := ^@b.[5..0]
                p := ^@c.[5..0]
            
            DAT
            
            a   byte  0
            b   word  0
            c   long  0
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0F 00 00 80    Method main @ $0000F (0 parameters, 0 returns)
            00004 00004       32 00 00 00    End
            00008 00008 00000 00             a                   byte    0
            00009 00009 00001 00 00          b                   word    0
            0000B 0000B 00003 00 00 00 00    c                   long    0
            ' PUB main() | p
            0000F 0000F       01             (stack size)
            '     p := ^@a
            00010 00010       4F 08 1A       MEM_BITFIELD_PTR BYTE PBASE+$00008
            00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     p := ^@b
            00014 00014       55 09 1A       MEM_BITFIELD_PTR WORD PBASE+$00009
            00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     p := ^@c
            00018 00018       5B 0B 1A       MEM_BITFIELD_PTR LONG PBASE+$0000B
            0001B 0001B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     p := ^@a.[5..0]
            0001C 0001C       4F 08          MEM_SETUP BYTE PBASE+$00008
            0001E 0001E       7D A0 01 1A    BITFIELD_PTR
            00022 00022       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     p := ^@b.[5..0]
            00023 00023       55 09          MEM_SETUP WORD PBASE+$00009
            00025 00025       7D A0 01 1A    BITFIELD_PTR
            00029 00029       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     p := ^@c.[5..0]
            0002A 0002A       5B 0B          MEM_SETUP LONG PBASE+$0000B
            0002C 0002C       7D A0 01 1A    BITFIELD_PTR
            00030 00030       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00031 00031       04             RETURN
            00032 00032       00 00          Padding
            """, compile(text));
    }

    @Test
    void testRegisterFieldPointer() throws Exception {
        String text = """
            PUB main() | p
            
                p := ^@INA
                p := ^@OUTA
                p := ^@DIRA
            
                p := ^@INA.[5..0]
                p := ^@OUTA.[5..0]
                p := ^@DIRA.[5..0]
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       25 00 00 00    End
            ' PUB main() | p
            00008 00008       01             (stack size)
            '     p := ^@INA
            00009 00009       BE 1A          REG_BITFIELD_PTR +$1FE (short)
            0000B 0000B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     p := ^@OUTA
            0000C 0000C       BC 1A          REG_BITFIELD_PTR +$1FC (short)
            0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     p := ^@DIRA
            0000F 0000F       BA 1A          REG_BITFIELD_PTR +$1FA (short)
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     p := ^@INA.[5..0]
            00012 00012       BE             REG_SETUP +$1FE (short)
            00013 00013       7D A0 01 1A    BITFIELD_PTR
            00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     p := ^@OUTA.[5..0]
            00018 00018       BC             REG_SETUP +$1FC (short)
            00019 00019       7D A0 01 1A    BITFIELD_PTR
            0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     p := ^@DIRA.[5..0]
            0001E 0001E       BA             REG_SETUP +$1FA (short)
            0001F 0001F       7D A0 01 1A    BITFIELD_PTR
            00023 00023       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00024 00024       04             RETURN
            00025 00025       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testClkModeField() throws Exception {
        String text = """
            \
            PUB main() | p
            
                p := ^@CLKMODE
                p := ^@CLKMODE.[5..0]
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       17 00 00 00    End
            ' PUB main() | p
            00008 00008       01             (stack size)
            '     p := ^@CLKMODE
            00009 00009       42 40          CONSTANT ($40)
            0000B 0000B       63 1A          MEM_BITFIELD_PTR LONG
            0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     p := ^@CLKMODE.[5..0]
            0000E 0000E       42 40          CONSTANT ($40)
            00010 00010       63             MEM_SETUP LONG
            00011 00011       7D A0 01 1A    BITFIELD_PTR
            00015 00015       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00016 00016       04             RETURN
            00017 00017       00             Padding
            """, compile(text));
    }

    @Test
    void testField() throws Exception {
        String text = """
            PUB main() | a, p
            
                a := FIELD[p]
                a := FIELD[p][1]
            
                FIELD[p] := a
                FIELD[p][1] := a
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1C 00 00 00    End
            ' PUB main() | a, p
            00008 00008       02             (stack size)
            '     a := FIELD[p]
            00009 00009       E1             VAR_READ LONG DBASE+$00001 (short)
            0000A 0000A       4B 1C          FIELD_READ
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := FIELD[p][1]
            0000D 0000D       E1             VAR_READ LONG DBASE+$00001 (short)
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       4C 1C          FIELD_READ
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     FIELD[p] := a
            00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)
            00013 00013       E1             VAR_READ LONG DBASE+$00001 (short)
            00014 00014       4B 1D          FIELD_WRITE
            '     FIELD[p][1] := a
            00016 00016       E0             VAR_READ LONG DBASE+$00000 (short)
            00017 00017       E1             VAR_READ LONG DBASE+$00001 (short)
            00018 00018       A2             CONSTANT (1)
            00019 00019       4C 1D          FIELD_WRITE
            0001B 0001B       04             RETURN
            """, compile(text));
    }

    @Test
    void testPAsmLocalLabel() throws Exception {
        String text = """
            DAT
                    org     $000
            start
                    mov     pr0, #0
            .l1     add     pr0, a
                    djnz    a, #.l1
            .l2     jmp     #.l2
            a       long    10
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000                start              \s
            00000 00000   000 00 B0 07 F6                        mov     pr0, #0
            00004 00004   001 04 B0 03 F1    .l1                 add     pr0, a
            00008 00008   002 FE 09 6C FB                        djnz    a, #.l1
            0000C 0000C   003 FC FF 9F FD    .l2                 jmp     #.l2
            00010 00010   004 0A 00 00 00    a                   long    10
            """, compile(text));
    }

    @Test
    void testAddpinsRange() throws Exception {
        String text = """
            PUB start() | a, b
            
                pinfloat(1 addpins 6)
                pinfloat(7..1)
                pinfloat(1..7)
                pinfloat(a..b)
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       1B 00 00 00    End
            ' PUB start() | a, b
            00008 00008       02             (stack size)
            '     pinfloat(1 addpins 6)
            00009 00009       44 81 01       CONSTANT (1 addpins 6)
            0000C 0000C       38             PINFLOAT
            '     pinfloat(7..1)
            0000D 0000D       44 81 01       CONSTANT (7 .. 1)
            00010 00010       38             PINFLOAT
            '     pinfloat(1..7)
            00011 00011       44 87 06       CONSTANT (1 .. 7)
            00014 00014       38             PINFLOAT
            '     pinfloat(a..b)
            00015 00015       E0             VAR_READ LONG DBASE+$00000 (short)
            00016 00016       E1             VAR_READ LONG DBASE+$00001 (short)
            00017 00017       9F 95          ADDPINS_RANGE
            00019 00019       38             PINFLOAT
            0001A 0001A       04             RETURN
            0001B 0001B       00             Padding
            """, compile(text));
    }

    @Test
    void testConditionalsCrossingBlocks() throws Exception {
        String text = """
            PUB start() | a
            
            #IF 0
                if CLKFREQ >= 40_000_000
                    b := 1_000
            #ENDIF
                    a := 1_000
            
                repeat
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       10 00 00 00    End
            ' PUB start() | a
            00008 00008       01             (stack size)
            '         a := 1_000
            00009 00009       44 E8 03       CONSTANT (1_000)
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     repeat
            0000D 0000D       12 7F          JMP $0000D (-1)
            0000F 0000F       04             RETURN
            """, compile(text));
    }

    @Test
    void testPreprocessorCaseStatements() throws Exception {
        String text = """
            PUB start() | a, b
            
                case a
                    1:
                        b := a + 1
            #IF 0
                    2:\
                        b := a + 2
            #ENDIF
                    3:
                        b := a + 3
            
                repeat
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       20 00 00 00    End
            ' PUB start() | a, b
            00008 00008       02             (stack size)
            '     case a
            00009 00009       42 1D          ADDRESS ($0001D)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       1C 05          CASE_JMP $00013 (5)
            0000F 0000F       A4             CONSTANT (3)
            00010 00010       1C 07          CASE_JMP $00018 (7)
            00012 00012       1E             CASE_DONE
            '             b := a + 1
            00013 00013       E0             VAR_READ LONG DBASE+$00000 (short)
            00014 00014       A2             CONSTANT (1)
            00015 00015       8A             ADD
            00016 00016       F1             VAR_WRITE LONG DBASE+$00001 (short)
            00017 00017       1E             CASE_DONE
            '             b := a + 3
            00018 00018       E0             VAR_READ LONG DBASE+$00000 (short)
            00019 00019       A4             CONSTANT (3)
            0001A 0001A       8A             ADD
            0001B 0001B       F1             VAR_WRITE LONG DBASE+$00001 (short)
            0001C 0001C       1E             CASE_DONE
            '     repeat
            0001D 0001D       12 7F          JMP $0001D (-1)
            0001F 0001F       04             RETURN
            """, compile(text));
    }

    @Test
    void testPreprocessorAlternateBlockStart() throws Exception {
        String text1 = """
            PUB start() | a, b
            
            #IF 0
                if a == 1
            #ELSE
                if a == 2
            #ENDIF
                    b := a + 1
            
                repeat
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       15 00 00 00    End
            ' PUB start() | a, b
            00008 00008       02             (stack size)
            '     if a == 2
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A3             CONSTANT (2)
            0000B 0000B       70             EQUAL
            0000C 0000C       13 05          JZ $00012 (5)
            '         b := a + 1
            0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)
            0000F 0000F       A2             CONSTANT (1)
            00010 00010       8A             ADD
            00011 00011       F1             VAR_WRITE LONG DBASE+$00001 (short)
            '     repeat
            00012 00012       12 7F          JMP $00012 (-1)
            00014 00014       04             RETURN
            00015 00015       00 00 00       Padding
            """, compile(text1));

        String text2 = """
            PUB start() | a, b
            
            #IF 1
                if a == 1
            #ELSE
                if a == 2
            #ENDIF
                    b := a + 1
            
                repeat
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       15 00 00 00    End
            ' PUB start() | a, b
            00008 00008       02             (stack size)
            '     if a == 1
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A2             CONSTANT (1)
            0000B 0000B       70             EQUAL
            0000C 0000C       13 05          JZ $00012 (5)
            '         b := a + 1
            0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)
            0000F 0000F       A2             CONSTANT (1)
            00010 00010       8A             ADD
            00011 00011       F1             VAR_WRITE LONG DBASE+$00001 (short)
            '     repeat
            00012 00012       12 7F          JMP $00012 (-1)
            00014 00014       04             RETURN
            00015 00015       00 00 00       Padding
            """, compile(text2));
    }

    @Test
    void testClkModeAndFreq() throws Exception {
        String text = """
            PUB start() | a, b
            
                    a := CLKMODE
                    a := CLKMODE.[23..18]
                    a := CLKMODE.byte[1]
                    a := @CLKMODE
            
                    CLKMODE := a
                    CLKMODE.[23..18] := a
                    CLKMODE.byte[1] := a
            
                    b := CLKFREQ
                    b := @CLKFREQ
            
            DAT     org    $000
            
                    rdlong c, #@CLKMODE
                    rdlong d, #@CLKFREQ
            
            c       res    1
            d       res    1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       10 00 00 80    Method start @ $00010 (0 parameters, 0 returns)
            00004 00004       41 00 00 00    End
            00008 00008   000                                    org     $000
            00008 00008   000 40 04 04 FB                        rdlong  c, #@CLKMODE
            0000C 0000C   001 44 06 04 FB                        rdlong  d, #@CLKFREQ
            00010 00010   002                c                   res     1
            00010 00010   003                d                   res     1
            ' PUB start() | a, b
            00010 00010       02             (stack size)
            '         a := CLKMODE
            00011 00011       42 40          CONSTANT ($40)
            00013 00013       63 1C          MEM_READ LONG
            00015 00015       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         a := CLKMODE.[23..18]
            00016 00016       42 40          CONSTANT ($40)
            00018 00018       63             MEM_SETUP LONG
            00019 00019       7E B2 01       BITFIELD_READ
            0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         a := CLKMODE.byte[1]
            0001D 0001D       42 40          CONSTANT ($40)
            0001F 0001F       A2             CONSTANT (1)
            00020 00020       64 1C          MEM_READ BYTE INDEXED
            00022 00022       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         a := @CLKMODE
            00023 00023       42 40          CONSTANT (64)
            00025 00025       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '         CLKMODE := a
            00026 00026       E0             VAR_READ LONG DBASE+$00000 (short)
            00027 00027       42 40          CONSTANT ($40)
            00029 00029       63 1D          MEM_WRITE LONG
            '         CLKMODE.[23..18] := a
            0002B 0002B       E0             VAR_READ LONG DBASE+$00000 (short)
            0002C 0002C       42 40          CONSTANT ($40)
            0002E 0002E       63             MEM_SETUP LONG
            0002F 0002F       7F B2 01       BITFIELD_WRITE
            '         CLKMODE.byte[1] := a
            00032 00032       E0             VAR_READ LONG DBASE+$00000 (short)
            00033 00033       42 40          CONSTANT ($40)
            00035 00035       A2             CONSTANT (1)
            00036 00036       64 1D          MEM_WRITE BYTE INDEXED
            '         b := CLKFREQ
            00038 00038       42 44          CONSTANT ($44)
            0003A 0003A       63 1C          MEM_READ LONG
            0003C 0003C       F1             VAR_WRITE LONG DBASE+$00001 (short)
            '         b := @CLKFREQ
            0003D 0003D       42 44          CONSTANT (68)
            0003F 0003F       F1             VAR_WRITE LONG DBASE+$00001 (short)
            00040 00040       04             RETURN
            00041 00041       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testChainedAssignments() throws Exception {
        String text = """
            PUB start() : r | a, b
            
                r := (a := b)
                r := (byte[@a][0] := b)
                r := (a.byte[0] := b)
                r := (a.[5..0] := b)
            
                r := (REG[1] := a)
                r := (REG[1].[5..0] := a)
            
                r := (FIELD[a] := b)
                r := (FIELD[a][1] := b)
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 10 80    Method start @ $00008 (0 parameters, 1 returns)
            00004 00004       39 00 00 00    End
            ' PUB start() : r | a, b
            00008 00008       02             (stack size)
            '     r := (a := b)
            00009 00009       E2             VAR_READ LONG DBASE+$00002 (short)
            0000A 0000A       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0000B 0000B       1E             WRITE (push)
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     r := (byte[@a][0] := b)
            0000D 0000D       E2             VAR_READ LONG DBASE+$00002 (short)
            0000E 0000E       D1 1B          VAR_ADDRESS DBASE+$00001 (short)
            00010 00010       A1             CONSTANT (0)
            00011 00011       64             MEM_SETUP BYTE INDEXED
            00012 00012       1E             WRITE (push)
            00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     r := (a.byte[0] := b)
            00014 00014       E2             VAR_READ LONG DBASE+$00002 (short)
            00015 00015       51 04          MEM_SETUP BYTE DBASE+$00004
            00017 00017       1E             WRITE (push)
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     r := (a.[5..0] := b)
            00019 00019       E2             VAR_READ LONG DBASE+$00002 (short)
            0001A 0001A       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0001B 0001B       7D A0 01       BITFIELD_SETUP
            0001E 0001E       1E             WRITE (push)
            0001F 0001F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     r := (REG[1] := a)
            00020 00020       E1             VAR_READ LONG DBASE+$00001 (short)
            00021 00021       4D 01          REG_SETUP +$001
            00023 00023       1E             WRITE (push)
            00024 00024       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     r := (REG[1].[5..0] := a)
            00025 00025       E1             VAR_READ LONG DBASE+$00001 (short)
            00026 00026       4D 01          REG_SETUP +$001
            00028 00028       7D A0 01       BITFIELD_SETUP
            0002B 0002B       1E             WRITE (push)
            0002C 0002C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     r := (FIELD[a] := b)
            0002D 0002D       E2             VAR_READ LONG DBASE+$00002 (short)
            0002E 0002E       E1             VAR_READ LONG DBASE+$00001 (short)
            0002F 0002F       4B 1E          FIELD_WRITE (push)
            00031 00031       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     r := (FIELD[a][1] := b)
            00032 00032       E2             VAR_READ LONG DBASE+$00002 (short)
            00033 00033       E1             VAR_READ LONG DBASE+$00001 (short)
            00034 00034       A2             CONSTANT (1)
            00035 00035       4C 1E          FIELD_WRITE (push)
            00037 00037       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00038 00038       04             RETURN
            00039 00039       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testMethodKeywordsOverride() throws Exception {
        String text = """
            PUB start()
            
                lstring(byte(1, 2))
            
            PUB lstring(a)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method start @ $0000C (0 parameters, 0 returns)
            00004 00004       15 00 00 81    Method lstring @ $00015 (1 parameters, 0 returns)
            00008 00008       17 00 00 00    End
            ' PUB start()
            0000C 0000C       00             (stack size)
            '     lstring(byte(1, 2))
            0000D 0000D       00             ANCHOR
            0000E 0000E       9E 02 01 02    BYTES
            00012 00012       0A 01          CALL_SUB (1)
            00014 00014       04             RETURN
            ' PUB lstring(a)
            00015 00015       00             (stack size)
            00016 00016       04             RETURN
            00017 00017       00             Padding
            """, compile(text));
    }

    @Test
    void testLocalVariableKeywordsOverride() throws Exception {
        String text = """
            PUB start() | lstring
            
                lstring("1234")
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       14 00 00 00    End
            ' PUB start() | lstring
            00008 00008       01             (stack size)
            '     lstring("1234")
            00009 00009       00             ANCHOR
            0000A 0000A       9E 05 31 32 33 STRING
            0000F 0000F       34 00
            00011 00011       E0             VAR_READ LONG DBASE+$00000 (short)
            00012 00012       0B             CALL_PTR
            00013 00013       04             RETURN
            """, compile(text));
    }

    @Test
    void testGlobalVariableKeywordsOverride() throws Exception {
        String text = """
            VAR
            
                long lstring
            
            PUB start()
            
                lstring("1234")
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 8)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       15 00 00 00    End
            ' PUB start()
            00008 00008       00             (stack size)
            '     lstring("1234")
            00009 00009       00             ANCHOR
            0000A 0000A       9E 05 31 32 33 STRING
            0000F 0000F       34 00
            00011 00011       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            00013 00013       0B             CALL_PTR
            00014 00014       04             RETURN
            00015 00015       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testStringExpressions() throws Exception {
        String text = """
            PUB start() | a
            
                a := string("1234" | $80)
                a := string($80 | "1234")
                a := string("1234" + "5678")
            
                a := "1234" | $80
                a := $80 | "1234"
                a := "1234" + "5678"
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       40 00 00 00    End
            ' PUB start() | a
            00008 00008       01             (stack size)
            '     a := string("1234" | $80)
            00009 00009       9E 05 31 32 33 STRING
            0000E 0000E       B4 00
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := string($80 | "1234")
            00011 00011       9E 05 B1 32 33 STRING
            00016 00016       34 00
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := string("1234" + "5678")
            00019 00019       9E 08 31 32 33 STRING
            0001E 0001E       69 36 37 38 00
            00023 00023       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := "1234" | $80
            00024 00024       9E 05 31 32 33 STRING
            00029 00029       B4 00
            0002B 0002B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := $80 | "1234"
            0002C 0002C       9E 05 B1 32 33 STRING
            00031 00031       34 00
            00033 00033       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := "1234" + "5678"
            00034 00034       9E 08 31 32 33 STRING
            00039 00039       69 36 37 38 00
            0003E 0003E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0003F 0003F       04             RETURN
            """, compile(text));
    }

    @Test
    void testStructureAssign() throws Exception {
        String text = """
            CON
                sPoint(byte x, word y)
            
            PUB start() | sPoint pt[2], a
            
                pt.x := 1
                pt.y := 2
            
                pt[1].x := 1
                pt[1].y := 2
            
                pt[a].x := 1
                pt[a].y := 2
            
                pt.x := pt.y
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       30 00 00 00    End
            ' PUB start() | sPoint pt[2], a
            00008 00008       03             (stack size)
            '     pt.x := 1
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       51 00 1D       MEM_WRITE BYTE DBASE+$00000
            '     pt.y := 2
            0000D 0000D       A3             CONSTANT (2)
            0000E 0000E       57 01 1D       MEM_WRITE WORD DBASE+$00001
            '     pt[1].x := 1
            00011 00011       A2             CONSTANT (1)
            00012 00012       51 03 1D       MEM_WRITE BYTE DBASE+$00003
            '     pt[1].y := 2
            00015 00015       A3             CONSTANT (2)
            00016 00016       57 04 1D       MEM_WRITE WORD DBASE+$00004
            '     pt[a].x := 1
            00019 00019       A2             CONSTANT (1)
            0001A 0001A       5D 06 1C       VAR_READ LONG DBASE+$00006
            0001D 0001D       69 05 03 1D    STRUCT_WRITE BYTE DBASE+$00000 (indexed)
            '     pt[a].y := 2
            00021 00021       A3             CONSTANT (2)
            00022 00022       5D 06 1C       VAR_READ LONG DBASE+$00006
            00025 00025       69 19 03 1D    STRUCT_WRITE WORD DBASE+$00001 (indexed)
            '     pt.x := pt.y
            00029 00029       57 01 1C       MEM_READ WORD DBASE+$00001
            0002C 0002C       51 00 1D       MEM_WRITE BYTE DBASE+$00000
            0002F 0002F       04             RETURN
            """, compile(text));
    }

    @Test
    void testStructureRead() throws Exception {
        String text = """
            CON
                sPoint(byte x, word y)
            
            PUB start() | sPoint pt[2], a, b
            
                a := pt.x
                a := pt.y
            
                a := pt[1].x
                a := pt[1].y
            
                a := pt[b].x
                a := pt[b].y
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       36 00 00 00    End
            ' PUB start() | sPoint pt[2], a, b
            00008 00008       04             (stack size)
            '     a := pt.x
            00009 00009       51 00 1C       MEM_READ BYTE DBASE+$00000
            0000C 0000C       5D 06 1D       VAR_WRITE LONG DBASE+$00006
            '     a := pt.y
            0000F 0000F       57 01 1C       MEM_READ WORD DBASE+$00001
            00012 00012       5D 06 1D       VAR_WRITE LONG DBASE+$00006
            '     a := pt[1].x
            00015 00015       51 03 1C       MEM_READ BYTE DBASE+$00003
            00018 00018       5D 06 1D       VAR_WRITE LONG DBASE+$00006
            '     a := pt[1].y
            0001B 0001B       57 04 1C       MEM_READ WORD DBASE+$00004
            0001E 0001E       5D 06 1D       VAR_WRITE LONG DBASE+$00006
            '     a := pt[b].x
            00021 00021       5D 0A 1C       VAR_READ LONG DBASE+$0000A
            00024 00024       69 05 03 1C    STRUCT_READ BYTE DBASE+$00000 (indexed)
            00028 00028       5D 06 1D       VAR_WRITE LONG DBASE+$00006
            '     a := pt[b].y
            0002B 0002B       5D 0A 1C       VAR_READ LONG DBASE+$0000A
            0002E 0002E       69 19 03 1C    STRUCT_READ WORD DBASE+$00001 (indexed)
            00032 00032       5D 06 1D       VAR_WRITE LONG DBASE+$00006
            00035 00035       04             RETURN
            00036 00036       00 00          Padding
            """, compile(text));
    }

    @Test
    void testNestedStructureAssign() throws Exception {
        String text = """
            CON
              STRUCT sPoint(byte x, word y)
              STRUCT sLine(sPoint a, sPoint b, byte color)
            
            PUB main() | sLine line[2], a, b
            
              line.a.x := a
              line.b.y := a
            
              line[1].a.x := a
              line[1].a.y := a
            
              line[b].a.x := a
              line[b].a.y := a
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       36 00 00 00    End
            ' PUB main() | sLine line[2], a, b
            00008 00008       06             (stack size)
            '   line.a.x := a
            00009 00009       5D 0E 1C       VAR_READ LONG DBASE+$0000E
            0000C 0000C       51 00 1D       MEM_WRITE BYTE DBASE+$00000
            '   line.b.y := a
            0000F 0000F       5D 0E 1C       VAR_READ LONG DBASE+$0000E
            00012 00012       57 04 1D       MEM_WRITE WORD DBASE+$00004
            '   line[1].a.x := a
            00015 00015       5D 0E 1C       VAR_READ LONG DBASE+$0000E
            00018 00018       51 07 1D       MEM_WRITE BYTE DBASE+$00007
            '   line[1].a.y := a
            0001B 0001B       5D 0E 1C       VAR_READ LONG DBASE+$0000E
            0001E 0001E       57 08 1D       MEM_WRITE WORD DBASE+$00008
            '   line[b].a.x := a
            00021 00021       5D 0E 1C       VAR_READ LONG DBASE+$0000E
            00024 00024       5D 12 1C       VAR_READ LONG DBASE+$00012
            00027 00027       69 05 07 1D    STRUCT_WRITE BYTE DBASE+$00000 (indexed)
            '   line[b].a.y := a
            0002B 0002B       5D 0E 1C       VAR_READ LONG DBASE+$0000E
            0002E 0002E       5D 12 1C       VAR_READ LONG DBASE+$00012
            00031 00031       69 19 07 1D    STRUCT_WRITE WORD DBASE+$00001 (indexed)
            00035 00035       04             RETURN
            00036 00036       00 00          Padding
            """, compile(text));
    }

    @Test
    void testNestedStructureRead() throws Exception {
        String text = """
            CON
              STRUCT sPoint(byte x, word y)
              STRUCT sLine(sPoint a, sPoint b, byte color)
            
            PUB main() | sLine line[2], a, b
            
              a := line.a.x
              a := line.b.y
            
              a := line[1].a.x
              a := line[1].a.y
            
              a := line[b].a.x
              a := line[b].a.y
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       36 00 00 00    End
            ' PUB main() | sLine line[2], a, b
            00008 00008       06             (stack size)
            '   a := line.a.x
            00009 00009       51 00 1C       MEM_READ BYTE DBASE+$00000
            0000C 0000C       5D 0E 1D       VAR_WRITE LONG DBASE+$0000E
            '   a := line.b.y
            0000F 0000F       57 04 1C       MEM_READ WORD DBASE+$00004
            00012 00012       5D 0E 1D       VAR_WRITE LONG DBASE+$0000E
            '   a := line[1].a.x
            00015 00015       51 07 1C       MEM_READ BYTE DBASE+$00007
            00018 00018       5D 0E 1D       VAR_WRITE LONG DBASE+$0000E
            '   a := line[1].a.y
            0001B 0001B       57 08 1C       MEM_READ WORD DBASE+$00008
            0001E 0001E       5D 0E 1D       VAR_WRITE LONG DBASE+$0000E
            '   a := line[b].a.x
            00021 00021       5D 12 1C       VAR_READ LONG DBASE+$00012
            00024 00024       69 05 07 1C    STRUCT_READ BYTE DBASE+$00000 (indexed)
            00028 00028       5D 0E 1D       VAR_WRITE LONG DBASE+$0000E
            '   a := line[b].a.y
            0002B 0002B       5D 12 1C       VAR_READ LONG DBASE+$00012
            0002E 0002E       69 19 07 1C    STRUCT_READ WORD DBASE+$00001 (indexed)
            00032 00032       5D 0E 1D       VAR_WRITE LONG DBASE+$0000E
            00035 00035       04             RETURN
            00036 00036       00 00          Padding
            """, compile(text));
    }

    @Test
    void testNestedStructuresIndex() throws Exception {
        String text = """
            CON
              STRUCT sPoint(byte x, word y)
              STRUCT sLine(sPoint a, sPoint b, byte color)
              STRUCT sPolygon(byte n, sPoint pt[8], byte color)
            
            VAR
              sPolygon poly[2]
            
            PUB main() | a, b
            
              a := poly.pt[0].x
              a := poly.pt[1].y
            
              a := poly[0].pt[0].x
              a := poly[1].pt[1].y
            
              a := poly[b].n
              a := poly[b].pt[a].x
              a := poly[b].color
            
              a := poly[b].pt[2].y
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 56)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       36 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '   a := poly.pt[0].x
            00009 00009       50 05 1C       MEM_READ BYTE VBASE+$00005
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a := poly.pt[1].y
            0000D 0000D       56 09 1C       MEM_READ WORD VBASE+$00009
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a := poly[0].pt[0].x
            00011 00011       50 05 1C       MEM_READ BYTE VBASE+$00005
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a := poly[1].pt[1].y
            00015 00015       56 23 1C       MEM_READ WORD VBASE+$00023
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a := poly[b].n
            00019 00019       E1             VAR_READ LONG DBASE+$00001 (short)
            0001A 0001A       68 45 1A 1C    STRUCT_READ BYTE VBASE+$00004 (indexed)
            0001E 0001E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a := poly[b].pt[a].x
            0001F 0001F       E1             VAR_READ LONG DBASE+$00001 (short)
            00020 00020       E0             VAR_READ LONG DBASE+$00000 (short)
            00021 00021       68 56 03 1A 1C STRUCT_READ BYTE VBASE+$00005 (indexed)
            00026 00026       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a := poly[b].color
            00027 00027       E1             VAR_READ LONG DBASE+$00001 (short)
            00028 00028       68 D5 03 1A 1C STRUCT_READ BYTE VBASE+$0001D (indexed)
            0002D 0002D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a := poly[b].pt[2].y
            0002E 0002E       E1             VAR_READ LONG DBASE+$00001 (short)
            0002F 0002F       68 C9 01 1A 1C STRUCT_READ WORD VBASE+$0000C (indexed)
            00034 00034       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00035 00035       04             RETURN
            00036 00036       00 00          Padding
            """, compile(text));
    }

    @Test
    void testNestedStructureAddress() throws Exception {
        String text = """
            CON
              STRUCT sPoint(byte x, word y)
              STRUCT sLine(sPoint a, sPoint b, byte color)
              STRUCT sPolygon(byte n, sPoint pt[8], byte color)
            
            VAR
              sPolygon poly[2]
            
            PUB main() | a, b
            
              a := @poly
              a := @poly[1]
            
              a := @poly[b]
            
              a := @poly.pt
              a := @poly.pt[1]
              a := @poly[b].pt[2]
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 56)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       29 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '   a := @poly
            00009 00009       68 40 00       STRUCT_ADDRESS LONG VBASE+$00004 (indexed)
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a := @poly[1]
            0000D 0000D       68 E0 03 00    STRUCT_ADDRESS LONG VBASE+$0001E (indexed)
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a := @poly[b]
            00012 00012       E1             VAR_READ LONG DBASE+$00001 (short)
            00013 00013       68 41 1A 00    STRUCT_ADDRESS LONG VBASE+$00004 (indexed)
            00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a := @poly.pt
            00018 00018       68 50 00       STRUCT_ADDRESS LONG VBASE+$00005 (indexed)
            0001B 0001B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a := @poly.pt[1]
            0001C 0001C       68 80 01 00    STRUCT_ADDRESS LONG VBASE+$00008 (indexed)
            00020 00020       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a := @poly[b].pt[2]
            00021 00021       E1             VAR_READ LONG DBASE+$00001 (short)
            00022 00022       68 B1 01 1A 00 STRUCT_ADDRESS LONG VBASE+$0000B (indexed)
            00027 00027       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00028 00028       04             RETURN
            00029 00029       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testExpandedPAsmLinesLabel() throws Exception {
        String text = """
            CON
                 _CLKFREQ = 160_000_000
            PUB main()
            
                coginit(cogexec_new, @entry, 0)
            
            DAT             org   $000
            
            entry           asmclk
                            ret
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       24 00 00 80    Method main @ $00024 (0 parameters, 0 returns)
            00004 00004       2D 00 00 00    End
            00008 00008   000                                    org     $000
            00008 00008   000 03 80 80 FF    entry               hubset  ##clkmode_ & !%11
            0000C 0000C   001 00 F0 67 FD
            00010 00010   002 86 01 80 FF                        waitx   ##20000000 / 100
            00014 00014   003 1F 80 66 FD
            00018 00018   004 03 80 80 FF                        hubset  ##clkmode_
            0001C 0001C   005 00 F6 67 FD
            00020 00020   006 2D 00 64 FD                        ret
            ' PUB main()
            00024 00024       00             (stack size)
            '     coginit(cogexec_new, @entry, 0)
            00025 00025       42 10          CONSTANT (16)
            00027 00027       5B 08 1B       MEM_ADDRESS PBASE+$00008
            0002A 0002A       A1             CONSTANT (0)
            0002B 0002B       25             COGINIT
            0002C 0002C       04             RETURN
            0002D 0002D       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testExpandedPAsmLinesLabelWithDefaultClock() throws Exception {
        String text = """
            PUB main()
            
                coginit(cogexec_new, @entry, 0)
            
            DAT             org   $000
            
            entry           asmclk
                            ret
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            00004 00004       19 00 00 00    End
            00008 00008   000                                    org     $000
            00008 00008   000 00 00 64 FD    entry               hubset  #clkmode_ & 1
            0000C 0000C   001 2D 00 64 FD                        ret
            ' PUB main()
            00010 00010       00             (stack size)
            '     coginit(cogexec_new, @entry, 0)
            00011 00011       42 10          CONSTANT (16)
            00013 00013       5B 08 1B       MEM_ADDRESS PBASE+$00008
            00016 00016       A1             CONSTANT (0)
            00017 00017       25             COGINIT
            00018 00018       04             RETURN
            00019 00019       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testPointers() throws Exception {
        String text = """
            PUB main() | a, b, ^WORD ptr
            
                a := ptr
                a := ptr++
                a := ++ptr
            
                a := [ptr]
                a := [ptr]++
                a := ++[ptr]
            
                a := ptr[++]
                a := ptr[++]--
                a := --ptr[++]
                a := [++]ptr
                a := [++]ptr--
                a := --[++]ptr
            
                ptr := a
                [ptr] := a
            
                ptr[++] := a
                [++]ptr := a
            
                a := ptr[++] := b
                a := [++]ptr := b
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       64 00 00 00    End
            ' PUB main() | a, b, ^WORD ptr
            00008 00008       03             (stack size)
            '     a := ptr
            00009 00009       E2             VAR_READ LONG (^WORD) DBASE+$00002 (short)
            0000A 0000A       62 1C          MEM_READ WORD
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr++
            0000D 0000D       E2             VAR_READ LONG (^WORD) DBASE+$00002 (short)
            0000E 0000E       62             MEM_SETUP WORD
            0000F 0000F       23             POST_INC (push)
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ++ptr
            00011 00011       E2             VAR_READ LONG (^WORD) DBASE+$00002 (short)
            00012 00012       62             MEM_SETUP WORD
            00013 00013       21             PRE_INC (push)
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [ptr]
            00015 00015       E2             VAR_READ LONG (^WORD) DBASE+$00002 (short)
            00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [ptr]++
            00017 00017       D2             VAR_SETUP LONG (^WORD) DBASE+$00002 (short)
            00018 00018       80 23          POST_INC (2) (push)
            0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ++[ptr]
            0001B 0001B       D2             VAR_SETUP LONG (^WORD) DBASE+$00002 (short)
            0001C 0001C       80 21          PRE_INC (2) (push)
            0001E 0001E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr[++]
            0001F 0001F       D2             VAR_SETUP LONG (^WORD) DBASE+$00002 (short)
            00020 00020       80 23          POST_INC (2) (push)
            00022 00022       62 1C          MEM_READ WORD
            00024 00024       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr[++]--
            00025 00025       D2             VAR_SETUP LONG (^WORD) DBASE+$00002 (short)
            00026 00026       80 23          POST_INC (2) (push)
            00028 00028       62             MEM_SETUP WORD
            00029 00029       24             POST_DEC (push)
            0002A 0002A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := --ptr[++]
            0002B 0002B       D2             VAR_SETUP LONG (^WORD) DBASE+$00002 (short)
            0002C 0002C       80 23          POST_INC (2) (push)
            0002E 0002E       62             MEM_SETUP WORD
            0002F 0002F       22             PRE_DEC (push)
            00030 00030       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [++]ptr
            00031 00031       D2             VAR_SETUP LONG (^WORD) DBASE+$00002 (short)
            00032 00032       80 21          PRE_INC (2) (push)
            00034 00034       62 1C          MEM_READ WORD
            00036 00036       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [++]ptr--
            00037 00037       D2             VAR_SETUP LONG (^WORD) DBASE+$00002 (short)
            00038 00038       80 21          PRE_INC (2) (push)
            0003A 0003A       62             MEM_SETUP WORD
            0003B 0003B       24             POST_DEC (push)
            0003C 0003C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := --[++]ptr
            0003D 0003D       D2             VAR_SETUP LONG (^WORD) DBASE+$00002 (short)
            0003E 0003E       80 21          PRE_INC (2) (push)
            00040 00040       62             MEM_SETUP WORD
            00041 00041       22             PRE_DEC (push)
            00042 00042       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr := a
            00043 00043       E0             VAR_READ LONG DBASE+$00000 (short)
            00044 00044       E2             VAR_READ LONG (^WORD) DBASE+$00002 (short)
            00045 00045       62 1D          MEM_WRITE WORD
            '     [ptr] := a
            00047 00047       E0             VAR_READ LONG DBASE+$00000 (short)
            00048 00048       F2             VAR_WRITE LONG (^WORD) DBASE+$00002 (short)
            '     ptr[++] := a
            00049 00049       E0             VAR_READ LONG DBASE+$00000 (short)
            0004A 0004A       D2             VAR_SETUP LONG (^WORD) DBASE+$00002 (short)
            0004B 0004B       80 23          POST_INC (2) (push)
            0004D 0004D       62 1D          MEM_WRITE WORD
            '     [++]ptr := a
            0004F 0004F       E0             VAR_READ LONG DBASE+$00000 (short)
            00050 00050       D2             VAR_SETUP LONG (^WORD) DBASE+$00002 (short)
            00051 00051       80 21          PRE_INC (2) (push)
            00053 00053       62 1D          MEM_WRITE WORD
            '     a := ptr[++] := b
            00055 00055       E1             VAR_READ LONG DBASE+$00001 (short)
            00056 00056       D2             VAR_SETUP LONG (^WORD) DBASE+$00002 (short)
            00057 00057       80 23          POST_INC (2) (push)
            00059 00059       62 1E          MEM_WRITE WORD (push)
            0005B 0005B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [++]ptr := b
            0005C 0005C       E1             VAR_READ LONG DBASE+$00001 (short)
            0005D 0005D       D2             VAR_SETUP LONG (^WORD) DBASE+$00002 (short)
            0005E 0005E       80 21          PRE_INC (2) (push)
            00060 00060       62 1E          MEM_WRITE WORD (push)
            00062 00062       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00063 00063       04             RETURN
            """, compile(text));
    }

    @Test
    void testStructurePointers() throws Exception {
        String text = """
            CON
                STRUCT sPoint(word x, word y, byte c)
            
            PUB main() | a, b, ^sPoint ptr, sPoint pt
            
                a := ptr.x++
                a := ++ptr.x
            
                a := [ptr]
                a := [ptr]++
                a := ++[ptr]
            
                a := ptr[++].x
                a := ptr[++].x--
                a := --ptr[++].x
                a := [++]ptr.x
                a := [++]ptr.x--
                a := --[++]ptr.x
            
                ptr.x := a
                [ptr] := a
            
                ptr[++].x := a
                [++]ptr.x := a
            
                a := ptr[++].x := b
                a := [++]ptr.x := b
            
                pt := ptr
                pt := ptr[++]
                pt := [++]ptr
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       89 00 00 00    End
            ' PUB main() | a, b, ^sPoint ptr, sPoint pt
            00008 00008       05             (stack size)
            '     a := ptr.x++
            00009 00009       E2             VAR_READ LONG (^SPOINT) DBASE+$00002 (short)
            0000A 0000A       6A 08          STRUCT_SETUP WORD POP+$00000 (indexed)
            0000C 0000C       23             POST_INC (push)
            0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ++ptr.x
            0000E 0000E       E2             VAR_READ LONG (^SPOINT) DBASE+$00002 (short)
            0000F 0000F       6A 08          STRUCT_SETUP WORD POP+$00000 (indexed)
            00011 00011       21             PRE_INC (push)
            00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [ptr]
            00013 00013       E2             VAR_READ LONG (^SPOINT) DBASE+$00002 (short)
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [ptr]++
            00015 00015       D2             VAR_SETUP LONG (^SPOINT) DBASE+$00002 (short)
            00016 00016       83 23          POST_INC (5) (push)
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ++[ptr]
            00019 00019       D2             VAR_SETUP LONG (^SPOINT) DBASE+$00002 (short)
            0001A 0001A       83 21          PRE_INC (5) (push)
            0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr[++].x
            0001D 0001D       D2             VAR_SETUP LONG (^SPOINT) DBASE+$00002 (short)
            0001E 0001E       83 23          POST_INC (5) (push)
            00020 00020       6A 08 1C       STRUCT_READ WORD POP+$00000 (indexed)
            00023 00023       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr[++].x--
            00024 00024       D2             VAR_SETUP LONG (^SPOINT) DBASE+$00002 (short)
            00025 00025       83 23          POST_INC (5) (push)
            00027 00027       6A 08          STRUCT_SETUP WORD POP+$00000 (indexed)
            00029 00029       24             POST_DEC (push)
            0002A 0002A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := --ptr[++].x
            0002B 0002B       D2             VAR_SETUP LONG (^SPOINT) DBASE+$00002 (short)
            0002C 0002C       83 23          POST_INC (5) (push)
            0002E 0002E       6A 08          STRUCT_SETUP WORD POP+$00000 (indexed)
            00030 00030       22             PRE_DEC (push)
            00031 00031       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [++]ptr.x
            00032 00032       D2             VAR_SETUP LONG (^SPOINT) DBASE+$00002 (short)
            00033 00033       83 21          PRE_INC (5) (push)
            00035 00035       6A 08 1C       STRUCT_READ WORD POP+$00000 (indexed)
            00038 00038       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [++]ptr.x--
            00039 00039       D2             VAR_SETUP LONG (^SPOINT) DBASE+$00002 (short)
            0003A 0003A       83 21          PRE_INC (5) (push)
            0003C 0003C       6A 08          STRUCT_SETUP WORD POP+$00000 (indexed)
            0003E 0003E       24             POST_DEC (push)
            0003F 0003F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := --[++]ptr.x
            00040 00040       D2             VAR_SETUP LONG (^SPOINT) DBASE+$00002 (short)
            00041 00041       83 21          PRE_INC (5) (push)
            00043 00043       6A 08          STRUCT_SETUP WORD POP+$00000 (indexed)
            00045 00045       22             PRE_DEC (push)
            00046 00046       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr.x := a
            00047 00047       E0             VAR_READ LONG DBASE+$00000 (short)
            00048 00048       E2             VAR_READ LONG (^SPOINT) DBASE+$00002 (short)
            00049 00049       6A 08 1D       STRUCT_WRITE WORD POP+$00000 (indexed)
            '     [ptr] := a
            0004C 0004C       E0             VAR_READ LONG DBASE+$00000 (short)
            0004D 0004D       F2             VAR_WRITE LONG (^SPOINT) DBASE+$00002 (short)
            '     ptr[++].x := a
            0004E 0004E       E0             VAR_READ LONG DBASE+$00000 (short)
            0004F 0004F       D2             VAR_SETUP LONG (^SPOINT) DBASE+$00002 (short)
            00050 00050       83 23          POST_INC (5) (push)
            00052 00052       6A 08 1D       STRUCT_WRITE WORD POP+$00000 (indexed)
            '     [++]ptr.x := a
            00055 00055       E0             VAR_READ LONG DBASE+$00000 (short)
            00056 00056       D2             VAR_SETUP LONG (^SPOINT) DBASE+$00002 (short)
            00057 00057       83 21          PRE_INC (5) (push)
            00059 00059       6A 08 1D       STRUCT_WRITE WORD POP+$00000 (indexed)
            '     a := ptr[++].x := b
            0005C 0005C       E1             VAR_READ LONG DBASE+$00001 (short)
            0005D 0005D       D2             VAR_SETUP LONG (^SPOINT) DBASE+$00002 (short)
            0005E 0005E       83 23          POST_INC (5) (push)
            00060 00060       6A 08          STRUCT_SETUP WORD POP+$00000 (indexed)
            00062 00062       1E             WRITE (push)
            00063 00063       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [++]ptr.x := b
            00064 00064       E1             VAR_READ LONG DBASE+$00001 (short)
            00065 00065       D2             VAR_SETUP LONG (^SPOINT) DBASE+$00002 (short)
            00066 00066       83 21          PRE_INC (5) (push)
            00068 00068       6A 08          STRUCT_SETUP WORD POP+$00000 (indexed)
            0006A 0006A       1E             WRITE (push)
            0006B 0006B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     pt := ptr
            0006C 0006C       E2             VAR_READ LONG (^SPOINT) DBASE+$00002 (short)
            0006D 0006D       6A 00 85       STRUCT_READ LONG POP+$00000 (indexed)
            00070 00070       69 C0 01 05    STRUCT_WRITE LONG DBASE+$0000C (indexed)
            '     pt := ptr[++]
            00074 00074       D2             VAR_SETUP LONG (^SPOINT) DBASE+$00002 (short)
            00075 00075       83 23          POST_INC (5) (push)
            00077 00077       6A 00 85       STRUCT_READ LONG POP+$00000 (indexed)
            0007A 0007A       69 C0 01 05    STRUCT_WRITE LONG DBASE+$0000C (indexed)
            '     pt := [++]ptr
            0007E 0007E       D2             VAR_SETUP LONG (^SPOINT) DBASE+$00002 (short)
            0007F 0007F       83 21          PRE_INC (5) (push)
            00081 00081       6A 00 85       STRUCT_READ LONG POP+$00000 (indexed)
            00084 00084       69 C0 01 05    STRUCT_WRITE LONG DBASE+$0000C (indexed)
            00088 00088       04             RETURN
            00089 00089       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testStructureMethodCall() throws Exception {
        String text = """
            CON
                STRUCT sFunc(long ptr)
            
            VAR
                sFunc tbl
            
            PUB main() | a, b, c
            
                tbl.ptr(a, b)
                tbl[1].ptr(a, b)
                tbl[c].ptr(a, b)
                tbl[c].ptr[b](a, b)
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 8)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       2A 00 00 00    End
            ' PUB main() | a, b, c
            00008 00008       03             (stack size)
            '     tbl.ptr(a, b)
            00009 00009       00             ANCHOR
            0000A 0000A       E0             VAR_READ LONG DBASE+$00000 (short)
            0000B 0000B       E1             VAR_READ LONG DBASE+$00001 (short)
            0000C 0000C       C1 1C          VAR_READ SFUNC VBASE+$00001 (short)
            0000E 0000E       0B             CALL_PTR
            '     tbl[1].ptr(a, b)
            0000F 0000F       00             ANCHOR
            00010 00010       E0             VAR_READ LONG DBASE+$00000 (short)
            00011 00011       E1             VAR_READ LONG DBASE+$00001 (short)
            00012 00012       C2 1C          VAR_READ SFUNC VBASE+$00002 (short)
            00014 00014       0B             CALL_PTR
            '     tbl[c].ptr(a, b)
            00015 00015       00             ANCHOR
            00016 00016       E0             VAR_READ LONG DBASE+$00000 (short)
            00017 00017       E1             VAR_READ LONG DBASE+$00001 (short)
            00018 00018       E2             VAR_READ LONG DBASE+$00002 (short)
            00019 00019       68 4D 04 1C    STRUCT_READ LONG VBASE+$00004 (indexed)
            0001D 0001D       0B             CALL_PTR
            '     tbl[c].ptr[b](a, b)
            0001E 0001E       00             ANCHOR
            0001F 0001F       E0             VAR_READ LONG DBASE+$00000 (short)
            00020 00020       E1             VAR_READ LONG DBASE+$00001 (short)
            00021 00021       E2             VAR_READ LONG DBASE+$00002 (short)
            00022 00022       E1             VAR_READ LONG DBASE+$00001 (short)
            00023 00023       68 4E 04 04 1C STRUCT_READ LONG VBASE+$00004 (indexed)
            00028 00028       0B             CALL_PTR
            00029 00029       04             RETURN
            0002A 0002A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testBytecodeInjection() throws Exception {
        String text = """
            PUB start() | a
            
                bytecode($A2, $F0) ' a := 1
                bytecode($A2, $F0, "a := 1")
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       0E 00 00 00    End
            ' PUB start() | a
            00008 00008       01             (stack size)
            '     bytecode($A2, $F0)
            00009 00009       A2 F0          BYTECODE
            '     bytecode($A2, $F0, "a := 1")
            0000B 0000B       A2 F0          a := 1
            0000D 0000D       04             RETURN
            0000E 0000E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testStructureOperators() throws Exception {
        String text = """
            CON
                sPoint(byte x, word y)
                sLine(sPoint a, sPoint b, byte color)
            
            PUB start() | sLine line, sLine line2
            
                line := line2
                line :=: line2
                if (line == line)
                if (line <> line)
            
                line.a := line.b
                line.a :=: line.b
                if (line.a == line.b)
                if (line.a <> line.b)
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       56 00 00 00    End
            ' PUB start() | sLine line, sLine line2
            00008 00008       04             (stack size)
            '     line := line2
            00009 00009       69 70 87       STRUCT_READ LONG DBASE+$00007 (indexed)
            0000C 0000C       69 00 07       STRUCT_WRITE LONG DBASE+$00000 (indexed)
            '     line :=: line2
            0000F 0000F       69 00 00       STRUCT_ADDRESS LONG DBASE+$00000 (indexed)
            00012 00012       69 70 00       STRUCT_ADDRESS LONG DBASE+$00007 (indexed)
            00015 00015       A8             CONSTANT (7)
            00016 00016       19 6E          BYTESWAP
            '     if (line == line)
            00018 00018       69 00 00       STRUCT_ADDRESS LONG DBASE+$00000 (indexed)
            0001B 0001B       69 00 00       STRUCT_ADDRESS LONG DBASE+$00000 (indexed)
            0001E 0001E       A8             CONSTANT (7)
            0001F 0001F       19 70          BYTECOMP
            00021 00021       13 01          JZ $00023 (1)
            '     if (line <> line)
            00023 00023       69 00 00       STRUCT_ADDRESS LONG DBASE+$00000 (indexed)
            00026 00026       69 00 00       STRUCT_ADDRESS LONG DBASE+$00000 (indexed)
            00029 00029       A8             CONSTANT (7)
            0002A 0002A       19 70          BYTECOMP
            0002C 0002C       77             BOOLEAN_NOT (push)
            0002D 0002D       13 01          JZ $0002F (1)
            '     line.a := line.b
            0002F 0002F       69 30 83       STRUCT_READ LONG DBASE+$00003 (indexed)
            00032 00032       69 00 03       STRUCT_WRITE LONG DBASE+$00000 (indexed)
            '     line.a :=: line.b
            00035 00035       69 00 00       STRUCT_ADDRESS LONG DBASE+$00000 (indexed)
            00038 00038       69 30 00       STRUCT_ADDRESS LONG DBASE+$00003 (indexed)
            0003B 0003B       A4             CONSTANT (3)
            0003C 0003C       19 6E          BYTESWAP
            '     if (line.a == line.b)
            0003E 0003E       69 00 00       STRUCT_ADDRESS LONG DBASE+$00000 (indexed)
            00041 00041       69 30 00       STRUCT_ADDRESS LONG DBASE+$00003 (indexed)
            00044 00044       A4             CONSTANT (3)
            00045 00045       19 70          BYTECOMP
            00047 00047       13 01          JZ $00049 (1)
            '     if (line.a <> line.b)
            00049 00049       69 00 00       STRUCT_ADDRESS LONG DBASE+$00000 (indexed)
            0004C 0004C       69 30 00       STRUCT_ADDRESS LONG DBASE+$00003 (indexed)
            0004F 0004F       A4             CONSTANT (3)
            00050 00050       19 70          BYTECOMP
            00052 00052       77             BOOLEAN_NOT (push)
            00053 00053       13 01          JZ $00055 (1)
            00055 00055       04             RETURN
            00056 00056       00 00          Padding
            """, compile(text));
    }

    @Test
    void testStructureArguments() throws Exception {
        String text = """
            CON
                sPoint(byte x, word y)
                sLine(sPoint a, sPoint b, byte color)
            
            PUB start() | sLine line
            
                line := test(line)
            
            PUB test(sLine line) : sLine r\s
            
                r := line
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method start @ $0000C (0 parameters, 0 returns)
            00004 00004       17 00 20 82    Method test @ $00017 (2 parameters, 2 returns)
            00008 00008       20 00 00 00    End
            ' PUB start() | sLine line
            0000C 0000C       02             (stack size)
            '     line := test(line)
            0000D 0000D       01             ANCHOR (push)
            0000E 0000E       69 00 87       STRUCT_READ LONG DBASE+$00000 (indexed)
            00011 00011       0A 01          CALL_SUB (1)
            00013 00013       69 00 07       STRUCT_WRITE LONG DBASE+$00000 (indexed)
            00016 00016       04             RETURN
            ' PUB test(sLine line) : sLine r
            00017 00017       00             (stack size)
            '     r := line
            00018 00018       69 00 87       STRUCT_READ LONG DBASE+$00000 (indexed)
            0001B 0001B       69 80 01 07    STRUCT_WRITE LONG DBASE+$00008 (indexed)
            0001F 0001F       04             RETURN
            """, compile(text));
    }

    @Test
    void testStructureMemberBitfield() throws Exception {
        String text = """
            CON
                sPoint(byte x, word y)
                sLine(sPoint a, sPoint b, byte color)
            
            VAR
                sPoint point
                ^sPoint pPoint
            
            PUB start() | sLine line, ^sLine pLine, v
            
                line.a.y.[12..0] := v
                v := line.a.y.[12..0]
                pLine.a.y.[12..0] := v
                v := pLine.a.y.[12..0]
            
                point.y.[12..0] := v
                v := point.y.[12..0]
                pPoint.y.[12..0] := v
                v := pPoint.y.[12..0]
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 12)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       56 00 00 00    End
            ' PUB start() | sLine line, ^sLine pLine, v
            00008 00008       04             (stack size)
            '     line.a.y.[12..0] := v
            00009 00009       5D 0B 1C       VAR_READ LONG DBASE+$0000B
            0000C 0000C       57 01          MEM_SETUP WORD DBASE+$00001
            0000E 0000E       7F 80 03       BITFIELD_WRITE
            '     v := line.a.y.[12..0]
            00011 00011       57 01          MEM_SETUP WORD DBASE+$00001
            00013 00013       7E 80 03       BITFIELD_READ
            00016 00016       5D 0B 1D       VAR_WRITE LONG DBASE+$0000B
            '     pLine.a.y.[12..0] := v
            00019 00019       5D 0B 1C       VAR_READ LONG DBASE+$0000B
            0001C 0001C       5D 07 1C       VAR_READ LONG (^SLINE) DBASE+$00007
            0001F 0001F       6A 18          STRUCT_SETUP WORD POP+$00001 (indexed)
            00021 00021       7F 80 03       BITFIELD_WRITE
            '     v := pLine.a.y.[12..0]
            00024 00024       5D 07 1C       VAR_READ LONG (^SLINE) DBASE+$00007
            00027 00027       6A 18          STRUCT_SETUP WORD POP+$00001 (indexed)
            00029 00029       7E 80 03       BITFIELD_READ
            0002C 0002C       5D 0B 1D       VAR_WRITE LONG DBASE+$0000B
            '     point.y.[12..0] := v
            0002F 0002F       5D 0B 1C       VAR_READ LONG DBASE+$0000B
            00032 00032       56 05          MEM_SETUP WORD VBASE+$00005
            00034 00034       7F 80 03       BITFIELD_WRITE
            '     v := point.y.[12..0]
            00037 00037       56 05          MEM_SETUP WORD VBASE+$00005
            00039 00039       7E 80 03       BITFIELD_READ
            0003C 0003C       5D 0B 1D       VAR_WRITE LONG DBASE+$0000B
            '     pPoint.y.[12..0] := v
            0003F 0003F       5D 0B 1C       VAR_READ LONG DBASE+$0000B
            00042 00042       5C 07 1C       VAR_READ LONG (^SPOINT) VBASE+$00007
            00045 00045       6A 18          STRUCT_SETUP WORD POP+$00001 (indexed)
            00047 00047       7F 80 03       BITFIELD_WRITE
            '     v := pPoint.y.[12..0]
            0004A 0004A       5C 07 1C       VAR_READ LONG (^SPOINT) VBASE+$00007
            0004D 0004D       6A 18          STRUCT_SETUP WORD POP+$00001 (indexed)
            0004F 0004F       7E 80 03       BITFIELD_READ
            00052 00052       5D 0B 1D       VAR_WRITE LONG DBASE+$0000B
            00055 00055       04             RETURN
            00056 00056       00 00          Padding
            """, compile(text));
    }

    @Test
    void testPRxRegisterImmediate() throws Exception {
        String text = """
            PUB main(a) | pixout[7]
            
                setregs(@pixout, pr0, 7)
                setregs(@pixout, #pr0, 7)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 81    Method main @ $00008 (1 parameters, 0 returns)
            00004 00004       19 00 00 00    End
            ' PUB main(a) | pixout[7]
            00008 00008       07             (stack size)
            '     setregs(@pixout, pr0, 7)
            00009 00009       D1 1B          VAR_ADDRESS DBASE+$00001 (short)
            0000B 0000B       B0 1C          REG_READ +$1D8 (short)
            0000D 0000D       A8             CONSTANT (7)
            0000E 0000E       19 68          SETREGS
            '     setregs(@pixout, #pr0, 7)
            00010 00010       D1 1B          VAR_ADDRESS DBASE+$00001 (short)
            00012 00012       44 D8 01       CONSTANT ($1d8)
            00015 00015       A8             CONSTANT (7)
            00016 00016       19 68          SETREGS
            00018 00018       04             RETURN
            00019 00019       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testRegistersInConstantExpressions() throws Exception {
        String text = """
            CON
               A = DIRA
            
            PUB main() | v
              v := A
              v := DIRA
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       11 00 00 00    End
            ' PUB main() | v
            00008 00008       01             (stack size)
            '   v := A
            00009 00009       44 FA 01       CONSTANT ($1fa)
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   v := DIRA
            0000D 0000D       BA 1C          REG_READ +$1FA (short)
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00010 00010       04             RETURN
            00011 00011       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testConstantValidation() throws Exception {
        String text = """
            CON
            
                PIN = 1
                DELAY = 1_000 / MS
            
            PUB main()
            
            """;

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile(text);
            }
        });
    }

    @Test
    void testPointerArguments() throws Exception {
        String text = """
            CON
                struct point(word x, word y, byte c)
            
            PUB main() | ^word wptr, ^point sptr
            
                method_1(wptr)
                method_1([++]wptr)
                method_1(wptr[++])
                method_1(--[++]wptr)
                method_1(wptr[++]--)
            
                method_s(sptr)
                method_s([++]sptr)
                method_s(sptr[++])
                method_1(--[++]sptr.x)
                method_1(sptr[++].x--)
            
            PRI method_1(p)
            
            PRI method_s(point p)
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            00004 00004       63 00 00 81    Method method_1 @ $00063 (1 parameters, 0 returns)
            00008 00008       65 00 00 82    Method method_s @ $00065 (2 parameters, 0 returns)
            0000C 0000C       67 00 00 00    End
            ' PUB main() | ^word wptr, ^point sptr
            00010 00010       02             (stack size)
            '     method_1(wptr)
            00011 00011       00             ANCHOR
            00012 00012       E0             VAR_READ LONG (^WORD) DBASE+$00000 (short)
            00013 00013       62 1C          MEM_READ WORD
            00015 00015       0A 01          CALL_SUB (1)
            '     method_1([++]wptr)
            00017 00017       00             ANCHOR
            00018 00018       D0             VAR_SETUP LONG (^WORD) DBASE+$00000 (short)
            00019 00019       80 21          PRE_INC (2) (push)
            0001B 0001B       62 1C          MEM_READ WORD
            0001D 0001D       0A 01          CALL_SUB (1)
            '     method_1(wptr[++])
            0001F 0001F       00             ANCHOR
            00020 00020       D0             VAR_SETUP LONG (^WORD) DBASE+$00000 (short)
            00021 00021       80 23          POST_INC (2) (push)
            00023 00023       62 1C          MEM_READ WORD
            00025 00025       0A 01          CALL_SUB (1)
            '     method_1(--[++]wptr)
            00027 00027       00             ANCHOR
            00028 00028       D0             VAR_SETUP LONG (^WORD) DBASE+$00000 (short)
            00029 00029       80 21          PRE_INC (2) (push)
            0002B 0002B       62             MEM_SETUP WORD
            0002C 0002C       22             PRE_DEC (push)
            0002D 0002D       0A 01          CALL_SUB (1)
            '     method_1(wptr[++]--)
            0002F 0002F       00             ANCHOR
            00030 00030       D0             VAR_SETUP LONG (^WORD) DBASE+$00000 (short)
            00031 00031       80 23          POST_INC (2) (push)
            00033 00033       62             MEM_SETUP WORD
            00034 00034       24             POST_DEC (push)
            00035 00035       0A 01          CALL_SUB (1)
            '     method_s(sptr)
            00037 00037       00             ANCHOR
            00038 00038       E1             VAR_READ LONG (^POINT) DBASE+$00001 (short)
            00039 00039       6A 00 85       STRUCT_READ LONG POP+$00000 (indexed)
            0003C 0003C       0A 02          CALL_SUB (2)
            '     method_s([++]sptr)
            0003E 0003E       00             ANCHOR
            0003F 0003F       D1             VAR_SETUP LONG (^POINT) DBASE+$00001 (short)
            00040 00040       83 21          PRE_INC (5) (push)
            00042 00042       6A 00 85       STRUCT_READ LONG POP+$00000 (indexed)
            00045 00045       0A 02          CALL_SUB (2)
            '     method_s(sptr[++])
            00047 00047       00             ANCHOR
            00048 00048       D1             VAR_SETUP LONG (^POINT) DBASE+$00001 (short)
            00049 00049       83 23          POST_INC (5) (push)
            0004B 0004B       6A 00 85       STRUCT_READ LONG POP+$00000 (indexed)
            0004E 0004E       0A 02          CALL_SUB (2)
            '     method_1(--[++]sptr.x)
            00050 00050       00             ANCHOR
            00051 00051       D1             VAR_SETUP LONG (^POINT) DBASE+$00001 (short)
            00052 00052       83 21          PRE_INC (5) (push)
            00054 00054       6A 08          STRUCT_SETUP WORD POP+$00000 (indexed)
            00056 00056       22             PRE_DEC (push)
            00057 00057       0A 01          CALL_SUB (1)
            '     method_1(sptr[++].x--)
            00059 00059       00             ANCHOR
            0005A 0005A       D1             VAR_SETUP LONG (^POINT) DBASE+$00001 (short)
            0005B 0005B       83 23          POST_INC (5) (push)
            0005D 0005D       6A 08          STRUCT_SETUP WORD POP+$00000 (indexed)
            0005F 0005F       24             POST_DEC (push)
            00060 00060       0A 01          CALL_SUB (1)
            00062 00062       04             RETURN
            ' PRI method_1(p)
            00063 00063       00             (stack size)
            00064 00064       04             RETURN
            ' PRI method_s(point p)
            00065 00065       00             (stack size)
            00066 00066       04             RETURN
            00067 00067       00             Padding
            """, compile(text));
    }

    @Test
    void testDebugStructure() throws Exception {
        String text = """
            CON
                struct point(word x, word y, byte c)
            
            PUB main() | point a
            
                debug(udec(a))
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       10 00 00 00    End
            ' PUB main() | point a
            00008 00008       02             (stack size)
            '     debug(udec(a))
            00009 00009       69 00 85       STRUCT_READ LONG DBASE+$00000 (indexed)
            0000C 0000C       41 08 01       DEBUG #1
            0000F 0000F       04             RETURN
            ' Debug data
            00B74 00000       09 00        \s
            00B76 00002       04 00          #1@0004
            ' #1
            00B78 00004       04             COGN
            00B79 00005       41 61 00       UDEC(a)
            00B7C 00008       00             DONE
            """, compile(text, true));
    }

    @Test
    void testDebugBitMask() throws Exception {
        String text = """
            DEBUG_MASK = %00000010
            
            PUB main() | a, b
            
                debug[0](udec(a))
                debug[1](udec(b))
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0E 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     debug[0](udec(a))
            '     debug[1](udec(b))
            00009 00009       E1             VAR_READ LONG DBASE+$00001 (short)
            0000A 0000A       41 04 01       DEBUG #1
            0000D 0000D       04             RETURN
            0000E 0000E       00 00          Padding
            ' Debug data
            00B74 00000       09 00        \s
            00B76 00002       04 00          #1@0004
            ' #1
            00B78 00004       04             COGN
            00B79 00005       41 62 00       UDEC(b)
            00B7C 00008       00             DONE
            """, compile(text, true));
    }

    @Test
    void testPointerVariablesAddress() throws Exception {
        String text = """
            {Spin2_v51}
            CON
                STRUCT sPoint(word x, word y, byte c)
            
            VAR
                word   wrd
                sPoint pt
            
            PUB main() | a, ^sPoint ptr, ^word wptr
            
                a := @wrd
                a := @@wrd
                a := @pt
                'a := @@pt
            
                a := @[wptr]
                a := @@[wptr]
                a := byte[@wptr][1]
                a := byte[@[wptr]][1]
            
                a := @[ptr]
                a := @@[ptr]
                a := byte[@ptr][1]
                a := byte[@[ptr]][1]
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 12)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       3E 00 00 00    End
            ' PUB main() | a, ^sPoint ptr, ^word wptr
            00008 00008       03             (stack size)
            '     a := @wrd
            00009 00009       56 04 1B       VAR_ADDRESS VBASE+$00004
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := @@wrd
            0000D 0000D       56 04 1C       VAR_READ WORD VBASE+$00004
            00010 00010       24             ADD PBASE
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := @pt
            00012 00012       68 60 00       STRUCT_ADDRESS LONG VBASE+$00006 (indexed)
            00015 00015       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := @[wptr]
            00016 00016       D2 1B          VAR_ADDRESS DBASE+$00002 (short)
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := @@[wptr]
            00019 00019       E2             VAR_READ LONG (^WORD) DBASE+$00002 (short)
            0001A 0001A       24             ADD PBASE
            0001B 0001B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := byte[@wptr][1]
            0001C 0001C       E2             VAR_READ LONG (^WORD) DBASE+$00002 (short)
            0001D 0001D       62 1B          MEM_ADDRESS
            0001F 0001F       A2             CONSTANT (1)
            00020 00020       64 1C          MEM_READ BYTE INDEXED
            00022 00022       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := byte[@[wptr]][1]
            00023 00023       D2 1B          VAR_ADDRESS DBASE+$00002 (short)
            00025 00025       A2             CONSTANT (1)
            00026 00026       64 1C          MEM_READ BYTE INDEXED
            00028 00028       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := @[ptr]
            00029 00029       D1 1B          VAR_ADDRESS DBASE+$00001 (short)
            0002B 0002B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := @@[ptr]
            0002C 0002C       E1             VAR_READ LONG (^SPOINT) DBASE+$00001 (short)
            0002D 0002D       24             ADD PBASE
            0002E 0002E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := byte[@ptr][1]
            0002F 0002F       E1             VAR_READ LONG (^SPOINT) DBASE+$00001 (short)
            00030 00030       6A 00 00       STRUCT_ADDRESS LONG POP+$00000 (indexed)
            00033 00033       A2             CONSTANT (1)
            00034 00034       64 1C          MEM_READ BYTE INDEXED
            00036 00036       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := byte[@[ptr]][1]
            00037 00037       D1 1B          VAR_ADDRESS DBASE+$00001 (short)
            00039 00039       A2             CONSTANT (1)
            0003A 0003A       64 1C          MEM_READ BYTE INDEXED
            0003C 0003C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0003D 0003D       04             RETURN
            0003E 0003E       00 00          Padding
            ' Debug data
            00B74 00000       02 00        \s
            """, compile(text, true));
    }

    @Test
    void testPointerOperationInConditionals() throws Exception {
        String text = """
            {Spin2_v51}
            CON
                STRUCT sPoint(word x, word y, byte c)
            
            PUB main() | a, ^sPoint sptr, ^word wptr
            
                if [sptr] := a
                if [sptr] += a
            
                if [wptr] := a
                if [wptr] += a
            
                if sptr.x := a
                if sptr.y += a
            
                if wptr := a
                if wptr += a
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       38 00 00 00    End
            ' PUB main() | a, ^sPoint sptr, ^word wptr
            00008 00008       03             (stack size)
            '     if [sptr] := a
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       D1             VAR_SETUP LONG (^SPOINT) DBASE+$00001 (short)
            0000B 0000B       1E             WRITE (push)
            0000C 0000C       13 01          JZ $0000E (1)
            '     if [sptr] += a
            0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)
            0000F 0000F       D1             VAR_SETUP LONG (^SPOINT) DBASE+$00001 (short)
            00010 00010       66             ADD_ASSIGN (push)
            00011 00011       13 01          JZ $00013 (1)
            '     if [wptr] := a
            00013 00013       E0             VAR_READ LONG DBASE+$00000 (short)
            00014 00014       D2             VAR_SETUP LONG (^WORD) DBASE+$00002 (short)
            00015 00015       1E             WRITE (push)
            00016 00016       13 01          JZ $00018 (1)
            '     if [wptr] += a
            00018 00018       E0             VAR_READ LONG DBASE+$00000 (short)
            00019 00019       D2             VAR_SETUP LONG (^WORD) DBASE+$00002 (short)
            0001A 0001A       66             ADD_ASSIGN (push)
            0001B 0001B       13 01          JZ $0001D (1)
            '     if sptr.x := a
            0001D 0001D       E0             VAR_READ LONG DBASE+$00000 (short)
            0001E 0001E       E1             VAR_READ LONG (^SPOINT) DBASE+$00001 (short)
            0001F 0001F       6A 08          STRUCT_SETUP WORD POP+$00000 (indexed)
            00021 00021       1E             WRITE (push)
            00022 00022       13 01          JZ $00024 (1)
            '     if sptr.y += a
            00024 00024       E0             VAR_READ LONG DBASE+$00000 (short)
            00025 00025       E1             VAR_READ LONG (^SPOINT) DBASE+$00001 (short)
            00026 00026       6A 28          STRUCT_SETUP WORD POP+$00002 (indexed)
            00028 00028       66             ADD_ASSIGN (push)
            00029 00029       13 01          JZ $0002B (1)
            '     if wptr := a
            0002B 0002B       E0             VAR_READ LONG DBASE+$00000 (short)
            0002C 0002C       E2             VAR_READ LONG (^WORD) DBASE+$00002 (short)
            0002D 0002D       62 1E          MEM_WRITE WORD (push)
            0002F 0002F       13 01          JZ $00031 (1)
            '     if wptr += a
            00031 00031       E0             VAR_READ LONG DBASE+$00000 (short)
            00032 00032       E2             VAR_READ LONG (^WORD) DBASE+$00002 (short)
            00033 00033       62             MEM_SETUP WORD
            00034 00034       66             ADD_ASSIGN (push)
            00035 00035       13 01          JZ $00037 (1)
            00037 00037       04             RETURN
            """, compile(text, false));
    }

    @Test
    void testCogspin() throws Exception {
        String text = """
            PUB main() | a, ptr
            
                cogspin(NEWCOG, cogFunction(), 0)
                cogspin(NEWCOG, ptr(), 0)
                coginit(NEWCOG, @start, 0)
                coginit(NEWCOG, ptr, 0)
            
                a := cogspin(NEWCOG, cogFunction(), 0)
                a := cogspin(NEWCOG, ptr(), 0)
                a := coginit(NEWCOG, @start, 0)
                a := coginit(NEWCOG, ptr, 0)
            
            PRI cogFunction()
            
            DAT org $000
            start
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       4C 00 00 80    Method cogFunction @ $0004C (0 parameters, 0 returns)
            00008 00008       4E 00 00 00    End
            0000C 0000C   000                                    org     $000
            0000C 0000C   000                start              \s
            ' PUB main() | a, ptr
            0000C 0000C       02             (stack size)
            '     cogspin(NEWCOG, cogFunction(), 0)
            0000D 0000D       42 10          CONSTANT (16)
            0000F 0000F       11 01          SUB_ADDRESS (1)
            00011 00011       A1             CONSTANT (0)
            00012 00012       19 58 00 25    COGSPIN
            '     cogspin(NEWCOG, ptr(), 0)
            00016 00016       42 10          CONSTANT (16)
            00018 00018       E1             VAR_READ LONG DBASE+$00001 (short)
            00019 00019       A1             CONSTANT (0)
            0001A 0001A       19 58 00 25    COGSPIN
            '     coginit(NEWCOG, @start, 0)
            0001E 0001E       42 10          CONSTANT (16)
            00020 00020       5B 0C 1B       MEM_ADDRESS PBASE+$0000C
            00023 00023       A1             CONSTANT (0)
            00024 00024       25             COGINIT
            '     coginit(NEWCOG, ptr, 0)
            00025 00025       42 10          CONSTANT (16)
            00027 00027       E1             VAR_READ LONG DBASE+$00001 (short)
            00028 00028       A1             CONSTANT (0)
            00029 00029       25             COGINIT
            '     a := cogspin(NEWCOG, cogFunction(), 0)
            0002A 0002A       42 10          CONSTANT (16)
            0002C 0002C       11 01          SUB_ADDRESS (1)
            0002E 0002E       A1             CONSTANT (0)
            0002F 0002F       19 58 00 26    COGSPIN
            00033 00033       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := cogspin(NEWCOG, ptr(), 0)
            00034 00034       42 10          CONSTANT (16)
            00036 00036       E1             VAR_READ LONG DBASE+$00001 (short)
            00037 00037       A1             CONSTANT (0)
            00038 00038       19 58 00 26    COGSPIN
            0003C 0003C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := coginit(NEWCOG, @start, 0)
            0003D 0003D       42 10          CONSTANT (16)
            0003F 0003F       5B 0C 1B       MEM_ADDRESS PBASE+$0000C
            00042 00042       A1             CONSTANT (0)
            00043 00043       26             COGINIT
            00044 00044       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := coginit(NEWCOG, ptr, 0)
            00045 00045       42 10          CONSTANT (16)
            00047 00047       E1             VAR_READ LONG DBASE+$00001 (short)
            00048 00048       A1             CONSTANT (0)
            00049 00049       26             COGINIT
            0004A 0004A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0004B 0004B       04             RETURN
            ' PRI cogFunction()
            0004C 0004C       00             (stack size)
            0004D 0004D       04             RETURN
            0004E 0004E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testConstantAddress() throws Exception {
        String text = """
            PUB main() | a
            
                BYTE[@c] := 1
                BYTE[@@c] := 2
                BYTE[@@@c] := 3
            
                a := BYTE[@c]
                a := BYTE[@@c]
                a := BYTE[@@@c]
            
                BYTE[@@@c + a] := 3
                a := BYTE[@@@c + a]
            
            DAT
            c       long    0
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       3C 00 00 00    End
            00008 00008 00000 00 00 00 00    c                   long    0
            ' PUB main() | a
            0000C 0000C       01             (stack size)
            '     BYTE[@c] := 1
            0000D 0000D       A2             CONSTANT (1)
            0000E 0000E       5B 08 1B       MEM_ADDRESS PBASE+$00008
            00011 00011       61 1D          MEM_WRITE BYTE
            '     BYTE[@@c] := 2
            00013 00013       A3             CONSTANT (2)
            00014 00014       5B 08 1C       MEM_READ LONG PBASE+$00008
            00017 00017       24             ADD_PBASE
            00018 00018       61 1D          MEM_WRITE BYTE
            '     BYTE[@@@c] := 3
            0001A 0001A       A4             CONSTANT (3)
            0001B 0001B       A9             CONSTANT ($00008)
            0001C 0001C       61 1D          MEM_WRITE BYTE
            '     a := BYTE[@c]
            0001E 0001E       5B 08 1B       MEM_ADDRESS PBASE+$00008
            00021 00021       61 1C          MEM_READ BYTE
            00023 00023       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := BYTE[@@c]
            00024 00024       5B 08 1C       MEM_READ LONG PBASE+$00008
            00027 00027       24             ADD_PBASE
            00028 00028       61 1C          MEM_READ BYTE
            0002A 0002A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := BYTE[@@@c]
            0002B 0002B       A9             CONSTANT ($00008)
            0002C 0002C       61 1C          MEM_READ BYTE
            0002E 0002E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     BYTE[@@@c + a] := 3
            0002F 0002F       A4             CONSTANT (3)
            00030 00030       A9             CONSTANT ($00008)
            00031 00031       E0             VAR_READ LONG DBASE+$00000 (short)
            00032 00032       8A             ADD
            00033 00033       61 1D          MEM_WRITE BYTE
            '     a := BYTE[@@@c + a]
            00035 00035       A9             CONSTANT ($00008)
            00036 00036       E0             VAR_READ LONG DBASE+$00000 (short)
            00037 00037       8A             ADD
            00038 00038       61 1C          MEM_READ BYTE
            0003A 0003A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0003B 0003B       04             RETURN
            """, compile(text));
    }

    @Test
    void testQuitLevel() throws Exception {
        String text = """
            PUB main() | a, b
            
                repeat b
                    repeat while a < 1
                        if a == 1
                            quit 1
                    if a == 1
                        quit
                    a := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       26 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     repeat b
            00009 00009       E1             VAR_READ LONG DBASE+$00001 (short)
            0000A 0000A       15 1A          TJZ $00025 (26)
            '         repeat while a < 1
            0000C 0000C       E0             VAR_READ LONG DBASE+$00000 (short)
            0000D 0000D       A2             CONSTANT (1)
            0000E 0000E       6C             LESS_THAN
            0000F 0000F       13 0A          JZ $0001A (10)
            '             if a == 1
            00011 00011       E0             VAR_READ LONG DBASE+$00000 (short)
            00012 00012       A2             CONSTANT (1)
            00013 00013       70             EQUAL
            00014 00014       13 03          JZ $00018 (3)
            '                 quit 1
            00016 00016       14 0E          JNZ $00025 (14)
            00018 00018       12 73          JMP $0000C (-13)
            '         if a == 1
            0001A 0001A       E0             VAR_READ LONG DBASE+$00000 (short)
            0001B 0001B       A2             CONSTANT (1)
            0001C 0001C       70             EQUAL
            0001D 0001D       13 03          JZ $00021 (3)
            '             quit
            0001F 0001F       14 05          JNZ $00025 (5)
            '         a := 1
            00021 00021       A2             CONSTANT (1)
            00022 00022       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00023 00023       16 68          DJNZ $0000C (-24)
            00025 00025       04             RETURN
            00026 00026       00 00          Padding
            """, compile(text));
    }

    @Test
    void testNextLevel() throws Exception {
        String text = """
            PUB main() | a, b
            
                repeat b
                    repeat while a < 1
                        if a == 1
                            next 1
                    if a == 1
                        next
                    a := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       26 00 00 00    End
            ' PUB main() | a, b
            00008 00008       02             (stack size)
            '     repeat b
            00009 00009       E1             VAR_READ LONG DBASE+$00001 (short)
            0000A 0000A       15 1A          TJZ $00025 (26)
            '         repeat while a < 1
            0000C 0000C       E0             VAR_READ LONG DBASE+$00000 (short)
            0000D 0000D       A2             CONSTANT (1)
            0000E 0000E       6C             LESS_THAN
            0000F 0000F       13 0A          JZ $0001A (10)
            '             if a == 1
            00011 00011       E0             VAR_READ LONG DBASE+$00000 (short)
            00012 00012       A2             CONSTANT (1)
            00013 00013       70             EQUAL
            00014 00014       13 03          JZ $00018 (3)
            '                 next 1
            00016 00016       12 0C          JMP $00023 (12)
            00018 00018       12 73          JMP $0000C (-13)
            '         if a == 1
            0001A 0001A       E0             VAR_READ LONG DBASE+$00000 (short)
            0001B 0001B       A2             CONSTANT (1)
            0001C 0001C       70             EQUAL
            0001D 0001D       13 03          JZ $00021 (3)
            '             next
            0001F 0001F       12 03          JMP $00023 (3)
            '         a := 1
            00021 00021       A2             CONSTANT (1)
            00022 00022       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00023 00023       16 68          DJNZ $0000C (-24)
            00025 00025       04             RETURN
            00026 00026       00 00          Padding
            """, compile(text));
    }

    @Test
    void testPointersFlexibility() throws Exception {
        String text = """
            PUB main() | a, b, c, ^WORD ptr
            
                a := ptr.byte
                a := ptr.byte[b]
                a := ptr.long.[0..8]
                a := ptr.long[b].[0..8]
            
                ptr.byte := a
                ptr.byte[b] := a
                ptr.long.[0..8] := a
                ptr.long[b].[0..8] := a
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       36 00 00 00    End
            ' PUB main() | a, b, c, ^WORD ptr
            00008 00008       04             (stack size)
            '     a := ptr.byte
            00009 00009       E3             VAR_READ LONG (^WORD) DBASE+$00003 (short)
            0000A 0000A       61 1C          MEM_READ BYTE
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr.byte[b]
            0000D 0000D       E3             VAR_READ LONG (^WORD) DBASE+$00003 (short)
            0000E 0000E       E1             VAR_READ LONG DBASE+$00001 (short)
            0000F 0000F       64 1C          MEM_READ BYTE INDEXED
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr.long.[0..8]
            00012 00012       E3             VAR_READ LONG (^WORD) DBASE+$00003 (short)
            00013 00013       63             MEM_SETUP LONG
            00014 00014       7E 88 06       BITFIELD_READ
            00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr.long[b].[0..8]
            00018 00018       E3             VAR_READ LONG (^WORD) DBASE+$00003 (short)
            00019 00019       E1             VAR_READ LONG DBASE+$00001 (short)
            0001A 0001A       66             MEM_SETUP LONG INDEXED
            0001B 0001B       7E 88 06       BITFIELD_READ
            0001E 0001E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr.byte := a
            0001F 0001F       E0             VAR_READ LONG DBASE+$00000 (short)
            00020 00020       E3             VAR_READ LONG (^WORD) DBASE+$00003 (short)
            00021 00021       61 1D          MEM_WRITE BYTE
            '     ptr.byte[b] := a
            00023 00023       E0             VAR_READ LONG DBASE+$00000 (short)
            00024 00024       E3             VAR_READ LONG (^WORD) DBASE+$00003 (short)
            00025 00025       E1             VAR_READ LONG DBASE+$00001 (short)
            00026 00026       64 1D          MEM_WRITE BYTE INDEXED
            '     ptr.long.[0..8] := a
            00028 00028       E0             VAR_READ LONG DBASE+$00000 (short)
            00029 00029       E3             VAR_READ LONG (^WORD) DBASE+$00003 (short)
            0002A 0002A       63             MEM_SETUP LONG
            0002B 0002B       7F 88 06       BITFIELD_WRITE
            '     ptr.long[b].[0..8] := a
            0002E 0002E       E0             VAR_READ LONG DBASE+$00000 (short)
            0002F 0002F       E3             VAR_READ LONG (^WORD) DBASE+$00003 (short)
            00030 00030       E1             VAR_READ LONG DBASE+$00001 (short)
            00031 00031       66             MEM_SETUP LONG INDEXED
            00032 00032       7F 88 06       BITFIELD_WRITE
            00035 00035       04             RETURN
            00036 00036       00 00          Padding
            """, compile(text));
    }

    @Test
    void testChainedPointersFlexibility() throws Exception {
        String text = """
            PUB main() | a, b, c, ^WORD ptr
            
                c := ptr.byte := a
                c := ptr.byte[b] := a
                c := ptr.long.[0..8] := a
                c := ptr.long[b].[0..8] := a
            
                c := [++]ptr.byte := a
                c := [++]ptr.byte[b] := a
                c := [++]ptr.long.[0..8] := a
                c := [++]ptr.long[b].[0..8] := a
            
                c := ptr[--].byte := a
                c := ptr[--].byte[b] := a
                c := ptr[--].long.[0..8] := a
                c := ptr[--].long[b].[0..8] := a
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       5E 00 00 00    End
            ' PUB main() | a, b, c, ^WORD ptr
            00008 00008       04             (stack size)
            '     c := ptr.byte := a
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       E3             VAR_READ LONG (^WORD) DBASE+$00003 (short)
            0000B 0000B       61 1E          MEM_WRITE BYTE (push)
            0000D 0000D       F2             VAR_WRITE LONG DBASE+$00002 (short)
            '     c := ptr.byte[b] := a
            0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)
            0000F 0000F       E3             VAR_READ LONG (^WORD) DBASE+$00003 (short)
            00010 00010       E1             VAR_READ LONG DBASE+$00001 (short)
            00011 00011       64 1E          MEM_WRITE BYTE INDEXED (push)
            00013 00013       F2             VAR_WRITE LONG DBASE+$00002 (short)
            '     c := ptr.long.[0..8] := a
            00014 00014       E0             VAR_READ LONG DBASE+$00000 (short)
            00015 00015       E3             VAR_READ LONG (^WORD) DBASE+$00003 (short)
            00016 00016       63             MEM_SETUP LONG
            00017 00017       7F 88 06       BITFIELD_WRITE
            0001A 0001A       F2             VAR_WRITE LONG DBASE+$00002 (short)
            '     c := ptr.long[b].[0..8] := a
            0001B 0001B       E0             VAR_READ LONG DBASE+$00000 (short)
            0001C 0001C       E3             VAR_READ LONG (^WORD) DBASE+$00003 (short)
            0001D 0001D       E1             VAR_READ LONG DBASE+$00001 (short)
            0001E 0001E       66             MEM_SETUP LONG INDEXED
            0001F 0001F       7F 88 06       BITFIELD_WRITE
            00022 00022       F2             VAR_WRITE LONG DBASE+$00002 (short)
            '     c := [++]ptr.byte := a
            00023 00023       E0             VAR_READ LONG DBASE+$00000 (short)
            00024 00024       D3             VAR_SETUP LONG (^WORD) DBASE+$00003 (short)
            00025 00025       21             PRE_INC (1) (push)
            00026 00026       61 1E          MEM_WRITE BYTE (push)
            00028 00028       F2             VAR_WRITE LONG DBASE+$00002 (short)
            '     c := [++]ptr.byte[b] := a
            00029 00029       E0             VAR_READ LONG DBASE+$00000 (short)
            0002A 0002A       D3             VAR_SETUP LONG (^WORD) DBASE+$00003 (short)
            0002B 0002B       21             PRE_INC (1) (push)
            0002C 0002C       E1             VAR_READ LONG DBASE+$00001 (short)
            0002D 0002D       64 1E          MEM_WRITE BYTE INDEXED (push)
            0002F 0002F       F2             VAR_WRITE LONG DBASE+$00002 (short)
            '     c := [++]ptr.long.[0..8] := a
            00030 00030       E0             VAR_READ LONG DBASE+$00000 (short)
            00031 00031       D3             VAR_SETUP LONG (^WORD) DBASE+$00003 (short)
            00032 00032       82 21          PRE_INC (4) (push)
            00034 00034       63             MEM_SETUP LONG
            00035 00035       7F 88 06       BITFIELD_WRITE
            00038 00038       F2             VAR_WRITE LONG DBASE+$00002 (short)
            '     c := [++]ptr.long[b].[0..8] := a
            00039 00039       E0             VAR_READ LONG DBASE+$00000 (short)
            0003A 0003A       D3             VAR_SETUP LONG (^WORD) DBASE+$00003 (short)
            0003B 0003B       82 21          PRE_INC (4) (push)
            0003D 0003D       E1             VAR_READ LONG DBASE+$00001 (short)
            0003E 0003E       66             MEM_SETUP LONG INDEXED
            0003F 0003F       7F 88 06       BITFIELD_WRITE
            00042 00042       F2             VAR_WRITE LONG DBASE+$00002 (short)
            '     c := ptr[--].byte := a
            00043 00043       E0             VAR_READ LONG DBASE+$00000 (short)
            00044 00044       D3             VAR_SETUP LONG (^WORD) DBASE+$00003 (short)
            00045 00045       24             POST_DEC (1) (push)
            00046 00046       61 1E          MEM_WRITE BYTE (push)
            00048 00048       F2             VAR_WRITE LONG DBASE+$00002 (short)
            '     c := ptr[--].byte[b] := a
            00049 00049       E0             VAR_READ LONG DBASE+$00000 (short)
            0004A 0004A       D3             VAR_SETUP LONG (^WORD) DBASE+$00003 (short)
            0004B 0004B       24             POST_DEC (1) (push)
            0004C 0004C       61 1E          MEM_WRITE BYTE (push)
            0004E 0004E       F2             VAR_WRITE LONG DBASE+$00002 (short)
            '     c := ptr[--].long.[0..8] := a
            0004F 0004F       E0             VAR_READ LONG DBASE+$00000 (short)
            00050 00050       D3             VAR_SETUP LONG (^WORD) DBASE+$00003 (short)
            00051 00051       82 24          POST_DEC (4) (push)
            00053 00053       63 1E          MEM_WRITE LONG (push)
            00055 00055       F2             VAR_WRITE LONG DBASE+$00002 (short)
            '     c := ptr[--].long[b].[0..8] := a
            00056 00056       E0             VAR_READ LONG DBASE+$00000 (short)
            00057 00057       D3             VAR_SETUP LONG (^WORD) DBASE+$00003 (short)
            00058 00058       82 24          POST_DEC (4) (push)
            0005A 0005A       63 1E          MEM_WRITE LONG (push)
            0005C 0005C       F2             VAR_WRITE LONG DBASE+$00002 (short)
            0005D 0005D       04             RETURN
            0005E 0005E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testStructureVariableAligment() throws Exception {
        String text = """
            CON
                sTest(word a, long c)
            
            VAR
                sTest v1
                sTest v2
            
            PUB start() | a
            
                v1.c := 1
                v2.c := 2
            
                a := v1.c
                a := v2.c
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 16)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       1A 00 00 00    End
            ' PUB start() | a
            00008 00008       01             (stack size)
            '     v1.c := 1
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       5C 06 1D       MEM_WRITE LONG VBASE+$00006
            '     v2.c := 2
            0000D 0000D       A3             CONSTANT (2)
            0000E 0000E       5C 0C 1D       MEM_WRITE LONG VBASE+$0000C
            '     a := v1.c
            00011 00011       5C 06 1C       MEM_READ LONG VBASE+$00006
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := v2.c
            00015 00015       5C 0C 1C       MEM_READ LONG VBASE+$0000C
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00019 00019       04             RETURN
            0001A 0001A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testStructureBitfield() throws Exception {
        String text = """
            CON
                STRUCT flags_t(LONG flags .ready[0].error[1].busy[2].done[31])
                STRUCT mixed_t(LONG flags .mode[1..0].count[15..8].high[31..24])
            
            VAR
                flags_t  vf
                mixed_t  vm
            
            PUB start() | a, b
            
                vf.flags := a
                vf.flags.ready := a
                vf.flags.error := a
                vf.flags.busy  := a
                vf.flags.done  := a
            
                vm.flags := 0
                vm.flags.mode  := %11
                vm.flags.count := $A5
                vm.flags.high  := $7E
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 12)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       2C 00 00 00    End
            ' PUB start() | a, b
            00008 00008       02             (stack size)
            '     vf.flags := a
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       C1 1D          VAR_WRITE FLAGS_T VBASE+$00001 (short)
            '     vf.flags.ready := a
            0000C 0000C       E0             VAR_READ LONG DBASE+$00000 (short)
            0000D 0000D       C1             VAR_SETUP FLAGS_T VBASE+$00001 (short)
            0000E 0000E       E0             BITFIELD_WRITE (short)
            '     vf.flags.error := a
            0000F 0000F       E0             VAR_READ LONG DBASE+$00000 (short)
            00010 00010       C1             VAR_SETUP FLAGS_T VBASE+$00001 (short)
            00011 00011       E1             BITFIELD_WRITE (short)
            '     vf.flags.busy  := a
            00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)
            00013 00013       C1             VAR_SETUP FLAGS_T VBASE+$00001 (short)
            00014 00014       E2             BITFIELD_WRITE (short)
            '     vf.flags.done  := a
            00015 00015       E0             VAR_READ LONG DBASE+$00000 (short)
            00016 00016       C1             VAR_SETUP FLAGS_T VBASE+$00001 (short)
            00017 00017       FF             BITFIELD_WRITE (short)
            '     vm.flags := 0
            00018 00018       A1             CONSTANT (0)
            00019 00019       C2 1D          VAR_WRITE MIXED_T VBASE+$00002 (short)
            '     vm.flags.mode  := %11
            0001B 0001B       A4             CONSTANT (%11)
            0001C 0001C       C2             VAR_SETUP MIXED_T VBASE+$00002 (short)
            0001D 0001D       7F 20          BITFIELD_WRITE
            '     vm.flags.count := $A5
            0001F 0001F       42 A5          CONSTANT ($A5)
            00021 00021       C2             VAR_SETUP MIXED_T VBASE+$00002 (short)
            00022 00022       7F E8 01       BITFIELD_WRITE
            '     vm.flags.high  := $7E
            00025 00025       42 7E          CONSTANT ($7E)
            00027 00027       C2             VAR_SETUP MIXED_T VBASE+$00002 (short)
            00028 00028       7F F8 01       BITFIELD_WRITE
            0002B 0002B       04             RETURN
            """, compile(text));
    }

    @Test
    void testStructureBitfieldPostEffects() throws Exception {
        String text = """
            CON
                STRUCT mixed_t(LONG flags .mode[1..0].count[15..8].high[31..24])
            
            VAR
                mixed_t  vm
            
            PUB start() | a, b
            
                vm.flags.mode~
                vm.flags.count~~
                vm.flags.high++
            
                a := vm.flags.mode~
                a := vm.flags.count~~
                a := vm.flags.high++
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 8)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       31 00 00 00    End
            ' PUB start() | a, b
            00008 00008       02             (stack size)
            '     vm.flags.mode~
            00009 00009       A1             CONSTANT (0)
            0000A 0000A       68 4C          STRUCT_SETUP LONG VBASE+$00004 (indexed)
            0000C 0000C       7F 20          BITFIELD_WRITE
            '     vm.flags.count~~
            0000E 0000E       A0             CONSTANT (-1)
            0000F 0000F       68 4C          STRUCT_SETUP LONG VBASE+$00004 (indexed)
            00011 00011       7F E8 01       BITFIELD_WRITE
            '     vm.flags.high++
            00014 00014       68 4C          STRUCT_SETUP LONG VBASE+$00004 (indexed)
            00016 00016       7D F8 01       BITFIELD_SETUP
            00019 00019       1F             POST_INC
            '     a := vm.flags.mode~
            0001A 0001A       A1             CONSTANT (0)
            0001B 0001B       68 4C          STRUCT_SETUP LONG VBASE+$00004 (indexed)
            0001D 0001D       7D 20          BITFIELD_SETUP
            0001F 0001F       29             SWAP
            00020 00020       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := vm.flags.count~~
            00021 00021       A0             CONSTANT (-1)
            00022 00022       68 4C          STRUCT_SETUP LONG VBASE+$00004 (indexed)
            00024 00024       7D E8 01       BITFIELD_SETUP
            00027 00027       29             SWAP
            00028 00028       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := vm.flags.high++
            00029 00029       68 4C          STRUCT_SETUP LONG VBASE+$00004 (indexed)
            0002B 0002B       7D F8 01       BITFIELD_SETUP
            0002E 0002E       23             POST_INC (push)
            0002F 0002F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00030 00030       04             RETURN
            00031 00031       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testStructureCircularDependency1() throws Exception {
        String text = """
            CON
                sStruct(byte a, word b, sStruct c)
            
            PUB start()
            """;

        Assertions.assertThrows(CompilerException.class, () -> {
            compile(text);
        });
    }

    @Test
    void testStructureCircularDependency2() throws Exception {
        String text = """
            CON
                sStruct1(byte a, word b, sStruct2 c)
                sStruct2(byte a, word b, sStruct1 c)
            
            PUB start()
            """;

        Assertions.assertThrows(CompilerException.class, () -> {
            compile(text);
        });
    }

    @Test
    void testSizeof() throws Exception {
        String text = """
            CON
                struct sPoint(word x, word y)
                struct sLine(sPoint a, sPoint b, byte c)
            
            VAR
                sLine line
                byte  buff[32]
            
            PUB start() | a
                a := sizeof(sPoint)
                a := sizeof(sLine)
                a := sizeof(sLine.a)
            
                a := sizeof(buff)
            
            DAT
                    mov     ptr, #sizeof(sLine)
                    mov     ptr, #sizeof(sLine.a)
            
            ptr     long    0
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 48)
            00000 00000       14 00 00 80    Method start @ $00014 (0 parameters, 0 returns)
            00004 00004       1F 00 00 00    End
            00008 00008 00000 09 10 04 F6                        mov     ptr, #sizeof(sLine)
            0000C 0000C 00004 04 10 04 F6                        mov     ptr, #sizeof(sLine.a)
            00010 00010 00008 00 00 00 00    ptr                 long    0
            ' PUB start() | a
            00014 00014       01             (stack size)
            '     a := sizeof(sPoint)
            00015 00015       A5             CONSTANT (4)
            00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := sizeof(sLine)
            00017 00017       AA             CONSTANT (9)
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := sizeof(sLine.a)
            00019 00019       A5             CONSTANT (4)
            0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := sizeof(buff)
            0001B 0001B       42 20          CONSTANT (32)
            0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001E 0001E       04             RETURN
            0001F 0001F       00             Padding
            """, compile(text));
    }

    @Test
    void testInlineAssemblyErrors() {
        String text = """
            PUB main()
            
                    org
                    mov     a, #0
                    end
            
            """;

        Assertions.assertThrows(CompilerException.class, () -> {
            Spin2Compiler compiler = new Spin2Compiler();
            Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompiler(compiler, new File("test.spin2"));
            objectCompiler.compileStep1(text);

            for (CompilerException msg : objectCompiler.getMessages()) {
                if (msg.type == CompilerException.ERROR) {
                    throw msg;
                }
            }
        });
    }

    @Test
    void testCoverage1() throws Exception {
        String text = """
            PUB start() | a, ^long ptr
            
                a := [ptr]
                a := [ptr]~
                a := [ptr]~~
                a := [ptr]++
                a := [ptr]--
                a := [ptr]!!
                a := [ptr]!
            
                a := [++]ptr.[15..0]
                a := [--]ptr.[a..0]
                a := [++]ptr.[15..0]~
                a := [--]ptr.[a..0]~~
                a := [++]ptr.[15..0]++
                a := [--]ptr.[a..0]--
                a := [++]ptr.[15..0]!!
                a := [--]ptr.[a..0]!
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       72 00 00 00    End
            ' PUB start() | a, ^long ptr
            00008 00008       02             (stack size)
            '     a := [ptr]
            00009 00009       E1             VAR_READ LONG (^LONG) DBASE+$00001 (short)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [ptr]~
            0000B 0000B       A1             CONSTANT (0)
            0000C 0000C       D1             VAR_SETUP LONG (^LONG) DBASE+$00001 (short)
            0000D 0000D       29             SWAP
            0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [ptr]~~
            0000F 0000F       A0             CONSTANT (-1)
            00010 00010       D1             VAR_SETUP LONG (^LONG) DBASE+$00001 (short)
            00011 00011       29             SWAP
            00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [ptr]++
            00013 00013       D1             VAR_SETUP LONG (^LONG) DBASE+$00001 (short)
            00014 00014       82 23          POST_INC (4) (push)
            00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [ptr]--
            00017 00017       D1             VAR_SETUP LONG (^LONG) DBASE+$00001 (short)
            00018 00018       82 24          POST_DEC (4) (push)
            0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [ptr]!!
            0001B 0001B       D1             VAR_SETUP LONG (^LONG) DBASE+$00001 (short)
            0001C 0001C       26             POST_LOGICAL_NOT (push)
            0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [ptr]!
            0001E 0001E       D1             VAR_SETUP LONG (^LONG) DBASE+$00001 (short)
            0001F 0001F       28             POST_NOT (push)
            00020 00020       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [++]ptr.[15..0]
            00021 00021       D1             VAR_SETUP LONG (^LONG) DBASE+$00001 (short)
            00022 00022       82 21          PRE_INC (4) (push)
            00024 00024       63             MEM_SETUP LONG
            00025 00025       7E E0 03       BITFIELD_READ
            00028 00028       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [--]ptr.[a..0]
            00029 00029       E0             VAR_READ LONG DBASE+$00000 (short)
            0002A 0002A       A1             CONSTANT (0)
            0002B 0002B       9F 94          BITRANGE
            0002D 0002D       D1             VAR_SETUP LONG (^LONG) DBASE+$00001 (short)
            0002E 0002E       82 22          PRE_DEC (4) (push)
            00030 00030       63             MEM_SETUP LONG
            00031 00031       7B             BITFIELD_READ (pop)
            00032 00032       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [++]ptr.[15..0]~
            00033 00033       A1             CONSTANT (0)
            00034 00034       D1             VAR_SETUP LONG (^LONG) DBASE+$00001 (short)
            00035 00035       82 21          PRE_INC (4) (push)
            00037 00037       63             MEM_SETUP LONG
            00038 00038       7D E0 03       BITFIELD_SETUP
            0003B 0003B       29             SWAP
            0003C 0003C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [--]ptr.[a..0]~~
            0003D 0003D       A0             CONSTANT (-1)
            0003E 0003E       E0             VAR_READ LONG DBASE+$00000 (short)
            0003F 0003F       A1             CONSTANT (0)
            00040 00040       9F 94          BITRANGE
            00042 00042       D1             VAR_SETUP LONG (^LONG) DBASE+$00001 (short)
            00043 00043       82 22          PRE_DEC (4) (push)
            00045 00045       63             MEM_SETUP LONG
            00046 00046       7A             BITFIELD_SETUP (pop)
            00047 00047       29             SWAP
            00048 00048       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [++]ptr.[15..0]++
            00049 00049       D1             VAR_SETUP LONG (^LONG) DBASE+$00001 (short)
            0004A 0004A       82 21          PRE_INC (4) (push)
            0004C 0004C       63             MEM_SETUP LONG
            0004D 0004D       7D E0 03       BITFIELD_SETUP
            00050 00050       23             POST_INC (push)
            00051 00051       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [--]ptr.[a..0]--
            00052 00052       E0             VAR_READ LONG DBASE+$00000 (short)
            00053 00053       A1             CONSTANT (0)
            00054 00054       9F 94          BITRANGE
            00056 00056       D1             VAR_SETUP LONG (^LONG) DBASE+$00001 (short)
            00057 00057       82 22          PRE_DEC (4) (push)
            00059 00059       63             MEM_SETUP LONG
            0005A 0005A       7A             BITFIELD_SETUP (pop)
            0005B 0005B       24             POST_DEC (push)
            0005C 0005C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [++]ptr.[15..0]!!
            0005D 0005D       D1             VAR_SETUP LONG (^LONG) DBASE+$00001 (short)
            0005E 0005E       82 21          PRE_INC (4) (push)
            00060 00060       63             MEM_SETUP LONG
            00061 00061       7D E0 03       BITFIELD_SETUP
            00064 00064       26             POST_LOGICAL_NOT (push)
            00065 00065       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := [--]ptr.[a..0]!
            00066 00066       E0             VAR_READ LONG DBASE+$00000 (short)
            00067 00067       A1             CONSTANT (0)
            00068 00068       9F 94          BITRANGE
            0006A 0006A       D1             VAR_SETUP LONG (^LONG) DBASE+$00001 (short)
            0006B 0006B       82 22          PRE_DEC (4) (push)
            0006D 0006D       63             MEM_SETUP LONG
            0006E 0006E       7A             BITFIELD_SETUP (pop)
            0006F 0006F       28             POST_NOT (push)
            00070 00070       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00071 00071       04             RETURN
            00072 00072       00 00          Padding
            """, compile(text));
    }

    @Test
    void testCoverage2() throws Exception {
        String text = """
            PUB start() | a, b, ^long ptr
            
                a := ptr[++]
                a := ptr[--].byte
                a := ptr[++].word
                a := ptr[--].long
            
                a := ptr[++]~
                a := ptr[--].byte~~
                a := ptr[++].word++
                a := ptr[--].long--
                a := ptr[++].word!!
                a := ptr[--].long!
            
                ptr[++] := a
                ptr[--].byte := a
                ptr[++].word := a
                ptr[--].long := a
            
                a := ptr~
                a := ptr~~
                a := ptr++
                a := ptr--
                a := ptr!!
                a := ptr!
            
                a := ptr.[15..0]~
                a := ptr.[b..0]~~
                a := ptr.[15..0]++
                a := ptr.[b..0]--
                a := ptr.[15..0]!!
                a := ptr.[b..0]!
            
                a := ptr.[15..0]
                a := ptr.[b..0]
                a := ptr.[15..0]
                a := ptr.[b..0]
                a := ptr.[15..0]
                a := ptr.[b..0]
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       D3 00 00 00    End
            ' PUB start() | a, b, ^long ptr
            00008 00008       03             (stack size)
            '     a := ptr[++]
            00009 00009       D2             VAR_SETUP LONG (^LONG) DBASE+$00002 (short)
            0000A 0000A       82 23          POST_INC (4) (push)
            0000C 0000C       63 1C          MEM_READ LONG
            0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr[--].byte
            0000F 0000F       D2             VAR_SETUP LONG (^LONG) DBASE+$00002 (short)
            00010 00010       24             POST_DEC (1) (push)
            00011 00011       61 1C          MEM_READ BYTE
            00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr[++].word
            00014 00014       D2             VAR_SETUP LONG (^LONG) DBASE+$00002 (short)
            00015 00015       80 23          POST_INC (2) (push)
            00017 00017       62 1C          MEM_READ WORD
            00019 00019       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr[--].long
            0001A 0001A       D2             VAR_SETUP LONG (^LONG) DBASE+$00002 (short)
            0001B 0001B       82 24          POST_DEC (4) (push)
            0001D 0001D       63 1C          MEM_READ LONG
            0001F 0001F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr[++]~
            00020 00020       A1             CONSTANT (0)
            00021 00021       D2             VAR_SETUP LONG (^LONG) DBASE+$00002 (short)
            00022 00022       82 23          POST_INC (4) (push)
            00024 00024       63             MEM_SETUP LONG
            00025 00025       29             SWAP
            00026 00026       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr[--].byte~~
            00027 00027       A0             CONSTANT (-1)
            00028 00028       D2             VAR_SETUP LONG (^LONG) DBASE+$00002 (short)
            00029 00029       24             POST_DEC (1) (push)
            0002A 0002A       61             MEM_SETUP BYTE
            0002B 0002B       29             SWAP
            0002C 0002C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr[++].word++
            0002D 0002D       D2             VAR_SETUP LONG (^LONG) DBASE+$00002 (short)
            0002E 0002E       80 23          POST_INC (2) (push)
            00030 00030       62             MEM_SETUP WORD
            00031 00031       23             POST_INC (push)
            00032 00032       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr[--].long--
            00033 00033       D2             VAR_SETUP LONG (^LONG) DBASE+$00002 (short)
            00034 00034       82 24          POST_DEC (4) (push)
            00036 00036       63             MEM_SETUP LONG
            00037 00037       24             POST_DEC (push)
            00038 00038       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr[++].word!!
            00039 00039       D2             VAR_SETUP LONG (^LONG) DBASE+$00002 (short)
            0003A 0003A       80 23          POST_INC (2) (push)
            0003C 0003C       62             MEM_SETUP WORD
            0003D 0003D       26             POST_LOGICAL_NOT (push)
            0003E 0003E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr[--].long!
            0003F 0003F       D2             VAR_SETUP LONG (^LONG) DBASE+$00002 (short)
            00040 00040       82 24          POST_DEC (4) (push)
            00042 00042       63             MEM_SETUP LONG
            00043 00043       28             POST_NOT (push)
            00044 00044       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr[++] := a
            00045 00045       E0             VAR_READ LONG DBASE+$00000 (short)
            00046 00046       D2             VAR_SETUP LONG (^LONG) DBASE+$00002 (short)
            00047 00047       82 23          POST_INC (4) (push)
            00049 00049       63 1D          MEM_WRITE LONG
            '     ptr[--].byte := a
            0004B 0004B       E0             VAR_READ LONG DBASE+$00000 (short)
            0004C 0004C       D2             VAR_SETUP LONG (^LONG) DBASE+$00002 (short)
            0004D 0004D       24             POST_DEC (1) (push)
            0004E 0004E       61 1D          MEM_WRITE BYTE
            '     ptr[++].word := a
            00050 00050       E0             VAR_READ LONG DBASE+$00000 (short)
            00051 00051       D2             VAR_SETUP LONG (^LONG) DBASE+$00002 (short)
            00052 00052       80 23          POST_INC (2) (push)
            00054 00054       62 1D          MEM_WRITE WORD
            '     ptr[--].long := a
            00056 00056       E0             VAR_READ LONG DBASE+$00000 (short)
            00057 00057       D2             VAR_SETUP LONG (^LONG) DBASE+$00002 (short)
            00058 00058       82 24          POST_DEC (4) (push)
            0005A 0005A       63 1D          MEM_WRITE LONG
            '     a := ptr~
            0005C 0005C       A1             CONSTANT (0)
            0005D 0005D       E2             VAR_READ LONG (^LONG) DBASE+$00002 (short)
            0005E 0005E       63             MEM_SETUP LONG
            0005F 0005F       29             SWAP
            00060 00060       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr~~
            00061 00061       A0             CONSTANT (-1)
            00062 00062       E2             VAR_READ LONG (^LONG) DBASE+$00002 (short)
            00063 00063       63             MEM_SETUP LONG
            00064 00064       29             SWAP
            00065 00065       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr++
            00066 00066       E2             VAR_READ LONG (^LONG) DBASE+$00002 (short)
            00067 00067       63             MEM_SETUP LONG
            00068 00068       23             POST_INC (push)
            00069 00069       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr--
            0006A 0006A       E2             VAR_READ LONG (^LONG) DBASE+$00002 (short)
            0006B 0006B       63             MEM_SETUP LONG
            0006C 0006C       24             POST_DEC (push)
            0006D 0006D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr!!
            0006E 0006E       E2             VAR_READ LONG (^LONG) DBASE+$00002 (short)
            0006F 0006F       63             MEM_SETUP LONG
            00070 00070       26             POST_LOGICAL_NOT (push)
            00071 00071       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr!
            00072 00072       E2             VAR_READ LONG (^LONG) DBASE+$00002 (short)
            00073 00073       63             MEM_SETUP LONG
            00074 00074       28             POST_NOT (push)
            00075 00075       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr.[15..0]~
            00076 00076       A1             CONSTANT (0)
            00077 00077       E2             VAR_READ LONG (^LONG) DBASE+$00002 (short)
            00078 00078       63             MEM_SETUP LONG
            00079 00079       7D E0 03       BITFIELD_SETUP
            0007C 0007C       29             SWAP
            0007D 0007D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr.[b..0]~~
            0007E 0007E       A0             CONSTANT (-1)
            0007F 0007F       E1             VAR_READ LONG DBASE+$00001 (short)
            00080 00080       A1             CONSTANT (0)
            00081 00081       9F 94          BITRANGE
            00083 00083       E2             VAR_READ LONG (^LONG) DBASE+$00002 (short)
            00084 00084       63             MEM_SETUP LONG
            00085 00085       7A             BITFIELD_SETUP (pop)
            00086 00086       29             SWAP
            00087 00087       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr.[15..0]++
            00088 00088       E2             VAR_READ LONG (^LONG) DBASE+$00002 (short)
            00089 00089       63             MEM_SETUP LONG
            0008A 0008A       7D E0 03       BITFIELD_SETUP
            0008D 0008D       23             POST_INC (push)
            0008E 0008E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr.[b..0]--
            0008F 0008F       E1             VAR_READ LONG DBASE+$00001 (short)
            00090 00090       A1             CONSTANT (0)
            00091 00091       9F 94          BITRANGE
            00093 00093       E2             VAR_READ LONG (^LONG) DBASE+$00002 (short)
            00094 00094       63             MEM_SETUP LONG
            00095 00095       7A             BITFIELD_SETUP (pop)
            00096 00096       24             POST_DEC (push)
            00097 00097       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr.[15..0]!!
            00098 00098       E2             VAR_READ LONG (^LONG) DBASE+$00002 (short)
            00099 00099       63             MEM_SETUP LONG
            0009A 0009A       7D E0 03       BITFIELD_SETUP
            0009D 0009D       26             POST_LOGICAL_NOT (push)
            0009E 0009E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr.[b..0]!
            0009F 0009F       E1             VAR_READ LONG DBASE+$00001 (short)
            000A0 000A0       A1             CONSTANT (0)
            000A1 000A1       9F 94          BITRANGE
            000A3 000A3       E2             VAR_READ LONG (^LONG) DBASE+$00002 (short)
            000A4 000A4       63             MEM_SETUP LONG
            000A5 000A5       7A             BITFIELD_SETUP (pop)
            000A6 000A6       28             POST_NOT (push)
            000A7 000A7       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr.[15..0]
            000A8 000A8       E2             VAR_READ LONG (^LONG) DBASE+$00002 (short)
            000A9 000A9       63             MEM_SETUP LONG
            000AA 000AA       7E E0 03       BITFIELD_READ
            000AD 000AD       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr.[b..0]
            000AE 000AE       E1             VAR_READ LONG DBASE+$00001 (short)
            000AF 000AF       A1             CONSTANT (0)
            000B0 000B0       9F 94          BITRANGE
            000B2 000B2       E2             VAR_READ LONG (^LONG) DBASE+$00002 (short)
            000B3 000B3       63             MEM_SETUP LONG
            000B4 000B4       7B             BITFIELD_READ (pop)
            000B5 000B5       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr.[15..0]
            000B6 000B6       E2             VAR_READ LONG (^LONG) DBASE+$00002 (short)
            000B7 000B7       63             MEM_SETUP LONG
            000B8 000B8       7E E0 03       BITFIELD_READ
            000BB 000BB       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr.[b..0]
            000BC 000BC       E1             VAR_READ LONG DBASE+$00001 (short)
            000BD 000BD       A1             CONSTANT (0)
            000BE 000BE       9F 94          BITRANGE
            000C0 000C0       E2             VAR_READ LONG (^LONG) DBASE+$00002 (short)
            000C1 000C1       63             MEM_SETUP LONG
            000C2 000C2       7B             BITFIELD_READ (pop)
            000C3 000C3       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr.[15..0]
            000C4 000C4       E2             VAR_READ LONG (^LONG) DBASE+$00002 (short)
            000C5 000C5       63             MEM_SETUP LONG
            000C6 000C6       7E E0 03       BITFIELD_READ
            000C9 000C9       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := ptr.[b..0]
            000CA 000CA       E1             VAR_READ LONG DBASE+$00001 (short)
            000CB 000CB       A1             CONSTANT (0)
            000CC 000CC       9F 94          BITRANGE
            000CE 000CE       E2             VAR_READ LONG (^LONG) DBASE+$00002 (short)
            000CF 000CF       63             MEM_SETUP LONG
            000D0 000D0       7B             BITFIELD_READ (pop)
            000D1 000D1       F0             VAR_WRITE LONG DBASE+$00000 (short)
            000D2 000D2       04             RETURN
            000D3 000D3       00             Padding
            """, compile(text));
    }

    String compile(String text) throws Exception {
        return compile(text, false);
    }

    String compile(String text, boolean debugEnabled) throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();
        compiler.setDebugEnabled(debugEnabled);
        Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompiler(compiler, new File("test.spin2"));
        Spin2Object obj = objectCompiler.compileObject(text);
        if (debugEnabled) {
            obj.setDebugData(compiler.generateDebugData());
            obj.setDebugger(new Spin2Debugger());
        }

        for (CompilerException msg : objectCompiler.getMessages()) {
            if (msg.type == CompilerException.ERROR) {
                throw msg;
            }
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateListing(new PrintStream(os));

        return os.toString().replaceAll("\\r\\n", "\n");
    }

}
