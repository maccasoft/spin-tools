/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Token;

class Spin2TreeBuilderTest {

    @Test
    void testSimpleAssignment() {
        Spin2TreeBuilder builder = new Spin2TreeBuilder();
        builder.addToken(new Token(0, "a"));
        builder.addToken(new Token(Token.OPERATOR, ":="));
        builder.addToken(new Token(Token.NUMBER, "1"));

        Spin2StatementNode root = builder.getRoot();

        assertEquals(":=", root.getText());
        assertEquals(2, root.getChildCount());
        assertEquals("a", root.getChild(0).getText());
        assertEquals(0, root.getChild(0).getChildCount());
        assertEquals("1", root.getChild(1).getText());
        assertEquals(0, root.getChild(1).getChildCount());

        assertEquals("a := 1", root.toString());
    }

    @Test
    void testExpressionAssignment() {
        Spin2TreeBuilder builder = new Spin2TreeBuilder();
        builder.addToken(new Token(0, "a"));
        builder.addToken(new Token(Token.OPERATOR, ":="));
        builder.addToken(new Token(Token.NUMBER, "b"));
        builder.addToken(new Token(Token.OPERATOR, "+"));
        builder.addToken(new Token(Token.NUMBER, "c"));
        builder.addToken(new Token(Token.OPERATOR, "*"));
        builder.addToken(new Token(Token.NUMBER, "3"));

        Spin2StatementNode root = builder.getRoot();

        assertEquals(":=", root.getText());
        assertEquals("a", root.getChild(0).getText());
        assertEquals("+", root.getChild(1).getText());
        assertEquals("b", root.getChild(1).getChild(0).getText());
        assertEquals("*", root.getChild(1).getChild(1).getText());
        assertEquals("c", root.getChild(1).getChild(1).getChild(0).getText());
        assertEquals("3", root.getChild(1).getChild(1).getChild(1).getText());

        assertEquals("a := b + c * 3", root.toString());
    }

    @Test
    void testGroupExpressionAssignment() {
        Spin2TreeBuilder builder = new Spin2TreeBuilder();
        builder.addToken(new Token(0, "a"));
        builder.addToken(new Token(Token.OPERATOR, ":="));
        builder.addToken(new Token(Token.OPERATOR, "("));
        builder.addToken(new Token(Token.NUMBER, "b"));
        builder.addToken(new Token(Token.OPERATOR, "+"));
        builder.addToken(new Token(Token.NUMBER, "c"));
        builder.addToken(new Token(Token.OPERATOR, ")"));
        builder.addToken(new Token(Token.OPERATOR, "*"));
        builder.addToken(new Token(Token.NUMBER, "3"));

        Spin2StatementNode root = builder.getRoot();

        assertEquals(":=", root.getText());
        assertEquals("a", root.getChild(0).getText());
        assertEquals("*", root.getChild(1).getText());
        assertEquals("(", root.getChild(1).getChild(0).getText());
        assertEquals("3", root.getChild(1).getChild(1).getText());
        assertEquals("+", root.getChild(1).getChild(0).getChild(0).getText());
        assertEquals("b", root.getChild(1).getChild(0).getChild(0).getChild(0).getText());
        assertEquals("c", root.getChild(1).getChild(0).getChild(0).getChild(1).getText());

        assertEquals("a := (b + c) * 3", root.toString());
    }

    @Test
    void testFunction() {
        Spin2TreeBuilder builder = new Spin2TreeBuilder();
        builder.addToken(new Token(0, "a"));
        builder.addToken(new Token(Token.OPERATOR, "("));
        builder.addToken(new Token(Token.OPERATOR, ")"));

        Spin2StatementNode root = builder.getRoot();

        assertEquals("a", root.getText());
        assertEquals(0, root.getChildCount());

        assertEquals("a()", root.toString());
    }

    @Test
    void testFunctionArgument() {
        Spin2TreeBuilder builder = new Spin2TreeBuilder();
        builder.addToken(new Token(0, "a"));
        builder.addToken(new Token(Token.OPERATOR, "("));
        builder.addToken(new Token(0, "b"));
        builder.addToken(new Token(Token.OPERATOR, ")"));

        Spin2StatementNode root = builder.getRoot();

        assertEquals("a", root.getText());
        assertEquals(1, root.getChildCount());
        assertEquals("b", root.getChild(0).getText());
        assertEquals(0, root.getChild(0).getChildCount());

        assertEquals("a(b)", root.toString());
    }

