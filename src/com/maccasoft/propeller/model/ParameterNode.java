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

public class ParameterNode extends Node {

    public ExpressionNode count;

    public ParameterNode(Node parent) {
        super(parent);
    }

    public ExpressionNode getCount() {
        return count;
    }

}