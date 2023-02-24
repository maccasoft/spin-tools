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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExpressionTest {

    static class SimpleContext implements Context {
        Map<String, Expression> symbols = new HashMap<String, Expression>();

        @Override
        public Expression getSymbol(String name) {
            return symbols.get(name);
        }

        @Override
        public boolean hasSymbol(String name) {
            return symbols.containsKey(name);
        }

        @Override
        public boolean isAddressSet() {
            return true;
        }

        @Override
        public int getAddress() {
            return 0;
        }

        @Override
        public int getObjectAddress() {
            return 0;
        }

        @Override
        public int getMemoryAddress() {
            return 0;
        }

    }

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
        SimpleContext context = new SimpleContext();
        context.symbols.put("a", new NumberLiteral(1));

        Expression exp = new Identifier("a", context);
        Assertions.assertTrue(exp.isConstant());
    }

    @Test
    void testVariableIsConstant() {
        SimpleContext context = new SimpleContext();
        context.symbols.put("a", new NumberLiteral(1));

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
