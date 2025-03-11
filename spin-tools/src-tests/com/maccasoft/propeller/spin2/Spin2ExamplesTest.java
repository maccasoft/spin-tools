/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.maccasoft.propeller.Compiler.FileSourceProvider;
import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.internal.FileUtils;
import com.maccasoft.propeller.model.RootNode;

@TestInstance(Lifecycle.PER_CLASS)
class Spin2ExamplesTest {

    static final String path = "examples/P2";
    static final String libraryPath = "library/spin2";

    @Test
    void test_Spin2_interpreter() throws Exception {
        compileAndCompare(new File("Spin2_interpreter.spin2"), new File("Spin2_interpreter.binary"));
    }

    @Test
    void test_Spin2_debugger() throws Exception {
        compileAndCompare(new File("Spin2_debugger.spin2"), new File("Spin2_debugger.binary"));
    }

    @Test
    void test_flash_bootloader() throws Exception {
        compileAndCompare(new File("flash_bootloader.spin2"), new File("flash_bootloader.binary"));
    }

    @Test
    void test_flash_loader() throws Exception {
        compileAndCompare(new File("flash_loader.spin2"), new File("flash_loader.binary"));
    }

    @Test
    void test_lz4stub() throws Exception {
        compileAndCompare(new File("lz4stub.spin2"), new File("lz4stub.binary"));
    }

    @ParameterizedTest(name = "{0}.spin2")
    @MethodSource("exampleSourceFiles")
    void test_examples(File sourceFile) throws Exception {
        File sourcePath = sourceFile.getParentFile();
        File binaryFile = new File(sourcePath, sourceFile.getName() + ".binary");
        compileAndCompare(new File(sourcePath, sourceFile.getName() + ".spin2"), binaryFile);
    }

    private List<File> exampleSourceFiles() {
        List<File> result = sourceFiles(new File(path));
        Collections.sort(result);
        return result;
    }

    @ParameterizedTest(name = "{0}.spin2")
    @MethodSource("librarySourceFiles")
    void test_library(File sourceFile) throws Exception {
        File sourcePath = sourceFile.getParentFile();
        File binaryFile = new File(sourcePath, sourceFile.getName() + ".binary");
        compileAndCompare(new File(sourcePath, sourceFile.getName() + ".spin2"), binaryFile);
    }

    private List<File> librarySourceFiles() {
        List<File> result = sourceFiles(new File(libraryPath));
        Collections.sort(result);
        return result;
    }

    private static List<File> sourceFiles(File dir) {
        List<File> result = new ArrayList<>();

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            if (name.endsWith(".spin2")) {
                name = name.substring(0, name.length() - 6);
                File binaryFile = new File(dir, name + ".binary");
                if (binaryFile.exists()) {
                    result.add(new File(dir, name));
                }
            }
            else if (!".".equals(name) && !"..".equals(name)) {
                if (files[i].isDirectory()) {
                    result.addAll(sourceFiles(files[i]));
                }
            }
        }

        return result;
    }

    void compileAndCompare(File source, File binary) throws Exception {
        String text = FileUtils.replaceTabs(loadFromFile(source), 8);
        byte[] expected = loadBinaryFromFile(binary);

        Spin2Parser subject = new Spin2Parser(text);
        RootNode root = subject.parse();

        Spin2Compiler compiler = new Spin2Compiler();
        compiler.setSourceProvider(new FileSourceProvider(new File[] {
            source.getParentFile(),
            new File(path),
            new File(libraryPath)
        }));
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
