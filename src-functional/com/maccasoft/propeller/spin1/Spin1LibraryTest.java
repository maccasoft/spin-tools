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

class Spin1LibraryTest {

    static final String path = "library/spin1";

    @BeforeAll
    static void setUp() throws Exception {
        Spin1ObjectCompiler.OPENSPIN_COMPATIBILITY = true;
    }

    @AfterAll
    static void cleanUp() throws Exception {
        Spin1ObjectCompiler.OPENSPIN_COMPATIBILITY = false;
    }

    @Test
    void test_char_type() throws Exception {
        compileAndCompare(new File(path, "char.type.spin"), new File(path, "char.type.binary"));
    }

    @Test
    void test_com_serial() throws Exception {
        compileAndCompare(new File(path, "com.serial.spin"), new File(path, "com.serial.binary"));
    }

    @Test
    // Comparison with binary from OpenSpin fails
    //  - different sequence of same-priority operators
    void test_com_serial_terminal() throws Exception {
        compileAndCompare(new File(path, "com.serial.terminal.spin"), new File(path, "com.serial.terminal.binary"));
    }

    @Test
    void test_com_spi() throws Exception {
        compileAndCompare(new File(path, "com.spi.spin"), new File(path, "com.spi.binary"));
    }

    @Test
    void test_commandparser() throws Exception {
        compileAndCompare(new File(path, "commandparser.spin"), new File(path, "commandparser.binary"));
    }

    @Test
    void test_debug_emulator_rtc() throws Exception {
        compileAndCompare(new File(path, "debug.emulator.rtc.spin"), new File(path, "debug.emulator.rtc.binary"));
    }

    @Test
    void test_debug_shell() throws Exception {
        compileAndCompare(new File(path, "debug.shell.spin"), new File(path, "debug.shell.binary"));
    }

    @Test
    void test_debug_stacklength() throws Exception {
        compileAndCompare(new File(path, "debug.stacklength.spin"), new File(path, "debug.stacklength.binary"));
    }

    @Test
    // Comparison with binary from OpenSpin fails
    //  - different sequence of same-priority operators
    void test_display_lcd_serial() throws Exception {
        compileAndCompare(new File(path, "display.lcd.serial.spin"), new File(path, "display.lcd.serial.binary"));
    }

    @Test
    void test_display_tv() throws Exception {
        compileAndCompare(new File(path, "display.tv.spin"), new File(path, "display.tv.binary"));
    }

    @Test
    void test_display_tv_graphics() throws Exception {
        compileAndCompare(new File(path, "display.tv.graphics.spin"), new File(path, "display.tv.graphics.binary"));
    }

    @Test
    void test_display_tv_text() throws Exception {
        compileAndCompare(new File(path, "display.tv.text.spin"), new File(path, "display.tv.text.binary"));
    }

    @Test
    void test_display_vga() throws Exception {
        compileAndCompare(new File(path, "display.vga.spin"), new File(path, "display.vga.binary"));
    }

    @Test
    void test_display_vga_bitmap_512x384() throws Exception {
        compileAndCompare(new File(path, "display.vga.bitmap.512x384.spin"), new File(path, "display.vga.bitmap.512x384.binary"));
    }

    @Test
    void test_display_vga_text() throws Exception {
        compileAndCompare(new File(path, "display.vga.text.spin"), new File(path, "display.vga.text.binary"));
    }

    @Test
    void test_display_vga_tile_1280x1024() throws Exception {
        compileAndCompare(new File(path, "display.vga.tile.1280x1024.spin"), new File(path, "display.vga.tile.1280x1024.binary"));
    }

    @Test
    void test_display_vga_tile_1600x1200() throws Exception {
        compileAndCompare(new File(path, "display.vga.tile.1600x1200.spin"), new File(path, "display.vga.tile.1600x1200.binary"));
    }

    @Test
    void test_input_keyboard() throws Exception {
        compileAndCompare(new File(path, "input.keyboard.spin"), new File(path, "input.keyboard.binary"));
    }

    @Test
    void test_input_keypad_4x4() throws Exception {
        compileAndCompare(new File(path, "input.keypad.4x4.spin"), new File(path, "input.keypad.4x4.binary"));
    }

    @Test
    // Comparison with binary from OpenSpin fails
    //  - different sequence of same-priority operators
    void test_input_mouse() throws Exception {
        compileAndCompare(new File(path, "input.mouse.spin"), new File(path, "input.mouse.binary"));
    }

    @Test
    // Comparison with binary from OpenSpin fails
    //  - different sequence of same-priority operators
    void test_input_quadrature() throws Exception {
        compileAndCompare(new File(path, "input.quadrature.spin"), new File(path, "input.quadrature.binary"));
    }

    @Test
    void test_input_trim_ad8803() throws Exception {
        compileAndCompare(new File(path, "input.trim.ad8803.spin"), new File(path, "input.trim.ad8803.binary"));
    }

    @Test
    void test_io() throws Exception {
        compileAndCompare(new File(path, "io.spin"), new File(path, "io.binary"));
    }

    @Test
    void test_math_float() throws Exception {
        // Comparison with binary from OpenSpin fails
        //  - floating point rounding
        compileAndCompare(new File(path, "math.float.spin"), new File(path, "math.float.binary"));
    }

