/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.io.ByteArrayOutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin1.bytecode.Constant;

public class Spin1ExpressionBuilder {

    static final int LEFT_TO_RIGHT = 0;
    static final int RIGHT_TO_LEFT = 1;

    static Map<String, Integer> operatorPrecedence = new HashMap<String, Integer>();
    static {
        operatorPrecedence.put("(", -2);
        operatorPrecedence.put(")", 99);

        operatorPrecedence.put("[", -1);
        operatorPrecedence.put("]", 99);

        operatorPrecedence.put("^^", 1);
        operatorPrecedence.put("||", 1);
        operatorPrecedence.put("|<", 1);
        operatorPrecedence.put(">|", 1);
        operatorPrecedence.put("!", 1);

        operatorPrecedence.put("<-", 2);
        operatorPrecedence.put("->", 2);
        operatorPrecedence.put("<<", 2);
        operatorPrecedence.put(">>", 2);
        operatorPrecedence.put("~>", 2);
        operatorPrecedence.put("><", 2);

        operatorPrecedence.put("&", 3);

        operatorPrecedence.put("|", 4);
        operatorPrecedence.put("^", 4);

        operatorPrecedence.put("*", 5);
        operatorPrecedence.put("**", 5);
        operatorPrecedence.put("/", 5);
        operatorPrecedence.put("//", 5);

        operatorPrecedence.put("+", 6);
        operatorPrecedence.put("-", 6);

        operatorPrecedence.put("#>", 7);
        operatorPrecedence.put("<#", 7);

        operatorPrecedence.put("<", 8);
        operatorPrecedence.put(">", 8);
        operatorPrecedence.put("<>", 8);
        operatorPrecedence.put("==", 8);
        operatorPrecedence.put("=<", 8);
        operatorPrecedence.put("=>", 8);

        operatorPrecedence.put("NOT", 9);

        operatorPrecedence.put("AND", 10);

        operatorPrecedence.put("OR", 11);

        operatorPrecedence.put(":=", 12);
        operatorPrecedence.put("^^=", 12);
        operatorPrecedence.put("||=", 12);
        operatorPrecedence.put("|<=", 12);
        operatorPrecedence.put(">|=", 12);
        operatorPrecedence.put("!=", 12);
        operatorPrecedence.put("<-=", 12);
        operatorPrecedence.put("->=", 12);
        operatorPrecedence.put("<<=", 12);
        operatorPrecedence.put(">>=", 12);
        operatorPrecedence.put("~>=", 12);
        operatorPrecedence.put("><=", 12);
        operatorPrecedence.put("&=", 12);
        operatorPrecedence.put("|=", 12);
        operatorPrecedence.put("^=", 12);
        operatorPrecedence.put("*=", 12);
        operatorPrecedence.put("**=", 12);
        operatorPrecedence.put("/=", 12);
        operatorPrecedence.put("//=", 12);
        operatorPrecedence.put("+=", 12);
        operatorPrecedence.put("-=", 12);
        operatorPrecedence.put("#>=", 12);
        operatorPrecedence.put("<#=", 12);
        operatorPrecedence.put("<=", 12);
        operatorPrecedence.put(">=", 12);
        operatorPrecedence.put("<>=", 12);
        operatorPrecedence.put("===", 12);
        operatorPrecedence.put("=<=", 12);
        operatorPrecedence.put("=>=", 12);
        operatorPrecedence.put("NOT=", 12);
        operatorPrecedence.put("AND=", 12);
        operatorPrecedence.put("OR=", 12);

        operatorPrecedence.put(",", 98);
    }

    public static class Spin1ExpressionNode extends Node {

        public Spin1ExpressionNode() {
        }

        public Spin1ExpressionNode(Token token) {
            tokens.add(token);
        }

        public Spin1ExpressionNode(Node parent) {
            super(parent);
        }

        public boolean isConstant() {
            return true;
        }

