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

public class Spin1FunctionalTest {

    @Test
    void testBlink() throws Exception {
        String text = ""
            + "CON\n"
            + "\n"
            + "    _XINFREQ = 5_000_000\n"
            + "    _CLKMODE = XTAL1 + PLL16X\n"
            + "\n"
            + "PUB start\n"
            + "\n"
            + "    ' initialization\n"
            + "    DIRA[16] := 1\n"
            + "\n"
            + "    repeat\n"
            + "        ' loop\n"
            + "        OUTA[16] ^= 1\n"
            + "        waitcnt(3_000_000 + CNT)";

        Assertions.assertEquals(""
            + "00000 00000       00 B4 C4 04    CLKFREQ\n"
            + "00004 00004       6F             CLKMODE\n"
            + "00005 00005       00             Checksum\n"
            + "00006 00006       10 00          PBASE\n"
            + "00008 00008       30 00          VBASE\n"
            + "0000A 0000A       38 00          DBASE\n"
            + "0000C 0000C       18 00          PCURR\n"
            + "0000E 0000E       3C 00          DCURR\n"
            + "' Object header\n"
            + "00010 00000       20 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       00             Object count\n"
            + "00014 00004       08 00 00 00    Function start @ $0008 (local size 0)\n"
            + "' PUB start\n"
            + "'     DIRA[16] := 1\n"
            + "00018 00008       36             CONSTANT (1)\n"
            + "00019 00009       38 10          CONSTANT (16)\n"
            + "0001B 0000B       3D B6          REGBIT_WRITE $1F6\n"
            + "'     repeat\n"
            + "'         OUTA[16] ^= 1\n"
            + "0001D 0000D       36             CONSTANT (1)\n"
            + "0001E 0000E       38 10          CONSTANT (16)\n"
            + "00020 00010       3D D4          REGBIT_MODIFY $1F4\n"
            + "00022 00012       4B             BITXOR\n"
            + "'         waitcnt(3_000_000 + CNT)\n"
            + "00023 00013       3A 2D C6 C0    CONSTANT (3_000_000)\n"
            + "00027 00017       3F 91          REG_READ $1F1\n"
            + "00029 00019       EC             ADD\n"
            + "0002A 0001A       23             WAITCNT\n"
            + "0002B 0001B       04 70          JMP $0000D (-16)\n"
            + "0002D 0001D       32             RETURN\n"
            + "0002E 0001E       00 00          Padding\n"
            + "", compile(text));
    }

    String compile(String text) throws Exception {
        Spin1TokenStream stream = new Spin1TokenStream(text);
        Spin1Parser subject = new Spin1Parser(stream);
        Node root = subject.parse();

        Spin1Compiler compiler = new Spin1Compiler();
        Spin1Object obj = compiler.compile(root);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateListing(new PrintStream(os));

        return os.toString();
    }

}
