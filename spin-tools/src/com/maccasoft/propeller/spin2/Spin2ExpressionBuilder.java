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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Abs;
import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.Addbits;
import com.maccasoft.propeller.expressions.Addpins;
import com.maccasoft.propeller.expressions.AddpinsRange;
import com.maccasoft.propeller.expressions.And;
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.Compare;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Decod;
import com.maccasoft.propeller.expressions.Defined;
import com.maccasoft.propeller.expressions.Divide;
import com.maccasoft.propeller.expressions.Encod;
import com.maccasoft.propeller.expressions.Equals;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Frac;
import com.maccasoft.propeller.expressions.GreaterOrEquals;
import com.maccasoft.propeller.expressions.GreaterOrEqualsUnsigned;
import com.maccasoft.propeller.expressions.GreaterThan;
import com.maccasoft.propeller.expressions.GreaterThanUnsigned;
import com.maccasoft.propeller.expressions.Group;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.IfElse;
import com.maccasoft.propeller.expressions.LessOrEquals;
import com.maccasoft.propeller.expressions.LessOrEqualsUnsigned;
import com.maccasoft.propeller.expressions.LessThan;
import com.maccasoft.propeller.expressions.LessThanUnsigned;
import com.maccasoft.propeller.expressions.LimitMax;
import com.maccasoft.propeller.expressions.LimitMin;
import com.maccasoft.propeller.expressions.LogicalAnd;
import com.maccasoft.propeller.expressions.LogicalNot;
import com.maccasoft.propeller.expressions.LogicalOr;
import com.maccasoft.propeller.expressions.LogicalXor;
import com.maccasoft.propeller.expressions.Modulo;
import com.maccasoft.propeller.expressions.Multiply;
import com.maccasoft.propeller.expressions.Nan;
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
import com.maccasoft.propeller.expressions.Sca;
import com.maccasoft.propeller.expressions.Scas;
import com.maccasoft.propeller.expressions.Scl;
import com.maccasoft.propeller.expressions.ShiftLeft;
import com.maccasoft.propeller.expressions.ShiftRight;
import com.maccasoft.propeller.expressions.Signx;
import com.maccasoft.propeller.expressions.Sqrt;
import com.maccasoft.propeller.expressions.Subtract;
import com.maccasoft.propeller.expressions.Trunc;
import com.maccasoft.propeller.expressions.Type;
import com.maccasoft.propeller.expressions.UnsignedDivide;
import com.maccasoft.propeller.expressions.UnsignedModulo;
import com.maccasoft.propeller.expressions.Xor;
import com.maccasoft.propeller.expressions.Zerox;
import com.maccasoft.propeller.model.Token;

public class Spin2ExpressionBuilder {

    static Map<String, Integer> precedence = new HashMap<String, Integer>();
    static {
        precedence.put(">>", 14);
        precedence.put("<<", 14);
        precedence.put("SAR", 14);
        precedence.put("ROR", 14);
        precedence.put("ROL", 14);
        precedence.put("REV", 14);
        precedence.put("ZEROX", 14);
        precedence.put("SIGNX", 14);

        precedence.put("&", 13);
        precedence.put("^", 12);
        precedence.put("|", 11);

        precedence.put("*", 10);
        precedence.put("/", 10);
        precedence.put("+/", 10);
        precedence.put("//", 10);
        precedence.put("+//", 10);
        precedence.put("SCA", 10);
        precedence.put("SCAS", 10);
        precedence.put("FRAC", 10);

        precedence.put("*.", 10);
        precedence.put("/.", 10);

        precedence.put("+", 9);
        precedence.put("-", 9);

        precedence.put("+.", 9);
        precedence.put("-.", 9);

        precedence.put("#>", 8);
        precedence.put("<#", 8);

        precedence.put("ADDBITS", 7);
        precedence.put("ADDPINS", 7);

        precedence.put("<", 6);
        precedence.put("+<", 6);
        precedence.put("<=", 6);
        precedence.put("+<=", 6);
        precedence.put("==", 6);
        precedence.put("<>", 6);
        precedence.put(">=", 6);
        precedence.put("+>=", 6);
        precedence.put(">", 6);
        precedence.put("+>", 6);
        precedence.put("<=>", 6);

        precedence.put("<.", 6);
        precedence.put("<=.", 6);
        precedence.put("<>.", 6);
        precedence.put("==.", 6);
        precedence.put(">=.", 6);
        precedence.put(">.", 6);

        precedence.put("&&", 5);
        precedence.put("AND", 5);
        precedence.put("^^", 5);
        precedence.put("XOR", 5);
        precedence.put("||", 5);
        precedence.put("OR", 5);

        precedence.put("..", 4);

        precedence.put(":", 3);
        precedence.put("?", 2);
    }

