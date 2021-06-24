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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Token;

/*

Spin1BytecodeExpression [:=]
+--- Spin1BytecodeExpression [a]
+--- Spin1BytecodeExpression [1]


Spin1BytecodeExpression [:=]
+--- Spin1BytecodeExpression [a]
+--- Spin1BytecodeExpression [+]
     +--- Spin1BytecodeExpression [1]
     +--- Spin1BytecodeExpression [*]
          +--- Spin1BytecodeExpression [2]
          +--- Spin1BytecodeExpression [3]

Spin1BytecodeExpression [:=]
+--- Spin1BytecodeExpression [b]
+--- Spin1BytecodeExpression [*]
     +--- Spin1BytecodeExpression [(]
     |    +--- Spin1BytecodeExpression [+]
     |         +--- Spin1BytecodeExpression [1]
     |         +--- Spin1BytecodeExpression [2]
     +--- Spin1BytecodeExpression [3]

Spin1BytecodeExpression [coginit]
+--- Spin1BytecodeExpression [,]
     +--- Spin1BytecodeExpression [cogid]
     +--- Spin1BytecodeExpression [@start]
     +--- Spin1BytecodeExpression [0]

 */

class Spin1BytecodeInstructionFactoryTest {

    @Test
    void testExpandBinaryOperation() {
        Spin1BytecodeLine line = new Spin1BytecodeLine(new Spin1Context(), null, "+", new ArrayList<>());
        line.arguments.add(new Spin1BytecodeExpression(new Token(Token.NUMBER, "1")));
        line.arguments.add(new Spin1BytecodeExpression(new Token(Token.NUMBER, "2")));

        List<Spin1BytecodeLine> list = line.expand();

        Assertions.assertEquals(3, list.size());
        Assertions.assertEquals("1", list.get(0).mnemonic);
        Assertions.assertEquals("2", list.get(1).mnemonic);
        Assertions.assertEquals("+", list.get(2).mnemonic);
    }

    @Test
    void testExpandAssignment() {
        Spin1BytecodeLine line = new Spin1BytecodeLine(new Spin1Context(), null, ":=", new ArrayList<>());
        line.arguments.add(new Spin1BytecodeExpression(new Token(Token.NUMBER, "a")));
        line.arguments.add(new Spin1BytecodeExpression(new Token(Token.NUMBER, "1")));

        List<Spin1BytecodeLine> list = line.expand();

        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("1", list.get(0).mnemonic);
        Assertions.assertEquals(":=", list.get(1).mnemonic);
        Assertions.assertEquals("a", list.get(1).getArgument(0).getText());
    }

    @Test
    void testExpandGroup() {
        Spin1BytecodeLine line = new Spin1BytecodeLine(new Spin1Context(), null, "(", new ArrayList<>());
        line.arguments.add(new Spin1BytecodeExpression(new Token(Token.NUMBER, "+")));
        line.arguments.get(0).addChild(new Spin1BytecodeExpression(new Token(Token.NUMBER, "1")));
        line.arguments.get(0).addChild(new Spin1BytecodeExpression(new Token(Token.NUMBER, "2")));

        List<Spin1BytecodeLine> list = line.expand();

        Assertions.assertEquals(3, list.size());
        Assertions.assertEquals("1", list.get(0).mnemonic);
        Assertions.assertEquals("2", list.get(1).mnemonic);
        Assertions.assertEquals("+", list.get(2).mnemonic);
    }

    @Test
    void testExpandArgumentsList() {
        Spin1BytecodeLine line = new Spin1BytecodeLine(new Spin1Context(), null, "coginit", new ArrayList<>());
        line.arguments.add(new Spin1BytecodeExpression(new Token(Token.NUMBER, ",")));
        line.arguments.get(0).addChild(new Spin1BytecodeExpression(new Token(Token.NUMBER, "cogid")));
        line.arguments.get(0).addChild(new Spin1BytecodeExpression(new Token(Token.NUMBER, "@start")));
        line.arguments.get(0).addChild(new Spin1BytecodeExpression(new Token(Token.NUMBER, "0")));

        List<Spin1BytecodeLine> list = line.expand();

        Assertions.assertEquals(4, list.size());
        Assertions.assertEquals("cogid", list.get(0).getMnemonic());
        Assertions.assertEquals("@start", list.get(1).getMnemonic());
        Assertions.assertEquals("0", list.get(2).getMnemonic());
        Assertions.assertEquals("coginit", list.get(3).getMnemonic());
        Assertions.assertEquals(3, list.get(3).getArgumentCount());
    }

}
