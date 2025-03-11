/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.Token;

class CParserTest {

    @Test
    void testGlobalVariables() throws Exception {
        CParser subject = new CParser(""
            + "int a, b, c;\n"
            + "short d;\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- VariableNode type=int identifier=a [int a, b, c]\n"
            + "    +-- VariableNode identifier=b [b]\n"
            + "    +-- VariableNode identifier=c [c]\n"
            + "+-- VariableNode type=short identifier=d [short d]\n"
            + "", tree(root));
    }

    @Test
    void testGlobalVariablesAssignment() throws Exception {
        CParser subject = new CParser(""
            + "int a = 1, b = 2, c = 3;\n"
            + "short d = 4;\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- VariableNode type=int identifier=a [int a = 1, b = 2, c = 3]\n"
            + "    +-- VariableNode identifier=b [b = 2]\n"
            + "    +-- VariableNode identifier=c [c = 3]\n"
            + "+-- VariableNode type=short identifier=d [short d = 4]\n"
            + "", tree(root));
    }

    @Test
    void testMethods() throws Exception {
        CParser subject = new CParser(""
            + "void main() {\n"
            + "    method(1, 2);\n"
            + "    a = function(1, 2);\n"
            + "}\n"
            + "\n"
            + "void method(int a, int b) {\n"
            + "    d = a * b;\n"
            + "}\n"
            + "\n"
            + "int function(int a, int b) {\n"
            + "    d = a * b;\n"
            + "    return d;\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- FunctionNode type=void identifier=main [void main() {]\n"
            + "    +-- StatementNode [    method(1, 2);]\n"
            + "    +-- StatementNode [    a = function(1, 2);]\n"
            + "    +-- StatementNode [}]\n"
            + "+-- FunctionNode type=void identifier=method [void method(int a, int b) {]\n"
            + "    +-- ParameterNode type=int identifier=a [int a]\n"
            + "    +-- ParameterNode type=int identifier=b [int b]\n"
            + "    +-- StatementNode [    d = a * b;]\n"
            + "    +-- StatementNode [}]\n"
            + "+-- FunctionNode type=int identifier=function [int function(int a, int b) {]\n"
            + "    +-- ParameterNode type=int identifier=a [int a]\n"
            + "    +-- ParameterNode type=int identifier=b [int b]\n"
            + "    +-- StatementNode [    d = a * b;]\n"
            + "    +-- StatementNode [    return d;]\n"
            + "    +-- StatementNode [}]\n"
            + "", tree(root));
    }

    @Test
    void testIfBlock() throws Exception {
        CParser subject = new CParser(""
            + "void main() {\n"
            + "    if (a == 1) {\n"
            + "        method(1, 2);\n"
            + "    }\n"
            + "    a = function(1, 2);\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- FunctionNode type=void identifier=main [void main() {]\n"
            + "    +-- StatementNode [    if (a == 1) {]\n"
            + "        +-- StatementNode [        method(1, 2);]\n"
            + "    +-- StatementNode [    }]\n"
            + "    +-- StatementNode [    a = function(1, 2);]\n"
            + "    +-- StatementNode [}]\n"
            + "", tree(root));
    }

    @Test
    void testIfElseBlock() throws Exception {
        CParser subject = new CParser(""
            + "void main() {\n"
            + "    if (a == 1) {\n"
            + "        method(1, 2);\n"
            + "    } else {\n"
            + "        a = function(1, 2);\n"
            + "    }\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- FunctionNode type=void identifier=main [void main() {]\n"
            + "    +-- StatementNode [    if (a == 1) {]\n"
            + "        +-- StatementNode [        method(1, 2);]\n"
            + "    +-- StatementNode [    }]\n"
            + "    +-- StatementNode [    else {]\n"
            + "        +-- StatementNode [        a = function(1, 2);]\n"
            + "    +-- StatementNode [    }]\n"
            + "    +-- StatementNode [}]\n"
            + "", tree(root));
    }

