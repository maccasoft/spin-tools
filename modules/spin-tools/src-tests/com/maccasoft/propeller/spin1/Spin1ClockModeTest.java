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

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.model.Node;

class Spin1ClockModeTest {

    @Test
    void testCompileConstants() {
        String text = ""
            + "CON\n"
            + "\n"
            + "    _XINFREQ = 5_000_000\n"
            + "    _CLKMODE = XTAL1 + PLL16X\n"
            + "";

        Spin1TokenStream stream = new Spin1TokenStream(text);
        Spin1Parser parser = new Spin1Parser(stream);
        Node root = parser.parse();

        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.compileConBlock(root.getChild(0));

        Assertions.assertEquals("5_000_000", subject.scope.getSymbol("_XINFREQ").toString());
        Assertions.assertEquals("XTAL1 + PLL16X", subject.scope.getSymbol("_CLKMODE").toString());
    }

    @Test
    void testDetermineClockDefault() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());

        subject.determineClock();

        Assertions.assertEquals(0b0_0_0_00_000, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(12_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_RCFAST() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Identifier("RCFAST", subject.scope));

        subject.determineClock();

        Assertions.assertEquals(0b0_0_0_00_000, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(12_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_RCSLOW() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Identifier("RCSLOW", subject.scope));

        subject.determineClock();

        Assertions.assertEquals(0b0_0_0_00_001, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(20_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XINPUT() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Identifier("XINPUT", subject.scope));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_0_1_00_010, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(5_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XINPUT_PLL1X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XINPUT", subject.scope), new Identifier("PLL1X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_00_011, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(5_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XINPUT_PLL2X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XINPUT", subject.scope), new Identifier("PLL2X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_00_100, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(10_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XINPUT_PLL4X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XINPUT", subject.scope), new Identifier("PLL4X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_00_101, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(20_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XINPUT_PLL8X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XINPUT", subject.scope), new Identifier("PLL8X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_00_110, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(40_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XINPUT_PLL16X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XINPUT", subject.scope), new Identifier("PLL16X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_00_111, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(80_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL1_PLL1X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XTAL1", subject.scope), new Identifier("PLL1X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_01_011, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(5_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL1_PLL2X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XTAL1", subject.scope), new Identifier("PLL2X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_01_100, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(10_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL1_PLL4X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XTAL1", subject.scope), new Identifier("PLL4X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_01_101, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(20_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL1_PLL8X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XTAL1", subject.scope), new Identifier("PLL8X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_01_110, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(40_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL1_PLL16X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XTAL1", subject.scope), new Identifier("PLL16X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_01_111, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(80_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL2_PLL1X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XTAL2", subject.scope), new Identifier("PLL1X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_10_011, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(5_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL2_PLL2X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XTAL2", subject.scope), new Identifier("PLL2X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_10_100, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(10_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL2_PLL4X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XTAL2", subject.scope), new Identifier("PLL4X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_10_101, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(20_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL2_PLL8X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XTAL2", subject.scope), new Identifier("PLL8X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_10_110, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(40_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL2_PLL16X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XTAL2", subject.scope), new Identifier("PLL16X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_10_111, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(80_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL3_PLL1X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XTAL3", subject.scope), new Identifier("PLL1X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_11_011, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(5_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL3_PLL2X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XTAL3", subject.scope), new Identifier("PLL2X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_11_100, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(10_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL3_PLL4X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XTAL3", subject.scope), new Identifier("PLL4X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_11_101, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(20_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL3_PLL8X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XTAL3", subject.scope), new Identifier("PLL8X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_11_110, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(40_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL3_PLL16X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1GlobalContext(), Collections.emptyMap());
        subject.scope.addSymbol("_CLKMODE", new Add(new Identifier("XTAL3", subject.scope), new Identifier("PLL16X", subject.scope)));
        subject.scope.addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_11_111, subject.scope.getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(80_000_000, subject.scope.getSymbol("CLKFREQ").getNumber().intValue());
    }

}
