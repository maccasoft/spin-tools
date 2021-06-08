/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Spin2TokenStreamTest {

    @Test
    void testKeyword() {
        Spin2TokenStream subject = new Spin2TokenStream(""
            + "CON  EnableFlow = 8                'single assignments\n"
            + "     DisableFlow = 4\n"
            + "     ColorBurstFreq = 3_579_545\n"
            + "");

        assertEquals("CON", subject.nextToken().getText());
        assertEquals("EnableFlow", subject.nextToken().getText());
        assertEquals("=", subject.nextToken().getText());
        assertEquals("8", subject.nextToken().getText());
        assertEquals(Spin2TokenStream.NL, subject.nextToken().type);

        assertEquals("DisableFlow", subject.nextToken().getText());
        assertEquals("=", subject.nextToken().getText());
        assertEquals("4", subject.nextToken().getText());
        assertEquals(Spin2TokenStream.NL, subject.nextToken().type);

        assertEquals("ColorBurstFreq", subject.nextToken().getText());
        assertEquals("=", subject.nextToken().getText());
        assertEquals("3_579_545", subject.nextToken().getText());
        assertEquals(Spin2TokenStream.NL, subject.nextToken().type);

        assertEquals(Spin2TokenStream.EOF, subject.nextToken().type);
    }

    @Test
    void testLineCount() {
        Spin2TokenStream subject = new Spin2TokenStream(""
            + "CON  EnableFlow = 8                'single assignments\n"
            + "     DisableFlow = 4\n"
            + "     ColorBurstFreq = 3_579_545\n"
            + "");

        assertEquals(0, subject.nextToken().line);
        assertEquals(0, subject.nextToken().line);
        assertEquals(0, subject.nextToken().line);
        assertEquals(0, subject.nextToken().line);
        //assertEquals(0, subject.nextToken().line);
        assertEquals(0, subject.nextToken().line);

        assertEquals(1, subject.nextToken().line);
        assertEquals(1, subject.nextToken().line);
        assertEquals(1, subject.nextToken().line);
        assertEquals(1, subject.nextToken().line);

        assertEquals(2, subject.nextToken().line);
        assertEquals(2, subject.nextToken().line);
        assertEquals(2, subject.nextToken().line);
        assertEquals(2, subject.nextToken().line);

        assertEquals(Spin2TokenStream.EOF, subject.nextToken().type);
    }

    @Test
    void testColumnCount() {
        Spin2TokenStream subject = new Spin2TokenStream(""
            + "CON  EnableFlow = 8                'single assignments\n"
            + "     DisableFlow = 4\n"
            + "     ColorBurstFreq = 3_579_545\n"
            + "");

        assertEquals(0, subject.nextToken().column); // CON
        assertEquals(5, subject.nextToken().column); // EnableFlow
        assertEquals(16, subject.nextToken().column); // =
        assertEquals(18, subject.nextToken().column); // 8
        assertEquals(55, subject.nextToken().column); // NL

        assertEquals(5, subject.nextToken().column); // DisableFlow
        assertEquals(17, subject.nextToken().column); // =
        assertEquals(19, subject.nextToken().column); // 4
        assertEquals(20, subject.nextToken().column); // NL

        assertEquals(5, subject.nextToken().column); // ColorBurstFreq
        assertEquals(20, subject.nextToken().column); // =
        assertEquals(22, subject.nextToken().column); // 3_579_545
        assertEquals(31, subject.nextToken().column); // NL
    }

    @Test
    void testGroupConsecutiveEndOfLine() {
        Spin2TokenStream subject = new Spin2TokenStream(""
            + "\n"
            + "\r\n"
            + "\n"
            + "\n"
            + "");

        assertEquals("\n\r\n\n\n", subject.nextToken().getText());
    }

    @Test
    void testGroupCRLF() {
        Spin2TokenStream subject = new Spin2TokenStream(""
            + " \n"
            + " \r\n"
            + "");

        assertEquals("\n", subject.nextToken().getText());
        assertEquals("\r\n", subject.nextToken().getText());
    }

    @Test
    void testOneCharacterOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("+-*/");

        assertEquals("+", subject.nextToken().getText());
        assertEquals("-", subject.nextToken().getText());
        assertEquals("*", subject.nextToken().getText());
        assertEquals("/", subject.nextToken().getText());
    }

    @Test
    void testTwoCharactersOperator() {
        Spin2TokenStream subject = new Spin2TokenStream(":=+=-=*=/=//||&&^^+/+<<>");

        assertEquals(":=", subject.nextToken().getText());
        assertEquals("+=", subject.nextToken().getText());
        assertEquals("-=", subject.nextToken().getText());
        assertEquals("*=", subject.nextToken().getText());
        assertEquals("/=", subject.nextToken().getText());
        assertEquals("//", subject.nextToken().getText());
        assertEquals("||", subject.nextToken().getText());
        assertEquals("&&", subject.nextToken().getText());
        assertEquals("^^", subject.nextToken().getText());
        assertEquals("+/", subject.nextToken().getText());
        assertEquals("+<", subject.nextToken().getText());
        assertEquals("<>", subject.nextToken().getText());
    }

    @Test
    void testThreeCharactersOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("<<=>>=+//<=>");

        assertEquals("<<=", subject.nextToken().getText());
        assertEquals(">>=", subject.nextToken().getText());
        assertEquals("+//", subject.nextToken().getText());
        assertEquals("<=>", subject.nextToken().getText());
    }

    @Test
    void testPasmPrefix() {
        Spin2TokenStream subject = new Spin2TokenStream("##@label");

        assertEquals("##", subject.nextToken().getText());
        assertEquals("@label", subject.nextToken().getText());
    }

    @Test
    void testLocalLabel() {
        Spin2TokenStream subject = new Spin2TokenStream(".label @.label");

        assertEquals(".label", subject.nextToken().getText());
        assertEquals("@.label", subject.nextToken().getText());
    }

    @Test
    void testDebugStatement() {
        Spin2TokenStream subject = new Spin2TokenStream("    debug(`s `uhex_long_array_(@buff, 512) `dly(50))  'show data\n");

        assertEquals("debug", subject.nextToken().getText());
        assertEquals("(", subject.nextToken().getText());
        assertEquals("`s `uhex_long_array_(@buff, 512) `dly(50)", subject.nextToken().getText());
        assertEquals(")", subject.nextToken().getText());
    }

}
