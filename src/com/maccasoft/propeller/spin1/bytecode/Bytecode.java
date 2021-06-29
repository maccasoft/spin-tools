/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1.bytecode;

import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionObject;

public class Bytecode extends Spin1BytecodeInstructionObject {

    public int value;
    public String text;

    public Bytecode(int value, String text) {
        this.value = value;
        this.text = text;
    }

    @Override
    public byte[] getBytes() {
        return new byte[] {
            (byte) value
        };
    }

    @Override
    public String toString() {
        return text;
    }

}
