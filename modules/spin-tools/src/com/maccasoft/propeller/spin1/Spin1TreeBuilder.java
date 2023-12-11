/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
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
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.model.Token;

public class Spin1TreeBuilder {

    static Map<String, Integer> precedence = new HashMap<>();
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
    }

    static Set<String> assignements = new HashSet<>();
    static {
        assignements.add(":=");

        assignements.add(">>=");
        assignements.add("<<=");
        assignements.add("~>=");
        assignements.add("->=");
        assignements.add("<-=");
        assignements.add("><=");

        assignements.add("&=");
        assignements.add("^=");
        assignements.add("|=");

        assignements.add("*=");
        assignements.add("**=");
        assignements.add("/=");
        assignements.add("//=");

        assignements.add("+=");
        assignements.add("-=");

        assignements.add("#>=");
        assignements.add("<#=");

        assignements.add("<=");
        assignements.add("=<=");
        assignements.add("===");
        assignements.add("<>=");
        assignements.add("=>=");
        assignements.add(">=");

        assignements.add("AND=");
        assignements.add("OR=");
    }

    static Set<String> unary = new HashSet<>();
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

    static Set<String> postEffect = new HashSet<>();
    static {
        postEffect.add("?");
        postEffect.add("~");
        postEffect.add("++");
        postEffect.add("--");
        postEffect.add("~~");
    }

    Context scope;

    int index;
    List<Token> tokens = new ArrayList<>();
    Set<String> dependencies = new HashSet<>();

    public Spin1TreeBuilder(Context scope) {
        this.scope = scope;
    }

    public void addToken(Token token) {
        if (token.type == 0 || token.type == Token.KEYWORD) {
            List<Token> l = scope.getDefinition(token.getText());
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

    public List<Token> getTokens() {
        return tokens;
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
                throw new CompilerException("unexpected " + token.getText(), token);
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

            if (assignements.contains(token.getText().toUpperCase())) {
                Spin1StatementNode node = new Spin1StatementNode(next());
                if (left != null) {
                    node.addChild(left);
                }
                node.addChild(parseLevel(parseAtom(), 0));
                return node;
            }

            Integer p = precedence.get(token.getText().toUpperCase());
            if (p == null || p.intValue() < level) {
                return left;
            }
            token = next();

            Spin1StatementNode right = left == null ? parseLevel(parseAtom(), p.intValue() + 1) : parseAtom();
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
            throw new CompilerException("expecting operand", tokens.get(tokens.size() - 1));
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
                throw new CompilerException("expecting )", token == null ? tokens.get(tokens.size() - 1) : token);
            }
            return node;
        }

        if ("[".equals(token.getText())) {
            next();
            Spin1StatementNode node = parseLevel(parseAtom(), 0);
            token = next();
            if (token == null || !"]".equals(token.getText())) {
                throw new CompilerException("expecting ]", token == null ? tokens.get(tokens.size() - 1) : token);
            }
            return node;
        }

        if (token.type == 0 || token.type == Token.FUNCTION) {
            Spin1StatementNode node = new Spin1StatementNode(next());
            if (peek() != null) {
                if (".".equals(peek().getText())) {
                    node.addChild(new Spin1StatementNode(next()));
                    if (peek() == null) {
                        return node;
                    }
                }
                if ("[".equals(peek().getText())) {
                    next();
                    node.addChild(new Spin1StatementNode.Index(parseLevel(parseAtom(), 0)));
                    token = next();
                    if (token == null || !"]".equals(token.getText())) {
                        throw new CompilerException("expecting ]", token == null ? tokens.get(tokens.size() - 1) : token);
                    }
                    if (peek() == null) {
                        return node;
                    }
                    if (".".equals(peek().getText())) {
                        node.addChild(new Spin1StatementNode(next()));
                        if (peek() == null) {
                            return node;
                        }
                    }
                    else if (peek().type != Token.OPERATOR && peek().getText().startsWith(".")) {
                        node.addChild(parseAtom());
                        if (peek() == null) {
                            return node;
                        }
                    }
                    if ("[".equals(peek().getText())) {
                        next();
                        node.addChild(new Spin1StatementNode.Index(parseLevel(parseAtom(), 0)));
                        token = next();
                        if (token == null || !"]".equals(token.getText())) {
                            throw new CompilerException("expecting ]", token == null ? tokens.get(tokens.size() - 1) : token);
                        }
                        if (peek() == null) {
                            return node;
                        }
                    }
                }
                if (postEffect.contains(peek().getText())) {
                    Token postToken = peek();
                    if (!"?".equals(postToken.getText()) || postToken.start == (token.stop + 1)) {
                        node.addChild(new Spin1StatementNode(next()));
                    }
                }
                else if ("(".equals(peek().getText())) {
                    next();
                    node = new Spin1StatementNode.Method(node);
                    if (peek() != null && ")".equals(peek().getText())) {
                        next();
                        return node;
                    }
                    for (;;) {
                        Spin1StatementNode child = new Spin1StatementNode.Argument(parseLevel(parseAtom(), 0));
                        if (node.getChildCount() == 1 && ":".equals(node.getChild(0).getText())) {
                            node.getChild(0).addChild(child);
                        }
                        else {
                            node.addChild(child);
                        }
                        token = next();
                        if (token == null) {
                            throw new CompilerException("expecting )", tokens.get(tokens.size() - 1));
                        }
                        if (")".equals(token.getText())) {
                            return node;
                        }
                        if (!",".equals(token.getText()) && !":".equals(token.getText())) {
                            throw new CompilerException("expecting )", token);
                        }
                    }
                }
            }
            return node;
        }

        if (token.type != Token.OPERATOR) {
            return new Spin1StatementNode(next());
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

}
