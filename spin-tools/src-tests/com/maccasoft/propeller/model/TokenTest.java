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

}
