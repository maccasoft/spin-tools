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

public class DataLineNode extends Node {

    public Node label;
    public Node condition;
    public Node instruction;
    public List<ParameterNode> parameters = new ArrayList<ParameterNode>();
    public Node modifier;

    public DataLineNode(Node parent) {
        super(parent);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitDataLine(this);
        super.accept(visitor);
    }

    @Override
    public int getStartIndex() {
        return tokens.size() != 0 ? tokens.get(0).start - tokens.get(0).column : -1;
    }

}