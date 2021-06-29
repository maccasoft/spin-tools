/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

public abstract class Spin1BytecodeInstructionObject {

    String label;

    public Spin1BytecodeInstructionObject() {

    }

    public Spin1BytecodeInstructionObject(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public int getSize() {
        return 1;
    }

    public abstract byte[] getBytes();

}
