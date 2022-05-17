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

import java.lang.reflect.Field;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.Token;

class Spin2ParseMethodTest {

    @Test
    void testParseMethod() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
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
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
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
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
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
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
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
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
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
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
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
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
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
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
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
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
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
    void testParseResults() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB start : result0, result1\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals("PUB", pub0.getType().getText());
        Assertions.assertEquals("start", pub0.getName().getText());
        Assertions.assertEquals(0, pub0.getParameters().size());
        Assertions.assertEquals(2, pub0.getReturnVariables().size());
        Assertions.assertEquals("result0", pub0.getReturnVariable(0).getText());
        Assertions.assertEquals("result1", pub0.getReturnVariable(1).getText());
        Assertions.assertEquals(0, pub0.getLocalVariables().size());

        Assertions.assertEquals("PUB start : result0, result1", pub0.getText());
    }

    @Test
    void testParseFull() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
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
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
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

    @Test
    void testParseIndentedBlock() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB start()\n"
            + "\n"
            + "    repeat\n"
            + "        a := b\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals(1, pub0.getChilds().size());
        Assertions.assertEquals(1, pub0.getChild(0).getChilds().size());

        Assertions.assertEquals(""
            + "    repeat", pub0.getChild(0).getText());
        Assertions.assertEquals(""
            + "        a := b", pub0.getChild(0).getChild(0).getText());
    }

    @Test
    void testParseIndentedBlockWithoutStatements() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB start()\n"
            + "\n"
            + "    repeat\n"
            + "    if\n"
            + "        a := b\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals(2, pub0.getChilds().size());
        Assertions.assertEquals(0, pub0.getChild(0).getChilds().size());
        Assertions.assertEquals(1, pub0.getChild(1).getChilds().size());

        Assertions.assertEquals(""
            + "    repeat", pub0.getChild(0).getText());
        Assertions.assertEquals(""
            + "    if", pub0.getChild(1).getText());
        Assertions.assertEquals(""
            + "        a := b", pub0.getChild(1).getChild(0).getText());
    }

    @Test
    void testParseMisplacedBlocks() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB start()\n"
            + "\n"
            + "    repeat\n"
            + "        a := b\n"
            + "         c := d\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals(1, pub0.getChilds().size());
        Assertions.assertEquals(2, pub0.getChild(0).getChilds().size());

        Assertions.assertEquals(""
            + "    repeat", pub0.getChild(0).getText());
        Assertions.assertEquals(""
            + "        a := b", pub0.getChild(0).getChild(0).getText());
        Assertions.assertEquals(""
            + "         c := d", pub0.getChild(0).getChild(1).getText());
    }

    @Test
    void testParseCaseBlock() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB start()\n"
            + "\n"
            + "    case a\n"
            + "        0: a := b\n"
            + "        1: c := d\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals(1, pub0.getChilds().size());
        Assertions.assertEquals(2, pub0.getChild(0).getChilds().size());
    }

    @Test
    void testParseCaseBlockWithMultipleStatements() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB start()\n"
            + "\n"
            + "    case a\n"
            + "        0: a := b\n"
            + "        1: c := d\n"
            + "           e := f\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- MethodNode type=PUB name=start [PUB start()]\n"
            + "    +-- StatementNode [    case a]\n"
            + "        +-- StatementNode [        0:]\n"
            + "            +-- StatementNode [        0: a := b]\n"
            + "        +-- StatementNode [        1:]\n"
            + "            +-- StatementNode [        1: c := d]\n"
            + "            +-- StatementNode [           e := f]\n"
            + "", tree(root));
    }

    @Test
    void testParseInlinePAsmBlock() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB start()\n"
            + "\n"
            + "    org\n"
            + "        nop\n"
            + "        ret\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals(3, pub0.getChilds().size());

        Assertions.assertEquals("    org", pub0.getChild(0).getText());
        Assertions.assertEquals("        nop", pub0.getChild(1).getText());
        Assertions.assertEquals("        ret", pub0.getChild(2).getText());
    }

    @Test
    void testParseIndentedInlinePAsmBlock() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB start()\n"
            + "\n"
            + "    repeat\n"
            + "        org\n"
            + "            nop\n"
            + "            ret\n"
            + ""));

        Node root = subject.parse();
        MethodNode pub0 = (MethodNode) root.getChild(0);

        Assertions.assertEquals(1, pub0.getChilds().size());
        Assertions.assertEquals(3, pub0.getChild(0).getChilds().size());

        Node node = pub0.getChild(0);
        Assertions.assertEquals("        org", node.getChild(0).getText());
        Assertions.assertEquals("            nop", node.getChild(1).getText());
        Assertions.assertEquals("            ret", node.getChild(2).getText());
    }

    String tree(Node root) throws Exception {
        return tree(root, 0);
    }

    String tree(Node root, int indent) throws Exception {
        StringBuilder sb = new StringBuilder();
        Field[] field = root.getClass().getFields();

        sb.append(root.getClass().getSimpleName());

        for (Token token : root.getTokens()) {
            for (int i = 0; i < field.length; i++) {
                if (field[i].get(root) == token) {
                    sb.append(" ");
                    sb.append(field[i].getName());
                    sb.append("=");
                    sb.append(token);
                    break;
                }
            }
        }

        sb.append(" [");
        sb.append(root.getText().replaceAll("\n", "\\\\n"));
        sb.append("]");
        sb.append(System.lineSeparator());

        for (Node child : root.getChilds()) {
            for (int i = 0; i < indent; i++) {
                sb.append("    ");
            }
            sb.append("+-- ");
            for (int i = 0; i < field.length; i++) {
                if (field[i].get(root) == child) {
                    sb.append(field[i].getName());
                    sb.append(" = ");
                    break;
                }
            }
            sb.append(tree(child, indent + 1));
        }

        return sb.toString();
    }

}
