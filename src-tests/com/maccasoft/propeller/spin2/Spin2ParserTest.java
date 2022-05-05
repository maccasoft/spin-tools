/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
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

import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.Token;

class Spin2ParserTest {

    @Test
    void testLocalLabel() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + ".label\n"
            + ""));

        Assertions.assertEquals(".label", subject.nextPAsmToken().getText());
    }

    @Test
    void testAddress() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "@label\n"
            + ""));

        Assertions.assertEquals("@label", subject.nextPAsmToken().getText());
    }

    @Test
    void testLocalLabelAddress() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "@.label\n"
            + ""));

        Assertions.assertEquals("@.label", subject.nextPAsmToken().getText());
    }

    @Test
    void testImmediateLabel() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "#label\n"
            + ""));

        Assertions.assertEquals("#", subject.nextPAsmToken().getText());
        Assertions.assertEquals("label", subject.nextPAsmToken().getText());
    }

    @Test
    void testImmediateLocalLabel() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "#.label\n"
            + ""));

        Assertions.assertEquals("#", subject.nextPAsmToken().getText());
        Assertions.assertEquals(".label", subject.nextPAsmToken().getText());
    }

    @Test
    void testAugmentedImmediateLabel() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "##label\n"
            + ""));

        Assertions.assertEquals("##", subject.nextPAsmToken().getText());
        Assertions.assertEquals("label", subject.nextPAsmToken().getText());
    }

    @Test
    void testAugmentedImmediateLocalLabel() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "##.label\n"
            + ""));

        Assertions.assertEquals("##", subject.nextPAsmToken().getText());
        Assertions.assertEquals(".label", subject.nextPAsmToken().getText());
    }

    @Test
    void testRangeValue() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "a..b\n"
            + ""));

        Assertions.assertEquals("a", subject.nextToken().getText());
        Assertions.assertEquals("..", subject.nextToken().getText());
        Assertions.assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testSpin1LocalLabel() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + ":label\n"
            + ""));

        Assertions.assertEquals(":", subject.nextToken().getText());
        Assertions.assertEquals("label", subject.nextToken().getText());
    }

    @Test
    void testObjectMethod() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "obj.method\n"
            + ""));

        Assertions.assertEquals("obj.method", subject.nextToken().getText());
    }

    @Test
    void testObjectArrayMethod() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "obj[0].method\n"
            + ""));

        Assertions.assertEquals("obj", subject.nextToken().getText());
        Assertions.assertEquals("[", subject.nextToken().getText());
        Assertions.assertEquals("0", subject.nextToken().getText());
        Assertions.assertEquals("]", subject.nextToken().getText());
        Assertions.assertEquals(".method", subject.nextToken().getText());
    }

    @Test
    void testSingleAssigments() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "CON  EnableFlow = 8\n"
            + "     DisableFlow = 4\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [CON  EnableFlow = 8\\n     DisableFlow = 4\\n]\n"
            + "+-- ConstantsNode [CON  EnableFlow = 8\\n     DisableFlow = 4]\n"
            + "    +-- ConstantAssignNode identifier=EnableFlow [EnableFlow = 8]\n"
            + "        +-- expression = ExpressionNode [8]\n"
            + "    +-- ConstantAssignNode identifier=DisableFlow [DisableFlow = 4]\n"
            + "        +-- expression = ExpressionNode [4]\n"
            + "", tree(root));
    }

    @Test
    void testCommaSeparatedAssignments() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "CON\n"
            + "     x = 5, y = -5, z = 1\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [CON\\n     x = 5, y = -5, z = 1\\n]\n"
            + "+-- ConstantsNode [CON\\n     x = 5, y = -5, z = 1]\n"
            + "    +-- ConstantAssignNode identifier=x [x = 5]\n"
            + "        +-- expression = ExpressionNode [5]\n"
            + "    +-- ConstantAssignNode identifier=y [y = -5]\n"
            + "        +-- expression = ExpressionNode [-5]\n"
            + "    +-- ConstantAssignNode identifier=z [z = 1]\n"
            + "        +-- expression = ExpressionNode [1]\n"
            + "", tree(root));
    }

    @Test
    void testEnumAssignments() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "CON  #0,a,b,c,d\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [CON  #0,a,b,c,d\\n]\n"
            + "+-- ConstantsNode [CON  #0,a,b,c,d]\n"
            + "    +-- ConstantSetEnumNode [#0]\n"
            + "        +-- start = ExpressionNode [0]\n"
            + "    +-- ConstantAssignNode identifier=a [a]\n"
            + "    +-- ConstantAssignNode identifier=b [b]\n"
            + "    +-- ConstantAssignNode identifier=c [c]\n"
            + "    +-- ConstantAssignNode identifier=d [d]\n"
            + "", tree(root));
    }

    @Test
    void testEnumAssigmentStepMultiplier() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "CON\n"
            + "     u[2]\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [CON\\n     u[2]\\n]\n"
            + "+-- ConstantsNode [CON\\n     u[2]]\n"
            + "    +-- ConstantAssignNode identifier=u [u[2]]\n"
            + "        +-- multiplier = ExpressionNode [2]\n"
            + "", tree(root));
    }

    @Test
    void testDefaultSection() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "     EnableFlow = 8\n"
            + "     DisableFlow = 4\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [EnableFlow = 8\\n     DisableFlow = 4\\n]\n"
            + "+-- ConstantsNode [EnableFlow = 8\\n     DisableFlow = 4]\n"
            + "    +-- ConstantAssignNode identifier=EnableFlow [EnableFlow = 8]\n"
            + "        +-- expression = ExpressionNode [8]\n"
            + "    +-- ConstantAssignNode identifier=DisableFlow [DisableFlow = 4]\n"
            + "        +-- expression = ExpressionNode [4]\n"
            + "", tree(root));
    }

    @Test
    void testParseObject() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "OBJ\n"
            + "    obj0 : \"file0\"\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [OBJ\\n    obj0 : \"file0\"\\n]\n"
            + "+-- ObjectsNode [OBJ\\n    obj0 : \"file0\"]\n"
            + "    +-- ObjectNode name=obj0 file=\"file0\" [obj0 : \"file0\"]\n"
            + "", tree(root));
    }

    @Test
    void testParseObjects() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "OBJ\n"
            + "    obj0 : \"file0\"\n"
            + "    obj1 : \"file1\"\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [OBJ\\n    obj0 : \"file0\"\\n    obj1 : \"file1\"\\n]\n"
            + "+-- ObjectsNode [OBJ\\n    obj0 : \"file0\"\\n    obj1 : \"file1\"]\n"
            + "    +-- ObjectNode name=obj0 file=\"file0\" [obj0 : \"file0\"]\n"
            + "    +-- ObjectNode name=obj1 file=\"file1\" [obj1 : \"file1\"]\n"
            + "", tree(root));
    }

    @Test
    void testParseObjectArray() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "OBJ\n"
            + "    obj0[10] : \"file0\"\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [OBJ\\n    obj0[10] : \"file0\"\\n]\n"
            + "+-- ObjectsNode [OBJ\\n    obj0[10] : \"file0\"]\n"
            + "    +-- ObjectNode name=obj0 file=\"file0\" [obj0[10] : \"file0\"]\n"
            + "        +-- count = Node [10]\n"
            + "", tree(root));
    }

    @Test
    void testParseObjectSyntaxError1() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "OBJ\n"
            + "    obj0 = \n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [OBJ\\n    obj0 = \\n]\n"
            + "+-- ObjectsNode [OBJ\\n    obj0 =]\n"
            + "    +-- ObjectNode name=obj0 [obj0 =]\n"
            + "", tree(root));
    }

    @Test
    void testParseObjectSyntaxError2() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "OBJ\n"
            + "    obj0 : \"file0\" a0\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [OBJ\\n    obj0 : \"file0\" a0\\n]\n"
            + "+-- ObjectsNode [OBJ\\n    obj0 : \"file0\" a0]\n"
            + "    +-- ObjectNode name=obj0 file=\"file0\" [obj0 : \"file0\" a0]\n"
            + "", tree(root));
    }

    @Test
    void testMethod() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB go()\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [PUB go()\\n]\n"
            + "+-- MethodNode type=PUB name=go [PUB go()]\n"
            + "", tree(root));
    }

    @Test
    void testMethodInvalidDeclarations() throws Exception {
        Node root = new Spin2Parser(new Spin2TokenStream(""
            + "PUB go to()\n"
            + "")).parse();
        Assertions.assertEquals(""
            + "Node [PUB go to()\\n]\n"
            + "+-- MethodNode type=PUB name=go [PUB go to()]\n"
            + "    +-- ErrorNode [to]\n"
            + "", tree(root));

        root = new Spin2Parser(new Spin2TokenStream(""
            + "PUB go() to\n"
            + "")).parse();
        Assertions.assertEquals(""
            + "Node [PUB go() to\\n]\n"
            + "+-- MethodNode type=PUB name=go [PUB go() to]\n"
            + "    +-- ErrorNode [to]\n"
            + "", tree(root));

        root = new Spin2Parser(new Spin2TokenStream(""
            + "PUB go(a,b) to\n"
            + "")).parse();
        Assertions.assertEquals(""
            + "Node [PUB go(a,b) to\\n]\n"
            + "+-- MethodNode type=PUB name=go [PUB go(a,b) to]\n"
            + "    +-- Node [a]\n"
            + "    +-- Node [b]\n"
            + "    +-- ErrorNode [to]\n"
            + "", tree(root));
    }

    @Test
    void testMethodParameters() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB StartTx(pin, baud)\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [PUB StartTx(pin, baud)\\n]\n"
            + "+-- MethodNode type=PUB name=StartTx [PUB StartTx(pin, baud)]\n"
            + "    +-- Node [pin]\n"
            + "    +-- Node [baud]\n"
            + "", tree(root));
    }

    @Test
    void testMethodInvalidParameters() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB StartTx(pin baud)\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [PUB StartTx(pin baud)\\n]\n"
            + "+-- MethodNode type=PUB name=StartTx [PUB StartTx(pin baud)]\n"
            + "    +-- Node [pin baud]\n"
            + "", tree(root));
    }

    @Test
    void testMethodLocalVariables() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PRI FFT1024() | a, b\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [PRI FFT1024() | a, b\\n]\n"
            + "+-- MethodNode type=PRI name=FFT1024 [PRI FFT1024() | a, b]\n"
            + "    +-- LocalVariableNode identifier=a [a]\n"
            + "    +-- LocalVariableNode identifier=b [b]\n"
            + "", tree(root));
    }

    @Test
    void testMethodInvalidLocalVariables() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PRI FFT1024() | a b\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [PRI FFT1024() | a b\\n]\n"
            + "+-- MethodNode type=PRI name=FFT1024 [PRI FFT1024() | a b]\n"
            + "    +-- LocalVariableNode identifier=a [a b]\n"
            + "", tree(root));
    }

    @Test
    void testMethodLocalVariableType() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PRI FFT1024() | LONG a, WORD b\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [PRI FFT1024() | LONG a, WORD b\\n]\n"
            + "+-- MethodNode type=PRI name=FFT1024 [PRI FFT1024() | LONG a, WORD b]\n"
            + "    +-- LocalVariableNode type=LONG identifier=a [LONG a]\n"
            + "    +-- LocalVariableNode type=WORD identifier=b [WORD b]\n"
            + "", tree(root));
    }

    @Test
    void testMethodLocalVariableSize() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PRI FFT1024(DataPtr) | x[1024], y[512]\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [PRI FFT1024(DataPtr) | x[1024], y[512]\\n]\n"
            + "+-- MethodNode type=PRI name=FFT1024 [PRI FFT1024(DataPtr) | x[1024], y[512]]\n"
            + "    +-- Node [DataPtr]\n"
            + "    +-- LocalVariableNode identifier=x [x[1024]]\n"
            + "        +-- size = ExpressionNode [1024]\n"
            + "    +-- LocalVariableNode identifier=y [y[512]]\n"
            + "        +-- size = ExpressionNode [512]\n"
            + "", tree(root));
    }

    @Test
    void testMethodReturnVariables() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PRI FFT1024() : a, b\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [PRI FFT1024() : a, b\\n]\n"
            + "+-- MethodNode type=PRI name=FFT1024 [PRI FFT1024() : a, b]\n"
            + "    +-- Node [a]\n"
            + "    +-- Node [b]\n"
            + "", tree(root));
    }

    @Test
    void testMethodInvalidReturnVariables() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PRI FFT1024() : a b\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [PRI FFT1024() : a b\\n]\n"
            + "+-- MethodNode type=PRI name=FFT1024 [PRI FFT1024() : a b]\n"
            + "    +-- Node [a b]\n"
            + "", tree(root));
    }

    @Test
    void testMethodUnexpectedTokens2() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB go(a,b) : c to\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [PUB go(a,b) : c to\\n]\n"
            + "+-- MethodNode type=PUB name=go [PUB go(a,b) : c to]\n"
            + "    +-- Node [a]\n"
            + "    +-- Node [b]\n"
            + "    +-- Node [c to]\n"
            + "", tree(root));
    }

    @Test
    void testMethodUnexpectedTokens3() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB go(a,b) | d, e to\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [PUB go(a,b) | d, e to\\n]\n"
            + "+-- MethodNode type=PUB name=go [PUB go(a,b) | d, e to]\n"
            + "    +-- Node [a]\n"
            + "    +-- Node [b]\n"
            + "    +-- LocalVariableNode identifier=d [d]\n"
            + "    +-- LocalVariableNode identifier=e [e to]\n"
            + "", tree(root));
    }

    @Test
    void testMethodUnexpectedTokens4() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB go(a,b) : c | d, e to\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [PUB go(a,b) : c | d, e to\\n]\n"
            + "+-- MethodNode type=PUB name=go [PUB go(a,b) : c | d, e to]\n"
            + "    +-- Node [a]\n"
            + "    +-- Node [b]\n"
            + "    +-- Node [c]\n"
            + "    +-- LocalVariableNode identifier=d [d]\n"
            + "    +-- LocalVariableNode identifier=e [e to]\n"
            + "", tree(root));
    }

    @Test
    void testParsePtr() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "DAT    wrlong a,ptra\n"
            + "       wrlong a,ptra++\n"
            + "       wrlong a,++ptra\n"
            + "       wrlong a,ptra[3]\n"
            + "       wrlong a,ptra--[3]\n"
            + "       wrlong a,--ptra[3]\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [DAT    wrlong a,ptra\\n       wrlong a,ptra++\\n       wrlong a,++ptra\\n       wrlong a,ptra[3]\\n       wrlong a,ptra--[3]\\n       wrlong a,--ptra[3]\\n]\n"
            + "+-- DataNode [DAT]\n"
            + "    +-- DataLineNode [DAT    wrlong a,ptra]\n"
            + "        +-- instruction = Node [wrlong]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [ptra]\n"
            + "    +-- DataLineNode [       wrlong a,ptra++]\n"
            + "        +-- instruction = Node [wrlong]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [ptra++]\n"
            + "    +-- DataLineNode [       wrlong a,++ptra]\n"
            + "        +-- instruction = Node [wrlong]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [++ptra]\n"
            + "    +-- DataLineNode [       wrlong a,ptra[3]]\n"
            + "        +-- instruction = Node [wrlong]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [ptra]\n"
            + "            +-- count = ExpressionNode [3]\n"
            + "    +-- DataLineNode [       wrlong a,ptra--[3]]\n"
            + "        +-- instruction = Node [wrlong]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [ptra--]\n"
            + "            +-- count = ExpressionNode [3]\n"
            + "    +-- DataLineNode [       wrlong a,--ptra[3]]\n"
            + "        +-- instruction = Node [wrlong]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [--ptra]\n"
            + "            +-- count = ExpressionNode [3]\n"
            + "", tree(root));
    }

    @Test
    void testParseVariables() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "VAR a\n"
            + "    b\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [VAR a\\n    b\\n]\n"
            + "+-- VariablesNode [VAR a\\n    b]\n"
            + "    +-- VariableNode identifier=a [a]\n"
            + "    +-- VariableNode identifier=b [b]\n"
            + "", tree(root));
    }

    @Test
    void testParseTypedVariables() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "VAR long a\n"
            + "    word b\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [VAR long a\\n    word b\\n]\n"
            + "+-- VariablesNode [VAR long a\\n    word b]\n"
            + "    +-- VariableNode type=long identifier=a [long a]\n"
            + "    +-- VariableNode type=word identifier=b [word b]\n"
            + "", tree(root));
    }

    @Test
    void testParseVariablesList() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "VAR long a, b, c\n"
            + "    word d, e\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [VAR long a, b, c\\n    word d, e\\n]\n"
            + "+-- VariablesNode [VAR long a, b, c\\n    word d, e]\n"
            + "    +-- VariableNode type=long identifier=a [long a]\n"
            + "    +-- VariableNode identifier=b [b]\n"
            + "    +-- VariableNode identifier=c [c]\n"
            + "    +-- VariableNode type=word identifier=d [word d]\n"
            + "    +-- VariableNode identifier=e [e]\n"
            + "", tree(root));
    }

    @Test
    void testParseVariableSize() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "VAR a[10]\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node [VAR a[10]\\n]\n"
            + "+-- VariablesNode [VAR a[10]]\n"
            + "    +-- VariableNode identifier=a [a[10]]\n"
            + "        +-- size = ExpressionNode [10]\n"
            + "", tree(root));
    }

    @Test
    void testParseTernaryExpression() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "a := (b == 1) ? 2 : 3\n"
            + ""));

        Node root = subject.parseStatement(new Node(), subject.stream.nextToken());
        Assertions.assertEquals("a := (b == 1) ? 2 : 3", root.getText());
    }

    @Test
    void testDatDebugLines() {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "DAT    org $000\n"
            + "       debug(`STARTING)\n"
            + ""));

        Node root = subject.parse();
        DataNode data0 = (DataNode) root.getChild(0);

        DataLineNode line0 = (DataLineNode) data0.getChild(0);
        Assertions.assertEquals("org", line0.instruction.getText());
        DataLineNode line1 = (DataLineNode) data0.getChild(1);
        Assertions.assertEquals("debug", line1.instruction.getText());
        Assertions.assertEquals("(", line1.getToken(1).getText());
        Assertions.assertEquals("`STARTING", line1.getToken(2).getText());
        Assertions.assertEquals(")", line1.getToken(3).getText());
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
