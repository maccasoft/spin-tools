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

import java.util.Collections;

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
        Assertions.assertEquals(0x001C, subject.dcurr);
    }

    @Test
    void testOneEmptyMethod() {
        Spin1TokenStream stream = new Spin1TokenStream("");
        Spin1Parser parser = new Spin1Parser(stream);
        Node root = parser.parse();

        Spin1Compiler subject = new Spin1Compiler();

        Spin1Method method = new Spin1Method(new Spin1Context(subject.scope));
        method.source.add(new Spin1BytecodeLine(new Spin1Context(subject.scope), null, "return", Collections.emptyList()));
        subject.methods.add(method);

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

        Spin1Method method1 = new Spin1Method(new Spin1Context(subject.scope));
        method1.source.add(new Spin1BytecodeLine(new Spin1Context(subject.scope), null, "return", Collections.emptyList()));
        subject.methods.add(method1);

        Spin1Method method2 = new Spin1Method(new Spin1Context(subject.scope));
        method2.source.add(new Spin1BytecodeLine(new Spin1Context(subject.scope), null, "return", Collections.emptyList()));
        subject.methods.add(method2);

        subject.compile(root);

        Assertions.assertEquals(0x0010, subject.pbase);
        Assertions.assertEquals(0x0020, subject.vbase);
        Assertions.assertEquals(0x0028, subject.dbase);

        Assertions.assertEquals(0x001C, subject.pcurr);
        Assertions.assertEquals(0x0030, subject.dcurr);
    }

    @Test
    void testTopMethodWithLocalVariables() {
        Spin1TokenStream stream = new Spin1TokenStream("");
        Spin1Parser parser = new Spin1Parser(stream);
        Node root = parser.parse();

        Spin1Compiler subject = new Spin1Compiler();

        Spin1Method method1 = new Spin1Method(new Spin1Context(subject.scope));
        method1.setLocalSize(method1.getLocalSize() + 4);
        method1.source.add(new Spin1BytecodeLine(new Spin1Context(subject.scope), null, "return", Collections.emptyList()));
        subject.methods.add(method1);

        Spin1Method method2 = new Spin1Method(new Spin1Context(subject.scope));
        method2.source.add(new Spin1BytecodeLine(new Spin1Context(subject.scope), null, "return", Collections.emptyList()));
        subject.methods.add(method2);

        subject.compile(root);

        Assertions.assertEquals(0x0010, subject.pbase);
        Assertions.assertEquals(0x0020, subject.vbase);
        Assertions.assertEquals(0x0028, subject.dbase);

        Assertions.assertEquals(0x001C, subject.pcurr);
        Assertions.assertEquals(0x0034, subject.dcurr);
    }

    @Test
    void testSecondMethodWithLocalVariables() {
        Spin1TokenStream stream = new Spin1TokenStream("");
        Spin1Parser parser = new Spin1Parser(stream);
        Node root = parser.parse();

        Spin1Compiler subject = new Spin1Compiler();

        Spin1Method method1 = new Spin1Method(new Spin1Context(subject.scope));
        method1.source.add(new Spin1BytecodeLine(new Spin1Context(subject.scope), null, "return", Collections.emptyList()));
        subject.methods.add(method1);

        Spin1Method method2 = new Spin1Method(new Spin1Context(subject.scope));
        method2.setLocalSize(method1.getLocalSize() + 4);
        method2.source.add(new Spin1BytecodeLine(new Spin1Context(subject.scope), null, "return", Collections.emptyList()));
        subject.methods.add(method2);

        subject.compile(root);

        Assertions.assertEquals(0x0010, subject.pbase);
        Assertions.assertEquals(0x0020, subject.vbase);
        Assertions.assertEquals(0x0028, subject.dbase);

        Assertions.assertEquals(0x001C, subject.pcurr);
        Assertions.assertEquals(0x0034, subject.dcurr);
    }

    @Test
    void testMethodsWithLocalVariables() {
        Spin1TokenStream stream = new Spin1TokenStream("");
        Spin1Parser parser = new Spin1Parser(stream);
        Node root = parser.parse();

        Spin1Compiler subject = new Spin1Compiler();

        Spin1Method method1 = new Spin1Method(new Spin1Context(subject.scope));
        method1.setLocalSize(method1.getLocalSize() + 4);
        method1.source.add(new Spin1BytecodeLine(new Spin1Context(subject.scope), null, "return", Collections.emptyList()));
        subject.methods.add(method1);

        Spin1Method method2 = new Spin1Method(new Spin1Context(subject.scope));
        method2.setLocalSize(method1.getLocalSize() + 4);
        method2.source.add(new Spin1BytecodeLine(new Spin1Context(subject.scope), null, "return", Collections.emptyList()));
        subject.methods.add(method2);

        subject.compile(root);

        Assertions.assertEquals(0x0010, subject.pbase);
        Assertions.assertEquals(0x0020, subject.vbase);
        Assertions.assertEquals(0x0028, subject.dbase);

        Assertions.assertEquals(0x001C, subject.pcurr);
        Assertions.assertEquals(0x003C, subject.dcurr);
    }

    @Test
    void testResolveMethodAddress() {
        Spin1TokenStream stream = new Spin1TokenStream("");
        Spin1Parser parser = new Spin1Parser(stream);
        Node root = parser.parse();

        Spin1Compiler subject = new Spin1Compiler();

        Spin1Method method1 = new Spin1Method(new Spin1Context(subject.scope));
        method1.setLocalSize(method1.getLocalSize() + 4);
        method1.source.add(new Spin1BytecodeLine(new Spin1Context(subject.scope), null, "return", Collections.emptyList()));
        subject.methods.add(method1);

        Spin1Method method2 = new Spin1Method(new Spin1Context(subject.scope));
        method2.setLocalSize(method1.getLocalSize() + 8);
        method2.source.add(new Spin1BytecodeLine(new Spin1Context(subject.scope), null, "return", Collections.emptyList()));
        subject.methods.add(method2);

        subject.compile(root);

        Assertions.assertEquals(0x001C, subject.methods.get(0).getScope().getHubAddress());
        Assertions.assertEquals(0x001D, subject.methods.get(1).getScope().getHubAddress());
    }

}
