/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.spin1.Spin1TokenStream;

class TokenTest {

    @Test
    void testMerge() {
        Spin1TokenStream stream = new Spin1TokenStream("\n    @a");
        stream.nextToken(); // NL
        Token token1 = stream.nextToken(); // @
        Token token2 = stream.nextToken(); // a
        Token result = token1.merge(token2);
        Assertions.assertEquals(token1.column, result.column);
        Assertions.assertEquals(token1.line, result.line);
        Assertions.assertEquals(token1.start, result.start);
        Assertions.assertEquals(token2.stop, result.stop);
    }

    @Test
    void testSubstringStart() {
        Spin1TokenStream stream = new Spin1TokenStream("\n    prefix.suffix");
        stream.nextToken(); // NL
        Token token = stream.nextToken();
        Assertions.assertEquals("prefix.suffix", token.getText());

        Token result = token.substring(6);
        Assertions.assertEquals(".suffix", result.getText());
        Assertions.assertEquals(token.column + 6, result.column);
        Assertions.assertEquals(token.line, result.line);
        Assertions.assertEquals(token.start + 6, result.start);
        Assertions.assertEquals(token.stop, result.stop);
    }

    @Test
    void testSubstringStartStop() {
        Spin1TokenStream stream = new Spin1TokenStream("\n    prefix.middle.suffix");
        stream.nextToken(); // NL
        Token token = stream.nextToken();
        Assertions.assertEquals("prefix.middle.suffix", token.getText());

        Token result = token.substring(6, 12);
        Assertions.assertEquals(".middle", result.getText());
        Assertions.assertEquals(token.column + 6, result.column);
        Assertions.assertEquals(token.line, result.line);
        Assertions.assertEquals(token.start + 6, result.start);
        Assertions.assertEquals(token.start + 12, result.stop);
    }

}
