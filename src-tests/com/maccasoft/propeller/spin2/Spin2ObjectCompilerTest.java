/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin2.Spin2ObjectCompiler.ObjectInfo;

class Spin2ObjectCompilerTest {

    @Test
    void testClockModeDefault() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2GlobalContext(), Collections.emptyMap());
        compiler.compileObject(root);

        Assertions.assertEquals(0b00_00, compiler.scope.getLocalSymbol("CLKMODE_").getNumber().intValue() & 0b11_11);
        Assertions.assertEquals(20_000_000, compiler.scope.getLocalSymbol("CLKFREQ_").getNumber().intValue());
    }

    @Test
    void testClkFreqClockMode() throws Exception {
        String text = ""
            + "_CLKFREQ = 250_000_000\n"
            + "PUB main()\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2GlobalContext(), Collections.emptyMap());
        compiler.compileObject(root);

        Assertions.assertEquals(250_000_000, compiler.scope.getLocalSymbol("CLKFREQ_").getNumber().intValue());
        Assertions.assertEquals(0b10_11, compiler.scope.getLocalSymbol("CLKMODE_").getNumber().intValue() & 0b11_11);
    }

    @Test
    void testXtlFreq1() throws Exception {
        String text = ""
            + "_XTLFREQ = 12_000_000\n"
            + "_CLKFREQ = 148_500_000\n"
            + "PUB main()\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2GlobalContext(), Collections.emptyMap());
        compiler.compileObject(root);

        Assertions.assertEquals(148_500_000, compiler.scope.getLocalSymbol("CLKFREQ_").getNumber().intValue());
        Assertions.assertEquals("011C62FF", String.format("%08X", compiler.scope.getLocalSymbol("CLKMODE_").getNumber().intValue()));
    }

    @Test
    void testXtlFreq2() throws Exception {
        String text = ""
            + "_XTLFREQ = 20_000_000\n"
            + "_CLKFREQ = 100_000_000\n"
            + "PUB main()\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2GlobalContext(), Collections.emptyMap());
        compiler.compileObject(root);

        Assertions.assertEquals(100_000_000, compiler.scope.getLocalSymbol("CLKFREQ_").getNumber().intValue());
        Assertions.assertEquals("0100090B", String.format("%08X", compiler.scope.getLocalSymbol("CLKMODE_").getNumber().intValue()));
    }

    @Test
    void testXtlFreqClockMode() throws Exception {
        String text = ""
            + "_XTLFREQ = 16_000_000\n"
            + "PUB main()\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2GlobalContext(), Collections.emptyMap());
        compiler.compileObject(root);

        Assertions.assertEquals(16_000_000, compiler.scope.getLocalSymbol("CLKFREQ_").getNumber().intValue());
        Assertions.assertEquals("0000000A", String.format("%08X", compiler.scope.getLocalSymbol("CLKMODE_").getNumber().intValue()));
    }

    @Test
    void testXinFreq() throws Exception {
        String text = ""
            + "_XINFREQ = 32_000_000\n"
            + "_CLKFREQ = 297_500_000\n"
            + "PUB main()\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2GlobalContext(), Collections.emptyMap());
        compiler.compileObject(root);

        Assertions.assertEquals(297_500_000, compiler.scope.getLocalSymbol("CLKFREQ_").getNumber().intValue());
        Assertions.assertEquals("01FE52F7", String.format("%08X", compiler.scope.getLocalSymbol("CLKMODE_").getNumber().intValue()));
    }

    @Test
    void testXinFreqClockMode() throws Exception {
        String text = ""
            + "_XINFREQ = 16_000_000\n"
            + "PUB main()\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2GlobalContext(), Collections.emptyMap());
        compiler.compileObject(root);

        Assertions.assertEquals(16_000_000, compiler.scope.getLocalSymbol("CLKFREQ_").getNumber().intValue());
        Assertions.assertEquals("00000006", String.format("%08X", compiler.scope.getLocalSymbol("CLKMODE_").getNumber().intValue()));
    }

    @Test
    void testRcSlow() throws Exception {
        String text = ""
            + "_RCSLOW\n"
            + "PUB main()\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2GlobalContext(), Collections.emptyMap());
        compiler.compileObject(root);

        Assertions.assertEquals(20_000, compiler.scope.getLocalSymbol("CLKFREQ_").getNumber().intValue());
        Assertions.assertEquals("00000001", String.format("%08X", compiler.scope.getLocalSymbol("CLKMODE_").getNumber().intValue()));
    }

    @Test
    void testRcFast() throws Exception {
        String text = ""
            + "_RCFAST\n"
            + "PUB main()\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2GlobalContext(), Collections.emptyMap());
        compiler.compileObject(root);

        Assertions.assertEquals(20_000_000, compiler.scope.getLocalSymbol("CLKFREQ_").getNumber().intValue());
        Assertions.assertEquals("00000000", String.format("%08X", compiler.scope.getLocalSymbol("CLKMODE_").getNumber().intValue()));
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

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(new Spin2GlobalContext(), Collections.emptyMap());
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
    void testCompilePtr() {
        Spin2ObjectCompiler subject = new Spin2ObjectCompiler(new Spin2GlobalContext(), Collections.emptyMap());

        Spin2Parser parser = new Spin2Parser(new Spin2TokenStream(""
            + "DAT    wrlong 0,ptra\n"
            + "       wrlong 0,ptra++\n"
            + "       wrlong 0,++ptra\n"
            + "       wrlong 0,ptra[3]\n"
            + "       wrlong 0,ptra--[3]\n"
            + "       wrlong 0,--ptra[3]\n"
            + ""));

        Node root = parser.parse();
        DataNode data0 = (DataNode) root.getChild(0);

        Spin2PAsmLine line = subject.compileDataLine((DataLineNode) data0.getChild(0));
        Assertions.assertEquals("ptra", line.getArguments().get(1).getExpression().toString());
        Assertions.assertEquals(
            Spin2InstructionObject.decodeToString(0b1111_1100011_001_000000000_100000000),
            Spin2InstructionObject.decodeToString(line.getInstructionObject().getBytes()));

        line = subject.compileDataLine((DataLineNode) data0.getChild(1));
        Assertions.assertEquals("ptra++", line.getArguments().get(1).getExpression().toString());
        Assertions.assertEquals(
            Spin2InstructionObject.decodeToString(0b1111_1100011_001_000000000_101100001),
            Spin2InstructionObject.decodeToString(line.getInstructionObject().getBytes()));

        line = subject.compileDataLine((DataLineNode) data0.getChild(2));
        Assertions.assertEquals("++ptra", line.getArguments().get(1).getExpression().toString());
        Assertions.assertEquals(
            Spin2InstructionObject.decodeToString(0b1111_1100011_001_000000000_101000001),
            Spin2InstructionObject.decodeToString(line.getInstructionObject().getBytes()));

        line = subject.compileDataLine((DataLineNode) data0.getChild(3));
        Assertions.assertEquals("ptra", line.getArguments().get(1).getExpression().toString());
        Assertions.assertEquals(3, line.getArguments().get(1).getCount());
        Assertions.assertEquals(
            Spin2InstructionObject.decodeToString(0b1111_1100011_001_000000000_100000011),
            Spin2InstructionObject.decodeToString(line.getInstructionObject().getBytes()));

        line = subject.compileDataLine((DataLineNode) data0.getChild(4));
        Assertions.assertEquals("ptra--", line.getArguments().get(1).getExpression().toString());
        Assertions.assertEquals(3, line.getArguments().get(1).getCount());
        Assertions.assertEquals(
            Spin2InstructionObject.decodeToString(0b1111_1100011_001_000000000_101111101),
            Spin2InstructionObject.decodeToString(line.getInstructionObject().getBytes()));

        line = subject.compileDataLine((DataLineNode) data0.getChild(5));
        Assertions.assertEquals("--ptra", line.getArguments().get(1).getExpression().toString());
        Assertions.assertEquals(3, line.getArguments().get(1).getCount());
        Assertions.assertEquals(
            Spin2InstructionObject.decodeToString(0b1111_1100011_001_000000000_101011101),
            Spin2InstructionObject.decodeToString(line.getInstructionObject().getBytes()));
    }

    @Test
    void testEmptyMethod() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       0A 00 00 00    End\n"
            + "' PUB main()\n"
            + "00008 00008       00             (stack size)\n"
            + "00009 00009       04             RETURN\n"
            + "0000A 0000A       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testLocalVarAssignment() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       0C 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     a := 1\n"
            + "00009 00009       A2             CONSTANT (1)\n"
            + "0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000B 0000B       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testGlobalVarAssignment() throws Exception {
        String text = ""
            + "VAR\n"
            + "\n"
            + "    long a\n"
            + "    byte b\n"
            + "    word c\n"
            + "    long d\n"
            + "\n"
            + "PUB main()\n"
            + "\n"
            + "    a := 1\n"
            + "    b := 2\n"
            + "    c := 3\n"
            + "    d := 4\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       19 00 00 00    End\n"
            + "' PUB main()\n"
            + "00008 00008       00             (stack size)\n"
            + "'     a := 1\n"
            + "00009 00009       A2             CONSTANT (1)\n"
            + "0000A 0000A       C1 81          VAR_WRITE LONG VBASE+$00001 (short)\n"
            + "'     b := 2\n"
            + "0000C 0000C       A3             CONSTANT (2)\n"
            + "0000D 0000D       51 08 81       VAR_WRITE BYTE VBASE+$00008\n"
            + "'     c := 3\n"
            + "00010 00010       A4             CONSTANT (3)\n"
            + "00011 00011       57 09 81       VAR_WRITE WORD VBASE+$00009\n"
            + "'     d := 4\n"
            + "00014 00014       A5             CONSTANT (4)\n"
            + "00015 00015       5D 0B 81       VAR_WRITE LONG VBASE+$0000B\n"
            + "00018 00018       04             RETURN\n"
            + "00019 00019       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testExpressionAssignment() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    a := 1 + b * 3\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       10 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     a := 1 + b * 3\n"
            + "00009 00009       A2             CONSTANT (1)\n"
            + "0000A 0000A       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0000B 0000B       A4             CONSTANT (3)\n"
            + "0000C 0000C       96             MULTIPLY\n"
            + "0000D 0000D       8A             ADD\n"
            + "0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testConstantExpressionAssignment() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    a := 1 + 2 * 3\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       0C 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     a := 1 + 2 * 3\n"
            + "00009 00009       A8             CONSTANT (1 + 2 * 3)\n"
            + "0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000B 0000B       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testCharacterAssigment() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    a := \"1\"\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       0D 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     a := \"1\"\n"
            + "00009 00009       45 31          CONSTANT (\"1\")\n"
            + "0000B 0000B       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       04             RETURN\n"
            + "0000D 0000D       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testIfConditional() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    if a == 0\n"
            + "        a := 1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       11 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     if a == 0\n"
            + "00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       A1             CONSTANT (0)\n"
            + "0000B 0000B       70             EQUAL\n"
            + "0000C 0000C       13 03          JZ $00010 (3)\n"
            + "'         a := 1\n"
            + "0000E 0000E       A2             CONSTANT (1)\n"
            + "0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00010 00010       04             RETURN\n"
            + "00011 00011       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testIfConditionExpression() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    if (a := b) == 0\n"
            + "        a := 1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       13 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     if (a := b) == 0\n"
            + "00009 00009       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0000A 0000A       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000B 0000B       82             WRITE\n"
            + "0000C 0000C       A1             CONSTANT (0)\n"
            + "0000D 0000D       70             EQUAL\n"
            + "0000E 0000E       13 03          JZ $00012 (3)\n"
            + "'         a := 1\n"
            + "00010 00010       A2             CONSTANT (1)\n"
            + "00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00012 00012       04             RETURN\n"
            + "00013 00013       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testIfElseConditional() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    if a == 0\n"
            + "        a := 1\n"
            + "    else\n"
            + "        a := 2\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       15 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     if a == 0\n"
            + "00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       A1             CONSTANT (0)\n"
            + "0000B 0000B       70             EQUAL\n"
            + "0000C 0000C       13 05          JZ $00012 (5)\n"
            + "'         a := 1\n"
            + "0000E 0000E       A2             CONSTANT (1)\n"
            + "0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00010 00010       12 03          JMP $00014 (3)\n"
            + "'     else\n"
            + "'         a := 2\n"
            + "00012 00012       A3             CONSTANT (2)\n"
            + "00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00014 00014       04             RETURN\n"
            + "00015 00015       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testIfElseIfConditional() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    if a == 0\n"
            + "        a := 1\n"
            + "    elseif a == 1\n"
            + "        a := 2\n"
            + "    elseif a == 2\n"
            + "        a := 3\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       23 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     if a == 0\n"
            + "00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       A1             CONSTANT (0)\n"
            + "0000B 0000B       70             EQUAL\n"
            + "0000C 0000C       13 05          JZ $00012 (5)\n"
            + "'         a := 1\n"
            + "0000E 0000E       A2             CONSTANT (1)\n"
            + "0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00010 00010       12 11          JMP $00022 (17)\n"
            + "'     elseif a == 1\n"
            + "00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00013 00013       A2             CONSTANT (1)\n"
            + "00014 00014       70             EQUAL\n"
            + "00015 00015       13 05          JZ $0001B (5)\n"
            + "'         a := 2\n"
            + "00017 00017       A3             CONSTANT (2)\n"
            + "00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00019 00019       12 08          JMP $00022 (8)\n"
            + "'     elseif a == 2\n"
            + "0001B 0001B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0001C 0001C       A3             CONSTANT (2)\n"
            + "0001D 0001D       70             EQUAL\n"
            + "0001E 0001E       13 03          JZ $00022 (3)\n"
            + "'         a := 3\n"
            + "00020 00020       A4             CONSTANT (3)\n"
            + "00021 00021       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00022 00022       04             RETURN\n"
            + "00023 00023       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testIfElseIfElseConditional() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    if a == 0\n"
            + "        a := 1\n"
            + "    elseif a == 1\n"
            + "        a := 2\n"
            + "    elseif a == 2\n"
            + "        a := 3\n"
            + "    else\n"
            + "        a := 4\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       27 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     if a == 0\n"
            + "00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       A1             CONSTANT (0)\n"
            + "0000B 0000B       70             EQUAL\n"
            + "0000C 0000C       13 05          JZ $00012 (5)\n"
            + "'         a := 1\n"
            + "0000E 0000E       A2             CONSTANT (1)\n"
            + "0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00010 00010       12 15          JMP $00026 (21)\n"
            + "'     elseif a == 1\n"
            + "00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00013 00013       A2             CONSTANT (1)\n"
            + "00014 00014       70             EQUAL\n"
            + "00015 00015       13 05          JZ $0001B (5)\n"
            + "'         a := 2\n"
            + "00017 00017       A3             CONSTANT (2)\n"
            + "00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00019 00019       12 0C          JMP $00026 (12)\n"
            + "'     elseif a == 2\n"
            + "0001B 0001B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0001C 0001C       A3             CONSTANT (2)\n"
            + "0001D 0001D       70             EQUAL\n"
            + "0001E 0001E       13 05          JZ $00024 (5)\n"
            + "'         a := 3\n"
            + "00020 00020       A4             CONSTANT (3)\n"
            + "00021 00021       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00022 00022       12 03          JMP $00026 (3)\n"
            + "'     else\n"
            + "'         a := 4\n"
            + "00024 00024       A5             CONSTANT (4)\n"
            + "00025 00025       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00026 00026       04             RETURN\n"
            + "00027 00027       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeat() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       0E 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     repeat\n"
            + "'         a := 1\n"
            + "00009 00009       A2             CONSTANT (1)\n"
            + "0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000B 0000B       12 7D          JMP $00009 (-3)\n"
            + "0000D 0000D       04             RETURN\n"
            + "0000E 0000E       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatCount() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat 10\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       0F 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     repeat 10\n"
            + "00009 00009       AB             CONSTANT (10)\n"
            + "'         a := 1\n"
            + "0000A 0000A       A2             CONSTANT (1)\n"
            + "0000B 0000B       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       16 7D          DJNZ $0000A (-3)\n"
            + "0000E 0000E       04             RETURN\n"
            + "0000F 0000F       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatVar() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat a\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       11 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     repeat a\n"
            + "00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       15 05          TJZ $00010 (5)\n"
            + "'         a := 1\n"
            + "0000C 0000C       A2             CONSTANT (1)\n"
            + "0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000E 0000E       16 7D          DJNZ $0000C (-3)\n"
            + "00010 00010       04             RETURN\n"
            + "00011 00011       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatVarQuit() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat a\n"
            + "        if a == 0\n"
            + "            a := 1\n"
            + "        else\n"
            + "            quit\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       1A 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     repeat a\n"
            + "00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       15 0E          TJZ $00019 (14)\n"
            + "'         if a == 0\n"
            + "0000C 0000C       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000D 0000D       A1             CONSTANT (0)\n"
            + "0000E 0000E       70             EQUAL\n"
            + "0000F 0000F       13 05          JZ $00015 (5)\n"
            + "'             a := 1\n"
            + "00011 00011       A2             CONSTANT (1)\n"
            + "00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00013 00013       12 03          JMP $00017 (3)\n"
            + "'         else\n"
            + "'             quit\n"
            + "00015 00015       14 03          JNZ $00019 (3)\n"
            + "00017 00017       16 74          DJNZ $0000C (-12)\n"
            + "00019 00019       04             RETURN\n"
            + "0001A 0001A       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatQuit() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat\n"
            + "        if a == 1\n"
            + "            quit\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       15 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     repeat\n"
            + "'         if a == 1\n"
            + "00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       A2             CONSTANT (1)\n"
            + "0000B 0000B       70             EQUAL\n"
            + "0000C 0000C       13 03          JZ $00010 (3)\n"
            + "'             quit\n"
            + "0000E 0000E       12 05          JMP $00014 (5)\n"
            + "'         a := 1\n"
            + "00010 00010       A2             CONSTANT (1)\n"
            + "00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00012 00012       12 76          JMP $00009 (-10)\n"
            + "00014 00014       04             RETURN\n"
            + "00015 00015       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatNext() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat\n"
            + "        if a == 1\n"
            + "            next\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       15 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     repeat\n"
            + "'         if a == 1\n"
            + "00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       A2             CONSTANT (1)\n"
            + "0000B 0000B       70             EQUAL\n"
            + "0000C 0000C       13 03          JZ $00010 (3)\n"
            + "'             next\n"
            + "0000E 0000E       12 7A          JMP $00009 (-6)\n"
            + "'         a := 1\n"
            + "00010 00010       A2             CONSTANT (1)\n"
            + "00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00012 00012       12 76          JMP $00009 (-10)\n"
            + "00014 00014       04             RETURN\n"
            + "00015 00015       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatWhile() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat while a < 1\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       13 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     repeat while a < 1\n"
            + "00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       A2             CONSTANT (1)\n"
            + "0000B 0000B       6C             LESS_THAN\n"
            + "0000C 0000C       13 05          JZ $00012 (5)\n"
            + "'         a := 1\n"
            + "0000E 0000E       A2             CONSTANT (1)\n"
            + "0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00010 00010       12 78          JMP $00009 (-8)\n"
            + "00012 00012       04             RETURN\n"
            + "00013 00013       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatUntil() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat until a < 1\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       13 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     repeat until a < 1\n"
            + "00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       A2             CONSTANT (1)\n"
            + "0000B 0000B       6C             LESS_THAN\n"
            + "0000C 0000C       14 05          JNZ $00012 (5)\n"
            + "'         a := 1\n"
            + "0000E 0000E       A2             CONSTANT (1)\n"
            + "0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00010 00010       12 78          JMP $00009 (-8)\n"
            + "00012 00012       04             RETURN\n"
            + "00013 00013       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatWhileQuit() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat while a < 1\n"
            + "        if a == 1\n"
            + "            quit\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       1A 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     repeat while a < 1\n"
            + "00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       A2             CONSTANT (1)\n"
            + "0000B 0000B       6C             LESS_THAN\n"
            + "0000C 0000C       13 0C          JZ $00019 (12)\n"
            + "'         if a == 1\n"
            + "0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       A2             CONSTANT (1)\n"
            + "00010 00010       70             EQUAL\n"
            + "00011 00011       13 03          JZ $00015 (3)\n"
            + "'             quit\n"
            + "00013 00013       12 05          JMP $00019 (5)\n"
            + "'         a := 1\n"
            + "00015 00015       A2             CONSTANT (1)\n"
            + "00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00017 00017       12 71          JMP $00009 (-15)\n"
            + "00019 00019       04             RETURN\n"
            + "0001A 0001A       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatPostWhile() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat\n"
            + "        a := 1\n"
            + "    while a < 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       11 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     repeat\n"
            + "'         a := 1\n"
            + "00009 00009       A2             CONSTANT (1)\n"
            + "0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     while a < 1\n"
            + "0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       A2             CONSTANT (1)\n"
            + "0000D 0000D       6C             LESS_THAN\n"
            + "0000E 0000E       14 7A          JNZ $00009 (-6)\n"
            + "00010 00010       04             RETURN\n"
            + "00011 00011       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatPostUntil() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat\n"
            + "        a := 1\n"
            + "    until a < 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       11 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     repeat\n"
            + "'         a := 1\n"
            + "00009 00009       A2             CONSTANT (1)\n"
            + "0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     until a < 1\n"
            + "0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       A2             CONSTANT (1)\n"
            + "0000D 0000D       6C             LESS_THAN\n"
            + "0000E 0000E       13 7A          JZ $00009 (-6)\n"
            + "00010 00010       04             RETURN\n"
            + "00011 00011       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatPostConditionQuit() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat\n"
            + "        if a == 1\n"
            + "            quit\n"
            + "        a := 1\n"
            + "    while a < 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       18 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     repeat\n"
            + "'         if a == 1\n"
            + "00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       A2             CONSTANT (1)\n"
            + "0000B 0000B       70             EQUAL\n"
            + "0000C 0000C       13 03          JZ $00010 (3)\n"
            + "'             quit\n"
            + "0000E 0000E       12 08          JMP $00017 (8)\n"
            + "'         a := 1\n"
            + "00010 00010       A2             CONSTANT (1)\n"
            + "00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     while a < 1\n"
            + "00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00013 00013       A2             CONSTANT (1)\n"
            + "00014 00014       6C             LESS_THAN\n"
            + "00015 00015       14 73          JNZ $00009 (-13)\n"
            + "00017 00017       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testRepeatPostConditionNext() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat\n"
            + "        if a == 1\n"
            + "            next\n"
            + "        a := 1\n"
            + "    until a < 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       18 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     repeat\n"
            + "'         if a == 1\n"
            + "00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       A2             CONSTANT (1)\n"
            + "0000B 0000B       70             EQUAL\n"
            + "0000C 0000C       13 03          JZ $00010 (3)\n"
            + "'             next\n"
            + "0000E 0000E       12 03          JMP $00012 (3)\n"
            + "'         a := 1\n"
            + "00010 00010       A2             CONSTANT (1)\n"
            + "00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     until a < 1\n"
            + "00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00013 00013       A2             CONSTANT (1)\n"
            + "00014 00014       6C             LESS_THAN\n"
            + "00015 00015       13 73          JZ $00009 (-13)\n"
            + "00017 00017       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testRepeatRange() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    repeat a from 1 to 10\n"
            + "        b := a + 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       16 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     repeat a from 1 to 10\n"
            + "00009 00009       45 0F          ADDRESS ($0000F)\n"
            + "0000B 0000B       AB             CONSTANT (10)\n"
            + "0000C 0000C       A2             CONSTANT (1)\n"
            + "0000D 0000D       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000E 0000E       7C             REPEAT\n"
            + "'         b := a + 1\n"
            + "0000F 0000F       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00010 00010       A2             CONSTANT (1)\n"
            + "00011 00011       8A             ADD\n"
            + "00012 00012       F1             VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "00013 00013       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "00014 00014       7E             REPEAT_LOOP\n"
            + "00015 00015       04             RETURN\n"
            + "00016 00016       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatRangeVariables() throws Exception {
        String text = ""
            + "PUB main() | a, b, c, d\n"
            + "\n"
            + "    repeat a from b to c\n"
            + "        d := a + 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       16 00 00 00    End\n"
            + "' PUB main() | a, b, c, d\n"
            + "00008 00008       10             (stack size)\n"
            + "'     repeat a from b to c\n"
            + "00009 00009       45 0F          ADDRESS ($0000F)\n"
            + "0000B 0000B       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "0000C 0000C       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0000D 0000D       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000E 0000E       7C             REPEAT\n"
            + "'         d := a + 1\n"
            + "0000F 0000F       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00010 00010       A2             CONSTANT (1)\n"
            + "00011 00011       8A             ADD\n"
            + "00012 00012       F3             VAR_WRITE LONG DBASE+$00003 (short)\n"
            + "00013 00013       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "00014 00014       7E             REPEAT_LOOP\n"
            + "00015 00015       04             RETURN\n"
            + "00016 00016       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatRangeReverse() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    repeat a from 10 to 1\n"
            + "        b := a + 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       16 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     repeat a from 10 to 1\n"
            + "00009 00009       45 0F          ADDRESS ($0000F)\n"
            + "0000B 0000B       A2             CONSTANT (1)\n"
            + "0000C 0000C       AB             CONSTANT (10)\n"
            + "0000D 0000D       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000E 0000E       7C             REPEAT\n"
            + "'         b := a + 1\n"
            + "0000F 0000F       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00010 00010       A2             CONSTANT (1)\n"
            + "00011 00011       8A             ADD\n"
            + "00012 00012       F1             VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "00013 00013       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "00014 00014       7E             REPEAT_LOOP\n"
            + "00015 00015       04             RETURN\n"
            + "00016 00016       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatRangeStep() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    repeat a from 1 to 10 step 5\n"
            + "        b := a + 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       17 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     repeat a from 1 to 10 step 5\n"
            + "00009 00009       45 10          ADDRESS ($00010)\n"
            + "0000B 0000B       AB             CONSTANT (10)\n"
            + "0000C 0000C       A6             CONSTANT (5)\n"
            + "0000D 0000D       A2             CONSTANT (1)\n"
            + "0000E 0000E       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       7D             REPEAT\n"
            + "'         b := a + 1\n"
            + "00010 00010       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00011 00011       A2             CONSTANT (1)\n"
            + "00012 00012       8A             ADD\n"
            + "00013 00013       F1             VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "00014 00014       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "00015 00015       7E             REPEAT_LOOP\n"
            + "00016 00016       04             RETURN\n"
            + "00017 00017       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatRangeNext() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    repeat a from 1 to 10 step 5\n"
            + "        if b > 5\n"
            + "            next\n"
            + "        b := a + 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       1E 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     repeat a from 1 to 10 step 5\n"
            + "00009 00009       45 10          ADDRESS ($00010)\n"
            + "0000B 0000B       AB             CONSTANT (10)\n"
            + "0000C 0000C       A6             CONSTANT (5)\n"
            + "0000D 0000D       A2             CONSTANT (1)\n"
            + "0000E 0000E       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       7D             REPEAT\n"
            + "'         if b > 5\n"
            + "00010 00010       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00011 00011       A6             CONSTANT (5)\n"
            + "00012 00012       74             GREATER_THAN\n"
            + "00013 00013       13 03          JZ $00017 (3)\n"
            + "'             next\n"
            + "00015 00015       12 05          JMP $0001B (5)\n"
            + "'         b := a + 1\n"
            + "00017 00017       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00018 00018       A2             CONSTANT (1)\n"
            + "00019 00019       8A             ADD\n"
            + "0001A 0001A       F1             VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "0001B 0001B       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0001C 0001C       7E             REPEAT_LOOP\n"
            + "0001D 0001D       04             RETURN\n"
            + "0001E 0001E       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatRangeQuit() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    repeat a from 1 to 10 step 5\n"
            + "        if b > 5\n"
            + "            quit\n"
            + "        b := a + 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       20 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     repeat a from 1 to 10 step 5\n"
            + "00009 00009       45 10          ADDRESS ($00010)\n"
            + "0000B 0000B       AB             CONSTANT (10)\n"
            + "0000C 0000C       A6             CONSTANT (5)\n"
            + "0000D 0000D       A2             CONSTANT (1)\n"
            + "0000E 0000E       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       7D             REPEAT\n"
            + "'         if b > 5\n"
            + "00010 00010       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00011 00011       A6             CONSTANT (5)\n"
            + "00012 00012       74             GREATER_THAN\n"
            + "00013 00013       13 05          JZ $00019 (5)\n"
            + "'             quit\n"
            + "00015 00015       18 0C          POP\n"
            + "00017 00017       12 07          JMP $0001F (7)\n"
            + "'         b := a + 1\n"
            + "00019 00019       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0001A 0001A       A2             CONSTANT (1)\n"
            + "0001B 0001B       8A             ADD\n"
            + "0001C 0001C       F1             VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "0001D 0001D       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0001E 0001E       7E             REPEAT_LOOP\n"
            + "0001F 0001F       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testRepeatCaseQuit() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat\n"
            + "        case a\n"
            + "            1: quit\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       1A 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     repeat\n"
            + "'         case a\n"
            + "00009 00009       45 15          ADDRESS ($00015)\n"
            + "0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       A2             CONSTANT (1)\n"
            + "0000D 0000D       1C 02          CASE_JMP $00010 (2)\n"
            + "0000F 0000F       1E             CASE_DONE\n"
            + "'             1: quit\n"
            + "00010 00010       18 04          POP\n"
            + "00012 00012       12 06          JMP $00019 (6)\n"
            + "00014 00014       1E             CASE_DONE\n"
            + "'         a := 1\n"
            + "00015 00015       A2             CONSTANT (1)\n"
            + "00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00017 00017       12 71          JMP $00009 (-15)\n"
            + "00019 00019       04             RETURN\n"
            + "0001A 0001A       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatCaseFastQuit() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat\n"
            + "        case_fast a\n"
            + "            1: quit\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       20 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     repeat\n"
            + "'         case_fast a\n"
            + "00009 00009       45 1B          ADDRESS ($0001B)\n"
            + "0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       1A             CASE_FAST\n"
            + "0000D 0000D       01 00 00 00    FROM 1\n"
            + "00011 00011       01 00          TO 1\n"
            + "00013 00013       04 00          CASE_FAST_JMP $00017 (4) <$00011>\n"
            + "00015 00015       07 00          CASE_FAST_JMP $0001A (7) <$00011>\n"
            + "'             1: quit\n"
            + "00017 00017       17             POP\n"
            + "00018 00018       12 06          JMP $0001F (6)\n"
            + "0001A 0001A       1B             CASE_FAST_DONE\n"
            + "'         a := 1\n"
            + "0001B 0001B       A2             CONSTANT (1)\n"
            + "0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001D 0001D       12 6B          JMP $00009 (-21)\n"
            + "0001F 0001F       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testRepeatRangeCaseQuit() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    repeat a from 1 to 10 step 5\n"
            + "        case a\n"
            + "            1: quit\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       21 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     repeat a from 1 to 10 step 5\n"
            + "00009 00009       45 10          ADDRESS ($00010)\n"
            + "0000B 0000B       AB             CONSTANT (10)\n"
            + "0000C 0000C       A6             CONSTANT (5)\n"
            + "0000D 0000D       A2             CONSTANT (1)\n"
            + "0000E 0000E       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       7D             REPEAT\n"
            + "'         case a\n"
            + "00010 00010       45 1C          ADDRESS ($0001C)\n"
            + "00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00013 00013       A2             CONSTANT (1)\n"
            + "00014 00014       1C 02          CASE_JMP $00017 (2)\n"
            + "00016 00016       1E             CASE_DONE\n"
            + "'             1: quit\n"
            + "00017 00017       18 14          POP\n"
            + "00019 00019       12 06          JMP $00020 (6)\n"
            + "0001B 0001B       1E             CASE_DONE\n"
            + "'         a := 1\n"
            + "0001C 0001C       A2             CONSTANT (1)\n"
            + "0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001E 0001E       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0001F 0001F       7E             REPEAT_LOOP\n"
            + "00020 00020       04             RETURN\n"
            + "00021 00021       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatRangeCaseFastQuit() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    repeat a from 1 to 10 step 5\n"
            + "        case_fast a\n"
            + "            1: quit\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       28 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     repeat a from 1 to 10 step 5\n"
            + "00009 00009       45 10          ADDRESS ($00010)\n"
            + "0000B 0000B       AB             CONSTANT (10)\n"
            + "0000C 0000C       A6             CONSTANT (5)\n"
            + "0000D 0000D       A2             CONSTANT (1)\n"
            + "0000E 0000E       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       7D             REPEAT\n"
            + "'         case_fast a\n"
            + "00010 00010       45 23          ADDRESS ($00023)\n"
            + "00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00013 00013       1A             CASE_FAST\n"
            + "00014 00014       01 00 00 00    FROM 1\n"
            + "00018 00018       01 00          TO 1\n"
            + "0001A 0001A       04 00          CASE_FAST_JMP $0001E (4) <$00018>\n"
            + "0001C 0001C       08 00          CASE_FAST_JMP $00022 (8) <$00018>\n"
            + "'             1: quit\n"
            + "0001E 0001E       18 10          POP\n"
            + "00020 00020       12 06          JMP $00027 (6)\n"
            + "00022 00022       1B             CASE_FAST_DONE\n"
            + "'         a := 1\n"
            + "00023 00023       A2             CONSTANT (1)\n"
            + "00024 00024       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00025 00025       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "00026 00026       7E             REPEAT_LOOP\n"
            + "00027 00027       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testRepeatNestedCaseQuit() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    repeat\n"
            + "        case a\n"
            + "            1: quit\n"
            + "            2:\n"
            + "                case a\n"
            + "                    1: quit\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       2A 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     repeat\n"
            + "'         case a\n"
            + "00009 00009       45 25          ADDRESS ($00025)\n"
            + "0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       A2             CONSTANT (1)\n"
            + "0000D 0000D       1C 05          CASE_JMP $00013 (5)\n"
            + "0000F 0000F       A3             CONSTANT (2)\n"
            + "00010 00010       1C 07          CASE_JMP $00018 (7)\n"
            + "00012 00012       1E             CASE_DONE\n"
            + "'             1: quit\n"
            + "00013 00013       18 04          POP\n"
            + "00015 00015       12 13          JMP $00029 (19)\n"
            + "00017 00017       1E             CASE_DONE\n"
            + "'                 case a\n"
            + "00018 00018       45 24          ADDRESS ($00024)\n"
            + "0001A 0001A       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0001B 0001B       A2             CONSTANT (1)\n"
            + "0001C 0001C       1C 02          CASE_JMP $0001F (2)\n"
            + "0001E 0001E       1E             CASE_DONE\n"
            + "'                     1: quit\n"
            + "0001F 0001F       18 0C          POP\n"
            + "00021 00021       12 07          JMP $00029 (7)\n"
            + "00023 00023       1E             CASE_DONE\n"
            + "00024 00024       1E             CASE_DONE\n"
            + "'         a := 1\n"
            + "00025 00025       A2             CONSTANT (1)\n"
            + "00026 00026       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00027 00027       12 61          JMP $00009 (-31)\n"
            + "00029 00029       04             RETURN\n"
            + "0002A 0002A       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatNestedCaseFastQuit() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    repeat\n"
            + "        case_fast a\n"
            + "            1: quit\n"
            + "            2:\n"
            + "                case_fast a\n"
            + "                    1: quit\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       36 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     repeat\n"
            + "'         case_fast a\n"
            + "00009 00009       45 31          ADDRESS ($00031)\n"
            + "0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       1A             CASE_FAST\n"
            + "0000D 0000D       01 00 00 00    FROM 1\n"
            + "00011 00011       02 00          TO 2\n"
            + "00013 00013       06 00          CASE_FAST_JMP $00019 (6) <$00011>\n"
            + "00015 00015       0A 00          CASE_FAST_JMP $0001D (10) <$00011>\n"
            + "00017 00017       1D 00          CASE_FAST_JMP $00030 (29) <$00011>\n"
            + "'             1: quit\n"
            + "00019 00019       17             POP\n"
            + "0001A 0001A       12 1A          JMP $00035 (26)\n"
            + "0001C 0001C       1B             CASE_FAST_DONE\n"
            + "'                 case_fast a\n"
            + "0001D 0001D       45 30          ADDRESS ($00030)\n"
            + "0001F 0001F       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00020 00020       1A             CASE_FAST\n"
            + "00021 00021       01 00 00 00    FROM 1\n"
            + "00025 00025       01 00          TO 1\n"
            + "00027 00027       04 00          CASE_FAST_JMP $0002B (4) <$00025>\n"
            + "00029 00029       08 00          CASE_FAST_JMP $0002F (8) <$00025>\n"
            + "'                     1: quit\n"
            + "0002B 0002B       18 04          POP\n"
            + "0002D 0002D       12 07          JMP $00035 (7)\n"
            + "0002F 0002F       1B             CASE_FAST_DONE\n"
            + "00030 00030       1B             CASE_FAST_DONE\n"
            + "'         a := 1\n"
            + "00031 00031       A2             CONSTANT (1)\n"
            + "00032 00032       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00033 00033       12 55          JMP $00009 (-43)\n"
            + "00035 00035       04             RETURN\n"
            + "00036 00036       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatRangeNestedCaseQuit() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    repeat a from 1 to 10 step 5\n"
            + "        case a\n"
            + "            1: quit\n"
            + "            2:\n"
            + "                case a\n"
            + "                    1: quit\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       31 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     repeat a from 1 to 10 step 5\n"
            + "00009 00009       45 10          ADDRESS ($00010)\n"
            + "0000B 0000B       AB             CONSTANT (10)\n"
            + "0000C 0000C       A6             CONSTANT (5)\n"
            + "0000D 0000D       A2             CONSTANT (1)\n"
            + "0000E 0000E       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       7D             REPEAT\n"
            + "'         case a\n"
            + "00010 00010       45 2C          ADDRESS ($0002C)\n"
            + "00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00013 00013       A2             CONSTANT (1)\n"
            + "00014 00014       1C 05          CASE_JMP $0001A (5)\n"
            + "00016 00016       A3             CONSTANT (2)\n"
            + "00017 00017       1C 07          CASE_JMP $0001F (7)\n"
            + "00019 00019       1E             CASE_DONE\n"
            + "'             1: quit\n"
            + "0001A 0001A       18 14          POP\n"
            + "0001C 0001C       12 13          JMP $00030 (19)\n"
            + "0001E 0001E       1E             CASE_DONE\n"
            + "'                 case a\n"
            + "0001F 0001F       45 2B          ADDRESS ($0002B)\n"
            + "00021 00021       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00022 00022       A2             CONSTANT (1)\n"
            + "00023 00023       1C 02          CASE_JMP $00026 (2)\n"
            + "00025 00025       1E             CASE_DONE\n"
            + "'                     1: quit\n"
            + "00026 00026       18 1C          POP\n"
            + "00028 00028       12 07          JMP $00030 (7)\n"
            + "0002A 0002A       1E             CASE_DONE\n"
            + "0002B 0002B       1E             CASE_DONE\n"
            + "'         a := 1\n"
            + "0002C 0002C       A2             CONSTANT (1)\n"
            + "0002D 0002D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0002E 0002E       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0002F 0002F       7E             REPEAT_LOOP\n"
            + "00030 00030       04             RETURN\n"
            + "00031 00031       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatAddressRange() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    repeat a from @c to @d\n"
            + "        b := a + 1\n"
            + "\n"
            + "DAT\n"
            + "\n"
            + "c       long    1\n"
            + "d       long    2\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)\n"
            + "00004 00004       22 00 00 00    End\n"
            + "00008 00008 00000 01 00 00 00    c                   long    1\n"
            + "0000C 0000C 00004 02 00 00 00    d                   long    2\n"
            + "' PUB main() | a, b\n"
            + "00010 00010       08             (stack size)\n"
            + "'     repeat a from @c to @d\n"
            + "00011 00011       45 1B          ADDRESS ($0001B)\n"
            + "00013 00013       5C 0C 7F       MEM_ADDRESS PBASE+$0000C\n"
            + "00016 00016       5C 08 7F       MEM_ADDRESS PBASE+$00008\n"
            + "00019 00019       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0001A 0001A       7C             REPEAT\n"
            + "'         b := a + 1\n"
            + "0001B 0001B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0001C 0001C       A2             CONSTANT (1)\n"
            + "0001D 0001D       8A             ADD\n"
            + "0001E 0001E       F1             VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "0001F 0001F       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "00020 00020       7E             REPEAT_LOOP\n"
            + "00021 00021       04             RETURN\n"
            + "00022 00022       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testMethodCall() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    function()\n"
            + "\n"
            + "PUB function()\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004 00004       11 00 00 80    Method function @ $00011 (0 parameters, 0 returns)\n"
            + "00008 00008       13 00 00 00    End\n"
            + "' PUB main()\n"
            + "0000C 0000C       00             (stack size)\n"
            + "'     function()\n"
            + "0000D 0000D       00             ANCHOR\n"
            + "0000E 0000E       0A 01          CALL_SUB (1)\n"
            + "00010 00010       04             RETURN\n"
            + "' PUB function()\n"
            + "00011 00011       00             (stack size)\n"
            + "00012 00012       04             RETURN\n"
            + "00013 00013       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testMethodArguments() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    function(1)\n"
            + "\n"
            + "PUB function(a)\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004 00004       12 00 00 81    Method function @ $00012 (1 parameters, 0 returns)\n"
            + "00008 00008       14 00 00 00    End\n"
            + "' PUB main()\n"
            + "0000C 0000C       00             (stack size)\n"
            + "'     function(1)\n"
            + "0000D 0000D       00             ANCHOR\n"
            + "0000E 0000E       A2             CONSTANT (1)\n"
            + "0000F 0000F       0A 01          CALL_SUB (1)\n"
            + "00011 00011       04             RETURN\n"
            + "' PUB function(a)\n"
            + "00012 00012       00             (stack size)\n"
            + "00013 00013       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testMethodCallReturn() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    a := function()\n"
            + "\n"
            + "PUB function() : rc\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004 00004       12 00 10 80    Method function @ $00012 (0 parameters, 1 returns)\n"
            + "00008 00008       14 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "0000C 0000C       04             (stack size)\n"
            + "'     a := function()\n"
            + "0000D 0000D       01             ANCHOR\n"
            + "0000E 0000E       0A 01          CALL_SUB (1)\n"
            + "00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00011 00011       04             RETURN\n"
            + "' PUB function() : rc\n"
            + "00012 00012       00             (stack size)\n"
            + "00013 00013       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testMethodCallTrap() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    a := \\function()\n"
            + "\n"
            + "PUB function() : rc\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004 00004       12 00 10 80    Method function @ $00012 (0 parameters, 1 returns)\n"
            + "00008 00008       14 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "0000C 0000C       04             (stack size)\n"
            + "'     a := \\function()\n"
            + "0000D 0000D       03             ANCHOR_TRAP\n"
            + "0000E 0000E       0A 01          CALL_SUB (1)\n"
            + "00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00011 00011       04             RETURN\n"
            + "' PUB function() : rc\n"
            + "00012 00012       00             (stack size)\n"
            + "00013 00013       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testMethodCharacterArguments() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    function(\"1\")\n"
            + "\n"
            + "PUB function(a)\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004 00004       13 00 00 81    Method function @ $00013 (1 parameters, 0 returns)\n"
            + "00008 00008       15 00 00 00    End\n"
            + "' PUB main()\n"
            + "0000C 0000C       00             (stack size)\n"
            + "'     function(\"1\")\n"
            + "0000D 0000D       00             ANCHOR\n"
            + "0000E 0000E       45 31          CONSTANT (\"1\")\n"
            + "00010 00010       0A 01          CALL_SUB (1)\n"
            + "00012 00012       04             RETURN\n"
            + "' PUB function(a)\n"
            + "00013 00013       00             (stack size)\n"
            + "00014 00014       04             RETURN\n"
            + "00015 00015       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testMethodStringArgument() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    function(string(\"1234\"))\n"
            + "\n"
            + "PUB function(a)\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004 00004       18 00 00 81    Method function @ $00018 (1 parameters, 0 returns)\n"
            + "00008 00008       1A 00 00 00    End\n"
            + "' PUB main()\n"
            + "0000C 0000C       00             (stack size)\n"
            + "'     function(string(\"1234\"))\n"
            + "0000D 0000D       00             ANCHOR\n"
            + "0000E 0000E       9E 05 31 32 33 STRING\n"
            + "00013 00013       34 00\n"
            + "00015 00015       0A 01          CALL_SUB (1)\n"
            + "00017 00017       04             RETURN\n"
            + "' PUB function(a)\n"
            + "00018 00018       00             (stack size)\n"
            + "00019 00019       04             RETURN\n"
            + "0001A 0001A       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testMethodAutomaticStringArgument() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    function(\"1234\")\n"
            + "\n"
            + "PUB function(a)\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004 00004       18 00 00 81    Method function @ $00018 (1 parameters, 0 returns)\n"
            + "00008 00008       1A 00 00 00    End\n"
            + "' PUB main()\n"
            + "0000C 0000C       00             (stack size)\n"
            + "'     function(\"1234\")\n"
            + "0000D 0000D       00             ANCHOR\n"
            + "0000E 0000E       9E 05 31 32 33 STRING\n"
            + "00013 00013       34 00\n"
            + "00015 00015       0A 01          CALL_SUB (1)\n"
            + "00017 00017       04             RETURN\n"
            + "' PUB function(a)\n"
            + "00018 00018       00             (stack size)\n"
            + "00019 00019       04             RETURN\n"
            + "0001A 0001A       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testMethodMixedStringArgument() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    function(string(\"1234\", 13, 10))\n"
            + "\n"
            + "PUB function(a)\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004 00004       1A 00 00 81    Method function @ $0001A (1 parameters, 0 returns)\n"
            + "00008 00008       1C 00 00 00    End\n"
            + "' PUB main()\n"
            + "0000C 0000C       00             (stack size)\n"
            + "'     function(string(\"1234\", 13, 10))\n"
            + "0000D 0000D       00             ANCHOR\n"
            + "0000E 0000E       9E 07 31 32 33 STRING\n"
            + "00013 00013       34 0D 0A 00\n"
            + "00017 00017       0A 01          CALL_SUB (1)\n"
            + "00019 00019       04             RETURN\n"
            + "' PUB function(a)\n"
            + "0001A 0001A       00             (stack size)\n"
            + "0001B 0001B       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testMethodOrder() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "PRI function1()\n"
            + "\n"
            + "PUB function2()\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)\n"
            + "00004 00004       12 00 00 80    Method function2 @ $00012 (0 parameters, 0 returns)\n"
            + "00008 00008       14 00 00 80    Method function1 @ $00014 (0 parameters, 0 returns)\n"
            + "0000C 0000C       16 00 00 00    End\n"
            + "' PUB main()\n"
            + "00010 00010       00             (stack size)\n"
            + "00011 00011       04             RETURN\n"
            + "' PUB function2()\n"
            + "00012 00012       00             (stack size)\n"
            + "00013 00013       04             RETURN\n"
            + "' PRI function1()\n"
            + "00014 00014       00             (stack size)\n"
            + "00015 00015       04             RETURN\n"
            + "00016 00016       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testPriMethodCall() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    function1()\n"
            + "\n"
            + "PRI function1()\n"
            + "\n"
            + "PUB function2()\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)\n"
            + "00004 00004       15 00 00 80    Method function2 @ $00015 (0 parameters, 0 returns)\n"
            + "00008 00008       17 00 00 80    Method function1 @ $00017 (0 parameters, 0 returns)\n"
            + "0000C 0000C       19 00 00 00    End\n"
            + "' PUB main()\n"
            + "00010 00010       00             (stack size)\n"
            + "'     function1()\n"
            + "00011 00011       00             ANCHOR\n"
            + "00012 00012       0A 02          CALL_SUB (2)\n"
            + "00014 00014       04             RETURN\n"
            + "' PUB function2()\n"
            + "00015 00015       00             (stack size)\n"
            + "00016 00016       04             RETURN\n"
            + "' PRI function1()\n"
            + "00017 00017       00             (stack size)\n"
            + "00018 00018       04             RETURN\n"
            + "00019 00019       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testAbort() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    abort\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       0B 00 00 00    End\n"
            + "' PUB main()\n"
            + "00008 00008       00             (stack size)\n"
            + "'     abort\n"
            + "00009 00009       06             ABORT\n"
            + "0000A 0000A       04             RETURN\n"
            + "0000B 0000B       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testAbortArgument() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    abort 1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       0C 00 00 00    End\n"
            + "' PUB main()\n"
            + "00008 00008       00             (stack size)\n"
            + "'     abort 1\n"
            + "00009 00009       A2             CONSTANT (1)\n"
            + "0000A 0000A       07             ABORT\n"
            + "0000B 0000B       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testDataPointer() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "        a := @b\n"
            + "\n"
            + "DAT             org     $000\n"
            + "\n"
            + "b               long    1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004 00004       12 00 00 00    End\n"
            + "00008 00008   000                                    org     $000\n"
            + "00008 00008   000 01 00 00 00    b                   long    1\n"
            + "' PUB main() | a\n"
            + "0000C 0000C       04             (stack size)\n"
            + "'         a := @b\n"
            + "0000D 0000D       5C 08 7F       MEM_ADDRESS PBASE+$00008\n"
            + "00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00011 00011       04             RETURN\n"
            + "00012 00012       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testDataValueRead() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "        a := b\n"
            + "\n"
            + "DAT             org     $000\n"
            + "\n"
            + "b               long    1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004 00004       12 00 00 00    End\n"
            + "00008 00008   000                                    org     $000\n"
            + "00008 00008   000 01 00 00 00    b                   long    1\n"
            + "' PUB main() | a\n"
            + "0000C 0000C       04             (stack size)\n"
            + "'         a := b\n"
            + "0000D 0000D       5C 08 80       MEM_READ LONG PBASE+$00008\n"
            + "00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00011 00011       04             RETURN\n"
            + "00012 00012       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testDataValueAssign() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "        b := a\n"
            + "\n"
            + "DAT             org     $000\n"
            + "\n"
            + "b               long    1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004 00004       12 00 00 00    End\n"
            + "00008 00008   000                                    org     $000\n"
            + "00008 00008   000 01 00 00 00    b                   long    1\n"
            + "' PUB main() | a\n"
            + "0000C 0000C       04             (stack size)\n"
            + "'         b := a\n"
            + "0000D 0000D       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000E 0000E       5C 08 81       MEM_WRITE LONG PBASE+$00008\n"
            + "00011 00011       04             RETURN\n"
            + "00012 00012       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testVarAddress() throws Exception {
        String text = ""
            + "VAR b[20], c\n"
            + "\n"
            + "PUB main() | a\n"
            + "\n"
            + "        a := @b\n"
            + "        a := @c\n"
            + "        a := @b[a]\n"
            + "        a := @c[a]\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       1B 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'         a := @b\n"
            + "00009 00009       C1 7F          VAR_ADDRESS VBASE+$00001 (short)\n"
            + "0000B 0000B       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'         a := @c\n"
            + "0000C 0000C       5D 54 7F       VAR_ADDRESS VBASE+$00054\n"
            + "0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'         a := @b[a]\n"
            + "00010 00010       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00011 00011       60 04 7F       VAR_ADDRESS_INDEXED VBASE+$00004\n"
            + "00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'         a := @c[a]\n"
            + "00015 00015       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00016 00016       60 54 7F       VAR_ADDRESS_INDEXED VBASE+$00054\n"
            + "00019 00019       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001A 0001A       04             RETURN\n"
            + "0001B 0001B       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testVarAbsoluteAddress() throws Exception {
        String text = ""
            + "VAR b[20], c\n"
            + "\n"
            + "PUB main() | a\n"
            + "\n"
            + "        a := @@b\n"
            + "        a := @@c\n"
            + "        a := @@b[a]\n"
            + "        a := @@c[a]\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       1F 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'         a := @@b\n"
            + "00009 00009       C1 80 24       VAR_ADDRESS PBASE+VBASE+$00001 (short)\n"
            + "0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'         a := @@c\n"
            + "0000D 0000D       5D 54 80 24    VAR_ADDRESS PBASE+VBASE+$00054\n"
            + "00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'         a := @@b[a]\n"
            + "00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00013 00013       60 04 80 24    VAR_ADDRESS_INDEXED PBASE+VBASE+$00004\n"
            + "00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'         a := @@c[a]\n"
            + "00018 00018       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00019 00019       60 54 80 24    VAR_ADDRESS_INDEXED PBASE+VBASE+$00054\n"
            + "0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001E 0001E       04             RETURN\n"
            + "0001F 0001F       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testLocalVarAddress() throws Exception {
        String text = ""
            + "PUB main() | a, b[20], c\n"
            + "\n"
            + "        a := @b\n"
            + "        a := @c\n"
            + "        a := @b[a]\n"
            + "        a := @c[a]\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       1B 00 00 00    End\n"
            + "' PUB main() | a, b[20], c\n"
            + "00008 00008       58             (stack size)\n"
            + "'         a := @b\n"
            + "00009 00009       D1 7F          VAR_ADDRESS DBASE+$00001 (short)\n"
            + "0000B 0000B       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'         a := @c\n"
            + "0000C 0000C       5E 54 7F       VAR_ADDRESS DBASE+$00054\n"
            + "0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'         a := @b[a]\n"
            + "00010 00010       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00011 00011       61 04 7F       VAR_ADDRESS_INDEXED DBASE+$00004\n"
            + "00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'         a := @c[a]\n"
            + "00015 00015       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00016 00016       61 54 7F       VAR_ADDRESS_INDEXED DBASE+$00054\n"
            + "00019 00019       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001A 0001A       04             RETURN\n"
            + "0001B 0001B       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testLocalVarAbsoluteAddress() throws Exception {
        String text = ""
            + "PUB main() | a, b[20], c\n"
            + "\n"
            + "        a := @@b\n"
            + "        a := @@c\n"
            + "        a := @@b[a]\n"
            + "        a := @@c[a]\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       1E 00 00 00    End\n"
            + "' PUB main() | a, b[20], c\n"
            + "00008 00008       58             (stack size)\n"
            + "'         a := @@b\n"
            + "00009 00009       E1 24          VAR_ADDRESS PBASE+DBASE+$00001 (short)\n"
            + "0000B 0000B       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'         a := @@c\n"
            + "0000C 0000C       5E 54 80 24    VAR_ADDRESS PBASE+DBASE+$00054\n"
            + "00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'         a := @@b[a]\n"
            + "00011 00011       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00012 00012       61 04 80 24    VAR_ADDRESS_INDEXED PBASE+DBASE+$00004\n"
            + "00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'         a := @@c[a]\n"
            + "00017 00017       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00018 00018       61 54 80 24    VAR_ADDRESS_INDEXED PBASE+DBASE+$00054\n"
            + "0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001D 0001D       04             RETURN\n"
            + "0001E 0001E       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testInternalFunction() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "        a := muldiv64(1, 2, 3)\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       10 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'         a := muldiv64(1, 2, 3)\n"
            + "00009 00009       A2             CONSTANT (1)\n"
            + "0000A 0000A       A3             CONSTANT (2)\n"
            + "0000B 0000B       A4             CONSTANT (3)\n"
            + "0000C 0000C       19 80          MULDIV64\n"
            + "0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testDatAbsoluteAddress() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "        a := @@b\n"
            + "        a := @@b[a]\n"
            + "\n"
            + "DAT             org   $000\n"
            + "b               byte  1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       09 00 00 80    Method main @ $00009 (0 parameters, 0 returns)\n"
            + "00004 00004       13 00 00 00    End\n"
            + "00008 00008   000                                    org     $000\n"
            + "00008 00008   000 01             b                   byte    1\n"
            + "' PUB main() | a\n"
            + "00009 00009       04             (stack size)\n"
            + "'         a := @@b\n"
            + "0000A 0000A       A9             CONSTANT (8)\n"
            + "0000B 0000B       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'         a := @@b[a]\n"
            + "0000C 0000C       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000D 0000D       53 08 80       MEM_READ BYTE INDEXED PBASE+$00008\n"
            + "00010 00010       24             ADD_PBASE\n"
            + "00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00012 00012       04             RETURN\n"
            + "00013 00013       00             Padding\n"
            + "", compile(text, false));
    }

    @Test
    void testDebug() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    debug()\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       0D 00 00 00    End\n"
            + "' PUB main()\n"
            + "00008 00008       00             (stack size)\n"
            + "'     debug()\n"
            + "00009 00009       44 00 01       DEBUG #1\n"
            + "0000C 0000C       04             RETURN\n"
            + "0000D 0000D       00 00 00       Padding\n"
            + "' Debug data\n"
            + "009C8 00000       06 00         \n"
            + "009CA 00002       04 00         \n"
            + "009CC 00004       04 00         \n"
            + "", compile(text, true));
    }

    @Test
    void testIgnoreDebug() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    debug()\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       0A 00 00 00    End\n"
            + "' PUB main()\n"
            + "00008 00008       00             (stack size)\n"
            + "00009 00009       04             RETURN\n"
            + "0000A 0000A       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testModifyExpressions() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    a += 1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       0D 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     a += 1\n"
            + "00009 00009       A2             CONSTANT (1)\n"
            + "0000A 0000A       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000B 0000B       A3             ADD_ASSIGN\n"
            + "0000C 0000C       04             RETURN\n"
            + "0000D 0000D       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testCase() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    case a\n"
            + "        1: a := 4\n"
            + "        2: a := 5\n"
            + "        3: a := 6\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       20 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     case a\n"
            + "00009 00009       45 1F          ADDRESS ($0001F)\n"
            + "0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       A2             CONSTANT (1)\n"
            + "0000D 0000D       1C 08          CASE_JMP $00016 (8)\n"
            + "0000F 0000F       A3             CONSTANT (2)\n"
            + "00010 00010       1C 08          CASE_JMP $00019 (8)\n"
            + "00012 00012       A4             CONSTANT (3)\n"
            + "00013 00013       1C 08          CASE_JMP $0001C (8)\n"
            + "00015 00015       1E             CASE_DONE\n"
            + "'         1: a := 4\n"
            + "00016 00016       A5             CONSTANT (4)\n"
            + "00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00018 00018       1E             CASE_DONE\n"
            + "'         2: a := 5\n"
            + "00019 00019       A6             CONSTANT (5)\n"
            + "0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001B 0001B       1E             CASE_DONE\n"
            + "'         3: a := 6\n"
            + "0001C 0001C       A7             CONSTANT (6)\n"
            + "0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001E 0001E       1E             CASE_DONE\n"
            + "0001F 0001F       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testCaseFast() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    case_fast a\n"
            + "        1: a := 4\n"
            + "        2: a := 5\n"
            + "        3: a := 6\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       25 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     case_fast a\n"
            + "00009 00009       45 24          ADDRESS ($00024)\n"
            + "0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       1A             CASE_FAST\n"
            + "0000D 0000D       01 00 00 00    FROM 1\n"
            + "00011 00011       03 00          TO 3\n"
            + "00013 00013       08 00          CASE_FAST_JMP $0001B (8) <$00011>\n"
            + "00015 00015       0B 00          CASE_FAST_JMP $0001E (11) <$00011>\n"
            + "00017 00017       0E 00          CASE_FAST_JMP $00021 (14) <$00011>\n"
            + "00019 00019       10 00          CASE_FAST_JMP $00023 (16) <$00011>\n"
            + "'         1: a := 4\n"
            + "0001B 0001B       A5             CONSTANT (4)\n"
            + "0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001D 0001D       1B             CASE_FAST_DONE\n"
            + "'         2: a := 5\n"
            + "0001E 0001E       A6             CONSTANT (5)\n"
            + "0001F 0001F       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00020 00020       1B             CASE_FAST_DONE\n"
            + "'         3: a := 6\n"
            + "00021 00021       A7             CONSTANT (6)\n"
            + "00022 00022       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00023 00023       1B             CASE_FAST_DONE\n"
            + "00024 00024       04             RETURN\n"
            + "00025 00025       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testCaseOther() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    case a\n"
            + "        1: a := 4\n"
            + "        2: a := 5\n"
            + "        3: a := 6\n"
            + "        other: a := 7\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       22 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     case a\n"
            + "00009 00009       45 21          ADDRESS ($00021)\n"
            + "0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       A2             CONSTANT (1)\n"
            + "0000D 0000D       1C 0A          CASE_JMP $00018 (10)\n"
            + "0000F 0000F       A3             CONSTANT (2)\n"
            + "00010 00010       1C 0A          CASE_JMP $0001B (10)\n"
            + "00012 00012       A4             CONSTANT (3)\n"
            + "00013 00013       1C 0A          CASE_JMP $0001E (10)\n"
            + "'         other: a := 7\n"
            + "00015 00015       A8             CONSTANT (7)\n"
            + "00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00017 00017       1E             CASE_DONE\n"
            + "'         1: a := 4\n"
            + "00018 00018       A5             CONSTANT (4)\n"
            + "00019 00019       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001A 0001A       1E             CASE_DONE\n"
            + "'         2: a := 5\n"
            + "0001B 0001B       A6             CONSTANT (5)\n"
            + "0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001D 0001D       1E             CASE_DONE\n"
            + "'         3: a := 6\n"
            + "0001E 0001E       A7             CONSTANT (6)\n"
            + "0001F 0001F       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00020 00020       1E             CASE_DONE\n"
            + "00021 00021       04             RETURN\n"
            + "00022 00022       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testCaseFastGaps() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    case_fast a\n"
            + "        1: a := 4\n"
            + "        5: a := 5\n"
            + "        7: a := 6\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       2D 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     case_fast a\n"
            + "00009 00009       45 2C          ADDRESS ($0002C)\n"
            + "0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       1A             CASE_FAST\n"
            + "0000D 0000D       01 00 00 00    FROM 1\n"
            + "00011 00011       07 00          TO 7\n"
            + "00013 00013       10 00          CASE_FAST_JMP $00023 (16) <$00011>\n"
            + "00015 00015       18 00          CASE_FAST_JMP $0002B (24) <$00011>\n"
            + "00017 00017       18 00          CASE_FAST_JMP $0002B (24) <$00011>\n"
            + "00019 00019       18 00          CASE_FAST_JMP $0002B (24) <$00011>\n"
            + "0001B 0001B       13 00          CASE_FAST_JMP $00026 (19) <$00011>\n"
            + "0001D 0001D       18 00          CASE_FAST_JMP $0002B (24) <$00011>\n"
            + "0001F 0001F       16 00          CASE_FAST_JMP $00029 (22) <$00011>\n"
            + "00021 00021       18 00          CASE_FAST_JMP $0002B (24) <$00011>\n"
            + "'         1: a := 4\n"
            + "00023 00023       A5             CONSTANT (4)\n"
            + "00024 00024       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00025 00025       1B             CASE_FAST_DONE\n"
            + "'         5: a := 5\n"
            + "00026 00026       A6             CONSTANT (5)\n"
            + "00027 00027       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00028 00028       1B             CASE_FAST_DONE\n"
            + "'         7: a := 6\n"
            + "00029 00029       A7             CONSTANT (6)\n"
            + "0002A 0002A       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0002B 0002B       1B             CASE_FAST_DONE\n"
            + "0002C 0002C       04             RETURN\n"
            + "0002D 0002D       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testCaseFastOther() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    case_fast a\n"
            + "        1: a := 4\n"
            + "        2: a := 5\n"
            + "        3: a := 6\n"
            + "        other: a := 7\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       28 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     case_fast a\n"
            + "00009 00009       45 27          ADDRESS ($00027)\n"
            + "0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       1A             CASE_FAST\n"
            + "0000D 0000D       01 00 00 00    FROM 1\n"
            + "00011 00011       03 00          TO 3\n"
            + "00013 00013       08 00          CASE_FAST_JMP $0001B (8) <$00011>\n"
            + "00015 00015       0B 00          CASE_FAST_JMP $0001E (11) <$00011>\n"
            + "00017 00017       0E 00          CASE_FAST_JMP $00021 (14) <$00011>\n"
            + "00019 00019       11 00          CASE_FAST_JMP $00024 (17) <$00011>\n"
            + "'         1: a := 4\n"
            + "0001B 0001B       A5             CONSTANT (4)\n"
            + "0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001D 0001D       1B             CASE_FAST_DONE\n"
            + "'         2: a := 5\n"
            + "0001E 0001E       A6             CONSTANT (5)\n"
            + "0001F 0001F       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00020 00020       1B             CASE_FAST_DONE\n"
            + "'         3: a := 6\n"
            + "00021 00021       A7             CONSTANT (6)\n"
            + "00022 00022       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00023 00023       1B             CASE_FAST_DONE\n"
            + "'         other: a := 7\n"
            + "00024 00024       A8             CONSTANT (7)\n"
            + "00025 00025       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00026 00026       1B             CASE_FAST_DONE\n"
            + "00027 00027       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testCaseFastWithGaps() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    case_fast a\n"
            + "        1: a := 4\n"
            + "        5: a := 5\n"
            + "        7: a := 6\n"
            + "        other: a := 7\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       30 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     case_fast a\n"
            + "00009 00009       45 2F          ADDRESS ($0002F)\n"
            + "0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       1A             CASE_FAST\n"
            + "0000D 0000D       01 00 00 00    FROM 1\n"
            + "00011 00011       07 00          TO 7\n"
            + "00013 00013       10 00          CASE_FAST_JMP $00023 (16) <$00011>\n"
            + "00015 00015       19 00          CASE_FAST_JMP $0002C (25) <$00011>\n"
            + "00017 00017       19 00          CASE_FAST_JMP $0002C (25) <$00011>\n"
            + "00019 00019       19 00          CASE_FAST_JMP $0002C (25) <$00011>\n"
            + "0001B 0001B       13 00          CASE_FAST_JMP $00026 (19) <$00011>\n"
            + "0001D 0001D       19 00          CASE_FAST_JMP $0002C (25) <$00011>\n"
            + "0001F 0001F       16 00          CASE_FAST_JMP $00029 (22) <$00011>\n"
            + "00021 00021       19 00          CASE_FAST_JMP $0002C (25) <$00011>\n"
            + "'         1: a := 4\n"
            + "00023 00023       A5             CONSTANT (4)\n"
            + "00024 00024       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00025 00025       1B             CASE_FAST_DONE\n"
            + "'         5: a := 5\n"
            + "00026 00026       A6             CONSTANT (5)\n"
            + "00027 00027       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00028 00028       1B             CASE_FAST_DONE\n"
            + "'         7: a := 6\n"
            + "00029 00029       A7             CONSTANT (6)\n"
            + "0002A 0002A       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0002B 0002B       1B             CASE_FAST_DONE\n"
            + "'         other: a := 7\n"
            + "0002C 0002C       A8             CONSTANT (7)\n"
            + "0002D 0002D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0002E 0002E       1B             CASE_FAST_DONE\n"
            + "0002F 0002F       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testCaseRange() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    case a\n"
            + "        1..5: a := 6\n"
            + "        other: a := 7\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       17 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     case a\n"
            + "00009 00009       45 16          ADDRESS ($00016)\n"
            + "0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       A2             CONSTANT (1)\n"
            + "0000D 0000D       A6             CONSTANT (5)\n"
            + "0000E 0000E       1D 04          CASE_RANGE_JMP $00013 (4)\n"
            + "'         other: a := 7\n"
            + "00010 00010       A8             CONSTANT (7)\n"
            + "00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00012 00012       1E             CASE_DONE\n"
            + "'         1..5: a := 6\n"
            + "00013 00013       A7             CONSTANT (6)\n"
            + "00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00015 00015       1E             CASE_DONE\n"
            + "00016 00016       04             RETURN\n"
            + "00017 00017       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testCaseFastRange() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    case_fast a\n"
            + "        1..5: a := 6\n"
            + "        other: a := 7\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       26 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     case_fast a\n"
            + "00009 00009       45 25          ADDRESS ($00025)\n"
            + "0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       1A             CASE_FAST\n"
            + "0000D 0000D       01 00 00 00    FROM 1\n"
            + "00011 00011       05 00          TO 5\n"
            + "00013 00013       0C 00          CASE_FAST_JMP $0001F (12) <$00011>\n"
            + "00015 00015       0F 00          CASE_FAST_JMP $00022 (15) <$00011>\n"
            + "00017 00017       0F 00          CASE_FAST_JMP $00022 (15) <$00011>\n"
            + "00019 00019       0F 00          CASE_FAST_JMP $00022 (15) <$00011>\n"
            + "0001B 0001B       0C 00          CASE_FAST_JMP $0001F (12) <$00011>\n"
            + "0001D 0001D       0F 00          CASE_FAST_JMP $00022 (15) <$00011>\n"
            + "'         1..5: a := 6\n"
            + "0001F 0001F       A7             CONSTANT (6)\n"
            + "00020 00020       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00021 00021       1B             CASE_FAST_DONE\n"
            + "'         other: a := 7\n"
            + "00022 00022       A8             CONSTANT (7)\n"
            + "00023 00023       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00024 00024       1B             CASE_FAST_DONE\n"
            + "00025 00025       04             RETURN\n"
            + "00026 00026       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testCaseList() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    case a\n"
            + "        1, 2      : a := 14\n"
            + "        3, 4, 5   : a := 15\n"
            + "        6, 7, 8, 9: a := 16\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       34 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     case a\n"
            + "00009 00009       45 33          ADDRESS ($00033)\n"
            + "0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       A2             CONSTANT (1)\n"
            + "0000D 0000D       1C 1A          CASE_JMP $00028 (26)\n"
            + "0000F 0000F       A3             CONSTANT (2)\n"
            + "00010 00010       1C 17          CASE_JMP $00028 (23)\n"
            + "00012 00012       A4             CONSTANT (3)\n"
            + "00013 00013       1C 17          CASE_JMP $0002B (23)\n"
            + "00015 00015       A5             CONSTANT (4)\n"
            + "00016 00016       1C 14          CASE_JMP $0002B (20)\n"
            + "00018 00018       A6             CONSTANT (5)\n"
            + "00019 00019       1C 11          CASE_JMP $0002B (17)\n"
            + "0001B 0001B       A7             CONSTANT (6)\n"
            + "0001C 0001C       1C 12          CASE_JMP $0002F (18)\n"
            + "0001E 0001E       A8             CONSTANT (7)\n"
            + "0001F 0001F       1C 0F          CASE_JMP $0002F (15)\n"
            + "00021 00021       A9             CONSTANT (8)\n"
            + "00022 00022       1C 0C          CASE_JMP $0002F (12)\n"
            + "00024 00024       AA             CONSTANT (9)\n"
            + "00025 00025       1C 09          CASE_JMP $0002F (9)\n"
            + "00027 00027       1E             CASE_DONE\n"
            + "'         1, 2      : a := 14\n"
            + "00028 00028       AF             CONSTANT (14)\n"
            + "00029 00029       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0002A 0002A       1E             CASE_DONE\n"
            + "'         3, 4, 5   : a := 15\n"
            + "0002B 0002B       45 0F          CONSTANT (15)\n"
            + "0002D 0002D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0002E 0002E       1E             CASE_DONE\n"
            + "'         6, 7, 8, 9: a := 16\n"
            + "0002F 0002F       45 10          CONSTANT (16)\n"
            + "00031 00031       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00032 00032       1E             CASE_DONE\n"
            + "00033 00033       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testCaseFastList() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    case_fast a\n"
            + "        1, 2      : a := 14\n"
            + "        3, 4, 5   : a := 15\n"
            + "        6, 7, 8, 9: a := 16\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       33 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     case_fast a\n"
            + "00009 00009       45 32          ADDRESS ($00032)\n"
            + "0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       1A             CASE_FAST\n"
            + "0000D 0000D       01 00 00 00    FROM 1\n"
            + "00011 00011       09 00          TO 9\n"
            + "00013 00013       14 00          CASE_FAST_JMP $00027 (20) <$00011>\n"
            + "00015 00015       14 00          CASE_FAST_JMP $00027 (20) <$00011>\n"
            + "00017 00017       17 00          CASE_FAST_JMP $0002A (23) <$00011>\n"
            + "00019 00019       17 00          CASE_FAST_JMP $0002A (23) <$00011>\n"
            + "0001B 0001B       17 00          CASE_FAST_JMP $0002A (23) <$00011>\n"
            + "0001D 0001D       1B 00          CASE_FAST_JMP $0002E (27) <$00011>\n"
            + "0001F 0001F       1B 00          CASE_FAST_JMP $0002E (27) <$00011>\n"
            + "00021 00021       1B 00          CASE_FAST_JMP $0002E (27) <$00011>\n"
            + "00023 00023       1B 00          CASE_FAST_JMP $0002E (27) <$00011>\n"
            + "00025 00025       1E 00          CASE_FAST_JMP $00031 (30) <$00011>\n"
            + "'         1, 2      : a := 14\n"
            + "00027 00027       AF             CONSTANT (14)\n"
            + "00028 00028       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00029 00029       1B             CASE_FAST_DONE\n"
            + "'         3, 4, 5   : a := 15\n"
            + "0002A 0002A       45 0F          CONSTANT (15)\n"
            + "0002C 0002C       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0002D 0002D       1B             CASE_FAST_DONE\n"
            + "'         6, 7, 8, 9: a := 16\n"
            + "0002E 0002E       45 10          CONSTANT (16)\n"
            + "00030 00030       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00031 00031       1B             CASE_FAST_DONE\n"
            + "00032 00032       04             RETURN\n"
            + "00033 00033       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testCaseFunction() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    case peek()\n"
            + "        1: a := 10 \n"
            + "        2: a := 20\n"
            + "        other: a:= 100 \n"
            + "\n"
            + "PRI peek() : rc\n"
            + "\n"
            + "    rc := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004 00004       24 00 10 80    Method peek @ $00024 (0 parameters, 1 returns)\n"
            + "00008 00008       28 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "0000C 0000C       04             (stack size)\n"
            + "'     case peek()\n"
            + "0000D 0000D       45 23          ADDRESS ($00023)\n"
            + "0000F 0000F       01             ANCHOR\n"
            + "00010 00010       0A 01          CALL_SUB (1)\n"
            + "00012 00012       A2             CONSTANT (1)\n"
            + "00013 00013       1C 08          CASE_JMP $0001C (8)\n"
            + "00015 00015       A3             CONSTANT (2)\n"
            + "00016 00016       1C 08          CASE_JMP $0001F (8)\n"
            + "'         other: a:= 100\n"
            + "00018 00018       45 64          CONSTANT (100)\n"
            + "0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001B 0001B       1E             CASE_DONE\n"
            + "'         1: a := 10\n"
            + "0001C 0001C       AB             CONSTANT (10)\n"
            + "0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001E 0001E       1E             CASE_DONE\n"
            + "'         2: a := 20\n"
            + "0001F 0001F       45 14          CONSTANT (20)\n"
            + "00021 00021       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00022 00022       1E             CASE_DONE\n"
            + "00023 00023       04             RETURN\n"
            + "' PRI peek() : rc\n"
            + "00024 00024       00             (stack size)\n"
            + "'     rc := 1\n"
            + "00025 00025       A2             CONSTANT (1)\n"
            + "00026 00026       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00027 00027       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testSendVariable() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    SEND(a)\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       0C 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     SEND(a)\n"
            + "00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       0D             SEND\n"
            + "0000B 0000B       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testSendBytes() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    SEND(1,2,3)\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       0F 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     SEND(1,2,3)\n"
            + "00009 00009       0E 03 01 02 03 SEND\n"
            + "0000E 0000E       04             RETURN\n"
            + "0000F 0000F       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testSendMixed() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    SEND(a,2,3)\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       10 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     SEND(a,2,3)\n"
            + "00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       0D             SEND\n"
            + "0000B 0000B       A3             CONSTANT (2)\n"
            + "0000C 0000C       0D             SEND\n"
            + "0000D 0000D       A4             CONSTANT (3)\n"
            + "0000E 0000E       0D             SEND\n"
            + "0000F 0000F       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testSendAssign() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    SEND := @out\n"
            + "\n"
            + "PRI out()\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004 00004       13 00 00 80    Method out @ $00013 (0 parameters, 0 returns)\n"
            + "00008 00008       15 00 00 00    End\n"
            + "' PUB main()\n"
            + "0000C 0000C       00             (stack size)\n"
            + "'     SEND := @out\n"
            + "0000D 0000D       11 01          SUB_ADDRESS (1)\n"
            + "0000F 0000F       4E 53 81       REG_WRITE +$1D3\n"
            + "00012 00012       04             RETURN\n"
            + "' PRI out()\n"
            + "00013 00013       00             (stack size)\n"
            + "00014 00014       04             RETURN\n"
            + "00015 00015       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testRecv() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    a := RECV()\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       0C 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     a := RECV()\n"
            + "00009 00009       0C             RECV\n"
            + "0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000B 0000B       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testRecvAssign() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    RECV := @in\n"
            + "\n"
            + "PRI in()\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004 00004       13 00 00 80    Method in @ $00013 (0 parameters, 0 returns)\n"
            + "00008 00008       15 00 00 00    End\n"
            + "' PUB main()\n"
            + "0000C 0000C       00             (stack size)\n"
            + "'     RECV := @in\n"
            + "0000D 0000D       11 01          SUB_ADDRESS (1)\n"
            + "0000F 0000F       4E 52 81       REG_WRITE +$1D2\n"
            + "00012 00012       04             RETURN\n"
            + "' PRI in()\n"
            + "00013 00013       00             (stack size)\n"
            + "00014 00014       04             RETURN\n"
            + "00015 00015       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testTypeExpression() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    a := BYTE[@b]\n"
            + "    a := BYTE[@b][1]\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       15 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     a := BYTE[@b]\n"
            + "00009 00009       D1 7F          VAR_ADDRESS DBASE+$00001 (short)\n"
            + "0000B 0000B       65 80          MEM_READ BYTE\n"
            + "0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := BYTE[@b][1]\n"
            + "0000E 0000E       D1 7F          VAR_ADDRESS DBASE+$00001 (short)\n"
            + "00010 00010       A2             CONSTANT (1)\n"
            + "00011 00011       62 80          MEM_READ BYTE INDEXED\n"
            + "00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00014 00014       04             RETURN\n"
            + "00015 00015       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testTypeAssign() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    BYTE[@b] := a\n"
            + "    BYTE[@b][1] := a\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       15 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     BYTE[@b] := a\n"
            + "00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       D1 7F          VAR_ADDRESS DBASE+$00001 (short)\n"
            + "0000C 0000C       65 81          MEM_WRITE BYTE\n"
            + "'     BYTE[@b][1] := a\n"
            + "0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       D1 7F          VAR_ADDRESS DBASE+$00001 (short)\n"
            + "00011 00011       A2             CONSTANT (1)\n"
            + "00012 00012       62 81          MEM_WRITE BYTE INDEXED\n"
            + "00014 00014       04             RETURN\n"
            + "00015 00015       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testTernaryExpression() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    a := (b == 1) ? 2 : 3\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       11 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     a := (b == 1) ? 2 : 3\n"
            + "00009 00009       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0000A 0000A       A2             CONSTANT (1)\n"
            + "0000B 0000B       70             EQUAL\n"
            + "0000C 0000C       A3             CONSTANT (2)\n"
            + "0000D 0000D       A4             CONSTANT (3)\n"
            + "0000E 0000E       6B             TERNARY_IF_ELSE\n"
            + "0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00010 00010       04             RETURN\n"
            + "00011 00011       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testListAssignment() throws Exception {
        String text = ""
            + "PUB start() : r | a, b\n"
            + "\n"
            + "    r, a, b := b, a, r\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 10 80    Method start @ $00008 (0 parameters, 1 returns)\n"
            + "00004 00004       10 00 00 00    End\n"
            + "' PUB start() : r | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     r, a, b := b, a, r\n"
            + "00009 00009       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "0000A 0000A       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)\n"
            + "0000D 0000D       F1             VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testListReturn() throws Exception {
        String text = ""
            + "PUB start() : a, b, c\n"
            + "\n"
            + "    a, b, c := function()\n"
            + "\n"
            + "PUB function() : r1, r2, r3\n"
            + "\n"
            + "    r1 := 1\n"
            + "    r2 := 2\n"
            + "    r3 := 3\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 30 80    Method start @ $0000C (0 parameters, 3 returns)\n"
            + "00004 00004       14 00 30 80    Method function @ $00014 (0 parameters, 3 returns)\n"
            + "00008 00008       1C 00 00 00    End\n"
            + "' PUB start() : a, b, c\n"
            + "0000C 0000C       00             (stack size)\n"
            + "'     a, b, c := function()\n"
            + "0000D 0000D       01             ANCHOR\n"
            + "0000E 0000E       0A 01          CALL_SUB (1)\n"
            + "00010 00010       F2             VAR_WRITE LONG DBASE+$00002 (short)\n"
            + "00011 00011       F1             VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00013 00013       04             RETURN\n"
            + "' PUB function() : r1, r2, r3\n"
            + "00014 00014       00             (stack size)\n"
            + "'     r1 := 1\n"
            + "00015 00015       A2             CONSTANT (1)\n"
            + "00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     r2 := 2\n"
            + "00017 00017       A3             CONSTANT (2)\n"
            + "00018 00018       F1             VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "'     r3 := 3\n"
            + "00019 00019       A4             CONSTANT (3)\n"
            + "0001A 0001A       F2             VAR_WRITE LONG DBASE+$00002 (short)\n"
            + "0001B 0001B       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testListSkipReturn() throws Exception {
        String text = ""
            + "PUB start() : a, b, c\n"
            + "\n"
            + "    a, _, c := function()\n"
            + "\n"
            + "PUB function() : r1, r2, r3\n"
            + "\n"
            + "    r1 := 1\n"
            + "    r2 := 2\n"
            + "    r3 := 3\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 30 80    Method start @ $0000C (0 parameters, 3 returns)\n"
            + "00004 00004       14 00 30 80    Method function @ $00014 (0 parameters, 3 returns)\n"
            + "00008 00008       1C 00 00 00    End\n"
            + "' PUB start() : a, b, c\n"
            + "0000C 0000C       00             (stack size)\n"
            + "'     a, _, c := function()\n"
            + "0000D 0000D       01             ANCHOR\n"
            + "0000E 0000E       0A 01          CALL_SUB (1)\n"
            + "00010 00010       F2             VAR_WRITE LONG DBASE+$00002 (short)\n"
            + "00011 00011       17             POP\n"
            + "00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00013 00013       04             RETURN\n"
            + "' PUB function() : r1, r2, r3\n"
            + "00014 00014       00             (stack size)\n"
            + "'     r1 := 1\n"
            + "00015 00015       A2             CONSTANT (1)\n"
            + "00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     r2 := 2\n"
            + "00017 00017       A3             CONSTANT (2)\n"
            + "00018 00018       F1             VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "'     r3 := 3\n"
            + "00019 00019       A4             CONSTANT (3)\n"
            + "0001A 0001A       F2             VAR_WRITE LONG DBASE+$00002 (short)\n"
            + "0001B 0001B       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testReturnListAsArguments() throws Exception {
        String text = ""
            + "PUB start() | a\n"
            + "\n"
            + "    function1(a, function2())\n"
            + "\n"
            + "PUB function1(p1, p2 , p3)\n"
            + "\n"
            + "PUB function2() : r1, r2\n"
            + "\n"
            + "    r1 := 1\n"
            + "    r2 := 2\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       10 00 00 80    Method start @ $00010 (0 parameters, 0 returns)\n"
            + "00004 00004       19 00 00 83    Method function1 @ $00019 (3 parameters, 0 returns)\n"
            + "00008 00008       1B 00 20 80    Method function2 @ $0001B (0 parameters, 2 returns)\n"
            + "0000C 0000C       21 00 00 00    End\n"
            + "' PUB start() | a\n"
            + "00010 00010       04             (stack size)\n"
            + "'     function1(a, function2())\n"
            + "00011 00011       00             ANCHOR\n"
            + "00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00013 00013       01             ANCHOR\n"
            + "00014 00014       0A 02          CALL_SUB (2)\n"
            + "00016 00016       0A 01          CALL_SUB (1)\n"
            + "00018 00018       04             RETURN\n"
            + "' PUB function1(p1, p2 , p3)\n"
            + "00019 00019       00             (stack size)\n"
            + "0001A 0001A       04             RETURN\n"
            + "' PUB function2() : r1, r2\n"
            + "0001B 0001B       00             (stack size)\n"
            + "'     r1 := 1\n"
            + "0001C 0001C       A2             CONSTANT (1)\n"
            + "0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     r2 := 2\n"
            + "0001E 0001E       A3             CONSTANT (2)\n"
            + "0001F 0001F       F1             VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "00020 00020       04             RETURN\n"
            + "00021 00021       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testPostEffects() throws Exception {
        String text = ""
            + "PUB start() : r | a, b\n"
            + "\n"
            + "    a++\n"
            + "    b--\n"
            + "    byte[a++] := 1\n"
            + "    byte[b--] := 2\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 10 80    Method start @ $00008 (0 parameters, 1 returns)\n"
            + "00004 00004       18 00 00 00    End\n"
            + "' PUB start() : r | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     a++\n"
            + "00009 00009       D1             VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "0000A 0000A       83             POST_INC\n"
            + "'     b--\n"
            + "0000B 0000B       D2             VAR_SETUP LONG DBASE+$00002 (short)\n"
            + "0000C 0000C       84             POST_DEC\n"
            + "'     byte[a++] := 1\n"
            + "0000D 0000D       A2             CONSTANT (1)\n"
            + "0000E 0000E       D1             VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "0000F 0000F       87             POST_INC (push)\n"
            + "00010 00010       65 81          MEM_WRITE BYTE\n"
            + "'     byte[b--] := 2\n"
            + "00012 00012       A3             CONSTANT (2)\n"
            + "00013 00013       D2             VAR_SETUP LONG DBASE+$00002 (short)\n"
            + "00014 00014       88             POST_DEC (push)\n"
            + "00015 00015       65 81          MEM_WRITE BYTE\n"
            + "00017 00017       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testIndexPostEffects() throws Exception {
        String text = ""
            + "PUB start() | a, b\n"
            + "\n"
            + "    b[3]++\n"
            + "    b[3]~\n"
            + "    a := b[3]++\n"
            + "    a := b[3]~\n"
            + "    a := long[3]++\n"
            + "    a := long[3]--\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       22 00 00 00    End\n"
            + "' PUB start() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     b[3]++\n"
            + "00009 00009       5E 10          VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "0000B 0000B       83             POST_INC\n"
            + "'     b[3]~\n"
            + "0000C 0000C       A1             CONSTANT (0)\n"
            + "0000D 0000D       5E 10 81       VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "'     a := b[3]++\n"
            + "00010 00010       5E 10          VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "00012 00012       87             POST_INC (push)\n"
            + "00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := b[3]~\n"
            + "00014 00014       A1             CONSTANT (0)\n"
            + "00015 00015       5E 10          VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "00017 00017       8D             SWAP\n"
            + "00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := long[3]++\n"
            + "00019 00019       A4             CONSTANT (3)\n"
            + "0001A 0001A       67             MEM_SETUP LONG\n"
            + "0001B 0001B       87             POST_INC (push)\n"
            + "0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := long[3]--\n"
            + "0001D 0001D       A4             CONSTANT (3)\n"
            + "0001E 0001E       67             MEM_SETUP LONG\n"
            + "0001F 0001F       88             POST_DEC (push)\n"
            + "00020 00020       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00021 00021       04             RETURN\n"
            + "00022 00022       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testPreEffects() throws Exception {
        String text = ""
            + "PUB start() | a, b\n"
            + "\n"
            + "    ++a\n"
            + "    --b\n"
            + "    byte[++a] := 1\n"
            + "    byte[--b] := 2\n"
            + "    b := ++byte[a]\n"
            + "    a := --byte[b]\n"
            + "    ++a[1]\n"
            + "    --b[2]\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       26 00 00 00    End\n"
            + "' PUB start() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     ++a\n"
            + "00009 00009       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       83             PRE_INC\n"
            + "'     --b\n"
            + "0000B 0000B       D1             VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "0000C 0000C       84             PRE_DEC\n"
            + "'     byte[++a] := 1\n"
            + "0000D 0000D       A2             CONSTANT (1)\n"
            + "0000E 0000E       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       85             PRE_INC (push)\n"
            + "00010 00010       65 81          MEM_WRITE BYTE\n"
            + "'     byte[--b] := 2\n"
            + "00012 00012       A3             CONSTANT (2)\n"
            + "00013 00013       D1             VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "00014 00014       86             PRE_DEC (push)\n"
            + "00015 00015       65 81          MEM_WRITE BYTE\n"
            + "'     b := ++byte[a]\n"
            + "00017 00017       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00018 00018       65             MEM_SETUP BYTE\n"
            + "00019 00019       85             PRE_INC (push)\n"
            + "0001A 0001A       F1             VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "'     a := --byte[b]\n"
            + "0001B 0001B       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0001C 0001C       65             MEM_SETUP BYTE\n"
            + "0001D 0001D       86             PRE_DEC (push)\n"
            + "0001E 0001E       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     ++a[1]\n"
            + "0001F 0001F       5E 04          VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "00021 00021       83             PRE_INC\n"
            + "'     --b[2]\n"
            + "00022 00022       5E 0C          VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "00024 00024       84             PRE_DEC\n"
            + "00025 00025       04             RETURN\n"
            + "00026 00026       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testDatVariablePreEffects() throws Exception {
        String text = ""
            + "PUB start()\n"
            + "\n"
            + "    ++a\n"
            + "    --b\n"
            + "\n"
            + "DAT\n"
            + "\n"
            + "a       long    0\n"
            + "b       long    0\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       10 00 00 80    Method start @ $00010 (0 parameters, 0 returns)\n"
            + "00004 00004       18 00 00 00    End\n"
            + "00008 00008 00000 00 00 00 00    a                   long    0\n"
            + "0000C 0000C 00004 00 00 00 00    b                   long    0\n"
            + "' PUB start()\n"
            + "00010 00010       00             (stack size)\n"
            + "'     ++a\n"
            + "00011 00011       5C 08          MEM_SETUP LONG PBASE+$00008\n"
            + "00013 00013       83             PRE_INC\n"
            + "'     --b\n"
            + "00014 00014       5C 0C          MEM_SETUP LONG PBASE+$0000C\n"
            + "00016 00016       84             PRE_DEC\n"
            + "00017 00017       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testListAssignmentEffects() throws Exception {
        String text = ""
            + "PUB start() : r | a, b\n"
            + "\n"
            + "    byte[a++], byte[b--] := byte[b++], byte[a--]\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 10 80    Method start @ $00008 (0 parameters, 1 returns)\n"
            + "00004 00004       1A 00 00 00    End\n"
            + "' PUB start() : r | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     byte[a++], byte[b--] := byte[b++], byte[a--]\n"
            + "00009 00009       D2             VAR_SETUP LONG DBASE+$00002 (short)\n"
            + "0000A 0000A       87             POST_INC (push)\n"
            + "0000B 0000B       65 80          MEM_READ BYTE\n"
            + "0000D 0000D       D1             VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "0000E 0000E       88             POST_DEC (push)\n"
            + "0000F 0000F       65 80          MEM_READ BYTE\n"
            + "00011 00011       D2             VAR_SETUP LONG DBASE+$00002 (short)\n"
            + "00012 00012       88             POST_DEC (push)\n"
            + "00013 00013       65 81          MEM_WRITE BYTE\n"
            + "00015 00015       D1             VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "00016 00016       87             POST_INC (push)\n"
            + "00017 00017       65 81          MEM_WRITE BYTE\n"
            + "00019 00019       04             RETURN\n"
            + "0001A 0001A       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testPostClear() throws Exception {
        String text = ""
            + "PUB start() | a, b\n"
            + "\n"
            + "    a~\n"
            + "    if (a~)\n"
            + "        b := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       13 00 00 00    End\n"
            + "' PUB start() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     a~\n"
            + "00009 00009       A1             CONSTANT (0)\n"
            + "0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     if (a~)\n"
            + "0000B 0000B       A1             CONSTANT (0)\n"
            + "0000C 0000C       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000D 0000D       8D             SWAP\n"
            + "0000E 0000E       13 03          JZ $00012 (3)\n"
            + "'         b := 1\n"
            + "00010 00010       A2             CONSTANT (1)\n"
            + "00011 00011       F1             VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "00012 00012       04             RETURN\n"
            + "00013 00013       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testPostSet() throws Exception {
        String text = ""
            + "PUB start() | a, b\n"
            + "\n"
            + "    a~~\n"
            + "    if (a~~)\n"
            + "        b := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       13 00 00 00    End\n"
            + "' PUB start() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     a~~\n"
            + "00009 00009       A0             CONSTANT (-1)\n"
            + "0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     if (a~~)\n"
            + "0000B 0000B       A0             CONSTANT (-1)\n"
            + "0000C 0000C       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000D 0000D       8D             SWAP\n"
            + "0000E 0000E       13 03          JZ $00012 (3)\n"
            + "'         b := 1\n"
            + "00010 00010       A2             CONSTANT (1)\n"
            + "00011 00011       F1             VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "00012 00012       04             RETURN\n"
            + "00013 00013       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testVariableTypeConstantIndex() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    a := b.byte[1]\n"
            + "    b.byte[1] := a\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       12 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     a := b.byte[1]\n"
            + "00009 00009       52 05 80       MEM_READ BYTE DBASE+$00005\n"
            + "0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     b.byte[1] := a\n"
            + "0000D 0000D       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000E 0000E       52 05 81       MEM_WRITE BYTE DBASE+$00005\n"
            + "00011 00011       04             RETURN\n"
            + "00012 00012       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testVariableTypeIndex() throws Exception {
        String text = ""
            + "PUB main() | a, b, c\n"
            + "\n"
            + "    a := b.byte[c]\n"
            + "    b.byte[c] := a\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       14 00 00 00    End\n"
            + "' PUB main() | a, b, c\n"
            + "00008 00008       0C             (stack size)\n"
            + "'     a := b.byte[c]\n"
            + "00009 00009       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "0000A 0000A       55 04 80       MEM_READ BYTE INDEXED DBASE+$00004\n"
            + "0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     b.byte[c] := a\n"
            + "0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00010 00010       55 04 81       MEM_WRITE BYTE INDEXED DBASE+$00004\n"
            + "00013 00013       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testLocalVarChainedAssignments() throws Exception {
        String text = ""
            + "PUB main() | a, b, c\n"
            + "\n"
            + "    a := b := 1\n"
            + "    a := b := c := 1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       14 00 00 00    End\n"
            + "' PUB main() | a, b, c\n"
            + "00008 00008       0C             (stack size)\n"
            + "'     a := b := 1\n"
            + "00009 00009       A2             CONSTANT (1)\n"
            + "0000A 0000A       D1             VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "0000B 0000B       82             WRITE\n"
            + "0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := b := c := 1\n"
            + "0000D 0000D       A2             CONSTANT (1)\n"
            + "0000E 0000E       D2             VAR_SETUP LONG DBASE+$00002 (short)\n"
            + "0000F 0000F       82             WRITE\n"
            + "00010 00010       D1             VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "00011 00011       82             WRITE\n"
            + "00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00013 00013       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testGlobalVarChainedAssignments() throws Exception {
        String text = ""
            + "VAR\n"
            + "\n"
            + "    long a\n"
            + "    byte b\n"
            + "    word c\n"
            + "\n"
            + "PUB main()\n"
            + "\n"
            + "    a := b := 1\n"
            + "    a := b := c := 1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       19 00 00 00    End\n"
            + "' PUB main()\n"
            + "00008 00008       00             (stack size)\n"
            + "'     a := b := 1\n"
            + "00009 00009       A2             CONSTANT (1)\n"
            + "0000A 0000A       51 08          VAR_SETUP BYTE VBASE+$00008\n"
            + "0000C 0000C       82             WRITE\n"
            + "0000D 0000D       C1 81          VAR_WRITE LONG VBASE+$00001 (short)\n"
            + "'     a := b := c := 1\n"
            + "0000F 0000F       A2             CONSTANT (1)\n"
            + "00010 00010       57 09          VAR_SETUP WORD VBASE+$00009\n"
            + "00012 00012       82             WRITE\n"
            + "00013 00013       51 08          VAR_SETUP BYTE VBASE+$00008\n"
            + "00015 00015       82             WRITE\n"
            + "00016 00016       C1 81          VAR_WRITE LONG VBASE+$00001 (short)\n"
            + "00018 00018       04             RETURN\n"
            + "00019 00019       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testVariableIndex() throws Exception {
        String text = ""
            + "PUB main() | a, b, c\n"
            + "\n"
            + "    a := b[c]\n"
            + "    a := b[1]\n"
            + "    b[c] := a\n"
            + "    b[1] := a\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       1C 00 00 00    End\n"
            + "' PUB main() | a, b, c\n"
            + "00008 00008       0C             (stack size)\n"
            + "'     a := b[c]\n"
            + "00009 00009       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "0000A 0000A       61 04 80       VAR_READ_INDEXED LONG DBASE+$00004\n"
            + "0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := b[1]\n"
            + "0000E 0000E       5E 08 80       VAR_READ LONG DBASE+$00001 (short)\n"
            + "00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     b[c] := a\n"
            + "00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00013 00013       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00014 00014       61 04 81       VAR_WRITE_INDEXED LONG DBASE+$00004\n"
            + "'     b[1] := a\n"
            + "00017 00017       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00018 00018       5E 08 81       VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "0001B 0001B       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testBitField() throws Exception {
        String text = ""
            + "PUB main() | a, b, c, d\n"
            + "\n"
            + "    a := b.[1]\n"
            + "    a := b.[2..1]\n"
            + "    a := b.[c]\n"
            + "    a := b.[d..c]\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       20 00 00 00    End\n"
            + "' PUB main() | a, b, c, d\n"
            + "00008 00008       10             (stack size)\n"
            + "'     a := b.[1]\n"
            + "00009 00009       D1             VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "0000A 0000A       E1 80          BITFIELD_READ (short)\n"
            + "0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := b.[2..1]\n"
            + "0000D 0000D       D1             VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "0000E 0000E       DF 21 80       BITFIELD_READ\n"
            + "00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := b.[c]\n"
            + "00012 00012       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00013 00013       D1             VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "00014 00014       DE 80          BITFIELD_READ (pop)\n"
            + "00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := b.[d..c]\n"
            + "00017 00017       E3             VAR_READ LONG DBASE+$00003 (short)\n"
            + "00018 00018       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00019 00019       9F 94          ADDBITS\n"
            + "0001B 0001B       D1             VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "0001C 0001C       DE 80          BITFIELD_READ (pop)\n"
            + "0001E 0001E       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001F 0001F       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testBitFieldChainedAssignment() throws Exception {
        String text = ""
            + "PUB main() | a, b, c\n"
            + "\n"
            + "    a.[1] := b.[2] := 1\n"
            + "    a.[1] := b.[2] := c.[3] := 1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       1B 00 00 00    End\n"
            + "' PUB main() | a, b, c\n"
            + "00008 00008       0C             (stack size)\n"
            + "'     a.[1] := b.[2] := 1\n"
            + "00009 00009       A2             CONSTANT (1)\n"
            + "0000A 0000A       D1             VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "0000B 0000B       E2 82          BITFIELD_WRITE (short) (push)\n"
            + "0000D 0000D       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000E 0000E       E1 81          BITFIELD_WRITE (short)\n"
            + "'     a.[1] := b.[2] := c.[3] := 1\n"
            + "00010 00010       A2             CONSTANT (1)\n"
            + "00011 00011       D2             VAR_SETUP LONG DBASE+$00002 (short)\n"
            + "00012 00012       E3 82          BITFIELD_WRITE (short) (push)\n"
            + "00014 00014       D1             VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "00015 00015       E2 82          BITFIELD_WRITE (short) (push)\n"
            + "00017 00017       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "00018 00018       E1 81          BITFIELD_WRITE (short)\n"
            + "0001A 0001A       04             RETURN\n"
            + "0001B 0001B       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testBitFieldConstants() throws Exception {
        String text = ""
            + "PUB main() | a, b, c, d\n"
            + "\n"
            + "    a.[0] := 1\n"
            + "    a.[1] := 1\n"
            + "    a.[16] := 1\n"
            + "    a.[17] := 1\n"
            + "    a := a.[0]\n"
            + "    a := a.[1]\n"
            + "    a := a.[16]\n"
            + "    a := a.[17]\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       2A 00 00 00    End\n"
            + "' PUB main() | a, b, c, d\n"
            + "00008 00008       10             (stack size)\n"
            + "'     a.[0] := 1\n"
            + "00009 00009       A2             CONSTANT (1)\n"
            + "0000A 0000A       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000B 0000B       E0 81          BITFIELD_WRITE (short)\n"
            + "'     a.[1] := 1\n"
            + "0000D 0000D       A2             CONSTANT (1)\n"
            + "0000E 0000E       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       E1 81          BITFIELD_WRITE (short)\n"
            + "'     a.[16] := 1\n"
            + "00011 00011       A2             CONSTANT (1)\n"
            + "00012 00012       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "00013 00013       F0 81          BITFIELD_WRITE (short)\n"
            + "'     a.[17] := 1\n"
            + "00015 00015       A2             CONSTANT (1)\n"
            + "00016 00016       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "00017 00017       F1 81          BITFIELD_WRITE (short)\n"
            + "'     a := a.[0]\n"
            + "00019 00019       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0001A 0001A       E0 80          BITFIELD_READ (short)\n"
            + "0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := a.[1]\n"
            + "0001D 0001D       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0001E 0001E       E1 80          BITFIELD_READ (short)\n"
            + "00020 00020       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := a.[16]\n"
            + "00021 00021       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "00022 00022       F0 80          BITFIELD_READ (short)\n"
            + "00024 00024       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := a.[17]\n"
            + "00025 00025       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "00026 00026       F1 80          BITFIELD_READ (short)\n"
            + "00028 00028       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00029 00029       04             RETURN\n"
            + "0002A 0002A       00 00          Padding\n"
            + ""
            + "", compile(text));
    }

    @Test
    void testBitFieldPostEffects() throws Exception {
        String text = ""
            + "PUB main() | a, b, c, d\n"
            + "\n"
            + "    a := b[3].[1]++\n"
            + "    a := b[3].[2..1]--\n"
            + "    a := b[a].[c]~\n"
            + "    a := b[a].[d..c]~~\n"
            + "    b[3].[1]++\n"
            + "    b[3].[2..1]--\n"
            + "    b[a].[c]~\n"
            + "    b[a].[d..c]~~\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       42 00 00 00    End\n"
            + "' PUB main() | a, b, c, d\n"
            + "00008 00008       10             (stack size)\n"
            + "'     a := b[3].[1]++\n"
            + "00009 00009       5E 10          VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "0000B 0000B       E1             BITFIELD_SETUP (short)\n"
            + "0000C 0000C       87             POST_INC\n"
            + "0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := b[3].[2..1]--\n"
            + "0000E 0000E       5E 10          VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "00010 00010       DF 21          BITFIELD_SETUP\n"
            + "00012 00012       88             POST_DEC\n"
            + "00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := b[a].[c]~\n"
            + "00014 00014       A1             CONSTANT (0)\n"
            + "00015 00015       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00016 00016       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00017 00017       61 04          VAR_SETUP_INDEXED LONG DBASE+$00004\n"
            + "00019 00019       DE             BITFIELD_SETUP (pop)\n"
            + "0001A 0001A       8D             SWAP\n"
            + "0001B 0001B       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := b[a].[d..c]~~\n"
            + "0001C 0001C       A0             CONSTANT (-1)\n"
            + "0001D 0001D       E3             VAR_READ LONG DBASE+$00003 (short)\n"
            + "0001E 0001E       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "0001F 0001F       9F 94          ADDBITS\n"
            + "00021 00021       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00022 00022       61 04          VAR_SETUP_INDEXED LONG DBASE+$00004\n"
            + "00024 00024       DE             BITFIELD_SETUP (pop)\n"
            + "00025 00025       8D             SWAP\n"
            + "00026 00026       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     b[3].[1]++\n"
            + "00027 00027       5E 10          VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "00029 00029       E1             BITFIELD_SETUP (short)\n"
            + "0002A 0002A       83             POST_INC\n"
            + "'     b[3].[2..1]--\n"
            + "0002B 0002B       5E 10          VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "0002D 0002D       DF 21          BITFIELD_SETUP\n"
            + "0002F 0002F       84             POST_DEC\n"
            + "'     b[a].[c]~\n"
            + "00030 00030       A1             CONSTANT (0)\n"
            + "00031 00031       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00032 00032       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00033 00033       61 04          VAR_SETUP_INDEXED LONG DBASE+$00004\n"
            + "00035 00035       DE             BITFIELD_SETUP (pop)\n"
            + "00036 00036       81             WRITE\n"
            + "'     b[a].[d..c]~~\n"
            + "00037 00037       A0             CONSTANT (-1)\n"
            + "00038 00038       E3             VAR_READ LONG DBASE+$00003 (short)\n"
            + "00039 00039       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "0003A 0003A       9F 94          ADDBITS\n"
            + "0003C 0003C       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0003D 0003D       61 04          VAR_SETUP_INDEXED LONG DBASE+$00004\n"
            + "0003F 0003F       DE             BITFIELD_SETUP (pop)\n"
            + "00040 00040       81             WRITE\n"
            + "00041 00041       04             RETURN\n"
            + "00042 00042       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testTypedBitField() throws Exception {
        String text = ""
            + "PUB main() | a, b, c, d\n"
            + "\n"
            + "    a := b.byte[1]\n"
            + "    a := b.byte[2..1]\n"
            + "    a := b.word[c]\n"
            + "    a := b.word[d..c]\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       1F 00 00 00    End\n"
            + "' PUB main() | a, b, c, d\n"
            + "00008 00008       10             (stack size)\n"
            + "'     a := b.byte[1]\n"
            + "00009 00009       52 05 80       MEM_READ BYTE DBASE+$00005\n"
            + "0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := b.byte[2..1]\n"
            + "0000D 0000D       A3             CONSTANT (2)\n"
            + "0000E 0000E       A2             CONSTANT (1)\n"
            + "0000F 0000F       55 04 80       MEM_READ BYTE INDEXED DBASE+$00004\n"
            + "00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := b.word[c]\n"
            + "00013 00013       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00014 00014       5B 04 80       MEM_READ WORD INDEXED DBASE+$00004\n"
            + "00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := b.word[d..c]\n"
            + "00018 00018       E3             VAR_READ LONG DBASE+$00003 (short)\n"
            + "00019 00019       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "0001A 0001A       5B 04 80       MEM_READ WORD INDEXED DBASE+$00004\n"
            + "0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001E 0001E       04             RETURN\n"
            + "0001F 0001F       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testIndexedBitField() throws Exception {
        String text = ""
            + "PUB main() | a, b, c, d\n"
            + "\n"
            + "    a := b[3].[1]\n"
            + "    a := b[3].[2..1]\n"
            + "    a := b[a].[c]\n"
            + "    a := b[a].[d..c]\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       26 00 00 00    End\n"
            + "' PUB main() | a, b, c, d\n"
            + "00008 00008       10             (stack size)\n"
            + "'     a := b[3].[1]\n"
            + "00009 00009       5E 10          VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "0000B 0000B       E1 80          BITFIELD_READ (short)\n"
            + "0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := b[3].[2..1]\n"
            + "0000E 0000E       5E 10          VAR_SETUP LONG DBASE+$00001 (short)\n"
            + "00010 00010       DF 21 80       BITFIELD_READ\n"
            + "00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := b[a].[c]\n"
            + "00014 00014       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00015 00015       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00016 00016       61 04          VAR_SETUP_INDEXED LONG DBASE+$00004\n"
            + "00018 00018       DE 80          BITFIELD_READ (pop)\n"
            + "0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := b[a].[d..c]\n"
            + "0001B 0001B       E3             VAR_READ LONG DBASE+$00003 (short)\n"
            + "0001C 0001C       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "0001D 0001D       9F 94          ADDBITS\n"
            + "0001F 0001F       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00020 00020       61 04          VAR_SETUP_INDEXED LONG DBASE+$00004\n"
            + "00022 00022       DE 80          BITFIELD_READ (pop)\n"
            + "00024 00024       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00025 00025       04             RETURN\n"
            + "00026 00026       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testMemoryBitFieldWrite() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    byte[0].[3] := a\n"
            + "    word[1].[3] := a\n"
            + "    long[2].[3] := a\n"
            + "    byte[0][4].[3] := a\n"
            + "    word[1][4].[3] := a\n"
            + "    long[2][4].[3] := a\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       2B 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     byte[0].[3] := a\n"
            + "00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       A1             CONSTANT (0)\n"
            + "0000B 0000B       65             MEM_SETUP BYTE\n"
            + "0000C 0000C       E3 81          BITFIELD_WRITE (short)\n"
            + "'     word[1].[3] := a\n"
            + "0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       A2             CONSTANT (1)\n"
            + "00010 00010       66             MEM_SETUP WORD\n"
            + "00011 00011       E3 81          BITFIELD_WRITE (short)\n"
            + "'     long[2].[3] := a\n"
            + "00013 00013       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00014 00014       A3             CONSTANT (2)\n"
            + "00015 00015       67             MEM_SETUP LONG\n"
            + "00016 00016       E3 81          BITFIELD_WRITE (short)\n"
            + "'     byte[0][4].[3] := a\n"
            + "00018 00018       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00019 00019       A1             CONSTANT (0)\n"
            + "0001A 0001A       A5             CONSTANT (4)\n"
            + "0001B 0001B       62             MEM_SETUP BYTE INDEXED\n"
            + "0001C 0001C       E3 81          BITFIELD_WRITE (short)\n"
            + "'     word[1][4].[3] := a\n"
            + "0001E 0001E       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0001F 0001F       A2             CONSTANT (1)\n"
            + "00020 00020       A5             CONSTANT (4)\n"
            + "00021 00021       63             MEM_SETUP WORD INDEXED\n"
            + "00022 00022       E3 81          BITFIELD_WRITE (short)\n"
            + "'     long[2][4].[3] := a\n"
            + "00024 00024       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00025 00025       A3             CONSTANT (2)\n"
            + "00026 00026       A5             CONSTANT (4)\n"
            + "00027 00027       64             MEM_SETUP LONG INDEXED\n"
            + "00028 00028       E3 81          BITFIELD_WRITE (short)\n"
            + "0002A 0002A       04             RETURN\n"
            + "0002B 0002B       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testMemoryBitFieldRead() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    a := byte[0].[3]\n"
            + "    a := word[1].[3]\n"
            + "    a := long[2].[3]\n"
            + "    a := byte[0][4].[3]\n"
            + "    a := word[1][4].[3]\n"
            + "    a := long[2][4].[3]\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       2B 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     a := byte[0].[3]\n"
            + "00009 00009       A1             CONSTANT (0)\n"
            + "0000A 0000A       65             MEM_SETUP BYTE\n"
            + "0000B 0000B       E3 80          BITFIELD_READ (short)\n"
            + "0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := word[1].[3]\n"
            + "0000E 0000E       A2             CONSTANT (1)\n"
            + "0000F 0000F       66             MEM_SETUP WORD\n"
            + "00010 00010       E3 80          BITFIELD_READ (short)\n"
            + "00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := long[2].[3]\n"
            + "00013 00013       A3             CONSTANT (2)\n"
            + "00014 00014       67             MEM_SETUP LONG\n"
            + "00015 00015       E3 80          BITFIELD_READ (short)\n"
            + "00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := byte[0][4].[3]\n"
            + "00018 00018       A1             CONSTANT (0)\n"
            + "00019 00019       A5             CONSTANT (4)\n"
            + "0001A 0001A       62             MEM_SETUP BYTE INDEXED\n"
            + "0001B 0001B       E3 80          BITFIELD_READ (short)\n"
            + "0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := word[1][4].[3]\n"
            + "0001E 0001E       A2             CONSTANT (1)\n"
            + "0001F 0001F       A5             CONSTANT (4)\n"
            + "00020 00020       63             MEM_SETUP WORD INDEXED\n"
            + "00021 00021       E3 80          BITFIELD_READ (short)\n"
            + "00023 00023       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := long[2][4].[3]\n"
            + "00024 00024       A3             CONSTANT (2)\n"
            + "00025 00025       A5             CONSTANT (4)\n"
            + "00026 00026       64             MEM_SETUP LONG INDEXED\n"
            + "00027 00027       E3 80          BITFIELD_READ (short)\n"
            + "00029 00029       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0002A 0002A       04             RETURN\n"
            + "0002B 0002B       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testMemoryPostEffects() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    a := byte[3]++\n"
            + "    a := word[3]--\n"
            + "    a := long[a]~\n"
            + "    a := long[a]~~\n"
            + "    byte[3]++\n"
            + "    word[3]--\n"
            + "    long[a]~\n"
            + "    long[a]~~\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       2A 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     a := byte[3]++\n"
            + "00009 00009       A4             CONSTANT (3)\n"
            + "0000A 0000A       65             MEM_SETUP BYTE\n"
            + "0000B 0000B       87             POST_INC (push)\n"
            + "0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := word[3]--\n"
            + "0000D 0000D       A4             CONSTANT (3)\n"
            + "0000E 0000E       66             MEM_SETUP WORD\n"
            + "0000F 0000F       88             POST_DEC (push)\n"
            + "00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := long[a]~\n"
            + "00011 00011       A1             CONSTANT (0)\n"
            + "00012 00012       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00013 00013       67             MEM_SETUP LONG\n"
            + "00014 00014       8D             SWAP\n"
            + "00015 00015       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := long[a]~~\n"
            + "00016 00016       A0             CONSTANT (-1)\n"
            + "00017 00017       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00018 00018       67             MEM_SETUP LONG\n"
            + "00019 00019       8D             SWAP\n"
            + "0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     byte[3]++\n"
            + "0001B 0001B       A4             CONSTANT (3)\n"
            + "0001C 0001C       65             MEM_SETUP BYTE\n"
            + "0001D 0001D       83             POST_INC\n"
            + "'     word[3]--\n"
            + "0001E 0001E       A4             CONSTANT (3)\n"
            + "0001F 0001F       66             MEM_SETUP WORD\n"
            + "00020 00020       84             POST_DEC\n"
            + "'     long[a]~\n"
            + "00021 00021       A1             CONSTANT (0)\n"
            + "00022 00022       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00023 00023       67             MEM_SETUP LONG\n"
            + "00024 00024       81             WRITE\n"
            + "'     long[a]~~\n"
            + "00025 00025       A0             CONSTANT (-1)\n"
            + "00026 00026       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00027 00027       67             MEM_SETUP LONG\n"
            + "00028 00028       81             WRITE\n"
            + "00029 00029       04             RETURN\n"
            + "0002A 0002A       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testMemoryBitFieldPostEffects() throws Exception {
        String text = ""
            + "PUB main() | a, b, c, d\n"
            + "\n"
            + "    a := byte[3].[1]++\n"
            + "    a := word[3].[2..1]--\n"
            + "    a := long[a].[c]~\n"
            + "    a := long[a].[d..c]~~\n"
            + "    byte[3].[1]++\n"
            + "    word[3].[2..1]--\n"
            + "    long[a].[c]~\n"
            + "    long[a].[d..c]~~\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       3E 00 00 00    End\n"
            + "' PUB main() | a, b, c, d\n"
            + "00008 00008       10             (stack size)\n"
            + "'     a := byte[3].[1]++\n"
            + "00009 00009       A4             CONSTANT (3)\n"
            + "0000A 0000A       65             MEM_SETUP BYTE\n"
            + "0000B 0000B       E1             BITFIELD_SETUP (short)\n"
            + "0000C 0000C       87             POST_INC (push)\n"
            + "0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := word[3].[2..1]--\n"
            + "0000E 0000E       A4             CONSTANT (3)\n"
            + "0000F 0000F       66             MEM_SETUP WORD\n"
            + "00010 00010       DF 21          BITFIELD_SETUP\n"
            + "00012 00012       88             POST_DEC (push)\n"
            + "00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := long[a].[c]~\n"
            + "00014 00014       A1             CONSTANT (0)\n"
            + "00015 00015       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00016 00016       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00017 00017       67             MEM_SETUP LONG\n"
            + "00018 00018       DE             BITFIELD_SETUP (pop)\n"
            + "00019 00019       8D             SWAP\n"
            + "0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := long[a].[d..c]~~\n"
            + "0001B 0001B       A0             CONSTANT (-1)\n"
            + "0001C 0001C       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0001D 0001D       E3             VAR_READ LONG DBASE+$00003 (short)\n"
            + "0001E 0001E       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "0001F 0001F       9F 94          ADDBITS\n"
            + "00021 00021       67             MEM_SETUP LONG\n"
            + "00022 00022       DE             BITFIELD_SETUP (pop)\n"
            + "00023 00023       8D             SWAP\n"
            + "00024 00024       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     byte[3].[1]++\n"
            + "00025 00025       A4             CONSTANT (3)\n"
            + "00026 00026       65             MEM_SETUP BYTE\n"
            + "00027 00027       E1             BITFIELD_SETUP (short)\n"
            + "00028 00028       83             POST_INC\n"
            + "'     word[3].[2..1]--\n"
            + "00029 00029       A4             CONSTANT (3)\n"
            + "0002A 0002A       66             MEM_SETUP WORD\n"
            + "0002B 0002B       DF 21          BITFIELD_SETUP\n"
            + "0002D 0002D       84             POST_DEC\n"
            + "'     long[a].[c]~\n"
            + "0002E 0002E       A1             CONSTANT (0)\n"
            + "0002F 0002F       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00030 00030       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00031 00031       67             MEM_SETUP LONG\n"
            + "00032 00032       DE             BITFIELD_SETUP (pop)\n"
            + "00033 00033       81             WRITE\n"
            + "'     long[a].[d..c]~~\n"
            + "00034 00034       A0             CONSTANT (-1)\n"
            + "00035 00035       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00036 00036       E3             VAR_READ LONG DBASE+$00003 (short)\n"
            + "00037 00037       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00038 00038       9F 94          ADDBITS\n"
            + "0003A 0003A       67             MEM_SETUP LONG\n"
            + "0003B 0003B       DE             BITFIELD_SETUP (pop)\n"
            + "0003C 0003C       81             WRITE\n"
            + "0003D 0003D       04             RETURN\n"
            + "0003E 0003E       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testMemoryBitfields() throws Exception {
        String text = ""
            + "PUB main() | a, b, c\n"
            + "\n"
            + "    word[@a][1].[2] ^= 3\n"
            + "    word[@a][b].[c] ^= 3\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       19 00 00 00    End\n"
            + "' PUB main() | a, b, c\n"
            + "00008 00008       0C             (stack size)\n"
            + "'     word[@a][1].[2] ^= 3\n"
            + "00009 00009       A4             CONSTANT (3)\n"
            + "0000A 0000A       D0 7F          VAR_ADDRESS DBASE+$00000 (short)\n"
            + "0000C 0000C       A2             CONSTANT (1)\n"
            + "0000D 0000D       63             MEM_SETUP WORD INDEXED\n"
            + "0000E 0000E       E2             BITFIELD_SETUP (short)\n"
            + "0000F 0000F       A9             BITXOR_ASSIGN\n"
            + "'     word[@a][b].[c] ^= 3\n"
            + "00010 00010       A4             CONSTANT (3)\n"
            + "00011 00011       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00012 00012       D0 7F          VAR_ADDRESS DBASE+$00000 (short)\n"
            + "00014 00014       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00015 00015       63             MEM_SETUP WORD INDEXED\n"
            + "00016 00016       DE             BITFIELD_SETUP (pop)\n"
            + "00017 00017       A9             BITXOR_ASSIGN\n"
            + "00018 00018       04             RETURN\n"
            + "00019 00019       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testInlineAssembly() throws Exception {
        String text = ""
            + "PUB main(a)\n"
            + "\n"
            + "        org\n"
            + "        mov     pr0, #0\n"
            + "l1      add     pr0, a\n"
            + "        djnz    a, #l1\n"
            + "        end\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 81    Method main @ $00008 (1 parameters, 0 returns)\n"
            + "00004 00004       20 00 00 00    End\n"
            + "' PUB main(a)\n"
            + "00008 00008       00             (stack size)\n"
            + "'         org\n"
            + "00009 00009       19 5C          INLINE-EXEC\n"
            + "0000B 0000B       00 00 03 00    ORG=$000, 4\n"
            + "0000F 0000F   003                                    org\n"
            + "0000F 0000F   000 00 B0 07 F6                        mov     pr0, #0\n"
            + "00013 00013   001 E0 B1 03 F1    l1                  add     pr0, a\n"
            + "00017 00017   002 FE C1 6F FB                        djnz    a, #l1\n"
            + "0001B 0001B   003 2D 00 64 FD                        ret\n"
            + "0001F 0001F       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testAssemblyCall() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "    call(#pasm0)\n"
            + "    call(#pasm1)\n"
            + "\n"
            + "DAT\n"
            + "        org\n"
            + "pasm0 _ret_ drvnot  pr0\n"
            + "pasm1 _ret_ drvl    pr0\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)\n"
            + "00004 00004       18 00 00 00    End\n"
            + "00008 00008   000                                    org\n"
            + "00008 00008   000 5F B0 63 0D    pasm0   _ret_       drvnot  pr0\n"
            + "0000C 0000C   001 58 B0 63 0D    pasm1   _ret_       drvl    pr0\n"
            + "' PUB main()\n"
            + "00010 00010       00             (stack size)\n"
            + "'     call(#pasm0)\n"
            + "00011 00011       A1             CONSTANT ($00000)\n"
            + "00012 00012       19 62          CALL\n"
            + "'     call(#pasm1)\n"
            + "00014 00014       A2             CONSTANT ($00001)\n"
            + "00015 00015       19 62          CALL\n"
            + "00017 00017       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testAssemblyCallExpression() throws Exception {
        String text = ""
            + "PUB main(a)\n"
            + "    call(#pasm + a)\n"
            + "\n"
            + "DAT\n"
            + "        org\n"
            + "pasm  _ret_ drvnot  pr0\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 81    Method main @ $0000C (1 parameters, 0 returns)\n"
            + "00004 00004       15 00 00 00    End\n"
            + "00008 00008   000                                    org\n"
            + "00008 00008   000 5F B0 63 0D    pasm    _ret_       drvnot  pr0\n"
            + "' PUB main(a)\n"
            + "0000C 0000C       00             (stack size)\n"
            + "'     call(#pasm + a)\n"
            + "0000D 0000D       5C 08 80       MEM_READ LONG PBASE+$00008\n"
            + "00010 00010       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00011 00011       8A             ADD\n"
            + "00012 00012       19 62          CALL\n"
            + "00014 00014       04             RETURN\n"
            + "00015 00015       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testRegisters() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    DIRA := 1\n"
            + "    DIRA[1] := 2\n"
            + "    a := INA\n"
            + "    a := INA[1]\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       18 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       04             (stack size)\n"
            + "'     DIRA := 1\n"
            + "00009 00009       A2             CONSTANT (1)\n"
            + "0000A 0000A       BA 81          REG_WRITE +$1FA (short)\n"
            + "'     DIRA[1] := 2\n"
            + "0000C 0000C       A3             CONSTANT (2)\n"
            + "0000D 0000D       4E 7B 81       REG_WRITE +$1FB\n"
            + "'     a := INA\n"
            + "00010 00010       BE 80          REG_READ +$1FE (short)\n"
            + "00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := INA[1]\n"
            + "00013 00013       4E 7F 80       REG_READ +$1FF\n"
            + "00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00017 00017       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testString() throws Exception {
        String text = ""
            + "PUB main() | a, b, c\n"
            + "\n"
            + "    a := string(\"1234\", 13, 10)\n"
            + "    b := \"1234\"\n"
            + "    c := @\"1234\"\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       24 00 00 00    End\n"
            + "' PUB main() | a, b, c\n"
            + "00008 00008       0C             (stack size)\n"
            + "'     a := string(\"1234\", 13, 10)\n"
            + "00009 00009       9E 07 31 32 33 STRING\n"
            + "0000E 0000E       34 0D 0A 00\n"
            + "00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     b := \"1234\"\n"
            + "00013 00013       9E 05 31 32 33 STRING\n"
            + "00018 00018       34 00\n"
            + "0001A 0001A       F1             VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "'     c := @\"1234\"\n"
            + "0001B 0001B       9E 05 31 32 33 STRING\n"
            + "00020 00020       34 00\n"
            + "00022 00022       F2             VAR_WRITE LONG DBASE+$00002 (short)\n"
            + "00023 00023       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testLookup() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    a := lookup(b : 10, 20..30, 40)\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       1A 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     a := lookup(b : 10, 20..30, 40)\n"
            + "00009 00009       45 18          ADDRESS ($00018)\n"
            + "0000B 0000B       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0000C 0000C       A2             CONSTANT (1)\n"
            + "0000D 0000D       AB             CONSTANT (10)\n"
            + "0000E 0000E       1F             LOOKUP\n"
            + "0000F 0000F       45 14          CONSTANT (20)\n"
            + "00011 00011       45 1E          CONSTANT (30)\n"
            + "00013 00013       21             LOOKUP\n"
            + "00014 00014       45 28          CONSTANT (40)\n"
            + "00016 00016       1F             LOOKUP\n"
            + "00017 00017       23             LOOKDONE\n"
            + "00018 00018       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00019 00019       04             RETURN\n"
            + "0001A 0001A       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testLookdownString() throws Exception {
        String text = ""
            + "PUB main() | a, b\n"
            + "\n"
            + "    a := lookdown(b : \"abcdefgh\")\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       28 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     a := lookdown(b : \"abcdefgh\")\n"
            + "00009 00009       45 26          ADDRESS ($00026)\n"
            + "0000B 0000B       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0000C 0000C       A2             CONSTANT (1)\n"
            + "0000D 0000D       45 61          CONSTANT (\"a\")\n"
            + "0000F 0000F       20             LOOKDOWN\n"
            + "00010 00010       45 62          CONSTANT (\"b\")\n"
            + "00012 00012       20             LOOKDOWN\n"
            + "00013 00013       45 63          CONSTANT (\"c\")\n"
            + "00015 00015       20             LOOKDOWN\n"
            + "00016 00016       45 64          CONSTANT (\"d\")\n"
            + "00018 00018       20             LOOKDOWN\n"
            + "00019 00019       45 65          CONSTANT (\"e\")\n"
            + "0001B 0001B       20             LOOKDOWN\n"
            + "0001C 0001C       45 66          CONSTANT (\"f\")\n"
            + "0001E 0001E       20             LOOKDOWN\n"
            + "0001F 0001F       45 67          CONSTANT (\"g\")\n"
            + "00021 00021       20             LOOKDOWN\n"
            + "00022 00022       45 68          CONSTANT (\"h\")\n"
            + "00024 00024       20             LOOKDOWN\n"
            + "00025 00025       23             LOOKDONE\n"
            + "00026 00026       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00027 00027       04             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testFloatOp() throws Exception {
        String text = ""
            + "PUB main() | a, b, c\n"
            + "\n"
            + "        a := b *. c\n"
            + "        a := round(b +. c)\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       16 00 00 00    End\n"
            + "' PUB main() | a, b, c\n"
            + "00008 00008       0C             (stack size)\n"
            + "'         a := b *. c\n"
            + "00009 00009       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0000A 0000A       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "0000B 0000B       19 A4          FLOAT_MULTIPLY\n"
            + "0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'         a := round(b +. c)\n"
            + "0000E 0000E       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0000F 0000F       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00010 00010       19 A0          FLOAT_ADD\n"
            + "00012 00012       19 8A          ROUND\n"
            + "00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00015 00015       04             RETURN\n"
            + "00016 00016       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testPostAssign() throws Exception {
        String text = ""
            + "PUB start() : a, b\n"
            + "\n"
            + "    if a\\true\n"
            + "        b := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 20 80    Method start @ $00008 (0 parameters, 2 returns)\n"
            + "00004 00004       11 00 00 00    End\n"
            + "' PUB start() : a, b\n"
            + "00008 00008       00             (stack size)\n"
            + "'     if a\\true\n"
            + "00009 00009       A0             CONSTANT (-1)\n"
            + "0000A 0000A       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000B 0000B       8D             SWAP\n"
            + "0000C 0000C       13 03          JZ $00010 (3)\n"
            + "'         b := 1\n"
            + "0000E 0000E       A2             CONSTANT (1)\n"
            + "0000F 0000F       F1             VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "00010 00010       04             RETURN\n"
            + "00011 00011       00 00 00       Padding\n"
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
            + "' Object header\n"
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
            + "' Object header\n"
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
            + "' Object header\n"
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
            + "' Object header\n"
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
            + "' Object header\n"
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
            + "' Object header\n"
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
            + "' Object header\n"
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
            + "' Object header\n"
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
            + "' Object header\n"
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
            + "' Object header\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 01 06 04 F6                        mov     a, #1\n"
            + "00004 00004   001 36 02 64 FD                        debug   #1\n"
            + "00008 00008   002 2D 00 64 FD                        ret\n"
            + "0000C 0000C   003                a                   res     1\n"
            + "' Debug data\n"
            + "009C8 00000       0C 00         \n"
            + "009CA 00002       04 00         \n"
            + "009CC 00004       01 04 41 61 00\n"
            + "009D1 00009       80 03 00\n"
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
            + "' Object header\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 01 04 04 F6                        mov     a, #1\n"
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
            + "' Object header\n"
            + "00000 00000   000                                    org     $000\n"
            + "00000 00000   000 08 04 04 F6                        mov     a, #@@a\n"
            + "00004 00004   001 2D 00 64 FD                        ret\n"
            + "00008 00008   002                a                   res     1\n"
            + "", compile(text, false));
    }

    @Test
    void testMethodAddress() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    a := @function\n"
            + "\n"
            + "PUB function()\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004 00004       11 00 00 80    Method function @ $00011 (0 parameters, 0 returns)\n"
            + "00008 00008       13 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "0000C 0000C       04             (stack size)\n"
            + "'     a := @function\n"
            + "0000D 0000D       11 01          SUB_ADDRESS (1)\n"
            + "0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00010 00010       04             RETURN\n"
            + "' PUB function()\n"
            + "00011 00011       00             (stack size)\n"
            + "00012 00012       04             RETURN\n"
            + "00013 00013       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testMethodPointers() throws Exception {
        String text = ""
            + "VAR"
            + "    long _ptr1"
            + "\n"
            + "PUB method(x, y) | _ptr2\n"
            + "    _ptr1(x, y)\n"
            + "    _ptr2(x, y)\n"
            + "    _ptr3(x, y)\n"
            + "\n"
            + "DAT\n"
            + "_ptr3           long  0\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 82    Method method @ $0000C (2 parameters, 0 returns)\n"
            + "00004 00004       20 00 00 00    End\n"
            + "00008 00008 00000 00 00 00 00    _ptr3               long    0\n"
            + "' PUB method(x, y) | _ptr2\n"
            + "0000C 0000C       04             (stack size)\n"
            + "'     _ptr1(x, y)\n"
            + "0000D 0000D       00             ANCHOR\n"
            + "0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00010 00010       C1 80          VAR_READ LONG VBASE+$00001 (short)\n"
            + "00012 00012       0B             CALL_PTR\n"
            + "'     _ptr2(x, y)\n"
            + "00013 00013       00             ANCHOR\n"
            + "00014 00014       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00015 00015       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00016 00016       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00017 00017       0B             CALL_PTR\n"
            + "'     _ptr3(x, y)\n"
            + "00018 00018       00             ANCHOR\n"
            + "00019 00019       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0001A 0001A       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0001B 0001B       5C 08 80       MEM_READ LONG PBASE+$00008\n"
            + "0001E 0001E       0B             CALL_PTR\n"
            + "0001F 0001F       04             RETURN\n"
            + "", compile(text, false));
    }

    @Test
    void testMethodPointersTrap() throws Exception {
        String text = ""
            + "VAR"
            + "    long _ptr1"
            + "\n"
            + "PUB method(x, y) | _ptr2\n"
            + "    \\_ptr1(x, y)\n"
            + "    \\_ptr2(x, y)\n"
            + "    \\_ptr3(x, y)\n"
            + "\n"
            + "DAT\n"
            + "_ptr3           long  0\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 82    Method method @ $0000C (2 parameters, 0 returns)\n"
            + "00004 00004       20 00 00 00    End\n"
            + "00008 00008 00000 00 00 00 00    _ptr3               long    0\n"
            + "' PUB method(x, y) | _ptr2\n"
            + "0000C 0000C       04             (stack size)\n"
            + "'     \\_ptr1(x, y)\n"
            + "0000D 0000D       02             ANCHOR_TRAP\n"
            + "0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00010 00010       C1 80          VAR_READ LONG VBASE+$00001 (short)\n"
            + "00012 00012       0B             CALL_PTR\n"
            + "'     \\_ptr2(x, y)\n"
            + "00013 00013       02             ANCHOR_TRAP\n"
            + "00014 00014       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00015 00015       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00016 00016       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00017 00017       0B             CALL_PTR\n"
            + "'     \\_ptr3(x, y)\n"
            + "00018 00018       02             ANCHOR_TRAP\n"
            + "00019 00019       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0001A 0001A       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0001B 0001B       5C 08 80       MEM_READ LONG PBASE+$00008\n"
            + "0001E 0001E       0B             CALL_PTR\n"
            + "0001F 0001F       04             RETURN\n"
            + "", compile(text, false));
    }

    @Test
    void testMethodPointerReturn() throws Exception {
        String text = ""
            + "VAR"
            + "    long _ptr"
            + "\n"
            + "PUB main() | a, b\n"
            + "    a := _ptr():1\n"
            + "    a, b := _ptr():2\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       15 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     a := _ptr():1\n"
            + "00009 00009       01             ANCHOR\n"
            + "0000A 0000A       C1 80          VAR_READ LONG VBASE+$00001 (short)\n"
            + "0000C 0000C       0B             CALL_PTR\n"
            + "0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a, b := _ptr():2\n"
            + "0000E 0000E       01             ANCHOR\n"
            + "0000F 0000F       C1 80          VAR_READ LONG VBASE+$00001 (short)\n"
            + "00011 00011       0B             CALL_PTR\n"
            + "00012 00012       F1             VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00014 00014       04             RETURN\n"
            + "00015 00015       00 00 00       Padding\n"
            + "", compile(text, false));
    }

    @Test
    void testMethodPointerReturnTernary() throws Exception {
        String text = ""
            + "VAR"
            + "    long _ptr"
            + "\n"
            + "PUB main() | a, b\n"
            + "    a := b ? _ptr():1 : 0\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       12 00 00 00    End\n"
            + "' PUB main() | a, b\n"
            + "00008 00008       08             (stack size)\n"
            + "'     a := b ? _ptr():1 : 0\n"
            + "00009 00009       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0000A 0000A       01             ANCHOR\n"
            + "0000B 0000B       C1 80          VAR_READ LONG VBASE+$00001 (short)\n"
            + "0000D 0000D       0B             CALL_PTR\n"
            + "0000E 0000E       A1             CONSTANT (0)\n"
            + "0000F 0000F       6B             TERNARY_IF_ELSE\n"
            + "00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00011 00011       04             RETURN\n"
            + "00012 00012       00 00          Padding\n"
            + "", compile(text, false));
    }

    @Test
    void testMethodPointersArray() throws Exception {
        String text = ""
            + "VAR"
            + "    long _ptr1"
            + "\n"
            + "PUB method(x, y) | _ptr2\n"
            + "    _ptr1[0](x, y)\n"
            + "    _ptr2[1](x, y)\n"
            + "    _ptr3[2](x, y)\n"
            + "\n"
            + "DAT\n"
            + "_ptr3           long  0\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 82    Method method @ $0000C (2 parameters, 0 returns)\n"
            + "00004 00004       23 00 00 00    End\n"
            + "00008 00008 00000 00 00 00 00    _ptr3               long    0\n"
            + "' PUB method(x, y) | _ptr2\n"
            + "0000C 0000C       04             (stack size)\n"
            + "'     _ptr1[0](x, y)\n"
            + "0000D 0000D       00             ANCHOR\n"
            + "0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00010 00010       5D 04 80       VAR_READ LONG VBASE+$00001 (short)\n"
            + "00013 00013       0B             CALL_PTR\n"
            + "'     _ptr2[1](x, y)\n"
            + "00014 00014       00             ANCHOR\n"
            + "00015 00015       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00016 00016       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00017 00017       5E 0C 80       VAR_READ LONG DBASE+$00002 (short)\n"
            + "0001A 0001A       0B             CALL_PTR\n"
            + "'     _ptr3[2](x, y)\n"
            + "0001B 0001B       00             ANCHOR\n"
            + "0001C 0001C       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0001D 0001D       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0001E 0001E       5C 10 80       MEM_READ LONG PBASE+$00010\n"
            + "00021 00021       0B             CALL_PTR\n"
            + "00022 00022       04             RETURN\n"
            + "00023 00023       00             Padding\n"
            + "", compile(text, false));
    }

    @Test
    void testMethodPointersArrayTrap() throws Exception {
        String text = ""
            + "VAR"
            + "    long _ptr1"
            + "\n"
            + "PUB method(x, y) | _ptr2, i\n"
            + "    \\_ptr1[i](x, y)\n"
            + "    \\_ptr2[i](x, y)\n"
            + "    \\_ptr3[i](x, y)\n"
            + "\n"
            + "DAT\n"
            + "_ptr3           long  0\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00 00 82    Method method @ $0000C (2 parameters, 0 returns)\n"
            + "00004 00004       26 00 00 00    End\n"
            + "00008 00008 00000 00 00 00 00    _ptr3               long    0\n"
            + "' PUB method(x, y) | _ptr2, i\n"
            + "0000C 0000C       08             (stack size)\n"
            + "'     \\_ptr1[i](x, y)\n"
            + "0000D 0000D       02             ANCHOR_TRAP\n"
            + "0000E 0000E       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00010 00010       E3             VAR_READ LONG DBASE+$00003 (short)\n"
            + "00011 00011       60 04 80       VAR_READ_INDEXED LONG VBASE+$00004\n"
            + "00014 00014       0B             CALL_PTR\n"
            + "'     \\_ptr2[i](x, y)\n"
            + "00015 00015       02             ANCHOR_TRAP\n"
            + "00016 00016       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00017 00017       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00018 00018       E3             VAR_READ LONG DBASE+$00003 (short)\n"
            + "00019 00019       61 08 80       VAR_READ_INDEXED LONG DBASE+$00008\n"
            + "0001C 0001C       0B             CALL_PTR\n"
            + "'     \\_ptr3[i](x, y)\n"
            + "0001D 0001D       02             ANCHOR_TRAP\n"
            + "0001E 0001E       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0001F 0001F       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00020 00020       E3             VAR_READ LONG DBASE+$00003 (short)\n"
            + "00021 00021       5F 08 80       MEM_READ LONG INDEXED PBASE+$00008\n"
            + "00024 00024       0B             CALL_PTR\n"
            + "00025 00025       04             RETURN\n"
            + "00026 00026       00 00          Padding\n"
            + "", compile(text, false));
    }

    @Test
    void testMethodPointerAsArgument() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    set(@method)\n"
            + "\n"
            + "PUB set(ptr) | a\n"
            + "\n"
            + "    a := ptr\n"
            + "\n"
            + "PUB method(x, y)\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)\n"
            + "00004 00004       17 00 00 81    Method set @ $00017 (1 parameters, 0 returns)\n"
            + "00008 00008       1B 00 00 82    Method method @ $0001B (2 parameters, 0 returns)\n"
            + "0000C 0000C       1D 00 00 00    End\n"
            + "' PUB main()\n"
            + "00010 00010       00             (stack size)\n"
            + "'     set(@method)\n"
            + "00011 00011       00             ANCHOR\n"
            + "00012 00012       11 02          SUB_ADDRESS (2)\n"
            + "00014 00014       0A 01          CALL_SUB (1)\n"
            + "00016 00016       04             RETURN\n"
            + "' PUB set(ptr) | a\n"
            + "00017 00017       04             (stack size)\n"
            + "'     a := ptr\n"
            + "00018 00018       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00019 00019       F1             VAR_WRITE LONG DBASE+$00001 (short)\n"
            + "0001A 0001A       04             RETURN\n"
            + "' PUB method(x, y)\n"
            + "0001B 0001B       00             (stack size)\n"
            + "0001C 0001C       04             RETURN\n"
            + "0001D 0001D       00 00 00       Padding\n"
            + "", compile(text, false));
    }

    @Test
    void testTypedAddress() throws Exception {
        String text = ""
            + "PUB main() | a, b, c\n"
            + "\n"
            + "    a := @word[b]\n"
            + "    a := @word[b][c]\n"
            + "    a := @@word[b]\n"
            + "    a := @@word[b][c]\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       1E 00 00 00    End\n"
            + "' PUB main() | a, b, c\n"
            + "00008 00008       0C             (stack size)\n"
            + "'     a := @word[b]\n"
            + "00009 00009       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0000A 0000A       66 7F          MEM_ADDRESS\n"
            + "0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := @word[b][c]\n"
            + "0000D 0000D       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0000E 0000E       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "0000F 0000F       63 7F          MEM_ADDRESS INDEXED\n"
            + "00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := @@word[b]\n"
            + "00012 00012       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00013 00013       66 80          MEM_READ WORD\n"
            + "00015 00015       24             ADD_PBASE\n"
            + "00016 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     a := @@word[b][c]\n"
            + "00017 00017       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00018 00018       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00019 00019       63 80          MEM_READ WORD INDEXED\n"
            + "0001B 0001B       24             ADD_PBASE\n"
            + "0001C 0001C       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001D 0001D       04             RETURN\n"
            + "0001E 0001E       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testTypedAddressMethodCall() throws Exception {
        String text = ""
            + "PUB main() | a, b, c, d, e\n"
            + "\n"
            + "    long[a](c, d, e)\n"
            + "    long[a][b](c, d, e)\n"
            + "    \\long[a](c, d, e)\n"
            + "    \\long[a][b](c, d, e)\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       2C 00 00 00    End\n"
            + "' PUB main() | a, b, c, d, e\n"
            + "00008 00008       14             (stack size)\n"
            + "'     long[a](c, d, e)\n"
            + "00009 00009       00             ANCHOR\n"
            + "0000A 0000A       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "0000B 0000B       E3             VAR_READ LONG DBASE+$00003 (short)\n"
            + "0000C 0000C       E4             VAR_READ LONG DBASE+$00004 (short)\n"
            + "0000D 0000D       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000E 0000E       67 80          MEM_READ LONG\n"
            + "00010 00010       0B             CALL_PTR\n"
            + "'     long[a][b](c, d, e)\n"
            + "00011 00011       00             ANCHOR\n"
            + "00012 00012       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00013 00013       E3             VAR_READ LONG DBASE+$00003 (short)\n"
            + "00014 00014       E4             VAR_READ LONG DBASE+$00004 (short)\n"
            + "00015 00015       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00016 00016       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00017 00017       64 80          MEM_READ LONG INDEXED\n"
            + "00019 00019       0B             CALL_PTR\n"
            + "'     \\long[a](c, d, e)\n"
            + "0001A 0001A       02             ANCHOR_TRAP\n"
            + "0001B 0001B       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "0001C 0001C       E3             VAR_READ LONG DBASE+$00003 (short)\n"
            + "0001D 0001D       E4             VAR_READ LONG DBASE+$00004 (short)\n"
            + "0001E 0001E       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0001F 0001F       67 80          MEM_READ LONG\n"
            + "00021 00021       0B             CALL_PTR\n"
            + "'     \\long[a][b](c, d, e)\n"
            + "00022 00022       02             ANCHOR_TRAP\n"
            + "00023 00023       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00024 00024       E3             VAR_READ LONG DBASE+$00003 (short)\n"
            + "00025 00025       E4             VAR_READ LONG DBASE+$00004 (short)\n"
            + "00026 00026       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00027 00027       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00028 00028       64 80          MEM_READ LONG INDEXED\n"
            + "0002A 0002A       0B             CALL_PTR\n"
            + "0002B 0002B       04             RETURN\n"
            + "", compile(text));
    }

    String compile(String text) throws Exception {
        return compile(text, false);
    }

    String compile(String text, boolean debugEnabled) throws Exception {
        Spin2Context scope = new Spin2GlobalContext();
        Map<String, ObjectInfo> childObjects = new HashMap<String, ObjectInfo>();
        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser subject = new Spin2Parser(stream);
        Node root = subject.parse();

        Spin2ObjectCompiler compiler = new Spin2ObjectCompiler(scope, childObjects, debugEnabled);
        Spin2Object obj = compiler.compileObject(root);
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
