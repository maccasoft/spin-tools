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

import com.maccasoft.propeller.CompilerMessage;
import com.maccasoft.propeller.model.Token;

public class Spin1TreeBuilder {

    static Map<String, Integer> precedence = new HashMap<String, Integer>();
    static {
        precedence.put(">>", 14);
        precedence.put("<<", 14);
        precedence.put("~>", 14);
        precedence.put("->", 14);
        precedence.put("<-", 14);
        precedence.put("><", 14);

        precedence.put("&", 13);

        precedence.put("^", 12);
        precedence.put("|", 12);

        precedence.put("*", 11);
        precedence.put("**", 11);
        precedence.put("/", 11);
        precedence.put("//", 11);

        precedence.put("+", 10);
        precedence.put("-", 10);

        precedence.put("#>", 9);
        precedence.put("<#", 9);

        precedence.put("<", 8);
        precedence.put("=<", 8);
        precedence.put("==", 8);
        precedence.put("<>", 8);
        precedence.put("=>", 8);
        precedence.put(">", 8);

        precedence.put("NOT", 7);

        precedence.put("AND", 6);

        precedence.put("OR", 5);

        precedence.put("..", 4);

        precedence.put(":", 3);
        precedence.put("?", 2);

        precedence.put(":=", 1);

        precedence.put(">>=", 0);
        precedence.put("<<=", 0);
        precedence.put("~>=", 0);
        precedence.put("->=", 0);
        precedence.put("<-=", 0);
        precedence.put("><=", 0);

        precedence.put("&=", 0);
        precedence.put("^=", 0);
        precedence.put("|=", 0);

        precedence.put("*=", 0);
        precedence.put("**=", 0);
        precedence.put("/=", 0);
        precedence.put("//=", 0);

        precedence.put("+=", 0);
        precedence.put("-=", 0);

        precedence.put("#>=", 0);
        precedence.put("<#=", 0);

        precedence.put("<=", 0);
        precedence.put("=<=", 0);
        precedence.put("===", 0);
        precedence.put("<>=", 0);
        precedence.put("=>=", 0);
        precedence.put(">=", 0);

        precedence.put("AND=", 0);
        precedence.put("OR=", 0);
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
        unary.add(">|");
        unary.add("^^");
        unary.add("@@");
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

    public Spin1TreeBuilder() {

    }

    public void addToken(Token token) {
        tokens.add(token);
    }

    public Spin1StatementNode getRoot() {
        Spin1StatementNode node = parseLevel(parseAtom(), 0);

        Token token = peek();
        while (token != null) {
            if (",".equals(token.getText())) {
                next();
                if (!",".equals(node.getText())) {
                    Spin1StatementNode newNode = new Spin1StatementNode(token);
                    newNode.addChild(node);
                    node = newNode;
                }
                node.addChild(parseLevel(parseAtom(), 0));
                token = peek();
            }
            else {
                throw new CompilerMessage("unexpected " + token.getText(), token);
            }
        }

        return node;
    }

    Spin1StatementNode parseLevel(Spin1StatementNode left, int level) {
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

            Spin1StatementNode right = parseAtom();
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

            Spin1StatementNode node = new Spin1StatementNode(token);
            if (left != null) {
                node.addChild(left);
            }
            node.addChild(right);
            left = node;
        }
    }

    Spin1StatementNode parseAtom() {
        Token token = peek();
        if (token == null) {
            throw new CompilerMessage("expecting operand", tokens.get(tokens.size() - 1));
        }

        if (unary.contains(token.getText().toUpperCase())) {
            Spin1StatementNode node = new Spin1StatementNode(next());
            node.addChild(parseAtom());
            return node;
        }
        if (precedence.containsKey(token.getText().toUpperCase())) {
            return null;
        }

        if ("(".equals(token.getText())) {
            next();
            Spin1StatementNode node = parseLevel(parseAtom(), 0);
            token = next();
            if (token == null || !")".equals(token.getText())) {
                throw new CompilerMessage("expecting )", token == null ? tokens.get(tokens.size() - 1) : token);
            }
            return node;
        }

        if ("[".equals(token.getText())) {
            next();
            Spin1StatementNode node = parseLevel(parseAtom(), 0);
            token = next();
            if (token == null || !"]".equals(token.getText())) {
                throw new CompilerMessage("expecting ]", token == null ? tokens.get(tokens.size() - 1) : token);
            }
            return node;
        }

        if (token.type == 0) {
            Spin1StatementNode node = new Spin1StatementNode(next());
            if (peek() != null) {
                if ("(".equals(peek().getText())) {
                    next();
                    if (peek() != null && ")".equals(peek().getText())) {
                        next();
                        return node;
                    }
                    for (;;) {
                        Spin1StatementNode child = parseLevel(parseAtom(), 0);
                        if (node.getChildCount() == 1 && ":".equals(node.getChild(0).getText())) {
                            node.getChild(0).addChild(child);
                        }
                        else {
                            node.addChild(child);
                        }
                        token = next();
                        if (token == null) {
                            throw new CompilerMessage("expecting )", tokens.get(tokens.size() - 1));
                        }
                        if (")".equals(token.getText())) {
                            return node;
                        }
                        if (!",".equals(token.getText()) && !":".equals(token.getText())) {
                            throw new CompilerMessage("expecting )", token);
                        }
                    }
                }
                if ("[".equals(peek().getText())) {
                    next();
                    node.addChild(parseLevel(parseAtom(), 0));
                    token = next();
                    if (token == null || !"]".equals(token.getText())) {
                        throw new CompilerMessage("expecting ]", token == null ? tokens.get(tokens.size() - 1) : token);
                    }

                    if (peek() == null) {
                        return node;
                    }
                    if ("[".equals(peek().getText())) {
                        next();
                        node.addChild(parseLevel(parseAtom(), 0));
                        token = next();
                        if (token == null || !"]".equals(token.getText())) {
                            throw new CompilerMessage("expecting ]", token == null ? tokens.get(tokens.size() - 1) : token);
                        }
                    }
                }
                Token postToken = peek();
                if (postToken != null && postEffect.contains(postToken.getText())) {
                    if (!"?".equals(postToken.getText()) || postToken.start == (token.stop + 1)) {
                        node.addChild(new Spin1StatementNode(next()));
                    }
                }
            }
            return node;
        }

        if (token.type != Token.OPERATOR) {
            return new Spin1StatementNode(next());
        }

        throw new CompilerMessage("unexpected " + token.getText(), token);
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

        text = "exponent += result := 13";
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
