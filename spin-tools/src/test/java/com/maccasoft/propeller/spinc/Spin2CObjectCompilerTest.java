/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.spin2.Spin2Debugger;
import com.maccasoft.propeller.spin2.Spin2Object;

class Spin2CObjectCompilerTest {

    @Test
    void testEmptyFunction() throws Exception {
        String text = """
            void main()
            {
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0A 00 00 00    End
            ' void main() {
            00008 00008       00             (stack size)
            ' }
            00009 00009       04             RETURN
            0000A 0000A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testParameterVarAssignment() throws Exception {
        String text = """
            void main(int a)
            {
                a = 1;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 81    Method main @ $00008 (1 parameters, 0 returns)
            00004 00004       0C 00 00 00    End
            ' void main(int a) {
            00008 00008       00             (stack size)
            '     a = 1;
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            ' }
            0000B 0000B       04             RETURN
            """, compile(text));
    }

    @Test
    void testLocalVarAssignment() throws Exception {
        String text = """
            void main()
            {
                int a;
            
                a = 1;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0C 00 00 00    End
            ' void main() {
            00008 00008       01             (stack size)
            '     a = 1;
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            ' }
            0000B 0000B       04             RETURN
            """, compile(text));
    }

    @Test
    void testLocalVarDeclarationAndAssignment() throws Exception {
        String text = """
            void main()
            {
                int a = 1;
                int b = 2, c = 3, d = 4;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       12 00 00 00    End
            ' void main() {
            00008 00008       04             (stack size)
            '     int a = 1;
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     int b = 2, c = 3, d = 4;
            0000B 0000B       A3             CONSTANT (2)
            0000C 0000C       F1             VAR_WRITE LONG DBASE+$00001 (short)
            0000D 0000D       A4             CONSTANT (3)
            0000E 0000E       F2             VAR_WRITE LONG DBASE+$00002 (short)
            0000F 0000F       A5             CONSTANT (4)
            00010 00010       F3             VAR_WRITE LONG DBASE+$00003 (short)
            ' }
            00011 00011       04             RETURN
            00012 00012       00 00          Padding
            """, compile(text));
    }

    @Test
    void testGlobalVarAssignment() throws Exception {
        String text = """
            long a = 1;
            byte b = 2;
            word c = 0, d = 0;
            
            void main()
            {
                c = 3;
                d = 4;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 16)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       21 00 00 00    End
            ' void main() {
            00008 00008       00             (stack size)
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       C1 1D          VAR_WRITE LONG VBASE+$00001 (short)
            0000C 0000C       A3             CONSTANT (2)
            0000D 0000D       50 08 1D       VAR_WRITE BYTE VBASE+$00008
            00010 00010       A1             CONSTANT (0)
            00011 00011       56 09 1D       VAR_WRITE WORD VBASE+$00009
            00014 00014       A1             CONSTANT (0)
            00015 00015       56 0B 1D       VAR_WRITE WORD VBASE+$0000B
            '     c = 3;
            00018 00018       A4             CONSTANT (3)
            00019 00019       56 09 1D       VAR_WRITE WORD VBASE+$00009
            '     d = 4;
            0001C 0001C       A5             CONSTANT (4)
            0001D 0001D       56 0B 1D       VAR_WRITE WORD VBASE+$0000B
            ' }
            00020 00020       04             RETURN
            00021 00021       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testExpressionAssignment() throws Exception {
        String text = """
            int a, b;
            
            void main()
            {
                a = 1 + b * 3;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 12)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       12 00 00 00    End
            ' void main() {
            00008 00008       00             (stack size)
            '     a = 1 + b * 3;
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       C2 1C          VAR_READ LONG VBASE+$00002 (short)
            0000C 0000C       A4             CONSTANT (3)
            0000D 0000D       96             MULTIPLY
            0000E 0000E       8A             ADD
            0000F 0000F       C1 1D          VAR_WRITE LONG VBASE+$00001 (short)
            ' }
            00011 00011       04             RETURN
            00012 00012       00 00          Padding
            """, compile(text));
    }

    @Test
    void testMethodCall() throws Exception {
        String text = """
            int a;
            
            void main()
            {
                setup();
            }
            
            void setup() {
                a = 1;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 8)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       11 00 00 80    Method setup @ $00011 (0 parameters, 0 returns)
            00008 00008       16 00 00 00    End
            ' void main() {
            0000C 0000C       00             (stack size)
            '     setup();
            0000D 0000D       00             ANCHOR
            0000E 0000E       0A 01          CALL_SUB (1)
            ' }
            00010 00010       04             RETURN
            ' void setup() {
            00011 00011       00             (stack size)
            '     a = 1;
            00012 00012       A2             CONSTANT (1)
            00013 00013       C1 1D          VAR_WRITE LONG VBASE+$00001 (short)
            ' }
            00015 00015       04             RETURN
            00016 00016       00 00          Padding
            """, compile(text));
    }

    @Test
    void testForLoop() throws Exception {
        String text = """
            int a;
            
            void main()
            {
                for(;;) {
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 8)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0C 00 00 00    End
            ' void main() {
            00008 00008       00             (stack size)
            '     for(;;) {
            00009 00009       12 7F          JMP $00009 (-1)
            '     }
            ' }
            0000B 0000B       04             RETURN
            """, compile(text));
    }

    @Test
    void testForLoopBreakAndContinue() throws Exception {
        String text = """
            int a, b;
            
            void main()
            {
                for(;;) {
                    if (a == 10) {
                        b++;
                        continue;
                    }
                    if (a == 20) {
                        b = 1;
                        break;
                    }
                    b += 2;
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 12)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       25 00 00 00    End
            ' void main() {
            00008 00008       00             (stack size)
            '     for(;;) {
            '         if (a == 10) {
            00009 00009       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            0000B 0000B       AB             CONSTANT (10)
            0000C 0000C       70             EQUAL
            0000D 0000D       13 05          JZ $00013 (5)
            '             b++;
            0000F 0000F       C2             VAR_SETUP LONG VBASE+$00002 (short)
            00010 00010       1F             POST_INC
            '             continue;
            00011 00011       12 77          JMP $00009 (-9)
            '         }
            '         if (a == 20) {
            00013 00013       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            00015 00015       42 14          CONSTANT (20)
            00017 00017       70             EQUAL
            00018 00018       13 06          JZ $0001F (6)
            '             b = 1;
            0001A 0001A       A2             CONSTANT (1)
            0001B 0001B       C2 1D          VAR_WRITE LONG VBASE+$00002 (short)
            '             break;
            0001D 0001D       12 06          JMP $00024 (6)
            '         }
            '         b += 2;
            0001F 0001F       A3             CONSTANT (2)
            00020 00020       C2             VAR_SETUP LONG VBASE+$00002 (short)
            00021 00021       3F             ADD_ASSIGN
            00022 00022       12 66          JMP $00009 (-26)
            '     }
            ' }
            00024 00024       04             RETURN
            00025 00025       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testForLoopTwoArguments() throws Exception {
        String text = """
            int a;
            
            void main()
            {
                for(a = 0; a < 100;) {
                    a++;
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 8)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       18 00 00 00    End
            ' void main() {
            00008 00008       00             (stack size)
            '     for(a = 0; a < 100;) {
            00009 00009       A1             CONSTANT (0)
            0000A 0000A       C1 1D          VAR_WRITE LONG VBASE+$00001 (short)
            0000C 0000C       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            0000E 0000E       42 64          CONSTANT (100)
            00010 00010       6C             LESS_THAN
            00011 00011       13 05          JZ $00017 (5)
            '         a++;
            00013 00013       C1             VAR_SETUP LONG VBASE+$00001 (short)
            00014 00014       1F             POST_INC
            00015 00015       12 76          JMP $0000C (-10)
            '     }
            ' }
            00017 00017       04             RETURN
            """, compile(text));
    }

    @Test
    void testForLoopThreeArguments() throws Exception {
        String text = """
            int a, b;
            
            void main()
            {
                for(a = 0; a < 100; a++) {
                    b++;
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 12)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1A 00 00 00    End
            ' void main() {
            00008 00008       00             (stack size)
            '     for(a = 0; a < 100; a++) {
            00009 00009       A1             CONSTANT (0)
            0000A 0000A       C1 1D          VAR_WRITE LONG VBASE+$00001 (short)
            0000C 0000C       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            0000E 0000E       42 64          CONSTANT (100)
            00010 00010       6C             LESS_THAN
            00011 00011       13 07          JZ $00019 (7)
            '         b++;
            00013 00013       C2             VAR_SETUP LONG VBASE+$00002 (short)
            00014 00014       1F             POST_INC
            00015 00015       C1             VAR_SETUP LONG VBASE+$00001 (short)
            00016 00016       1F             POST_INC
            00017 00017       12 74          JMP $0000C (-12)
            '     }
            ' }
            00019 00019       04             RETURN
            0001A 0001A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testForLoopMultipleInits() throws Exception {
        String text = """
            int a, b;
            
            void main()
            {
                for(a = 0, b = 1; a < 100; a++) {
                    b++;
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 12)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1D 00 00 00    End
            ' void main() {
            00008 00008       00             (stack size)
            '     for(a = 0, b = 1; a < 100; a++) {
            00009 00009       A1             CONSTANT (0)
            0000A 0000A       C1 1D          VAR_WRITE LONG VBASE+$00001 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       C2 1D          VAR_WRITE LONG VBASE+$00002 (short)
            0000F 0000F       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            00011 00011       42 64          CONSTANT (100)
            00013 00013       6C             LESS_THAN
            00014 00014       13 07          JZ $0001C (7)
            '         b++;
            00016 00016       C2             VAR_SETUP LONG VBASE+$00002 (short)
            00017 00017       1F             POST_INC
            00018 00018       C1             VAR_SETUP LONG VBASE+$00001 (short)
            00019 00019       1F             POST_INC
            0001A 0001A       12 74          JMP $0000F (-12)
            '     }
            ' }
            0001C 0001C       04             RETURN
            0001D 0001D       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testForLoopScopeVariables() throws Exception {
        String text = """
            void main()
            {
                int a;
            
                for(word b1 = 0; b1 < 10; b1++) {
                    a += 1;
                }
            
                for(byte b1 = 0; b1 < 10; b1++) {
                    a += 2;
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       30 00 00 00    End
            ' void main() {
            00008 00008       02             (stack size)
            '     for(word b1 = 0; b1 < 10; b1++) {
            00009 00009       A1             CONSTANT (0)
            0000A 0000A       57 04 1D       VAR_WRITE WORD DBASE+$00004
            0000D 0000D       57 04 1C       VAR_READ WORD DBASE+$00004
            00010 00010       AB             CONSTANT (10)
            00011 00011       6C             LESS_THAN
            00012 00012       13 09          JZ $0001C (9)
            '         a += 1;
            00014 00014       A2             CONSTANT (1)
            00015 00015       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00016 00016       3F             ADD_ASSIGN
            00017 00017       57 04          VAR_SETUP WORD DBASE+$00004
            00019 00019       1F             POST_INC
            0001A 0001A       12 72          JMP $0000D (-14)
            '     }
            '     for(byte b1 = 0; b1 < 10; b1++) {
            0001C 0001C       A1             CONSTANT (0)
            0001D 0001D       51 04 1D       VAR_WRITE BYTE DBASE+$00004
            00020 00020       51 04 1C       VAR_READ BYTE DBASE+$00004
            00023 00023       AB             CONSTANT (10)
            00024 00024       6C             LESS_THAN
            00025 00025       13 09          JZ $0002F (9)
            '         a += 2;
            00027 00027       A3             CONSTANT (2)
            00028 00028       D0             VAR_SETUP LONG DBASE+$00000 (short)
            00029 00029       3F             ADD_ASSIGN
            0002A 0002A       51 04          VAR_SETUP BYTE DBASE+$00004
            0002C 0002C       1F             POST_INC
            0002D 0002D       12 72          JMP $00020 (-14)
            '     }
            ' }
            0002F 0002F       04             RETURN
            """, compile(text));
    }

    @Test
    void testWhileLoop() throws Exception {
        String text = """
            int a;
            
            void main()
            {
                while(a < 100) {
                    a++;
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 8)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       15 00 00 00    End
            ' void main() {
            00008 00008       00             (stack size)
            '     while(a < 100) {
            00009 00009       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            0000B 0000B       42 64          CONSTANT (100)
            0000D 0000D       6C             LESS_THAN
            0000E 0000E       13 05          JZ $00014 (5)
            '         a++;
            00010 00010       C1             VAR_SETUP LONG VBASE+$00001 (short)
            00011 00011       1F             POST_INC
            00012 00012       12 76          JMP $00009 (-10)
            '     }
            ' }
            00014 00014       04             RETURN
            00015 00015       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testWhileLoopBreakAndContinue() throws Exception {
        String text = """
            int a, b;
            
            void main()
            {
                while(a < 100) {
                    if (a == 10) {
                        b++;
                        continue;
                    }
                    if (a == 20) {
                        b = 1;
                        break;
                    }
                    a++;
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 12)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       2B 00 00 00    End
            ' void main() {
            00008 00008       00             (stack size)
            '     while(a < 100) {
            00009 00009       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            0000B 0000B       42 64          CONSTANT (100)
            0000D 0000D       6C             LESS_THAN
            0000E 0000E       13 1B          JZ $0002A (27)
            '         if (a == 10) {
            00010 00010       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            00012 00012       AB             CONSTANT (10)
            00013 00013       70             EQUAL
            00014 00014       13 05          JZ $0001A (5)
            '             b++;
            00016 00016       C2             VAR_SETUP LONG VBASE+$00002 (short)
            00017 00017       1F             POST_INC
            '             continue;
            00018 00018       12 70          JMP $00009 (-16)
            '         }
            '         if (a == 20) {
            0001A 0001A       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            0001C 0001C       42 14          CONSTANT (20)
            0001E 0001E       70             EQUAL
            0001F 0001F       13 06          JZ $00026 (6)
            '             b = 1;
            00021 00021       A2             CONSTANT (1)
            00022 00022       C2 1D          VAR_WRITE LONG VBASE+$00002 (short)
            '             break;
            00024 00024       12 05          JMP $0002A (5)
            '         }
            '         a++;
            00026 00026       C1             VAR_SETUP LONG VBASE+$00001 (short)
            00027 00027       1F             POST_INC
            00028 00028       12 60          JMP $00009 (-32)
            '     }
            ' }
            0002A 0002A       04             RETURN
            0002B 0002B       00             Padding
            """, compile(text));
    }

    @Test
    void testDoWhileLoop() throws Exception {
        String text = """
            int a;
            
            void main()
            {
                do {
                    a++;
                } while(a < 100);
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 8)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       13 00 00 00    End
            ' void main() {
            00008 00008       00             (stack size)
            '     do {
            '         a++;
            00009 00009       C1             VAR_SETUP LONG VBASE+$00001 (short)
            0000A 0000A       1F             POST_INC
            '     } while(a < 100);
            0000B 0000B       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            0000D 0000D       42 64          CONSTANT (100)
            0000F 0000F       6C             LESS_THAN
            00010 00010       14 78          JNZ $00009 (-8)
            ' }
            00012 00012       04             RETURN
            00013 00013       00             Padding
            """, compile(text));
    }

    @Test
    void testDoUntilLoop() throws Exception {
        String text = """
            int a;
            
            void main()
            {
                do {
                    a++;
                } until(a > 100);
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 8)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       13 00 00 00    End
            ' void main() {
            00008 00008       00             (stack size)
            '     do {
            '         a++;
            00009 00009       C1             VAR_SETUP LONG VBASE+$00001 (short)
            0000A 0000A       1F             POST_INC
            '     } until(a > 100);
            0000B 0000B       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            0000D 0000D       42 64          CONSTANT (100)
            0000F 0000F       74             GREATER_THAN
            00010 00010       13 78          JZ $00009 (-8)
            ' }
            00012 00012       04             RETURN
            00013 00013       00             Padding
            """, compile(text));
    }

    @Test
    void testConstants() throws Exception {
        String text = """
            #define _CLKFREQ 160_000_000
            
            void main() {
                int a;
            
                a = _CLKFREQ;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       10 00 00 00    End
            ' void main() {
            00008 00008       01             (stack size)
            '     a = _CLKFREQ;
            00009 00009       46 00 68 89 09 CONSTANT (160_000_000)
            0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            ' }
            0000F 0000F       04             RETURN
            """, compile(text));
    }

    @Test
    void testIf() throws Exception {
        String text = """
            void main()
            {
                int a;
            
                if (a == 0) {
                    a = 1;
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       11 00 00 00    End
            ' void main() {
            00008 00008       01             (stack size)
            '     if (a == 0) {
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A1             CONSTANT (0)
            0000B 0000B       70             EQUAL
            0000C 0000C       13 03          JZ $00010 (3)
            '         a = 1;
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     }
            ' }
            00010 00010       04             RETURN
            00011 00011       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testIfElse() throws Exception {
        String text = """
            void main()
            {
                int a;
            
                if (a == 0) {
                    a = 1;
                }
                else {
                    a = 2;
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       15 00 00 00    End
            ' void main() {
            00008 00008       01             (stack size)
            '     if (a == 0) {
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A1             CONSTANT (0)
            0000B 0000B       70             EQUAL
            0000C 0000C       13 05          JZ $00012 (5)
            '         a = 1;
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00010 00010       12 03          JMP $00014 (3)
            '     }
            '     else {
            '         a = 2;
            00012 00012       A3             CONSTANT (2)
            00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     }
            ' }
            00014 00014       04             RETURN
            00015 00015       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testIfElseSimpleBlock() throws Exception {
        String text = """
            void main()
            {
                int a;
            
                if (a == 0)
                    a = 1;
                else
                    a = 2;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       15 00 00 00    End
            ' void main() {
            00008 00008       01             (stack size)
            '     if (a == 0)
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A1             CONSTANT (0)
            0000B 0000B       70             EQUAL
            0000C 0000C       13 05          JZ $00012 (5)
            '         a = 1;
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00010 00010       12 03          JMP $00014 (3)
            '     else
            '         a = 2;
            00012 00012       A3             CONSTANT (2)
            00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)
            ' }
            00014 00014       04             RETURN
            00015 00015       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testIfElseIf() throws Exception {
        String text = """
            void main()
            {
                int a;
            
                if (a == 0) {
                    a = 1;
                }
                else if (a == 1) {
                    a = 2;
                }
                else if (a == 2) {
                    a = 3;
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       23 00 00 00    End
            ' void main() {
            00008 00008       01             (stack size)
            '     if (a == 0) {
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A1             CONSTANT (0)
            0000B 0000B       70             EQUAL
            0000C 0000C       13 05          JZ $00012 (5)
            '         a = 1;
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00010 00010       12 11          JMP $00022 (17)
            '     }
            '     else if (a == 1) {
            00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)
            00013 00013       A2             CONSTANT (1)
            00014 00014       70             EQUAL
            00015 00015       13 05          JZ $0001B (5)
            '         a = 2;
            00017 00017       A3             CONSTANT (2)
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00019 00019       12 08          JMP $00022 (8)
            '     }
            '     else if (a == 2) {
            0001B 0001B       E0             VAR_READ LONG DBASE+$00000 (short)
            0001C 0001C       A3             CONSTANT (2)
            0001D 0001D       70             EQUAL
            0001E 0001E       13 03          JZ $00022 (3)
            '         a = 3;
            00020 00020       A4             CONSTANT (3)
            00021 00021       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     }
            ' }
            00022 00022       04             RETURN
            00023 00023       00             Padding
            """, compile(text));
    }

    @Test
    void testIfElseIfElse() throws Exception {
        String text = """
            void main()
            {
                int a;
            
                if (a == 0) {
                    a = 1;
                }
                else if (a == 1) {
                    a = 2;
                }
                else if (a == 2) {
                    a = 3;
                }
                else {
                    a = 4;
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       27 00 00 00    End
            ' void main() {
            00008 00008       01             (stack size)
            '     if (a == 0) {
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       A1             CONSTANT (0)
            0000B 0000B       70             EQUAL
            0000C 0000C       13 05          JZ $00012 (5)
            '         a = 1;
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00010 00010       12 15          JMP $00026 (21)
            '     }
            '     else if (a == 1) {
            00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)
            00013 00013       A2             CONSTANT (1)
            00014 00014       70             EQUAL
            00015 00015       13 05          JZ $0001B (5)
            '         a = 2;
            00017 00017       A3             CONSTANT (2)
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00019 00019       12 0C          JMP $00026 (12)
            '     }
            '     else if (a == 2) {
            0001B 0001B       E0             VAR_READ LONG DBASE+$00000 (short)
            0001C 0001C       A3             CONSTANT (2)
            0001D 0001D       70             EQUAL
            0001E 0001E       13 05          JZ $00024 (5)
            '         a = 3;
            00020 00020       A4             CONSTANT (3)
            00021 00021       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00022 00022       12 03          JMP $00026 (3)
            '     }
            '     else {
            '         a = 4;
            00024 00024       A5             CONSTANT (4)
            00025 00025       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     }
            ' }
            00026 00026       04             RETURN
            00027 00027       00             Padding
            """, compile(text));
    }

    @Test
    void testInlinePAsmBlock() throws Exception {
        String text = """
            void main()
            {
                asm {
                    nop
                    ret
                }
                asm {
                    org
                    nop
                    ret
                }
                asm {
                    orgh
                    nop
                    ret
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       32 00 00 00    End
            ' void main() {
            00008 00008       00             (stack size)
            '     asm {
            00009 00009       19 5C 00 00 01 INLINE-EXEC ORG=$000, 2
            0000E 0000E       00
            0000F 0000F   000 00 00 00 00                        nop
            00013 00013   001 2D 00 64 FD                        ret
            '     }
            '     asm {
            00017 00017       19 5C 00 00 01 INLINE-EXEC ORG=$000, 2
            0001C 0001C       00
            0001D 0001D   000                                    org
            0001D 0001D   000 00 00 00 00                        nop
            00021 00021   001 2D 00 64 FD                        ret
            '     }
            '     asm {
            00025 00025       19 5E 02 00    INLINE-EXEC ORGH 2
            00029 00029   000                                    orgh
            00029 00029   000 00 00 00 00                        nop
            0002D 0002D   004 2D 00 64 FD                        ret
            '     }
            ' }
            00031 00031       04             RETURN
            00032 00032       00 00          Padding
            """, compile(text));
    }

    @Test
    void testInlineLocalLabels() throws Exception {
        String text = """
            void main()
            {
                asm {
            start
            .l1     nop
                    jmp #.l1
            setup
            .l1     nop
                    jmp #.l1
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       20 00 00 00    End
            ' void main() {
            00008 00008       00             (stack size)
            '     asm {
            00009 00009       19 5C 00 00 03 INLINE-EXEC ORG=$000, 4
            0000E 0000E       00
            0000F 0000F   000                start              \s
            0000F 0000F   000 00 00 00 00    .l1                 nop
            00013 00013   001 F8 FF 9F FD                        jmp     #.l1
            00017 00017   002                setup              \s
            00017 00017   002 00 00 00 00    .l1                 nop
            0001B 0001B   003 F8 FF 9F FD                        jmp     #.l1
            '     }
            ' }
            0001F 0001F       04             RETURN
            """, compile(text));
    }

    @Test
    void testDebug() throws Exception {
        String text = """
            void main()
            {
                debug();
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0D 00 00 00    End
            ' void main() {
            00008 00008       00             (stack size)
            '     debug();
            00009 00009       41 00 01       DEBUG #1
            ' }
            0000C 0000C       04             RETURN
            0000D 0000D       00 00 00       Padding
            ' Debug data
            00B74 00000       05 00        \s
            00B76 00002       04 00          #1@0004
            ' #1
            00B78 00004       00             DONE
            """, compile(text, true));
    }

    @Test
    void testSwitch() throws Exception {
        String text = """
            void main()
            {
                int a;
                switch(a) {
                    case 1:
                        a = 4;
                        break;
                    case 2:
                        a = 5;
                        break;
                    case 3:
                        a = 6;
                        break;
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       20 00 00 00    End
            ' void main() {
            00008 00008       01             (stack size)
            '     switch(a) {
            00009 00009       42 1F          ADDRESS ($0001F)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       1C 08          CASE_JMP $00016 (8)
            0000F 0000F       A3             CONSTANT (2)
            00010 00010       1C 08          CASE_JMP $00019 (8)
            00012 00012       A4             CONSTANT (3)
            00013 00013       1C 08          CASE_JMP $0001C (8)
            00015 00015       1E             CASE_DONE
            '             a = 4;
            00016 00016       A5             CONSTANT (4)
            00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00018 00018       1E             CASE_DONE
            '             a = 5;
            00019 00019       A6             CONSTANT (5)
            0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001B 0001B       1E             CASE_DONE
            '             a = 6;
            0001C 0001C       A7             CONSTANT (6)
            0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001E 0001E       1E             CASE_DONE
            '     }
            ' }
            0001F 0001F       04             RETURN
            """, compile(text));
    }

    @Test
    void testSwitchDefault() throws Exception {
        String text = """
            void main()
            {
                int a;
                switch(a) {
                    case 1:
                        a = 4;
                        break;
                    case 2:
                        a = 5;
                        break;
                    default:
                        a = 6;
                        break;
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1C 00 00 00    End
            ' void main() {
            00008 00008       01             (stack size)
            '     switch(a) {
            00009 00009       42 1B          ADDRESS ($0001B)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       1C 07          CASE_JMP $00015 (7)
            0000F 0000F       A3             CONSTANT (2)
            00010 00010       1C 07          CASE_JMP $00018 (7)
            '             a = 6;
            00012 00012       A7             CONSTANT (6)
            00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00014 00014       1E             CASE_DONE
            '             a = 4;
            00015 00015       A5             CONSTANT (4)
            00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00017 00017       1E             CASE_DONE
            '             a = 5;
            00018 00018       A6             CONSTANT (5)
            00019 00019       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001A 0001A       1E             CASE_DONE
            '     }
            ' }
            0001B 0001B       04             RETURN
            """, compile(text));
    }

    @Test
    void testSwitchCaseWithoutBreak() throws Exception {
        String text = """
            void main()
            {
                int a;
                switch(a) {
                    case 1:
                        a = 4;
                    case 2:
                        a = 5;
                    case 3:
                        a = 6;
                        break;
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1E 00 00 00    End
            ' void main() {
            00008 00008       01             (stack size)
            '     switch(a) {
            00009 00009       42 1D          ADDRESS ($0001D)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       1C 08          CASE_JMP $00016 (8)
            0000F 0000F       A3             CONSTANT (2)
            00010 00010       1C 07          CASE_JMP $00018 (7)
            00012 00012       A4             CONSTANT (3)
            00013 00013       1C 06          CASE_JMP $0001A (6)
            00015 00015       1E             CASE_DONE
            '             a = 4;
            00016 00016       A5             CONSTANT (4)
            00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '             a = 5;
            00018 00018       A6             CONSTANT (5)
            00019 00019       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '             a = 6;
            0001A 0001A       A7             CONSTANT (6)
            0001B 0001B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001C 0001C       1E             CASE_DONE
            '     }
            ' }
            0001D 0001D       04             RETURN
            0001E 0001E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testSwitchCaseWithoutStatements() throws Exception {
        String text = """
            void main()
            {
                int a;
                switch(a) {
                    case 1:
                    case 2:
                        a = 5;
                        break;
                    case 3:
                        a = 6;
                        break;
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1D 00 00 00    End
            ' void main() {
            00008 00008       01             (stack size)
            '     switch(a) {
            00009 00009       42 1C          ADDRESS ($0001C)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       1C 08          CASE_JMP $00016 (8)
            0000F 0000F       A3             CONSTANT (2)
            00010 00010       1C 05          CASE_JMP $00016 (5)
            00012 00012       A4             CONSTANT (3)
            00013 00013       1C 05          CASE_JMP $00019 (5)
            00015 00015       1E             CASE_DONE
            '             a = 5;
            00016 00016       A6             CONSTANT (5)
            00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00018 00018       1E             CASE_DONE
            '             a = 6;
            00019 00019       A7             CONSTANT (6)
            0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001B 0001B       1E             CASE_DONE
            '     }
            ' }
            0001C 0001C       04             RETURN
            0001D 0001D       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testSwitchCaseWithSpinSyntax() throws Exception {
        String text = """
            void main()
            {
                int a;
                switch(a) {
                    case 1, 2:
                        a = 5;
                        break;
                    case 3..5,7,8:
                        a = 6;
                        break;
                }
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       24 00 00 00    End
            ' void main() {
            00008 00008       01             (stack size)
            '     switch(a) {
            00009 00009       42 23          ADDRESS ($00023)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       1C 0F          CASE_JMP $0001D (15)
            0000F 0000F       A3             CONSTANT (2)
            00010 00010       1C 0C          CASE_JMP $0001D (12)
            00012 00012       A4             CONSTANT (3)
            00013 00013       A6             CONSTANT (5)
            00014 00014       1D 0B          CASE_RANGE_JMP $00020 (11)
            00016 00016       A8             CONSTANT (7)
            00017 00017       1C 08          CASE_JMP $00020 (8)
            00019 00019       A9             CONSTANT (8)
            0001A 0001A       1C 05          CASE_JMP $00020 (5)
            0001C 0001C       1E             CASE_DONE
            '             a = 5;
            0001D 0001D       A6             CONSTANT (5)
            0001E 0001E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001F 0001F       1E             CASE_DONE
            '             a = 6;
            00020 00020       A7             CONSTANT (6)
            00021 00021       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00022 00022       1E             CASE_DONE
            '     }
            ' }
            00023 00023       04             RETURN
            """, compile(text));
    }

    @Test
    void testPreprocessorNotDefined() throws Exception {
        String text = """
            void main(int a)
            {
            #ifdef TEST
                a = 1;
            #endif
                a++;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 81    Method main @ $00008 (1 parameters, 0 returns)
            00004 00004       0C 00 00 00    End
            ' void main(int a) {
            00008 00008       00             (stack size)
            '     a++;
            00009 00009       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000A 0000A       1F             POST_INC
            ' }
            0000B 0000B       04             RETURN
            """, compile(text));
    }

    @Test
    void testPreprocessorDefined() throws Exception {
        String text = """
            #define TEST
            
            void main(int a)
            {
            #ifdef TEST
                a = 1;
            #endif
                a++;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 81    Method main @ $00008 (1 parameters, 0 returns)
            00004 00004       0E 00 00 00    End
            ' void main(int a) {
            00008 00008       00             (stack size)
            '     a = 1;
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a++;
            0000B 0000B       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000C 0000C       1F             POST_INC
            ' }
            0000D 0000D       04             RETURN
            0000E 0000E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testPreprocessorNotDefinedElse() throws Exception {
        String text = """
            void main(int a)
            {
            #ifdef TEST
                a = 1;
            #else
                a = 2;
            #endif
                a++;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 81    Method main @ $00008 (1 parameters, 0 returns)
            00004 00004       0E 00 00 00    End
            ' void main(int a) {
            00008 00008       00             (stack size)
            '     a = 2;
            00009 00009       A3             CONSTANT (2)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a++;
            0000B 0000B       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000C 0000C       1F             POST_INC
            ' }
            0000D 0000D       04             RETURN
            0000E 0000E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testNestedPreprocessorNotDefinedElse1() throws Exception {
        String text = """
            void main(int a)
            {
            #ifndef P2
              #ifdef TEST
                a = 1;
              #else
                a = 2;
              #endif
                a++;
            #endif
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 81    Method main @ $00008 (1 parameters, 0 returns)
            00004 00004       0E 00 00 00    End
            ' void main(int a) {
            00008 00008       00             (stack size)
            '     a = 2;
            00009 00009       A3             CONSTANT (2)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a++;
            0000B 0000B       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000C 0000C       1F             POST_INC
            ' }
            0000D 0000D       04             RETURN
            0000E 0000E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testNestedPreprocessorNotDefinedElse2() throws Exception {
        String text = """
            #define TEST
            
            void main(int a)
            {
            #ifndef P2
              #ifdef TEST
                a = 1;
              #else
                a = 2;
              #endif
                a++;
            #endif
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 81    Method main @ $00008 (1 parameters, 0 returns)
            00004 00004       0E 00 00 00    End
            ' void main(int a) {
            00008 00008       00             (stack size)
            '     a = 1;
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a++;
            0000B 0000B       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000C 0000C       1F             POST_INC
            ' }
            0000D 0000D       04             RETURN
            0000E 0000E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testPreprocessorIf() throws Exception {
        String text = """
            #define TEST 1
            
            void main(int a)
            {
            #if defined(__P2__)
              #if TEST
                a = 1;
              #else
                a = 2;
              #endif
                a++;
            #endif
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 81    Method main @ $00008 (1 parameters, 0 returns)
            00004 00004       0E 00 00 00    End
            ' void main(int a) {
            00008 00008       00             (stack size)
            '     a = 1;
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a++;
            0000B 0000B       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000C 0000C       1F             POST_INC
            ' }
            0000D 0000D       04             RETURN
            0000E 0000E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testFloatAssignment() throws Exception {
        String text = """
            float a = 1.0, b;
            
            void main()
            {
                float c = 3.0, d;
            
                b = 2.0;
                d = 4.0;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 12)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       21 00 00 00    End
            ' void main() {
            00008 00008       02             (stack size)
            00009 00009       46 00 00 80 3F CONSTANT (1.0)
            0000E 0000E       C1 1D          VAR_WRITE FLOAT VBASE+$00001 (short)
            '     float c = 3.0, d;
            00010 00010       46 00 00 40 40 CONSTANT (3.0)
            00015 00015       F0             VAR_WRITE FLOAT DBASE+$00000 (short)
            '     b = 2.0;
            00016 00016       47 1E          CONSTANT (2.0)
            00018 00018       C2 1D          VAR_WRITE FLOAT VBASE+$00002 (short)
            '     d = 4.0;
            0001A 0001A       46 00 00 80 40 CONSTANT (4.0)
            0001F 0001F       F1             VAR_WRITE FLOAT DBASE+$00001 (short)
            ' }
            00020 00020       04             RETURN
            00021 00021       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testFloatExpression() throws Exception {
        String text = """
            void main()
            {
                float a = 3.0, b;
            
                b = (a + 1.0) * 2.0;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1D 00 00 00    End
            ' void main() {
            00008 00008       02             (stack size)
            '     float a = 3.0, b;
            00009 00009       46 00 00 40 40 CONSTANT (3.0)
            0000E 0000E       F0             VAR_WRITE FLOAT DBASE+$00000 (short)
            '     b = (a + 1.0) * 2.0;
            0000F 0000F       E0             VAR_READ FLOAT DBASE+$00000 (short)
            00010 00010       46 00 00 80 3F CONSTANT (1.0)
            00015 00015       19 BE          FLOAT_ADD
            00017 00017       47 1E          CONSTANT (2.0)
            00019 00019       19 C2          FLOAT_MULTIPLY
            0001B 0001B       F1             VAR_WRITE FLOAT DBASE+$00001 (short)
            ' }
            0001C 0001C       04             RETURN
            0001D 0001D       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testFloatPromotedConstants() throws Exception {
        String text = """
            void main()
            {
                float a = 3, b;
            
                b = (a + 1) * 2;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1D 00 00 00    End
            ' void main() {
            00008 00008       02             (stack size)
            '     float a = 3, b;
            00009 00009       46 00 00 40 40 CONSTANT (3.0)
            0000E 0000E       F0             VAR_WRITE FLOAT DBASE+$00000 (short)
            '     b = (a + 1) * 2;
            0000F 0000F       E0             VAR_READ FLOAT DBASE+$00000 (short)
            00010 00010       46 00 00 80 3F CONSTANT (1.0)
            00015 00015       19 BE          FLOAT_ADD
            00017 00017       47 1E          CONSTANT (2.0)
            00019 00019       19 C2          FLOAT_MULTIPLY
            0001B 0001B       F1             VAR_WRITE FLOAT DBASE+$00001 (short)
            ' }
            0001C 0001C       04             RETURN
            0001D 0001D       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testFloatPromotedExpression() throws Exception {
        String text = """
            void main()
            {
                int a = 3;
                float b;
            
                b = (a + 1) * 2;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       14 00 00 00    End
            ' void main() {
            00008 00008       02             (stack size)
            '     int a = 3;
            00009 00009       A4             CONSTANT (3)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     b = (a + 1) * 2;
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       A2             CONSTANT (1)
            0000D 0000D       8A             ADD
            0000E 0000E       A3             CONSTANT (2)
            0000F 0000F       96             MULTIPLY
            00010 00010       19 A6          FLOAT
            00012 00012       F1             VAR_WRITE FLOAT DBASE+$00001 (short)
            ' }
            00013 00013       04             RETURN
            """, compile(text));
    }

    @Test
    void testFloatToIntegerExpression() throws Exception {
        String text = """
            void main()
            {
                float a = 3;
                int b;
            
                b = (a + 1) * 2;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1F 00 00 00    End
            ' void main() {
            00008 00008       02             (stack size)
            '     float a = 3;
            00009 00009       46 00 00 40 40 CONSTANT (3.0)
            0000E 0000E       F0             VAR_WRITE FLOAT DBASE+$00000 (short)
            '     b = (a + 1) * 2;
            0000F 0000F       E0             VAR_READ FLOAT DBASE+$00000 (short)
            00010 00010       46 00 00 80 3F CONSTANT (1.0)
            00015 00015       19 BE          FLOAT_ADD
            00017 00017       47 1E          CONSTANT (2.0)
            00019 00019       19 C2          FLOAT_MULTIPLY
            0001B 0001B       19 A8          ROUND
            0001D 0001D       F1             VAR_WRITE LONG DBASE+$00001 (short)
            ' }
            0001E 0001E       04             RETURN
            0001F 0001F       00             Padding
            """, compile(text));
    }

    @Test
    void testMixedExpression() throws Exception {
        String text = """
            void main()
            {
                int a = 3, b;
                float c, d;
            
                c = (a + d) * b;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       18 00 00 00    End
            ' void main() {
            00008 00008       04             (stack size)
            '     int a = 3, b;
            00009 00009       A4             CONSTANT (3)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     c = (a + d) * b;
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       19 A6          FLOAT
            0000E 0000E       E3             VAR_READ FLOAT DBASE+$00003 (short)
            0000F 0000F       19 BE          FLOAT_ADD
            00011 00011       E1             VAR_READ LONG DBASE+$00001 (short)
            00012 00012       19 A6          FLOAT
            00014 00014       19 C2          FLOAT_MULTIPLY
            00016 00016       F2             VAR_WRITE FLOAT DBASE+$00002 (short)
            ' }
            00017 00017       04             RETURN
            """, compile(text));
    }

    @Test
    void testFloatFunctionArguments() throws Exception {
        String text = """
            void main()
            {
                setup(1, 2.0);
            }
            
            void setup(int a, float b)
            {
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       14 00 00 82    Method setup @ $00014 (2 parameters, 0 returns)
            00008 00008       16 00 00 00    End
            ' void main() {
            0000C 0000C       00             (stack size)
            '     setup(1, 2.0);
            0000D 0000D       00             ANCHOR
            0000E 0000E       A2             CONSTANT (1)
            0000F 0000F       47 1E          CONSTANT (2.0)
            00011 00011       0A 01          CALL_SUB (1)
            ' }
            00013 00013       04             RETURN
            ' void setup(int a, float b) {
            00014 00014       00             (stack size)
            ' }
            00015 00015       04             RETURN
            00016 00016       00 00          Padding
            """, compile(text));
    }

    @Test
    void testPromoteFloatFunctionArguments() throws Exception {
        String text = """
            void main()
            {
                int a, b;
            
                setup(a, b);
            }
            
            void setup(int a, float b)
            {
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       15 00 00 82    Method setup @ $00015 (2 parameters, 0 returns)
            00008 00008       17 00 00 00    End
            ' void main() {
            0000C 0000C       02             (stack size)
            '     setup(a, b);
            0000D 0000D       00             ANCHOR
            0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)
            0000F 0000F       E1             VAR_READ LONG DBASE+$00001 (short)
            00010 00010       19 A6          FLOAT
            00012 00012       0A 01          CALL_SUB (1)
            ' }
            00014 00014       04             RETURN
            ' void setup(int a, float b) {
            00015 00015       00             (stack size)
            ' }
            00016 00016       04             RETURN
            00017 00017       00             Padding
            """, compile(text));
    }

    @Test
    void testRoundFloatFunctionArguments() throws Exception {
        String text = """
            void main()
            {
                float a, b;
            
                setup(a, b);
            }
            
            void setup(int a, float b)
            {
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       15 00 00 82    Method setup @ $00015 (2 parameters, 0 returns)
            00008 00008       17 00 00 00    End
            ' void main() {
            0000C 0000C       02             (stack size)
            '     setup(a, b);
            0000D 0000D       00             ANCHOR
            0000E 0000E       E0             VAR_READ FLOAT DBASE+$00000 (short)
            0000F 0000F       19 A8          ROUND
            00011 00011       E1             VAR_READ FLOAT DBASE+$00001 (short)
            00012 00012       0A 01          CALL_SUB (1)
            ' }
            00014 00014       04             RETURN
            ' void setup(int a, float b) {
            00015 00015       00             (stack size)
            ' }
            00016 00016       04             RETURN
            00017 00017       00             Padding
            """, compile(text));
    }

    @Test
    void testBytePointer() throws Exception {
        String text = """
            byte * ptr, a, *b;
            
            void main()
            {
                ptr = "Hello, World!";
                a = 'a';
                b = "b";
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 16)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       28 00 00 00    End
            ' void main() {
            00008 00008       00             (stack size)
            '     ptr = "Hello, World!";
            00009 00009       9E 0E 48 65 6C STRING
            0000E 0000E       6C 6F 2C 20 57
            00013 00013       6F 72 6C 64 21
            00018 00018       00
            00019 00019       C1 1D          VAR_WRITE LONG VBASE+$00001 (short)
            '     a = 'a';
            0001B 0001B       42 61          CONSTANT ("a")
            0001D 0001D       50 08 1D       VAR_WRITE BYTE VBASE+$00008
            '     b = "b";
            00020 00020       9E 02 62 00    STRING
            00024 00024       5C 09 1D       VAR_WRITE LONG VBASE+$00009
            ' }
            00027 00027       04             RETURN
            """, compile(text));
    }

    @Test
    void testBytePointerStatements() throws Exception {
        String text = """
            void main()
            {
                byte * ptr, a, *b;
            
                ptr = "Hello, World!";
                a = 'a';
                b = "b";
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       27 00 00 00    End
            ' void main() {
            00008 00008       03             (stack size)
            '     ptr = "Hello, World!";
            00009 00009       9E 0E 48 65 6C STRING
            0000E 0000E       6C 6F 2C 20 57
            00013 00013       6F 72 6C 64 21
            00018 00018       00
            00019 00019       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a = 'a';
            0001A 0001A       42 61          CONSTANT ("a")
            0001C 0001C       51 04 1D       VAR_WRITE BYTE DBASE+$00004
            '     b = "b";
            0001F 0001F       9E 02 62 00    STRING
            00023 00023       5D 05 1D       VAR_WRITE LONG DBASE+$00005
            ' }
            00026 00026       04             RETURN
            00027 00027       00             Padding
            """, compile(text));
    }

    @Test
    void testBytePointerArgument() throws Exception {
        String text = """
            void main()
            {
                str("Hello, World!");
            }
            
            void str(byte * ptr)
            {
            
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       21 00 00 81    Method str @ $00021 (1 parameters, 0 returns)
            00008 00008       23 00 00 00    End
            ' void main() {
            0000C 0000C       00             (stack size)
            '     str("Hello, World!");
            0000D 0000D       00             ANCHOR
            0000E 0000E       9E 0E 48 65 6C STRING
            00013 00013       6C 6F 2C 20 57
            00018 00018       6F 72 6C 64 21
            0001D 0001D       00
            0001E 0001E       0A 01          CALL_SUB (1)
            ' }
            00020 00020       04             RETURN
            ' void str(byte * ptr) {
            00021 00021       00             (stack size)
            ' }
            00022 00022       04             RETURN
            00023 00023       00             Padding
            """, compile(text));
    }

    @Test
    void testPointerDereference() throws Exception {
        String text = """
            void main()
            {
                int a;
                byte * ptr1;
                word * ptr2;
                int * ptr3;
            
                a = *ptr1;
                a = *ptr2;
                a = *ptr3;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       16 00 00 00    End
            ' void main() {
            00008 00008       04             (stack size)
            '     a = *ptr1;
            00009 00009       E1             VAR_READ LONG DBASE+$00001 (short)
            0000A 0000A       61 1C          MEM_READ BYTE
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a = *ptr2;
            0000D 0000D       E2             VAR_READ LONG DBASE+$00002 (short)
            0000E 0000E       62 1C          MEM_READ WORD
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a = *ptr3;
            00011 00011       E3             VAR_READ LONG DBASE+$00003 (short)
            00012 00012       63 1C          MEM_READ LONG
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            ' }
            00015 00015       04             RETURN
            00016 00016       00 00          Padding
            """, compile(text));
    }

    @Test
    void testPointerDereferenceAssign() throws Exception {
        String text = """
            void main()
            {
                int a;
                byte * ptr1;
                word * ptr2;
                int * ptr3;
            
                *ptr1 = a;
                *ptr2 = a;
                *ptr3 = a;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       16 00 00 00    End
            ' void main() {
            00008 00008       04             (stack size)
            '     *ptr1 = a;
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            0000B 0000B       61 1D          MEM_WRITE BYTE
            '     *ptr2 = a;
            0000D 0000D       E0             VAR_READ LONG DBASE+$00000 (short)
            0000E 0000E       E2             VAR_READ LONG DBASE+$00002 (short)
            0000F 0000F       62 1D          MEM_WRITE WORD
            '     *ptr3 = a;
            00011 00011       E0             VAR_READ LONG DBASE+$00000 (short)
            00012 00012       E3             VAR_READ LONG DBASE+$00003 (short)
            00013 00013       63 1D          MEM_WRITE LONG
            ' }
            00015 00015       04             RETURN
            00016 00016       00 00          Padding
            """, compile(text));
    }

    @Test
    void testPointerDereferencePostEffect() throws Exception {
        String text = """
            void main()
            {
                int a;
                byte * ptr1;
                word * ptr2;
                int * ptr3;
            
                a = *ptr1++;
                a = *ptr2++;
                a = *ptr3++;
            
                a = *ptr1--;
                a = *ptr2--;
                a = *ptr3--;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       30 00 00 00    End
            ' void main() {
            00008 00008       04             (stack size)
            '     a = *ptr1++;
            00009 00009       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0000A 0000A       23             POST_INC (push)
            0000B 0000B       61 1C          MEM_READ BYTE
            0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a = *ptr2++;
            0000E 0000E       E2             VAR_READ LONG DBASE+$00002 (short)
            0000F 0000F       A3             CONSTANT (2)
            00010 00010       D2             VAR_SETUP LONG DBASE+$00002 (short)
            00011 00011       3F             ADD_ASSIGN
            00012 00012       62 1C          MEM_READ WORD
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a = *ptr3++;
            00015 00015       E3             VAR_READ LONG DBASE+$00003 (short)
            00016 00016       A5             CONSTANT (4)
            00017 00017       D3             VAR_SETUP LONG DBASE+$00003 (short)
            00018 00018       3F             ADD_ASSIGN
            00019 00019       63 1C          MEM_READ LONG
            0001B 0001B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a = *ptr1--;
            0001C 0001C       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0001D 0001D       24             POST_DEC (push)
            0001E 0001E       61 1C          MEM_READ BYTE
            00020 00020       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a = *ptr2--;
            00021 00021       E2             VAR_READ LONG DBASE+$00002 (short)
            00022 00022       A3             CONSTANT (2)
            00023 00023       D2             VAR_SETUP LONG DBASE+$00002 (short)
            00024 00024       40             SUBTRACT_ASSIGN
            00025 00025       62 1C          MEM_READ WORD
            00027 00027       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a = *ptr3--;
            00028 00028       E3             VAR_READ LONG DBASE+$00003 (short)
            00029 00029       A5             CONSTANT (4)
            0002A 0002A       D3             VAR_SETUP LONG DBASE+$00003 (short)
            0002B 0002B       40             SUBTRACT_ASSIGN
            0002C 0002C       63 1C          MEM_READ LONG
            0002E 0002E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            ' }
            0002F 0002F       04             RETURN
            """, compile(text));
    }

    @Test
    void testPointerDereferencePostEffectAssign() throws Exception {
        String text = """
            void main()
            {
                int a;
                byte * ptr1;
                word * ptr2;
                int * ptr3;
            
                *ptr1++ = a;
                *ptr2++ = a;
                *ptr3++ = a;
            
                *ptr1-- = a;
                *ptr2-- = a;
                *ptr3-- = a;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       30 00 00 00    End
            ' void main() {
            00008 00008       04             (stack size)
            '     *ptr1++ = a;
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0000B 0000B       23             POST_INC (push)
            0000C 0000C       61 1D          MEM_WRITE BYTE
            '     *ptr2++ = a;
            0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)
            0000F 0000F       E2             VAR_READ LONG DBASE+$00002 (short)
            00010 00010       A3             CONSTANT (2)
            00011 00011       D2             VAR_SETUP LONG DBASE+$00002 (short)
            00012 00012       3F             ADD_ASSIGN
            00013 00013       62 1D          MEM_WRITE WORD
            '     *ptr3++ = a;
            00015 00015       E0             VAR_READ LONG DBASE+$00000 (short)
            00016 00016       E3             VAR_READ LONG DBASE+$00003 (short)
            00017 00017       A5             CONSTANT (4)
            00018 00018       D3             VAR_SETUP LONG DBASE+$00003 (short)
            00019 00019       3F             ADD_ASSIGN
            0001A 0001A       63 1D          MEM_WRITE LONG
            '     *ptr1-- = a;
            0001C 0001C       E0             VAR_READ LONG DBASE+$00000 (short)
            0001D 0001D       D1             VAR_SETUP LONG DBASE+$00001 (short)
            0001E 0001E       24             POST_DEC (push)
            0001F 0001F       61 1D          MEM_WRITE BYTE
            '     *ptr2-- = a;
            00021 00021       E0             VAR_READ LONG DBASE+$00000 (short)
            00022 00022       E2             VAR_READ LONG DBASE+$00002 (short)
            00023 00023       A3             CONSTANT (2)
            00024 00024       D2             VAR_SETUP LONG DBASE+$00002 (short)
            00025 00025       40             SUBTRACT_ASSIGN
            00026 00026       62 1D          MEM_WRITE WORD
            '     *ptr3-- = a;
            00028 00028       E0             VAR_READ LONG DBASE+$00000 (short)
            00029 00029       E3             VAR_READ LONG DBASE+$00003 (short)
            0002A 0002A       A5             CONSTANT (4)
            0002B 0002B       D3             VAR_SETUP LONG DBASE+$00003 (short)
            0002C 0002C       40             SUBTRACT_ASSIGN
            0002D 0002D       63 1D          MEM_WRITE LONG
            ' }
            0002F 0002F       04             RETURN
            """, compile(text));
    }

    @Test
    void testPointerArray() throws Exception {
        String text = """
            void main()
            {
                int a, b;
                byte * ptr1;
                word * ptr2;
                int * ptr3;
            
                a = ptr1[0];
                a = ptr2[1];
                a = ptr3[2];
            
                a = ptr1[b+0];
                a = ptr2[b+1];
                a = ptr3[b+2];
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       2E 00 00 00    End
            ' void main() {
            00008 00008       05             (stack size)
            '     a = ptr1[0];
            00009 00009       E2             VAR_READ LONG DBASE+$00002 (short)
            0000A 0000A       A1             CONSTANT (0)
            0000B 0000B       64 1C          MEM_READ BYTE INDEXED
            0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a = ptr2[1];
            0000E 0000E       E3             VAR_READ LONG DBASE+$00003 (short)
            0000F 0000F       A2             CONSTANT (1)
            00010 00010       65 1C          MEM_READ WORD INDEXED
            00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a = ptr3[2];
            00013 00013       E4             VAR_READ LONG DBASE+$00004 (short)
            00014 00014       A3             CONSTANT (2)
            00015 00015       66 1C          MEM_READ LONG INDEXED
            00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a = ptr1[b+0];
            00018 00018       E2             VAR_READ LONG DBASE+$00002 (short)
            00019 00019       E1             VAR_READ LONG DBASE+$00001 (short)
            0001A 0001A       A1             CONSTANT (0)
            0001B 0001B       8A             ADD
            0001C 0001C       64 1C          MEM_READ BYTE INDEXED
            0001E 0001E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a = ptr2[b+1];
            0001F 0001F       E3             VAR_READ LONG DBASE+$00003 (short)
            00020 00020       E1             VAR_READ LONG DBASE+$00001 (short)
            00021 00021       A2             CONSTANT (1)
            00022 00022       8A             ADD
            00023 00023       65 1C          MEM_READ WORD INDEXED
            00025 00025       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a = ptr3[b+2];
            00026 00026       E4             VAR_READ LONG DBASE+$00004 (short)
            00027 00027       E1             VAR_READ LONG DBASE+$00001 (short)
            00028 00028       A3             CONSTANT (2)
            00029 00029       8A             ADD
            0002A 0002A       66 1C          MEM_READ LONG INDEXED
            0002C 0002C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            ' }
            0002D 0002D       04             RETURN
            0002E 0002E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testPointerArrayAssign() throws Exception {
        String text = """
            void main()
            {
                int a;
                byte * ptr1;
                word * ptr2;
                int * ptr3;
            
                ptr1[0] = a;
                ptr2[1] = a;
                ptr3[2] = a;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       19 00 00 00    End
            ' void main() {
            00008 00008       04             (stack size)
            '     ptr1[0] = a;
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            0000B 0000B       A1             CONSTANT (0)
            0000C 0000C       64 1D          MEM_WRITE BYTE INDEXED
            '     ptr2[1] = a;
            0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)
            0000F 0000F       E2             VAR_READ LONG DBASE+$00002 (short)
            00010 00010       A2             CONSTANT (1)
            00011 00011       65 1D          MEM_WRITE WORD INDEXED
            '     ptr3[2] = a;
            00013 00013       E0             VAR_READ LONG DBASE+$00000 (short)
            00014 00014       E3             VAR_READ LONG DBASE+$00003 (short)
            00015 00015       A3             CONSTANT (2)
            00016 00016       66 1D          MEM_WRITE LONG INDEXED
            ' }
            00018 00018       04             RETURN
            00019 00019       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testStructureAssign() throws Exception {
        String text = """
            struct sPoint {
                byte x;
                word y;
            };
            
            void main()
            {
                sPoint pt[2];
                int a;
            
                pt.x = 1;
                pt.y = 2;
            
                pt[1].x = 1;
                pt[1].y = 2;
            
                pt[a].x = 1;
                pt[a].y = 2;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       2A 00 00 00    End
            ' void main() {
            00008 00008       03             (stack size)
            '     pt.x = 1;
            00009 00009       A2             CONSTANT (1)
            0000A 0000A       51 00 1D       MEM_WRITE BYTE DBASE+$00000
            '     pt.y = 2;
            0000D 0000D       A3             CONSTANT (2)
            0000E 0000E       57 01 1D       MEM_WRITE WORD DBASE+$00001
            '     pt[1].x = 1;
            00011 00011       A2             CONSTANT (1)
            00012 00012       51 03 1D       MEM_WRITE BYTE DBASE+$00003
            '     pt[1].y = 2;
            00015 00015       A3             CONSTANT (2)
            00016 00016       57 04 1D       MEM_WRITE WORD DBASE+$00004
            '     pt[a].x = 1;
            00019 00019       A2             CONSTANT (1)
            0001A 0001A       5D 06 1C       VAR_READ LONG DBASE+$00006
            0001D 0001D       69 05 03 1D    STRUCT_WRITE BYTE DBASE+$00000 (indexed)
            '     pt[a].y = 2;
            00021 00021       A3             CONSTANT (2)
            00022 00022       5D 06 1C       VAR_READ LONG DBASE+$00006
            00025 00025       69 19 03 1D    STRUCT_WRITE WORD DBASE+$00001 (indexed)
            ' }
            00029 00029       04             RETURN
            0002A 0002A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testStructureRead() throws Exception {
        String text = """
            struct sPoint {
                byte x;
                word y;
            };
            
            void main()
            {
                sPoint pt[2];
                int a, b;
            
                a = pt.x;
                a = pt.y;
            
                a = pt[1].x;
                a = pt[1].y;
            
                a = pt[b].x;
                a = pt[b].y;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       36 00 00 00    End
            ' void main() {
            00008 00008       04             (stack size)
            '     a = pt.x;
            00009 00009       51 00 1C       MEM_READ BYTE DBASE+$00000
            0000C 0000C       5D 06 1D       VAR_WRITE LONG DBASE+$00006
            '     a = pt.y;
            0000F 0000F       57 01 1C       MEM_READ WORD DBASE+$00001
            00012 00012       5D 06 1D       VAR_WRITE LONG DBASE+$00006
            '     a = pt[1].x;
            00015 00015       51 03 1C       MEM_READ BYTE DBASE+$00003
            00018 00018       5D 06 1D       VAR_WRITE LONG DBASE+$00006
            '     a = pt[1].y;
            0001B 0001B       57 04 1C       MEM_READ WORD DBASE+$00004
            0001E 0001E       5D 06 1D       VAR_WRITE LONG DBASE+$00006
            '     a = pt[b].x;
            00021 00021       5D 0A 1C       VAR_READ LONG DBASE+$0000A
            00024 00024       69 05 03 1C    STRUCT_READ BYTE DBASE+$00000 (indexed)
            00028 00028       5D 06 1D       VAR_WRITE LONG DBASE+$00006
            '     a = pt[b].y;
            0002B 0002B       5D 0A 1C       VAR_READ LONG DBASE+$0000A
            0002E 0002E       69 19 03 1C    STRUCT_READ WORD DBASE+$00001 (indexed)
            00032 00032       5D 06 1D       VAR_WRITE LONG DBASE+$00006
            ' }
            00035 00035       04             RETURN
            00036 00036       00 00          Padding
            """, compile(text));
    }

    @Test
    void testNestedStructureAssign() throws Exception {
        String text = """
            struct sPoint {
                byte x;
                word y;
            };
            
            struct sLine {
                sPoint a;
                sPoint b;
                byte   color;
            };
            
            void main()
            {
              sLine line[2];
              int a, b;
            
              line.a.x = a;
              line.b.y = a;
            
              line[1].a.x = a;
              line[1].a.y = a;
            
              line[b].a.x = a;
              line[b].a.y = a;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       36 00 00 00    End
            ' void main() {
            00008 00008       06             (stack size)
            '   line.a.x = a;
            00009 00009       5D 0E 1C       VAR_READ LONG DBASE+$0000E
            0000C 0000C       51 00 1D       MEM_WRITE BYTE DBASE+$00000
            '   line.b.y = a;
            0000F 0000F       5D 0E 1C       VAR_READ LONG DBASE+$0000E
            00012 00012       57 04 1D       MEM_WRITE WORD DBASE+$00004
            '   line[1].a.x = a;
            00015 00015       5D 0E 1C       VAR_READ LONG DBASE+$0000E
            00018 00018       51 07 1D       MEM_WRITE BYTE DBASE+$00007
            '   line[1].a.y = a;
            0001B 0001B       5D 0E 1C       VAR_READ LONG DBASE+$0000E
            0001E 0001E       57 08 1D       MEM_WRITE WORD DBASE+$00008
            '   line[b].a.x = a;
            00021 00021       5D 0E 1C       VAR_READ LONG DBASE+$0000E
            00024 00024       5D 12 1C       VAR_READ LONG DBASE+$00012
            00027 00027       69 05 07 1D    STRUCT_WRITE BYTE DBASE+$00000 (indexed)
            '   line[b].a.y = a;
            0002B 0002B       5D 0E 1C       VAR_READ LONG DBASE+$0000E
            0002E 0002E       5D 12 1C       VAR_READ LONG DBASE+$00012
            00031 00031       69 19 07 1D    STRUCT_WRITE WORD DBASE+$00001 (indexed)
            ' }
            00035 00035       04             RETURN
            00036 00036       00 00          Padding
            """, compile(text));
    }

    @Test
    void testNestedStructureRead() throws Exception {
        String text = """
            struct sPoint {
                byte x;
                word y;
            };
            
            struct sLine {
                sPoint a;
                sPoint b;
                byte   color;
            };
            
            void main()
            {
              sLine line[2];
              int a, b;
            
              a = line.a.x;
              a = line.b.y;
            
              a = line[1].a.x;
              a = line[1].a.y;
            
              a = line[b].a.x;
              a = line[b].a.y;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       36 00 00 00    End
            ' void main() {
            00008 00008       06             (stack size)
            '   a = line.a.x;
            00009 00009       51 00 1C       MEM_READ BYTE DBASE+$00000
            0000C 0000C       5D 0E 1D       VAR_WRITE LONG DBASE+$0000E
            '   a = line.b.y;
            0000F 0000F       57 04 1C       MEM_READ WORD DBASE+$00004
            00012 00012       5D 0E 1D       VAR_WRITE LONG DBASE+$0000E
            '   a = line[1].a.x;
            00015 00015       51 07 1C       MEM_READ BYTE DBASE+$00007
            00018 00018       5D 0E 1D       VAR_WRITE LONG DBASE+$0000E
            '   a = line[1].a.y;
            0001B 0001B       57 08 1C       MEM_READ WORD DBASE+$00008
            0001E 0001E       5D 0E 1D       VAR_WRITE LONG DBASE+$0000E
            '   a = line[b].a.x;
            00021 00021       5D 12 1C       VAR_READ LONG DBASE+$00012
            00024 00024       69 05 07 1C    STRUCT_READ BYTE DBASE+$00000 (indexed)
            00028 00028       5D 0E 1D       VAR_WRITE LONG DBASE+$0000E
            '   a = line[b].a.y;
            0002B 0002B       5D 12 1C       VAR_READ LONG DBASE+$00012
            0002E 0002E       69 19 07 1C    STRUCT_READ WORD DBASE+$00001 (indexed)
            00032 00032       5D 0E 1D       VAR_WRITE LONG DBASE+$0000E
            ' }
            00035 00035       04             RETURN
            00036 00036       00 00          Padding
            """, compile(text));
    }

    @Test
    void testNestedStructuresIndex() throws Exception {
        String text = """
            struct sPoint {
                byte x;
                word y;
            };
            
            struct sLine {
                sPoint a;
                sPoint b;
                byte   color;
            };
            
            struct sPolygon {
                byte   n;
                sPoint pt[8];
                byte   color;
            };
            
            sPolygon poly[2];
            
            void main()
            {
              int a, b;
            
              a = poly.pt[0].x;
              a = poly.pt[1].y;
            
              a = poly[0].pt[0].x;
              a = poly[1].pt[1].y;
            
              a = poly[b].n;
              a = poly[b].pt[a].x;
              a = poly[b].color;
            
              a = poly[b].pt[2].y;
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 56)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       36 00 00 00    End
            ' void main() {
            00008 00008       02             (stack size)
            '   a = poly.pt[0].x;
            00009 00009       50 05 1C       MEM_READ BYTE VBASE+$00005
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a = poly.pt[1].y;
            0000D 0000D       56 09 1C       MEM_READ WORD VBASE+$00009
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a = poly[0].pt[0].x;
            00011 00011       50 05 1C       MEM_READ BYTE VBASE+$00005
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a = poly[1].pt[1].y;
            00015 00015       56 23 1C       MEM_READ WORD VBASE+$00023
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a = poly[b].n;
            00019 00019       E1             VAR_READ LONG DBASE+$00001 (short)
            0001A 0001A       68 45 1A 1C    STRUCT_READ BYTE VBASE+$00004 (indexed)
            0001E 0001E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a = poly[b].pt[a].x;
            0001F 0001F       E1             VAR_READ LONG DBASE+$00001 (short)
            00020 00020       E0             VAR_READ LONG DBASE+$00000 (short)
            00021 00021       68 56 03 1A 1C STRUCT_READ BYTE VBASE+$00005 (indexed)
            00026 00026       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a = poly[b].color;
            00027 00027       E1             VAR_READ LONG DBASE+$00001 (short)
            00028 00028       68 D5 03 1A 1C STRUCT_READ BYTE VBASE+$0001D (indexed)
            0002D 0002D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a = poly[b].pt[2].y;
            0002E 0002E       E1             VAR_READ LONG DBASE+$00001 (short)
            0002F 0002F       68 C9 01 1A 1C STRUCT_READ WORD VBASE+$0000C (indexed)
            00034 00034       F0             VAR_WRITE LONG DBASE+$00000 (short)
            ' }
            00035 00035       04             RETURN
            00036 00036       00 00          Padding
            """, compile(text));
    }

    @Test
    void testStructureAddress() throws Exception {
        String text = """
            struct sPoint {
                byte x;
                word y;
            };
            
            struct sLine {
                sPoint a;
                sPoint b;
                byte   color;
            };
            
            struct sPolygon {
                byte   n;
                sPoint pt[8];
                byte   color;
            };
            
            sPolygon poly[2];
            
            void main()
            {
              int a, b;
            
              a = &poly;
              a = &poly[1];
            
              a = &poly[b];
            
              a = &poly.pt;
              a = &poly.pt[1];
              a = &poly[b].pt[2];
            }
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 56)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       29 00 00 00    End
            ' void main() {
            00008 00008       02             (stack size)
            '   a = &poly;
            00009 00009       68 40 00       STRUCT_ADDRESS LONG VBASE+$00004 (indexed)
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a = &poly[1];
            0000D 0000D       68 E0 03 00    STRUCT_ADDRESS LONG VBASE+$0001E (indexed)
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a = &poly[b];
            00012 00012       E1             VAR_READ LONG DBASE+$00001 (short)
            00013 00013       68 41 1A 00    STRUCT_ADDRESS LONG VBASE+$00004 (indexed)
            00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a = &poly.pt;
            00018 00018       68 50 00       STRUCT_ADDRESS LONG VBASE+$00005 (indexed)
            0001B 0001B       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a = &poly.pt[1];
            0001C 0001C       68 80 01 00    STRUCT_ADDRESS LONG VBASE+$00008 (indexed)
            00020 00020       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   a = &poly[b].pt[2];
            00021 00021       E1             VAR_READ LONG DBASE+$00001 (short)
            00022 00022       68 B1 01 1A 00 STRUCT_ADDRESS LONG VBASE+$0000B (indexed)
            00027 00027       F0             VAR_WRITE LONG DBASE+$00000 (short)
            ' }
            00028 00028       04             RETURN
            00029 00029       00 00 00       Padding
            """, compile(text));
    }

    String compile(String text) throws Exception {
        return compile(text, false);
    }

    String compile(String text, boolean debugEnabled) throws Exception {
        Spin2CCompiler compiler = new Spin2CCompiler();
        compiler.setDebugEnabled(debugEnabled);

        Spin2CObjectCompiler objectCompiler = new Spin2CObjectCompiler(compiler, new File("test.spin2"));
        Spin2Object obj = objectCompiler.compileObject(text);
        if (debugEnabled) {
            obj.setDebugData(compiler.generateDebugData());
            obj.setDebugger(new Spin2Debugger());
        }

        for (CompilerException msg : compiler.getMessages()) {
            if (msg.type == CompilerException.ERROR) {
                throw msg;
            }
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateListing(new PrintStream(os));

        return os.toString().replaceAll("\\r\\n", "\n");
    }

}
