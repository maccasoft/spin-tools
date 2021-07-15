/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin1.Spin1Compiler;
import com.maccasoft.propeller.spin1.Spin1Object;
import com.maccasoft.propeller.spin1.Spin1Parser;
import com.maccasoft.propeller.spin1.Spin1TokenStream;

class Spin1CompilerFunctionalTest {

    private static File systemTmpdir = new File(System.getProperty("java.io.tmpdir"));
    private static final String prefix = "spin_tools_tests_";
    private static final SecureRandom random = new SecureRandom();
    private static File tmpdir;

    @BeforeAll
    static void setUp() throws Exception {
        long n = random.nextLong();
        if (n == Long.MIN_VALUE) {
            n = 0; // corner case
        }
        else {
            n = Math.abs(n);
        }

        String name = prefix + Long.toString(n);
        tmpdir = new File(systemTmpdir, name);
        tmpdir.mkdir();
        if (!name.equals(tmpdir.getName())) {
            if (System.getSecurityManager() != null) {
                throw new IOException("Unable to create temporary file");
            }
            else {
                throw new IOException("Unable to create temporary file, " + tmpdir);
            }
        }

        Spin1Compiler.OPENSPIN_COMPATIBILITY = true;
    }

    @AfterAll
    static void cleanUp() throws Exception {
        Spin1Compiler.OPENSPIN_COMPATIBILITY = false;

        tmpdir.delete();
    }

