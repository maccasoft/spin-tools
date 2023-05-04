/*
 * Copyright (c) 2022 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.Formatter;

class Spin2FormatterTest {

    @Test
    void testDefault() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "A=1*2+3\n"
            + "F=1*(2+3)\n"
            + "#0,a,b[2],c,d\n"
            + "#10[5],e,f\n"
            + "");
        Assertions.assertEquals(""
            + "    A = 1 * 2 + 3\n"
            + "    F = 1 * (2 + 3)\n"
            + "    #0, a, b[2], c, d\n"
            + "    #10[5], e, f\n"
            + "", text);
    }

    @Test
    void testConstants() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "CON\n"
            + "A=1*2+3\n"
            + "F=1*(2+3)\n"
            + "#0,a,b[2],c,d\n"
            + "#10[5],e,f\n"
            + "");
        Assertions.assertEquals(""
            + "CON\n"
            + "    A = 1 * 2 + 3\n"
            + "    F = 1 * (2 + 3)\n"
            + "    #0, a, b[2], c, d\n"
            + "    #10[5], e, f\n"
            + "", text);
    }

    @Test
    void testVariables() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "VAR\n"
            + "long A\n"
            + " word b,c\n"
            + "byte d[12], e[100], f[1*5]\n"
            + "");
        Assertions.assertEquals(""
            + "VAR\n"
            + "    long A\n"
            + "    word b, c\n"
            + "    byte d[12], e[100], f[1 * 5]\n"
            + "", text);
    }

    @Test
    void testObjects() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "OBJ\n"
            + "o1:\"file1.spin2\"\n"
            + "o2[2]:\"file2.spin2\"\n"
            + "");
        Assertions.assertEquals(""
            + "OBJ\n"
            + "    o1 : \"file1.spin2\"\n"
            + "    o2[2] : \"file2.spin2\"\n"
            + "", text);
    }

    @Test
    void testMethod() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start\n"
            + "", text);
    }

    @Test
    void testMethodParameters() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start(a,b,c)\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start(a, b, c)\n"
            + "", text);
    }

    @Test
    void testMethodReturns() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start():a\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start() : a\n"
            + "", text);
    }

    @Test
    void testMethodVariables() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()|a,b\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start() | a, b\n"
            + "", text);
    }

    @Test
    void testMethodFull() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start(a,b):c|d,e\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start(a, b) : c | d, e\n"
            + "", text);
    }

    @Test
    void testMethodStatements() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()\n"
            + " a:=1\n"
            + "  b:=b*(c+d)\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    a := 1\n"
            + "    b := b * (c + d)\n"
            + "", text);
    }

    @Test
    void testMethodStatementsBlock() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()\n"
            + " a:=1\n"
            + " if z==1\n"
            + "  b:=b*(c+d)\n"
            + "  e:=$12\n"
            + " f:=2\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    a := 1\n"
            + "    if z == 1\n"
            + "        b := b * (c + d)\n"
            + "        e := $12\n"
            + "    f := 2\n"
            + "", text);
    }

    @Test
    void testMultipleMethods() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()\n"
            + " a:=1\n"
            + "PRI init()\n"
            + "  b:=b*(c+d)\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    a := 1\n"
            + "\n"
            + "PRI init()\n"
            + "    b := b * (c + d)\n"
            + "", text);
    }

    @Test
    void testCaseStatements() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()\n"
            + " case a\n"
            + "  1:\n"
            + "   b:=1\n"
            + "  2:\n"
            + "   b:=2\n"
            + "  other:\n"
            + "   b:=3\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    case a\n"
            + "        1:\n"
            + "            b := 1\n"
            + "        2:\n"
            + "            b := 2\n"
            + "        other:\n"
            + "            b := 3\n"
            + "", text);
    }

    @Test
    void testCaseExpressionStatements() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()\n"
            + " case a\n"
            + "  1,2,3:\n"
            + "   b:=1\n"
            + "  4..7:\n"
            + "   b:=2\n"
            + "  other:\n"
            + "   b:=3\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    case a\n"
            + "        1, 2, 3:\n"
            + "            b := 1\n"
            + "        4..7:\n"
            + "            b := 2\n"
            + "        other:\n"
            + "            b := 3\n"
            + "", text);
    }

    @Test
    void testCaseInlineStatements() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()\n"
            + " case a\n"
            + "  1:b:=1\n"
            + "  2: b:=2\n"
            + "   c:=4\n"
            + "  other:\n"
            + "   b:=3\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    case a\n"
            + "        1:\n"
            + "            b := 1\n"
            + "        2:\n"
            + "            b := 2\n"
            + "            c := 4\n"
            + "        other:\n"
            + "            b := 3\n"
            + "", text);
    }

    @Test
    void testNestedCaseStatements() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()\n"
            + " case a\n"
            + "  1:\n"
            + "   if c == 2\n"
            + "     b:=1\n"
            + "   else\n"
            + "     b:=5\n"
            + "  2:\n"
            + "   case b\n"
            + "    1: b:=2\n"
            + "  other:\n"
            + "   b:=3\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    case a\n"
            + "        1:\n"
            + "            if c == 2\n"
            + "                b := 1\n"
            + "            else\n"
            + "                b := 5\n"
            + "        2:\n"
            + "            case b\n"
            + "                1:\n"
            + "                    b := 2\n"
            + "        other:\n"
            + "            b := 3\n"
            + "", text);
    }

    @Test
    void testInlinePAsm() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()\n"
            + " a:=1\n"
            + " org\n"
            + "      mov a,#12\n"
            + " if_c drvnot #1\n"
            + " end\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    a := 1\n"
            + "    org\n"
            + "                mov     a, #12\n"
            + "        if_c    drvnot  #1\n"
            + "    end\n"
            + "", text);
    }

    @Test
    void testPAsm() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "DAT\n"
            + "label if_c mov a,#12\n"
            + "");
        Assertions.assertEquals(""
            + "DAT\n"
            + "label   if_c    mov     a, #12\n"
            + "", text);
    }

    @Test
    void testPAsmLabel() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "DAT\n"
            + "label\n"
            + "");
        Assertions.assertEquals(""
            + "DAT\n"
            + "label\n"
            + "", text);
    }

    @Test
    void testPAsmLocalLabel() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "DAT\n"
            + "label\n"
            + ".l1 nop\n"
            + " mov a, #.l1\n"
            + " jmp #\\.l1\n"
            + "");
        Assertions.assertEquals(""
            + "DAT\n"
            + "label\n"
            + ".l1             nop\n"
            + "                mov     a, #.l1\n"
            + "                jmp     #\\.l1\n"
            + "", text);
    }

    @Test
    void testPAsmInstruction() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "DAT\n"
            + " asmclk\n"
            + " mov a,#12\n"
            + "");
        Assertions.assertEquals(""
            + "DAT\n"
            + "                asmclk\n"
            + "                mov     a, #12\n"
            + "", text);
    }

    @Test
    void testPAsmCondition() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "DAT\n"
            + " if_c mov a,#12\n"
            + "");
        Assertions.assertEquals(""
            + "DAT\n"
            + "        if_c    mov     a, #12\n"
            + "", text);
    }

    @Test
    void testPAsmModifier() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "DAT\n"
            + " mov a,#12 wc,wz\n"
            + "");
        Assertions.assertEquals(""
            + "DAT\n"
            + "                mov     a, #12      wc,wz\n"
            + "", text);
    }

    @Test
    void testPAsmImmediateExpressions() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "DAT\n"
            + " mov a,#(12*SHIFT)+1\n"
            + " mov a,##12\n"
            + " wrlong #1,#12\n"
            + " wrlong ##1,##12\n"
            + " jmp #\\12\n"
            + " jmp #\\(12*SHIFT)+1\n"
            + "");
        Assertions.assertEquals(""
            + "DAT\n"
            + "                mov     a, #(12 * SHIFT) + 1\n"
            + "                mov     a, ##12\n"
            + "                wrlong  #1, #12\n"
            + "                wrlong  ##1, ##12\n"
            + "                jmp     #\\12\n"
            + "                jmp     #\\(12 * SHIFT) + 1\n"
            + "", text);
    }

    @Test
    void testPAsmRep() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "DAT\n"
            + " rep @.l,#a\n"
            + "");
        Assertions.assertEquals(""
            + "DAT\n"
            + "                rep     @.l, #a\n"
            + "", text);
    }

    @Test
    void testComment() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "{\n"
            + " Comment\n"
            + "}\n"
            + "");
        Assertions.assertEquals(""
            + "{\n"
            + " Comment\n"
            + "}\n"
            + "", text);
    }

    @Test
    void testSectionComment() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "CON ' Constants\n"
            + "VAR ' Variables\n"
            + "OBJ ' Objects\n"
            + "DAT ' Data\n"
            + "");
        Assertions.assertEquals(""
            + "CON ' Constants\n"
            + "VAR ' Variables\n"
            + "OBJ ' Objects\n"
            + "DAT ' Data\n"
            + "", text);
    }

    @Test
    void testLineComments() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "A=1*2+3 ' Comment\n"
            + "F=1*(2+3) ' Comment\n"
            + "#0,a,b[2],c,d ' Comment\n"
            + "#10[5],e,f ' Comment\n"
            + "");
        Assertions.assertEquals(""
            + "    A = 1 * 2 + 3 ' Comment\n"
            + "    F = 1 * (2 + 3) ' Comment\n"
            + "    #0, a, b[2], c, d ' Comment\n"
            + "    #10[5], e, f ' Comment\n"
            + "", text);
    }

    @Test
    void testInlineBlockComments() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "A=1*2+3{c}\n"
            + "F=1{a}*({b}2+3) ' Comment\n"
            + "");
        Assertions.assertEquals(""
            + "    A = 1 * 2 + 3 {c}\n"
            + "    F = 1 {a} * ( {b} 2 + 3) ' Comment\n"
            + "", text);
    }

    @Test
    void testPAsmDebug() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "DAT\n"
            + " debug(uhex_long(a),udec(b,c,#d))\n"
            + " debug(\"value = \", udec_(a))\n"
            + "");
        Assertions.assertEquals(""
            + "DAT\n"
            + "                debug(uhex_long(a), udec(b, c, #d))\n"
            + "                debug(\"value = \", udec_(a))\n"
            + "", text);
    }

    @Test
    void testColumnZeroBlocks() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()\n"
            + "\n"
            + "case a\n"
            + "  0: a := b\n"
            + "  1: c := d\n"
            + "repeat\n"
            + "  if a<>0\n"
            + "    a := b\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "\n"
            + "    case a\n"
            + "        0:\n"
            + "            a := b\n"
            + "        1:\n"
            + "            c := d\n"
            + "    repeat\n"
            + "        if a <> 0\n"
            + "            a := b\n"
            + "", text);
    }

    @Test
    void testExpressions() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()\n"
            + " a:=++b\n"
            + " a:=b++\n"
            + " a:=-(c*2)\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    a := ++b\n"
            + "    a := b++\n"
            + "    a := -(c * 2)\n"
            + "", text);
    }

}