    @Test
    void test_math_random() throws Exception {
        compileAndCompare(new File(path, "math.random.spin"), new File(path, "math.random.binary"));
    }

    @Test
    void test_math_rctime() throws Exception {
        compileAndCompare(new File(path, "math.rctime.spin"), new File(path, "math.rctime.binary"));
    }

    @Test
    void test_motor_servo() throws Exception {
        compileAndCompare(new File(path, "motor.servo.spin"), new File(path, "motor.servo.binary"));
    }

    @Test
    void test_sensor_accel_dual_memsic2125() throws Exception {
        compileAndCompare(new File(path, "sensor.accel.dual.memsic2125.spin"), new File(path, "sensor.accel.dual.memsic2125.binary"));
    }

    @Test
    void test_sensor_accel_dual_mxd2125() throws Exception {
        compileAndCompare(new File(path, "sensor.accel.dual.mxd2125.spin"), new File(path, "sensor.accel.dual.mxd2125.binary"));
    }

    @Test
    void test_sensor_accel_tri_h48c() throws Exception {
        compileAndCompare(new File(path, "sensor.accel.tri.h48c.spin"), new File(path, "sensor.accel.tri.h48c.binary"));
    }

    @Test
    void test_sensor_compass_hm55b() throws Exception {
        compileAndCompare(new File(path, "sensor.compass.hm55b.spin"), new File(path, "sensor.compass.hm55b.binary"));
    }

    @Test
    void test_sensor_light2frequency_tsl230() throws Exception {
        compileAndCompare(new File(path, "sensor.light2frequency.tsl230.spin"), new File(path, "sensor.light2frequency.tsl230.binary"));
    }

    @Test
    void test_sensor_ping() throws Exception {
        compileAndCompare(new File(path, "sensor.ping.spin"), new File(path, "sensor.ping.binary"));
    }

    @Test
    void test_signal_adc() throws Exception {
        compileAndCompare(new File(path, "signal.adc.spin"), new File(path, "signal.adc.binary"));
    }

    @Test
    void test_signal_adc_mcp3208() throws Exception {
        compileAndCompare(new File(path, "signal.adc.mcp3208.spin"), new File(path, "signal.adc.mcp3208.binary"));
    }

    @Test
    void test_signal_dither() throws Exception {
        compileAndCompare(new File(path, "signal.dither.spin"), new File(path, "signal.dither.binary"));
    }

    @Test
    void test_signal_spatializer() throws Exception {
        compileAndCompare(new File(path, "signal.spatializer.spin"), new File(path, "signal.spatializer.binary"));
    }

    @Test
    void test_signal_synth() throws Exception {
        compileAndCompare(new File(path, "signal.synth.spin"), new File(path, "signal.synth.binary"));
    }

    @Test
    void test_signal_synth_speech() throws Exception {
        compileAndCompare(new File(path, "signal.synth.speech.spin"), new File(path, "signal.synth.speech.binary"));
    }

    @Test
    void test_string() throws Exception {
        compileAndCompare(new File(path, "string.spin"), new File(path, "string.binary"));
    }

    @Test
    // Comparison with binary from OpenSpin fails
    //  - different sequence of same-priority operators
    void test_string_float() throws Exception {
        compileAndCompare(new File(path, "string.float.spin"), new File(path, "string.float.binary"));
    }

    @Test
    // Comparison with binary from OpenSpin fails
    //  - different sequence of same-priority operators
    void test_string_integer() throws Exception {
        compileAndCompare(new File(path, "string.integer.spin"), new File(path, "string.integer.binary"));
    }

    @Test
    void test_string_type() throws Exception {
        compileAndCompare(new File(path, "string.type.spin"), new File(path, "string.type.binary"));
    }

    @Test
    void test_time_clock() throws Exception {
        compileAndCompare(new File(path, "time.clock.spin"), new File(path, "time.clock.binary"));
    }

    @Test
    void test_time() throws Exception {
        compileAndCompare(new File(path, "time.spin"), new File(path, "time.binary"));
    }

    @Test
    void test_tiny_com_i2c_spin() throws Exception {
        compileAndCompare(new File(path, "tiny.com.i2c.spin"), new File(path, "tiny.com.i2c.binary"));
    }

    @Test
    void test_tiny_com_serial_spin() throws Exception {
        compileAndCompare(new File(path, "tiny.com.serial.spin"), new File(path, "tiny.com.serial.binary"));
    }

    @Test
    void test_tiny_com_spi_spin() throws Exception {
        compileAndCompare(new File(path, "tiny.com.spi.spin"), new File(path, "tiny.com.spi.binary"));
    }

    @Test
    // Comparison with binary from OpenSpin fails
    //  - different sequence of same-priority operators
    void test_tiny_math_float_spin() throws Exception {
        compileAndCompare(new File(path, "tiny.math.float.spin"), new File(path, "tiny.math.float.binary"));
    }

    @Test
    void test_tiny_sensor_accel_dual_mxd2125() throws Exception {
        compileAndCompare(new File(path, "tiny.sensor.accel.dual.mxd2125.spin"), new File(path, "tiny.sensor.accel.dual.mxd2125.binary"));
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
