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

import com.maccasoft.propeller.spin1.Spin1Bytecode;
import com.maccasoft.propeller.spin1.Spin1Context;

public class Bytecode extends Spin1Bytecode {

    byte[] code;

    public Bytecode(Spin1Context context, int code, String text) {
        super(context, text.toUpperCase());
        this.code = new byte[] {
            (byte) code
        };
    }

    public Bytecode(Spin1Context context, byte[] code, String text) {
        super(context, text.toUpperCase());
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
