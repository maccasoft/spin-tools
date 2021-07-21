/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;

class Spin2CompilerFunctionalTest {

    @Test
    void testCompileBlinkSpin() throws Exception {
        String text = ""
            + "CON\n"
            + "    _clkfreq = 160_000_000\n"
            + "\n"
            + "PUB main() | ct\n"
            + "\n"
            + "    ct := getct()                   ' get current timer\n"
            + "    repeat\n"
            + "        pint(56)                    ' toggle pin 56\n"
            + "        waitct(ct += _clkfreq / 2)  ' wait half second\n"
            + "";

        byte[] expected = getResource("blink.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testInterpreter() throws Exception {
        String text = getResourceAsString("Spin2_interpreter.spin2");

        byte[] expected = getResource("Spin2_interpreter.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testApple1VGA() throws Exception {
        String text = getResourceAsString("m6502_apple1_vga.spin2");

        byte[] expected = getResource("m6502_apple1_vga.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testApple1CVBS() throws Exception {
        String text = getResourceAsString("m6502_apple1_cvbs.spin2");

        byte[] expected = getResource("m6502_apple1_cvbs.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testNstr() throws Exception {
        String text = getResourceAsString("jm_nstr.spin2");

        byte[] expected = getResource("jm_nstr.binary");
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

    byte[] getResource(String name) throws Exception {
        InputStream is = getClass().getResourceAsStream(name);
        try {
            byte[] b = new byte[is.available()];
            is.read(b);
            return b;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                is.close();
            } catch (Exception e) {

            }
        }
    }

    class Spin2CompilerAdapter extends Spin2Compiler {

        Map<String, String> sources;

        public Spin2CompilerAdapter(Map<String, String> sources) {
            this.sources = sources;
        }

        @Override
        protected Spin2Object getObject(String fileName) {
            String text = getObjectSource(fileName);
            if (text == null) {
                throw new RuntimeException("file " + fileName + " not found");
            }
            Spin2TokenStream stream = new Spin2TokenStream(text);
            Spin2Parser subject = new Spin2Parser(stream);
            Node root = subject.parse();

            Spin2CompilerAdapter compiler = new Spin2CompilerAdapter(sources);
            return compiler.compileObject(root);
        }

        protected String getObjectSource(String fileName) {
            return sources.get(fileName);
        }

    }

    void compileAndCompare(String text, Map<String, String> sources, byte[] expected) throws Exception {
        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser subject = new Spin2Parser(stream);
        Node root = subject.parse();

        Spin2CompilerAdapter compiler = new Spin2CompilerAdapter(sources);
        Spin2Object obj = compiler.compile(root);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateBinary(os);

        os = new ByteArrayOutputStream();
        obj.generateListing(new PrintStream(os));
        String actualListing = os.toString().replaceAll("\\r\\n", "\n");

        String expectedListing;
        if (expected != null) {
            obj.setBytes(expected, obj.getInterpreter() != null ? obj.getInterpreter().getPBase() : 0x0000);

            os = new ByteArrayOutputStream();
            obj.generateListing(new PrintStream(os));
            expectedListing = os.toString().replaceAll("\\r\\n", "\n");
        }
        else {
            expectedListing = "";
        }

        Assertions.assertEquals(expectedListing, actualListing);
    }

}