    static Set<String> unary = new HashSet<String>();
    static {
        unary.add("+");
        unary.add("-");
        unary.add("!!");
        unary.add("NOT");
        unary.add("!");
        unary.add("ABS");
        unary.add("ENCOD");
        unary.add("DECOD");
        unary.add("BMASK");
        unary.add("ONES");
        unary.add("SQRT");
        unary.add("QLOG");
        unary.add("QEXP");
    }

    static Set<String> postEffect = new HashSet<String>();
    static {
    }

    static Map<String, Expression> constants = new HashMap<>();
    static {
        constants.put("IJMP3", new NumberLiteral(0x1F0, 16));
        constants.put("IRET3", new NumberLiteral(0x1F1, 16));
        constants.put("IJMP2", new NumberLiteral(0x1F2, 16));
        constants.put("IRET2", new NumberLiteral(0x1F3, 16));
        constants.put("IJMP1", new NumberLiteral(0x1F4, 16));
        constants.put("IRET1", new NumberLiteral(0x1F5, 16));
        constants.put("PA", new NumberLiteral(0x1F6, 16));
        constants.put("PB", new NumberLiteral(0x1F7, 16));
        constants.put("PTRA", new NumberLiteral(0x1F8, 16));
        constants.put("PTRB", new NumberLiteral(0x1F9, 16));
        constants.put("DIRA", new NumberLiteral(0x1FA, 16));
        constants.put("DIRB", new NumberLiteral(0x1FB, 16));
        constants.put("OUTA", new NumberLiteral(0x1FC, 16));
        constants.put("OUTB", new NumberLiteral(0x1FD, 16));
        constants.put("INA", new NumberLiteral(0x1FE, 16));
        constants.put("INB", new NumberLiteral(0x1FF, 16));

        constants.put("PR0", new NumberLiteral(0x1D8, 16));
        constants.put("PR1", new NumberLiteral(0x1D9, 16));
        constants.put("PR2", new NumberLiteral(0x1DA, 16));
        constants.put("PR3", new NumberLiteral(0x1DB, 16));
        constants.put("PR4", new NumberLiteral(0x1DC, 16));
        constants.put("PR5", new NumberLiteral(0x1DD, 16));
        constants.put("PR6", new NumberLiteral(0x1DE, 16));
        constants.put("PR7", new NumberLiteral(0x1DF, 16));
    }

    Context context;
    boolean allowRegisters;
    boolean ignoreMissing;
    List<Token> tokens = new ArrayList<Token>();

    int index;
    Set<String> dependencies = new HashSet<>();

    public Spin2ExpressionBuilder(Context context) {
        this(context, false, new ArrayList<>());
    }

    public Spin2ExpressionBuilder(Context context, boolean allowRegisters) {
        this(context, allowRegisters, new ArrayList<>());
    }

    public Spin2ExpressionBuilder(Context context, List<Token> tokens) {
        this(context, false, tokens);
    }

    public Spin2ExpressionBuilder(Context context, boolean allowRegisters, List<Token> tokens) {
        this.context = context;
        this.allowRegisters = allowRegisters;

        tokens.iterator().forEachRemaining((t) -> {
            addToken(t);
        });
    }

    public void setIgnoreMissing(boolean ignoreMissing) {
        this.ignoreMissing = ignoreMissing;
    }

