/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.SourceProvider;
import com.maccasoft.propeller.spin2.Spin2Object;
import com.maccasoft.propeller.spin2.Spin2Parser;
import com.maccasoft.propeller.spin2.Spin2TokenStream;

class Spin2CCompilerTest {

    @Test
    void testObjectLink() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.c", ""
            + "#include <text2>\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "\n"
            + "}\n"
            + "");
        sources.put("text2.spin2", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       14 00 00 00    Object \"text2.spin2\" @ $00014\n"
            + "00004 00004       04 00 00 00    Variables @ $00004\n"
            + "00008 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)\n"
            + "0000C 0000C       12 00 00 00    End\n"
            + "' void main() {\n"
            + "00010 00010       00             (stack size)\n"
            + "' }\n"
            + "00011 00011       04             RETURN\n"
            + "00012 00012       00 00          Padding\n"
            + "' Object \"text2.spin2\" header (var size 4)\n"
            + "00014 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)\n"
            + "00018 00004       0E 00 00 00    End\n"
            + "' PUB start(a, b) | c\n"
            + "0001C 00008       01             (stack size)\n"
            + "'     c := a + b\n"
            + "0001D 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "0001E 0000A       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "0001F 0000B       8A             ADD\n"
            + "00020 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)\n"
            + "00021 0000D       04             RETURN\n"
            + "00022 0000E       00 00          Padding\n"
            + "", compile("main.c", sources));
    }

    @Test
    void testObjectMethodCall() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.c", ""
            + "#include <text2>\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "    text2.start(1, 2);\n"
            + "}\n"
            + "");
        sources.put("text2.spin2", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       18 00 00 00    Object \"text2.spin2\" @ $00018\n"
            + "00004 00004       04 00 00 00    Variables @ $00004\n"
            + "00008 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)\n"
            + "0000C 0000C       18 00 00 00    End\n"
            + "' void main() {\n"
            + "00010 00010       00             (stack size)\n"
            + "'     text2.start(1, 2);\n"
            + "00011 00011       00             ANCHOR\n"
            + "00012 00012       A2             CONSTANT (1)\n"
            + "00013 00013       A3             CONSTANT (2)\n"
            + "00014 00014       08 00 00       CALL_OBJ_SUB (0.0)\n"
            + "' }\n"
            + "00017 00017       04             RETURN\n"
            + "' Object \"text2.spin2\" header (var size 4)\n"
            + "00018 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)\n"
            + "0001C 00004       0E 00 00 00    End\n"
            + "' PUB start(a, b) | c\n"
            + "00020 00008       01             (stack size)\n"
            + "'     c := a + b\n"
            + "00021 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00022 0000A       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00023 0000B       8A             ADD\n"
            + "00024 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)\n"
            + "00025 0000D       04             RETURN\n"
            + "00026 0000E       00 00          Padding\n"
            + "", compile("main.c", sources));
    }

    @Test
    void testObjectInclude() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.c", ""
            + "#include \"text2\"\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "\n"
            + "}\n"
            + "");
        sources.put("text2.spin2", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       0A 00 00 00    End\n"
            + "' void main() {\n"
            + "00008 00008       00             (stack size)\n"
            + "' }\n"
            + "00009 00009       04             RETURN\n"
            + "0000A 0000A       00 00          Padding\n"
            + "' Object \"text2.spin2\" header (var size 4)\n"
            + "0000C 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)\n"
            + "00010 00004       0E 00 00 00    End\n"
            + "' PUB start(a, b) | c\n"
            + "00014 00008       01             (stack size)\n"
            + "'     c := a + b\n"
            + "00015 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00016 0000A       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00017 0000B       8A             ADD\n"
            + "00018 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)\n"
            + "00019 0000D       04             RETURN\n"
            + "0001A 0000E       00 00          Padding\n"
            + "", compile("main.c", sources, false, false));
    }

    @Test
    void testIncludedObjectMethodCall() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.c", ""
            + "#include \"text2\"\n"
            + "\n"
            + "text2 o;\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "    o.start(1, 2);\n"
            + "}\n"
            + "");
        sources.put("text2.spin2", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       18 00 00 00    Object \"text2.spin2\" @ $00018\n"
            + "00004 00004       04 00 00 00    Variables @ $00004\n"
            + "00008 00008       10 00 00 80    Method main @ $00010 (0 parameters, 0 returns)\n"
            + "0000C 0000C       18 00 00 00    End\n"
            + "' void main() {\n"
            + "00010 00010       00             (stack size)\n"
            + "'     o.start(1, 2);\n"
            + "00011 00011       00             ANCHOR\n"
            + "00012 00012       A2             CONSTANT (1)\n"
            + "00013 00013       A3             CONSTANT (2)\n"
            + "00014 00014       08 00 00       CALL_OBJ_SUB (0.0)\n"
            + "' }\n"
            + "00017 00017       04             RETURN\n"
            + "' Object \"text2.spin2\" header (var size 4)\n"
            + "00018 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)\n"
            + "0001C 00004       0E 00 00 00    End\n"
            + "' PUB start(a, b) | c\n"
            + "00020 00008       01             (stack size)\n"
            + "'     c := a + b\n"
            + "00021 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00022 0000A       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00023 0000B       8A             ADD\n"
            + "00024 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)\n"
            + "00025 0000D       04             RETURN\n"
            + "00026 0000E       00 00          Padding\n"
            + "", compile("main.c", sources));
    }

    @Test
    void testIncludedObjectInstancesMethodCall() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.c", ""
            + "#include \"text2\"\n"
            + "\n"
            + "text2 o1;\n"
            + "text2 o2;\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "    o1.start(1, 2);\n"
            + "    o2.start(3, 4);\n"
            + "}\n"
            + "");
        sources.put("text2.spin2", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       28 00 00 00    Object \"text2.spin2\" @ $00028\n"
            + "00004 00004       04 00 00 00    Variables @ $00004\n"
            + "00008 00008       28 00 00 00    Object \"text2.spin2\" @ $00028\n"
            + "0000C 0000C       08 00 00 00    Variables @ $00008\n"
            + "00010 00010       18 00 00 80    Method main @ $00018 (0 parameters, 0 returns)\n"
            + "00014 00014       26 00 00 00    End\n"
            + "' void main() {\n"
            + "00018 00018       00             (stack size)\n"
            + "'     o1.start(1, 2);\n"
            + "00019 00019       00             ANCHOR\n"
            + "0001A 0001A       A2             CONSTANT (1)\n"
            + "0001B 0001B       A3             CONSTANT (2)\n"
            + "0001C 0001C       08 00 00       CALL_OBJ_SUB (0.0)\n"
            + "'     o2.start(3, 4);\n"
            + "0001F 0001F       00             ANCHOR\n"
            + "00020 00020       A4             CONSTANT (3)\n"
            + "00021 00021       A5             CONSTANT (4)\n"
            + "00022 00022       08 01 00       CALL_OBJ_SUB (1.0)\n"
            + "' }\n"
            + "00025 00025       04             RETURN\n"
            + "00026 00026       00 00          Padding\n"
            + "' Object \"text2.spin2\" header (var size 4)\n"
            + "00028 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)\n"
            + "0002C 00004       0E 00 00 00    End\n"
            + "' PUB start(a, b) | c\n"
            + "00030 00008       01             (stack size)\n"
            + "'     c := a + b\n"
            + "00031 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00032 0000A       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00033 0000B       8A             ADD\n"
            + "00034 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)\n"
            + "00035 0000D       04             RETURN\n"
            + "00036 0000E       00 00          Padding\n"
            + "", compile("main.c", sources, true, false));
    }

    @Test
    void testIncludedObjectArrayMethodCall() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.c", ""
            + "#include \"text2\"\n"
            + "\n"
            + "text2 o[2];\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "    o[0].start(1, 2);\n"
            + "    o[1].start(3, 4);\n"
            + "}\n"
            + "");
        sources.put("text2.spin2", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header\n"
            + "00000 00000       28 00 00 00    Object \"text2.spin2\" @ $00028\n"
            + "00004 00004       04 00 00 00    Variables @ $00004\n"
            + "00008 00008       28 00 00 00    Object \"text2.spin2\" @ $00028\n"
            + "0000C 0000C       08 00 00 00    Variables @ $00008\n"
            + "00010 00010       18 00 00 80    Method main @ $00018 (0 parameters, 0 returns)\n"
            + "00014 00014       28 00 00 00    End\n"
            + "' void main() {\n"
            + "00018 00018       00             (stack size)\n"
            + "'     o[0].start(1, 2);\n"
            + "00019 00019       00             ANCHOR\n"
            + "0001A 0001A       A2             CONSTANT (1)\n"
            + "0001B 0001B       A3             CONSTANT (2)\n"
            + "0001C 0001C       A1             CONSTANT (0)\n"
            + "0001D 0001D       09 00 00       CALL_OBJ_SUB (0.0) (indexed)\n"
            + "'     o[1].start(3, 4);\n"
            + "00020 00020       00             ANCHOR\n"
            + "00021 00021       A4             CONSTANT (3)\n"
            + "00022 00022       A5             CONSTANT (4)\n"
            + "00023 00023       A2             CONSTANT (1)\n"
            + "00024 00024       09 00 00       CALL_OBJ_SUB (0.0) (indexed)\n"
            + "' }\n"
            + "00027 00027       04             RETURN\n"
            + "' Object \"text2.spin2\" header (var size 4)\n"
            + "00028 00000       08 00 00 82    Method start @ $00008 (2 parameters, 0 returns)\n"
            + "0002C 00004       0E 00 00 00    End\n"
            + "' PUB start(a, b) | c\n"
            + "00030 00008       01             (stack size)\n"
            + "'     c := a + b\n"
            + "00031 00009       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00032 0000A       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00033 0000B       8A             ADD\n"
            + "00034 0000C       F2             VAR_WRITE LONG DBASE+$00002 (short)\n"
            + "00035 0000D       04             RETURN\n"
            + "00036 0000E       00 00          Padding\n"
            + "", compile("main.c", sources, true, false));
    }

    String compile(String rootFile, Map<String, String> sources) throws Exception {
        return compile(rootFile, sources, false, false);
    }

    String compile(String rootFile, Map<String, String> sources, boolean removeUnused, boolean debugEnabled) throws Exception {
        CTokenStream stream = new CTokenStream(sources.get(rootFile));
        CParser subject = new CParser(stream);
        Node root = subject.parse();

        Spin2CCompiler compiler = new Spin2CCompiler();
        compiler.addSourceProvider(new SourceProvider() {

            @Override
            public File getFile(String name) {
                if (sources.containsKey(name)) {
                    return new File(name);
                }
                return null;
            }

            @Override
            public Node getParsedSource(String name) {
                String text = sources.get(name);
                if (text == null) {
                    return null;
                }
                Spin2TokenStream stream = new Spin2TokenStream(text);
                Spin2Parser subject = new Spin2Parser(stream);
                return subject.parse();
            }

        });
        compiler.setRemoveUnusedMethods(removeUnused);
        compiler.setDebugEnabled(debugEnabled);
        Spin2Object obj = compiler.compileObject(new File(rootFile), root);

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
