/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.maccasoft.propeller.SpinObject;

public class Spin1Object extends SpinObject {

    public static class Spin1LinkDataObject extends LinkDataObject {

        public Spin1LinkDataObject(Object object, long varSize) {
            super(object, varSize);
        }

        @Override
        public void setOffset(long offset) {
            this.bytes = new byte[] {
                (byte) offset,
                (byte) (offset >> 8),
                this.bytes[2],
                this.bytes[3]
            };
            super.setOffset(offset);
        }

        @Override
        public void setVarOffset(long varOffset) {
            this.bytes = new byte[] {
                this.bytes[0],
                this.bytes[1],
                (byte) varOffset,
                (byte) (varOffset >> 8)
            };
            super.setVarOffset(varOffset);
        }

    }

    int dcurr;

    public Spin1Object() {

    }

    public int getDcurr() {
        return dcurr;
    }

    public void setDcurr(int dcurr) {
        this.dcurr = dcurr;
    }

    public byte[] getRAM() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        generateBinary(os);

        int count = (32 * 1024) - os.size();
        if (count > 0) {
            os.write(new byte[count]);
        }

        return os.toByteArray();
    }

}
