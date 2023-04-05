/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CTokenStreamTest {

    @Test
    void testDecimalNumber() {
        CTokenStream subject = new CTokenStream("1.234");

        assertEquals("1.234", subject.nextToken().getText());
    }

    @Test
    void testAssign() {
        CTokenStream subject = new CTokenStream("a = b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testAdd() {
        CTokenStream subject = new CTokenStream("a + b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("+", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testAddAssign() {
        CTokenStream subject = new CTokenStream("a += b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("+=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testSub() {
        CTokenStream subject = new CTokenStream("a - b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("-", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testSubAssign() {
        CTokenStream subject = new CTokenStream("a -= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("-=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testMult() {
        CTokenStream subject = new CTokenStream("a * b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("*", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testMultAssign() {
        CTokenStream subject = new CTokenStream("a *= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("*=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testDiv() {
        CTokenStream subject = new CTokenStream("a / b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("/", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testDivAssign() {
        CTokenStream subject = new CTokenStream("a /= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("/=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testMod() {
        CTokenStream subject = new CTokenStream("a % b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("%", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testModAssign() {
        CTokenStream subject = new CTokenStream("a %= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("%=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testIncrement() {
        CTokenStream subject = new CTokenStream("a++");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("++", subject.nextToken().getText());
    }

    @Test
    void testDecrement() {
        CTokenStream subject = new CTokenStream("a--");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("--", subject.nextToken().getText());
    }

    @Test
    void testAnd() {
        CTokenStream subject = new CTokenStream("a & b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("&", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testAndAssign() {
        CTokenStream subject = new CTokenStream("a &= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("&=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testOr() {
        CTokenStream subject = new CTokenStream("a | b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("|", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testOrAssign() {
        CTokenStream subject = new CTokenStream("a |= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("|=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testXor() {
        CTokenStream subject = new CTokenStream("a ^ b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("^", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testXorAssign() {
        CTokenStream subject = new CTokenStream("a ^= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("^=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testShiftRight() {
        CTokenStream subject = new CTokenStream("a >> b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(">>", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testShiftRightAssign() {
        CTokenStream subject = new CTokenStream("a >>= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(">>=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testShiftLeft() {
        CTokenStream subject = new CTokenStream("a << b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<<", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testShiftLeftAssign() {
        CTokenStream subject = new CTokenStream("a <<= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<<=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testEqual() {
        CTokenStream subject = new CTokenStream("a == b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("==", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testNotEqual() {
        CTokenStream subject = new CTokenStream("a != b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("!=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testGreaterThan() {
        CTokenStream subject = new CTokenStream("a > b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(">", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testGreaterThanEqual() {
        CTokenStream subject = new CTokenStream("a >= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(">=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testLessThan() {
        CTokenStream subject = new CTokenStream("a < b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testLessThanEqual() {
        CTokenStream subject = new CTokenStream("a <= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testLogicalAnd() {
        CTokenStream subject = new CTokenStream("a && b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("&&", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testLogicalOr() {
        CTokenStream subject = new CTokenStream("a || b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("||", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testLogicalNot() {
        CTokenStream subject = new CTokenStream("!a");

        assertEquals("!", subject.nextToken().getText());
        assertEquals("a", subject.nextToken().getText());
    }

}
