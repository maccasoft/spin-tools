/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.Addbits;
import com.maccasoft.propeller.expressions.Addpins;
import com.maccasoft.propeller.expressions.And;
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.Decod;
import com.maccasoft.propeller.expressions.Divide;
import com.maccasoft.propeller.expressions.Equals;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.GreaterOrEquals;
import com.maccasoft.propeller.expressions.GreaterThan;
import com.maccasoft.propeller.expressions.Group;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.LessOrEquals;
import com.maccasoft.propeller.expressions.LessThan;
import com.maccasoft.propeller.expressions.LogicalAnd;
import com.maccasoft.propeller.expressions.LogicalNot;
import com.maccasoft.propeller.expressions.LogicalOr;
import com.maccasoft.propeller.expressions.LogicalXor;
import com.maccasoft.propeller.expressions.Modulo;
import com.maccasoft.propeller.expressions.Multiply;
import com.maccasoft.propeller.expressions.Negative;
import com.maccasoft.propeller.expressions.Not;
import com.maccasoft.propeller.expressions.NotEquals;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.Or;
import com.maccasoft.propeller.expressions.Round;
import com.maccasoft.propeller.expressions.Sca;
import com.maccasoft.propeller.expressions.Scas;
import com.maccasoft.propeller.expressions.ShiftLeft;
import com.maccasoft.propeller.expressions.ShiftRight;
import com.maccasoft.propeller.expressions.Subtract;
import com.maccasoft.propeller.expressions.Trunc;
import com.maccasoft.propeller.expressions.UnsignedDivide;
import com.maccasoft.propeller.expressions.UnsignedModulo;
import com.maccasoft.propeller.expressions.Xor;
import com.maccasoft.propeller.model.Token;

public class Spin2ExpressionBuilder {

    static Map<String, Integer> precedence = new HashMap<String, Integer>();
    static {
        precedence.put(">>", 13);
        precedence.put("<<", 13);
        precedence.put("SAR", 13);
        precedence.put("ROR", 13);
        precedence.put("ROL", 13);
        precedence.put("REV", 13);
        precedence.put("ZEROX", 13);
        precedence.put("SIGNX", 13);

        precedence.put("&", 12);
        precedence.put("^", 11);
        precedence.put("|", 10);

        precedence.put("*", 9);
        precedence.put("/", 9);
        precedence.put("+/", 9);
        precedence.put("//", 9);
        precedence.put("+//", 9);
        precedence.put("SCA", 9);
        precedence.put("SCAS", 9);
        precedence.put("FRAC", 9);

        precedence.put("+", 8);
        precedence.put("-", 8);

        precedence.put("#>", 7);
        precedence.put("<#", 7);

        precedence.put("ADDBITS", 6);
        precedence.put("ADDPINS", 6);

        precedence.put("<", 5);
        precedence.put("+<", 5);
        precedence.put("<=", 5);
        precedence.put("+<=", 5);
        precedence.put("==", 5);
        precedence.put("<>", 5);
        precedence.put(">=", 5);
        precedence.put("+>=", 5);
        precedence.put(">", 5);
        precedence.put("+>", 5);
        precedence.put("<=>", 5);

        precedence.put("&&", 4);
        precedence.put("AND", 4);
        precedence.put("^^", 4);
        precedence.put("XOR", 4);
        precedence.put("||", 4);
        precedence.put("OR", 4);

        precedence.put("..", 3);

        precedence.put(":", 2);
        precedence.put("?", 2);
    }

    static Set<String> unary = new HashSet<String>();
    static {
        unary.add("+");
        unary.add("-");
        unary.add("!");
        unary.add("!!");
        unary.add("\\");
        unary.add("++");
        unary.add("--");
        unary.add("NOT");
        unary.add("ABS");
        unary.add("ENCOD");
        unary.add("DECOD");
        unary.add("BMASK");
        unary.add("SQRT");
        unary.add("ROUND");
    }

    static Set<String> postEffect = new HashSet<String>();
    static {
        postEffect.add("?");
        postEffect.add("~");
        postEffect.add("++");
        postEffect.add("--");
        postEffect.add("~~");
    }

    Spin2Context context;
    List<Token> tokens = new ArrayList<Token>();

    int index;

    public Spin2ExpressionBuilder(Spin2Context context, List<Token> tokens) {
        this.context = context;
        this.tokens = tokens;
    }

