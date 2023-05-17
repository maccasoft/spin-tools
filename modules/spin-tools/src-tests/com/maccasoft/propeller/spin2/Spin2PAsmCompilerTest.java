/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.ByteArrayOutputStream;
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
            + "DAT             org   $000\n"
            + "                long    0\n"
            + "                res    1\n"
            + "                res    2\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 00 00 00 00                        long    0\n"
            + "00004 00004   001                                    res     1\n"
            + "00004 00004   002                                    res     2\n"
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
            + "                byte    0\n"
            + "                byte    1\n"
            + "                byte    2\n"
            + "                byte    3\n"
            + "                byte    4\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 00                                 byte    0\n"
            + "00001 00001   000 01                                 byte    1\n"
            + "00002 00002   000 02                                 byte    2\n"
            + "00003 00003   000 03                                 byte    3\n"
            + "00004 00004   001 04                                 byte    4\n"
            + "", compile(text));
    }

    @Test
    void testWord() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "                word    0\n"
            + "                word    1\n"
            + "                word    2\n"
            + "                word    3\n"
            + "                word    4\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 00 00                              word    0\n"
            + "00002 00002   000 01 00                              word    1\n"
            + "00004 00004   001 02 00                              word    2\n"
            + "00006 00006   001 03 00                              word    3\n"
            + "00008 00008   002 04 00                              word    4\n"
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
    void testPAsmDebug() throws Exception {
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
            + "00000 00000   000 01 06 04 F6                        mov     a, #1\n"
            + "00004 00004   001 36 02 64 FD                        debug   #1\n"
            + "00008 00008   002 2D 00 64 FD                        ret\n"
            + "0000C 0000C   003                a                   res     1\n"
            + "' Debug data\n"
            + "00B24 00000       0C 00         \n"
            + "00B26 00002       04 00         \n"
            + "00B28 00004       01 04 41 61 00\n"
            + "00B2D 00009       80 03 00\n"
            + "", compile(text, true));
    }

    @Test
    void testIgnorePAsmDebug() throws Exception {
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
            + "00004 00004   001 2D 00 64 FD                        ret\n"
            + "00008 00008   002                a                   res     1\n"
            + "", compile(text, false));
    }

    @Test
    void testKeepPAsmDebugLabel() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "                mov   a, #1\n"
            + "label           debug(udec(a))\n"
            + "                ret\n"
            + "a               res   1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 01 04 04 F6                        mov     a, #1\n"
            + "00004 00004   001                label               \n"
            + "00004 00004   001 2D 00 64 FD                        ret\n"
            + "00008 00008   002                a                   res     1\n"
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

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2Compiler());
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

    String compile(String text) throws Exception {
        return compile(text, false);
    }

    String compile(String text, boolean debugEnabled) throws Exception {
        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Spin2Compiler compiler = new Spin2Compiler();
        compiler.setDebugEnabled(debugEnabled);
        Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompiler(compiler);
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
