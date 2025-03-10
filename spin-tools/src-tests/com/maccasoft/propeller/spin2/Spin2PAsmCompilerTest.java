/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.model.Node;

class Spin2PAsmCompilerTest {

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
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000                start               \n"
            + "00000 00000   000 01 04 60 FD                        cogid   a\n"
            + "00004 00004   001 03 04 60 FD                        cogstop a\n"
            + "00008 00008   002                a                   res     1\n"
            + "", compile(text));
    }

    @Test
    void testLocalLabels() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "start\n"
            + "                mov     a, #1\n"
            + "                jmp     #.l2\n"
            + ".l1             add     a, #1\n"
            + "                add     a, #1\n"
            + ".l2             add     a, #1\n"
            + "                jmp     #.l1\n"
            + "\n"
            + "a               res     1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000                start               \n"
            + "00000 00000   000 01 0C 04 F6                        mov     a, #1\n"
            + "00004 00004   001 08 00 90 FD                        jmp     #.l2\n"
            + "00008 00008   002 01 0C 04 F1    .l1                 add     a, #1\n"
            + "0000C 0000C   003 01 0C 04 F1                        add     a, #1\n"
            + "00010 00010   004 01 0C 04 F1    .l2                 add     a, #1\n"
            + "00014 00014   005 F0 FF 9F FD                        jmp     #.l1\n"
            + "00018 00018   006                a                   res     1\n"
            + "", compile(text));
    }

    @Test
    void testAliasedLabels() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "start\n"
            + "                mov     a, #1\n"
            + "                jmp     #.l2\n"
            + ".l1\n"
            + "                add     a, #1\n"
            + "                add     a, #1\n"
            + ".l2\n"
            + "                add     a, #1\n"
            + "                jmp     #.l1\n"
            + "\n"
            + "a               res     1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000                start               \n"
            + "00000 00000   000 01 0C 04 F6                        mov     a, #1\n"
            + "00004 00004   001 08 00 90 FD                        jmp     #.l2\n"
            + "00008 00008   002                .l1                 \n"
            + "00008 00008   002 01 0C 04 F1                        add     a, #1\n"
            + "0000C 0000C   003 01 0C 04 F1                        add     a, #1\n"
            + "00010 00010   004                .l2                 \n"
            + "00010 00010   004 01 0C 04 F1                        add     a, #1\n"
            + "00014 00014   005 F0 FF 9F FD                        jmp     #.l1\n"
            + "00018 00018   006                a                   res     1\n"
            + "", compile(text));
    }

    @Test
    void testDuplicatedLocalLabels() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "start\n"
            + "                mov     a, #1\n"
            + "                jmp     #.l2\n"
            + ".l1             add     a, #1\n"
            + "                add     a, #1\n"
            + ".l2             add     a, #1\n"
            + "                jmp     #.l1\n"
            + "\n"
            + "setup\n"
            + "                mov     a, #1\n"
            + "                jmp     #.l2\n"
            + ".l1             add     a, #1\n"
            + "                add     a, #1\n"
            + ".l2             add     a, #1\n"
            + "                jmp     #.l1\n"
            + "\n"
            + "a               res     1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000                start               \n"
            + "00000 00000   000 01 18 04 F6                        mov     a, #1\n"
            + "00004 00004   001 08 00 90 FD                        jmp     #.l2\n"
            + "00008 00008   002 01 18 04 F1    .l1                 add     a, #1\n"
            + "0000C 0000C   003 01 18 04 F1                        add     a, #1\n"
            + "00010 00010   004 01 18 04 F1    .l2                 add     a, #1\n"
            + "00014 00014   005 F0 FF 9F FD                        jmp     #.l1\n"
            + "00018 00018   006                setup               \n"
            + "00018 00018   006 01 18 04 F6                        mov     a, #1\n"
            + "0001C 0001C   007 08 00 90 FD                        jmp     #.l2\n"
            + "00020 00020   008 01 18 04 F1    .l1                 add     a, #1\n"
            + "00024 00024   009 01 18 04 F1                        add     a, #1\n"
            + "00028 00028   00A 01 18 04 F1    .l2                 add     a, #1\n"
            + "0002C 0002C   00B F0 FF 9F FD                        jmp     #.l1\n"
            + "00030 00030   00C                a                   res     1\n"
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
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 00 00 00 00                        long    0\n"
            + "00004 00004   001 01 00 00 00                        long    1\n"
            + "00008 00008   002                                    org     $100\n"
            + "00008 00008   100 02 00 00 00                        long    2\n"
            + "0000C 0000C   101 03 00 00 00                        long    3\n"
            + "00010 00010   102 04 00 00 00                        long    4\n"
            + "", compile(text));
    }

    @Test
    void testOrgf() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "label0          long    1\n"
            + "                long    2\n"
            + "                orgf  $10\n"
            + "label10         long    2\n"
            + "                long    3\n"
            + "                long    4\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 01 00 00 00    label0              long    1\n"
            + "00004 00004   001 02 00 00 00                        long    2\n"
            + "00008 00008   002 00 00 00 00                        orgf    $10\n"
            + "0000C 0000C   003 00 00 00 00   \n"
            + "00010 00010   004 00 00 00 00   \n"
            + "00014 00014   005 00 00 00 00   \n"
            + "00018 00018   006 00 00 00 00   \n"
            + "0001C 0001C   007 00 00 00 00   \n"
            + "00020 00020   008 00 00 00 00   \n"
            + "00024 00024   009 00 00 00 00   \n"
            + "00028 00028   00A 00 00 00 00   \n"
            + "0002C 0002C   00B 00 00 00 00   \n"
            + "00030 00030   00C 00 00 00 00   \n"
            + "00034 00034   00D 00 00 00 00   \n"
            + "00038 00038   00E 00 00 00 00   \n"
            + "0003C 0003C   00F 00 00 00 00\n"
            + "00040 00040   010 02 00 00 00    label10             long    2\n"
            + "00044 00044   011 03 00 00 00                        long    3\n"
            + "00048 00048   012 04 00 00 00                        long    4\n"
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
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 00 00 00 00                        long    0\n"
            + "00004 00004   001                                    res\n"
            + "00004 00004   002                                    res     1\n"
            + "00004 00004   003                                    res     2\n"
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
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 00 00 00 00                        long    0[$10]\n"
            + "00004 00004   001 00 00 00 00   \n"
            + "00008 00008   002 00 00 00 00   \n"
            + "0000C 0000C   003 00 00 00 00   \n"
            + "00010 00010   004 00 00 00 00   \n"
            + "00014 00014   005 00 00 00 00   \n"
            + "00018 00018   006 00 00 00 00   \n"
            + "0001C 0001C   007 00 00 00 00   \n"
            + "00020 00020   008 00 00 00 00   \n"
            + "00024 00024   009 00 00 00 00   \n"
            + "00028 00028   00A 00 00 00 00   \n"
            + "0002C 0002C   00B 00 00 00 00   \n"
            + "00030 00030   00C 00 00 00 00   \n"
            + "00034 00034   00D 00 00 00 00   \n"
            + "00038 00038   00E 00 00 00 00   \n"
            + "0003C 0003C   00F 00 00 00 00\n"
            + "00040 00040   010                                    fit     $10\n"
            + "", compile(text));
    }

    @Test
    void testCogFitLimit() throws Exception {
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
    void testLutFitLimit() throws Exception {
        String text = ""
            + "DAT             org   $200\n"
            + "                long    0[$10]\n"
            + "                long    1\n"
            + "                fit   $210\n"
            + "";

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile(text);
            }
        });
    }

    @Test
    void testCogOrgFitLimit() throws Exception {
        String text = ""
            + "DAT             org   $000, $010\n"
            + "                long    0[$10]\n"
            + "                long    1\n"
            + "";

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile(text);
            }
        });
    }

    @Test
    void testLutOrgFitLimit() throws Exception {
        String text = ""
            + "DAT             org   $200, $210\n"
            + "                long    0[$10]\n"
            + "                long    1\n"
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
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 01                                 byte    1\n"
            + "00001 00001   000 02                                 byte    2\n"
            + "00002 00002   000 03 03 04 04                        byte    3[2], 4[3]\n"
            + "00006 00006   001 04                     byte    3[2], 4[3]\n"
            + "00007 00007       00             Padding\n"
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
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 01 00                              word    1\n"
            + "00002 00002   000 02 00                              word    2\n"
            + "00004 00004   001 03 00 03 00                        word    3[2], 4[3]\n"
            + "00008 00008   002 04 00 04 00   \n"
            + "0000C 0000C   003 04 00\n"
            + "0000E 0000E       00 00          Padding\n"
            + "", compile(text));
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
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 01 00 00 00                        long    1\n"
            + "00004 00004   001 02 00 00 00                        long    2\n"
            + "00008 00008   002 03 00 00 00                        long    3[2], 4[3]\n"
            + "0000C 0000C   003 03 00 00 00   \n"
            + "00010 00010   004 04 00 00 00   \n"
            + "00014 00014   005 04 00 00 00   \n"
            + "00018 00018   006 04 00 00 00\n"
            + "", compile(text));
    }

    @Test
    void testDataOverride() throws Exception {
        String text = ""
            + "DAT\n"
            + "                long    1, byte 2, 3, word 4, 5\n"
            + "                word    1, byte 2, 3, long 4, 5\n"
            + "                byte    1, word 2, 3, long 4, 5\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000 00000 01 00 00 00                        long    1, byte 2, 3, word 4, 5\n"
            + "00004 00004 00004 02 03 00 00   \n"
            + "00008 00008 00008 00 04 00 05   \n"
            + "0000C 0000C 0000C 00 00 00\n"
            + "0000F 0000F 0000F 01 00 02 03                        word    1, byte 2, 3, long 4, 5\n"
            + "00013 00013 00013 00 04 00 00   \n"
            + "00017 00017 00017 00 05 00\n"
            + "0001A 0001A 0001A 01 02 00 03                        byte    1, word 2, 3, long 4, 5\n"
            + "0001E 0001E 0001E 04 00 00 00   \n"
            + "00022 00022 00022 05\n"
            + "00023 00023       00             Padding\n"
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
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 00                                 bytefit $00\n"
            + "00001 00001   000 01                                 bytefit $01\n"
            + "00002 00002   000 02                                 bytefit $02\n"
            + "00003 00003   000 80                                 bytefit -$80\n"
            + "00004 00004   001 FF                                 bytefit $FF\n"
            + "00005 00005       00 00 00       Padding\n"
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
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 00 00                              wordfit $0000\n"
            + "00002 00002   000 01 00                              wordfit $0001\n"
            + "00004 00004   001 02 00                              wordfit $0002\n"
            + "00006 00006   001 00 80                              wordfit -$8000\n"
            + "00008 00008   002 FF FF                              wordfit $FFFF\n"
            + "0000A 0000A       00 00          Padding\n"
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
    void testLongLiteralRet() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "                mov   1, ##0\n"
            + "        _ret_   mov   1, ##0\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 00 00 00 FF                        mov     1, ##0\n"
            + "00004 00004   001 00 02 04 F6\n"
            + "00008 00008   002 00 00 00 FF            _ret_       mov     1, ##0\n"
            + "0000C 0000C   003 00 02 04 06\n"
            + "", compile(text));
    }

    @Test
    void testDebug() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "                mov   a, #1\n"
            + "                debug(udec(a))\n"
            + "    if_c        debug(udec(a))\n"
            + "    __ret__     debug(udec(a))\n"
            + "                ret\n"
            + "a               res   1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 01 0C 04 F6                        mov     a, #1\n"
            + "00004 00004   001 36 02 64 FD                        debug(udec(a))\n"
            + "00008 00008   002 31 02 64 3D            if_c        debug(udec(a))\n"
            + "0000C 0000C   003 36 04 64 FD\n"
            + "00010 00010   004 36 06 64 FD    __ret__             debug(udec(a))\n"
            + "00014 00014   005 2D 00 64 FD                        ret\n"
            + "00018 00018   006                a                   res     1\n"
            + "' Debug data\n"
            + "00B74 00000       20 00         \n"
            + "00B76 00002       08 00         \n"
            + "00B78 00004       10 00         \n"
            + "00B7A 00006       18 00         \n"
            + "' #1\n"
            + "00B7C 00008       01             ASMMODE\n"
            + "00B7D 00009       04             COGN\n"
            + "00B7E 0000A       41 61 00 80 06 UDEC(a)\n"
            + "00B83 0000F       00             DONE\n"
            + "' #2\n"
            + "00B84 00010       01             ASMMODE\n"
            + "00B85 00011       04             COGN\n"
            + "00B86 00012       41 61 00 80 06 UDEC(a)\n"
            + "00B8B 00017       00             DONE\n"
            + "' #3\n"
            + "00B8C 00018       01             ASMMODE\n"
            + "00B8D 00019       04             COGN\n"
            + "00B8E 0001A       41 61 00 80 06 UDEC(a)\n"
            + "00B93 0001F       00             DONE\n"
            + "", compile(text, true));
    }

    @Test
    void testIgnoreDebug() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "                mov   a, #1\n"
            + "                debug(udec(a))\n"
            + "                ret\n"
            + "a               res   1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 01 04 04 F6                        mov     a, #1\n"
            + "00004 00004   001                                    debug(udec(a))\n"
            + "00004 00004   001 2D 00 64 FD                        ret\n"
            + "00008 00008   002                a                   res     1\n"
            + "", compile(text, false));
    }

    @Test
    void testThrowDebugException() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "                mov   a, #1\n"
            + "                debug(udec(b))\n"
            + "                ret\n"
            + "a               res   1\n"
            + "";

        Assertions.assertThrows(CompilerException.class, () -> {
            compile(text, false);
        });
    }

    @Test
    void testKeepDebugLabel() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "                mov   a, #label\n"
            + "label           debug(udec(a))\n"
            + "                mov   a, #label + 1\n"
            + "                ret\n"
            + "a               res   1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 01 06 04 F6                        mov     a, #label\n"
            + "00004 00004   001                label               debug(udec(a))\n"
            + "00004 00004   001 02 06 04 F6                        mov     a, #label + 1\n"
            + "00008 00008   002 2D 00 64 FD                        ret\n"
            + "0000C 0000C   003                a                   res     1\n"
            + "", compile(text, false));
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
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 08 04 04 F6                        mov     a, #@@a\n"
            + "00004 00004   001 2D 00 64 FD                        ret\n"
            + "00008 00008   002                a                   res     1\n"
            + "", compile(text, false));
    }

    @Test
    void testDollarSymbol() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "\n"
            + "start\n"
            + "                asmclk\n"
            + "\n"
            + "                drvnot  #56\n"
            + "                jmp     #$-1\n"
            + "";

        Spin2Parser parser = new Spin2Parser(text);
        Node root = parser.parse();

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2Compiler(), new File("test.spin2"));
        compiler.compileObject(root);

        Assertions.assertEquals(0x000L, compiler.source.get(0).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x000L, compiler.source.get(1).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x000L, compiler.source.get(2).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x002L, compiler.source.get(3).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x004L, compiler.source.get(4).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x006L, compiler.source.get(5).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x007L, compiler.source.get(6).getScope().getSymbol("$").getNumber());
    }

    @Test
    void testCompilePtr() throws Exception {
        String text = ""
            + "DAT    wrlong 0,ptra\n"
            + "       wrlong 0,ptra++\n"
            + "       wrlong 0,++ptra\n"
            + "       wrlong 0,ptra[3]\n"
            + "       wrlong 0,ptra--[3]\n"
            + "       wrlong 0,--ptra[3]\n"
            + "\n"
            + "       mov    0,#ptra+1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000 00000 00 01 64 FC                        wrlong  0, ptra\n"
            + "00004 00004 00004 61 01 64 FC                        wrlong  0, ptra++\n"
            + "00008 00008 00008 41 01 64 FC                        wrlong  0, ++ptra\n"
            + "0000C 0000C 0000C 03 01 64 FC                        wrlong  0, ptra[3]\n"
            + "00010 00010 00010 7D 01 64 FC                        wrlong  0, ptra--[3]\n"
            + "00014 00014 00014 5D 01 64 FC                        wrlong  0, --ptra[3]\n"
            + "00018 00018 00018 F9 01 04 F6                        mov     0, #ptra + 1\n"
            + "", compile(text));
    }

    @Test
    void testClkFreq() throws Exception {
        String text = ""
            + "_CLKFREQ = 250_000_000\n"
            + "\n"
            + "DAT     org    $000\n"
            + "\n"
            + "        mov    a, ##_CLKFREQ\n"
            + "\n"
            + "a       res    1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 59 73 07 FF                        mov     a, ##_CLKFREQ\n"
            + "00004 00004   001 80 04 04 F6\n"
            + "00008 00008   002                a                   res     1\n"
            + "", compile(text));
    }

    @Test
    void testPtr() throws Exception {
        String text = ""
            + "DAT     org    $000\n"
            + "\n"
            + "        rdlong a, ptra\n"
            + "        rdlong a, ptrb\n"
            + "\n"
            + "        rdlong a, ptra++\n"
            + "        rdlong a, ptrb++\n"
            + "        rdlong a, ptra--\n"
            + "        rdlong a, ptrb--\n"
            + "\n"
            + "        rdlong a, ++ptra\n"
            + "        rdlong a, ++ptrb\n"
            + "        rdlong a, --ptra\n"
            + "        rdlong a, --ptrb\n"
            + "\n"
            + "a       res    1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 00 15 04 FB                        rdlong  a, ptra\n"
            + "00004 00004   001 80 15 04 FB                        rdlong  a, ptrb\n"
            + "00008 00008   002 61 15 04 FB                        rdlong  a, ptra++\n"
            + "0000C 0000C   003 E1 15 04 FB                        rdlong  a, ptrb++\n"
            + "00010 00010   004 7F 15 04 FB                        rdlong  a, ptra--\n"
            + "00014 00014   005 FF 15 04 FB                        rdlong  a, ptrb--\n"
            + "00018 00018   006 41 15 04 FB                        rdlong  a, ++ptra\n"
            + "0001C 0001C   007 C1 15 04 FB                        rdlong  a, ++ptrb\n"
            + "00020 00020   008 5F 15 04 FB                        rdlong  a, --ptra\n"
            + "00024 00024   009 DF 15 04 FB                        rdlong  a, --ptrb\n"
            + "00028 00028   00A                a                   res     1\n"
            + "", compile(text));
    }

    @Test
    void testPtrOffset() throws Exception {
        String text = ""
            + "DAT     org    $000\n"
            + "\n"
            + "        rdlong a, ptra[1]\n"
            + "        rdlong a, ptrb[-1]\n"
            + "\n"
            + "        rdlong a, ptra[1]++\n"
            + "        rdlong a, ptrb[-1]++\n"
            + "        rdlong a, ptra[1]--\n"
            + "        rdlong a, ptrb[-1]--\n"
            + "\n"
            + "        rdlong a, ++ptra[1]\n"
            + "        rdlong a, ++ptrb[-1]\n"
            + "        rdlong a, --ptra[1]\n"
            + "        rdlong a, --ptrb[-1]\n"
            + "\n"
            + "a       res    1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 01 15 04 FB                        rdlong  a, ptra[1]\n"
            + "00004 00004   001 BF 15 04 FB                        rdlong  a, ptrb[-1]\n"
            + "00008 00008   002 61 15 04 FB                        rdlong  a, ptra++[1]\n"
            + "0000C 0000C   003 FF 15 04 FB                        rdlong  a, ptrb++[-1]\n"
            + "00010 00010   004 7F 15 04 FB                        rdlong  a, ptra--[1]\n"
            + "00014 00014   005 E1 15 04 FB                        rdlong  a, ptrb--[-1]\n"
            + "00018 00018   006 41 15 04 FB                        rdlong  a, ++ptra[1]\n"
            + "0001C 0001C   007 DF 15 04 FB                        rdlong  a, ++ptrb[-1]\n"
            + "00020 00020   008 5F 15 04 FB                        rdlong  a, --ptra[1]\n"
            + "00024 00024   009 C1 15 04 FB                        rdlong  a, --ptrb[-1]\n"
            + "00028 00028   00A                a                   res     1\n"
            + "", compile(text));
    }

    @Test
    void testPtrAugs() throws Exception {
        String text = ""
            + "DAT     org    $000\n"
            + "\n"
            + "        rdlong a, ptra[##0]\n"
            + "        rdlong a, ptrb[##0]\n"
            + "\n"
            + "        rdlong a, ptra[##1]\n"
            + "        rdlong a, ptrb[##-1]\n"
            + "        rdlong a, ptra[##1024]\n"
            + "        rdlong a, ptrb[##-1024]\n"
            + "\n"
            + "        rdlong a, ptra++[##1]\n"
            + "        rdlong a, ptrb++[##-1]\n"
            + "        rdlong a, ptra++[##1024]\n"
            + "        rdlong a, ptrb++[##-1024]\n"
            + "\n"
            + "        rdlong a, ptra--[##1]\n"
            + "        rdlong a, ptrb--[##-1]\n"
            + "        rdlong a, ptra--[##1024]\n"
            + "        rdlong a, ptrb--[##-1024]\n"
            + "\n"
            + "        rdlong a, ++ptra[##1]\n"
            + "        rdlong a, ++ptrb[##-1]\n"
            + "        rdlong a, ++ptra[##1024]\n"
            + "        rdlong a, ++ptrb[##-1024]\n"
            + "\n"
            + "        rdlong a, --ptra[##1]\n"
            + "        rdlong a, --ptrb[##-1]\n"
            + "        rdlong a, --ptra[##1024]\n"
            + "        rdlong a, --ptrb[##-1024]\n"
            + "\n"
            + "a       res    1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 00 40 00 FF                        rdlong  a, ptra[##0]\n"
            + "00004 00004   001 00 58 04 FB\n"
            + "00008 00008   002 00 60 00 FF                        rdlong  a, ptrb[##0]\n"
            + "0000C 0000C   003 00 58 04 FB\n"
            + "00010 00010   004 00 40 00 FF                        rdlong  a, ptra[##1]\n"
            + "00014 00014   005 01 58 04 FB\n"
            + "00018 00018   006 FF 67 00 FF                        rdlong  a, ptrb[##-1]\n"
            + "0001C 0001C   007 FF 59 04 FB\n"
            + "00020 00020   008 02 40 00 FF                        rdlong  a, ptra[##1024]\n"
            + "00024 00024   009 00 58 04 FB\n"
            + "00028 00028   00A FE 67 00 FF                        rdlong  a, ptrb[##-1024]\n"
            + "0002C 0002C   00B 00 58 04 FB\n"
            + "00030 00030   00C 00 58 00 FF                        rdlong  a, ptra++[##1]\n"
            + "00034 00034   00D 01 58 04 FB\n"
            + "00038 00038   00E FF 7F 00 FF                        rdlong  a, ptrb++[##-1]\n"
            + "0003C 0003C   00F FF 59 04 FB\n"
            + "00040 00040   010 02 58 00 FF                        rdlong  a, ptra++[##1024]\n"
            + "00044 00044   011 00 58 04 FB\n"
            + "00048 00048   012 FE 7F 00 FF                        rdlong  a, ptrb++[##-1024]\n"
            + "0004C 0004C   013 00 58 04 FB\n"
            + "00050 00050   014 FF 5F 00 FF                        rdlong  a, ptra--[##1]\n"
            + "00054 00054   015 FF 59 04 FB\n"
            + "00058 00058   016 00 78 00 FF                        rdlong  a, ptrb--[##-1]\n"
            + "0005C 0005C   017 01 58 04 FB\n"
            + "00060 00060   018 FE 5F 00 FF                        rdlong  a, ptra--[##1024]\n"
            + "00064 00064   019 00 58 04 FB\n"
            + "00068 00068   01A 02 78 00 FF                        rdlong  a, ptrb--[##-1024]\n"
            + "0006C 0006C   01B 00 58 04 FB\n"
            + "00070 00070   01C 00 50 00 FF                        rdlong  a, ++ptra[##1]\n"
            + "00074 00074   01D 01 58 04 FB\n"
            + "00078 00078   01E FF 77 00 FF                        rdlong  a, ++ptrb[##-1]\n"
            + "0007C 0007C   01F FF 59 04 FB\n"
            + "00080 00080   020 02 50 00 FF                        rdlong  a, ++ptra[##1024]\n"
            + "00084 00084   021 00 58 04 FB\n"
            + "00088 00088   022 FE 77 00 FF                        rdlong  a, ++ptrb[##-1024]\n"
            + "0008C 0008C   023 00 58 04 FB\n"
            + "00090 00090   024 FF 57 00 FF                        rdlong  a, --ptra[##1]\n"
            + "00094 00094   025 FF 59 04 FB\n"
            + "00098 00098   026 00 70 00 FF                        rdlong  a, --ptrb[##-1]\n"
            + "0009C 0009C   027 01 58 04 FB\n"
            + "000A0 000A0   028 FE 57 00 FF                        rdlong  a, --ptra[##1024]\n"
            + "000A4 000A4   029 00 58 04 FB\n"
            + "000A8 000A8   02A 02 70 00 FF                        rdlong  a, --ptrb[##-1024]\n"
            + "000AC 000AC   02B 00 58 04 FB\n"
            + "000B0 000B0   02C                a                   res     1\n"
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
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 AA 11                              byte    $FFAA, $BB995511\n"
            + "00002 00002   000 AA FF 11 55                        byte    word $FFAA, long $BB995511\n"
            + "00006 00006   001 99 BB\n"
            + "00008 00008   002 AA FF 11 55                        word    $FFAA, long $BB995511\n"
            + "0000C 0000C   003 99 BB\n"
            + "0000E 0000E   003 AA FF 11 55                        bytefit word $FFAA, long $BB995511\n"
            + "00012 00012   004 99 BB\n"
            + "00014 00014   005 AA FF 11 55                        wordfit $FFAA, long $BB995511\n"
            + "00018 00018   006 99 BB\n"
            + "0001A 0001A       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testAlignment() throws Exception {
        String text = ""
            + "DAT\n"
            + "\n"
            + "                byte    1, 2, 3\n"
            + "                long    4\n"
            + "\n"
            + "                byte    5, 6\n"
            + "                mov     a, b\n"
            + "                \n"
            + "a               long    1\n"
            + "b               long    1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000 00000 01 02 03                           byte    1, 2, 3\n"
            + "00003 00003 00003 04 00 00 00                        long    4\n"
            + "00007 00007 00007 05 06                              byte    5, 6\n"
            + "00009 00009 00009 11 1A 00 F6                        mov     a, b\n"
            + "0000D 0000D 0000D 01 00 00 00    a                   long    1\n"
            + "00011 00011 00011 01 00 00 00    b                   long    1\n"
            + "00015 00015       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testOrgAlignment() throws Exception {
        String text = ""
            + "DAT\n"
            + "                org     $000\n"
            + "\n"
            + "                byte    1, 2, 3\n"
            + "                long    4\n"
            + "\n"
            + "                byte    5, 6\n"
            + "                mov     a, b\n"
            + "                \n"
            + "a               long    1\n"
            + "b               long    1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 01 02 03                           byte    1, 2, 3\n"
            + "00003 00003   000 04 00 00 00                        long    4\n"
            + "00007 00007   001 05 06                              byte    5, 6\n"
            + "00009 00009       00 00 00       (filler)\n"
            + "0000C 0000C   003 05 08 00 F6                        mov     a, b\n"
            + "00010 00010   004 01 00 00 00    a                   long    1\n"
            + "00014 00014   005 01 00 00 00    b                   long    1\n"
            + "", compile(text));
    }

    @Test
    void testFVarOverride() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
            + "\n"
            + "                byte    99, fvar 99\n"
            + "                byte    -99, fvars -99\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 63 63                              byte    99, fvar 99\n"
            + "00002 00002   000 9D 9D 7F                           byte    -99, fvars -99\n"
            + "00005 00005       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testStringExpressions() throws Exception {
        String text = ""
            + "DAT             org     $000\n"
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
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 31 32 33 B4                        byte    \"1234\" | $80\n"
            + "00004 00004   001 31 00 32 00                        word    \"1234\" | $80\n"
            + "00008 00008   002 33 00 B4 00\n"
            + "0000C 0000C   003 31 00 00 00                        long    \"1234\" | $80\n"
            + "00010 00010   004 32 00 00 00   \n"
            + "00014 00014   005 33 00 00 00   \n"
            + "00018 00018   006 B4 00 00 00\n"
            + "0001C 0001C   007 31 32 33 B4                        byte    \"1234\" | $180\n"
            + "00020 00020   008 31 00 32 00                        word    \"1234\" | $180\n"
            + "00024 00024   009 33 00 B4 01\n"
            + "00028 00028   00A 31 00 00 00                        long    \"1234\" | $180\n"
            + "0002C 0002C   00B 32 00 00 00   \n"
            + "00030 00030   00C 33 00 00 00   \n"
            + "00034 00034   00D B4 01 00 00\n"
            + "", compile(text));
    }

    @Test
    void testCallPaPb() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "DAT\n"
            + "\n"
            + "                org $000\n"
            + "\n"
            + "a               long    0\n"
            + "\n"
            + "DAT\n"
            + "\n"
            + "                orgh\n"
            + "\n"
            + ".loop           callpa a, #.label\n"
            + "                nop\n"
            + "                callpb a, #.label\n"
            + "                nop\n"
            + "                jmp    #.loop\n"
            + "\n"
            + ".label          nop\n"
            + "                ret\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000       28 00 00 80    Method main @ $00028 (0 parameters, 0 returns)\n"
            + "00004 00004       2A 00 00 00    End\n"
            + "00008 00008   000                                    org     $000\n"
            + "00008 00008   000 00 00 00 00    a                   long    0\n"
            + "0000C 0000C 00400                                    orgh\n"
            + "0000C 0000C 00400 04 00 44 FB    .loop               callpa  a, #.label\n"
            + "00010 00010 00404 00 00 00 00                        nop\n"
            + "00014 00014 00408 02 00 54 FB                        callpb  a, #.label\n"
            + "00018 00018 0040C 00 00 00 00                        nop\n"
            + "0001C 0001C 00410 EC FF 9F FD                        jmp     #.loop\n"
            + "00020 00020 00414 00 00 00 00    .label              nop\n"
            + "00024 00024 00418 2D 00 64 FD                        ret\n"
            + "' PUB main()\n"
            + "00028 00028       00             (stack size)\n"
            + "00029 00029       04             RETURN\n"
            + "0002A 0002A       00 00          Padding\n"
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
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 2D 00 64 FD                        ret\n"
            + "00004 00004   001                a                   res     1\n"
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
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 02 04 04 F6                        mov     a, #2\n"
            + "00004 00004   001 2D 00 64 FD                        ret\n"
            + "00008 00008   002                a                   res     1\n"
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
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 2D 00 64 FD                        ret\n"
            + "00004 00004   001                a                   res     1\n"
            + "", compile(text, false));
    }

    @Test
    void testStructure() throws Exception {
        String text = ""
            + "CON\n"
            + "    sPoint(word x, word y)\n"
            + "\n"
            + "DAT\n"
            + "pt              sPoint  0\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000 00000 00 00 00 00    pt                  sPoint  0\n"
            + "", compile(text));
    }

    @Test
    void testStructureAlias() throws Exception {
        String text = ""
            + "CON\n"
            + "    sPoint(word x, word y)\n"
            + "\n"
            + "DAT\n"
            + "pt              sPoint\n"
            + "                word    1\n"
            + "                word    2\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000 00000                pt                  sPoint\n"
            + "00000 00000 00000 01 00                              word    1\n"
            + "00002 00002 00002 02 00                              word    2\n"
            + "", compile(text));
    }

    @Test
    void testStructureRead() throws Exception {
        String text = ""
            + "CON\n"
            + "    sPoint(byte x, word y)\n"
            + "\n"
            + "PUB start() | a, b\n"
            + "\n"
            + "    a := pt.x\n"
            + "    a := pt.y\n"
            + "\n"
            + "    a := pt[1].x\n"
            + "    a := pt[1].y\n"
            + "\n"
            + "    a := pt[b].x\n"
            + "    a := pt[b].y\n"
            + "\n"
            + "DAT\n"
            + "pt              sPoint\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       28 00 00 00    End\n"
            + "00008 00008 00000                pt                  sPoint\n"
            + "' PUB start() | a, b\n"
            + "00008 00008       02             (stack size)\n"
            + "'     a := pt.x\n"
            + "00009 00009       4F 08 80       MEM_READ BYTE PBASE+$00008\n"
            + "0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := pt.y\n"
            + "0000D 0000D       55 09 80       MEM_READ WORD PBASE+$00009\n"
            + "00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := pt[1].x\n"
            + "00011 00011       4F 0B 80       MEM_READ BYTE PBASE+$0000B\n"
            + "00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := pt[1].y\n"
            + "00015 00015       55 0C 80       MEM_READ WORD PBASE+$0000C\n"
            + "00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := pt[b].x\n"
            + "00019 00019       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0001A 0001A       67 85 01 03 80 STRUCT_READ BYTE PBASE+$00008 (indexed)\n"
            + "0001F 0001F       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := pt[b].y\n"
            + "00020 00020       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00021 00021       67 99 01 03 80 STRUCT_READ WORD PBASE+$00009 (indexed)\n"
            + "00026 00026       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00027 00027       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testDatInstruction() throws Exception {
        String text = ""
            + "DAT entry       org $000\n"
            + "DAT label       mov a, #1\n"
            + "a               res 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                entry               org     $000\n"
            + "00000 00000   000 01 02 04 F6    label               mov     a, #1\n"
            + "00004 00004   001                a                   res     1\n"
            + "", compile(text));
    }

    @Test
    void testDatNameError() throws Exception {
        String text = ""
            + "DAT entry label org $000\n"
            + "";

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile(text);
            }

        });
    }

    @Test
    void testNamespace() throws Exception {
        String text = ""
            + "DAT\n"
            + "\n"
            + "                org $000\n"
            + "\n"
            + "                coginit #1, #@entry\n"
            + "                coginit #2, #@progb.entry\n"
            + "                coginit #3, #@progc.entry\n"
            + "\n"
            + "DAT\n"
            + "                org     $000\n"
            + "entry\n"
            + "                mov     a, #a\n"
            + "                mov     a, #a_alias\n"
            + "                mov     b, #b\n"
            + "                mov     c, #c\n"
            + "                ret\n"
            + "\n"
            + "a_alias\n"
            + "a               long    0\n"
            + "b               res     1\n"
            + "c\n"
            + "\n"
            + "DAT             namesp  progb\n"
            + "                org     $010\n"
            + "\n"
            + "entry\n"
            + "                mov     a, #a\n"
            + "                mov     a, #a_alias\n"
            + "                mov     b, #b\n"
            + "                mov     c, #c\n"
            + "                ret\n"
            + "\n"
            + "a_alias\n"
            + "a               long    0\n"
            + "b               res     1\n"
            + "c\n"
            + "DAT             namesp  progc\n"
            + "                org     $020\n"
            + "\n"
            + "entry\n"
            + "                mov     a, #a\n"
            + "                mov     a, #a_alias\n"
            + "                mov     b, #b\n"
            + "                mov     c, #c\n"
            + "                ret\n"
            + "\n"
            + "a_alias\n"
            + "a               long    0\n"
            + "b               res     1\n"
            + "c\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 0C 02 EC FC                        coginit #1, #@entry\n"
            + "00004 00004   001 24 04 EC FC                        coginit #2, #@progb.entry\n"
            + "00008 00008   002 3C 06 EC FC                        coginit #3, #@progc.entry\n"
            + "0000C 0000C   003                                    org     $000\n"
            + "0000C 0000C   000                entry               \n"
            + "0000C 0000C   000 05 0A 04 F6                        mov     a, #a\n"
            + "00010 00010   001 05 0A 04 F6                        mov     a, #a_alias\n"
            + "00014 00014   002 06 0C 04 F6                        mov     b, #b\n"
            + "00018 00018   003 07 0E 04 F6                        mov     c, #c\n"
            + "0001C 0001C   004 2D 00 64 FD                        ret\n"
            + "00020 00020   005                a_alias             \n"
            + "00020 00020   005 00 00 00 00    a                   long    0\n"
            + "00024 00024   006                b                   res     1\n"
            + "00024 00024   007                c                   \n"
            + "00024 00024   007                                    namesp  progb\n"
            + "00024 00024   007                                    org     $010\n"
            + "00024 00024   010                entry               \n"
            + "00024 00024   010 15 2A 04 F6                        mov     a, #a\n"
            + "00028 00028   011 15 2A 04 F6                        mov     a, #a_alias\n"
            + "0002C 0002C   012 16 2C 04 F6                        mov     b, #b\n"
            + "00030 00030   013 17 2E 04 F6                        mov     c, #c\n"
            + "00034 00034   014 2D 00 64 FD                        ret\n"
            + "00038 00038   015                a_alias             \n"
            + "00038 00038   015 00 00 00 00    a                   long    0\n"
            + "0003C 0003C   016                b                   res     1\n"
            + "0003C 0003C   017                c                   \n"
            + "0003C 0003C   017                                    namesp  progc\n"
            + "0003C 0003C   017                                    org     $020\n"
            + "0003C 0003C   020                entry               \n"
            + "0003C 0003C   020 25 4A 04 F6                        mov     a, #a\n"
            + "00040 00040   021 25 4A 04 F6                        mov     a, #a_alias\n"
            + "00044 00044   022 26 4C 04 F6                        mov     b, #b\n"
            + "00048 00048   023 27 4E 04 F6                        mov     c, #c\n"
            + "0004C 0004C   024 2D 00 64 FD                        ret\n"
            + "00050 00050   025                a_alias             \n"
            + "00050 00050   025 00 00 00 00    a                   long    0\n"
            + "00054 00054   026                b                   res     1\n"
            + "00054 00054   027                c                   \n"
            + "", compile(text));
    }

    String compile(String text) throws Exception {
        return compile(text, false);
    }

    String compile(String text, boolean debugEnabled) throws Exception {
        Spin2Parser parser = new Spin2Parser(text);
        Node root = parser.parse();

        Spin2Compiler compiler = new Spin2Compiler();
        compiler.setDebugEnabled(debugEnabled);
        Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompiler(compiler, new File("test.spin2"));
        Spin2Object obj = objectCompiler.compileObject(root);
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
