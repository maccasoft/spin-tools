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
            + "PUB start | delay\n"
            + "\n"
            + "    ' initialization\n"
            + "    DIRA[16] := 1\n"
            + "    delay := CNT + CLKFREQ / 2\n"
            + "\n"
            + "    repeat\n"
            + "        ' loop\n"
            + "        OUTA[16] ^= 1\n"
            + "        waitcnt(delay += CLKFREQ / 2)";

        Assertions.assertEquals(""
            + "00000 00000       00 B4 C4 04    CLKFREQ\n"
            + "00004 00004       6F             CLKMODE\n"
            + "00005 00005       00             Checksum\n"
            + "00006 00006       10 00          PBASE\n"
            + "00008 00008       40 00          VBASE\n"
            + "0000A 0000A       48 00          DBASE\n"
            + "0000C 0000C       18 00          PCURR\n"
            + "0000E 0000E       4C 00          DCURR\n"
            + "' Object header\n"
            + "00010 00000       30 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       00             Object count\n"
            + "00014 00004       08 00 04 00    Function start @ $0008 (local size 4)\n"
            + "' PUB start | delay\n"
            + "'     DIRA[16] := 1\n"
            + "00018 00008       36             CONSTANT (1)\n"
            + "00019 00009       38 10          CONSTANT (16)\n"
            + "0001B 0000B       3D B6          REGBIT_WRITE $1F6\n"
            + "'     delay := CNT + CLKFREQ / 2\n"
            + "0001D 0000D       3F 91          REG_READ $1F1\n"
            + "0001F 0000F       3B 04 C4 B4 00 CONSTANT (80000000)\n"
            + "00024 00014       38 02          CONSTANT (2)\n"
            + "00026 00016       F6             DIVIDE\n"
            + "00027 00017       EC             ADD\n"
            + "00028 00018       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     repeat\n"
            + "'         OUTA[16] ^= 1\n"
            + "00029 00019       36             CONSTANT (1)\n"
            + "0002A 0001A       38 10          CONSTANT (16)\n"
            + "0002C 0001C       3D D4          REGBIT_MODIFY $1F4\n"
            + "0002E 0001E       4B             BITXOR\n"
            + "'         waitcnt(delay += CLKFREQ / 2)\n"
            + "0002F 0001F       3B 04 C4 B4 00 CONSTANT (80000000)\n"
            + "00034 00024       38 02          CONSTANT (2)\n"
            + "00036 00026       F6             DIVIDE\n"
            + "00037 00027       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "00038 00028       CC             ADD\n"
            + "00039 00029       23             WAITCNT\n"
            + "0003A 0002A       04 6D          JMP $00019 (-19)\n"
            + "0003C 0002C       32             RETURN\n"
            + "0003D 0002D       00 00 00       Padding\n"
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
