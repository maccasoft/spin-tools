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

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings({
    "unchecked"
})
class Spin2CompilerTest {

    @Test
    void testEmptySource() throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Parser parser = parseText("");
        parser.prog().accept(compiler);
    }

    @Test
    void testUnknownSource() throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Parser parser = parseText("EnableFlow = 8\n");
        parser.prog().accept(compiler);
    }

    @Test
    void testDeclareConstant() throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Parser parser = parseText("CON\nEnableFlow = 8\n");
        parser.prog().accept(compiler);

        Assertions.assertEquals(8, compiler.scope.getInteger("EnableFlow"));
    }

    @Test
    void testReferenceToConstant() throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Parser parser = parseText("CON\nPWM_base = 8\nPWM_pins = PWM_base ADDPINS 7\n");
        parser.prog().accept(compiler);

        Assertions.assertEquals(8, compiler.scope.getInteger("PWM_base"));
        Assertions.assertEquals(8 + (7 << 6), compiler.scope.getInteger("PWM_pins"));
    }

    @Test
    void testForwardReferenceToConstant() throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Parser parser = parseText("CON\nPWM_pins = PWM_base ADDPINS 7\nPWM_base = 8\n");
        parser.prog().accept(compiler);

        Assertions.assertEquals(8, compiler.scope.getInteger("PWM_base"));
        Assertions.assertEquals(8 + (7 << 6), compiler.scope.getInteger("PWM_pins"));
    }

    @Test
    void testEnumerationDefault() throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Parser parser = parseText("CON  #0,a,b,c,d\n");
        parser.prog().accept(compiler);

        Assertions.assertEquals(0, compiler.scope.getInteger("a"));
        Assertions.assertEquals(1, compiler.scope.getInteger("b"));
        Assertions.assertEquals(2, compiler.scope.getInteger("c"));
        Assertions.assertEquals(3, compiler.scope.getInteger("d"));
    }

    @Test
    void testEnumerationStart() throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Parser parser = parseText(""
            + "CON  #0,a,b,c,d\n"
            + "     #1,e,f,g,h\n");
        parser.prog().accept(compiler);

        Assertions.assertEquals(1, compiler.scope.getInteger("e"));
        Assertions.assertEquals(2, compiler.scope.getInteger("f"));
        Assertions.assertEquals(3, compiler.scope.getInteger("g"));
        Assertions.assertEquals(4, compiler.scope.getInteger("h"));
    }

    @Test
    void testEnumerationStartAndStep() throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Parser parser = parseText(""
            + "CON  \n"
            + "     #4[2],i,j,k,l\n");
        parser.prog().accept(compiler);

        Assertions.assertEquals(4, compiler.scope.getInteger("i"));
        Assertions.assertEquals(6, compiler.scope.getInteger("j"));
        Assertions.assertEquals(8, compiler.scope.getInteger("k"));
        Assertions.assertEquals(10, compiler.scope.getInteger("l"));
    }

    @Test
    void testEnumerationStepMultiplier() throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Parser parser = parseText(""
            + "CON  #16\n"
            + "     q\n"
            + "     r[0]\n"
            + "     s\n"
            + "     t\n"
            + "     u[2]\n"
            + "     v\n"
            + "     w\n");
        parser.prog().accept(compiler);

        Assertions.assertEquals(16, compiler.scope.getInteger("q"));
        Assertions.assertEquals(17, compiler.scope.getInteger("r"));
        Assertions.assertEquals(17, compiler.scope.getInteger("s"));
        Assertions.assertEquals(18, compiler.scope.getInteger("t"));
        Assertions.assertEquals(19, compiler.scope.getInteger("u"));
        Assertions.assertEquals(21, compiler.scope.getInteger("v"));
        Assertions.assertEquals(22, compiler.scope.getInteger("w"));
    }

    @Test
    void testEnumerationSectionReset() throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Parser parser = parseText(""
            + "CON  #1,e,f,g,h\n"
            + "\n"
            + "CON  e0,e1,e2\n");
        parser.prog().accept(compiler);

        Assertions.assertEquals(1, compiler.scope.getInteger("e"));
        Assertions.assertEquals(2, compiler.scope.getInteger("f"));
        Assertions.assertEquals(3, compiler.scope.getInteger("g"));
        Assertions.assertEquals(4, compiler.scope.getInteger("h"));

        Assertions.assertEquals(0, compiler.scope.getInteger("e0"));
        Assertions.assertEquals(1, compiler.scope.getInteger("e1"));
        Assertions.assertEquals(2, compiler.scope.getInteger("e2"));
    }

    @Test
    void testBuildPAsmLine() throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Parser parser = parseText(""
            + "DAT             ORG\n"
            + "\n"
            + "IncPins         MOV     DIRA,#$FF\n"
            + "Loop            ADD     OUTA,#1\n"
            + "                JMP     #Loop\n");
        parser.prog().accept(compiler);

        Assertions.assertEquals(4, compiler.source.size());
    }

    Spin2Parser parseText(String text) throws Exception {
        Spin2Lexer lexer = new Spin2Lexer(CharStreams.fromString(text));
        lexer.removeErrorListeners();
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Spin2Parser parser = new Spin2Parser(tokens);
        parser.removeErrorListeners();
        return parser;
    }
}
