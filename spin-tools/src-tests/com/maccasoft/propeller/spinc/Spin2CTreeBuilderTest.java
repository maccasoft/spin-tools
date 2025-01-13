/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin2.Spin2StatementNode;

class Spin2CTreeBuilderTest {

    @Test
    void testMultipleStatements() {
        String text = "a = 1, b = 2, c = 3";
        Assertions.assertEquals(""
            + "[,]\n"
            + " +-- [=]\n"
            + "      +-- [a]\n"
            + "      +-- [1]\n"
            + " +-- [=]\n"
            + "      +-- [b]\n"
            + "      +-- [2]\n"
            + " +-- [=]\n"
            + "      +-- [c]\n"
            + "      +-- [3]\n"
            + "", parse(text));
    }

    @Test
    void testPostEffect() {
        String text = "a++";
        Assertions.assertEquals(""
            + "[a]\n"
            + " +-- [++]\n"
            + "", parse(text));
    }

    @Test
    void testPointerDereference() {
        String text = "*a";
        Assertions.assertEquals(""
            + "[*]\n"
            + " +-- [a]\n"
            + "", parse(text));
    }

    @Test
    void testPointerDereferencePostEffect() {
        String text = "*a++";
        Assertions.assertEquals(""
            + "[*]\n"
            + " +-- [a]\n"
            + "      +-- [++]\n"
            + "", parse(text));
    }

    @Test
    void testStructureArray() {
        String text = "obj[1].element";
        Assertions.assertEquals(""
            + "[obj]\n"
            + " +-- [1]\n"
            + " +-- [.element]\n"
            + "", parse(text));
    }

    @Test
    void testStructureArrayPostEffect() {
        String text = "obj[1].element++";
        Assertions.assertEquals(""
            + "[obj]\n"
            + " +-- [1]\n"
            + " +-- [.element]\n"
            + "      +-- [++]\n"
            + "", parse(text));
    }

    @Test
    void testStructureArrayIndex() {
        String text = "obj[1].element[2]";
        Assertions.assertEquals(""
            + "[obj]\n"
            + " +-- [1]\n"
            + " +-- [.element]\n"
            + "      +-- [2]\n"
            + "", parse(text));
    }

    @Test
    void testStructureArrayIndexPostEffect() {
        String text = "obj[1].element[2]++";
        Assertions.assertEquals(""
            + "[obj]\n"
            + " +-- [1]\n"
            + " +-- [.element]\n"
            + "      +-- [2]\n"
            + "      +-- [++]\n"
            + "", parse(text));
    }

    String parse(String text) {
        Spin2CTreeBuilder builder = new Spin2CTreeBuilder(new Context());

        CParser parser = new CParser(new CTokenStream(text));
        while (true) {
            Token token = parser.nextTokenSkipNL();
            if (token.type == Token.EOF) {
                break;
            }
            builder.addToken(token);
        }

        Spin2StatementNode root = builder.getRoot();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        print(new PrintStream(os), root, 0);
        return os.toString().replaceAll("\\r\\n", "\n");
    }

    void print(PrintStream out, Spin2StatementNode node, int indent) {
        if (indent != 0) {
            for (int i = 1; i < indent; i++) {
                out.print("     ");
            }
            out.print(" +-- ");
        }

        out.print("[" + node.getText().replaceAll("\n", "\\\\n") + "]");
        out.println();

        for (Spin2StatementNode child : node.getChilds()) {
            print(out, child, indent + 1);
        }
    }

}
