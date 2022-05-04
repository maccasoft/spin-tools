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

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.model.Token;

public class Spin2TreeBuilder {

    static Map<String, Integer> precedence = new HashMap<String, Integer>();
    static {
        precedence.put(">>", 16);
        precedence.put("<<", 16);
        precedence.put("SAR", 16);
        precedence.put("ROR", 16);
        precedence.put("ROL", 16);
        precedence.put("REV", 16);
        precedence.put("ZEROX", 16);
        precedence.put("SIGNX", 16);

        precedence.put("&", 15);
        precedence.put("^", 14);
        precedence.put("|", 13);

        precedence.put("*", 12);
        precedence.put("/", 12);
        precedence.put("+/", 12);
        precedence.put("//", 12);
        precedence.put("+//", 12);
        precedence.put("SCA", 12);
        precedence.put("SCAS", 12);
        precedence.put("FRAC", 12);

        precedence.put("*.", 12);
        precedence.put("/.", 12);

        precedence.put("+", 11);
        precedence.put("-", 11);

        precedence.put("+.", 11);
        precedence.put("-.", 11);

        precedence.put("#>", 10);
        precedence.put("<#", 10);

        precedence.put("ADDBITS", 9);
        precedence.put("ADDPINS", 9);

        precedence.put("<", 8);
        precedence.put("+<", 8);
        precedence.put("<=", 8);
        precedence.put("+<=", 8);
        precedence.put("==", 8);
        precedence.put("<>", 8);
        precedence.put(">=", 8);
        precedence.put("+>=", 8);
        precedence.put(">", 8);
        precedence.put("+>", 8);
        precedence.put("<=>", 8);

        precedence.put("<.", 8);
        precedence.put("<=.", 8);
        precedence.put("<>.", 8);
        precedence.put("==.", 8);
        precedence.put(">=.", 8);
        precedence.put(">.", 8);

        precedence.put("NOT", 7);

        precedence.put("&&", 6);
        precedence.put("AND", 6);
        precedence.put("^^", 6);
        precedence.put("XOR", 6);
        precedence.put("||", 6);
        precedence.put("OR", 6);

        precedence.put("..", 5);

        precedence.put(":", 4);
        precedence.put("?", 3);

        precedence.put(",", 2);

        precedence.put(":=", 1);
        precedence.put("\\", 1);

        precedence.put(">>=", 0);
        precedence.put("<<=", 0);
        precedence.put("SAR=", 0);
        precedence.put("ROR=", 0);
        precedence.put("ROL=", 0);
        precedence.put("REV=", 0);
        precedence.put("ZEROX=", 0);
        precedence.put("SIGNX=", 0);

        precedence.put("&=", 0);
        precedence.put("^=", 0);
        precedence.put("|=", 0);

        precedence.put("*=", 0);
        precedence.put("/=", 0);
        precedence.put("+/=", 0);
        precedence.put("//=", 0);
        precedence.put("+//=", 0);
        precedence.put("SCA=", 0);
        precedence.put("SCAS=", 0);
        precedence.put("FRAC=", 0);

        precedence.put("+=", 0);
        precedence.put("-=", 0);

        precedence.put("#>=", 0);
        precedence.put("<#=", 0);

        precedence.put("ADDBITS=", 0);
        precedence.put("ADDPINS=", 0);

        precedence.put("+<=", 0);
        precedence.put("<==", 0);
        precedence.put("+<==", 0);
        precedence.put("===", 0);
        precedence.put("<>=", 0);
        precedence.put("+>==", 0);
        precedence.put(">==", 0);
        precedence.put("+>=", 0);
        precedence.put("<=>=", 0);

        precedence.put("&&=", 0);
        precedence.put("AND=", 0);
        precedence.put("^^=", 0);
        precedence.put("XOR=", 0);
        precedence.put("||=", 0);
        precedence.put("OR=", 0);
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
        unary.add("!!=");
        unary.add("!");
        unary.add("!=");
        unary.add("ABS");
        unary.add("ABS=");
        unary.add("ENCOD");
        unary.add("ENCOD=");
        unary.add("DECOD");
        unary.add("DECOD=");
        unary.add("BMASK");
        unary.add("BMASK=");
        unary.add("ONES");
        unary.add("ONES=");
        unary.add("SQRT");
        unary.add("SQRT=");
        unary.add("QLOG");
        unary.add("QLOG=");
        unary.add("QEXP");
        unary.add("QEXP=");
    }

    static Set<String> postEffect = new HashSet<String>();
    static {
        postEffect.add("++");
        postEffect.add("--");
        postEffect.add("!!");
        postEffect.add("!");
        postEffect.add("~");
        postEffect.add("~~");
    }

