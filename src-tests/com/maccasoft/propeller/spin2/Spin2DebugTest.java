/*
 * Copyright (c) 2022 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.model.Token;

class Spin2DebugTest {

    @Test
    void testRegister() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("rega", new NumberLiteral(1));
        context.addSymbol("@rega", new NumberLiteral(4));

        String text = "debug(udec(rega))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 41 72 65 67 61 00 80 01 00", actual);
    }

    @Test
    void testRegisterImmediate() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("rega", new NumberLiteral(1));
        context.addSymbol("@rega", new NumberLiteral(4));

        String text = "debug(udec(#rega))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 41 23 72 65 67 61 00 00 01 00", actual);
    }

    @Test
    void testRegisterPointer() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("rega", new NumberLiteral(1));
        context.addSymbol("@rega", new NumberLiteral(4));

        String text = "debug(udec(@rega))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 41 40 72 65 67 61 00 80 04 00", actual);
    }

    @Test
    void testRegisterPointerImmediate() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("rega", new NumberLiteral(1));
        context.addSymbol("@rega", new NumberLiteral(4));

        String text = "debug(udec(#@rega))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 41 23 40 72 65 67 61 00 00 04 00", actual);
    }

    @Test
    void testRegisterArray() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("rega", new NumberLiteral(1));
        context.addSymbol("@rega", new NumberLiteral(4));

        String text = "debug(udec_reg_array(rega,4))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 51 72 65 67 61 00 80 01 80 04 00", actual);
    }

    @Test
    void testRegisterArrayImmediateCount() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("rega", new NumberLiteral(1));
        context.addSymbol("@rega", new NumberLiteral(4));

        String text = "debug(udec_reg_array(rega,#4))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 51 72 65 67 61 00 80 01 00 04 00", actual);
    }

    @Test
    void testRegisterValueOnly() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("reg", new NumberLiteral(10));

        String text = "debug(udec_(reg))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 43 80 0A 00", actual);
    }

    @Test
    void testDly() {
        Spin2Context context = new Spin2Context();

        String text = "debug(dly(#100))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 07 00 64 00", actual);
    }

    @Test
    void testMultipleArguments() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("reg1", new NumberLiteral(10));
        context.addSymbol("reg2", new NumberLiteral(11));
        context.addSymbol("reg3", new NumberLiteral(12));

        String text = "debug(udec(reg1,reg2,reg3))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 41 72 65 67 31 00 80 0A 40 72 65 67 32 00 80 0B 40 72 65 67 33 00 80 0C 00", actual);
    }

    @Test
    void testMultipleStatements() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("reg1", new NumberLiteral(10));
        context.addSymbol("reg2", new NumberLiteral(11));
        context.addSymbol("reg3", new NumberLiteral(12));

        String text = "debug(udec(reg1), udec(reg2), udec(reg3))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 41 72 65 67 31 00 80 0A 40 72 65 67 32 00 80 0B 40 72 65 67 33 00 80 0C 00", actual);
    }

    @Test
    void testString() {
        Spin2Context context = new Spin2Context();

        String text = "debug(\"start\")";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 06 73 74 61 72 74 00 00", actual);
    }

    @Test
    void testZStr() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("rega", new NumberLiteral(1));
        context.addSymbol("@rega", new NumberLiteral(4));

        String text = "debug(zstr(rega))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 25 72 65 67 61 00 80 01 00", actual);
    }

    @Test
    void testZStrPointer() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("rega", new NumberLiteral(1));
        context.addSymbol("@rega", new NumberLiteral(4));

        String text = "debug(zstr(@rega))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 25 40 72 65 67 61 00 80 04 00", actual);
    }

    @Test
    void testZStrImmediate() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("rega", new NumberLiteral(1));
        context.addSymbol("@rega", new NumberLiteral(4));

        String text = "debug(zstr(#rega))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 25 23 72 65 67 61 00 00 01 00", actual);
    }

    @Test
    void testLString() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("ptr", new NumberLiteral(10));

        String text = "debug(lstr(ptr,#12))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 35 70 74 72 00 80 0A 00 0C 00", actual);
    }

    @Test
    void testBacktick() {
        Spin2Context context = new Spin2Context();

        String text = "debug(`12345)";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 06 60 31 32 33 34 35 00 00", actual);
    }

    @Test
    void testStringBacktick() {
        Spin2Context context = new Spin2Context();

        String text = "debug(\"`12345\")";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 06 60 31 32 33 34 35 00 00", actual);
    }

    @Test
    void testSpin() {
        String text = "debug(udec(reg))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(parse(text)));
        Assertions.assertEquals("04 41 72 65 67 00 00", actual);
    }

    @Test
    void testSpinArray() {
        String text = "debug(udec_long_array(reg,2))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(parse(text)));
        Assertions.assertEquals("04 5D 72 65 67 00 00", actual);
    }

    @Test
    void testSpinCondition() {
        String text = "debug(if(a > 1), udec(reg))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(parse(text)));
        Assertions.assertEquals("02 04 41 72 65 67 00 00", actual);
    }

    @Test
    void testSpinConditionMiddle() {
        String text = "debug(udec(reg), if(a > 1), udec(a))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(parse(text)));
        Assertions.assertEquals("04 41 72 65 67 00 02 40 61 00 00", actual);
    }

    @Test
    void testPAsmCondition() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("a", new NumberLiteral(1));
        context.addSymbol("reg", new NumberLiteral(10));

        String text = "debug(if(a), udec(reg))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 02 80 01 04 41 72 65 67 00 80 0A 00", actual);
    }

    @Test
    void testPAsmConditionMiddle() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("a", new NumberLiteral(1));
        context.addSymbol("reg", new NumberLiteral(10));

        String text = "debug(udec(reg), if(a), udec(a))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 41 72 65 67 00 80 0A 02 80 01 40 61 00 80 01 00", actual);
    }

    List<Token> parseTokens(String text) {
        List<Token> tokens = new ArrayList<Token>();

        Spin2TokenStream stream = new Spin2TokenStream(text);
        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF) {
                break;
            }
            tokens.add(token);
        }

        return tokens;
    }

    Spin2StatementNode parse(String text) {
        Spin2TreeBuilder builder = new Spin2TreeBuilder();

        Spin2TokenStream stream = new Spin2TokenStream(text);
        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF) {
                break;
            }
            builder.addToken(token);
        }

        return builder.getRoot();
    }

    String dumpDebugData(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            if (i != 0) {
                sb.append(" ");
            }
            sb.append(String.format("%02X", data[i] & 0xFF));
        }
        return sb.toString();
    }

}
