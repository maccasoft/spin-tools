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

import com.maccasoft.propeller.model.ConstantAssignEnumNode;
import com.maccasoft.propeller.model.ConstantAssignNode;
import com.maccasoft.propeller.model.ConstantSetEnumNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;

class Spin2ParserTest {

    @Test
    void testSingleAssigments() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "CON  EnableFlow = 8\n"
            + "     DisableFlow = 4\n"
            + ""));

        Node root = subject.parse();
        ConstantsNode con0 = (ConstantsNode) root.getChild(0);

        Assertions.assertEquals(2, con0.getChilds().size());

        ConstantAssignNode node0 = (ConstantAssignNode) con0.getChild(0);
        Assertions.assertEquals("EnableFlow", node0.identifier.getText());
        Assertions.assertEquals("8", node0.expression.getText());

        ConstantAssignNode node1 = (ConstantAssignNode) con0.getChild(1);
        Assertions.assertEquals("DisableFlow", node1.identifier.getText());
        Assertions.assertEquals("4", node1.expression.getText());
    }

    @Test
    void testCommaSeparatedAssignments() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "CON\n"
            + "     x = 5, y = -5, z = 1\n"
            + ""));

        Node root = subject.parse();
        ConstantsNode con0 = (ConstantsNode) root.getChild(0);

        Assertions.assertEquals(3, con0.getChilds().size());

        ConstantAssignNode node0 = (ConstantAssignNode) con0.getChild(0);
        Assertions.assertEquals("x", node0.identifier.getText());
        Assertions.assertEquals("5", node0.expression.getText());

        ConstantAssignNode node1 = (ConstantAssignNode) con0.getChild(1);
        Assertions.assertEquals("y", node1.identifier.getText());
        Assertions.assertEquals("-5", node1.expression.getText());

        ConstantAssignNode node2 = (ConstantAssignNode) con0.getChild(2);
        Assertions.assertEquals("z", node2.identifier.getText());
        Assertions.assertEquals("1", node2.expression.getText());
    }

    @Test
    void testEnumAssignments() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "CON  #0,a,b,c,d\n"
            + ""));

        Node root = subject.parse();
        ConstantsNode con0 = (ConstantsNode) root.getChild(0);

        ConstantSetEnumNode node0 = (ConstantSetEnumNode) con0.getChild(0);
        Assertions.assertEquals("0", node0.start.getText());
        Assertions.assertNull(node0.step);

        Assertions.assertEquals("a", ((ConstantAssignEnumNode) con0.getChild(1)).identifier.getText());
        Assertions.assertEquals("b", ((ConstantAssignEnumNode) con0.getChild(2)).identifier.getText());
        Assertions.assertEquals("c", ((ConstantAssignEnumNode) con0.getChild(3)).identifier.getText());
        Assertions.assertEquals("d", ((ConstantAssignEnumNode) con0.getChild(4)).identifier.getText());
    }

    @Test
    void testEnumAssigmentStepMultiplier() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "CON\n"
            + "     u[2]\n"
            + ""));

        Node root = subject.parse();
        ConstantsNode con0 = (ConstantsNode) root.getChild(0);

        ConstantAssignEnumNode node0 = (ConstantAssignEnumNode) con0.getChild(0);
        Assertions.assertEquals("u", node0.identifier.getText());
        Assertions.assertEquals("2", node0.multiplier.getText());
    }

    @Test
    void testDefaultSection() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "     EnableFlow = 8\n"
            + "     DisableFlow = 4\n"
            + ""));

        Node root = subject.parse();
        ConstantsNode con0 = (ConstantsNode) root.getChild(0);

        Assertions.assertEquals(2, con0.getChilds().size());

        ConstantAssignNode node0 = (ConstantAssignNode) con0.getChild(0);
        Assertions.assertEquals("EnableFlow", node0.identifier.getText());
        Assertions.assertEquals("8", node0.expression.getText());

        ConstantAssignNode node1 = (ConstantAssignNode) con0.getChild(1);
        Assertions.assertEquals("DisableFlow", node1.identifier.getText());
        Assertions.assertEquals("4", node1.expression.getText());
    }

    @Test
    void testMethod() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB go()\n"
            + ""));

        Node root = subject.parse();
        MethodNode method0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("PUB", method0.type.getText());
        Assertions.assertEquals("go", method0.name.getText());
    }

    @Test
    void testMethodParameters() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB StartTx(pin, baud)\n"
            + ""));

        Node root = subject.parse();
        MethodNode method0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("pin", method0.parameters.get(0).getText());
        Assertions.assertEquals("baud", method0.parameters.get(1).getText());
    }

    @Test
    void testMethodLocalVariables() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PRI FFT1024() | a, b\n"
            + ""));

        Node root = subject.parse();
        MethodNode method0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("a", method0.localVariables.get(0).identifier.getText());
        Assertions.assertEquals("b", method0.localVariables.get(1).identifier.getText());
    }

    @Test
    void testMethodLocalVariableType() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PRI FFT1024() | LONG a, WORD b\n"
            + ""));

        Node root = subject.parse();
        MethodNode method0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("LONG", method0.localVariables.get(0).type.getText());
        Assertions.assertEquals("WORD", method0.localVariables.get(1).type.getText());
    }

    @Test
    void testMethodLocalVariableSize() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PRI FFT1024(DataPtr) | x[1024], y[512]\n"
            + ""));

        Node root = subject.parse();
        MethodNode method0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("1024", method0.localVariables.get(0).size.getText());
        Assertions.assertEquals("512", method0.localVariables.get(1).size.getText());
    }

    @Test
    void testParsePtr() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "DAT    wrlong a,ptra\n"
            + "       wrlong a,ptra++\n"
            + "       wrlong a,++ptra\n"
            + "       wrlong a,ptra[3]\n"
            + "       wrlong a,ptra--[3]\n"
            + "       wrlong a,--ptra[3]\n"
            + ""));

        Node root = subject.parse();
        DataNode data0 = (DataNode) root.getChild(0);

        DataLineNode line = (DataLineNode) data0.getChild(0);
        Assertions.assertEquals("ptra", line.parameters.get(1).getText());
        Assertions.assertEquals(0, line.parameters.get(1).getChilds().size());

        line = (DataLineNode) data0.getChild(1);
        Assertions.assertEquals("ptra++", line.parameters.get(1).getText());
        Assertions.assertEquals(0, line.parameters.get(1).getChilds().size());

        line = (DataLineNode) data0.getChild(2);
        Assertions.assertEquals("++ptra", line.parameters.get(1).getText());
        Assertions.assertEquals(0, line.parameters.get(1).getChilds().size());

        line = (DataLineNode) data0.getChild(3);
        Assertions.assertEquals("ptra", line.parameters.get(1).getText());
        Assertions.assertEquals("3", line.parameters.get(1).getChild(0).getText());

        line = (DataLineNode) data0.getChild(4);
        Assertions.assertEquals("ptra--", line.parameters.get(1).getText());
        Assertions.assertEquals("3", line.parameters.get(1).getChild(0).getText());

        line = (DataLineNode) data0.getChild(5);
        Assertions.assertEquals("--ptra", line.parameters.get(1).getText());
        Assertions.assertEquals("3", line.parameters.get(1).getChild(0).getText());
    }

    @Test
    void testParseVariables() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "VAR a\n"
            + "    b\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(1, root.getChilds().size());
        Assertions.assertEquals(VariablesNode.class, root.getChild(0).getClass());

        Assertions.assertEquals(2, root.getChild(0).getChilds().size());
        Assertions.assertEquals("a", root.getChild(0).getChild(0).getText());
        Assertions.assertEquals("b", root.getChild(0).getChild(1).getText());

        Assertions.assertEquals(VariableNode.class, root.getChild(0).getChild(0).getClass());
        Assertions.assertEquals(VariableNode.class, root.getChild(0).getChild(1).getClass());
    }

    @Test
    void testParseTypedVariables() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "VAR long a\n"
            + "    word b\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(1, root.getChilds().size());
        Assertions.assertEquals(VariablesNode.class, root.getChild(0).getClass());

        Assertions.assertEquals(2, root.getChild(0).getChilds().size());

        VariableNode node0 = (VariableNode) root.getChild(0).getChild(0);
        Assertions.assertEquals("long", node0.type.getText());
        Assertions.assertEquals("a", node0.identifier.getText());

        VariableNode node1 = (VariableNode) root.getChild(0).getChild(1);
        Assertions.assertEquals("word", node1.type.getText());
        Assertions.assertEquals("b", node1.identifier.getText());
    }

    @Test
    void testParseVariablesList() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "VAR long a, b, c\n"
            + "    word d, e\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(1, root.getChilds().size());
        Assertions.assertEquals(VariablesNode.class, root.getChild(0).getClass());

        Assertions.assertEquals(5, root.getChild(0).getChilds().size());
        Assertions.assertEquals("long a", root.getChild(0).getChild(0).getText());
        Assertions.assertEquals("b", root.getChild(0).getChild(1).getText());
        Assertions.assertEquals("c", root.getChild(0).getChild(2).getText());
        Assertions.assertEquals("word d", root.getChild(0).getChild(3).getText());
        Assertions.assertEquals("e", root.getChild(0).getChild(4).getText());

        Assertions.assertEquals(VariableNode.class, root.getChild(0).getChild(0).getClass());
        Assertions.assertEquals(VariableNode.class, root.getChild(0).getChild(1).getClass());
        Assertions.assertEquals(VariableNode.class, root.getChild(0).getChild(2).getClass());
        Assertions.assertEquals(VariableNode.class, root.getChild(0).getChild(3).getClass());
        Assertions.assertEquals(VariableNode.class, root.getChild(0).getChild(4).getClass());
    }

    @Test
    void testParseVariableSize() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "VAR a[10]\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(1, root.getChilds().size());
        Assertions.assertEquals(VariablesNode.class, root.getChild(0).getClass());

        VariableNode node = (VariableNode) root.getChild(0).getChild(0);
        Assertions.assertEquals("a", node.identifier.getText());
        Assertions.assertEquals("10", node.size.getText());

        Assertions.assertEquals(1, node.getChilds().size());
    }

    @Test
    void testParseTernaryExpression() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "a := (b == 1) ? 2 : 3\n"
            + ""));

        Node root = subject.parseStatement(new Node(), subject.stream.nextToken());
        Assertions.assertEquals("a := (b == 1) ? 2 : 3", root.getText());
        Assertions.assertEquals(1, root.getChilds().size());
    }

}