        public byte[] getObjectCode() {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                for (Node node : childs) {
                    os.write(((Spin1ExpressionNode) node).getObjectCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return os.toByteArray();
        }

    }

    public static class Spin1NumberLiteralNode extends Spin1ExpressionNode {

        private final Number value;
        private final int base;
        private String text;

        public Spin1NumberLiteralNode(Token token) {
            tokens.add(token);

            String value = token.getText();
            if (value.startsWith("%%")) {
                value = value.substring(2);
                this.base = 4;
            }
            else if (value.startsWith("%")) {
                value = value.substring(1);
                this.base = 2;
            }
            else if (value.startsWith("$")) {
                value = value.substring(1);
                this.base = 16;
            }
            else {
                this.base = 10;
            }
            if (value.contains(".")) {
                this.value = Double.parseDouble(value.replace("_", ""));
            }
            else {
                this.value = Long.parseLong(value.replace("_", ""), this.base);
            }
            this.text = value;
        }

        @Override
        public byte[] getObjectCode() {
            return Constant.compileConstant(value.intValue());
        }

        @Override
        public String toString() {
            if (text == null) {
                switch (base) {
                    case 2:
                        text = "%" + Long.toBinaryString(value.longValue());
                        break;
                    case 4:
                        text = "%%" + Long.toString(value.longValue(), 4);
                        break;
                    case 16:
                        text = "$" + Long.toHexString(value.longValue());
                        break;
                    default:
                        text = value.toString();
                        break;
                }
            }
            return text;
        }

    }

    public static class Spin1ExpressionOperatorNode extends Spin1ExpressionNode {

        static Map<String, Integer> mathOp = new HashMap<String, Integer>();
        static {
            mathOp.put("->", 0b111_00000); //  rotate right
            mathOp.put("<-", 0b111_00001); //  rotate left
            mathOp.put(">>", 0b111_00010); //  shift right
            mathOp.put("<<", 0b111_00011); //  shift left
            mathOp.put("|>", 0b111_00100); //  limit minimum (signed)
            mathOp.put("<|", 0b111_00101); //  limit maximum (signed)
            mathOp.put("-", 0b111_00110); //   negate
            mathOp.put("!", 0b111_00111); //   bitwise not
            mathOp.put("&", 0b111_01000); //   bitwise and
            mathOp.put("||", 0b111_01001); //  absolute
            mathOp.put("|", 0b111_01010); //   bitwise or
            mathOp.put("^", 0b111_01011); //   bitwise xor
            mathOp.put("+", 0b111_01100); //   add
            mathOp.put("-", 0b111_01101); //   subtract
            mathOp.put("~>", 0b111_01110); //  shift arithmetic right
            mathOp.put("><", 0b111_01111); //  reverse bits
            mathOp.put("AND", 0b111_10000); // boolean and
            mathOp.put(">|", 0b111_10001); //  encode (0-32)
            mathOp.put("OR", 0b111_10010); //  boolean or
            mathOp.put("|<", 0b111_10011); //  decode
            mathOp.put("*", 0b111_10100); //   multiply, return lower half (signed)
            mathOp.put("**", 0b111_10101); //  multiply, return upper half (signed)
            mathOp.put("/", 0b111_10110); //   divide, return quotient (signed)
            mathOp.put("//", 0b111_10111); //  divide, return remainder (signed)
            mathOp.put("^^", 0b111_11000); //  square root
            mathOp.put("<", 0b111_11001); //   test below (signed)
            mathOp.put(">", 0b111_11010); //   test above (signed)
            mathOp.put("<>", 0b111_11011); //  test not equal
            mathOp.put("==", 0b111_11100); //  test equal
            mathOp.put("=<", 0b111_11101); //  test below or equal (signed)
            mathOp.put("=>", 0b111_11110); //  test above or equal (signed)
            mathOp.put("NOT", 0b111_11111); // boolean not
        }

        public Spin1ExpressionOperatorNode(Token token) {
            super(token);
        }

        @Override
        public byte[] getObjectCode() {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                for (Node node : childs) {
                    os.write(((Spin1ExpressionNode) node).getObjectCode());
                }
                Integer op = mathOp.get(tokens.get(0).getText());
                if (op != null) {
                    os.write(op.intValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return os.toByteArray();
        }

        @Override
        public String toString() {
            return tokens.get(0).getText();
        }

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

        public abstract Spin1ExpressionNode evaluate();

        @Override
        public String toString() {
            return token.getText();
        }
    }

    private class UnaryOperator extends Operator {

        public UnaryOperator(Token token) {
            this.token = token;
            this.precedence = operatorPrecedence.get(token.getText());
            this.associativity = LEFT_TO_RIGHT;
        }

        @Override
        public Spin1ExpressionNode evaluate() {
            Spin1ExpressionOperatorNode result = new Spin1ExpressionOperatorNode(token);
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
        public Spin1ExpressionNode evaluate() {
            Spin1ExpressionNode right = operands.pop();
            Spin1ExpressionOperatorNode result = new Spin1ExpressionOperatorNode(token);
            result.addChild(operands.pop());
            result.addChild(right);
            return result;
        }

    }

    private class FunctionOperator extends Operator {

        Token name;

        public FunctionOperator(Token name, Token token) {
            this.name = name;
            this.token = token;
            this.precedence = operatorPrecedence.get(token.getText());
            this.associativity = LEFT_TO_RIGHT;
        }

        @Override
        public Spin1ExpressionNode evaluate() {
            Spin1ExpressionNode right = operands.pop();
            Spin1ExpressionNode result = new Spin1ExpressionNode();
            result.addToken(name);
            result.addToken(token);
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
        public Spin1ExpressionNode evaluate() {
            Spin1ExpressionNode node0 = operands.pop();
            Spin1ExpressionNode node1 = operands.pop();

            if (",".equals(node0.getToken(0).getText())) {
                node0.addChild(node1);
                return node0;
            }

            Spin1ExpressionNode result = new Spin1ExpressionNode();
            result.addToken(token);
            result.addChild(node1);
            result.addChild(node0);
            return result;
        }

    }

    int state;
    Deque<Spin1ExpressionNode> operands = new ArrayDeque<Spin1ExpressionNode>();
    Deque<Operator> operators = new ArrayDeque<Operator>();

    public final Operator SENTINEL = new Operator(99, RIGHT_TO_LEFT) {

        @Override
        public Spin1ExpressionNode evaluate() {
            throw new RuntimeException("Can not evaluate sentinel.");
        }

        @Override
        public String toString() {
            return "SENTINEL";
        };
    };

    public Spin1ExpressionBuilder() {
        operators.push(SENTINEL);
    }

    public Spin1ExpressionNode getExpression(List<Token> tokens) {
        int state = 0;

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            switch (state) {
                case 0:
                    if (token.type == Token.OPERATOR) {
                        addOperator(new UnaryOperator(token));
                        break;
                    }
                    // fall through
                case 1:
                    if (token.type == Token.OPERATOR || operatorPrecedence.containsKey(token.getText().toUpperCase())) {
                        addOperatorToken(token);
                        break;
                    }
                    if (i + 1 < tokens.size()) {
                        Token next = tokens.get(i + 1);
                        if (next.type != Token.EOF && next.type != Token.NL) {
                            if ("(".equals(next.getText())) {
                                addFunctionOperatorToken(token, next);
                                i++;
                                state = 0;
                                break;
                            }
                        }
                    }
                    addValueToken(token);
                    state = 2;
                    break;
                case 2:
                    addOperatorToken(token);
                    state = 1;
                    break;
            }
        }

        return getExpression();
    }

    public void addValueToken(Token token) {
        if (token.type == Token.NUMBER) {
            operands.push(new Spin1NumberLiteralNode(token));
        }
        else {
            operands.push(new Spin1ExpressionNode(token));
        }
    }

    public void addOperatorToken(Token token) {
        Operator operator;
        if (",".equals(token.getText())) {
            operator = new SequenceOperator(token);
        }
        else if ("(".equals(token.getText())) {
            operator = new UnaryOperator(token);
        }
        else if ("[".equals(token.getText())) {
            operator = new UnaryOperator(token);
        }
        else {
            operator = new BinaryOperator(token);
        }
        addOperator(operator);
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

    public Spin1ExpressionNode getExpression() {
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

        return operands.pop();
    }

    public static void main(String[] args) {
        try {
            Spin1ExpressionNode node = compile("1 + 2 * 3");
            print(node, 0);
            byte[] code = node.getObjectCode();
            for (int i = 0; i < code.length; i++) {
                System.out.println(String.format(" %02X", code[i]));
            }

            node = compile("(1 + 2) * 3");
            print(node, 0);
            code = node.getObjectCode();
            for (int i = 0; i < code.length; i++) {
                System.out.println(String.format(" %02X", code[i]));
            }

            node = compile("line(ship_x - (last_cos ~> 15), ship_y - (last_sin ~> 15) )");
            print(node, 0);
            code = node.getObjectCode();
            for (int i = 0; i < code.length; i++) {
                System.out.println(String.format(" %02X", code[i]));
            }

            //print(compile("(1 + 2) * 3"), 0);
            //print(compile("    a := CNT"), 0);
            //print(compile("        waitcnt(a += 3_000_000)"), 0);
            //print(compile("Player(@Wav,10,11)"), 0);
            //print(compile("gr.line(ship_x - (last_cos ~> 15), ship_y - (last_sin ~> 15) )"), 0);
            //print(compile("mouse.button(THRUST_BUTTON_ID) and (Rand & $01)"), 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Spin1ExpressionNode compile(String text) {
        List<Token> tokens = new ArrayList<Token>();

        Spin1TokenStream stream = new Spin1TokenStream(text);
        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF || token.type == Token.NL) {
                break;
            }
            tokens.add(token);
        }

        Spin1ExpressionBuilder builder = new Spin1ExpressionBuilder();
        return builder.getExpression(tokens);
    }

    static void print(Node node, int indent) {
        if (indent != 0) {
            for (int i = 1; i < indent; i++) {
                System.out.print("|    ");
            }
            System.out.print("+--- ");
        }

        System.out.print(node.getClass().getSimpleName());
        //for (Token token : node.tokens) {
        //    System.out.print(" [" + token.getText().replaceAll("\n", "\\n") + "]");
        //}
        System.out.println(" [" + node.getText().replaceAll("\n", "\\\\n") + "]");

        for (Node child : node.getChilds()) {
            print(child, indent + 1);
        }
    }

}
