/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.HashMap;
import java.util.Map;

public class Spin2Bytecode {

    static class Descriptor {
        byte[] code;
        int parameters;
        int returns;

        public Descriptor(int b0, int b1, int parameters, int returns) {
            if (b1 == -1) {
                this.code = new byte[] {
                    (byte) b0
                };
            }
            else {
                this.code = new byte[] {
                    (byte) b0,
                    (byte) b1
                };
            }
            this.parameters = parameters;
            this.returns = returns;
        }

        public int getParameters() {
            return parameters;
        }

        public int getReturns() {
            return returns;
        }

    }

    static Map<String, Descriptor> descriptors = new HashMap<String, Descriptor>();
    static {
        descriptors.put("HUBSET", new Descriptor(0x19, 0x54, 1, 0));
        descriptors.put("CLKSET", new Descriptor(0x19, 0x56, 2, 0));
        descriptors.put("CLKFREQ", new Descriptor(0x19, 0x58, 0, 1));
        //descriptors.put("COGSPIN", new Descriptor(0x19, 0x5A, 3));
        descriptors.put("COGCHK", new Descriptor(0x19, 0x5C, 1, 1));
        descriptors.put("REGEXEC", new Descriptor(0x19, 0x60, 1, 0));
        descriptors.put("REGLOAD", new Descriptor(0x19, 0x62, 1, 0));
        descriptors.put("CALL", new Descriptor(0x19, 0x64, 1, 0));
        descriptors.put("GETREGS", new Descriptor(0x19, 0x66, 3, 0));
        descriptors.put("SETREGS", new Descriptor(0x19, 0x68, 3, 0));
        descriptors.put("BYTEMOVE", new Descriptor(0x19, 0x6A, 3, 0));
        descriptors.put("BYTEFILL", new Descriptor(0x19, 0x6C, 3, 0));
        descriptors.put("WORDMOVE", new Descriptor(0x19, 0x6E, 3, 0));
        descriptors.put("WORDFILL", new Descriptor(0x19, 0x70, 3, 0));
        descriptors.put("LONGMOVE", new Descriptor(0x19, 0x72, 3, 0));
        descriptors.put("LONGFILL", new Descriptor(0x19, 0x74, 3, 0));
        descriptors.put("STRSIZE", new Descriptor(0x19, 0x76, 1, 1));
        descriptors.put("STRCOMP", new Descriptor(0x19, 0x78, 2, 1));
        descriptors.put("STRCOPY", new Descriptor(0x19, 0x7A, 3, 0));
        descriptors.put("GETCRC", new Descriptor(0x19, 0x7C, 3, 1));
        descriptors.put("WAITUS", new Descriptor(0x19, 0x7E, 1, 0));
        descriptors.put("WAITMS", new Descriptor(0x19, 0x80, 1, 0));
        descriptors.put("GETMS", new Descriptor(0x19, 0x82, 0, 1));
        descriptors.put("GETSEC", new Descriptor(0x19, 0x84, 0, 1));
        descriptors.put("MULDIV64", new Descriptor(0x19, 0x86, 3, 1));
        descriptors.put("QSIN", new Descriptor(0x19, 0x88, 3, 1));
        descriptors.put("QCOS", new Descriptor(0x19, 0x8A, 3, 1));
        descriptors.put("ROTXY", new Descriptor(0x19, 0x8C, 3, 2));
        descriptors.put("POLXY", new Descriptor(0x19, 0x8E, 2, 2));
        descriptors.put("XYPOL", new Descriptor(0x19, 0x90, 2, 2));

        descriptors.put("NAN", new Descriptor(0x19, 0x92, 1, 1));
        descriptors.put("FABS", new Descriptor(0x19, 0x96, 1, 1));
        descriptors.put("FSQRT", new Descriptor(0x19, 0x98, 1, 1));
        descriptors.put("ROUND", new Descriptor(0x19, 0xAE, 1, 1));
        descriptors.put("TRUNC", new Descriptor(0x19, 0xB0, 1, 1));
        descriptors.put("FLOAT", new Descriptor(0x19, 0xB2, 1, 1));

        descriptors.put("COGSTOP", new Descriptor(0x27, -1, 1, 0));
        descriptors.put("COGID", new Descriptor(0x28, -1, 0, 1));

        descriptors.put("LOCKNEW", new Descriptor(0x29, -1, 0, 1));
        descriptors.put("LOCKRET", new Descriptor(0x2A, -1, 1, 0));
        descriptors.put("LOCKTRY", new Descriptor(0x2B, -1, 1, 1));
        descriptors.put("LOCKREL", new Descriptor(0x2C, -1, 1, 0));
        descriptors.put("LOCKCHK", new Descriptor(0x2D, -1, 1, 1));

        descriptors.put("COGATN", new Descriptor(0x2E, -1, 1, 0));
        descriptors.put("POLLATN", new Descriptor(0x2F, -1, 0, 1));
        descriptors.put("WAITATN", new Descriptor(0x30, -1, 0, 0));

        descriptors.put("GETRND", new Descriptor(0x31, -1, 0, 1));
        descriptors.put("GETCT", new Descriptor(0x32, -1, 0, 1));
        descriptors.put("POLLCT", new Descriptor(0x33, -1, 1, 1));
        descriptors.put("WAITCT", new Descriptor(0x34, -1, 1, 0));

        descriptors.put("PINW", new Descriptor(0x35, -1, 2, 0));
        descriptors.put("PINWRITE", new Descriptor(0x35, -1, 2, 0));
        descriptors.put("PINL", new Descriptor(0x36, -1, 1, 0));
        descriptors.put("PINLOW", new Descriptor(0x36, -1, 1, 0));
        descriptors.put("PINH", new Descriptor(0x37, -1, 1, 0));
        descriptors.put("PINHIGH", new Descriptor(0x37, -1, 1, 0));
        descriptors.put("PINT", new Descriptor(0x38, -1, 1, 0));
        descriptors.put("PINTOGGLE", new Descriptor(0x38, -1, 1, 0));
        descriptors.put("PINF", new Descriptor(0x39, -1, 1, 0));
        descriptors.put("PINFLOAT", new Descriptor(0x39, -1, 1, 0));
        descriptors.put("PINR", new Descriptor(0x3A, -1, 1, 1));
        descriptors.put("PINREAD", new Descriptor(0x3A, -1, 1, 1));

        descriptors.put("PINSTART", new Descriptor(0x3B, -1, 4, 0));
        descriptors.put("PINCLEAR", new Descriptor(0x3C, -1, 1, 0));

        descriptors.put("WRPIN", new Descriptor(0x3D, -1, 2, 0));
        descriptors.put("WXPIN", new Descriptor(0x3E, -1, 2, 0));
        descriptors.put("WYPIN", new Descriptor(0x3F, -1, 2, 0));
        descriptors.put("AKPIN", new Descriptor(0x40, -1, 1, 0));
        descriptors.put("RDPIN", new Descriptor(0x41, -1, 1, 1));
        descriptors.put("RQPIN", new Descriptor(0x42, -1, 1, 1));
    }

    public static Descriptor getDescriptor(String s) {
        return descriptors.get(s.toUpperCase());
    }

    protected Spin2Context context;
    String text;

    public Spin2Bytecode(Spin2Context context) {
        this.context = new Spin2Context(context);
    }

    public Spin2Bytecode(Spin2Context context, String text) {
        this.context = new Spin2Context(context);
        this.text = text;
    }

    public Spin2Context getContext() {
        return context;
    }

    public int resolve(int address) {
        context.setAddress(address);
        return address + getSize();
    }

    public int getSize() {
        return 0;
    }

    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public String toString() {
        return text;
    }

}
