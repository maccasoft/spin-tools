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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.maccasoft.propeller.model.Token;

public class Spin1TreeBuilder {

    static int highestPrecedence = 1;
    static Map<String, Integer> precedence = new HashMap<String, Integer>();
    static {
        precedence.put(">>", highestPrecedence);
        precedence.put("<<", highestPrecedence);
        precedence.put("~>", highestPrecedence);
        precedence.put("->", highestPrecedence);
        precedence.put("<-", highestPrecedence);
        precedence.put("><", highestPrecedence);
        highestPrecedence++;

        precedence.put("&", highestPrecedence);
        highestPrecedence++;
        precedence.put("^", highestPrecedence);
        precedence.put("|", highestPrecedence);
        highestPrecedence++;

        precedence.put("*", highestPrecedence);
        precedence.put("**", highestPrecedence);
        precedence.put("/", highestPrecedence);
        precedence.put("//", highestPrecedence);
        highestPrecedence++;

        precedence.put("+", highestPrecedence);
        precedence.put("-", highestPrecedence);
        highestPrecedence++;

        precedence.put("#>", highestPrecedence);
        precedence.put("<#", highestPrecedence);
        highestPrecedence++;

        precedence.put("<", highestPrecedence);
        precedence.put("=<", highestPrecedence);
        precedence.put("==", highestPrecedence);
        precedence.put("<>", highestPrecedence);
        precedence.put("=>", highestPrecedence);
        precedence.put(">", highestPrecedence);
        highestPrecedence++;

        precedence.put("AND", highestPrecedence);
        highestPrecedence++;
        precedence.put("OR", highestPrecedence);
        highestPrecedence++;

        precedence.put("..", highestPrecedence);
        highestPrecedence++;

        precedence.put(":", highestPrecedence);
        precedence.put("?", highestPrecedence);
        highestPrecedence++;

        precedence.put(":=", highestPrecedence);

        precedence.put(">>=", highestPrecedence);
        precedence.put("<<=", highestPrecedence);
        precedence.put("~>=", highestPrecedence);
        precedence.put("->=", highestPrecedence);
        precedence.put("<-=", highestPrecedence);
        precedence.put("><=", highestPrecedence);

        precedence.put("&=", highestPrecedence);
        precedence.put("^=", highestPrecedence);
        precedence.put("|=", highestPrecedence);

        precedence.put("*=", highestPrecedence);
        precedence.put("**=", highestPrecedence);
        precedence.put("/=", highestPrecedence);
        precedence.put("//=", highestPrecedence);

        precedence.put("+=", highestPrecedence);
        precedence.put("-=", highestPrecedence);

        precedence.put("#>=", highestPrecedence);
        precedence.put("<#=", highestPrecedence);

        precedence.put("<=", highestPrecedence);
        precedence.put("=<=", highestPrecedence);
        precedence.put("===", highestPrecedence);
        precedence.put("<>=", highestPrecedence);
        precedence.put("=>=", highestPrecedence);
        precedence.put(">=", highestPrecedence);

        precedence.put("AND=", highestPrecedence);
        precedence.put("OR=", highestPrecedence);
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
    }

    static Set<String> postEffect = new HashSet<String>();
    static {
        postEffect.add("?");
        postEffect.add("~");
        postEffect.add("++");
        postEffect.add("--");
        postEffect.add("~~");
    }

    int index;
    List<Token> tokens = new ArrayList<Token>();

    public void addToken(Token token) {
        tokens.add(token);
    }

    public Spin1StatementNode getRoot() {
        Spin1StatementNode left = parseLevel(highestPrecedence);

        Token token = peek();
        if (token == null) {
            return left;
        }

        Integer p = precedence.get(token.getText().toUpperCase());
        if (p != null && p.intValue() == highestPrecedence) {
            Spin1StatementNode node = new Spin1StatementNode(next());
            node.addChild(left);
            node.addChild(parseLevel(highestPrecedence));
            return node;
        }

        throw new RuntimeException("unexpected " + token.getText());
    }

