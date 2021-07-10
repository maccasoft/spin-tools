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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import com.maccasoft.propeller.model.Token;

public class Spin2TreeBuilder {

    static final int LEFT_TO_RIGHT = 0;
    static final int RIGHT_TO_LEFT = 1;

    static Map<String, Integer> operatorPrecedence = new HashMap<String, Integer>();
    static {
        operatorPrecedence.put("(", -2);
        operatorPrecedence.put(")", 99);

        operatorPrecedence.put("[", -1);
        operatorPrecedence.put("]", 99);

        operatorPrecedence.put(">>", 3);
        operatorPrecedence.put("<<", 3);
        operatorPrecedence.put("SAR", 3);
        operatorPrecedence.put("ROR", 3);
        operatorPrecedence.put("ROL", 3);
        operatorPrecedence.put("REV", 3);
        operatorPrecedence.put("ZEROX", 3);
        operatorPrecedence.put("SIGNX", 3);

        operatorPrecedence.put("&", 4);
        operatorPrecedence.put("^", 5);
        operatorPrecedence.put("|", 6);

        operatorPrecedence.put("*", 7);
        operatorPrecedence.put("/", 7);
        operatorPrecedence.put("+/", 7);
        operatorPrecedence.put("//", 7);
        operatorPrecedence.put("+//", 7);
        operatorPrecedence.put("SCA", 7);
        operatorPrecedence.put("SCAS", 7);
        operatorPrecedence.put("FRAC", 7);

        operatorPrecedence.put("+", 8);
        operatorPrecedence.put("-", 8);

        operatorPrecedence.put("#>", 9);
        operatorPrecedence.put("<#", 9);

        operatorPrecedence.put("ADDBITS", 10);
        operatorPrecedence.put("ADDPINS", 10);

        operatorPrecedence.put("<", 11);
        operatorPrecedence.put("+<", 11);
        operatorPrecedence.put("<=", 11);
        operatorPrecedence.put("+<=", 11);
        operatorPrecedence.put("==", 11);
        operatorPrecedence.put("<>", 11);
        operatorPrecedence.put(">=", 11);
        operatorPrecedence.put("+>=", 11);
        operatorPrecedence.put(">", 11);
        operatorPrecedence.put("+>", 11);
        operatorPrecedence.put("<=>", 11);

        operatorPrecedence.put("&&", 12);
        operatorPrecedence.put("AND", 12);
        operatorPrecedence.put("^^", 12);
        operatorPrecedence.put("XOR", 12);
        operatorPrecedence.put("||", 12);
        operatorPrecedence.put("OR", 12);

        operatorPrecedence.put("..", 16);

        operatorPrecedence.put(":=", 17);

        operatorPrecedence.put(">>=", 17);
        operatorPrecedence.put("<<=", 17);
        operatorPrecedence.put("SAR=", 17);
        operatorPrecedence.put("ROR=", 17);
        operatorPrecedence.put("ROL=", 17);
        operatorPrecedence.put("REV=", 17);
        operatorPrecedence.put("ZEROX=", 17);
        operatorPrecedence.put("SIGNX=", 17);

        operatorPrecedence.put("&=", 17);
        operatorPrecedence.put("^=", 17);
        operatorPrecedence.put("|=", 17);

        operatorPrecedence.put("*=", 17);
        operatorPrecedence.put("/=", 17);
        operatorPrecedence.put("+/=", 17);
        operatorPrecedence.put("//=", 17);
        operatorPrecedence.put("+//=", 17);
        operatorPrecedence.put("SCA=", 17);
        operatorPrecedence.put("SCAS=", 17);
        operatorPrecedence.put("FRAC=", 17);

        operatorPrecedence.put("+=", 17);
        operatorPrecedence.put("-=", 17);

        operatorPrecedence.put("#>=", 17);
        operatorPrecedence.put("<#=", 17);

        operatorPrecedence.put("ADDBITS=", 17);
        operatorPrecedence.put("ADDPINS=", 17);

        operatorPrecedence.put("<=", 17);
        operatorPrecedence.put("+<=", 17);
        operatorPrecedence.put("<==", 17);
        operatorPrecedence.put("+<==", 17);
        operatorPrecedence.put("===", 17);
        operatorPrecedence.put("<>=", 17);
        operatorPrecedence.put(">==", 17);
        operatorPrecedence.put("+>==", 17);
        operatorPrecedence.put(">=", 17);
        operatorPrecedence.put("+>=", 17);
        operatorPrecedence.put("<=>=", 17);

        operatorPrecedence.put("&&=", 17);
        operatorPrecedence.put("AND=", 17);
        operatorPrecedence.put("^^=", 17);
        operatorPrecedence.put("XOR=", 17);
        operatorPrecedence.put("||=", 17);
        operatorPrecedence.put("OR=", 17);

        operatorPrecedence.put(",", 98);
    }

