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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.SpinObject.DataObject;
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
            + "00B28 00000       0F 00         \n"
            + "00B2A 00002       04 00         \n"
            + "' #1\n"
            + "00B2C 00004       06 60 69 6E 64 STRING (`index=)\n"
            + "00B31 00009       65 78 3D 00\n"
            + "00B35 0000D       43             UDEC\n"
            + "00B36 0000E       00             DONE\n"
            + "", compile(text));
    }

    @Test
    void testRepeatDebug() throws Exception {
        String text = ""
            + "PUB main() | a\n"
            + "\n"
            + "    repeat\n"
            + "        debug(udec(a))\n"
            + "        a := 1\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       12 00 00 00    End\n"
            + "' PUB main() | a\n"
            + "00008 00008       01             (stack size)\n"
            + "'     repeat\n"
            + "'         debug(udec(a))\n"
            + "00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0000A 0000A       43 04 01       DEBUG #1\n"
            + "'         a := 1\n"
            + "0000D 0000D       A2             CONSTANT (1)\n"
            + "0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "0000F 0000F       12 79          JMP $00009 (-7)\n"
            + "00011 00011       04             RETURN\n"
            + "00012 00012       00 00          Padding\n"
            + "' Debug data\n"
            + "00B28 00000       09 00         \n"
            + "00B2A 00002       04 00         \n"
            + "' #1\n"
            + "00B2C 00004       04             COGN\n"
            + "00B2D 00005       41 61 00       UDEC(a)\n"
            + "00B30 00008       00             DONE\n"
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
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       04             COGN\n"
            + "00002 00002       41 72 65 67 61 UDEC(rega)\n"
            + "00007 00007       00 80 01\n"
            + "0000A 0000A       00             DONE\n"
            + "", actual);
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
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       04             COGN\n"
            + "00002 00002       41 23 72 65 67 UDEC(#rega)\n"
            + "00007 00007       61 00 00 01\n"
            + "0000B 0000B       00             DONE\n"
            + "", actual);
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
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       04             COGN\n"
            + "00002 00002       41 40 72 65 67 UDEC(@rega)\n"
            + "00007 00007       61 00 80 04\n"
            + "0000B 0000B       00             DONE\n"
            + "", actual);
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
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       04             COGN\n"
            + "00002 00002       41 23 40 72 65 UDEC(#@rega)\n"
            + "00007 00007       67 61 00 00 04\n"
            + "0000C 0000C       00             DONE\n"
            + "", actual);
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
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       04             COGN\n"
            + "00002 00002       51 72 65 67 61 UDEC_REG_ARRAY(rega)\n"
            + "00007 00007       00 80 01 80 04\n"
            + "0000C 0000C       00             DONE\n"
            + "", actual);
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
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       04             COGN\n"
            + "00002 00002       51 72 65 67 61 UDEC_REG_ARRAY(rega)\n"
            + "00007 00007       00 80 01 00 04\n"
            + "0000C 0000C       00             DONE\n"
            + "", actual);
    }

    @Test
    void testRegisterValueOnly() {
        Context context = new Context();
        context.addSymbol("reg", new NumberLiteral(10));

        String text = "debug(udec_(reg))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       04             COGN\n"
            + "00002 00002       43 80 0A       UDEC\n"
            + "00005 00005       00             DONE\n"
            + "", actual);
    }

    @Test
    void testDly() {
        Context context = new Context();

        String text = "debug(dly(#100))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       04             COGN\n"
            + "00002 00002       07 00 64       DLY\n"
            + "00005 00005       00             DONE\n"
            + "", actual);
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
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       04             COGN\n"
            + "00002 00002       41 72 65 67 31 UDEC(reg1)\n"
            + "00007 00007       00 80 0A\n"
            + "0000A 0000A       40 72 65 67 32 UDEC(reg2)\n"
            + "0000F 0000F       00 80 0B\n"
            + "00012 00012       40 72 65 67 33 UDEC(reg3)\n"
            + "00017 00017       00 80 0C\n"
            + "0001A 0001A       00             DONE\n"
            + "", actual);
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
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       04             COGN\n"
            + "00002 00002       41 72 65 67 31 UDEC(reg1)\n"
            + "00007 00007       00 80 0A\n"
            + "0000A 0000A       40 72 65 67 32 UDEC(reg2)\n"
            + "0000F 0000F       00 80 0B\n"
            + "00012 00012       40 72 65 67 33 UDEC(reg3)\n"
            + "00017 00017       00 80 0C\n"
            + "0001A 0001A       00             DONE\n"
            + "", actual);
    }

    @Test
    void testSpinString() {
        String text = "debug(\"start\")";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(parse(text)));
        Assertions.assertEquals(""
            + "00000 00000       04             COGN\n"
            + "00001 00001       06 73 74 61 72 STRING (start)\n"
            + "00006 00006       74 00\n"
            + "00008 00008       00             DONE\n"
            + "", actual);
    }

    @Test
    void testSpinStringConcatenate() {
        String text = "debug(\"start\", 13, 10, \"end\", 13, 10)";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(parse(text)));
        Assertions.assertEquals(""
            + "00000 00000       04             COGN\n"
            + "00001 00001       06 73 74 61 72 STRING (start..end..)\n"
            + "00006 00006       74 0D 0A 65 6E\n"
            + "0000B 0000B       64 0D 0A 00\n"
            + "0000F 0000F       00             DONE\n"
            + "", actual);
    }

    @Test
    void testSpinStringAndVars() {
        String text = "debug(udec(reg1), \"start\", udec(reg2,reg3))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(parse(text)));
        Assertions.assertEquals(""
            + "00000 00000       04             COGN\n"
            + "00001 00001       41 72 65 67 31 UDEC(reg1)\n"
            + "00006 00006       00\n"
            + "00007 00007       06 73 74 61 72 STRING (start)\n"
            + "0000C 0000C       74 00\n"
            + "0000E 0000E       41 72 65 67 32 UDEC(reg2)\n"
            + "00013 00013       00\n"
            + "00014 00014       40 72 65 67 33 UDEC(reg3)\n"
            + "00019 00019       00\n"
            + "0001A 0001A       00             DONE\n"
            + "", actual);
    }

    @Test
    void testPAsmString() {
        Context context = new Context();

        String text = "debug(\"start\")";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       04             COGN\n"
            + "00002 00002       06 73 74 61 72 STRING (start)\n"
            + "00007 00007       74 00\n"
            + "00009 00009       00             DONE\n"
            + "", actual);
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
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       04             COGN\n"
            + "00002 00002       41 72 65 67 31 UDEC(reg1)\n"
            + "00007 00007       00 80 0A\n"
            + "0000A 0000A       06 73 74 61 72 STRING (start)\n"
            + "0000F 0000F       74 00\n"
            + "00011 00011       41 72 65 67 32 UDEC(reg2)\n"
            + "00016 00016       00 80 0B\n"
            + "00019 00019       40 72 65 67 33 UDEC(reg3)\n"
            + "0001E 0001E       00 80 0C\n"
            + "00021 00021       00             DONE\n"
            + "", actual);
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
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       04             COGN\n"
            + "00002 00002       25 72 65 67 61 ZSTR(rega)\n"
            + "00007 00007       00 80 01\n"
            + "0000A 0000A       00             DONE\n"
            + "", actual);
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
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       04             COGN\n"
            + "00002 00002       25 40 72 65 67 ZSTR(@rega)\n"
            + "00007 00007       61 00 80 04\n"
            + "0000B 0000B       00             DONE\n"
            + "", actual);
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
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       04             COGN\n"
            + "00002 00002       25 23 72 65 67 ZSTR(#rega)\n"
            + "00007 00007       61 00 00 01\n"
            + "0000B 0000B       00             DONE\n"
            + "", actual);
    }

    @Test
    void testLString() {
        Context context = new Context();
        context.addSymbol("ptr", new NumberLiteral(10));

        String text = "debug(lstr(ptr,#12))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       04             COGN\n"
            + "00002 00002       35 70 74 72 00 LSTR(ptr)\n"
            + "00007 00007       80 0A 00 0C\n"
            + "0000B 0000B       00             DONE\n"
            + "", actual);
    }

    @Test
    void testBacktick() {
        Context context = new Context();

        String text = "debug(`12345)";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       06 60 31 32 33 STRING (`12345)\n"
            + "00006 00006       34 35 00\n"
            + "00009 00009       00             DONE\n"
            + "", actual);
    }

    @Test
    void testBacktickCommand() {
        Context context = new Context();
        context.addSymbol("a", new NumberLiteral(1));
        context.addSymbol("b", new NumberLiteral(2));

        String text = "debug(`12345 `(a,b))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       06 60 31 32 33 STRING (`12345 )\n"
            + "00006 00006       34 35 20 00\n"
            + "0000A 0000A       63 80 01       SDEC\n"
            + "0000D 0000D       62 80 02       SDEC\n"
            + "00010 00010       00             DONE\n"
            + "", actual);
    }

    @Test
    void testStringBacktick() {
        Context context = new Context();

        String text = "debug(\"`12345\")";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       06 60 31 32 33 STRING (`12345)\n"
            + "00006 00006       34 35 00\n"
            + "00009 00009       00             DONE\n"
            + "", actual);
    }

    @Test
    void testStringBacktickCommand() {
        Context context = new Context();
        context.addSymbol("a", new NumberLiteral(1));

        String text = "debug(`12345`(a))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       06 60 31 32 33 STRING (`12345)\n"
            + "00006 00006       34 35 00\n"
            + "00009 00009       63 80 01       SDEC\n"
            + "0000C 0000C       00             DONE\n"
            + "", actual);
    }

    @Test
    void testSpin() {
        String text = "debug(udec(reg))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(parse(text)));
        Assertions.assertEquals(""
            + "00000 00000       04             COGN\n"
            + "00001 00001       41 72 65 67 00 UDEC(reg)\n"
            + "00006 00006       00             DONE\n"
            + "", actual);
    }

    @Test
    void testSpinBacktickCommand() {
        String text = "debug(`12345 `uhex_(a,b))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(parse(text)));
        Assertions.assertEquals(""
            + "00000 00000       06 60 31 32 33 STRING (`12345 )\n"
            + "00005 00005       34 35 20 00\n"
            + "00009 00009       83             UHEX\n"
            + "0000A 0000A       82             UHEX\n"
            + "0000B 0000B       00             DONE\n"
            + "", actual);
    }

    @Test
    void testSpinBacktickShortCommand() {
        String text = "debug(`12345 `(a,b))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(parse(text)));
        Assertions.assertEquals(""
            + "00000 00000       06 60 31 32 33 STRING (`12345 )\n"
            + "00005 00005       34 35 20 00\n"
            + "00009 00009       63             SDEC\n"
            + "0000A 0000A       62             SDEC\n"
            + "0000B 0000B       00             DONE\n"
            + "", actual);
    }

    @Test
    void testSpinBacktickCommandConcatenate() {
        String text = "debug(`a = `(a) b = `(b))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(parse(text)));
        Assertions.assertEquals(""
            + "00000 00000       06 60 61 20 3D STRING (`a = )\n"
            + "00005 00005       20 00\n"
            + "00007 00007       63             SDEC\n"
            + "00008 00008       06 20 62 20 3D STRING ( b = )\n"
            + "0000D 0000D       20 00\n"
            + "0000F 0000F       63             SDEC\n"
            + "00010 00010       00             DONE\n"
            + "", actual);
    }

    @Test
    void testSpinArray() {
        String text = "debug(udec_long_array(reg,2))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(parse(text)));
        Assertions.assertEquals(""
            + "00000 00000       04             COGN\n"
            + "00001 00001       5D 72 65 67 00 UDEC_LONG_ARRAY(reg)\n"
            + "00006 00006       00             DONE\n"
            + "", actual);
    }

    @Test
    void testSpinCondition() {
        String text = "debug(if(a > 1), udec(reg))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(parse(text)));
        Assertions.assertEquals(""
            + "00000 00000       02             IF\n"
            + "00001 00001       04             COGN\n"
            + "00002 00002       41 72 65 67 00 UDEC(reg)\n"
            + "00007 00007       00             DONE\n"
            + "", actual);
    }

    @Test
    void testSpinConditionMiddle() {
        String text = "debug(udec(reg), if(a > 1), udec(a))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(parse(text)));
        Assertions.assertEquals(""
            + "00000 00000       04             COGN\n"
            + "00001 00001       41 72 65 67 00 UDEC(reg)\n"
            + "00006 00006       02             IF\n"
            + "00007 00007       40 61 00       UDEC(a)\n"
            + "0000A 0000A       00             DONE\n"
            + "", actual);
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
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       02 80 01       IF\n"
            + "00004 00004       04             COGN\n"
            + "00005 00005       41 72 65 67 00 UDEC(reg)\n"
            + "0000A 0000A       80 0A\n"
            + "0000C 0000C       00             DONE\n"
            + "", actual);
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
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       04             COGN\n"
            + "00002 00002       41 72 65 67 00 UDEC(reg)\n"
            + "00007 00007       80 0A\n"
            + "00009 00009       02 80 01       IF\n"
            + "0000C 0000C       40 61 00 80 01 UDEC(a)\n"
            + "00011 00011       00             DONE\n"
            + "", actual);
    }

    @Test
    void testChar() {
        String text = "debug(``#(letter) lutcolors)";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(parse(text)));
        Assertions.assertEquals(""
            + "00000 00000       06 60 00       STRING (`)\n"
            + "00003 00003       05             CHAR\n"
            + "00004 00004       06 20 6C 75 74 STRING ( lutcolors)\n"
            + "00009 00009       63 6F 6C 6F 72\n"
            + "0000E 0000E       73 00\n"
            + "00010 00010       00             DONE\n"
            + "", actual);
    }

    @Test
    void testPAsmChar() {
        Context context = new Context();
        context.addSymbol("letter", new NumberLiteral(1));

        String text = "debug(``#(letter) lutcolors)";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals(""
            + "00000 00000       01             ASMMODE\n"
            + "00001 00001       06 60 00       STRING (`)\n"
            + "00004 00004       05 80 01       CHAR\n"
            + "00007 00007       06 20 6C 75 74 STRING ( lutcolors)\n"
            + "0000C 0000C       63 6F 6C 6F 72\n"
            + "00011 00011       73 00\n"
            + "00013 00013       00             DONE\n"
            + "", actual);
    }

    @Test
    void testUnknownDebugCommand() {
        String text = "debug(`MyPlot `unknown_command(a))";

        Spin2Debug subject = new Spin2Debug();
        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                subject.compileDebugStatement(parse(text));
            }

        });
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
        Spin2TreeBuilder builder = new Spin2TreeBuilder(new Context());

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

    String dumpDebugData(List<DataObject> l) {
        Spin2Object object = new Spin2Object();

        for (DataObject obj : l) {
            object.write(obj);
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        object.generateListing(new PrintStream(os));
        return os.toString().replaceAll("\\r\\n", "\n");
    }

    String compile(String text) throws Exception {
        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Spin2Compiler compiler = new Spin2Compiler();
        compiler.setDebugEnabled(true);
        Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompiler(compiler, new File("test.spin2"));
        Spin2Object obj = objectCompiler.compileObject(root);
        obj.setDebugData(compiler.generateDebugData());
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
