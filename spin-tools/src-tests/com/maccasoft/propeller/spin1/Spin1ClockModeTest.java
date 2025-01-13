/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
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

import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.NumberLiteral;

class Spin1ClockModeTest {

    @Test
    void testDetermineClockDefault() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));

        subject.determineClock();

        Assertions.assertEquals(0b0_0_0_00_000, subject.clkMode);
        Assertions.assertEquals(12_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_RCFAST() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Identifier("RCFAST", subject.getScope()));

        subject.determineClock();

        Assertions.assertEquals(0b0_0_0_00_000, subject.clkMode);
        Assertions.assertEquals(12_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_RCSLOW() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Identifier("RCSLOW", subject.getScope()));

        subject.determineClock();

        Assertions.assertEquals(0b0_0_0_00_001, subject.clkMode);
        Assertions.assertEquals(20_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XINPUT() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Identifier("XINPUT", subject.getScope()));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_0_1_00_010, subject.clkMode);
        Assertions.assertEquals(5_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XINPUT_PLL1X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XINPUT", subject.getScope()), new Identifier("PLL1X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_00_011, subject.clkMode);
        Assertions.assertEquals(5_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XINPUT_PLL2X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XINPUT", subject.getScope()), new Identifier("PLL2X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_00_100, subject.clkMode);
        Assertions.assertEquals(10_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XINPUT_PLL4X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XINPUT", subject.getScope()), new Identifier("PLL4X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_00_101, subject.clkMode);
        Assertions.assertEquals(20_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XINPUT_PLL8X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XINPUT", subject.getScope()), new Identifier("PLL8X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_00_110, subject.clkMode);
        Assertions.assertEquals(40_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XINPUT_PLL16X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XINPUT", subject.getScope()), new Identifier("PLL16X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_00_111, subject.clkMode);
        Assertions.assertEquals(80_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XTAL1_PLL1X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XTAL1", subject.getScope()), new Identifier("PLL1X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_01_011, subject.clkMode);
        Assertions.assertEquals(5_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XTAL1_PLL2X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XTAL1", subject.getScope()), new Identifier("PLL2X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_01_100, subject.clkMode);
        Assertions.assertEquals(10_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XTAL1_PLL4X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XTAL1", subject.getScope()), new Identifier("PLL4X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_01_101, subject.clkMode);
        Assertions.assertEquals(20_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XTAL1_PLL8X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XTAL1", subject.getScope()), new Identifier("PLL8X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_01_110, subject.clkMode);
        Assertions.assertEquals(40_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XTAL1_PLL16X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XTAL1", subject.getScope()), new Identifier("PLL16X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_01_111, subject.clkMode);
        Assertions.assertEquals(80_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XTAL2_PLL1X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XTAL2", subject.getScope()), new Identifier("PLL1X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_10_011, subject.clkMode);
        Assertions.assertEquals(5_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XTAL2_PLL2X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XTAL2", subject.getScope()), new Identifier("PLL2X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_10_100, subject.clkMode);
        Assertions.assertEquals(10_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XTAL2_PLL4X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XTAL2", subject.getScope()), new Identifier("PLL4X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_10_101, subject.clkMode);
        Assertions.assertEquals(20_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XTAL2_PLL8X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XTAL2", subject.getScope()), new Identifier("PLL8X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_10_110, subject.clkMode);
        Assertions.assertEquals(40_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XTAL2_PLL16X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XTAL2", subject.getScope()), new Identifier("PLL16X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_10_111, subject.clkMode);
        Assertions.assertEquals(80_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XTAL3_PLL1X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XTAL3", subject.getScope()), new Identifier("PLL1X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_11_011, subject.clkMode);
        Assertions.assertEquals(5_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XTAL3_PLL2X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XTAL3", subject.getScope()), new Identifier("PLL2X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_11_100, subject.clkMode);
        Assertions.assertEquals(10_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XTAL3_PLL4X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XTAL3", subject.getScope()), new Identifier("PLL4X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_11_101, subject.clkMode);
        Assertions.assertEquals(20_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XTAL3_PLL8X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XTAL3", subject.getScope()), new Identifier("PLL8X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_11_110, subject.clkMode);
        Assertions.assertEquals(40_000_000, subject.clkFreq);
    }

    @Test
    void testDetermineClock_XTAL3_PLL16X() {
        Spin1ObjectCompiler subject = new Spin1ObjectCompiler(new Spin1Compiler(), new File("test.spin"));
        subject.getScope().addSymbol("_CLKMODE", new Add(new Identifier("XTAL3", subject.getScope()), new Identifier("PLL16X", subject.getScope())));
        subject.getScope().addSymbol("_XINFREQ", new NumberLiteral(5_000_000));

        subject.determineClock();

        Assertions.assertEquals(0b0_1_1_11_111, subject.clkMode);
        Assertions.assertEquals(80_000_000, subject.clkFreq);
    }

}