    private static abstract class Operator {

        public Token token;
        protected int precedence;
        protected int associativity;

        protected Operator() {

        }

        public Operator(int precedence, int associativity) {
            this.precedence = precedence;
            this.associativity = associativity;
        }

        public boolean yieldsTo(Operator other) {
            if (associativity == LEFT_TO_RIGHT) {
                return precedence > other.precedence;
            }
            else {
                return precedence >= other.precedence;
            }
        }

        public abstract Spin2StatementNode evaluate();

        @Override
        public String toString() {
            return token.getText();
        }
    }

    private class UnaryOperator extends Operator {

        public UnaryOperator(Token token) {
            this.token = token;
            this.precedence = operatorPrecedence.get(token.getText());
            this.associativity = RIGHT_TO_LEFT;
        }

        @Override
        public Spin2StatementNode evaluate() {
            Spin2StatementNode result = new Spin2StatementNode(token);
            result.addChild(operands.pop());
            return result;
        }

    }

    private class GroupOperator extends Operator {

        public GroupOperator(Token token) {
            this.token = token;
            this.precedence = operatorPrecedence.get(token.getText());
            this.associativity = LEFT_TO_RIGHT;
        }

        @Override
        public Spin2StatementNode evaluate() {
            Spin2StatementNode result = new Spin2StatementNode(token);
            result.addChild(operands.pop());
            return result;
        }

    }

    private class BinaryOperator extends Operator {

        public BinaryOperator(Token token) {
            this.token = token;
            this.precedence = operatorPrecedence.get(token.getText().toUpperCase());
            this.associativity = LEFT_TO_RIGHT;
        }

        @Override
        public Spin2StatementNode evaluate() {
            Spin2StatementNode right = operands.pop();
            Spin2StatementNode result = new Spin2StatementNode(token);
            result.addChild(operands.pop());
            result.addChild(right);
            return result;
        }

    }

    private class FunctionOperator extends Operator {

        Token name;

        public FunctionOperator(Token name, Token token) {
            this.name = name;
            this.name.type = Token.FUNCTION;
            this.token = token;
            this.precedence = operatorPrecedence.get(token.getText());
            this.associativity = LEFT_TO_RIGHT;
        }

        @Override
        public Spin2StatementNode evaluate() {
            Spin2StatementNode right = operands.pop();
            Spin2StatementNode result = new Spin2StatementNode(name);
            result.addChild(right);
            return result;
        }

    }

    private class SequenceOperator extends Operator {

        public SequenceOperator(Token token) {
            this.token = token;
            this.precedence = 98;
            this.associativity = RIGHT_TO_LEFT;
        }

        @Override
        public Spin2StatementNode evaluate() {
            Spin2StatementNode node0 = operands.pop();
            Spin2StatementNode node1 = operands.pop();

            if (",".equals(node0.getText())) {
                node0.addChild(0, node1);
                return node0;
            }

            Spin2StatementNode result = new Spin2StatementNode(token);
            result.addChild(node1);
            result.addChild(node0);
            return result;
        }

    }

    int state;
    Deque<Spin2StatementNode> operands = new ArrayDeque<Spin2StatementNode>();
    Deque<Operator> operators = new ArrayDeque<Operator>();

