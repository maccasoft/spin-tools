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
import java.io.InputStream;
import java.io.PrintStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;

class Spin1CompilerFunctionalTest {

    private static File systemTmpdir = new File(System.getProperty("java.io.tmpdir"));
    private static final String prefix = "spin_tools_tests_";
    private static final SecureRandom random = new SecureRandom();
    private static File tmpdir;

    @BeforeAll
    static void setUp() throws Exception {
        Spin1Compiler.OPENSPIN_COMPATIBILITY = true;

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
    void testCharType() throws Exception {
        String text = getResourceAsString("char.type.spin");

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testComSerial() throws Exception {
        String text = getResourceAsString("com.serial.spin");

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testComSpi() throws Exception {
        String text = getResourceAsString("com.spi.spin");

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testDisplayTV() throws Exception {
        String text = getResourceAsString("display.tv.spin");

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testDisplayVGA() throws Exception {
        String text = getResourceAsString("display.vga.spin");

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testDisplayVGABitmap() throws Exception {
        String text = getResourceAsString("display.vga.bitmap.512x384.spin");

        byte[] expected = compileReference(text, Collections.emptyMap());
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    String getResourceAsString(String name) throws Exception {
        InputStream is = getClass().getResourceAsStream(name);
        try {
            byte[] b = new byte[is.available()];
            is.read(b);
            return new String(b);
        } finally {
            is.close();
        }
    }

    class Spin1CompilerAdapter extends Spin1Compiler {

        Map<String, String> sources;

        public Spin1CompilerAdapter(Map<String, String> sources) {
            this.sources = sources;
        }

        @Override
        protected Spin1Object getObject(String fileName) {
            String text = getObjectSource(fileName);
            Spin1TokenStream stream = new Spin1TokenStream(text);
            Spin1Parser subject = new Spin1Parser(stream);
            Node root = subject.parse();

            Spin1CompilerAdapter compiler = new Spin1CompilerAdapter(sources);
            return compiler.compileObject(root);
        }

        protected String getObjectSource(String fileName) {
            return sources.get(fileName);
        }

    }

    void compileAndCompare(String text, Map<String, String> sources, byte[] expected) throws Exception {
        Spin1TokenStream stream = new Spin1TokenStream(text);
        Spin1Parser subject = new Spin1Parser(stream);
        Node root = subject.parse();

        Spin1CompilerAdapter compiler = new Spin1CompilerAdapter(sources);
        Spin1Object obj = compiler.compile(root);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateBinary(os);

        byte[] actual = os.toByteArray();

        byte sum = 0;
        for (int i = 0; i < actual.length; i++) {
            sum += actual[i];
        }
        actual[5] = (byte) (0x14 - sum);

        os = new ByteArrayOutputStream();
        obj.generateListing(new PrintStream(os));
        String listing = os.toString();

        expected[5] = actual[5] = 0;
        //Assertions.assertArrayEquals(expected, actual, listing);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expected.length && i < actual.length; i++) {
            if (expected[i] != actual[i]) {
                sb.append(String.format("%04X expected: %02X but was: %02X", i, expected[i] & 0xFF, actual[i] & 0xFF) + "\n");
            }
        }
        if (sb.length() != 0) {
            sb.append(listing);
            System.out.println(sb.toString());
            Assertions.fail(sb.toString());
        }
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

        return code;
    }

}
