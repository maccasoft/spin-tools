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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.Addpins;
import com.maccasoft.propeller.expressions.And;
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Divide;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.Modulo;
import com.maccasoft.propeller.expressions.Multiply;
import com.maccasoft.propeller.expressions.Negative;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.Or;
import com.maccasoft.propeller.expressions.Positive;
import com.maccasoft.propeller.expressions.Round;
import com.maccasoft.propeller.expressions.ShiftLeft;
import com.maccasoft.propeller.expressions.ShiftRight;
import com.maccasoft.propeller.expressions.Subtract;
import com.maccasoft.propeller.expressions.Trunc;
import com.maccasoft.propeller.expressions.Xor;
import com.maccasoft.propeller.spin.Spin2Parser.ConstantContext;
import com.maccasoft.propeller.spin.Spin2Parser.ConstantsContext;
import com.maccasoft.propeller.spin.Spin2Parser.DataLineContext;
import com.maccasoft.propeller.spin.Spin2Parser.ExpressionContext;
import com.maccasoft.propeller.spin.Spin2Parser.ProgContext;

@SuppressWarnings({
    "unchecked", "rawtypes"
})
public class Spin2Compiler extends Spin2BaseVisitor {

    Spin2Context scope = new Spin2GlobalContext();
    List<Spin2PAsmLine> source = new ArrayList<Spin2PAsmLine>();

    public Spin2Compiler() {

    }

    public void generateObjectCode(OutputStream os) throws Exception {
        for (Spin2PAsmLine line : source) {
            line.generateObjectCode(os);
        }
    }

    @Override
    public Object visitProg(ProgContext ctx) {
        super.visitProg(ctx);

        int _clkfreq = 20000000;
        if (scope.hasSymbol("_clkfreq")) {
            _clkfreq = scope.getSymbol("_clkfreq").getNumber().intValue();
        }
        scope.addSymbol("clkmode_", new NumberLiteral(getClockMode(20000000, _clkfreq)));

        int address = 0;
        for (Spin2PAsmLine line : source) {
            address = line.resolve(address);
        }

        return null;
    }

    int getClockMode(int xinfreq, int clkfreq) {
        int divd;
        int zzzz = 11; // 0b10_11
        double e, post, mult, Fpfd, Fvco, Fout;
        double result_mult = 0;
        //double result_Fout = 0;
        int result_pppp = 0, result_divd = 0;
        double error = 1e9;

        for (int pppp = 0; pppp <= 15; pppp++) {
            if (pppp == 0) {
                post = 1.0;
            }
            else {
                post = pppp * 2.0;
            }
            for (divd = 64; divd >= 1; --divd) {
                Fpfd = Math.round(xinfreq / (double) divd);
                mult = Math.round(clkfreq * post / Fpfd);
                Fvco = Math.round(Fpfd * mult);
                Fout = Math.round(Fvco / post);
                e = Math.abs(Fout - clkfreq);
                if ((e <= error) && (Fpfd >= 250000) && (mult <= 1024) && (Fvco > 99e6) && ((Fvco <= 201e6) || (Fvco <= clkfreq + 1e6))) {
                    result_divd = divd;
                    result_mult = mult;
                    result_pppp = (pppp - 1) & 15;
                    //result_Fout = Fout;
                    error = e;
                }
            }
        }
        if (error > 100000.0) {
            throw new RuntimeException(String.format("Unable to find clock settings for freq %f Hz with input freq %f Hz", clkfreq, xinfreq));
        }

        int D = result_divd - 1;
        int M = ((int) result_mult) - 1;
        int clkmode = zzzz | (result_pppp << 4) | (M << 8) | (D << 18) | (1 << 24);

        //int finalfreq = (int) Math.round(result_Fout);

        return clkmode;
    }

    @Override
    public Object visitConstants(ConstantsContext ctx) {
        ctx.accept(new Spin2BaseVisitor() {
            int enumValue = 0, enumIncrement = 1;

            @Override
            public Object visitConstant(ConstantContext ctx) {
                if (ctx.start != null) {
                    enumValue = Integer.parseInt(ctx.start.getText());
                    enumIncrement = 1;
                }

                if (ctx.step != null) {
                    enumIncrement = Integer.parseInt(ctx.step.getText());
                }

                if (ctx.name != null) {
                    if (ctx.exp != null) {
                        Expression expression = compileExpression(scope, ctx.exp);
                        scope.addSymbol(ctx.name.getText(), expression);
                    }
                    else {
                        scope.addSymbol(ctx.name.getText(), new NumberLiteral(enumValue));
                    }

                    enumValue += ctx.multiplier != null ? enumIncrement * Integer.parseInt(ctx.multiplier.getText()) : enumIncrement;
                }

                return null;
            }

        });
        return null;
    }

    @Override
    public Object visitDataLine(DataLineContext ctx) {
        Spin2PAsmLineBuilderVisitor lineBuilder = new Spin2PAsmLineBuilderVisitor(new Spin2Context(scope));
        ctx.accept(lineBuilder);

        Spin2PAsmLine line = lineBuilder.getLine();
        if (line.getLabel() != null) {
            try {
                scope.addSymbol(line.getLabel(), new ContextLiteral(line.getScope()));
            } catch (RuntimeException e) {
                System.err.println(line);
            }
        }
        source.addAll(line.expand());

        return null;
    }

