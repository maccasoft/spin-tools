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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;

class Spin1CompilerTest {

    @AfterEach
    void afterEach() {
        Spin1Compiler.OPENSPIN_COMPATIBILITY = false;
    }

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
            + "00009 00009       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "0000A 0000A       80             WRITE\n"
            + "0000B 0000B       35             CONSTANT (0)\n"
            + "0000C 0000C       FC             TEST_EQUAL\n"
            + "0000D 0000D       0A 02          JZ $00011 (2)\n"
            + "'         a := 1\n"
            + "0000F 0000F       36             CONSTANT (1)\n"
            + "00010 00010       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00011 00011       32             RETURN\n"
            + "00012 00012       00 00          Padding\n"
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
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main() | a\n"
            + "'     repeat 10\n"
            + "00008 00008       38 0A          CONSTANT (10)\n"
            + "0000A 0000A       08 04          TJZ $00010 (4)\n"
            + "'         a := 1\n"
            + "0000C 0000C       36             CONSTANT (1)\n"
            + "0000D 0000D       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0000E 0000E       09 7C          DJNZ $0000C (-4)\n"
            + "00010 00010       32             RETURN\n"
            + "00011 00011       00 00 00       Padding\n"
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
    void testModifyExpressions() throws Exception {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    a += 1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       0C 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main | a\n"
            + "'     a += 1\n"
            + "00008 00008       36             CONSTANT (1)\n"
            + "00009 00009       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "0000A 0000A       4C             ADD\n"
            + "0000B 0000B       32             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testCase() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    case a\n"
            + "        1: a := 4\n"
            + "        2:\n"
            + "           a := 5\n"
            + "        3: a := 6\n"
            + "        other: a := 7\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       28 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main() | a\n"
            + "'     case a\n"
            + "00008 00008       38 26          CONSTANT (.label_13)\n"
            + "0000A 0000A       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0000B 0000B       36             CONSTANT (1)\n"
            + "0000C 0000C       0D 0C          CASE-JMP $0001A (12)\n"
            + "0000E 0000E       38 02          CONSTANT (2)\n"
            + "00010 00010       0D 0C          CASE-JMP $0001E (12)\n"
            + "00012 00012       38 03          CONSTANT (3)\n"
            + "00014 00014       0D 0C          CASE-JMP $00022 (12)\n"
            + "'         other: a := 7\n"
            + "00016 00016       38 07          CONSTANT (7)\n"
            + "00018 00018       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00019 00019       0C             CASE_DONE\n"
            + "'         1: a := 4\n"
            + "0001A 0001A       38 04          CONSTANT (4)\n"
            + "0001C 0001C       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0001D 0001D       0C             CASE_DONE\n"
            + "'            a := 5\n"
            + "0001E 0001E       38 05          CONSTANT (5)\n"
            + "00020 00020       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00021 00021       0C             CASE_DONE\n"
            + "'         3: a := 6\n"
            + "00022 00022       38 06          CONSTANT (6)\n"
            + "00024 00024       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00025 00025       0C             CASE_DONE\n"
            + "00026 00026       32             RETURN\n"
            + "00027 00027       00             Padding\n"
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
            + "00000 00000       1C 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main() | a\n"
            + "'     case a\n"
            + "00008 00008       38 18          CONSTANT (.label_7)\n"
            + "0000A 0000A       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0000B 0000B       36             CONSTANT (1)\n"
            + "0000C 0000C       38 05          CONSTANT (5)\n"
            + "0000E 0000E       0E 04          CASE-RANGE-JMP $00014 (4)\n"
            + "'         other: a := 7\n"
            + "00010 00010       38 07          CONSTANT (7)\n"
            + "00012 00012       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00013 00013       0C             CASE_DONE\n"
            + "'         1..5: a := 6\n"
            + "00014 00014       38 06          CONSTANT (6)\n"
            + "00016 00016       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00017 00017       0C             CASE_DONE\n"
            + "00018 00018       32             RETURN\n"
            + "00019 00019       00 00 00       Padding\n"
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
            + "00000 00000       18 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 08 00    Function main @ $0008 (local size 8)\n"
            + "' PUB main() | a, b\n"
            + "'     a := (b == 1) ? 2 : 3\n"
            + "00008 00008       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "00009 00009       36             CONSTANT (1)\n"
            + "0000A 0000A       FC             TEST_EQUAL\n"
            + "0000B 0000B       0A 04          JZ $00011 (4)\n"
            + "0000D 0000D       38 02          CONSTANT (2)\n"
            + "0000F 0000F       04 02          JMP $00013 (2)\n"
            + "00011 00011       38 03          CONSTANT (3)\n"
            + "00013 00013       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00014 00014       32             RETURN\n"
            + "00015 00015       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testMethodCall() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    function1(1, 2, 3)\n"
            + "    \\function2\n"
            + "\n"
            + "PUB function1(a, b, c)\n"
            + "\n"
            + "PUB function2\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       20 00          Object size\n"
            + "00002 00002       04             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       10 00 00 00    Function main @ $0010 (local size 0)\n"
            + "00008 00008       1C 00 00 00    Function function1 @ $001C (local size 0)\n"
            + "0000C 0000C       1D 00 00 00    Function function2 @ $001D (local size 0)\n"
            + "' PUB main()\n"
            + "'     function1(1, 2, 3)\n"
            + "00010 00010       01             ANCHOR\n"
            + "00011 00011       36             CONSTANT (1)\n"
            + "00012 00012       38 02          CONSTANT (2)\n"
            + "00014 00014       38 03          CONSTANT (3)\n"
            + "00016 00016       05 02          CALL_SUB\n"
            + "'     \\function2\n"
            + "00018 00018       03             ANCHOR (TRY)\n"
            + "00019 00019       05 03          CALL_SUB\n"
            + "0001B 0001B       32             RETURN\n"
            + "' PUB function1(a, b, c)\n"
            + "0001C 0001C       32             RETURN\n"
            + "' PUB function2\n"
            + "0001D 0001D       32             RETURN\n"
            + "0001E 0001E       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testMethodReturn() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    return 1\n"
            + "\n"
            + "PUB function1 | a\n"
            + "\n"
            + "    return a\n"
            + "\n"
            + "PUB function2 : b\n"
            + "\n"
            + "    b := 1\n"
            + "    return\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       1C 00          Object size\n"
            + "00002 00002       04             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       10 00 00 00    Function main @ $0010 (local size 0)\n"
            + "00008 00008       13 00 04 00    Function function1 @ $0013 (local size 4)\n"
            + "0000C 0000C       16 00 00 00    Function function2 @ $0016 (local size 0)\n"
            + "' PUB main()\n"
            + "'     return 1\n"
            + "00010 00010       36             CONSTANT (1)\n"
            + "00011 00011       33             RETURN\n"
            + "00012 00012       32             RETURN\n"
            + "' PUB function1 | a\n"
            + "'     return a\n"
            + "00013 00013       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00014 00014       33             RETURN\n"
            + "00015 00015       32             RETURN\n"
            + "' PUB function2 : b\n"
            + "'     b := 1\n"
            + "00016 00016       36             CONSTANT (1)\n"
            + "00017 00017       61             VAR_WRITE LONG DBASE+$0000 (short)\n"
            + "'     return\n"
            + "00018 00018       32             RETURN\n"
            + "00019 00019       32             RETURN\n"
            + "0001A 0001A       00 00          Padding\n"
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

    @Test
    void testCompilePAsmReference() throws Exception {
        String text = ""
            + "PUB main\n"
            + "\n"
            + "    coginit(cogid, @start, 0)\n"
            + "\n"
            + "DAT             org     $000\n"
            + "\n"
            + "start           cogid   a\n"
            + "                cogstop a\n"
            + "\n"
            + "a               res     1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       18 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       10 00 00 00    Function main @ $0010 (local size 0)\n"
            + "00008 00008   000                                    org     $000\n"
            + "00008 00008   000 01 04 FC 0C    start               cogid   a\n"
            + "0000C 0000C   001 03 04 7C 0C                        cogstop a\n"
            + "00010 00010   002                a                   res     1\n"
            + "' PUB main\n"
            + "'     coginit(cogid, @start, 0)\n"
            + "00010 00010       3F 89          REG_READ $1E9\n"
            + "00012 00012       C7 08          MEM_ADDRESS LONG PBASE+$0008\n"
            + "00014 00014       35             CONSTANT (0)\n"
            + "00015 00015       2C             COGINIT\n"
            + "00016 00016       32             RETURN\n"
            + "00017 00017       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testObjectLink() throws Exception {
        String text = ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main() | a\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "";

        Map<String, String> sources = new HashMap<String, String>();
        sources.put("text2", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       10 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       01             Object count\n"
            + "00004 00004       0C 00 04 00    Function main @ $000C (local size 4)\n"
            + "00008 00008       10 00          Header offset\n"
            + "0000A 0000A       00 00          Var offset\n"
            + "' PUB main() | a\n"
            + "'     a := 1\n"
            + "0000C 0000C       36             CONSTANT (1)\n"
            + "0000D 0000D       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0000E 0000E       32             RETURN\n"
            + "0000F 0000F       00             Padding\n"
            + "' Object header\n"
            + "00010 00000       10 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       00             Object count\n"
            + "00014 00004       08 00 04 00    Function start @ $0008 (local size 4)\n"
            + "' PUB start(a, b) | c\n"
            + "'     c := a + b\n"
            + "00018 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00019 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0001A 0000A       EC             ADD\n"
            + "0001B 0000B       6D             VAR_WRITE LONG DBASE+$000C (short)\n"
            + "0001C 0000C       32             RETURN\n"
            + "0001D 0000D       00 00 00       Padding\n"
            + "", compile(text, sources));
    }

    @Test
    void testObjectVariablesLink() throws Exception {
        String text = ""
            + "VAR\n"
            + "\n"
            + "    long b\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "    p : \"text2\"\n"
            + "\n"
            + "PUB main() | a\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "";

        Map<String, String> sources = new HashMap<String, String>();
        sources.put("text1", ""
            + "VAR\n"
            + "\n"
            + "    long d, e\n"
            + "\n"
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");
        sources.put("text2", ""
            + "VAR\n"
            + "\n"
            + "    long d, e, f, g\n"
            + "\n"
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       02             Object count\n"
            + "00004 00004       10 00 04 00    Function main @ $0010 (local size 4)\n"
            + "00008 00008       14 00          Header offset\n"
            + "0000A 0000A       04 00          Var offset\n"
            + "0000C 0000C       24 00          Header offset\n"
            + "0000E 0000E       0C 00          Var offset\n"
            + "' PUB main() | a\n"
            + "'     a := 1\n"
            + "00010 00010       36             CONSTANT (1)\n"
            + "00011 00011       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00012 00012       32             RETURN\n"
            + "00013 00013       00             Padding\n"
            + "' Object header\n"
            + "00014 00000       10 00          Object size\n"
            + "00016 00002       02             Method count + 1\n"
            + "00017 00003       00             Object count\n"
            + "00018 00004       08 00 04 00    Function start @ $0008 (local size 4)\n"
            + "' PUB start(a, b) | c\n"
            + "'     c := a + b\n"
            + "0001C 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0001D 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0001E 0000A       EC             ADD\n"
            + "0001F 0000B       6D             VAR_WRITE LONG DBASE+$000C (short)\n"
            + "00020 0000C       32             RETURN\n"
            + "00021 0000D       00 00 00       Padding\n"
            + "' Object header\n"
            + "00024 00000       10 00          Object size\n"
            + "00026 00002       02             Method count + 1\n"
            + "00027 00003       00             Object count\n"
            + "00028 00004       08 00 04 00    Function start @ $0008 (local size 4)\n"
            + "' PUB start(a, b) | c\n"
            + "'     c := a + b\n"
            + "0002C 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0002D 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0002E 0000A       EC             ADD\n"
            + "0002F 0000B       6D             VAR_WRITE LONG DBASE+$000C (short)\n"
            + "00030 0000C       32             RETURN\n"
            + "00031 0000D       00 00 00       Padding\n"
            + "", compile(text, sources));
    }

    @Test
    void testObjectMethodCall() throws Exception {
        String text = ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main() | a\n"
            + "\n"
            + "    o.start(1, 2)\n"
            + "\n"
            + "";

        Map<String, String> sources = new HashMap<String, String>();
        sources.put("text2", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       01             Object count\n"
            + "00004 00004       0C 00 04 00    Function main @ $000C (local size 4)\n"
            + "00008 00008       14 00          Header offset\n"
            + "0000A 0000A       00 00          Var offset\n"
            + "' PUB main() | a\n"
            + "'     o.start(1, 2)\n"
            + "0000C 0000C       01             ANCHOR\n"
            + "0000D 0000D       36             CONSTANT (1)\n"
            + "0000E 0000E       38 02          CONSTANT (2)\n"
            + "00010 00010       06 02 01       CALL_OBJ_SUB\n"
            + "00013 00013       32             RETURN\n"
            + "' Object header\n"
            + "00014 00000       10 00          Object size\n"
            + "00016 00002       02             Method count + 1\n"
            + "00017 00003       00             Object count\n"
            + "00018 00004       08 00 04 00    Function start @ $0008 (local size 4)\n"
            + "' PUB start(a, b) | c\n"
            + "'     c := a + b\n"
            + "0001C 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0001D 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0001E 0000A       EC             ADD\n"
            + "0001F 0000B       6D             VAR_WRITE LONG DBASE+$000C (short)\n"
            + "00020 0000C       32             RETURN\n"
            + "00021 0000D       00 00 00       Padding\n"
            + "", compile(text, sources));
    }

    @Test
    void testObjectConstant() throws Exception {
        String text = ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main() | a\n"
            + "\n"
            + "    a := o#CONSTANT\n"
            + "\n"
            + "";

        Map<String, String> sources = new HashMap<String, String>();
        sources.put("text2", ""
            + "CON\n"
            + "\n"
            + "    CONSTANT = 1\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       10 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       01             Object count\n"
            + "00004 00004       0C 00 04 00    Function main @ $000C (local size 4)\n"
            + "00008 00008       10 00          Header offset\n"
            + "0000A 0000A       00 00          Var offset\n"
            + "' PUB main() | a\n"
            + "'     a := o#CONSTANT\n"
            + "0000C 0000C       36             CONSTANT (1)\n"
            + "0000D 0000D       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0000E 0000E       32             RETURN\n"
            + "0000F 0000F       00             Padding\n"
            + "' Object header\n"
            + "00010 00000       04 00          Object size\n"
            + "00012 00002       01             Method count + 1\n"
            + "00013 00003       00             Object count\n"
            + "", compile(text, sources));
    }

    @Test
    void testLookdown() throws Exception {
        String text = ""
            + "PUB main | a, b\n"
            + "\n"
            + "    a := lookdown(b : 10, 20, 30, 40)\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       1C 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 08 00    Function main @ $0008 (local size 8)\n"
            + "' PUB main | a, b\n"
            + "'     a := lookdown(b : 10, 20, 30, 40)\n"
            + "00008 00008       36             CONSTANT (1)\n"
            + "00009 00009       38 19          CONSTANT (25)\n"
            + "0000B 0000B       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0000C 0000C       38 0A          CONSTANT (10)\n"
            + "0000E 0000E       11             LOOKDOWN\n"
            + "0000F 0000F       38 14          CONSTANT (20)\n"
            + "00011 00011       11             LOOKDOWN\n"
            + "00012 00012       38 1E          CONSTANT (30)\n"
            + "00014 00014       11             LOOKDOWN\n"
            + "00015 00015       38 28          CONSTANT (40)\n"
            + "00017 00017       11             LOOKDOWN\n"
            + "00018 00018       0F             LOOKDONE\n"
            + "00019 00019       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0001A 0001A       32             RETURN\n"
            + "0001B 0001B       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testLookupRange() throws Exception {
        String text = ""
            + "PUB main | a, b\n"
            + "\n"
            + "    a := lookup(b : 10, 20..30, 40)\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       1C 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 08 00    Function main @ $0008 (local size 8)\n"
            + "' PUB main | a, b\n"
            + "'     a := lookup(b : 10, 20..30, 40)\n"
            + "00008 00008       36             CONSTANT (1)\n"
            + "00009 00009       38 18          CONSTANT (24)\n"
            + "0000B 0000B       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0000C 0000C       38 0A          CONSTANT (10)\n"
            + "0000E 0000E       10             LOOKUP\n"
            + "0000F 0000F       38 14          CONSTANT (20)\n"
            + "00011 00011       38 1E          CONSTANT (30)\n"
            + "00013 00013       12             LOOKUP\n"
            + "00014 00014       38 28          CONSTANT (40)\n"
            + "00016 00016       10             LOOKUP\n"
            + "00017 00017       0F             LOOKDONE\n"
            + "00018 00018       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00019 00019       32             RETURN\n"
            + "0001A 0001A       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testUnaryOperators() throws Exception {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    a++\n"
            + "    a--\n"
            + "    a~\n"
            + "    a~~\n"
            + "    ++a\n"
            + "    --a\n"
            + "    ~a\n"
            + "    ~~a\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       1C 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main | a\n"
            + "'     a++\n"
            + "00008 00008       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "00009 00009       2E             POST_INC\n"
            + "'     a--\n"
            + "0000A 0000A       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "0000B 0000B       3E             POST_DEC\n"
            + "'     a~\n"
            + "0000C 0000C       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "0000D 0000D       18             POST_CLEAR\n"
            + "'     a~~\n"
            + "0000E 0000E       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "0000F 0000F       1C             POST_SET\n"
            + "'     ++a\n"
            + "00010 00010       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "00011 00011       26             PRE_INC\n"
            + "'     --a\n"
            + "00012 00012       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "00013 00013       36             PRE_DEC\n"
            + "'     ~a\n"
            + "00014 00014       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "00015 00015       10             SIGN_EXTEND_BYTE\n"
            + "'     ~~a\n"
            + "00016 00016       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "00017 00017       14             SIGN_EXTEND_WORD\n"
            + "00018 00018       32             RETURN\n"
            + "00019 00019       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testUnaryOperatorsAssignment() throws Exception {
        String text = ""
            + "PUB main | a, b\n"
            + "\n"
            + "    b := a++\n"
            + "    b := a--\n"
            + "    b := a~\n"
            + "    b := a~~\n"
            + "    b := ++a\n"
            + "    b := --a\n"
            + "    b := ~a\n"
            + "    b := ~~a\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       24 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 08 00    Function main @ $0008 (local size 8)\n"
            + "' PUB main | a, b\n"
            + "'     b := a++\n"
            + "00008 00008       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "00009 00009       AE             POST_INC\n"
            + "0000A 0000A       69             VAR_WRITE LONG DBASE+$0008 (short)\n"
            + "'     b := a--\n"
            + "0000B 0000B       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "0000C 0000C       BE             POST_DEC\n"
            + "0000D 0000D       69             VAR_WRITE LONG DBASE+$0008 (short)\n"
            + "'     b := a~\n"
            + "0000E 0000E       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "0000F 0000F       98             POST_CLEAR\n"
            + "00010 00010       69             VAR_WRITE LONG DBASE+$0008 (short)\n"
            + "'     b := a~~\n"
            + "00011 00011       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "00012 00012       9C             POST_SET\n"
            + "00013 00013       69             VAR_WRITE LONG DBASE+$0008 (short)\n"
            + "'     b := ++a\n"
            + "00014 00014       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "00015 00015       A6             PRE_INC\n"
            + "00016 00016       69             VAR_WRITE LONG DBASE+$0008 (short)\n"
            + "'     b := --a\n"
            + "00017 00017       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "00018 00018       B6             PRE_DEC\n"
            + "00019 00019       69             VAR_WRITE LONG DBASE+$0008 (short)\n"
            + "'     b := ~a\n"
            + "0001A 0001A       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "0001B 0001B       90             SIGN_EXTEND_BYTE\n"
            + "0001C 0001C       69             VAR_WRITE LONG DBASE+$0008 (short)\n"
            + "'     b := ~~a\n"
            + "0001D 0001D       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "0001E 0001E       94             SIGN_EXTEND_WORD\n"
            + "0001F 0001F       69             VAR_WRITE LONG DBASE+$0008 (short)\n"
            + "00020 00020       32             RETURN\n"
            + "00021 00021       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testRegisterUnaryOperators() throws Exception {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    DIRA[1]~\n"
            + "    DIRA[2]~~\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main | a\n"
            + "'     DIRA[1]~\n"
            + "00008 00008       36             CONSTANT (1)\n"
            + "00009 00009       3D D6          REGBIT_MODIFY $1F6\n"
            + "0000B 0000B       18             POST_CLEAR\n"
            + "'     DIRA[2]~~\n"
            + "0000C 0000C       38 02          CONSTANT (2)\n"
            + "0000E 0000E       3D D6          REGBIT_MODIFY $1F6\n"
            + "00010 00010       1C             POST_SET\n"
            + "00011 00011       32             RETURN\n"
            + "00012 00012       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testRegisterBit() throws Exception {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    DIRA[1] := 1\n"
            + "    DIRA[2..5] := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main | a\n"
            + "'     DIRA[1] := 1\n"
            + "00008 00008       36             CONSTANT (1)\n"
            + "00009 00009       36             CONSTANT (1)\n"
            + "0000A 0000A       3D B6          REGBIT_WRITE $1F6\n"
            + "'     DIRA[2..5] := 1\n"
            + "0000C 0000C       36             CONSTANT (1)\n"
            + "0000D 0000D       38 02          CONSTANT (2)\n"
            + "0000F 0000F       38 05          CONSTANT (5)\n"
            + "00011 00011       3D B6          REGBIT_WRITE $1F6\n"
            + "00013 00013       32             RETURN\n"
            + "", compile(text));
    }

    @Test
    void testVarSizeAssignment() throws Exception {
        String text = ""
            + "PUB main | a, b\n"
            + "\n"
            + "    BYTE[a] := 1\n"
            + "    b := BYTE[a]\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       10 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 08 00    Function main @ $0008 (local size 8)\n"
            + "' PUB main | a, b\n"
            + "'     BYTE[a] := 1\n"
            + "00008 00008       36             CONSTANT (1)\n"
            + "00009 00009       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0000A 0000A       81             MEM_WRITE BYTE POP\n"
            + "'     b := BYTE[a]\n"
            + "0000B 0000B       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0000C 0000C       80             MEM_READ BYTE POP\n"
            + "0000D 0000D       69             VAR_WRITE LONG DBASE+$0008 (short)\n"
            + "0000E 0000E       32             RETURN\n"
            + "0000F 0000F       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testVarOrdering() throws Exception {
        String text = ""
            + "VAR\n"
            + "\n"
            + "    byte a\n"
            + "    word b\n"
            + "    long c\n"
            + "\n"
            + "PUB main\n"
            + "\n"
            + "    a := 1\n"
            + "    b := 1\n"
            + "    c := 1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 00 00    Function main @ $0008 (local size 0)\n"
            + "' PUB main\n"
            + "'     a := 1\n"
            + "00008 00008       36             CONSTANT (1)\n"
            + "00009 00009       89 06          VAR_WRITE BYTE VBASE+$0006\n"
            + "'     b := 1\n"
            + "0000B 0000B       36             CONSTANT (1)\n"
            + "0000C 0000C       A9 04          VAR_WRITE WORD VBASE+$0004\n"
            + "'     c := 1\n"
            + "0000E 0000E       36             CONSTANT (1)\n"
            + "0000F 0000F       41             VAR_WRITE LONG VBASE+$0000 (short)\n"
            + "00010 00010       32             RETURN\n"
            + "00011 00011       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testIfCaseElse() throws Exception {
        Spin1Compiler.OPENSPIN_COMPATIBILITY = true;

        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    if a >= 1\n"
            + "        case a\n"
            + "            1: a := 4\n"
            + "            2: a := 5\n"
            + "            3: a := 6\n"
            + "    else\n"
            + "        a := 8\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       30 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main() | a\n"
            + "'     if a >= 1\n"
            + "00008 00008       36             CONSTANT (1)\n"
            + "00009 00009       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "0000A 0000A       DA             TEST_ABOVE\n"
            + "0000B 0000B       0A 1D          JZ $0002A (29)\n"
            + "'         case a\n"
            + "0000D 0000D       38 28          CONSTANT (.label_11)\n"
            + "0000F 0000F       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00010 00010       36             CONSTANT (1)\n"
            + "00011 00011       0D 09          CASE-JMP $0001C (9)\n"
            + "00013 00013       37 00          CONSTANT (2)\n"
            + "00015 00015       0D 09          CASE-JMP $00020 (9)\n"
            + "00017 00017       37 21          CONSTANT (3)\n"
            + "00019 00019       0D 09          CASE-JMP $00024 (9)\n"
            + "0001B 0001B       0C             JMP_POP\n"
            + "'             1: a := 4\n"
            + "0001C 0001C       37 01          CONSTANT (4)\n"
            + "0001E 0001E       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0001F 0001F       0C             CASE_DONE\n"
            + "'             2: a := 5\n"
            + "00020 00020       38 05          CONSTANT (5)\n"
            + "00022 00022       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00023 00023       0C             CASE_DONE\n"
            + "'             3: a := 6\n"
            + "00024 00024       38 06          CONSTANT (6)\n"
            + "00026 00026       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00027 00027       0C             CASE_DONE\n"
            + "00028 00028       04 03          JMP $0002D (3)\n"
            + "'     else\n"
            + "'         a := 8\n"
            + "0002A 0002A       37 02          CONSTANT (8)\n"
            + "0002C 0002C       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0002D 0002D       32             RETURN\n"
            + "0002E 0002E       00 00          Padding\n"
            + "", compile(text));
    }

