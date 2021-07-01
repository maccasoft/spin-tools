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

import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.StatementNode;

class Spin1ModelTest {

    String text1 = ""
        + "PUB start\n"
        + "\n"
        + "    method1\n"
        + "    repeat\n"
        + "        method2\n"
        + "\n"
        + "PUB method1(par0, par1)\n"
        + "\n"
        + "    \n"
        + "\n"
        + "PUB method2 | loc0, loc1\n"
        + "\n"
        + "    \n"
        + "\n";

    @Test
    void testGetNodeAt() {
        Spin1Parser parser = new Spin1Parser(new Spin1TokenStream(text1));
        Spin1Model subject = new Spin1Model(parser.parse());

        Node node1 = subject.getNodeAt(15);
        Assertions.assertEquals(StatementNode.class, node1.getClass());
        Assertions.assertEquals("    method1", node1.getText());

        Node node2 = subject.getNodeAt(67);
        Assertions.assertEquals(MethodNode.class, node2.getClass());
        Assertions.assertEquals("PUB method1(par0, par1)", node2.getText());

        Node node3 = subject.getNodeAt(99);
        Assertions.assertEquals(MethodNode.class, node3.getClass());
        Assertions.assertEquals("PUB method2 | loc0, loc1", node3.getText());
    }

    @Test
    void testGetMethodProposals() {
        Spin1Parser parser = new Spin1Parser(new Spin1TokenStream(text1));
        Spin1Model subject = new Spin1Model(parser.parse());

        Node node = subject.getNodeAt(15);

        List<IContentProposal> list = subject.getMethodProposals(node, "");

        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("method1", list.get(0).getLabel());
        Assertions.assertEquals("method2", list.get(1).getLabel());
    }

    @Test
    void testGetMethodProposalsInitialText() {
        Spin1Parser parser = new Spin1Parser(new Spin1TokenStream(text1));
        Spin1Model subject = new Spin1Model(parser.parse());

        Node node = subject.getNodeAt(15);

        List<IContentProposal> list = subject.getMethodProposals(node, "m");

        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("method1", list.get(0).getLabel());
        Assertions.assertEquals("method2", list.get(1).getLabel());
    }

    @Test
    void testGetParametersProposals() {
        Spin1Parser parser = new Spin1Parser(new Spin1TokenStream(text1));
        Spin1Model subject = new Spin1Model(parser.parse());

        Node node = subject.getNodeAt(67);

        List<IContentProposal> list = subject.getMethodProposals(node, "");

        Assertions.assertEquals(4, list.size());
        Assertions.assertEquals("par0", list.get(0).getLabel());
        Assertions.assertEquals("par1", list.get(1).getLabel());
        Assertions.assertEquals("start", list.get(2).getLabel());
        Assertions.assertEquals("method2", list.get(3).getLabel());
    }

    @Test
    void testGetLocalVariablesProposals() {
        Spin1Parser parser = new Spin1Parser(new Spin1TokenStream(text1));
        Spin1Model subject = new Spin1Model(parser.parse());

        Node node = subject.getNodeAt(99);

        List<IContentProposal> list = subject.getMethodProposals(node, "");

        Assertions.assertEquals(4, list.size());
        Assertions.assertEquals("loc0", list.get(0).getLabel());
        Assertions.assertEquals("loc1", list.get(1).getLabel());
        Assertions.assertEquals("start", list.get(2).getLabel());
        Assertions.assertEquals("method1", list.get(3).getLabel());
    }

    @Test
    void testGetGlobalVariablesProposals() {
        Spin1Parser parser = new Spin1Parser(new Spin1TokenStream(""
            + "VAR\n"
            + "\n"
            + "    var0\n"
            + "    byte var1[10]\n"
            + "\n"
            + "PUB start\n"
            + "\n"
            + "    \n"
            + "\n"));
        Spin1Model subject = new Spin1Model(parser.parse());

        Node node = subject.getNodeAt(48);

        List<IContentProposal> list = subject.getMethodProposals(node, "");

        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("var0", list.get(0).getLabel());
        Assertions.assertEquals("var1", list.get(1).getLabel());
    }

    @Test
    void testGetConstansProposals() {
        Spin1Parser parser = new Spin1Parser(new Spin1TokenStream(""
            + "CON\n"
            + "\n"
            + "    con0 = 1\n"
            + "    con1 = 2\n"
            + "\n"
            + "PUB start\n"
            + "\n"
            + "    \n"
            + "\n"));
        Spin1Model subject = new Spin1Model(parser.parse());

        Node node = subject.getNodeAt(47);

        List<IContentProposal> list = subject.getMethodProposals(node, "");

        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("con0", list.get(0).getLabel());
        Assertions.assertEquals("con1", list.get(1).getLabel());
    }

    @Test
    void testGetDatLabelProposals() {
        Spin1Parser parser = new Spin1Parser(new Spin1TokenStream(""
            + "PUB start\n"
            + "\n"
            + "    \n"
            + "\n"
            + "DAT\n"
            + "\n"
            + "label0      nop\n"
            + ".local      nop\n"
            + "label1      long 0, 1, 2\n"
            + "\n"));
        Spin1Model subject = new Spin1Model(parser.parse());

        Node node = subject.getNodeAt(15);

        List<IContentProposal> list = subject.getMethodProposals(node, "");

        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("label0", list.get(0).getLabel());
        Assertions.assertEquals("label1", list.get(1).getLabel());
    }

}
