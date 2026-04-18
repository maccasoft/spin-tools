/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.SpinObject;

public class Spin1Object extends SpinObject {

    public static class Spin1LinkDataObject extends LinkDataObject {

        public Spin1LinkDataObject(ObjectCompiler objectCompiler, int varSize) {
            super(objectCompiler, varSize);
            this.text = objectCompiler.getFile().getName();
        }

        @Override
        public void setOffset(int offset) {
            this.bytes = new byte[] {
                (byte) offset,
                (byte) (offset >> 8),
                this.bytes[2],
                this.bytes[3]
            };
            super.setOffset(offset);
        }

        @Override
        public void setVarOffset(int varOffset) {
            this.bytes = new byte[] {
                this.bytes[0],
                this.bytes[1],
                (byte) varOffset,
                (byte) (varOffset >> 8)
            };
            super.setVarOffset(varOffset);
        }

        @Override
        public void generateListing(int address, int offset, PrintStream ps) {
            ps.printf("%05X %05X       %02X %02X %02X %02X    Object \"%s\" @ $%04X (variables @ $%04X)%n",
                address + offset, address, bytes[0], bytes[1], bytes[2], bytes[3], text, getOffset(), getVarOffset());
        }

    }

    int dcurr;

    public Spin1Object() {

    }

    public Spin1Object(File file, int address) {
        super(file, address);
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
