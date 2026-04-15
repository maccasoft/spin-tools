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
import java.util.List;

public class DataLineNode extends Node {

    public static class ParameterNode extends Node {

        public ExpressionNode count;

        public ParameterNode(Node parent) {
            super(parent);
        }

        public ExpressionNode getCount() {
            return count;
        }

    }

    public static class ModifierNode extends Node {

        public ModifierNode(Node parent) {
            super(parent);
        }

    }

    public Token label;
    public Token condition;
    public Token instruction;
    public List<ParameterNode> parameters = new ArrayList<ParameterNode>();
    public ModifierNode modifier;

    public DataLineNode(Node parent) {
        super(parent);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitDataLine(this);
    }

    @Override
    public int getStartIndex() {
        return !tokens.isEmpty() ? tokens.getFirst().start - tokens.getFirst().column : -1;
    }

    @Override
    public String getPath() {
        StringBuilder sb = new StringBuilder();

        if (parent != null) {
            sb.append(parent.getPath());
        }
        sb.append("/");
        if (label != null) {
            sb.append(label.getText().toUpperCase());
        }
        else {
            sb.append(parent.indexOf(this));
        }

        return sb.toString();
    }

    public String getText() {
        if (tokens.isEmpty()) {
            return "";
        }
        if (tokens.size() == 1 && tokens.getFirst().type == Token.EOF) {
            return "<EOF>";
        }
        int s = tokens.getFirst().start - tokens.getFirst().column;
        int e = tokens.getLast().stop;
        TokenStream stream = getStartToken().getStream();
        return stream.getSource(s, e);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getClass().getSimpleName());
        if (label != null) {
            sb.append(" label=").append(label.getText());
        }
        if (condition != null) {
            sb.append(" condition=").append(condition.getText());
        }
        if (instruction != null) {
            sb.append(" instruction=").append(instruction.getText());
        }
        sb.append(dumpTokens());

        return sb.toString();
    }

}
