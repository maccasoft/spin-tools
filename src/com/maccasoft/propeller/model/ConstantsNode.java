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

import com.maccasoft.propeller.spin.Spin2ModelVisitor;

public class ConstantsNode extends Node {

    public ConstantsNode(Node parent) {
        super(parent);
    }

    @Override
    public void accept(Spin2ModelVisitor visitor) {
        visitor.visitConstants(this);
        super.accept(visitor);
    }

}