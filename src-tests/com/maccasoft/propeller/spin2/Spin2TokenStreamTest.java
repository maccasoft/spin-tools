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

class Spin2TokenStreamTest {

    /*@Test
    void testKeyword() {
        Spin2TokenStream subject = new Spin2TokenStream(""
            + "CON  EnableFlow = 8                'single assignments\n"
            + "     DisableFlow = 4\n"
            + "     ColorBurstFreq = 3_579_545\n"
            + "");
    
        assertEquals("CON", subject.nextToken().getText());
        assertEquals("EnableFlow", subject.nextToken().getText());
        assertEquals("=", subject.nextToken().getText());
        assertEquals("8", subject.nextToken().getText());
        assertEquals(Token.NL, subject.nextToken().type);
    
        assertEquals("DisableFlow", subject.nextToken().getText());
        assertEquals("=", subject.nextToken().getText());
        assertEquals("4", subject.nextToken().getText());
        assertEquals(Token.NL, subject.nextToken().type);
    
        assertEquals("ColorBurstFreq", subject.nextToken().getText());
        assertEquals("=", subject.nextToken().getText());
        assertEquals("3_579_545", subject.nextToken().getText());
        assertEquals(Token.NL, subject.nextToken().type);
    
        assertEquals(Token.EOF, subject.nextToken().type);
    }
    
    @Test
    void testLineCount() {
        Spin2TokenStream subject = new Spin2TokenStream(""
            + "CON  EnableFlow = 8                'single assignments\n"
            + "     DisableFlow = 4\n"
            + "     ColorBurstFreq = 3_579_545\n"
            + "");
    
        assertEquals(0, subject.nextToken().line);
        assertEquals(0, subject.nextToken().line);
        assertEquals(0, subject.nextToken().line);
        assertEquals(0, subject.nextToken().line);
        //assertEquals(0, subject.nextToken().line);
        assertEquals(0, subject.nextToken().line);
    
        assertEquals(1, subject.nextToken().line);
        assertEquals(1, subject.nextToken().line);
        assertEquals(1, subject.nextToken().line);
        assertEquals(1, subject.nextToken().line);
    
        assertEquals(2, subject.nextToken().line);
        assertEquals(2, subject.nextToken().line);
        assertEquals(2, subject.nextToken().line);
        assertEquals(2, subject.nextToken().line);
    
        assertEquals(Token.EOF, subject.nextToken().type);
    }
    
    @Test
    void testEmptyLineCount() {
        Spin2TokenStream subject = new Spin2TokenStream(""
            + "CON  EnableFlow = 8                'single assignments\n"
            + "\n"
            + "\n"
            + "     DisableFlow = 4\n"
            + "\n"
            + "     ColorBurstFreq = 3_579_545\n"
            + "");
    
        assertEquals(0, subject.nextToken().line);
        assertEquals(0, subject.nextToken().line);
        assertEquals(0, subject.nextToken().line);
        assertEquals(0, subject.nextToken().line);
        //assertEquals(0, subject.nextToken().line);
        assertEquals(0, subject.nextToken().line);
    
        assertEquals(3, subject.nextToken().line);
        assertEquals(3, subject.nextToken().line);
        assertEquals(3, subject.nextToken().line);
        assertEquals(3, subject.nextToken().line);
    
        assertEquals(5, subject.nextToken().line);
        assertEquals(5, subject.nextToken().line);
        assertEquals(5, subject.nextToken().line);
        assertEquals(5, subject.nextToken().line);
    
        assertEquals(Token.EOF, subject.nextToken().type);
    }
    
    @Test
    void testBlockCommentsLineCount() {
        Spin2TokenStream subject = new Spin2TokenStream(""
            + "{\n"
            + "    comment\n"
            + "}\n"
            + "CON  EnableFlow = 8\n"
            + "\n"
            + "\n"
            + "     DisableFlow = 4\n"
            + "\n"
            + "{\n"
            + "    comment\n"
            + "}\n"
            + "     ColorBurstFreq = 3_579_545\n"
            + "");
    
        assertEquals(2, subject.nextToken().line); // EOL after block comment
    
        assertEquals(3, subject.nextToken().line);
        assertEquals(3, subject.nextToken().line);
        assertEquals(3, subject.nextToken().line);
        assertEquals(3, subject.nextToken().line);
        assertEquals(3, subject.nextToken().line);
    
        assertEquals(6, subject.nextToken().line);
        assertEquals(6, subject.nextToken().line);
        assertEquals(6, subject.nextToken().line);
        assertEquals(6, subject.nextToken().line);
    
        assertEquals(10, subject.nextToken().line); // EOL after block comment
    
        assertEquals(11, subject.nextToken().line);
        assertEquals(11, subject.nextToken().line);
        assertEquals(11, subject.nextToken().line);
        assertEquals(11, subject.nextToken().line);
    
        assertEquals(Token.EOF, subject.nextToken().type);
    }
    
    @Test
    void testColumnCount() {
        Spin2TokenStream subject = new Spin2TokenStream(""
            + "CON  EnableFlow = 8                'single assignments\n"
            + "     DisableFlow = 4\n"
            + "     ColorBurstFreq = 3_579_545\n"
            + "");
    
        assertEquals(0, subject.nextToken().column); // CON
        assertEquals(5, subject.nextToken().column); // EnableFlow
        assertEquals(16, subject.nextToken().column); // =
        assertEquals(18, subject.nextToken().column); // 8
        assertEquals(55, subject.nextToken().column); // NL
    
        assertEquals(5, subject.nextToken().column); // DisableFlow
        assertEquals(17, subject.nextToken().column); // =
        assertEquals(19, subject.nextToken().column); // 4
        assertEquals(20, subject.nextToken().column); // NL
    
        assertEquals(5, subject.nextToken().column); // ColorBurstFreq
        assertEquals(20, subject.nextToken().column); // =
        assertEquals(22, subject.nextToken().column); // 3_579_545
        assertEquals(31, subject.nextToken().column); // NL
    }
    
    @Test
    void testGroupConsecutiveEndOfLine() {
        Spin2TokenStream subject = new Spin2TokenStream(""
            + "\n"
            + "\r\n"
            + "\n"
            + "\n"
            + "");
    
        assertEquals("\n\r\n\n\n", subject.nextToken().getText());
    }
    
    @Test
    void testGroupCRLF() {
        Spin2TokenStream subject = new Spin2TokenStream(""
            + " \n"
            + " \r\n"
            + "");
    
        assertEquals("\n", subject.nextToken().getText());
        assertEquals("\r\n", subject.nextToken().getText());
    }
    
    @Test
    void testOneCharacterOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("+-*");
    
        assertEquals("+", subject.nextToken().getText());
        assertEquals("-", subject.nextToken().getText());
        assertEquals("*", subject.nextToken().getText());
        assertEquals("/", subject.nextToken().getText());
    }
    
    @Test
    void testTwoCharactersOperator() {
        Spin2TokenStream subject = new Spin2TokenStream(":=+=-=*=/=//||&&^^+/+<<>");
    
        assertEquals(":=", subject.nextToken().getText());
        assertEquals("+=", subject.nextToken().getText());
        assertEquals("-=", subject.nextToken().getText());
        assertEquals("*=", subject.nextToken().getText());
        assertEquals("/=", subject.nextToken().getText());
        assertEquals("//", subject.nextToken().getText());
        assertEquals("||", subject.nextToken().getText());
        assertEquals("&&", subject.nextToken().getText());
        assertEquals("^^", subject.nextToken().getText());
        assertEquals("+/", subject.nextToken().getText());
        assertEquals("+<", subject.nextToken().getText());
        assertEquals("<>", subject.nextToken().getText());
    }
    
    @Test
    void testThreeCharactersOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("<<=>>=+//<=>");
    
        assertEquals("<<=", subject.nextToken().getText());
        assertEquals(">>=", subject.nextToken().getText());
        assertEquals("+//", subject.nextToken().getText());
        assertEquals("<=>", subject.nextToken().getText());
    }
    
    @Test
    void testPasmPrefix() {
        Spin2TokenStream subject = new Spin2TokenStream("##@label");
    
        assertEquals("##", subject.nextToken().getText());
        assertEquals("@label", subject.nextToken().getText());
    }
    
    @Test
    void testLocalLabel() {
        Spin2TokenStream subject = new Spin2TokenStream(".label @.label");
    
        assertEquals(".label", subject.nextToken().getText());
        assertEquals("@.label", subject.nextToken().getText());
    }
    
    @Test
    void testDebugStatement() {
        Spin2TokenStream subject = new Spin2TokenStream("    debug(`s `uhex_long_array_(@buff, 512) `dly(50))  'show data\n");
    
        assertEquals("debug", subject.nextToken().getText());
        assertEquals("(", subject.nextToken().getText());
        assertEquals("`s `uhex_long_array_(@buff, 512) `dly(50)", subject.nextToken().getText());
        assertEquals(")", subject.nextToken().getText());
    }
    
    @Test
    void testBlockComment() {
        Spin2TokenStream subject = new Spin2TokenStream("{ first line\n  second line }");
    
        assertEquals("{ first line\n  second line }", subject.nextToken(true).getText());
    }
    
    @Test
    void testLineComment() {
        Spin2TokenStream subject = new Spin2TokenStream("' first line\n' second line");
    
        assertEquals("' first line", subject.nextToken(true).getText());
        assertEquals("\n", subject.nextToken(true).getText());
        assertEquals("' second line", subject.nextToken(true).getText());
    }
    
    @Test
    void testBlockDocumentComment() {
        Spin2TokenStream subject = new Spin2TokenStream("{{ first line\n  second line }}");
    
        assertEquals("{{ first line\n  second line }}", subject.nextToken(true).getText());
    }
    
    @Test
    void testLineDocumentComment() {
        Spin2TokenStream subject = new Spin2TokenStream("'' first line\n'' second line");
    
        assertEquals("'' first line", subject.nextToken(true).getText());
        assertEquals("\n", subject.nextToken(true).getText());
        assertEquals("'' second line", subject.nextToken(true).getText());
    }
    
    @Test
    void testNestedBlockComment() {
        Spin2TokenStream subject = new Spin2TokenStream("{ first line\n{ second line }}");
    
        assertEquals("{ first line\n{ second line }}", subject.nextToken(true).getText());
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
    void testDecimalNumber() {
        Spin2TokenStream subject = new Spin2TokenStream("1.234");
    
        assertEquals("1.234", subject.nextToken().getText());
    }*/

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
    void testNotEqualOperator() {
        Spin2TokenStream subject = new Spin2TokenStream("a <> b");

        assertEquals("a", subject.nextToken().getText());
        assertEquals("<>", subject.nextToken().getText());
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
    void testAndAssignmentOperator2() {
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

        assertEquals(".a", subject.nextToken().getText());
    }

    @Test
    void testAddress() {
        Spin2TokenStream subject = new Spin2TokenStream("@a");

        assertEquals("@a", subject.nextToken().getText());
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
        assertEquals(".a", subject.nextToken().getText());
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

}
