/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.bytecode;

import com.maccasoft.propeller.spin2.Spin2Bytecode;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin2.Spin2PAsmLine;

public class InlinePAsm extends Spin2Bytecode {

    Spin2PAsmLine line;

    public InlinePAsm(Context context, Spin2PAsmLine line) {
        super(context);
        this.line = line;
    }

    public Spin2PAsmLine getLine() {
        return line;
    }

    @Override
    public int resolve(int address) {
        return line.resolve(address, false);
    }

    @Override
    public int getSize() {
        return line.getInstructionObject().getSize();
    }

    @Override
    public byte[] getBytes() {
        return line.getInstructionObject().getBytes();
    }

    @Override
    public String toString() {
        return line.toString();
    }

}
