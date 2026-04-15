/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Node {

    final Node parent;
    final List<Token> tokens = new ArrayList<Token>();
    final List<Node> childs = new ArrayList<Node>();

    List<Token> document = new ArrayList<Token>();

    String description;
    boolean exclude;

    public Node() {
        this.parent = null;
    }

    public Node(Node parent) {
        this.parent = parent;
        this.parent.addChild(this);
    }

    public Node(Collection<Token> list) {
        this.parent = null;
        this.tokens.addAll(list);
    }

    public void accept(NodeVisitor visitor) {
        getChilds().iterator().forEachRemaining((child) -> {
            child.accept(visitor);
        });
    }

    public void addToken(Token token) {
        tokens.add(token);
    }

    public void addAllTokens(Collection<Token> list) {
        tokens.addAll(list);
    }

    public Token getStartToken() {
        if (tokens.isEmpty()) {
            return null;
        }
        return tokens.getFirst();
    }

    public Token getStopToken() {
        if (tokens.isEmpty()) {
            return null;
        }
        return tokens.getLast();
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public int getTokenCount() {
        return tokens.size();
    }

    public Token getToken(int index) {
        return tokens.get(index);
    }

    public boolean contains(Token token) {
        return tokens.contains(token);
    }

    public Node getParent() {
        return parent;
    }

    public void addChild(Node node) {
        childs.add(node);
    }

    public Node getChild(int index) {
        return childs.get(index);
    }

    public List<Node> getChilds() {
        return childs;
    }

    public int getChildCount() {
        return childs.size();
    }

    public int getStartIndex() {
        return !tokens.isEmpty() ? tokens.getFirst().start : -1;
    }

    public int getStopIndex() {
        return !tokens.isEmpty() ? tokens.getLast().stop : -1;
    }

    public String getText() {
        if (tokens.isEmpty()) {
            return "";
        }
        if (tokens.size() == 1 && tokens.getFirst().type == Token.EOF) {
            return "<EOF>";
        }
        int s = tokens.getFirst().start;
        int e = tokens.getLast().stop;
        TokenStream stream = getStartToken().getStream();
        return stream.getSource(s, e);
    }

    public void addDocument(Token token) {
        document.add(token);
    }

    public List<Token> getDocument() {
        return document;
    }

    public TokenIterator tokenIterator() {
        return new TokenIterator(tokens);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(Token comment) {
        int eol;

        this.description = comment.getText();

        while ((eol = this.description.indexOf("\r")) != -1) {
            this.description = this.description.substring(0, eol);
        }
        while ((eol = this.description.indexOf("\n")) != -1) {
            this.description = this.description.substring(0, eol);
        }

        while (this.description.startsWith("'")) {
            this.description = this.description.substring(1);
        }
        while (this.description.startsWith("{")) {
            this.description = this.description.substring(1);
        }
        while (this.description.endsWith("}")) {
            this.description = this.description.substring(0, this.description.length() - 1);
        }

        this.description = this.description.trim();
    }

    public boolean isExclude() {
        return exclude;
    }

    public void setExclude(boolean exclude) {
        this.exclude = exclude;
    }

    public String getPath() {
        StringBuilder sb = new StringBuilder();

        if (parent != null) {
            sb.append(parent.getPath());
        }
        sb.append("/");
        sb.append(getClass().getSimpleName());
        if (parent != null) {
            sb.append(parent.indexOf(this));
        }

        return sb.toString();
    }

    public int indexOf(Node node) {
        int i = 0;
        for (Node child : getChilds()) {
            if (child == node) {
                return i;
            }
            if (child.getClass().isInstance(node)) {
                i++;
            }
        }
        return -1;
    }

    public RootNode getRoot() {
        Node root = this;

        while (!(root instanceof RootNode)) {
            root = root.getParent();
            if (root == null) {
                return null;
            }
        }

        return (RootNode) root;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + dumpTokens();
    }

    protected String dumpTokens() {
        StringBuilder sb = new StringBuilder();
        sb.append(" [");

        Iterator<Token> iter = tokens.iterator();
        if (iter.hasNext()) {
            Token token = iter.next();
            sb.append(token.getText());
            int line = token.line;
            int index = token.stop + 1;
            while (iter.hasNext()) {
                token = iter.next();
                if (token.line != line || index > token.start) {
                    sb.append(" ");
                }
                else {
                    sb.repeat(" ", token.start - index);
                }
                sb.append(token.getText());
                line = token.line;
                index = token.stop + 1;
            }
        }

        sb.append("]");

        return sb.toString();
    }

}
