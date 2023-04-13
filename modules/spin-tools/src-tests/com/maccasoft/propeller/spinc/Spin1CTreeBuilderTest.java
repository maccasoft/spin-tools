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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin1.Spin1StatementNode;

class Spin1CTreeBuilderTest {

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
        Spin1CTreeBuilder builder = new Spin1CTreeBuilder(new Spin1CContext());

        CParser parser = new CParser(new CTokenStream(text));
        while (true) {
            Token token = parser.nextTokenSkipNL();
            if (token.type == Token.EOF) {
                break;
            }
            builder.addToken(token);
        }

        Spin1StatementNode root = builder.getRoot();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        print(new PrintStream(os), root, 0);
        return os.toString().replaceAll("\\r\\n", "\n");
    }

    void print(PrintStream out, Spin1StatementNode node, int indent) {
        if (indent != 0) {
            for (int i = 1; i < indent; i++) {
                out.print("     ");
            }
            out.print(" +-- ");
        }

        out.print("[" + node.getText().replaceAll("\n", "\\\\n") + "]");
        out.println();

        for (Spin1StatementNode child : node.getChilds()) {
            print(out, child, indent + 1);
        }
    }

}
