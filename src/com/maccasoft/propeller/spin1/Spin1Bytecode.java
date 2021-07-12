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

import java.util.HashMap;
import java.util.Map;

public class Spin1Bytecode {

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
        descriptors.put("BYTEFILL", new Descriptor(0b00011000, 3));
        descriptors.put("WORDFILL", new Descriptor(0b00011001, 3));
        descriptors.put("LONGFILL", new Descriptor(0b00011010, 3));
        descriptors.put("WAITPEQ", new Descriptor(0b00011011, 3));

        descriptors.put("BYTEMOVE", new Descriptor(0b00011100, 3));
        descriptors.put("WORDMOVE", new Descriptor(0b00011101, 3));
        descriptors.put("LONGMOVE", new Descriptor(0b00011110, 3));
        descriptors.put("WAITPNE", new Descriptor(0b00011111, 3));

        descriptors.put("CLKSET", new Descriptor(0b00100000, 2));
        descriptors.put("COGSTOP", new Descriptor(0b00100001, 1));
        descriptors.put("LOCKRET", new Descriptor(0b00100010, 1));
        descriptors.put("WAITCNT", new Descriptor(0b00100011, 1));

        descriptors.put("WAITVID", new Descriptor(0b00100111, 2));
    }

    public static Descriptor getDescriptor(String s) {
        return descriptors.get(s.toUpperCase());
    }

    protected Spin1Context context;
    String text;

    public Spin1Bytecode(Spin1Context context) {
        this.context = new Spin1Context(context);
    }

    public Spin1Bytecode(Spin1Context context, String text) {
        this.context = new Spin1Context(context);
        this.text = text;
    }

    public Spin1Context getContext() {
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