    public final Operator SENTINEL = new Operator(99, RIGHT_TO_LEFT) {

        @Override
        public Spin2StatementNode evaluate() {
            throw new RuntimeException("Can not evaluate sentinel.");
        }

        @Override
        public String toString() {
            return "SENTINEL";
        };
    };

    public Spin2TreeBuilder() {
        operators.push(SENTINEL);
    }

    public void addValueToken(Token token) {
        Spin2StatementNode operand = new Spin2StatementNode(token);
        operands.push(operand);
    }

    public void addOperatorToken(Token token) {
        Operator operator;
        if (",".equals(token.getText())) {
            operator = new SequenceOperator(token);
        }
        else if ("(".equals(token.getText())) {
            operator = new GroupOperator(token);
        }
        else {
            operator = new BinaryOperator(token);
        }
        addOperator(operator);
    }

    public void addUnaryOperator(Token token) {
        addOperator(new UnaryOperator(token));
    }

    public void addOperator(Operator operator) {
        evaluateNotYieldingTo(operator);

        if ("(".equals(operator.token.getText()) || "[".equals(operator.token.getText())) {
            operators.push(operator);
            operators.push(SENTINEL);
        }
        else if (")".equals(operator.token.getText()) || "]".equals(operator.token.getText())) {
            if (operators.pop() != SENTINEL) {
                throw new RuntimeException("Sentinel expected.");
            }
            if (")".equals(operator.token.getText()) && !"(".equals(operators.peek().token.getText())) {
                throw new RuntimeException("Group open expected.");
            }
            if ("]".equals(operator.token.getText()) && !"[".equals(operators.peek().token.getText())) {
                throw new RuntimeException("Index open expected.");
            }
        }
        else {
            operators.push(operator);
        }
    }

    public void addFunctionOperatorToken(Token name, Token token) {
        Operator operator = new FunctionOperator(name, token);
        addOperator(operator);
    }

    private void evaluateNotYieldingTo(Operator operator) {
        while (!operators.peek().yieldsTo(operator)) {
            operands.push(operators.pop().evaluate());
        }
    }

    public Spin2StatementNode getRoot() {
        if (operands.isEmpty() || operators.isEmpty()) {
            throw new RuntimeException("Operands / operators is empty: " + this);
        }

        // process remainder
        evaluateNotYieldingTo(SENTINEL);

        if (operators.size() > 1 && operators.peek() == SENTINEL) {
            throw new RuntimeException("Group close expected.");
        }
        if (operands.size() > 1 || operators.size() != 1) {
            throw new RuntimeException("Not all operands / operators were processed: " + this);
        }

        state = 0;

        return operands.pop();
    }

