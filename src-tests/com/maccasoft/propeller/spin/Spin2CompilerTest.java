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

        Assertions.assertEquals(new Integer(8), compiler.scope.getSymbol("EnableFlow").getNumber());
    }

    @Test
    void testReferenceToConstant() throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Parser parser = parseText("CON\nPWM_base = 8\nPWM_pins = PWM_base ADDPINS 7\n");
        parser.prog().accept(compiler);

        Assertions.assertEquals(new Integer(8), compiler.scope.getSymbol("PWM_base").getNumber());
        Assertions.assertEquals(new Integer(8 + (7 << 6)), compiler.scope.getSymbol("PWM_pins").getNumber());
    }

    @Test
    void testForwardReferenceToConstant() throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Parser parser = parseText("CON\nPWM_pins = PWM_base ADDPINS 7\nPWM_base = 8\n");
        parser.prog().accept(compiler);

        Assertions.assertEquals(new Integer(8), compiler.scope.getSymbol("PWM_base").getNumber());
        Assertions.assertEquals(new Integer(8 + (7 << 6)), compiler.scope.getSymbol("PWM_pins").getNumber());
    }

    @Test
    void testEnumerationDefault() throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Parser parser = parseText("CON  #0,a,b,c,d\n");
        parser.prog().accept(compiler);

        Assertions.assertEquals(new Integer(0), compiler.scope.getSymbol("a").getNumber());
        Assertions.assertEquals(new Integer(1), compiler.scope.getSymbol("b").getNumber());
        Assertions.assertEquals(new Integer(2), compiler.scope.getSymbol("c").getNumber());
        Assertions.assertEquals(new Integer(3), compiler.scope.getSymbol("d").getNumber());
    }

    @Test
    void testEnumerationStart() throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Parser parser = parseText(""
            + "CON  #0,a,b,c,d\n"
            + "     #1,e,f,g,h\n");
        parser.prog().accept(compiler);

        Assertions.assertEquals(new Integer(1), compiler.scope.getSymbol("e").getNumber());
        Assertions.assertEquals(new Integer(2), compiler.scope.getSymbol("f").getNumber());
        Assertions.assertEquals(new Integer(3), compiler.scope.getSymbol("g").getNumber());
        Assertions.assertEquals(new Integer(4), compiler.scope.getSymbol("h").getNumber());
    }

    @Test
    void testEnumerationStartAndStep() throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Parser parser = parseText(""
            + "CON  \n"
            + "     #4[2],i,j,k,l\n");
        parser.prog().accept(compiler);

        Assertions.assertEquals(new Integer(4), compiler.scope.getSymbol("i").getNumber());
        Assertions.assertEquals(new Integer(6), compiler.scope.getSymbol("j").getNumber());
        Assertions.assertEquals(new Integer(8), compiler.scope.getSymbol("k").getNumber());
        Assertions.assertEquals(new Integer(10), compiler.scope.getSymbol("l").getNumber());
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

        Assertions.assertEquals(new Integer(16), compiler.scope.getSymbol("q").getNumber());
        Assertions.assertEquals(new Integer(17), compiler.scope.getSymbol("r").getNumber());
        Assertions.assertEquals(new Integer(17), compiler.scope.getSymbol("s").getNumber());
        Assertions.assertEquals(new Integer(18), compiler.scope.getSymbol("t").getNumber());
        Assertions.assertEquals(new Integer(19), compiler.scope.getSymbol("u").getNumber());
        Assertions.assertEquals(new Integer(21), compiler.scope.getSymbol("v").getNumber());
        Assertions.assertEquals(new Integer(22), compiler.scope.getSymbol("w").getNumber());
    }

    @Test
    void testEnumerationSectionReset() throws Exception {
        Spin2Compiler compiler = new Spin2Compiler();

        Spin2Parser parser = parseText(""
            + "CON  #1,e,f,g,h\n"
            + "\n"
            + "CON  e0,e1,e2\n");
        parser.prog().accept(compiler);

        Assertions.assertEquals(new Integer(1), compiler.scope.getSymbol("e").getNumber());
        Assertions.assertEquals(new Integer(2), compiler.scope.getSymbol("f").getNumber());
        Assertions.assertEquals(new Integer(3), compiler.scope.getSymbol("g").getNumber());
        Assertions.assertEquals(new Integer(4), compiler.scope.getSymbol("h").getNumber());

        Assertions.assertEquals(new Integer(0), compiler.scope.getSymbol("e0").getNumber());
        Assertions.assertEquals(new Integer(1), compiler.scope.getSymbol("e1").getNumber());
        Assertions.assertEquals(new Integer(2), compiler.scope.getSymbol("e2").getNumber());
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