    int index;
    List<Token> tokens = new ArrayList<Token>();

    public Spin2TreeBuilder() {

    }

    public void addToken(Token token) {
        tokens.add(token);
    }

    public Spin2StatementNode getRoot() {
        Spin2StatementNode node = parseLevel(parseAtom(), 0, true);

        Token token = peek();
        while (token != null) {
            if (",".equals(token.getText())) {
                next();
                if (!",".equals(node.getText())) {
                    Spin2StatementNode newNode = new Spin2StatementNode(token);
                    newNode.addChild(node);
                    node = newNode;
                }
                node.addChild(parseLevel(parseAtom(), 0, true));
                token = peek();
            }
            else {
                throw new CompilerException("unexpected " + token.getText(), token);
            }
        }

        return node;
    }

    Spin2StatementNode parseLevel(Spin2StatementNode left, int level, boolean comma) {
        for (;;) {
            Token token = peek();
            if (token == null) {
                return left;
            }
            if (",".equals(token.getText()) && !comma) {
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
                if (",".equals(token.getText()) && !comma) {
                    break;
                }
                right = parseLevel(right, p.intValue() + 1, comma);
            }

            Spin2StatementNode node = new Spin2StatementNode(token);
            if (left != null) {
                node.addChild(left);
            }
            node.addChild(right);
            left = node;
        }
    }

    Spin2StatementNode parseAtom() {
        Token token = peek();
        if (token == null) {
            throw new CompilerException("expecting operand", tokens.get(tokens.size() - 1));
        }

        if (unary.contains(token.getText().toUpperCase())) {
            Spin2StatementNode node = new Spin2StatementNode(next());
            node.addChild(parseAtom());
            return node;
        }
        if (precedence.containsKey(token.getText().toUpperCase())) {
            return null;
        }

        if ("(".equals(token.getText())) {
            next();
            Spin2StatementNode node = parseLevel(parseAtom(), 0, false);
            token = next();
            if (token == null || !")".equals(token.getText())) {
                throw new CompilerException("expecting )", token == null ? tokens.get(tokens.size() - 1) : token);
            }
            return node;
        }

        if ("[".equals(token.getText())) {
            next();
            Spin2StatementNode node = parseLevel(parseAtom(), 0, false);
            token = next();
            if (token == null || !"]".equals(token.getText())) {
                throw new CompilerException("expecting ]", token == null ? tokens.get(tokens.size() - 1) : token);
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
                        Spin2StatementNode child = parseLevel(parseAtom(), 0, false);
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
                if (".".equals(peek().getText())) {
                    node.addChild(new Spin2StatementNode(next()));
                    if (peek() == null) {
                        return node;
                    }
                }
                if ("[".equals(peek().getText())) {
                    next();
                    node.addChild(parseLevel(parseAtom(), 0, false));
                    token = next();
                    if (token == null || !"]".equals(token.getText())) {
                        throw new CompilerException("expecting ]", token == null ? tokens.get(tokens.size() - 1) : token);
                    }

                    if (peek() == null) {
                        return node;
                    }
                    if ("[".equals(peek().getText())) {
                        next();
                        node.addChild(parseLevel(parseAtom(), 0, false));
                        token = next();
                        if (token == null || !"]".equals(token.getText())) {
                            throw new CompilerException("expecting ]", token == null ? tokens.get(tokens.size() - 1) : token);
                        }
                    }
                    if (peek() == null) {
                        return node;
                    }
                    if (".".equals(peek().getText())) {
                        node.addChild(new Spin2StatementNode(next()));
                        if (peek() == null) {
                            return node;
                        }
                    }
                    else if (peek().getText().startsWith(".")) {
                        node.addChild(parseAtom());
                        if (peek() == null) {
                            return node;
                        }
                    }
                    if ("[".equals(peek().getText())) {
                        next();
                        node.addChild(parseLevel(parseAtom(), 0, false));
                        token = next();
                        if (token == null || !"]".equals(token.getText())) {
                            throw new CompilerException("expecting ]", token == null ? tokens.get(tokens.size() - 1) : token);
                        }
                    }
                }
                Token postToken = peek();
                if (postToken != null && postEffect.contains(postToken.getText())) {
                    if (!"?".equals(postToken.getText()) || postToken.start == (token.stop + 1)) {
                        node.addChild(new Spin2StatementNode(next()));
                    }
                }
            }
            return node;
        }

        if (token.type != Token.OPERATOR) {
            return new Spin2StatementNode(next());
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

    public static void main(String[] args) {
        String text;

        text = "    o[0].start(a, b)";
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
            if (".".equals(token.getText())) {
                Token nextToken = stream.peekNext();
                if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                    token = token.merge(stream.nextToken());
                }
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
