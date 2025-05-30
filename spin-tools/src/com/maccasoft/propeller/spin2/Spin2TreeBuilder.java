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
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.DataVariable;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.model.Token;

public class Spin2TreeBuilder {

    static Map<String, Integer> precedence = new HashMap<>();
    static {
        precedence.put(">>", 17);
        precedence.put("<<", 17);
        precedence.put("SAR", 17);
        precedence.put("ROR", 17);
        precedence.put("ROL", 17);
        precedence.put("REV", 17);
        precedence.put("ZEROX", 17);
        precedence.put("SIGNX", 17);

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
        precedence.put("POW", 11);

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

        precedence.put("\\", 17);
    }

    static Set<String> assignements = new HashSet<>();
    static {
        assignements.add(":=");
        assignements.add(":=:");

        assignements.add(">>=");
        assignements.add("<<=");
        assignements.add("SAR=");
        assignements.add("ROR=");
        assignements.add("ROL=");
        assignements.add("REV=");
        assignements.add("ZEROX=");
        assignements.add("SIGNX=");

        assignements.add("&=");
        assignements.add("^=");
        assignements.add("|=");

        assignements.add("*=");
        assignements.add("/=");
        assignements.add("+/=");
        assignements.add("//=");
        assignements.add("+//=");
        assignements.add("SCA=");
        assignements.add("SCAS=");
        assignements.add("FRAC=");

        assignements.add("+=");
        assignements.add("-=");

        assignements.add("#>=");
        assignements.add("<#=");

        assignements.add("ADDBITS=");
        assignements.add("ADDPINS=");

        assignements.add("+<=");
        assignements.add("<==");
        assignements.add("+<==");
        assignements.add("===");
        assignements.add("<>=");
        assignements.add("+>==");
        assignements.add(">==");
        assignements.add("+>=");
        assignements.add("<=>=");

        assignements.add("&&=");
        assignements.add("AND=");
        assignements.add("^^=");
        assignements.add("XOR=");
        assignements.add("||=");
        assignements.add("OR=");
    }

    static Set<String> unary = new HashSet<>();
    static {
        unary.add("+");
        unary.add("-");
        unary.add("-=");
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
        unary.add("FABS");
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
        unary.add("FSQRT");
        unary.add("QLOG");
        unary.add("QLOG=");
        unary.add("QEXP");
        unary.add("QEXP=");
        unary.add("+.");
        unary.add("-.");
        unary.add("LOG2");
        unary.add("LOG10");
        unary.add("LOG");
        unary.add("EXP2");
        unary.add("EXP10");
        unary.add("EXP");
    }

    static Set<String> postEffect = new HashSet<>();
    static {
        postEffect.add("++");
        postEffect.add("--");
        postEffect.add("!!");
        postEffect.add("!");
        postEffect.add("~");
        postEffect.add("~~");
    }

    static Set<String> typeFunctions = new HashSet<>();
    static {
        typeFunctions.add("byte");
        typeFunctions.add("word");
        typeFunctions.add("long");
    }

    Context scope;

    int index;
    List<Token> tokens = new ArrayList<>();
    Set<String> dependencies = new HashSet<>();

    public Spin2TreeBuilder(Context scope) {
        this.scope = scope;
    }

