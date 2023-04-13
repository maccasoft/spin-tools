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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.maccasoft.propeller.model.Token;

public class Spin1StatementNode {

    Token token;

    Map<String, Spin1StatementNode> properties = new HashMap<String, Spin1StatementNode>();
    List<Spin1StatementNode> childs = new ArrayList<Spin1StatementNode>();

    String comment;
    Spin1StatementNode parent;

    protected Object data;
    protected Map<String, Object> keyedData = new HashMap<String, Object>();

    public static class Index extends Spin1StatementNode {

        public Index(Spin1StatementNode node) {
            super(node.token);
            this.properties.putAll(node.properties);
            this.childs.addAll(node.childs);
            this.data = node.data;
            this.keyedData = node.keyedData;
        }
    }

    public static class Method extends Spin1StatementNode {

        public Method(Token token) {
            super(token);
        }

        public Method(Spin1StatementNode node) {
            super(node.token);
            this.properties.putAll(node.properties);
            this.childs.addAll(node.childs);
            this.data = node.data;
            this.keyedData = node.keyedData;
        }
    }

    public static class Argument extends Spin1StatementNode {

        public Argument(Spin1StatementNode node) {
            super(node.token);
            this.properties.putAll(node.properties);
            this.childs.addAll(node.childs);
            this.data = node.data;
            this.keyedData = node.keyedData;
        }
    }

    public Spin1StatementNode(Token token) {
        this.token = token;
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

    public List<Token> getTokens() {
        List<Token> list = new ArrayList<>();

        if (childs.size() == 0) {
            list.add(token);
        }
        else if (childs.size() == 1) {
            list.add(token);
            list.addAll(childs.get(0).getTokens());
        }
        else {
            int i = 0;
            while (i < childs.size()) {
                if (i != 0) {
                    list.add(token);
                }
                list.addAll(childs.get(i++).getTokens());
            }
        }

        return list;
    }

    public void setProperty(String name, Spin1StatementNode node) {
        properties.put(name, node);
    }

    public String getPropertiesText() {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Spin1StatementNode> entry : properties.entrySet()) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append("[");
            sb.append(entry.getKey());
            sb.append("(");
            sb.append(entry.getValue());
            sb.append(")]");
        }
        return sb.toString();
    }

    public Spin1StatementNode getProperty(String name) {
        return properties.get(name);
    }

    public Spin1StatementNode getParent() {
        return parent;
    }

    public void addChild(Spin1StatementNode node) {
        node.parent = this;
        childs.add(node);
    }

    public void addChild(int index, Spin1StatementNode node) {
        node.parent = this;
        childs.add(index, node);
    }

    public Spin1StatementNode getChild(int index) {
        return childs.get(index);
    }

    public int getChildCount() {
        return childs.size();
    }

    public List<Spin1StatementNode> getChilds() {
        return childs;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (token.type) {
            case Token.OPERATOR:
                if (",".equals(token.getText())) {
                    for (int i = 0; i < childs.size(); i++) {
                        if (i != 0) {
                            sb.append(", ");
                        }
                        sb.append(childs.get(i));
                    }
                }
                else if ("(".equals(token.getText())) {
                    sb.append("(");
                    for (int i = 0; i < childs.size(); i++) {
                        sb.append(childs.get(i));
                    }
                    sb.append(")");
                }
                else if ("[".equals(token.getText())) {
                    sb.append(childs.get(0));
                    sb.append("[");
                    for (int i = 1; i < childs.size(); i++) {
                        sb.append(childs.get(i));
                    }
                    sb.append("]");
                }
                else if ("?".equals(token.getText())) {
                    sb.append(childs.get(0));
                    sb.append(" ? ");
                    sb.append(childs.get(1));
                    sb.append(" : ");
                    sb.append(childs.get(2));
                }
                else {
                    sb.append(childs.get(0));
                    sb.append(" ");
                    sb.append(token.getText());
                    sb.append(" ");
                    sb.append(childs.get(1));
                }
                break;
            case Token.FUNCTION:
                sb.append(token.getText());
                sb.append("(");
                if (childs.size() != 0) {
                    sb.append(childs.get(0));
                }
                sb.append(")");
                break;
            default:
                sb.append(token.getText());
                for (int i = 0; i < childs.size(); i++) {
                    sb.append(" ");
                    sb.append(childs.get(i));
                }
                break;
        }
        return sb.toString();
    }

}
