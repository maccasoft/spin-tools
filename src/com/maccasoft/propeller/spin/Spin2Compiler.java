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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.maccasoft.propeller.expressions.Abs;
import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.Addpins;
import com.maccasoft.propeller.expressions.And;
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Divide;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Frac;
import com.maccasoft.propeller.expressions.Group;
import com.maccasoft.propeller.expressions.HubContextLiteral;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.LogicalAnd;
import com.maccasoft.propeller.expressions.LogicalOr;
import com.maccasoft.propeller.expressions.LogicalXor;
import com.maccasoft.propeller.expressions.Modulo;
import com.maccasoft.propeller.expressions.Multiply;
import com.maccasoft.propeller.expressions.Negative;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.Or;
import com.maccasoft.propeller.expressions.Positive;
import com.maccasoft.propeller.expressions.Round;
import com.maccasoft.propeller.expressions.Sca;
import com.maccasoft.propeller.expressions.Scas;
import com.maccasoft.propeller.expressions.ShiftLeft;
import com.maccasoft.propeller.expressions.ShiftRight;
import com.maccasoft.propeller.expressions.Sqrt;
import com.maccasoft.propeller.expressions.Subtract;
import com.maccasoft.propeller.expressions.Trunc;
import com.maccasoft.propeller.expressions.Xor;
import com.maccasoft.propeller.spin.Spin2Parser.ConstantAssignContext;
import com.maccasoft.propeller.spin.Spin2Parser.ConstantEnumContext;
import com.maccasoft.propeller.spin.Spin2Parser.ConstantEnumNameContext;
import com.maccasoft.propeller.spin.Spin2Parser.ConstantsSectionContext;
import com.maccasoft.propeller.spin.Spin2Parser.DataLineContext;
import com.maccasoft.propeller.spin.Spin2Parser.ExpressionContext;
import com.maccasoft.propeller.spin.Spin2Parser.ProgContext;
import com.maccasoft.propeller.spin.instructions.Org;
import com.maccasoft.propeller.spin.instructions.Orgh;

@SuppressWarnings({
    "unchecked", "rawtypes"
})
public class Spin2Compiler extends Spin2ParserBaseVisitor {

    Spin2Context scope = new Spin2GlobalContext();
    List<Spin2PAsmLine> source = new ArrayList<Spin2PAsmLine>();

    int enumValue = 0, enumIncrement = 1;

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

        while (scope.getParent() != null) {
            scope = scope.getParent();
        }

        int _clkfreq = 20000000;
        if (scope.hasSymbol("_clkfreq")) {
            _clkfreq = scope.getSymbol("_clkfreq").getNumber().intValue();
        }
        if (scope.hasSymbol("_CLKFREQ")) {
            _clkfreq = scope.getSymbol("_CLKFREQ").getNumber().intValue();
        }
        scope.addSymbol("clkmode_", new NumberLiteral(getClockMode(20000000, _clkfreq)));

