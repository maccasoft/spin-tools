/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.ConstantStatement;
import com.maccasoft.propeller.model.ErrorNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;

class Spin1ParserTest {

    @Test
    void testParseDefaultAsConstants() throws Exception {
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
            + "    con0 = 1\n"
            + "    con1 = 2\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [con0 = 1\\n    con1 = 2\\n]\n"
            + "+-- ConstantsNode [con0 = 1\\n    con1 = 2]\n"
            + "    +-- ConstantAssignNode [con0]\n"
            + "        +-- expression = ExpressionNode [1]\n"
            + "    +-- ConstantAssignNode [con1]\n"
            + "        +-- expression = ExpressionNode [2]\n"
            + "", tree(root));
    }

    @Test
    void testParseConstants() throws Exception {
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
            + "CON con0 = 1\n"
            + "    con1 = 2\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [CON con0 = 1\\n    con1 = 2\\n]\n"
            + "+-- ConstantsNode [CON con0 = 1\\n    con1 = 2]\n"
            + "    +-- ConstantAssignNode [con0]\n"
            + "        +-- expression = ExpressionNode [1]\n"
            + "    +-- ConstantAssignNode [con1]\n"
            + "        +-- expression = ExpressionNode [2]\n"
            + "", tree(root));
    }

    @Test
    void testParseConstantsList() throws Exception {
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
            + "CON con0 = 1\n"
            + "    con1 = 2, con2 = 3, con3 = 4\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [CON con0 = 1\\n    con1 = 2, con2 = 3, con3 = 4\\n]\n"
            + "+-- ConstantsNode [CON con0 = 1\\n    con1 = 2, con2 = 3, con3 = 4]\n"
            + "    +-- ConstantAssignNode [con0]\n"
            + "        +-- expression = ExpressionNode [1]\n"
            + "    +-- ConstantAssignNode [con1]\n"
            + "        +-- expression = ExpressionNode [2]\n"
            + "    +-- ConstantAssignNode [con2]\n"
            + "        +-- expression = ExpressionNode [3]\n"
            + "    +-- ConstantAssignNode [con3]\n"
            + "        +-- expression = ExpressionNode [4]\n"
            + "", tree(root));
    }

    @Test
    void testParseEnumConstants() throws Exception {
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
            + "CON #0\n"
            + "    con0\n"
            + "    con1\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [CON #0\\n    con0\\n    con1\\n]\n"
            + "+-- ConstantsNode [CON #0\\n    con0\\n    con1]\n"
            + "    +-- ConstantSetEnumNode\n"
            + "        +-- start = ExpressionNode [0]\n"
            + "    +-- ConstantAssignNode [con0]\n"
            + "    +-- ConstantAssignNode [con1]\n"
            + "", tree(root));
    }

    @Test
    void testParseEnumConstantsList() throws Exception {
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
            + "CON #0\n"
            + "    con0\n"
            + "    con1, con2, con3\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [CON #0\\n    con0\\n    con1, con2, con3\\n]\n"
            + "+-- ConstantsNode [CON #0\\n    con0\\n    con1, con2, con3]\n"
            + "    +-- ConstantSetEnumNode\n"
            + "        +-- start = ExpressionNode [0]\n"
            + "    +-- ConstantAssignNode [con0]\n"
            + "    +-- ConstantAssignNode [con1]\n"
            + "    +-- ConstantAssignNode [con2]\n"
            + "    +-- ConstantAssignNode [con3]\n"
            + "", tree(root));
    }

    @Test
    void testParseEnumStepConstants() throws Exception {
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
            + "CON #0[2]\n"
            + "    con0\n"
            + "    con1, con2, con3\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [CON #0[2]\\n    con0\\n    con1, con2, con3\\n]\n"
            + "+-- ConstantsNode [CON #0[2]\\n    con0\\n    con1, con2, con3]\n"
            + "    +-- ConstantSetEnumNode\n"
            + "        +-- start = ExpressionNode [0]\n"
            + "        +-- step = ExpressionNode [2]\n"
            + "    +-- ConstantAssignNode [con0]\n"
            + "    +-- ConstantAssignNode [con1]\n"
            + "    +-- ConstantAssignNode [con2]\n"
            + "    +-- ConstantAssignNode [con3]\n"
            + "", tree(root));
    }

    @Test
    void testParseEnumMultiplierConstants() throws Exception {
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
            + "CON #0\n"
            + "    con0\n"
            + "    con1[2]\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [CON #0\\n    con0\\n    con1[2]\\n]\n"
            + "+-- ConstantsNode [CON #0\\n    con0\\n    con1[2]]\n"
            + "    +-- ConstantSetEnumNode\n"
            + "        +-- start = ExpressionNode [0]\n"
            + "    +-- ConstantAssignNode [con0]\n"
            + "    +-- ConstantAssignNode [con1]\n"
            + "        +-- multiplier = ExpressionNode [2]\n"
            + "", tree(root));
    }

    @Test
    void testParseObject() {
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
            + "OBJ\n"
            + "    obj0 : \"file0\"\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(1, root.getChilds().size());
        Assertions.assertEquals(ObjectsNode.class, root.getChild(0).getClass());

        Assertions.assertEquals(1, root.getChild(0).getChilds().size());
        Assertions.assertEquals(ObjectNode.class, root.getChild(0).getChild(0).getClass());

        ObjectNode node = (ObjectNode) root.getChild(0).getChild(0);
        Assertions.assertEquals("obj0 : \"file0\"", node.getText());
        Assertions.assertEquals("obj0", node.name.getText());
        Assertions.assertNull(node.count);
        Assertions.assertEquals("\"file0\"", node.file.getText());
    }

    @Test
    void testParseObjects() {
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
            + "OBJ\n"
            + "    obj0 : \"file0\"\n"
            + "    obj1 : \"file1\"\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(1, root.getChilds().size());
        Assertions.assertEquals(ObjectsNode.class, root.getChild(0).getClass());

        Assertions.assertEquals(2, root.getChild(0).getChilds().size());
        Assertions.assertEquals(ObjectNode.class, root.getChild(0).getChild(0).getClass());
        Assertions.assertEquals(ObjectNode.class, root.getChild(0).getChild(1).getClass());

        ObjectNode node0 = (ObjectNode) root.getChild(0).getChild(0);
        Assertions.assertEquals("obj0 : \"file0\"", node0.getText());
        Assertions.assertEquals("obj0", node0.name.getText());
        Assertions.assertNull(node0.count);
        Assertions.assertEquals("\"file0\"", node0.file.getText());

        ObjectNode node1 = (ObjectNode) root.getChild(0).getChild(1);
        Assertions.assertEquals("obj1 : \"file1\"", node1.getText());
        Assertions.assertEquals("obj1", node1.name.getText());
        Assertions.assertNull(node0.count);
        Assertions.assertEquals("\"file1\"", node1.file.getText());
    }

    @Test
    void testParseObjectArray() {
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
            + "OBJ\n"
            + "    obj0[10] : \"file0\"\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(1, root.getChilds().size());
        Assertions.assertEquals(ObjectsNode.class, root.getChild(0).getClass());

        Assertions.assertEquals(1, root.getChild(0).getChilds().size());
        Assertions.assertEquals(ObjectNode.class, root.getChild(0).getChild(0).getClass());

        ObjectNode node = (ObjectNode) root.getChild(0).getChild(0);
        Assertions.assertEquals("obj0[10] : \"file0\"", node.getText());
        Assertions.assertEquals("obj0", node.name.getText());
        Assertions.assertEquals("10", node.count.getText());
        Assertions.assertEquals("\"file0\"", node.file.getText());
    }

    @Test
    void testParseObjectSyntaxError1() {
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
            + "OBJ\n"
            + "    obj0 = \n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(1, root.getChilds().size());
        Assertions.assertEquals(ObjectsNode.class, root.getChild(0).getClass());

        Assertions.assertEquals(1, root.getChild(0).getChilds().size());
        Assertions.assertEquals(ObjectNode.class, root.getChild(0).getChild(0).getClass());

        ObjectNode node = (ObjectNode) root.getChild(0).getChild(0);
        Assertions.assertEquals(ErrorNode.class, node.getChild(node.getChilds().size() - 1).getClass());
    }

    @Test
    void testParseObjectSyntaxError2() {
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
            + "OBJ\n"
            + "    obj0 : \"file0\" a0\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(1, root.getChilds().size());
        Assertions.assertEquals(ObjectsNode.class, root.getChild(0).getClass());

        Assertions.assertEquals(1, root.getChild(0).getChilds().size());
        Assertions.assertEquals(ObjectNode.class, root.getChild(0).getChild(0).getClass());

        ObjectNode node = (ObjectNode) root.getChild(0).getChild(0);
        Assertions.assertEquals(ErrorNode.class, node.getChild(node.getChilds().size() - 1).getClass());
    }

    @Test
    void testParseMethods() {
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
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
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
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

        Assertions.assertEquals("    method1", pub0.getChild(0).getText());
        Assertions.assertEquals("    repeat", pub0.getChild(1).getText());

        Assertions.assertEquals(StatementNode.class, pub0.getChild(0).getClass());
        Assertions.assertEquals(StatementNode.class, pub0.getChild(1).getClass());
    }

    @Test
    void testParseVariables() {
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
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
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
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
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
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
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
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
        Spin1Parser subject = new Spin1Parser(new Spin1TokenStream(""
            + "a := (b == 1) ? 2 : 3\n"
            + ""));

        Node root = subject.parseStatement(new Node(), subject.stream.nextToken());
        Assertions.assertEquals("a := (b == 1) ? 2 : 3", root.getText());
    }

    String tree(Node root) throws Exception {
        return tree(root, 0);
    }

    String tree(Node node, int indent) throws Exception {
        StringBuilder sb = new StringBuilder();

        if (indent != 0) {
            for (int i = 1; i < indent; i++) {
                sb.append("    ");
            }
            sb.append("+-- ");
        }

        sb.append(node.getClass().getSimpleName());
        if (node instanceof ConstantStatement) {
            ConstantStatement constant = (ConstantStatement) node;
            if (constant.identifier != null) {
                sb.append(" [" + constant.identifier + "]");
            }
            sb.append(System.lineSeparator());
            for (Node child : node.getChilds()) {
                for (int i = 1; i < indent + 1; i++) {
                    sb.append("    ");
                }
                sb.append("+-- ");

                Field[] field = node.getClass().getFields();
                for (int i = 0; i < field.length; i++) {
                    if (field[i].get(node) == child) {
                        sb.append(field[i].getName());
                        sb.append(" = ");
                    }
                }
                sb.append(tree(child, 0));
            }
        }
        else {
            sb.append(" [" + node.getText().replaceAll("\n", "\\\\n") + "]");
            sb.append(System.lineSeparator());

            for (Node child : node.getChilds()) {
                sb.append(tree(child, indent + 1));
            }
        }

        return sb.toString();
    }

}
