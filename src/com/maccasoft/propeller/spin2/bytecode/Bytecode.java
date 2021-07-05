/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.bytecode;

import com.maccasoft.propeller.spin2.Spin2Bytecode;

public class Bytecode extends Spin2Bytecode {

    byte[] code;

    public Bytecode(int code, String text) {
        super(text.toUpperCase());
        this.code = new byte[] {
            (byte) code
        };
    }

    public Bytecode(byte[] code, String text) {
        super(text.toUpperCase());
        this.code = code;
    }

    @Override
    public int getSize() {
        return code.length;
    }

    @Override
    public byte[] getBytes() {
        return code;
    }

}
