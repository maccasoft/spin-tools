/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
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

public class MethodNode extends Node {

    public Token type;
    public Token name;
    public List<ParameterNode> parameters = new ArrayList<ParameterNode>();
    public List<ReturnNode> returnVariables = new ArrayList<ReturnNode>();
    public List<LocalVariableNode> localVariables = new ArrayList<LocalVariableNode>();

    public static class ParameterNode extends Node {

        public Token type;
        public Token identifier;
        public ExpressionNode defaultValue;

        public ParameterNode(MethodNode parent) {
            super(parent);
            parent.parameters.add(this);
        }

        @Override
        public void addToken(Token token) {
            tokens.add(token);
        }

        public Token getType() {
            return type;
        }

        public Token getIdentifier() {
            return identifier;
        }

        public ExpressionNode getDefaultValue() {
            return defaultValue;
        }
    }

    public static class ReturnNode extends Node {

        public Token type;
        public Token identifier;

        public ReturnNode(MethodNode parent) {
            super(parent);
            parent.returnVariables.add(this);
        }

        @Override
        public void addToken(Token token) {
            tokens.add(token);
        }

        public Token getType() {
            return type;
        }

        public Token getIdentifier() {
            return identifier;
        }
    }

    public static class LocalVariableNode extends Node {

        public Token type;
        public Token identifier;
        public ExpressionNode size;

        public LocalVariableNode(MethodNode parent) {
            super(parent);
            parent.localVariables.add(this);
        }

        @Override
        public void addToken(Token token) {
            tokens.add(token);
        }

        public Token getType() {
            return type;
        }

        public Token getIdentifier() {
            return identifier;
        }

        public ExpressionNode getSize() {
            return size;
        }
    }

    public MethodNode(Node parent, Token type) {
        super(parent);
        this.type = type;
        addToken(type);
    }

    public MethodNode(Node parent, Token type, Token name) {
        super(parent);
        this.type = type;
        this.name = name;
        if (type != null) {
            addToken(type);
        }
        if (name != null) {
            addToken(name);
        }
    }

    @Override
    public void accept(NodeVisitor visitor) {
        if (visitor.visitMethod(this)) {
            super.accept(visitor);
        }
    }

    public Token getType() {
        return type;
    }

    public boolean isPublic() {
        return "PUB".equalsIgnoreCase(type.getText());
    }

    public Token getName() {
        return name;
    }

    public List<ParameterNode> getParameters() {
        return parameters;
    }

    public int getParametersCount() {
        return parameters.size();
    }

    public int getMinParametersCount() {
        int count = parameters.size();
        while (count > 0 && parameters.get(count - 1).getDefaultValue() != null) {
            count--;
        }
        return count;
    }

    public ParameterNode getParameter(int index) {
        return parameters.get(index);
    }

    public List<ReturnNode> getReturnVariables() {
        return returnVariables;
    }

    public ReturnNode getReturnVariable(int index) {
        return returnVariables.get(index);
    }

    public List<LocalVariableNode> getLocalVariables() {
        return localVariables;
    }

    public LocalVariableNode getLocalVariable(int index) {
        return localVariables.get(index);
    }

    @Override
    public String getPath() {
        StringBuilder sb = new StringBuilder();

        sb.append("/");
        sb.append(type.getText().toUpperCase());
        if (name != null) {
            sb.append(" ");
            sb.append(name.getText().toUpperCase());
        }
        else {
            sb.append(parent.indexOf(this));
        }

        return sb.toString();
    }

}