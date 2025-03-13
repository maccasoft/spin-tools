/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.util.ArrayList;
import java.util.List;

import com.maccasoft.propeller.model.Token;

public class Spin1StatementNode {

    Token token;

    List<Spin1StatementNode> childs = new ArrayList<Spin1StatementNode>();

    String comment;
    Spin1StatementNode parent;

    public static class Index extends Spin1StatementNode {

        public Index(Spin1StatementNode node) {
            super(node.token);
            this.childs.addAll(node.childs);
        }
    }

    public static class Method extends Spin1StatementNode {

        public Method(Token token) {
            super(token);
        }

        public Method(Spin1StatementNode node) {
            super(node.token);
            this.childs.addAll(node.childs);
        }
    }

    public static class Argument extends Spin1StatementNode {

        public Argument(Spin1StatementNode node) {
            super(node.token);
            this.childs.addAll(node.childs);
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

        return list;
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
