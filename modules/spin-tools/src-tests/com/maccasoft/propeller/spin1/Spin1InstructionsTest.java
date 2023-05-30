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

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;

class Spin1InstructionsTest {

    @Test
    void testAbs() throws Exception {
        byte[] code = compile("DAT             abs     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC A8"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testAbsneg() throws Exception {
        byte[] code = compile("DAT             absneg  1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC AC"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testAdd() throws Exception {
        byte[] code = compile("DAT             add     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 80"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testAddabs() throws Exception {
        byte[] code = compile("DAT             addabs  1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 88"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testAdds() throws Exception {
        byte[] code = compile("DAT             adds    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC D0"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testAddsx() throws Exception {
        byte[] code = compile("DAT             addsx   1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC D8"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testAddx() throws Exception {
        byte[] code = compile("DAT             addx    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC C8"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testAnd() throws Exception {
        byte[] code = compile("DAT             and     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 60"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testAndn() throws Exception {
        byte[] code = compile("DAT             andn    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 64"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testCall() throws Exception {
        byte[] code = compile(""
            + "DAT\n"
            + "label           call    #label\n"
            + "label_ret       nop");
        Assertions.assertEquals(decodeToString("00 02 FC 5C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testClkset() throws Exception {
        byte[] code = compile("DAT             clkset  1");
        Assertions.assertEquals(decodeToString("00 02 7C 0C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testCmp() throws Exception {
        byte[] code = compile("DAT             cmp     1, 2");
        Assertions.assertEquals(decodeToString("02 02 3C 84"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testCmps() throws Exception {
        byte[] code = compile("DAT             cmps    1, 2");
        Assertions.assertEquals(decodeToString("02 02 3C C0"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testCmpsub() throws Exception {
        byte[] code = compile("DAT             cmpsub  1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC E0"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testCmpsx() throws Exception {
        byte[] code = compile("DAT             cmpsx   1, 2");
        Assertions.assertEquals(decodeToString("02 02 3C C4"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testCmpx() throws Exception {
        byte[] code = compile("DAT             cmpx    1, 2");
        Assertions.assertEquals(decodeToString("02 02 3C CC"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testCogid() throws Exception {
        byte[] code = compile("DAT             cogid   1");
        Assertions.assertEquals(decodeToString("01 02 FC 0C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testCoginit() throws Exception {
        byte[] code = compile("DAT             coginit 1");
        Assertions.assertEquals(decodeToString("02 02 7C 0C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testCogstop() throws Exception {
        byte[] code = compile("DAT             cogstop 1");
        Assertions.assertEquals(decodeToString("03 02 7C 0C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testDjnz() throws Exception {
        byte[] code = compile("DAT             djnz    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC E4"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testEnc() throws Exception {
        byte[] code = compile("DAT             enc     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 18"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testHubop() throws Exception {
        byte[] code = compile("DAT             hubop   1, 2");
        Assertions.assertEquals(decodeToString("02 02 3C 0C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testJmp() throws Exception {
        byte[] code = compile("DAT             jmp     1");
        Assertions.assertEquals(decodeToString("01 00 3C 5C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testJmpret() throws Exception {
        byte[] code = compile("DAT             jmpret  1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 5C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testLockclr() throws Exception {
        byte[] code = compile("DAT             lockclr 1");
        Assertions.assertEquals(decodeToString("07 02 7C 0C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testLocknew() throws Exception {
        byte[] code = compile("DAT             locknew 1");
        Assertions.assertEquals(decodeToString("04 02 FC 0C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testLockret() throws Exception {
        byte[] code = compile("DAT             lockret 1");
        Assertions.assertEquals(decodeToString("05 02 7C 0C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testLockset() throws Exception {
        byte[] code = compile("DAT             lockset 1");
        Assertions.assertEquals(decodeToString("06 02 7C 0C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testMax() throws Exception {
        byte[] code = compile("DAT             max     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 4C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testMaxs() throws Exception {
        byte[] code = compile("DAT             maxs    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 44"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testMin() throws Exception {
        byte[] code = compile("DAT             min     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 48"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testMins() throws Exception {
        byte[] code = compile("DAT             mins    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 40"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testMov() throws Exception {
        byte[] code = compile("DAT             mov     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC A0"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testMovd() throws Exception {
        byte[] code = compile("DAT             movd    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 54"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testMovi() throws Exception {
        byte[] code = compile("DAT             movi    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 58"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testMovs() throws Exception {
        byte[] code = compile("DAT             movs    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 50"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testMuxc() throws Exception {
        byte[] code = compile("DAT             muxc    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 70"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testMuxnc() throws Exception {
        byte[] code = compile("DAT             muxnc   1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 74"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testMuxnz() throws Exception {
        byte[] code = compile("DAT             muxnz   1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 7C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testMuxz() throws Exception {
        byte[] code = compile("DAT             muxz    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 78"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testMul() throws Exception {
        byte[] code = compile("DAT             mul     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 10"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testMuls() throws Exception {
        byte[] code = compile("DAT             muls    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 14"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testNeg() throws Exception {
        byte[] code = compile("DAT             neg     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC A4"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testNegc() throws Exception {
        byte[] code = compile("DAT             negc    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC B0"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testNegnc() throws Exception {
        byte[] code = compile("DAT             negnc   1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC B4"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testNegnz() throws Exception {
        byte[] code = compile("DAT             negnz   1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC BC"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testNegz() throws Exception {
        byte[] code = compile("DAT             negz    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC B8"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testNop() throws Exception {
        byte[] code = compile("DAT             nop");
        Assertions.assertEquals(decodeToString("00 00 00 00"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testOnes() throws Exception {
        byte[] code = compile("DAT             ones    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 1C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testOr() throws Exception {
        byte[] code = compile("DAT             or      1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 68"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testRcl() throws Exception {
        byte[] code = compile("DAT             rcl     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 34"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testRcr() throws Exception {
        byte[] code = compile("DAT             rcr     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 30"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testRdbyte() throws Exception {
        byte[] code = compile("DAT             rdbyte  1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 00"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testRdlong() throws Exception {
        byte[] code = compile("DAT             rdlong  1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 08"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testRdword() throws Exception {
        byte[] code = compile("DAT             rdword  1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 04"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testRet() throws Exception {
        byte[] code = compile("DAT             ret");
        Assertions.assertEquals(decodeToString("00 00 7C 5C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testRev() throws Exception {
        byte[] code = compile("DAT             rev     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 3C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testRol() throws Exception {
        byte[] code = compile("DAT             rol     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 24"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testRor() throws Exception {
        byte[] code = compile("DAT             ror     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 20"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testSar() throws Exception {
        byte[] code = compile("DAT             sar     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 38"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testShl() throws Exception {
        byte[] code = compile("DAT             shl     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 2C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testShr() throws Exception {
        byte[] code = compile("DAT             shr     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 28"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testSub() throws Exception {
        byte[] code = compile("DAT             sub     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 84"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testSubabs() throws Exception {
        byte[] code = compile("DAT             subabs  1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 8C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testSubs() throws Exception {
        byte[] code = compile("DAT             subs    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC D4"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testSubsx() throws Exception {
        byte[] code = compile("DAT             subsx   1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC DC"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testSubx() throws Exception {
        byte[] code = compile("DAT             subx    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC CC"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testSumc() throws Exception {
        byte[] code = compile("DAT             sumc    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 90"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testSumnc() throws Exception {
        byte[] code = compile("DAT             sumnc   1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 94"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testSumnz() throws Exception {
        byte[] code = compile("DAT             sumnz   1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 9C"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testSumz() throws Exception {
        byte[] code = compile("DAT             sumz    1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 98"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testTest() throws Exception {
        byte[] code = compile("DAT             test    1, 2");
        Assertions.assertEquals(decodeToString("02 02 3C 60"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testTestn() throws Exception {
        byte[] code = compile("DAT             testn   1, 2");
        Assertions.assertEquals(decodeToString("02 02 3C 64"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testTjnz() throws Exception {
        byte[] code = compile("DAT             tjnz    1, 2");
        Assertions.assertEquals(decodeToString("02 02 3C E8"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testTjz() throws Exception {
        byte[] code = compile("DAT             tjz     1, 2");
        Assertions.assertEquals(decodeToString("02 02 3C EC"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testWaitcnt() throws Exception {
        byte[] code = compile("DAT             waitcnt 1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC F8"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testWaitpeq() throws Exception {
        byte[] code = compile("DAT             waitpeq 1, 2");
        Assertions.assertEquals(decodeToString("02 02 3C F0"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testWaitpne() throws Exception {
        byte[] code = compile("DAT             waitpne 1, 2");
        Assertions.assertEquals(decodeToString("02 02 3C F4"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testWaitvid() throws Exception {
        byte[] code = compile("DAT             waitvid 1, 2");
        Assertions.assertEquals(decodeToString("02 02 3C FC"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testWrbyte() throws Exception {
        byte[] code = compile("DAT             wrbyte  1, 2");
        Assertions.assertEquals(decodeToString("02 02 3C 00"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testWrlong() throws Exception {
        byte[] code = compile("DAT             wrlong  1, 2");
        Assertions.assertEquals(decodeToString("02 02 3C 08"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testWrword() throws Exception {
        byte[] code = compile("DAT             wrword  1, 2");
        Assertions.assertEquals(decodeToString("02 02 3C 04"), Spin1InstructionObject.decodeToString(code));
    }

    @Test
    void testXor() throws Exception {
        byte[] code = compile("DAT             xor     1, 2");
        Assertions.assertEquals(decodeToString("02 02 BC 6C"), Spin1InstructionObject.decodeToString(code));
    }

    byte[] compile(String text) throws Exception {
        Spin1TokenStream stream = new Spin1TokenStream(text);
        Spin1Parser parser = new Spin1Parser(stream);
        Node root = parser.parse();

        Spin1ObjectCompiler compiler = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        compiler.compileObject(root);

        Spin1InstructionObject obj = compiler.source.get(0).getInstructionObject();
        return obj.getBytes();
    }

    static String decodeToString(String s) {
        int b0 = Integer.parseInt(s.substring(0, 2), 16);
        int b1 = Integer.parseInt(s.substring(3, 5), 16);
        int b2 = Integer.parseInt(s.substring(6, 8), 16);
        int b3 = Integer.parseInt(s.substring(9, 11), 16);
        return Spin1InstructionObject.decodeToString((b0 & 0xFF) | ((b1 & 0xFF) << 8) | ((b2 & 0xFF) << 16) | ((b3 & 0xFF) << 24));
    }

}
