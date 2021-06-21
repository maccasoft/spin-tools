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

public abstract class Spin1BytecodeObject {

    protected final Spin1Context context;

    public Spin1BytecodeObject(Spin1Context context) {
        this.context = context;
    }

    public int resolve(int address) {
        return address + getSize();
    }

    public int getSize() {
        return 1;
    }

    public abstract byte[] getBytes();

}
