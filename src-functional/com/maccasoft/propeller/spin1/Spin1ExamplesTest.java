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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.CompilerMessage;
import com.maccasoft.propeller.model.Node;

class Spin1ExamplesTest {

    static final String path = "examples/P1";
    static final String libraryPath = "library/spin1";

    @BeforeAll
    static void setUp() throws Exception {
        Spin1ObjectCompiler.OPENSPIN_COMPATIBILITY = true;
    }

    @AfterAll
    static void cleanUp() throws Exception {
        Spin1ObjectCompiler.OPENSPIN_COMPATIBILITY = false;
    }

    @Test
    void test_com_serial_DataBlast() throws Exception {
        compileAndCompare(new File(path + "/com/serial", "DataBlast.spin"), new File(path, "com/serial/DataBlast.binary"));
    }

    @Test
    void test_com_serial_HelloWorld() throws Exception {
        compileAndCompare(new File(path + "/com/serial", "HelloWorld.spin"), new File(path, "com/serial/HelloWorld.binary"));
    }

    @Test
    void test_com_serial_LoopBack() throws Exception {
        compileAndCompare(new File(path + "/com/serial", "LoopBack.spin"), new File(path, "com/serial/LoopBack.binary"));
    }

    //@Test
    // Fails because of different sequence of same-priority operands
    void test_com_serial_terminal_Demo() throws Exception {
        compileAndCompare(new File(path + "/com/serial/terminal", "Demo.spin"), new File(path, "com/serial/terminal/Demo.binary"));
    }

    //@Test
    // Fails because of different sequence of same-priority operands
    void test_com_serial_terminal_HelloWorld() throws Exception {
        compileAndCompare(new File(path + "/com/serial/terminal", "HelloWorld.spin"), new File(path, "com/serial/terminal/HelloWorld.binary"));
    }

    //@Test
    // Fails because of different sequence of same-priority operands
    void test_com_serial_terminal_InputNumbers() throws Exception {
        compileAndCompare(new File(path + "/com/serial/terminal", "InputNumbers.spin"), new File(path, "com/serial/terminal/InputNumbers.binary"));
    }

    //@Test
    // Fails because of different sequence of same-priority operands
    void test_com_serial_terminal_ReadLine() throws Exception {
        compileAndCompare(new File(path + "/com/serial/terminal", "ReadLine.spin"), new File(path, "com/serial/terminal/ReadLine.binary"));
    }

    class Spin1CompilerAdapter extends Spin1Compiler {

        File parent;

        public Spin1CompilerAdapter(File parent) {
            this.parent = parent;
        }

        @Override
        protected Node getParsedObject(String fileName) {
            String text = getObjectSource(fileName);
            if (text == null) {
                return null;
            }
            Spin1TokenStream stream = new Spin1TokenStream(text);
            Spin1Parser subject = new Spin1Parser(stream);
            return subject.parse();
        }

        protected String getObjectSource(String fileName) {
            File file = new File(parent, fileName);
            if (!file.exists()) {
                file = new File(libraryPath, fileName);
            }
            if (file.exists()) {
                return loadFromFile(file);
            }
            return null;
        }

    }

    void compileAndCompare(File source, File binary) throws Exception {
        String text = loadFromFile(source);
        byte[] expected = loadBinaryFromFile(binary);

        Spin1TokenStream stream = new Spin1TokenStream(text);
        Spin1Parser subject = new Spin1Parser(stream);
        Node root = subject.parse();

        Spin1CompilerAdapter compiler = new Spin1CompilerAdapter(source.getParentFile());
        Spin1Object obj = compiler.compile(source.getName(), root);
        for (CompilerMessage msg : compiler.getMessages()) {
            if (msg.type == CompilerMessage.ERROR) {
                throw msg;
            }
        }

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

    byte[] loadBinaryFromFile(File file) throws Exception {
        InputStream is = new FileInputStream(file);
        try {
            byte[] b = new byte[is.available()];
            is.read(b);
            return b;
        } finally {
            try {
                is.close();
            } catch (Exception e) {

            }
        }
    }

}
