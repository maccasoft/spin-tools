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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Abs;
import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.And;
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Decod;
import com.maccasoft.propeller.expressions.Defined;
import com.maccasoft.propeller.expressions.Divide;
import com.maccasoft.propeller.expressions.Equals;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.GreaterOrEquals;
import com.maccasoft.propeller.expressions.GreaterThan;
import com.maccasoft.propeller.expressions.Group;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.IfElse;
import com.maccasoft.propeller.expressions.LessOrEquals;
import com.maccasoft.propeller.expressions.LessThan;
import com.maccasoft.propeller.expressions.LimitMax;
import com.maccasoft.propeller.expressions.LimitMin;
import com.maccasoft.propeller.expressions.LogicalAnd;
import com.maccasoft.propeller.expressions.LogicalOr;
import com.maccasoft.propeller.expressions.Modulo;
import com.maccasoft.propeller.expressions.Multiply;
import com.maccasoft.propeller.expressions.Negative;
import com.maccasoft.propeller.expressions.Not;
import com.maccasoft.propeller.expressions.NotEquals;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.Or;
import com.maccasoft.propeller.expressions.Rev;
import com.maccasoft.propeller.expressions.Rol;
import com.maccasoft.propeller.expressions.Ror;
import com.maccasoft.propeller.expressions.Round;
import com.maccasoft.propeller.expressions.Sar;
import com.maccasoft.propeller.expressions.Scl;
import com.maccasoft.propeller.expressions.ShiftLeft;
import com.maccasoft.propeller.expressions.ShiftRight;
import com.maccasoft.propeller.expressions.Sqrt;
import com.maccasoft.propeller.expressions.Subtract;
import com.maccasoft.propeller.expressions.Trunc;
import com.maccasoft.propeller.expressions.Type;
import com.maccasoft.propeller.expressions.Xor;
import com.maccasoft.propeller.model.Token;

public class Spin1ExpressionBuilder {

    static class SpinIdentifier extends Identifier {

        Token token;

        public SpinIdentifier(Token token, Context context) {
            super(token.getText(), context);
            this.token = token;
        }

        @Override
        public Expression resolve() {
            try {
                return super.resolve();
            } catch (Exception e) {
                throw new CompilerException(e, token);
            }
        }

    }

    static Map<String, Integer> precedence = new HashMap<String, Integer>();
    static {
        precedence.put(">>", 13);
        precedence.put("<<", 13);
        precedence.put("~>", 13);
        precedence.put("->", 13);
        precedence.put("<-", 13);
        precedence.put("><", 13);

        precedence.put("&", 12);

        precedence.put("^", 11);
        precedence.put("|", 11);

        precedence.put("*", 10);
        precedence.put("**", 10);
        precedence.put("/", 10);
        precedence.put("//", 10);

        precedence.put("+", 9);
        precedence.put("-", 9);

        precedence.put("#>", 8);
        precedence.put("<#", 8);

        precedence.put("<", 7);
        precedence.put("=<", 7);
        precedence.put("==", 7);
        precedence.put("<>", 7);
        precedence.put("=>", 7);
        precedence.put(">", 7);

        precedence.put("AND", 6);

        precedence.put("OR", 5);

        precedence.put("..", 4);

        precedence.put(":", 3);
        precedence.put("?", 2);
    }

    static Set<String> unary = new HashSet<String>();
    static {
        unary.add("+");
        unary.add("-");
        unary.add("?");
        unary.add("!");
        unary.add("\\");
        unary.add("~");
        unary.add("++");
        unary.add("--");
        unary.add("||");
        unary.add("~~");
        unary.add("|<");
        unary.add("^^");
    }

    static Set<String> postEffect = new HashSet<String>();
    static {
        postEffect.add("?");
        postEffect.add("~");
        postEffect.add("++");
        postEffect.add("--");
        postEffect.add("~~");
    }

    static Map<String, Expression> constants = new HashMap<>();
    static {
        constants.put("PAR", new NumberLiteral(0x1F0, 16));
        constants.put("CNT", new NumberLiteral(0x1F1, 16));
        constants.put("INA", new NumberLiteral(0x1F2, 16));
        constants.put("INB", new NumberLiteral(0x1F3, 16));
        constants.put("OUTA", new NumberLiteral(0x1F4, 16));
        constants.put("OUTB", new NumberLiteral(0x1F5, 16));
        constants.put("DIRA", new NumberLiteral(0x1F6, 16));
        constants.put("DIRB", new NumberLiteral(0x1F7, 16));
        constants.put("CTRA", new NumberLiteral(0x1F8, 16));
        constants.put("CTRB", new NumberLiteral(0x1F9, 16));
        constants.put("FRQA", new NumberLiteral(0x1FA, 16));
        constants.put("FRQB", new NumberLiteral(0x1FB, 16));
        constants.put("PHSA", new NumberLiteral(0x1FC, 16));
        constants.put("PHSB", new NumberLiteral(0x1FD, 16));
        constants.put("VCFG", new NumberLiteral(0x1FE, 16));
        constants.put("VSCL", new NumberLiteral(0x1FF, 16));
    }

