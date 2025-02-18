/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Node {

    final Node parent;
    final List<Token> tokens = new ArrayList<Token>();
    final List<Node> childs = new ArrayList<Node>();

    List<Token> document = new ArrayList<Token>();
    List<Token> comments = new ArrayList<Token>();

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
        for (Node child : childs) {
            child.accept(visitor);
        }
    }

    public void addToken(Token token) {
        tokens.add(token);
    }

    public void addAllTokens(Collection<Token> list) {
        tokens.addAll(list);
    }

    public Token getStartToken() {
        if (tokens.size() == 0) {
            return null;
        }
        return tokens.get(0);
    }

    public Token getStopToken() {
        if (tokens.size() == 0) {
            return null;
        }
        return tokens.get(tokens.size() - 1);
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
        return tokens.size() != 0 ? tokens.get(0).start : -1;
    }

    public int getStopIndex() {
        return tokens.size() != 0 ? tokens.get(tokens.size() - 1).stop : -1;
    }

    public String getText() {
        if (getTokenCount() == 0) {
            return "";
        }
        TokenStream stream = getStartToken().getStream();
        return stream.getSource(getStartIndex(), getStopIndex());
    }

    public void addDocument(Token token) {
        document.add(token);
    }

    public List<Token> getDocument() {
        return document;
    }

    public void addComment(Token token) {
        comments.add(token);
    }

    public void addAllComments(Collection<Token> c) {
        comments.addAll(c);
    }

    public List<Token> getComments() {
        return comments;
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

    @Override
    public String toString() {
        if (tokens.size() == 0) {
            return "";
        }
        if (tokens.size() == 1 && tokens.get(0).type == Token.EOF) {
            return "<EOF>";
        }
        int s = tokens.size() != 0 ? tokens.get(0).start - tokens.get(0).column : -1;
        int e = tokens.size() != 0 ? tokens.get(tokens.size() - 1).stop : -1;
        TokenStream stream = getStartToken().getStream();
        return stream.getSource(s, e);
    }

}
