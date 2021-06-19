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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;

class Spin1ExpressionBuilderTest {

    @Test
    void testBasicOperatorPrecedence() {
        Spin1TokenStream stream = new Spin1TokenStream("1 + 2 * 3");

        Spin1ExpressionBuilder subject = new Spin1ExpressionBuilder();
        subject.addValueToken(stream.nextToken()); // 1
        subject.addOperatorToken(stream.nextToken()); // +
        subject.addValueToken(stream.nextToken()); // 2
        subject.addOperatorToken(stream.nextToken()); // *
        subject.addValueToken(stream.nextToken()); // 3

        Node root = subject.getExpression();

        Assertions.assertEquals("+", root.getToken(0).getText());
        Assertions.assertEquals("*", root.getChild(0).getToken(0).getText());
        Assertions.assertEquals("3", root.getChild(0).getChild(0).getToken(0).getText());
        Assertions.assertEquals("2", root.getChild(0).getChild(1).getToken(0).getText());
        Assertions.assertEquals("1", root.getChild(1).getToken(0).getText());
    }

    @Test
    void testGroupOperatorPrecedence() {
        Spin1TokenStream stream = new Spin1TokenStream("(1 + 2) * 3");

        Spin1ExpressionBuilder subject = new Spin1ExpressionBuilder();
        subject.addOperatorToken(stream.nextToken()); // (
        subject.addValueToken(stream.nextToken()); // 1
        subject.addOperatorToken(stream.nextToken()); // +
        subject.addValueToken(stream.nextToken()); // 2
        subject.addOperatorToken(stream.nextToken()); // )
        subject.addOperatorToken(stream.nextToken()); // *
        subject.addValueToken(stream.nextToken()); // 3

        Node root = subject.getExpression();

        Assertions.assertEquals("*", root.getToken(0).getText());
        Assertions.assertEquals("3", root.getChild(0).getToken(0).getText());
        Assertions.assertEquals("(", root.getChild(1).getToken(0).getText());
        Assertions.assertEquals("+", root.getChild(1).getChild(0).getToken(0).getText());
        Assertions.assertEquals("2", root.getChild(1).getChild(0).getChild(0).getToken(0).getText());
        Assertions.assertEquals("1", root.getChild(1).getChild(0).getChild(1).getToken(0).getText());
    }

}