    @AfterEach
    void clean() {
        if (!tmpdir.exists() || !tmpdir.getName().startsWith(prefix) || tmpdir.equals(systemTmpdir)) {
            return;
        }

        String[] files = tmpdir.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return !"..".equals(name) && !".".equals(name);
            }
        });
        for (int i = 0; i < files.length; i++) {
            File f = new File(tmpdir, files[i]);
            f.delete();
        }
    }

    @Test
    void testEmptyMethod() throws Exception {
        String text = ""
            + "PUB main\n"
            + "\n"
            + "";

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testLocalVarAssignment() throws Exception {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "";

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testGlobalVarAssignment() throws Exception {
        String text = ""
            + "VAR\n"
            + "\n"
            + "    long a\n"
            + "\n"
            + "PUB main\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "";

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testExpressionAssignment() throws Exception {
        String text = ""
            + "PUB main | a, b\n"
            + "\n"
            + "    a := 1 + b * 3\n"
            + "\n"
            + "";

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testIfConditional() throws Exception {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    if a == 1\n"
            + "        a := 2\n"
            + "\n"
            + "";

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testIfConditionExpression() throws Exception {
        String text = ""
            + "PUB main | a, b\n"
            + "\n"
            + "    if (a := b) == 0\n"
            + "        a := 1\n"
            + "\n"
            + "";

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testRepeat() throws Exception {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    repeat\n"
            + "        a := 1\n"
            + "";

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testRepeatCount() throws Exception {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    repeat 10\n"
            + "        a := 1\n"
            + "";

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testRepeatVarCounter() throws Exception {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    repeat a\n"
            + "        a := 1\n"
            + "";

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testRepeatQuit() throws Exception {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    repeat\n"
            + "        if a == 1\n"
            + "            quit\n"
            + "        a := 1\n"
            + "";

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testRepeatNext() throws Exception {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    repeat\n"
            + "        if a == 1\n"
            + "            next\n"
            + "        a := 1\n"
            + "";

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testRepeatWhile() throws Exception {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    repeat while a < 1\n"
            + "        a := 1\n"
            + "";

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testRepeatUntil() throws Exception {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    repeat until a < 1\n"
            + "        a := 1\n"
            + "";

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testRepeatWhileQuit() throws Exception {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    repeat while a < 1\n"
            + "        if a == 1\n"
            + "            quit\n"
            + "        a := 1\n"
            + "";

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testRepeatPostWhile() throws Exception {
        String text = ""
            + "PUB main | a\n"
            + "\n"
            + "    repeat\n"
            + "        a := 1\n"
            + "    while a < 1\n"
            + "";

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testMethodCall() throws Exception {
        String text = ""
            + "PUB main\n"
            + "\n"
            + "    function1(1, 2, 3)\n"
            + "    \\function2\n"
            + "\n"
            + "PUB function1(a, b, c)\n"
            + "\n"
            + "PUB function2\n"
            + "\n"
            + "";

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testMethodReturn() throws Exception {
        String text = ""
            + "PUB main\n"
            + "\n"
            + "    return 1\n"
            + "\n"
            + "PUB function1 | a\n"
            + "\n"
            + "    return a\n"
            + "\n"
            + "PUB function2 : b\n"
            + "\n"
            + "    b := 1\n"
            + "    return\n"
            + "\n"
            + "";

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testObjectLink() throws Exception {
        String text = ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main | a\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "";

        Map<String, String> sources = new HashMap<String, String>();
        sources.put("text2", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        byte[] expected = compileReference(text, sources);
        compileAndCompare(text, sources, expected);
    }

    @Test
    void testObjectVariablesLink() throws Exception {
        String text = ""
            + "VAR\n"
            + "\n"
            + "    long b\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    o : \"text1\"\n"
            + "    p : \"text2\"\n"
            + "\n"
            + "PUB main | a\n"
            + "\n"
            + "    a := 1\n"
            + "\n"
            + "";

        Map<String, String> sources = new HashMap<String, String>();
        sources.put("text1", ""
            + "VAR\n"
            + "\n"
            + "    long d, e\n"
            + "\n"
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");
        sources.put("text2", ""
            + "VAR\n"
            + "\n"
            + "    long d, e, f, g\n"
            + "\n"
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    d := e + f\n"
            + "\n"
            + "");

        byte[] expected = compileReference(text, sources);
        compileAndCompare(text, sources, expected);
    }

    @Test
    void testObjectMethodCall() throws Exception {
        String text = ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main | a\n"
            + "\n"
            + "    o.start(1, 2)\n"
            + "\n"
            + "";

        Map<String, String> sources = new HashMap<String, String>();
        sources.put("text2", ""
            + "PUB start(a, b) | c\n"
            + "\n"
            + "    c := a + b\n"
            + "\n"
            + "");

        byte[] expected = compileReference(text, sources);
        compileAndCompare(text, sources, expected);
    }

    @Test
    void testObjectConstant() throws Exception {
        String text = ""
            + "OBJ\n"
            + "\n"
            + "    o : \"text2\"\n"
            + "\n"
            + "PUB main | a\n"
            + "\n"
            + "    a := o#VALUE\n"
            + "\n"
            + "";

        Map<String, String> sources = new HashMap<String, String>();
        sources.put("text2", ""
            + "CON\n"
            + "\n"
            + "    VALUE = 1\n"
            + "\n"
            + "PUB main | a\n"
            + "\n"
            + "");

        byte[] expected = compileReference(text, sources);
        compileAndCompare(text, sources, expected);
    }

    @Test
    void testBlink() throws Exception {
        String text = ""
            + "CON\n"
            + "\n"
            + "    _XINFREQ = 5_000_000\n"
            + "    _CLKMODE = XTAL1 + PLL16X\n"
            + "\n"
            + "    PIN = 16\n"
            + "\n"
            + "PUB start | ct\n"
            + "\n"
            + "    ' initialization\n"
            + "    DIRA[PIN] := 1\n"
            + "    ct := CNT\n"
            + "\n"
            + "    repeat\n"
            + "        ' loop\n"
            + "        OUTA[PIN] ^= 1\n"
            + "        waitcnt(ct += CLKFREQ / 2)";

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    void compileAndCompare(String text, Map<String, String> sources, byte[] expected) throws Exception {
        Spin1TokenStream stream = new Spin1TokenStream(text);
        Spin1Parser subject = new Spin1Parser(stream);
        Node root = subject.parse();

        Spin1Compiler compiler = new Spin1Compiler() {

            @Override
            protected Spin1Object getObject(String fileName) {
                String text = sources.get(fileName);
                Spin1TokenStream stream = new Spin1TokenStream(text);
                Spin1Parser subject = new Spin1Parser(stream);
                Node root = subject.parse();

                Spin1Compiler compiler = new Spin1Compiler();
                return compiler.compileObject(root);
            }

        };
        Spin1Object obj = compiler.compile(root);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateBinary(os);

        byte[] actual = os.toByteArray();

        os = new ByteArrayOutputStream();
        obj.generateListing(new PrintStream(os));
        String listing = os.toString();

        Assertions.assertArrayEquals(expected, actual, listing);
    }

    byte[] compileReference(String text, Map<String, String> sources) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(tmpdir, "text.spin")));
        writer.write(text);
        writer.close();

        for (Entry<String, String> entry : sources.entrySet()) {
            writer = new BufferedWriter(new FileWriter(new File(tmpdir, entry.getKey() + ".spin")));
            writer.write(entry.getValue());
            writer.close();
        }

        List<String> cmd = new ArrayList<String>();
        cmd.add("/opt/parallax/bin/openspin");
        cmd.add("-b");
        cmd.add("-o");
        cmd.add("text.spin.binary");
        cmd.add("text.spin");

        ProcessBuilder builder = new ProcessBuilder(cmd);
        //builder.inheritIO();
        builder.directory(tmpdir);

        Process p = builder.start();
        p.waitFor();

        File binaryFile = new File(tmpdir, "text.spin.binary");

        byte[] code = new byte[(int) binaryFile.length()];

        FileInputStream is = new FileInputStream(binaryFile);
        is.read(code);
        is.close();

        code[5] = 0;

        return code;
    }

}
