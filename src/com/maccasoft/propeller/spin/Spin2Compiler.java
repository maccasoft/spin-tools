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
import java.util.Iterator;
import java.util.List;

import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.ExpressionBuilder;
import com.maccasoft.propeller.expressions.HubContextLiteral;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.model.ConstantAssignEnumNode;
import com.maccasoft.propeller.model.ConstantAssignNode;
import com.maccasoft.propeller.model.ConstantSetEnumNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.ParameterNode;
import com.maccasoft.propeller.spin.Spin2TokenStream.Token;
import com.maccasoft.propeller.spin.instructions.Org;
import com.maccasoft.propeller.spin.instructions.Orgh;

public class Spin2Compiler {

    Spin2Context scope = new Spin2GlobalContext();
    List<Spin2PAsmLine> source = new ArrayList<Spin2PAsmLine>();

    ExpressionBuilder expressionBuilder = new ExpressionBuilder();

    final Spin2ModelVisitor compilerVisitor = new Spin2ModelVisitor() {

        int enumValue = 0, enumIncrement = 1;

        @Override
        public void visitConstants(ConstantsNode node) {
            while (scope.getParent() != null) {
                scope = scope.getParent();
            }
            enumValue = 0;
            enumIncrement = 1;
        }

        @Override
        public void visitConstantAssign(ConstantAssignNode node) {
            String name = node.identifier.getText();
            try {
                Expression expression = buildExpression(node.expression, scope);
                scope.addSymbol(name, expression);
            } catch (Exception e) {
                for (Token t : node.expression.getTokens()) {
                    System.err.print(" " + t.getText());
                }
                System.err.println();
                throw e;
            }
        }

        @Override
        public void visitConstantSetEnum(ConstantSetEnumNode node) {
            Expression expression = buildExpression(node.start, scope);
            enumValue = expression.getNumber().intValue();
            if (node.step != null) {
                expression = buildExpression(node.step, scope);
                enumIncrement = expression.getNumber().intValue();
            }
            else {
                enumIncrement = 1;
            }
        }

        @Override
        public void visitConstantAssignEnum(ConstantAssignEnumNode node) {
            scope.addSymbol(node.identifier.getText(), new NumberLiteral(enumValue));
            if (node.multiplier != null) {
                Expression expression = buildExpression(node.multiplier, scope);
                enumValue += enumIncrement * expression.getNumber().intValue();
            }
            else {
                enumValue += enumIncrement;
            }
        }

        @Override
        public void visitDataLine(DataLineNode node) {
            String label = node.label != null ? node.label.getText() : null;
            String condition = node.condition != null ? node.condition.getText() : null;
            String mnemonic = node.instruction != null ? node.instruction.getText() : null;
            String modifier = node.modifier != null ? node.modifier.getText() : null;
            List<Spin2PAsmExpression> parameters = new ArrayList<Spin2PAsmExpression>();

            Spin2Context localScope = new Spin2Context(scope);

            for (ParameterNode param : node.parameters) {
                int index = 0;
                String prefix = null;
                Expression expression = null, count = null;

                if (param.getText().toUpperCase().contains("PTRA") || param.getText().toUpperCase().contains("PTRB")) {
                    expression = new Identifier(param.getText(), scope);
                }
                else {
                    Token token;
                    if (index < param.getTokens().size()) {
                        token = param.getToken(index);
                        if (token.getText().startsWith("#")) {
                            prefix = (prefix == null ? "" : prefix) + token.getText();
                            index++;
                        }
                    }
                    if (index < param.getTokens().size()) {
                        token = param.getToken(index);
                        if ("\\".equals(token.getText())) {
                            prefix = (prefix == null ? "" : prefix) + token.getText();
                            index++;
                        }
                    }
                    if (index < param.getTokens().size()) {
                        try {
                            expression = buildExpression(param.getTokens().subList(index, param.getTokens().size()), localScope);
                        } catch (Exception e) {
                            for (Token t : param.getTokens().subList(index, param.getTokens().size())) {
                                System.err.print(" " + t.getText());
                            }
                            System.err.println();
                            throw e;
                        }
                    }
                }
                if (param.count != null) {
                    try {
                        count = buildExpression(param.count, localScope);
                    } catch (Exception e) {
                        for (Token t : param.count.getTokens()) {
                            System.err.print(" " + t.getText());
                        }
                        System.err.println();
                        throw e;
                    }
                }
                parameters.add(new Spin2PAsmExpression(prefix, expression, count));
            }

            Spin2PAsmLine pasmLine = new Spin2PAsmLine(localScope, label, condition, mnemonic, parameters, modifier);
            if (pasmLine.getLabel() != null) {
                try {
                    if (!pasmLine.isLocalLabel() && scope.getParent() != null) {
                        scope = scope.getParent();
                    }
                    scope.addSymbol(pasmLine.getLabel(), new ContextLiteral(pasmLine.getScope()));
                    scope.addSymbol("@" + pasmLine.getLabel(), new HubContextLiteral(pasmLine.getScope()));
                    if (!pasmLine.isLocalLabel()) {
                        scope = new Spin2Context(scope);
                    }
                } catch (RuntimeException e) {
                    System.err.println(pasmLine);
                    e.printStackTrace();
                }
            }
            source.addAll(pasmLine.expand());
        }

    };