    Spin1StatementNode parseLevel(int level) {
        Spin1StatementNode left = level == 0 ? parseAtom() : parseLevel(level - 1);

        Token token = peek();
        if (token == null) {
            return left;
        }

        Integer p = precedence.get(token.getText().toUpperCase());
        if (p != null && p.intValue() == level) {
            Spin1StatementNode node = new Spin1StatementNode(next());
            Spin1StatementNode right = level == 0 ? parseAtom() : parseLevel(level);
            node.addChild(left);
            node.addChild(right);
            return node;
        }

        return left;
    }

    Spin1StatementNode parseAtom() {
        Token token = peek();

        if (unary.contains(token.getText())) {
            Spin1StatementNode node = new Spin1StatementNode(next());
            node.addChild(parseAtom());
            return node;
        }

        if ("(".equals(token.getText())) {
            next();
            Spin1StatementNode node = parseLevel(highestPrecedence);
            token = next();
            if (token == null) {
                throw new RuntimeException("expecting closing parenthesis");
            }
            if (!")".equals(token.getText())) {
                throw new RuntimeException("expecting closing parenthesis, got " + token.getText());
            }
            return node;
        }

        if ("[".equals(token.getText())) {
            next();
            Spin1StatementNode node = parseLevel(highestPrecedence);
            token = next();
            if (token == null) {
                throw new RuntimeException("expecting closing parenthesis");
            }
            if (!"]".equals(token.getText())) {
                throw new RuntimeException("expecting closing parenthesis, got " + token.getText());
            }
            return node;
        }

        if (token.type == 0) {
            Spin1StatementNode node = new Spin1StatementNode(next());
            if ((token = peek()) != null) {
                if ("(".equals(token.getText())) {
                    next();
                    if (peek() != null && ")".equals(peek().getText())) {
                        next();
                        return node;
                    }
                    for (;;) {
                        Spin1StatementNode child = parseLevel(highestPrecedence);
                        if (node.getChildCount() == 1 && ":".equals(node.getChild(0).getText())) {
                            node.getChild(0).addChild(child);
                        }
                        else {
                            node.addChild(child);
                        }
                        token = next();
                        if (token == null) {
                            throw new RuntimeException("expecting closing parenthesis");
                        }
                        if (")".equals(token.getText())) {
                            return node;
                        }
                        if (!",".equals(token.getText()) && !":".equals(token.getText())) {
                            throw new RuntimeException("expecting closing parenthesis, got " + token.getText());
                        }
                    }
                }
                if ("[".equals(token.getText())) {
                    next();
                    node.addChild(parseLevel(highestPrecedence));
                    token = next();
                    if (token == null) {
                        throw new RuntimeException("expecting closing parenthesis");
                    }
                    if (!"]".equals(token.getText())) {
                        throw new RuntimeException("expecting closing parenthesis, got " + token.getText());
                    }

                    token = peek();
                    if (token == null) {
                        return node;
                    }
                    if ("[".equals(token.getText())) {
                        next();
                        node.addChild(parseLevel(highestPrecedence));
                        token = next();
                        if (token == null) {
                            throw new RuntimeException("expecting closing parenthesis");
                        }
                        if (!"]".equals(token.getText())) {
                            throw new RuntimeException("expecting closing parenthesis, got " + token.getText());
                        }
                    }
                }
                token = peek();
                if (token != null && postEffect.contains(token.getText())) {
                    node.addChild(new Spin1StatementNode(next()));
                }
            }
            return node;
        }

        if (token.type != Token.OPERATOR) {
            return new Spin1StatementNode(next());
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

    public static void main(String[] args) {
        String text;

        text = "A, B, C";
        System.out.println(text);
        System.out.println(parse(text));
    }

    static String parse(String text) {
        Spin1TreeBuilder builder = new Spin1TreeBuilder();

        Spin1TokenStream stream = new Spin1TokenStream(text);
        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF) {
                break;
            }
            builder.tokens.add(token);
        }

        Spin1StatementNode root = builder.getRoot();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        print(new PrintStream(os), root, 0);
        return os.toString();
    }

    static void print(PrintStream out, Spin1StatementNode node, int indent) {
        if (indent != 0) {
            for (int i = 1; i < indent; i++) {
                out.print("     ");
            }
            out.print(" +-- ");
        }

        out.print("[" + node.getText().replaceAll("\n", "\\\\n") + "]");
        out.println();

        for (Spin1StatementNode child : node.getChilds()) {
            print(out, child, indent + 1);
        }
    }

}
