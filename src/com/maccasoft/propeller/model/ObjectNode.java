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

import com.maccasoft.propeller.spin.Spin2TokenStream.Token;

public class ObjectNode extends Node {

    public Token name;
    public Node count;
    public Token file;

    public ObjectNode(Node parent) {
        super(parent);
    }

}