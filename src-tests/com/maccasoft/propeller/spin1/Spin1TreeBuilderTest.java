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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Token;

class Spin1TreeBuilderTest {

    @Test
    void testOperatorPrecedence() {
        String text = "1 + 2 * 3";
        Assertions.assertEquals(""
            + "[+]\n"
            + " +-- [1]\n"
            + " +-- [*]\n"
            + "      +-- [2]\n"
            + "      +-- [3]\n"
            + "", parseExpression(text));
    }

    @Test
    void testGroupOperatorPrecedence() {
        String text = "(1 + 2) * 3";
        Assertions.assertEquals(""
            + "[*]\n"
            + " +-- [(]\n"
            + "      +-- [+]\n"
            + "           +-- [1]\n"
            + "           +-- [2]\n"
            + " +-- [3]\n"
            + "", parseExpression(text));
    }

    @Test
    void testAssignmentExpression() {
        String text = "a := 1 + 2 * 3";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [+]\n"
            + "      +-- [1]\n"
            + "      +-- [*]\n"
            + "           +-- [2]\n"
            + "           +-- [3]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testFunctionCall() {
        String text = "function()";
        Assertions.assertEquals(""
            + "[function]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testFunctionCallArguments() {
        String text = "function(1, 2, 3)";
        Assertions.assertEquals(""
            + "[function]\n"
            + " +-- [,]\n"
            + "      +-- [1]\n"
            + "      +-- [2]\n"
            + "      +-- [3]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testFunctionExpressionArguments() {
        String text = "function(1 + 2 * 3, 4, (5 + 6) * 7)";
        Assertions.assertEquals(""
            + "[function]\n"
            + " +-- [,]\n"
            + "      +-- [+]\n"
            + "           +-- [1]\n"
            + "           +-- [*]\n"
            + "                +-- [2]\n"
            + "                +-- [3]\n"
            + "      +-- [4]\n"
            + "      +-- [*]\n"
            + "           +-- [(]\n"
            + "                +-- [+]\n"
            + "                     +-- [5]\n"
            + "                     +-- [6]\n"
            + "           +-- [7]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testFunctionAssignment() {
        String text = "a := function()";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [function]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testFunctionArgumentsAssignment() {
        String text = "a := function(1, 2, 3)";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [function]\n"
            + "      +-- [,]\n"
            + "           +-- [1]\n"
            + "           +-- [2]\n"
            + "           +-- [3]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testFunctionExpressionAssignment() {
        String text = "a := function1() + function2() * function3()";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [+]\n"
            + "      +-- [function1]\n"
            + "      +-- [*]\n"
            + "           +-- [function2]\n"
            + "           +-- [function3]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testFunctionArgumentsExpressionAssignment() {
        String text = "a := function1(1, 2) + function2(3) * function3(4, 5, 6)";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [+]\n"
            + "      +-- [function1]\n"
            + "           +-- [,]\n"
            + "                +-- [1]\n"
            + "                +-- [2]\n"
            + "      +-- [*]\n"
            + "           +-- [function2]\n"
            + "                +-- [3]\n"
            + "           +-- [function3]\n"
            + "                +-- [,]\n"
            + "                     +-- [4]\n"
            + "                     +-- [5]\n"
            + "                     +-- [6]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testFunctionGroupingAssignment() {
        String text = "a := (function1(1, 2) + function2(3)) * function3(4, 5, 6)";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [*]\n"
            + "      +-- [(]\n"
            + "           +-- [+]\n"
            + "                +-- [function1]\n"
            + "                     +-- [,]\n"
            + "                          +-- [1]\n"
            + "                          +-- [2]\n"
            + "                +-- [function2]\n"
            + "                     +-- [3]\n"
            + "      +-- [function3]\n"
            + "           +-- [,]\n"
            + "                +-- [4]\n"
            + "                +-- [5]\n"
            + "                +-- [6]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testUnaryOperator() {
        String text = "a := -b";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [-]\n"
            + "      +-- [b]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testUnaryExpression() {
        String text = "a := -(1 + 2 * 3)";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [-]\n"
            + "      +-- [(]\n"
            + "           +-- [+]\n"
            + "                +-- [1]\n"
            + "                +-- [*]\n"
            + "                     +-- [2]\n"
            + "                     +-- [3]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testIndex() {
        String text = "a := b[1]";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [[]\n"
            + "      +-- [b]\n"
            + "      +-- [1]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testIndexAssignment() {
        String text = "a[1] := b";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [[]\n"
            + "      +-- [a]\n"
            + "      +-- [1]\n"
            + " +-- [b]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testIndexRange() {
        String text = "a := b[1..5]";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [[]\n"
            + "      +-- [b]\n"
            + "      +-- [..]\n"
            + "           +-- [1]\n"
            + "           +-- [5]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testIndexRangeAssignment() {
        String text = "a[1..5] := b";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [[]\n"
            + "      +-- [a]\n"
            + "      +-- [..]\n"
            + "           +-- [1]\n"
            + "           +-- [5]\n"
            + " +-- [b]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testTernary() {
        String text = "a := b == 1 ? 2 : 3";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [?]\n"
            + "      +-- [==]\n"
            + "           +-- [b]\n"
            + "           +-- [1]\n"
            + "      +-- [2]\n"
            + "      +-- [3]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testFunctionTryCall() {
        String text = "\\function1()";
        Assertions.assertEquals(""
            + "[\\]\n"
            + " +-- [function1]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testFunctionTryAssignment() {
        String text = "a := \\function1()";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [\\]\n"
            + "      +-- [function1]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testFunctionTryGrouping() {
        String text = "a := (function1(1, 2) + \\function2(3)) * function3(4, 5, 6)";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [*]\n"
            + "      +-- [(]\n"
            + "           +-- [+]\n"
            + "                +-- [function1]\n"
            + "                     +-- [,]\n"
            + "                          +-- [1]\n"
            + "                          +-- [2]\n"
            + "                +-- [\\]\n"
            + "                     +-- [function2]\n"
            + "                          +-- [3]\n"
            + "      +-- [function3]\n"
            + "           +-- [,]\n"
            + "                +-- [4]\n"
            + "                +-- [5]\n"
            + "                +-- [6]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testLookdownArguments() {
        String text = "a := lookdown(b : 1,2,3,4)";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [lookdown]\n"
            + "      +-- [:]\n"
            + "           +-- [b]\n"
            + "           +-- [,]\n"
            + "                +-- [1]\n"
            + "                +-- [2]\n"
            + "                +-- [3]\n"
            + "                +-- [4]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testPostIncrement() {
        String text = "a++";
        Assertions.assertEquals(""
            + "[a]\n"
            + " +-- [++]\n"
            + "", parseExpression(text));
    }

    @Test
    void testPreDecrement() {
        String text = "--a";
        Assertions.assertEquals(""
            + "[--]\n"
            + " +-- [a]\n"
            + "", parseExpression(text));
    }

    @Test
    void testUnaryEffectsExpression() {
        String text = "a := b + c~~ * --d";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [+]\n"
            + "      +-- [b]\n"
            + "      +-- [*]\n"
            + "           +-- [c]\n"
            + "                +-- [~~]\n"
            + "           +-- [--]\n"
            + "                +-- [d]\n"
            + "", parseAssignment(text));
    }

    @Test
    void testUnaryIndexAssigment() {
        String text = "a[1]~~";
        Assertions.assertEquals(""
            + "[[]\n"
            + " +-- [a]\n"
            + " +-- [1]\n"
            + " +-- [~~]\n"
            + "", parseExpression(text));
    }

    String parseAssignment(String text) {
        return parse(0, text);
    }

    String parseExpression(String text) {
        return parse(2, text);
    }

    String parse(int state, String text) {
        Spin1TreeBuilder builder = new Spin1TreeBuilder();
        builder.setState(state);

        Spin1TokenStream stream = new Spin1TokenStream(text);
        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF) {
                break;
            }
            builder.addToken(token);
        }

        Spin1StatementNode root = builder.getRoot();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        print(new PrintStream(os), root, 0);
        return os.toString();
    }

    void print(PrintStream out, Spin1StatementNode node, int indent) {
        if (indent != 0) {
            for (int i = 1; i < indent; i++) {
                out.print("     ");
            }
            out.print(" +-- ");
        }

        out.print("[" + node.getText().replaceAll("\n", "\\\\n") + "]");
        out.println();

        for (Spin1StatementNode child : node.getChilds()) {
            print(out, child, indent + 1);
        }
    }

}
