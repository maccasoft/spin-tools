/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1.examples;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin1.Spin1Compiler;
import com.maccasoft.propeller.spin1.Spin1Object;
import com.maccasoft.propeller.spin1.Spin1Parser;
import com.maccasoft.propeller.spin1.Spin1TokenStream;

class Spin1ExamplesTest {

    @BeforeAll
    static void setUp() throws Exception {
        Spin1Compiler.OPENSPIN_COMPATIBILITY = true;
    }

    @AfterAll
    static void cleanUp() throws Exception {
        Spin1Compiler.OPENSPIN_COMPATIBILITY = false;
    }

    @Test
    void test_com_serial_DataBlast() throws Exception {
        String text = loadFromFile(new File("examples/P1/com/serial", "DataBlast.spin"));

        byte[] expected = getResource("com/serial/DataBlast.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_com_serial_HelloWorld() throws Exception {
        String text = loadFromFile(new File("examples/P1/com/serial", "HelloWorld.spin"));

        byte[] expected = getResource("com/serial/HelloWorld.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_com_serial_LoopBack() throws Exception {
        String text = loadFromFile(new File("examples/P1/com/serial", "LoopBack.spin"));

        byte[] expected = getResource("com/serial/LoopBack.binary");
        compileAndCompare(text, expected);
    }

    //@Test
    // Fails because of different sequence of same-priority operands
    void test_com_serial_terminal_Demo() throws Exception {
        String text = loadFromFile(new File("examples/P1/com/serial/terminal", "Demo.spin"));

        byte[] expected = getResource("com/serial/terminal/Demo.binary");
        compileAndCompare(text, expected);
    }

    //@Test
    // Fails because of different sequence of same-priority operands
    void test_com_serial_terminal_HelloWorld() throws Exception {
        String text = loadFromFile(new File("examples/P1/com/serial/terminal", "HelloWorld.spin"));

        byte[] expected = getResource("com/serial/terminal/HelloWorld.binary");
        compileAndCompare(text, expected);
    }

    //@Test
    // Fails because of different sequence of same-priority operands
    void test_com_serial_terminal_InputNumbers() throws Exception {
        String text = loadFromFile(new File("examples/P1/com/serial/terminal", "InputNumbers.spin"));

        byte[] expected = getResource("com/serial/terminal/InputNumbers.binary");
        compileAndCompare(text, expected);
    }

    //@Test
    // Fails because of different sequence of same-priority operands
    void test_com_serial_terminal_ReadLine() throws Exception {
        String text = loadFromFile(new File("examples/P1/com/serial/terminal", "ReadLine.spin"));

        byte[] expected = getResource("com/serial/terminal/ReadLine.binary");
        compileAndCompare(text, expected);
    }

    String getResourceAsString(String name) {
        InputStream is = getClass().getResourceAsStream(name);
        try {
            byte[] b = new byte[is.available()];
            is.read(b);
            return new String(b);
        } catch (Exception e) {
            throw new RuntimeException("can't find resource " + name, e);
        } finally {
            try {
                is.close();
            } catch (Exception e) {

            }
        }
    }

    byte[] getResource(String name) {
        InputStream is = getClass().getResourceAsStream(name);
        try {
            byte[] b = new byte[is.available()];
            is.read(b);
            return b;
        } catch (Exception e) {
            throw new RuntimeException("can't find resource " + name, e);
        } finally {
            try {
                is.close();
            } catch (Exception e) {

            }
        }
    }

    String loadFromFile(File file) {
        String line;
        StringBuilder sb = new StringBuilder();

        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                reader.close();
            } catch (Exception e) {
                throw new RuntimeException("error reading file " + file, e);
            }
        }

        return sb.toString();
    }

    class Spin1CompilerAdapter extends Spin1Compiler {

        public Spin1CompilerAdapter() {

        }

        @Override
        protected Spin1Object getObject(String fileName) {
            String text = getObjectSource(fileName);
            if (text == null) {
                throw new RuntimeException("file " + fileName + " not found");
            }
            Spin1TokenStream stream = new Spin1TokenStream(text);
            Spin1Parser subject = new Spin1Parser(stream);
            Node root = subject.parse();

            Spin1CompilerAdapter compiler = new Spin1CompilerAdapter();
            return compiler.compileObject(root);
        }

        protected String getObjectSource(String fileName) {
            fileName += ".spin";
            File file = new File("library/spin1", fileName);
            if (file.exists()) {
                return loadFromFile(file);
            }
            return getResourceAsString(fileName);
        }

    }

    void compileAndCompare(String text, byte[] expected) throws Exception {
        Spin1TokenStream stream = new Spin1TokenStream(text);
        Spin1Parser subject = new Spin1Parser(stream);
        Node root = subject.parse();

        Spin1CompilerAdapter compiler = new Spin1CompilerAdapter();
        Spin1Object obj = compiler.compile(root);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateBinary(os);

        byte[] actual = os.toByteArray();

        byte sum = 0;
        for (int i = 0; i < actual.length; i++) {
            sum += actual[i];
        }
        actual[5] = (byte) (0x14 - sum);

        expected[5] = actual[5] = 0;

        os = new ByteArrayOutputStream();
        obj.generateListing(new PrintStream(os));
        String actualListing = os.toString();

        obj.setBytes(expected, 0);

        os = new ByteArrayOutputStream();
        obj.generateListing(new PrintStream(os));
        String expectedListing = os.toString();

        Assertions.assertEquals(expectedListing, actualListing);
    }

}
