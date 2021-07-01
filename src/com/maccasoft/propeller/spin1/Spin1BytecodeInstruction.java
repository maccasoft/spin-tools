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

public class Spin1BytecodeInstruction {

    Spin1Context context;
    String label;

    public Spin1BytecodeInstruction(Spin1Context context) {
        this.context = context;
    }

    public Spin1BytecodeInstruction(Spin1Context context, String label) {
        this.context = context;
        this.label = label;
    }

    public Spin1Context getContext() {
        return context;
    }

    public String getLabel() {
        return label;
    }

    public int resolve(int address) {
        context.setAddress(address);
        return address + getSize();
    }

    public int getSize() {
        return 0;
    }

    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public String toString() {
        return label != null ? label : "";
    }

}
