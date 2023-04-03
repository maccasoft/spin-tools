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

    String parse(String text) {
        Spin2CTreeBuilder builder = new Spin2CTreeBuilder(new Spin2CContext());

        CTokenStream stream = new CTokenStream(text);
        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF) {
                break;
            }
            if (".".equals(token.getText())) {
                Token nextToken = stream.peekNext();
                if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                    token = token.merge(stream.nextToken());
                }
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
