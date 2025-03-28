/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
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

import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.Token;

class Spin1ParserTest {

    @Test
    void testLocalLabel() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + ":label\n"
            + "");

        Assertions.assertEquals(":label", subject.nextPAsmToken().getText());
    }

    @Test
    void testAddress() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + "@label\n"
            + "");

        Assertions.assertEquals("@label", subject.nextPAsmToken().getText());
    }

    @Test
    void testAbsoluteAddress() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + "@@label\n"
            + "");

        Assertions.assertEquals("@@label", subject.nextPAsmToken().getText());
    }

    @Test
    void testLocalLabelAddress() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + "@:label\n"
            + "");

        Assertions.assertEquals("@:label", subject.nextPAsmToken().getText());
    }

    @Test
    void testAbsoluteLocalLabelAddress() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + "@@:label\n"
            + "");

        Assertions.assertEquals("@@:label", subject.nextPAsmToken().getText());
    }

    @Test
    void testImmediateLabel() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + "#label\n"
            + "");

        Assertions.assertEquals("#", subject.nextPAsmToken().getText());
        Assertions.assertEquals("label", subject.nextPAsmToken().getText());
    }

    @Test
    void testImmediateLocalLabel() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + "#:label\n"
            + "");

        Assertions.assertEquals("#", subject.nextPAsmToken().getText());
        Assertions.assertEquals(":label", subject.nextPAsmToken().getText());
    }

    @Test
    void testRangeValue() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + "a..b\n"
            + "");

        Assertions.assertEquals("a", subject.nextToken().getText());
        Assertions.assertEquals("..", subject.nextToken().getText());
        Assertions.assertEquals("b", subject.nextToken().getText());
    }

    @Test
    void testSpinLocalLabel() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + ":label\n"
            + "");

        Assertions.assertEquals(":", subject.nextToken().getText());
        Assertions.assertEquals("label", subject.nextToken().getText());
    }

    @Test
    void testObjectMethod() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + "obj.method\n"
            + "");

        Assertions.assertEquals("obj.method", subject.nextToken().getText());
    }

    @Test
    void testObjectArrayMethod() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + "obj[0].method\n"
            + "");

        Assertions.assertEquals("obj", subject.nextToken().getText());
        Assertions.assertEquals("[", subject.nextToken().getText());
        Assertions.assertEquals("0", subject.nextToken().getText());
        Assertions.assertEquals("]", subject.nextToken().getText());
        Assertions.assertEquals(".method", subject.nextToken().getText());
    }

    @Test
    void testSingleAssigments() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
    void testParseObjects() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
            + "PUB go\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=go [PUB go]\n"
            + "", tree(root));
    }

    @Test
    void testMethodInvalidDeclarations() throws Exception {
        Node root = new Spin1Parser(""
            + "PUB go to\n"
            + "").parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=go [PUB go to]\n"
            + "", tree(root));

        root = new Spin1Parser(""
            + "PUB go to\n"
            + "").parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=go [PUB go to]\n"
            + "", tree(root));

        root = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
    void testMethodInvalidParameters() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + "PUB StartTx(pin baud)\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=StartTx [PUB StartTx(pin baud)]\n"
            + "    +-- ParameterNode identifier=pin [pin baud]\n"
            + "", tree(root));
    }

    @Test
    void testMethodLocalVariables() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + "PRI FFT1024 | a, b\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PRI name=FFT1024 [PRI FFT1024 | a, b]\n"
            + "    +-- LocalVariableNode identifier=a [a]\n"
            + "    +-- LocalVariableNode identifier=b [b]\n"
            + "", tree(root));
    }

    @Test
    void testMethodInvalidLocalVariables() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + "PRI FFT1024 | a b\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PRI name=FFT1024 [PRI FFT1024 | a b]\n"
            + "    +-- LocalVariableNode identifier=a [a b]\n"
            + "", tree(root));
    }

    @Test
    void testMethodLocalVariableType() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + "PRI FFT1024 | LONG a, WORD b\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PRI name=FFT1024 [PRI FFT1024 | LONG a, WORD b]\n"
            + "    +-- LocalVariableNode type=LONG identifier=a [LONG a]\n"
            + "    +-- LocalVariableNode type=WORD identifier=b [WORD b]\n"
            + "", tree(root));
    }

    @Test
    void testMethodLocalVariableSize() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
            + "PRI FFT1024 : a, b\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PRI name=FFT1024 [PRI FFT1024 : a, b]\n"
            + "    +-- ReturnNode identifier=a [a]\n"
            + "    +-- ReturnNode identifier=b [b]\n"
            + "", tree(root));
    }

    @Test
    void testMethodInvalidReturnVariables() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + "PRI FFT1024 : a b\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PRI name=FFT1024 [PRI FFT1024 : a b]\n"
            + "    +-- ReturnNode identifier=a [a b]\n"
            + "", tree(root));
    }

    @Test
    void testMethodUnexpectedTokens2() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + "PUB go(a,b) : c to\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=go [PUB go(a,b) : c to]\n"
            + "    +-- ParameterNode identifier=a [a]\n"
            + "    +-- ParameterNode identifier=b [b]\n"
            + "    +-- ReturnNode identifier=c [c to]\n"
            + "", tree(root));
    }

    @Test
    void testMethodUnexpectedTokens3() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + "PUB go(a,b) | d, e to\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=go [PUB go(a,b) | d, e to]\n"
            + "    +-- ParameterNode identifier=a [a]\n"
            + "    +-- ParameterNode identifier=b [b]\n"
            + "    +-- LocalVariableNode identifier=d [d]\n"
            + "    +-- LocalVariableNode identifier=e [e to]\n"
            + "", tree(root));
    }

    @Test
    void testMethodUnexpectedTokens4() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
            + "PUB go(a,b) : c | d, e to\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- MethodNode type=PUB name=go [PUB go(a,b) : c | d, e to]\n"
            + "    +-- ParameterNode identifier=a [a]\n"
            + "    +-- ParameterNode identifier=b [b]\n"
            + "    +-- ReturnNode identifier=c [c]\n"
            + "    +-- LocalVariableNode identifier=d [d]\n"
            + "    +-- LocalVariableNode identifier=e [e to]\n"
            + "", tree(root));
    }

    @Test
    void testVariables() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
    void testDefaultSectionPreprocessorDirective() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
        Spin1Parser subject = new Spin1Parser(""
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
            + "    +-- StatementNode [    a := b * 2]\n"
            + "    +-- DirectiveNode [#endif]\n"
            + "", tree(root));
    }

    @Test
    void testStatementBlockPreprocessorDirective() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
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
            + "    +-- StatementNode [    if 1]\n"
            + "        +-- DirectiveNode [#ifdef A]\n"
            + "        +-- StatementNode [        a := b * 2]\n"
            + "        +-- DirectiveNode [#endif]\n"
            + "", tree(root));
    }

    @Test
    void testPAsmPreprocessorDirective() throws Exception {
        Spin1Parser subject = new Spin1Parser(""
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
