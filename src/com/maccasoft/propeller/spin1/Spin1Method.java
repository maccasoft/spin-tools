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

public class Spin1Method {

    Spin1Context scope;

    public Spin1Method(Spin1Context scope) {
        this.scope = scope;
    }

    public Spin1Context getScope() {
        return scope;
    }

    public int resolve(int address) {
        scope.setAddress(address);
        return address + getSize();
    }

    public int getLocalSize() {
        return 0;
    }

    public int getSize() {
        return getBytes().length;
    }

    public byte[] getBytes() {
        return new byte[] {
            (byte) 0x3F, (byte) 0x89, // COGID -> REG_READ 1E9 (89 = (1E9 - 1E0) | %1_00_00000
            (byte) 0xC7, (byte) 0x08, // MEM_ADDRESS LONG PBASE+$0008
            (byte) 0x35, //              CONSTANT 0
            (byte) 0x2C, //              COGINIT
            (byte) 0x32, //              RETURN_PLAIN
        };
    }
}
