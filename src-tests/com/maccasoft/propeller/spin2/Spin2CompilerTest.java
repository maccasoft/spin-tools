/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.Node;

class Spin2CompilerTest {

    @Test
    void testDollarSymbol() throws Exception {
        String text = ""
            + "DAT             org   $000\n"
            + "\n"
            + "start\n"
            + "                asmclk\n"
            + "\n"
            + "                drvnot  #56\n"
            + "                jmp     #$-1\n"
            + "";

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser parser = new Spin2Parser(stream);
        Node root = parser.parse();

        Spin2Compiler compiler = new Spin2Compiler();
        Spin2Object obj = compiler.compile(root);

        Assertions.assertEquals(0x000L, compiler.source.get(0).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x000L, compiler.source.get(1).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x000L, compiler.source.get(2).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x002L, compiler.source.get(3).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x004L, compiler.source.get(4).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x006L, compiler.source.get(5).getScope().getSymbol("$").getNumber());
        Assertions.assertEquals(0x007L, compiler.source.get(6).getScope().getSymbol("$").getNumber());

        obj.generateBinary(new ByteArrayOutputStream());
    }

    @Test
    void testCompilePtr() {
        Spin2Compiler subject = new Spin2Compiler();

        Spin2Parser parser = new Spin2Parser(new Spin2TokenStream(""
            + "DAT    wrlong 0,ptra\n"
            + "       wrlong 0,ptra++\n"
            + "       wrlong 0,++ptra\n"
            + "       wrlong 0,ptra[3]\n"
            + "       wrlong 0,ptra--[3]\n"
            + "       wrlong 0,--ptra[3]\n"
            + ""));

        Node root = parser.parse();
        DataNode data0 = (DataNode) root.getChild(0);

        subject.compilerVisitor.visitDataLine((DataLineNode) data0.getChild(0));
        Spin2PAsmLine line = subject.source.get(0);
        Assertions.assertEquals("ptra", line.getArguments().get(1).getExpression().toString());
        Assertions.assertEquals(
            Spin2InstructionObject.decodeToString(0b1111_1100011_001_000000000_100000000),
            Spin2InstructionObject.decodeToString(line.getInstructionObject().getBytes()));

        subject.compilerVisitor.visitDataLine((DataLineNode) data0.getChild(1));
        line = subject.source.get(1);
        Assertions.assertEquals("ptra++", line.getArguments().get(1).getExpression().toString());
        Assertions.assertEquals(
            Spin2InstructionObject.decodeToString(0b1111_1100011_001_000000000_101100001),
            Spin2InstructionObject.decodeToString(line.getInstructionObject().getBytes()));

        subject.compilerVisitor.visitDataLine((DataLineNode) data0.getChild(2));
        line = subject.source.get(2);
        Assertions.assertEquals("++ptra", line.getArguments().get(1).getExpression().toString());
        Assertions.assertEquals(
            Spin2InstructionObject.decodeToString(0b1111_1100011_001_000000000_101000001),
            Spin2InstructionObject.decodeToString(line.getInstructionObject().getBytes()));

        subject.compilerVisitor.visitDataLine((DataLineNode) data0.getChild(3));
        line = subject.source.get(3);
        Assertions.assertEquals("ptra", line.getArguments().get(1).getExpression().toString());
        Assertions.assertEquals(3, line.getArguments().get(1).getCount());
        Assertions.assertEquals(
            Spin2InstructionObject.decodeToString(0b1111_1100011_001_000000000_100000011),
            Spin2InstructionObject.decodeToString(line.getInstructionObject().getBytes()));

        subject.compilerVisitor.visitDataLine((DataLineNode) data0.getChild(4));
        line = subject.source.get(4);
        Assertions.assertEquals("ptra--", line.getArguments().get(1).getExpression().toString());
        Assertions.assertEquals(3, line.getArguments().get(1).getCount());
        Assertions.assertEquals(
            Spin2InstructionObject.decodeToString(0b1111_1100011_001_000000000_101111101),
            Spin2InstructionObject.decodeToString(line.getInstructionObject().getBytes()));

        subject.compilerVisitor.visitDataLine((DataLineNode) data0.getChild(5));
        line = subject.source.get(5);
        Assertions.assertEquals("--ptra", line.getArguments().get(1).getExpression().toString());
        Assertions.assertEquals(3, line.getArguments().get(1).getCount());
        Assertions.assertEquals(
            Spin2InstructionObject.decodeToString(0b1111_1100011_001_000000000_101011101),
            Spin2InstructionObject.decodeToString(line.getInstructionObject().getBytes()));
    }

}
