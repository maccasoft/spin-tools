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

class FormatterTest {

    @Test
    void testFormatDatLine() {
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
    void testFormatDatLabelOnly() {
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
    void testFormatDatInstructionOnly() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "DAT\n"
            + " asmclk\n"
            + "");
        Assertions.assertEquals(""
            + "DAT\n"
            + "                asmclk\n"
            + "", text);
    }

    @Test
    void testFormatDatLargeLabels() {
        Formatter subject = new Spin2Formatter();
        subject.setIsolateLargeLabels(true);
        String text = subject.format(""
            + "DAT\n"
            + "label if_c mov a,#12\n"
            + "large_label mov b,#34\n"
            + "very_large_label_1 mov b,#34\n"
            + "");
        Assertions.assertEquals(""
            + "DAT\n"
            + "label   if_c    mov     a, #12\n"
            + "large_label     mov     b, #34\n"
            + "very_large_label_1\n"
            + "                mov     b, #34\n"
            + "", text);
    }

    @Test
    void testFormatDatLargeCondition() {
        Formatter subject = new Spin2Formatter();
        subject.setIsolateLargeLabels(true);
        subject.setAdjustPAsmColumns(true);
        String text = subject.format(""
            + "DAT\n"
            + " if_z_and_nc mov a,#12\n"
            + "");
        Assertions.assertEquals(""
            + "DAT\n"
            + "        if_z_and_nc mov     a, #12\n"
            + "", text);
    }

    @Test
    void testFormatDatLargeLabelAndCondition() {
        Formatter subject = new Spin2Formatter();
        subject.setIsolateLargeLabels(true);
        subject.setAdjustPAsmColumns(true);
        String text = subject.format(""
            + "DAT\n"
            + "very_large_label_1\n"
            + " if_z_and_nc mov a,#12\n"
            + "");
        Assertions.assertEquals(""
            + "DAT\n"
            + "very_large_label_1\n"
            + "        if_z_and_nc mov     a, #12\n"
            + "", text);
    }

    @Test
    void testFormatDatLocalLabels() {
        Formatter subject = new Spin2Formatter();
        subject.setIsolateLargeLabels(true);
        String text = subject.format(""
            + "DAT\n"
            + "label if_c mov a,#12\n"
            + ".local mov b,#34\n"
            + "");
        Assertions.assertEquals(""
            + "DAT\n"
            + "label   if_c    mov     a, #12\n"
            + ".local          mov     b, #34\n"
            + "", text);
    }

