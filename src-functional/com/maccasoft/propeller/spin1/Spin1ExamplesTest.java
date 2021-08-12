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
    void test_char_Types() throws Exception {
        compileAndCompare(new File(path + "/char", "Types.spin"), new File(path + "/char", "Types.binary"));
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

    @Test
    void test_com_serial_terminal_Demo() throws Exception {
        compileAndCompare(new File(path + "/com/serial/terminal", "Demo.spin"), new File(path, "com/serial/terminal/Demo.binary"));
    }

    @Test
    void test_com_serial_terminal_HelloWorld() throws Exception {
        compileAndCompare(new File(path + "/com/serial/terminal", "HelloWorld.spin"), new File(path, "com/serial/terminal/HelloWorld.binary"));
    }

    @Test
    void test_com_serial_terminal_InputNumbers() throws Exception {
        compileAndCompare(new File(path + "/com/serial/terminal", "InputNumbers.spin"), new File(path, "com/serial/terminal/InputNumbers.binary"));
    }

    @Test
    void test_com_serial_terminal_ReadLine() throws Exception {
        compileAndCompare(new File(path + "/com/serial/terminal", "ReadLine.spin"), new File(path, "com/serial/terminal/ReadLine.binary"));
    }

    @Test
    void test_com_spi_DS1620TemperatureSensing() throws Exception {
        compileAndCompare(new File(path + "/com/spi", "DS1620TemperatureSensing"));
    }

    @Test
    void test_display_Graphics_Demo() throws Exception {
        compileAndCompare(new File(path + "/display", "Graphics_Demo"));
    }

    @Test
    void test_display_Graphics_Palette() throws Exception {
        compileAndCompare(new File(path + "/display", "Graphics_Palette"));
    }

    @Test
    void test_display_TV_Terminal_Demo() throws Exception {
        compileAndCompare(new File(path + "/display", "TV_Terminal_Demo"));
    }

    @Test
    void test_display_TV_Text_Demo() throws Exception {
        compileAndCompare(new File(path + "/display", "TV_Text_Demo"));
    }

    @Test
    void test_input_Keyboard() throws Exception {
        compileAndCompare(new File(path + "/input", "Keyboard.spin"), new File(path + "/input", "Keyboard.binary"));
    }

    @Test
    void test_input_Keypad4x4() throws Exception {
        compileAndCompare(new File(path + "/input", "Keypad4x4.spin"), new File(path + "/input", "Keypad4x4.binary"));
    }

    @Test
    void test_input_TrimAD8803() throws Exception {
        compileAndCompare(new File(path + "/input", "TrimAD8803.spin"), new File(path + "/input", "TrimAD8803.binary"));
    }

    @Test
    void test_math_float_FrequencyTable() throws Exception {
        compileAndCompare(new File(path + "/math/float", "FrequencyTable"));
    }

    @Test
    void test_math_float_LogTable() throws Exception {
        compileAndCompare(new File(path + "/math/float", "LogTable"));
    }

    @Test
    void test_math_random_RandomNumbers() throws Exception {
        compileAndCompare(new File(path + "/math/random", "RandomNumbers"));
    }

    @Test
    void test_math_random_WhiteNoise() throws Exception {
        compileAndCompare(new File(path + "/math/random", "WhiteNoise"));
    }

    @Test
    void test_string_Basics() throws Exception {
        compileAndCompare(new File(path + "/string", "Basics.spin"), new File(path + "/string", "Basics.binary"));
    }

    @Test
    void test_string_Calculator() throws Exception {
        compileAndCompare(new File(path + "/string", "Calculator.spin"), new File(path + "/string", "Calculator.binary"));
    }

    @Test
    void test_string_CommandLine() throws Exception {
        compileAndCompare(new File(path + "/string", "CommandLine.spin"), new File(path + "/string", "CommandLine.binary"));
    }

    @Test
    void test_string_CopyAppend() throws Exception {
        compileAndCompare(new File(path + "/string", "CopyAppend.spin"), new File(path + "/string", "CopyAppend.binary"));
    }

    @Test
    void test_string_LeftMidRight() throws Exception {
        compileAndCompare(new File(path + "/string", "LeftMidRight.spin"), new File(path + "/string", "LeftMidRight.binary"));
    }

    @Test
    void test_string_Replace() throws Exception {
        compileAndCompare(new File(path + "/string", "Replace.spin"), new File(path + "/string", "Replace.binary"));
    }

    @Test
    void test_string_Tokenize() throws Exception {
        compileAndCompare(new File(path + "/string", "Tokenize.spin"), new File(path + "/string", "Tokenize.binary"));
    }

    @Test
    void test_string_UpperLower() throws Exception {
        compileAndCompare(new File(path + "/string", "UpperLower.spin"), new File(path + "/string", "UpperLower.binary"));
    }

    @Test
    void test_string_integer_PrintNumbers() throws Exception {
        compileAndCompare(new File(path + "/string/integer", "PrintNumbers.spin"), new File(path + "/string/integer", "PrintNumbers.binary"));
    }

    @Test
    void test_system_Counters() throws Exception {
        compileAndCompare(new File(path + "/system", "Counters.spin"), new File(path + "/system", "Counters.binary"));
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

    void compileAndCompare(File source) throws Exception {
        compileAndCompare(new File(source.getAbsolutePath() + ".spin"), new File(source.getAbsolutePath() + ".binary"));
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
