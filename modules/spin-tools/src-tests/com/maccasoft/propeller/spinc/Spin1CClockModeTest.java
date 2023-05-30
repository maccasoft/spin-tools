/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;

class Spin1CClockModeTest {

    @Test
    void testDetermineClockDefault() {
        String text = ""
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_0_0_00_000, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(12_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_RCFAST() {
        String text = ""
            + "#define _CLKMODE RCFAST\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_0_0_00_000, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(12_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_RCSLOW() {
        String text = ""
            + "#define _CLKMODE RCSLOW\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_0_0_00_001, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(20_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XINPUT() {
        String text = ""
            + "#define _CLKMODE XINPUT\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_0_1_00_010, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(5_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XINPUT_PLL1X() {
        String text = ""
            + "#define _CLKMODE XINPUT+PLL1X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_00_011, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(5_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XINPUT_PLL2X() {
        String text = ""
            + "#define _CLKMODE XINPUT+PLL2X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_00_100, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(10_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XINPUT_PLL4X() {
        String text = ""
            + "#define _CLKMODE XINPUT+PLL4X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_00_101, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(20_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XINPUT_PLL8X() {
        String text = ""
            + "#define _CLKMODE XINPUT+PLL8X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_00_110, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(40_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XINPUT_PLL16X() {
        String text = ""
            + "#define _CLKMODE XINPUT+PLL16X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_00_111, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(80_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL1_PLL1X() {
        String text = ""
            + "#define _CLKMODE XTAL1+PLL1X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_01_011, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(5_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL1_PLL2X() {
        String text = ""
            + "#define _CLKMODE XTAL1+PLL2X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_01_100, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(10_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL1_PLL4X() {
        String text = ""
            + "#define _CLKMODE XTAL1+PLL4X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_01_101, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(20_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL1_PLL8X() {
        String text = ""
            + "#define _CLKMODE XTAL1+PLL8X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_01_110, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(40_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL1_PLL16X() {
        String text = ""
            + "#define _CLKMODE XTAL1+PLL16X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_01_111, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(80_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL2_PLL1X() {
        String text = ""
            + "#define _CLKMODE XTAL2+PLL1X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_10_011, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(5_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL2_PLL2X() {
        String text = ""
            + "#define _CLKMODE XTAL2+PLL2X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_10_100, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(10_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL2_PLL4X() {
        String text = ""
            + "#define _CLKMODE XTAL2+PLL4X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_10_101, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(20_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL2_PLL8X() {
        String text = ""
            + "#define _CLKMODE XTAL2+PLL8X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_10_110, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(40_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL2_PLL16X() {
        String text = ""
            + "#define _CLKMODE XTAL2+PLL16X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_10_111, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(80_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL3_PLL1X() {
        String text = ""
            + "#define _CLKMODE XTAL3+PLL1X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_11_011, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(5_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL3_PLL2X() {
        String text = ""
            + "#define _CLKMODE XTAL3+PLL2X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_11_100, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(10_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL3_PLL4X() {
        String text = ""
            + "#define _CLKMODE XTAL3+PLL4X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_11_101, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(20_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL3_PLL8X() {
        String text = ""
            + "#define _CLKMODE XTAL3+PLL8X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_11_110, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(40_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

    @Test
    void testDetermineClock_XTAL3_PLL16X() {
        String text = ""
            + "#define _CLKMODE XTAL3+PLL16X\n"
            + "#define _XINFREQ 5_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "}\n"
            + "";

        CTokenStream stream = new CTokenStream(text);
        CParser parser = new CParser(stream);
        Node root = parser.parse();

        Spin1CObjectCompiler subject = new Spin1CObjectCompiler(new Spin1CCompiler(), new File("test.spin"));
        subject.compileObject(root);

        Assertions.assertEquals(0b0_1_1_11_111, subject.getScope().getSymbol("CLKMODE").getNumber().intValue());
        Assertions.assertEquals(80_000_000, subject.getScope().getSymbol("CLKFREQ").getNumber().intValue());
    }

}