    @Test
    void testFormatDatLargeImmediate() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "DAT\n"
            + " mov a,##12\n"
            + "");
        Assertions.assertEquals(""
            + "DAT\n"
            + "                mov     a, ##12\n"
            + "", text);
    }

    @Test
    void testFormatDatEffect() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "DAT\n"
            + "label if_c mov a,#12 wc\n"
            + " if_c mov a,#12 wc\n"
            + "");
        Assertions.assertEquals(""
            + "DAT\n"
            + "label   if_c    mov     a, #12      wc\n"
            + "        if_c    mov     a, #12      wc\n"
            + "", text);
    }

    @Test
    void testFormatAdjustDatEffect() {
        Formatter subject = new Spin2Formatter();
        subject.setAdjustPAsmColumns(true);
        String text = subject.format(""
            + "DAT\n"
            + "label if_c mov a,#12 wc\n"
            + "large_label if_c mov a,#12 wc\n"
            + "");
        Assertions.assertEquals(""
            + "DAT\n"
            + "label       if_c    mov     a, #12      wc\n"
            + "large_label if_c    mov     a, #12      wc\n"
            + "", text);
    }

    @Test
    void testFormatDatOrgLine() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "DAT org $000,$1F0\n"
            + "");
        Assertions.assertEquals(""
            + "DAT             org     $000, $1F0\n"
            + "", text);
    }

    @Test
    void testFormatConstant() {
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
    void testDefaultFormatConstant() {
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
    void testFormatVariables() {
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
    void testFormatMethod() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start\n"
            + "", text);
    }

    @Test
    void testFormatMethodParameters() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start(a,b,c)\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start(a, b, c)\n"
            + "", text);
    }

    @Test
    void testFormatMethodReturns() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start():a\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start() : a\n"
            + "", text);
    }

    @Test
    void testFormatMethodVariables() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()|a,b\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start() | a, b\n"
            + "", text);
    }

    @Test
    void testFormatMethodFull() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start(a,b):c|d,e\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start(a, b) : c | d, e\n"
            + "", text);
    }

    @Test
    void testFormatFunctionCall() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()\n"
            + " function(a,b,c)\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    function(a, b, c)\n"
            + "", text);
    }

    @Test
    void testFormatObjectFunctionCall() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()\n"
            + " obj.function(a,b,c)\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    obj.function(a, b, c)\n"
            + "", text);
    }

    @Test
    void testFormatObjectConstant() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()\n"
            + " function(a,obj#b,obj.c)\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    function(a, obj#b, obj.c)\n"
            + "", text);
    }

    @Test
    void testFormatStatements() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()\n"
            + " a:=1\n"
            + " b:=b*(c+d)\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    a := 1\n"
            + "    b := b * (c + d)\n"
            + "", text);
    }

    @Test
    void testFormatIndentedStatements() {
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
    void testFormatIndentedBlocks() {
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
    void testFormatMultipleMethods() {
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
    void testFormatComments() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "' line comment\n"
            + "{ block comment }\n"
            + "");
        Assertions.assertEquals(""
            + "' line comment\n"
            + "{ block comment }\n"
            + "", text);
    }

    @Test
    void testFormatSectionComments() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "CON ' line comment\n"
            + "VAR { block comment }\n"
            + "PUB start({no args})\n"
            + "DAT ' section\n"
            + "");
        Assertions.assertEquals(""
            + "CON ' line comment\n"
            + "\n"
            + "VAR { block comment }\n"
            + "\n"
            + "PUB start({no args})\n"
            + "\n"
            + "DAT ' section\n"
            + "", text);
    }

    @Test
    void testFormatInlinePAsm() {
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
    void testKeepBlankLines() {
        Formatter subject = new Spin2Formatter();
        subject.setKeepBlankLines(true);
        String text = subject.format(""
            + "CON\n"
            + "A=1*2+3\n"
            + "F=1*(2+3)\n"
            + "\n"
            + "#0,a,b[2],c,d\n"
            + "#10[5],e,f\n"
            + "");
        Assertions.assertEquals(""
            + "CON\n"
            + "    A = 1 * 2 + 3\n"
            + "    F = 1 * (2 + 3)\n"
            + "\n"
            + "    #0, a, b[2], c, d\n"
            + "    #10[5], e, f\n"
            + "", text);
    }

    @Test
    void testKeepInlinePAsmBlankLines() {
        Formatter subject = new Spin2Formatter();
        subject.setKeepBlankLines(true);
        String text = subject.format(""
            + "PUB start()\n"
            + "\n"
            + " a:=1\n"
            + "\n"
            + " org\n"
            + "\n"
            + "      mov a,#12\n"
            + " if_c drvnot #1\n"
            + "\n"
            + " end\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "    org\n"
            + "\n"
            + "                mov     a, #12\n"
            + "        if_c    drvnot  #1\n"
            + "\n"
            + "    end\n"
            + "", text);
    }

    @Test
    void testFormatStatementComments() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()\n"
            + " ' comment\n"
            + " a:=1\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    ' comment\n"
            + "    a := 1\n"
            + "", text);
    }

    @Test
    void testFormatIndentedStatementComments() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()\n"
            + " repeat\n"
            + "   ' comment\n"
            + "   a:=1\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    repeat\n"
            + "        ' comment\n"
            + "        a := 1\n"
            + "", text);
    }

    @Test
    void testFormatStatementLineComments() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()\n"
            + " a:=1 ' comment\n"
            + " a:=2 { comment }\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    a := 1  ' comment\n"
            + "    a := 2  { comment }\n"
            + "", text);
    }

    @Test
    void testFormatStatementInlineComments() {
        Formatter subject = new Spin2Formatter();
        String text = subject.format(""
            + "PUB start()\n"
            + " a:=2{a}*3{b}+c\n"
            + "");
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    a := 2{a} * 3{b} + c\n"
            + "", text);
    }

    @Test
    void testFormatCaseStatements() {
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
    void testFormatCaseRangeStatements() {
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
    void testFormatInlineCaseStatements() {
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
            + "        1:  b := 1\n"
            + "        2:  b := 2\n"
            + "            c := 4\n"
            + "        other:\n"
            + "            b := 3\n"
            + "", text);
    }

    @Test
    void testFormatNestedCaseBlocks() {
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
            + "                1:  b := 2\n"
            + "        other:\n"
            + "            b := 3\n"
            + "", text);
    }

}
