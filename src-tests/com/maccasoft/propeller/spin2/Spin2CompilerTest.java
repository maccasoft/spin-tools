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
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.CompilerMessage;
import com.maccasoft.propeller.model.Node;

class Spin2CompilerTest {

    @Test
    void testObjectLink() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin2", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main() | a\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "");
        sources.put("text2.spin2", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       14 00 00 00    Object \"text2.spin2\" @ $00014\n"
            + "00004 00004       04 00 00 00    Variables @ $00004\n"
            + "00008 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)\n"
            + "0000C 0000C       14 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00010 00010       04             (stack size)\n"
            + "'     a := 1\n"
            + "00011 00011       A2             CONSTANT (1)\n"
            + "00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00013 00013       04             RETURN\n"
            + "' Object \"text2.spin2\" header (var size 4)\n"
            + "00014 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)\n"
            + "00018 00004       0E 00 00 00    End\n"
            + "' PUB start(a, b) | c\n"
            + "0001C 00008       04             (stack size)\n"
            + "'     c := a + b\n"
            + "0001D 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0001E 0000A       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0001F 0000B       8A             ADD\n"
            + "00020 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)\n"
            + "00021 0000D       04             RETURN\n"
            + "00022 0000E       00 00          Padding\n"
            + "", compile("main.spin2", sources));
    }

    @Test
    void testObjectConstant() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin2", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main() | a\n"
            + "\n"
            + "    a := o.CONSTANT\n"
            + "\n"
            + "");
        sources.put("text2.spin2", ""
            + "CON\n"
            + "\n"
            + "    CONSTANT = 1\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       14 00 00 00    Object \"text2.spin2\" @ $00014\n"
            + "00004 00004       04 00 00 00    Variables @ $00004\n"
            + "00008 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)\n"
            + "0000C 0000C       14 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00010 00010       04             (stack size)\n"
            + "'     a := o.CONSTANT\n"
            + "00011 00011       A2             CONSTANT (1)\n"
            + "00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "00013 00013       04             RETURN\n"
            + "' Object \"text2.spin2\" header (var size 4)\n"
            + "", compile("main.spin2", sources));
    }

    @Test
    void testObjectVariablesLink() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin2", ""
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
        sources.put("text1.spin2", ""
            + "VAR\n"
            + "\n"
            + "    long d, e\n"
            + "\n"
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");
        sources.put("text2.spin2", ""
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
            + "00000 00000       1C 00 00 00    Object \"text1.spin2\" @ $0001C\n"
            + "00004 00004       08 00 00 00    Variables @ $00008\n"
            + "00008 00008       2C 00 00 00    Object \"text2.spin2\" @ $0002C\n"
            + "0000C 0000C       14 00 00 00    Variables @ $00014\n"
            + "00010 00010       18 00 00 80    Method main @ $00018 (0 parameters, 0 returns)\n"
            + "00014 00014       1C 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00018 00018       04             (stack size)\n"
            + "'     a := 1\n"
            + "00019 00019       A2             CONSTANT (1)\n"
            + "0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0001B 0001B       04             RETURN\n"
            + "' Object \"text1.spin2\" header (var size 12)\n"
            + "0001C 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)\n"
            + "00020 00004       0E 00 00 00    End\n"
            + "' PUB start(a, b) | c\n"
            + "00024 00008       04             (stack size)\n"
            + "'     c := a + b\n"
            + "00025 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00026 0000A       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00027 0000B       8A             ADD\n"
            + "00028 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)\n"
            + "00029 0000D       04             RETURN\n"
            + "0002A 0000E       00 00          Padding\n"
            + "' Object \"text2.spin2\" header (var size 20)\n"
            + "0002C 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)\n"
            + "00030 00004       0E 00 00 00    End\n"
            + "' PUB start(a, b) | c\n"
            + "00034 00008       04             (stack size)\n"
            + "'     c := a + b\n"
            + "00035 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00036 0000A       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00037 0000B       8A             ADD\n"
            + "00038 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)\n"
            + "00039 0000D       04             RETURN\n"
            + "0003A 0000E       00 00          Padding\n"
            + "", compile("main.spin2", sources));
    }

    @Test
    void testObjectMethodCall() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin2", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main() | a\n"
            + "\n"
            + "    o.start(1, 2)\n"
            + "\n"
            + "");
        sources.put("text2.spin2", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       18 00 00 00    Object \"text2.spin2\" @ $00018\n"
            + "00004 00004       04 00 00 00    Variables @ $00004\n"
            + "00008 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)\n"
            + "0000C 0000C       18 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00010 00010       04             (stack size)\n"
            + "'     o.start(1, 2)\n"
            + "00011 00011       00             ANCHOR\n"
            + "00012 00012       A2             CONSTANT (1)\n"
            + "00013 00013       A3             CONSTANT (2)\n"
            + "00014 00014       08 00 00       CALL_OBJ_SUB (0.0)\n"
            + "00017 00017       04             RETURN\n"
            + "' Object \"text2.spin2\" header (var size 4)\n"
            + "00018 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)\n"
            + "0001C 00004       0E 00 00 00    End\n"
            + "' PUB start(a, b) | c\n"
            + "00020 00008       04             (stack size)\n"
            + "'     c := a + b\n"
            + "00021 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00022 0000A       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00023 0000B       8A             ADD\n"
            + "00024 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)\n"
            + "00025 0000D       04             RETURN\n"
            + "00026 0000E       00 00          Padding\n"
            + "", compile("main.spin2", sources));
    }

    @Test
    void testNestedObjectsLink() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin2", ""
            + "VAR\n"
            + "\n"
            + "    long a\n"
            + "    byte b\n"
            + "    word c\n"
            + "    long d\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    t1 : \"text1\"\n"
            + "    t2 : \"text2\"\n"
            + "\n"
            + "PUB main()\n"
            + "\n"
            + "    a := 1\n"
            + "    b := 2\n"
            + "    c := 3\n"
            + "    d := 4\n"
            + "");
        sources.put("text1.spin2", ""
            + "OBJ\n"
            + "\n"
            + "    t3 : \"text3\"\n"
            + "\n"
            + "VAR\n"
            + "\n"
            + "    long a1\n"
            + "\n"
            + "PUB main()\n"
            + "\n"
            + "    a1 := 5\n"
            + "");
        sources.put("text2.spin2", ""
            + "VAR\n"
            + "\n"
            + "    word a2\n"
            + "    word b2\n"
            + "\n"
            + "PUB main()\n"
            + "\n"
            + "    a2 := 6\n"
            + "    b2 := 7\n"
            + "");
        sources.put("text3.spin2", ""
            + "VAR\n"
            + "\n"
            + "    byte a3\n"
            + "    long b3\n"
            + "\n"
            + "PUB main()\n"
            + "\n"
            + "    a3 := 8\n"
            + "    b3 := 9\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       2C 00 00 00    Object \"text1.spin2\" @ $0002C\n"
            + "00004 00004       10 00 00 00    Variables @ $00010\n"
            + "00008 00008       58 00 00 00    Object \"text2.spin2\" @ $00058\n"
            + "0000C 0000C       24 00 00 00    Variables @ $00024\n"
            + "00010 00010       18 00 00 80    Method main @ $00018 (0 parameters, 0 returns)\n"
            + "00014 00014       29 00 00 00    End\n"
            + "' PUB main()\n"
            + "00018 00018       00             (stack size)\n"
            + "'     a := 1\n"
            + "00019 00019       A2             CONSTANT (1)\n"
            + "0001A 0001A       C1 81          VAR_WRITE LONG VBASE+$00001 (short)\n"
            + "'     b := 2\n"
            + "0001C 0001C       A3             CONSTANT (2)\n"
            + "0001D 0001D       51 08 81       VAR_WRITE BYTE VBASE+$00008\n"
            + "'     c := 3\n"
            + "00020 00020       A4             CONSTANT (3)\n"
            + "00021 00021       57 09 81       VAR_WRITE WORD VBASE+$00009\n"
            + "'     d := 4\n"
            + "00024 00024       A5             CONSTANT (4)\n"
            + "00025 00025       5D 0B 81       VAR_WRITE LONG VBASE+$0000B\n"
            + "00028 00028       04             RETURN\n"
            + "00029 00029       00 00 00       Padding\n"
            + "' Object \"text1.spin2\" header (var size 20)\n"
            + "0002C 00000       18 00 00 00    Object \"text3.spin2\" @ $00018\n"
            + "00030 00004       08 00 00 00    Variables @ $00008\n"
            + "00034 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)\n"
            + "00038 0000C       15 00 00 00    End\n"
            + "' PUB main()\n"
            + "0003C 00010       00             (stack size)\n"
            + "'     a1 := 5\n"
            + "0003D 00011       A6             CONSTANT (5)\n"
            + "0003E 00012       C1 81          VAR_WRITE LONG VBASE+$00001 (short)\n"
            + "00040 00014       04             RETURN\n"
            + "00041 00015       00 00 00       Padding\n"
            + "' Object \"text3.spin2\" header (var size 12)\n"
            + "00044 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00048 00004       12 00 00 00    End\n"
            + "' PUB main()\n"
            + "0004C 00008       00             (stack size)\n"
            + "'     a3 := 8\n"
            + "0004D 00009       A9             CONSTANT (8)\n"
            + "0004E 0000A       51 04 81       VAR_WRITE BYTE VBASE+$00004\n"
            + "'     b3 := 9\n"
            + "00051 0000D       AA             CONSTANT (9)\n"
            + "00052 0000E       5D 05 81       VAR_WRITE LONG VBASE+$00005\n"
            + "00055 00011       04             RETURN\n"
            + "00056 00012       00 00          Padding\n"
            + "' Object \"text2.spin2\" header (var size 8)\n"
            + "00058 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "0005C 00004       12 00 00 00    End\n"
            + "' PUB main()\n"
            + "00060 00008       00             (stack size)\n"
            + "'     a2 := 6\n"
            + "00061 00009       A7             CONSTANT (6)\n"
            + "00062 0000A       57 04 81       VAR_WRITE WORD VBASE+$00004\n"
            + "'     b2 := 7\n"
            + "00065 0000D       A8             CONSTANT (7)\n"
            + "00066 0000E       57 06 81       VAR_WRITE WORD VBASE+$00006\n"
            + "00069 00011       04             RETURN\n"
            + "0006A 00012       00 00          Padding\n"
            + "", compile("main.spin2", sources));
    }

    @Test
    void testDuplicatedObjectLink() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin2", ""
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
        sources.put("object1.spin2", ""
            + "VAR\n"
            + "\n"
            + "    long d\n"
            + "    long e\n"
            + "\n"
            + "PUB function(a) : r\n"
            + "\n"
            + "    return a + d * e\n"
            + "");
        sources.put("object2.spin2", ""
            + "VAR\n"
            + "\n"
            + "    long d\n"
            + "\n"
            + "PUB function(a) : r | b, c\n"
            + "\n"
            + "    return (a + b * c) * d\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       34 00 00 00    Object \"object1.spin2\" @ $00034\n"
            + "00004 00004       08 00 00 00    Variables @ $00008\n"
            + "00008 00008       34 00 00 00    Object \"object1.spin2\" @ $00034\n"
            + "0000C 0000C       14 00 00 00    Variables @ $00014\n"
            + "00010 00010       48 00 00 00    Object \"object2.spin2\" @ $00048\n"
            + "00014 00014       20 00 00 00    Variables @ $00020\n"
            + "00018 00018       20 00 00 80    Method main @ $00020 (0 parameters, 0 returns)\n"
            + "0001C 0001C       31 00 00 00    End\n"
            + "' PUB main | a\n"
            + "00020 00020       04             (stack size)\n"
            + "'     o1.function(1)\n"
            + "00021 00021       00             ANCHOR\n"
            + "00022 00022       A2             CONSTANT (1)\n"
            + "00023 00023       08 00 00       CALL_OBJ_SUB (0.0)\n"
            + "'     o2.function(2)\n"
            + "00026 00026       00             ANCHOR\n"
            + "00027 00027       A3             CONSTANT (2)\n"
            + "00028 00028       08 01 00       CALL_OBJ_SUB (1.0)\n"
            + "'     o3.function(3)\n"
            + "0002B 0002B       00             ANCHOR\n"
            + "0002C 0002C       A4             CONSTANT (3)\n"
            + "0002D 0002D       08 02 00       CALL_OBJ_SUB (2.0)\n"
            + "00030 00030       04             RETURN\n"
            + "00031 00031       00 00 00       Padding\n"
            + "' Object \"object1.spin2\" header (var size 12)\n"
            + "00034 00000       08 00 10 81    Method function @ $00008 (1 parameters, 1 returns)\n"
            + "00038 00004       12 00 00 00    End\n"
            + "' PUB function(a) : r\n"
            + "0003C 00008       00             (stack size)\n"
            + "'     return a + d * e\n"
            + "0003D 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0003E 0000A       C1 80          VAR_READ LONG VBASE+$00001 (short)\n"
            + "00040 0000C       C2 80          VAR_READ LONG VBASE+$00002 (short)\n"
            + "00042 0000E       96             MULTIPLY\n"
            + "00043 0000F       8A             ADD\n"
            + "00044 00010       05             return\n"
            + "00045 00011       04             RETURN\n"
            + "00046 00012       00 00          Padding\n"
            + "' Object \"object2.spin2\" header (var size 8)\n"
            + "00048 00000       08 00 10 81    Method function @ $00008 (1 parameters, 1 returns)\n"
            + "0004C 00004       13 00 00 00    End\n"
            + "' PUB function(a) : r | b, c\n"
            + "00050 00008       08             (stack size)\n"
            + "'     return (a + b * c) * d\n"
            + "00051 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00052 0000A       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00053 0000B       E3             VAR_READ LONG DBASE+$00003 (short)\n"
            + "00054 0000C       96             MULTIPLY\n"
            + "00055 0000D       8A             ADD\n"
            + "00056 0000E       C1 80          VAR_READ LONG VBASE+$00001 (short)\n"
            + "00058 00010       96             MULTIPLY\n"
            + "00059 00011       05             return\n"
            + "0005A 00012       04             RETURN\n"
            + "0005B 00013       00             Padding\n"
            + "", compile("main.spin2", sources));
    }

    @Test
    void testNestedDuplicatedObjectLink() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin2", ""
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
        sources.put("object1.spin2", ""
            + "VAR\n"
            + "\n"
            + "    long d\n"
            + "    long e\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o3 : \"object2\"\n"
            + "\n"
            + "PUB function(a) : r\n"
            + "\n"
            + "    e := o3.function(3)\n"
            + "    return a + d * e\n"
            + "");
        sources.put("object2.spin2", ""
            + "VAR\n"
            + "\n"
            + "    long d\n"
            + "\n"
            + "PUB function(a) : r | b, c\n"
            + "\n"
            + "    return (a + b * c) * d\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       34 00 00 00    Object \"object1.spin2\" @ $00034\n"
            + "00004 00004       08 00 00 00    Variables @ $00008\n"
            + "00008 00008       34 00 00 00    Object \"object1.spin2\" @ $00034\n"
            + "0000C 0000C       1C 00 00 00    Variables @ $0001C\n"
            + "00010 00010       58 00 00 00    Object \"object2.spin2\" @ $00058\n"
            + "00014 00014       30 00 00 00    Variables @ $00030\n"
            + "00018 00018       20 00 00 80    Method main @ $00020 (0 parameters, 0 returns)\n"
            + "0001C 0001C       31 00 00 00    End\n"
            + "' PUB main | a\n"
            + "00020 00020       04             (stack size)\n"
            + "'     o1.function(1)\n"
            + "00021 00021       00             ANCHOR\n"
            + "00022 00022       A2             CONSTANT (1)\n"
            + "00023 00023       08 00 02       CALL_OBJ_SUB (0.2)\n"
            + "'     o2.function(2)\n"
            + "00026 00026       00             ANCHOR\n"
            + "00027 00027       A3             CONSTANT (2)\n"
            + "00028 00028       08 01 02       CALL_OBJ_SUB (1.2)\n"
            + "'     o3.function(3)\n"
            + "0002B 0002B       00             ANCHOR\n"
            + "0002C 0002C       A4             CONSTANT (3)\n"
            + "0002D 0002D       08 02 00       CALL_OBJ_SUB (2.0)\n"
            + "00030 00030       04             RETURN\n"
            + "00031 00031       00 00 00       Padding\n"
            + "' Object \"object1.spin2\" header (var size 20)\n"
            + "00034 00000       24 00 00 00    Object \"object2.spin2\" @ $00024\n"
            + "00038 00004       0C 00 00 00    Variables @ $0000C\n"
            + "0003C 00008       10 00 10 81    Method function @ $00010 (1 parameters, 1 returns)\n"
            + "00040 0000C       21 00 00 00    End\n"
            + "' PUB function(a) : r\n"
            + "00044 00010       00             (stack size)\n"
            + "'     e := o3.function(3)\n"
            + "00045 00011       01             ANCHOR\n"
            + "00046 00012       A4             CONSTANT (3)\n"
            + "00047 00013       08 00 00       CALL_OBJ_SUB (0.0)\n"
            + "0004A 00016       C2 81          VAR_WRITE LONG VBASE+$00002 (short)\n"
            + "'     return a + d * e\n"
            + "0004C 00018       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0004D 00019       C1 80          VAR_READ LONG VBASE+$00001 (short)\n"
            + "0004F 0001B       C2 80          VAR_READ LONG VBASE+$00002 (short)\n"
            + "00051 0001D       96             MULTIPLY\n"
            + "00052 0001E       8A             ADD\n"
            + "00053 0001F       05             return\n"
            + "00054 00020       04             RETURN\n"
            + "00055 00021       00 00 00       Padding\n"
            + "' Object \"object2.spin2\" header (var size 8)\n"
            + "00058 00000       08 00 10 81    Method function @ $00008 (1 parameters, 1 returns)\n"
            + "0005C 00004       13 00 00 00    End\n"
            + "' PUB function(a) : r | b, c\n"
            + "00060 00008       08             (stack size)\n"
            + "'     return (a + b * c) * d\n"
            + "00061 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00062 0000A       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00063 0000B       E3             VAR_READ LONG DBASE+$00003 (short)\n"
            + "00064 0000C       96             MULTIPLY\n"
            + "00065 0000D       8A             ADD\n"
            + "00066 0000E       C1 80          VAR_READ LONG VBASE+$00001 (short)\n"
            + "00068 00010       96             MULTIPLY\n"
            + "00069 00011       05             return\n"
            + "0006A 00012       04             RETURN\n"
            + "0006B 00013       00             Padding\n"
            + "", compile("main.spin2", sources));
    }

    @Test
    void testNestedChildDuplicatedObjectLink() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin2", ""
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
        sources.put("object1.spin2", ""
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
        sources.put("object2.spin2", ""
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
        sources.put("object3.spin2", ""
            + "VAR\n"
            + "\n"
            + "    long d\n"
            + "\n"
            + "PUB function(a) : r | b, c\n"
            + "\n"
            + "    return (a + b * c) * d\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       24 00 00 00    Object \"object1.spin2\" @ $00024\n"
            + "00004 00004       08 00 00 00    Variables @ $00008\n"
            + "00008 00008       48 00 00 00    Object \"object2.spin2\" @ $00048\n"
            + "0000C 0000C       1C 00 00 00    Variables @ $0001C\n"
            + "00010 00010       18 00 00 80    Method main @ $00018 (0 parameters, 0 returns)\n"
            + "00014 00014       24 00 00 00    End\n"
            + "' PUB main | a\n"
            + "00018 00018       04             (stack size)\n"
            + "'     o1.function(1)\n"
            + "00019 00019       00             ANCHOR\n"
            + "0001A 0001A       A2             CONSTANT (1)\n"
            + "0001B 0001B       08 00 02       CALL_OBJ_SUB (0.2)\n"
            + "'     o2.function(2)\n"
            + "0001E 0001E       00             ANCHOR\n"
            + "0001F 0001F       A3             CONSTANT (2)\n"
            + "00020 00020       08 01 02       CALL_OBJ_SUB (1.2)\n"
            + "00023 00023       04             RETURN\n"
            + "' Object \"object1.spin2\" header (var size 20)\n"
            + "00024 00000       48 00 00 00    Object \"object3.spin2\" @ $00048\n"
            + "00028 00004       0C 00 00 00    Variables @ $0000C\n"
            + "0002C 00008       10 00 10 81    Method function @ $00010 (1 parameters, 1 returns)\n"
            + "00030 0000C       21 00 00 00    End\n"
            + "' PUB function(a) : r\n"
            + "00034 00010       00             (stack size)\n"
            + "'     e := o3.function(4)\n"
            + "00035 00011       01             ANCHOR\n"
            + "00036 00012       A5             CONSTANT (4)\n"
            + "00037 00013       08 00 00       CALL_OBJ_SUB (0.0)\n"
            + "0003A 00016       C2 81          VAR_WRITE LONG VBASE+$00002 (short)\n"
            + "'     return a + d * e\n"
            + "0003C 00018       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0003D 00019       C1 80          VAR_READ LONG VBASE+$00001 (short)\n"
            + "0003F 0001B       C2 80          VAR_READ LONG VBASE+$00002 (short)\n"
            + "00041 0001D       96             MULTIPLY\n"
            + "00042 0001E       8A             ADD\n"
            + "00043 0001F       05             return\n"
            + "00044 00020       04             RETURN\n"
            + "00045 00021       00 00 00       Padding\n"
            + "' Object \"object2.spin2\" header (var size 20)\n"
            + "00048 00000       24 00 00 00    Object \"object3.spin2\" @ $00024\n"
            + "0004C 00004       0C 00 00 00    Variables @ $0000C\n"
            + "00050 00008       10 00 10 81    Method function @ $00010 (1 parameters, 1 returns)\n"
            + "00054 0000C       21 00 00 00    End\n"
            + "' PUB function(a) : r | b, c\n"
            + "00058 00010       08             (stack size)\n"
            + "'     e := o3.function(5)\n"
            + "00059 00011       01             ANCHOR\n"
            + "0005A 00012       A6             CONSTANT (5)\n"
            + "0005B 00013       08 00 00       CALL_OBJ_SUB (0.0)\n"
            + "0005E 00016       C2 81          VAR_WRITE LONG VBASE+$00002 (short)\n"
            + "'     return a + d / e\n"
            + "00060 00018       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00061 00019       C1 80          VAR_READ LONG VBASE+$00001 (short)\n"
            + "00063 0001B       C2 80          VAR_READ LONG VBASE+$00002 (short)\n"
            + "00065 0001D       97             DIVIDE\n"
            + "00066 0001E       8A             ADD\n"
            + "00067 0001F       05             return\n"
            + "00068 00020       04             RETURN\n"
            + "00069 00021       00 00 00       Padding\n"
            + "' Object \"object3.spin2\" header (var size 8)\n"
            + "0006C 00000       08 00 10 81    Method function @ $00008 (1 parameters, 1 returns)\n"
            + "00070 00004       13 00 00 00    End\n"
            + "' PUB function(a) : r | b, c\n"
            + "00074 00008       08             (stack size)\n"
            + "'     return (a + b * c) * d\n"
            + "00075 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00076 0000A       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00077 0000B       E3             VAR_READ LONG DBASE+$00003 (short)\n"
            + "00078 0000C       96             MULTIPLY\n"
            + "00079 0000D       8A             ADD\n"
            + "0007A 0000E       C1 80          VAR_READ LONG VBASE+$00001 (short)\n"
            + "0007C 00010       96             MULTIPLY\n"
            + "0007D 00011       05             return\n"
            + "0007E 00012       04             RETURN\n"
            + "0007F 00013       00             Padding\n"
            + "", compile("main.spin2", sources));
    }

    @Test
    void testCircularReference1() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin2", ""
            + "OBJ\n"
            + "\n"
            + "    o1 : \"object1\"\n"
            + "    o2 : \"object2\"\n"
            + "\n"
            + "");
        sources.put("object1.spin2", ""
            + "OBJ\n"
            + "\n"
            + "    o3 : \"object2\"\n"
            + "\n"
            + "");
        sources.put("object2.spin2", ""
            + "OBJ\n"
            + "\n"
            + "    o4 : \"main\"\n"
            + "\n"
            + "");

        Assertions.assertThrows(CompilerMessage.class, () -> {
            compile("main.spin2", sources);
        });
    }

    @Test
    void testCircularReference2() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin2", ""
            + "OBJ\n"
            + "\n"
            + "    o1 : \"object1\"\n"
            + "\n"
            + "");
        sources.put("object1.spin2", ""
            + "OBJ\n"
            + "\n"
            + "    o3 : \"object2\"\n"
            + "\n"
            + "");
        sources.put("object2.spin2", ""
            + "OBJ\n"
            + "\n"
            + "    o4 : \"main\"\n"
            + "\n"
            + "");

        Assertions.assertThrows(CompilerMessage.class, () -> {
            compile("main.spin2", sources);
        });
    }

    @Test
    void testDebug() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin2", ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main() | a\n"
            + "\n"
            + "    debug(udec(a))\n"
            + "\n"
            + "");
        sources.put("text2.spin2", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "    debug(udec(a,b,c))\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       18 00 00 00    Object \"text2.spin2\" @ $00018\n"
            + "00004 00004       04 00 00 00    Variables @ $00004\n"
            + "00008 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)\n"
            + "0000C 0000C       16 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00010 00010       04             (stack size)\n"
            + "'     debug(udec(a))\n"
            + "00011 00011       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00012 00012       44 04 02       DEBUG #2\n"
            + "00015 00015       04             RETURN\n"
            + "00016 00016       00 00          Padding\n"
            + "' Object \"text2.spin2\" header (var size 4)\n"
            + "00018 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)\n"
            + "0001C 00004       14 00 00 00    End\n"
            + "' PUB start(a, b) | c\n"
            + "00020 00008       04             (stack size)\n"
            + "'     c := a + b\n"
            + "00021 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00022 0000A       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00023 0000B       8A             ADD\n"
            + "00024 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)\n"
            + "'     debug(udec(a,b,c))\n"
            + "00025 0000D       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00026 0000E       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00027 0000F       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00028 00010       44 0C 01       DEBUG #1\n"
            + "0002B 00013       04             RETURN\n"
            + "' Debug data\n"
            + "00000 00000       16 00         \n"
            + "00002 00002       06 00         \n"
            + "00004 00004       11 00         \n"
            + "00006 00006       04 41 61 00 40\n"
            + "0000B 0000B       62 00 40 63 00\n"
            + "00010 00010       00\n"
            + "00011 00011       04 41 61 00 00\n"
            + "", compile("main.spin2", sources, true));
    }

    @Test
    void testDuplicatedObjectsDebug() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.spin2", ""
            + "OBJ\n"
            + "\n"
            + "    o1 : \"text2\"\n"
            + "    o2 : \"text2\"\n"
            + "\n"
            + "PUB main() | a\n"
            + "\n"
            + "    debug(udec(a))\n"
            + "\n"
            + "");
        sources.put("text2.spin2", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "    debug(udec(a,b,c))\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       20 00 00 00    Object \"text2.spin2\" @ $00020\n"
            + "00004 00004       04 00 00 00    Variables @ $00004\n"
            + "00008 00008       20 00 00 00    Object \"text2.spin2\" @ $00020\n"
            + "0000C 0000C       08 00 00 00    Variables @ $00008\n"
            + "00010 00010       18 00 00 80    Method main @ $00018 (0 parameters, 0 returns)\n"
            + "00014 00014       1E 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00018 00018       04             (stack size)\n"
            + "'     debug(udec(a))\n"
            + "00019 00019       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0001A 0001A       44 04 02       DEBUG #2\n"
            + "0001D 0001D       04             RETURN\n"
            + "0001E 0001E       00 00          Padding\n"
            + "' Object \"text2.spin2\" header (var size 4)\n"
            + "00020 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)\n"
            + "00024 00004       14 00 00 00    End\n"
            + "' PUB start(a, b) | c\n"
            + "00028 00008       04             (stack size)\n"
            + "'     c := a + b\n"
            + "00029 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0002A 0000A       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0002B 0000B       8A             ADD\n"
            + "0002C 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)\n"
            + "'     debug(udec(a,b,c))\n"
            + "0002D 0000D       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0002E 0000E       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0002F 0000F       E2             VAR_READ LONG DBASE+$00002 (short)\n"
            + "00030 00010       44 0C 01       DEBUG #1\n"
            + "00033 00013       04             RETURN\n"
            + "' Debug data\n"
            + "00000 00000       16 00         \n"
            + "00002 00002       06 00         \n"
            + "00004 00004       11 00         \n"
            + "00006 00006       04 41 61 00 40\n"
            + "0000B 0000B       62 00 40 63 00\n"
            + "00010 00010       00\n"
            + "00011 00011       04 41 61 00 00\n"
            + "", compile("main.spin2", sources, true));
    }

    class Spin2CompilerAdapter extends Spin2Compiler {

        Map<String, String> sources;

        public Spin2CompilerAdapter(Map<String, String> sources) {
            this.sources = sources;
        }

        @Override
        protected Node getParsedObject(String fileName) {
            String text = sources.get(fileName);
            if (text == null) {
                return null;
            }
            Spin2TokenStream stream = new Spin2TokenStream(text);
            Spin2Parser subject = new Spin2Parser(stream);
            return subject.parse();
        }

    }

    String compile(String rootFile, Map<String, String> sources) throws Exception {
        return compile(rootFile, sources, false);
    }

    String compile(String rootFile, Map<String, String> sources, boolean debugEnabled) throws Exception {
        Spin2TokenStream stream = new Spin2TokenStream(sources.get(rootFile));
        Spin2Parser subject = new Spin2Parser(stream);
        Node root = subject.parse();

        Spin2CompilerAdapter compiler = new Spin2CompilerAdapter(sources);
        compiler.setDebugEnabled(debugEnabled);
        Spin2Object obj = compiler.compileObject(rootFile, root);

        for (CompilerMessage msg : compiler.getMessages()) {
            if (msg.type == CompilerMessage.ERROR) {
                throw msg;
            }
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateListing(new PrintStream(os));

        return os.toString().replaceAll("\\r\\n", "\n");
    }

}
