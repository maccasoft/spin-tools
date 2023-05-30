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
import com.maccasoft.propeller.model.Parser;
import com.maccasoft.propeller.model.SourceProvider;
import com.maccasoft.propeller.spin1.Spin1Object;

class Spin1CCompilerTest {

    @Test
    void testObjectLink() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.c", ""
            + "text2 o;\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "    int a = 1;\n"
            + "}\n"
            + "");
        sources.put("text2.spin", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       10 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       01             Object count\n"
            + "00004 00004       0C 00 04 00    Function main @ $000C (local size 4)\n"
            + "00008 00008       10 00 00 00    Object \"text2.spin\" @ $0010 (variables @ $0000)\n"
            + "' void main() {\n"
            + "'     int a = 1;\n"
            + "0000C 0000C       36             CONSTANT (1)\n"
            + "0000D 0000D       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "' }\n"
            + "0000E 0000E       32             RETURN\n"
            + "0000F 0000F       00             Padding\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
            + "00010 00000       10 00          Object size\n"
            + "00012 00002       02             Method count + 1\n"
            + "00013 00003       00             Object count\n"
            + "00014 00004       08 00 04 00    Function start @ $0008 (local size 4)\n"
            + "' PUB start(a, b) | c\n"
            + "'     c := a + b\n"
            + "00018 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00019 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0001A 0000A       EC             ADD\n"
            + "0001B 0000B       6D             VAR_WRITE LONG DBASE+$000C (short)\n"
            + "0001C 0000C       32             RETURN\n"
            + "0001D 0000D       00 00 00       Padding\n"
            + "", compile("main.c", sources));
    }

    @Test
    void testObjectConstant() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.c", ""
            + "#include \"text2\"\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "    int a = CONST;\n"
            + "}\n"
            + "");
        sources.put("text2.spin", ""
            + "CON\n"
            + "\n"
            + "    CONST = 1\n"
            + "\n"
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       0C 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       00             Object count\n"
            + "00004 00004       08 00 04 00    Function main @ $0008 (local size 4)\n"
            + "' void main() {\n"
            + "'     int a = CONST;\n"
            + "00008 00008       36             CONSTANT (1)\n"
            + "00009 00009       65             VAR_WRITE LONG DBASE+$0004 (short)\n"
            + "' }\n"
            + "0000A 0000A       32             RETURN\n"
            + "0000B 0000B       00             Padding\n"
            + "", compile("main.c", sources, false));
    }

    @Test
    void testObjectMethodCall() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.c", ""
            + "\n"
            + "text2 o;\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "    o.start(1, 2);\n"
            + "}\n"
            + "");
        sources.put("text2.spin", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       01             Object count\n"
            + "00004 00004       0C 00 00 00    Function main @ $000C (local size 0)\n"
            + "00008 00008       14 00 00 00    Object \"text2.spin\" @ $0014 (variables @ $0000)\n"
            + "' void main() {\n"
            + "'     o.start(1, 2);\n"
            + "0000C 0000C       01             ANCHOR\n"
            + "0000D 0000D       36             CONSTANT (1)\n"
            + "0000E 0000E       38 02          CONSTANT (2)\n"
            + "00010 00010       06 02 01       CALL_OBJ_SUB\n"
            + "' }\n"
            + "00013 00013       32             RETURN\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
            + "00014 00000       10 00          Object size\n"
            + "00016 00002       02             Method count + 1\n"
            + "00017 00003       00             Object count\n"
            + "00018 00004       08 00 04 00    Function start @ $0008 (local size 4)\n"
            + "' PUB start(a, b) | c\n"
            + "'     c := a + b\n"
            + "0001C 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0001D 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0001E 0000A       EC             ADD\n"
            + "0001F 0000B       6D             VAR_WRITE LONG DBASE+$000C (short)\n"
            + "00020 0000C       32             RETURN\n"
            + "00021 0000D       00 00 00       Padding\n"
            + "", compile("main.c", sources));
    }

    @Test
    void testObjectInstances() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.c", ""
            + "text2 o1;\n"
            + "text2 o2;\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "    o1.start(1, 2);\n"
            + "    o2.start(3, 4);\n"
            + "}\n"
            + "");
        sources.put("text2.spin", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       20 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       02             Object count\n"
            + "00004 00004       10 00 00 00    Function main @ $0010 (local size 0)\n"
            + "00008 00008       20 00 00 00    Object \"text2.spin\" @ $0020 (variables @ $0000)\n"
            + "0000C 0000C       20 00 00 00    Object \"text2.spin\" @ $0020 (variables @ $0000)\n"
            + "' void main() {\n"
            + "'     o1.start(1, 2);\n"
            + "00010 00010       01             ANCHOR\n"
            + "00011 00011       36             CONSTANT (1)\n"
            + "00012 00012       38 02          CONSTANT (2)\n"
            + "00014 00014       06 02 01       CALL_OBJ_SUB\n"
            + "'     o2.start(3, 4);\n"
            + "00017 00017       01             ANCHOR\n"
            + "00018 00018       38 03          CONSTANT (3)\n"
            + "0001A 0001A       38 04          CONSTANT (4)\n"
            + "0001C 0001C       06 03 01       CALL_OBJ_SUB\n"
            + "' }\n"
            + "0001F 0001F       32             RETURN\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
            + "00020 00000       10 00          Object size\n"
            + "00022 00002       02             Method count + 1\n"
            + "00023 00003       00             Object count\n"
            + "00024 00004       08 00 04 00    Function start @ $0008 (local size 4)\n"
            + "' PUB start(a, b) | c\n"
            + "'     c := a + b\n"
            + "00028 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "00029 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0002A 0000A       EC             ADD\n"
            + "0002B 0000B       6D             VAR_WRITE LONG DBASE+$000C (short)\n"
            + "0002C 0000C       32             RETURN\n"
            + "0002D 0000D       00 00 00       Padding\n"
            + "", compile("main.c", sources));
    }

    @Test
    void testObjectArray() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.c", ""
            + "text2 o[2];\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "\n"
            + "}\n"
            + "");
        sources.put("text2.spin", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       14 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       02             Object count\n"
            + "00004 00004       10 00 00 00    Function main @ $0010 (local size 0)\n"
            + "00008 00008       14 00 00 00    Object \"text2.spin\" @ $0014 (variables @ $0000)\n"
            + "0000C 0000C       14 00 00 00    Object \"text2.spin\" @ $0014 (variables @ $0000)\n"
            + "' void main() {\n"
            + "' }\n"
            + "00010 00010       32             RETURN\n"
            + "00011 00011       00 00 00       Padding\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
            + "00014 00000       10 00          Object size\n"
            + "00016 00002       02             Method count + 1\n"
            + "00017 00003       00             Object count\n"
            + "00018 00004       08 00 04 00    Function start @ $0008 (local size 4)\n"
            + "' PUB start(a, b) | c\n"
            + "'     c := a + b\n"
            + "0001C 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0001D 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0001E 0000A       EC             ADD\n"
            + "0001F 0000B       6D             VAR_WRITE LONG DBASE+$000C (short)\n"
            + "00020 0000C       32             RETURN\n"
            + "00021 0000D       00 00 00       Padding\n"
            + "", compile("main.c", sources));
    }

    @Test
    void testObjectArrayMethodCall() throws Exception {
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("main.c", ""
            + "text2 o[2];\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "    o[0].start(1, 2);\n"
            + "    o[1].start(3, 4);\n"
            + "}\n"
            + "");
        sources.put("text2.spin", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        Assertions.assertEquals(""
            + "' Object header (var size 0)\n"
            + "00000 00000       24 00          Object size\n"
            + "00002 00002       02             Method count + 1\n"
            + "00003 00003       02             Object count\n"
            + "00004 00004       10 00 00 00    Function main @ $0010 (local size 0)\n"
            + "00008 00008       24 00 00 00    Object \"text2.spin\" @ $0024 (variables @ $0000)\n"
            + "0000C 0000C       24 00 00 00    Object \"text2.spin\" @ $0024 (variables @ $0000)\n"
            + "' void main() {\n"
            + "'     o[0].start(1, 2);\n"
            + "00010 00010       01             ANCHOR\n"
            + "00011 00011       36             CONSTANT (1)\n"
            + "00012 00012       38 02          CONSTANT (2)\n"
            + "00014 00014       35             CONSTANT (0)\n"
            + "00015 00015       07 02 01       CALL_OBJ_SUB\n"
            + "'     o[1].start(3, 4);\n"
            + "00018 00018       01             ANCHOR\n"
            + "00019 00019       38 03          CONSTANT (3)\n"
            + "0001B 0001B       38 04          CONSTANT (4)\n"
            + "0001D 0001D       36             CONSTANT (1)\n"
            + "0001E 0001E       07 02 01       CALL_OBJ_SUB\n"
            + "' }\n"
            + "00021 00021       32             RETURN\n"
            + "00022 00022       00 00          Padding\n"
            + "' Object \"text2.spin\" header (var size 0)\n"
            + "00024 00000       10 00          Object size\n"
            + "00026 00002       02             Method count + 1\n"
            + "00027 00003       00             Object count\n"
            + "00028 00004       08 00 04 00    Function start @ $0008 (local size 4)\n"
            + "' PUB start(a, b) | c\n"
            + "'     c := a + b\n"
            + "0002C 00008       64             VAR_READ LONG DBASE+$0004 (short)\n"
            + "0002D 00009       68             VAR_READ LONG DBASE+$0008 (short)\n"
            + "0002E 0000A       EC             ADD\n"
            + "0002F 0000B       6D             VAR_WRITE LONG DBASE+$000C (short)\n"
            + "00030 0000C       32             RETURN\n"
            + "00031 0000D       00 00 00       Padding\n"
            + "", compile("main.c", sources));
    }

    String compile(String rootFile, Map<String, String> sources) throws Exception {
        return compile(rootFile, sources, false);
    }

    String compile(String rootFile, Map<String, String> sources, boolean removeUnused) throws Exception {
        CTokenStream stream = new CTokenStream(sources.get(rootFile));
        CParser subject = new CParser(stream);
        Node root = subject.parse();

        Spin1CCompiler compiler = new Spin1CCompiler();
        compiler.setSourceProvider(new SourceProvider() {

            @Override
            public File getFile(String name) {
                if (sources.containsKey(name)) {
                    return new File(name);
                }
                return null;
            }

            @Override
            public Node getParsedSource(File file) {
                String text = sources.get(file.getName());
                if (text == null) {
                    return null;
                }
                String suffix = file.getName().substring(file.getName().lastIndexOf('.'));
                Parser parser = Parser.getInstance(suffix, text);
                return parser.parse();
            }

        });
        compiler.setRemoveUnusedMethods(removeUnused);
        Spin1Object obj = compiler.compileObject(new File(rootFile), root);

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
