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

import com.maccasoft.propeller.model.ConstantAssignEnumNode;
import com.maccasoft.propeller.model.ConstantAssignNode;
import com.maccasoft.propeller.model.ConstantSetEnumNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.StatementNode;

class Spin1ParserTest {

    @Test
    void testParseDefaultAsConstants() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "    con0 = 1\n"
            + "    con1 = 2\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(1, root.getChilds().size());
        Assertions.assertEquals(ConstantsNode.class, root.getChild(0).getClass());

        Assertions.assertEquals(2, root.getChild(0).getChilds().size());
        Assertions.assertEquals("con0 = 1", root.getChild(0).getChild(0).getText());
        Assertions.assertEquals("con1 = 2", root.getChild(0).getChild(1).getText());

        Assertions.assertEquals(ConstantAssignNode.class, root.getChild(0).getChild(0).getClass());
        Assertions.assertEquals(ConstantAssignNode.class, root.getChild(0).getChild(1).getClass());
    }

    @Test
    void testParseConstants() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "CON con0 = 1\n"
            + "    con1 = 2\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(1, root.getChilds().size());
        Assertions.assertEquals(ConstantsNode.class, root.getChild(0).getClass());

        Assertions.assertEquals(2, root.getChild(0).getChilds().size());
        Assertions.assertEquals("con0 = 1", root.getChild(0).getChild(0).getText());
        Assertions.assertEquals("con1 = 2", root.getChild(0).getChild(1).getText());

        Assertions.assertEquals(ConstantAssignNode.class, root.getChild(0).getChild(0).getClass());
        Assertions.assertEquals(ConstantAssignNode.class, root.getChild(0).getChild(1).getClass());
    }

    @Test
    void testParseConstantsList() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "CON con0 = 1\n"
            + "    con1 = 2, con2 = 3, con3 = 4\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(1, root.getChilds().size());
        Assertions.assertEquals(ConstantsNode.class, root.getChild(0).getClass());

        Assertions.assertEquals(4, root.getChild(0).getChilds().size());
        Assertions.assertEquals("con0 = 1", root.getChild(0).getChild(0).getText());
        Assertions.assertEquals("con1 = 2", root.getChild(0).getChild(1).getText());
        Assertions.assertEquals("con2 = 3", root.getChild(0).getChild(2).getText());
        Assertions.assertEquals("con3 = 4", root.getChild(0).getChild(3).getText());

        Assertions.assertEquals(ConstantAssignNode.class, root.getChild(0).getChild(0).getClass());
        Assertions.assertEquals(ConstantAssignNode.class, root.getChild(0).getChild(1).getClass());
        Assertions.assertEquals(ConstantAssignNode.class, root.getChild(0).getChild(2).getClass());
        Assertions.assertEquals(ConstantAssignNode.class, root.getChild(0).getChild(3).getClass());
    }

    @Test
    void testParseEnumConstants() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "CON #0\n"
            + "    con0\n"
            + "    con1\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(1, root.getChilds().size());
        Assertions.assertEquals(ConstantsNode.class, root.getChild(0).getClass());

        Assertions.assertEquals(3, root.getChild(0).getChilds().size());
        Assertions.assertEquals("0", root.getChild(0).getChild(0).getText());
        Assertions.assertEquals("con0", root.getChild(0).getChild(1).getText());
        Assertions.assertEquals("con1", root.getChild(0).getChild(2).getText());

        Assertions.assertEquals(ConstantSetEnumNode.class, root.getChild(0).getChild(0).getClass());
        Assertions.assertEquals(ConstantAssignEnumNode.class, root.getChild(0).getChild(1).getClass());
        Assertions.assertEquals(ConstantAssignEnumNode.class, root.getChild(0).getChild(2).getClass());
    }

    @Test
    void testParseEnumConstantsList() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "CON #0\n"
            + "    con0\n"
            + "    con1, con2, con3\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(1, root.getChilds().size());

        Assertions.assertEquals(5, root.getChild(0).getChilds().size());
        Assertions.assertEquals("0", root.getChild(0).getChild(0).getText());
        Assertions.assertEquals("con0", root.getChild(0).getChild(1).getText());
        Assertions.assertEquals("con1", root.getChild(0).getChild(2).getText());
        Assertions.assertEquals("con2", root.getChild(0).getChild(3).getText());
        Assertions.assertEquals("con3", root.getChild(0).getChild(4).getText());

        Assertions.assertEquals(ConstantSetEnumNode.class, root.getChild(0).getChild(0).getClass());
        Assertions.assertEquals(ConstantAssignEnumNode.class, root.getChild(0).getChild(1).getClass());
        Assertions.assertEquals(ConstantAssignEnumNode.class, root.getChild(0).getChild(2).getClass());
        Assertions.assertEquals(ConstantAssignEnumNode.class, root.getChild(0).getChild(3).getClass());
        Assertions.assertEquals(ConstantAssignEnumNode.class, root.getChild(0).getChild(4).getClass());
    }

    @Test
    void testParseMethods() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "PUB start\n"
            + "\n"
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + ""));

        Node root = subject.parse();

        Assertions.assertEquals(3, root.getChilds().size());
        Assertions.assertEquals(MethodNode.class, root.getChild(0).getClass());
        Assertions.assertEquals(MethodNode.class, root.getChild(1).getClass());
        Assertions.assertEquals(MethodNode.class, root.getChild(2).getClass());

        Assertions.assertEquals("PUB start", root.getChild(0).getText());
        Assertions.assertEquals("PUB method1", root.getChild(1).getText());
        Assertions.assertEquals("PUB method2", root.getChild(2).getText());
    }

    @Test
    void testParseStatements() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "PUB start\n"
            + "\n"
            + "    method1\n"
            + "    repeat\n"
            + "        method2\n"
            + "\n"
            + "PUB method1\n"
            + "\n"
            + "PUB method2\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(3, root.getChilds().size());

        MethodNode pub0 = (MethodNode) root.getChild(0);
        Assertions.assertEquals(2, pub0.getChilds().size());

        Assertions.assertEquals("method1", pub0.getChild(0).getText());
        Assertions.assertEquals("repeat\n        method2", pub0.getChild(1).getText());

        Assertions.assertEquals(StatementNode.class, pub0.getChild(0).getClass());
        Assertions.assertEquals(StatementNode.class, pub0.getChild(1).getClass());
    }

}