    public void addToken(Token token) {

        switch (state) {
            case 0:
                if (token.type != 0 && token.type != Token.NUMBER && token.type != Token.STRING) {
                    throw new RuntimeException("error: expecting identifier, got " + token.getText());
                }
                addValueToken(token);
                state = 1;
                break;
            case 1: {
                if ("[".equals(token.getText())) {
                    addOperatorToken(token);
                    state = 5;
                    break;
                }
                if ("(".equals(token.getText())) {
                    Spin2StatementNode node = operands.pop();
                    addOperator(new FunctionOperator(node.getToken(), token));
                    state = 4;
                    break;
                }
                Integer op = operatorPrecedence.get(token.getText().toUpperCase());
                if (op == null || op.intValue() != 17) {
                    throw new RuntimeException("error: expecting assignment operator, got " + token.getText());
                }
                addOperatorToken(token);
                state = 2;
                break;
            }
            case 2:
                if ("(".equals(token.getText()) || "]".equals(token.getText())) {
                    addOperatorToken(token);
                    break;
                }
                if (token.type != 0 && token.type != Token.NUMBER && token.type != Token.STRING) {
                    throw new RuntimeException("error: expecting constant or identifier, got " + token.getText());
                }
                addValueToken(token);
                state = 3;
                break;
            case 3: {
                if ("(".equals(token.getText())) {
                    Spin2StatementNode node = operands.pop();
                    addOperator(new FunctionOperator(node.getToken(), token));
                    state = 4;
                    break;
                }
                Integer op = operatorPrecedence.get(token.getText().toUpperCase());
                if (op == null) {
                    throw new RuntimeException("error: expecting operator, got " + token.getText());
                }
                addOperatorToken(token);
                if (")".equals(token.getText()) || "]".equals(token.getText())) {
                    break;
                }
                state = 2;
                break;
            }
            case 4:
                if (")".equals(token.getText())) {
                    operators.pop();
                    FunctionOperator operator = (FunctionOperator) operators.pop();
                    operands.push(new Spin2StatementNode(operator.name));
                    state = 3;
                    break;
                }
                if (token.type != 0 && token.type != Token.NUMBER && token.type != Token.STRING) {
                    throw new RuntimeException("error: expecting constant or identifier, got " + token.getText());
                }
                addValueToken(token);
                state = 3;
                break;
            case 5:
                if (token.type != 0 && token.type != Token.NUMBER && token.type != Token.STRING) {
                    throw new RuntimeException("error: expecting constant or identifier, got " + token.getText());
                }
                addValueToken(token);
                state = 6;
                break;
            case 6: {
                Integer op = operatorPrecedence.get(token.getText().toUpperCase());
                if (op == null) {
                    throw new RuntimeException("error: expecting operator, got " + token.getText());
                }
                addOperatorToken(token);
                if (")".equals(token.getText())) {
                    break;
                }
                if ("]".equals(token.getText())) {
                    state = 1;
                    break;
                }
                state = 2;
                break;
            }
        }
    }

    public void setState(int state) {
        this.state = state;
    }

    public static void main(String[] args) {
        Token[] tokens = new Token[] {
            //new Token(0, "a"), new Token(Token.OPERATOR, ":="), new Token(Token.NUMBER, "1"), new Token(Token.NL, "\n"),
            //new Token(0, "b"), new Token(Token.OPERATOR, ":="), new Token(Token.NUMBER, "2"), new Token(Token.NL, "\n"),
            //new Token(0, "a"), new Token(Token.OPERATOR, ":="), new Token(Token.OPERATOR, "("), new Token(0, "b"), new Token(Token.OPERATOR, "+"), new Token(0, "c"), new Token(Token.OPERATOR, ")"),
            //new Token(Token.OPERATOR, "*"), new Token(Token.NUMBER, "3"),
            //new Token(Token.NL, "\n"),
            //new Token(0, "waitcnt"), new Token(Token.OPERATOR, "("), new Token(Token.OPERATOR, ")"), new Token(Token.NL, "\n"),
            //new Token(0, "waitcnt"), new Token(Token.OPERATOR, "("), new Token(Token.NUMBER, "1"), new Token(Token.OPERATOR, ")"), new Token(Token.NL, "\n"),
            //new Token(0, "waitcnt"), new Token(Token.OPERATOR, "("), new Token(Token.NUMBER, "a"), new Token(Token.OPERATOR, "+="), new Token(Token.NUMBER, "1_000"), new Token(Token.OPERATOR, ")"),
            //new Token(Token.NL, "\n"),
            new Token(0, "b"), new Token(Token.OPERATOR, "["), new Token(Token.NUMBER, "1"), new Token(Token.OPERATOR, "]"), new Token(Token.OPERATOR, ":="), new Token(0, "a"),
            new Token(Token.NL, "\n"),
        };
        Spin2TreeBuilder builder = new Spin2TreeBuilder();
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].type == Token.NL) {
                Spin2StatementNode root = builder.getRoot();
                print(root, 0);
            }
            else {
                builder.addToken(tokens[i]);
            }
        }
    }

    static void print(Spin2StatementNode node, int indent) {
        if (indent != 0) {
            for (int i = 1; i < indent; i++) {
                System.out.print("|    ");
            }
            System.out.print("+--- ");
        }

        System.out.print(node.getClass().getSimpleName());
        System.out.print(" [" + node.getText().replaceAll("\n", "\\\\n") + "]");
        System.out.println();

        for (Spin2StatementNode child : node.getChilds()) {
            print(child, indent + 1);
        }
    }
}