    @Test
    void testIfElseIfElseBlock() throws Exception {
        CParser subject = new CParser(""
            + "void main()\n"
            + "{\n"
            + "    int a;\n"
            + "\n"
            + "    if (a == 0) {\n"
            + "        a = 1;\n"
            + "    }\n"
            + "    else if (a == 1) {\n"
            + "        a = 2;\n"
            + "    }\n"
            + "    else if (a == 2) {\n"
            + "        a = 3;\n"
            + "    }\n"
            + "    else {\n"
            + "        a = 4;\n"
            + "    }\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- FunctionNode type=void identifier=main [void main() {]\n"
            + "    +-- StatementNode [    int a;]\n"
            + "    +-- StatementNode [    if (a == 0) {]\n"
            + "        +-- StatementNode [        a = 1;]\n"
            + "    +-- StatementNode [    }]\n"
            + "    +-- StatementNode [    else if (a == 1) {]\n"
            + "        +-- StatementNode [        a = 2;]\n"
            + "    +-- StatementNode [    }]\n"
            + "    +-- StatementNode [    else if (a == 2) {]\n"
            + "        +-- StatementNode [        a = 3;]\n"
            + "    +-- StatementNode [    }]\n"
            + "    +-- StatementNode [    else {]\n"
            + "        +-- StatementNode [        a = 4;]\n"
            + "    +-- StatementNode [    }]\n"
            + "    +-- StatementNode [}]\n"
            + "", tree(root));
    }

    @Test
    void testSimpleIfBlock() throws Exception {
        CParser subject = new CParser(""
            + "void main() {\n"
            + "    if (a == 1)\n"
            + "        method(1, 2);\n"
            + "    a = function(1, 2);\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- FunctionNode type=void identifier=main [void main() {]\n"
            + "    +-- StatementNode [    if (a == 1)]\n"
            + "        +-- StatementNode [        method(1, 2);]\n"
            + "    +-- StatementNode [    a = function(1, 2);]\n"
            + "    +-- StatementNode [}]\n"
            + "", tree(root));
    }

    @Test
    void testSimpleIfElseBlock() throws Exception {
        CParser subject = new CParser(""
            + "void main() {\n"
            + "    if (a == 1)\n"
            + "        method(1, 2);\n"
            + "    else\n"
            + "        a = function(1, 2);\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- FunctionNode type=void identifier=main [void main() {]\n"
            + "    +-- StatementNode [    if (a == 1)]\n"
            + "        +-- StatementNode [        method(1, 2);]\n"
            + "    +-- StatementNode [    else]\n"
            + "        +-- StatementNode [        a = function(1, 2);]\n"
            + "    +-- StatementNode [}]\n"
            + "", tree(root));
    }

    @Test
    void testSimpleIfElseIfElseBlock() throws Exception {
        CParser subject = new CParser(""
            + "void main()\n"
            + "{\n"
            + "    int a;\n"
            + "\n"
            + "    if (a == 0)\n"
            + "        a = 1;\n"
            + "    else if (a == 1)\n"
            + "        a = 2;\n"
            + "    else if (a == 2)\n"
            + "        a = 3;\n"
            + "    else\n"
            + "        a = 4;\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- FunctionNode type=void identifier=main [void main() {]\n"
            + "    +-- StatementNode [    int a;]\n"
            + "    +-- StatementNode [    if (a == 0)]\n"
            + "        +-- StatementNode [        a = 1;]\n"
            + "    +-- StatementNode [    else if (a == 1)]\n"
            + "        +-- StatementNode [        a = 2;]\n"
            + "    +-- StatementNode [    else if (a == 2)]\n"
            + "        +-- StatementNode [        a = 3;]\n"
            + "    +-- StatementNode [    else]\n"
            + "        +-- StatementNode [        a = 4;]\n"
            + "    +-- StatementNode [}]\n"
            + "", tree(root));
    }

