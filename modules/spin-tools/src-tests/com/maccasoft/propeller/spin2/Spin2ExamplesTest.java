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

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.model.Node;

class Spin2ExamplesTest {

    static final String path = "examples/P2";
    static final String libraryPath = "library/spin2";

    @Test
    void test_ansi_vgatext_demo() throws Exception {
        compileAndCompare(new File(path, "ansi_vgatext_demo.spin2"), new File(path, "ansi_vgatext_demo.binary"));
    }

    @Test
    void test_jm_1_wire_demo() throws Exception {
        compileAndCompare(new File(path, "jm_1-wire_demo.spin2"), new File(path, "jm_1-wire_demo.binary"));
    }

    @Test
    void test_jm_apa102c_demo() throws Exception {
        compileAndCompare(new File(path, "jm_apa102c_demo.spin2"), new File(path, "jm_apa102c_demo.binary"));
    }

    @Test
    void test_jm_at24c32_demo() throws Exception {
        compileAndCompare(new File(path, "jm_at24c32_demo.spin2"), new File(path, "jm_at24c32_demo.binary"));
    }

    @Test
    void test_jm_chauvet_led_splash() throws Exception {
        compileAndCompare(new File(path, "jm_chauvet_led-splash.spin2"), new File(path, "jm_chauvet_led-splash.binary"));
    }

    @Test
    void test_jm_click_4_20ma_demo() throws Exception {
        compileAndCompare(new File(path, "jm_click_4-20ma_demo.spin2"), new File(path, "jm_click_4-20ma_demo.binary"));
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
    void test_jm_ds3231_demo() throws Exception {
        compileAndCompare(new File(path, "jm_ds3231_demo.spin2"), new File(path, "jm_ds3231_demo.binary"));
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
    void test_jm_format_strings_demo() throws Exception {
        compileAndCompare(new File(path, "jm_format_strings_demo.spin2"), new File(path, "jm_format_strings_demo.binary"));
    }

    @Test
    void test_jm_i2c_devices() throws Exception {
        compileAndCompare(new File(path, "jm_i2c_devices.spin2"), new File(path, "jm_i2c_devices.binary"));
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
    void test_jm_max31855_k_demo() throws Exception {
        compileAndCompare(new File(path, "jm_max31855-k_demo.spin2"), new File(path, "jm_max31855-k_demo.binary"));
    }

    @Test
    void test_jm_max7219_ez_demo() throws Exception {
        compileAndCompare(new File(path, "jm_max7219_ez_demo.spin2"), new File(path, "jm_max7219_ez_demo.binary"));
    }

    @Test
    void test_jm_p2_es_matrix_control_demo() throws Exception {
        compileAndCompare(new File(path, "jm_p2-es_matrix_control_demo.spin2"), new File(path, "jm_p2-es_matrix_control_demo.binary"));
    }

    @Test
    void test_jm_pwm_demo() throws Exception {
        compileAndCompare(new File(path, "jm_pwm_demo.spin2"), new File(path, "jm_pwm_demo.binary"));
    }

    @Test
    void test_m6502_apple1() throws Exception {
        compileAndCompare(new File(path, "m6502_apple1.spin2"), new File(path, "m6502_apple1.binary"));
    }

    @Test
    void test_Spin2_interpreter() throws Exception {
        compileAndCompare(new File("Spin2_interpreter.spin2"), new File("Spin2_interpreter.binary"));
    }

    @Test
    void test_Spin2_debugger() throws Exception {
        compileAndCompare(new File("Spin2_debugger.spin2"), new File("Spin2_debugger.binary"));
    }

    @Test
    void test_flash_loader() throws Exception {
        compileAndCompare(new File("flash_loader.spin2"), new File("flash_loader.binary"));
    }

    class Spin2CompilerAdapter extends Spin2Compiler {

        File parent;

        public Spin2CompilerAdapter(File parent) {
            this.parent = parent;
        }

        @Override
        public File getFile(String name) {
            File file = new File(parent, name);
            if (!file.exists() || file.isDirectory()) {
                file = new File(path, name);
            }
            if (!file.exists() || file.isDirectory()) {
                file = new File(libraryPath, name);
            }
            return file.exists() && !file.isDirectory() ? file : null;
        }

        @Override
        public Node getParsedObject(String fileName) {
            String text = getObjectSource(fileName);
            if (text == null) {
                return null;
            }
            Spin2TokenStream stream = new Spin2TokenStream(text);
            Spin2Parser subject = new Spin2Parser(stream);
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

        @Override
        protected byte[] getBinaryFile(String fileName) {
            File file = new File(parent, fileName);
            if (!file.exists()) {
                file = new File(libraryPath, fileName);
            }
            if (file.exists()) {
                return loadBinaryFromFile(file);
            }
            return null;
        }

    }

    void compileAndCompare(File source, File binary) throws Exception {
        String text = loadFromFile(source);
        byte[] expected = loadBinaryFromFile(binary);

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser subject = new Spin2Parser(stream);
        Node root = subject.parse();

        Spin2CompilerAdapter compiler = new Spin2CompilerAdapter(source.getParentFile());
        Spin2Object obj = compiler.compile(source, root);
        for (CompilerException msg : compiler.getMessages()) {
            if (msg.type == CompilerException.ERROR) {
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
        else {
            try {
                InputStream is = getClass().getResourceAsStream(file.getName());
                byte[] b = new byte[is.available()];
                is.read(b);
                return new String(b);
            } catch (Exception e) {
                throw new RuntimeException("error reading file " + file, e);
            }
        }

        return sb.toString();
    }

    byte[] loadBinaryFromFile(File file) {
        try {
            if (file.exists()) {
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
            else {
                InputStream is = getClass().getResourceAsStream(file.getName());
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
        } catch (Exception e) {
            throw new RuntimeException("error reading file " + file, e);
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
