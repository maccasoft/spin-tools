/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1.bytecode;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin1.Spin1Bytecode;

public class Bytecode extends Spin1Bytecode {

    byte[] code;

    public Bytecode(Context context, int code, String text) {
        super(context, text);
        this.code = new byte[] {
            (byte) code
        };
    }

    public Bytecode(Context context, byte[] code, String text) {
        super(context, text);
        this.code = code;
        if (code == null) {
            System.out.println("null " + text);
        }
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