    @Test
    void testWhileBlock() throws Exception {
        CParser subject = new CParser(""
            + "void main() {\n"
            + "    while (a == 1) {\n"
            + "        method(1, 2);\n"
            + "        d++;\n"
            + "    }\n"
            + "    a = function(1, 2);\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- FunctionNode type=void identifier=main [void main() {]\n"
            + "    +-- StatementNode [    while (a == 1) {]\n"
            + "        +-- StatementNode [        method(1, 2);]\n"
            + "        +-- StatementNode [        d++;]\n"
            + "    +-- StatementNode [    }]\n"
            + "    +-- StatementNode [    a = function(1, 2);]\n"
            + "    +-- StatementNode [}]\n"
            + "", tree(root));
    }

    @Test
    void testDoWhileBlock() throws Exception {
        CParser subject = new CParser(""
            + "void main() {\n"
            + "    do {\n"
            + "        method(1, 2);\n"
            + "        d++;\n"
            + "    } while (a == 1);\n"
            + "    a = function(1, 2);\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- FunctionNode type=void identifier=main [void main() {]\n"
            + "    +-- StatementNode [    do {]\n"
            + "        +-- StatementNode [        method(1, 2);]\n"
            + "        +-- StatementNode [        d++;]\n"
            + "    +-- StatementNode [    } while (a == 1);]\n"
            + "    +-- StatementNode [    a = function(1, 2);]\n"
            + "    +-- StatementNode [}]\n"
            + "", tree(root));
    }

    @Test
    void testFor() throws Exception {
        CParser subject = new CParser(""
            + "void main() {\n"
            + "    for(a = 1; a < 100; a++) {\n"
            + "        method(a, 2);\n"
            + "    }\n"
            + "    for(;;) {\n"
            + "        method(a++, 2);\n"
            + "    }\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- FunctionNode type=void identifier=main [void main() {]\n"
            + "    +-- StatementNode [    for(a = 1; a < 100; a++) {]\n"
            + "        +-- StatementNode [        method(a, 2);]\n"
            + "    +-- StatementNode [    }]\n"
            + "    +-- StatementNode [    for(;;) {]\n"
            + "        +-- StatementNode [        method(a++, 2);]\n"
            + "    +-- StatementNode [    }]\n"
            + "    +-- StatementNode [}]\n"
            + "", tree(root));
    }

    @Test
    void testDefine() throws Exception {
        CParser subject = new CParser(""
            + "#define CLKFREQ 160_000_000\n"
            + "#define PIN_RX  63\n"
            + "#define PIN_TX  62\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- DefineNode identifier=CLKFREQ [#define CLKFREQ 160_000_000]\n"
            + "+-- DefineNode identifier=PIN_RX [#define PIN_RX  63]\n"
            + "+-- DefineNode identifier=PIN_TX [#define PIN_TX  62]\n"
            + "", tree(root));
    }

    @Test
    void testInclude() throws Exception {
        CParser subject = new CParser(""
            + "#include \"object\"\n"
            + "#include <object>\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- IncludeNode [#include \"object\"]\n"
            + "+-- IncludeNode [#include <object>]\n"
            + "", tree(root));
    }

    @Test
    void testSameLineBlocks() throws Exception {
        CParser subject = new CParser(""
            + "void main() {\n"
            + "    if (a == 1) method(1, 2);\n"
            + "    while (a < 10) { a++; }\n"
            + "    a = function(1, 2);\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- FunctionNode type=void identifier=main [void main() {]\n"
            + "    +-- StatementNode [    if (a == 1)]\n"
            + "        +-- StatementNode [    if (a == 1) method(1, 2);]\n"
            + "    +-- StatementNode [    while (a < 10) {]\n"
            + "        +-- StatementNode [    while (a < 10) { a++;]\n"
            + "    +-- StatementNode [    while (a < 10) { a++; }]\n"
            + "    +-- StatementNode [    a = function(1, 2);]\n"
            + "    +-- StatementNode [}]\n"
            + "", tree(root));
    }

