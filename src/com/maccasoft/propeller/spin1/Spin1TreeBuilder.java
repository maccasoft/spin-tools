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
import java.util.List;
import java.util.Map;

import com.maccasoft.propeller.model.Token;

public class Spin1TreeBuilder {

    static int highestPrecedence = 1;
    static Map<String, Integer> precedence = new HashMap<String, Integer>();
    static {
        precedence.put("(", highestPrecedence);
        precedence.put(")", highestPrecedence);
        highestPrecedence++;

        precedence.put("[", highestPrecedence);
        precedence.put("]", highestPrecedence);
        highestPrecedence++;

        precedence.put("\\", highestPrecedence);
        highestPrecedence++;

        precedence.put(">>", highestPrecedence);
        precedence.put("<<", highestPrecedence);
        precedence.put("~>", highestPrecedence);
        precedence.put("->", highestPrecedence);
        precedence.put("<-", highestPrecedence);
        precedence.put("><", highestPrecedence);
        //precedence.put("ZEROX", 3);
        //precedence.put("SIGNX", 3);
        highestPrecedence++;

        precedence.put("&", highestPrecedence);
        highestPrecedence++;
        precedence.put("^", highestPrecedence);
        precedence.put("|", highestPrecedence);
        highestPrecedence++;

        precedence.put("*", highestPrecedence);
        precedence.put("**", highestPrecedence);
        precedence.put("/", highestPrecedence);
        //precedence.put("+/", 7);
        precedence.put("//", highestPrecedence);
        //precedence.put("+//", 7);
        //precedence.put("SCA", 7);
        //precedence.put("SCAS", 7);
        //precedence.put("FRAC", 7);
        highestPrecedence++;

        precedence.put("+", highestPrecedence);
        precedence.put("-", highestPrecedence);
        highestPrecedence++;

        precedence.put("#>", highestPrecedence);
        precedence.put("<#", highestPrecedence);
        highestPrecedence++;

        precedence.put("<", highestPrecedence);
        //precedence.put("+<", 11);
        precedence.put("=<", highestPrecedence);
        //precedence.put("+<=", 11);
        precedence.put("==", highestPrecedence);
        precedence.put("<>", highestPrecedence);
        precedence.put("=>", highestPrecedence);
        //precedence.put("+>=", 11);
        precedence.put(">", highestPrecedence);
        //precedence.put("+>", 11);
        //precedence.put("<=>", 11);
        highestPrecedence++;

        //precedence.put("&&", 12);
        precedence.put("AND", highestPrecedence);
        //precedence.put("^^", 12);
        //precedence.put("XOR", 12);
        //precedence.put("||", 12);
        highestPrecedence++;
        precedence.put("OR", highestPrecedence);
        highestPrecedence++;

        precedence.put("..", highestPrecedence);
        highestPrecedence++;

        precedence.put(",", highestPrecedence);
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
        //precedence.put("ZEROX=", highestPrecedence);
        //precedence.put("SIGNX=", highestPrecedence);

        precedence.put("&=", highestPrecedence);
        precedence.put("^=", highestPrecedence);
        precedence.put("|=", highestPrecedence);

        precedence.put("*=", highestPrecedence);
        precedence.put("**=", highestPrecedence);
        precedence.put("/=", highestPrecedence);
        //precedence.put("+/=", highestPrecedence);
        precedence.put("//=", highestPrecedence);
        //precedence.put("+//=", highestPrecedence);
        //precedence.put("SCA=", highestPrecedence);
        //precedence.put("SCAS=", highestPrecedence);
        //precedence.put("FRAC=", highestPrecedence);

        precedence.put("+=", highestPrecedence);
        precedence.put("-=", highestPrecedence);

        precedence.put("#>=", highestPrecedence);
        precedence.put("<#=", highestPrecedence);

        precedence.put("<=", highestPrecedence);
        //precedence.put("+<=", highestPrecedence);
        precedence.put("=<=", highestPrecedence);
        //precedence.put("+<==", highestPrecedence);
        precedence.put("===", highestPrecedence);
        precedence.put("<>=", highestPrecedence);
        precedence.put("=>=", highestPrecedence);
        //precedence.put("+>==", highestPrecedence);
        precedence.put(">=", highestPrecedence);
        //precedence.put("+>=", highestPrecedence);
        //precedence.put("<=>=", highestPrecedence);

        //precedence.put("&&=", highestPrecedence);
        precedence.put("AND=", highestPrecedence);
        //precedence.put("^^=", highestPrecedence);
        //precedence.put("XOR=", highestPrecedence);
        //precedence.put("||=", highestPrecedence);
        precedence.put("OR=", highestPrecedence);
    }

    List<Token> tokens = new ArrayList<Token>();

    public void addToken(Token token) {
        tokens.add(token);
    }

    public Spin1StatementNode getRoot() {
        Spin1StatementNode root = buildTree(highestPrecedence, tokens);
        tokens.clear();
        return root;
    }

    public Spin1StatementNode buildTree(List<Token> tokens) {
        Spin1StatementNode root = buildTree(highestPrecedence, tokens);
        return root;
    }

