/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin2.Spin2StatementNode;

public class Spin2CTreeBuilder {

    static Map<String, Integer> precedence = new HashMap<String, Integer>();
    static {
        precedence.put(">>", 16);
        precedence.put("<<", 16);
        //precedence.put("SAR", 16);
        //precedence.put("ROR", 16);
        //precedence.put("ROL", 16);
        //precedence.put("REV", 16);
        //precedence.put("ZEROX", 16);
        //precedence.put("SIGNX", 16);

        precedence.put("&", 15);
        precedence.put("^", 14);
        precedence.put("|", 13);

        precedence.put("*", 12);
        precedence.put("/", 12);
        //precedence.put("+/", 12);
        precedence.put("%", 12);
        //precedence.put("+//", 12);
        //precedence.put("SCA", 12);
        //precedence.put("SCAS", 12);
        //precedence.put("FRAC", 12);

        //precedence.put("*.", 12);
        //precedence.put("/.", 12);

        precedence.put("+", 11);
        precedence.put("-", 11);

        //precedence.put("+.", 11);
        //precedence.put("-.", 11);

        //precedence.put("#>", 10);
        //precedence.put("<#", 10);

        //precedence.put("ADDBITS", 9);
        //precedence.put("ADDPINS", 9);

        precedence.put("<", 8);
        //precedence.put("+<", 8);
        precedence.put("<=", 8);
        //precedence.put("+<=", 8);
        precedence.put("==", 8);
        precedence.put("!=", 8);
        precedence.put(">=", 8);
        //precedence.put("+>=", 8);
        precedence.put(">", 8);
        //precedence.put("+>", 8);
        //precedence.put("<=>", 8);

        //precedence.put("<.", 8);
        //precedence.put("<=.", 8);
        //precedence.put("<>.", 8);
        //precedence.put("==.", 8);
        //precedence.put(">=.", 8);
        //precedence.put(">.", 8);

        //precedence.put("NOT", 7);

        precedence.put("&&", 6);
        //precedence.put("AND", 6);
        //precedence.put("^^", 6);
        //precedence.put("XOR", 6);
        precedence.put("||", 6);
        //precedence.put("OR", 6);

        precedence.put("..", 5);

        precedence.put(":", 4);
        precedence.put("?", 3);

        precedence.put(",", 2);

        precedence.put("\\", 1);
    }

    static Set<String> assignements = new HashSet<String>();
    static {
        assignements.add("=");

        assignements.add(">>=");
        assignements.add("<<=");
        //assignements.add("SAR=");
        //assignements.add("ROR=");
        //assignements.add("ROL=");
        //assignements.add("REV=");
        //assignements.add("ZEROX=");
        //assignements.add("SIGNX=");

        assignements.add("&=");
        assignements.add("^=");
        assignements.add("|=");

        assignements.add("*=");
        assignements.add("/=");
        //assignements.add("+/=");
        assignements.add("%=");
        //assignements.add("+//=");
        //assignements.add("SCA=");
        //assignements.add("SCAS=");
        //assignements.add("FRAC=");

        assignements.add("+=");
        assignements.add("-=");

        //assignements.add("#>=");
        //assignements.add("<#=");

        //assignements.add("ADDBITS=");
        //assignements.add("ADDPINS=");

        //assignements.add("+<=");
        //assignements.add("<==");
        //assignements.add("+<==");
        //assignements.add("===");
        //assignements.add("<>=");
        //assignements.add("+>==");
        //assignements.add(">==");
        //assignements.add("+>=");
        //assignements.add("<=>=");

        //assignements.add("&&=");
        //assignements.add("AND=");
        //assignements.add("^^=");
        //assignements.add("XOR=");
        //assignements.add("||=");
        //assignements.add("OR=");
    }

    static Set<String> unary = new HashSet<String>();
    static {
        unary.add("*");
        unary.add("+");
        unary.add("-");
        unary.add("++");
        unary.add("--");
        //unary.add("??");
        //unary.add("\\");
        //unary.add("!!");
        //unary.add("!!=");
        unary.add("!");
        //unary.add("!=");
        //unary.add("ABS");
        //unary.add("ABS=");
        //unary.add("ENCOD");
        //unary.add("ENCOD=");
        //unary.add("DECOD");
        //unary.add("DECOD=");
        //unary.add("BMASK");
        //unary.add("BMASK=");
        //unary.add("ONES");
        //unary.add("ONES=");
        //unary.add("SQRT");
        //unary.add("SQRT=");
        //unary.add("QLOG");
        //unary.add("QLOG=");
        //unary.add("QEXP");
        //unary.add("QEXP=");
        unary.add("~");
    }

    static Set<String> postEffect = new HashSet<String>();
    static {
        postEffect.add("++");
        postEffect.add("--");
        //postEffect.add("!!");
        //postEffect.add("!");
        //postEffect.add("~");
        //postEffect.add("~~");
    }

    Context scope;

    int index;
    List<Token> tokens = new ArrayList<Token>();

    public Spin2CTreeBuilder(Context scope) {
        this.scope = scope;
    }

