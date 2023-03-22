/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.And;
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.Defined;
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
import com.maccasoft.propeller.expressions.ShiftLeft;
import com.maccasoft.propeller.expressions.ShiftRight;
import com.maccasoft.propeller.expressions.Subtract;
import com.maccasoft.propeller.expressions.Xor;
import com.maccasoft.propeller.model.Token;

public class CExpressionBuilder {

    static Map<String, Integer> precedence = new HashMap<String, Integer>();
    static {
        precedence.put(">>", 14);
        precedence.put("<<", 14);

        precedence.put("&", 13);
        precedence.put("^", 12);
        precedence.put("|", 11);

        precedence.put("*", 10);
        precedence.put("/", 10);
        precedence.put("//", 10);

        precedence.put("+", 9);
        precedence.put("-", 9);

        precedence.put("<", 6);
        precedence.put("<=", 6);
        precedence.put("==", 6);
        precedence.put("!=", 6);
        precedence.put(">=", 6);
        precedence.put(">", 6);

        precedence.put("&&", 5);
        precedence.put("and", 5);
        precedence.put("^^", 5);
        precedence.put("xor", 5);
        precedence.put("||", 5);
        precedence.put("or", 5);
    }

    static Set<String> unary = new HashSet<String>();
    static {
        unary.add("+");
        unary.add("-");
        unary.add("!");
        unary.add("not");
    }

    protected Spin2CContext context;
    protected List<Token> tokens = new ArrayList<Token>();

    int index;
    Set<String> dependencies = new HashSet<>();

    public CExpressionBuilder(Spin2CContext context) {
        this.context = context;
    }

    public void addToken(Token token) {
        List<Token> list = context.getDefinition(token.getText());
        if (list != null && list.size() != 0) {
            if (dependencies.contains(token.getText())) {
                throw new CompilerException("circular dependency", token);
            }
            dependencies.add(token.getText());
            Iterator<Token> iter = list.iterator();
            while (iter.hasNext()) {
                addToken(iter.next());
            }
            dependencies.remove(token.getText());
        }
        else {
            tokens.add(token);
        }
    }

    public void addTokenLiteral(Token token) {
        tokens.add(token);
    }

    public Expression getExpression() {
        Expression node = parseLevel(parseAtom(), 0);

        Token token = peek();
        if (token != null) {
            throw new CompilerException("unexpected " + token.getText(), token);
        }

        return node;
    }

    protected Expression parseLevel(Expression left, int level) {
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
                case "<=":
                    left = new LessOrEquals(left, right);
                    break;
                case "==":
                    left = new Equals(left, right);
                    break;
                case "!=":
                    left = new NotEquals(left, right);
                    break;
                case ">=":
                    left = new GreaterOrEquals(left, right);
                    break;
                case ">":
                    left = new GreaterThan(left, right);
                    break;

                default:
                    throw new CompilerException("unsupported operator " + token.getText(), token);
            }
        }
    }

    protected Expression parseAtom() {
        Token token = peek();
        if (token == null) {
            throw new CompilerException("expecting operand", tokens.get(tokens.size() - 1));
        }

        if (unary.contains(token.getText())) {
            token = next();
            switch (token.getText().toUpperCase()) {
                case "!":
                    return new Not(parseAtom());
                case "not":
                    return new LogicalNot(parseAtom());
                case "+":
                    return parseAtom();
                case "-":
                    return new Negative(parseAtom());
                default:
                    throw new CompilerException("invalid unary operator " + token.getText(), token);
            }
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
            if ("defined".equals(token.getText())) {
                token = next();
                if (token == null || !"(".equals(token.getText())) {
                    throw new CompilerException("expecting (", token == null ? tokens.get(tokens.size() - 1) : token);
                }
                token = next();
                if (token == null) {
                    throw new CompilerException("expecting identifier", token == null ? tokens.get(tokens.size() - 1) : token);
                }
                String identifier = token.getText();
                token = next();
                if (token == null || !")".equals(token.getText())) {
                    throw new CompilerException("expecting )", token == null ? tokens.get(tokens.size() - 1) : token);
                }
                return new Defined(identifier, context);
            }
            else if (token.type == Token.NUMBER) {
                return new NumberLiteral(token.getText());
            }
            else if (token.type == Token.STRING) {
                String s = token.getText().substring(1);
                return new CharacterLiteral(s.substring(0, s.length() - 1));
            }
            return new Identifier(token.getText(), context);
        }

        throw new CompilerException("unexpected " + token.getText(), token);
    }

    protected Token peek() {
        if (index < tokens.size()) {
            return tokens.get(index);
        }
        return null;
    }

    protected Token next() {
        if (index < tokens.size()) {
            return tokens.get(index++);
        }
        return null;
    }

    public List<Token> getTokens() {
        return tokens;
    }

}
