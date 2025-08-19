/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
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
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.model.Parser;
import com.maccasoft.propeller.model.RootNode;
import com.maccasoft.propeller.model.SourceProvider;

class Spin1CompilerTest {

    @Test
    void testObjectLink() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main | a\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "");
        sources.put("text2.spin", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 0)\n"
            + "00010 00000       10 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       01             Object count\n"
            + "00014 00004       0C 00 04 00    Function main @ $000C (local size 4)\n"
            + "00018 00008       10 00 00 00    Object \"text2.spin\" @ $0010 (variables @ $0000)\n"
            + "' PUB main | a\n"
            + "'     a := 1\n"
            + "0001C 0000C       36             CONSTANT (1)\n"
            + "0001D 0000D       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0001E 0000E       32             RETURN\n"
            + "0001F 0000F       00             Padding\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
            + "00020 00000       10 00          Object size\n"
            + "00022 00002       02             Method count + 1\n"
            + "00023 00003       00             Object count\n"
            + "00024 00004       08 00 04 00    Function start @ $0008 (local size 4)\n"
            + "' PUB start(a, b) | c\n"
            + "'     c := a + b\n"
            + "00028 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00029 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0002A 0000A       EC             ADD\n"
            + "0002B 0000B       6D             VAR_WRITE LONG DBASE+$000C (short)\n"
            + "0002C 0000C       32             RETURN\n"
            + "0002D 0000D       00 00 00       Padding\n"
            + "", compile("main.spin", sources));
    }

    @Test
    void testObjectVariablesLink() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "VAR\n"
            + "\n"
            + "    long b\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "    p : \"text2\"\n"
            + "\n"
            + "PUB main | a\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "");
        sources.put("text1.spin", ""
            + "VAR\n"
            + "\n"
            + "    long d, e\n"
            + "\n"
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");
        sources.put("text2.spin", ""
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
            + "' Object \"main.spin\" header (var size 28)\n"
            + "00010 00000       14 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       02             Object count\n"
            + "00014 00004       10 00 04 00    Function main @ $0010 (local size 4)\n"
            + "00018 00008       14 00 04 00    Object \"text1.spin\" @ $0014 (variables @ $0004)\n"
            + "0001C 0000C       24 00 0C 00    Object \"text2.spin\" @ $0024 (variables @ $000C)\n"
            + "' PUB main | a\n"
            + "'     a := 1\n"
            + "00020 00010       36             CONSTANT (1)\n"
            + "00021 00011       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00022 00012       32             RETURN\n"
            + "00023 00013       00             Padding\n"
            + "' Object \"text1.spin\" header (var size 8)\n"
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
            + "' Object \"text2.spin\" header (var size 16)\n"
            + "00034 00000       10 00          Object size\n"
            + "00036 00002       02             Method count + 1\n"
            + "00037 00003       00             Object count\n"
            + "00038 00004       08 00 04 00    Function start @ $0008 (local size 4)\n"
            + "' PUB start(a, b) | c\n"
            + "'     c := a + b\n"
            + "0003C 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0003D 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0003E 0000A       EC             ADD\n"
            + "0003F 0000B       6D             VAR_WRITE LONG DBASE+$000C (short)\n"
            + "00040 0000C       32             RETURN\n"
            + "00041 0000D       00 00 00       Padding\n"
            + "", compile("main.spin", sources));
    }

    @Test
    void testObjectMethod() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main | a\n"
            + "\n"
            + "    o.start(1, 2)\n"
            + "    \\o.start(1, 2)\n"
            + "\n"
            + "PUB f1\n"
            + "\n"
            + "PUB f2\n"
            + "\n"
            + "");
        sources.put("text2.spin", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 0)\n"
            + "00010 00000       28 00          Object size\n"
            + "00012 00002       04             Method count + 1\n"
            + "00013 00003       01             Object count\n"
            + "00014 00004       14 00 04 00    Function main @ $0014 (local size 4)\n"
            + "00018 00008       23 00 00 00    Function f1 @ $0023 (local size 0)\n"
            + "0001C 0000C       24 00 00 00    Function f2 @ $0024 (local size 0)\n"
            + "00020 00010       28 00 00 00    Object \"text2.spin\" @ $0028 (variables @ $0000)\n"
            + "' PUB main | a\n"
            + "'     o.start(1, 2)\n"
            + "00024 00014       01             ANCHOR\n"
            + "00025 00015       36             CONSTANT (1)\n"
            + "00026 00016       37 00          CONSTANT (2)\n"
            + "00028 00018       06 04 01       CALL_OBJ_SUB (4.0)\n"
            + "'     \\o.start(1, 2)\n"
            + "0002B 0001B       03             ANCHOR (TRY)\n"
            + "0002C 0001C       36             CONSTANT (1)\n"
            + "0002D 0001D       37 00          CONSTANT (2)\n"
            + "0002F 0001F       06 04 01       CALL_OBJ_SUB (4.0)\n"
            + "00032 00022       32             RETURN\n"
            + "' PUB f1\n"
            + "00033 00023       32             RETURN\n"
            + "' PUB f2\n"
            + "00034 00024       32             RETURN\n"
            + "00035 00025       00 00 00       Padding\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
            + "00038 00000       10 00          Object size\n"
            + "0003A 00002       02             Method count + 1\n"
            + "0003B 00003       00             Object count\n"
            + "0003C 00004       08 00 04 00    Function start @ $0008 (local size 4)\n"
            + "' PUB start(a, b) | c\n"
            + "'     c := a + b\n"
            + "00040 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00041 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "00042 0000A       EC             ADD\n"
            + "00043 0000B       6D             VAR_WRITE LONG DBASE+$000C (short)\n"
            + "00044 0000C       32             RETURN\n"
            + "00045 0000D       00 00 00       Padding\n"
            + "", compile("main.spin", sources));
    }

    @Test
    void testObjectMethodDefaultArgument() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main | a\n"
            + "\n"
            + "    o.start(1)\n"
            + "\n"
            + "PUB f1\n"
            + "\n"
            + "PUB f2\n"
            + "\n"
            + "");
        sources.put("text2.spin", ""
            + "PUB start(a, b = 2) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 0)\n"
            + "00010 00000       20 00          Object size\n"
            + "00012 00002       04             Method count + 1\n"
            + "00013 00003       01             Object count\n"
            + "00014 00004       14 00 04 00    Function main @ $0014 (local size 4)\n"
            + "00018 00008       1C 00 00 00    Function f1 @ $001C (local size 0)\n"
            + "0001C 0000C       1D 00 00 00    Function f2 @ $001D (local size 0)\n"
            + "00020 00010       20 00 00 00    Object \"text2.spin\" @ $0020 (variables @ $0000)\n"
            + "' PUB main | a\n"
            + "'     o.start(1)\n"
            + "00024 00014       01             ANCHOR\n"
            + "00025 00015       36             CONSTANT (1)\n"
            + "00026 00016       37 00          CONSTANT (2)\n"
            + "00028 00018       06 04 01       CALL_OBJ_SUB (4.0)\n"
            + "0002B 0001B       32             RETURN\n"
            + "' PUB f1\n"
            + "0002C 0001C       32             RETURN\n"
            + "' PUB f2\n"
            + "0002D 0001D       32             RETURN\n"
            + "0002E 0001E       00 00          Padding\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
            + "00030 00000       10 00          Object size\n"
            + "00032 00002       02             Method count + 1\n"
            + "00033 00003       00             Object count\n"
            + "00034 00004       08 00 04 00    Function start @ $0008 (local size 4)\n"
            + "' PUB start(a, b = 2) | c\n"
            + "'     c := a + b\n"
            + "00038 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00039 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0003A 0000A       EC             ADD\n"
            + "0003B 0000B       6D             VAR_WRITE LONG DBASE+$000C (short)\n"
            + "0003C 0000C       32             RETURN\n"
            + "0003D 0000D       00 00 00       Padding\n"
            + "", compile("main.spin", sources));
    }

    @Test
    void testObjectMethodDefaultArgumentOverride() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main | a\n"
            + "\n"
            + "    o.start(1, 3)\n"
            + "\n"
            + "PUB f1\n"
            + "\n"
            + "PUB f2\n"
            + "\n"
            + "");
        sources.put("text2.spin", ""
            + "PUB start(a, b = 2) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 0)\n"
            + "00010 00000       20 00          Object size\n"
            + "00012 00002       04             Method count + 1\n"
            + "00013 00003       01             Object count\n"
            + "00014 00004       14 00 04 00    Function main @ $0014 (local size 4)\n"
            + "00018 00008       1C 00 00 00    Function f1 @ $001C (local size 0)\n"
            + "0001C 0000C       1D 00 00 00    Function f2 @ $001D (local size 0)\n"
            + "00020 00010       20 00 00 00    Object \"text2.spin\" @ $0020 (variables @ $0000)\n"
            + "' PUB main | a\n"
            + "'     o.start(1, 3)\n"
            + "00024 00014       01             ANCHOR\n"
            + "00025 00015       36             CONSTANT (1)\n"
            + "00026 00016       37 21          CONSTANT (3)\n"
            + "00028 00018       06 04 01       CALL_OBJ_SUB (4.0)\n"
            + "0002B 0001B       32             RETURN\n"
            + "' PUB f1\n"
            + "0002C 0001C       32             RETURN\n"
            + "' PUB f2\n"
            + "0002D 0001D       32             RETURN\n"
            + "0002E 0001E       00 00          Padding\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
            + "00030 00000       10 00          Object size\n"
            + "00032 00002       02             Method count + 1\n"
            + "00033 00003       00             Object count\n"
            + "00034 00004       08 00 04 00    Function start @ $0008 (local size 4)\n"
            + "' PUB start(a, b = 2) | c\n"
            + "'     c := a + b\n"
            + "00038 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00039 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0003A 0000A       EC             ADD\n"
            + "0003B 0000B       6D             VAR_WRITE LONG DBASE+$000C (short)\n"
            + "0003C 0000C       32             RETURN\n"
            + "0003D 0000D       00 00 00       Padding\n"
            + "", compile("main.spin", sources));
    }

    @Test
    void testObjectConstant() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main | a\n"
            + "\n"
            + "    a := o#CONSTANT\n"
            + "\n"
            + "");
        sources.put("text2.spin", ""
            + "CON\n"
            + "\n"
            + "    CONSTANT = 1\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 0)\n"
            + "00010 00000       10 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       01             Object count\n"
            + "00014 00004       0C 00 04 00    Function main @ $000C (local size 4)\n"
            + "00018 00008       10 00 00 00    Object \"text2.spin\" @ $0010 (variables @ $0000)\n"
            + "' PUB main | a\n"
            + "'     a := o#CONSTANT\n"
            + "0001C 0000C       36             CONSTANT (1)\n"
            + "0001D 0000D       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0001E 0000E       32             RETURN\n"
            + "0001F 0000F       00             Padding\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
            + "00020 00000       04 00          Object size\n"
            + "00022 00002       01             Method count + 1\n"
            + "00023 00003       00             Object count\n"
            + "", compile("main.spin", sources));
    }

    @Test
    void testObjectConstantsReference() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "CON\n"
            + "\n"
            + "    #o#LAST\n"
            + "    NUMBER\n"
            + "    NUMBER2\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main | a, b\n"
            + "\n"
            + "    a := NUMBER\n"
            + "    b := NUMBER2\n"
            + "\n"
            + "");
        sources.put("text2.spin", ""
            + "CON\n"
            + "\n"
            + "    #0\n"
            + "    ONE, TWO\n"
            + "    LAST\n"
            + "\n"
            + "PUB start\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 0)\n"
            + "00010 00000       14 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       01             Object count\n"
            + "00014 00004       0C 00 08 00    Function main @ $000C (local size 8)\n"
            + "00018 00008       14 00 00 00    Object \"text2.spin\" @ $0014 (variables @ $0000)\n"
            + "' PUB main | a, b\n"
            + "'     a := NUMBER\n"
            + "0001C 0000C       37 00          CONSTANT (o#LAST)\n"
            + "0001E 0000E       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     b := NUMBER2\n"
            + "0001F 0000F       37 21          CONSTANT (o#LAST + 1)\n"
            + "00021 00011       69             VAR_WRITE LONG DBASE+$0008 (short)\n"
            + "00022 00012       32             RETURN\n"
            + "00023 00013       00             Padding\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
            + "00024 00000       0C 00          Object size\n"
            + "00026 00002       02             Method count + 1\n"
            + "00027 00003       00             Object count\n"
            + "00028 00004       08 00 00 00    Function start @ $0008 (local size 0)\n"
            + "' PUB start\n"
            + "0002C 00008       32             RETURN\n"
            + "0002D 00009       00 00 00       Padding\n"
            + "", compile("main.spin", sources));
    }

    @Test
    void testDuplicatedObjectLink() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "VAR\n"
            + "\n"
            + "    long b\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o1 : \"object1\"\n"
            + "    o2 : \"object1\"\n"
            + "    o3 : \"object2\"\n"
            + "\n"
            + "PUB main | a\n"
            + "\n"
            + "    o1.function(1)\n"
            + "    o2.function(2)\n"
            + "    o3.function(3)\n"
            + "");
        sources.put("object1.spin", ""
            + "VAR\n"
            + "\n"
            + "    long d\n"
            + "    long e\n"
            + "\n"
            + "PUB function(a)\n"
            + "\n"
            + "    return a + d * e\n"
            + "");
        sources.put("object2.spin", ""
            + "VAR\n"
            + "\n"
            + "    long d\n"
            + "\n"
            + "PUB function(a) | b, c\n"
            + "\n"
            + "    return (a + b * c) * d\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 24)\n"
            + "00010 00000       28 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       03             Object count\n"
            + "00014 00004       14 00 04 00    Function main @ $0014 (local size 4)\n"
            + "00018 00008       28 00 04 00    Object \"object1.spin\" @ $0028 (variables @ $0004)\n"
            + "0001C 0000C       28 00 0C 00    Object \"object1.spin\" @ $0028 (variables @ $000C)\n"
            + "00020 00010       38 00 14 00    Object \"object2.spin\" @ $0038 (variables @ $0014)\n"
            + "' PUB main | a\n"
            + "'     o1.function(1)\n"
            + "00024 00014       01             ANCHOR\n"
            + "00025 00015       36             CONSTANT (1)\n"
            + "00026 00016       06 02 01       CALL_OBJ_SUB (2.0)\n"
            + "'     o2.function(2)\n"
            + "00029 00019       01             ANCHOR\n"
            + "0002A 0001A       37 00          CONSTANT (2)\n"
            + "0002C 0001C       06 03 01       CALL_OBJ_SUB (3.0)\n"
            + "'     o3.function(3)\n"
            + "0002F 0001F       01             ANCHOR\n"
            + "00030 00020       37 21          CONSTANT (3)\n"
            + "00032 00022       06 04 01       CALL_OBJ_SUB (4.0)\n"
            + "00035 00025       32             RETURN\n"
            + "00036 00026       00 00          Padding\n"
            + "' Object \"object1.spin\" header (var size 8)\n"
            + "00038 00000       10 00          Object size\n"
            + "0003A 00002       02             Method count + 1\n"
            + "0003B 00003       00             Object count\n"
            + "0003C 00004       08 00 00 00    Function function @ $0008 (local size 0)\n"
            + "' PUB function(a)\n"
            + "'     return a + d * e\n"
            + "00040 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00041 00009       40             VAR_READ LONG VBASE+$0000 (short)\n"
            + "00042 0000A       44             VAR_READ LONG VBASE+$0004 (short)\n"
            + "00043 0000B       F4             MULTIPLY\n"
            + "00044 0000C       EC             ADD\n"
            + "00045 0000D       33             RETURN\n"
            + "00046 0000E       32             RETURN\n"
            + "00047 0000F       00             Padding\n"
            + "' Object \"object2.spin\" header (var size 4)\n"
            + "00048 00000       14 00          Object size\n"
            + "0004A 00002       02             Method count + 1\n"
            + "0004B 00003       00             Object count\n"
            + "0004C 00004       08 00 08 00    Function function @ $0008 (local size 8)\n"
            + "' PUB function(a) | b, c\n"
            + "'     return (a + b * c) * d\n"
            + "00050 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00051 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "00052 0000A       6C             VAR_READ LONG DBASE+$000C (short)\n"
            + "00053 0000B       F4             MULTIPLY\n"
            + "00054 0000C       EC             ADD\n"
            + "00055 0000D       40             VAR_READ LONG VBASE+$0000 (short)\n"
            + "00056 0000E       F4             MULTIPLY\n"
            + "00057 0000F       33             RETURN\n"
            + "00058 00010       32             RETURN\n"
            + "00059 00011       00 00 00       Padding\n"
            + "", compile("main.spin", sources));
    }

    @Test
    void testNestedDuplicatedObjectLink() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "VAR\n"
            + "\n"
            + "    long b\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o1 : \"object1\"\n"
            + "    o2 : \"object1\"\n"
            + "    o3 : \"object2\"\n"
            + "\n"
            + "PUB main | a\n"
            + "\n"
            + "    o1.function(1)\n"
            + "    o2.function(2)\n"
            + "    o3.function(3)\n"
            + "");
        sources.put("object1.spin", ""
            + "VAR\n"
            + "\n"
            + "    long d\n"
            + "    long e\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o3 : \"object2\"\n"
            + "\n"
            + "PUB function(a)\n"
            + "\n"
            + "    e := o3.function(3)\n"
            + "    return a + d * e\n"
            + "");
        sources.put("object2.spin", ""
            + "VAR\n"
            + "\n"
            + "    long d\n"
            + "\n"
            + "PUB function(a) | b, c\n"
            + "\n"
            + "    return (a + b * c) * d\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 32)\n"
            + "00010 00000       28 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       03             Object count\n"
            + "00014 00004       14 00 04 00    Function main @ $0014 (local size 4)\n"
            + "00018 00008       28 00 04 00    Object \"object1.spin\" @ $0028 (variables @ $0004)\n"
            + "0001C 0000C       28 00 10 00    Object \"object1.spin\" @ $0028 (variables @ $0010)\n"
            + "00020 00010       44 00 1C 00    Object \"object2.spin\" @ $0044 (variables @ $001C)\n"
            + "' PUB main | a\n"
            + "'     o1.function(1)\n"
            + "00024 00014       01             ANCHOR\n"
            + "00025 00015       36             CONSTANT (1)\n"
            + "00026 00016       06 02 01       CALL_OBJ_SUB (2.0)\n"
            + "'     o2.function(2)\n"
            + "00029 00019       01             ANCHOR\n"
            + "0002A 0001A       37 00          CONSTANT (2)\n"
            + "0002C 0001C       06 03 01       CALL_OBJ_SUB (3.0)\n"
            + "'     o3.function(3)\n"
            + "0002F 0001F       01             ANCHOR\n"
            + "00030 00020       37 21          CONSTANT (3)\n"
            + "00032 00022       06 04 01       CALL_OBJ_SUB (4.0)\n"
            + "00035 00025       32             RETURN\n"
            + "00036 00026       00 00          Padding\n"
            + "' Object \"object1.spin\" header (var size 12)\n"
            + "00038 00000       1C 00          Object size\n"
            + "0003A 00002       02             Method count + 1\n"
            + "0003B 00003       01             Object count\n"
            + "0003C 00004       0C 00 00 00    Function function @ $000C (local size 0)\n"
            + "00040 00008       1C 00 08 00    Object \"object2.spin\" @ $001C (variables @ $0008)\n"
            + "' PUB function(a)\n"
            + "'     e := o3.function(3)\n"
            + "00044 0000C       00             ANCHOR\n"
            + "00045 0000D       37 21          CONSTANT (3)\n"
            + "00047 0000F       06 02 01       CALL_OBJ_SUB (2.0)\n"
            + "0004A 00012       45             VAR_WRITE LONG VBASE+$0004 (short)\n"
            + "'     return a + d * e\n"
            + "0004B 00013       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0004C 00014       40             VAR_READ LONG VBASE+$0000 (short)\n"
            + "0004D 00015       44             VAR_READ LONG VBASE+$0004 (short)\n"
            + "0004E 00016       F4             MULTIPLY\n"
            + "0004F 00017       EC             ADD\n"
            + "00050 00018       33             RETURN\n"
            + "00051 00019       32             RETURN\n"
            + "00052 0001A       00 00          Padding\n"
            + "' Object \"object2.spin\" header (var size 4)\n"
            + "00054 00000       14 00          Object size\n"
            + "00056 00002       02             Method count + 1\n"
            + "00057 00003       00             Object count\n"
            + "00058 00004       08 00 08 00    Function function @ $0008 (local size 8)\n"
            + "' PUB function(a) | b, c\n"
            + "'     return (a + b * c) * d\n"
            + "0005C 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0005D 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0005E 0000A       6C             VAR_READ LONG DBASE+$000C (short)\n"
            + "0005F 0000B       F4             MULTIPLY\n"
            + "00060 0000C       EC             ADD\n"
            + "00061 0000D       40             VAR_READ LONG VBASE+$0000 (short)\n"
            + "00062 0000E       F4             MULTIPLY\n"
            + "00063 0000F       33             RETURN\n"
            + "00064 00010       32             RETURN\n"
            + "00065 00011       00 00 00       Padding\n"
            + "", compile("main.spin", sources));
    }

    @Test
    void testNestedChildDuplicatedObjectLink() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "VAR\n"
            + "\n"
            + "    long b\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o1 : \"object1\"\n"
            + "    o2 : \"object2\"\n"
            + "\n"
            + "PUB main | a\n"
            + "\n"
            + "    o1.function(1)\n"
            + "    o2.function(2)\n"
            + "");
        sources.put("object1.spin", ""
            + "VAR\n"
            + "\n"
            + "    long d\n"
            + "    long e\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o3 : \"object3\"\n"
            + "\n"
            + "PUB function(a) : r\n"
            + "\n"
            + "    e := o3.function(4)\n"
            + "    return a + d * e\n"
            + "");
        sources.put("object2.spin", ""
            + "VAR\n"
            + "\n"
            + "    long d\n"
            + "    long e\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o3 : \"object3\"\n"
            + "\n"
            + "PUB function(a) : r | b, c\n"
            + "\n"
            + "    e := o3.function(5)\n"
            + "    return a + d / e\n"
            + "");
        sources.put("object3.spin", ""
            + "VAR\n"
            + "\n"
            + "    long d\n"
            + "\n"
            + "PUB function(a) : r | b, c\n"
            + "\n"
            + "    return (a + b * c) * d\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 28)\n"
            + "00010 00000       1C 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       02             Object count\n"
            + "00014 00004       10 00 04 00    Function main @ $0010 (local size 4)\n"
            + "00018 00008       1C 00 04 00    Object \"object1.spin\" @ $001C (variables @ $0004)\n"
            + "0001C 0000C       38 00 10 00    Object \"object2.spin\" @ $0038 (variables @ $0010)\n"
            + "' PUB main | a\n"
            + "'     o1.function(1)\n"
            + "00020 00010       01             ANCHOR\n"
            + "00021 00011       36             CONSTANT (1)\n"
            + "00022 00012       06 02 01       CALL_OBJ_SUB (2.0)\n"
            + "'     o2.function(2)\n"
            + "00025 00015       01             ANCHOR\n"
            + "00026 00016       37 00          CONSTANT (2)\n"
            + "00028 00018       06 03 01       CALL_OBJ_SUB (3.0)\n"
            + "0002B 0001B       32             RETURN\n"
            + "' Object \"object1.spin\" header (var size 12)\n"
            + "0002C 00000       1C 00          Object size\n"
            + "0002E 00002       02             Method count + 1\n"
            + "0002F 00003       01             Object count\n"
            + "00030 00004       0C 00 00 00    Function function @ $000C (local size 0)\n"
            + "00034 00008       38 00 08 00    Object \"object3.spin\" @ $0038 (variables @ $0008)\n"
            + "' PUB function(a) : r\n"
            + "'     e := o3.function(4)\n"
            + "00038 0000C       00             ANCHOR\n"
            + "00039 0000D       37 01          CONSTANT (4)\n"
            + "0003B 0000F       06 02 01       CALL_OBJ_SUB (2.0)\n"
            + "0003E 00012       45             VAR_WRITE LONG VBASE+$0004 (short)\n"
            + "'     return a + d * e\n"
            + "0003F 00013       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00040 00014       40             VAR_READ LONG VBASE+$0000 (short)\n"
            + "00041 00015       44             VAR_READ LONG VBASE+$0004 (short)\n"
            + "00042 00016       F4             MULTIPLY\n"
            + "00043 00017       EC             ADD\n"
            + "00044 00018       33             RETURN\n"
            + "00045 00019       32             RETURN\n"
            + "00046 0001A       00 00          Padding\n"
            + "' Object \"object2.spin\" header (var size 12)\n"
            + "00048 00000       1C 00          Object size\n"
            + "0004A 00002       02             Method count + 1\n"
            + "0004B 00003       01             Object count\n"
            + "0004C 00004       0C 00 08 00    Function function @ $000C (local size 8)\n"
            + "00050 00008       1C 00 08 00    Object \"object3.spin\" @ $001C (variables @ $0008)\n"
            + "' PUB function(a) : r | b, c\n"
            + "'     e := o3.function(5)\n"
            + "00054 0000C       00             ANCHOR\n"
            + "00055 0000D       38 05          CONSTANT (5)\n"
            + "00057 0000F       06 02 01       CALL_OBJ_SUB (2.0)\n"
            + "0005A 00012       45             VAR_WRITE LONG VBASE+$0004 (short)\n"
            + "'     return a + d / e\n"
            + "0005B 00013       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0005C 00014       40             VAR_READ LONG VBASE+$0000 (short)\n"
            + "0005D 00015       44             VAR_READ LONG VBASE+$0004 (short)\n"
            + "0005E 00016       F6             DIVIDE\n"
            + "0005F 00017       EC             ADD\n"
            + "00060 00018       33             RETURN\n"
            + "00061 00019       32             RETURN\n"
            + "00062 0001A       00 00          Padding\n"
            + "' Object \"object3.spin\" header (var size 4)\n"
            + "00064 00000       14 00          Object size\n"
            + "00066 00002       02             Method count + 1\n"
            + "00067 00003       00             Object count\n"
            + "00068 00004       08 00 08 00    Function function @ $0008 (local size 8)\n"
            + "' PUB function(a) : r | b, c\n"
            + "'     return (a + b * c) * d\n"
            + "0006C 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0006D 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0006E 0000A       6C             VAR_READ LONG DBASE+$000C (short)\n"
            + "0006F 0000B       F4             MULTIPLY\n"
            + "00070 0000C       EC             ADD\n"
            + "00071 0000D       40             VAR_READ LONG VBASE+$0000 (short)\n"
            + "00072 0000E       F4             MULTIPLY\n"
            + "00073 0000F       33             RETURN\n"
            + "00074 00010       32             RETURN\n"
            + "00075 00011       00 00 00       Padding\n"
            + "", compile("main.spin", sources));
    }

    @Test
    void testCircularReference1() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o1 : \"object1\"\n"
            + "    o2 : \"object2\"\n"
            + "\n"
            + "");
        sources.put("object1.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o3 : \"object2\"\n"
            + "\n"
            + "");
        sources.put("object2.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o4 : \"main\"\n"
            + "\n"
            + "");

        Assertions.assertThrows(CompilerException.class, () -> {
            compile("main.spin", sources);
        });
    }

    @Test
    void testCircularReference2() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o1 : \"object1\"\n"
            + "\n"
            + "");
        sources.put("object1.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o3 : \"object2\"\n"
            + "\n"
            + "");
        sources.put("object2.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o4 : \"main\"\n"
            + "\n"
            + "");

        Assertions.assertThrows(CompilerException.class, () -> {
            compile("main.spin", sources);
        });
    }

    @Test
    void testRemoveUnusedMethods() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "PUB main | a\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 0)\n"
            + "00010 00000       0C 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       00             Object count\n"
            + "00014 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' PUB main | a\n"
            + "'     a := 1\n"
            + "00018 00008       36             CONSTANT (1)\n"
            + "00019 00009       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0001A 0000A       32             RETURN\n"
            + "0001B 0000B       00             Padding\n"
            + "", compile("main.spin", sources, true));
    }

    @Test
    void testRemoveMiddleUnusedMethods() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "PUB main | a\n"
            + "\n"
            + "    a := 1\n"
            + "    start(1, 2)\n"
            + "\n"
            + "PUB stop | c\n"
            + "\n"
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 0)\n"
            + "00010 00000       1C 00          Object size\n"
            + "00012 00002       03             Method count + 1\n"
            + "00013 00003       00             Object count\n"
            + "00014 00004       0C 00 04 00    Function main @ $000C (local size 4)\n"
            + "00018 00008       15 00 04 00    Function start @ $0015 (local size 4)\n"
            + "' PUB main | a\n"
            + "'     a := 1\n"
            + "0001C 0000C       36             CONSTANT (1)\n"
            + "0001D 0000D       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     start(1, 2)\n"
            + "0001E 0000E       01             ANCHOR\n"
            + "0001F 0000F       36             CONSTANT (1)\n"
            + "00020 00010       37 00          CONSTANT (2)\n"
            + "00022 00012       05 02          CALL_SUB (1)\n"
            + "00024 00014       32             RETURN\n"
            + "' PUB start(a, b) | c\n"
            + "'     c := a + b\n"
            + "00025 00015       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00026 00016       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "00027 00017       EC             ADD\n"
            + "00028 00018       6D             VAR_WRITE LONG DBASE+$000C (short)\n"
            + "00029 00019       32             RETURN\n"
            + "0002A 0001A       00 00          Padding\n"
            + "", compile("main.spin", sources, true));
    }

    @Test
    void testUnusedMethodsCauseErrors() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "PUB main | a\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "PUB start(a, b)\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile("main.spin", sources, true);
            }

        });
    }

    @Test
    void testObjectUnusedMethodCall() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main | a\n"
            + "\n"
            + "    o.start(1, 2)\n"
            + "    o.method2\n"
            + "\n"
            + "");
        sources.put("text2.spin", ""
            + "PUB start(a, b)\n"
            + "\n"
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 0)\n"
            + "00010 00000       18 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       01             Object count\n"
            + "00014 00004       0C 00 04 00    Function main @ $000C (local size 4)\n"
            + "00018 00008       18 00 00 00    Object \"text2.spin\" @ $0018 (variables @ $0000)\n"
            + "' PUB main | a\n"
            + "'     o.start(1, 2)\n"
            + "0001C 0000C       01             ANCHOR\n"
            + "0001D 0000D       36             CONSTANT (1)\n"
            + "0001E 0000E       37 00          CONSTANT (2)\n"
            + "00020 00010       06 02 01       CALL_OBJ_SUB (2.0)\n"
            + "'     o.method2\n"
            + "00023 00013       01             ANCHOR\n"
            + "00024 00014       06 02 02       CALL_OBJ_SUB (2.1)\n"
            + "00027 00017       32             RETURN\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
            + "00028 00000       10 00          Object size\n"
            + "0002A 00002       03             Method count + 1\n"
            + "0002B 00003       00             Object count\n"
            + "0002C 00004       0C 00 00 00    Function start @ $000C (local size 0)\n"
            + "00030 00008       0D 00 00 00    Function method2 @ $000D (local size 0)\n"
            + "' PUB start(a, b)\n"
            + "00034 0000C       32             RETURN\n"
            + "' PUB method2\n"
            + "00035 0000D       32             RETURN\n"
            + "00036 0000E       00 00          Padding\n"
            + "", compile("main.spin", sources, true));
    }

    @Test
    void testObjectArray() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o[2] : \"text2\"\n"
            + "\n"
            + "PUB main\n"
            + "\n"
            + "");
        sources.put("text2.spin", ""
            + "PUB start\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 0)\n"
            + "00010 00000       14 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       02             Object count\n"
            + "00014 00004       10 00 00 00    Function main @ $0010 (local size 0)\n"
            + "00018 00008       14 00 00 00    Object \"text2.spin\" @ $0014 (variables @ $0000)\n"
            + "0001C 0000C       14 00 00 00    Object \"text2.spin\" @ $0014 (variables @ $0000)\n"
            + "' PUB main\n"
            + "00020 00010       32             RETURN\n"
            + "00021 00011       00 00 00       Padding\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
            + "00024 00000       0C 00          Object size\n"
            + "00026 00002       02             Method count + 1\n"
            + "00027 00003       00             Object count\n"
            + "00028 00004       08 00 00 00    Function start @ $0008 (local size 0)\n"
            + "' PUB start\n"
            + "0002C 00008       32             RETURN\n"
            + "0002D 00009       00 00 00       Padding\n"
            + "", compile("main.spin", sources));
    }

    @Test
    void testObjectArrayExpression() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "CON\n"
            + "\n"
            + "    NUMBER = 2\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o[NUMBER + 1] : \"text2\"\n"
            + "\n"
            + "PUB main\n"
            + "\n"
            + "");
        sources.put("text2.spin", ""
            + "PUB start\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 0)\n"
            + "00010 00000       18 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       03             Object count\n"
            + "00014 00004       14 00 00 00    Function main @ $0014 (local size 0)\n"
            + "00018 00008       18 00 00 00    Object \"text2.spin\" @ $0018 (variables @ $0000)\n"
            + "0001C 0000C       18 00 00 00    Object \"text2.spin\" @ $0018 (variables @ $0000)\n"
            + "00020 00010       18 00 00 00    Object \"text2.spin\" @ $0018 (variables @ $0000)\n"
            + "' PUB main\n"
            + "00024 00014       32             RETURN\n"
            + "00025 00015       00 00 00       Padding\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
            + "00028 00000       0C 00          Object size\n"
            + "0002A 00002       02             Method count + 1\n"
            + "0002B 00003       00             Object count\n"
            + "0002C 00004       08 00 00 00    Function start @ $0008 (local size 0)\n"
            + "' PUB start\n"
            + "00030 00008       32             RETURN\n"
            + "00031 00009       00 00 00       Padding\n"
            + "", compile("main.spin", sources));
    }

    @Test
    void testObjectArrayMethodCall() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o[2] : \"text2\"\n"
            + "\n"
            + "PUB main\n"
            + "\n"
            + "    o[0].start(1, 2)\n"
            + "    \\o[1].start(3, 4)\n"
            + "\n"
            + "");
        sources.put("text2.spin", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 0)\n"
            + "00010 00000       24 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       02             Object count\n"
            + "00014 00004       10 00 00 00    Function main @ $0010 (local size 0)\n"
            + "00018 00008       24 00 00 00    Object \"text2.spin\" @ $0024 (variables @ $0000)\n"
            + "0001C 0000C       24 00 00 00    Object \"text2.spin\" @ $0024 (variables @ $0000)\n"
            + "' PUB main\n"
            + "'     o[0].start(1, 2)\n"
            + "00020 00010       01             ANCHOR\n"
            + "00021 00011       36             CONSTANT (1)\n"
            + "00022 00012       37 00          CONSTANT (2)\n"
            + "00024 00014       35             CONSTANT (0)\n"
            + "00025 00015       07 02 01       CALL_OBJ_SUB (2.0) (indexed)\n"
            + "'     \\o[1].start(3, 4)\n"
            + "00028 00018       03             ANCHOR (TRY)\n"
            + "00029 00019       37 21          CONSTANT (3)\n"
            + "0002B 0001B       37 01          CONSTANT (4)\n"
            + "0002D 0001D       36             CONSTANT (1)\n"
            + "0002E 0001E       07 02 01       CALL_OBJ_SUB (2.0) (indexed)\n"
            + "00031 00021       32             RETURN\n"
            + "00032 00022       00 00          Padding\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
            + "00034 00000       10 00          Object size\n"
            + "00036 00002       02             Method count + 1\n"
            + "00037 00003       00             Object count\n"
            + "00038 00004       08 00 04 00    Function start @ $0008 (local size 4)\n"
            + "' PUB start(a, b) | c\n"
            + "'     c := a + b\n"
            + "0003C 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0003D 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0003E 0000A       EC             ADD\n"
            + "0003F 0000B       6D             VAR_WRITE LONG DBASE+$000C (short)\n"
            + "00040 0000C       32             RETURN\n"
            + "00041 0000D       00 00 00       Padding\n"
            + "", compile("main.spin", sources));
    }

    @Test
    void testDatInclude() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "PUB main\n"
            + "\n"
            + "DAT\n"
            + "    org $000\n"
            + "    call    #label\n"
            + "    jmp     #$\n"
            + "    include \"text2\"\n"
            + "a   long    0\n"
            + "");
        sources.put("text2.spin", ""
            + "DAT\n"
            + "\n"
            + "label\n"
            + "          mov a, #1\n"
            + "label_ret ret\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 0)\n"
            + "00010 00000       20 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       00             Object count\n"
            + "00014 00004       1C 00 00 00    Function main @ $001C (local size 0)\n"
            + "00018 00008   000                                    org     $000\n"
            + "00018 00008   000 02 06 FC 5C                        call    #label\n"
            + "0001C 0000C   001 01 00 7C 5C                        jmp     #$\n"
            + "00020 00010   002                                    include \"text2\"\n"
            + "00020 00010   002                label               \n"
            + "00020 00010   002 01 08 FC A0                        mov     a, #1\n"
            + "00024 00014   003 00 00 7C 5C    label_ret           ret\n"
            + "00028 00018   004 00 00 00 00    a                   long    0\n"
            + "' PUB main\n"
            + "0002C 0001C       32             RETURN\n"
            + "0002D 0001D       00 00 00       Padding\n"
            + "", compile("main.spin", sources));
    }

    @Test
    void testPreprocessorDefineInheritance() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "#define VALUE 2\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main | a\n"
            + "\n"
            + "    a := o.start()\n"
            + "\n"
            + "");
        sources.put("text2.spin", ""
            + "PUB start : r\n"
            + "\n"
            + "    r := VALUE\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 0)\n"
            + "00010 00000       14 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       01             Object count\n"
            + "00014 00004       0C 00 04 00    Function main @ $000C (local size 4)\n"
            + "00018 00008       14 00 00 00    Object \"text2.spin\" @ $0014 (variables @ $0000)\n"
            + "' PUB main | a\n"
            + "'     a := o.start()\n"
            + "0001C 0000C       00             ANCHOR\n"
            + "0001D 0000D       06 02 01       CALL_OBJ_SUB (2.0)\n"
            + "00020 00010       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00021 00011       32             RETURN\n"
            + "00022 00012       00 00          Padding\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
            + "00024 00000       0C 00          Object size\n"
            + "00026 00002       02             Method count + 1\n"
            + "00027 00003       00             Object count\n"
            + "00028 00004       08 00 00 00    Function start @ $0008 (local size 0)\n"
            + "' PUB start : r\n"
            + "'     r := VALUE\n"
            + "0002C 00008       37 00          CONSTANT (2)\n"
            + "0002E 0000A       61             VAR_WRITE LONG DBASE+$0000 (short)\n"
            + "0002F 0000B       32             RETURN\n"
            + "", compile("main.spin", sources));
    }

    @Test
    void testUnusedMethodsStringData() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "PUB main | a\n"
            + "\n"
            + "    a := string(\"1-main\")\n"
            + "    used\n"
            + "\n"
            + "PUB unused | c\n"
            + "\n"
            + "    c := string(\"2-unused\")\n"
            + "\n"
            + "PUB used | c\n"
            + "\n"
            + "    c := string(\"3-used\")\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 0)\n"
            + "00010 00000       28 00          Object size\n"
            + "00012 00002       03             Method count + 1\n"
            + "00013 00003       00             Object count\n"
            + "00014 00004       0C 00 04 00    Function main @ $000C (local size 4)\n"
            + "00018 00008       1B 00 04 00    Function used @ $001B (local size 4)\n"
            + "' PUB main | a\n"
            + "'     a := string(\"1-main\")\n"
            + "0001C 0000C       87 80 14       MEM_ADDRESS BYTE PBASE+$0014\n"
            + "0001F 0000F       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     used\n"
            + "00020 00010       01             ANCHOR\n"
            + "00021 00011       05 02          CALL_SUB (1)\n"
            + "00023 00013       32             RETURN\n"
            + "' (string data)\n"
            + "00024 00014       31 2D 6D 61 69 STRING\n"
            + "00029 00019       6E 00\n"
            + "' PUB used | c\n"
            + "'     c := string(\"3-used\")\n"
            + "0002B 0001B       87 80 20       MEM_ADDRESS BYTE PBASE+$0020\n"
            + "0002E 0001E       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0002F 0001F       32             RETURN\n"
            + "' (string data)\n"
            + "00030 00020       33 2D 75 73 65 STRING\n"
            + "00035 00025       64 00\n"
            + "00037 00027       00             Padding\n"
            + "", compile("main.spin", sources, true));
    }

    @Test
    void testChildObjectPAsmAddress() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main\n"
            + "\n"
            + "");
        sources.put("text2.spin", ""
            + "VAR\n"
            + "    long a\n"
            + "\n"
            + "PUB main | ptr, b\n"
            + "\n"
            + "    ptr := @a\n"
            + "    ptr := @@a\n"
            + "\n"
            + "    ptr := @b\n"
            + "    ptr := @@b\n"
            + "\n"
            + "    ptr := @table\n"
            + "    ptr := @@table\n"
            + "    ptr := @@table[a]\n"
            + "    ptr := @@@table\n"
            + "\n"
            + "DAT\n"
            + "        org     $000\n"
            + "\n"
            + "table   long    0\n"
            + "");

        Assertions.assertEquals(""
            + "' Object \"main.spin\" header (var size 4)\n"
            + "00010 00000       10 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       01             Object count\n"
            + "00014 00004       0C 00 00 00    Function main @ $000C (local size 0)\n"
            + "00018 00008       10 00 00 00    Object \"text2.spin\" @ $0010 (variables @ $0000)\n"
            + "' PUB main\n"
            + "0001C 0000C       32             RETURN\n"
            + "0001D 0000D       00 00 00       Padding\n"
            + "' Object \"text2.spin\" header (var size 4)\n"
            + "00020 00000       2C 00          Object size\n"
            + "00022 00002       02             Method count + 1\n"
            + "00023 00003       00             Object count\n"
            + "00024 00004       0C 00 08 00    Function main @ $000C (local size 8)\n"
            + "00028 00008   000                                    org     $000\n"
            + "00028 00008   000 00 00 00 00    table               long    0\n"
            + "' PUB main | ptr, b\n"
            + "'     ptr := @a\n"
            + "0002C 0000C       43             VAR_ADDRESS VBASE+$0000 (short)\n"
            + "0002D 0000D       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     ptr := @@a\n"
            + "0002E 0000E       40             VAR_READ LONG VBASE+$0000 (short)\n"
            + "0002F 0000F       97 00          MEM_ADDRESS_INDEXED BYTE PBASE+$0000\n"
            + "00031 00011       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     ptr := @b\n"
            + "00032 00012       6B             VAR_ADDRESS DBASE+$0008 (short)\n"
            + "00033 00013       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     ptr := @@b\n"
            + "00034 00014       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "00035 00015       97 00          MEM_ADDRESS_INDEXED BYTE PBASE+$0000\n"
            + "00037 00017       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     ptr := @table\n"
            + "00038 00018       C7 08          MEM_ADDRESS LONG PBASE+$0008\n"
            + "0003A 0001A       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     ptr := @@table\n"
            + "0003B 0001B       C4 08          MEM_READ LONG PBASE+$0008\n"
            + "0003D 0001D       97 00          MEM_ADDRESS_INDEXED BYTE PBASE+$0000\n"
            + "0003F 0001F       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     ptr := @@table[a]\n"
            + "00040 00020       40             VAR_READ LONG VBASE+$0000 (short)\n"
            + "00041 00021       D4 08          MEM_READ_INDEXED LONG PBASE+$0008\n"
            + "00043 00023       97 00          MEM_ADDRESS_INDEXED BYTE PBASE+$0000\n"
            + "00045 00025       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     ptr := @@@table\n"
            + "00046 00026       38 28          CONSTANT ($0028)\n"
            + "00048 00028       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00049 00029       32             RETURN\n"
            + "0004A 0002A       00 00          Padding\n"
            + "", compile("main.spin", sources));
    }

    String compile(String rootFile, Map<String, String> sources) throws Exception {
        return compile(rootFile, sources, false);
    }

    String compile(String rootFile, Map<String, String> sources, boolean removeUnused) throws Exception {
        Spin1Parser subject = new Spin1Parser(sources.get(rootFile));
        RootNode root = subject.parse();

        Spin1Compiler compiler = new Spin1Compiler();
        compiler.setSourceProvider(new SourceProvider() {

            @Override
            public File getFile(String name) {
                if (sources.containsKey(name)) {
                    return new File(name);
                }
                return null;
            }

            @Override
            public RootNode getParsedSource(File file) {
                String text = sources.get(file.getName());
                if (text == null) {
                    return null;
                }
                String suffix = file.getName().substring(file.getName().lastIndexOf('.'));
                Parser parser = Parser.getInstance(suffix, text);
                return parser.parse();
            }

        });
        compiler.setRemoveUnusedMethods(removeUnused);
        Spin1Object obj = compiler.compileObject(new File(rootFile), root);

        for (CompilerException msg : compiler.getMessages()) {
            if (msg.type == CompilerException.ERROR) {
                throw msg;
            }
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateListing(16, new PrintStream(os));

        return os.toString().replaceAll("\\r\\n", "\n");
    }

}
