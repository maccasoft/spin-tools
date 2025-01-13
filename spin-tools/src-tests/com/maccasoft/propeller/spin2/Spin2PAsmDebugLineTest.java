/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.model.Token;

class Spin2PAsmDebugLineTest {

    @Test
    void testToString() {
        List<Token> tokens = Arrays.asList(
            new Token(0, "debug"),
            new Token(Token.OPERATOR, "("),
            new Token(0, "udec"),
            new Token(Token.OPERATOR, "("),
            new Token(0, "a"),
            new Token(Token.OPERATOR, ","),
            new Token(0, "b"),
            new Token(Token.OPERATOR, ","),
            new Token(0, "c"),
            new Token(Token.OPERATOR, ")"),
            new Token(Token.OPERATOR, ")"));

        Spin2PAsmDebugLine debugLine = Spin2PAsmDebugLine.buildFrom(new Context(), tokens);

        Assertions.assertEquals("debug(udec(a, b, c))", debugLine.toString());
    }

}
