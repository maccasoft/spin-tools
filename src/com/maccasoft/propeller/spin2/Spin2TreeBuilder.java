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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.maccasoft.propeller.model.Token;

public class Spin2TreeBuilder {

    static int highestPrecedence = 1;
    static Map<String, Integer> precedence = new HashMap<String, Integer>();
    static {
        precedence.put(">>", highestPrecedence);
        precedence.put("<<", highestPrecedence);
        precedence.put("SAR", highestPrecedence);
        precedence.put("ROR", highestPrecedence);
        precedence.put("ROL", highestPrecedence);
        precedence.put("REV", highestPrecedence);
        precedence.put("ZEROX", highestPrecedence);
        precedence.put("SIGNX", highestPrecedence);
        highestPrecedence++;

        precedence.put("&", highestPrecedence++);
        precedence.put("^", highestPrecedence++);
        precedence.put("|", highestPrecedence++);

        precedence.put("*", highestPrecedence);
        precedence.put("/", highestPrecedence);
        precedence.put("+/", highestPrecedence);
        precedence.put("//", highestPrecedence);
        precedence.put("+//", highestPrecedence);
        precedence.put("SCA", highestPrecedence);
        precedence.put("SCAS", highestPrecedence);
        precedence.put("FRAC", highestPrecedence);
        highestPrecedence++;

        precedence.put("+", highestPrecedence);
        precedence.put("-", highestPrecedence);
        highestPrecedence++;

        precedence.put("#>", highestPrecedence);
        precedence.put("<#", highestPrecedence);
        highestPrecedence++;

        precedence.put("ADDBITS", highestPrecedence);
        precedence.put("ADDPINS", highestPrecedence);
        highestPrecedence++;

        precedence.put("<", highestPrecedence);
        precedence.put("+<", highestPrecedence);
        precedence.put("<=", highestPrecedence);
        precedence.put("+<=", highestPrecedence);
        precedence.put("==", highestPrecedence);
        precedence.put("<>", highestPrecedence);
        precedence.put(">=", highestPrecedence);
        precedence.put("+>=", highestPrecedence);
        precedence.put(">", highestPrecedence);
        precedence.put("+>", highestPrecedence);
        precedence.put("<=>", highestPrecedence);
        highestPrecedence++;

        precedence.put("&&", highestPrecedence);
        precedence.put("AND", highestPrecedence);
        precedence.put("^^", highestPrecedence);
        precedence.put("XOR", highestPrecedence);
        precedence.put("||", highestPrecedence);
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
        precedence.put("SAR=", highestPrecedence);
        precedence.put("ROR=", highestPrecedence);
        precedence.put("ROL=", highestPrecedence);
        precedence.put("REV=", highestPrecedence);
        precedence.put("ZEROX=", highestPrecedence);
        precedence.put("SIGNX=", highestPrecedence);

        precedence.put("&=", highestPrecedence);
        precedence.put("^=", highestPrecedence);
        precedence.put("|=", highestPrecedence);

        precedence.put("*=", highestPrecedence);
        precedence.put("/=", highestPrecedence);
        precedence.put("+/=", highestPrecedence);
        precedence.put("//=", highestPrecedence);
        precedence.put("+//=", highestPrecedence);
        precedence.put("SCA=", highestPrecedence);
        precedence.put("SCAS=", highestPrecedence);
        precedence.put("FRAC=", highestPrecedence);

        precedence.put("+=", highestPrecedence);
        precedence.put("-=", highestPrecedence);

        precedence.put("#>=", highestPrecedence);
        precedence.put("<#=", highestPrecedence);

        precedence.put("ADDBITS=", highestPrecedence);
        precedence.put("ADDPINS=", highestPrecedence);

        precedence.put("<=", highestPrecedence);
        precedence.put("+<=", highestPrecedence);
        precedence.put("<==", highestPrecedence);
        precedence.put("+<==", highestPrecedence);
        precedence.put("===", highestPrecedence);
        precedence.put("<>=", highestPrecedence);
        precedence.put(">==", highestPrecedence);
        precedence.put("+>==", highestPrecedence);
        precedence.put(">=", highestPrecedence);
        precedence.put("+>=", highestPrecedence);
        precedence.put("<=>=", highestPrecedence);

        precedence.put("&&=", highestPrecedence);
        precedence.put("AND=", highestPrecedence);
        precedence.put("^^=", highestPrecedence);
        precedence.put("XOR=", highestPrecedence);
        precedence.put("||=", highestPrecedence);
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

    public Spin2StatementNode getRoot() {
        Spin2StatementNode node = parseLevel(highestPrecedence);

        Token token = peek();
        while (token != null) {
            if (",".equals(token.getText())) {
                next();
                if (!",".equals(node.getText())) {
                    Spin2StatementNode newNode = new Spin2StatementNode(token);
                    newNode.addChild(node);
                    node = newNode;
                }
                node.addChild(parseLevel(highestPrecedence));
                token = peek();
            }
            else {
                throw new RuntimeException("unexpected " + token.getText());
            }
        }

        return node;
    }

    Spin2StatementNode parseLevel(int level) {
        Spin2StatementNode left = level == 0 ? parseAtom() : parseLevel(level - 1);

        Token token = peek();
        if (token == null) {
            return left;
        }

        Integer p = precedence.get(token.getText().toUpperCase());
        if (p != null && p.intValue() == level) {
            Spin2StatementNode node = new Spin2StatementNode(next());
            Spin2StatementNode right = level == 0 ? parseAtom() : parseLevel(level);
            node.addChild(left);
            node.addChild(right);
            return node;
        }

        return left;
    }

    Spin2StatementNode parseAtom() {
        Token token = peek();

        if (unary.contains(token.getText())) {
            Spin2StatementNode node = new Spin2StatementNode(next());
            node.addChild(parseAtom());
            return node;
        }

        if ("(".equals(token.getText())) {
            next();
            Spin2StatementNode node = parseLevel(highestPrecedence);
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
            Spin2StatementNode node = parseLevel(highestPrecedence);
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
            Spin2StatementNode node = new Spin2StatementNode(next());
            if ((token = peek()) != null) {
                if ("(".equals(token.getText())) {
                    next();
                    if (peek() != null && ")".equals(peek().getText())) {
                        next();
                        return node;
                    }
                    for (;;) {
                        Spin2StatementNode child = parseLevel(highestPrecedence);
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
                    node.addChild(new Spin2StatementNode(next()));
                }
            }
            return node;
        }

        if (token.type != Token.OPERATOR) {
            return new Spin2StatementNode(next());
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
        Spin2TreeBuilder builder = new Spin2TreeBuilder();

        Spin2TokenStream stream = new Spin2TokenStream(text);
        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF) {
                break;
            }
            builder.tokens.add(token);
        }

        Spin2StatementNode root = builder.getRoot();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        print(new PrintStream(os), root, 0);
        return os.toString();
    }

    static void print(PrintStream out, Spin2StatementNode node, int indent) {
        if (indent != 0) {
            for (int i = 1; i < indent; i++) {
                out.print("     ");
            }
            out.print(" +-- ");
        }

        out.print("[" + node.getText().replaceAll("\n", "\\\\n") + "]");
        out.println();

        for (Spin2StatementNode child : node.getChilds()) {
            print(out, child, indent + 1);
        }
    }
}
