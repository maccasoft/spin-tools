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
import java.io.File;
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
            + "    PIN = 16\n"
            + "\n"
            + "PUB start | ct\n"
            + "\n"
            + "    ' initialization\n"
            + "    DIRA[PIN] := 1\n"
            + "    ct := CNT\n"
            + "\n"
            + "    repeat\n"
            + "        ' loop\n"
            + "        OUTA[PIN] ^= 1\n"
            + "        waitcnt(ct += CLKFREQ / 2)";

        Assertions.assertEquals(""
            + "00000 00000       00 B4 C4 04    CLKFREQ\n"
            + "00004 00004       6F             CLKMODE\n"
            + "00005 00005       00             Checksum\n"
            + "00006 00006       10 00          PBASE\n"
            + "00008 00008       34 00          VBASE\n"
            + "0000A 0000A       3C 00          DBASE\n"
            + "0000C 0000C       18 00          PCURR\n"
            + "0000E 0000E       44 00          DCURR\n"
            + "' Object header\n"
            + "00010 00000       24 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       00             Object count\n"
            + "00014 00004       08 00 04 00    Function start @ $0008 (local size 4)\n"
            + "' PUB start | ct\n"
            + "'     DIRA[PIN] := 1\n"
            + "00018 00008       36             CONSTANT (1)\n"
            + "00019 00009       38 10          CONSTANT (16)\n"
            + "0001B 0000B       3D B6          REGBIT_WRITE $1F6\n"
            + "'     ct := CNT\n"
            + "0001D 0000D       3F 91          REG_READ $1F1\n"
            + "0001F 0000F       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     repeat\n"
            + "'         OUTA[PIN] ^= 1\n"
            + "00020 00010       36             CONSTANT (1)\n"
            + "00021 00011       38 10          CONSTANT (16)\n"
            + "00023 00013       3D D4          REGBIT_MODIFY $1F4\n"
            + "00025 00015       4B             BITXOR\n"
            + "'         waitcnt(ct += CLKFREQ / 2)\n"
            + "00026 00016       3B 02 62 5A 00 CONSTANT (80000000 / 2)\n"
            + "0002B 0001B       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "0002C 0001C       CC             ADD\n"
            + "0002D 0001D       23             WAITCNT\n"
            + "0002E 0001E       04 70          JMP $00010 (-16)\n"
            + "00030 00020       32             RETURN\n"
            + "00031 00021       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testBlinkOpenSpin() throws Exception {
        String text = ""
            + "CON\n"
            + "\n"
            + "    _XINFREQ = 5_000_000\n"
            + "    _CLKMODE = XTAL1 + PLL16X\n"
            + "\n"
            + "    PIN = 16\n"
            + "\n"
            + "PUB start | ct\n"
            + "\n"
            + "    ' initialization\n"
            + "    DIRA[PIN] := 1\n"
            + "    ct := CNT\n"
            + "\n"
            + "    repeat\n"
            + "        ' loop\n"
            + "        OUTA[PIN] ^= 1\n"
            + "        waitcnt(ct += CLKFREQ / 2)";

        Assertions.assertEquals(""
            + "00000 00000       00 B4 C4 04    CLKFREQ\n"
            + "00004 00004       6F             CLKMODE\n"
            + "00005 00005       00             Checksum\n"
            + "00006 00006       10 00          PBASE\n"
            + "00008 00008       34 00          VBASE\n"
            + "0000A 0000A       3C 00          DBASE\n"
            + "0000C 0000C       18 00          PCURR\n"
            + "0000E 0000E       44 00          DCURR\n"
            + "' Object header\n"
            + "00010 00000       24 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       00             Object count\n"
            + "00014 00004       08 00 04 00    Function start @ $0008 (local size 4)\n"
            + "' PUB start | ct\n"
            + "'     DIRA[PIN] := 1\n"
            + "00018 00008       36             CONSTANT (1)\n"
            + "00019 00009       37 03          CONSTANT (16)\n"
            + "0001B 0000B       3D B6          REGBIT_WRITE $1F6\n"
            + "'     ct := CNT\n"
            + "0001D 0000D       3F 91          REG_READ $1F1\n"
            + "0001F 0000F       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "'     repeat\n"
            + "'         OUTA[PIN] ^= 1\n"
            + "00020 00010       36             CONSTANT (1)\n"
            + "00021 00011       37 03          CONSTANT (16)\n"
            + "00023 00013       3D D4          REGBIT_MODIFY $1F4\n"
            + "00025 00015       4B             BITXOR\n"
            + "'         waitcnt(ct += CLKFREQ / 2)\n"
            + "00026 00016       35             CONSTANT (0)\n"
            + "00027 00017       C0             MEM_READ LONG POP\n"
            + "00028 00018       37 00          CONSTANT (2)\n"
            + "0002A 0001A       F6             DIVIDE\n"
            + "0002B 0001B       66             VAR_MODIFY LONG DBASE+$0004 (short)\n"
            + "0002C 0001C       CC             ADD\n"
            + "0002D 0001D       23             WAITCNT\n"
            + "0002E 0001E       04 70          JMP $00010 (-16)\n"
            + "00030 00020       32             RETURN\n"
            + "00031 00021       00 00 00       Padding\n"
            + "", compile(text, true));
    }

    String compile(String text) throws Exception {
        return compile(text, false);
    }

    String compile(String text, boolean openspinCompatible) throws Exception {
        Spin1TokenStream stream = new Spin1TokenStream(text);
        Spin1Parser subject = new Spin1Parser(stream);
        Node root = subject.parse();

        Spin1Compiler compiler = new Spin1Compiler();
        compiler.setOpenspinCompatible(openspinCompatible);
        Spin1Object obj = compiler.compile(new File("main.spin"), "main.spin", root);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateListing(new PrintStream(os));

        return os.toString().replaceAll("\\r\\n", "\n");
    }

}