    @Test
    void testTypeIndex() throws Exception {
        String text = ""
            + "PUB main | a, b\n"
            + "\n"
            + "    a := WORD[b]\n"
            + "    WORD[b] := a\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       10 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 08 00    Function main @ $0008 (local size 8)\n"
            + "' PUB main | a, b\n"
            + "'     a := WORD[b]\n"
            + "00008 00008       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "00009 00009       A0             MEM_READ WORD POP\n"
            + "0000A 0000A       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     WORD[b] := a\n"
            + "0000B 0000B       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0000C 0000C       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0000D 0000D       A1             MEM_WRITE WORD POP\n"
            + "0000E 0000E       32             RETURN\n"
            + "0000F 0000F       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testArray() throws Exception {
        String text = ""
            + "PUB main | a, b\n"
            + "\n"
            + "    a := WORD[b][0]\n"
            + "    WORD[b][0] := a\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 08 00    Function main @ $0008 (local size 8)\n"
            + "' PUB main | a, b\n"
            + "'     a := WORD[b][0]\n"
            + "00008 00008       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "00009 00009       35             CONSTANT (0)\n"
            + "0000A 0000A       B0             MEM_READ_INDEXED WORD POP\n"
            + "0000B 0000B       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     WORD[b][0] := a\n"
            + "0000C 0000C       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0000D 0000D       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0000E 0000E       35             CONSTANT (0)\n"
            + "0000F 0000F       B1             MEM_WRITE_INDEXED WORD POP\n"
            + "00010 00010       32             RETURN\n"
            + "00011 00011       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testVariableIndex() throws Exception {
        String text = ""
            + "PUB main | a, b[5]\n"
            + "\n"
            + "    a := b[1]\n"
            + "    b[1] := a\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 18 00    Function main @ $0008 (local size 24)\n"
            + "' PUB main | a, b[5]\n"
            + "'     a := b[1]\n"
            + "00008 00008       36             CONSTANT (1)\n"
            + "00009 00009       DC 08          VAR_READ_INDEXED LONG DBASE+$0008 (short)\n"
            + "0000B 0000B       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     b[1] := a\n"
            + "0000C 0000C       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0000D 0000D       36             CONSTANT (1)\n"
            + "0000E 0000E       DD 08          VAR_WRITE_INDEXED LONG DBASE+$0008 (short)\n"
            + "00010 00010       32             RETURN\n"
            + "00011 00011       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testRepeatComplexExpression() throws Exception {
        String text = ""
            + "PUB main | a, b, c\n"
            + "\n"
            + "    repeat until ((a := byte[b][c++]) == 0)\n"
            + "        a := 1\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       18 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 0C 00    Function main @ $0008 (local size 12)\n"
            + "' PUB main | a, b, c\n"
            + "'     repeat until ((a := byte[b][c++]) == 0)\n"
            + "00008 00008       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "00009 00009       6E             VAR_MODIFY LONG DBASE+$000C (short)\n"
            + "0000A 0000A       AE             POST_INC\n"
            + "0000B 0000B       90             MEM_READ_INDEXED BYTE POP\n"
            + "0000C 0000C       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "0000D 0000D       80             WRITE\n"
            + "0000E 0000E       35             CONSTANT (0)\n"
            + "0000F 0000F       FC             TEST_EQUAL\n"
            + "00010 00010       0B 04          JNZ $00016 (4)\n"
            + "'         a := 1\n"
            + "00012 00012       36             CONSTANT (1)\n"
            + "00013 00013       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00014 00014       04 72          JMP $00008 (-14)\n"
            + "00016 00016       32             RETURN\n"
            + "00017 00017       00             Padding\n"
            + "", compile(text));
    }

    @Test
    void testUnaryExpressions() throws Exception {
        String text = ""
            + "PUB main | a, b\n"
            + "\n"
            + "    a := -b\n"
            + "    a := ?b\n"
            + "    a := b?\n"
            + "    a++\n"
            + "    --a\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       18 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 08 00    Function main @ $0008 (local size 8)\n"
            + "' PUB main | a, b\n"
            + "'     a := -b\n"
            + "00008 00008       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "00009 00009       E6             NEGATE\n"
            + "0000A 0000A       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     a := ?b\n"
            + "0000B 0000B       6A             VAR_MODIFY LONG DBASE+$0008 (short)\n"
            + "0000C 0000C       88             RANDOM_FORWARD\n"
            + "0000D 0000D       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     a := b?\n"
            + "0000E 0000E       6A             VAR_MODIFY LONG DBASE+$0008 (short)\n"
            + "0000F 0000F       8C             RANDOM_REVERSE\n"
            + "00010 00010       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     a++\n"
            + "00011 00011       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "00012 00012       2E             POST_INC\n"
            + "'     --a\n"
            + "00013 00013       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "00014 00014       36             PRE_DEC\n"
            + "00015 00015       32             RETURN\n"
            + "00016 00016       00 00          Padding\n"
            + "", compile(text));
    }

    String compile(String text) throws Exception {
        return compile(text, Collections.emptyMap());
    }

    String compile(String text, Map<String, String> sources) throws Exception {
        Spin1TokenStream stream = new Spin1TokenStream(text);
        Spin1Parser subject = new Spin1Parser(stream);
        Node root = subject.parse();

        Spin1Compiler compiler = new Spin1Compiler() {

            @Override
            protected Spin1Object getObject(String fileName) {
                String text = sources.get(fileName);
                Spin1TokenStream stream = new Spin1TokenStream(text);
                Spin1Parser subject = new Spin1Parser(stream);
                Node root = subject.parse();

                Spin1Compiler compiler = new Spin1Compiler();
                return compiler.compileObject(root);
            }

        };
        Spin1Object obj = compiler.compileObject(root);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateListing(new PrintStream(os));

        return os.toString();
    }

}