    public void addToken(Token token) {
        if (token.type == Token.KEYWORD) {
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
            else if (token.getText().startsWith("`")) {
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
            if (token == null || token.type == Token.STRING) {
                return left;
            }
            if (",".equals(token.getText()) && !comma) {
                return left;
            }

            if (assignements.contains(token.getText().toUpperCase())) {
                Spin2StatementNode node = new Spin2StatementNode(next());
                if (left != null) {
                    node.addChild(left);
                }
                node.addChild(parseLevel(parseAtom(), 0, comma));
                return node;
            }

            Integer p = precedence.get(token.getText().toUpperCase());
            if (p == null || p.intValue() < level) {
                return left;
            }
            token = next();

            Spin2StatementNode right = left == null ? parseLevel(parseAtom(), p.intValue() + 1, comma) : parseAtom();

            if (",".equals(token.getText())) {
                Token nextToken = peek();
                if (nextToken != null) {
                    Integer nextP = precedence.get(peek().getText().toUpperCase());
                    if (nextP != null && nextP.intValue() > p.intValue()) {
                        right = parseLevel(right, p.intValue() + 1, comma);
                    }
                }
            }
            else {
                for (;;) {
                    Token nextToken = peek();
                    if (nextToken == null) {
                        break;
                    }
                    if (assignements.contains(nextToken.getText().toUpperCase())) {
                        Spin2StatementNode node = new Spin2StatementNode(next());
                        node.addChild(right);
                        node.addChild(parseLevel(parseAtom(), 0, false));
                        right = node;
                    }
                    else {
                        Integer nextP = precedence.get(nextToken.getText().toUpperCase());
                        if (nextP == null || nextP.intValue() <= p.intValue()) {
                            break;
                        }
                        if (",".equals(token.getText()) && !comma) {
                            break;
                        }
                        right = parseLevel(right, p.intValue() + 1, comma);
                    }
                }
            }
            if (right == null) {
                throw new CompilerException("expecting expression", tokens.get(index < tokens.size() ? index : index - 1));
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

        if (token.type == Token.STRING) {
            return new Spin2StatementNode(next());
        }

        if (unary.contains(token.getText().toUpperCase())) {
            token = next();
            Spin2StatementNode node = new Spin2StatementNode(token);
            if ((token = peek()) == null) {
                return node;
            }
            if ("]".equals(token.getText()) || ")".equals(token.getText())) {
                return node;
            }
            node.addChild(parseAtom());
            return node;
        }
        if (precedence.containsKey(token.getText().toUpperCase())) {
            return null;
        }

        if ("(".equals(token.getText())) {
            Token first = next();
            Spin2StatementNode node = parseLevel(parseAtom(), 0, false);
            token = next();
            if (token == null || !")".equals(token.getText())) {
                throw new CompilerException("expecting )", token == null ? tokens.get(tokens.size() - 1) : token);
            }
            node.firstToken = first;
            node.lastToken = token;
            return node;
        }

        if ("[++]".equals(token.getText()) || "[--]".equals(token.getText())) {
            Spin2StatementNode node = new Spin2StatementNode(next());
            if ((token = peek()) != null) {
                if (token.type == Token.KEYWORD) {
                    node.addChild(parseAtom());
                }
            }
            return node;
        }

        if ("@".equals(token.getText())) {
            Token first = next();
            if ((token = peek()) == null) {
                throw new CompilerException("syntax error", tokens.get(tokens.size() - 1));
            }
            if (token.type == Token.STRING) {
                token = first.merge(next());
                token.type = Token.STRING;
                return new Spin2StatementNode(token);
            }
            Spin2StatementNode node = parseAtom();
            node.token = first.merge(node.token);
            node.token.type = Token.KEYWORD;
            return node;
        }

        if ("@@".equals(token.getText()) || "^@".equals(token.getText())) {
            Token first = next();
            if ((token = peek()) == null) {
                throw new CompilerException("syntax error", tokens.get(tokens.size() - 1));
            }
            if ("[".equals(token.getText())) {
                first = first.merge(next());

                if ((token = next()) == null) {
                    throw new CompilerException("syntax error", tokens.get(tokens.size() - 1));
                }
                if (token.type != Token.KEYWORD) {
                    throw new CompilerException("expecting variable", token == null ? tokens.get(tokens.size() - 1) : token);
                }
                Token last = next();
                if (last == null || !"]".equals(last.getText())) {
                    throw new CompilerException("expecting ]", last == null ? tokens.get(tokens.size() - 1) : last);
                }
                token = first.merge(token).merge(last);
                Spin2StatementNode node = new Spin2StatementNode(token);

                if ((token = peek()) != null) {
                    if (postEffect.contains(token.getText())) {
                        Token postToken = peek();
                        if (!"?".equals(postToken.getText()) || postToken.start == (token.stop + 1)) {
                            node.addChild(new Spin2StatementNode(next()));
                        }
                    }
                }

                return node;
            }
            if (token.type != Token.KEYWORD) {
                throw new CompilerException("syntax error", token);
            }
            Spin2StatementNode node = parseAtom();
            node.token = first.merge(node.token);
            node.token.type = Token.KEYWORD;
            return node;
        }

        if ("[".equals(token.getText())) {
            Token first = next();

            if ((token = next()) == null) {
                throw new CompilerException("syntax error", tokens.get(tokens.size() - 1));
            }
            if (token.type != Token.KEYWORD) {
                throw new CompilerException("expecting variable", token == null ? tokens.get(tokens.size() - 1) : token);
            }
            Token last = next();
            if (last == null || !"]".equals(last.getText())) {
                throw new CompilerException("expecting ]", last == null ? tokens.get(tokens.size() - 1) : last);
            }
            token = first.merge(token).merge(last);
            Spin2StatementNode node = new Spin2StatementNode(token);

            if ((token = peek()) != null) {
                if (postEffect.contains(token.getText())) {
                    Token postToken = peek();
                    if (!"?".equals(postToken.getText()) || postToken.start == (token.stop + 1)) {
                        node.addChild(new Spin2StatementNode(next()));
                    }
                }
            }

            return node;
        }

        if (token.type == Token.KEYWORD) {
            Spin2StatementNode node = new Spin2StatementNode(next());
            if (peek() != null) {
                if (".".equals(peek().getText())) {
                    node.addChild(new Spin2StatementNode(next()));
                    if (peek() == null) {
                        return node;
                    }
                }
                if ("[++]".equals(peek().getText()) || "[--]".equals(peek().getText())) {
                    node.addChild(new Spin2StatementNode(next()));
                    if (peek() == null) {
                        return node;
                    }
                    if (".".equals(peek().getText())) {
                        node.addChild(new Spin2StatementNode(next()));
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
                        Token first = next();
                        Spin2StatementNode childNode = parseLevel(parseAtom(), 0, false);
                        token = next();
                        node.addChild(new Spin2StatementNode.Index(childNode, first, token));
                        if (token == null || !"]".equals(token.getText())) {
                            throw new CompilerException("expecting ]", token == null ? tokens.get(tokens.size() - 1) : token);
                        }
                        if (peek() == null) {
                            return node;
                        }
                    }
                }
                else if ("[".equals(peek().getText())) {
                    Token first = next();
                    Spin2StatementNode childNode = parseLevel(parseAtom(), 0, false);
                    token = next();
                    node.addChild(new Spin2StatementNode.Index(childNode, first, token));
                    if (token == null || !"]".equals(token.getText())) {
                        throw new CompilerException("expecting ]", token == null ? tokens.get(tokens.size() - 1) : token);
                    }
                    if (peek() == null) {
                        return node;
                    }
                    if ("[".equals(peek().getText())) {
                        first = next();
                        childNode = parseLevel(parseAtom(), 0, false);
                        token = next();
                        node.addChild(new Spin2StatementNode.Index(childNode, first, token));
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
                    else if (peek().type != Token.OPERATOR && peek().getText().startsWith(".")) {
                        node.addChild(parseAtom());
                        if (peek() == null) {
                            return node;
                        }
                    }
                    if ("[".equals(peek().getText())) {
                        first = next();
                        childNode = parseLevel(parseAtom(), 0, false);
                        token = next();
                        node.addChild(new Spin2StatementNode.Index(childNode, first, token));
                        if (token == null || !"]".equals(token.getText())) {
                            throw new CompilerException("expecting ]", token == null ? tokens.get(tokens.size() - 1) : token);
                        }
                        if (peek() == null) {
                            return node;
                        }
                    }
                }
                else if (typeFunctions.contains(node.getText().toLowerCase()) && peek() != null && peek().type != Token.OPERATOR) {
                    node.addChild(parseLevel(parseAtom(), 0, false));
                    return node;
                }
                if ("(".equals(peek().getText())) {
                    Token first = next();
                    node.setMethod(true);
                    Token next = peek();
                    if (next != null && ")".equals(next.getText())) {
                        token = next();
                        String[] ar = node.getText().split("[\\.]");
                        Expression expression = scope.getLocalSymbol(ar[0]);
                        if ((expression instanceof Variable) || (expression instanceof DataVariable)) {
                            next = peek();
                            if (next != null && ":".equals(next.getText())) {
                                int currentIndex = index;
                                next = next();
                                Token value = next();
                                if (value != null) {
                                    if (value.type == Token.NUMBER) {
                                        node.setReturnLongs(Integer.valueOf(value.getText()).intValue());
                                    }
                                    else {
                                        Spin2Struct struct = scope.getStructureDefinition(value.getText());
                                        if (struct != null) {
                                            node.setReturnLongs((struct.getTypeSize() + 3) / 4);
                                        }
                                        else {
                                            throw new CompilerException("expected integer constant", value);
                                        }
                                    }
                                    token = value;
                                }
                                else {
                                    index = currentIndex;
                                }
                            }
                        }
                        node.firstToken = first;
                        node.lastToken = token;
                        return node;
                    }
                    boolean isBacktickString = false;
                    for (;;) {
                        Spin2StatementNode child = new Spin2StatementNode.Argument(parseLevel(parseAtom(), 0, false));
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
                            token = next();
                            String[] ar = node.getText().split("[\\.]");
                            Expression expression = scope.getLocalSymbol(ar[0]);
                            if ((expression instanceof Variable) || (expression instanceof DataVariable)) {
                                next = peek();
                                if (next != null && ":".equals(next.getText())) {
                                    int currentIndex = index;
                                    next = next();
                                    Token value = next();
                                    if (value != null) {
                                        if (value.type == Token.NUMBER) {
                                            node.setReturnLongs(Integer.valueOf(value.getText()).intValue());
                                        }
                                        else {
                                            Spin2Struct struct = scope.getStructureDefinition(value.getText());
                                            if (struct != null) {
                                                node.setReturnLongs((struct.getTypeSize() + 3) / 4);
                                            }
                                            else {
                                                throw new CompilerException("expected integer constant", value);
                                            }
                                        }
                                        token = value;
                                    }
                                    else {
                                        index = currentIndex;
                                    }
                                }
                            }
                            node.firstToken = first;
                            node.lastToken = token;
                            return node;
                        }
                        if (child.getToken().getText().startsWith("`")) {
                            isBacktickString = true;
                        }
                        if (isBacktickString) {
                            continue;
                        }
                        if (!",".equals(token.getText()) && !":".equals(token.getText())) {
                            throw new CompilerException("expecting )", token);
                        }
                        next();
                    }
                }

                if (postEffect.contains(peek().getText())) {
                    Token postToken = peek();
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

}
