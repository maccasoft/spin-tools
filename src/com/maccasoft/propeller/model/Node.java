/*
 * Copyright (c) 2021 Marco Maccaferri and others.
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

    protected final Node parent;
    protected final List<Token> tokens = new ArrayList<Token>();
    protected final List<Node> childs = new ArrayList<Node>();

    public Node() {
        this.parent = null;
    }

    public Node(Node parent) {
        this.parent = parent;
        this.parent.childs.add(this);
    }

    public void accept(NodeVisitor visitor) {
        for (Node child : childs) {
            child.accept(visitor);
        }
    }

    public void addToken(Token token) {
        tokens.add(token);
        if (parent != null) {
            parent.addToken(token);
        }
    }

    public void addAllTokens(Collection<Token> list) {
        tokens.addAll(list);
        if (parent != null) {
            parent.addAllTokens(list);
        }
    }

    public Token getStartToken() {
        return tokens.get(0);
    }

    public Token getStopToken() {
        return tokens.get(tokens.size() - 1);
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public Token getToken(int index) {
        return tokens.get(index);
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

    public int getStartIndex() {
        return tokens.size() != 0 ? tokens.get(0).start : -1;
    }

    public int getStopIndex() {
        return tokens.size() != 0 ? tokens.get(tokens.size() - 1).stop : -1;
    }

    public String getText() {
        TokenStream stream = getStartToken().getStream();
        return stream.getSource(getStartIndex(), getStopIndex());
    }

    @Override
    public String toString() {
        return getText();
    }

}
