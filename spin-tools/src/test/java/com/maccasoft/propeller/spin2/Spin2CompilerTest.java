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
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.model.SourceProvider;

class Spin2CompilerTest {

    @Test
    void testObjectLink() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o : "text2"
            
            PUB main() | a
            
                a := 1
            
            """);
        sources.put("text2.spin2", """
            PUB start(a, b) | c
            
                c := a + b
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 8)
            01888 00000       14 00 00 00    Object "text2.spin2" @ $00014
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            01894 0000C       14 00 00 00    End
            ' PUB main() | a
            01898 00010       01             (stack size)
            '     a := 1
            01899 00011       A2             CONSTANT (1)
            0189A 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0189B 00013       04             RETURN
            ' Object "text2.spin2" header (var size 4)
            0189C 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)
            018A0 00004       0E 00 00 00    End
            ' PUB start(a, b) | c
            018A4 00008       01             (stack size)
            '     c := a + b
            018A5 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018A6 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            018A7 0000B       8A             ADD
            018A8 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)
            018A9 0000D       04             RETURN
            018AA 0000E       00 00          Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testObjectConstant() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o : "text2"
            
            PUB main() | a
            
                a := o.CONSTANT
            
            """);
        sources.put("text2.spin2", """
            CON
            
                CONSTANT = 1
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 8)
            01888 00000       14 00 00 00    Object "text2.spin2" @ $00014
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            01894 0000C       14 00 00 00    End
            ' PUB main() | a
            01898 00010       01             (stack size)
            '     a := o.CONSTANT
            01899 00011       A2             CONSTANT (1)
            0189A 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0189B 00013       04             RETURN
            ' Object "text2.spin2" header (var size 4)
            """, compile("main.spin2", sources));
    }

    @Test
    void testObjectVariablesLink() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            VAR
            
                long b
            
            OBJ
            
                o : "text1"
                p : "text2"
            
            PUB main() | a
            
                a := 1
            
            """);
        sources.put("text1.spin2", """
            VAR
            
                long d, e
            
            PUB start(a, b) | c
            
                c := a + b
            
            """);
        sources.put("text2.spin2", """
            VAR
            
                long d, e, f, g
            
            PUB start(a, b) | c
            
                c := a + b
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 40)
            01888 00000       1C 00 00 00    Object "text1.spin2" @ $0001C
            0188C 00004       08 00 00 00    Variables @ $00008
            01890 00008       2C 00 00 00    Object "text2.spin2" @ $0002C
            01894 0000C       14 00 00 00    Variables @ $00014
            01898 00010       18 00 00 80    Method main @ $00018 (0 parameters, 0 returns)
            0189C 00014       1C 00 00 00    End
            ' PUB main() | a
            018A0 00018       01             (stack size)
            '     a := 1
            018A1 00019       A2             CONSTANT (1)
            018A2 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            018A3 0001B       04             RETURN
            ' Object "text1.spin2" header (var size 12)
            018A4 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)
            018A8 00004       0E 00 00 00    End
            ' PUB start(a, b) | c
            018AC 00008       01             (stack size)
            '     c := a + b
            018AD 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018AE 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            018AF 0000B       8A             ADD
            018B0 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)
            018B1 0000D       04             RETURN
            018B2 0000E       00 00          Padding
            ' Object "text2.spin2" header (var size 20)
            018B4 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)
            018B8 00004       0E 00 00 00    End
            ' PUB start(a, b) | c
            018BC 00008       01             (stack size)
            '     c := a + b
            018BD 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018BE 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            018BF 0000B       8A             ADD
            018C0 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)
            018C1 0000D       04             RETURN
            018C2 0000E       00 00          Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testObjectMethod() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o : "text2"
            
            PUB main() | a
            
                o.start(1, 2)
                \\o.start(3, 4)
            
            """);
        sources.put("text2.spin2", """
            PUB start(a, b) | c
            
                c := a + b
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 8)
            01888 00000       20 00 00 00    Object "text2.spin2" @ $00020
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            01894 0000C       1E 00 00 00    End
            ' PUB main() | a
            01898 00010       01             (stack size)
            '     o.start(1, 2)
            01899 00011       00             ANCHOR
            0189A 00012       A2             CONSTANT (1)
            0189B 00013       A3             CONSTANT (2)
            0189C 00014       08 00 00       CALL_OBJ_SUB (0.0)
            '     \\o.start(3, 4)
            0189F 00017       02             ANCHOR_TRAP
            018A0 00018       A4             CONSTANT (3)
            018A1 00019       A5             CONSTANT (4)
            018A2 0001A       08 00 00       CALL_OBJ_SUB (0.0)
            018A5 0001D       04             RETURN
            018A6 0001E       00 00          Padding
            ' Object "text2.spin2" header (var size 4)
            018A8 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)
            018AC 00004       0E 00 00 00    End
            ' PUB start(a, b) | c
            018B0 00008       01             (stack size)
            '     c := a + b
            018B1 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018B2 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            018B3 0000B       8A             ADD
            018B4 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)
            018B5 0000D       04             RETURN
            018B6 0000E       00 00          Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testObjectMethodDefaultArgument() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o : "text2"
            
            PUB main() | a
            
                o.start(1)
            
            """);
        sources.put("text2.spin2", """
            PUB start(a, b = 2) | c
            
                c := a + b
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 8)
            01888 00000       18 00 00 00    Object "text2.spin2" @ $00018
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            01894 0000C       18 00 00 00    End
            ' PUB main() | a
            01898 00010       01             (stack size)
            '     o.start(1)
            01899 00011       00             ANCHOR
            0189A 00012       A2             CONSTANT (1)
            0189B 00013       A3             CONSTANT (2)
            0189C 00014       08 00 00       CALL_OBJ_SUB (0.0)
            0189F 00017       04             RETURN
            ' Object "text2.spin2" header (var size 4)
            018A0 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)
            018A4 00004       0E 00 00 00    End
            ' PUB start(a, b = 2) | c
            018A8 00008       01             (stack size)
            '     c := a + b
            018A9 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018AA 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            018AB 0000B       8A             ADD
            018AC 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)
            018AD 0000D       04             RETURN
            018AE 0000E       00 00          Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testObjectMethodDefaultArgumentOverride() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o : "text2"
            
            PUB main() | a
            
                o.start(1, 3)
            
            """);
        sources.put("text2.spin2", """
            PUB start(a, b = 2) | c
            
                c := a + b
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 8)
            01888 00000       18 00 00 00    Object "text2.spin2" @ $00018
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            01894 0000C       18 00 00 00    End
            ' PUB main() | a
            01898 00010       01             (stack size)
            '     o.start(1, 3)
            01899 00011       00             ANCHOR
            0189A 00012       A2             CONSTANT (1)
            0189B 00013       A4             CONSTANT (3)
            0189C 00014       08 00 00       CALL_OBJ_SUB (0.0)
            0189F 00017       04             RETURN
            ' Object "text2.spin2" header (var size 4)
            018A0 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)
            018A4 00004       0E 00 00 00    End
            ' PUB start(a, b = 2) | c
            018A8 00008       01             (stack size)
            '     c := a + b
            018A9 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018AA 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            018AB 0000B       8A             ADD
            018AC 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)
            018AD 0000D       04             RETURN
            018AE 0000E       00 00          Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testObjectMethodReturnListAsArguments() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o : "text2"
            
            PUB main() | a
            
                function1(a, o.function2())
            
            PUB function1(p1, p2 , p3)
            
            """);
        sources.put("text2.spin2", """
            PUB function2() : r1, r2
            
                r1 := 1
                r2 := 2
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 8)
            01888 00000       20 00 00 00    Object "text2.spin2" @ $00020
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       14 00 00 80    Method main @ $00014 (0 parameters, 0 returns)
            01894 0000C       1E 00 00 83    Method function1 @ $0001E (3 parameters, 0 returns)
            01898 00010       20 00 00 00    End
            ' PUB main() | a
            0189C 00014       01             (stack size)
            '     function1(a, o.function2())
            0189D 00015       00             ANCHOR
            0189E 00016       E0             VAR_READ LONG DBASE+$00000 (short)
            0189F 00017       01             ANCHOR (push)
            018A0 00018       08 00 00       CALL_OBJ_SUB (0.0)
            018A3 0001B       0A 03          CALL_SUB (3)
            018A5 0001D       04             RETURN
            ' PUB function1(p1, p2 , p3)
            018A6 0001E       00             (stack size)
            018A7 0001F       04             RETURN
            ' Object "text2.spin2" header (var size 4)
            018A8 00000       08 00 20 80    Method function2 @ $00008 (0 parameters, 2 returns)
            018AC 00004       0E 00 00 00    End
            ' PUB function2() : r1, r2
            018B0 00008       00             (stack size)
            '     r1 := 1
            018B1 00009       A2             CONSTANT (1)
            018B2 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     r2 := 2
            018B3 0000B       A3             CONSTANT (2)
            018B4 0000C       F1             VAR_WRITE LONG DBASE+$00001 (short)
            018B5 0000D       04             RETURN
            018B6 0000E       00 00          Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testObjectMethodReturnListAsArguments2() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o : "text2"
            
            PUB main() | a
            
                o.function1(a, o.function2())
            
            """);
        sources.put("text2.spin2", """
            PUB function1(p1, p2 , p3)
            
            PUB function2() : r1, r2
            
                r1 := 1
                r2 := 2
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 8)
            01888 00000       1C 00 00 00    Object "text2.spin2" @ $0001C
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            01894 0000C       1B 00 00 00    End
            ' PUB main() | a
            01898 00010       01             (stack size)
            '     o.function1(a, o.function2())
            01899 00011       00             ANCHOR
            0189A 00012       E0             VAR_READ LONG DBASE+$00000 (short)
            0189B 00013       01             ANCHOR (push)
            0189C 00014       08 00 01       CALL_OBJ_SUB (0.1)
            0189F 00017       08 00 00       CALL_OBJ_SUB (0.0)
            018A2 0001A       04             RETURN
            018A3 0001B       00             Padding
            ' Object "text2.spin2" header (var size 4)
            018A4 00000       0C 00 00 83    Method function1 @ $0000C (3 parameters, 0 returns)
            018A8 00004       0E 00 20 80    Method function2 @ $0000E (0 parameters, 2 returns)
            018AC 00008       14 00 00 00    End
            ' PUB function1(p1, p2 , p3)
            018B0 0000C       00             (stack size)
            018B1 0000D       04             RETURN
            ' PUB function2() : r1, r2
            018B2 0000E       00             (stack size)
            '     r1 := 1
            018B3 0000F       A2             CONSTANT (1)
            018B4 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     r2 := 2
            018B5 00011       A3             CONSTANT (2)
            018B6 00012       F1             VAR_WRITE LONG DBASE+$00001 (short)
            018B7 00013       04             RETURN
            """, compile("main.spin2", sources));
    }

    @Test
    void testObjectMethodPointer() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o : "text2"
            
            PUB main() | a
            
                a := @o.start
            
            """);
        sources.put("text2.spin2", """
            PUB start(a, b) | c
            
                c := a + b
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 8)
            01888 00000       18 00 00 00    Object "text2.spin2" @ $00018
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            01894 0000C       16 00 00 00    End
            ' PUB main() | a
            01898 00010       01             (stack size)
            '     a := @o.start
            01899 00011       0F 00 00       OBJ_SUB_ADDRESS (0.0)
            0189C 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0189D 00015       04             RETURN
            0189E 00016       00 00          Padding
            ' Object "text2.spin2" header (var size 4)
            018A0 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)
            018A4 00004       0E 00 00 00    End
            ' PUB start(a, b) | c
            018A8 00008       01             (stack size)
            '     c := a + b
            018A9 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018AA 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            018AB 0000B       8A             ADD
            018AC 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)
            018AD 0000D       04             RETURN
            018AE 0000E       00 00          Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testObjectMethodPointerAsArgument() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o[2] : "text2"
            
            PUB main()
            
                o[0].set(@method)
                o[1].set(@method)
            
            PUB method(x, y)
            
            """);
        sources.put("text2.spin2", """
            PUB set(ptr) | a
            
                a := ptr
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 12)
            01888 00000       30 00 00 00    Object "text2.spin2" @ $00030
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       30 00 00 00    Object "text2.spin2" @ $00030
            01894 0000C       08 00 00 00    Variables @ $00008
            01898 00010       1C 00 00 80    Method main @ $0001C (0 parameters, 0 returns)
            0189C 00014       2C 00 00 82    Method method @ $0002C (2 parameters, 0 returns)
            018A0 00018       2E 00 00 00    End
            ' PUB main()
            018A4 0001C       00             (stack size)
            '     o[0].set(@method)
            018A5 0001D       00             ANCHOR
            018A6 0001E       11 05          SUB_ADDRESS (5)
            018A8 00020       A1             CONSTANT (0)
            018A9 00021       09 00 00       CALL_OBJ_SUB (0.0) (indexed)
            '     o[1].set(@method)
            018AC 00024       00             ANCHOR
            018AD 00025       11 05          SUB_ADDRESS (5)
            018AF 00027       A2             CONSTANT (1)
            018B0 00028       09 00 00       CALL_OBJ_SUB (0.0) (indexed)
            018B3 0002B       04             RETURN
            ' PUB method(x, y)
            018B4 0002C       00             (stack size)
            018B5 0002D       04             RETURN
            018B6 0002E       00 00          Padding
            ' Object "text2.spin2" header (var size 4)
            018B8 00000       08 00 00 81    Method set @ $00008 (1 parameters, 0 returns)
            018BC 00004       0C 00 00 00    End
            ' PUB set(ptr) | a
            018C0 00008       01             (stack size)
            '     a := ptr
            018C1 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018C2 0000A       F1             VAR_WRITE LONG DBASE+$00001 (short)
            018C3 0000B       04             RETURN
            """, compile("main.spin2", sources));
    }

    @Test
    void testNestedObjectsLink() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            VAR
            
                long a
                byte b
                word c
                long d
            
            OBJ
            
                t1 : "text1"
                t2 : "text2"
            
            PUB main()
            
                a := 1
                b := 2
                c := 3
                d := 4
            """);
        sources.put("text1.spin2", """
            OBJ
            
                t3 : "text3"
            
            VAR
            
                long a1
            
            PUB main()
            
                a1 := 5
            """);
        sources.put("text2.spin2", """
            VAR
            
                word a2
                word b2
            
            PUB main()
            
                a2 := 6
                b2 := 7
            """);
        sources.put("text3.spin2", """
            VAR
            
                byte a3
                long b3
            
            PUB main()
            
                a3 := 8
                b3 := 9
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 44)
            01888 00000       2C 00 00 00    Object "text1.spin2" @ $0002C
            0188C 00004       10 00 00 00    Variables @ $00010
            01890 00008       58 00 00 00    Object "text2.spin2" @ $00058
            01894 0000C       24 00 00 00    Variables @ $00024
            01898 00010       18 00 00 80    Method main @ $00018 (0 parameters, 0 returns)
            0189C 00014       29 00 00 00    End
            ' PUB main()
            018A0 00018       00             (stack size)
            '     a := 1
            018A1 00019       A2             CONSTANT (1)
            018A2 0001A       C1 1D          VAR_WRITE LONG VBASE+$00001 (short)
            '     b := 2
            018A4 0001C       A3             CONSTANT (2)
            018A5 0001D       50 08 1D       VAR_WRITE BYTE VBASE+$00008
            '     c := 3
            018A8 00020       A4             CONSTANT (3)
            018A9 00021       56 09 1D       VAR_WRITE WORD VBASE+$00009
            '     d := 4
            018AC 00024       A5             CONSTANT (4)
            018AD 00025       5C 0B 1D       VAR_WRITE LONG VBASE+$0000B
            018B0 00028       04             RETURN
            018B1 00029       00 00 00       Padding
            ' Object "text1.spin2" header (var size 20)
            018B4 00000       18 00 00 00    Object "text3.spin2" @ $00018
            018B8 00004       08 00 00 00    Variables @ $00008
            018BC 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            018C0 0000C       15 00 00 00    End
            ' PUB main()
            018C4 00010       00             (stack size)
            '     a1 := 5
            018C5 00011       A6             CONSTANT (5)
            018C6 00012       C1 1D          VAR_WRITE LONG VBASE+$00001 (short)
            018C8 00014       04             RETURN
            018C9 00015       00 00 00       Padding
            ' Object "text3.spin2" header (var size 12)
            018CC 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            018D0 00004       12 00 00 00    End
            ' PUB main()
            018D4 00008       00             (stack size)
            '     a3 := 8
            018D5 00009       A9             CONSTANT (8)
            018D6 0000A       50 04 1D       VAR_WRITE BYTE VBASE+$00004
            '     b3 := 9
            018D9 0000D       AA             CONSTANT (9)
            018DA 0000E       5C 05 1D       VAR_WRITE LONG VBASE+$00005
            018DD 00011       04             RETURN
            018DE 00012       00 00          Padding
            ' Object "text2.spin2" header (var size 8)
            018E0 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            018E4 00004       12 00 00 00    End
            ' PUB main()
            018E8 00008       00             (stack size)
            '     a2 := 6
            018E9 00009       A7             CONSTANT (6)
            018EA 0000A       56 04 1D       VAR_WRITE WORD VBASE+$00004
            '     b2 := 7
            018ED 0000D       A8             CONSTANT (7)
            018EE 0000E       56 06 1D       VAR_WRITE WORD VBASE+$00006
            018F1 00011       04             RETURN
            018F2 00012       00 00          Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testDuplicatedObjectLink() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            VAR
            
                long b
            
            OBJ
            
                o1 : "object1"
                o2 : "object1"
                o3 : "object2"
            
            PUB main() | a
            
                o1.function(1)
                o2.function(2)
                o3.function(3)
            """);
        sources.put("object1.spin2", """
            VAR
            
                long d
                long e
            
            PUB function(a) : r
            
                return a + d * e
            """);
        sources.put("object2.spin2", """
            VAR
            
                long d
            
            PUB function(a) : r | b, c
            
                return (a + b * c) * d
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 40)
            01888 00000       34 00 00 00    Object "object1.spin2" @ $00034
            0188C 00004       08 00 00 00    Variables @ $00008
            01890 00008       34 00 00 00    Object "object1.spin2" @ $00034
            01894 0000C       14 00 00 00    Variables @ $00014
            01898 00010       48 00 00 00    Object "object2.spin2" @ $00048
            0189C 00014       20 00 00 00    Variables @ $00020
            018A0 00018       20 00 00 80    Method main @ $00020 (0 parameters, 0 returns)
            018A4 0001C       31 00 00 00    End
            ' PUB main() | a
            018A8 00020       01             (stack size)
            '     o1.function(1)
            018A9 00021       00             ANCHOR
            018AA 00022       A2             CONSTANT (1)
            018AB 00023       08 00 00       CALL_OBJ_SUB (0.0)
            '     o2.function(2)
            018AE 00026       00             ANCHOR
            018AF 00027       A3             CONSTANT (2)
            018B0 00028       08 01 00       CALL_OBJ_SUB (1.0)
            '     o3.function(3)
            018B3 0002B       00             ANCHOR
            018B4 0002C       A4             CONSTANT (3)
            018B5 0002D       08 02 00       CALL_OBJ_SUB (2.0)
            018B8 00030       04             RETURN
            018B9 00031       00 00 00       Padding
            ' Object "object1.spin2" header (var size 12)
            018BC 00000       08 00 10 81    Method function @ $00008 (1 parameters, 1 returns)
            018C0 00004       12 00 00 00    End
            ' PUB function(a) : r
            018C4 00008       00             (stack size)
            '     return a + d * e
            018C5 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018C6 0000A       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            018C8 0000C       C2 1C          VAR_READ LONG VBASE+$00002 (short)
            018CA 0000E       96             MULTIPLY
            018CB 0000F       8A             ADD
            018CC 00010       05             RETURN
            018CD 00011       04             RETURN
            018CE 00012       00 00          Padding
            ' Object "object2.spin2" header (var size 8)
            018D0 00000       08 00 10 81    Method function @ $00008 (1 parameters, 1 returns)
            018D4 00004       13 00 00 00    End
            ' PUB function(a) : r | b, c
            018D8 00008       02             (stack size)
            '     return (a + b * c) * d
            018D9 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018DA 0000A       E2             VAR_READ LONG DBASE+$00002 (short)
            018DB 0000B       E3             VAR_READ LONG DBASE+$00003 (short)
            018DC 0000C       96             MULTIPLY
            018DD 0000D       8A             ADD
            018DE 0000E       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            018E0 00010       96             MULTIPLY
            018E1 00011       05             RETURN
            018E2 00012       04             RETURN
            018E3 00013       00             Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testNestedDuplicatedObjectLink() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            VAR
            
                long b
            
            OBJ
            
                o1 : "object1"
                o2 : "object1"
                o3 : "object2"
            
            PUB main() | a
            
                o1.function(1)
                o2.function(2)
                o3.function(3)
            """);
        sources.put("object1.spin2", """
            VAR
            
                long d
                long e
            
            OBJ
            
                o3 : "object2"
            
            PUB function(a) : r
            
                e := o3.function(3)
                return a + d * e
            """);
        sources.put("object2.spin2", """
            VAR
            
                long d
            
            PUB function(a) : r | b, c
            
                return (a + b * c) * d
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 56)
            01888 00000       34 00 00 00    Object "object1.spin2" @ $00034
            0188C 00004       08 00 00 00    Variables @ $00008
            01890 00008       34 00 00 00    Object "object1.spin2" @ $00034
            01894 0000C       1C 00 00 00    Variables @ $0001C
            01898 00010       58 00 00 00    Object "object2.spin2" @ $00058
            0189C 00014       30 00 00 00    Variables @ $00030
            018A0 00018       20 00 00 80    Method main @ $00020 (0 parameters, 0 returns)
            018A4 0001C       31 00 00 00    End
            ' PUB main() | a
            018A8 00020       01             (stack size)
            '     o1.function(1)
            018A9 00021       00             ANCHOR
            018AA 00022       A2             CONSTANT (1)
            018AB 00023       08 00 02       CALL_OBJ_SUB (0.2)
            '     o2.function(2)
            018AE 00026       00             ANCHOR
            018AF 00027       A3             CONSTANT (2)
            018B0 00028       08 01 02       CALL_OBJ_SUB (1.2)
            '     o3.function(3)
            018B3 0002B       00             ANCHOR
            018B4 0002C       A4             CONSTANT (3)
            018B5 0002D       08 02 00       CALL_OBJ_SUB (2.0)
            018B8 00030       04             RETURN
            018B9 00031       00 00 00       Padding
            ' Object "object1.spin2" header (var size 20)
            018BC 00000       24 00 00 00    Object "object2.spin2" @ $00024
            018C0 00004       0C 00 00 00    Variables @ $0000C
            018C4 00008       10 00 10 81    Method function @ $00010 (1 parameters, 1 returns)
            018C8 0000C       21 00 00 00    End
            ' PUB function(a) : r
            018CC 00010       00             (stack size)
            '     e := o3.function(3)
            018CD 00011       01             ANCHOR (push)
            018CE 00012       A4             CONSTANT (3)
            018CF 00013       08 00 00       CALL_OBJ_SUB (0.0)
            018D2 00016       C2 1D          VAR_WRITE LONG VBASE+$00002 (short)
            '     return a + d * e
            018D4 00018       E0             VAR_READ LONG DBASE+$00000 (short)
            018D5 00019       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            018D7 0001B       C2 1C          VAR_READ LONG VBASE+$00002 (short)
            018D9 0001D       96             MULTIPLY
            018DA 0001E       8A             ADD
            018DB 0001F       05             RETURN
            018DC 00020       04             RETURN
            018DD 00021       00 00 00       Padding
            ' Object "object2.spin2" header (var size 8)
            018E0 00000       08 00 10 81    Method function @ $00008 (1 parameters, 1 returns)
            018E4 00004       13 00 00 00    End
            ' PUB function(a) : r | b, c
            018E8 00008       02             (stack size)
            '     return (a + b * c) * d
            018E9 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018EA 0000A       E2             VAR_READ LONG DBASE+$00002 (short)
            018EB 0000B       E3             VAR_READ LONG DBASE+$00003 (short)
            018EC 0000C       96             MULTIPLY
            018ED 0000D       8A             ADD
            018EE 0000E       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            018F0 00010       96             MULTIPLY
            018F1 00011       05             RETURN
            018F2 00012       04             RETURN
            018F3 00013       00             Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testNestedChildDuplicatedObjectLink() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            VAR
            
                long b
            
            OBJ
            
                o1 : "object1"
                o2 : "object2"
            
            PUB main() | a
            
                o1.function(1)
                o2.function(2)
            """);
        sources.put("object1.spin2", """
            VAR
            
                long d
                long e
            
            OBJ
            
                o3 : "object3"
            
            PUB function(a) : r
            
                e := o3.function(4)
                return a + d * e
            """);
        sources.put("object2.spin2", """
            VAR
            
                long d
                long e
            
            OBJ
            
                o3 : "object3"
            
            PUB function(a) : r | b, c
            
                e := o3.function(5)
                return a + d / e
            """);
        sources.put("object3.spin2", """
            VAR
            
                long d
            
            PUB function(a) : r | b, c
            
                return (a + b * c) * d
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 48)
            01888 00000       24 00 00 00    Object "object1.spin2" @ $00024
            0188C 00004       08 00 00 00    Variables @ $00008
            01890 00008       48 00 00 00    Object "object2.spin2" @ $00048
            01894 0000C       1C 00 00 00    Variables @ $0001C
            01898 00010       18 00 00 80    Method main @ $00018 (0 parameters, 0 returns)
            0189C 00014       24 00 00 00    End
            ' PUB main() | a
            018A0 00018       01             (stack size)
            '     o1.function(1)
            018A1 00019       00             ANCHOR
            018A2 0001A       A2             CONSTANT (1)
            018A3 0001B       08 00 02       CALL_OBJ_SUB (0.2)
            '     o2.function(2)
            018A6 0001E       00             ANCHOR
            018A7 0001F       A3             CONSTANT (2)
            018A8 00020       08 01 02       CALL_OBJ_SUB (1.2)
            018AB 00023       04             RETURN
            ' Object "object1.spin2" header (var size 20)
            018AC 00000       48 00 00 00    Object "object3.spin2" @ $00048
            018B0 00004       0C 00 00 00    Variables @ $0000C
            018B4 00008       10 00 10 81    Method function @ $00010 (1 parameters, 1 returns)
            018B8 0000C       21 00 00 00    End
            ' PUB function(a) : r
            018BC 00010       00             (stack size)
            '     e := o3.function(4)
            018BD 00011       01             ANCHOR (push)
            018BE 00012       A5             CONSTANT (4)
            018BF 00013       08 00 00       CALL_OBJ_SUB (0.0)
            018C2 00016       C2 1D          VAR_WRITE LONG VBASE+$00002 (short)
            '     return a + d * e
            018C4 00018       E0             VAR_READ LONG DBASE+$00000 (short)
            018C5 00019       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            018C7 0001B       C2 1C          VAR_READ LONG VBASE+$00002 (short)
            018C9 0001D       96             MULTIPLY
            018CA 0001E       8A             ADD
            018CB 0001F       05             RETURN
            018CC 00020       04             RETURN
            018CD 00021       00 00 00       Padding
            ' Object "object2.spin2" header (var size 20)
            018D0 00000       24 00 00 00    Object "object3.spin2" @ $00024
            018D4 00004       0C 00 00 00    Variables @ $0000C
            018D8 00008       10 00 10 81    Method function @ $00010 (1 parameters, 1 returns)
            018DC 0000C       21 00 00 00    End
            ' PUB function(a) : r | b, c
            018E0 00010       02             (stack size)
            '     e := o3.function(5)
            018E1 00011       01             ANCHOR (push)
            018E2 00012       A6             CONSTANT (5)
            018E3 00013       08 00 00       CALL_OBJ_SUB (0.0)
            018E6 00016       C2 1D          VAR_WRITE LONG VBASE+$00002 (short)
            '     return a + d / e
            018E8 00018       E0             VAR_READ LONG DBASE+$00000 (short)
            018E9 00019       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            018EB 0001B       C2 1C          VAR_READ LONG VBASE+$00002 (short)
            018ED 0001D       97             DIVIDE
            018EE 0001E       8A             ADD
            018EF 0001F       05             RETURN
            018F0 00020       04             RETURN
            018F1 00021       00 00 00       Padding
            ' Object "object3.spin2" header (var size 8)
            018F4 00000       08 00 10 81    Method function @ $00008 (1 parameters, 1 returns)
            018F8 00004       13 00 00 00    End
            ' PUB function(a) : r | b, c
            018FC 00008       02             (stack size)
            '     return (a + b * c) * d
            018FD 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018FE 0000A       E2             VAR_READ LONG DBASE+$00002 (short)
            018FF 0000B       E3             VAR_READ LONG DBASE+$00003 (short)
            01900 0000C       96             MULTIPLY
            01901 0000D       8A             ADD
            01902 0000E       C1 1C          VAR_READ LONG VBASE+$00001 (short)
            01904 00010       96             MULTIPLY
            01905 00011       05             RETURN
            01906 00012       04             RETURN
            01907 00013       00             Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testCircularReference1() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o1 : "object1"
                o2 : "object2"
            
            """);
        sources.put("object1.spin2", """
            OBJ
            
                o3 : "object2"
            
            """);
        sources.put("object2.spin2", """
            OBJ
            
                o4 : "main"
            
            """);

        Assertions.assertThrows(CompilerException.class, () -> {
            compile("main.spin2", sources);
        });
    }

    @Test
    void testCircularReference2() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o1 : "object1"
            
            """);
        sources.put("object1.spin2", """
            OBJ
            
                o3 : "object2"
            
            """);
        sources.put("object2.spin2", """
            OBJ
            
                o4 : "main"
            
            """);

        Assertions.assertThrows(CompilerException.class, () -> {
            compile("main.spin2", sources);
        });
    }

    @Test
    void testDebug() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o : "text2"
            
            PUB main() | a
            
                debug(udec(a))
            
            """);
        sources.put("text2.spin2", """
            PUB start(a, b) | c
            
                c := a + b
                debug(udec(a,b,c))
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 8)
            01888 00000       18 00 00 00    Object "text2.spin2" @ $00018
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            01894 0000C       16 00 00 00    End
            ' PUB main() | a
            01898 00010       01             (stack size)
            '     debug(udec(a))
            01899 00011       E0             VAR_READ LONG DBASE+$00000 (short)
            0189A 00012       41 04 02       DEBUG #2
            0189D 00015       04             RETURN
            0189E 00016       00 00          Padding
            ' Object "text2.spin2" header (var size 4)
            018A0 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)
            018A4 00004       14 00 00 00    End
            ' PUB start(a, b) | c
            018A8 00008       01             (stack size)
            '     c := a + b
            018A9 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018AA 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            018AB 0000B       8A             ADD
            018AC 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)
            '     debug(udec(a,b,c))
            018AD 0000D       E0             VAR_READ LONG DBASE+$00000 (short)
            018AE 0000E       E1             VAR_READ LONG DBASE+$00001 (short)
            018AF 0000F       E2             VAR_READ LONG DBASE+$00002 (short)
            018B0 00010       41 0C 01       DEBUG #1
            018B3 00013       04             RETURN
            ' Debug data
            00B74 00000       16 00        \s
            00B76 00002       06 00          #1@0006
            00B78 00004       11 00          #2@0011
            ' #1
            00B7A 00006       04             COGN
            00B7B 00007       41 61 00       UDEC(a)
            00B7E 0000A       40 62 00       UDEC(b)
            00B81 0000D       40 63 00       UDEC(c)
            00B84 00010       00             DONE
            ' #2
            00B85 00011       04             COGN
            00B86 00012       41 61 00       UDEC(a)
            00B89 00015       00             DONE
            """, compile("main.spin2", sources, false, true));
    }

    @Test
    void testDuplicateObjectDebug() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o1 : "text2"
                o2 : "text2"
            
            PUB main() | a
            
                debug(udec(a))
            
            """);
        sources.put("text2.spin2", """
            PUB start(a, b) | c
            
                c := a + b
                debug(udec(a,b,c))
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 12)
            01888 00000       20 00 00 00    Object "text2.spin2" @ $00020
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       20 00 00 00    Object "text2.spin2" @ $00020
            01894 0000C       08 00 00 00    Variables @ $00008
            01898 00010       18 00 00 80    Method main @ $00018 (0 parameters, 0 returns)
            0189C 00014       1E 00 00 00    End
            ' PUB main() | a
            018A0 00018       01             (stack size)
            '     debug(udec(a))
            018A1 00019       E0             VAR_READ LONG DBASE+$00000 (short)
            018A2 0001A       41 04 02       DEBUG #2
            018A5 0001D       04             RETURN
            018A6 0001E       00 00          Padding
            ' Object "text2.spin2" header (var size 4)
            018A8 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)
            018AC 00004       14 00 00 00    End
            ' PUB start(a, b) | c
            018B0 00008       01             (stack size)
            '     c := a + b
            018B1 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018B2 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            018B3 0000B       8A             ADD
            018B4 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)
            '     debug(udec(a,b,c))
            018B5 0000D       E0             VAR_READ LONG DBASE+$00000 (short)
            018B6 0000E       E1             VAR_READ LONG DBASE+$00001 (short)
            018B7 0000F       E2             VAR_READ LONG DBASE+$00002 (short)
            018B8 00010       41 0C 01       DEBUG #1
            018BB 00013       04             RETURN
            ' Debug data
            00B74 00000       16 00        \s
            00B76 00002       06 00          #1@0006
            00B78 00004       11 00          #2@0011
            ' #1
            00B7A 00006       04             COGN
            00B7B 00007       41 61 00       UDEC(a)
            00B7E 0000A       40 62 00       UDEC(b)
            00B81 0000D       40 63 00       UDEC(c)
            00B84 00010       00             DONE
            ' #2
            00B85 00011       04             COGN
            00B86 00012       41 61 00       UDEC(a)
            00B89 00015       00             DONE
            """, compile("main.spin2", sources, false, true));
    }

    @Test
    void testDuplicatedObjectsPAsmDebug() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o1 : "text2"
                o2 : "text2"
            
            PUB main() | a
            
                debug(udec(a))
            """);
        sources.put("text2.spin2", """
            DAT             org   $000
                            mov   a, #1
                            debug(udec(a))
                            ret
            a               res   1
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 12)
            01888 00000       20 00 00 00    Object "text2.spin2" @ $00020
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       20 00 00 00    Object "text2.spin2" @ $00020
            01894 0000C       08 00 00 00    Variables @ $00008
            01898 00010       18 00 00 80    Method main @ $00018 (0 parameters, 0 returns)
            0189C 00014       1E 00 00 00    End
            ' PUB main() | a
            018A0 00018       01             (stack size)
            '     debug(udec(a))
            018A1 00019       E0             VAR_READ LONG DBASE+$00000 (short)
            018A2 0001A       41 04 02       DEBUG #2
            018A5 0001D       04             RETURN
            018A6 0001E       00 00          Padding
            ' Object "text2.spin2" header (var size 4)
            018A8 00000   000                                    org     $000
            018A8 00000   000 01 06 04 F6                        mov     a, #1
            018AC 00004   001 36 02 64 FD                        debug(udec(a))
            018B0 00008   002 2D 00 64 FD                        ret
            018B4 0000C   003                a                   res     1
            ' Debug data
            00B74 00000       13 00        \s
            00B76 00002       06 00          #1@0006
            00B78 00004       0E 00          #2@000E
            ' #1
            00B7A 00006       01             ASMMODE
            00B7B 00007       04             COGN
            00B7C 00008       41 61 00 80 03 UDEC(a)
            00B81 0000D       00             DONE
            ' #2
            00B82 0000E       04             COGN
            00B83 0000F       41 61 00       UDEC(a)
            00B86 00012       00             DONE
            """, compile("main.spin2", sources, false, true));
    }

    @Test
    void testDuplicatedObjectsDebug() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o1 : "text2"
                o2 : "text2"
            
            PUB main() | a
            
                debug(udec(a))
            
            """);
        sources.put("text2.spin2", """
            PUB start(a, b) | c
            
                c := a + b
                debug(udec(a,b,c))
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 12)
            01888 00000       20 00 00 00    Object "text2.spin2" @ $00020
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       20 00 00 00    Object "text2.spin2" @ $00020
            01894 0000C       08 00 00 00    Variables @ $00008
            01898 00010       18 00 00 80    Method main @ $00018 (0 parameters, 0 returns)
            0189C 00014       1E 00 00 00    End
            ' PUB main() | a
            018A0 00018       01             (stack size)
            '     debug(udec(a))
            018A1 00019       E0             VAR_READ LONG DBASE+$00000 (short)
            018A2 0001A       41 04 02       DEBUG #2
            018A5 0001D       04             RETURN
            018A6 0001E       00 00          Padding
            ' Object "text2.spin2" header (var size 4)
            018A8 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)
            018AC 00004       14 00 00 00    End
            ' PUB start(a, b) | c
            018B0 00008       01             (stack size)
            '     c := a + b
            018B1 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018B2 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            018B3 0000B       8A             ADD
            018B4 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)
            '     debug(udec(a,b,c))
            018B5 0000D       E0             VAR_READ LONG DBASE+$00000 (short)
            018B6 0000E       E1             VAR_READ LONG DBASE+$00001 (short)
            018B7 0000F       E2             VAR_READ LONG DBASE+$00002 (short)
            018B8 00010       41 0C 01       DEBUG #1
            018BB 00013       04             RETURN
            ' Debug data
            00B74 00000       16 00        \s
            00B76 00002       06 00          #1@0006
            00B78 00004       11 00          #2@0011
            ' #1
            00B7A 00006       04             COGN
            00B7B 00007       41 61 00       UDEC(a)
            00B7E 0000A       40 62 00       UDEC(b)
            00B81 0000D       40 63 00       UDEC(c)
            00B84 00010       00             DONE
            ' #2
            00B85 00011       04             COGN
            00B86 00012       41 61 00       UDEC(a)
            00B89 00015       00             DONE
            """, compile("main.spin2", sources, false, true));
    }

    @Test
    void testObjectDebugDisabled() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o : "text2"
            
            PUB main() | a
            
                debug(udec(a))
            
            """);
        sources.put("text2.spin2", """
            DEBUG_DISABLE = true
            
            PUB start(a, b) | c
            
                c := a + b
                debug(udec(a,b,c))
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 8)
            01888 00000       18 00 00 00    Object "text2.spin2" @ $00018
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            01894 0000C       16 00 00 00    End
            ' PUB main() | a
            01898 00010       01             (stack size)
            '     debug(udec(a))
            01899 00011       E0             VAR_READ LONG DBASE+$00000 (short)
            0189A 00012       41 04 01       DEBUG #1
            0189D 00015       04             RETURN
            0189E 00016       00 00          Padding
            ' Object "text2.spin2" header (var size 4)
            018A0 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)
            018A4 00004       0E 00 00 00    End
            ' PUB start(a, b) | c
            018A8 00008       01             (stack size)
            '     c := a + b
            018A9 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018AA 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            018AB 0000B       8A             ADD
            018AC 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)
            '     debug(udec(a,b,c))
            018AD 0000D       04             RETURN
            018AE 0000E       00 00          Padding
            ' Debug data
            00B74 00000       09 00        \s
            00B76 00002       04 00          #1@0004
            ' #1
            00B78 00004       04             COGN
            00B79 00005       41 61 00       UDEC(a)
            00B7C 00008       00             DONE
            """, compile("main.spin2", sources, false, true));
    }

    @Test
    void testDisableObjectDebugParameter() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o : "text2" | DEBUG_DISABLE=true
            
            PUB main() | a
            
                debug(udec(a))
            
            """);
        sources.put("text2.spin2", """
            PUB start(a, b) | c
            
                c := a + b
                debug(udec(a,b,c))
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 8)
            01888 00000       18 00 00 00    Object "text2.spin2" @ $00018
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            01894 0000C       16 00 00 00    End
            ' PUB main() | a
            01898 00010       01             (stack size)
            '     debug(udec(a))
            01899 00011       E0             VAR_READ LONG DBASE+$00000 (short)
            0189A 00012       41 04 01       DEBUG #1
            0189D 00015       04             RETURN
            0189E 00016       00 00          Padding
            ' Object "text2.spin2" header (var size 4)
            018A0 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)
            018A4 00004       0E 00 00 00    End
            ' PUB start(a, b) | c
            018A8 00008       01             (stack size)
            '     c := a + b
            018A9 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018AA 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            018AB 0000B       8A             ADD
            018AC 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)
            '     debug(udec(a,b,c))
            018AD 0000D       04             RETURN
            018AE 0000E       00 00          Padding
            ' Debug data
            00B74 00000       09 00        \s
            00B76 00002       04 00          #1@0004
            ' #1
            00B78 00004       04             COGN
            00B79 00005       41 61 00       UDEC(a)
            00B7C 00008       00             DONE
            """, compile("main.spin2", sources, false, true));
    }

    @Test
    void testEnableObjectDebugParameter() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o : "text2" | DEBUG_DISABLE=false
            
            PUB main() | a
            
                debug(udec(a))
            
            """);
        sources.put("text2.spin2", """
            DEBUG_DISABLE = true
            
            PUB start(a, b) | c
            
                c := a + b
                debug(udec(a,b,c))
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 8)
            01888 00000       18 00 00 00    Object "text2.spin2" @ $00018
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            01894 0000C       16 00 00 00    End
            ' PUB main() | a
            01898 00010       01             (stack size)
            '     debug(udec(a))
            01899 00011       E0             VAR_READ LONG DBASE+$00000 (short)
            0189A 00012       41 04 02       DEBUG #2
            0189D 00015       04             RETURN
            0189E 00016       00 00          Padding
            ' Object "text2.spin2" header (var size 4)
            018A0 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)
            018A4 00004       14 00 00 00    End
            ' PUB start(a, b) | c
            018A8 00008       01             (stack size)
            '     c := a + b
            018A9 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018AA 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            018AB 0000B       8A             ADD
            018AC 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)
            '     debug(udec(a,b,c))
            018AD 0000D       E0             VAR_READ LONG DBASE+$00000 (short)
            018AE 0000E       E1             VAR_READ LONG DBASE+$00001 (short)
            018AF 0000F       E2             VAR_READ LONG DBASE+$00002 (short)
            018B0 00010       41 0C 01       DEBUG #1
            018B3 00013       04             RETURN
            ' Debug data
            00B74 00000       16 00        \s
            00B76 00002       06 00          #1@0006
            00B78 00004       11 00          #2@0011
            ' #1
            00B7A 00006       04             COGN
            00B7B 00007       41 61 00       UDEC(a)
            00B7E 0000A       40 62 00       UDEC(b)
            00B81 0000D       40 63 00       UDEC(c)
            00B84 00010       00             DONE
            ' #2
            00B85 00011       04             COGN
            00B86 00012       41 61 00       UDEC(a)
            00B89 00015       00             DONE
            """, compile("main.spin2", sources, false, true));
    }

    @Test
    void testRemoveUnusedMethods() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            PUB main() | a
            
                a := 1
            
            PUB start(a, b) | c
            
                c := a + b
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 4)
            01888 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            0188C 00004       0C 00 00 00    End
            ' PUB main() | a
            01890 00008       01             (stack size)
            '     a := 1
            01891 00009       A2             CONSTANT (1)
            01892 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            01893 0000B       04             RETURN
            """, compile("main.spin2", sources, true, false));
    }

    @Test
    void testRemoveMiddleUnusedMethods() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            PUB main() | a
            
                a := 1
                start(1, 2)
            
            PUB stop() | c
            
            PUB start(a, b) | c
            
                c := a + b
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 4)
            01888 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            0188C 00004       15 00 00 82    Method start @ $00015 (2 parameters, 0 returns)
            01890 00008       1B 00 00 00    End
            ' PUB main() | a
            01894 0000C       01             (stack size)
            '     a := 1
            01895 0000D       A2             CONSTANT (1)
            01896 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     start(1, 2)
            01897 0000F       00             ANCHOR
            01898 00010       A2             CONSTANT (1)
            01899 00011       A3             CONSTANT (2)
            0189A 00012       0A 01          CALL_SUB (1)
            0189C 00014       04             RETURN
            ' PUB start(a, b) | c
            0189D 00015       01             (stack size)
            '     c := a + b
            0189E 00016       E0             VAR_READ LONG DBASE+$00000 (short)
            0189F 00017       E1             VAR_READ LONG DBASE+$00001 (short)
            018A0 00018       8A             ADD
            018A1 00019       F2             VAR_WRITE LONG DBASE+$00002 (short)
            018A2 0001A       04             RETURN
            018A3 0001B       00             Padding
            """, compile("main.spin2", sources, true, false));
    }

    @Test
    void testPAsmOnlyRemoveUnusedMethods() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            DAT             org     $000
            
            start
                            cogid   a
                            cogstop a
            
            a               res     1
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 4)
            00000 00000   000                                    org     $000
            00000 00000   000                start              \s
            00000 00000   000 01 04 60 FD                        cogid   a
            00004 00004   001 03 04 60 FD                        cogstop a
            00008 00008   002                a                   res     1
            """, compile("main.spin2", sources, true, false));
    }

    @Test
    void testUnusedMethodsCauseErrors() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            PUB main() | a
            
                a := 1
            
            PUB start(a, b)
            
                c := a + b
            
            """);

        Assertions.assertThrows(CompilerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                compile("main.spin2", sources, true, false);
            }

        });
    }

    @Test
    void testObjectUnusedMethodCall() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o : "text2"
            
            PUB main() | a
            
                o.start(1, 2)
                o.method2()
            
            """);
        sources.put("text2.spin2", """
            PUB start(a, b)
            
            PUB method1()
            
            PUB method2()
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 8)
            01888 00000       1C 00 00 00    Object "text2.spin2" @ $0001C
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            01894 0000C       1C 00 00 00    End
            ' PUB main() | a
            01898 00010       01             (stack size)
            '     o.start(1, 2)
            01899 00011       00             ANCHOR
            0189A 00012       A2             CONSTANT (1)
            0189B 00013       A3             CONSTANT (2)
            0189C 00014       08 00 00       CALL_OBJ_SUB (0.0)
            '     o.method2()
            0189F 00017       00             ANCHOR
            018A0 00018       08 00 01       CALL_OBJ_SUB (0.1)
            018A3 0001B       04             RETURN
            ' Object "text2.spin2" header (var size 4)
            018A4 00000       0C 00 00 82    Method start @ $0000C (2 parameters, 0 returns)
            018A8 00004       0E 00 00 80    Method method2 @ $0000E (0 parameters, 0 returns)
            018AC 00008       10 00 00 00    End
            ' PUB start(a, b)
            018B0 0000C       00             (stack size)
            018B1 0000D       04             RETURN
            ' PUB method2()
            018B2 0000E       00             (stack size)
            018B3 0000F       04             RETURN
            """, compile("main.spin2", sources, true, false));
    }

    @Test
    void testObjectArray() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o[2] : "text2"
            
            PUB main()
            
            """);
        sources.put("text2.spin2", """
            PUB start()
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 12)
            01888 00000       1C 00 00 00    Object "text2.spin2" @ $0001C
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       1C 00 00 00    Object "text2.spin2" @ $0001C
            01894 0000C       08 00 00 00    Variables @ $00008
            01898 00010       18 00 00 80    Method main @ $00018 (0 parameters, 0 returns)
            0189C 00014       1A 00 00 00    End
            ' PUB main()
            018A0 00018       00             (stack size)
            018A1 00019       04             RETURN
            018A2 0001A       00 00          Padding
            ' Object "text2.spin2" header (var size 4)
            018A4 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            018A8 00004       0A 00 00 00    End
            ' PUB start()
            018AC 00008       00             (stack size)
            018AD 00009       04             RETURN
            018AE 0000A       00 00          Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testObjectArrayExpression() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            CON
            
                NUMBER = 2
            
            OBJ
            
                o[NUMBER + 1] : "text2"
            
            PUB main()
            
            """);
        sources.put("text2.spin2", """
            PUB start()
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 16)
            01888 00000       24 00 00 00    Object "text2.spin2" @ $00024
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       24 00 00 00    Object "text2.spin2" @ $00024
            01894 0000C       08 00 00 00    Variables @ $00008
            01898 00010       24 00 00 00    Object "text2.spin2" @ $00024
            0189C 00014       0C 00 00 00    Variables @ $0000C
            018A0 00018       20 00 00 80    Method main @ $00020 (0 parameters, 0 returns)
            018A4 0001C       22 00 00 00    End
            ' PUB main()
            018A8 00020       00             (stack size)
            018A9 00021       04             RETURN
            018AA 00022       00 00          Padding
            ' Object "text2.spin2" header (var size 4)
            018AC 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            018B0 00004       0A 00 00 00    End
            ' PUB start()
            018B4 00008       00             (stack size)
            018B5 00009       04             RETURN
            018B6 0000A       00 00          Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testObjectConstants() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o : "text2"
            
            PUB main() | a
            
                a := o.ZERO
                a := o.ONE
                a := o._LAST
            
            """);
        sources.put("text2.spin2", """
            CON
            
                #0
                ZERO, ONE
                _LAST
            
            PUB start()
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 8)
            01888 00000       18 00 00 00    Object "text2.spin2" @ $00018
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            01894 0000C       18 00 00 00    End
            ' PUB main() | a
            01898 00010       01             (stack size)
            '     a := o.ZERO
            01899 00011       A1             CONSTANT (0)
            0189A 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := o.ONE
            0189B 00013       A2             CONSTANT (1)
            0189C 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := o._LAST
            0189D 00015       A3             CONSTANT (2)
            0189E 00016       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0189F 00017       04             RETURN
            ' Object "text2.spin2" header (var size 4)
            018A0 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            018A4 00004       0A 00 00 00    End
            ' PUB start()
            018A8 00008       00             (stack size)
            018A9 00009       04             RETURN
            018AA 0000A       00 00          Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testObjectConstantsReference() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            CON
            
                #o._LAST
                TWO, THREE
            
            OBJ
            
                o : "text2"
            
            PUB main() | a
            
                a := TWO
                a := THREE
            
            """);
        sources.put("text2.spin2", """
            CON
            
                #0
                ZERO, ONE
                _LAST
            
            PUB start()
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 8)
            01888 00000       18 00 00 00    Object "text2.spin2" @ $00018
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            01894 0000C       16 00 00 00    End
            ' PUB main() | a
            01898 00010       01             (stack size)
            '     a := TWO
            01899 00011       A3             CONSTANT (o._LAST)
            0189A 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := THREE
            0189B 00013       A4             CONSTANT (o._LAST + 1)
            0189C 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0189D 00015       04             RETURN
            0189E 00016       00 00          Padding
            ' Object "text2.spin2" header (var size 4)
            018A0 00000       08 00 00 80    Method start @ $00008 (0 parameters, 0 returns)
            018A4 00004       0A 00 00 00    End
            ' PUB start()
            018A8 00008       00             (stack size)
            018A9 00009       04             RETURN
            018AA 0000A       00 00          Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testObjectArrayMethodCall() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o[2] : "text2"
            
            PUB main()
            
                o[0].start(1, 2)
                \\o[1].start(3, 4)
            
            """);
        sources.put("text2.spin2", """
            PUB start(a, b) | c
            
                c := a + b
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 12)
            01888 00000       28 00 00 00    Object "text2.spin2" @ $00028
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       28 00 00 00    Object "text2.spin2" @ $00028
            01894 0000C       08 00 00 00    Variables @ $00008
            01898 00010       18 00 00 80    Method main @ $00018 (0 parameters, 0 returns)
            0189C 00014       28 00 00 00    End
            ' PUB main()
            018A0 00018       00             (stack size)
            '     o[0].start(1, 2)
            018A1 00019       00             ANCHOR
            018A2 0001A       A2             CONSTANT (1)
            018A3 0001B       A3             CONSTANT (2)
            018A4 0001C       A1             CONSTANT (0)
            018A5 0001D       09 00 00       CALL_OBJ_SUB (0.0) (indexed)
            '     \\o[1].start(3, 4)
            018A8 00020       02             ANCHOR_TRAP
            018A9 00021       A4             CONSTANT (3)
            018AA 00022       A5             CONSTANT (4)
            018AB 00023       A2             CONSTANT (1)
            018AC 00024       09 00 00       CALL_OBJ_SUB (0.0) (indexed)
            018AF 00027       04             RETURN
            ' Object "text2.spin2" header (var size 4)
            018B0 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)
            018B4 00004       0E 00 00 00    End
            ' PUB start(a, b) | c
            018B8 00008       01             (stack size)
            '     c := a + b
            018B9 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018BA 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            018BB 0000B       8A             ADD
            018BC 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)
            018BD 0000D       04             RETURN
            018BE 0000E       00 00          Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testObjectArrayMethodPointer() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o[2] : "text2"
            
            PUB main() | a, b
            
                a := @o[0].start
                b := @o[1].start
            
            """);
        sources.put("text2.spin2", """
            PUB start(a, b) | c
            
                c := a + b
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 12)
            01888 00000       24 00 00 00    Object "text2.spin2" @ $00024
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       24 00 00 00    Object "text2.spin2" @ $00024
            01894 0000C       08 00 00 00    Variables @ $00008
            01898 00010       18 00 00 80    Method main @ $00018 (0 parameters, 0 returns)
            0189C 00014       24 00 00 00    End
            ' PUB main() | a, b
            018A0 00018       02             (stack size)
            '     a := @o[0].start
            018A1 00019       A1             CONSTANT (0)
            018A2 0001A       10 00 00       OBJ_SUB_ADDRESS (0.0) (indexed)
            018A5 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     b := @o[1].start
            018A6 0001E       A2             CONSTANT (1)
            018A7 0001F       10 00 00       OBJ_SUB_ADDRESS (0.0) (indexed)
            018AA 00022       F1             VAR_WRITE LONG DBASE+$00001 (short)
            018AB 00023       04             RETURN
            ' Object "text2.spin2" header (var size 4)
            018AC 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)
            018B0 00004       0E 00 00 00    End
            ' PUB start(a, b) | c
            018B4 00008       01             (stack size)
            '     c := a + b
            018B5 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018B6 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            018B7 0000B       8A             ADD
            018B8 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)
            018B9 0000D       04             RETURN
            018BA 0000E       00 00          Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testMixedObjectArrayMethodCall() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o[2] : "text2"
                o2 : "text2"
            
            PUB main()
            
                o[0].start(1, 2)
                o[1].start(3, 4)
                o2.start(5, 6)
            
            """);
        sources.put("text2.spin2", """
            PUB start(a, b) | c
            
                c := a + b
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 16)
            01888 00000       38 00 00 00    Object "text2.spin2" @ $00038
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       38 00 00 00    Object "text2.spin2" @ $00038
            01894 0000C       08 00 00 00    Variables @ $00008
            01898 00010       38 00 00 00    Object "text2.spin2" @ $00038
            0189C 00014       0C 00 00 00    Variables @ $0000C
            018A0 00018       20 00 00 80    Method main @ $00020 (0 parameters, 0 returns)
            018A4 0001C       36 00 00 00    End
            ' PUB main()
            018A8 00020       00             (stack size)
            '     o[0].start(1, 2)
            018A9 00021       00             ANCHOR
            018AA 00022       A2             CONSTANT (1)
            018AB 00023       A3             CONSTANT (2)
            018AC 00024       A1             CONSTANT (0)
            018AD 00025       09 00 00       CALL_OBJ_SUB (0.0) (indexed)
            '     o[1].start(3, 4)
            018B0 00028       00             ANCHOR
            018B1 00029       A4             CONSTANT (3)
            018B2 0002A       A5             CONSTANT (4)
            018B3 0002B       A2             CONSTANT (1)
            018B4 0002C       09 00 00       CALL_OBJ_SUB (0.0) (indexed)
            '     o2.start(5, 6)
            018B7 0002F       00             ANCHOR
            018B8 00030       A6             CONSTANT (5)
            018B9 00031       A7             CONSTANT (6)
            018BA 00032       08 02 00       CALL_OBJ_SUB (2.0)
            018BD 00035       04             RETURN
            018BE 00036       00 00          Padding
            ' Object "text2.spin2" header (var size 4)
            018C0 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)
            018C4 00004       0E 00 00 00    End
            ' PUB start(a, b) | c
            018C8 00008       01             (stack size)
            '     c := a + b
            018C9 00009       E0             VAR_READ LONG DBASE+$00000 (short)
            018CA 0000A       E1             VAR_READ LONG DBASE+$00001 (short)
            018CB 0000B       8A             ADD
            018CC 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)
            018CD 0000D       04             RETURN
            018CE 0000E       00 00          Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testPAsmAddress() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            PUB start() | ptr
            
                ptr := @b
                ptr := @@b
                ptr := @@@b
            
                ptr := @c
                ptr := @@c
                ptr := @@@c
            
            DAT
                            org     $000
            
            b               long    b
                            long    @b
                            long    @@@b
            
                            long    c
                            long    @c
                            long    @@@c
            
            DAT
                            orgh
            
            c               long    c
                            long    @c
                            long    @@@c
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 4)
            01888 00000       2C 00 00 80    Method start @ $0002C (0 parameters, 0 returns)
            0188C 00004       48 00 00 00    End
            01890 00008   000                                    org     $000
            01890 00008   000 00 00 00 00    b                   long    b
            01894 0000C   001 08 00 00 00                        long    @b
            01898 00010   002 90 18 00 00                        long    @@@b
            0189C 00014   003 18 04 00 00                        long    c
            018A0 00018   004 20 00 00 00                        long    @c
            018A4 0001C   005 A8 18 00 00                        long    @@@c
            018A8 00020 00400                                    orgh
            018A8 00020 00400 00 04 00 00    c                   long    c
            018AC 00024 00404 20 00 00 00                        long    @c
            018B0 00028 00408 A8 18 00 00                        long    @@@c
            ' PUB start() | ptr
            018B4 0002C       01             (stack size)
            '     ptr := @b
            018B5 0002D       5B 08 1B       MEM_ADDRESS PBASE+$00008
            018B8 00030       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr := @@b
            018B9 00031       5B 08 1C       MEM_READ LONG PBASE+$00008
            018BC 00034       24             ADD_PBASE
            018BD 00035       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr := @@@b
            018BE 00036       44 90 18       CONSTANT ($01890)
            018C1 00039       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr := @c
            018C2 0003A       5B 20 1B       MEM_ADDRESS PBASE+$00020
            018C5 0003D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr := @@c
            018C6 0003E       5B 20 1C       MEM_READ LONG PBASE+$00020
            018C9 00041       24             ADD_PBASE
            018CA 00042       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr := @@@c
            018CB 00043       44 A8 18       CONSTANT ($018A8)
            018CE 00046       F0             VAR_WRITE LONG DBASE+$00000 (short)
            018CF 00047       04             RETURN
            """, compile("main.spin2", sources));
    }

    @Test
    void testChildObjectPAsmAddress() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
            
                o : "text1"
            
            PUB main()
            
            """);
        sources.put("text1.spin2", """
            PUB start() | ptr
            
                ptr := @b
                ptr := @@b
                ptr := @@@b
            
                ptr := @c
                ptr := @@c
                ptr := @@@c
            
            DAT
                            org     $000
            
            b               long    b
                            long    @b
                            long    @@@b
            
                            long    c
                            long    @c
                            long    @@@c
            
            DAT
                            orgh
            
            c               long    c
                            long    @c
                            long    @@@c
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 8)
            01888 00000       14 00 00 00    Object "text1.spin2" @ $00014
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            01894 0000C       12 00 00 00    End
            ' PUB main()
            01898 00010       00             (stack size)
            01899 00011       04             RETURN
            0189A 00012       00 00          Padding
            ' Object "text1.spin2" header (var size 4)
            0189C 00000       2C 00 00 80    Method start @ $0002C (0 parameters, 0 returns)
            018A0 00004       48 00 00 00    End
            018A4 00008   000                                    org     $000
            018A4 00008   000 00 00 00 00    b                   long    b
            018A8 0000C   001 08 00 00 00                        long    @b
            018AC 00010   002 A4 18 00 00                        long    @@@b
            018B0 00014   003 18 04 00 00                        long    c
            018B4 00018   004 20 00 00 00                        long    @c
            018B8 0001C   005 BC 18 00 00                        long    @@@c
            018BC 00020 00400                                    orgh
            018BC 00020 00400 00 04 00 00    c                   long    c
            018C0 00024 00404 20 00 00 00                        long    @c
            018C4 00028 00408 BC 18 00 00                        long    @@@c
            ' PUB start() | ptr
            018C8 0002C       01             (stack size)
            '     ptr := @b
            018C9 0002D       5B 08 1B       MEM_ADDRESS PBASE+$00008
            018CC 00030       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr := @@b
            018CD 00031       5B 08 1C       MEM_READ LONG PBASE+$00008
            018D0 00034       24             ADD_PBASE
            018D1 00035       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr := @@@b
            018D2 00036       44 A4 18       CONSTANT ($018A4)
            018D5 00039       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr := @c
            018D6 0003A       5B 20 1B       MEM_ADDRESS PBASE+$00020
            018D9 0003D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr := @@c
            018DA 0003E       5B 20 1C       MEM_READ LONG PBASE+$00020
            018DD 00041       24             ADD_PBASE
            018DE 00042       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     ptr := @@@c
            018DF 00043       44 BC 18       CONSTANT ($018BC)
            018E2 00046       F0             VAR_WRITE LONG DBASE+$00000 (short)
            018E3 00047       04             RETURN
            """, compile("main.spin2", sources));
    }

    @Test
    void testSpinAbsoluteAddress() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            PUB main() | a
            
                a := @@driver
            
            DAT
                            org   $000
            driver          jmp   #$
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 4)
            01888 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            0188C 00004       13 00 00 00    End
            01890 00008   000                                    org     $000
            01890 00008   000 FC FF 9F FD    driver              jmp     #$
            ' PUB main() | a
            01894 0000C       01             (stack size)
            '     a := @@driver
            01895 0000D       5B 08 1C       MEM_READ LONG PBASE+$00008
            01898 00010       24             ADD_PBASE
            01899 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0189A 00012       04             RETURN
            0189B 00013       00             Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testSpinAbsoluteAddressExpression() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            PUB main() | a, b
            
                a := @@driver + b
            
            DAT
                            org   $000
            driver          jmp   #$
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 4)
            01888 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)
            0188C 00004       15 00 00 00    End
            01890 00008   000                                    org     $000
            01890 00008   000 FC FF 9F FD    driver              jmp     #$
            ' PUB main() | a, b
            01894 0000C       02             (stack size)
            '     a := @@driver + b
            01895 0000D       5B 08 1C       MEM_READ LONG PBASE+$00008
            01898 00010       24             ADD_PBASE
            01899 00011       E1             VAR_READ LONG DBASE+$00001 (short)
            0189A 00012       8A             ADD
            0189B 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0189C 00014       04             RETURN
            0189D 00015       00 00 00       Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testMixedSpinAndHubExecCode() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            CON
                _CLKFREQ = 160_000_000
            
            PUB main()
            
                coginit(HUBEXEC_NEW, @blink, 0)
                repeat
            
            DAT
                            org
            
            delay           long    _CLKFREQ / 2
            ct              res     1
            
                            orgh
            '
            blink
                            getct   ct
            .loop           drvnot  #56
                            addct1  ct, delay
                            waitct1
                            jmp     #\\.loop
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 4)
            01888 00000       20 00 00 80    Method main @ $00020 (0 parameters, 0 returns)
            0188C 00004       2B 00 00 00    End
            01890 00008   000                                    org
            01890 00008   000 00 B4 C4 04    delay               long    _CLKFREQ / 2
            01894 0000C   001                ct                  res     1
            01894 0000C 00400                                    orgh
            01894 0000C 00400                blink              \s
            01894 0000C 00400 1A 02 60 FD                        getct   ct
            01898 00010 00404 5F 70 64 FD    .loop               drvnot  #56
            0189C 00014 00408 00 02 60 FA                        addct1  ct, delay
            018A0 00018 0040C 24 22 60 FD                        waitct1
            018A4 0001C 00410 04 04 80 FD                        jmp     #\\.loop
            ' PUB main()
            018A8 00020       00             (stack size)
            '     coginit(HUBEXEC_NEW, @blink, 0)
            018A9 00021       42 30          CONSTANT (48)
            018AB 00023       5B 0C 1B       MEM_ADDRESS PBASE+$0000C
            018AE 00026       A1             CONSTANT (0)
            018AF 00027       25             COGINIT
            '     repeat
            018B0 00028       12 7F          JMP $00028 (-1)
            018B2 0002A       04             RETURN
            018B3 0002B       00             Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testSpinOrgModes() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            PUB start()
            
            DAT
                            org     $000
            
            driver1         jmp     #\\$
            
            DAT
                            org     $040
            
            driver2         jmp     #\\$
            
            DAT
                            orgh
            
            driver3         jmp     #\\$
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 4)
            01888 00000       14 00 00 80    Method start @ $00014 (0 parameters, 0 returns)
            0188C 00004       16 00 00 00    End
            01890 00008   000                                    org     $000
            01890 00008   000 00 00 80 FD    driver1             jmp     #\\$
            01894 0000C   001                                    org     $040
            01894 0000C   040 40 00 80 FD    driver2             jmp     #\\$
            01898 00010 00400                                    orgh
            01898 00010 00400 00 04 80 FD    driver3             jmp     #\\$
            ' PUB start()
            0189C 00014       00             (stack size)
            0189D 00015       04             RETURN
            0189E 00016       00 00          Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testSpinOrgh() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            PUB main()
            
            DAT
                            orgh
            a               long    0
            
                            org
            b               long    a
                            long    b
                            long    c
            
                            orgh
            c               long    0
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 4)
            01888 00000       1C 00 00 80    Method main @ $0001C (0 parameters, 0 returns)
            0188C 00004       1E 00 00 00    End
            01890 00008 00400                                    orgh
            01890 00008 00400 00 00 00 00    a                   long    0
            01894 0000C   101                                    org
            01894 0000C   000 00 04 00 00    b                   long    a
            01898 00010   001 00 00 00 00                        long    b
            0189C 00014   002 10 04 00 00                        long    c
            018A0 00018 00410                                    orgh
            018A0 00018 00410 00 00 00 00    c                   long    0
            ' PUB main()
            018A4 0001C       00             (stack size)
            018A5 0001D       04             RETURN
            018A6 0001E       00 00          Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testObjectParameters() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            CON
                VAL5 = 5
            
            OBJ
            
                o0 : "text2"
                o1 : "text2" | PAR1 = 3
                o2 : "text2" | PAR1 = 4, PAR2 = VAL5
            
            PUB main()
            
                o0.setup()
                o1.setup()
                o2.setup()
            
            """);
        sources.put("text2.spin2", """
            CON
                #1, PAR1, PAR2
            
            PUB setup() | a, b
            
                a := PAR1
                b := PAR2
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 16)
            01888 00000       30 00 00 00    Object "text2.spin2" @ $00030
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       40 00 00 00    Object "text2.spin2" @ $00040
            01894 0000C       08 00 00 00    Variables @ $00008
            01898 00010       50 00 00 00    Object "text2.spin2" @ $00050
            0189C 00014       0C 00 00 00    Variables @ $0000C
            018A0 00018       20 00 00 80    Method main @ $00020 (0 parameters, 0 returns)
            018A4 0001C       2E 00 00 00    End
            ' PUB main()
            018A8 00020       00             (stack size)
            '     o0.setup()
            018A9 00021       00             ANCHOR
            018AA 00022       08 00 00       CALL_OBJ_SUB (0.0)
            '     o1.setup()
            018AD 00025       00             ANCHOR
            018AE 00026       08 01 00       CALL_OBJ_SUB (1.0)
            '     o2.setup()
            018B1 00029       00             ANCHOR
            018B2 0002A       08 02 00       CALL_OBJ_SUB (2.0)
            018B5 0002D       04             RETURN
            018B6 0002E       00 00          Padding
            ' Object "text2.spin2" header (var size 4)
            018B8 00000       08 00 00 80    Method setup @ $00008 (0 parameters, 0 returns)
            018BC 00004       0E 00 00 00    End
            ' PUB setup() | a, b
            018C0 00008       02             (stack size)
            '     a := PAR1
            018C1 00009       A2             CONSTANT (1)
            018C2 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     b := PAR2
            018C3 0000B       A3             CONSTANT (2)
            018C4 0000C       F1             VAR_WRITE LONG DBASE+$00001 (short)
            018C5 0000D       04             RETURN
            018C6 0000E       00 00          Padding
            ' Object "text2.spin2" header (var size 4)
            018C8 00000       08 00 00 80    Method setup @ $00008 (0 parameters, 0 returns)
            018CC 00004       0E 00 00 00    End
            ' PUB setup() | a, b
            018D0 00008       02             (stack size)
            '     a := PAR1
            018D1 00009       A4             CONSTANT (3)
            018D2 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     b := PAR2
            018D3 0000B       A3             CONSTANT (2)
            018D4 0000C       F1             VAR_WRITE LONG DBASE+$00001 (short)
            018D5 0000D       04             RETURN
            018D6 0000E       00 00          Padding
            ' Object "text2.spin2" header (var size 4)
            018D8 00000       08 00 00 80    Method setup @ $00008 (0 parameters, 0 returns)
            018DC 00004       0E 00 00 00    End
            ' PUB setup() | a, b
            018E0 00008       02             (stack size)
            '     a := PAR1
            018E1 00009       A5             CONSTANT (4)
            018E2 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     b := PAR2
            018E3 0000B       A6             CONSTANT (VAL5)
            018E4 0000C       F1             VAR_WRITE LONG DBASE+$00001 (short)
            018E5 0000D       04             RETURN
            018E6 0000E       00 00          Padding
            """, compile("main.spin2", sources));
    }

    @Test
    void testPreprocessorDefineInheritance() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            #DEFINE VALUE 2
            
            OBJ
            
                o : "text2"
            
            PUB main() | a
            
                a := o.start()
            
            """);
        sources.put("text2.spin2", """
            PUB start() : r
            
                r := VALUE
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 8)
            01888 00000       18 00 00 00    Object "text2.spin2" @ $00018
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)
            01894 0000C       17 00 00 00    End
            ' PUB main() | a
            01898 00010       01             (stack size)
            '     a := o.start()
            01899 00011       01             ANCHOR (push)
            0189A 00012       08 00 00       CALL_OBJ_SUB (0.0)
            0189D 00015       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0189E 00016       04             RETURN
            0189F 00017       00             Padding
            ' Object "text2.spin2" header (var size 4)
            018A0 00000       08 00 10 80    Method start @ $00008 (0 parameters, 1 returns)
            018A4 00004       0C 00 00 00    End
            ' PUB start() : r
            018A8 00008       00             (stack size)
            '     r := VALUE
            018A9 00009       A3             CONSTANT (2)
            018AA 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            018AB 0000B       04             RETURN
            """, compile("main.spin2", sources));
    }

    @Test
    void testStructureFromChildObject() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            VAR
            
                o.sLine line
            
            OBJ
            
                o : "structs"
            
            PUB start() | o.sPoint pt
            
                pt.x := 1
                pt.y := 2
                line.a.x := 3
                line.a.y := 4
            
            """);
        sources.put("structs.spin2", """
            CON
                STRUCT sPoint(byte x, word y)
                STRUCT sLine(sPoint a, sPoint b, byte color)
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 16)
            01888 00000       24 00 00 00    Object "structs.spin2" @ $00024
            0188C 00004       0C 00 00 00    Variables @ $0000C
            01890 00008       10 00 00 80    Method start @ $00010 (0 parameters, 0 returns)
            01894 0000C       22 00 00 00    End
            ' PUB start() | o.sPoint pt
            01898 00010       01             (stack size)
            '     pt.x := 1
            01899 00011       A2             CONSTANT (1)
            0189A 00012       51 00 1D       MEM_WRITE BYTE DBASE+$00000
            '     pt.y := 2
            0189D 00015       A3             CONSTANT (2)
            0189E 00016       57 01 1D       MEM_WRITE WORD DBASE+$00001
            '     line.a.x := 3
            018A1 00019       A4             CONSTANT (3)
            018A2 0001A       50 04 1D       MEM_WRITE BYTE VBASE+$00004
            '     line.a.y := 4
            018A5 0001D       A5             CONSTANT (4)
            018A6 0001E       56 05 1D       MEM_WRITE WORD VBASE+$00005
            018A9 00021       04             RETURN
            018AA 00022       00 00          Padding
            ' Object "structs.spin2" header (var size 4)
            """, compile("main.spin2", sources));
    }

    @Test
    void testCopyStructureFromChildObject() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            CON
            
                sPoint = o.sPoint
                STRUCT sLine = o.sLine
            
            VAR
            
                sLine line
            
            OBJ
            
                o : "structs"
            
            PUB start() | sPoint pt
            
                pt.x := 1
                pt.y := 2
                line.a.x := 3
                line.a.y := 4
            
            """);
        sources.put("structs.spin2", """
            CON
                STRUCT sPoint(byte x, word y)
                STRUCT sLine(sPoint a, sPoint b, byte color)
            
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 16)
            01888 00000       24 00 00 00    Object "structs.spin2" @ $00024
            0188C 00004       0C 00 00 00    Variables @ $0000C
            01890 00008       10 00 00 80    Method start @ $00010 (0 parameters, 0 returns)
            01894 0000C       22 00 00 00    End
            ' PUB start() | sPoint pt
            01898 00010       01             (stack size)
            '     pt.x := 1
            01899 00011       A2             CONSTANT (1)
            0189A 00012       51 00 1D       MEM_WRITE BYTE DBASE+$00000
            '     pt.y := 2
            0189D 00015       A3             CONSTANT (2)
            0189E 00016       57 01 1D       MEM_WRITE WORD DBASE+$00001
            '     line.a.x := 3
            018A1 00019       A4             CONSTANT (3)
            018A2 0001A       50 04 1D       MEM_WRITE BYTE VBASE+$00004
            '     line.a.y := 4
            018A5 0001D       A5             CONSTANT (4)
            018A6 0001E       56 05 1D       MEM_WRITE WORD VBASE+$00005
            018A9 00021       04             RETURN
            018AA 00022       00 00          Padding
            ' Object "structs.spin2" header (var size 4)
            """, compile("main.spin2", sources));
    }

    @Test
    void testChildObjectSizeof() throws Exception {
        Map<String, String> sources = new HashMap<>();
        sources.put("main.spin2", """
            OBJ
                o : "text2"
            
            PUB start() | a
                a := sizeof(o.sPoint)
                a := sizeof(o.sLine)
                a := sizeof(o.sLine.a)
            
            DAT
                    mov     ptr, #sizeof(o.sLine)
                    mov     ptr, #sizeof(o.sLine.a)
            
            ptr     long    0
            """);
        sources.put("text2.spin2", """
            CON
                struct sPoint(word x, word y)
                struct sLine(sPoint a, sPoint b, byte c)
            """);

        Assertions.assertEquals("""
            ' Object "main.spin2" header (var size 8)
            01888 00000       24 00 00 00    Object "text2.spin2" @ $00024
            0188C 00004       04 00 00 00    Variables @ $00004
            01890 00008       1C 00 00 80    Method start @ $0001C (0 parameters, 0 returns)
            01894 0000C       24 00 00 00    End
            01898 00010 00000 09 10 04 F6                        mov     ptr, #sizeof(o.sLine)
            0189C 00014 00004 04 10 04 F6                        mov     ptr, #sizeof(o.sLine.a)
            018A0 00018 00008 00 00 00 00    ptr                 long    0
            ' PUB start() | a
            018A4 0001C       01             (stack size)
            '     a := sizeof(o.sPoint)
            018A5 0001D       A5             CONSTANT (4)
            018A6 0001E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := sizeof(o.sLine)
            018A7 0001F       AA             CONSTANT (9)
            018A8 00020       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '     a := sizeof(o.sLine.a)
            018A9 00021       A5             CONSTANT (4)
            018AA 00022       F0             VAR_WRITE LONG DBASE+$00000 (short)
            018AB 00023       04             RETURN
            ' Object "text2.spin2" header (var size 4)
            """, compile("main.spin2", sources));
    }

    String compile(String rootFile, Map<String, String> sources) throws Exception {
        return compile(rootFile, sources, false, false);
    }

    String compile(String rootFile, Map<String, String> sources, boolean removeUnused, boolean debugEnabled) throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();
        compiler.setSourceProvider(new SourceProvider() {

            @Override
            public File getFile(String name) {
                if (sources.containsKey(name)) {
                    return new File(name);
                }
                return null;
            }

            @Override
            public String getSource(File file) {
                return sources.get(file.getName());
            }

        });
        compiler.setRemoveUnusedMethods(removeUnused);
        compiler.setDebugEnabled(debugEnabled);
        Spin2Object obj = compiler.compile(new File(rootFile), sources.get(rootFile));

        for (CompilerException msg : compiler.getMessages()) {
            if (msg.type == CompilerException.ERROR) {
                throw msg;
            }
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateListing(new PrintStream(os));

        return os.toString().replaceAll("\\r\\n", "\n");
    }

}