    public void addToken(Token token) {
        if (token.type == Token.KEYWORD) {
            List<Token> l = context.getDefinition(token.getText());
            if (l != null && l.size() != 0) {
                if (dependencies.contains(token.getText())) {
                    throw new CompilerException("circular dependency", token);
                }
                dependencies.add(token.getText());
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

    public int getTokenCount() {
        return tokens.size();
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
                case "SAR":
                    left = new Sar(left, right);
                    break;
                case "ROR":
                    left = new Ror(left, right);
                    break;
                case "ROL":
                    left = new Rol(left, right);
                    break;
                case "REV":
                    left = new Rev(left, right);
                    break;
                case "SIGNX":
                    left = new Signx(left, right);
                    break;
                case "ZEROX":
                    left = new Zerox(left, right);
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
                case "*.":
                    left = new Multiply(left, right);
                    break;
                case "**":
                    left = new Scl(left, right);
                    break;
                case "/":
                case "/.":
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
                case "FRAC":
                    left = new Frac(left, right);
                    break;

                case "+":
                case "+.":
                    left = new Add(left, right);
                    break;
                case "-":
                case "-.":
                    left = new Subtract(left, right);
                    break;

                case "#>":
                    left = new LimitMin(left, right);
                    break;
                case "<#":
                    left = new LimitMax(left, right);
                    break;

                case "ADDBITS":
                    left = new Addbits(left, right);
                    break;
                case "ADDPINS":
                    left = new Addpins(left, right);
                    break;
                case "..":
                    left = new AddpinsRange(left, right);
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
                case "<.":
                    left = new LessThan(left, right);
                    break;
                case "<=":
                case "<=.":
                    left = new LessOrEquals(left, right);
                    break;
                case "==":
                case "==.":
                    left = new Equals(left, right);
                    break;
                case "<>":
                case "<>.":
                    left = new NotEquals(left, right);
                    break;
                case ">=":
                case ">=.":
                    left = new GreaterOrEquals(left, right);
                    break;
                case ">":
                case ">.":
                    left = new GreaterThan(left, right);
                    break;

                case "+<":
                    left = new LessThanUnsigned(left, right);
                    break;
                case "+<=":
                    left = new LessOrEqualsUnsigned(left, right);
                    break;
                case "+>=":
                    left = new GreaterOrEqualsUnsigned(left, right);
                    break;
                case "+>":
                    left = new GreaterThanUnsigned(left, right);
                    break;

                case "<=>":
                    left = new Compare(left, right);
                    break;

                case "?":
                    if (!(right instanceof IfElse)) {
                        throw new CompilerException("unsupported operator " + token.getText(), token);
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

        if (unary.contains(token.getText().toUpperCase())) {
            token = next();
            switch (token.getText().toUpperCase()) {
                case "!":
                    return new Not(parseAtom());
                case "!!":
                case "NOT":
                    return new LogicalNot(parseAtom());
                case "+":
                    return parseAtom();
                case "-":
                    return new Negative(parseAtom());
                case "ENCOD":
                    return new Encod(parseAtom());
                case "DECOD":
                    return new Decod(parseAtom());
                default:
                    throw new CompilerException("invalid unary operator " + token.getText(), token);
            }
        }

        if ("BYTE".equalsIgnoreCase(token.getText()) || "WORD".equalsIgnoreCase(token.getText()) || "LONG".equalsIgnoreCase(token.getText())) {
            token = next();
            return new Type(token.getText(), parseLevel(parseAtom(), 0));
        }
        if ("FVAR".equalsIgnoreCase(token.getText()) || "FVARS".equalsIgnoreCase(token.getText())) {
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

            return compilePseudoFunction(token);
        }

        throw new CompilerException("unexpected " + token.getText(), token);
    }

    protected Expression compilePseudoFunction(Token token) {

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
            case "SQRT":
            case "FSQRT": {
                token = next();
                if (token == null || !"(".equals(token.getText())) {
                    throw new CompilerException("expecting (", token == null ? tokens.get(tokens.size() - 1) : token);
                }
                Expression expression = new Sqrt(parseLevel(parseAtom(), 0));
                token = next();
                if (token == null || !")".equals(token.getText())) {
                    throw new CompilerException("expecting )", token == null ? tokens.get(tokens.size() - 1) : token);
                }
                return expression;
            }
            case "ABS":
            case "FABS": {
                token = next();
                if (token == null || !"(".equals(token.getText())) {
                    throw new CompilerException("expecting (", token == null ? tokens.get(tokens.size() - 1) : token);
                }
                Expression expression = new Abs(parseLevel(parseAtom(), 0));
                token = next();
                if (token == null || !")".equals(token.getText())) {
                    throw new CompilerException("expecting )", token == null ? tokens.get(tokens.size() - 1) : token);
                }
                return expression;
            }
            case "NAN": {
                token = next();
                if (token == null || !"(".equals(token.getText())) {
                    throw new CompilerException("expecting (", token == null ? tokens.get(tokens.size() - 1) : token);
                }
                Expression expression = new Nan(parseLevel(parseAtom(), 0));
                token = next();
                if (token == null || !")".equals(token.getText())) {
                    throw new CompilerException("expecting )", token == null ? tokens.get(tokens.size() - 1) : token);
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
                    String s = token.getText().substring(1);
                    return new CharacterLiteral(s.substring(0, s.length() - 1));
                }
                return new Identifier(token.getText(), context, ignoreMissing ? Long.valueOf(0) : null);
        }
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

}
