/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang3.BitField;

public abstract class Spin2InstructionObject {

    public static final BitField e = new BitField(0b11110000000000000000000000000000); // condition
    public static final BitField o = new BitField(0b00001111111000000000000000000000); // instruction
    public static final BitField c = new BitField(0b00000000000100000000000000000000); // write c
    public static final BitField z = new BitField(0b00000000000010000000000000000000); // write z
    public static final BitField l = new BitField(0b00000000000001000000000000000000); // literal
    public static final BitField i = new BitField(0b00000000000001000000000000000000);
    public static final BitField d = new BitField(0b00000000000000111111111000000000); // destination
    public static final BitField s = new BitField(0b00000000000000000000000111111111); // source

    public static final BitField w = new BitField(0b00000000011000000000000000000000);

    public static final BitField r = new BitField(0b00000000000100000000000000000000); // relative/absolute
    public static final BitField a = new BitField(0b00000000000011111111111111111111); // address

    public static final BitField x = new BitField(0b00000000011111111111111111111111);

    public static int encode(int IIIIIII, boolean C, boolean Z, boolean I, int DDDDDDDD, int SSSSSSSS) {
        int value = e.setValue(0, 0b1111);
        value = o.setValue(value, IIIIIII);
        value = c.setBoolean(value, C);
        value = z.setBoolean(value, Z);
        value = l.setBoolean(value, I);
        value = d.setValue(value, DDDDDDDD);
        return s.setValue(value, SSSSSSSS);
    }

    public static int encode(int IIIIIII) {
        int value = e.setValue(0, 0b1111);
        return o.setValue(value, IIIIIII);
    }

    public static int encodeAddress(int value, boolean R, int address) {
        return a.setValue(r.setBoolean(value, R), address);
    }

    protected final Spin2Context context;

    public Spin2InstructionObject(Spin2Context context) {
        this.context = context;
    }

    public int resolve(int address) {
        context.setAddress(address);
        return address + 1;
    }

    public int getSize() {
        return 4;
    }

    public void generateObjectCode(OutputStream output) throws IOException {
        byte[] object = getBytes();
        output.write(object, 0, object.length);
    }

    public abstract byte[] getBytes();

    protected byte[] getBytes(int value) {
        return new byte[] {
            (byte) (value & 0xFF),
            (byte) ((value >> 8) & 0xFF),
            (byte) ((value >> 16) & 0xFF),
            (byte) ((value >> 24) & 0xFF)
        };
    }

    protected byte[] getBytes(byte[] prefix, int value) {
        return new byte[] {
            prefix[0], prefix[1], prefix[2], prefix[3],
            (byte) (value & 0xFF),
            (byte) ((value >> 8) & 0xFF),
            (byte) ((value >> 16) & 0xFF),
            (byte) ((value >> 24) & 0xFF)
        };
    }

}