    public Spin2Compiler() {

    }

    public void generateObjectCode(OutputStream os) throws Exception {
        int address = 0;

        for (Spin2PAsmLine line : source) {
            while (address < line.getScope().getHubAddress()) {
                os.write(0x00);
                address++;
            }
            Spin2InstructionObject obj = line.getInstructionObject();
            byte[] code = obj.getBytes();
            if (code != null && code.length != 0) {
                os.write(code);
                address += code.length;
            }
        }
    }

    public void compile(Node root) {
        boolean hubMode = false;
        int address = 0, hubAddress = 0;

        root.accept(compilerVisitor);

        while (scope.getParent() != null) {
            scope = scope.getParent();
        }

        int _clkfreq = 20000000;
        if (scope.hasSymbol("_clkfreq")) {
            _clkfreq = scope.getSymbol("_clkfreq").getNumber().intValue();
        }
        else if (scope.hasSymbol("_CLKFREQ")) {
            _clkfreq = scope.getSymbol("_CLKFREQ").getNumber().intValue();
        }

        if (!scope.hasSymbol("_clkfreq")) {
            scope.addSymbol("_clkfreq", new NumberLiteral(_clkfreq));
        }
        if (!scope.hasSymbol("_CLKFREQ")) {
            scope.addSymbol("_CLKFREQ", new NumberLiteral(_clkfreq));
        }

        int _clkmode = getClockMode(20000000, _clkfreq);
        if (!scope.hasSymbol("clkmode_")) {
            scope.addSymbol("clkmode_", new NumberLiteral(_clkmode));
        }
        if (!scope.hasSymbol("CLKMODE_")) {
            scope.addSymbol("CLKMODE_", new NumberLiteral(_clkmode));
        }

        for (Spin2PAsmLine line : source) {
            try {
                line.getScope().setHubAddress(hubAddress);
                if (line.getInstructionFactory() instanceof Orgh) {
                    hubMode = true;
                }
                if (line.getInstructionFactory() instanceof Org) {
                    hubMode = false;
                    hubAddress = (hubAddress + 3) & ~3;
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

    public List<Spin2PAsmLine> getSource() {
        return source;
    }

    Expression buildExpression(Node node, Spin2Context scope) {
        return buildExpression(node.getTokens(), scope);
    }

    Expression buildExpression(List<Token> tokens, Spin2Context scope) {
        int state = 0;
        Iterator<Token> iter = tokens.iterator();

        while (iter.hasNext()) {
            Token token = iter.next();

            if ("(".equals(token.getText())) {
                expressionBuilder.addOperatorToken(expressionBuilder.GROUP_OPEN);
                state = 0;
                continue;
            }
            else if (")".equals(token.getText())) {
                expressionBuilder.addOperatorToken(expressionBuilder.GROUP_CLOSE);
                state = 1;
                continue;
            }

            switch (state) {
                case 0:
                    if ("!".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.NOT);
                    }
                    else if ("!!".equals(token.getText()) || "NOT".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.LOGICAL_NOT);
                    }
                    else if ("+".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.POSITIVE);
                    }
                    else if ("-".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.NEGATIVE);
                    }
                    else if ("ABS".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ABS);
                    }
                    else if ("ENCOD".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ENCOD);
                    }
                    else if ("DECOD".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.DECOD);
                    }
                    else if ("BMASK".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.BMASK);
                    }
                    else if ("SQRT".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.SQRT);
                    }
                    else if ("ROUND".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ROUND);
                    }
                    else if (token.type == Spin2TokenStream.NUMBER) {
                        if ("$".equals(token.getText())) {
                            expressionBuilder.addValueToken(new Identifier(token.getText(), scope));
                        }
                        else {
                            expressionBuilder.addValueToken(new NumberLiteral(token.getText()));
                        }
                    }
                    else if (token.getText().startsWith("\"")) {
                        expressionBuilder.addValueToken(new CharacterLiteral(token.getText().charAt(1)));
                    }
                    else {
                        expressionBuilder.addValueToken(new Identifier(token.getText(), scope));
                    }
                    state = 1;
                    break;
                case 1:
                    if ("!".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.NOT);
                    }
                    else if ("!!".equals(token.getText()) || "NOT".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.LOGICAL_NOT);
                    }
                    else if ("ABS".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ABS);
                    }
                    else if ("ENCOD".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ENCOD);
                    }
                    else if ("DECOD".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.DECOD);
                    }
                    else if ("BMASK".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.BMASK);
                    }
                    else if ("SQRT".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.SQRT);
                    }
                    else if (">>".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.SHIFT_RIGHT);
                    }
                    else if ("<<".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.SHIFT_LEFT);
                    }
                    else if ("&".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.AND);
                    }
                    else if ("^".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.XOR);
                    }
                    else if ("|".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.OR);
                    }
                    else if ("*".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.MULTIPLY);
                    }
                    else if ("/".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.DIVIDE);
                    }
                    else if ("//".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.MODULO);
                    }
                    else if ("+/".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.UNSIGNED_DIVIDE);
                    }
                    else if ("+//".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.UNSIGNED_MODULO);
                    }
                    else if ("SCA".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.SCA);
                    }
                    else if ("SCAS".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.SCAS);
                    }
                    else if ("FRAC".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.FRAC);
                    }
                    else if ("+".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ADD);
                    }
                    else if ("-".equals(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.SUBTRACT);
                    }
                    else if ("ADDBITS".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ADDBITS);
                    }
                    else if ("ADDPINS".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ADDPINS);
                    }
                    else if ("&&".equalsIgnoreCase(token.getText()) || "AND".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.LOGICAL_AND);
                    }
                    else if ("^^".equalsIgnoreCase(token.getText()) || "XOR".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.LOGICAL_XOR);
                    }
                    else if ("||".equalsIgnoreCase(token.getText()) || "OR".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.LOGICAL_OR);
                    }
                    else if ("?".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.TERNARYIF);
                    }
                    else if (":".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.TERNARYELSE);
                    }
                    else if ("ROUND".equalsIgnoreCase(token.getText())) {
                        expressionBuilder.addOperatorToken(expressionBuilder.ROUND);
                    }
                    else if (token.type == Spin2TokenStream.NUMBER) {
                        if ("$".equals(token.getText())) {
                            expressionBuilder.addValueToken(new Identifier(token.getText(), scope));
                        }
                        else {
                            expressionBuilder.addValueToken(new NumberLiteral(token.getText()));
                        }
                    }
                    else if (token.getText().startsWith("\"")) {
                        expressionBuilder.addValueToken(new CharacterLiteral(token.getText().charAt(1)));
                    }
                    else {
                        expressionBuilder.addValueToken(new Identifier(token.getText(), scope));
                    }
                    state = 0;
                    break;
            }
        }

        return expressionBuilder.getExpression();
    }
}