    Spin1StatementNode buildTree(int level, List<Token> tokens) {
        int nested;

        if (tokens.size() == 1) {
            return new Spin1StatementNode(tokens.get(0));
        }
        if (tokens.size() == 2) {
            Spin1StatementNode node = new Spin1StatementNode(tokens.get(0));
            node.addChild(new Spin1StatementNode(tokens.get(1)));
            return node;
        }

        String text = tokens.get(0).getText();
        if ("-".equals(text) || "++".equals(text) || "--".equals(text) || "~".equals(text) || "~~".equals(text) || "?".equals(text) || "||".equals(text)) {
            Spin1StatementNode node = new Spin1StatementNode(tokens.get(0));
            node.addChild(buildTree(new ArrayList<Token>(tokens.subList(1, tokens.size()))));
            return node;
        }

        if (level == 0) {
            throw new RuntimeException("expression syntax error");
        }

        int i = 0;
        List<Token> left = new ArrayList<Token>();

        while (i < tokens.size()) {
            Token token = tokens.get(i);

            Integer p = precedence.get(token.getText().toUpperCase());
            if (p != null && p.intValue() == level) {
                Spin1StatementNode node = new Spin1StatementNode(token);

                if (left.size() != 0) {
                    Spin1StatementNode child = buildTree(level, left);
                    node.addChild(child);
                }

                i++;

                if ("[".equals(node.getText())) {
                    List<Token> right = new ArrayList<Token>();

                    while (i < tokens.size()) {
                        token = tokens.get(i);
                        if ("]".equals(token.getText())) {
                            if (right.size() != 0) {
                                Spin1StatementNode child = buildTree(highestPrecedence, right);
                                node.addChild(child);
                                right.clear();
                            }
                            if (i + 1 < tokens.size()) {
                                if ("[".equals(tokens.get(i + 1).getText())) {
                                    i++;
                                }
                            }
                        }
                        else {
                            right.add(token);
                        }
                        i++;
                    }

                    if (right.size() != 0) {
                        Spin1StatementNode child = buildTree(highestPrecedence, right);
                        node.addChild(child);
                        right.clear();
                    }
                }
                else {
                    List<Token> right = new ArrayList<Token>(tokens.subList(i, tokens.size()));
                    if (right.size() != 0) {
                        if ("(".equals(node.getText()) && ")".equals(right.get(right.size() - 1).getText())) {
                            right.remove(right.size() - 1);
                        }
                    }
                    if (right.size() != 0) {
                        Spin1StatementNode child = buildTree("(".equals(node.getText()) || "[".equals(node.getText()) ? highestPrecedence : level, right);
                        if (",".equals(token.getText()) && ",".equals(child.getText())) {
                            node.getChilds().addAll(child.getChilds());
                        }
                        else {
                            node.addChild(child);
                        }
                    }
                }

                if ("(".equals(node.getText())) {
                    if (node.getChildCount() == 1) {
                        return node.getChild(0);
                    }
                    else {
                        Spin1StatementNode newNode = new Spin1StatementNode(node.getChild(0).getToken());
                        for (int x = 1; x < node.getChildCount(); x++) {
                            newNode.addChild(node.getChild(x));
                        }
                        return newNode;
                    }

                }

                return node;
            }

            left.add(token);

            if ("(".equals(token.getText())) {
                i++;
                nested = 0;
                while (i < tokens.size()) {
                    token = tokens.get(i);
                    left.add(token);
                    if ("(".equals(token.getText())) {
                        nested++;
                    }
                    else if (")".equals(token.getText())) {
                        if (nested == 0) {
                            break;
                        }
                        nested--;
                    }
                    i++;
                }
            }
            else if ("[".equals(token.getText())) {
                i++;
                nested = 0;
                while (i < tokens.size()) {
                    token = tokens.get(i);
                    left.add(token);
                    if ("[".equals(token.getText())) {
                        nested++;
                    }
                    else if ("]".equals(token.getText())) {
                        if (nested == 0) {
                            break;
                        }
                        nested--;
                    }
                    i++;
                }
            }

            i++;
        }

        return buildTree(level - 1, tokens);
    }

    public static void main(String[] args) {
        String text;

        /*String text = "function(1 + 2 * 3, 4, (5 + 6) * 7)";
        System.out.println(text);
        System.out.println(parse(text));

        text = "a := function1(1, 2) + function2(3) * function3(4, 5, 6)";
        System.out.println(text);
        System.out.println(parse(text));

        text = "a := b[c]";
        System.out.println(text);
        System.out.println(parse(text));

        text = "a := b[c][0]";
        System.out.println(text);
        System.out.println(parse(text));

        text = "a := b[c][0][1]";
        System.out.println(text);
        System.out.println(parse(text));

        text = "a := b[0+1*2] + e[4 + c * d] * long[@a][1]";
        System.out.println(text);
        System.out.println(parse(text));
        
        String text = "a[1]~~";
        System.out.println(text);
        System.out.println(parse(text));
        
        text = "~~a[1]";
        System.out.println(text);
        System.out.println(parse(text));
        
        text = "    a := lookup(b : 10, 20..30, 40)";
        System.out.println(text);
        System.out.println(parse(text));
        
        text = "((chr := byte[stringptr][index++]) == 0)";
        System.out.println(text);
        System.out.println(parse(text));*/

        //text = "z_pad or (div == 1)";
        //System.out.println(text);
        //System.out.println(parse(text));

        text = "chr := -15 + --chr & %11011111 + 39*(chr > 56)";
        System.out.println(text);
        System.out.println(parse(text));
    }

    static String parse(String text) {
        List<Token> tokens = new ArrayList<Token>();

        Spin1TokenStream stream = new Spin1TokenStream(text);
        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF) {
                break;
            }
            tokens.add(token);
        }

        Spin1TreeBuilder builder = new Spin1TreeBuilder();
        Spin1StatementNode root = builder.buildTree(tokens);

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