    @Test
    void testGlobalVariablesAndFunctions() throws Exception {
        CParser subject = new CParser(""
            + "int a = 1, b = 2, c = 3;\n"
            + "short d;\n"
            + "\n"
            + "void main() {\n"
            + "    method(1, 2);\n"
            + "    a = function(1, 2);\n"
            + "}\n"
            + "\n"
            + "void method(int a, int b) {\n"
            + "    d = a * b;\n"
            + "}\n"
            + "\n"
            + "int function(int a, int b) {\n"
            + "    d = a * b;\n"
            + "    return d;\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- VariableNode type=int identifier=a [int a = 1, b = 2, c = 3]\n"
            + "    +-- VariableNode identifier=b [b = 2]\n"
            + "    +-- VariableNode identifier=c [c = 3]\n"
            + "+-- VariableNode type=short identifier=d [short d]\n"
            + "+-- FunctionNode type=void identifier=main [void main() {]\n"
            + "    +-- StatementNode [    method(1, 2);]\n"
            + "    +-- StatementNode [    a = function(1, 2);]\n"
            + "    +-- StatementNode [}]\n"
            + "+-- FunctionNode type=void identifier=method [void method(int a, int b) {]\n"
            + "    +-- ParameterNode type=int identifier=a [int a]\n"
            + "    +-- ParameterNode type=int identifier=b [int b]\n"
            + "    +-- StatementNode [    d = a * b;]\n"
            + "    +-- StatementNode [}]\n"
            + "+-- FunctionNode type=int identifier=function [int function(int a, int b) {]\n"
            + "    +-- ParameterNode type=int identifier=a [int a]\n"
            + "    +-- ParameterNode type=int identifier=b [int b]\n"
            + "    +-- StatementNode [    d = a * b;]\n"
            + "    +-- StatementNode [    return d;]\n"
            + "    +-- StatementNode [}]\n"
            + "", tree(root));
    }

    @Test
    void testParseInlinePAsmBlock() throws Exception {
        CParser subject = new CParser(""
            + "void main()\n"
            + "{\n"
            + "    asm {\n"
            + "        nop\n"
            + "        ret\n"
            + "    }\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- FunctionNode type=void identifier=main [void main() {]\n"
            + "    +-- StatementNode [    asm {]\n"
            + "        +-- DataLineNode instruction=nop [        nop]\n"
            + "        +-- DataLineNode instruction=ret [        ret]\n"
            + "    +-- StatementNode [    }]\n"
            + "    +-- StatementNode [}]\n"
            + "", tree(root));
    }

    @Test
    void testUnknownTypeVariable() throws Exception {
        CParser subject = new CParser(""
            + "object a, b, c;\n"
            + "object d;\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- VariableNode type=object identifier=a [object a, b, c]\n"
            + "    +-- VariableNode identifier=b [b]\n"
            + "    +-- VariableNode identifier=c [c]\n"
            + "+-- VariableNode type=object identifier=d [object d]\n"
            + "", tree(root));
    }

    @Test
    void testObjectMethod() throws Exception {
        CParser subject = new CParser(""
            + "obj.method\n"
            + "");

        Assertions.assertEquals("obj.method", subject.nextToken().getText());
    }

    @Test
    void testObjectArrayMethod() throws Exception {
        CParser subject = new CParser(""
            + "obj[0].method\n"
            + "");

        Assertions.assertEquals("obj", subject.nextToken().getText());
        Assertions.assertEquals("[", subject.nextToken().getText());
        Assertions.assertEquals("0", subject.nextToken().getText());
        Assertions.assertEquals("]", subject.nextToken().getText());
        Assertions.assertEquals(".method", subject.nextToken().getText());
    }

    @Test
    void testPointerVariables() throws Exception {
        CParser subject = new CParser(""
            + "int *a, *b;\n"
            + "short *c;\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "    int *d;\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- VariableNode type=int * identifier=a [int *a, *b]\n"
            + "    +-- VariableNode type=* identifier=b [*b]\n"
            + "+-- VariableNode type=short * identifier=c [short *c]\n"
            + "+-- FunctionNode type=void identifier=main [void main() {]\n"
            + "    +-- StatementNode [    int *d;]\n"
            + "    +-- StatementNode [}]\n"
            + "", tree(root));
    }

