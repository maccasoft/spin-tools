/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
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

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.model.Token;

class Spin1TreeBuilderTest {

    @Test
    void testOperatorPrecedence1() {
        String text = "1 + 2 * 3";
        Assertions.assertEquals(""
            + "[+]\n"
            + " +-- [1]\n"
            + " +-- [*]\n"
            + "      +-- [2]\n"
            + "      +-- [3]\n"
            + "", parse(text));
    }

    @Test
    void testOperatorPrecedence2() {
        String text = "a * b << 1 + 2";
        Assertions.assertEquals(""
            + "[+]\n"
            + " +-- [*]\n"
            + "      +-- [a]\n"
            + "      +-- [<<]\n"
            + "           +-- [b]\n"
            + "           +-- [1]\n"
            + " +-- [2]\n"
            + "", parse(text));
    }

    @Test
    public void testAssignmentsChain() throws Exception {
        String text = "a := b := c";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [:=]\n"
            + "      +-- [b]\n"
            + "      +-- [c]\n"
            + "", parse(text));
    }

    @Test
    void testGroupOperatorPrecedence() {
        String text = "(1 + 2) * 3";
        Assertions.assertEquals(""
            + "[*]\n"
            + " +-- [+]\n"
            + "      +-- [1]\n"
            + "      +-- [2]\n"
            + " +-- [3]\n"
            + "", parse(text));
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
            + "", parse(text));
    }

    @Test
    void testFunctionCall() {
        String text = "function()";
        Assertions.assertEquals(""
            + "[function]\n"
            + "", parse(text));
    }

    @Test
    void testFunctionCallArguments() {
        String text = "function(1, 2, 3)";
        Assertions.assertEquals(""
            + "[function]\n"
            + " +-- [1]\n"
            + " +-- [2]\n"
            + " +-- [3]\n"
            + "", parse(text));
    }

    @Test
    void testFunctionExpressionArguments() {
        String text = "function(1 + 2 * 3, 4, (5 + 6) * 7)";
        Assertions.assertEquals(""
            + "[function]\n"
            + " +-- [+]\n"
            + "      +-- [1]\n"
            + "      +-- [*]\n"
            + "           +-- [2]\n"
            + "           +-- [3]\n"
            + " +-- [4]\n"
            + " +-- [*]\n"
            + "      +-- [+]\n"
            + "           +-- [5]\n"
            + "           +-- [6]\n"
            + "      +-- [7]\n"
            + "", parse(text));
    }

    @Test
    void testFunctionAssignment() {
        String text = "a := function()";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [function]\n"
            + "", parse(text));
    }

    @Test
    void testFunctionArgumentsAssignment() {
        String text = "a := function(1, 2, 3)";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [function]\n"
            + "      +-- [1]\n"
            + "      +-- [2]\n"
            + "      +-- [3]\n"
            + "", parse(text));
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
            + "", parse(text));
    }

    @Test
    void testFunctionArgumentsExpressionAssignment() {
        String text = "a := function1(1, 2) + function2(3) * function3(4, 5, 6)";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [+]\n"
            + "      +-- [function1]\n"
            + "           +-- [1]\n"
            + "           +-- [2]\n"
            + "      +-- [*]\n"
            + "           +-- [function2]\n"
            + "                +-- [3]\n"
            + "           +-- [function3]\n"
            + "                +-- [4]\n"
            + "                +-- [5]\n"
            + "                +-- [6]\n"
            + "", parse(text));
    }

    @Test
    void testFunctionGroupingAssignment() {
        String text = "a := (function1(1, 2) + function2(3)) * function3(4, 5, 6)";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [*]\n"
            + "      +-- [+]\n"
            + "           +-- [function1]\n"
            + "                +-- [1]\n"
            + "                +-- [2]\n"
            + "           +-- [function2]\n"
            + "                +-- [3]\n"
            + "      +-- [function3]\n"
            + "           +-- [4]\n"
            + "           +-- [5]\n"
            + "           +-- [6]\n"
            + "", parse(text));
    }

    @Test
    void testUnaryOperator() {
        String text = "a := -b";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [-]\n"
            + "      +-- [b]\n"
            + "", parse(text));
    }

    @Test
    void testUnaryExpression() {
        String text = "a := -(1 + 2 * 3)";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [-]\n"
            + "      +-- [+]\n"
            + "           +-- [1]\n"
            + "           +-- [*]\n"
            + "                +-- [2]\n"
            + "                +-- [3]\n"
            + "", parse(text));
    }

    @Test
    void testIndex() {
        String text = "a := b[1]";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [b]\n"
            + "      +-- [1]\n"
            + "", parse(text));
    }

    @Test
    void testIndexAssignment() {
        String text = "a[1] := b";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + "      +-- [1]\n"
            + " +-- [b]\n"
            + "", parse(text));
    }

    @Test
    void testIndexRange() {
        String text = "a := b[1..5]";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [b]\n"
            + "      +-- [..]\n"
            + "           +-- [1]\n"
            + "           +-- [5]\n"
            + "", parse(text));
    }

    @Test
    void testIndexRangeAssignment() {
        String text = "a[1..5] := b";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + "      +-- [..]\n"
            + "           +-- [1]\n"
            + "           +-- [5]\n"
            + " +-- [b]\n"
            + "", parse(text));
    }

    @Test
    void testTernary() {
        String text = "a := b ? c : d";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [?]\n"
            + "      +-- [b]\n"
            + "      +-- [:]\n"
            + "           +-- [c]\n"
            + "           +-- [d]\n"
            + "", parse(text));
    }

    @Test
    void testTernaryExpression() {
        String text = "a := b > 1 ? c + 1 : d + e * 4";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [?]\n"
            + "      +-- [>]\n"
            + "           +-- [b]\n"
            + "           +-- [1]\n"
            + "      +-- [:]\n"
            + "           +-- [+]\n"
            + "                +-- [c]\n"
            + "                +-- [1]\n"
            + "           +-- [+]\n"
            + "                +-- [d]\n"
            + "                +-- [*]\n"
            + "                     +-- [e]\n"
            + "                     +-- [4]\n"
            + "", parse(text));
    }

    @Test
    void testFunctionTryCall() {
        String text = "\\function1()";
        Assertions.assertEquals(""
            + "[\\]\n"
            + " +-- [function1]\n"
            + "", parse(text));
    }

    @Test
    void testFunctionTryAssignment() {
        String text = "a := \\function1()";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [\\]\n"
            + "      +-- [function1]\n"
            + "", parse(text));
    }

    @Test
    void testFunctionTryGrouping() {
        String text = "a := (function1(1, 2) + \\function2(3)) * function3(4, 5, 6)";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [*]\n"
            + "      +-- [+]\n"
            + "           +-- [function1]\n"
            + "                +-- [1]\n"
            + "                +-- [2]\n"
            + "           +-- [\\]\n"
            + "                +-- [function2]\n"
            + "                     +-- [3]\n"
            + "      +-- [function3]\n"
            + "           +-- [4]\n"
            + "           +-- [5]\n"
            + "           +-- [6]\n"
            + "", parse(text));
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
            + "           +-- [1]\n"
            + "           +-- [2]\n"
            + "           +-- [3]\n"
            + "           +-- [4]\n"
            + "", parse(text));
    }

    @Test
    void testPostIncrement() {
        String text = "a++";
        Assertions.assertEquals(""
            + "[a]\n"
            + " +-- [++]\n"
            + "", parse(text));
    }

    @Test
    void testPreDecrement() {
        String text = "--a";
        Assertions.assertEquals(""
            + "[--]\n"
            + " +-- [a]\n"
            + "", parse(text));
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
            + "", parse(text));
    }

    @Test
    void testUnaryIndexAssigment() {
        String text = "a[1]~~";
        Assertions.assertEquals(""
            + "[a]\n"
            + " +-- [1]\n"
            + " +-- [~~]\n"
            + "", parse(text));
    }

    @Test
    void testUnaryIndexPreAssigment() {
        String text = "~~a[1]";
        Assertions.assertEquals(""
            + "[~~]\n"
            + " +-- [a]\n"
            + "      +-- [1]\n"
            + "", parse(text));
    }

    @Test
    void testArray() {
        String text = "a := b[c][0]";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [b]\n"
            + "      +-- [c]\n"
            + "      +-- [0]\n"
            + "", parse(text));
    }

    @Test
    void testArrayPost() {
        String text = "a := b[c][0]~";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [b]\n"
            + "      +-- [c]\n"
            + "      +-- [0]\n"
            + "      +-- [~]\n"
            + "", parse(text));
    }

    @Test
    void testArrayAssignment() {
        String text = "a[c][0] := b";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + "      +-- [c]\n"
            + "      +-- [0]\n"
            + " +-- [b]\n"
            + "", parse(text));
    }

    @Test
    void testArgumentRange() {
        String text = "a := c(10, 20..30, 40)";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [c]\n"
            + "      +-- [10]\n"
            + "      +-- [..]\n"
            + "           +-- [20]\n"
            + "           +-- [30]\n"
            + "      +-- [40]\n"
            + "", parse(text));
    }

    @Test
    void test1() {
        String text = "not cc.IsDigit(look) and look <> term#NL";
        Assertions.assertEquals(""
            + "[and]\n"
            + " +-- [not]\n"
            + "      +-- [cc.IsDigit]\n"
            + "           +-- [look]\n"
            + " +-- [<>]\n"
            + "      +-- [look]\n"
            + "      +-- [term#NL]\n"
            + "", parse(text));
    }

    @Test
    void test2() {
        String text = "r := not w & $10";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [r]\n"
            + " +-- [not]\n"
            + "      +-- [&]\n"
            + "           +-- [w]\n"
            + "           +-- [$10]\n"
            + "", parse(text));
    }

    @Test
    void testFunctionExpression() {
        String text = "start(a, b) > 1";
        Assertions.assertEquals(""
            + "[>]\n"
            + " +-- [start]\n"
            + "      +-- [a]\n"
            + "      +-- [b]\n"
            + " +-- [1]\n"
            + "", parse(text));
    }

    @Test
    void testObjectArrayExpression() {
        String text = "o[0].start(a, b) > 1";
        Assertions.assertEquals(""
            + "[>]\n"
            + " +-- [o]\n"
            + "      +-- [0]\n"
            + "      +-- [.start]\n"
            + "           +-- [a]\n"
            + "           +-- [b]\n"
            + " +-- [1]\n"
            + "", parse(text));
    }

    @Test
    void testRotateAssign() {
        String text = "dira[a] := b <-= 1";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [dira]\n"
            + "      +-- [a]\n"
            + " +-- [<-=]\n"
            + "      +-- [b]\n"
            + "      +-- [1]\n"
            + "", parse(text));
    }

    @Test
    void testAddAssign() {
        String text = "dira[a] := b += 1";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [dira]\n"
            + "      +-- [a]\n"
            + " +-- [+=]\n"
            + "      +-- [b]\n"
            + "      +-- [1]\n"
            + "", parse(text));
    }

    @Test
    public void testAssignmentPriority() throws Exception {
        String text = "exponent += result := 13";
        Assertions.assertEquals(""
            + "[+=]\n"
            + " +-- [exponent]\n"
            + " +-- [:=]\n"
            + "      +-- [result]\n"
            + "      +-- [13]\n"
            + "", parse(text));
    }

    @Test
    void testComplexPriority() {
        String text = "mask_value := |<xpin + |<ypin";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [mask_value]\n"
            + " +-- [+]\n"
            + "      +-- [|<]\n"
            + "           +-- [xpin]\n"
            + "      +-- [|<]\n"
            + "           +-- [ypin]\n"
            + "", parse(text));
    }

    @Test
    void testActual() {
        String text = "waitcnt(((clkfreq / 1_000 * msecs - 3932) #> WMIN) + cnt)";
        Assertions.assertEquals(""
            + "[waitcnt]\n"
            + " +-- [+]\n"
            + "      +-- [#>]\n"
            + "           +-- [-]\n"
            + "                +-- [*]\n"
            + "                     +-- [/]\n"
            + "                          +-- [clkfreq]\n"
            + "                          +-- [1_000]\n"
            + "                     +-- [msecs]\n"
            + "                +-- [3932]\n"
            + "           +-- [WMIN]\n"
            + "      +-- [cnt]\n"
            + "", parse(text));
    }

    @Test
    void testAbsoluteAddress() {
        String text = "a := byte[@@b[c]][0]";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [byte]\n"
            + "      +-- [@@]\n"
            + "           +-- [b]\n"
            + "                +-- [c]\n"
            + "      +-- [0]\n"
            + "", parse(text));
    }

    @Test
    void testRangeExpression() {
        String text = "a..b - 1";
        Assertions.assertEquals(""
            + "[..]\n"
            + " +-- [a]\n"
            + " +-- [-]\n"
            + "      +-- [b]\n"
            + "      +-- [1]\n"
            + "", parse(text));
    }

    @Test
    void testIndexRangeExpression() {
        String text = "a[0]..b[1] - 2";
        Assertions.assertEquals(""
            + "[..]\n"
            + " +-- [a]\n"
            + "      +-- [0]\n"
            + " +-- [-]\n"
            + "      +-- [b]\n"
            + "           +-- [1]\n"
            + "      +-- [2]\n"
            + "", parse(text));
    }

    @Test
    void testUnaryPriority() {
        String text = "not a := b and (c == b)";
        Assertions.assertEquals(""
            + "[not]\n"
            + " +-- [:=]\n"
            + "      +-- [a]\n"
            + "      +-- [and]\n"
            + "           +-- [b]\n"
            + "           +-- [==]\n"
            + "                +-- [c]\n"
            + "                +-- [b]\n"
            + "", parse(text));
    }

    @Test
    public void testAssignmentsPriority() throws Exception {
        String text = "a + b := c * d";
        Assertions.assertEquals(""
            + "[+]\n"
            + " +-- [a]\n"
            + " +-- [:=]\n"
            + "      +-- [b]\n"
            + "      +-- [*]\n"
            + "           +-- [c]\n"
            + "           +-- [d]\n"
            + "", parse(text));
    }

    String parse(String text) {
        Spin1TreeBuilder builder = new Spin1TreeBuilder(new Context());

        Spin1TokenStream stream = new Spin1TokenStream(text);
        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF) {
                break;
            }
            if (".".equals(token.getText())) {
                Token nextToken = stream.peekNext();
                if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                    token = token.merge(stream.nextToken());
                }
            }
            builder.addToken(token);
        }

        Spin1StatementNode root = builder.getRoot();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        print(new PrintStream(os), root, 0);
        return os.toString().replaceAll("\\r\\n", "\n");
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
