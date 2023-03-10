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

public class ErrorNode extends Node {

    String description;

    public ErrorNode(Node parent) {
        super(parent);
        this.description = "Syntax error";
    }

    public ErrorNode(Node parent, String description) {
        super(parent);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
