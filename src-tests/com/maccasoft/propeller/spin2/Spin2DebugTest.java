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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.model.Token;

class Spin2DebugTest {

    @Test
    void testRegister() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("reg", new NumberLiteral(10));

        String text = "debug(udec(reg))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(context, parse(text)));
        Assertions.assertEquals("01 04 41 72 65 67 00 80 0A 00", actual);
    }

    @Test
    void testRegisterArray() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("reg", new NumberLiteral(10));

        String text = "debug(udec_reg_array(#reg,#2))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(context, parse(text)));
        Assertions.assertEquals("01 04 51 72 65 67 00 00 0A 00 02 00", actual);
    }

    @Test
    void testRegisterValueOnly() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("reg", new NumberLiteral(10));

        String text = "debug(udec_(reg))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(context, parse(text)));
        Assertions.assertEquals("01 04 43 80 0A 00", actual);
    }

    @Test
    void testDly() {
        Spin2Context context = new Spin2Context();

        String text = "debug(dly(#100))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(context, parse(text)));
        Assertions.assertEquals("01 04 07 00 64 00", actual);
    }

    @Test
    void testMultipleArguments() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("reg1", new NumberLiteral(10));
        context.addSymbol("reg2", new NumberLiteral(11));
        context.addSymbol("reg3", new NumberLiteral(12));

        String text = "debug(udec(reg1,reg2,reg3))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(context, parse(text)));
        Assertions.assertEquals("01 04 41 72 65 67 31 00 80 0A 40 72 65 67 32 00 80 0B 40 72 65 67 33 00 80 0C 00", actual);
    }

    @Test
    void testMultipleStatements() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("reg1", new NumberLiteral(10));
        context.addSymbol("reg2", new NumberLiteral(11));
        context.addSymbol("reg3", new NumberLiteral(12));

        String text = "debug(udec(reg1), udec(reg2), udec(reg3))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(context, parse(text)));
        Assertions.assertEquals("01 04 41 72 65 67 31 00 80 0A 40 72 65 67 32 00 80 0B 40 72 65 67 33 00 80 0C 00", actual);
    }

    @Test
    void testString() {
        Spin2Context context = new Spin2Context();

        String text = "debug(\"start\")";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(context, parse(text)));
        Assertions.assertEquals("01 04 06 73 74 61 72 74 00 00", actual);
    }

    @Test
    void testZString1() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("ptr", new NumberLiteral(10));

        String text = "debug(zstr(ptr))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(context, parse(text)));
        Assertions.assertEquals("01 04 25 70 74 72 00 80 0A 00", actual);
    }

    @Test
    void testZString2() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("@ptr", new NumberLiteral(10));

        String text = "debug(zstr(@ptr))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(context, parse(text)));
        Assertions.assertEquals("01 04 25 40 70 74 72 00 80 0A 00", actual);
    }

    @Test
    void testLString() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("ptr", new NumberLiteral(10));

        String text = "debug(lstr(ptr,#12))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(context, parse(text)));
        Assertions.assertEquals("01 04 35 70 74 72 00 80 0A 00 0C 00", actual);
    }

    @Test
    void testBacktick() {
        Spin2Context context = new Spin2Context();

        String text = "debug(`12345)";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(context, parse(text)));
        Assertions.assertEquals("01 04 06 60 31 32 33 34 35 00 00", actual);
    }

    @Test
    void testStringBacktick() {
        Spin2Context context = new Spin2Context();

        String text = "debug(\"`12345\")";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(context, parse(text)));
        Assertions.assertEquals("01 04 06 60 31 32 33 34 35 00 00", actual);
    }

    @Test
    void testSpin() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("reg", new NumberLiteral(10));

        String text = "debug(udec(reg))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(context, parse(text)));
        Assertions.assertEquals("04 41 72 65 67 00 00", actual);
    }

    @Test
    void testSpinArray() {
        Spin2Context context = new Spin2Context();
        context.addSymbol("reg", new NumberLiteral(10));

        String text = "debug(udec_long_array(reg,2))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(context, parse(text)));
        Assertions.assertEquals("04 5D 72 65 67 00 00", actual);
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