        boolean hubMode = false;
        int address = 0, hubAddress = 0;
        for (Spin2PAsmLine line : source) {
            try {
                line.getScope().setHubAddress(hubAddress);
                if (line.getInstructionFactory() instanceof Orgh) {
                    hubMode = true;
                }
                if (line.getInstructionFactory() instanceof Org) {
                    hubMode = false;
                }
                address = line.resolve(hubMode ? hubAddress : address);
                if (hubMode) {
                    hubAddress += line.getInstructionObject().getSize();
                    if (address > hubAddress) {
                        hubAddress = address;
                    }
                    else {
                        address = hubAddress;
                    }
                }
                else {
                    hubAddress += line.getInstructionObject().getSize();
                }
            } catch (Exception e) {
                System.err.println(line);
                e.printStackTrace();
            }
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
    public Object visitConstantsSection(ConstantsSectionContext ctx) {
        while (scope.getParent() != null) {
            scope = scope.getParent();
        }
        enumValue = 0;
        enumIncrement = 1;
        return super.visitConstantsSection(ctx);
    }

    @Override
    public Object visitConstantAssign(ConstantAssignContext ctx) {
        Expression expression = buildExpression(scope, ctx.exp);
        scope.addSymbol(ctx.name.getText(), expression);
        return null;
    }

    @Override
    public Object visitConstantEnum(ConstantEnumContext ctx) {
        Expression expression = buildExpression(scope, ctx.start);
        enumValue = expression.getNumber().intValue();
        if (ctx.step != null) {
            expression = buildExpression(scope, ctx.step);
            enumIncrement = expression.getNumber().intValue();
        }
        else {
            enumIncrement = 1;
        }
        return super.visitConstantEnum(ctx);
    }

    @Override
    public Object visitConstantEnumName(ConstantEnumNameContext ctx) {
        scope.addSymbol(ctx.name.getText(), new NumberLiteral(enumValue));
        enumValue += ctx.multiplier != null ? enumIncrement * Integer.parseInt(ctx.multiplier.getText()) : enumIncrement;
        return super.visitConstantEnumName(ctx);
    }

    @Override
    public Object visitDataLine(DataLineContext ctx) {
        Spin2PAsmLineBuilderVisitor lineBuilder = new Spin2PAsmLineBuilderVisitor(new Spin2Context(scope), ctx);
        ctx.accept(lineBuilder);

        Spin2PAsmLine line = lineBuilder.getLine();
        if (line.getLabel() != null) {
            try {
                if (!line.isLocalLabel() && scope.getParent() != null) {
                    scope = scope.getParent();
                }
                scope.addSymbol(line.getLabel(), new ContextLiteral(line.getScope()));
                scope.addSymbol("@" + line.getLabel(), new HubContextLiteral(line.getScope()));
                if (!line.isLocalLabel()) {
                    scope = new Spin2Context(scope);
                }
            } catch (RuntimeException e) {
                System.err.println(line);
                e.printStackTrace();
            }
        }
        source.addAll(line.expand());

        return null;
    }

    public List<Spin2PAsmLine> getSource() {
        return source;
    }

    public static Expression buildExpression(Spin2Context scope, ExpressionContext ctx) {
        if (ctx.operator != null) {
            String op = ctx.operator.getText();
            if (ctx.exp != null) {
                if ("+".equals(op)) {
                    return new Positive(buildExpression(scope, ctx.exp));
                }
                else if ("-".equals(op)) {
                    return new Negative(buildExpression(scope, ctx.exp));
                }
                else if ("abs".equalsIgnoreCase(op)) {
                    return new Abs(buildExpression(scope, ctx.exp));
                }
                else if ("sqrt".equalsIgnoreCase(op)) {
                    return new Sqrt(buildExpression(scope, ctx.exp));
                }
                else if ("float".equalsIgnoreCase(op)) {
                    return new com.maccasoft.propeller.expressions.Float(buildExpression(scope, ctx.exp));
                }
                else if ("round".equalsIgnoreCase(op)) {
                    return new Round(buildExpression(scope, ctx.exp));
                }
                else if ("trunc".equalsIgnoreCase(op)) {
                    return new Trunc(buildExpression(scope, ctx.exp));
                }
            }
            else {
                if (">>".equals(op)) {
                    return new ShiftRight(buildExpression(scope, ctx.left), buildExpression(scope, ctx.right));
                }
                else if ("<<".equals(op)) {
                    return new ShiftLeft(buildExpression(scope, ctx.left), buildExpression(scope, ctx.right));
                }
                else if ("&".equals(op)) {
                    return new And(buildExpression(scope, ctx.left), buildExpression(scope, ctx.right));
                }
                else if ("^".equals(op)) {
                    return new Xor(buildExpression(scope, ctx.left), buildExpression(scope, ctx.right));
                }
                else if ("|".equals(op)) {
                    return new Or(buildExpression(scope, ctx.left), buildExpression(scope, ctx.right));
                }
                else if ("*".equals(op)) {
                    return new Multiply(buildExpression(scope, ctx.left), buildExpression(scope, ctx.right));
                }
                else if ("/".equals(op)) {
                    return new Divide(buildExpression(scope, ctx.left), buildExpression(scope, ctx.right));
                }
                else if ("//".equals(op)) {
                    return new Modulo(buildExpression(scope, ctx.left), buildExpression(scope, ctx.right));
                }
                else if ("+".equals(op)) {
                    return new Add(buildExpression(scope, ctx.left), buildExpression(scope, ctx.right));
                }
                else if ("-".equals(op)) {
                    return new Subtract(buildExpression(scope, ctx.left), buildExpression(scope, ctx.right));
                }
                else if ("addpins".equalsIgnoreCase(op)) {
                    return new Addpins(buildExpression(scope, ctx.left), buildExpression(scope, ctx.right));
                }
                else if ("frac".equalsIgnoreCase(op)) {
                    return new Frac(buildExpression(scope, ctx.left), buildExpression(scope, ctx.right));
                }
                else if ("sca".equalsIgnoreCase(op)) {
                    return new Sca(buildExpression(scope, ctx.left), buildExpression(scope, ctx.right));
                }
                else if ("scas".equalsIgnoreCase(op)) {
                    return new Scas(buildExpression(scope, ctx.left), buildExpression(scope, ctx.right));
                }
                else if ("&&".equals(op) || "and".equalsIgnoreCase(op)) {
                    return new LogicalAnd(buildExpression(scope, ctx.left), buildExpression(scope, ctx.right));
                }
                else if ("^^".equals(op) || "xor".equalsIgnoreCase(op)) {
                    return new LogicalXor(buildExpression(scope, ctx.left), buildExpression(scope, ctx.right));
                }
                else if ("||".equals(op) || "or".equalsIgnoreCase(op)) {
                    return new LogicalOr(buildExpression(scope, ctx.left), buildExpression(scope, ctx.right));
                }
            }
        }
        else if ("(".equals(ctx.getStart().getText())) {
            return new Group(buildExpression(scope, ctx.exp));
        }
        else if (ctx.atom() != null) {
            String s = ctx.atom().getText();
            if (s.startsWith("%%")) {
                return new NumberLiteral(Long.parseLong(s.substring(2).replace("_", ""), 4));
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
            if (ctx.getStart().getText().startsWith("@")) {
                return new Identifier(ctx.getStart().getText() + s, scope);
            }
            return new Identifier(s, scope);
        }
        return new NumberLiteral(0);
    }
}
