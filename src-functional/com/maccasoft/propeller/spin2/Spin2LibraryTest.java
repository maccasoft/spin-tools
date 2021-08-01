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

class Spin2LibraryTest {

    static final String path = "library/spin2";

    @Test
    void test_cjmj_pin_test_w_led_matrix() throws Exception {
        compileAndCompare(new File(path, "cjmj_pin_test_w_led_matrix.spin2"), new File(path, "cjmj_pin_test_w_led_matrix.binary"));
    }

    @Test
    void test_jm_1_wire() throws Exception {
        compileAndCompare(new File(path, "jm_1-wire.spin2"), new File(path, "jm_1-wire.binary"));
    }

    @Test
    void test_jm_apa102c() throws Exception {
        compileAndCompare(new File(path, "jm_apa102c.spin2"), new File(path, "jm_apa102c.binary"));
    }

    @Test
    void test_jm_click_4_20ma_rx() throws Exception {
        compileAndCompare(new File(path, "jm_click_4-20ma_rx.spin2"), new File(path, "jm_click_4-20ma_rx.binary"));
    }

    @Test
    void test_jm_click_4_20ma_tx() throws Exception {
        compileAndCompare(new File(path, "jm_click_4-20ma_tx.spin2"), new File(path, "jm_click_4-20ma_tx.binary"));
    }

    @Test
    void test_jm_click_dmx_tx() throws Exception {
        compileAndCompare(new File(path, "jm_click_dmx-tx.spin2"), new File(path, "jm_click_dmx-tx.binary"));
    }

    @Test
    void test_jm_click_relay2() throws Exception {
        compileAndCompare(new File(path, "jm_click_relay2.spin2"), new File(path, "jm_click_relay2.binary"));
    }

    @Test
    void test_jm_click_rs485_3v3() throws Exception {
        compileAndCompare(new File(path, "jm_click_rs485_3v3.spin2"), new File(path, "jm_click_rs485_3v3.binary"));
    }

    @Test
    void test_jm_click_rtc10() throws Exception {
        compileAndCompare(new File(path, "jm_click_rtc10.spin2"), new File(path, "jm_click_rtc10.binary"));
    }

    @Test
    void test_jm_dc_motor() throws Exception {
        compileAndCompare(new File(path, "jm_dc_motor.spin2"), new File(path, "jm_dc_motor.binary"));
    }

    @Test
    void test_jm_dmx_tx() throws Exception {
        compileAndCompare(new File(path, "jm_dmx_tx.spin2"), new File(path, "jm_dmx_tx.binary"));
    }

    @Test
    void test_jm_ez_analog() throws Exception {
        // Comparison with binary from Propeller Tool fails
        // because of different sequence of same-priority operators (limit-min and limit-max in this case)
        compileAndCompare(new File(path, "jm_ez_analog.spin2"), new File(path, "jm_ez_analog.binary"));
    }

    @Test
    void test_jm_ez_button() throws Exception {
        compileAndCompare(new File(path, "jm_ez_button.spin2"), new File(path, "jm_ez_button.binary"));
    }

    @Test
    void test_jm_ez_sound() throws Exception {
        compileAndCompare(new File(path, "jm_ez_sound.spin2"), new File(path, "jm_ez_sound.binary"));
    }

    @Test
    void test_jm_ez_spi() throws Exception {
        compileAndCompare(new File(path, "jm_ez_spi.spin2"), new File(path, "jm_ez_spi.binary"));
    }

    @Test
    void test_jm_fullduplexserial() throws Exception {
        compileAndCompare(new File(path, "jm_fullduplexserial.spin2"), new File(path, "jm_fullduplexserial.binary"));
    }

    @Test
    void test_jm_gamma8() throws Exception {
        compileAndCompare(new File(path, "jm_gamma8.spin2"), new File(path, "jm_gamma8.binary"));
    }

    @Test
    void test_jm_hd485() throws Exception {
        compileAndCompare(new File(path, "jm_hd485.spin2"), new File(path, "jm_hd485.binary"));
    }

    @Test
    void test_jm_i2c() throws Exception {
        compileAndCompare(new File(path, "jm_i2c.spin2"), new File(path, "jm_i2c.binary"));
    }

    @Test
    void test_jm_lcd_pcf8574() throws Exception {
        compileAndCompare(new File(path, "jm_lcd_pcf8574.spin2"), new File(path, "jm_lcd_pcf8574.binary"));
    }

    @Test
    void test_jm_led_gamma() throws Exception {
        compileAndCompare(new File(path, "jm_led_gamma.spin2"), new File(path, "jm_led_gamma.binary"));
    }

    @Test
    void test_jm_max31855() throws Exception {
        compileAndCompare(new File(path, "jm_max31855.spin2"), new File(path, "jm_max31855.binary"));
    }

