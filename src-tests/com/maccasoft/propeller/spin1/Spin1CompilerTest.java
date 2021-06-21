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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;

class Spin1CompilerTest {

    @Test
    void testCompileConstants() {
        String text = ""
            + "CON\n"
            + "\n"
            + "    _XINFREQ = 5_000_000\n"
            + "    _CLKMODE = XTAL1 + PLL16X\n"
            + "";

        Spin1TokenStream stream = new Spin1TokenStream(text);
        Spin1Parser parser = new Spin1Parser(stream);
        Node root = parser.parse();

        Spin1Compiler subject = new Spin1Compiler();
        subject.compileConBlock(root.getChild(0));

        Assertions.assertEquals("5_000_000", subject.scope.getSymbol("_XINFREQ").toString());
        Assertions.assertEquals("XTAL1 + PLL16X", subject.scope.getSymbol("_CLKMODE").toString());
    }

    @Test
    void testEmpty() {
        Spin1TokenStream stream = new Spin1TokenStream("");
        Spin1Parser parser = new Spin1Parser(stream);
        Node root = parser.parse();

        Spin1Compiler subject = new Spin1Compiler();
        subject.compile(root);

        Assertions.assertEquals(0x0010, subject.pbase);
        Assertions.assertEquals(0x0014, subject.vbase);
        Assertions.assertEquals(0x001C, subject.dbase);

        Assertions.assertEquals(0x0014, subject.pcurr);
        Assertions.assertEquals(0x0020, subject.dcurr);
    }

    @Test
    void testOneEmptyMethod() {
        Spin1TokenStream stream = new Spin1TokenStream("");
        Spin1Parser parser = new Spin1Parser(stream);
        Node root = parser.parse();

        Spin1Compiler subject = new Spin1Compiler();
        subject.methods.add(new Spin1Method(new Spin1Context(subject.scope)) {

            @Override
            public byte[] getBytes() {
                return new byte[] {
                    (byte) 0x32, //              RETURN_PLAIN
                };
            }

        });
        subject.compile(root);

        Assertions.assertEquals(0x0010, subject.pbase);
        Assertions.assertEquals(0x001C, subject.vbase);
        Assertions.assertEquals(0x0024, subject.dbase);

        Assertions.assertEquals(0x0018, subject.pcurr);
        Assertions.assertEquals(0x0028, subject.dcurr);
    }

    @Test
    void testTwoEmptyMethods() {
        Spin1TokenStream stream = new Spin1TokenStream("");
        Spin1Parser parser = new Spin1Parser(stream);
        Node root = parser.parse();

        Spin1Compiler subject = new Spin1Compiler();
        subject.methods.add(new Spin1Method(new Spin1Context(subject.scope)) {

            @Override
            public byte[] getBytes() {
                return new byte[] {
                    (byte) 0x32, //              RETURN_PLAIN
                };
            }

        });
        subject.methods.add(new Spin1Method(new Spin1Context(subject.scope)) {

            @Override
            public byte[] getBytes() {
                return new byte[] {
                    (byte) 0x32, //              RETURN_PLAIN
                };
            }

        });
        subject.compile(root);

        Assertions.assertEquals(0x0010, subject.pbase);
        Assertions.assertEquals(0x0020, subject.vbase);
        Assertions.assertEquals(0x0028, subject.dbase);

        Assertions.assertEquals(0x001C, subject.pcurr);
        Assertions.assertEquals(0x002C, subject.dcurr);
    }

    @Test
    void testTopMethodWithLocalVariables() {
        Spin1TokenStream stream = new Spin1TokenStream("");
        Spin1Parser parser = new Spin1Parser(stream);
        Node root = parser.parse();

        Spin1Compiler subject = new Spin1Compiler();
        subject.methods.add(new Spin1Method(subject.scope) {

            @Override
            public int getLocalSize() {
                return 4;
            }

            @Override
            public byte[] getBytes() {
                return new byte[] {
                    (byte) 0x32, //              RETURN_PLAIN
                };
            }

        });
        subject.methods.add(new Spin1Method(subject.scope) {

            @Override
            public byte[] getBytes() {
                return new byte[] {
                    (byte) 0x32, //              RETURN_PLAIN
                };
            }

        });
        subject.compile(root);

        Assertions.assertEquals(0x0010, subject.pbase);
        Assertions.assertEquals(0x0020, subject.vbase);
        Assertions.assertEquals(0x0028, subject.dbase);

        Assertions.assertEquals(0x001C, subject.pcurr);
        Assertions.assertEquals(0x0030, subject.dcurr);
    }

