/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.model;

public class ObjectNode extends Node {

    public Token name;
    public ExpressionNode count;
    public Token file;

    public ObjectNode(Node parent) {
        super(parent);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitObject(this);
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

}