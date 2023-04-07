/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.Compiler.FileSourceProvider;
import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin2.Spin2Debugger;
import com.maccasoft.propeller.spin2.Spin2Object;

class Spin2CExamplesTest {

    static final String path = "examples/P2";
    static final String libraryPath = "library/spin2";

    @Test
    void test_i2c_scanner() throws Exception {
        compileAndCompare(new File(path, "i2c_scanner.c"), new File(path, "i2c_scanner.binary"));
    }

    @Test
    void testPNutBlink() throws Exception {
        String text = ""
            + "#define _CLKFREQ 160_000_000\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "    for(;;) {\n"
            + "        pinwrite(addpins(56, 7), getrnd());\n"
            + "        waitms(100);\n"
            + "    }\n"
            + "}\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 4)\n"
            + "00000 00000       08 00 00 80    Method main @ $00008 (0 parameters, 0 returns)\n"
            + "00004 00004       15 00 00 00    End\n"
            + "' void main() {\n"
            + "00008 00008       00             (stack size)\n"
            + "'     for(;;) {\n"
            + "'         pinwrite(addpins(56, 7), getrnd());\n"
            + "00009 00009       46 F8 01       CONSTANT (56 addpins 7)\n"
            + "0000C 0000C       31             GETRND\n"
            + "0000D 0000D       35             PINWRITE\n"
            + "'         waitms(100);\n"
            + "0000E 0000E       44 64          CONSTANT (100)\n"
            + "00010 00010       19 80          WAITMS\n"
            + "00012 00012       12 76          JMP $00009 (-10)\n"
            + "'     }\n"
            + "' }\n"
            + "00014 00014       04             RETURN\n"
            + "00015 00015       00 00 00       Padding\n"
            + "", compile(text));
    }

    @Test
    void testFlexspinBlinkCog() throws Exception {
        String text = ""
            + "#define _CLKFREQ 160_000_000\n"
            + "\n"
            + "#define BASEPIN 56\n"
            + "#define TOGGLE_DELAY 250\n"
            + "\n"
            + "long stack[64];\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "    int cog;\n"
            + "\n"
            + "    cog = cogspin(NEWCOG, blink(BASEPIN, TOGGLE_DELAY), &stack);\n"
            + "\n"
            + "    for(;;) {\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "void blink(int pin, int delay)\n"
            + "{\n"
            + "    for(;;) {\n"
            + "        pintoggle(pin);\n"
            + "        waitms(delay);\n"
            + "    }\n"
            + "}\n"
            + "";

        Assertions.assertEquals(""
            + "' Object header (var size 260)\n"
            + "00000 00000       0C 00 00 80    Method main @ $0000C (0 parameters, 0 returns)\n"
            + "00004 00004       1F 00 00 82    Method blink @ $0001F (2 parameters, 0 returns)\n"
            + "00008 00008       28 00 00 00    End\n"
            + "' void main() {\n"
            + "0000C 0000C       01             (stack size)\n"
            + "'     cog = cogspin(NEWCOG, blink(BASEPIN, TOGGLE_DELAY), &stack);\n"
            + "0000D 0000D       44 10          CONSTANT (16)\n"
            + "0000F 0000F       44 38          CONSTANT (56)\n"
            + "00011 00011       44 FA          CONSTANT (250)\n"
            + "00013 00013       11 01          SUB_ADDRESS (1)\n"
            + "00015 00015       C1 7F          VAR_ADDRESS VBASE+$00001 (short)\n"
            + "00017 00017       19 5A          COGSPIN\n"
            + "00019 00019       02 26          POP_RETURN (???)\n"
            + "0001B 0001B       F0             VAR_WRITE LONG DBASE+$00000 (short)\n"
            + "'     for(;;) {\n"
            + "0001C 0001C       12 7F          JMP $0001C (-1)\n"
            + "'     }\n"
            + "' }\n"
            + "0001E 0001E       04             RETURN\n"
            + "' void blink(int pin, int delay) {\n"
            + "0001F 0001F       00             (stack size)\n"
            + "'     for(;;) {\n"
            + "'         pintoggle(pin);\n"
            + "00020 00020       E0             VAR_READ LONG DBASE+$00000 (short)\n"
            + "00021 00021       38             PINTOGGLE\n"
            + "'         waitms(delay);\n"
            + "00022 00022       E1             VAR_READ LONG DBASE+$00001 (short)\n"
            + "00023 00023       19 80          WAITMS\n"
            + "00025 00025       12 7A          JMP $00020 (-6)\n"
            + "'     }\n"
            + "' }\n"
            + "00027 00027       04             RETURN\n"
            + "", compile(text));
    }

    String compile(String text) throws Exception {
        return compile(text, false);
    }

    String compile(String text, boolean debugEnabled) throws Exception {
        CTokenStream stream = new CTokenStream(text);
        CParser subject = new CParser(stream);
        Node root = subject.parse();

        Spin2CObjectCompiler compiler = new Spin2CObjectCompiler(new Spin2CCompiler(), new ArrayList<>());
        compiler.debugEnabled = debugEnabled;
        Spin2Object obj = compiler.compileObject(root);
        if (debugEnabled) {
            obj.setDebugData(compiler.generateDebugData());
            obj.setDebugger(new Spin2Debugger());
        }

        for (CompilerException msg : compiler.getMessages()) {
            if (msg.type == CompilerException.ERROR) {
                throw msg;
            }
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        obj.generateListing(new PrintStream(os));

        return os.toString().replaceAll("\\r\\n", "\n");
    }

    void compileAndCompare(File source, File binary) throws Exception {
        String text = loadFromFile(source);
        byte[] expected = loadBinaryFromFile(binary);

        CTokenStream stream = new CTokenStream(text);
        CParser subject = new CParser(stream);
        Node root = subject.parse();

        Spin2CCompiler compiler = new Spin2CCompiler();
        compiler.addSourceProvider(new FileSourceProvider(new File[] {
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
