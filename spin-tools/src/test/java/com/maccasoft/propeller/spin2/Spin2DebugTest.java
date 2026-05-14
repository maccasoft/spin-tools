/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.SpinObject.DataObject;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.DataVariable;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin2.Spin2Debug.DebugDataObject;

class Spin2DebugTest {

    @Test
    void testDebugExpression() throws Exception {
        String text = """
            PUB main() | a
            
                debug(`index=`udec(long[a++]))
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       11 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     debug(`index=`udec(long[a++]))
            00009 00009       D0             VAR_SETUP LONG DBASE+$00000 (short)
            0000A 0000A       23             POST_INC (push)
            0000B 0000B       63 1C          MEM_READ LONG
            0000D 0000D       41 04 01       DEBUG #1
            00010 00010       04             RETURN
            00011 00011       00 00 00       Padding
            ' Debug data
            00B74 00000       0F 00        \s
            00B76 00002       04 00          #1@0004
            ' #1
            00B78 00004       06 60 69 6E 64 STRING (`index=)
            00B7D 00009       65 78 3D 00
            00B81 0000D       43             UDEC
            00B82 0000E       00             DONE
            """, compile(text));
    }

    @Test
    void testRepeatDebug() throws Exception {
        String text = """
            PUB main() | a
            
                repeat
                    debug(udec(a))
                    a := 1
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       12 00 00 00    End
            ' PUB main() | a
            00008 00008       01             (stack size)
            '     repeat
            '         debug(udec(a))
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       41 04 01       DEBUG #1
            '         a := 1
            0000D 0000D       A2             CONSTANT (1)
            0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0000F 0000F       12 79          JMP $00009 (-7)
            00011 00011       04             RETURN
            00012 00012       00 00          Padding
            ' Debug data
            00B74 00000       09 00        \s
            00B76 00002       04 00          #1@0004
            ' #1
            00B78 00004       04             COGN
            00B79 00005       41 61 00       UDEC(a)
            00B7C 00008       00             DONE
            """, compile(text));
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
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       41 72 65 67 61 UDEC(rega)
            00007 00007       00 80 01
            0000A 0000A       00             DONE
            """, actual);
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
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       41 23 72 65 67 UDEC(#rega)
            00007 00007       61 00 00 01
            0000B 0000B       00             DONE
            """, actual);
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
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       41 40 72 65 67 UDEC(@rega)
            00007 00007       61 00 80 04
            0000B 0000B       00             DONE
            """, actual);
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
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       41 23 40 72 65 UDEC(#@rega)
            00007 00007       67 61 00 00 04
            0000C 0000C       00             DONE
            """, actual);
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
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       51 72 65 67 61 UDEC_REG_ARRAY(rega)
            00007 00007       00 80 01 80 04
            0000C 0000C       00             DONE
            """, actual);
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
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       51 72 65 67 61 UDEC_REG_ARRAY(rega)
            00007 00007       00 80 01 00 04
            0000C 0000C       00             DONE
            """, actual);
    }

    @Test
    void testRegisterValueOnly() {
        Context context = new Context();
        context.addSymbol("reg", new NumberLiteral(10));

        String text = "debug(udec_(reg))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       43 80 0A       UDEC
            00005 00005       00             DONE
            """, actual);
    }

    @Test
    void testDly() {
        Context context = new Context();

        String text = "debug(dly(#100))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       07 00 64       DLY
            00005 00005       00             DONE
            """, actual);
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
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       41 72 65 67 31 UDEC(reg1)
            00007 00007       00 80 0A
            0000A 0000A       40 72 65 67 32 UDEC(reg2)
            0000F 0000F       00 80 0B
            00012 00012       40 72 65 67 33 UDEC(reg3)
            00017 00017       00 80 0C
            0001A 0001A       00             DONE
            """, actual);
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
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       41 72 65 67 31 UDEC(reg1)
            00007 00007       00 80 0A
            0000A 0000A       40 72 65 67 32 UDEC(reg2)
            0000F 0000F       00 80 0B
            00012 00012       40 72 65 67 33 UDEC(reg3)
            00017 00017       00 80 0C
            0001A 0001A       00             DONE
            """, actual);
    }

    @Test
    void testSpinString() {
        String text = "debug(\"start\")";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(new Context(), parse(text)));
        Assertions.assertEquals("""
            00000 00000       04             COGN
            00001 00001       06 73 74 61 72 STRING (start)
            00006 00006       74 00
            00008 00008       00             DONE
            """, actual);
    }

    @Test
    void testSpinStringConcatenate() {
        String text = "debug(\"start\", 13, 10, \"end\", 13, 10)";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(new Context(), parse(text)));
        Assertions.assertEquals("""
            00000 00000       04             COGN
            00001 00001       06 73 74 61 72 STRING (start..end..)
            00006 00006       74 0D 0A 65 6E
            0000B 0000B       64 0D 0A 00
            0000F 0000F       00             DONE
            """, actual);
    }

    @Test
    void testSpinStringAndVars() {
        String text = "debug(udec(reg1), \"start\", udec(reg2,reg3))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(new Context(), parse(text)));
        Assertions.assertEquals("""
            00000 00000       04             COGN
            00001 00001       41 72 65 67 31 UDEC(reg1)
            00006 00006       00
            00007 00007       06 73 74 61 72 STRING (start)
            0000C 0000C       74 00
            0000E 0000E       41 72 65 67 32 UDEC(reg2)
            00013 00013       00
            00014 00014       40 72 65 67 33 UDEC(reg3)
            00019 00019       00
            0001A 0001A       00             DONE
            """, actual);
    }

    @Test
    void testPAsmString() {
        Context context = new Context();

        String text = "debug(\"start\")";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       06 73 74 61 72 STRING (start)
            00007 00007       74 00
            00009 00009       00             DONE
            """, actual);
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
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       41 72 65 67 31 UDEC(reg1)
            00007 00007       00 80 0A
            0000A 0000A       06 73 74 61 72 STRING (start)
            0000F 0000F       74 00
            00011 00011       41 72 65 67 32 UDEC(reg2)
            00016 00016       00 80 0B
            00019 00019       40 72 65 67 33 UDEC(reg3)
            0001E 0001E       00 80 0C
            00021 00021       00             DONE
            """, actual);
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
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       25 72 65 67 61 ZSTR(rega)
            00007 00007       00 80 01
            0000A 0000A       00             DONE
            """, actual);
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
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       25 40 72 65 67 ZSTR(@rega)
            00007 00007       61 00 80 04
            0000B 0000B       00             DONE
            """, actual);
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
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       25 23 72 65 67 ZSTR(#rega)
            00007 00007       61 00 00 01
            0000B 0000B       00             DONE
            """, actual);
    }

    @Test
    void testSpinLSTR() {
        String text = "debug(lstr(ptr,#12))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(new Context(), parse(text)));
        Assertions.assertEquals("""
            00000 00000       04             COGN
            00001 00001       35 70 74 72 00 LSTR(ptr)
            00006 00006       00             DONE
            """, actual);
    }

    @Test
    void testPAsmLSTR() {
        Context context = new Context();
        context.addSymbol("ptr", new NumberLiteral(10));

        String text = "debug(lstr(ptr,#12))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       35 70 74 72 00 LSTR(ptr)
            00007 00007       80 0A 00 0C
            0000B 0000B       00             DONE
            """, actual);
    }

    @Test
    void testBacktick() {
        Context context = new Context();

        String text = "debug(`12345)";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       06 60 31 32 33 STRING (`12345)
            00006 00006       34 35 00
            00009 00009       00             DONE
            """, actual);
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
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       06 60 31 32 33 STRING (`12345 )
            00006 00006       34 35 20 00
            0000A 0000A       63 80 01       SDEC
            0000D 0000D       62 80 02       SDEC
            00010 00010       00             DONE
            """, actual);
    }

    @Test
    void testStringBacktick() {
        Context context = new Context();

        String text = "debug(\"`12345\")";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       06 60 31 32 33 STRING (`12345)
            00006 00006       34 35 00
            00009 00009       00             DONE
            """, actual);
    }

    @Test
    void testStringBacktickCommand() {
        Context context = new Context();
        context.addSymbol("a", new NumberLiteral(1));

        String text = "debug(`12345`(a))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       06 60 31 32 33 STRING (`12345)
            00006 00006       34 35 00
            00009 00009       63 80 01       SDEC
            0000C 0000C       00             DONE
            """, actual);
    }

    @Test
    void testSpin() {
        String text = "debug(udec(reg))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(new Context(), parse(text)));
        Assertions.assertEquals("""
            00000 00000       04             COGN
            00001 00001       41 72 65 67 00 UDEC(reg)
            00006 00006       00             DONE
            """, actual);
    }

    @Test
    void testSpinBacktickCommand() {
        String text = "debug(`12345 `uhex_(a,b))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(new Context(), parse(text)));
        Assertions.assertEquals("""
            00000 00000       06 60 31 32 33 STRING (`12345 )
            00005 00005       34 35 20 00
            00009 00009       83             UHEX
            0000A 0000A       82             UHEX
            0000B 0000B       00             DONE
            """, actual);
    }

    @Test
    void testSpinBacktickShortCommand() {
        String text = "debug(`12345 `(a,b))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(new Context(), parse(text)));
        Assertions.assertEquals("""
            00000 00000       06 60 31 32 33 STRING (`12345 )
            00005 00005       34 35 20 00
            00009 00009       63             SDEC
            0000A 0000A       62             SDEC
            0000B 0000B       00             DONE
            """, actual);
    }

    @Test
    void testSpinBacktickCommandConcatenate() {
        String text = "debug(`a = `(a) b = `(b))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(new Context(), parse(text)));
        Assertions.assertEquals("""
            00000 00000       06 60 61 20 3D STRING (`a = )
            00005 00005       20 00
            00007 00007       63             SDEC
            00008 00008       06 20 62 20 3D STRING ( b = )
            0000D 0000D       20 00
            0000F 0000F       63             SDEC
            00010 00010       00             DONE
            """, actual);
    }

    @Test
    void testSpinArray() {
        String text = "debug(udec_long_array(reg,2))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(new Context(), parse(text)));
        Assertions.assertEquals("""
            00000 00000       04             COGN
            00001 00001       5D 72 65 67 00 UDEC_LONG_ARRAY(reg)
            00006 00006       00             DONE
            """, actual);
    }

    @Test
    void testSpinCondition() {
        String text = "debug(if(a > 1), udec(reg))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(new Context(), parse(text)));
        Assertions.assertEquals("""
            00000 00000       02             IF
            00001 00001       04             COGN
            00002 00002       41 72 65 67 00 UDEC(reg)
            00007 00007       00             DONE
            """, actual);
    }

    @Test
    void testSpinConditionMiddle() {
        String text = "debug(udec(reg), if(a > 1), udec(a))";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(new Context(), parse(text)));
        Assertions.assertEquals("""
            00000 00000       04             COGN
            00001 00001       41 72 65 67 00 UDEC(reg)
            00006 00006       02             IF
            00007 00007       40 61 00       UDEC(a)
            0000A 0000A       00             DONE
            """, actual);
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
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       02 80 01       IF
            00004 00004       04             COGN
            00005 00005       41 72 65 67 00 UDEC(reg)
            0000A 0000A       80 0A
            0000C 0000C       00             DONE
            """, actual);
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
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       41 72 65 67 00 UDEC(reg)
            00007 00007       80 0A
            00009 00009       02 80 01       IF
            0000C 0000C       40 61 00 80 01 UDEC(a)
            00011 00011       00             DONE
            """, actual);
    }

    @Test
    void testChar() {
        String text = "debug(``#(letter) lutcolors)";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(new Context(), parse(text)));
        Assertions.assertEquals("""
            00000 00000       06 60 00       STRING (`)
            00003 00003       05             CHAR
            00004 00004       06 20 6C 75 74 STRING ( lutcolors)
            00009 00009       63 6F 6C 6F 72
            0000E 0000E       73 00
            00010 00010       00             DONE
            """, actual);
    }

    @Test
    void testPAsmChar() {
        Context context = new Context();
        context.addSymbol("letter", new NumberLiteral(1));

        String text = "debug(``#(letter) lutcolors)";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       06 60 00       STRING (`)
            00004 00004       05 80 01       CHAR
            00007 00007       06 20 6C 75 74 STRING ( lutcolors)
            0000C 0000C       63 6F 6C 6F 72
            00011 00011       73 00
            00013 00013       00             DONE
            """, actual);
    }

    @Test
    void testEmptyDebug() throws Exception {
        String text = """
            PUB main()
            
                debug()
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0D 00 00 00    End
            ' PUB main()
            00008 00008       00             (stack size)
            '     debug()
            00009 00009       41 00 01       DEBUG #1
            0000C 0000C       04             RETURN
            0000D 0000D       00 00 00       Padding
            ' Debug data
            00B74 00000       05 00        \s
            00B76 00002       04 00          #1@0004
            ' #1
            00B78 00004       00             DONE
            """, compile(text));
    }

    @Test
    void testDebugWithoutParenthesis() throws Exception {
        String text = """
            PUB main()
            
                debug
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       0D 00 00 00    End
            ' PUB main()
            00008 00008       00             (stack size)
            '     debug
            00009 00009       41 00 00       DEBUG #0
            0000C 0000C       04             RETURN
            0000D 0000D       00 00 00       Padding
            ' Debug data
            00B74 00000       02 00        \s
            """, compile(text));
    }

    @Test
    void testEmptyPAsmDebug() throws Exception {
        String text = """
            PUB main()
            
            DAT
            
                debug()
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       0E 00 00 00    End
            00008 00008 00000 36 02 64 FD                        debug()
            ' PUB main()
            0000C 0000C       00             (stack size)
            0000D 0000D       04             RETURN
            0000E 0000E       00 00          Padding
            ' Debug data
            00B74 00000       05 00        \s
            00B76 00002       04 00          #1@0004
            ' #1
            00B78 00004       00             DONE
            """, compile(text));
    }

    @Test
    void testPAsmDebugWithoutParenthesis() throws Exception {
        String text = """
            PUB main()
            
            DAT
            
                debug
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            00004 00004       0E 00 00 00    End
            00008 00008 00000 36 00 64 FD                        debug
            ' PUB main()
            0000C 0000C       00             (stack size)
            0000D 0000D       04             RETURN
            0000E 0000E       00 00          Padding
            ' Debug data
            00B74 00000       02 00        \s
            """, compile(text));
    }

    @Test
    void testSpinBool() {
        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(new Context(), parse("debug(bool(a,b))")));
        Assertions.assertEquals("""
            00000 00000       04             COGN
            00001 00001       21 61 00       BOOL(a)
            00004 00004       20 62 00       BOOL(b)
            00007 00007       00             DONE
            """, actual);
    }

    @Test
    void testSpinBoolBacktick() {
        Spin2Debug subject = new Spin2Debug();

        String actual = dumpDebugData(subject.compileDebugStatement(new Context(), parse("debug(``?(a,b))")));
        Assertions.assertEquals("""
            00000 00000       06 60 00       STRING (`)
            00003 00003       23             BOOL
            00004 00004       22             BOOL
            00005 00005       00             DONE
            """, actual);
    }

    @Test
    void testSpinC_Z() {
        String text = "debug(c_z, udec(a), c_z)";

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compileDebugStatement(new Context(), parse(text)));
        Assertions.assertEquals("""
            00000 00000       04             COGN
            00001 00001       0B             C_Z
            00002 00002       40 61 00       UDEC(a)
            00005 00005       0A             C_Z
            00006 00006       00             DONE
            """, actual);
    }

    @Test
    void testPAsmBool() {
        Context context = new Context();
        context.addSymbol("a", new NumberLiteral(1));
        context.addSymbol("b", new NumberLiteral(2));

        Spin2Debug subject = new Spin2Debug();

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens("debug(bool(a,b))"));
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       21 61 00 80 01 BOOL(a)
            00007 00007       20 62 00 80 02 BOOL(b)
            0000C 0000C       00             DONE
            """, actual);
    }

    @Test
    void testPAsmBoolBacktick() {
        Context context = new Context();
        context.addSymbol("a", new NumberLiteral(1));
        context.addSymbol("b", new NumberLiteral(2));

        Spin2Debug subject = new Spin2Debug();

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens("debug(``?(a,b))"));
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       06 60 00       STRING (`)
            00004 00004       23 80 01       BOOL
            00007 00007       22 80 02       BOOL
            0000A 0000A       00             DONE
            """, actual);
    }

    @Test
    void testPAsmC_Z() {
        Context context = new Context();
        context.addSymbol("a", new NumberLiteral(1));

        String text = "debug(c_z, udec(a), c_z)";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       0B             C_Z
            00003 00003       40 61 00 80 01 UDEC(a)
            00008 00008       0A             C_Z
            00009 00009       00             DONE
            """, actual);
    }

    @Test
    void testVariables() throws Exception {
        String text = """
            PUB main() | a, b, c
            
                debug(a, b, udec(a), c)
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       11 00 00 00    End
            ' PUB main() | a, b, c
            00008 00008       03             (stack size)
            '     debug(a, b, udec(a), c)
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            0000B 0000B       E0             VAR_READ LONG DBASE+$00000 (short)
            0000C 0000C       E2             VAR_READ LONG DBASE+$00002 (short)
            0000D 0000D       41 10 01       DEBUG #1
            00010 00010       04             RETURN
            00011 00011       00 00 00       Padding
            ' Debug data
            00B74 00000       0C 00        \s
            00B76 00002       04 00          #1@0004
            ' #1
            00B78 00004       04             COGN
            00B79 00005       05             CHAR
            00B7A 00006       05             CHAR
            00B7B 00007       41 61 00       UDEC(a)
            00B7E 0000A       05             CHAR
            00B7F 0000B       00             DONE
            """, compile(text));
    }

    @Test
    void testBacktickStringConcatenation() throws Exception {
        String text = """
            PUB main() | a, b, c
            
                debug(`test a=`(a), b=`(b), c=`(c))
                debug(`test `(a)+`(b)*`(c))
            
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       16 00 00 00    End
            ' PUB main() | a, b, c
            00008 00008       03             (stack size)
            '     debug(`test a=`(a), b=`(b), c=`(c))
            00009 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            0000A 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            0000B 0000B       E2             VAR_READ LONG DBASE+$00002 (short)
            0000C 0000C       41 0C 01       DEBUG #1
            '     debug(`test `(a)+`(b)*`(c))
            0000F 0000F       E0             VAR_READ LONG DBASE+$00000 (short)
            00010 00010       E1             VAR_READ LONG DBASE+$00001 (short)
            00011 00011       E2             VAR_READ LONG DBASE+$00002 (short)
            00012 00012       41 0C 02       DEBUG #2
            00015 00015       04             RETURN
            00016 00016       00 00          Padding
            ' Debug data
            00B74 00000       32 00        \s
            00B76 00002       06 00          #1@0006
            00B78 00004       20 00          #2@0020
            ' #1
            00B7A 00006       06 60 74 65 73 STRING (`test a=)
            00B7F 0000B       74 20 61 3D 00
            00B84 00010       63             SDEC
            00B85 00011       06 2C 20 62 3D STRING (, b=)
            00B8A 00016       00
            00B8B 00017       63             SDEC
            00B8C 00018       06 2C 20 63 3D STRING (, c=)
            00B91 0001D       00
            00B92 0001E       63             SDEC
            00B93 0001F       00             DONE
            ' #2
            00B94 00020       06 60 74 65 73 STRING (`test )
            00B99 00025       74 20 00
            00B9C 00028       63             SDEC
            00B9D 00029       06 2B 00       STRING (+)
            00BA0 0002C       63             SDEC
            00BA1 0002D       06 2A 00       STRING (*)
            00BA4 00030       63             SDEC
            00BA5 00031       00             DONE
            """, compile(text));
    }

    @Test
    void testPAsmBacktickStringConcatenation() throws Exception {
        Context context = new Context();
        context.addSymbol("a", new NumberLiteral(1));
        context.addSymbol("b", new NumberLiteral(2));
        context.addSymbol("c", new NumberLiteral(3));

        String text = "debug(`test a=`(a), b=`(b), c=`(c))";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       06 60 74 65 73 STRING (`test a=)
            00006 00006       74 20 61 3D 00
            0000B 0000B       63 80 01       SDEC
            0000E 0000E       06 2C 20 62 3D STRING (, b=)
            00013 00013       00
            00014 00014       63 80 02       SDEC
            00017 00017       06 2C 20 63 3D STRING (, c=)
            0001C 0001C       00
            0001D 0001D       63 80 03       SDEC
            00020 00020       00             DONE
            """, actual);
    }

    @Test
    void testPAsmVariables() {
        Context context = new Context();

        Context context1 = new Context(context);
        context1.setAddress(1);

        Context context2 = new Context(context);
        context2.setAddress(2);

        Context context3 = new Context(context);
        context3.setAddress(3);

        context.addSymbol("a", new DataVariable(context1, "LONG"));
        context.addSymbol("b", new DataVariable(context2, "LONG"));
        context.addSymbol("c", new DataVariable(context3, "LONG"));

        String text = "debug(a, b, udec(a), c)";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       05 80 01       CHAR
            00005 00005       05 80 02       CHAR
            00008 00008       41 61 00 80 01 UDEC(a)
            0000D 0000D       05 80 03       CHAR
            00010 00010       00             DONE
            """, actual);
    }

    @Test
    void testPAsmConstants() {
        Context context = new Context();
        context.addSymbol("a", new NumberLiteral(1));
        context.addSymbol("b", new NumberLiteral(2));
        context.addSymbol("c", new NumberLiteral(3));

        String text = "debug(a, b, udec(a), c)";

        Spin2PAsmDebugLine root = Spin2PAsmDebugLine.buildFrom(context, parseTokens(text));

        Spin2Debug subject = new Spin2Debug();
        String actual = dumpDebugData(subject.compilePAsmDebugStatement(root));
        Assertions.assertEquals("""
            00000 00000       01             ASMMODE
            00001 00001       04             COGN
            00002 00002       06 01 02 00    STRING (..)
            00006 00006       41 61 00 80 01 UDEC(a)
            0000B 0000B       06 03 00       STRING (.)
            0000E 0000E       00             DONE
            """, actual);
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

    String dumpDebugData(DebugDataObject data) {
        Spin2Object object = new Spin2Object();
        for (DataObject d : data.getDataObjects()) {
            object.write(d);
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        object.generateListing(new PrintStream(os));
        return os.toString().replaceAll("\\r\\n", "\n");
    }

    String compile(String text) throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();
        compiler.setDebugEnabled(true);
        Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompiler(compiler, new File("test.spin2"));
        Spin2Object obj = objectCompiler.compileObject(text);
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
