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

class Spin2PAsmCompilerTest {

    @Test
    void testCompile() throws Exception {
        String text = """
            DAT             org     $000
            
            start
                            cogid   a
                            cogstop a
            
            a               res     1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000                start              \s
            00000 00000   000 01 04 60 FD                        cogid   a
            00004 00004   001 03 04 60 FD                        cogstop a
            00008 00008   002                a                   res     1
            """, compile(text));
    }

    @Test
    void testLocalLabels() throws Exception {
        String text = """
            DAT             org     $000
            
            start
                            mov     a, #1
                            jmp     #.l2
            .l1             add     a, #1
                            add     a, #1
            .l2             add     a, #1
                            jmp     #.l1
            
            a               res     1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000                start              \s
            00000 00000   000 01 0C 04 F6                        mov     a, #1
            00004 00004   001 08 00 90 FD                        jmp     #.l2
            00008 00008   002 01 0C 04 F1    .l1                 add     a, #1
            0000C 0000C   003 01 0C 04 F1                        add     a, #1
            00010 00010   004 01 0C 04 F1    .l2                 add     a, #1
            00014 00014   005 F0 FF 9F FD                        jmp     #.l1
            00018 00018   006                a                   res     1
            """, compile(text));
    }

    @Test
    void testAliasedLabels() throws Exception {
        String text = """
            DAT             org     $000
            
            start
                            mov     a, #1
                            jmp     #.l2
            .l1
                            add     a, #1
                            add     a, #1
            .l2
                            add     a, #1
                            jmp     #.l1
            
            a               res     1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000                start              \s
            00000 00000   000 01 0C 04 F6                        mov     a, #1
            00004 00004   001 08 00 90 FD                        jmp     #.l2
            00008 00008   002                .l1                \s
            00008 00008   002 01 0C 04 F1                        add     a, #1
            0000C 0000C   003 01 0C 04 F1                        add     a, #1
            00010 00010   004                .l2                \s
            00010 00010   004 01 0C 04 F1                        add     a, #1
            00014 00014   005 F0 FF 9F FD                        jmp     #.l1
            00018 00018   006                a                   res     1
            """, compile(text));
    }

    @Test
    void testDuplicatedLocalLabels() throws Exception {
        String text = """
            DAT             org     $000
            
            start
                            mov     a, #1
                            jmp     #.l2
            .l1             add     a, #1
                            add     a, #1
            .l2             add     a, #1
                            jmp     #.l1
            
            setup
                            mov     a, #1
                            jmp     #.l2
            .l1             add     a, #1
                            add     a, #1
            .l2             add     a, #1
                            jmp     #.l1
            
            a               res     1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000                start              \s
            00000 00000   000 01 18 04 F6                        mov     a, #1
            00004 00004   001 08 00 90 FD                        jmp     #.l2
            00008 00008   002 01 18 04 F1    .l1                 add     a, #1
            0000C 0000C   003 01 18 04 F1                        add     a, #1
            00010 00010   004 01 18 04 F1    .l2                 add     a, #1
            00014 00014   005 F0 FF 9F FD                        jmp     #.l1
            00018 00018   006                setup              \s
            00018 00018   006 01 18 04 F6                        mov     a, #1
            0001C 0001C   007 08 00 90 FD                        jmp     #.l2
            00020 00020   008 01 18 04 F1    .l1                 add     a, #1
            00024 00024   009 01 18 04 F1                        add     a, #1
            00028 00028   00A 01 18 04 F1    .l2                 add     a, #1
            0002C 0002C   00B F0 FF 9F FD                        jmp     #.l1
            00030 00030   00C                a                   res     1
            """, compile(text));
    }

    @Test
    void testOrg() throws Exception {
        String text = """
            DAT             org   $000
                            long    0
                            long    1
                            org   $100
                            long    2
                            long    3
                            long    4
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 00 00 00 00                        long    0
            00004 00004   001 01 00 00 00                        long    1
            00008 00008   002                                    org     $100
            00008 00008   100 02 00 00 00                        long    2
            0000C 0000C   101 03 00 00 00                        long    3
            00010 00010   102 04 00 00 00                        long    4
            """, compile(text));
    }

    @Test
    void testOrgf() throws Exception {
        String text = """
            DAT             org   $000
            label0          long    1
                            long    2
                            orgf  $10
            label10         long    2
                            long    3
                            long    4
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 01 00 00 00    label0              long    1
            00004 00004   001 02 00 00 00                        long    2
            00008 00008   002 00 00 00 00                        orgf    $10
            0000C 0000C   003 00 00 00 00  \s
            00010 00010   004 00 00 00 00  \s
            00014 00014   005 00 00 00 00  \s
            00018 00018   006 00 00 00 00  \s
            0001C 0001C   007 00 00 00 00  \s
            00020 00020   008 00 00 00 00  \s
            00024 00024   009 00 00 00 00  \s
            00028 00028   00A 00 00 00 00  \s
            0002C 0002C   00B 00 00 00 00  \s
            00030 00030   00C 00 00 00 00  \s
            00034 00034   00D 00 00 00 00  \s
            00038 00038   00E 00 00 00 00  \s
            0003C 0003C   00F 00 00 00 00
            00040 00040   010 02 00 00 00    label10             long    2
            00044 00044   011 03 00 00 00                        long    3
            00048 00048   012 04 00 00 00                        long    4
            """, compile(text));
    }

    @Test
    void testRes() throws Exception {
        String text = """
            DAT             org     $000
                            long    0
                            res
                            res     1
                            res     2
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 00 00 00 00                        long    0
            00004 00004   001                                    res
            00004 00004   002                                    res     1
            00004 00004   003                                    res     2
            """, compile(text));
    }

    @Test
    void testFit() throws Exception {
        String text = """
            DAT             org   $000
                            long    0[$10]
                            fit   $10
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 00 00 00 00                        long    0[$10]
            00004 00004   001 00 00 00 00  \s
            00008 00008   002 00 00 00 00  \s
            0000C 0000C   003 00 00 00 00  \s
            00010 00010   004 00 00 00 00  \s
            00014 00014   005 00 00 00 00  \s
            00018 00018   006 00 00 00 00  \s
            0001C 0001C   007 00 00 00 00  \s
            00020 00020   008 00 00 00 00  \s
            00024 00024   009 00 00 00 00  \s
            00028 00028   00A 00 00 00 00  \s
            0002C 0002C   00B 00 00 00 00  \s
            00030 00030   00C 00 00 00 00  \s
            00034 00034   00D 00 00 00 00  \s
            00038 00038   00E 00 00 00 00  \s
            0003C 0003C   00F 00 00 00 00
            00040 00040   010                                    fit     $10
            """, compile(text));
    }

    @Test
    void testCogFitLimit() throws Exception {
        String text = """
            DAT             org   $000
                            long    0[$10]
                            long    1
                            fit   $10
            """;

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile(text);
            }
        });
    }

    @Test
    void testLutFitLimit() throws Exception {
        String text = """
            DAT             org   $200
                            long    0[$10]
                            long    1
                            fit   $210
            """;

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile(text);
            }
        });
    }

    @Test
    void testCogOrgFitLimit() throws Exception {
        String text = """
            DAT             org   $000, $010
                            long    0[$10]
                            long    1
            """;

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile(text);
            }
        });
    }

    @Test
    void testLutOrgFitLimit() throws Exception {
        String text = """
            DAT             org   $200, $210
                            long    0[$10]
                            long    1
            """;

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile(text);
            }
        });
    }

    @Test
    void testByte() throws Exception {
        String text = """
            DAT             org   $000
                            byte    1
                            byte    2
                            byte    3[2], 4[3]
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 01                                 byte    1
            00001 00001   000 02                                 byte    2
            00002 00002   000 03 03 04 04                        byte    3[2], 4[3]
            00006 00006   001 04                     byte    3[2], 4[3]
            00007 00007       00             Padding
            """, compile(text));
    }

    @Test
    void testWord() throws Exception {
        String text = """
            DAT             org   $000
                            word    1
                            word    2
                            word    3[2], 4[3]
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 01 00                              word    1
            00002 00002   000 02 00                              word    2
            00004 00004   001 03 00 03 00                        word    3[2], 4[3]
            00008 00008   002 04 00 04 00  \s
            0000C 0000C   003 04 00
            0000E 0000E       00 00          Padding
            """, compile(text));
    }

    @Test
    void testLong() throws Exception {
        String text = """
            DAT             org   $000
                            long    1
                            long    2
                            long    3[2], 4[3]
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 01 00 00 00                        long    1
            00004 00004   001 02 00 00 00                        long    2
            00008 00008   002 03 00 00 00                        long    3[2], 4[3]
            0000C 0000C   003 03 00 00 00  \s
            00010 00010   004 04 00 00 00  \s
            00014 00014   005 04 00 00 00  \s
            00018 00018   006 04 00 00 00
            """, compile(text));
    }

    @Test
    void testDataOverride() throws Exception {
        String text = """
            DAT
                            long    1, byte 2, 3, word 4, 5
                            word    1, byte 2, 3, long 4, 5
                            byte    1, word 2, 3, long 4, 5
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000 00000 01 00 00 00                        long    1, byte 2, 3, word 4, 5
            00004 00004 00004 02 03 00 00  \s
            00008 00008 00008 00 04 00 05  \s
            0000C 0000C 0000C 00 00 00
            0000F 0000F 0000F 01 00 02 03                        word    1, byte 2, 3, long 4, 5
            00013 00013 00013 00 04 00 00  \s
            00017 00017 00017 00 05 00
            0001A 0001A 0001A 01 02 00 03                        byte    1, word 2, 3, long 4, 5
            0001E 0001E 0001E 04 00 00 00  \s
            00022 00022 00022 05
            00023 00023       00             Padding
            """, compile(text));
    }

    @Test
    void testBytefit() throws Exception {
        String text = """
            DAT             org   $000
                            bytefit $00
                            bytefit $01
                            bytefit $02
                            bytefit -$80
                            bytefit $FF
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 00                                 bytefit $00
            00001 00001   000 01                                 bytefit $01
            00002 00002   000 02                                 bytefit $02
            00003 00003   000 80                                 bytefit -$80
            00004 00004   001 FF                                 bytefit $FF
            00005 00005       00 00 00       Padding
            """, compile(text));

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile("""
                    DAT             org   $000
                                    bytefit -$81
                    """);
            }
        });

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile("""
                    DAT             org   $000
                                    bytefit $100
                    """);
            }
        });
    }

    @Test
    void testWordfit() throws Exception {
        String text = """
            DAT             org   $000
                            wordfit $0000
                            wordfit $0001
                            wordfit $0002
                            wordfit -$8000
                            wordfit $FFFF
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 00 00                              wordfit $0000
            00002 00002   000 01 00                              wordfit $0001
            00004 00004   001 02 00                              wordfit $0002
            00006 00006   001 00 80                              wordfit -$8000
            00008 00008   002 FF FF                              wordfit $FFFF
            0000A 0000A       00 00          Padding
            """, compile(text));

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile("""
                    DAT             org   $000
                                    wordfit -$8001
                    """);
            }
        });

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile("""
                    DAT             org   $000
                                    wordfit $10000
                    """);
            }
        });
    }

    @Test
    void testLongLiteralRet() throws Exception {
        String text = """
            DAT             org   $000
                            mov   1, ##0
                    _ret_   mov   1, ##0
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 00 00 00 FF                        mov     1, ##0
            00004 00004   001 00 02 04 F6
            00008 00008   002 00 00 00 FF            _ret_       mov     1, ##0
            0000C 0000C   003 00 02 04 06
            """, compile(text));
    }

    @Test
    void testDebug() throws Exception {
        String text = """
            DAT             org   $000
                            mov   a, #1
                            debug(udec(a))
                if_c        debug(udec(a))
                __ret__     debug(udec(a))
                            ret
            a               res   1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 01 0C 04 F6                        mov     a, #1
            00004 00004   001 36 02 64 FD                        debug(udec(a))
            00008 00008   002 31 02 64 3D            if_c        debug(udec(a))
            0000C 0000C   003 36 02 64 FD
            00010 00010   004 36 02 64 FD    __ret__             debug(udec(a))
            00014 00014   005 2D 00 64 FD                        ret
            00018 00018   006                a                   res     1
            ' Debug data
            00B74 00000       0C 00        \s
            00B76 00002       04 00          #1@0004
            ' #1
            00B78 00004       01             ASMMODE
            00B79 00005       04             COGN
            00B7A 00006       41 61 00 80 06 UDEC(a)
            00B7F 0000B       00             DONE
            """, compile(text, true));
    }

    @Test
    void testIgnoreDebug() throws Exception {
        String text = """
            DAT             org   $000
                            mov   a, #1
                            debug(udec(a))
                            ret
            a               res   1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 01 04 04 F6                        mov     a, #1
            00004 00004   001                                    debug(udec(a))
            00004 00004   001 2D 00 64 FD                        ret
            00008 00008   002                a                   res     1
            """, compile(text, false));
    }

    @Test
    void testThrowDebugException() throws Exception {
        String text = """
            DAT             org   $000
                            mov   a, #1
                            debug(udec(b))
                            ret
            a               res   1
            """;

        Assertions.assertThrows(CompilerException.class, () -> {
            compile(text, false);
        });
    }

    @Test
    void testKeepDebugLabel() throws Exception {
        String text = """
            DAT             org   $000
                            mov   a, #label
            label           debug(udec(a))
                            mov   a, #label + 1
                            ret
            a               res   1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 01 06 04 F6                        mov     a, #label
            00004 00004   001                label               debug(udec(a))
            00004 00004   001 02 06 04 F6                        mov     a, #label + 1
            00008 00008   002 2D 00 64 FD                        ret
            0000C 0000C   003                a                   res     1
            """, compile(text, false));
    }

    @Test
    void testDollarSymbol() throws Exception {
        String text = """
            DAT             org   $000
            
            start
                            mov     a, ##123
            
                            drvnot  #56
                            jmp     #$-1
            
            a               res 1
            b               res 1
            """;

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2Compiler(), new File("test.spin2"));
        compiler.compileObject(text);

        Assertions.assertEquals(0x000L, compiler.source.get(0).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x000L, compiler.source.get(1).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x000L, compiler.source.get(2).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x002L, compiler.source.get(3).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x003L, compiler.source.get(4).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x004L, compiler.source.get(5).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x005L, compiler.source.get(6).getScope().getSymbol("$").getNumber());
    }

    @Test
    void testCompilePtr() throws Exception {
        String text = """
            DAT    wrlong 0,ptra
                   wrlong 0,ptra++
                   wrlong 0,++ptra
                   wrlong 0,ptra[3]
                   wrlong 0,ptra--[3]
                   wrlong 0,--ptra[3]
            
                   mov    0,#ptra+1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000 00000 00 01 64 FC                        wrlong  0, ptra
            00004 00004 00004 61 01 64 FC                        wrlong  0, ptra++
            00008 00008 00008 41 01 64 FC                        wrlong  0, ++ptra
            0000C 0000C 0000C 03 01 64 FC                        wrlong  0, ptra[3]
            00010 00010 00010 7D 01 64 FC                        wrlong  0, ptra--[3]
            00014 00014 00014 5D 01 64 FC                        wrlong  0, --ptra[3]
            00018 00018 00018 F9 01 04 F6                        mov     0, #ptra + 1
            """, compile(text));
    }

    @Test
    void testClkFreq() throws Exception {
        String text = """
            _CLKFREQ = 250_000_000
            
            DAT     org    $000
            
                    mov    a, ##_CLKFREQ
            
            a       res    1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 59 73 07 FF                        mov     a, ##_CLKFREQ
            00004 00004   001 80 04 04 F6
            00008 00008   002                a                   res     1
            """, compile(text));
    }

    @Test
    void testPtr() throws Exception {
        String text = """
            DAT     org    $000
            
                    rdlong a, ptra
                    rdlong a, ptrb
            
                    rdlong a, ptra++
                    rdlong a, ptrb++
                    rdlong a, ptra--
                    rdlong a, ptrb--
            
                    rdlong a, ++ptra
                    rdlong a, ++ptrb
                    rdlong a, --ptra
                    rdlong a, --ptrb
            
            a       res    1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 00 15 04 FB                        rdlong  a, ptra
            00004 00004   001 80 15 04 FB                        rdlong  a, ptrb
            00008 00008   002 61 15 04 FB                        rdlong  a, ptra++
            0000C 0000C   003 E1 15 04 FB                        rdlong  a, ptrb++
            00010 00010   004 7F 15 04 FB                        rdlong  a, ptra--
            00014 00014   005 FF 15 04 FB                        rdlong  a, ptrb--
            00018 00018   006 41 15 04 FB                        rdlong  a, ++ptra
            0001C 0001C   007 C1 15 04 FB                        rdlong  a, ++ptrb
            00020 00020   008 5F 15 04 FB                        rdlong  a, --ptra
            00024 00024   009 DF 15 04 FB                        rdlong  a, --ptrb
            00028 00028   00A                a                   res     1
            """, compile(text));
    }

    @Test
    void testPtrOffset() throws Exception {
        String text = """
            DAT     org    $000
            
                    rdlong a, ptra[1]
                    rdlong a, ptra[-1]
                    rdlong a, ptrb[1]
                    rdlong a, ptrb[-1]
            
                    rdlong a, ptra++[1]
                    rdlong a, ptra--[1]
            
                    rdlong a, ++ptra[1]
                    rdlong a, --ptra[1]
            
            a       res    1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 01 11 04 FB                        rdlong  a, ptra[1]
            00004 00004   001 3F 11 04 FB                        rdlong  a, ptra[-1]
            00008 00008   002 81 11 04 FB                        rdlong  a, ptrb[1]
            0000C 0000C   003 BF 11 04 FB                        rdlong  a, ptrb[-1]
            00010 00010   004 61 11 04 FB                        rdlong  a, ptra++[1]
            00014 00014   005 7F 11 04 FB                        rdlong  a, ptra--[1]
            00018 00018   006 41 11 04 FB                        rdlong  a, ++ptra[1]
            0001C 0001C   007 5F 11 04 FB                        rdlong  a, --ptra[1]
            00020 00020   008                a                   res     1
            """, compile(text));
    }

    @Test
    void testPtrAugs() throws Exception {
        String text = """
            DAT     org    $000
            
                    rdlong a, ptra[##0]
                    rdlong a, ptrb[##0]
            
                    rdlong a, ptra[##1]
                    rdlong a, ptrb[##-1]
                    rdlong a, ptra[##1024]
                    rdlong a, ptrb[##-1024]
            
                    rdlong a, ptra++[##1]
                    rdlong a, ptrb++[##-1]
                    rdlong a, ptra++[##1024]
                    rdlong a, ptrb++[##-1024]
            
                    rdlong a, ptra--[##1]
                    rdlong a, ptrb--[##-1]
                    rdlong a, ptra--[##1024]
                    rdlong a, ptrb--[##-1024]
            
                    rdlong a, ++ptra[##1]
                    rdlong a, ++ptrb[##-1]
                    rdlong a, ++ptra[##1024]
                    rdlong a, ++ptrb[##-1024]
            
                    rdlong a, --ptra[##1]
                    rdlong a, --ptrb[##-1]
                    rdlong a, --ptra[##1024]
                    rdlong a, --ptrb[##-1024]
            
            a       res    1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 00 40 00 FF                        rdlong  a, ptra[##0]
            00004 00004   001 00 58 04 FB
            00008 00008   002 00 60 00 FF                        rdlong  a, ptrb[##0]
            0000C 0000C   003 00 58 04 FB
            00010 00010   004 00 40 00 FF                        rdlong  a, ptra[##1]
            00014 00014   005 01 58 04 FB
            00018 00018   006 FF 67 00 FF                        rdlong  a, ptrb[##-1]
            0001C 0001C   007 FF 59 04 FB
            00020 00020   008 02 40 00 FF                        rdlong  a, ptra[##1024]
            00024 00024   009 00 58 04 FB
            00028 00028   00A FE 67 00 FF                        rdlong  a, ptrb[##-1024]
            0002C 0002C   00B 00 58 04 FB
            00030 00030   00C 00 58 00 FF                        rdlong  a, ptra++[##1]
            00034 00034   00D 01 58 04 FB
            00038 00038   00E FF 7F 00 FF                        rdlong  a, ptrb++[##-1]
            0003C 0003C   00F FF 59 04 FB
            00040 00040   010 02 58 00 FF                        rdlong  a, ptra++[##1024]
            00044 00044   011 00 58 04 FB
            00048 00048   012 FE 7F 00 FF                        rdlong  a, ptrb++[##-1024]
            0004C 0004C   013 00 58 04 FB
            00050 00050   014 FF 5F 00 FF                        rdlong  a, ptra--[##1]
            00054 00054   015 FF 59 04 FB
            00058 00058   016 00 78 00 FF                        rdlong  a, ptrb--[##-1]
            0005C 0005C   017 01 58 04 FB
            00060 00060   018 FE 5F 00 FF                        rdlong  a, ptra--[##1024]
            00064 00064   019 00 58 04 FB
            00068 00068   01A 02 78 00 FF                        rdlong  a, ptrb--[##-1024]
            0006C 0006C   01B 00 58 04 FB
            00070 00070   01C 00 50 00 FF                        rdlong  a, ++ptra[##1]
            00074 00074   01D 01 58 04 FB
            00078 00078   01E FF 77 00 FF                        rdlong  a, ++ptrb[##-1]
            0007C 0007C   01F FF 59 04 FB
            00080 00080   020 02 50 00 FF                        rdlong  a, ++ptra[##1024]
            00084 00084   021 00 58 04 FB
            00088 00088   022 FE 77 00 FF                        rdlong  a, ++ptrb[##-1024]
            0008C 0008C   023 00 58 04 FB
            00090 00090   024 FF 57 00 FF                        rdlong  a, --ptra[##1]
            00094 00094   025 FF 59 04 FB
            00098 00098   026 00 70 00 FF                        rdlong  a, --ptrb[##-1]
            0009C 0009C   027 01 58 04 FB
            000A0 000A0   028 FE 57 00 FF                        rdlong  a, --ptra[##1024]
            000A4 000A4   029 00 58 04 FB
            000A8 000A8   02A 02 70 00 FF                        rdlong  a, --ptrb[##-1024]
            000AC 000AC   02B 00 58 04 FB
            000B0 000B0   02C                a                   res     1
            """, compile(text));
    }

    @Test
    void testValueSizeOverride() throws Exception {
        String text = """
            DAT             org     $000
            
                            byte    $FFAA, $BB995511
                            byte    word $FFAA, long $BB995511
                            word    $FFAA, long $BB995511
            
                            bytefit word $FFAA, long $BB995511
                            wordfit $FFAA, long $BB995511
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 AA 11                              byte    $FFAA, $BB995511
            00002 00002   000 AA FF 11 55                        byte    word $FFAA, long $BB995511
            00006 00006   001 99 BB
            00008 00008   002 AA FF 11 55                        word    $FFAA, long $BB995511
            0000C 0000C   003 99 BB
            0000E 0000E   003 AA FF 11 55                        bytefit word $FFAA, long $BB995511
            00012 00012   004 99 BB
            00014 00014   005 AA FF 11 55                        wordfit $FFAA, long $BB995511
            00018 00018   006 99 BB
            0001A 0001A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testAlignment() throws Exception {
        String text = """
            DAT
            
                            byte    1, 2, 3
                            long    4
            
                            byte    5, 6
                            mov     a, b
                           \s
            a               long    1
            b               long    1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000 00000 01 02 03                           byte    1, 2, 3
            00003 00003 00003 04 00 00 00                        long    4
            00007 00007 00007 05 06                              byte    5, 6
            00009 00009 00009 11 1A 00 F6                        mov     a, b
            0000D 0000D 0000D 01 00 00 00    a                   long    1
            00011 00011 00011 01 00 00 00    b                   long    1
            00015 00015       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testOrgAlignment() throws Exception {
        String text = """
            DAT
                            org     $000
            
                            byte    1, 2, 3
                            long    4
            
                            byte    5, 6
                            mov     a, b
                           \s
            a               long    1
            b               long    1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 01 02 03                           byte    1, 2, 3
            00003 00003   000 04 00 00 00                        long    4
            00007 00007   001 05 06                              byte    5, 6
            00009 00009       00 00 00       (filler)
            0000C 0000C   003 05 08 00 F6                        mov     a, b
            00010 00010   004 01 00 00 00    a                   long    1
            00014 00014   005 01 00 00 00    b                   long    1
            """, compile(text));
    }

    @Test
    void testFVarOverride() throws Exception {
        String text = """
            DAT             org     $000
            
                            byte    99, fvar 99
                            byte    -99, fvars -99
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 63 63                              byte    99, fvar 99
            00002 00002   000 9D 9D 7F                           byte    -99, fvars -99
            00005 00005       00 00 00       Padding
            """, compile(text));
    }

    @Test
    void testStringExpressions() throws Exception {
        String text = """
            DAT             org     $000
            
                            byte    "1234" | $80
                            word    "1234" | $80
                            long    "1234" | $80
            
                            byte    "1234" | $180
                            word    "1234" | $180
                            long    "1234" | $180
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 31 32 33 B4                        byte    "1234" | $80
            00004 00004   001 31 00 32 00                        word    "1234" | $80
            00008 00008   002 33 00 B4 00
            0000C 0000C   003 31 00 00 00                        long    "1234" | $80
            00010 00010   004 32 00 00 00  \s
            00014 00014   005 33 00 00 00  \s
            00018 00018   006 B4 00 00 00
            0001C 0001C   007 31 32 33 B4                        byte    "1234" | $180
            00020 00020   008 31 00 32 00                        word    "1234" | $180
            00024 00024   009 33 00 B4 01
            00028 00028   00A 31 00 00 00                        long    "1234" | $180
            0002C 0002C   00B 32 00 00 00  \s
            00030 00030   00C 33 00 00 00  \s
            00034 00034   00D B4 01 00 00
            """, compile(text));
    }

    @Test
    void testCallPaPb() throws Exception {
        String text = """
            PUB main()
            
            DAT
            
                            org $000
            
            a               long    0
            
            DAT
            
                            orgh
            
            .loop           callpa a, #.label
                            nop
                            callpb a, #.label
                            nop
                            jmp    #.loop
            
            .label          nop
                            ret
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       28 00 00 80    Method main @ $00028 (0 parameters, 0 returns)
            00004 00004       2A 00 00 00    End
            00008 00008   000                                    org     $000
            00008 00008   000 00 00 00 00    a                   long    0
            0000C 0000C 00400                                    orgh
            0000C 0000C 00400 04 00 44 FB    .loop               callpa  a, #.label
            00010 00010 00404 00 00 00 00                        nop
            00014 00014 00408 02 00 54 FB                        callpb  a, #.label
            00018 00018 0040C 00 00 00 00                        nop
            0001C 0001C 00410 EC FF 9F FD                        jmp     #.loop
            00020 00020 00414 00 00 00 00    .label              nop
            00024 00024 00418 2D 00 64 FD                        ret
            ' PUB main()
            00028 00028       00             (stack size)
            00029 00029       04             RETURN
            0002A 0002A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testPreprocessorConditional() throws Exception {
        String text = """
            DAT             org   $000
            #ifdef _TEST
                            mov   a, #1
            #endif
                            ret
            a               res   1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 2D 00 64 FD                        ret
            00004 00004   001                a                   res     1
            """, compile(text, false));
    }

    @Test
    void testPreprocessorElseConditional() throws Exception {
        String text = """
            DAT             org   $000
            #ifdef _TEST
                            mov   a, #1
            #else
                            mov   a, #2
            #endif
                            ret
            a               res   1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 02 04 04 F6                        mov     a, #2
            00004 00004   001 2D 00 64 FD                        ret
            00008 00008   002                a                   res     1
            """, compile(text, false));
    }

    @Test
    void testPreprocessorNestedConditional() throws Exception {
        String text = """
            DAT             org   $000
            #ifdef _DEBUG
            #ifdef _TEST
                            mov   a, #1
            #else
                            mov   a, #2
            #endif
            #endif
                            ret
            a               res   1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 2D 00 64 FD                        ret
            00004 00004   001                a                   res     1
            """, compile(text, false));
    }

    @Test
    void testStructure() throws Exception {
        String text = """
            CON
                sPoint(word x, word y)
            
            DAT
            pt              sPoint
                            word    1
                            word    2
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000 00000                pt                  sPoint
            00000 00000 00000 01 00                              word    1
            00002 00002 00002 02 00                              word    2
            """, compile(text));
    }

    @Test
    void testStructureRead() throws Exception {
        String text = """
            CON
                sPoint(byte x, word y)
            
            PUB start() | a, b
            
                a := pt.x
                a := pt.y
            
                a := pt[1].x
                a := pt[1].y
            
                a := pt[b].x
                a := pt[b].y
            
            DAT
            pt              sPoint
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            00004 00004       28 00 00 00    End
            00008 00008 00000                pt                  sPoint
            ' PUB start() | a, b
            00008 00008       02             (stack size)
            '     a := pt.x
            00009 00009       4F 08 1C       MEM_READ BYTE PBASE+$00008
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := pt.y
            0000D 0000D       55 09 1C       MEM_READ WORD PBASE+$00009
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := pt[1].x
            00011 00011       4F 0B 1C       MEM_READ BYTE PBASE+$0000B
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := pt[1].y
            00015 00015       55 0C 1C       MEM_READ WORD PBASE+$0000C
            00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := pt[b].x
            00019 00019       E1             VAR_READ LONG DBASE+$00001 (short)
            0001A 0001A       67 85 01 03 1C STRUCT_READ BYTE PBASE+$00008 (indexed)
            0001F 0001F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := pt[b].y
            00020 00020       E1             VAR_READ LONG DBASE+$00001 (short)
            00021 00021       67 99 01 03 1C STRUCT_READ WORD PBASE+$00009 (indexed)
            00026 00026       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00027 00027       04             RETURN
            """, compile(text));
    }

    @Test
    void testDatInstruction() throws Exception {
        String text = """
            DAT entry       org $000
            DAT label       mov a, #1
            a               res 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                entry               org     $000
            00000 00000   000 01 02 04 F6    label               mov     a, #1
            00004 00004   001                a                   res     1
            """, compile(text));
    }

    @Test
    void testDatNameError() throws Exception {
        String text = "DAT entry label org $000\n";

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile(text);
            }

        });
    }

    @Test
    void testNamespace() throws Exception {
        String text = """
            DAT
            
                            org $000
            
                            coginit #1, #@entry
                            coginit #2, #@progb.entry
                            coginit #3, #@progc.entry
            
            DAT
                            org     $000
            entry
                            mov     a, #a
                            mov     a, #a_alias
                            mov     b, #b
                            mov     c, #c
                            ret
            
            a_alias
            a               long    0
            b               res     1
            c
            
            DAT             namesp  progb
                            org     $010
            
            entry
                            mov     a, #a
                            mov     a, #a_alias
                            mov     b, #b
                            mov     c, #c
                            ret
            
            a_alias
            a               long    0
            b               res     1
            c
            DAT             namesp  progc
                            org     $020
            
            entry
                            mov     a, #a
                            mov     a, #a_alias
                            mov     b, #b
                            mov     c, #c
                            ret
            
            a_alias
            a               long    0
            b               res     1
            c
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 0C 02 EC FC                        coginit #1, #@entry
            00004 00004   001 24 04 EC FC                        coginit #2, #@progb.entry
            00008 00008   002 3C 06 EC FC                        coginit #3, #@progc.entry
            0000C 0000C   003                                    org     $000
            0000C 0000C   000                entry              \s
            0000C 0000C   000 05 0A 04 F6                        mov     a, #a
            00010 00010   001 05 0A 04 F6                        mov     a, #a_alias
            00014 00014   002 06 0C 04 F6                        mov     b, #b
            00018 00018   003 07 0E 04 F6                        mov     c, #c
            0001C 0001C   004 2D 00 64 FD                        ret
            00020 00020   005                a_alias            \s
            00020 00020   005 00 00 00 00    a                   long    0
            00024 00024   006                b                   res     1
            00024 00024   007                c                  \s
            00024 00024   007                                    namesp  progb
            00024 00024   007                                    org     $010
            00024 00024   010                entry              \s
            00024 00024   010 15 2A 04 F6                        mov     a, #a
            00028 00028   011 15 2A 04 F6                        mov     a, #a_alias
            0002C 0002C   012 16 2C 04 F6                        mov     b, #b
            00030 00030   013 17 2E 04 F6                        mov     c, #c
            00034 00034   014 2D 00 64 FD                        ret
            00038 00038   015                a_alias            \s
            00038 00038   015 00 00 00 00    a                   long    0
            0003C 0003C   016                b                   res     1
            0003C 0003C   017                c                  \s
            0003C 0003C   017                                    namesp  progc
            0003C 0003C   017                                    org     $020
            0003C 0003C   020                entry              \s
            0003C 0003C   020 25 4A 04 F6                        mov     a, #a
            00040 00040   021 25 4A 04 F6                        mov     a, #a_alias
            00044 00044   022 26 4C 04 F6                        mov     b, #b
            00048 00048   023 27 4E 04 F6                        mov     c, #c
            0004C 0004C   024 2D 00 64 FD                        ret
            00050 00050   025                a_alias            \s
            00050 00050   025 00 00 00 00    a                   long    0
            00054 00054   026                b                   res     1
            00054 00054   027                c                  \s
            """, compile(text));
    }

    @Test
    void testDitto() throws Exception {
        String text = """
            CON
                pin_nco = 0
                pin_base = 8
            
            DAT
            
                            DITTO   8
                            wypin   pin_nco+$$, #pin_base+$$
                            DITTO   END
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000 00000                                    DITTO   8
            00000 00000 00000 08 00 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            00004 00004 00004 09 02 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            00008 00008 00008 0A 04 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            0000C 0000C 0000C 0B 06 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            00010 00010 00010 0C 08 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            00014 00014 00014 0D 0A 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            00018 00018 00018 0E 0C 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            0001C 0001C 0001C 0F 0E 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            00020 00020 00020                                    DITTO   END
            """, compile(text));
    }

    @Test
    void testRelativeJump() throws Exception {
        String text = """
            DAT             org     $000
            
            start
                            add     b, #1
                            djnz    a, #start
            
            a               long    10
            b               long    0
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000                start              \s
            00000 00000   000 01 06 04 F1                        add     b, #1
            00004 00004   001 FE 05 6C FB                        djnz    a, #start
            00008 00008   002 0A 00 00 00    a                   long    10
            0000C 0000C   003 00 00 00 00    b                   long    0
            """, compile(text));
    }

    @Test
    void testRelativeJumpHub() throws Exception {
        String text = """
            PUB main()
            
            DAT             org     $000
            
            a               long    10
            b               long    0
            
            DAT             orgh
            
            start
                            add     b, #1
                            djnz    a, #start
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       18 00 00 80    Method main @ $00018 (0 parameters, 0 returns)
            00004 00004       1A 00 00 00    End
            00008 00008   000                                    org     $000
            00008 00008   000 0A 00 00 00    a                   long    10
            0000C 0000C   001 00 00 00 00    b                   long    0
            00010 00010 00400                                    orgh
            00010 00010 00400                start              \s
            00010 00010 00400 01 02 04 F1                        add     b, #1
            00014 00014 00404 FE 01 6C FB                        djnz    a, #start
            ' PUB main()
            00018 00018       00             (stack size)
            00019 00019       04             RETURN
            0001A 0001A       00 00          Padding
            """, compile(text));
    }

    @Test
    void testRelativeJumpHubUnaligned() throws Exception {
        String text = """
            PUB main()
            
            DAT             org     $000
            
            a               long    10
            b               long    0
            
            DAT             orgh
            
                            word    1
            start
                            add     b, #1
                            djnz    a, #start
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       1A 00 00 80    Method main @ $0001A (0 parameters, 0 returns)
            00004 00004       1C 00 00 00    End
            00008 00008   000                                    org     $000
            00008 00008   000 0A 00 00 00    a                   long    10
            0000C 0000C   001 00 00 00 00    b                   long    0
            00010 00010 00400                                    orgh
            00010 00010 00400 01 00                              word    1
            00012 00012 00402                start              \s
            00012 00012 00402 01 02 04 F1                        add     b, #1
            00016 00016 00406 FE 01 6C FB                        djnz    a, #start
            ' PUB main()
            0001A 0001A       00             (stack size)
            0001B 0001B       04             RETURN
            """, compile(text));
    }

    @Test
    void testDittoInline() throws Exception {
        String text = """
            CON
                pin_nco = 0
                pin_base = 8
            
            PUB main()
            
                    org
                            DITTO   8
                            wypin   pin_nco+$$, #pin_base+$$
                            DITTO   END
                    end
            
                    orgh
                            DITTO   8
                            wypin   pin_nco+$$, #pin_base+$$
                            DITTO   END
                    end
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       5C 00 00 00    End
            ' PUB main()
            00008 00008       00             (stack size)
            '         org
            00009 00009       19 5C 00 00 08 INLINE-EXEC ORG=$000, 9
            0000E 0000E       00
            0000F 0000F   000                                    org
            0000F 0000F   000                                    DITTO   8
            0000F 0000F   000 08 00 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            00013 00013   001 09 02 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            00017 00017   002 0A 04 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            0001B 0001B   003 0B 06 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            0001F 0001F   004 0C 08 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            00023 00023   005 0D 0A 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            00027 00027   006 0E 0C 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            0002B 0002B   007 0F 0E 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            0002F 0002F   008                                    DITTO   END
            0002F 0002F   008 2D 00 64 FD                        ret
            '         orgh
            00033 00033       19 5E 09 00    INLINE-EXEC ORGH 9
            00037 00037   000                                    orgh
            00037 00037   000                                    DITTO   8
            00037 00037   000 08 00 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            0003B 0003B   004 09 02 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            0003F 0003F   008 0A 04 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            00043 00043   00C 0B 06 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            00047 00047   010 0C 08 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            0004B 0004B   014 0D 0A 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            0004F 0004F   018 0E 0C 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            00053 00053   01C 0F 0E 24 FC                        wypin   pin_nco + $$, #pin_base + $$
            00057 00057   020                                    DITTO   END
            00057 00057   020 2D 00 64 FD                        ret
            0005B 0005B       04             RETURN
            """, compile(text));
    }

    @Test
    void testLutInstructions() throws Exception {
        String text = """
            DAT             org     $000
            
                            rdlut   c, b        wz
                            rdlut   0-0, ptra++
                            rdlut   a, ptra[8]  wz
            
                            wrlut   c, b
                            wrlut   0-0, ptra++
                            wrlut   a, ptra[8]
            
            a               res     1
            b               res     1
            c               res     1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000 07 10 A8 FA                        rdlut   c, b            wz
            00004 00004   001 61 01 A4 FA                        rdlut   0 - 0, ptra++
            00008 00008   002 08 0D AC FA                        rdlut   a, ptra[8]      wz
            0000C 0000C   003 07 10 30 FC                        wrlut   c, b
            00010 00010   004 61 01 34 FC                        wrlut   0 - 0, ptra++
            00014 00014   005 08 0D 34 FC                        wrlut   a, ptra[8]
            00018 00018   006                a                   res     1
            00018 00018   007                b                   res     1
            00018 00018   008                c                   res     1
            """, compile(text));
    }

    @Test
    void testNamespaceVar() throws Exception {
        String text = """
            PUB main() | b
            
                b := prog.a
                prog.a := b
            
            DAT
            
                            org $000
                            namesp  prog
            
            a               long    0
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       16 00 00 00    End
            00008 00008   000                                    org     $000
            00008 00008   000                                    namesp  prog
            00008 00008   000 00 00 00 00    a                   long    0
            ' PUB main() | b
            0000C 0000C       01             (stack size)
            '     b := prog.a
            0000D 0000D       5B 08 1C       MEM_READ LONG PBASE+$00008
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     prog.a := b
            00011 00011       E0             VAR_READ LONG DBASE+$00000 (short)
            00012 00012       5B 08 1D       MEM_WRITE LONG PBASE+$00008
            00015 00015       04             RETURN
            00016 00016       00 00          Padding
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
