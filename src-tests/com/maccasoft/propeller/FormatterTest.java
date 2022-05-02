/*
 * Copyright (c) 2022 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.spin2.Spin2TokenStream;

class FormatterTest {

    @Test
    void testFormatDatLine() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "DAT\nlabel if_c mov a,#12\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "DAT\n"
            + "label   if_c    mov     a, #12\n", subject.format(stream));
    }

    @Test
    void testFormatDatLabelOnly() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "DAT\nlabel\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "DAT\n"
            + "label\n", subject.format(stream));
    }

    @Test
    void testFormatDatInstructionOnly() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "DAT\n asmclk\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "DAT\n"
            + "                asmclk\n", subject.format(stream));
    }

    @Test
    void testFormatDatLargeLabels() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "DAT\n"
            + "label if_c mov a,#12\n"
            + "large_label mov b,#34\n"
            + "very_large_label_1 mov b,#34\n"
            + "");
        Formatter subject = new Formatter();
        subject.isolateLargeLabels = true;
        Assertions.assertEquals(""
            + "DAT\n"
            + "label   if_c    mov     a, #12\n"
            + "large_label     mov     b, #34\n"
            + "very_large_label_1\n"
            + "                mov     b, #34\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatDatLargeCondition() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "DAT\n"
            + " if_z_and_nc mov a,#12\n"
            + "");
        Formatter subject = new Formatter();
        subject.isolateLargeLabels = true;
        Assertions.assertEquals(""
            + "DAT\n"
            + "        if_z_and_nc mov     a, #12\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatDatLocalLabels() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "DAT\n"
            + "label if_c mov a,#12\n"
            + ".local mov b,#34\n"
            + "");
        Formatter subject = new Formatter();
        subject.isolateLargeLabels = true;
        Assertions.assertEquals(""
            + "DAT\n"
            + "label   if_c    mov     a, #12\n"
            + ".local          mov     b, #34\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatDatLargeImmediate() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "DAT\n mov a,##12\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "DAT\n"
            + "                mov     a, ##12\n", subject.format(stream));
    }

    @Test
    void testFormatConstant() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "CON\n"
            + "A=1*2+3\n"
            + "F=1*(2+3)\n"
            + "#0,a,b[2],c,d\n"
            + "#10[5],e,f\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "CON\n"
            + "    A = 1 * 2 + 3\n"
            + "    F = 1 * (2 + 3)\n"
            + "    #0, a, b[2], c, d\n"
            + "    #10[5], e, f\n"
            + "", subject.format(stream));
    }

    @Test
    void testDefaultFormatConstant() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "A=1*2+3\n"
            + "F=1*(2+3)\n"
            + "#0,a,b[2],c,d\n"
            + "#10[5],e,f\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "    A = 1 * 2 + 3\n"
            + "    F = 1 * (2 + 3)\n"
            + "    #0, a, b[2], c, d\n"
            + "    #10[5], e, f\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatVariables() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "VAR\n"
            + "long A\n"
            + " word b,c\n"
            + "byte d[12], e[100], f[1*5]\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "VAR\n"
            + "    long A\n"
            + "    word b, c\n"
            + "    byte d[12], e[100], f[1 * 5]\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatMethod() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "PUB start\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "PUB start\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatMethodParameters() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "PUB start(a,b,c)\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "PUB start(a, b, c)\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatMethodReturns() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "PUB start():a\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "PUB start() : a\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatMethodVariables() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "PUB start()|a,b\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "PUB start() | a, b\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatMethodFull() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "PUB start(a,b):c|d,e\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "PUB start(a, b) : c | d, e\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatFunctionCall() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "PUB start()\n"
            + " function(a,b,c)\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    function(a, b, c)\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatObjectFunctionCall() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "PUB start()\n"
            + " obj.function(a,b,c)\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    obj.function(a, b, c)\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatObjectConstant() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "PUB start()\n"
            + " function(a,obj#b,obj.c)\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    function(a, obj#b, obj.c)\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatStatements() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "PUB start()\n"
            + " a:=1\n"
            + " b:=b*(c+d)\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    a := 1\n"
            + "    b := b * (c + d)\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatIndentedStatements() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "PUB start()\n"
            + " a:=1\n"
            + "  b:=b*(c+d)\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    a := 1\n"
            + "    b := b * (c + d)\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatIndentedBlocks() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "PUB start()\n"
            + " a:=1\n"
            + " if z==1\n"
            + "  b:=b*(c+d)\n"
            + "  e:=$12\n"
            + " f:=2\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    a := 1\n"
            + "    if z == 1\n"
            + "        b := b * (c + d)\n"
            + "        e := $12\n"
            + "    f := 2\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatMultipleMethods() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "PUB start()\n"
            + " a:=1\n"
            + "PRI init()\n"
            + "  b:=b*(c+d)\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    a := 1\n"
            + "\n"
            + "PRI init()\n"
            + "    b := b * (c + d)\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatComments() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "' line comment\n"
            + "{ block comment }\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "' line comment\n"
            + "{ block comment }\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatSectionComments() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "CON ' line comment\n"
            + "VAR { block comment }\n"
            + "PUB start({no args})\n"
            + "DAT ' section\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "CON ' line comment\n"
            + "\n"
            + "VAR { block comment }\n"
            + "\n"
            + "PUB start({no args})\n"
            + "\n"
            + "DAT ' section\n"
            + "", subject.format(stream));
    }

    @Test
    void testFormatInlinePAsm() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "PUB start()\n"
            + " a:=1\n"
            + " org\n"
            + "      mov a,#12\n"
            + " if_c drvnot #1\n"
            + " end\n"
            + "");
        Formatter subject = new Formatter();
        Assertions.assertEquals(""
            + "PUB start()\n"
            + "    a := 1\n"
            + "    org\n"
            + "                mov     a, #12\n"
            + "        if_c    drvnot  #1\n"
            + "    end\n"
            + "", subject.format(stream));
    }

    @Test
    void testKeepBlankLines() {
        Spin2TokenStream stream = new Spin2TokenStream(""
            + "CON\n"
            + "A=1*2+3\n"
            + "F=1*(2+3)\n"
            + "\n"
            + "#0,a,b[2],c,d\n"
            + "#10[5],e,f\n"
            + "");
        Formatter subject = new Formatter();
        subject.keepBlankLines = true;
        Assertions.assertEquals(""
            + "CON\n"
            + "    A = 1 * 2 + 3\n"
            + "    F = 1 * (2 + 3)\n"
            + "\n"
            + "    #0, a, b[2], c, d\n"
            + "    #10[5], e, f\n"
            + "", subject.format(stream));
    }

}
