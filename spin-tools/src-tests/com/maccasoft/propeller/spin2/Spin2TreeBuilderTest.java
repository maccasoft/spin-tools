/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.model.Token;

class Spin2TreeBuilderTest {

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
    void testFunctionArgumentsOverride() {
        String text = "a := function(1, byte 2, word 3)";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [function]\n"
            + "      +-- [1]\n"
            + "      +-- [byte]\n"
            + "           +-- [2]\n"
            + "      +-- [word]\n"
            + "           +-- [3]\n"
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
        String text = "a := b == 1 ? 2 : 3";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [?]\n"
            + "      +-- [==]\n"
            + "           +-- [b]\n"
            + "           +-- [1]\n"
            + "      +-- [:]\n"
            + "           +-- [2]\n"
            + "           +-- [3]\n"
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
        String text = "a := b + c++ * --d";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [+]\n"
            + "      +-- [b]\n"
            + "      +-- [*]\n"
            + "           +-- [c]\n"
            + "                +-- [++]\n"
            + "           +-- [--]\n"
            + "                +-- [d]\n"
            + "", parse(text));
    }

    @Test
    void testUnaryIndexAssigment() {
        String text = "a[1]++";
        Assertions.assertEquals(""
            + "[a]\n"
            + " +-- [1]\n"
            + " +-- [++]\n"
            + "", parse(text));
    }

    @Test
    void testUnaryIndexPreAssigment() {
        String text = "++a[1]";
        Assertions.assertEquals(""
            + "[++]\n"
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
        String text = "a := b[c][0]++";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [b]\n"
            + "      +-- [c]\n"
            + "      +-- [0]\n"
            + "      +-- [++]\n"
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
    void testArgumentFunction() {
        String text = "a := c(10, d(20, 30), 40)";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [c]\n"
            + "      +-- [10]\n"
            + "      +-- [d]\n"
            + "           +-- [20]\n"
            + "           +-- [30]\n"
            + "      +-- [40]\n"
            + "", parse(text));
    }

    @Test
    void testListAssignment() {
        String text = "a, b, c := d, e, f";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [,]\n"
            + "      +-- [,]\n"
            + "           +-- [a]\n"
            + "           +-- [b]\n"
            + "      +-- [c]\n"
            + " +-- [,]\n"
            + "      +-- [,]\n"
            + "           +-- [d]\n"
            + "           +-- [e]\n"
            + "      +-- [f]\n"
            + "", parse(text));
    }

    @Test
    void testBitField() {
        String text = "a := b.[1]";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [b]\n"
            + "      +-- [.]\n"
            + "      +-- [1]\n"
            + "", parse(text));
    }

    @Test
    void testIndexedBitField() {
        String text = "a := b[0].[1]";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [b]\n"
            + "      +-- [0]\n"
            + "      +-- [.]\n"
            + "      +-- [1]\n"
            + "", parse(text));
    }

    @Test
    void test1() {
        String text = "not cc.IsDigit(look) and look <> term.NL";
        Assertions.assertEquals(""
            + "[and]\n"
            + " +-- [not]\n"
            + "      +-- [cc.IsDigit]\n"
            + "           +-- [look]\n"
            + " +-- [<>]\n"
            + "      +-- [look]\n"
            + "      +-- [term.NL]\n"
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
    void test3() {
        String text = "word[0].[1] := a";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [word]\n"
            + "      +-- [0]\n"
            + "      +-- [.]\n"
            + "      +-- [1]\n"
            + " +-- [a]\n"
            + "", parse(text));
    }

    @Test
    void testSqrt() {
        String text = "a := sqrt(1.0)";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [sqrt]\n"
            + "      +-- [1.0]\n"
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
    void testObjectFunctionCall() {
        String text = "o.function()";
        Assertions.assertEquals(""
            + "[o.function]\n"
            + "", parse(text));
    }

    @Test
    void testObjectFunctionCallArguments() {
        String text = "o.function(a, b)";
        Assertions.assertEquals(""
            + "[o.function]\n"
            + " +-- [a]\n"
            + " +-- [b]\n"
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
    void testMethodReturn() {
        Context context = new Context();
        context.addSymbol("ptr", new Variable("LONG", "ptr", 1));

        String text = "a := ptr():1";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [ptr]\n"
            + "", parse(context, text));
    }

    @Test
    void testMethodArgumentAndReturn() {
        Context context = new Context();
        context.addSymbol("ptr", new Variable("LONG", "ptr", 1));

        String text = "a := ptr(b):1";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [ptr]\n"
            + "      +-- [b]\n"
            + "", parse(context, text));
    }

    @Test
    void testCallRegisterExpression() {
        String text = "call(#a + 2)";
        Assertions.assertEquals(""
            + "[call]\n"
            + " +-- [+]\n"
            + "      +-- [#a]\n"
            + "      +-- [2]\n"
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
    void testStructureAssignment() {
        String text = "a := b.c";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [b.c]\n"
            + "", parse(text));
    }

    @Test
    void testStructureIndexAssignment() {
        String text = "a := b[1].c";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [b]\n"
            + "      +-- [1]\n"
            + "      +-- [.c]\n"
            + "", parse(text));
    }

    @Test
    void testStructureMultiIndexAssignment() {
        String text = "a := b[1].c.d[2].e";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [b]\n"
            + "      +-- [1]\n"
            + "      +-- [.c.d]\n"
            + "           +-- [2]\n"
            + "           +-- [.e]\n"
            + "", parse(text));
    }

    @Test
    void testDebug() {
        String text = "debug(``(letter) lutcolors `uhex_long_array_(image_address, lut_size) longs_2bit)";
        Assertions.assertEquals(""
            + "[debug]\n"
            + " +-- [`]\n"
            + " +-- [`]\n"
            + "      +-- [letter]\n"
            + " +-- [ lutcolors ]\n"
            + " +-- [`uhex_long_array_]\n"
            + "      +-- [image_address]\n"
            + "      +-- [lut_size]\n"
            + " +-- [ longs_2bit]\n"
            + "", parse(text));
    }

    @Test
    void testDebugBacktickExpression() {
        String text = "debug(`MyBitmap `uhex_(flag[i++ & $1F]) `dly(100))";
        Assertions.assertEquals(""
            + "[debug]\n"
            + " +-- [`MyBitmap ]\n"
            + " +-- [`uhex_]\n"
            + "      +-- [flag]\n"
            + "           +-- [&]\n"
            + "                +-- [i]\n"
            + "                     +-- [++]\n"
            + "                +-- [$1F]\n"
            + " +-- [ ]\n"
            + " +-- [`dly]\n"
            + "      +-- [100]\n"
            + "", parse(text));
    }

    @Test
    void testPointers() {
        String text = "a := [b]";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [b]\n"
            + "", parse(text));
    }

    @Test
    void testPointersPostEffect1() {
        String text = "a := [b]++";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [b]\n"
            + "      +-- [++]\n"
            + "", parse(text));
    }

    @Test
    void testPointersPostEffect2() {
        String text = "a := b[++]";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [b]\n"
            + "      +-- [++]\n"
            + "", parse(text));
    }

    @Test
    void testPointersPreEffect1() {
        String text = "a := ++[b]";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [++]\n"
            + "      +-- [b]\n"
            + "", parse(text));
    }

    @Test
    void testPointersPreEffect2() {
        Context context = new Context();
        context.addSymbol("a", new Variable("LONG", "a", 1));
        context.addSymbol("b", new Variable("LONG", "b", 1));

        String text = "a := [++]b";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [++]\n"
            + "      +-- [b]\n"
            + "", parse(context, text));
    }

    @Test
    void testPointersPreEffect3() {
        Context context = new Context();
        context.addSymbol("a", new Variable("LONG", "a", 1));
        context.addSymbol("b", new Variable("LONG", "b", 1));

        String text = "[++]a := b";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [++]\n"
            + "      +-- [a]\n"
            + " +-- [b]\n"
            + "", parse(context, text));
    }

    void testSkipReturnValue() {
        String text = "_, a := function()";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [,]\n"
            + "      +-- [_]\n"
            + "      +-- [a]\n"
            + " +-- [function]\n"
            + "", parse(text));
    }

    @Test
    void testSkipMultipleReturnValue() {
        String text = "_, a, _ := function()";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [,]\n"
            + "      +-- [,]\n"
            + "           +-- [_]\n"
            + "           +-- [a]\n"
            + "      +-- [_]\n"
            + " +-- [function]\n"
            + "", parse(text));
    }

    @Test
    void testSkipReturnCount() {
        String text = "a, _(1), c := function()";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [,]\n"
            + "      +-- [,]\n"
            + "           +-- [a]\n"
            + "           +-- [_]\n"
            + "                +-- [1]\n"
            + "      +-- [c]\n"
            + " +-- [function]\n"
            + "", parse(text));
    }

    @Test
    void testSkipTypeReturnCount() {
        String text = "a, _(type) := function()";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [,]\n"
            + "      +-- [a]\n"
            + "      +-- [_]\n"
            + "           +-- [type]\n"
            + " +-- [function]\n"
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

    @Test
    void testListRange() {
        String text = "1, 2, 3..4, 5";
        Assertions.assertEquals(""
            + "[,]\n"
            + " +-- [,]\n"
            + "      +-- [,]\n"
            + "           +-- [1]\n"
            + "           +-- [2]\n"
            + "      +-- [..]\n"
            + "           +-- [3]\n"
            + "           +-- [4]\n"
            + " +-- [5]\n"
            + "", parse(text));
    }

    @Test
    void testSwap() {
        String text = "a <> b \\a";
        Assertions.assertEquals(""
            + "[<>]\n"
            + " +-- [a]\n"
            + " +-- [\\]\n"
            + "      +-- [b]\n"
            + "      +-- [a]\n"
            + "", parse(text));
    }

    @Test
    void testMethodPointersTernary() {
        Context context = new Context();
        context.addSymbol("ptrb", new Variable("LONG", "ptrb", 1));
        context.addSymbol("ptrc", new Variable("LONG", "ptrc", 1));

        String text = "a := a <> 0 ? ptrb():1 : ptrc():1";
        Assertions.assertEquals(""
            + "[:=]\n"
            + " +-- [a]\n"
            + " +-- [?]\n"
            + "      +-- [<>]\n"
            + "           +-- [a]\n"
            + "           +-- [0]\n"
            + "      +-- [:]\n"
            + "           +-- [ptrb]\n"
            + "           +-- [ptrc]\n"
            + "", parse(context, text));
    }

    @Test
    void testPointerSyntax() {
        Context context = new Context();
        context.addSymbol("ptr", new Variable("LONG", "ptr", 1));

        Assertions.assertEquals(""
            + "[++]\n"
            + " +-- [ptr]\n"
            + "", parse(context, "[++]ptr"));

        Assertions.assertEquals(""
            + "[ptr]\n"
            + " +-- [++]\n"
            + "", parse(context, "ptr[++]"));

        Assertions.assertEquals(""
            + "[++]\n"
            + " +-- [ptr.x]\n"
            + "", parse(context, "[++]ptr.x"));

        Assertions.assertEquals(""
            + "[ptr]\n"
            + " +-- [++]\n"
            + " +-- [.x]\n"
            + "", parse(context, "ptr[++].x"));

        Assertions.assertEquals(""
            + "[++]\n"
            + " +-- [--]\n"
            + "      +-- [ptr.x]\n"
            + "", parse(context, "++[--]ptr.x"));

        Assertions.assertEquals(""
            + "[++]\n"
            + " +-- [ptr]\n"
            + "      +-- [--]\n"
            + "      +-- [.x]\n"
            + "", parse(context, "++ptr[--].x"));

        Assertions.assertEquals(""
            + "[and]\n"
            + " +-- [ptr]\n"
            + " +-- [ptr.x]\n"
            + "", parse(context, "[ptr] and ptr.x"));
    }

    String parse(String text) {
        return parse(new Context(), text);
    }

    String parse(Context context, String text) {
        Spin2TreeBuilder builder = new Spin2TreeBuilder(context);

        Spin2TokenStream stream = new Spin2TokenStream(text);
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

        Spin2StatementNode root = builder.getRoot();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        print(new PrintStream(os), root, 0);
        return os.toString().replaceAll("\\r\\n", "\n");
    }

    void print(PrintStream out, Spin2StatementNode node, int indent) {
        if (indent != 0) {
            for (int i = 1; i < indent; i++) {
                out.print("     ");
            }
            out.print(" +-- ");
        }

        out.print("[" + node.getText().replaceAll("\n", "\\\\n") + "]");
        out.println();

        for (Spin2StatementNode child : node.getChilds()) {
            print(out, child, indent + 1);
        }
    }

}
