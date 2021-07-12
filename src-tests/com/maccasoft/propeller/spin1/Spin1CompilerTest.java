/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;

class Spin1CompilerTest {

    @Test
    void testEmptyMethod() throws Exception {
        String text = ""
            + "PUB main\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 00 00    Function main @ $0008 (local size 0)\n"
            + "' PUB main\n"
            + "00008 00008       32             RETURN\n"
            + "00009 00009       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testLocalVarAssignment() throws Exception {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main | a\n"
            + "'     a := 1\n"
            + "00008 00008       36             CONSTANT (1)\n"
            + "00009 00009       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0000A 0000A       32             RETURN\n"
            + "0000B 0000B       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testGlobalVarAssignment() throws Exception {
        String text = ""
            + "VAR a\n"
            + "\n"
            + "PUB main\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 00 00    Function main @ $0008 (local size 0)\n"
            + "' PUB main\n"
            + "'     a := 1\n"
            + "00008 00008       36             CONSTANT (1)\n"
            + "00009 00009       41             VAR_WRITE LONG VBASE+$0000 (short)\n"
            + "0000A 0000A       32             RETURN\n"
            + "0000B 0000B       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testExpressionAssignment() throws Exception {
        String text = ""
            + "PUB main | a, b\n"
            + "\n"
            + "    a := 1 + b * 3\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       10 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 08 00    Function main @ $0008 (local size 8)\n"
            + "' PUB main | a, b\n"
            + "'     a := 1 + b * 3\n"
            + "00008 00008       36             CONSTANT (1)\n"
            + "00009 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0000A 0000A       38 03          CONSTANT (3)\n"
            + "0000C 0000C       F4             MULTIPLY\n"
            + "0000D 0000D       EC             ADD\n"
            + "0000E 0000E       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0000F 0000F       32             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testIfConditional() throws Exception {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    if a == 1\n"
            + "        a := 2\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main | a\n"
            + "'     if a == 1\n"
            + "00008 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00009 00009       36             CONSTANT (1)\n"
            + "0000A 0000A       FC             TEST_EQUAL\n"
            + "0000B 0000B       0A 03          JZ $00010 (3)\n"
            + "'         a := 2\n"
            + "0000D 0000D       38 02          CONSTANT (2)\n"
            + "0000F 0000F       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00010 00010       32             RETURN\n"
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
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 08 00    Function main @ $0008 (local size 8)\n"
            + "' PUB main() | a, b\n"
            + "'     if (a := b) == 0\n"
            + "00008 00008       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "00009 00009       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0000A 0000A       35             CONSTANT (0)\n"
            + "0000B 0000B       FC             TEST_EQUAL\n"
            + "0000C 0000C       0A 02          JZ $00010 (2)\n"
            + "'         a := 1\n"
            + "0000E 0000E       36             CONSTANT (1)\n"
            + "0000F 0000F       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00010 00010       32             RETURN\n"
            + "00011 00011       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeat() throws Exception {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    repeat\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       10 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main | a\n"
            + "'     repeat\n"
            + "'         a := 1\n"
            + "00008 00008       36             CONSTANT (1)\n"
            + "00009 00009       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0000A 0000A       04 7C          JMP $00008 (-4)\n"
            + "0000C 0000C       32             RETURN\n"
            + "0000D 0000D       00 00 00       Padding\n"
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
            + "00000 00000       10 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main() | a\n"
            + "'     repeat 10\n"
            + "00008 00008       38 0A          CONSTANT (10)\n"
            + "'         a := 1\n"
            + "0000A 0000A       36             CONSTANT (1)\n"
            + "0000B 0000B       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0000C 0000C       09 7C          DJNZ $0000A (-4)\n"
            + "0000E 0000E       32             RETURN\n"
            + "0000F 0000F       00             Padding\n"
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
            + "' Object header\n"
            + "00000 00000       10 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main() | a\n"
            + "'     repeat a\n"
            + "00008 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00009 00009       08 04          TJZ $0000F (4)\n"
            + "'         a := 1\n"
            + "0000B 0000B       36             CONSTANT (1)\n"
            + "0000C 0000C       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0000D 0000D       09 7C          DJNZ $0000B (-4)\n"
            + "0000F 0000F       32             RETURN\n"
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
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main() | a\n"
            + "'     repeat\n"
            + "'         if a == 1\n"
            + "00008 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00009 00009       36             CONSTANT (1)\n"
            + "0000A 0000A       FC             TEST_EQUAL\n"
            + "0000B 0000B       0A 02          JZ $0000F (2)\n"
            + "'             quit\n"
            + "0000D 0000D       04 04          JMP $00013 (4)\n"
            + "'         a := 1\n"
            + "0000F 0000F       36             CONSTANT (1)\n"
            + "00010 00010       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00011 00011       04 75          JMP $00008 (-11)\n"
            + "00013 00013       32             RETURN\n"
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
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main() | a\n"
            + "'     repeat\n"
            + "'         if a == 1\n"
            + "00008 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00009 00009       36             CONSTANT (1)\n"
            + "0000A 0000A       FC             TEST_EQUAL\n"
            + "0000B 0000B       0A 02          JZ $0000F (2)\n"
            + "'             next\n"
            + "0000D 0000D       04 79          JMP $00008 (-7)\n"
            + "'         a := 1\n"
            + "0000F 0000F       36             CONSTANT (1)\n"
            + "00010 00010       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00011 00011       04 75          JMP $00008 (-11)\n"
            + "00013 00013       32             RETURN\n"
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
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main() | a\n"
            + "'     repeat while a < 1\n"
            + "00008 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00009 00009       36             CONSTANT (1)\n"
            + "0000A 0000A       F9             TEST_BELOW\n"
            + "0000B 0000B       0A 04          JZ $00011 (4)\n"
            + "'         a := 1\n"
            + "0000D 0000D       36             CONSTANT (1)\n"
            + "0000E 0000E       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0000F 0000F       04 77          JMP $00008 (-9)\n"
            + "00011 00011       32             RETURN\n"
            + "00012 00012       00 00          Padding\n"
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
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main() | a\n"
            + "'     repeat until a < 1\n"
            + "00008 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00009 00009       36             CONSTANT (1)\n"
            + "0000A 0000A       F9             TEST_BELOW\n"
            + "0000B 0000B       0B 04          JNZ $00011 (4)\n"
            + "'         a := 1\n"
            + "0000D 0000D       36             CONSTANT (1)\n"
            + "0000E 0000E       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0000F 0000F       04 77          JMP $00008 (-9)\n"
            + "00011 00011       32             RETURN\n"
            + "00012 00012       00 00          Padding\n"
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
            + "00000 00000       1C 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main() | a\n"
            + "'     repeat while a < 1\n"
            + "00008 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00009 00009       36             CONSTANT (1)\n"
            + "0000A 0000A       F9             TEST_BELOW\n"
            + "0000B 0000B       0A 0B          JZ $00018 (11)\n"
            + "'         if a == 1\n"
            + "0000D 0000D       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0000E 0000E       36             CONSTANT (1)\n"
            + "0000F 0000F       FC             TEST_EQUAL\n"
            + "00010 00010       0A 02          JZ $00014 (2)\n"
            + "'             quit\n"
            + "00012 00012       04 04          JMP $00018 (4)\n"
            + "'         a := 1\n"
            + "00014 00014       36             CONSTANT (1)\n"
            + "00015 00015       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00016 00016       04 70          JMP $00008 (-16)\n"
            + "00018 00018       32             RETURN\n"
            + "00019 00019       00 00 00       Padding\n"
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
            + "00000 00000       10 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main() | a\n"
            + "'     repeat\n"
            + "'         a := 1\n"
            + "00008 00008       36             CONSTANT (1)\n"
            + "00009 00009       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     while a < 1\n"
            + "0000A 0000A       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0000B 0000B       36             CONSTANT (1)\n"
            + "0000C 0000C       F9             TEST_BELOW\n"
            + "0000D 0000D       0B 79          JNZ $00008 (-7)\n"
            + "0000F 0000F       32             RETURN\n"
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
            + "00000 00000       10 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main() | a\n"
            + "'     repeat\n"
            + "'         a := 1\n"
            + "00008 00008       36             CONSTANT (1)\n"
            + "00009 00009       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     until a < 1\n"
            + "0000A 0000A       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0000B 0000B       36             CONSTANT (1)\n"
            + "0000C 0000C       F9             TEST_BELOW\n"
            + "0000D 0000D       0A 79          JZ $00008 (-7)\n"
            + "0000F 0000F       32             RETURN\n"
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
            + "00000 00000       18 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main() | a\n"
            + "'     repeat\n"
            + "'         if a == 1\n"
            + "00008 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00009 00009       36             CONSTANT (1)\n"
            + "0000A 0000A       FC             TEST_EQUAL\n"
            + "0000B 0000B       0A 02          JZ $0000F (2)\n"
            + "'             quit\n"
            + "0000D 0000D       04 07          JMP $00016 (7)\n"
            + "'         a := 1\n"
            + "0000F 0000F       36             CONSTANT (1)\n"
            + "00010 00010       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     while a < 1\n"
            + "00011 00011       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00012 00012       36             CONSTANT (1)\n"
            + "00013 00013       F9             TEST_BELOW\n"
            + "00014 00014       0B 72          JNZ $00008 (-14)\n"
            + "00016 00016       32             RETURN\n"
            + "00017 00017       00             Padding\n"
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
            + "00000 00000       18 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main() | a\n"
            + "'     repeat\n"
            + "'         if a == 1\n"
            + "00008 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00009 00009       36             CONSTANT (1)\n"
            + "0000A 0000A       FC             TEST_EQUAL\n"
            + "0000B 0000B       0A 02          JZ $0000F (2)\n"
            + "'             next\n"
            + "0000D 0000D       04 02          JMP $00011 (2)\n"
            + "'         a := 1\n"
            + "0000F 0000F       36             CONSTANT (1)\n"
            + "00010 00010       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     until a < 1\n"
            + "00011 00011       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00012 00012       36             CONSTANT (1)\n"
            + "00013 00013       F9             TEST_BELOW\n"
            + "00014 00014       0A 72          JZ $00008 (-14)\n"
            + "00016 00016       32             RETURN\n"
            + "00017 00017       00             Padding\n"
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
            + "00000 00000       18 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 08 00    Function main @ $0008 (local size 8)\n"
            + "' PUB main() | a, b\n"
            + "'     repeat a from 1 to 10\n"
            + "00008 00008       36             CONSTANT (1)\n"
            + "00009 00009       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'         b := a + 1\n"
            + "0000A 0000A       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0000B 0000B       36             CONSTANT (1)\n"
            + "0000C 0000C       EC             ADD\n"
            + "0000D 0000D       69             VAR_WRITE LONG DBASE+$0008 (short)\n"
            + "0000E 0000E       36             CONSTANT (1)\n"
            + "0000F 0000F       38 0A          CONSTANT (10)\n"
            + "00011 00011       66 02 76       VAR_MODIFY LONG DBASE+$0004 (short) REPEAT-JMP $0000A (-10)\n"
            + "00014 00014       32             RETURN\n"
            + "00015 00015       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatRangeVariables() throws Exception {
        String text = ""
            + "VAR\n"
            + "    long a, b\n"
            + "\n"
            + "PUB main() | c, d\n"
            + "\n"
            + "    repeat a from b to c\n"
            + "        d := a + 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 08 00    Function main @ $0008 (local size 8)\n"
            + "' PUB main() | c, d\n"
            + "'     repeat a from b to c\n"
            + "00008 00008       44             VAR_READ LONG VBASE+$0004 (short)\n"
            + "00009 00009       41             VAR_WRITE LONG VBASE+$0000 (short)\n"
            + "'         d := a + 1\n"
            + "0000A 0000A       40             VAR_READ LONG VBASE+$0000 (short)\n"
            + "0000B 0000B       36             CONSTANT (1)\n"
            + "0000C 0000C       EC             ADD\n"
            + "0000D 0000D       69             VAR_WRITE LONG DBASE+$0008 (short)\n"
            + "0000E 0000E       44             VAR_READ LONG VBASE+$0004 (short)\n"
            + "0000F 0000F       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00010 00010       42 02 77       VAR_MODIFY LONG VBASE+$0000 (short) REPEAT-JMP $0000A (-9)\n"
            + "00013 00013       32             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testRepeatRangeStepVariables() throws Exception {
        String text = ""
            + "VAR\n"
            + "    long a, b\n"
            + "\n"
            + "PUB main() | c, d\n"
            + "\n"
            + "    repeat a from b to c step 5\n"
            + "        d := a + 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       18 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 08 00    Function main @ $0008 (local size 8)\n"
            + "' PUB main() | c, d\n"
            + "'     repeat a from b to c step 5\n"
            + "00008 00008       44             VAR_READ LONG VBASE+$0004 (short)\n"
            + "00009 00009       41             VAR_WRITE LONG VBASE+$0000 (short)\n"
            + "'         d := a + 1\n"
            + "0000A 0000A       40             VAR_READ LONG VBASE+$0000 (short)\n"
            + "0000B 0000B       36             CONSTANT (1)\n"
            + "0000C 0000C       EC             ADD\n"
            + "0000D 0000D       69             VAR_WRITE LONG DBASE+$0008 (short)\n"
            + "0000E 0000E       38 05          CONSTANT (5)\n"
            + "00010 00010       44             VAR_READ LONG VBASE+$0004 (short)\n"
            + "00011 00011       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00012 00012       42 06 75       VAR_MODIFY LONG VBASE+$0000 (short) REPEAT-JMP $0000A (-11)\n"
            + "00015 00015       32             RETURN\n"
            + "00016 00016       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatRangeStepNext() throws Exception {
        String text = ""
            + "VAR\n"
            + "    long a, b\n"
            + "\n"
            + "PUB main() | c, d\n"
            + "\n"
            + "    repeat a from b to c step 5\n"
            + "        if c == 2\n"
            + "            next\n"
            + "        d := a + 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       20 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 08 00    Function main @ $0008 (local size 8)\n"
            + "' PUB main() | c, d\n"
            + "'     repeat a from b to c step 5\n"
            + "00008 00008       44             VAR_READ LONG VBASE+$0004 (short)\n"
            + "00009 00009       41             VAR_WRITE LONG VBASE+$0000 (short)\n"
            + "'         if c == 2\n"
            + "0000A 0000A       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0000B 0000B       38 02          CONSTANT (2)\n"
            + "0000D 0000D       FC             TEST_EQUAL\n"
            + "0000E 0000E       0A 02          JZ $00012 (2)\n"
            + "'             next\n"
            + "00010 00010       04 04          JMP $00016 (4)\n"
            + "'         d := a + 1\n"
            + "00012 00012       40             VAR_READ LONG VBASE+$0000 (short)\n"
            + "00013 00013       36             CONSTANT (1)\n"
            + "00014 00014       EC             ADD\n"
            + "00015 00015       69             VAR_WRITE LONG DBASE+$0008 (short)\n"
            + "00016 00016       38 05          CONSTANT (5)\n"
            + "00018 00018       44             VAR_READ LONG VBASE+$0004 (short)\n"
            + "00019 00019       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0001A 0001A       42 06 6D       VAR_MODIFY LONG VBASE+$0000 (short) REPEAT-JMP $0000A (-19)\n"
            + "0001D 0001D       32             RETURN\n"
            + "0001E 0001E       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testCompilePAsm() throws Exception {
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
            + "' Object header\n"
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

    String compile(String text) throws Exception {
        Spin1TokenStream stream = new Spin1TokenStream(text);
        Spin1Parser subject = new Spin1Parser(stream);
        Node root = subject.parse();

        Spin1Compiler compiler = new Spin1Compiler();
        Spin1Object obj = compiler.compileObject(root);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateListing(new PrintStream(os));

        return os.toString();
    }

}
