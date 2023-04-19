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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.Token;

class Spin2DebugTest {

    @Test
    void testDebugExpression() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    debug(`index=`udec(long[a++]))\n"
            + "\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       11 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       01             (stack size)\n"
            + "'     debug(`index=`udec(long[a++]))\n"
            + "00009 00009       D0             VAR_SETUP LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       87             POST_INC (push)\n"
            + "0000B 0000B       68 80          MEM_READ LONG\n"
            + "0000D 0000D       43 04 01       DEBUG #1\n"
            + "00010 00010       04             RETURN\n"
            + "00011 00011       00 00 00       Padding\n"
            + "' Debug data\n"
            + "00B24 00000       1A 00         \n"
            + "00B26 00002       04 00         \n"
            + "00B28 00004       04 06 60 69 6E\n"
            + "00B2D 00009       64 65 78 3D 00\n"
            + "00B32 0000E       41 6C 6F 6E 67\n"
            + "00B37 00013       5B 61 2B 2B 5D\n"
            + "00B3C 00018       00 00\n"
            + "", compile(text));
    }

    @Test
    void testRegister() {
        Context context = new Context();
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
        Context context = new Context();
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
        Context context = new Context();
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
        Context context = new Context();
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
        Context context = new Context();
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
        Context context = new Context();
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
        Context context = new Context();
        context.addSymbol("reg", new NumberLiteral(10));

        String text = "debug(udec_(reg))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 43 80 0A 00", actual);
    }

    @Test
    void testDly() {
        Context context = new Context();

        String text = "debug(dly(#100))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 07 00 64 00", actual);
    }

    @Test
    void testMultipleArguments() {
        Context context = new Context();
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
        Context context = new Context();
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
    void testSpinString() {
        String text = "debug(\"start\")";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(parse(text)));
        Assertions.assertEquals("04 06 73 74 61 72 74 00 00", actual);
    }

    @Test
    void testSpinStringAndVars() {
        String text = "debug(udec(reg1), \"start\", udec(reg2,reg3))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(parse(text)));
        Assertions.assertEquals("04 41 72 65 67 31 00 06 73 74 61 72 74 00 41 72 65 67 32 00 40 72 65 67 33 00 00", actual);
    }

    @Test
    void testPAsmString() {
        Context context = new Context();

        String text = "debug(\"start\")";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 06 73 74 61 72 74 00 00", actual);
    }

    @Test
    void testPAsmStringAndRegisters() {
        Context context = new Context();
        context.addSymbol("reg1", new NumberLiteral(10));
        context.addSymbol("reg2", new NumberLiteral(11));
        context.addSymbol("reg3", new NumberLiteral(12));

        String text = "debug(udec(reg1), \"start\", udec(reg2), udec(reg3))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 41 72 65 67 31 00 80 0A 06 73 74 61 72 74 00 41 72 65 67 32 00 80 0B 40 72 65 67 33 00 80 0C 00", actual);
    }

    @Test
    void testZStr() {
        Context context = new Context();
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
        Context context = new Context();
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
        Context context = new Context();
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
        Context context = new Context();
        context.addSymbol("ptr", new NumberLiteral(10));

        String text = "debug(lstr(ptr,#12))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 35 70 74 72 00 80 0A 00 0C 00", actual);
    }

    @Test
    void testBacktick() {
        Context context = new Context();

        String text = "debug(`12345)";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("01 04 06 60 31 32 33 34 35 00 00", actual);
    }

    @Test
    void testStringBacktick() {
        Context context = new Context();

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
        Context context = new Context();
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
        Context context = new Context();
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
            if ("@".equals(token.getText())) {
                Token nextToken = stream.peekNext();
                if (token.isAdjacent(nextToken)) {
                    token = token.merge(stream.nextToken());
                    if (".".equals(nextToken.getText())) {
                        nextToken = stream.peekNext();
                        if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                            token = token.merge(stream.nextToken());
                        }
                    }
                }
            }
            else if (".".equals(token.getText())) {
                Token nextToken = stream.peekNext();
                if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                    token = token.merge(stream.nextToken());
                }
            }
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

    String compile(String text) throws Exception {
        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Spin2Compiler compiler = new Spin2Compiler();
        compiler.setDebugEnabled(true);
        Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompiler(compiler, new ArrayList<>());
        Spin2Object obj = objectCompiler.compileObject(root);
        obj.setDebugData(objectCompiler.generateDebugData());
        obj.setDebugger(new Spin2Debugger());

        for (CompilerException msg : objectCompiler.getMessages()) {
            if (msg.type == CompilerException.ERROR) {
                throw msg;
            }
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateListing(new PrintStream(os));

        return os.toString().replaceAll("\\r\\n", "\n");
    }

}
