/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.maccasoft.propeller.model.Token;

public class Spin2StatementNode {

    Token token;

    protected Map<String, Spin2StatementNode> properties = new HashMap<String, Spin2StatementNode>();
    protected List<Spin2StatementNode> childs = new ArrayList<Spin2StatementNode>();

    protected String comment;
    protected Spin2StatementNode parent;

    protected Object data;
    protected Map<String, Object> keyedData = new HashMap<String, Object>();

    public static class Index extends Spin2StatementNode {

        public Index(Spin2StatementNode node) {
            super(node.token);
            this.properties.putAll(node.properties);
            this.childs.addAll(node.childs);
            this.data = node.data;
            this.keyedData = node.keyedData;
        }
    }

    public static class Method extends Spin2StatementNode {

        public Method(Spin2StatementNode node) {
            super(node.token);
            this.properties.putAll(node.properties);
            this.childs.addAll(node.childs);
            this.data = node.data;
            this.keyedData = node.keyedData;
        }
    }

    public static class Argument extends Spin2StatementNode {

        public Argument(Spin2StatementNode node) {
            super(node.token);
            this.properties.putAll(node.properties);
            this.childs.addAll(node.childs);
            this.data = node.data;
            this.keyedData = node.keyedData;
        }
    }

    public Spin2StatementNode(Token token) {
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

    public void setProperty(String name, Spin2StatementNode node) {
        properties.put(name, node);
    }

    public String getPropertiesText() {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Spin2StatementNode> entry : properties.entrySet()) {
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

    public Spin2StatementNode getProperty(String name) {
        return properties.get(name);
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
                    if (childs.size() == 3) {
                        sb.append(childs.get(0));
                        sb.append(" ? ");
                        sb.append(childs.get(1));
                        sb.append(" : ");
                        sb.append(childs.get(2));
                    }
                    else {
                        sb.append(token.getText());
                        for (int i = 0; i < childs.size(); i++) {
                            sb.append(" ");
                            sb.append(childs.get(i));
                        }
                    }
                }
                else {
                    if (childs.size() > 0) {
                        sb.append(childs.get(0));
                        if (childs.size() > 1) {
                            sb.append(" ");
                            sb.append(token.getText());
                            sb.append(" ");
                            sb.append(childs.get(1));
                        }
                    }
                    else {
                        sb.append(" ");
                        sb.append(token.getText());
                    }
                }
                break;
            default: {
                sb.append(token.getText());

                int i = 0;
                while (i < childs.size()) {
                    if (!(childs.get(i) instanceof Index)) {
                        break;
                    }
                    sb.append("[");
                    sb.append(childs.get(i++));
                    sb.append("]");
                }
                if (i < childs.size() && (childs.get(i) instanceof Argument)) {
                    sb.append("(");
                    sb.append(childs.get(i++));
                    while (i < childs.size()) {
                        if (!(childs.get(i) instanceof Argument)) {
                            break;
                        }
                        sb.append(", ");
                        sb.append(childs.get(i++));
                    }
                    sb.append(")");
                }
                while (i < childs.size()) {
                    sb.append(" ");
                    sb.append(childs.get(i++));
                }
                break;
            }
        }
        return sb.toString();
    }

}
