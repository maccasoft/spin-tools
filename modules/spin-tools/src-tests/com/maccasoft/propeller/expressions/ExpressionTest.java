/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.expressions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExpressionTest {

    @Test
    void testIsConstant() {
        Expression exp = new NumberLiteral(1);
        Assertions.assertTrue(exp.isConstant());
    }

    @Test
    void testBinaryExpressionIsConstant() {
        Expression exp = new Add(new NumberLiteral(1), new NumberLiteral(2));
        Assertions.assertTrue(exp.isConstant());
    }

    @Test
    void testIdentifierIsConstant() {
        Context context = new Context();
        context.addSymbol("a", new NumberLiteral(1));

        Expression exp = new Identifier("a", context);
        Assertions.assertTrue(exp.isConstant());
    }

    @Test
    void testVariableIsConstant() {
        Context context = new Context();
        context.addSymbol("a", new NumberLiteral(1));

        Expression exp = new Variable("LONG", "a", new NumberLiteral(1), 0);
        Assertions.assertFalse(exp.isConstant());
    }

    @Test
    void testVariableBinaryExpressionIsConstant() {
        Expression exp = new Add(new NumberLiteral(1), new Variable("LONG", "a", new NumberLiteral(1), 0));
        Assertions.assertFalse(exp.isConstant());
    }

    @Test
    void testRev() {
        Expression exp = new Rev(new NumberLiteral("$8005"), new Subtract(new NumberLiteral(16), new NumberLiteral(1)));
        Assertions.assertEquals(0xA001, exp.getNumber().longValue());
    }

    @Test
    void testSar() {
        Expression exp = new Sar(new NumberLiteral(0b10000000000000000000000000000000), new NumberLiteral(8));
        Assertions.assertEquals(0b11111111100000000000000000000000, exp.getNumber().longValue());
    }

    @Test
    void testShiftRight() {
        Expression exp = new ShiftRight(new NumberLiteral(0b10000000000000000000000000000000), new NumberLiteral(8));
        Assertions.assertEquals(0b00000000100000000000000000000000, exp.getNumber().longValue());
    }

    @Test
    void testSignx() {
        Expression exp = new Signx(new NumberLiteral(0b00000000101110000000000000000001), new NumberLiteral(23));
        Assertions.assertEquals(0b11111111101110000000000000000001, exp.getNumber().longValue());
    }

    @Test
    void testZerox() {
        Expression exp = new Zerox(new NumberLiteral(0b11111111111100000000000000000001), new NumberLiteral(23));
        Assertions.assertEquals(0b00000000111100000000000000000001, exp.getNumber().longValue());
    }

}