    @Test
    void testFunctionArgumentExpression() {
        Spin2TreeBuilder builder = new Spin2TreeBuilder();
        builder.addToken(new Token(0, "a"));
        builder.addToken(new Token(Token.OPERATOR, "("));
        builder.addToken(new Token(0, "b"));
        builder.addToken(new Token(Token.OPERATOR, "+"));
        builder.addToken(new Token(0, "1"));
        builder.addToken(new Token(Token.OPERATOR, ")"));

        Spin2StatementNode root = builder.getRoot();

        assertEquals("a", root.getText());
        assertEquals("+", root.getChild(0).getText());
        assertEquals("b", root.getChild(0).getChild(0).getText());
        assertEquals("1", root.getChild(0).getChild(1).getText());

        assertEquals("a(b + 1)", root.toString());
    }

    @Test
    void testFunctionArgumentList() {
        Spin2TreeBuilder builder = new Spin2TreeBuilder();
        builder.addToken(new Token(0, "a"));
        builder.addToken(new Token(Token.OPERATOR, "("));
        builder.addToken(new Token(0, "b"));
        builder.addToken(new Token(Token.OPERATOR, ","));
        builder.addToken(new Token(0, "c"));
        builder.addToken(new Token(Token.OPERATOR, ","));
        builder.addToken(new Token(0, "d"));
        builder.addToken(new Token(Token.OPERATOR, ")"));

        Spin2StatementNode root = builder.getRoot();

        assertEquals("a", root.getText());
        assertEquals(",", root.getChild(0).getText());
        assertEquals("b", root.getChild(0).getChild(0).getText());
        assertEquals("c", root.getChild(0).getChild(1).getText());
        assertEquals("d", root.getChild(0).getChild(2).getText());

        assertEquals("a(b, c, d)", root.toString());
    }

    @Test
    void testFunctionAssignment() {
        Spin2TreeBuilder builder = new Spin2TreeBuilder();
        builder.addToken(new Token(0, "a"));
        builder.addToken(new Token(Token.OPERATOR, ":="));
        builder.addToken(new Token(0, "b"));
        builder.addToken(new Token(Token.OPERATOR, "("));
        builder.addToken(new Token(Token.OPERATOR, ")"));

        Spin2StatementNode root = builder.getRoot();

        assertEquals(":=", root.getText());
        assertEquals("a", root.getChild(0).getText());
        assertEquals("b", root.getChild(1).getText());

        assertEquals("a := b()", root.toString());
    }

    @Test
    void testFunctionWithArgumentsAssignment() {
        Spin2TreeBuilder builder = new Spin2TreeBuilder();
        builder.addToken(new Token(0, "a"));
        builder.addToken(new Token(Token.OPERATOR, ":="));
        builder.addToken(new Token(0, "b"));
        builder.addToken(new Token(Token.OPERATOR, "("));
        builder.addToken(new Token(0, "c"));
        builder.addToken(new Token(Token.OPERATOR, ","));
        builder.addToken(new Token(0, "d"));
        builder.addToken(new Token(Token.OPERATOR, ")"));

        Spin2StatementNode root = builder.getRoot();

        assertEquals(":=", root.getText());
        assertEquals("a", root.getChild(0).getText());
        assertEquals("b", root.getChild(1).getText());
        assertEquals(",", root.getChild(1).getChild(0).getText());
        assertEquals("c", root.getChild(1).getChild(0).getChild(0).getText());
        assertEquals("d", root.getChild(1).getChild(0).getChild(1).getText());

        assertEquals("a := b(c, d)", root.toString());
    }

    @Test
    void testIndex() {
        Spin2TreeBuilder builder = new Spin2TreeBuilder();
        builder.addToken(new Token(0, "a"));
        builder.addToken(new Token(Token.OPERATOR, ":="));
        builder.addToken(new Token(0, "b"));
        builder.addToken(new Token(Token.OPERATOR, "["));
        builder.addToken(new Token(Token.NUMBER, "1"));
        builder.addToken(new Token(Token.OPERATOR, "]"));

        Spin2StatementNode root = builder.getRoot();

        assertEquals(":=", root.getText());
        assertEquals("a", root.getChild(0).getText());
        assertEquals("[", root.getChild(1).getText());
        assertEquals("b", root.getChild(1).getChild(0).getText());
        assertEquals("1", root.getChild(1).getChild(1).getText());

        assertEquals("a := b[1]", root.toString());
    }

}
