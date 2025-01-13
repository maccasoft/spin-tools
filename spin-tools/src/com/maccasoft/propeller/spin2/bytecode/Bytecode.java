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

public class Bytecode extends Spin2Bytecode {

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