    @Test
    void testSecondMethodWithLocalVariables() {
        Spin1TokenStream stream = new Spin1TokenStream("");
        Spin1Parser parser = new Spin1Parser(stream);
        Node root = parser.parse();

        Spin1Compiler subject = new Spin1Compiler();
        subject.methods.add(new Spin1Method(new Spin1Context(subject.scope)) {

            @Override
            public byte[] getBytes() {
                return new byte[] {
                    (byte) 0x32, //              RETURN_PLAIN
                };
            }

        });
        subject.methods.add(new Spin1Method(new Spin1Context(subject.scope)) {

            @Override
            public int getLocalSize() {
                return 4;
            }

            @Override
            public byte[] getBytes() {
                return new byte[] {
                    (byte) 0x32, //              RETURN_PLAIN
                };
            }

        });
        subject.compile(root);

        Assertions.assertEquals(0x0010, subject.pbase);
        Assertions.assertEquals(0x0020, subject.vbase);
        Assertions.assertEquals(0x0028, subject.dbase);

        Assertions.assertEquals(0x001C, subject.pcurr);
        Assertions.assertEquals(0x002C, subject.dcurr);
    }

    @Test
    void testMethodsWithLocalVariables() {
        Spin1TokenStream stream = new Spin1TokenStream("");
        Spin1Parser parser = new Spin1Parser(stream);
        Node root = parser.parse();

        Spin1Compiler subject = new Spin1Compiler();
        subject.methods.add(new Spin1Method(new Spin1Context(subject.scope)) {

            @Override
            public int getLocalSize() {
                return 4;
            }

            @Override
            public byte[] getBytes() {
                return new byte[] {
                    (byte) 0x32, //              RETURN_PLAIN
                };
            }

        });
        subject.methods.add(new Spin1Method(subject.scope) {

            @Override
            public int getLocalSize() {
                return 8;
            }

            @Override
            public byte[] getBytes() {
                return new byte[] {
                    (byte) 0x32, //              RETURN_PLAIN
                };
            }

        });
        subject.compile(root);

        Assertions.assertEquals(0x0010, subject.pbase);
        Assertions.assertEquals(0x0020, subject.vbase);
        Assertions.assertEquals(0x0028, subject.dbase);

        Assertions.assertEquals(0x001C, subject.pcurr);
        Assertions.assertEquals(0x0030, subject.dcurr);
    }

    @Test
    void testResolveMethodAddress() {
        Spin1TokenStream stream = new Spin1TokenStream("");
        Spin1Parser parser = new Spin1Parser(stream);
        Node root = parser.parse();

        Spin1Compiler subject = new Spin1Compiler();
        subject.methods.add(new Spin1Method(new Spin1Context(subject.scope)) {

            @Override
            public int getLocalSize() {
                return 4;
            }

            @Override
            public byte[] getBytes() {
                return new byte[] {
                    (byte) 0x32, //              RETURN_PLAIN
                };
            }

        });
        subject.methods.add(new Spin1Method(new Spin1Context(subject.scope)) {

            @Override
            public int getLocalSize() {
                return 8;
            }

            @Override
            public byte[] getBytes() {
                return new byte[] {
                    (byte) 0x32, //              RETURN_PLAIN
                };
            }

        });
        subject.compile(root);

        Assertions.assertEquals(0x001C, subject.methods.get(0).getScope().getAddress());
        Assertions.assertEquals(0x001D, subject.methods.get(1).getScope().getAddress());
    }

}
