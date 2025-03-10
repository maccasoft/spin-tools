/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.maccasoft.propeller.Compiler.FileSourceProvider;
import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.internal.FileUtils;
import com.maccasoft.propeller.model.Node;

@TestInstance(Lifecycle.PER_CLASS)
class Spin1ExamplesTest {

    static final String path = "examples/P1";
    static final String libraryPath = "library/spin1";

    @ParameterizedTest(name = "{0}.spin")
    @MethodSource("librarySourceFiles")
    void test_library(File sourceFile) throws Exception {
        File sourcePath = sourceFile.getParentFile();
        File binaryFile = new File(sourcePath, sourceFile.getName() + ".binary");
        compileAndCompare(new File(sourcePath, sourceFile.getName() + ".spin"), binaryFile);
    }

    private List<File> librarySourceFiles() {
        List<File> result = sourceFiles(new File(libraryPath));
        Collections.sort(result);
        return result;
    }

    @ParameterizedTest(name = "{0}.spin")
    @MethodSource("exampleSourceFiles")
    void test_examples(File sourceFile) throws Exception {
        File sourcePath = sourceFile.getParentFile();
        File binaryFile = new File(sourcePath, sourceFile.getName() + ".binary");
        compileAndCompare(new File(sourcePath, sourceFile.getName() + ".spin"), binaryFile);
    }

    private List<File> exampleSourceFiles() {
        List<File> result = sourceFiles(new File(path));
        Collections.sort(result);
        return result;
    }

    private static List<File> sourceFiles(File dir) {
        List<File> result = new ArrayList<>();

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            if (name.endsWith(".spin")) {
                name = name.substring(0, name.length() - 5);
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

    void compileAndCompare(File source) throws Exception {
        compileAndCompare(new File(source.getAbsolutePath() + ".spin"), new File(source.getAbsolutePath() + ".binary"));
    }

    void compileAndCompare(File source, File binary) throws Exception {
        String text = FileUtils.loadFromFile(source);
        Spin1Parser subject = new Spin1Parser(text);
        Node root = subject.parse();

        Spin1Compiler compiler = new Spin1Compiler();
        compiler.setSourceProvider(new FileSourceProvider(new File[] {
            source.getParentFile(),
            new File(path),
            new File(libraryPath)
        }));
        compiler.setOpenspinCompatible(true);
        Spin1Object obj = compiler.compile(source, root);
        for (CompilerException msg : compiler.getMessages()) {
            if (msg.type == CompilerException.ERROR) {
                throw msg;
            }
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateListing(new PrintStream(os));
        String actualListing = os.toString();

        byte[] expected = loadBinaryFromFile(binary);
        obj.setBytes(expected, 0);

        os = new ByteArrayOutputStream();
        obj.generateListing(new PrintStream(os));
        String expectedListing = os.toString();

        Assertions.assertEquals(expectedListing, actualListing);
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

}
