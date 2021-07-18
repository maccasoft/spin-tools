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

        precedence.put("+", 9);
        precedence.put("-", 9);

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

        precedence.put("&&", 5);
        precedence.put("AND", 5);
        precedence.put("^^", 5);
        precedence.put("XOR", 5);
        precedence.put("||", 5);
        precedence.put("OR", 5);

        precedence.put("..", 4);

        precedence.put(":", 3);
        precedence.put("?", 2);

        precedence.put(":=", 1);

        precedence.put(">>=", 1);
        precedence.put("<<=", 1);
        precedence.put("SAR=", 1);
        precedence.put("ROR=", 1);
        precedence.put("ROL=", 1);
        precedence.put("REV=", 1);
        precedence.put("ZEROX=", 1);
        precedence.put("SIGNX=", 1);

        precedence.put("&=", 1);
        precedence.put("^=", 1);
        precedence.put("|=", 1);

        precedence.put("*=", 1);
        precedence.put("/=", 1);
        precedence.put("+/=", 1);
        precedence.put("//=", 1);
        precedence.put("+//=", 1);
        precedence.put("SCA=", 1);
        precedence.put("SCAS=", 1);
        precedence.put("FRAC=", 1);

        precedence.put("+=", 1);
        precedence.put("-=", 1);

        precedence.put("#>=", 1);
        precedence.put("<#=", 1);

        precedence.put("ADDBITS=", 1);
        precedence.put("ADDPINS=", 1);

        precedence.put("<=", 1);
        precedence.put("+<=", 1);
        precedence.put("<==", 1);
        precedence.put("+<==", 1);
        precedence.put("===", 1);
        precedence.put("<>=", 1);
        precedence.put(">==", 1);
        precedence.put("+>==", 1);
        precedence.put(">=", 1);
        precedence.put("+>=", 1);
        precedence.put("<=>=", 1);

        precedence.put("&&=", 1);
        precedence.put("AND=", 1);
        precedence.put("^^=", 1);
        precedence.put("XOR=", 1);
        precedence.put("||=", 1);
        precedence.put("OR=", 1);
    }

    static Set<String> unary = new HashSet<String>();
    static {
        unary.add("+");
        unary.add("-");
        unary.add("++");
        unary.add("--");
        unary.add("??");
        unary.add("\\");
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
        postEffect.add("++");
        postEffect.add("--");
        postEffect.add("!!");
        postEffect.add("!");
    }

    int index;
    List<Token> tokens = new ArrayList<Token>();

    public Spin2TreeBuilder() {

    }

    public void addToken(Token token) {
        tokens.add(token);
    }

    public Spin2StatementNode getRoot() {
        Spin2StatementNode node = parseLevel(parseAtom(), 0);

        Token token = peek();
        while (token != null) {
            if (",".equals(token.getText())) {
                next();
                if (!",".equals(node.getText())) {
                    Spin2StatementNode newNode = new Spin2StatementNode(token);
                    newNode.addChild(node);
                    node = newNode;
                }
                node.addChild(parseLevel(parseAtom(), 0));
                token = peek();
            }
            else {
                throw new RuntimeException("unexpected " + token.getText());
            }
        }

        return node;
    }

    Spin2StatementNode parseLevel(Spin2StatementNode left, int level) {
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

            Spin2StatementNode right = parseAtom();
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

            Spin2StatementNode node = new Spin2StatementNode(token);
            node.addChild(left);
            node.addChild(right);
            left = node;
        }
    }

    Spin2StatementNode parseAtom() {
        Token token = peek();

        if (unary.contains(token.getText().toUpperCase())) {
            Spin2StatementNode node = new Spin2StatementNode(next());
            node.addChild(parseAtom());
            return node;
        }

        if ("(".equals(token.getText())) {
            next();
            Spin2StatementNode node = parseLevel(parseAtom(), 0);
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
            Spin2StatementNode node = parseLevel(parseAtom(), 0);
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
            if (peek() != null) {
                if ("(".equals(peek().getText())) {
                    next();
                    if (peek() != null && ")".equals(peek().getText())) {
                        next();
                        return node;
                    }
                    for (;;) {
                        Spin2StatementNode child = parseLevel(parseAtom(), 0);
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
                if ("[".equals(peek().getText())) {
                    next();
                    node.addChild(parseLevel(parseAtom(), 0));
                    token = next();
                    if (token == null) {
                        throw new RuntimeException("expecting closing parenthesis");
                    }
                    if (!"]".equals(token.getText())) {
                        throw new RuntimeException("expecting closing parenthesis, got " + token.getText());
                    }

                    if (peek() == null) {
                        return node;
                    }
                    if ("[".equals(peek().getText())) {
                        next();
                        node.addChild(parseLevel(parseAtom(), 0));
                        token = next();
                        if (token == null) {
                            throw new RuntimeException("expecting closing parenthesis");
                        }
                        if (!"]".equals(token.getText())) {
                            throw new RuntimeException("expecting closing parenthesis, got " + token.getText());
                        }
                    }
                }
                Token postToken = peek();
                if (postToken != null && postEffect.contains(postToken.getText())) {
                    if (!"?".equals(postToken.getText()) || postToken.column == (token.column + 1)) {
                        node.addChild(new Spin2StatementNode(next()));
                    }
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

        text = "16 / 2 / 2";
        System.out.println(text);
        System.out.println(parse(text));

        text = "160 * 25 - 1";
        System.out.println(text);
        System.out.println(parse(text));

        text = "a := b := c := 1";
        System.out.println(text);
        System.out.println(parse(text));

        text = "dotf := muldiv64(x_total, pal ? pal_cf * 4 * 128 : ntsc_cf * 4 * 128, pal ? pal_cc : ntsc_cc)";
        System.out.println(text);
        System.out.println(parse(text));

        text = "x_total := pal ? 416 : 378";
        System.out.println(text);
        System.out.println(parse(text));

        text = "31 - encod clkfreq";
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
