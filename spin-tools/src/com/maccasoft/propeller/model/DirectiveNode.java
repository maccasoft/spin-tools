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

public class DirectiveNode extends Node {

    public static class IncludeNode extends DirectiveNode {

        public Token file;

        public IncludeNode(Node parent) {
            super(parent);
        }

        public Token getFile() {
            return file;
        }

        public String getFileName() {
            if (file == null) {
                return null;
            }
            if (file.type != Token.STRING) {
                return file.getText();
            }
            return file.getText().substring(1, file.getText().length() - 1);
        }

        @Override
        public String getPath() {
            StringBuilder sb = new StringBuilder();

            sb.append("/");
            if (file != null) {
                sb.append(file.getText().toUpperCase());
            }

            return sb.toString();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append(getClass().getSimpleName());
            if (file != null) {
                sb.append(" file=").append(file.getText());
            }
            sb.append(dumpTokens());

            return sb.toString();
        }

    }

    public static class DefineNode extends DirectiveNode {

        public Token identifier;
        public List<Token> definition = new ArrayList<>();

        public DefineNode(Node parent) {
            super(parent);
        }

        public Token getIdentifier() {
            return identifier;
        }

        public List<Token> getDefinition() {
            return definition;
        }

        public void addDefinition(Token token) {
            definition.add(token);
            super.addToken(token);
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

    public DirectiveNode(Node parent) {
        super(parent);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitDirective(this);
    }

    public Token getDirective() {
        return tokens.size() >= 2 ? tokens.get(1) : null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getClass().getSimpleName());
        sb.append(dumpTokens());

        return sb.toString();
    }

}
