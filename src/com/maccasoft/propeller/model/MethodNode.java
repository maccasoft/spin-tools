/*
 * Copyright (c) 2021 Marco Maccaferri and others.
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

import com.maccasoft.propeller.spin.Spin2TokenStream.Token;

public class MethodNode extends Node {

    public Token type;
    public Token name;
    public List<Node> parameters = new ArrayList<Node>();
    public List<Node> returnVariables = new ArrayList<Node>();
    public List<LocalVariableNode> localVariables = new ArrayList<LocalVariableNode>();

    public MethodNode(Node parent, Token type) {
        super(parent);
        this.type = type;
        addToken(type);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitMethod(this);
        super.accept(visitor);
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

    public List<Node> getParameters() {
        return parameters;
    }

    public Node getParameter(int index) {
        return parameters.get(index);
    }

    public List<Node> getReturnVariables() {
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