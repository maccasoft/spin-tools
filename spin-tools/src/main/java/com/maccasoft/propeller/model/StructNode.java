/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

public class StructNode extends Node {

    public static class Member extends Node {

        public Token modifier;
        public Token type;
        public Token identifier;
        public ExpressionNode size;

        public Member(Node parent) {
            super(parent);
        }

        public Member(Node parent, Token identifier) {
            super(parent);
            this.identifier = identifier;
            if (identifier != null) {
                addToken(identifier);
            }
        }

        public Token getModifier() {
            return modifier;
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
        public void addToken(Token token) {
            parent.addToken(token);
            super.addToken(token);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append(getClass().getSimpleName());
            if (modifier != null) {
                sb.append(" modifier=").append(modifier.getText());
            }
            if (type != null) {
                sb.append(" type=").append(type.getText());
            }
            if (identifier != null) {
                sb.append(" identifier=").append(identifier.getText());
            }
            sb.append(dumpTokens());

            return sb.toString();
        }

    }

    public Token type;
    public Token identifier;

    public StructNode(Node parent) {
        super(parent);
    }

    public StructNode(Node parent, Token identifier) {
        super(parent);
        this.identifier = identifier;
        if (identifier != null) {
            tokens.add(identifier);
        }
    }

    public StructNode(Node parent, Token type, Token identifier) {
        super(parent);
        this.type = type;
        this.identifier = identifier;
        if (type != null) {
            tokens.add(type);
        }
        if (identifier != null) {
            tokens.add(identifier);
        }
    }

    public Token getType() {
        return type;
    }

    public Token getIdentifier() {
        return identifier;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitStruct(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getClass().getSimpleName());
        if (type != null) {
            sb.append(" type=").append(type.getText());
        }
        if (identifier != null) {
            sb.append(" identifier=").append(identifier.getText());
        }
        sb.append(dumpTokens());

        return sb.toString();
    }

}
