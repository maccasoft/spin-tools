/*
 * Copyright (c) 2023 Marco Maccaferri and others.
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
import java.util.Objects;

public class FunctionNode extends Node {

    public Token modifier;
    public Token type;
    public Token identifier;
    public List<ParameterNode> parameters = new ArrayList<ParameterNode>();
    public List<ReturnNode> returnVariables = new ArrayList<ReturnNode>();
    public List<LocalVariableNode> localVariables = new ArrayList<LocalVariableNode>();

    public static class ParameterNode extends Node {

        public Token type;
        public Token identifier;

        public ParameterNode(FunctionNode parent) {
            super(parent);
            parent.parameters.add(this);
        }

        @Override
        public void addToken(Token token) {
            tokens.add(token);
            parent.addToken(token);
        }

        public Token getType() {
            return type;
        }

        public Token getIdentifier() {
            return identifier;
        }

    }

    public static class ReturnNode extends Node {

        public Token identifier;

        public ReturnNode(FunctionNode parent) {
            super(parent);
            parent.returnVariables.add(this);
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

    public static class LocalVariableNode extends Node {

        public Token type;
        public Token identifier;
        public ExpressionNode size;

        public LocalVariableNode(FunctionNode parent) {
            super(parent);
            parent.localVariables.add(this);
        }

        @Override
        public void addToken(Token token) {
            tokens.add(token);
            parent.addToken(token);
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

        @Override
        public int hashCode() {
            return Objects.hash(identifier);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            LocalVariableNode other = (LocalVariableNode) obj;
            return Objects.equals(identifier, other.identifier);
        }

    }

    public FunctionNode(Node parent, Token modifier, Token type, Token identifier) {
        super(parent);
        this.modifier = modifier;
        this.type = type;
        this.identifier = identifier;
        if (modifier != null) {
            tokens.add(modifier);
        }
        if (type != null) {
            tokens.add(type);
        }
        if (identifier != null) {
            tokens.add(identifier);
        }
    }

    @Override
    public void accept(NodeVisitor visitor) {
        if (visitor.visitFunction(this)) {
            super.accept(visitor);
        }
    }

    public Token getModifier() {
        return modifier;
    }

    public Token getType() {
        return type;
    }

    public boolean isPublic() {
        return modifier == null || !modifier.getText().equals("static");
    }

    public Token getIdentifier() {
        return identifier;
    }

    public List<ParameterNode> getParameters() {
        return parameters;
    }

    public Node getParameter(int index) {
        return parameters.get(index);
    }

    public List<ReturnNode> getReturnVariables() {
        return returnVariables;
    }

    public Node getReturnVariable(int index) {
        return returnVariables.get(index);
    }

    public List<LocalVariableNode> getLocalVariables() {
        return localVariables;
    }

    public LocalVariableNode getLocalVariable(int index) {
        return localVariables.get(index);
    }

}
