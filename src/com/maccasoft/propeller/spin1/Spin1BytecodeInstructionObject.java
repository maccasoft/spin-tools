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

import java.io.IOException;
import java.io.OutputStream;

public abstract class Spin1BytecodeInstructionObject {

    protected final Spin1Context context;

    public Spin1BytecodeInstructionObject(Spin1Context context) {
        this.context = context;
    }

    public int resolve(int address) {
        context.setAddress(address);
        return address + getSize();
    }

    public int getSize() {
        return getBytes().length;
    }

    public void generateObjectCode(OutputStream output) throws IOException {
        byte[] object = getBytes();
        output.write(object, 0, object.length);
    }

    public abstract byte[] getBytes();

}
