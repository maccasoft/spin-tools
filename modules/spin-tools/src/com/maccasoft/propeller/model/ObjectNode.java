/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

}