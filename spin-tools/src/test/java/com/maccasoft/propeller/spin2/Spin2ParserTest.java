/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;

class Spin2ParserTest {

    @Test
    void testSingleAssigments() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "CON  EnableFlow = 8\n"
            + "     DisableFlow = 4\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- ConstantsNode [CON]\n"
            + "    +-- ConstantNode identifier=EnableFlow [EnableFlow = 8]\n"
            + "        +-- expression = ExpressionNode [8]\n"
            + "    +-- ConstantNode identifier=DisableFlow [DisableFlow = 4]\n"
            + "        +-- expression = ExpressionNode [4]\n"
            + "", tree(root));
    }

    @Test
    void testCommaSeparatedAssignments() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "CON\n"
            + "     x = 5, y = -5, z = 1\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
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
        Spin2Parser subject = new Spin2Parser(""
            + "CON\n"
            + "    #0,a,b,c,d\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
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
        Spin2Parser subject = new Spin2Parser(""
            + "CON  #0,a,b,c,d\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
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
        Spin2Parser subject = new Spin2Parser(""
            + "CON\n"
            + "     u[2]\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- ConstantsNode [CON]\n"
            + "    +-- ConstantNode identifier=u [u[2]]\n"
            + "        +-- multiplier = ExpressionNode [2]\n"
            + "", tree(root));
    }

    @Test
    void testDefaultSection() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "     EnableFlow = 8\n"
            + "     DisableFlow = 4\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- ConstantsNode []\n"
            + "    +-- ConstantNode identifier=EnableFlow [EnableFlow = 8]\n"
            + "        +-- expression = ExpressionNode [8]\n"
            + "    +-- ConstantNode identifier=DisableFlow [DisableFlow = 4]\n"
            + "        +-- expression = ExpressionNode [4]\n"
            + "", tree(root));
    }

    @Test
    void testParseObject() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "OBJ\n"
            + "    obj0 : \"file0\"\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- ObjectsNode [OBJ]\n"
            + "    +-- ObjectNode name=obj0 file=\"file0\" [obj0 : \"file0\"]\n"
            + "", tree(root));
    }

    @Test
    void testObjectParameters() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "OBJ\n"
            + "    obj0 : \"file0\" | par0 = 1, par1 = 1\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- ObjectsNode [OBJ]\n"
            + "    +-- ObjectNode name=obj0 file=\"file0\" [obj0 : \"file0\" | par0 = 1, par1 = 1]\n"
            + "        +-- ParameterNode identifier=par0 [par0 = 1]\n"
            + "            +-- expression = ExpressionNode [1]\n"
            + "        +-- ParameterNode identifier=par1 [par1 = 1]\n"
            + "            +-- expression = ExpressionNode [1]\n"
            + "", tree(root));
    }

    @Test
    void testObjectGetText() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "OBJ\n"
            + "    obj0 : \"file0\"\n"
            + "    obj1 : \"file0\" | par0 = 1, par1 = 1\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals("    obj0 : \"file0\"", root.getChild(0).getChild(0).getText());
        Assertions.assertEquals("    obj1 : \"file0\" | par0 = 1, par1 = 1", root.getChild(0).getChild(1).getText());
    }

    @Test
    void testParseObjects() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "OBJ\n"
            + "    obj0 : \"file0\"\n"
            + "    obj1 : \"file1\"\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- ObjectsNode [OBJ]\n"
            + "    +-- ObjectNode name=obj0 file=\"file0\" [obj0 : \"file0\"]\n"
            + "    +-- ObjectNode name=obj1 file=\"file1\" [obj1 : \"file1\"]\n"
            + "", tree(root));
    }

    @Test
    void testParseObjectArray() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "OBJ\n"
            + "    obj0[10] : \"file0\"\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- ObjectsNode [OBJ]\n"
            + "    +-- ObjectNode name=obj0 file=\"file0\" [obj0[10] : \"file0\"]\n"
            + "        +-- count = ExpressionNode [10]\n"
            + "", tree(root));
    }

    @Test
    void testParseObjectSyntaxError1() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "OBJ\n"
            + "    obj0 = \n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- ObjectsNode [OBJ]\n"
            + "    +-- ObjectNode name=obj0 [obj0 =]\n"
            + "", tree(root));
    }

    @Test
    void testParseObjectSyntaxError2() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "OBJ\n"
            + "    obj0 : \"file0\" a0\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- ObjectsNode [OBJ]\n"
            + "    +-- ObjectNode name=obj0 file=\"file0\" [obj0 : \"file0\" a0]\n"
            + "", tree(root));
    }

    @Test
    void testMethod() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "PUB go()\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=go [PUB go()]\n"
            + "", tree(root));
    }

    @Test
    void testMethodInvalidDeclarations() throws Exception {
        Node root = new Spin2Parser(""
            + "PUB go to()\n"
            + "").parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=go [PUB go to()]\n"
            + "", tree(root));

        root = new Spin2Parser(""
            + "PUB go() to\n"
            + "").parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=go [PUB go() to]\n"
            + "", tree(root));

        root = new Spin2Parser(""
            + "PUB go(a,b) to\n"
            + "").parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=go [PUB go(a,b) to]\n"
            + "    +-- ParameterNode identifier=a [a]\n"
            + "    +-- ParameterNode identifier=b [b]\n"
            + "", tree(root));
    }

    @Test
    void testMethodParameters() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "PUB StartTx(pin, baud)\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=StartTx [PUB StartTx(pin, baud)]\n"
            + "    +-- ParameterNode identifier=pin [pin]\n"
            + "    +-- ParameterNode identifier=baud [baud]\n"
            + "", tree(root));
    }

    @Test
    void testMethodParameterDefaultValue() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "PUB StartTx(pin, baud = 115_200)\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=StartTx [PUB StartTx(pin, baud = 115_200)]\n"
            + "    +-- ParameterNode identifier=pin [pin]\n"
            + "    +-- ParameterNode identifier=baud [baud = 115_200]\n"
            + "        +-- defaultValue = ExpressionNode [115_200]\n"
            + "", tree(root));
    }

    @Test
    void testMethodLocalVariables() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "PRI FFT1024() | a, b\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PRI name=FFT1024 [PRI FFT1024() | a, b]\n"
            + "    +-- LocalVariableNode identifier=a [a]\n"
            + "    +-- LocalVariableNode identifier=b [b]\n"
            + "", tree(root));
    }

    @Test
    void testMethodLocalVariableType() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "PRI FFT1024() | LONG a, WORD b\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PRI name=FFT1024 [PRI FFT1024() | LONG a, WORD b]\n"
            + "    +-- LocalVariableNode type=LONG identifier=a [LONG a]\n"
            + "    +-- LocalVariableNode type=WORD identifier=b [WORD b]\n"
            + "", tree(root));
    }

    @Test
    void testMethodLocalVariableSize() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "PRI FFT1024(DataPtr) | x[1024], y[512]\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
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
        Spin2Parser subject = new Spin2Parser(""
            + "PRI FFT1024() : a, b\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PRI name=FFT1024 [PRI FFT1024() : a, b]\n"
            + "    +-- ReturnNode identifier=a [a]\n"
            + "    +-- ReturnNode identifier=b [b]\n"
            + "", tree(root));
    }

    @Test
    void testMethodInlinePAsm() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "PUB go(a,b) : c | d, e\n"
            + "  org\n"
            + "      mov d, a\n"
            + "      and d, e wz\n"
            + "  end\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=go [PUB go(a,b) : c | d, e]\n"
            + "    +-- ParameterNode identifier=a [a]\n"
            + "    +-- ParameterNode identifier=b [b]\n"
            + "    +-- ReturnNode identifier=c [c]\n"
            + "    +-- LocalVariableNode identifier=d [d]\n"
            + "    +-- LocalVariableNode identifier=e [e]\n"
            + "    +-- DataLineNode instruction=org [org]\n"
            + "    +-- DataLineNode instruction=mov [mov d, a]\n"
            + "        +-- ParameterNode [d]\n"
            + "        +-- ParameterNode [a]\n"
            + "    +-- DataLineNode instruction=and [and d, e wz]\n"
            + "        +-- ParameterNode [d]\n"
            + "        +-- ParameterNode [e]\n"
            + "        +-- modifier = ModifierNode [wz]\n"
            + "    +-- StatementNode [end]\n"
            + "", tree(root));
    }

    @Test
    void testParsePtr() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "DAT    wrlong a,ptra\n"
            + "       wrlong a,ptra++\n"
            + "       wrlong a,++ptra\n"
            + "       wrlong a,ptra[3]\n"
            + "       wrlong a,ptra--[3]\n"
            + "       wrlong a,--ptra[3]\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- DataNode [DAT]\n"
            + "    +-- DataLineNode instruction=wrlong [wrlong a,ptra]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [ptra]\n"
            + "    +-- DataLineNode instruction=wrlong [wrlong a,ptra++]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [ptra++]\n"
            + "    +-- DataLineNode instruction=wrlong [wrlong a,++ptra]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [++ptra]\n"
            + "    +-- DataLineNode instruction=wrlong [wrlong a,ptra[3]]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [ptra[3]]\n"
            + "    +-- DataLineNode instruction=wrlong [wrlong a,ptra--[3]]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [ptra--[3]]\n"
            + "    +-- DataLineNode instruction=wrlong [wrlong a,--ptra[3]]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [--ptra[3]]\n"
            + "", tree(root));
    }

    @Test
    void testVariables() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "VAR a\n"
            + "    b\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- VariablesNode [VAR a]\n"
            + "    +-- VariableNode identifier=a [a]\n"
            + "    +-- VariablesNode [b]\n"
            + "        +-- VariableNode identifier=b [b]\n"
            + "", tree(root));
    }

    @Test
    void testTypedVariables() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "VAR long a\n"
            + "    word b\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- VariablesNode [VAR long a]\n"
            + "    +-- VariableNode type=long identifier=a [long a]\n"
            + "    +-- VariablesNode [word b]\n"
            + "        +-- VariableNode type=word identifier=b [word b]\n"
            + "", tree(root));
    }

    @Test
    void testVariablesList() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "VAR long a, b, c\n"
            + "    word d, e\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- VariablesNode [VAR long a, b, c]\n"
            + "    +-- VariableNode type=long identifier=a [long a]\n"
            + "    +-- VariableNode identifier=b [b]\n"
            + "    +-- VariableNode identifier=c [c]\n"
            + "    +-- VariablesNode [word d, e]\n"
            + "        +-- VariableNode type=word identifier=d [word d]\n"
            + "        +-- VariableNode identifier=e [e]\n"
            + "", tree(root));
    }

    @Test
    void testVariableSize() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "VAR a[10]\n"
            + "    b[20]\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- VariablesNode [VAR a[10]]\n"
            + "    +-- VariableNode identifier=a [a[10]]\n"
            + "        +-- size = ExpressionNode [10]\n"
            + "    +-- VariablesNode [b[20]]\n"
            + "        +-- VariableNode identifier=b [b[20]]\n"
            + "            +-- size = ExpressionNode [20]\n"
            + "", tree(root));
    }

    @Test
    void testVariablesListAndSize() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "VAR a, b[10], c\n"
            + "    word d[20], e\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- VariablesNode [VAR a, b[10], c]\n"
            + "    +-- VariableNode identifier=a [a]\n"
            + "    +-- VariableNode identifier=b [b[10]]\n"
            + "        +-- size = ExpressionNode [10]\n"
            + "    +-- VariableNode identifier=c [c]\n"
            + "    +-- VariablesNode [word d[20], e]\n"
            + "        +-- VariableNode type=word identifier=d [word d[20]]\n"
            + "            +-- size = ExpressionNode [20]\n"
            + "        +-- VariableNode identifier=e [e]\n"
            + "", tree(root));
    }

    @Test
    void testVariablesPreprocessor() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "VAR a, b[10], c\n"
            + "#ifdef KEY\n"
            + "    word d[20], e\n"
            + "#endif\n"
            + "    long f, g\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- VariablesNode [VAR a, b[10], c]\n"
            + "    +-- VariableNode identifier=a [a]\n"
            + "    +-- VariableNode identifier=b [b[10]]\n"
            + "        +-- size = ExpressionNode [10]\n"
            + "    +-- VariableNode identifier=c [c]\n"
            + "    +-- DirectiveNode [#ifdef KEY]\n"
            + "    +-- VariablesNode [word d[20], e]\n"
            + "        +-- VariableNode type=word identifier=d [word d[20]]\n"
            + "            +-- size = ExpressionNode [20]\n"
            + "        +-- VariableNode identifier=e [e]\n"
            + "    +-- DirectiveNode [#endif]\n"
            + "    +-- VariablesNode [long f, g]\n"
            + "        +-- VariableNode type=long identifier=f [long f]\n"
            + "        +-- VariableNode identifier=g [g]\n"
            + "", tree(root));
    }

    @Test
    void testContinueNextLineOperator() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "PUB main()\n"
            + "    a := ...\n"
            + "         1 + 2 * 3"
            + "");
        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=main [PUB main()]\n"
            + "    +-- StatementNode [a := 1 + 2 * 3]\n"
            + "", tree(root));
    }

    @Test
    void testDatDebugLines() {
        Spin2Parser subject = new Spin2Parser(""
            + "DAT    org $000\n"
            + "       debug(`STARTING)\n"
            + "");

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
        Spin2Parser subject = new Spin2Parser(""
            + "PUB main() | a\n"
            + "\n"
            + "    case a\n"
            + "        1: a := 4\n"
            + "        2: a := 5\n"
            + "        3: a := 6\n"
            + "\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=main [PUB main() | a]\n"
            + "    +-- LocalVariableNode identifier=a [a]\n"
            + "    +-- StatementNode [case a]\n"
            + "        +-- StatementNode [1:]\n"
            + "            +-- StatementNode [a := 4]\n"
            + "        +-- StatementNode [2:]\n"
            + "            +-- StatementNode [a := 5]\n"
            + "        +-- StatementNode [3:]\n"
            + "            +-- StatementNode [a := 6]\n"
            + "", tree(root));
    }

    @Test
    void testDefaultSectionPreprocessorDirective() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "     A = 1\n"
            + "  #ifdef P\n"
            + "     B = 2\n"
            + "  #endif\n"
            + "     C = 3\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
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
        Spin2Parser subject = new Spin2Parser(""
            + "CON\n"
            + "     A = 1\n"
            + "  #ifdef P\n"
            + "     B = 2\n"
            + "  #endif\n"
            + "     C = 3\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
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
        Spin2Parser subject = new Spin2Parser(""
            + "CON\n"
            + "#ifdef P\n"
            + "#0,a,b,c,d\n"
            + "#endif\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
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
        Spin2Parser subject = new Spin2Parser(""
            + "PUB main()\n"
            + "  #ifdef A\n"
            + "    a := b * 2\n"
            + "  #endif\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=main [PUB main()]\n"
            + "    +-- DirectiveNode [#ifdef A]\n"
            + "    +-- StatementNode [a := b * 2]\n"
            + "    +-- DirectiveNode [#endif]\n"
            + "", tree(root));
    }

    @Test
    void testStatementBlockPreprocessorDirective() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "PUB main()\n"
            + "    if 1\n"
            + "  #ifdef A\n"
            + "        a := b * 2\n"
            + "  #endif\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=main [PUB main()]\n"
            + "    +-- StatementNode [if 1]\n"
            + "        +-- DirectiveNode [#ifdef A]\n"
            + "        +-- StatementNode [a := b * 2]\n"
            + "        +-- DirectiveNode [#endif]\n"
            + "", tree(root));
    }

    @Test
    void testPAsmPreprocessorDirective() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "DAT\n"
            + "        org $000\n"
            + "  #ifdef A\n"
            + "        mov a, #1\n"
            + "  #endif\n"
            + "        ret\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- DataNode [DAT]\n"
            + "    +-- DataLineNode instruction=org [org $000]\n"
            + "        +-- ParameterNode [$000]\n"
            + "    +-- DirectiveNode [#ifdef A]\n"
            + "    +-- DataLineNode instruction=mov [mov a, #1]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [#1]\n"
            + "    +-- DirectiveNode [#endif]\n"
            + "    +-- DataLineNode instruction=ret [ret]\n"
            + "", tree(root));
    }

    @Test
    void testStructure() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "CON\n"
            + "    sPoint(x, y)\n"
            + "    sLine(sPoint a, sPoint b, BYTE color)\n"
            + "    sPolygon(sPoint points[10], BYTE color)\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- ConstantsNode [CON]\n"
            + "    +-- StructNode identifier=sPoint [sPoint(x, y)]\n"
            + "        +-- Member identifier=x [x]\n"
            + "        +-- Member identifier=y [y]\n"
            + "    +-- StructNode identifier=sLine [sLine(sPoint a, sPoint b, BYTE color)]\n"
            + "        +-- Member type=sPoint identifier=a [sPoint a]\n"
            + "        +-- Member type=sPoint identifier=b [sPoint b]\n"
            + "        +-- Member type=BYTE identifier=color [BYTE color]\n"
            + "    +-- StructNode identifier=sPolygon [sPolygon(sPoint points[10], BYTE color)]\n"
            + "        +-- Member type=sPoint identifier=points [sPoint points[10]]\n"
            + "            +-- size = ExpressionNode [10]\n"
            + "        +-- Member type=BYTE identifier=color [BYTE color]\n"
            + "", tree(root));
    }

    @Test
    void testStructureMemberBitfield() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "CON\n"
            + "    sPoint(long .x[9..0] .y[19..10)\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- ConstantsNode [CON]\n"
            + "    +-- StructNode identifier=sPoint [sPoint(long .x[9..0] .y[19..10)]\n"
            + "        +-- Member type=long identifier=.x [long .x[9..0]]\n"
            + "            +-- size = ExpressionNode [9..0]\n"
            + "        +-- Member identifier=.y [.y[19..10)]\n"
            + "            +-- size = ExpressionNode [19..10)]\n"
            + "", tree(root));
    }

    @Test
    void testMultipleStructureDeclaration() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "CON\n"
            + "    struct sPoint(x, y), struct sLine(sPoint a, sPoint b, BYTE color)\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- ConstantsNode [CON]\n"
            + "    +-- StructNode type=struct identifier=sPoint [struct sPoint(x, y)]\n"
            + "        +-- Member identifier=x [x]\n"
            + "        +-- Member identifier=y [y]\n"
            + "    +-- StructNode type=struct identifier=sLine [struct sLine(sPoint a, sPoint b, BYTE color)]\n"
            + "        +-- Member type=sPoint identifier=a [sPoint a]\n"
            + "        +-- Member type=sPoint identifier=b [sPoint b]\n"
            + "        +-- Member type=BYTE identifier=color [BYTE color]\n"
            + "", tree(root));
    }

    @Test
    void testInlineAssembly() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "PUB main(a)\n"
            + "\n"
            + "        org\n"
            + "        mov     pr0, #0\n"
            + "l1      add     pr0, a\n"
            + "        djnz    a, #l1\n"
            + "        end\n"
            + "\n"
            + "        orgh\n"
            + "        mov     pr0, #0\n"
            + "l1      add     pr0, a\n"
            + "        djnz    a, #l1\n"
            + "        end\n"
            + "\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=main [PUB main(a)]\n"
            + "    +-- ParameterNode identifier=a [a]\n"
            + "    +-- DataLineNode instruction=org [org]\n"
            + "    +-- DataLineNode instruction=mov [mov     pr0, #0]\n"
            + "        +-- ParameterNode [pr0]\n"
            + "        +-- ParameterNode [#0]\n"
            + "    +-- DataLineNode label=l1 instruction=add [l1      add     pr0, a]\n"
            + "        +-- ParameterNode [pr0]\n"
            + "        +-- ParameterNode [a]\n"
            + "    +-- DataLineNode instruction=djnz [djnz    a, #l1]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [#l1]\n"
            + "    +-- StatementNode [end]\n"
            + "    +-- DataLineNode instruction=orgh [orgh]\n"
            + "    +-- DataLineNode instruction=mov [mov     pr0, #0]\n"
            + "        +-- ParameterNode [pr0]\n"
            + "        +-- ParameterNode [#0]\n"
            + "    +-- DataLineNode label=l1 instruction=add [l1      add     pr0, a]\n"
            + "        +-- ParameterNode [pr0]\n"
            + "        +-- ParameterNode [a]\n"
            + "    +-- DataLineNode instruction=djnz [djnz    a, #l1]\n"
            + "        +-- ParameterNode [a]\n"
            + "        +-- ParameterNode [#l1]\n"
            + "    +-- StatementNode [end]\n"
            + "", tree(root));
    }

    @Test
    void testPreprocessor() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "PUB start() | a\n"
            + "\n"
            + "#IF 0\n"
            + "    if CLKFREQ >= 40_000_000\n"
            + "        b := 1_000\n"
            + "#ENDIF\n"
            + "        a := 1_000\n"
            + "\n"
            + "    repeat\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=start [PUB start() | a]\n"
            + "    +-- LocalVariableNode identifier=a [a]\n"
            + "    +-- DirectiveNode [#IF 0]\n"
            + "    +-- StatementNode [if CLKFREQ >= 40_000_000]\n"
            + "        +-- StatementNode [b := 1_000]\n"
            + "        +-- DirectiveNode [#ENDIF]\n"
            + "        +-- StatementNode [a := 1_000]\n"
            + "    +-- StatementNode [repeat]\n"
            + "", tree(root));
    }

    @Test
    void testCasePreprocessor() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "PUB start() | a, b\n"
            + "\n"
            + "    case a\n"
            + "        1:\n"
            + "            b := a + 1\n"
            + "#IF 0\n"
            + "        2:"
            + "            b := a + 2\n"
            + "#ENDIF\n"
            + "        3:\n"
            + "            b := a + 3\n"
            + "\n"
            + "    repeat\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=start [PUB start() | a, b]\n"
            + "    +-- LocalVariableNode identifier=a [a]\n"
            + "    +-- LocalVariableNode identifier=b [b]\n"
            + "    +-- StatementNode [case a]\n"
            + "        +-- StatementNode [1:]\n"
            + "            +-- StatementNode [b := a + 1]\n"
            + "            +-- DirectiveNode [#IF 0]\n"
            + "        +-- StatementNode [2:]\n"
            + "            +-- StatementNode [b := a + 2]\n"
            + "            +-- DirectiveNode [#ENDIF]\n"
            + "        +-- StatementNode [3:]\n"
            + "            +-- StatementNode [b := a + 3]\n"
            + "    +-- StatementNode [repeat]\n"
            + "", tree(root));
    }

    @Test
    void testPreprocessorAlternateBlockStart() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "PUB start() | a, b\n"
            + "\n"
            + "    if a == 1\n"
            + "#IF 0\n"
            + "    if a == 2\n"
            + "#ENDIF\n"
            + "        b := a + 1\n"
            + "\n"
            + "    repeat\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=start [PUB start() | a, b]\n"
            + "    +-- LocalVariableNode identifier=a [a]\n"
            + "    +-- LocalVariableNode identifier=b [b]\n"
            + "    +-- StatementNode [if a == 1]\n"
            + "        +-- DirectiveNode [#IF 0]\n"
            + "    +-- StatementNode [if a == 2]\n"
            + "        +-- DirectiveNode [#ENDIF]\n"
            + "        +-- StatementNode [b := a + 1]\n"
            + "    +-- StatementNode [repeat]\n"
            + "", tree(root));
    }

    @Test
    void testSectionDescription() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "CON ' Constants\n"
            + "\n"
            + "VAR ' Variables\n"
            + "\n"
            + "OBJ ' Objects\n"
            + "\n"
            + "PUB start() ' Method\n"
            + "\n"
            + "DAT ' Data\n"
            + "");

        Node root = subject.parse();

        Assertions.assertEquals("Constants", root.getChild(0).getDescription());
        Assertions.assertEquals("Variables", root.getChild(1).getDescription());
        Assertions.assertEquals("Objects", root.getChild(2).getDescription());
        //Assertions.assertEquals("Method", root.getChild(3).getDescription());
        Assertions.assertEquals("Data", root.getChild(4).getDescription());
    }

    @Test
    void testMethodDocument() throws Exception {
        Spin2Parser subject = new Spin2Parser(""
            + "PUB start() ' Method\n"
            + "'' Document as comment lines\n"
            + "' End\n"
            + "\n"
            + "PRI method1()\n"
            + "{{ Document as block comment lines }}\n"
            + "\n"
            + "PRI method2()\n"
            + "' Ignore\n"
            + "\n"
            + "PRI method3()\n"
            + "{ Ignore }\n"
            + "\n"
            + "PRI method4()\n"
            + "'' Document as \n"
            + "'' comment lines\n"
            + "\n"
            + "PRI method5()\n"
            + "{{ Document as }}\n"
            + "{{ block comment lines }}\n"
            + "");

        Node root = subject.parse();

        MethodNode node = (MethodNode) root.getChild(0);
        Assertions.assertEquals(1, node.getDocument().size());
        Assertions.assertEquals("'' Document as comment lines", node.getDocument().get(0).getText());

        node = (MethodNode) root.getChild(1);
        Assertions.assertEquals(1, node.getDocument().size());
        Assertions.assertEquals("{{ Document as block comment lines }}", node.getDocument().get(0).getText());

        node = (MethodNode) root.getChild(2);
        Assertions.assertEquals(0, node.getDocument().size());

        node = (MethodNode) root.getChild(3);
        Assertions.assertEquals(0, node.getDocument().size());

        node = (MethodNode) root.getChild(4);
        Assertions.assertEquals(2, node.getDocument().size());

        node = (MethodNode) root.getChild(5);
        Assertions.assertEquals(2, node.getDocument().size());
    }

    String tree(Node root) throws Exception {
        return tree(root, 0);
    }

    String tree(Node root, int indent) throws Exception {
        StringBuilder sb = new StringBuilder();
        Field[] field = root.getClass().getFields();

        sb.append(root);
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
