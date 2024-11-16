/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
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
    void testAbsoluteAddress() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "@@label\n"
            + ""));

        Assertions.assertEquals("@@label", subject.nextPAsmToken().getText());
    }

    @Test
    void testLocalLabelAddress() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "@.label\n"
            + ""));

        Assertions.assertEquals("@.label", subject.nextPAsmToken().getText());
    }

    @Test
    void testAbsoluteLocalLabelAddress() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "@@.label\n"
            + ""));

        Assertions.assertEquals("@@.label", subject.nextPAsmToken().getText());
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
            + "Node []\n"
            + "+-- ConstantsNode [CON]\n"
            + "    +-- ConstantNode identifier=EnableFlow [EnableFlow = 8]\n"
            + "        +-- expression = ExpressionNode [8]\n"
            + "    +-- ConstantNode identifier=DisableFlow [DisableFlow = 4]\n"
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
            + "Node []\n"
            + "+-- ConstantsNode [CON]\n"
            + "    +-- ConstantNode identifier=x [x = 5]\n"
            + "        +-- expression = ExpressionNode [5]\n"
            + "    +-- ConstantNode identifier=y [y = -5]\n"
            + "        +-- expression = ExpressionNode [-5]\n"
            + "    +-- ConstantNode identifier=z [z = 1]\n"
            + "        +-- expression = ExpressionNode [1]\n"
            + "", tree(root));
    }

    @Test
    void testEnum() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "CON\n"
            + "    #0,a,b,c,d\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- ConstantsNode [CON]\n"
            + "    +-- ConstantNode [#0]\n"
            + "        +-- start = ExpressionNode [0]\n"
            + "    +-- ConstantNode identifier=a [a]\n"
            + "    +-- ConstantNode identifier=b [b]\n"
            + "    +-- ConstantNode identifier=c [c]\n"
            + "    +-- ConstantNode identifier=d [d]\n"
            + "", tree(root));
    }

    @Test
    void testConLineEnum() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "CON  #0,a,b,c,d\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- ConstantsNode [CON]\n"
            + "    +-- ConstantNode [#0]\n"
            + "        +-- start = ExpressionNode [0]\n"
            + "    +-- ConstantNode identifier=a [a]\n"
            + "    +-- ConstantNode identifier=b [b]\n"
            + "    +-- ConstantNode identifier=c [c]\n"
            + "    +-- ConstantNode identifier=d [d]\n"
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
            + "Node []\n"
            + "+-- ConstantsNode [CON]\n"
            + "    +-- ConstantNode identifier=u [u[2]]\n"
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
            + "Node []\n"
            + "+-- ConstantsNode []\n"
            + "    +-- ConstantNode identifier=EnableFlow [EnableFlow = 8]\n"
            + "        +-- expression = ExpressionNode [8]\n"
            + "    +-- ConstantNode identifier=DisableFlow [DisableFlow = 4]\n"
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
            + "Node []\n"
            + "+-- ObjectsNode [OBJ]\n"
            + "    +-- ObjectNode name=obj0 file=\"file0\" [obj0 : \"file0\"]\n"
            + "", tree(root));
    }

    @Test
    void testObjectParameters() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "OBJ\n"
            + "    obj0 : \"file0\" | par0 = 1, par1 = 1\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- ObjectsNode [OBJ]\n"
            + "    +-- ObjectNode name=obj0 file=\"file0\" [obj0 : \"file0\" | par0 = 1, par1 = 1]\n"
            + "        +-- ParameterNode identifier=par0 [par0 = 1]\n"
            + "            +-- expression = ExpressionNode [1]\n"
            + "        +-- ParameterNode identifier=par1 [par1 = 1]\n"
            + "            +-- expression = ExpressionNode [1]\n"
            + "", tree(root));
    }

    @Test
    void testObjectNodeToString() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "OBJ\n"
            + "    obj0 : \"file0\"\n"
            + "    obj1 : \"file0\" | par0 = 1, par1 = 1\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals("    obj0 : \"file0\"", root.getChild(0).getChild(0).toString());
        Assertions.assertEquals("    obj1 : \"file0\" | par0 = 1, par1 = 1", root.getChild(0).getChild(1).toString());
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
            + "Node []\n"
            + "+-- ObjectsNode [OBJ]\n"
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
            + "Node []\n"
            + "+-- ObjectsNode [OBJ]\n"
            + "    +-- ObjectNode name=obj0 file=\"file0\" [obj0[10] : \"file0\"]\n"
            + "        +-- count = ExpressionNode [10]\n"
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
            + "Node []\n"
            + "+-- ObjectsNode [OBJ]\n"
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
            + "Node []\n"
            + "+-- ObjectsNode [OBJ]\n"
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
            + "Node []\n"
            + "+-- MethodNode type=PUB name=go [PUB go()]\n"
            + "", tree(root));
    }

    @Test
    void testMethodInvalidDeclarations() throws Exception {
        Node root = new Spin2Parser(new Spin2TokenStream(""
            + "PUB go to()\n"
            + "")).parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- MethodNode type=PUB name=go [PUB go to()]\n"
            + "", tree(root));

        root = new Spin2Parser(new Spin2TokenStream(""
            + "PUB go() to\n"
            + "")).parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- MethodNode type=PUB name=go [PUB go() to]\n"
            + "", tree(root));

        root = new Spin2Parser(new Spin2TokenStream(""
            + "PUB go(a,b) to\n"
            + "")).parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- MethodNode type=PUB name=go [PUB go(a,b) to]\n"
            + "    +-- ParameterNode identifier=a [a]\n"
            + "    +-- ParameterNode identifier=b [b]\n"
            + "", tree(root));
    }

    @Test
    void testMethodParameters() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB StartTx(pin, baud)\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- MethodNode type=PUB name=StartTx [PUB StartTx(pin, baud)]\n"
            + "    +-- ParameterNode identifier=pin [pin]\n"
            + "    +-- ParameterNode identifier=baud [baud]\n"
            + "", tree(root));
    }

    @Test
    void testMethodParameterDefaultValue() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB StartTx(pin, baud = 115_200)\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- MethodNode type=PUB name=StartTx [PUB StartTx(pin, baud = 115_200)]\n"
            + "    +-- ParameterNode identifier=pin [pin]\n"
            + "    +-- ParameterNode identifier=baud [baud = 115_200]\n"
            + "        +-- defaultValue = ExpressionNode [115_200]\n"
            + "", tree(root));
    }

    @Test
    void testMethodInvalidParameters() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB StartTx(pin baud)\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- MethodNode type=PUB name=StartTx [PUB StartTx(pin baud)]\n"
            + "    +-- ParameterNode identifier=pin [pin baud]\n"
            + "", tree(root));
    }

    @Test
    void testMethodLocalVariables() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PRI FFT1024() | a, b\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- MethodNode type=PRI name=FFT1024 [PRI FFT1024() | a, b]\n"
            + "    +-- LocalVariableNode identifier=a [a]\n"
            + "    +-- LocalVariableNode identifier=b [b]\n"
            + "", tree(root));
    }

    @Test
    void testMethodLocalVariableType() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PRI FFT1024() | LONG a, WORD b\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
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
            + "Node []\n"
            + "+-- MethodNode type=PRI name=FFT1024 [PRI FFT1024(DataPtr) | x[1024], y[512]]\n"
            + "    +-- ParameterNode identifier=DataPtr [DataPtr]\n"
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
            + "Node []\n"
            + "+-- MethodNode type=PRI name=FFT1024 [PRI FFT1024() : a, b]\n"
            + "    +-- ReturnNode identifier=a [a]\n"
            + "    +-- ReturnNode identifier=b [b]\n"
            + "", tree(root));
    }

    @Test
    void testMethodInvalidReturnVariables() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PRI FFT1024() : a b\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- MethodNode type=PRI name=FFT1024 [PRI FFT1024() : a b]\n"
            + "    +-- ReturnNode identifier=a [a b]\n"
            + "", tree(root));
    }

    @Test
    void testMethodUnexpectedTokens2() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB go(a,b) : c to\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- MethodNode type=PUB name=go [PUB go(a,b) : c to]\n"
            + "    +-- ParameterNode identifier=a [a]\n"
            + "    +-- ParameterNode identifier=b [b]\n"
            + "    +-- ReturnNode identifier=c [c to]\n"
            + "", tree(root));
    }

    @Test
    void testMethodInlinePAsm() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB go(a,b) : c | d, e\n"
            + "  org\n"
            + "      mov d, a\n"
            + "      and d, e wz\n"
            + "  end\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- MethodNode type=PUB name=go [PUB go(a,b) : c | d, e]\n"
            + "    +-- ParameterNode identifier=a [a]\n"
            + "    +-- ParameterNode identifier=b [b]\n"
            + "    +-- ReturnNode identifier=c [c]\n"
            + "    +-- LocalVariableNode identifier=d [d]\n"
            + "    +-- LocalVariableNode identifier=e [e]\n"
            + "    +-- DataLineNode instruction=org [  org]\n"
            + "    +-- DataLineNode instruction=mov [      mov d, a]\n"
            + "        +-- ParameterNode [d]\n"
            + "        +-- ParameterNode [a]\n"
            + "    +-- DataLineNode instruction=and [      and d, e wz]\n"
            + "        +-- ParameterNode [d]\n"
            + "        +-- ParameterNode [e]\n"
            + "        +-- modifier = Node [wz]\n"
            + "    +-- StatementNode [  end]\n"
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
            + "Node []\n"
            + "+-- DataNode [DAT]\n"
            + "    +-- DataLineNode instruction=wrlong [DAT    wrlong a,ptra]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [ptra]\n"
            + "    +-- DataLineNode instruction=wrlong [       wrlong a,ptra++]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [ptra++]\n"
            + "    +-- DataLineNode instruction=wrlong [       wrlong a,++ptra]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [++ptra]\n"
            + "    +-- DataLineNode instruction=wrlong [       wrlong a,ptra[3]]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [ptra]\n"
            + "            +-- count = ExpressionNode [3]\n"
            + "    +-- DataLineNode instruction=wrlong [       wrlong a,ptra--[3]]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [ptra--]\n"
            + "            +-- count = ExpressionNode [3]\n"
            + "    +-- DataLineNode instruction=wrlong [       wrlong a,--ptra[3]]\n"
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
            + "Node []\n"
            + "+-- VariablesNode [VAR]\n"
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
            + "Node []\n"
            + "+-- VariablesNode [VAR]\n"
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
            + "Node []\n"
            + "+-- VariablesNode [VAR]\n"
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
            + "Node []\n"
            + "+-- VariablesNode [VAR]\n"
            + "    +-- VariableNode identifier=a [a[10]]\n"
            + "        +-- size = ExpressionNode [10]\n"
            + "", tree(root));
    }

    @Test
    void testContinueNextLineOperator() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB main()\n"
            + "    a := ...\n"
            + "         1 + 2 * 3"
            + ""));
        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- MethodNode type=PUB name=main [PUB main()]\n"
            + "    +-- StatementNode [    a := ...\\n         1 + 2 * 3]\n"
            + "", tree(root));
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

    @Test
    void testCase() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB main() | a\n"
            + "\n"
            + "    case a\n"
            + "        1: a := 4\n"
            + "        2: a := 5\n"
            + "        3: a := 6\n"
            + "\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- MethodNode type=PUB name=main [PUB main() | a]\n"
            + "    +-- LocalVariableNode identifier=a [a]\n"
            + "    +-- StatementNode [    case a]\n"
            + "        +-- StatementNode [        1:]\n"
            + "            +-- StatementNode [        1: a := 4]\n"
            + "        +-- StatementNode [        2:]\n"
            + "            +-- StatementNode [        2: a := 5]\n"
            + "        +-- StatementNode [        3:]\n"
            + "            +-- StatementNode [        3: a := 6]\n"
            + "", tree(root));
    }

    @Test
    void testDefaultSectionPreprocessorDirective() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "     A = 1\n"
            + "  #ifdef P\n"
            + "     B = 2\n"
            + "  #endif\n"
            + "     C = 3\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- ConstantsNode []\n"
            + "    +-- ConstantNode identifier=A [A = 1]\n"
            + "        +-- expression = ExpressionNode [1]\n"
            + "    +-- DirectiveNode [#ifdef P]\n"
            + "    +-- ConstantNode identifier=B [B = 2]\n"
            + "        +-- expression = ExpressionNode [2]\n"
            + "    +-- DirectiveNode [#endif]\n"
            + "    +-- ConstantNode identifier=C [C = 3]\n"
            + "        +-- expression = ExpressionNode [3]\n"
            + "", tree(root));
    }

    @Test
    void testConstantsPreprocessorDirective() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "CON\n"
            + "     A = 1\n"
            + "  #ifdef P\n"
            + "     B = 2\n"
            + "  #endif\n"
            + "     C = 3\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- ConstantsNode [CON]\n"
            + "    +-- ConstantNode identifier=A [A = 1]\n"
            + "        +-- expression = ExpressionNode [1]\n"
            + "    +-- DirectiveNode [#ifdef P]\n"
            + "    +-- ConstantNode identifier=B [B = 2]\n"
            + "        +-- expression = ExpressionNode [2]\n"
            + "    +-- DirectiveNode [#endif]\n"
            + "    +-- ConstantNode identifier=C [C = 3]\n"
            + "        +-- expression = ExpressionNode [3]\n"
            + "", tree(root));
    }

    @Test
    void testConstantsEnumAndPreprocessor() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "CON\n"
            + "#ifdef P\n"
            + "#0,a,b,c,d\n"
            + "#endif\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- ConstantsNode [CON]\n"
            + "    +-- DirectiveNode [#ifdef P]\n"
            + "    +-- ConstantNode [#0]\n"
            + "        +-- start = ExpressionNode [0]\n"
            + "    +-- ConstantNode identifier=a [a]\n"
            + "    +-- ConstantNode identifier=b [b]\n"
            + "    +-- ConstantNode identifier=c [c]\n"
            + "    +-- ConstantNode identifier=d [d]\n"
            + "    +-- DirectiveNode [#endif]\n"
            + "", tree(root));
    }

    @Test
    void testMethodPreprocessorDirective() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB main()\n"
            + "  #ifdef A\n"
            + "    a := b * 2\n"
            + "  #endif\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- MethodNode type=PUB name=main [PUB main()]\n"
            + "    +-- DirectiveNode [#ifdef A]\n"
            + "    +-- StatementNode [    a := b * 2]\n"
            + "    +-- DirectiveNode [#endif]\n"
            + "", tree(root));
    }

    @Test
    void testStatementBlockPreprocessorDirective() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "PUB main()\n"
            + "    if 1\n"
            + "  #ifdef A\n"
            + "        a := b * 2\n"
            + "  #endif\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- MethodNode type=PUB name=main [PUB main()]\n"
            + "    +-- StatementNode [    if 1]\n"
            + "        +-- DirectiveNode [#ifdef A]\n"
            + "        +-- StatementNode [        a := b * 2]\n"
            + "        +-- DirectiveNode [#endif]\n"
            + "", tree(root));
    }

    @Test
    void testPAsmPreprocessorDirective() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "DAT\n"
            + "        org $000\n"
            + "  #ifdef A\n"
            + "        mov a, #1\n"
            + "  #endif\n"
            + "        ret\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- DataNode [DAT]\n"
            + "    +-- DataLineNode instruction=org [        org $000]\n"
            + "        +-- ParameterNode [$000]\n"
            + "    +-- DirectiveNode [#ifdef A]\n"
            + "    +-- DataLineNode instruction=mov [        mov a, #1]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [#1]\n"
            + "    +-- DirectiveNode [#endif]\n"
            + "    +-- DataLineNode instruction=ret [        ret]\n"
            + "", tree(root));
    }

    @Test
    void testStructure() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "CON\n"
            + "    sPoint(x, y)\n"
            + "    sLine(sPoint a, sPoint b, BYTE color)\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- ConstantsNode [CON]\n"
            + "    +-- TypeDefinitionNode identifier=sPoint [sPoint(x, y)]\n"
            + "        +-- Node [x]\n"
            + "        +-- Node [y]\n"
            + "    +-- TypeDefinitionNode identifier=sLine [sLine(sPoint a, sPoint b, BYTE color)]\n"
            + "        +-- Node [sPoint a]\n"
            + "        +-- Node [sPoint b]\n"
            + "        +-- Node [BYTE color]\n"
            + "", tree(root));
    }

    @Test
    void testStructureDeclaration() throws Exception {
        Spin2Parser subject = new Spin2Parser(new Spin2TokenStream(""
            + "CON\n"
            + "    struct sPoint(x, y)\n"
            + "    struct sLine(sPoint a, sPoint b, BYTE color)\n"
            + ""));

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "Node []\n"
            + "+-- ConstantsNode [CON]\n"
            + "    +-- TypeDefinitionNode type=struct identifier=sPoint [struct sPoint(x, y)]\n"
            + "        +-- Node [x]\n"
            + "        +-- Node [y]\n"
            + "    +-- TypeDefinitionNode type=struct identifier=sLine [struct sLine(sPoint a, sPoint b, BYTE color)]\n"
            + "        +-- Node [sPoint a]\n"
            + "        +-- Node [sPoint b]\n"
            + "        +-- Node [BYTE color]\n"
            + "", tree(root));
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