    @Test
    void testSwitch() throws Exception {
        CParser subject = new CParser(""
            + "void main()\n"
            + "{\n"
            + "    switch(a) {\n"
            + "        case 1:\n"
            + "            a = 4;\n"
            + "            break;\n"
            + "        case 2:\n"
            + "            a = 5;\n"
            + "            break;\n"
            + "        case 3:\n"
            + "            a = 6;\n"
            + "            break;\n"
            + "    }\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- FunctionNode type=void identifier=main [void main() {]\n"
            + "    +-- StatementNode [    switch(a) {]\n"
            + "        +-- StatementNode [        case 1:]\n"
            + "            +-- StatementNode [            a = 4;]\n"
            + "            +-- StatementNode [            break;]\n"
            + "        +-- StatementNode [        case 2:]\n"
            + "            +-- StatementNode [            a = 5;]\n"
            + "            +-- StatementNode [            break;]\n"
            + "        +-- StatementNode [        case 3:]\n"
            + "            +-- StatementNode [            a = 6;]\n"
            + "            +-- StatementNode [            break;]\n"
            + "    +-- StatementNode [    }]\n"
            + "    +-- StatementNode [}]\n"
            + "", tree(root));
    }

    @Test
    void testSwitchDefault() throws Exception {
        CParser subject = new CParser(""
            + "void main()\n"
            + "{\n"
            + "    switch(a) {\n"
            + "        case 1:\n"
            + "            a = 4;\n"
            + "            break;\n"
            + "        case 2:\n"
            + "            a = 5;\n"
            + "            break;\n"
            + "        default:\n"
            + "            a = 6;\n"
            + "            break;\n"
            + "    }\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- FunctionNode type=void identifier=main [void main() {]\n"
            + "    +-- StatementNode [    switch(a) {]\n"
            + "        +-- StatementNode [        case 1:]\n"
            + "            +-- StatementNode [            a = 4;]\n"
            + "            +-- StatementNode [            break;]\n"
            + "        +-- StatementNode [        case 2:]\n"
            + "            +-- StatementNode [            a = 5;]\n"
            + "            +-- StatementNode [            break;]\n"
            + "        +-- StatementNode [        default:]\n"
            + "            +-- StatementNode [            a = 6;]\n"
            + "            +-- StatementNode [            break;]\n"
            + "    +-- StatementNode [    }]\n"
            + "    +-- StatementNode [}]\n"
            + "", tree(root));
    }

    @Test
    void testSwitchCaseWithoutBreak() throws Exception {
        CParser subject = new CParser(""
            + "void main()\n"
            + "{\n"
            + "    switch(a) {\n"
            + "        case 1:\n"
            + "            a = 4;\n"
            + "        case 2:\n"
            + "            a = 5;\n"
            + "        case 3:\n"
            + "            a = 6;\n"
            + "            break;\n"
            + "    }\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- FunctionNode type=void identifier=main [void main() {]\n"
            + "    +-- StatementNode [    switch(a) {]\n"
            + "        +-- StatementNode [        case 1:]\n"
            + "            +-- StatementNode [            a = 4;]\n"
            + "        +-- StatementNode [        case 2:]\n"
            + "            +-- StatementNode [            a = 5;]\n"
            + "        +-- StatementNode [        case 3:]\n"
            + "            +-- StatementNode [            a = 6;]\n"
            + "            +-- StatementNode [            break;]\n"
            + "    +-- StatementNode [    }]\n"
            + "    +-- StatementNode [}]\n"
            + "", tree(root));
    }

