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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;

class Spin1CompilerTest {

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

        Spin1Compiler subject = new Spin1Compiler();
        subject.compileConBlock(root.getChild(0));

        Assertions.assertEquals("5_000_000", subject.scope.getSymbol("_XINFREQ").toString());
        Assertions.assertEquals("XTAL1 + PLL16X", subject.scope.getSymbol("_CLKMODE").toString());
    }

}
