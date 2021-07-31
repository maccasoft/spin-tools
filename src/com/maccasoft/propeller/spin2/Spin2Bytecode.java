/*
 * Copyright (c) 2021 Marco Maccaferri and others.
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

        public Descriptor(int b, int parameters) {
            this.code = new byte[] {
                (byte) b
            };
            this.parameters = parameters;
        }

        public Descriptor(int b0, int b1, int parameters) {
            this.code = new byte[] {
                (byte) b0,
                (byte) b1
            };
            this.parameters = parameters;
        }
    }
    static Map<String, Descriptor> descriptors = new HashMap<String, Descriptor>();
    static {
        descriptors.put("HUBSET", new Descriptor(0x19, 0x54, 1));
        descriptors.put("CLKSET", new Descriptor(0x19, 0x56, 2));
        descriptors.put("CLKFREQ", new Descriptor(0x19, 0x58, 0));
        //descriptors.put("COGSPIN", new Descriptor(0x19, 0x5A, 3));
        descriptors.put("REGEXEC", new Descriptor(0x19, 0x5E, 1));
        descriptors.put("REGLOAD", new Descriptor(0x19, 0x60, 1));
        descriptors.put("CALL", new Descriptor(0x19, 0x62, 1));
        descriptors.put("GETREGS", new Descriptor(0x19, 0x64, 3));
        descriptors.put("SETREGS", new Descriptor(0x19, 0x66, 3));
        descriptors.put("BYTEMOVE", new Descriptor(0x19, 0x68, 3));
        descriptors.put("BYTEFILL", new Descriptor(0x19, 0x6A, 3));
        descriptors.put("WORDMOVE", new Descriptor(0x19, 0x6C, 3));
        descriptors.put("WORDFILL", new Descriptor(0x19, 0x6E, 3));
        descriptors.put("LONGMOVE", new Descriptor(0x19, 0x70, 3));
        descriptors.put("LONGFILL", new Descriptor(0x19, 0x72, 3));
        descriptors.put("STRSIZE", new Descriptor(0x19, 0x74, 1));
        descriptors.put("STRCOMP", new Descriptor(0x19, 0x76, 2));
        descriptors.put("WAITUS", new Descriptor(0x19, 0x78, 1));
        descriptors.put("WAITMS", new Descriptor(0x19, 0x7A, 1));
        descriptors.put("GETMS", new Descriptor(0x19, 0x7C, 0));
        descriptors.put("GETSEC", new Descriptor(0x19, 0x7E, 0));
        descriptors.put("MULDIV64", new Descriptor(0x19, 0x80, 3));
        descriptors.put("QSIN", new Descriptor(0x19, 0x82, 3));
        descriptors.put("QCOS", new Descriptor(0x19, 0x84, 3));

        descriptors.put("COGSTOP", new Descriptor(0x27, 1));
        descriptors.put("COGID", new Descriptor(0x28, 0));
        descriptors.put("COGCHK", new Descriptor(0x29, 1));

        descriptors.put("LOCKNEW", new Descriptor(0x2A, 0));
        descriptors.put("LOCKRET", new Descriptor(0x2B, 1));
        descriptors.put("LOCKTRY", new Descriptor(0x2C, 1));
        descriptors.put("LOCKREL", new Descriptor(0x2D, 1));
        descriptors.put("LOCKCHK", new Descriptor(0x2E, 1));

        descriptors.put("COGATN", new Descriptor(0x2F, 1));
        descriptors.put("POLLATN", new Descriptor(0x30, 0));
        descriptors.put("WAITATN", new Descriptor(0x31, 0));

        descriptors.put("GETRND", new Descriptor(0x32, 0));
        descriptors.put("GETCT", new Descriptor(0x33, 0));
        descriptors.put("POLLCT", new Descriptor(0x34, 1));
        descriptors.put("WAITCT", new Descriptor(0x35, 1));

        descriptors.put("PINW", new Descriptor(0x36, 2));
        descriptors.put("PINWRITE", new Descriptor(0x36, 2));
        descriptors.put("PINL", new Descriptor(0x37, 1));
        descriptors.put("PINLOW", new Descriptor(0x37, 1));
        descriptors.put("PINH", new Descriptor(0x38, 1));
        descriptors.put("PINHIGH", new Descriptor(0x38, 1));
        descriptors.put("PINT", new Descriptor(0x39, 1));
        descriptors.put("PINTOGGLE", new Descriptor(0x39, 1));
        descriptors.put("PINF", new Descriptor(0x3A, 1));
        descriptors.put("PINFLOAT", new Descriptor(0x3A, 1));
        descriptors.put("PINR", new Descriptor(0x3B, 1));
        descriptors.put("PINREAD", new Descriptor(0x3B, 1));

        descriptors.put("PINSTART", new Descriptor(0x3C, 4));
        descriptors.put("PINCLEAR", new Descriptor(0x3D, 1));

        descriptors.put("WRPIN", new Descriptor(0x3E, 2));
        descriptors.put("WXPIN", new Descriptor(0x3F, 2));
        descriptors.put("WYPIN", new Descriptor(0x40, 2));
        descriptors.put("AKPIN", new Descriptor(0x41, 1));
        descriptors.put("RDPIN", new Descriptor(0x42, 1));
        descriptors.put("RQPIN", new Descriptor(0x43, 1));

        descriptors.put("ROTXY", new Descriptor(0x68, 3));
        descriptors.put("POLXY", new Descriptor(0x69, 2));
        descriptors.put("XYPOL", new Descriptor(0x6A, 2));
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