    @Test
    void test_jm_max7219_ez() throws Exception {
        compileAndCompare(new File(path, "jm_max7219_ez.spin2"), new File(path, "jm_max7219_ez.binary"));
    }

    @Test
    void test_jm_prng() throws Exception {
        compileAndCompare(new File(path, "jm_prng.spin2"), new File(path, "jm_prng.binary"));
    }

    @Test
    void test_jm_serial() throws Exception {
        compileAndCompare(new File(path, "jm_serial.spin2"), new File(path, "jm_serial.binary"));
    }

    @Test
    void test_jm_spi() throws Exception {
        compileAndCompare(new File(path, "jm_spi.spin2"), new File(path, "jm_spi.binary"));
    }

    @Test
    void test_jm_time_200() throws Exception {
        compileAndCompare(new File(path, "jm_time_200.spin2"), new File(path, "jm_time_200.binary"));
    }

    @Test
    void test_jm_nstr() throws Exception {
        compileAndCompare(new File(path, "jm_nstr.spin2"), new File(path, "jm_nstr.binary"));
    }

    @Test
    void test_jm_p2_es_control() throws Exception {
        compileAndCompare(new File(path, "jm_p2-es_control.spin2"), new File(path, "jm_p2-es_control.binary"));
    }

    @Test
    void test_jm_p2_es_matrix() throws Exception {
        compileAndCompare(new File(path, "jm_p2-es_matrix.spin2"), new File(path, "jm_p2-es_matrix.binary"));
    }

    @Test
    void test_jm_pcf8574() throws Exception {
        compileAndCompare(new File(path, "jm_pcf8574.spin2"), new File(path, "jm_pcf8574.binary"));
    }

    @Test
    void test_jm_pwm() throws Exception {
        compileAndCompare(new File(path, "jm_pwm.spin2"), new File(path, "jm_pwm.binary"));
    }

    @Test
    void test_jm_quadrature() throws Exception {
        compileAndCompare(new File(path, "jm_quadrature.spin2"), new File(path, "jm_quadrature.binary"));
    }

    @Test
    void test_jm_rgbx_pixel() throws Exception {
        compileAndCompare(new File(path, "jm_rgbx_pixel.spin2"), new File(path, "jm_rgbx_pixel.binary"));
    }

    @Test
    void test_jm_servo() throws Exception {
        compileAndCompare(new File(path, "jm_servo.spin2"), new File(path, "jm_servo.binary"));
    }

    @Test
    void test_jm_sircs_rx() throws Exception {
        compileAndCompare(new File(path, "jm_sircs_rx.spin2"), new File(path, "jm_sircs_rx.binary"));
    }

    @Test
    void test_jm_tm1638() throws Exception {
        compileAndCompare(new File(path, "jm_tm1638.spin2"), new File(path, "jm_tm1638.binary"));
    }

    @Test
    void test_jm_tm163x_segs7() throws Exception {
        compileAndCompare(new File(path, "jm_tm163x_segs7.spin2"), new File(path, "jm_tm163x_segs7.binary"));
    }

    @Test
    void test_jm_sircs_tx() throws Exception {
        compileAndCompare(new File(path, "jm_sircs_tx.spin2"), new File(path, "jm_sircs_tx.binary"));
    }

    @Test
    void test_reSound() throws Exception {
        compileAndCompare(new File(path, "reSound.spin2"), new File(path, "reSound.binary"));
    }

    @Test
    void test_strings() throws Exception {
        compileAndCompare(new File(path, "strings.spin2"), new File(path, "strings.binary"));
    }

    @Test
    void test_vga_tile_driver() throws Exception {
        compileAndCompare(new File(path, "vga_tile_driver.spin2"), new File(path, "vga_tile_driver.binary"));
    }

    /*
    @Test
    void test_Spin2_interpreter() throws Exception {
        String text = getResourceAsString("Spin2_interpreter.spin2");
    
        new File(path, "Spin2_interpreter.binary");
        compileAndCompare(text, expected);
    }
    */
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
            if (compiler.hasErrors()) {
                for (CompilerMessage msg : compiler.getMessages()) {
                    logMessage(msg);
                }
            }
            return compiler.compileObject(root);
        }

        protected String getObjectSource(String fileName) {
            fileName += ".spin2";
            File file = new File(parent, fileName);
            if (file.exists()) {
                return loadFromFile(file);
            }
            throw new RuntimeException("file " + file + " not found");
        }

        @Override
        protected byte[] getBinaryFile(String fileName) {
            File file = new File(parent, fileName);
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

        return sb.toString();
    }

    byte[] loadBinaryFromFile(File file) {
        try {
            InputStream is = new FileInputStream(file);
            byte[] b = new byte[is.available()];
            is.read(b);
            is.close();
            return b;
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
