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
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;

class Spin1LibraryTest {

    @BeforeAll
    static void setUp() throws Exception {
        Spin1Compiler.OPENSPIN_COMPATIBILITY = true;
    }

    @AfterAll
    static void cleanUp() throws Exception {
        Spin1Compiler.OPENSPIN_COMPATIBILITY = false;
    }

    @Test
    void test_char_type() throws Exception {
        String text = loadFromFile(new File("library/spin1", "char.type.spin"));

        byte[] expected = getResource("char.type.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_com_serial() throws Exception {
        String text = loadFromFile(new File("library/spin1", "com.serial.spin"));

        byte[] expected = getResource("com.serial.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_com_spi() throws Exception {
        String text = loadFromFile(new File("library/spin1", "com.spi.spin"));

        byte[] expected = getResource("com.spi.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_display_tv() throws Exception {
        String text = loadFromFile(new File("library/spin1", "display.tv.spin"));

        byte[] expected = getResource("display.tv.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_display_vga() throws Exception {
        String text = loadFromFile(new File("library/spin1", "display.vga.spin"));

        byte[] expected = getResource("display.vga.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_display_vga_bitmap_512x384() throws Exception {
        String text = loadFromFile(new File("library/spin1", "display.vga.bitmap.512x384.spin"));

        byte[] expected = getResource("display.vga.bitmap.512x384.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_input_keyboard() throws Exception {
        String text = loadFromFile(new File("library/spin1", "input.keyboard.spin"));

        byte[] expected = getResource("input.keyboard.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_io() throws Exception {
        String text = loadFromFile(new File("library/spin1", "io.spin"));

        byte[] expected = getResource("io.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_math_rctime() throws Exception {
        String text = loadFromFile(new File("library/spin1", "math.rctime.spin"));

        byte[] expected = getResource("math.rctime.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_signal_adc() throws Exception {
        String text = loadFromFile(new File("library/spin1", "signal.adc.spin"));

        byte[] expected = getResource("signal.adc.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_signal_adc_mcp3208() throws Exception {
        String text = loadFromFile(new File("library/spin1", "signal.adc.mcp3208.spin"));

        byte[] expected = getResource("signal.adc.mcp3208.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_signal_dither() throws Exception {
        String text = loadFromFile(new File("library/spin1", "signal.dither.spin"));

        byte[] expected = getResource("signal.dither.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_signal_spatializer() throws Exception {
        String text = loadFromFile(new File("library/spin1", "signal.spatializer.spin"));

        byte[] expected = getResource("signal.spatializer.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_string() throws Exception {
        String text = loadFromFile(new File("library/spin1", "string.spin"));

        byte[] expected = getResource("string.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_string_type() throws Exception {
        String text = loadFromFile(new File("library/spin1", "string.type.spin"));

        byte[] expected = getResource("string.type.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_time_clock() throws Exception {
        String text = loadFromFile(new File("library/spin1", "time.clock.spin"));

        byte[] expected = getResource("time.clock.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_time() throws Exception {
        String text = loadFromFile(new File("library/spin1", "time.spin"));

        byte[] expected = getResource("time.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_math_random() throws Exception {
        String text = loadFromFile(new File("library/spin1", "math.random.spin"));

        byte[] expected = getResource("math.random.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test_motor_servo() throws Exception {
        String text = loadFromFile(new File("library/spin1", "motor.servo.spin"));

        byte[] expected = getResource("motor.servo.binary");
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
