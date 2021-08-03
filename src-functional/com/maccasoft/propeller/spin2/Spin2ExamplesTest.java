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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.CompilerMessage;
import com.maccasoft.propeller.model.Node;

class Spin2ExamplesTest {

    static final String path = "examples/P2";
    static final String libraryPath = "library/spin2";

    @Test
    void test_jm_1_wire_demo() throws Exception {
        compileAndCompare(new File(path, "jm_1-wire_demo.spin2"), new File(path, "jm_1-wire_demo.binary"));
    }

    @Test
    void test_jm_apa102c_demo() throws Exception {
        compileAndCompare(new File(path, "jm_apa102c_demo.spin2"), new File(path, "jm_apa102c_demo.binary"));
    }

    @Test
    void test_jm_click_dmx_tx_demo() throws Exception {
        compileAndCompare(new File(path, "jm_click_dmx-tx_demo.spin2"), new File(path, "jm_click_dmx-tx_demo.binary"));
    }

    @Test
    void test_jm_click_rs485_demo() throws Exception {
        compileAndCompare(new File(path, "jm_click_rs485_demo.spin2"), new File(path, "jm_click_rs485_demo.binary"));
    }

    @Test
    void test_jm_click_rs485_test() throws Exception {
        compileAndCompare(new File(path, "jm_click_rs485_test.spin2"), new File(path, "jm_click_rs485_test.binary"));
    }

    @Test
    void test_jm_click_rtc_10_demo() throws Exception {
        compileAndCompare(new File(path, "jm_click_rtc-10_demo.spin2"), new File(path, "jm_click_rtc-10_demo.binary"));
    }

    @Test
    void test_jm_dc_motor_demo() throws Exception {
        compileAndCompare(new File(path, "jm_dc_motor_demo.spin2"), new File(path, "jm_dc_motor_demo.binary"));
    }

    @Test
    void test_jm_ez_analog_demo() throws Exception {
        compileAndCompare(new File(path, "jm_ez_analog_demo.spin2"), new File(path, "jm_ez_analog_demo.binary"));
    }

    @Test
    void test_jm_ez_button_demo() throws Exception {
        compileAndCompare(new File(path, "jm_ez_button_demo.spin2"), new File(path, "jm_ez_button_demo.binary"));
    }

    @Test
    void test_jm_ez_sound_demo() throws Exception {
        compileAndCompare(new File(path, "jm_ez_sound_demo.spin2"), new File(path, "jm_ez_sound_demo.binary"));
    }

    @Test
    void test_jm_ez_spi_demo() throws Exception {
        compileAndCompare(new File(path, "jm_ez_spi_demo.spin2"), new File(path, "jm_ez_spi_demo.binary"));
    }

    @Test
    void test_jm_i2c_scanner() throws Exception {
        compileAndCompare(new File(path, "jm_i2c_scanner.spin2"), new File(path, "jm_i2c_scanner.binary"));
    }

    @Test
    void test_jm_lcd_pcf8574_demo() throws Exception {
        compileAndCompare(new File(path, "jm_lcd_pcf8574_demo.spin2"), new File(path, "jm_lcd_pcf8574_demo.binary"));
    }

    @Test
    void test_jm_pwm_demo() throws Exception {
        compileAndCompare(new File(path, "jm_pwm_demo.spin2"), new File(path, "jm_pwm_demo.binary"));
    }

    @Test
    void test_m6502_apple1_cvbs() throws Exception {
        compileAndCompare(new File(path, "m6502_apple1_cvbs.spin2"), new File(path, "m6502_apple1_cvbs.binary"));
    }

    @Test
    void test_m6502_apple1_vga() throws Exception {
        compileAndCompare(new File(path, "m6502_apple1_vga.spin2"), new File(path, "m6502_apple1_vga.binary"));
    }

    class Spin2CompilerAdapter extends Spin2Compiler {

        File parent;

        public Spin2CompilerAdapter(File parent) {
            this.parent = parent;
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

            Spin2CompilerAdapter compiler = new Spin2CompilerAdapter(parent);
            return compiler.compileObject(root);
        }

        protected String getObjectSource(String fileName) {
            fileName += ".spin2";
            File file = new File(parent, fileName);
            if (!file.exists()) {
                file = new File(libraryPath, fileName);
            }
            if (file.exists()) {
                return loadFromFile(file);
            }
            throw new RuntimeException("file " + file + " not found");
        }

    }

    void compileAndCompare(File source, File binary) throws Exception {
        String text = loadFromFile(source);
        byte[] expected = loadBinaryFromFile(binary);

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser subject = new Spin2Parser(stream);
        Node root = subject.parse();

        Spin2CompilerAdapter compiler = new Spin2CompilerAdapter(source.getParentFile());
        Spin2Object obj = compiler.compile(root);
        for (CompilerMessage msg : compiler.getMessages()) {
            if (msg.type == CompilerMessage.ERROR) {
                throw msg;
            }
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateBinary(os);
        byte[] actual = os.toByteArray();

        os = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(os);

        if (obj.getInterpreter() != null) {
            printInterpreterParameters(actual, out);
        }
        obj.generateListing(out);
        String actualListing = os.toString().replaceAll("\\r\\n", "\n");

        String expectedListing;
        if (expected != null) {
            obj.setBytes(expected, obj.getInterpreter() != null ? obj.getInterpreter().getPBase() : 0x0000);

            os = new ByteArrayOutputStream();
            out = new PrintStream(os);
            if (obj.getInterpreter() != null) {
                printInterpreterParameters(expected, out);
            }
            obj.generateListing(out);
            expectedListing = os.toString().replaceAll("\\r\\n", "\n");
        }
        else {
            expectedListing = "";
        }

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

    void printInterpreterParameters(byte[] binary, PrintStream out) {
        out.println(String.format("%02X %02X %02X %02X PBASE", binary[0x30], binary[0x31], binary[0x32], binary[0x33]));
        out.println(String.format("%02X %02X %02X %02X VBASE", binary[0x34], binary[0x35], binary[0x36], binary[0x37]));
        out.println(String.format("%02X %02X %02X %02X DBASE", binary[0x38], binary[0x39], binary[0x3A], binary[0x3B]));
        out.println(String.format("%02X %02X %02X %02X Longs to clear", binary[0x3C], binary[0x3D], binary[0x3E], binary[0x3F]));
        out.println();
        out.println(String.format("%02X %02X %02X %02X CLKMODE", binary[0x40], binary[0x41], binary[0x42], binary[0x43]));
        out.println(String.format("%02X %02X %02X %02X CLKFREQ", binary[0x44], binary[0x45], binary[0x46], binary[0x47]));
        out.println();
    }

}
