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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.ErrorNode;
import com.maccasoft.propeller.model.Node;

class Spin2ParseDatLineTest {

    @Test
    void testParseLabel() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "DAT\n"
            + "label\n"
            + ""));

        Node root = subject.parse();
        DataLineNode line0 = (DataLineNode) root.getChild(0).getChild(0);

        Assertions.assertEquals("label", line0.label.getText());
        Assertions.assertNull(line0.condition);
        Assertions.assertNull(line0.instruction);
        Assertions.assertEquals(0, line0.parameters.size());
        Assertions.assertNull(line0.modifier);

        Assertions.assertEquals("label", line0.getText());
    }

    @Test
    void testParseCondition() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "DAT\n"
            + "        if_c\n"
            + ""));

        Node root = subject.parse();
        DataLineNode line0 = (DataLineNode) root.getChild(0).getChild(0);

        Assertions.assertNull(line0.label);
        Assertions.assertEquals("if_c", line0.condition.getText());
        Assertions.assertNull(line0.instruction);
        Assertions.assertEquals(0, line0.parameters.size());
        Assertions.assertNull(line0.modifier);

        Assertions.assertEquals("        if_c", line0.getText());
    }

    @Test
    void testParseInstruction() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "DAT\n"
            + "                mov\n"
            + ""));

        Node root = subject.parse();
        DataLineNode line0 = (DataLineNode) root.getChild(0).getChild(0);

        Assertions.assertNull(line0.label);
        Assertions.assertNull(line0.condition);
        Assertions.assertEquals("mov", line0.instruction.getText());
        Assertions.assertEquals(0, line0.parameters.size());
        Assertions.assertNull(line0.modifier);

        Assertions.assertEquals("                mov", line0.getText());
    }

    @Test
    void testParseInstructionParameters() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "DAT\n"
            + "                mov     a, b\n"
            + ""));

        Node root = subject.parse();
        DataLineNode line0 = (DataLineNode) root.getChild(0).getChild(0);

        Assertions.assertNull(line0.label);
        Assertions.assertNull(line0.condition);
        Assertions.assertEquals("mov", line0.instruction.getText());
        Assertions.assertEquals(2, line0.parameters.size());
        Assertions.assertEquals("a", line0.parameters.get(0).getText());
        Assertions.assertEquals("b", line0.parameters.get(1).getText());
        Assertions.assertNull(line0.modifier);

        Assertions.assertEquals("                mov     a, b", line0.getText());
    }

    @Test
    void testParseModifier() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "DAT\n"
            + "                mov     a, b    wz\n"
            + ""));

        Node root = subject.parse();
        DataLineNode line0 = (DataLineNode) root.getChild(0).getChild(0);

        Assertions.assertNull(line0.label);
        Assertions.assertNull(line0.condition);
        Assertions.assertEquals("mov", line0.instruction.getText());
        Assertions.assertEquals(2, line0.parameters.size());
        Assertions.assertEquals("a", line0.parameters.get(0).getText());
        Assertions.assertEquals("b", line0.parameters.get(1).getText());
        Assertions.assertEquals("wz", line0.modifier.getText());

        Assertions.assertEquals("                mov     a, b    wz", line0.getText());
    }

    @Test
    void testParseFullLine() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "DAT\n"
            + "label   if_c    mov     a, b    wz\n"
            + ""));

        Node root = subject.parse();
        DataLineNode line0 = (DataLineNode) root.getChild(0).getChild(0);

        Assertions.assertEquals("label", line0.label.getText());
        Assertions.assertEquals("if_c", line0.condition.getText());
        Assertions.assertEquals("mov", line0.instruction.getText());
        Assertions.assertEquals(2, line0.parameters.size());
        Assertions.assertEquals("a", line0.parameters.get(0).getText());
        Assertions.assertEquals("b", line0.parameters.get(1).getText());
        Assertions.assertEquals("wz", line0.modifier.getText());

        Assertions.assertEquals("label   if_c    mov     a, b    wz", line0.getText());
    }

    @Test
    void testUnknownCondition() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "DAT\n"
            + "label   if_c1   mov     a, b    wz\n"
            + ""));

        Node root = subject.parse();
        DataLineNode line0 = (DataLineNode) root.getChild(0).getChild(0);

        Assertions.assertEquals("label", line0.label.getText());
        Assertions.assertNull(line0.condition);
        Assertions.assertNull(line0.instruction);
        Assertions.assertEquals(0, line0.parameters.size());
        Assertions.assertNull(line0.modifier);

        Assertions.assertEquals(ErrorNode.class, line0.getChild(line0.getChilds().size() - 1).getClass());

        Assertions.assertEquals("label   if_c1   mov     a, b    wz", line0.getText());
    }

    @Test
    void testUnknownInstruction() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "DAT\n"
            + "label   if_c    mov1    a, b    wz\n"
            + ""));

        Node root = subject.parse();
        DataLineNode line0 = (DataLineNode) root.getChild(0).getChild(0);

        Assertions.assertEquals("label", line0.label.getText());
        Assertions.assertEquals("if_c", line0.condition.getText());
        Assertions.assertNull(line0.instruction);
        Assertions.assertEquals(0, line0.parameters.size());
        Assertions.assertNull(line0.modifier);

        Assertions.assertEquals(ErrorNode.class, line0.getChild(line0.getChilds().size() - 1).getClass());

        Assertions.assertEquals("label   if_c    mov1    a, b    wz", line0.getText());
    }

    @Test
    void testUnknownModifierParsedAsParameter() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "DAT\n"
            + "label   if_c    mov     a, b    wz1\n"
            + ""));

        Node root = subject.parse();
        DataLineNode line0 = (DataLineNode) root.getChild(0).getChild(0);

        Assertions.assertEquals("label", line0.label.getText());
        Assertions.assertEquals("if_c", line0.condition.getText());
        Assertions.assertEquals("mov", line0.instruction.getText());
        Assertions.assertEquals(2, line0.parameters.size());
        Assertions.assertEquals("a", line0.parameters.get(0).getText());
        Assertions.assertEquals("b    wz1", line0.parameters.get(1).getText());
        Assertions.assertNull(line0.modifier);

        Assertions.assertEquals("label   if_c    mov     a, b    wz1", line0.getText());
    }

    @Test
    void testUnknownModifier() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "DAT\n"
            + "label   if_c    mov     a, b    wz,wc1\n"
            + ""));

        Node root = subject.parse();
        DataLineNode line0 = (DataLineNode) root.getChild(0).getChild(0);

        Assertions.assertEquals("label", line0.label.getText());
        Assertions.assertEquals("if_c", line0.condition.getText());
        Assertions.assertEquals("mov", line0.instruction.getText());
        Assertions.assertEquals(2, line0.parameters.size());
        Assertions.assertEquals("a", line0.parameters.get(0).getText());
        Assertions.assertEquals("b", line0.parameters.get(1).getText());
        Assertions.assertEquals("wz,", line0.modifier.getText());

        Assertions.assertEquals(ErrorNode.class, line0.getChild(line0.getChilds().size() - 1).getClass());

        Assertions.assertEquals("label   if_c    mov     a, b    wz,wc1", line0.getText());
    }

    @Test
    void testParseData() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "DAT\n"
            + "                long    1, 2, 3, 4\n"
            + ""));

        Node root = subject.parse();
        DataLineNode line0 = (DataLineNode) root.getChild(0).getChild(0);

        Assertions.assertNull(line0.label);
        Assertions.assertNull(line0.condition);
        Assertions.assertEquals("long", line0.instruction.getText());
        Assertions.assertEquals(4, line0.parameters.size());
        Assertions.assertEquals("1", line0.parameters.get(0).getText());
        Assertions.assertEquals("2", line0.parameters.get(1).getText());
        Assertions.assertEquals("3", line0.parameters.get(2).getText());
        Assertions.assertEquals("4", line0.parameters.get(3).getText());
        Assertions.assertNull(line0.modifier);

        Assertions.assertEquals("                long    1, 2, 3, 4", line0.getText());
    }

    @Test
    void testParseDataArray() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "DAT\n"
            + "                long    1[2], 3, 4, 5[6]\n"
            + ""));

        Node root = subject.parse();
        DataLineNode line0 = (DataLineNode) root.getChild(0).getChild(0);

        Assertions.assertNull(line0.label);
        Assertions.assertNull(line0.condition);
        Assertions.assertEquals("long", line0.instruction.getText());
        Assertions.assertEquals(4, line0.parameters.size());
        Assertions.assertEquals("1", line0.parameters.get(0).getText());
        Assertions.assertEquals("2", line0.parameters.get(0).count.getText());
        Assertions.assertEquals("3", line0.parameters.get(1).getText());
        Assertions.assertNull(line0.parameters.get(1).count);
        Assertions.assertEquals("4", line0.parameters.get(2).getText());
        Assertions.assertNull(line0.parameters.get(2).count);
        Assertions.assertEquals("5", line0.parameters.get(3).getText());
        Assertions.assertEquals("6", line0.parameters.get(3).count.getText());
        Assertions.assertNull(line0.modifier);

        Assertions.assertEquals("                long    1[2], 3, 4, 5[6]", line0.getText());
    }

    @Test
    void testParseUnknownTypeData() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "DAT\n"
            + "                long1   1, 2, 3, 4\n"
            + ""));

        Node root = subject.parse();
        DataLineNode line0 = (DataLineNode) root.getChild(0).getChild(0);

        Assertions.assertNull(line0.label);
        Assertions.assertNull(line0.condition);
        Assertions.assertNull(line0.instruction);
        Assertions.assertEquals(0, line0.parameters.size());
        Assertions.assertNull(line0.modifier);

        Assertions.assertEquals(ErrorNode.class, line0.getChild(line0.getChilds().size() - 1).getClass());

        Assertions.assertEquals("                long1   1, 2, 3, 4", line0.getText());
    }

}
