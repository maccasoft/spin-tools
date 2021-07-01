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
        public int getAddress() {
            return 0;
        }

        @Override
        public int getHubAddress() {
            return 0;
        }

        @Override
        public int getObjectOffset() {
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

}
