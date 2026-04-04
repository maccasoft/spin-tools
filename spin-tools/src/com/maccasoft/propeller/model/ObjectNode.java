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

public class ObjectNode extends Node {

    public Token name;
    public ExpressionNode count;
    public Token file;
    public List<ParameterNode> parameters = new ArrayList<ParameterNode>();

    public static class ParameterNode extends Node {

        public Token identifier;
        public ExpressionNode expression;

        public ParameterNode(ObjectNode parent) {
            super(parent);
            parent.parameters.add(this);
        }

        @Override
        public void addToken(Token token) {
            tokens.add(token);
            parent.addToken(token);
        }

        public Token getIdentifier() {
            return identifier;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append(getClass().getSimpleName());
            if (identifier != null) {
                sb.append(" identifier=").append(identifier.getText());
            }
            sb.append(dumpTokens());

            return sb.toString();
        }

    }

    public ObjectNode(Node parent) {
        super(parent);
    }

    public boolean equals(ObjectNode obj) {
        if (!name.equals(obj.name)) {
            return false;
        }
        return true;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitObject(this);
    }

    public String getFileName() {
        if (file == null) {
            return null;
        }
        String text = file.getText();
        if (text.startsWith("\"")) {
            text = text.substring(1);
        }
        if (text.endsWith("\"")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    @Override
    public String getPath() {
        StringBuilder sb = new StringBuilder();

        if (parent != null) {
            sb.append(parent.getPath());
        }
        sb.append("/");
        if (name != null) {
            sb.append(name.getText().toUpperCase());
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
        if (name != null) {
            sb.append(" name=").append(name.getText());
        }
        if (file != null) {
            sb.append(" file=").append(file.getText());
        }
        sb.append(dumpTokens());

        return sb.toString();
    }

}
