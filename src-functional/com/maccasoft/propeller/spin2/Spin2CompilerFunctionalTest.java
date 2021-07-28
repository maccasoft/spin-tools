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
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.CompilerMessage;
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
        compileAndCompare(text, expected);
    }

    @Test
    void testLedMatrix() throws Exception {
        String text = loadFromFile(new File("library/spin2", "cjmj_pin_test_w_led_matrix.spin2"));

        byte[] expected = getResource("cjmj_pin_test_w_led_matrix.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void test1Wire() throws Exception {
        String text = loadFromFile(new File("library/spin2", "jm_1-wire.spin2"));

        byte[] expected = getResource("jm_1-wire.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void testAPA102C() throws Exception {
        String text = loadFromFile(new File("library/spin2", "jm_apa102c.spin2"));

        byte[] expected = getResource("jm_apa102c.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void testEZAnalog() throws Exception {
        // Comparison with binary from Propeller Tool fails
        // because of different sequence of same-priority operators (limit-min and limit-max in this case)
        String text = loadFromFile(new File("library/spin2", "jm_ez_analog.spin2"));

        byte[] expected = getResource("jm_ez_analog.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void testEZButton() throws Exception {
        String text = loadFromFile(new File("library/spin2", "jm_ez_button.spin2"));

        byte[] expected = getResource("jm_ez_button.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void testEZSound() throws Exception {
        String text = loadFromFile(new File("library/spin2", "jm_ez_sound.spin2"));

        byte[] expected = getResource("jm_ez_sound.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void testEZSPI() throws Exception {
        String text = loadFromFile(new File("library/spin2", "jm_ez_spi.spin2"));

        byte[] expected = getResource("jm_ez_spi.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void testFullDuplexSerial() throws Exception {
        String text = loadFromFile(new File("library/spin2", "jm_fullduplexserial.spin2"));

        byte[] expected = getResource("jm_fullduplexserial.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void testGamma8() throws Exception {
        String text = loadFromFile(new File("library/spin2", "jm_gamma8.spin2"));

        byte[] expected = getResource("jm_gamma8.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void testHD485() throws Exception {
        String text = loadFromFile(new File("library/spin2", "jm_hd485.spin2"));

        byte[] expected = getResource("jm_hd485.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void testI2C() throws Exception {
        String text = loadFromFile(new File("library/spin2", "jm_i2c.spin2"));

        byte[] expected = getResource("jm_i2c.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void testLCD_PCF8574() throws Exception {
        String text = loadFromFile(new File("library/spin2", "jm_lcd_pcf8574.spin2"));

        byte[] expected = getResource("jm_lcd_pcf8574.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void testNstr() throws Exception {
        String text = loadFromFile(new File("library/spin2", "jm_nstr.spin2"));

        byte[] expected = getResource("jm_nstr.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void testPCF8574() throws Exception {
        String text = loadFromFile(new File("library/spin2", "jm_pcf8574.spin2"));

        byte[] expected = getResource("jm_pcf8574.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void testReSound() throws Exception {
        String text = loadFromFile(new File("library/spin2", "reSound.spin2"));

        byte[] expected = getResource("reSound.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void testStrings() throws Exception {
        String text = loadFromFile(new File("library/spin2", "strings.spin2"));

        byte[] expected = getResource("strings.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void testVGA() throws Exception {
        String text = loadFromFile(new File("library/spin2", "vga_tile_driver.spin2"));

        byte[] expected = getResource("vga_tile_driver.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void testApple1CVBS() throws Exception {
        String text = loadFromFile(new File("examples/P2", "m6502_apple1_cvbs.spin2"));

        byte[] expected = getResource("m6502_apple1_cvbs.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void testApple1VGA() throws Exception {
        String text = loadFromFile(new File("examples/P2", "m6502_apple1_vga.spin2"));

        byte[] expected = getResource("m6502_apple1_vga.binary");
        compileAndCompare(text, expected);
    }

    @Test
    void testInterpreter() throws Exception {
        String text = getResourceAsString("Spin2_interpreter.spin2");

        byte[] expected = getResource("Spin2_interpreter.binary");
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

    class Spin2CompilerAdapter extends Spin2Compiler {

        public Spin2CompilerAdapter() {

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

            Spin2CompilerAdapter compiler = new Spin2CompilerAdapter();
            return compiler.compileObject(root);
        }

        protected String getObjectSource(String fileName) {
            fileName += ".spin2";
            File file = new File("library/spin2", fileName);
            if (file.exists()) {
                return loadFromFile(file);
            }
            return getResourceAsString(fileName);
        }

    }

    void compileAndCompare(String text, byte[] expected) throws Exception {
        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser subject = new Spin2Parser(stream);
        Node root = subject.parse();

        Spin2CompilerAdapter compiler = new Spin2CompilerAdapter();
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
