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
import java.util.List;

import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin2.Spin2Debug.DebugDataObject;

public class Spin2StatementNode {

    Token token;
    Token firstToken;
    Token lastToken;

    boolean method;
    int returnLongs;

    protected List<Spin2StatementNode> childs = new ArrayList<Spin2StatementNode>();

    protected String comment;
    protected Spin2StatementNode parent;

    DebugDataObject debugData;

    public static class Index extends Spin2StatementNode {

        public Index(Spin2StatementNode node, Token firstToken, Token lastToken) {
            super(node.token);
            this.firstToken = firstToken;
            this.lastToken = lastToken;
            this.childs.addAll(node.childs);
            this.method = node.method;
            this.returnLongs = node.returnLongs;
        }
    }

    public static class Argument extends Spin2StatementNode {

        public Argument(Spin2StatementNode node) {
            super(node.token);
            this.firstToken = node.firstToken;
            this.lastToken = node.lastToken;
            this.childs.addAll(node.childs);
            this.method = node.method;
            this.returnLongs = node.returnLongs;
        }

        public Argument(Token token) {
            super(token);
        }
    }

    public Spin2StatementNode(Token token) {
        this.token = token;
    }

    public Spin2StatementNode(Token token, boolean method) {
        this.token = token;
        this.method = method;
    }

    public int getType() {
        return token.type;
    }

    public String getText() {
        return token.getText();
    }

    public Token getToken() {
        return token;
    }

    public Token getFirstToken() {
        return firstToken;
    }

    public void setFirstToken(Token firstToken) {
        this.firstToken = firstToken;
    }

    public Token getLastToken() {
        return lastToken;
    }

    public void setLastToken(Token lastToken) {
        this.lastToken = lastToken;
    }

    public List<Token> getTokens() {
        List<Token> list = new ArrayList<>();

        if (firstToken != null) {
            list.add(firstToken);
        }

        list.add(token);

        int i = 0;
        while (i < childs.size()) {
            List<Token> childTokens = childs.get(i++).getTokens();
            if (childTokens.size() != 0) {
                if (childTokens.get(0).start > list.get(list.size() - 1).start) {
                    list.addAll(childTokens);
                }
                else if (childTokens.get(childTokens.size() - 1).start < list.get(0).start) {
                    list.addAll(0, childTokens);
                }
            }
        }

        if (lastToken != null) {
            list.add(lastToken);
        }

        return list;
    }

    public Spin2StatementNode getParent() {
        return parent;
    }

    public void addChild(Spin2StatementNode node) {
        node.parent = this;
        childs.add(node);
    }

    public void addChild(int index, Spin2StatementNode node) {
        node.parent = this;
        childs.add(index, node);
    }

    public Spin2StatementNode getChild(int index) {
        return childs.get(index);
    }

    public int getChildCount() {
        return childs.size();
    }

    public List<Spin2StatementNode> getChilds() {
        return childs;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isMethod() {
        return method;
    }

    public void setMethod(boolean method) {
        this.method = method;
    }

    public int getReturnLongs() {
        return returnLongs;
    }

    public void setReturnLongs(int returnLongs) {
        this.returnLongs = returnLongs;
    }

    @Override
    public String toString() {
        int[] result = new int[] {
            token.start, token.stop
        };
        if (firstToken != null) {
            result[0] = Math.min(result[0], firstToken.start);
            result[1] = Math.max(result[1], firstToken.stop);
        }
        if (lastToken != null) {
            result[0] = Math.min(result[0], lastToken.start);
            result[1] = Math.max(result[1], lastToken.stop);
        }
        for (Spin2StatementNode childNode : getChilds()) {
            result = computeStartStopIndex(childNode, result);
        }
        return token.getStream().getSource(result[0], result[1]);
    }

    int[] computeStartStopIndex(Spin2StatementNode node, int[] result) {
        result[0] = Math.min(result[0], node.getToken().start);
        result[1] = Math.max(result[1], node.getToken().stop);

        if (node.firstToken != null) {
            result[0] = Math.min(result[0], node.firstToken.start);
            result[1] = Math.max(result[1], node.firstToken.stop);
        }
        if (node.lastToken != null) {
            result[0] = Math.min(result[0], node.lastToken.start);
            result[1] = Math.max(result[1], node.lastToken.stop);
        }

        for (Spin2StatementNode childNode : node.getChilds()) {
            result = computeStartStopIndex(childNode, result);
        }

        return result;
    }

    public DebugDataObject getDebugData() {
        return debugData;
    }

    public void setDebugData(DebugDataObject debugData) {
        this.debugData = debugData;
    }

}
