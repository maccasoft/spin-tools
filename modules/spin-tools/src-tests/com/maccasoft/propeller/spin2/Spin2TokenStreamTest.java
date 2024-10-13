/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
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

class Spin2TokenStreamTest {

    @Test
    void testDecimalNumber() {
        Spin2TokenStream subject = new Spin2TokenStream("1.234");

        assertEquals("1.234", subject.nextToken().getText());
    }

    @Test
    void testNumericRangeOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("12..34");

        assertEquals("12", subject.nextToken().getText());
        assertEquals("..", subject.nextToken().getText());
        assertEquals("34", subject.nextToken().getText());
    }

    @Test
    void testKeywordRangeOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("ab..cd");

        assertEquals("ab", subject.nextToken().getText());
        assertEquals("..", subject.nextToken().getText());
        assertEquals("cd", subject.nextToken().getText());
    }

    @Test
    void testShiftRightOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a >> b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(">>", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testShiftRightAssignmentOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a >>= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(">>=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testShiftLeftOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a << b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<<", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testShiftLeftAssignmentOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a <<= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<<=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testBitwiseAndOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a & b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("&", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testBitwiseAndAssignmentOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a &= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("&=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testBitwiseOrOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a | b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("|", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testBitwiseOrAssignmentOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a |= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("|=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testBitwiseXorOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a ^ b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("^", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testBitwiseXorAssignmentOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a ^= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("^=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testMultiplyOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a * b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("*", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testFloatMultiply() {
        Spin2TokenStream subject = new Spin2TokenStream("a *. b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("*.", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testMultiplyAssignmentOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a *= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("*=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testDivideOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a / b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("/", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testFloatDivide() {
        Spin2TokenStream subject = new Spin2TokenStream("a/.b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("/.", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testDivideAssignmentOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a /= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("/=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testUnsignedDivideOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a +/ b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("+/", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testUnsignedDivideAssignmentOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a +/= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("+/=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testModulusOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a // b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("//", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testModulusAssignmentOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a //= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("//=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testUnsignedModulusOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a +// b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("+//", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testUnsignedModulusAssignmentOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a +//= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("+//=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testAddOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a + b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("+", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testFloatAdd() {
        Spin2TokenStream subject = new Spin2TokenStream("a+.b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("+.", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testAddAssignmentOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a += b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("+=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testSubtractOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a - b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("-", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testFloatSubtract() {
        Spin2TokenStream subject = new Spin2TokenStream("a -. b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("-.", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testSubtractAssignmentOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a -= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("-=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testLimitMinimumOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a #> b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("#>", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testLimitMinimumAssignmentOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a #>= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("#>=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testLimitMaximumOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a <# b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<#", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testLimitMaximumAssignmentOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a <#= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<#=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testLessThanOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a < b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testFloatLessThanOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a <. b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<.", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testUnsignedLessThanOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a +< b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("+<", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testLessThanOrEqualOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a <= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testFloatLessThanOrEqualOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a <=. b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<=.", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testUnsignedLessThanOrEqualOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a +<= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("+<=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testEqualOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a == b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("==", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testFloatEqualOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a ==. b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("==.", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testNotEqualOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a <> b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<>", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testFloatNotEqualOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a <>. b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<>.", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testGreaterThanOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a > b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(">", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testFloatGreaterThanOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a >. b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(">.", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testUnsignedGreaterThanOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a +> b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("+>", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testGreaterThanOrEqualOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a >= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(">=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testFloatGreaterThanOrEqualOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a >=. b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(">=.", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testUnsignedGreaterThanOrEqualOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a +>= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("+>=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testSignedComparison() {
        Spin2TokenStream subject = new Spin2TokenStream("a <=> b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<=>", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testAndOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a AND b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("AND", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testAndAssignmentOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a AND= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("AND=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testAndOperator2() {
        Spin2TokenStream subject = new Spin2TokenStream("a && b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("&&", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testAndOperator2Assignment() {
        Spin2TokenStream subject = new Spin2TokenStream("a &&= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("&&=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testXorOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a XOR b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("XOR", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testXorAssignmentOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a XOR= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("XOR=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testOrOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a OR b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("OR", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testOrAssignmentOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a OR= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("OR=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testScasOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a SCAS b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("SCAS", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testScasOperatorAssignment() {
        Spin2TokenStream subject = new Spin2TokenStream("a SCAS= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("SCAS=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testAddbitsOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a ADDBITS b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("ADDBITS", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testAddbitsOperatorAssignment() {
        Spin2TokenStream subject = new Spin2TokenStream("a ADDBITS= b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("ADDBITS=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testImmediate() {
        Spin2TokenStream subject = new Spin2TokenStream("#a");

        assertEquals("#", subject.nextToken().getText());
        assertEquals("a", subject.nextToken().getText());
    }

    @Test
    void testLongImmediate() {
        Spin2TokenStream subject = new Spin2TokenStream("##a");

        assertEquals("##", subject.nextToken().getText());
        assertEquals("a", subject.nextToken().getText());
    }

    @Test
    void testLocalLabel() {
        Spin2TokenStream subject = new Spin2TokenStream(".a");

        assertEquals(".", subject.nextToken().getText());
        assertEquals("a", subject.nextToken().getText());
    }

    @Test
    void testAddress() {
        Spin2TokenStream subject = new Spin2TokenStream("@a");

        assertEquals("@", subject.nextToken().getText());
        assertEquals("a", subject.nextToken().getText());
    }

    @Test
    void testAbsoluteAddress() {
        Spin2TokenStream subject = new Spin2TokenStream("@@a");

        assertEquals("@@", subject.nextToken().getText());
        assertEquals("a", subject.nextToken().getText());
    }

    @Test
    void testAbsolute() {
        Spin2TokenStream subject = new Spin2TokenStream("#\\a");

        assertEquals("#", subject.nextToken().getText());
        assertEquals("\\", subject.nextToken().getText());
        assertEquals("a", subject.nextToken().getText());
    }

    @Test
    void testAbsoluteLocalLabel() {
        Spin2TokenStream subject = new Spin2TokenStream("#\\.a");

        assertEquals("#", subject.nextToken().getText());
        assertEquals("\\", subject.nextToken().getText());
        assertEquals(".", subject.nextToken().getText());
        assertEquals("a", subject.nextToken().getText());
    }

    @Test
    void testEqualOperatorNoSpace() {
        Spin2TokenStream subject = new Spin2TokenStream("a==b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("==", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testAssignOperatorNoSpace() {
        Spin2TokenStream subject = new Spin2TokenStream("a=b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("=", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testObjectMethod() {
        Spin2TokenStream subject = new Spin2TokenStream("a.b");

        assertEquals("a.b", subject.nextToken().getText());
    }

    @Test
    void testObjectConstant() {
        Spin2TokenStream subject = new Spin2TokenStream("a#b");

        assertEquals("a#b", subject.nextToken().getText());
    }

    @Test
    void testBit() {
        Spin2TokenStream subject = new Spin2TokenStream("a.");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(".", subject.nextToken().getText());
    }

    @Test
    void testPreRandom() {
        Spin2TokenStream subject = new Spin2TokenStream("??a");

        assertEquals("??", subject.nextToken().getText());
        assertEquals("a", subject.nextToken().getText());
    }

    @Test
    void testContinueNextLine() {
        Spin2TokenStream subject = new Spin2TokenStream("a := ...\n     1");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(":=", subject.nextToken().getText());
        assertEquals("...\n", subject.nextToken().getText());
        assertEquals("1", subject.nextToken().getText());
    }

    @Test
    void testScientificNotation1() {
        Spin2TokenStream subject = new Spin2TokenStream("1e2");

        assertEquals("1e2", subject.nextToken().getText());
    }

    @Test
    void testScientificNotation2() {
        Spin2TokenStream subject = new Spin2TokenStream("1e+2");

        assertEquals("1e+2", subject.nextToken().getText());
    }

    @Test
    void testScientificNotation3() {
        Spin2TokenStream subject = new Spin2TokenStream("1e-2");

        assertEquals("1e-2", subject.nextToken().getText());
    }

    @Test
    void testHexScientificNotation() {
        Spin2TokenStream subject = new Spin2TokenStream("$E+1");

        assertEquals("$E", subject.nextToken().getText());
        assertEquals("+", subject.nextToken().getText());
        assertEquals("1", subject.nextToken().getText());
    }

    @Test
    void testContiguosOperators() {
        Spin2TokenStream subject = new Spin2TokenStream("a:=++b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals(":=", subject.nextToken().getText());
        assertEquals("++", subject.nextToken().getText());
        assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testLFLineCount() {
        Spin2TokenStream subject = new Spin2TokenStream("a\nb\n\nc\n");

        assertEquals(0, subject.nextToken().line); // a
        assertEquals(0, subject.nextToken().line); // NL
        assertEquals(1, subject.nextToken().line); // b
        assertEquals(1, subject.nextToken().line); // NL
        assertEquals(2, subject.nextToken().line); // NL
        assertEquals(3, subject.nextToken().line); // c
        assertEquals(3, subject.nextToken().line); // NL
    }

    @Test
    void testCRLFLineCount() {
        Spin2TokenStream subject = new Spin2TokenStream("a\r\nb\r\n\r\nc\r\n");

        assertEquals(0, subject.nextToken().line); // a
        assertEquals(0, subject.nextToken().line); // NL
        assertEquals(1, subject.nextToken().line); // b
        assertEquals(1, subject.nextToken().line); // NL
        assertEquals(2, subject.nextToken().line); // NL
        assertEquals(3, subject.nextToken().line); // c
        assertEquals(3, subject.nextToken().line); // NL
    }

    @Test
    void testCRLineCount() {
        Spin2TokenStream subject = new Spin2TokenStream("a\rb\r\rc\r");

        assertEquals(0, subject.nextToken().line); // a
        assertEquals(0, subject.nextToken().line); // NL
        assertEquals(1, subject.nextToken().line); // b
        assertEquals(1, subject.nextToken().line); // NL
        assertEquals(2, subject.nextToken().line); // NL
        assertEquals(3, subject.nextToken().line); // c
        assertEquals(3, subject.nextToken().line); // NL
    }

    @Test
    void testBlockCommentLFLineNumbers() {
        Spin2TokenStream subject = new Spin2TokenStream("{ \n }\nA\n");

        Token token = subject.nextToken();
        assertEquals("{ \n }", token.getText());
        assertEquals(0, token.line);

        token = subject.nextToken();
        assertEquals("", token.getText());
        assertEquals(1, token.line);

        token = subject.nextToken();
        assertEquals("A", token.getText());
        assertEquals(2, token.line);
    }

    @Test
    void testBlockCommentCRLineNumbers() {
        Spin2TokenStream subject = new Spin2TokenStream("{ \r }\rA\r");

        Token token = subject.nextToken();
        assertEquals("{ \r }", token.getText());
        assertEquals(0, token.line);

        token = subject.nextToken();
        assertEquals("", token.getText());
        assertEquals(1, token.line);

        token = subject.nextToken();
        assertEquals("A", token.getText());
        assertEquals(2, token.line);
    }

    @Test
    void testBlockCommentCFLFLineNumbers() {
        Spin2TokenStream subject = new Spin2TokenStream("{ \r\n }\r\nA\r\n");

        Token token = subject.nextToken();
        assertEquals("{ \r\n }", token.getText());
        assertEquals(0, token.line);

        token = subject.nextToken();
        assertEquals("", token.getText());
        assertEquals(1, token.line);

        token = subject.nextToken();
        assertEquals("A", token.getText());
        assertEquals(2, token.line);
    }

    @Test
    void testAtString() {
        Spin2TokenStream subject = new Spin2TokenStream("@\"text\"");

        assertEquals("@", subject.nextToken().getText());
        assertEquals("\"text\"", subject.nextToken().getText());
    }

    @Test
    void testBacktickString() {
        Spin2TokenStream subject = new Spin2TokenStream("``(letter) lutcolors `uhex_long_array_(image_address, lut_size) longs_2bit");

        assertEquals("`", subject.nextToken().getText());
        assertEquals("`", subject.nextToken().getText());
        assertEquals("(", subject.nextToken().getText());
        assertEquals("letter", subject.nextToken().getText());
        assertEquals(")", subject.nextToken().getText());
        assertEquals(" lutcolors ", subject.nextToken().getText());
        assertEquals("`uhex_long_array_", subject.nextToken().getText());
        assertEquals("(", subject.nextToken().getText());
        assertEquals("image_address", subject.nextToken().getText());
        assertEquals(",", subject.nextToken().getText());
        assertEquals("lut_size", subject.nextToken().getText());
        assertEquals(")", subject.nextToken().getText());
        assertEquals(" longs_2bit", subject.nextToken().getText());
    }

    @Test
    void testA() {
        Spin2TokenStream subject = new Spin2TokenStream("`MyBitmap `uhex_(flag[i++ & $1F]) `dly(100)");

        assertEquals("`MyBitmap ", subject.nextToken().getText());
        assertEquals("`uhex_", subject.nextToken().getText());
        assertEquals("(", subject.nextToken().getText());
        assertEquals("flag", subject.nextToken().getText());
        assertEquals("[", subject.nextToken().getText());
        assertEquals("i", subject.nextToken().getText());
        assertEquals("++", subject.nextToken().getText());
        assertEquals("&", subject.nextToken().getText());
        assertEquals("$1F", subject.nextToken().getText());
        assertEquals("]", subject.nextToken().getText());
        assertEquals(")", subject.nextToken().getText());
    }

    @Test
    void testFieldPointer() {
        Spin2TokenStream subject = new Spin2TokenStream("^@a");

        assertEquals("^@", subject.nextToken().getText());
        assertEquals("a", subject.nextToken().getText());
    }

    @Test
    void testContinuationLineNumber() {
        Spin2TokenStream subject = new Spin2TokenStream("repeat a ... { multiline\r\ncomment}\n       from 1 ...\r\n       to 10\n");

        Token token = subject.nextToken();
        assertEquals("repeat", token.getText());
        assertEquals(0, token.line);
        assertEquals(0, token.column);
        token = subject.nextToken();
        assertEquals("a", token.getText());
        token = subject.nextToken();
        assertEquals("... { multiline\r\n"
            + "comment}\n"
            + "", token.getText());

        token = subject.nextToken();
        assertEquals("from", token.getText());
        assertEquals(2, token.line);
        assertEquals(7, token.column);
        token = subject.nextToken();
        assertEquals("1", token.getText());
        token = subject.nextToken();
        assertEquals("...\r\n", token.getText());

        token = subject.nextToken();
        assertEquals("to", token.getText());
        assertEquals(3, token.line);
        assertEquals(7, token.column);
        token = subject.nextToken();
        assertEquals("10", token.getText());
    }

}
