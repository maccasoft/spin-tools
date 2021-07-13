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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Spin1TokenStreamTest {

    @Test
    void testConstantAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a = b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testVariableAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a := b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(":=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testAddOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a + b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("+", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testAddAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a += b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("+=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testSubtractOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a - b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("-", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testSubtractAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a -= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("-=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testMultiplyOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a * b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("*", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testMultiplyAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a *= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("*=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testMultiplyAndReturnUpperOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a ** b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("**", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testMultiplyAndReturnUpperAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a **= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("**=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testDivideOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a / b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("/", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testDivideAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a /= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("/=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testModulusOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a // b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("//", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testModulusAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a //= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("//=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testLimitMinimumOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a #> b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("#>", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testLimitMinimumAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a #>= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("#>=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testLimitMaximumOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a <# b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<#", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testLimitMaximumAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a <#= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<#=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testSAROperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a ~> b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("~>", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testSARAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a ~>= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("~>=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testShiftLeftOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a << b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<<", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testShiftLeftAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a <<= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<<=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testShiftRightOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a >> b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(">>", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testShiftRightAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a >>= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(">>=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testRotateLeftOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a <- b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<-", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testRotateLeftAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a <-= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<-=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testRotateRightOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a -> b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("->", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testRotateRightAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a ->= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("->=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testReverseOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a >< b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("><", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testReverseAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a ><= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("><=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testBitwiseAndOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a & b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("&", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testBitwiseAndAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a &= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("&=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testBitwiseOrOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a | b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("|", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testBitwiseOrAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a |= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("|=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testBitwiseXorOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a ^ b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("^", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testBitwiseXorAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a ^= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("^=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testAndOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a AND b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("AND", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testAndAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a AND= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("AND=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testOrOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a OR b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("OR", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testOrAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a OR= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("OR=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testEqualOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a == b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("==", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testEqualAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a === b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("===", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testNotEqualOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a <> b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<>", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testNotEqualAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a <>= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<>=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testLessThanOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a < b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testLessThanAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a <= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testgreatThanOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a > b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(">", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testGreatThanAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a >= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(">=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testEqualsOrLessOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a =< b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("=<", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testEqualsOrLessAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a =<= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("=<=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testEqualsOrgreatOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a => b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("=>", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testEqualsOrgreatAssignmentOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("a =>= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("=>=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testBinaryNumber() {
        Spin1TokenStream subject = new Spin1TokenStream("%0001_0011");

        assertEquals("%0001_0011", subject.nextToken().getText());
    }

    @Test
    void testQuaternaryNumber() {
        Spin1TokenStream subject = new Spin1TokenStream("%%0123_3210");

        assertEquals("%%0123_3210", subject.nextToken().getText());
    }

    @Test
    void testHexadecimalNumber() {
        Spin1TokenStream subject = new Spin1TokenStream("$0123_ABCD");

        assertEquals("$0123_ABCD", subject.nextToken().getText());
    }

    @Test
    void testString() {
        Spin1TokenStream subject = new Spin1TokenStream("\"abcde\"");

        assertEquals("\"abcde\"", subject.nextToken().getText());
    }

    @Test
    void testObjectConstantReference() {
        Spin1TokenStream subject = new Spin1TokenStream("obj#constant");

        assertEquals("obj#constant", subject.nextToken().getText());
    }

    @Test
    void testObjectMethodReference() {
        Spin1TokenStream subject = new Spin1TokenStream("obj.method");

        assertEquals("obj.method", subject.nextToken().getText());
    }

    @Test
    void testRangeIndicator() {
        Spin1TokenStream subject = new Spin1TokenStream("0..7");

        assertEquals("0", subject.nextToken().getText());
        assertEquals("..", subject.nextToken().getText());
        assertEquals("7", subject.nextToken().getText());
    }

    @Test
    void testIdentifierRangeIndicator() {
        Spin1TokenStream subject = new Spin1TokenStream("A..B");

        assertEquals("A", subject.nextToken().getText());
        assertEquals("..", subject.nextToken().getText());
        assertEquals("B", subject.nextToken().getText());
    }

    @Test
    void testHexadecimalRangeIndicator() {
        Spin1TokenStream subject = new Spin1TokenStream("$00..$1F");

        assertEquals("$00", subject.nextToken().getText());
        assertEquals("..", subject.nextToken().getText());
        assertEquals("$1F", subject.nextToken().getText());
    }

    @Test
    void testBinaryRangeIndicator() {
        Spin1TokenStream subject = new Spin1TokenStream("%00..%11");

        assertEquals("%00", subject.nextToken().getText());
        assertEquals("..", subject.nextToken().getText());
        assertEquals("%11", subject.nextToken().getText());
    }

    @Test
    void testBlockComment() {
        Spin1TokenStream subject = new Spin1TokenStream("{ first line\n  second line }\n");

        assertEquals("{ first line\n  second line }", subject.nextToken(true).getText());
        assertEquals("\n", subject.nextToken(true).getText());
    }

    @Test
    void testLineComment() {
        Spin1TokenStream subject = new Spin1TokenStream("' first line\n' second line\n");

        assertEquals("' first line", subject.nextToken(true).getText());
        assertEquals("\n", subject.nextToken(true).getText());
        assertEquals("' second line", subject.nextToken(true).getText());
        assertEquals("\n", subject.nextToken(true).getText());
    }

    @Test
    void testBlockDocumentComment() {
        Spin1TokenStream subject = new Spin1TokenStream("{{ first line\n  second line }}\n");

        assertEquals("{{ first line\n  second line }}", subject.nextToken(true).getText());
        assertEquals("\n", subject.nextToken(true).getText());
    }

    @Test
    void testLineDocumentComment() {
        Spin1TokenStream subject = new Spin1TokenStream("'' first line\n'' second line\n");

        assertEquals("'' first line", subject.nextToken(true).getText());
        assertEquals("\n", subject.nextToken(true).getText());
        assertEquals("'' second line", subject.nextToken(true).getText());
        assertEquals("\n", subject.nextToken(true).getText());
    }

    @Test
    void testNestedBlockComment() {
        Spin1TokenStream subject = new Spin1TokenStream("{ first line\n{ second line }}\n");

        assertEquals("{ first line\n{ second line }}", subject.nextToken(true).getText());
        assertEquals("\n", subject.nextToken(true).getText());
    }

    @Test
    void testNumericRangeOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("12..34");

        assertEquals("12", subject.nextToken().getText());
        assertEquals("..", subject.nextToken().getText());
        assertEquals("34", subject.nextToken().getText());
    }

    @Test
    void testKeywordRangeOperator() {
        Spin1TokenStream subject = new Spin1TokenStream("ab..cd");

        assertEquals("ab", subject.nextToken().getText());
        assertEquals("..", subject.nextToken().getText());
        assertEquals("cd", subject.nextToken().getText());
    }

    @Test
    void testDecimalNumber() {
        Spin1TokenStream subject = new Spin1TokenStream("1.234");

        assertEquals("1.234", subject.nextToken().getText());
    }

    @Test
    void testPostIncrement() {
        Spin1TokenStream subject = new Spin1TokenStream("a++");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("++", subject.nextToken().getText());
    }

    @Test
    void testPostClear() {
        Spin1TokenStream subject = new Spin1TokenStream("a~");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("~", subject.nextToken().getText());
    }

    @Test
    void testPostSet() {
        Spin1TokenStream subject = new Spin1TokenStream("a~~");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("~~", subject.nextToken().getText());
    }

    @Test
    void testPreIncrement() {
        Spin1TokenStream subject = new Spin1TokenStream("++a");

        assertEquals("++", subject.nextToken().getText());
        assertEquals("a", subject.nextToken().getText());
    }

}
