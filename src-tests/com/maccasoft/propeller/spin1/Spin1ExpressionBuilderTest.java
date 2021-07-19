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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.model.Token;

class Spin1ExpressionBuilderTest {

    @Test
    void testOperatorsPrecedence() {
        String text = "1 + 2 * 3";
        Expression expression = parse(text);
        Assertions.assertEquals(text, expression.toString());
        Assertions.assertEquals(new Long(7), expression.getNumber());
    }

    @Test
    void testOperatorsSequence() {
        String text = "16 / 2 / 2";
        Expression expression = parse(text);
        Assertions.assertEquals(text, expression.toString());
        Assertions.assertEquals(new Long(4), expression.getNumber());
    }

    @Test
    void testTernary() {
        String text = "b ? c : d";
        Expression expression = parse(text);
        Assertions.assertEquals(text, expression.toString());
    }

    @Test
    void testTernaryExpression() {
        String text = "b > 1 ? c + 1 : d + e * 4";
        Expression expression = parse(text);
        Assertions.assertEquals(text, expression.toString());
    }

    @Test
    void testExpression() {
        String text = "-3 & $1F";
        Expression expression = parse(text);
        Assertions.assertEquals(text, expression.toString());
        Assertions.assertEquals(new Long(0x1D), expression.getNumber());
    }

    Expression parse(String text) {
        List<Token> tokens = new ArrayList<Token>();

        Spin1TokenStream stream = new Spin1TokenStream(text);
        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF) {
                break;
            }
            tokens.add(token);
        }

        Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(new Spin1Context(), tokens);
        return builder.getExpression();
    }

}
