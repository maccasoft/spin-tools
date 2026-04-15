/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1.instructions;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin1.Spin1InstructionObject;

public class FileInc extends Spin1InstructionObject {

    byte[] data;

    public FileInc(Context context, byte[] data) {
        super(context);
        this.data = data;
    }

    @Override
    public int resolve(int address, int memoryAddress) {
        context.setAddress(address);
        context.setMemoryAddress(memoryAddress);
        return address + data.length;
    }

    @Override
    public int getSize() {
        return data.length;
    }

    @Override
    public byte[] getBytes() {
        return data;
    }

}