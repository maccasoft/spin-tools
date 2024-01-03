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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.expressions.NumberLiteral;

class Spin1GlobalContextTest {

    @Test
    void testBuiltInSymbols() {
        Spin1GlobalContext subject = new Spin1GlobalContext(false);
        Assertions.assertNotNull(subject.getLocalSymbol("OUTA"));
        Assertions.assertNotNull(subject.getLocalSymbol("outa"));
        Assertions.assertNotNull(subject.getLocalSymbol("OutA"));
    }

    @Test
    void testCaseSensitiveBuiltInSymbols() {
        Spin1GlobalContext subject = new Spin1GlobalContext(true);
        Assertions.assertNotNull(subject.getLocalSymbol("OUTA"));
        Assertions.assertNotNull(subject.getLocalSymbol("outa"));
        Assertions.assertNotNull(subject.getLocalSymbol("OutA"));
    }

    @Test
    void testSymbols() {
        Spin1GlobalContext subject = new Spin1GlobalContext(false);

        subject.addSymbol("SYMBOL", new NumberLiteral(1));

        Assertions.assertNotNull(subject.getLocalSymbol("SYMBOL"));
        Assertions.assertNotNull(subject.getLocalSymbol("symbol"));
        Assertions.assertNotNull(subject.getLocalSymbol("Symbol"));
    }

    @Test
    void testCaseSensitiveSymbols() {
        Spin1GlobalContext subject = new Spin1GlobalContext(true);

        subject.addSymbol("SYMBOL", new NumberLiteral(1));

        Assertions.assertNotNull(subject.getLocalSymbol("SYMBOL"));
        Assertions.assertNull(subject.getLocalSymbol("symbol"));
        Assertions.assertNull(subject.getLocalSymbol("Symbol"));
    }

    @Test
    void testExistingSymbols() {
        Spin1GlobalContext subject = new Spin1GlobalContext(false);

        subject.addSymbol("SYMBOL", new NumberLiteral(1));

        Assertions.assertThrows(RuntimeException.class, () -> {
            subject.addSymbol("SYMBOL", new NumberLiteral(1));
        });
        Assertions.assertThrows(RuntimeException.class, () -> {
            subject.addSymbol("symbol", new NumberLiteral(1));
        });
        Assertions.assertThrows(RuntimeException.class, () -> {
            subject.addSymbol("Symbol", new NumberLiteral(1));
        });
    }

    @Test
    void testCaseSensitiveExistingSymbols() {
        Spin1GlobalContext subject = new Spin1GlobalContext(true);

        subject.addSymbol("SYMBOL", new NumberLiteral(1));

        Assertions.assertThrows(RuntimeException.class, () -> {
            subject.addSymbol("SYMBOL", new NumberLiteral(1));
        });
        subject.addSymbol("symbol", new NumberLiteral(1));
        subject.addSymbol("Symbol", new NumberLiteral(1));
    }

    @Test
    void testHasSymbols() {
        Spin1GlobalContext subject = new Spin1GlobalContext(false);

        subject.addSymbol("SYMBOL", new NumberLiteral(1));

        Assertions.assertTrue(subject.hasSymbol("SYMBOL"));
        Assertions.assertTrue(subject.hasSymbol("symbol"));
        Assertions.assertTrue(subject.hasSymbol("Symbol"));
    }

    @Test
    void testCaseSensitiveHasSymbols() {
        Spin1GlobalContext subject = new Spin1GlobalContext(true);

        subject.addSymbol("SYMBOL", new NumberLiteral(1));

        Assertions.assertTrue(subject.hasSymbol("SYMBOL"));
        Assertions.assertFalse(subject.hasSymbol("symbol"));
        Assertions.assertFalse(subject.hasSymbol("Symbol"));
    }

}