    public void addToken(Token token) {
        if (token.type == Token.KEYWORD) {
            List<Token> l = scope.getDefinition(token.getText());
            if (l != null) {
                l.iterator().forEachRemaining((t) -> {
                    addToken(t);
                });
                return;
            }
        }
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
                throw new CompilerException("unexpected " + token.getText(), token);
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
            if (",".equals(token.getText())) {
                return left;
            }

            if (assignements.contains(token.getText().toUpperCase())) {
                Spin2StatementNode node = new Spin2StatementNode(next());
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
                if (",".equals(token.getText())) {
                    break;
                }
                right = parseLevel(right, p.intValue() + 1);
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

        if ("sizeof".equals(token.getText())) {
            Spin2StatementNode node = new Spin2StatementNode(next(), true);
            token = peek();
            if (token != null && "(".equals(token.getText())) {
                next();
                while ((token = next()) != null) {
                    if (")".equals(token.getText())) {
                        break;
                    }
                    node.addChild(new Spin2StatementNode(token));
                }
            }
            return node;
        }

        if (unary.contains(token.getText())) {
            Spin2StatementNode node = new Spin2StatementNode(next());
            node.addChild(parseAtom());
            return node;
        }
        if (precedence.containsKey(token.getText())) {
            return null;
        }

        if ("(".equals(token.getText())) {
            next();
            Spin2StatementNode node = parseLevel(parseAtom(), 0);
            token = next();
            if (token == null || !")".equals(token.getText())) {
                throw new CompilerException("expecting )", token == null ? tokens.get(tokens.size() - 1) : token);
            }
            return node;
        }

        if ("[".equals(token.getText())) {
            next();
            Spin2StatementNode node = parseLevel(parseAtom(), 0);
            token = next();
            if (token == null || !"]".equals(token.getText())) {
                throw new CompilerException("expecting ]", token == null ? tokens.get(tokens.size() - 1) : token);
            }
            return node;
        }

        if (token.type == 0 || token.type == Token.KEYWORD || token.type == Token.FUNCTION) {
            Spin2StatementNode node = new Spin2StatementNode(next());
            if (peek() != null) {
                if (".".equals(peek().getText())) {
                    node.addChild(new Spin2StatementNode(next()));
                    if (peek() == null) {
                        return node;
                    }
                }
                if ("[".equals(peek().getText())) {
                    Token start = next();
                    Spin2StatementNode childNode = parseLevel(parseAtom(), 0);
                    token = next();
                    node.addChild(new Spin2StatementNode.Index(childNode, start, token));
                    if (token == null || !"]".equals(token.getText())) {
                        throw new CompilerException("expecting ]", token == null ? tokens.get(tokens.size() - 1) : token);
                    }
                    if (peek() == null) {
                        return node;
                    }
                    if ("[".equals(peek().getText())) {
                        start = next();
                        childNode = parseLevel(parseAtom(), 0);
                        token = next();
                        node.addChild(new Spin2StatementNode.Index(childNode, start, token));
                        if (token == null || !"]".equals(token.getText())) {
                            throw new CompilerException("expecting ]", token == null ? tokens.get(tokens.size() - 1) : token);
                        }
                        if (peek() == null) {
                            return node;
                        }
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
                        start = next();
                        childNode = parseLevel(parseAtom(), 0);
                        token = next();
                        node.addChild(new Spin2StatementNode.Index(childNode, start, token));
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
                        node.addChild(new Spin2StatementNode(next()));
                    }
                }
                else if ("(".equals(peek().getText())) {
                    next();
                    node.setMethod(true);
                    if (peek() != null && ")".equals(peek().getText())) {
                        token = next();
                        if (peek() != null && peek().column == token.column + 1) {
                            if (":".equals(peek().getText())) {
                                next();
                                if ((token = next()) == null) {
                                    throw new CompilerException("expecting return count", token);
                                }
                            }
                        }
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
                        token = peek();
                        if (token == null) {
                            throw new CompilerException("expecting )", tokens.get(tokens.size() - 1));
                        }
                        if (")".equals(token.getText())) {
                            next();
                            if (peek() != null && peek().column == token.column + 1) {
                                if (":".equals(peek().getText())) {
                                    next();
                                    if ((token = next()) == null) {
                                        throw new CompilerException("expecting return count", token);
                                    }
                                }
                            }
                            return node;
                        }
                        if (child.getToken().type == Token.STRING && child.getToken().getText().startsWith("`")) {
                            continue;
                        }
                        if (!",".equals(token.getText()) && !":".equals(token.getText())) {
                            throw new CompilerException("expecting )", token);
                        }
                        next();
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
            Token result = tokens.get(index);
            if ("#".equals(result.getText()) && (index + 1) < tokens.size()) {
                result = result.merge(tokens.get(index + 1));
            }
            return result;
        }
        return null;
    }

    Token next() {
        if (index < tokens.size()) {
            Token result = tokens.get(index++);
            if ("#".equals(result.getText()) && index < tokens.size()) {
                result = result.merge(tokens.get(index++));
            }
            return result;
        }
        return null;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public boolean isEmpty() {
        return tokens.size() == 0;
    }

}
