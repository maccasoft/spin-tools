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

import com.maccasoft.propeller.model.ErrorNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;

class Spin1ParseMethodTest {

    @Test
    void testParseMethod() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "PUB start\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("PUB", pub0.getType().getText());
        Assertions.assertEquals("start", pub0.getName().getText());
        Assertions.assertEquals(0, pub0.getParameters().size());
        Assertions.assertEquals(0, pub0.getReturnVariables().size());
        Assertions.assertEquals(0, pub0.getLocalVariables().size());

        Assertions.assertEquals("PUB start", pub0.getText());
    }

    @Test
    void testParseMethodEmptParameters() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "PUB start()\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("PUB", pub0.getType().getText());
        Assertions.assertEquals("start", pub0.getName().getText());
        Assertions.assertEquals(0, pub0.getParameters().size());
        Assertions.assertEquals(0, pub0.getReturnVariables().size());
        Assertions.assertEquals(0, pub0.getLocalVariables().size());

        Assertions.assertEquals("PUB start()", pub0.getText());
    }

    @Test
    void testParseParameter() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "PUB start(arg0)\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("PUB", pub0.getType().getText());
        Assertions.assertEquals("start", pub0.getName().getText());
        Assertions.assertEquals(1, pub0.getParameters().size());
        Assertions.assertEquals("arg0", pub0.getParameter(0).getText());
        Assertions.assertEquals(0, pub0.getReturnVariables().size());
        Assertions.assertEquals(0, pub0.getLocalVariables().size());

        Assertions.assertEquals("PUB start(arg0)", pub0.getText());
    }

    @Test
    void testParseParameters() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "PUB start(arg0,arg1,arg2)\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("PUB", pub0.getType().getText());
        Assertions.assertEquals("start", pub0.getName().getText());
        Assertions.assertEquals(3, pub0.getParameters().size());
        Assertions.assertEquals("arg0", pub0.getParameter(0).getText());
        Assertions.assertEquals("arg1", pub0.getParameter(1).getText());
        Assertions.assertEquals("arg2", pub0.getParameter(2).getText());
        Assertions.assertEquals(0, pub0.getReturnVariables().size());
        Assertions.assertEquals(0, pub0.getLocalVariables().size());

        Assertions.assertEquals("PUB start(arg0,arg1,arg2)", pub0.getText());
    }

    @Test
    void testParseLocal() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "PUB start | var0\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("PUB", pub0.getType().getText());
        Assertions.assertEquals("start", pub0.getName().getText());
        Assertions.assertEquals(0, pub0.getParameters().size());
        Assertions.assertEquals(0, pub0.getReturnVariables().size());
        Assertions.assertEquals(1, pub0.getLocalVariables().size());
        Assertions.assertEquals("var0", pub0.getLocalVariable(0).getIdentifier().getText());

        Assertions.assertEquals("PUB start | var0", pub0.getText());
    }

    @Test
    void testParseLocals() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "PUB start | var0, var1, var2\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("PUB", pub0.getType().getText());
        Assertions.assertEquals("start", pub0.getName().getText());
        Assertions.assertEquals(0, pub0.getParameters().size());
        Assertions.assertEquals(0, pub0.getReturnVariables().size());
        Assertions.assertEquals(3, pub0.getLocalVariables().size());
        Assertions.assertNull(pub0.getLocalVariable(0).getType());
        Assertions.assertEquals("var0", pub0.getLocalVariable(0).getIdentifier().getText());
        Assertions.assertNull(pub0.getLocalVariable(1).getType());
        Assertions.assertEquals("var1", pub0.getLocalVariable(1).getIdentifier().getText());
        Assertions.assertNull(pub0.getLocalVariable(1).getType());
        Assertions.assertEquals("var2", pub0.getLocalVariable(2).getIdentifier().getText());

        Assertions.assertEquals("PUB start | var0, var1, var2", pub0.getText());
    }

    @Test
    void testParseTypedLocals() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "PUB start | byte var0, word var1, long var2\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("PUB", pub0.getType().getText());
        Assertions.assertEquals("start", pub0.getName().getText());
        Assertions.assertEquals(0, pub0.getParameters().size());
        Assertions.assertEquals(0, pub0.getReturnVariables().size());
        Assertions.assertEquals(3, pub0.getLocalVariables().size());
        Assertions.assertEquals("byte", pub0.getLocalVariable(0).getType().getText());
        Assertions.assertEquals("var0", pub0.getLocalVariable(0).getIdentifier().getText());
        Assertions.assertEquals("word", pub0.getLocalVariable(1).getType().getText());
        Assertions.assertEquals("var1", pub0.getLocalVariable(1).getIdentifier().getText());
        Assertions.assertEquals("long", pub0.getLocalVariable(2).getType().getText());
        Assertions.assertEquals("var2", pub0.getLocalVariable(2).getIdentifier().getText());

        Assertions.assertEquals("PUB start | byte var0, word var1, long var2", pub0.getText());
    }

    @Test
    void testParseArrayLocal() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "PUB start | var0[10]\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("PUB", pub0.getType().getText());
        Assertions.assertEquals("start", pub0.getName().getText());
        Assertions.assertEquals(0, pub0.getParameters().size());
        Assertions.assertEquals(0, pub0.getReturnVariables().size());
        Assertions.assertEquals(1, pub0.getLocalVariables().size());
        Assertions.assertEquals("var0", pub0.getLocalVariable(0).getIdentifier().getText());
        Assertions.assertEquals("10", pub0.getLocalVariable(0).getSize().getText());

        Assertions.assertEquals("PUB start | var0[10]", pub0.getText());
    }

    @Test
    void testParseResult() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "PUB start : result\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("PUB", pub0.getType().getText());
        Assertions.assertEquals("start", pub0.getName().getText());
        Assertions.assertEquals(0, pub0.getParameters().size());
        Assertions.assertEquals(1, pub0.getReturnVariables().size());
        Assertions.assertEquals("result", pub0.getReturnVariable(0).getText());
        Assertions.assertEquals(0, pub0.getLocalVariables().size());

        Assertions.assertEquals("PUB start : result", pub0.getText());
    }

    @Test
    void testAllowOneResultOnly() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "PUB start : result0, result1\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("PUB", pub0.getType().getText());
        Assertions.assertEquals("start", pub0.getName().getText());
        Assertions.assertEquals(0, pub0.getParameters().size());
        Assertions.assertEquals(1, pub0.getReturnVariables().size());
        Assertions.assertEquals("result0", pub0.getReturnVariable(0).getText());
        Assertions.assertEquals(0, pub0.getLocalVariables().size());

        Assertions.assertEquals(ErrorNode.class, pub0.getChild(pub0.getChilds().size() - 1).getClass());

        Assertions.assertEquals("PUB start : result0, result1", pub0.getText());
    }

    @Test
    void testDontAllowTypedParameters() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "PUB start(word arg0)\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("PUB", pub0.getType().getText());
        Assertions.assertEquals("start", pub0.getName().getText());
        Assertions.assertEquals(0, pub0.getParameters().size());
        Assertions.assertEquals(0, pub0.getReturnVariables().size());
        Assertions.assertEquals(0, pub0.getLocalVariables().size());

        Assertions.assertEquals(ErrorNode.class, pub0.getChild(pub0.getChilds().size() - 1).getClass());

        Assertions.assertEquals("PUB start(word arg0)", pub0.getText());
    }

    @Test
    void testParseUnknowTypeLocal() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "PUB start | byte var0, word var1 var2\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("PUB", pub0.getType().getText());
        Assertions.assertEquals("start", pub0.getName().getText());
        Assertions.assertEquals(0, pub0.getParameters().size());
        Assertions.assertEquals(0, pub0.getReturnVariables().size());
        Assertions.assertEquals(2, pub0.getLocalVariables().size());
        Assertions.assertEquals("byte var0", pub0.getLocalVariable(0).getText());
        Assertions.assertEquals("word var1", pub0.getLocalVariable(1).getText());

        Assertions.assertEquals(ErrorNode.class, pub0.getChild(pub0.getChilds().size() - 1).getClass());

        Assertions.assertEquals("PUB start | byte var0, word var1 var2", pub0.getText());
    }

    @Test
    void testParseFull() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "PUB start(arg0, arg1) : var0 | byte var1, word var2\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("PUB", pub0.getType().getText());
        Assertions.assertEquals("start", pub0.getName().getText());
        Assertions.assertEquals(2, pub0.getParameters().size());
        Assertions.assertEquals("arg0", pub0.getParameter(0).getText());
        Assertions.assertEquals("arg1", pub0.getParameter(1).getText());
        Assertions.assertEquals(1, pub0.getReturnVariables().size());
        Assertions.assertEquals("var0", pub0.getReturnVariable(0).getText());
        Assertions.assertEquals(2, pub0.getLocalVariables().size());
        Assertions.assertEquals("byte var1", pub0.getLocalVariable(0).getText());
        Assertions.assertEquals("word var2", pub0.getLocalVariable(1).getText());

        Assertions.assertEquals("PUB start(arg0, arg1) : var0 | byte var1, word var2", pub0.getText());
    }

    @Test
    void testParseFullOrder() {
        Spin1Parser subject = new Spin1Parser(new Spin2TokenStream(""
            + "PUB start(arg0, arg1) | byte var1, word var2 : var0\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("PUB", pub0.getType().getText());
        Assertions.assertEquals("start", pub0.getName().getText());
        Assertions.assertEquals(2, pub0.getParameters().size());
        Assertions.assertEquals("arg0", pub0.getParameter(0).getText());
        Assertions.assertEquals("arg1", pub0.getParameter(1).getText());
        Assertions.assertEquals(1, pub0.getReturnVariables().size());
        Assertions.assertEquals("var0", pub0.getReturnVariable(0).getText());
        Assertions.assertEquals(2, pub0.getLocalVariables().size());
        Assertions.assertEquals("byte var1", pub0.getLocalVariable(0).getText());
        Assertions.assertEquals("word var2", pub0.getLocalVariable(1).getText());

        Assertions.assertEquals("PUB start(arg0, arg1) | byte var1, word var2 : var0", pub0.getText());
    }

}
