/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.model.Token;

@TestInstance(Lifecycle.PER_CLASS)
class Spin2BytecodeCompilerTest {

    Spin2Compiler compiler;
    Spin2BytecodeCompiler subject;
    Context context;
    Spin2Method method;

    @BeforeEach
    void setUp() {
        compiler = new Spin2Compiler();
        subject = new Spin2ObjectCompiler(compiler, new File("test.spin"));

        context = subject.getScope();
        context.addSymbol("a", new Variable("LONG", "a", 1));
        context.addSymbol("b", new Variable("LONG", "b", 1));

        Spin2Struct point = new Spin2Struct(context);
        point.addMember(new Token(0, "WORD"), new Token(0, "x"), new NumberLiteral(1));
        point.addMember(new Token(0, "WORD"), new Token(0, "y"), new NumberLiteral(1));
        point.setTypeSize(4);
        context.addStructureDefinition("sPoint", point);

        Spin2Struct line = new Spin2Struct(context);
        line.addMember(new Token(0, "SPOINT"), new Token(0, "a"), new NumberLiteral(1));
        line.addMember(new Token(0, "SPOINT"), new Token(0, "b"), new NumberLiteral(1));
        line.addMember(new Token(0, "BYTE"), new Token(0, "color"), new NumberLiteral(1));
        line.setTypeSize(4 + 4 + 1);
        context.addStructureDefinition("sLine", line);

        context.addSymbol("point", new Variable("SPOINT", "point", 1));
        context.addSymbol("line", new Variable("SLINE", "line", 1));

        method = new Spin2Method(context, "main");

        Spin2Method f2 = new Spin2Method(new Context(context), "f2");
        f2.addParameter(new LocalVariable("LONG", "p1", null, 1, f2.getVarOffset()));
        f2.addParameter(new LocalVariable("LONG", "p2", null, 1, f2.getVarOffset()));
        Method methodExpression = new Method(f2.getLabel(), f2.getParameterLongs(), f2.getReturnLongs());
        methodExpression.setData(Spin2Method.class.getName(), f2);
        context.addSymbol(f2.getLabel(), methodExpression);

        Spin2Method f2r = new Spin2Method(new Context(context), "f2r");
        f2r.addParameter(new LocalVariable("LONG", "p1", null, 1, f2r.getVarOffset()));
        f2r.addParameter(new LocalVariable("LONG", "p2", null, 1, f2r.getVarOffset()));
        f2r.addReturnVariable(new LocalVariable("LONG", "r1", 1, f2r.getVarOffset()));
        methodExpression = new Method(f2r.getLabel(), f2r.getParameterLongs(), f2r.getReturnLongs());
        methodExpression.setData(Spin2Method.class.getName(), f2r);
        context.addSymbol(f2r.getLabel(), methodExpression);
    }

    @Test
    void testConstant() {
        Spin2StatementNode root = parse("1234");

        subject.compileBytecodeExpression(context, method, root, false);

        Assertions.assertEquals(""
            + "[1234] (return = 1)\n"
            + "", print(root));
    }

    @Test
    void testSimpleAssignment() {
        Spin2StatementNode root = parse("a := 1234");

        subject.compileBytecodeExpression(context, method, root, false);

        Assertions.assertEquals(""
            + "[:=] (return = 0)\n"
            + " +-- [a] (return = 1)\n"
            + " +-- [1234] (return = 1)\n"
            + "", print(root));
    }

    @Test
    void testExpressionAssignment() {
        Spin2StatementNode root = parse("a := b + 1");

        subject.compileBytecodeExpression(context, method, root, false);

        Assertions.assertEquals(""
            + "[:=] (return = 0)\n"
            + " +-- [a] (return = 1)\n"
            + " +-- [+] (return = 1)\n"
            + "      +-- [b] (return = 1)\n"
            + "      +-- [1] (return = 1)\n"
            + "", print(root));
    }

    @Test
    void testChainedAssignment() {
        Spin2StatementNode root = parse("a := b := 1234");

        subject.compileBytecodeExpression(context, method, root, false);

        Assertions.assertEquals(""
            + "[:=] (return = 0)\n"
            + " +-- [a] (return = 1)\n"
            + " +-- [:=] (return = 1)\n"
            + "      +-- [b] (return = 1)\n"
            + "      +-- [1234] (return = 1)\n"
            + "", print(root));
    }

    @Test
    void testMethodCall() {
        Spin2StatementNode root = parse("f2(a, b)");

        subject.compileBytecodeExpression(context, method, root, false);

        Assertions.assertEquals(""
            + "[f2] (return = 0)\n"
            + " +-- [a] (return = 1)\n"
            + " +-- [b] (return = 1)\n"
            + "", print(root));
    }

    @Test
    void testMethodCallAssignment() {
        Spin2StatementNode root = parse("a := f2(a, b)");

        subject.compileBytecodeExpression(context, method, root, false);

        Assertions.assertEquals(""
            + "[:=] (return = 0)\n"
            + " +-- [a] (return = 1)\n"
            + " +-- [f2] (return = 0)\n"
            + "      +-- [a] (return = 1)\n"
            + "      +-- [b] (return = 1)\n"
            + "", print(root));
    }

    @Test
    void testMethodReturnCall() {
        Spin2StatementNode root = parse("f2r(a, b)");

        subject.compileBytecodeExpression(context, method, root, false);

        Assertions.assertEquals(""
            + "[f2r] (return = 0)\n"
            + " +-- [a] (return = 1)\n"
            + " +-- [b] (return = 1)\n"
            + "", print(root));
    }

    @Test
    void testMethodReturnCallAssignment() {
        Spin2StatementNode root = parse("a := f2r(a, b)");

        subject.compileBytecodeExpression(context, method, root, false);

        Assertions.assertEquals(""
            + "[:=] (return = 0)\n"
            + " +-- [a] (return = 1)\n"
            + " +-- [f2r] (return = 1)\n"
            + "      +-- [a] (return = 1)\n"
            + "      +-- [b] (return = 1)\n"
            + "", print(root));
    }

    @Test
    void testStructure() {
        Spin2StatementNode root = parse("line");

        subject.compileBytecodeExpression(context, method, root, false);

        Assertions.assertEquals(""
            + "[line] (return = 3)\n"
            + "", print(root));
    }

    @Test
    void testStructureMember() {
        Spin2StatementNode root = parse("line.b");

        subject.compileBytecodeExpression(context, method, root, false);

        Assertions.assertEquals(""
            + "[line.b] (return = 1)\n"
            + "", print(root));
    }

    Spin2StatementNode parse(String text) {
        Spin2TreeBuilder builder = new Spin2TreeBuilder(context);

        Spin2TokenStream stream = new Spin2TokenStream(text);
        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF) {
                break;
            }
            if (".".equals(token.getText())) {
                Token nextToken = stream.peekNext();
                if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                    token = token.merge(stream.nextToken());
                }
            }
            builder.addToken(token);
        }

        return builder.getRoot();
    }

    String print(Spin2StatementNode root) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        print(new PrintStream(os), root, 0);
        return os.toString().replaceAll("\\r\\n", "\n");
    }

    void print(PrintStream out, Spin2StatementNode node, int indent) {
        if (indent != 0) {
            for (int i = 1; i < indent; i++) {
                out.print("     ");
            }
            out.print(" +-- ");
        }

        out.print("[" + node.getText().replaceAll("\n", "\\\\n") + "] (return = " + node.getReturnLongs() + ")");
        out.println();

        for (Spin2StatementNode child : node.getChilds()) {
            print(out, child, indent + 1);
        }
    }

}
