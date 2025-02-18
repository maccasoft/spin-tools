/*
 * Copyright (c) 2025 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NodeTest {

    @Test
    void testRootPath() {
        Node node = new Node();
        Assertions.assertEquals("/Node", node.getPath());
    }

    @Test
    void testBlockPath() {
        Node node = new Node();
        Node c0 = new ConstantsNode(node);
        Node c1 = new ConstantsNode(node);
        Node v0 = new VariablesNode(node);

        Assertions.assertEquals("/CON0", c0.getPath());
        Assertions.assertEquals("/CON1", c1.getPath());
        Assertions.assertEquals("/VAR0", v0.getPath());
    }

    @Test
    void testConstantsBlockPath() {
        Node node = new Node();
        ConstantsNode var = new ConstantsNode(node);
        ConstantNode v0 = new ConstantNode(var);
        v0.identifier = new Token(0, "a");
        ConstantNode v1 = new ConstantNode(var);

        Assertions.assertEquals("/CON0", var.getPath());
        Assertions.assertEquals("/CON0/A", v0.getPath());
        Assertions.assertEquals("/CON0/1", v1.getPath());
    }

    @Test
    void testVariablesBlockPath() {
        Node node = new Node();
        VariablesNode var = new VariablesNode(node);
        VariableNode v0 = new VariableNode(var);
        v0.identifier = new Token(0, "a");
        VariableNode v1 = new VariableNode(var);

        Assertions.assertEquals("/VAR0", var.getPath());
        Assertions.assertEquals("/VAR0/A", v0.getPath());
        Assertions.assertEquals("/VAR0/1", v1.getPath());
    }

    @Test
    void testStatementsBlockPath() {
        Node node = new Node();
        MethodNode m0 = new MethodNode(node, new Token(0, "PUB"));
        m0.name = new Token(0, "main");
        StatementNode s0 = new StatementNode(m0);
        StatementNode s1 = new StatementNode(m0);
        StatementNode s10 = new StatementNode(s1);

        Assertions.assertEquals("/PUB MAIN", m0.getPath());
        Assertions.assertEquals("/PUB MAIN/StatementNode0", s0.getPath());
        Assertions.assertEquals("/PUB MAIN/StatementNode1", s1.getPath());
        Assertions.assertEquals("/PUB MAIN/StatementNode1/StatementNode0", s10.getPath());
    }

}
