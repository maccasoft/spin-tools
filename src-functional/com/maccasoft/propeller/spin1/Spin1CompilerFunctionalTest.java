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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.CompilerMessage;
import com.maccasoft.propeller.model.Node;

class Spin1CompilerFunctionalTest {

    @BeforeAll
    static void setUp() throws Exception {
        Spin1Compiler.OPENSPIN_COMPATIBILITY = true;
    }

    @AfterAll
    static void cleanUp() throws Exception {
        Spin1Compiler.OPENSPIN_COMPATIBILITY = false;
    }

    @Test
    void testCharType() throws Exception {
        String text = getResourceAsString("char.type.spin");

        byte[] expected = getResource("char.type.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testComSerial() throws Exception {
        String text = getResourceAsString("com.serial.spin");

        byte[] expected = getResource("com.serial.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testComSpi() throws Exception {
        String text = getResourceAsString("com.spi.spin");

        byte[] expected = getResource("com.spi.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testDisplayTV() throws Exception {
        String text = getResourceAsString("display.tv.spin");

        byte[] expected = getResource("display.tv.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testDisplayVGA() throws Exception {
        String text = getResourceAsString("display.vga.spin");

        byte[] expected = getResource("display.vga.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testDisplayVGABitmap() throws Exception {
        String text = getResourceAsString("display.vga.bitmap.512x384.spin");

        byte[] expected = getResource("display.vga.bitmap.512x384.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testInputKeyboard() throws Exception {
        String text = getResourceAsString("input.keyboard.spin");

        byte[] expected = getResource("input.keyboard.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testIO() throws Exception {
        String text = getResourceAsString("io.spin");

        byte[] expected = getResource("io.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testMathRCTime() throws Exception {
        String text = getResourceAsString("math.rctime.spin");
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("io", getResourceAsString("io.spin"));
        sources.put("time", getResourceAsString("time.spin"));

        byte[] expected = getResource("math.rctime.binary");
        compileAndCompare(text, sources, expected);
    }

    @Test
    void testSignalADC() throws Exception {
        String text = getResourceAsString("signal.adc.spin");

        byte[] expected = getResource("signal.adc.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testSignalADC3208() throws Exception {
        String text = getResourceAsString("signal.adc.mcp3208.spin");

        byte[] expected = getResource("signal.adc.mcp3208.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testSignalDither() throws Exception {
        String text = getResourceAsString("signal.dither.spin");

        byte[] expected = getResource("signal.dither.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testSignalSpatializer() throws Exception {
        String text = getResourceAsString("signal.spatializer.spin");

        byte[] expected = getResource("signal.spatializer.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testString() throws Exception {
        String text = getResourceAsString("string.spin");
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("char.type", getResourceAsString("char.type.spin"));

        byte[] expected = getResource("string.binary");
        compileAndCompare(text, sources, expected);
    }

    @Test
    void testStringType() throws Exception {
        String text = getResourceAsString("string.type.spin");
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("char.type", getResourceAsString("char.type.spin"));

        byte[] expected = getResource("string.type.binary");
        compileAndCompare(text, sources, expected);
    }

    @Test
    void testTimeClock() throws Exception {
        String text = getResourceAsString("time.clock.spin");

        byte[] expected = getResource("time.clock.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testTime() throws Exception {
        String text = getResourceAsString("time.spin");

        byte[] expected = getResource("time.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testMathRandom() throws Exception {
        String text = getResourceAsString("math.random.spin");

        byte[] expected = getResource("math.random.binary");
        compileAndCompare(text, Collections.emptyMap(), expected);
    }

    @Test
    void testMotorServo() throws Exception {
        String text = getResourceAsString("motor.servo.spin");
        Map<String, String> sources = new HashMap<String, String>();
        sources.put("motor.servo.ramp", getResourceAsString("motor.servo.ramp.spin"));

        byte[] expected = getResource("motor.servo.binary");
        compileAndCompare(text, sources, expected);
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
            if (text == null) {
                throw new RuntimeException("file " + fileName + " not found");
            }
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

    void compileAndCompare2(String text, Map<String, String> sources, byte[] expected) throws Exception {
        Spin1TokenStream stream = new Spin1TokenStream(text);
        Spin1Parser subject = new Spin1Parser(stream);
        Node root = subject.parse();

        Spin1CompilerAdapter compiler = new Spin1CompilerAdapter(sources);
        Spin1Object obj = compiler.compile(root);

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

}
