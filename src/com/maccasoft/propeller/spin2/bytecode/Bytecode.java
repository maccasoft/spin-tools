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
import com.maccasoft.propeller.spin2.Spin2Context;

public class Bytecode extends Spin2Bytecode {

    int code;
    String text;

    public Bytecode(Spin2Context context, String label, int code, String text) {
        super(context, label);
        this.code = code;
        this.text = text.toUpperCase();
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public byte[] getBytes() {
        return new byte[] {
            (byte) code
        };
    }

    @Override
    public String toString() {
        return text;
    }

}
