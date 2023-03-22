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
    void testShiftRightOperator() {
        CTokenStream subject = new CTokenStream("a >> b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(">>", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testShiftRightAssignmentOperator() {
        CTokenStream subject = new CTokenStream("a >>= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(">>=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testShiftLeftOperator() {
        CTokenStream subject = new CTokenStream("a << b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<<", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testShiftLeftAssignmentOperator() {
        CTokenStream subject = new CTokenStream("a <<= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<<=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

}
