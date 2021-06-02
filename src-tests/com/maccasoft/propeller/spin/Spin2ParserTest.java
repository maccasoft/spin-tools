/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.spin.Spin2Parser.ConstantAssignEnum;
import com.maccasoft.propeller.spin.Spin2Parser.ConstantAssignExpression;
import com.maccasoft.propeller.spin.Spin2Parser.ConstantSetEnum;
import com.maccasoft.propeller.spin.Spin2Parser.Constants;
import com.maccasoft.propeller.spin.Spin2Parser.Method;
import com.maccasoft.propeller.spin.Spin2Parser.Node;

class Spin2ParserTest {

    @Test
    void testSingleAssigments() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "CON  EnableFlow = 8\n"
            + "     DisableFlow = 4\n"
            + ""));

        Node root = subject.parse();
        Constants con0 = (Constants) root.childs.get(0);

        Assertions.assertEquals(2, con0.childs.size());

        ConstantAssignExpression node0 = (ConstantAssignExpression) con0.childs.get(0);
        Assertions.assertEquals("EnableFlow", node0.identifier.getText());
        Assertions.assertEquals("8", node0.expression.getText());

        ConstantAssignExpression node1 = (ConstantAssignExpression) con0.childs.get(1);
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
        Constants con0 = (Constants) root.childs.get(0);

        Assertions.assertEquals(3, con0.childs.size());

        ConstantAssignExpression node0 = (ConstantAssignExpression) con0.childs.get(0);
        Assertions.assertEquals("x", node0.identifier.getText());
        Assertions.assertEquals("5", node0.expression.getText());

        ConstantAssignExpression node1 = (ConstantAssignExpression) con0.childs.get(1);
        Assertions.assertEquals("y", node1.identifier.getText());
        Assertions.assertEquals("-5", node1.expression.getText());

        ConstantAssignExpression node2 = (ConstantAssignExpression) con0.childs.get(2);
        Assertions.assertEquals("z", node2.identifier.getText());
        Assertions.assertEquals("1", node2.expression.getText());
    }

    @Test
    void testEnumAssignments() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "CON  #0,a,b,c,d\n"
            + ""));

        Node root = subject.parse();
        Constants con0 = (Constants) root.childs.get(0);

        ConstantSetEnum node0 = (ConstantSetEnum) con0.childs.get(0);
        Assertions.assertEquals("0", node0.start.getText());
        Assertions.assertNull(node0.step);

        Assertions.assertEquals("a", ((ConstantAssignEnum) con0.childs.get(1)).identifier.getText());
        Assertions.assertEquals("b", ((ConstantAssignEnum) con0.childs.get(2)).identifier.getText());
        Assertions.assertEquals("c", ((ConstantAssignEnum) con0.childs.get(3)).identifier.getText());
        Assertions.assertEquals("d", ((ConstantAssignEnum) con0.childs.get(4)).identifier.getText());
    }

    @Test
    void testEnumAssigmentStepMultiplier() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "CON\n"
            + "     u[2]\n"
            + ""));

        Node root = subject.parse();
        Constants con0 = (Constants) root.childs.get(0);

        ConstantAssignEnum node0 = (ConstantAssignEnum) con0.childs.get(0);
        Assertions.assertEquals("u", node0.identifier.getText());
        Assertions.assertEquals("2", node0.multiplier.getText());
    }

    @Test
    void testMethod() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB go()\n"
            + ""));

        Node root = subject.parse();
        Method method0 = (Method) root.childs.get(0);

        Assertions.assertEquals("PUB", method0.type.getText());
        Assertions.assertEquals("go", method0.name.getText());
    }

    @Test
    void testMethodParameters() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB StartTx(pin, baud)\n"
            + ""));

        Node root = subject.parse();
        Method method0 = (Method) root.childs.get(0);

        Assertions.assertEquals("pin", method0.parameters.get(0).getText());
        Assertions.assertEquals("baud", method0.parameters.get(1).getText());
    }

    @Test
    void testMethodLocalVariables() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PRI FFT1024() | a, b\n"
            + ""));

        Node root = subject.parse();
        Method method0 = (Method) root.childs.get(0);

        Assertions.assertEquals("a", method0.localVariables.get(0).identifier.getText());
        Assertions.assertEquals("b", method0.localVariables.get(1).identifier.getText());
    }

    @Test
    void testMethodLocalVariableType() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PRI FFT1024() | LONG a, WORD b\n"
            + ""));

        Node root = subject.parse();
        Method method0 = (Method) root.childs.get(0);

        Assertions.assertEquals("LONG", method0.localVariables.get(0).type.getText());
        Assertions.assertEquals("WORD", method0.localVariables.get(1).type.getText());
    }

    @Test
    void testMethodLocalVariableSize() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PRI FFT1024(DataPtr) | x[1024], y[512]\n"
            + ""));

        Node root = subject.parse();
        Method method0 = (Method) root.childs.get(0);

        Assertions.assertEquals("1024", method0.localVariables.get(0).size.getText());
        Assertions.assertEquals("512", method0.localVariables.get(1).size.getText());
    }

}
