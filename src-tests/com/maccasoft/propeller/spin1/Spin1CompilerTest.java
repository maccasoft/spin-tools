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
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.model.Node;

class Spin1CompilerTest {

    @AfterEach
    void afterEach() {
        Spin1ObjectCompiler.OPENSPIN_COMPATIBILITY = false;
    }

    @Test
    void testObjectLink() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main() | a\n"
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
            + "' Object header (var size 0)\n"
            + "00000 00000       10 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       01             Object count\n"
            + "00004 00004       0C 00 04 00    Function main @ $000C (local size 4)\n"
            + "00008 00008       10 00 00 00    Object \"text2.spin\" @ $0010 (variables @ $0000)\n"
            + "' PUB main() | a\n"
            + "'     a := 1\n"
            + "0000C 0000C       36             CONSTANT (1)\n"
            + "0000D 0000D       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0000E 0000E       32             RETURN\n"
            + "0000F 0000F       00             Padding\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
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
            + "PUB main() | a\n"
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
            + "' Object header (var size 4)\n"
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       02             Object count\n"
            + "00004 00004       10 00 04 00    Function main @ $0010 (local size 4)\n"
            + "00008 00008       14 00 04 00    Object \"text1.spin\" @ $0014 (variables @ $0004)\n"
            + "0000C 0000C       24 00 0C 00    Object \"text2.spin\" @ $0024 (variables @ $000C)\n"
            + "' PUB main() | a\n"
            + "'     a := 1\n"
            + "00010 00010       36             CONSTANT (1)\n"
            + "00011 00011       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00012 00012       32             RETURN\n"
            + "00013 00013       00             Padding\n"
            + "' Object \"text1.spin\" header (var size 8)\n"
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
            + "' Object \"text2.spin\" header (var size 16)\n"
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
            + "", compile("main.spin", sources));
    }

    @Test
    void testObjectMethodCall() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main() | a\n"
            + "\n"
            + "    o.start(1, 2)\n"
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
            + "' Object header (var size 0)\n"
            + "00000 00000       20 00          Object size\n"
            + "00002 00002       04             Method count + 1\n"
            + "00003 00003       01             Object count\n"
            + "00004 00004       14 00 04 00    Function main @ $0014 (local size 4)\n"
            + "00008 00008       1C 00 00 00    Function f1 @ $001C (local size 0)\n"
            + "0000C 0000C       1D 00 00 00    Function f2 @ $001D (local size 0)\n"
            + "00010 00010       20 00 00 00    Object \"text2.spin\" @ $0020 (variables @ $0000)\n"
            + "' PUB main() | a\n"
            + "'     o.start(1, 2)\n"
            + "00014 00014       01             ANCHOR\n"
            + "00015 00015       36             CONSTANT (1)\n"
            + "00016 00016       38 02          CONSTANT (2)\n"
            + "00018 00018       06 04 01       CALL_OBJ_SUB\n"
            + "0001B 0001B       32             RETURN\n"
            + "' PUB f1\n"
            + "0001C 0001C       32             RETURN\n"
            + "' PUB f2\n"
            + "0001D 0001D       32             RETURN\n"
            + "0001E 0001E       00 00          Padding\n"
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
    void testObjectConstant() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main() | a\n"
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
            + "' Object header (var size 0)\n"
            + "00000 00000       10 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       01             Object count\n"
            + "00004 00004       0C 00 04 00    Function main @ $000C (local size 4)\n"
            + "00008 00008       10 00 00 00    Object \"text2.spin\" @ $0010 (variables @ $0000)\n"
            + "' PUB main() | a\n"
            + "'     a := o#CONSTANT\n"
            + "0000C 0000C       36             CONSTANT (1)\n"
            + "0000D 0000D       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "0000E 0000E       32             RETURN\n"
            + "0000F 0000F       00             Padding\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
            + "00010 00000       04 00          Object size\n"
            + "00012 00002       01             Method count + 1\n"
            + "00013 00003       00             Object count\n"
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
            + "' Object header (var size 4)\n"
            + "00000 00000       28 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       03             Object count\n"
            + "00004 00004       14 00 04 00    Function main @ $0014 (local size 4)\n"
            + "00008 00008       28 00 04 00    Object \"object1.spin\" @ $0028 (variables @ $0004)\n"
            + "0000C 0000C       28 00 0C 00    Object \"object1.spin\" @ $0028 (variables @ $000C)\n"
            + "00010 00010       38 00 14 00    Object \"object2.spin\" @ $0038 (variables @ $0014)\n"
            + "' PUB main | a\n"
            + "'     o1.function(1)\n"
            + "00014 00014       01             ANCHOR\n"
            + "00015 00015       36             CONSTANT (1)\n"
            + "00016 00016       06 02 01       CALL_OBJ_SUB\n"
            + "'     o2.function(2)\n"
            + "00019 00019       01             ANCHOR\n"
            + "0001A 0001A       38 02          CONSTANT (2)\n"
            + "0001C 0001C       06 03 01       CALL_OBJ_SUB\n"
            + "'     o3.function(3)\n"
            + "0001F 0001F       01             ANCHOR\n"
            + "00020 00020       38 03          CONSTANT (3)\n"
            + "00022 00022       06 04 01       CALL_OBJ_SUB\n"
            + "00025 00025       32             RETURN\n"
            + "00026 00026       00 00          Padding\n"
            + "' Object \"object1.spin\" header (var size 8)\n"
            + "00028 00000       10 00          Object size\n"
            + "0002A 00002       02             Method count + 1\n"
            + "0002B 00003       00             Object count\n"
            + "0002C 00004       08 00 00 00    Function function @ $0008 (local size 0)\n"
            + "' PUB function(a)\n"
            + "'     return a + d * e\n"
            + "00030 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00031 00009       40             VAR_READ LONG VBASE+$0000 (short)\n"
            + "00032 0000A       44             VAR_READ LONG VBASE+$0004 (short)\n"
            + "00033 0000B       F4             MULTIPLY\n"
            + "00034 0000C       EC             ADD\n"
            + "00035 0000D       33             RETURN\n"
            + "00036 0000E       32             RETURN\n"
            + "00037 0000F       00             Padding\n"
            + "' Object \"object2.spin\" header (var size 4)\n"
            + "00038 00000       14 00          Object size\n"
            + "0003A 00002       02             Method count + 1\n"
            + "0003B 00003       00             Object count\n"
            + "0003C 00004       08 00 08 00    Function function @ $0008 (local size 8)\n"
            + "' PUB function(a) | b, c\n"
            + "'     return (a + b * c) * d\n"
            + "00040 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00041 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "00042 0000A       6C             VAR_READ LONG DBASE+$000C (short)\n"
            + "00043 0000B       F4             MULTIPLY\n"
            + "00044 0000C       EC             ADD\n"
            + "00045 0000D       40             VAR_READ LONG VBASE+$0000 (short)\n"
            + "00046 0000E       F4             MULTIPLY\n"
            + "00047 0000F       33             RETURN\n"
            + "00048 00010       32             RETURN\n"
            + "00049 00011       00 00 00       Padding\n"
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
            + "' Object header (var size 4)\n"
            + "00000 00000       28 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       03             Object count\n"
            + "00004 00004       14 00 04 00    Function main @ $0014 (local size 4)\n"
            + "00008 00008       28 00 04 00    Object \"object1.spin\" @ $0028 (variables @ $0004)\n"
            + "0000C 0000C       28 00 10 00    Object \"object1.spin\" @ $0028 (variables @ $0010)\n"
            + "00010 00010       44 00 1C 00    Object \"object2.spin\" @ $0044 (variables @ $001C)\n"
            + "' PUB main | a\n"
            + "'     o1.function(1)\n"
            + "00014 00014       01             ANCHOR\n"
            + "00015 00015       36             CONSTANT (1)\n"
            + "00016 00016       06 02 01       CALL_OBJ_SUB\n"
            + "'     o2.function(2)\n"
            + "00019 00019       01             ANCHOR\n"
            + "0001A 0001A       38 02          CONSTANT (2)\n"
            + "0001C 0001C       06 03 01       CALL_OBJ_SUB\n"
            + "'     o3.function(3)\n"
            + "0001F 0001F       01             ANCHOR\n"
            + "00020 00020       38 03          CONSTANT (3)\n"
            + "00022 00022       06 04 01       CALL_OBJ_SUB\n"
            + "00025 00025       32             RETURN\n"
            + "00026 00026       00 00          Padding\n"
            + "' Object \"object1.spin\" header (var size 12)\n"
            + "00028 00000       1C 00          Object size\n"
            + "0002A 00002       02             Method count + 1\n"
            + "0002B 00003       01             Object count\n"
            + "0002C 00004       0C 00 00 00    Function function @ $000C (local size 0)\n"
            + "00030 00008       1C 00 08 00    Object \"object2.spin\" @ $001C (variables @ $0008)\n"
            + "' PUB function(a)\n"
            + "'     e := o3.function(3)\n"
            + "00034 0000C       00             ANCHOR\n"
            + "00035 0000D       38 03          CONSTANT (3)\n"
            + "00037 0000F       06 02 01       CALL_OBJ_SUB\n"
            + "0003A 00012       45             VAR_WRITE LONG VBASE+$0004 (short)\n"
            + "'     return a + d * e\n"
            + "0003B 00013       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0003C 00014       40             VAR_READ LONG VBASE+$0000 (short)\n"
            + "0003D 00015       44             VAR_READ LONG VBASE+$0004 (short)\n"
            + "0003E 00016       F4             MULTIPLY\n"
            + "0003F 00017       EC             ADD\n"
            + "00040 00018       33             RETURN\n"
            + "00041 00019       32             RETURN\n"
            + "00042 0001A       00 00          Padding\n"
            + "' Object \"object2.spin\" header (var size 4)\n"
            + "00044 00000       14 00          Object size\n"
            + "00046 00002       02             Method count + 1\n"
            + "00047 00003       00             Object count\n"
            + "00048 00004       08 00 08 00    Function function @ $0008 (local size 8)\n"
            + "' PUB function(a) | b, c\n"
            + "'     return (a + b * c) * d\n"
            + "0004C 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0004D 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0004E 0000A       6C             VAR_READ LONG DBASE+$000C (short)\n"
            + "0004F 0000B       F4             MULTIPLY\n"
            + "00050 0000C       EC             ADD\n"
            + "00051 0000D       40             VAR_READ LONG VBASE+$0000 (short)\n"
            + "00052 0000E       F4             MULTIPLY\n"
            + "00053 0000F       33             RETURN\n"
            + "00054 00010       32             RETURN\n"
            + "00055 00011       00 00 00       Padding\n"
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
            + "' Object header (var size 4)\n"
            + "00000 00000       1C 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       02             Object count\n"
            + "00004 00004       10 00 04 00    Function main @ $0010 (local size 4)\n"
            + "00008 00008       1C 00 04 00    Object \"object1.spin\" @ $001C (variables @ $0004)\n"
            + "0000C 0000C       38 00 10 00    Object \"object2.spin\" @ $0038 (variables @ $0010)\n"
            + "' PUB main | a\n"
            + "'     o1.function(1)\n"
            + "00010 00010       01             ANCHOR\n"
            + "00011 00011       36             CONSTANT (1)\n"
            + "00012 00012       06 02 01       CALL_OBJ_SUB\n"
            + "'     o2.function(2)\n"
            + "00015 00015       01             ANCHOR\n"
            + "00016 00016       38 02          CONSTANT (2)\n"
            + "00018 00018       06 03 01       CALL_OBJ_SUB\n"
            + "0001B 0001B       32             RETURN\n"
            + "' Object \"object1.spin\" header (var size 12)\n"
            + "0001C 00000       1C 00          Object size\n"
            + "0001E 00002       02             Method count + 1\n"
            + "0001F 00003       01             Object count\n"
            + "00020 00004       0C 00 00 00    Function function @ $000C (local size 0)\n"
            + "00024 00008       38 00 08 00    Object \"object3.spin\" @ $0038 (variables @ $0008)\n"
            + "' PUB function(a) : r\n"
            + "'     e := o3.function(4)\n"
            + "00028 0000C       00             ANCHOR\n"
            + "00029 0000D       38 04          CONSTANT (4)\n"
            + "0002B 0000F       06 02 01       CALL_OBJ_SUB\n"
            + "0002E 00012       45             VAR_WRITE LONG VBASE+$0004 (short)\n"
            + "'     return a + d * e\n"
            + "0002F 00013       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00030 00014       40             VAR_READ LONG VBASE+$0000 (short)\n"
            + "00031 00015       44             VAR_READ LONG VBASE+$0004 (short)\n"
            + "00032 00016       F4             MULTIPLY\n"
            + "00033 00017       EC             ADD\n"
            + "00034 00018       33             RETURN\n"
            + "00035 00019       32             RETURN\n"
            + "00036 0001A       00 00          Padding\n"
            + "' Object \"object2.spin\" header (var size 12)\n"
            + "00038 00000       1C 00          Object size\n"
            + "0003A 00002       02             Method count + 1\n"
            + "0003B 00003       01             Object count\n"
            + "0003C 00004       0C 00 08 00    Function function @ $000C (local size 8)\n"
            + "00040 00008       1C 00 08 00    Object \"object3.spin\" @ $001C (variables @ $0008)\n"
            + "' PUB function(a) : r | b, c\n"
            + "'     e := o3.function(5)\n"
            + "00044 0000C       00             ANCHOR\n"
            + "00045 0000D       38 05          CONSTANT (5)\n"
            + "00047 0000F       06 02 01       CALL_OBJ_SUB\n"
            + "0004A 00012       45             VAR_WRITE LONG VBASE+$0004 (short)\n"
            + "'     return a + d / e\n"
            + "0004B 00013       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0004C 00014       40             VAR_READ LONG VBASE+$0000 (short)\n"
            + "0004D 00015       44             VAR_READ LONG VBASE+$0004 (short)\n"
            + "0004E 00016       F6             DIVIDE\n"
            + "0004F 00017       EC             ADD\n"
            + "00050 00018       33             RETURN\n"
            + "00051 00019       32             RETURN\n"
            + "00052 0001A       00 00          Padding\n"
            + "' Object \"object3.spin\" header (var size 4)\n"
            + "00054 00000       14 00          Object size\n"
            + "00056 00002       02             Method count + 1\n"
            + "00057 00003       00             Object count\n"
            + "00058 00004       08 00 08 00    Function function @ $0008 (local size 8)\n"
            + "' PUB function(a) : r | b, c\n"
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
            + "' Object header\n"
            + "00000 00000       1C 00          Object size\n"
            + "00002 00002       03             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       0C 00 04 00    Function main @ $000C (local size 4)\n"
            + "00008 00008       15 00 04 00    Function start @ $0015 (local size 4)\n"
            + "' PUB main | a\n"
            + "'     a := 1\n"
            + "0000C 0000C       36             CONSTANT (1)\n"
            + "0000D 0000D       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     start(1, 2)\n"
            + "0000E 0000E       01             ANCHOR\n"
            + "0000F 0000F       36             CONSTANT (1)\n"
            + "00010 00010       38 02          CONSTANT (2)\n"
            + "00012 00012       05 02          CALL_SUB\n"
            + "00014 00014       32             RETURN\n"
            + "' PUB start(a, b) | c\n"
            + "'     c := a + b\n"
            + "00015 00015       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00016 00016       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "00017 00017       EC             ADD\n"
            + "00018 00018       6D             VAR_WRITE LONG DBASE+$000C (short)\n"
            + "00019 00019       32             RETURN\n"
            + "0001A 0001A       00 00          Padding\n"
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
            + "PUB main() | a\n"
            + "\n"
            + "    o.start(1, 2)\n"
            + "    o.method2()\n"
            + "\n"
            + "");
        sources.put("text2.spin", ""
            + "PUB start(a, b)\n"
            + "\n"
            + "PUB method1()\n"
            + "\n"
            + "PUB method2()\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       18 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       01             Object count\n"
            + "00004 00004       0C 00 04 00    Function main @ $000C (local size 4)\n"
            + "00008 00008       18 00 00 00    Object \"text2.spin\" @ $0018 (variables @ $0000)\n"
            + "' PUB main() | a\n"
            + "'     o.start(1, 2)\n"
            + "0000C 0000C       01             ANCHOR\n"
            + "0000D 0000D       36             CONSTANT (1)\n"
            + "0000E 0000E       38 02          CONSTANT (2)\n"
            + "00010 00010       06 02 01       CALL_OBJ_SUB\n"
            + "'     o.method2()\n"
            + "00013 00013       01             ANCHOR\n"
            + "00014 00014       06 02 02       CALL_OBJ_SUB\n"
            + "00017 00017       32             RETURN\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
            + "00018 00000       10 00          Object size\n"
            + "0001A 00002       03             Method count + 1\n"
            + "0001B 00003       00             Object count\n"
            + "0001C 00004       0C 00 00 00    Function start @ $000C (local size 0)\n"
            + "00020 00008       0D 00 00 00    Function method2 @ $000D (local size 0)\n"
            + "' PUB start(a, b)\n"
            + "00024 0000C       32             RETURN\n"
            + "' PUB method2()\n"
            + "00025 0000D       32             RETURN\n"
            + "00026 0000E       00 00          Padding\n"
            + "", compile("main.spin", sources, true));
    }

    @Test
    void testObjectArrayLink() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin", ""
            + "OBJ\n"
            + "\n"
            + "    o[2] : \"text2\"\n"
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
            + "' Object header (var size 0)\n"
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       02             Object count\n"
            + "00004 00004       10 00 04 00    Function main @ $0010 (local size 4)\n"
            + "00008 00008       14 00 00 00    Object \"text2.spin\" @ $0014 (variables @ $0000)\n"
            + "0000C 0000C       14 00 00 00    Object \"text2.spin\" @ $0014 (variables @ $0000)\n"
            + "' PUB main | a\n"
            + "'     a := 1\n"
            + "00010 00010       36             CONSTANT (1)\n"
            + "00011 00011       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "00012 00012       32             RETURN\n"
            + "00013 00013       00             Padding\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
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
            + "    o[1].start(3, 4)\n"
            + "\n"
            + "");
        sources.put("text2.spin", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       24 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       02             Object count\n"
            + "00004 00004       10 00 00 00    Function main @ $0010 (local size 0)\n"
            + "00008 00008       24 00 00 00    Object \"text2.spin\" @ $0024 (variables @ $0000)\n"
            + "0000C 0000C       24 00 00 00    Object \"text2.spin\" @ $0024 (variables @ $0000)\n"
            + "' PUB main\n"
            + "'     o[0].start(1, 2)\n"
            + "00010 00010       01             ANCHOR\n"
            + "00011 00011       36             CONSTANT (1)\n"
            + "00012 00012       38 02          CONSTANT (2)\n"
            + "00014 00014       35             CONSTANT (0)\n"
            + "00015 00015       07 02 01       CALL_OBJ_SUB\n"
            + "'     o[1].start(3, 4)\n"
            + "00018 00018       01             ANCHOR\n"
            + "00019 00019       38 03          CONSTANT (3)\n"
            + "0001B 0001B       38 04          CONSTANT (4)\n"
            + "0001D 0001D       36             CONSTANT (1)\n"
            + "0001E 0001E       07 02 01       CALL_OBJ_SUB\n"
            + "00021 00021       32             RETURN\n"
            + "00022 00022       00 00          Padding\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
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
            + "", compile("main.spin", sources));
    }

    class Spin1CompilerAdapter extends Spin1Compiler {

        Map<String, String> sources;

        public Spin1CompilerAdapter(Map<String, String> sources) {
            this.sources = sources;
        }

        @Override
        protected Node getParsedObject(String fileName) {
            String text = sources.get(fileName);
            if (text == null) {
                return null;
            }
            Spin1TokenStream stream = new Spin1TokenStream(text);
            Spin1Parser subject = new Spin1Parser(stream);
            return subject.parse();
        }

    }

    String compile(String rootFile, Map<String, String> sources) throws Exception {
        return compile(rootFile, sources, false);
    }

    String compile(String rootFile, Map<String, String> sources, boolean removeUnused) throws Exception {
        Spin1TokenStream stream = new Spin1TokenStream(sources.get(rootFile));
        Spin1Parser subject = new Spin1Parser(stream);
        Node root = subject.parse();

        Spin1Compiler compiler = new Spin1CompilerAdapter(sources);
        compiler.setRemoveUnusedMethods(removeUnused);
        Spin1Object obj = compiler.compileObject(rootFile, root);

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
