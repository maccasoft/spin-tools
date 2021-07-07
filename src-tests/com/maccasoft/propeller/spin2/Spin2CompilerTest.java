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
import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.Node;

class Spin2CompilerTest {

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

        Spin2Compiler compiler = new Spin2Compiler();
        Spin2Object obj = compiler.compile(root);

        Assertions.assertEquals(0x000L, compiler.source.get(0).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x000L, compiler.source.get(1).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x000L, compiler.source.get(2).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x002L, compiler.source.get(3).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x004L, compiler.source.get(4).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x006L, compiler.source.get(5).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x007L, compiler.source.get(6).getScope().getSymbol("$").getNumber());

        obj.generateBinary(new ByteArrayOutputStream());
    }

    @Test
    void testCompilePtr() {
        Spin2Compiler subject = new Spin2Compiler();

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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     0A 00 00 00    | End\n"
            + "' PUB main()\n"
            + "00008     00             | (stack size)\n"
            + "00009     04             | RETURN\n"
            + "0000A     00 00          | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     0C 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     a := 1\n"
            + "00009     A2             | CONSTANT (1)\n"
            + "0000A     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000B     04             | RETURN\n"
            + "", compile(text));
    }

    @Test
    void testGlobalVarAssignment() throws Exception {
        String text = ""
            + "VAR a\n"
            + "\n"
            + "PUB main()\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     0D 00 00 00    | End\n"
            + "' PUB main()\n"
            + "00008     00             | (stack size)\n"
            + "'     a := 1\n"
            + "00009     A2             | CONSTANT (1)\n"
            + "0000A     C1 81          | VAR_WRITE LONG VBASE+$00001 (short)\n"
            + "0000C     04             | RETURN\n"
            + "0000D     00 00 00       | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     10 00 00 00    | End\n"
            + "' PUB main() | a, b\n"
            + "00008     08             | (stack size)\n"
            + "'     a := 1 + b * 3\n"
            + "00009     A2             | CONSTANT (1)\n"
            + "0000A     E1             | VAR_READ LONG DBASE+$00001 (short)\n"
            + "0000B     A4             | CONSTANT (3)\n"
            + "0000C     96             | MULTIPLY\n"
            + "0000D     8A             | ADD\n"
            + "0000E     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000F     04             | RETURN\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     0C 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     a := 1 + 2 * 3\n"
            + "00009     A8             | CONSTANT (1 + 2 * 3)\n"
            + "0000A     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000B     04             | RETURN\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     0D 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     a := \"1\"\n"
            + "00009     45 31          | CONSTANT (\"1\")\n"
            + "0000B     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000C     04             | RETURN\n"
            + "0000D     00 00 00       | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     11 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     if a == 0\n"
            + "00009     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A     A1             | CONSTANT (0)\n"
            + "0000B     70             | EQUAL\n"
            + "0000C     13 03          | JZ $00010 (3)\n"
            + "'         a := 1\n"
            + "0000E     A2             | CONSTANT (1)\n"
            + "0000F     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00010     04             | RETURN\n"
            + "00011     00 00 00       | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     15 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     if a == 0\n"
            + "00009     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A     A1             | CONSTANT (0)\n"
            + "0000B     70             | EQUAL\n"
            + "0000C     13 05          | JZ $00012 (5)\n"
            + "'         a := 1\n"
            + "0000E     A2             | CONSTANT (1)\n"
            + "0000F     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00010     12 03          | JMP $00014 (3)\n"
            + "'     else\n"
            + "'         a := 2\n"
            + "00012     A3             | CONSTANT (2)\n"
            + "00013     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00014     04             | RETURN\n"
            + "00015     00 00 00       | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     23 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     if a == 0\n"
            + "00009     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A     A1             | CONSTANT (0)\n"
            + "0000B     70             | EQUAL\n"
            + "0000C     13 05          | JZ $00012 (5)\n"
            + "'         a := 1\n"
            + "0000E     A2             | CONSTANT (1)\n"
            + "0000F     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00010     12 11          | JMP $00022 (17)\n"
            + "'     elseif a == 1\n"
            + "00012     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "00013     A2             | CONSTANT (1)\n"
            + "00014     70             | EQUAL\n"
            + "00015     13 05          | JZ $0001B (5)\n"
            + "'         a := 2\n"
            + "00017     A3             | CONSTANT (2)\n"
            + "00018     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00019     12 08          | JMP $00022 (8)\n"
            + "'     elseif a == 2\n"
            + "0001B     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "0001C     A3             | CONSTANT (2)\n"
            + "0001D     70             | EQUAL\n"
            + "0001E     13 03          | JZ $00022 (3)\n"
            + "'         a := 3\n"
            + "00020     A4             | CONSTANT (3)\n"
            + "00021     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00022     04             | RETURN\n"
            + "00023     00             | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     27 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     if a == 0\n"
            + "00009     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A     A1             | CONSTANT (0)\n"
            + "0000B     70             | EQUAL\n"
            + "0000C     13 05          | JZ $00012 (5)\n"
            + "'         a := 1\n"
            + "0000E     A2             | CONSTANT (1)\n"
            + "0000F     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00010     12 15          | JMP $00026 (21)\n"
            + "'     elseif a == 1\n"
            + "00012     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "00013     A2             | CONSTANT (1)\n"
            + "00014     70             | EQUAL\n"
            + "00015     13 05          | JZ $0001B (5)\n"
            + "'         a := 2\n"
            + "00017     A3             | CONSTANT (2)\n"
            + "00018     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00019     12 0C          | JMP $00026 (12)\n"
            + "'     elseif a == 2\n"
            + "0001B     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "0001C     A3             | CONSTANT (2)\n"
            + "0001D     70             | EQUAL\n"
            + "0001E     13 05          | JZ $00024 (5)\n"
            + "'         a := 3\n"
            + "00020     A4             | CONSTANT (3)\n"
            + "00021     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00022     12 03          | JMP $00026 (3)\n"
            + "'     else\n"
            + "'         a := 4\n"
            + "00024     A5             | CONSTANT (4)\n"
            + "00025     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00026     04             | RETURN\n"
            + "00027     00             | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     0E 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     repeat\n"
            + "'         a := 1\n"
            + "00009     A2             | CONSTANT (1)\n"
            + "0000A     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000B     12 7D          | JMP $00009 (-3)\n"
            + "0000D     04             | RETURN\n"
            + "0000E     00 00          | Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatCounter() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat 10\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     0F 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     repeat 10\n"
            + "00009     AB             | CONSTANT (10)\n"
            + "'         a := 1\n"
            + "0000A     A2             | CONSTANT (1)\n"
            + "0000B     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000C     16 7D          | DJNZ $0000A (-3)\n"
            + "0000E     04             | RETURN\n"
            + "0000F     00             | Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatVarCounter() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat a\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     0F 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     repeat a\n"
            + "00009     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "'         a := 1\n"
            + "0000A     A2             | CONSTANT (1)\n"
            + "0000B     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000C     16 7D          | DJNZ $0000A (-3)\n"
            + "0000E     04             | RETURN\n"
            + "0000F     00             | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     15 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     repeat\n"
            + "'         if a == 1\n"
            + "00009     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A     A2             | CONSTANT (1)\n"
            + "0000B     70             | EQUAL\n"
            + "0000C     13 03          | JZ $00010 (3)\n"
            + "'             quit\n"
            + "0000E     12 05          | JMP $00014 (5)\n"
            + "'         a := 1\n"
            + "00010     A2             | CONSTANT (1)\n"
            + "00011     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00012     12 76          | JMP $00009 (-10)\n"
            + "00014     04             | RETURN\n"
            + "00015     00 00 00       | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     15 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     repeat\n"
            + "'         if a == 1\n"
            + "00009     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A     A2             | CONSTANT (1)\n"
            + "0000B     70             | EQUAL\n"
            + "0000C     13 03          | JZ $00010 (3)\n"
            + "'             next\n"
            + "0000E     12 7A          | JMP $00009 (-6)\n"
            + "'         a := 1\n"
            + "00010     A2             | CONSTANT (1)\n"
            + "00011     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00012     12 76          | JMP $00009 (-10)\n"
            + "00014     04             | RETURN\n"
            + "00015     00 00 00       | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     13 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     repeat while a < 1\n"
            + "00009     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A     A2             | CONSTANT (1)\n"
            + "0000B     6C             | LESS_THAN\n"
            + "0000C     13 05          | JZ $00012 (5)\n"
            + "'         a := 1\n"
            + "0000E     A2             | CONSTANT (1)\n"
            + "0000F     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00010     12 78          | JMP $00009 (-8)\n"
            + "00012     04             | RETURN\n"
            + "00013     00             | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     13 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     repeat until a < 1\n"
            + "00009     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A     A2             | CONSTANT (1)\n"
            + "0000B     6C             | LESS_THAN\n"
            + "0000C     14 05          | JNZ $00012 (5)\n"
            + "'         a := 1\n"
            + "0000E     A2             | CONSTANT (1)\n"
            + "0000F     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00010     12 78          | JMP $00009 (-8)\n"
            + "00012     04             | RETURN\n"
            + "00013     00             | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     11 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     repeat\n"
            + "'         a := 1\n"
            + "00009     A2             | CONSTANT (1)\n"
            + "0000A     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     while a < 1\n"
            + "0000B     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C     A2             | CONSTANT (1)\n"
            + "0000D     6C             | LESS_THAN\n"
            + "0000E     14 7A          | JNZ $00009 (-6)\n"
            + "00010     04             | RETURN\n"
            + "00011     00 00 00       | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     11 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     repeat\n"
            + "'         a := 1\n"
            + "00009     A2             | CONSTANT (1)\n"
            + "0000A     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     until a < 1\n"
            + "0000B     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C     A2             | CONSTANT (1)\n"
            + "0000D     6C             | LESS_THAN\n"
            + "0000E     13 7A          | JZ $00009 (-6)\n"
            + "00010     04             | RETURN\n"
            + "00011     00 00 00       | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     18 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     repeat\n"
            + "'         if a == 1\n"
            + "00009     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A     A2             | CONSTANT (1)\n"
            + "0000B     70             | EQUAL\n"
            + "0000C     13 03          | JZ $00010 (3)\n"
            + "'             quit\n"
            + "0000E     12 08          | JMP $00017 (8)\n"
            + "'         a := 1\n"
            + "00010     A2             | CONSTANT (1)\n"
            + "00011     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     while a < 1\n"
            + "00012     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "00013     A2             | CONSTANT (1)\n"
            + "00014     6C             | LESS_THAN\n"
            + "00015     14 73          | JNZ $00009 (-13)\n"
            + "00017     04             | RETURN\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     18 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     repeat\n"
            + "'         if a == 1\n"
            + "00009     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A     A2             | CONSTANT (1)\n"
            + "0000B     70             | EQUAL\n"
            + "0000C     13 03          | JZ $00010 (3)\n"
            + "'             next\n"
            + "0000E     12 03          | JMP $00012 (3)\n"
            + "'         a := 1\n"
            + "00010     A2             | CONSTANT (1)\n"
            + "00011     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     until a < 1\n"
            + "00012     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "00013     A2             | CONSTANT (1)\n"
            + "00014     6C             | LESS_THAN\n"
            + "00015     13 73          | JZ $00009 (-13)\n"
            + "00017     04             | RETURN\n"
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
            + "00000     0C 00 00 80    | Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004     11 00 00 80    | Method function @ $00011 (0 parameters, 0 returns)\n"
            + "00008     13 00 00 00    | End\n"
            + "' PUB main()\n"
            + "0000C     00             | (stack size)\n"
            + "'     function()\n"
            + "0000D     00             | RETURNS_COUNT (0)\n"
            + "0000E     0A 01          | CALL_SUB (1)\n"
            + "00010     04             | RETURN\n"
            + "' PUB function()\n"
            + "00011     00             | (stack size)\n"
            + "00012     04             | RETURN\n"
            + "00013     00             | Padding\n"
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
            + "00000     0C 00 00 80    | Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004     12 00 00 81    | Method function @ $00012 (1 parameters, 0 returns)\n"
            + "00008     14 00 00 00    | End\n"
            + "' PUB main()\n"
            + "0000C     00             | (stack size)\n"
            + "'     function(1)\n"
            + "0000D     00             | RETURNS_COUNT (0)\n"
            + "0000E     A2             | CONSTANT (1)\n"
            + "0000F     0A 01          | CALL_SUB (1)\n"
            + "00011     04             | RETURN\n"
            + "' PUB function(a)\n"
            + "00012     00             | (stack size)\n"
            + "00013     04             | RETURN\n"
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
            + "00000     0C 00 00 80    | Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004     13 00 00 81    | Method function @ $00013 (1 parameters, 0 returns)\n"
            + "00008     15 00 00 00    | End\n"
            + "' PUB main()\n"
            + "0000C     00             | (stack size)\n"
            + "'     function(\"1\")\n"
            + "0000D     00             | RETURNS_COUNT (0)\n"
            + "0000E     45 31          | CONSTANT (\"1\")\n"
            + "00010     0A 01          | CALL_SUB (1)\n"
            + "00012     04             | RETURN\n"
            + "' PUB function(a)\n"
            + "00013     00             | (stack size)\n"
            + "00014     04             | RETURN\n"
            + "00015     00 00 00       | Padding\n"
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
            + "00000     0C 00 00 80    | Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004     18 00 00 81    | Method function @ $00018 (1 parameters, 0 returns)\n"
            + "00008     1A 00 00 00    | End\n"
            + "' PUB main()\n"
            + "0000C     00             | (stack size)\n"
            + "'     function(string(\"1234\"))\n"
            + "0000D     00             | RETURNS_COUNT (0)\n"
            + "0000E     9E 05 31 32 33 | STRING\n"
            + "00013     34 00          |\n"
            + "00015     0A 01          | CALL_SUB (1)\n"
            + "00017     04             | RETURN\n"
            + "' PUB function(a)\n"
            + "00018     00             | (stack size)\n"
            + "00019     04             | RETURN\n"
            + "0001A     00 00          | Padding\n"
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
            + "00000     0C 00 00 80    | Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004     18 00 00 81    | Method function @ $00018 (1 parameters, 0 returns)\n"
            + "00008     1A 00 00 00    | End\n"
            + "' PUB main()\n"
            + "0000C     00             | (stack size)\n"
            + "'     function(\"1234\")\n"
            + "0000D     00             | RETURNS_COUNT (0)\n"
            + "0000E     9E 05 31 32 33 | STRING\n"
            + "00013     34 00          |\n"
            + "00015     0A 01          | CALL_SUB (1)\n"
            + "00017     04             | RETURN\n"
            + "' PUB function(a)\n"
            + "00018     00             | (stack size)\n"
            + "00019     04             | RETURN\n"
            + "0001A     00 00          | Padding\n"
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
            + "00000     0C 00 00 80    | Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004     1A 00 00 81    | Method function @ $0001A (1 parameters, 0 returns)\n"
            + "00008     1C 00 00 00    | End\n"
            + "' PUB main()\n"
            + "0000C     00             | (stack size)\n"
            + "'     function(string(\"1234\", 13, 10))\n"
            + "0000D     00             | RETURNS_COUNT (0)\n"
            + "0000E     9E 07 31 32 33 | STRING\n"
            + "00013     34 0D 0A 00    |\n"
            + "00017     0A 01          | CALL_SUB (1)\n"
            + "00019     04             | RETURN\n"
            + "' PUB function(a)\n"
            + "0001A     00             | (stack size)\n"
            + "0001B     04             | RETURN\n"
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
            + "00000     10 00 00 80    | Method main @ $00010 (0 parameters, 0 returns)\n"
            + "00004     12 00 00 80    | Method function2 @ $00012 (0 parameters, 0 returns)\n"
            + "00008     14 00 00 80    | Method function1 @ $00014 (0 parameters, 0 returns)\n"
            + "0000C     16 00 00 00    | End\n"
            + "' PUB main()\n"
            + "00010     00             | (stack size)\n"
            + "00011     04             | RETURN\n"
            + "' PUB function2()\n"
            + "00012     00             | (stack size)\n"
            + "00013     04             | RETURN\n"
            + "' PRI function1()\n"
            + "00014     00             | (stack size)\n"
            + "00015     04             | RETURN\n"
            + "00016     00 00          | Padding\n"
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
            + "00000     10 00 00 80    | Method main @ $00010 (0 parameters, 0 returns)\n"
            + "00004     15 00 00 80    | Method function2 @ $00015 (0 parameters, 0 returns)\n"
            + "00008     17 00 00 80    | Method function1 @ $00017 (0 parameters, 0 returns)\n"
            + "0000C     19 00 00 00    | End\n"
            + "' PUB main()\n"
            + "00010     00             | (stack size)\n"
            + "'     function1()\n"
            + "00011     00             | RETURNS_COUNT (0)\n"
            + "00012     0A 02          | CALL_SUB (2)\n"
            + "00014     04             | RETURN\n"
            + "' PUB function2()\n"
            + "00015     00             | (stack size)\n"
            + "00016     04             | RETURN\n"
            + "' PRI function1()\n"
            + "00017     00             | (stack size)\n"
            + "00018     04             | RETURN\n"
            + "00019     00 00 00       | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     0B 00 00 00    | End\n"
            + "' PUB main()\n"
            + "00008     00             | (stack size)\n"
            + "'     abort\n"
            + "00009     06             | ABORT\n"
            + "0000A     04             | RETURN\n"
            + "0000B     00             | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     0C 00 00 00    | End\n"
            + "' PUB main()\n"
            + "00008     00             | (stack size)\n"
            + "'     abort 1\n"
            + "00009     A2             | CONSTANT (1)\n"
            + "0000A     07             | ABORT\n"
            + "0000B     04             | RETURN\n"
            + "", compile(text));
    }

    @Test
    void testDataPointer() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "        a := @value\n"
            + "\n"
            + "DAT             org     $000\n"
            + "\n"
            + "value           long    1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "00000     0C 00 00 80    | Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004     12 00 00 00    | End\n"
            + "00008 000                |                     org     000\n"
            + "00008 000 01 00 00 00    | value               long    1\n"
            + "' PUB main() | a\n"
            + "0000C     04             | (stack size)\n"
            + "'         a := @value\n"
            + "0000D     5C 08 7F       | MEM_ADDRESS PBASE+$00008\n"
            + "00010     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00011     04             | RETURN\n"
            + "00012     00 00          | Padding\n"
            + "", compile(text));
    }

    @Test
    void testDataValue() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "        a := value\n"
            + "\n"
            + "DAT             org     $000\n"
            + "\n"
            + "value           long    1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "00000     0C 00 00 80    | Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004     12 00 00 00    | End\n"
            + "00008 000                |                     org     000\n"
            + "00008 000 01 00 00 00    | value               long    1\n"
            + "' PUB main() | a\n"
            + "0000C     04             | (stack size)\n"
            + "'         a := value\n"
            + "0000D     5C 08 80       | MEM_READ PBASE+$00008\n"
            + "00010     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00011     04             | RETURN\n"
            + "00012     00 00          | Padding\n"
            + "", compile(text));
    }

    @Test
    void testVarPointer() throws Exception {
        String text = ""
            + "VAR b[10]\n"
            + "\n"
            + "PUB main() | a\n"
            + "\n"
            + "        a := @b\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     0E 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'         a := @b\n"
            + "00009     5D 04 7F       | MEM_ADDRESS VBASE+$00004\n"
            + "0000C     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000D     04             | RETURN\n"
            + "0000E     00 00          | Padding\n"
            + "", compile(text));
    }

    @Test
    void testLocalVarPointer() throws Exception {
        String text = ""
            + "PUB main() | a, b[10]\n"
            + "\n"
            + "        a := @b\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     0E 00 00 00    | End\n"
            + "' PUB main() | a, b[10]\n"
            + "00008     2C             | (stack size)\n"
            + "'         a := @b\n"
            + "00009     5E 04 7F       | MEM_ADDRESS DBASE+$00004\n"
            + "0000C     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000D     04             | RETURN\n"
            + "0000E     00 00          | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     10 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'         a := muldiv64(1, 2, 3)\n"
            + "00009     A2             | CONSTANT (1)\n"
            + "0000A     A3             | CONSTANT (2)\n"
            + "0000B     A4             | CONSTANT (3)\n"
            + "0000C     19 80          | MULDIV64\n"
            + "0000E     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000F     04             | RETURN\n"
            + "", compile(text));
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     0A 00 00 00    | End\n"
            + "' PUB main()\n"
            + "00008     00             | (stack size)\n"
            + "'     debug()\n"
            + "00009     04             | RETURN\n"
            + "0000A     00 00          | Padding\n"
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
            + "        other: a := 7\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     22 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     case a\n"
            + "00009     45 21          | CONSTANT ($00021)\n"
            + "0000B     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000C     A2             | CONSTANT (1)\n"
            + "0000D     1C 0A          | CASE_JMP $00018 (10)\n"
            + "0000F     A3             | CONSTANT (2)\n"
            + "00010     1C 0A          | CASE_JMP $0001B (10)\n"
            + "00012     A4             | CONSTANT (3)\n"
            + "00013     1C 0A          | CASE_JMP $0001E (10)\n"
            + "'         other: a := 7\n"
            + "00015     A8             | CONSTANT (7)\n"
            + "00016     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00017     1E             | CASE_DONE\n"
            + "'         1: a := 4\n"
            + "00018     A5             | CONSTANT (4)\n"
            + "00019     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001A     1E             | CASE_DONE\n"
            + "'         2: a := 5\n"
            + "0001B     A6             | CONSTANT (5)\n"
            + "0001C     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001D     1E             | CASE_DONE\n"
            + "'         3: a := 6\n"
            + "0001E     A7             | CONSTANT (6)\n"
            + "0001F     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00020     1E             | CASE_DONE\n"
            + "00021     04             | RETURN\n"
            + "00022     00 00          | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     0C 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     SEND(a)\n"
            + "00009     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A     0D             | SEND\n"
            + "0000B     04             | RETURN\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     0F 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     SEND(1,2,3)\n"
            + "00009     0E 03 01 02 03 | SEND\n"
            + "0000E     04             | RETURN\n"
            + "0000F     00             | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     10 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     SEND(a,2,3)\n"
            + "00009     E0             | VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A     0D             | SEND\n"
            + "0000B     A3             | CONSTANT (2)\n"
            + "0000C     0D             | SEND\n"
            + "0000D     A4             | CONSTANT (3)\n"
            + "0000E     0D             | SEND\n"
            + "0000F     04             | RETURN\n"
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
            + "00000     0C 00 00 80    | Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004     13 00 00 80    | Method out @ $00013 (0 parameters, 0 returns)\n"
            + "00008     15 00 00 00    | End\n"
            + "' PUB main()\n"
            + "0000C     00             | (stack size)\n"
            + "'     SEND := @out\n"
            + "0000D     11 01          | SUB (1)\n"
            + "0000F     4E 53 81       | REG_WRITE +$1D3\n"
            + "00012     04             | RETURN\n"
            + "' PRI out()\n"
            + "00013     00             | (stack size)\n"
            + "00014     04             | RETURN\n"
            + "00015     00 00 00       | Padding\n"
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
            + "00000     08 00 00 80    | Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004     0C 00 00 00    | End\n"
            + "' PUB main() | a\n"
            + "00008     04             | (stack size)\n"
            + "'     a := RECV()\n"
            + "00009     0C             | RECV\n"
            + "0000A     F0             | VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000B     04             | RETURN\n"
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
            + "00000     0C 00 00 80    | Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004     13 00 00 80    | Method in @ $00013 (0 parameters, 0 returns)\n"
            + "00008     15 00 00 00    | End\n"
            + "' PUB main()\n"
            + "0000C     00             | (stack size)\n"
            + "'     RECV := @in\n"
            + "0000D     11 01          | SUB (1)\n"
            + "0000F     4E 52 81       | REG_WRITE +$1D2\n"
            + "00012     04             | RETURN\n"
            + "' PRI in()\n"
            + "00013     00             | (stack size)\n"
            + "00014     04             | RETURN\n"
            + "00015     00 00 00       | Padding\n"
            + "", compile(text));
    }

    String compile(String text) throws Exception {
        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser subject = new Spin2Parser(stream);
        Node root = subject.parse();

        Spin2Compiler compiler = new Spin2Compiler();
        Spin2Object obj = compiler.compile(root);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateListing(new PrintStream(os));

        return os.toString();
    }

}