    Context context;
    boolean allowRegisters;
    List<Token> tokens = new ArrayList<Token>();

    int index;

    public Spin1ExpressionBuilder(Context context) {
        this(context, false, new ArrayList<>());
    }

    public Spin1ExpressionBuilder(Context context, boolean allowRegisters) {
        this(context, allowRegisters, new ArrayList<>());
    }

    public Spin1ExpressionBuilder(Context context, List<Token> tokens) {
        this(context, false, tokens);
    }

    public Spin1ExpressionBuilder(Context context, boolean allowRegisters, List<Token> tokens) {
        this.context = context;
        this.allowRegisters = allowRegisters;
        tokens.iterator().forEachRemaining((t) -> {
            addToken(t);
        });
    }

    public void addToken(Token token) {
        if (token.type == 0 || token.type == Token.KEYWORD) {
            List<Token> l = context.getDefinition(token.getText());
            if (l != null) {
                l.iterator().forEachRemaining((t) -> {
                    addToken(t);
                });
                return;
            }
        }
        tokens.add(token);
    }

    public void addTokenLiteral(Token token) {
        tokens.add(token);
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public Expression getExpression() {
        if (tokens.size() == 0) {
            throw new RuntimeException("expecting expression");
        }

        Expression node = parseLevel(parseAtom(), 0);

        Token token = peek();
        if (token != null) {
            throw new CompilerException("unexpected " + token.getText(), token);
        }

        return node;
    }

    Expression parseLevel(Expression left, int level) {
        for (;;) {
            Token token = peek();
            if (token == null) {
                return left;
            }

            Integer p = precedence.get(token.getText().toUpperCase());
            if (p == null || p.intValue() < level) {
                return left;
            }
            token = next();

            Expression right = parseAtom();
            for (;;) {
                Token nextToken = peek();
                if (nextToken == null) {
                    break;
                }
                Integer nextP = precedence.get(nextToken.getText().toUpperCase());
                if (nextP == null || nextP.intValue() <= p.intValue()) {
                    break;
                }
                right = parseLevel(right, p.intValue() + 1);
            }

            switch (token.getText().toUpperCase()) {
                case ">>":
                    left = new ShiftRight(left, right);
                    break;
                case "<<":
                    left = new ShiftLeft(left, right);
                    break;
                case "~>":
                    left = new Sar(left, right);
                    break;
                case "->":
                    left = new Ror(left, right);
                    break;
                case "<-":
                    left = new Rol(left, right);
                    break;
                case "><":
                    left = new Rev(left, right);
                    break;

                case "&":
                    left = new And(left, right);
                    break;
                case "^":
                    left = new Xor(left, right);
                    break;
                case "|":
                    left = new Or(left, right);
                    break;

                case "*":
                    left = new Multiply(left, right);
                    break;
                case "**":
                    left = new Scl(left, right);
                    break;
                case "/":
                    left = new Divide(left, right);
                    break;
                case "//":
                    left = new Modulo(left, right);
                    break;

                case "+":
                    left = new Add(left, right);
                    break;
                case "-":
                    left = new Subtract(left, right);
                    break;

                case "#>":
                    left = new LimitMin(left, right);
                    break;
                case "<#":
                    left = new LimitMax(left, right);
                    break;

                case "<":
                    left = new LessThan(left, right);
                    break;
                case "=<":
                    left = new LessOrEquals(left, right);
                    break;
                case "==":
                    left = new Equals(left, right);
                    break;
                case "<>":
                    left = new NotEquals(left, right);
                    break;
                case "=>":
                    left = new GreaterOrEquals(left, right);
                    break;
                case ">":
                    left = new GreaterThan(left, right);
                    break;

                case "AND":
                    left = new LogicalAnd(left, right);
                    break;
                case "OR":
                    left = new LogicalOr(left, right);
                    break;

                case "?":
                    if (!(right instanceof IfElse)) {
                        throw new CompilerException("invalid operator " + token.getText(), token);
                    }
                    left = new IfElse(left, ((IfElse) right).getTrueTerm(), ((IfElse) right).getFalseTerm());
                    break;
                case ":":
                    left = new IfElse(null, left, right);
                    break;

                default:
                    throw new CompilerException("unsupported operator " + token.getText(), token);
            }
        }
    }

    Expression parseAtom() {
        Token token = peek();
        if (token == null) {
            throw new CompilerException("expecting operand", tokens.get(tokens.size() - 1));
        }

        if (unary.contains(token.getText())) {
            token = next();
            switch (token.getText()) {
                case "+":
                    return parseAtom();
                case "-":
                    return new Negative(parseAtom());
                case "!":
                    return new Not(parseAtom());
                case "|<":
                    return new Decod(parseAtom());
                case "||":
                    return new Abs(parseAtom());
                case "^^":
                    return new Sqrt(parseAtom());
                default:
                    throw new CompilerException("invalid unary operator " + token.getText(), token);
            }
        }

        if ("BYTE".equalsIgnoreCase(token.getText()) || "WORD".equalsIgnoreCase(token.getText()) || "LONG".equalsIgnoreCase(token.getText())) {
            token = next();
            return new Type(token.getText(), parseLevel(parseAtom(), 0));
        }

        if ("(".equals(token.getText())) {
            next();
            Group expression = new Group(parseLevel(parseAtom(), 0));
            token = next();
            if (token == null || !")".equals(token.getText())) {
                throw new CompilerException("expecting )", token == null ? tokens.get(tokens.size() - 1) : token);
            }
            return expression;
        }

        if (token.type != Token.OPERATOR) {
            token = next();

            if (allowRegisters) {
                Expression expression = constants.get(token.getText().toUpperCase());
                if (expression != null) {
                    return expression;
                }
            }

            switch (token.getText().toUpperCase()) {
                case "DEFINED": {
                    token = next();
                    if (token == null || !"(".equals(token.getText())) {
                        throw new CompilerException("expecting (", token == null ? tokens.get(tokens.size() - 1) : token);
                    }
                    token = next();
                    if (token == null) {
                        throw new CompilerException("expecting identifier", token == null ? tokens.get(tokens.size() - 1) : token);
                    }
                    Expression expression = new Defined(token.getText(), context);
                    token = next();
                    if (token == null || !")".equals(token.getText())) {
                        throw new CompilerException("expecting )", token == null ? tokens.get(tokens.size() - 1) : token);
                    }
                    return expression;
                }
                case "FLOAT": {
                    token = next();
                    if (token == null || !"(".equals(token.getText())) {
                        throw new CompilerException("expecting (", token == null ? tokens.get(tokens.size() - 1) : token);
                    }
                    Expression expression = new com.maccasoft.propeller.expressions.Float(parseLevel(parseAtom(), 0));
                    token = next();
                    if (token == null || !")".equals(token.getText())) {
                        throw new CompilerException("expecting )", token == null ? tokens.get(tokens.size() - 1) : token);
                    }
                    return expression;
                }
                case "TRUNC": {
                    token = next();
                    if (token == null || !"(".equals(token.getText())) {
                        throw new CompilerException("expecting (", token == null ? tokens.get(tokens.size() - 1) : token);
                    }
                    Expression expression = new Trunc(parseLevel(parseAtom(), 0));
                    token = next();
                    if (token == null || !")".equals(token.getText())) {
                        throw new CompilerException("expecting )", token == null ? tokens.get(tokens.size() - 1) : token);
                    }
                    return expression;
                }
                case "ROUND": {
                    token = next();
                    if (token == null || !"(".equals(token.getText())) {
                        throw new CompilerException("expecting (", token == null ? tokens.get(tokens.size() - 1) : token);
                    }
                    Expression expression = new Round(parseLevel(parseAtom(), 0));
                    token = next();
                    if (token == null || !")".equals(token.getText())) {
                        throw new CompilerException("expecting )", token == null ? tokens.get(tokens.size() - 1) : token);
                    }
                    return expression;
                }
                default:
                    if (token.type == Token.NUMBER) {
                        if ("$".equals(token.getText())) {
                            return new SpinIdentifier(token, context);
                        }
                        return new NumberLiteral(token.getText());
                    }
                    if (token.type == Token.STRING) {
                        String s = token.getText().substring(1);
                        return new CharacterLiteral(s.substring(0, s.length() - 1));
                    }
                    return new SpinIdentifier(token, context);
            }
        }

        throw new CompilerException("unexpected " + token.getText(), token);
    }

    Token peek() {
        if (index < tokens.size()) {
            return tokens.get(index);
        }
        return null;
    }

    Token next() {
        if (index < tokens.size()) {
            return tokens.get(index++);
        }
        return null;
    }

    static Context scope = new Context();

    public static void main(String[] args) {
        String text;
        Expression expression;

        text = "16 / 2 / 2";
        System.out.println(text);
        expression = parse(text);
        System.out.println(expression + " = " + expression.getNumber());

        text = "160 * 25 - 1";
        System.out.println(text);
        expression = parse(text);
        System.out.println(expression + " = " + expression.getNumber());
    }

    static Expression parse(String text) {
        List<Token> tokens = new ArrayList<Token>();

        Spin1TokenStream stream = new Spin1TokenStream(text);
        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF) {
                break;
            }
            tokens.add(token);
        }

        Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder(scope, tokens);
        return builder.getExpression();
    }

}
