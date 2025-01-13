/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.model.Node;

class Spin1PAsmCompilerTest {

    @Test
    void testCompile() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "start\n"
            + "                cogid   a\n"
            + "                cogstop a\n"
            + "\n"
            + "a               res     1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       0C 00          Object size\n"
            + "00002 00002       01             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004   000                                    org     $000\n"
            + "00004 00004   000                start               \n"
            + "00004 00004   000 01 04 FC 0C                        cogid   a\n"
            + "00008 00008   001 03 04 7C 0C                        cogstop a\n"
            + "0000C 0000C   002                a                   res     1\n"
            + "", compile(text));
    }

    @Test
    void testLocalLabels() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "start\n"
            + "                mov     a, #1\n"
            + "                jmp     #:l2\n"
            + ":l1             add     a, #1\n"
            + "                add     a, #1\n"
            + ":l2             add     a, #1\n"
            + "                jmp     #:l1\n"
            + "\n"
            + "a               res     1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       1C 00          Object size\n"
            + "00002 00002       01             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004   000                                    org     $000\n"
            + "00004 00004   000                start               \n"
            + "00004 00004   000 01 0C FC A0                        mov     a, #1\n"
            + "00008 00008   001 04 00 7C 5C                        jmp     #:l2\n"
            + "0000C 0000C   002 01 0C FC 80    :l1                 add     a, #1\n"
            + "00010 00010   003 01 0C FC 80                        add     a, #1\n"
            + "00014 00014   004 01 0C FC 80    :l2                 add     a, #1\n"
            + "00018 00018   005 02 00 7C 5C                        jmp     #:l1\n"
            + "0001C 0001C   006                a                   res     1\n"
            + "", compile(text));
    }

    @Test
    void testAliasedLabels() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "start\n"
            + "                mov     a, #1\n"
            + "                jmp     #:l2\n"
            + ":l1\n"
            + "                add     a, #1\n"
            + "                add     a, #1\n"
            + ":l2\n"
            + "                add     a, #1\n"
            + "                jmp     #:l1\n"
            + "\n"
            + "a               res     1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       1C 00          Object size\n"
            + "00002 00002       01             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004   000                                    org     $000\n"
            + "00004 00004   000                start               \n"
            + "00004 00004   000 01 0C FC A0                        mov     a, #1\n"
            + "00008 00008   001 04 00 7C 5C                        jmp     #:l2\n"
            + "0000C 0000C   002                :l1                 \n"
            + "0000C 0000C   002 01 0C FC 80                        add     a, #1\n"
            + "00010 00010   003 01 0C FC 80                        add     a, #1\n"
            + "00014 00014   004                :l2                 \n"
            + "00014 00014   004 01 0C FC 80                        add     a, #1\n"
            + "00018 00018   005 02 00 7C 5C                        jmp     #:l1\n"
            + "0001C 0001C   006                a                   res     1\n"
            + "", compile(text));
    }

    @Test
    void testDuplicatedLocalLabels() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "start\n"
            + "                mov     a, #1\n"
            + "                jmp     #:l2\n"
            + ":l1             add     a, #1\n"
            + "                add     a, #1\n"
            + ":l2             add     a, #1\n"
            + "                jmp     #:l1\n"
            + "\n"
            + "setup\n"
            + "                mov     a, #1\n"
            + "                jmp     #:l2\n"
            + ":l1             add     a, #1\n"
            + "                add     a, #1\n"
            + ":l2             add     a, #1\n"
            + "                jmp     #:l1\n"
            + "\n"
            + "a               res     1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       34 00          Object size\n"
            + "00002 00002       01             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004   000                                    org     $000\n"
            + "00004 00004   000                start               \n"
            + "00004 00004   000 01 18 FC A0                        mov     a, #1\n"
            + "00008 00008   001 04 00 7C 5C                        jmp     #:l2\n"
            + "0000C 0000C   002 01 18 FC 80    :l1                 add     a, #1\n"
            + "00010 00010   003 01 18 FC 80                        add     a, #1\n"
            + "00014 00014   004 01 18 FC 80    :l2                 add     a, #1\n"
            + "00018 00018   005 02 00 7C 5C                        jmp     #:l1\n"
            + "0001C 0001C   006                setup               \n"
            + "0001C 0001C   006 01 18 FC A0                        mov     a, #1\n"
            + "00020 00020   007 0A 00 7C 5C                        jmp     #:l2\n"
            + "00024 00024   008 01 18 FC 80    :l1                 add     a, #1\n"
            + "00028 00028   009 01 18 FC 80                        add     a, #1\n"
            + "0002C 0002C   00A 01 18 FC 80    :l2                 add     a, #1\n"
            + "00030 00030   00B 08 00 7C 5C                        jmp     #:l1\n"
            + "00034 00034   00C                a                   res     1\n"
            + "", compile(text));
    }

    @Test
    void testOrg() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "                long    0\n"
            + "                long    1\n"
            + "                org   $100\n"
            + "                long    2\n"
            + "                long    3\n"
            + "                long    4\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       18 00          Object size\n"
            + "00002 00002       01             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004   000                                    org     $000\n"
            + "00004 00004   000 00 00 00 00                        long    0\n"
            + "00008 00008   001 01 00 00 00                        long    1\n"
            + "0000C 0000C   002                                    org     $100\n"
            + "0000C 0000C   100 02 00 00 00                        long    2\n"
            + "00010 00010   101 03 00 00 00                        long    3\n"
            + "00014 00014   102 04 00 00 00                        long    4\n"
            + "", compile(text));
    }

    @Test
    void testRes() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "                long    0\n"
            + "                res\n"
            + "                res     1\n"
            + "                res     2\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       08 00          Object size\n"
            + "00002 00002       01             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004   000                                    org     $000\n"
            + "00004 00004   000 00 00 00 00                        long    0\n"
            + "00008 00008   001                                    res\n"
            + "00008 00008   002                                    res     1\n"
            + "00008 00008   003                                    res     2\n"
            + "", compile(text));
    }

    @Test
    void testFit() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "                long    0[$10]\n"
            + "                fit   $10\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       44 00          Object size\n"
            + "00002 00002       01             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004   000                                    org     $000\n"
            + "00004 00004   000 00 00 00 00                        long    0[$10]\n"
            + "00008 00008   001 00 00 00 00   \n"
            + "0000C 0000C   002 00 00 00 00   \n"
            + "00010 00010   003 00 00 00 00   \n"
            + "00014 00014   004 00 00 00 00   \n"
            + "00018 00018   005 00 00 00 00   \n"
            + "0001C 0001C   006 00 00 00 00   \n"
            + "00020 00020   007 00 00 00 00   \n"
            + "00024 00024   008 00 00 00 00   \n"
            + "00028 00028   009 00 00 00 00   \n"
            + "0002C 0002C   00A 00 00 00 00   \n"
            + "00030 00030   00B 00 00 00 00   \n"
            + "00034 00034   00C 00 00 00 00   \n"
            + "00038 00038   00D 00 00 00 00   \n"
            + "0003C 0003C   00E 00 00 00 00   \n"
            + "00040 00040   00F 00 00 00 00\n"
            + "00044 00044   010                                    fit     $10\n"
            + "", compile(text));
    }

    @Test
    void testFitLimit() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "                long    0[$10]\n"
            + "                long    1\n"
            + "                fit   $10\n"
            + "";

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile(text);
            }
        });
    }

    @Test
    void testByte() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "                byte    1\n"
            + "                byte    2\n"
            + "                byte    3[2], 4[3]\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       0C 00          Object size\n"
            + "00002 00002       01             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004   000                                    org     $000\n"
            + "00004 00004   000 01                                 byte    1\n"
            + "00005 00005   000 02                                 byte    2\n"
            + "00006 00006   000 03 03 04 04                        byte    3[2], 4[3]\n"
            + "0000A 0000A   001 04                     byte    3[2], 4[3]\n"
            + "0000B 0000B       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testWord() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "                word    1\n"
            + "                word    2\n"
            + "                word    3[2], 4[3]\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       01             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004   000                                    org     $000\n"
            + "00004 00004   000 01 00                              word    1\n"
            + "00006 00006   000 02 00                              word    2\n"
            + "00008 00008   001 03 00 03 00                        word    3[2], 4[3]\n"
            + "0000C 0000C   002 04 00 04 00   \n"
            + "00010 00010   003 04 00\n"
            + "00012 00012       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testBytefit() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "                bytefit $00\n"
            + "                bytefit $01\n"
            + "                bytefit $02\n"
            + "                bytefit -$80\n"
            + "                bytefit $FF\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       0C 00          Object size\n"
            + "00002 00002       01             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004   000                                    org     $000\n"
            + "00004 00004   000 00                                 bytefit $00\n"
            + "00005 00005   000 01                                 bytefit $01\n"
            + "00006 00006   000 02                                 bytefit $02\n"
            + "00007 00007   000 80                                 bytefit -$80\n"
            + "00008 00008   001 FF                                 bytefit $FF\n"
            + "00009 00009       00 00 00       Padding\n"
            + "", compile(text));

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile(""
                    + "DAT             org   $000\n"
                    + "                bytefit -$81\n"
                    + "");
            }
        });

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile(""
                    + "DAT             org   $000\n"
                    + "                bytefit $100\n"
                    + "");
            }
        });
    }

    @Test
    void testWordfit() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "                wordfit $0000\n"
            + "                wordfit $0001\n"
            + "                wordfit $0002\n"
            + "                wordfit -$8000\n"
            + "                wordfit $FFFF\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       10 00          Object size\n"
            + "00002 00002       01             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004   000                                    org     $000\n"
            + "00004 00004   000 00 00                              wordfit $0000\n"
            + "00006 00006   000 01 00                              wordfit $0001\n"
            + "00008 00008   001 02 00                              wordfit $0002\n"
            + "0000A 0000A   001 00 80                              wordfit -$8000\n"
            + "0000C 0000C   002 FF FF                              wordfit $FFFF\n"
            + "0000E 0000E       00 00          Padding\n"
            + "", compile(text));

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile(""
                    + "DAT             org   $000\n"
                    + "                wordfit -$8001\n"
                    + "");
            }
        });

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile(""
                    + "DAT             org   $000\n"
                    + "                wordfit $10000\n"
                    + "");
            }
        });
    }

    @Test
    void testLong() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "                long    1\n"
            + "                long    2\n"
            + "                long    3[2], 4[3]\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       20 00          Object size\n"
            + "00002 00002       01             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004   000                                    org     $000\n"
            + "00004 00004   000 01 00 00 00                        long    1\n"
            + "00008 00008   001 02 00 00 00                        long    2\n"
            + "0000C 0000C   002 03 00 00 00                        long    3[2], 4[3]\n"
            + "00010 00010   003 03 00 00 00   \n"
            + "00014 00014   004 04 00 00 00   \n"
            + "00018 00018   005 04 00 00 00   \n"
            + "0001C 0001C   006 04 00 00 00\n"
            + "", compile(text));
    }

    @Test
    void testAbsoluteAddress() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "                mov   a, #@@a\n"
            + "                ret\n"
            + "a               res   1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       0C 00          Object size\n"
            + "00002 00002       01             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004   000                                    org     $000\n"
            + "00004 00004   000 0C 04 FC A0                        mov     a, #@@a\n"
            + "00008 00008   001 00 00 7C 5C                        ret\n"
            + "0000C 0000C   002                a                   res     1\n"
            + "", compile(text));
    }

    @Test
    void testValueSizeOverride() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                byte    $FFAA, $BB995511\n"
            + "                byte    word $FFAA, long $BB995511\n"
            + "                word    $FFAA, long $BB995511\n"
            + "\n"
            + "                bytefit word $FFAA, long $BB995511\n"
            + "                wordfit $FFAA, long $BB995511\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       20 00          Object size\n"
            + "00002 00002       01             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004   000                                    org     $000\n"
            + "00004 00004   000 AA 11                              byte    $FFAA, $BB995511\n"
            + "00006 00006   000 AA FF 11 55                        byte    word $FFAA, long $BB995511\n"
            + "0000A 0000A   001 99 BB\n"
            + "0000C 0000C   002 AA FF 11 55                        word    $FFAA, long $BB995511\n"
            + "00010 00010   003 99 BB\n"
            + "00012 00012   003 AA FF 11 55                        bytefit word $FFAA, long $BB995511\n"
            + "00016 00016   004 99 BB\n"
            + "00018 00018   005 AA FF 11 55                        wordfit $FFAA, long $BB995511\n"
            + "0001C 0001C   006 99 BB\n"
            + "0001E 0001E       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testAlignment() throws Exception {
        String text = ""
            + "PUB null\n"
            + "\n"
            + "DAT\n"
            + "\n"
            + "                byte    1, 2, 3\n"
            + "                long    4\n"
            + "\n"
            + "                byte    5, 6\n"
            + "                mov     a, b\n"
            + "                \n"
            + "a               res     1\n"
            + "b               res     1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       1C 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       18 00 00 00    Function null @ $0018 (local size 0)\n"
            + "00008 00008   000 01 02 03                           byte    1, 2, 3\n"
            + "0000B 0000B       00             (filler)\n"
            + "0000C 0000C   001 04 00 00 00                        long    4\n"
            + "00010 00010   002 05 06                              byte    5, 6\n"
            + "00012 00012       00 00          (filler)\n"
            + "00014 00014   003 05 08 BC A0                        mov     a, b\n"
            + "00018 00018   004                a                   res     1\n"
            + "00018 00018   005                b                   res     1\n"
            + "' PUB null\n"
            + "00018 00018       32             RETURN\n"
            + "00019 00019       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testStringExpressions() throws Exception {
        String text = ""
            + "PUB null\n"
            + "\n"
            + "DAT\n"
            + "\n"
            + "                byte    \"1234\" | $80\n"
            + "                word    \"1234\" | $80\n"
            + "                long    \"1234\" | $80\n"
            + "\n"
            + "                byte    \"1234\" | $180\n"
            + "                word    \"1234\" | $180\n"
            + "                long    \"1234\" | $180\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       44 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       40 00 00 00    Function null @ $0040 (local size 0)\n"
            + "00008 00008   000 31 32 33 B4                        byte    \"1234\" | $80\n"
            + "0000C 0000C   001 31 00 32 00                        word    \"1234\" | $80\n"
            + "00010 00010   002 33 00 B4 00\n"
            + "00014 00014   003 31 00 00 00                        long    \"1234\" | $80\n"
            + "00018 00018   004 32 00 00 00   \n"
            + "0001C 0001C   005 33 00 00 00   \n"
            + "00020 00020   006 B4 00 00 00\n"
            + "00024 00024   007 31 32 33 B4                        byte    \"1234\" | $180\n"
            + "00028 00028   008 31 00 32 00                        word    \"1234\" | $180\n"
            + "0002C 0002C   009 33 00 B4 01\n"
            + "00030 00030   00A 31 00 00 00                        long    \"1234\" | $180\n"
            + "00034 00034   00B 32 00 00 00   \n"
            + "00038 00038   00C 33 00 00 00   \n"
            + "0003C 0003C   00D B4 01 00 00\n"
            + "' PUB null\n"
            + "00040 00040       32             RETURN\n"
            + "00041 00041       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testPreprocessorConditional() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "#ifdef _TEST\n"
            + "                mov   a, #1\n"
            + "#endif\n"
            + "                ret\n"
            + "a               res   1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       08 00          Object size\n"
            + "00002 00002       01             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004   000                                    org     $000\n"
            + "00004 00004   000 00 00 7C 5C                        ret\n"
            + "00008 00008   001                a                   res     1\n"
            + "", compile(text, false));
    }

    @Test
    void testPreprocessorElseConditional() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "#ifdef _TEST\n"
            + "                mov   a, #1\n"
            + "#else\n"
            + "                mov   a, #2\n"
            + "#endif\n"
            + "                ret\n"
            + "a               res   1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       0C 00          Object size\n"
            + "00002 00002       01             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004   000                                    org     $000\n"
            + "00004 00004   000 02 04 FC A0                        mov     a, #2\n"
            + "00008 00008   001 00 00 7C 5C                        ret\n"
            + "0000C 0000C   002                a                   res     1\n"
            + "", compile(text, false));
    }

    @Test
    void testPreprocessorNestedConditional() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "#ifdef _DEBUG\n"
            + "#ifdef _TEST\n"
            + "                mov   a, #1\n"
            + "#else\n"
            + "                mov   a, #2\n"
            + "#endif\n"
            + "#endif\n"
            + "                ret\n"
            + "a               res   1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       08 00          Object size\n"
            + "00002 00002       01             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004   000                                    org     $000\n"
            + "00004 00004   000 00 00 7C 5C                        ret\n"
            + "00008 00008   001                a                   res     1\n"
            + "", compile(text, false));
    }

    String compile(String text) throws Exception {
        return compile(text, false);
    }

    String compile(String text, boolean openspinCompatible) throws Exception {
        Spin1TokenStream stream = new Spin1TokenStream(text);
        Spin1Parser subject = new Spin1Parser(stream);
        Node root = subject.parse();

        Spin1ObjectCompiler compiler = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        Spin1Object obj = compiler.compileObject(root);

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