    @Test
    void testPreprocessor() throws Exception {
        CParser subject = new CParser(""
            + "void main(int a)\n"
            + "{\n"
            + "#ifdef TEST\n"
            + "    a = 1;\n"
            + "#endif\n"
            + "    a++;\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- FunctionNode type=void identifier=main [void main(int a) {]\n"
            + "    +-- ParameterNode type=int identifier=a [int a]\n"
            + "    +-- DirectiveNode [#ifdef TEST]\n"
            + "    +-- StatementNode [    a = 1;]\n"
            + "    +-- DirectiveNode [#endif]\n"
            + "    +-- StatementNode [    a++;]\n"
            + "    +-- StatementNode [}]\n"
            + "", tree(root));
    }

    @Test
    void testStructureDefinition() throws Exception {
        CParser subject = new CParser(""
            + "struct data {\n"
            + "    byte a;\n"
            + "    byte b, c;\n"
            + "    byte d[10];\n"
            + "};\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- TypeDefinitionNode type=struct identifier=data [struct data { byte a; byte b, c; byte d[10]; };]\n"
            + "    +-- Definition type=byte identifier=a [byte a]\n"
            + "    +-- Definition type=byte identifier=b [byte b]\n"
            + "        +-- Definition identifier=c [c]\n"
            + "    +-- Definition type=byte identifier=d [byte d[10]]\n"
            + "", tree(root));
    }

    @Test
    void testStructureDefinitionAndVariables() throws Exception {
        CParser subject = new CParser(""
            + "struct data {\n"
            + "    byte a;\n"
            + "    byte b, c;\n"
            + "    byte d[10];\n"
            + "} a, *b, c;\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- TypeDefinitionNode type=struct identifier=data [struct data { byte a; byte b, c; byte d[10]; } a, *b, c;]\n"
            + "    +-- Definition type=byte identifier=a [byte a]\n"
            + "    +-- Definition type=byte identifier=b [byte b]\n"
            + "        +-- Definition identifier=c [c]\n"
            + "    +-- Definition type=byte identifier=d [byte d[10]]\n"
            + "    +-- VariableNode identifier=a [a]\n"
            + "    +-- VariableNode type=* identifier=b [*b]\n"
            + "    +-- VariableNode identifier=c [c]\n"
            + "", tree(root));
    }

    @Test
    void testStructureVariables() throws Exception {
        CParser subject = new CParser(""
            + "struct data a;\n"
            + "struct data b, c;\n"
            + "struct data *d, e, *f;\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- VariableNode modifier=struct type=data identifier=a [struct data a]\n"
            + "+-- VariableNode modifier=struct type=data identifier=b [struct data b, c]\n"
            + "    +-- VariableNode identifier=c [c]\n"
            + "+-- VariableNode modifier=struct type=data identifier=* [struct data *d, e, *f]\n"
            + "    +-- VariableNode identifier=e [e]\n"
            + "    +-- VariableNode type=* identifier=f [*f]\n"
            + "", tree(root));
    }

    @Test
    void testSimpleIfBlock2() throws Exception {
        CParser subject = new CParser(""
            + "void main() {\n"
            + "    if (a == 1)\n"
            + "    while (a == 1) {\n"
            + "        method(1, 2);\n"
            + "        d++;\n"
            + "    }\n"
            + "    a = function(1, 2);\n"
            + "}\n"
            + "");

        Node root = subject.parse();
        Assertions.assertEquals(""
            + "RootNode []\n"
            + "+-- FunctionNode type=void identifier=main [void main() {]\n"
            + "    +-- StatementNode [    if (a == 1)]\n"
            + "        +-- StatementNode [    while (a == 1) {]\n"
            + "            +-- StatementNode [        method(1, 2);]\n"
            + "            +-- StatementNode [        d++;]\n"
            + "        +-- StatementNode [    }]\n"
            + "    +-- StatementNode [    a = function(1, 2);]\n"
            + "    +-- StatementNode [}]\n"
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

        String text = root.getText().replaceAll("[\n\r]+[ ]*", " ");
        if (text.indexOf('}') < text.indexOf('{')) {
            text = text.replaceAll("}[ ]+", "");
        }

        sb.append(" [");
        sb.append(text);
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