    public static Expression compileExpression(Spin2Context scope, ExpressionContext ctx) {
        if (ctx.operator != null) {
            String op = ctx.operator.getText();
            if (ctx.left == null) {
                if ("+".equals(op)) {
                    return new Positive(compileExpression(scope, ctx.right));
                }
                else if ("-".equals(op)) {
                    return new Negative(compileExpression(scope, ctx.right));
                }
            }
            else {
                if ("+".equals(op)) {
                    return new Add(compileExpression(scope, ctx.left), compileExpression(scope, ctx.right));
                }
                else if ("-".equals(op)) {
                    return new Subtract(compileExpression(scope, ctx.left), compileExpression(scope, ctx.right));
                }
                else if ("/".equals(op)) {
                    return new Divide(compileExpression(scope, ctx.left), compileExpression(scope, ctx.right));
                }
                else if ("*".equals(op)) {
                    return new Multiply(compileExpression(scope, ctx.left), compileExpression(scope, ctx.right));
                }
                else if ("|".equals(op)) {
                    return new Or(compileExpression(scope, ctx.left), compileExpression(scope, ctx.right));
                }
                else if ("^".equals(op)) {
                    return new Xor(compileExpression(scope, ctx.left), compileExpression(scope, ctx.right));
                }
                else if ("&".equals(op)) {
                    return new And(compileExpression(scope, ctx.left), compileExpression(scope, ctx.right));
                }
                else if (">>".equals(op)) {
                    return new ShiftRight(compileExpression(scope, ctx.left), compileExpression(scope, ctx.right));
                }
                else if ("<<".equals(op)) {
                    return new ShiftLeft(compileExpression(scope, ctx.left), compileExpression(scope, ctx.right));
                }
                else if ("//".equals(op)) {
                    return new Modulo(compileExpression(scope, ctx.left), compileExpression(scope, ctx.right));
                }
                else if ("addpins".equalsIgnoreCase(op)) {
                    return new Addpins(compileExpression(scope, ctx.left), compileExpression(scope, ctx.right));
                }
                else if ("round".equalsIgnoreCase(op)) {
                    return new Round(compileExpression(scope, ctx.exp));
                }
                else if ("trunc".equalsIgnoreCase(op)) {
                    return new Trunc(compileExpression(scope, ctx.exp));
                }
            }
        }
        else if (ctx.atom() != null) {
            String s = ctx.atom().getText();
            if (s.startsWith("%%")) {
                return new NumberLiteral(Long.parseLong(s.substring(1).replace("_", ""), 4));
            }
            else if (s.startsWith("%")) {
                return new NumberLiteral(Long.parseLong(s.substring(1).replace("_", ""), 2));
            }
            else if (s.startsWith("$")) {
                if (s.length() == 1) {
                    return new Identifier(s, scope);
                }
                return new NumberLiteral(Long.parseLong(s.substring(1).replace("_", ""), 16));
            }
            else if (s.startsWith("\"")) {
                return new CharacterLiteral(s.charAt(1));
            }
            else if ((s.charAt(0) >= '0' && s.charAt(0) <= '9')) {
                if (s.contains(".")) {
                    return new NumberLiteral(Double.parseDouble(s.replace("_", "")));
                }
                else {
                    return new NumberLiteral(Long.parseLong(s.replace("_", "")));
                }
            }
            return new Identifier(s, scope);
        }
        return new NumberLiteral(0);
    }

    public static void main(String[] args) {
        Spin2Compiler compiler = new Spin2Compiler();
        try {
            String text = ""
                + "CON\n"
                + "\n"
                + "    _clkfreq = 160_000_000\n"
                + "\n"
                + "    delay    = _clkfreq / 2\n"
                + "\n"
                + "DAT\n"
                + "\n"
                + "                org   $000\n"
                + "\n"
                + "start\n"
                + "                asmclk                      ' set clock\n"
                + "\n"
                + "                getct   ct                  ' get current timer\n"
                + ".loop           drvnot  #56                 ' toggle output\n"
                + "                addct1  ct, ##delay         ' set delay to timer 1\n"
                + "                waitct1                     ' wait for timer 1 expire\n"
                + "                jmp     #\\.loop\n"
                + "\n"
                + "ct              res     1\n";

            CharStream input = CharStreams.fromString(text);

            Spin2Lexer lexer = new Spin2Lexer(input);
            lexer.removeErrorListeners();
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            Spin2Parser parser = new Spin2Parser(tokens);
            parser.removeErrorListeners();

            parser.prog().accept(compiler);

            int total = 0;
            for (Spin2PAsmLine line : compiler.source) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                line.generateObjectCode(os);
                byte[] code = os.toByteArray();

                int address = line.getScope().getAddress();
                System.out.print(String.format("%06X %03X ", total, address++));
                for (int i = 0; i < code.length; i++) {
                    if (i > 0 && (i % 4) == 0) {
                        System.out.println(" |");
                        System.out.print(String.format("%06X %03X ", total, address++));
                    }
                    System.out.print(String.format(" %02X", code[i]));
                    total++;
                }
                if (code.length == 0 || (code.length % 4) != 0) {
                    for (int i = 0; i < 4 - (code.length % 4); i++) {
                        System.out.print("   ");
                    }
                }
                System.out.println(" | " + line);
            }
            System.out.print(String.format("%06X\n", total));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
