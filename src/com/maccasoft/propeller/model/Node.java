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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

    protected final Node parent;
    protected final List<Token> tokens = new ArrayList<Token>();
    protected final List<Node> childs = new ArrayList<Node>();

    protected Object data;
    protected Map<String, Object> keyedData = new HashMap<String, Object>();

    public List<Token> document = new ArrayList<Token>();

    public Node() {
        this.parent = null;
    }

    public Node(Node parent) {
        this.parent = parent;
        this.parent.childs.add(this);
    }

    public Node(Collection<Token> list) {
        this.parent = null;
        tokens.addAll(list);
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
        return tokens.get(0);
    }

    public Token getStopToken() {
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData(String key) {
        return keyedData.get(key);
    }

    public void setData(String key, Object data) {
        this.keyedData.put(key, data);
    }

    public void addDocument(Token token) {
        document.add(token);
    }

    public List<Token> getDocument() {
        return document;
    }

    @Override
    public String toString() {
        if (tokens.size() == 0) {
            return "";
        }
        if (tokens.size() == 1 && tokens.get(0).type == Token.EOF) {
            return "<EOF>";
        }
        return getText();
    }

}