    public Expression getExpression() {
        Expression node = parseLevel(parseAtom(), 0);

        Token token = peek();
        if (token != null) {
            throw new RuntimeException("unexpected " + token.getText());
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
                right = parseLevel(right, level + 1);
            }

            switch (token.getText().toUpperCase()) {
                case ">>":
                    left = new ShiftRight(left, right);
                    break;
                case "<<":
                    left = new ShiftLeft(left, right);
                    break;
                case "~>":
                    throw new RuntimeException("invalid binary operator " + token.getText());
                case "->":
                    throw new RuntimeException("invalid binary operator " + token.getText());
                case "<-":
                    throw new RuntimeException("invalid binary operator " + token.getText());
                case "><":
                    throw new RuntimeException("invalid binary operator " + token.getText());

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
                    throw new RuntimeException("invalid binary operator " + token.getText());
                case "/":
                    left = new Divide(left, right);
                    break;
                case "//":
                    left = new Modulo(left, right);
                    break;
                case "+/":
                    left = new UnsignedDivide(left, right);
                    break;
                case "+//":
                    left = new UnsignedModulo(left, right);
                    break;
                case "SCA":
                    left = new Sca(left, right);
                    break;
                case "SCAS":
                    left = new Scas(left, right);
                    break;

                case "+":
                    left = new Add(left, right);
                    break;
                case "-":
                    left = new Subtract(left, right);
                    break;

                case "ADDBITS":
                    left = new Addbits(left, right);
                    break;
                case "ADDPINS":
                    left = new Addpins(left, right);
                    break;

                case "&&":
                case "AND":
                    left = new LogicalAnd(left, right);
                    break;
                case "||":
                case "OR":
                    left = new LogicalOr(left, right);
                    break;
                case "^^":
                case "XOR":
                    left = new LogicalXor(left, right);
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

                case "?":
                    throw new RuntimeException("invalid binary operator " + token.getText());
                case ":":
                    throw new RuntimeException("invalid binary operator " + token.getText());

                default:
                    throw new RuntimeException("invalid binary operator " + token.getText());
            }
        }
    }

    Expression parseAtom() {
        Token token = peek();

        if (unary.contains(token.getText())) {
            token = next();
            switch (token.getText()) {
                case "!":
                    return new Not(parseAtom());
                case "!!":
                case "NOT":
                    return new LogicalNot(parseAtom());
                case "+":
                    return parseAtom();
                case "-":
                    return new Negative(parseAtom());
                case "|<":
                    return new Decod(parseAtom());
                default:
                    throw new RuntimeException("invalid unary operator " + token.getText());
            }
        }

        if ("(".equals(token.getText())) {
            next();
            Group expression = new Group(parseLevel(parseAtom(), 0));
            token = next();
            if (token == null || !")".equals(token.getText())) {
                throw new RuntimeException("expecting closing parenthesis");
            }
            return expression;
        }

        if (token.type != Token.OPERATOR) {
            token = next();
            switch (token.getText().toUpperCase()) {
                case "FLOAT": {
                    next();
                    Expression expression = new com.maccasoft.propeller.expressions.Float(parseLevel(parseAtom(), 0));
                    token = next();
                    if (token == null || !")".equals(token.getText())) {
                        throw new RuntimeException("expecting closing parenthesis");
                    }
                    return expression;
                }
                case "TRUNC": {
                    next();
                    Expression expression = new Trunc(parseLevel(parseAtom(), 0));
                    token = next();
                    if (token == null || !")".equals(token.getText())) {
                        throw new RuntimeException("expecting closing parenthesis");
                    }
                    return expression;
                }
                case "ROUND": {
                    next();
                    Expression expression = new Round(parseLevel(parseAtom(), 0));
                    token = next();
                    if (token == null || !")".equals(token.getText())) {
                        throw new RuntimeException("expecting closing parenthesis");
                    }
                    return expression;
                }
                default:
                    if (token.type == Token.NUMBER) {
                        if ("$".equals(token.getText())) {
                            return new Identifier(token.getText(), context);
                        }
                        return new NumberLiteral(token.getText());
                    }
                    if (token.type == Token.STRING) {
                        return new CharacterLiteral(token.getText().charAt(1));
                    }
                    return new Identifier(token.getText(), context);
            }
        }

        throw new RuntimeException("unexpected " + token.getText());
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

    static Spin2Context scope = new Spin2Context();

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

        text = "48 addpins 1";
        System.out.println(text);
        expression = parse(text);
        System.out.println(expression + " = " + expression.getNumber());
    }

    static Expression parse(String text) {
        List<Token> tokens = new ArrayList<Token>();

        Spin2TokenStream stream = new Spin2TokenStream(text);
        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF) {
                break;
            }
            tokens.add(token);
        }

        Spin2ExpressionBuilder builder = new Spin2ExpressionBuilder(scope, tokens);
        return builder.getExpression();
    }

}
