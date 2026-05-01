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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.CompilerException;

class Spin2OffsetOfTest {

    @Test
    void testSimpleOffsets() throws Exception {
        String text = """
            CON
              STRUCT point_t(LONG x, LONG y, LONG z)
            
            PUB main() | r
            
              r := OFFSETOF(point_t.x)       ' expect 0
              r := OFFSETOF(point_t.y)       ' expect 4
              r := OFFSETOF(point_t.z)       ' expect 8
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       10 00 00 00    End
            ' PUB main() | r
            00008 00008       01             (stack size)
            '   r := OFFSETOF(point_t.x)
            00009 00009       A1             CONSTANT (0)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(point_t.y)
            0000B 0000B       A5             CONSTANT (4)
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(point_t.z)
            0000D 0000D       A9             CONSTANT (8)
            0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0000F 0000F       04             RETURN
            """, compile(text));
    }

    @Test
    void testMixedOffsets() throws Exception {
        String text = """
            CON
              STRUCT mixed_t(BYTE flags, WORD count, LONG value, BYTE status)
            
            PUB main() | r
            
              r := OFFSETOF(mixed_t.flags)    ' expect 0
              r := OFFSETOF(mixed_t.count)    ' expect 1
              r := OFFSETOF(mixed_t.value)    ' expect 3
              r := OFFSETOF(mixed_t.status)   ' expect 7
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       12 00 00 00    End
            ' PUB main() | r
            00008 00008       01             (stack size)
            '   r := OFFSETOF(mixed_t.flags)
            00009 00009       A1             CONSTANT (0)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(mixed_t.count)
            0000B 0000B       A2             CONSTANT (1)
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(mixed_t.value)
            0000D 0000D       A4             CONSTANT (3)
            0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(mixed_t.status)
            0000F 0000F       A8             CONSTANT (7)
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00011 00011       04             RETURN
            00012 00012       00 00          Padding
            """, compile(text));
    }

    @Test
    void testNestedOffsets() throws Exception {
        String text = """
            CON
              STRUCT point_t(LONG x, LONG y, LONG z)
              STRUCT line_t(point_t start, point_t finish)
            
            PUB main() | r
            
              r := OFFSETOF(line_t.start)         ' expect 0
              r := OFFSETOF(line_t.finish)        ' expect 12
              r := OFFSETOF(line_t.start.x)       ' expect 0
              r := OFFSETOF(line_t.start.y)       ' expect 4
              r := OFFSETOF(line_t.start.z)       ' expect 8
              r := OFFSETOF(line_t.finish.x)      ' expect 12
              r := OFFSETOF(line_t.finish.y)      ' expect 16
              r := OFFSETOF(line_t.finish.z)      ' expect 20
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1C 00 00 00    End
            ' PUB main() | r
            00008 00008       01             (stack size)
            '   r := OFFSETOF(line_t.start)
            00009 00009       A1             CONSTANT (0)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(line_t.finish)
            0000B 0000B       AD             CONSTANT (12)
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(line_t.start.x)
            0000D 0000D       A1             CONSTANT (0)
            0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(line_t.start.y)
            0000F 0000F       A5             CONSTANT (4)
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(line_t.start.z)
            00011 00011       A9             CONSTANT (8)
            00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(line_t.finish.x)
            00013 00013       AD             CONSTANT (12)
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(line_t.finish.y)
            00015 00015       42 10          CONSTANT (16)
            00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(line_t.finish.z)
            00018 00018       42 14          CONSTANT (20)
            0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001B 0001B       04             RETURN
            """, compile(text));
    }

    @Test
    void testArrayIndexedOffsets() throws Exception {
        String text = """
            CON
              STRUCT point_t(LONG x, LONG y, LONG z)
              STRUCT line_t(point_t start, point_t finish)
              STRUCT triangle_t(line_t sides[3])
            
            PUB main() | r
            
              r := OFFSETOF(triangle_t.sides[0])            ' expect 0
              r := OFFSETOF(triangle_t.sides[1])            ' expect 24
              r := OFFSETOF(triangle_t.sides[2])            ' expect 48
            
              ' Array index with nested member access
              r := OFFSETOF(triangle_t.sides[0].start.x)   ' expect 0
              r := OFFSETOF(triangle_t.sides[0].finish.x)  ' expect 12
              r := OFFSETOF(triangle_t.sides[1].start.x)   ' expect 24
              r := OFFSETOF(triangle_t.sides[1].finish.y)  ' expect 40
              r := OFFSETOF(triangle_t.sides[2].finish.z)  ' expect 68
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1F 00 00 00    End
            ' PUB main() | r
            00008 00008       01             (stack size)
            '   r := OFFSETOF(triangle_t.sides[0])
            00009 00009       A1             CONSTANT (0)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(triangle_t.sides[1])
            0000B 0000B       42 18          CONSTANT (24)
            0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(triangle_t.sides[2])
            0000E 0000E       42 30          CONSTANT (48)
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(triangle_t.sides[0].start.x)
            00011 00011       A1             CONSTANT (0)
            00012 00012       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(triangle_t.sides[0].finish.x)
            00013 00013       AD             CONSTANT (12)
            00014 00014       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(triangle_t.sides[1].start.x)
            00015 00015       42 18          CONSTANT (24)
            00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(triangle_t.sides[1].finish.y)
            00018 00018       42 28          CONSTANT (40)
            0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(triangle_t.sides[2].finish.z)
            0001B 0001B       42 44          CONSTANT (68)
            0001D 0001D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001E 0001E       04             RETURN
            0001F 0001F       00             Padding
            """, compile(text));
    }

    @Test
    void testDeepNestedOffsets() throws Exception {
        String text = """
            CON
              STRUCT point_t(LONG x, LONG y, LONG z)
              STRUCT box_t(point_t min, point_t max)
              STRUCT scene_t(box_t bounds, LONG id)
            
            PUB main() | r
            
              r := OFFSETOF(scene_t.bounds)           ' expect 0
              r := OFFSETOF(scene_t.id)               ' expect 24
              r := OFFSETOF(scene_t.bounds.min)       ' expect 0
              r := OFFSETOF(scene_t.bounds.max)       ' expect 12
              r := OFFSETOF(scene_t.bounds.min.x)    ' expect 0
              r := OFFSETOF(scene_t.bounds.min.z)    ' expect 8
              r := OFFSETOF(scene_t.bounds.max.x)    ' expect 12
              r := OFFSETOF(scene_t.bounds.max.z)    ' expect 20
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       1C 00 00 00    End
            ' PUB main() | r
            00008 00008       01             (stack size)
            '   r := OFFSETOF(scene_t.bounds)
            00009 00009       A1             CONSTANT (0)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(scene_t.id)
            0000B 0000B       42 18          CONSTANT (24)
            0000D 0000D       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(scene_t.bounds.min)
            0000E 0000E       A1             CONSTANT (0)
            0000F 0000F       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(scene_t.bounds.max)
            00010 00010       AD             CONSTANT (12)
            00011 00011       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(scene_t.bounds.min.x)
            00012 00012       A1             CONSTANT (0)
            00013 00013       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(scene_t.bounds.min.z)
            00014 00014       A9             CONSTANT (8)
            00015 00015       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(scene_t.bounds.max.x)
            00016 00016       AD             CONSTANT (12)
            00017 00017       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(scene_t.bounds.max.z)
            00018 00018       42 14          CONSTANT (20)
            0001A 0001A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            0001B 0001B       04             RETURN
            """, compile(text));
    }

    @Test
    void testStructWithoutMembers() throws Exception {
        String text = """
            CON
              STRUCT point_t(LONG x, LONG y, LONG z)
              STRUCT mixed_t(BYTE flags, WORD count, LONG value, BYTE status)
              STRUCT line_t(point_t start, point_t finish)
              STRUCT box_t(point_t min, point_t max)
              STRUCT scene_t(box_t bounds, LONG id)
            
            PUB main() | r
            
              r := OFFSETOF(point_t)          ' expect 0
              r := OFFSETOF(mixed_t)          ' expect 0
              r := OFFSETOF(line_t)           ' expect 0
              r := OFFSETOF(scene_t)          ' expect 0
            """;

        Assertions.assertEquals("""
            ' Object "test.spin2" header (var size 4)
            00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)
            00004 00004       12 00 00 00    End
            ' PUB main() | r
            00008 00008       01             (stack size)
            '   r := OFFSETOF(point_t)
            00009 00009       A1             CONSTANT (0)
            0000A 0000A       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(mixed_t)
            0000B 0000B       A1             CONSTANT (0)
            0000C 0000C       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(line_t)
            0000D 0000D       A1             CONSTANT (0)
            0000E 0000E       F0             VAR_WRITE LONG DBASE+$00000 (short)
            '   r := OFFSETOF(scene_t)
            0000F 0000F       A1             CONSTANT (0)
            00010 00010       F0             VAR_WRITE LONG DBASE+$00000 (short)
            00011 00011       04             RETURN
            00012 00012       00 00          Padding
            """, compile(text));
    }

    String compile(String text) throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();
        Spin2ObjectCompiler objectCompiler = new Spin2ObjectCompiler(compiler, new File("test.spin2"));
        Spin2Object obj = objectCompiler.compileObject(text);

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
