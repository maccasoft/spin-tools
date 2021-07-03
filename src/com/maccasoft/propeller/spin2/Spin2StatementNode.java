/*
 * Copyright (c) 2021 Marco Maccaferri and others.
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

public class Spin2StatementNode {

    public static final int STRING = 3;
    public static final int NUMBER = 4;
    public static final int OPERATOR = 6;
    public static final int FUNCTION = 9;

    int type;
    String text;

    Map<String, Spin2StatementNode> properties = new HashMap<String, Spin2StatementNode>();
    private List<Spin2StatementNode> childs = new ArrayList<Spin2StatementNode>();

    String comment;
    Spin2StatementNode parent;

    protected Object data;
    protected Map<String, Object> keyedData = new HashMap<String, Object>();

    public Spin2StatementNode(int type, String text) {
        this.type = type;
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public String getText() {
        return text;
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
        switch (type) {
            case OPERATOR:
                if (",".equals(text)) {
                    for (int i = 0; i < childs.size(); i++) {
                        if (i != 0) {
                            sb.append(", ");
                        }
                        sb.append(childs.get(i));
                    }
                }
                else if ("(".equals(text)) {
                    sb.append("(");
                    for (int i = 0; i < childs.size(); i++) {
                        sb.append(childs.get(i));
                    }
                    sb.append(")");
                }
                else if ("[".equals(text)) {
                    sb.append(childs.get(0));
                    sb.append("[");
                    for (int i = 1; i < childs.size(); i++) {
                        sb.append(childs.get(i));
                    }
                    sb.append("]");
                }
                else {
                    sb.append(childs.get(0).toString());
                    sb.append(" ");
                    sb.append(text);
                    sb.append(" ");
                    sb.append(childs.get(1).toString());
                }
                break;
            case FUNCTION:
                sb.append(text);
                sb.append("(");
                if (childs.size() != 0) {
                    sb.append(childs.get(0));
                }
                sb.append(")");
                break;
            default:
                sb.append(text);
                for (int i = 0; i < childs.size(); i++) {
                    sb.append(" ");
                    sb.append(childs.get(i));
                }
                break;
        }
        return sb.toString();
    }

}
