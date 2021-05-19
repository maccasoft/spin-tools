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
    public static final BitField cz = new BitField(0b00000000000110000000000000000000); // write cz
    public static final BitField l = new BitField(0b00000000000010000000000000000000); // d literal
    public static final BitField i = new BitField(0b00000000000001000000000000000000); // s immediate
    public static final BitField d = new BitField(0b00000000000000111111111000000000); // destination
    public static final BitField s = new BitField(0b00000000000000000000000111111111); // source

    public static final BitField czi = new BitField(0b00000000000111000000000000000000); // write czi

    public static final BitField nnn = new BitField(0b00000000001110000000000000000000);
    public static final BitField w = new BitField(0b00000000011000000000000000000000);

    public static final BitField r = new BitField(0b00000000000100000000000000000000); // relative/absolute
    public static final BitField a = new BitField(0b00000000000011111111111111111111); // address

    public static final BitField x = new BitField(0b00000000011111111111111111111111);

    public static int encode(int IIIIIII, boolean C, boolean Z, boolean I, int DDDDDDDD, int SSSSSSSS) {
        int value = e.setValue(0, 0b1111);
        value = o.setValue(value, IIIIIII);
        value = c.setBoolean(value, C);
        value = z.setBoolean(value, Z);
        value = i.setBoolean(value, I);
        value = d.setValue(value, DDDDDDDD);
        return s.setValue(value, SSSSSSSS);
    }

    public static int encode(int IIIIIII, int CZ, boolean I, int DDDDDDDD, int SSSSSSSS) {
        int value = e.setValue(0, 0b1111);
        value = o.setValue(value, IIIIIII);
        value = cz.setValue(value, CZ);
        value = i.setBoolean(value, I);
        value = d.setValue(value, DDDDDDDD);
        return s.setValue(value, SSSSSSSS);
    }

    public static int decode(byte[] b) {
        return b[0] | (b[1] << 8) | (b[1] << 16) | (b[1] << 24);
    }

    public static String decodeToString(byte[] b) {
        return decodeToString((b[0] & 0xFF) | ((b[1] & 0xFF) << 8) | ((b[2] & 0xFF) << 16) | ((b[3] & 0xFF) << 24));
    }

    public static String decodeToString(int value) {
        return String.format("%s_%s_%s%s%s_%s_%s",
            toBinary(e.getValue(value), 4),
            toBinary(o.getValue(value), 7),
            toBinary(c.getValue(value), 1),
            toBinary(z.getValue(value), 1),
            toBinary(i.getValue(value), 1),
            toBinary(d.getValue(value), 9),
            toBinary(s.getValue(value), 9));
    }

    static String toBinary(int value, int size) {
        StringBuilder sb = new StringBuilder(Integer.toBinaryString(value));
        if (sb.length() > size) {
            sb.setLength(size);
        }
        while (sb.length() < size) {
            sb.insert(0, '0');
        }
        return sb.toString();
    }

    protected final Spin2Context context;

    public Spin2InstructionObject(Spin2Context context) {
        this.context = context;
    }

    public int resolve(int address) {
        context.setAddress(address);
        return address + 1;
    }

    protected int encodeEffect(String effect) {
        if (effect == null) {
            return 0b00;
        }
        else if ("wcz".equals(effect)) {
            return 0b11;
        }
        else if (effect.contains("wc") || "andc".equals(effect) || "orc".equals(effect) || "xor".equals(effect)) {
            return 0b10;
        }
        else if (effect.contains("wz") || "andz".equals(effect) || "orz".equals(effect) || "xorz".equals(effect)) {
            return 0b01;
        }
        return 0;
    }

    public int getSize() {
        return 4;
    }

    public void generateObjectCode(OutputStream output) throws IOException {
        byte[] object = getBytes();
        output.write(object, 0, object.length);
    }

    protected int encodeAugs(String condition, int number) {
        int value = e.setValue(0, condition == null ? 0b1111 : context.getInteger(condition));
        value = o.setValue(value, 0b1111000);
        return x.setValue(value, number >> 9);
    }

    protected int encodeAugd(String condition, int number) {
        int value = e.setValue(0, condition == null ? 0b1111 : context.getInteger(condition));
        value = o.setValue(value, 0b1111100);
        return x.setValue(value, number >> 9);
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

    protected byte[] getBytes(int prefix1, int prefix2, int value) {
        return new byte[] {
            (byte) (prefix1 & 0xFF),
            (byte) ((prefix1 >> 8) & 0xFF),
            (byte) ((prefix1 >> 16) & 0xFF),
            (byte) ((prefix1 >> 24) & 0xFF),
            (byte) (prefix2 & 0xFF),
            (byte) ((prefix2 >> 8) & 0xFF),
            (byte) ((prefix2 >> 16) & 0xFF),
            (byte) ((prefix2 >> 24) & 0xFF),
            (byte) (value & 0xFF),
            (byte) ((value >> 8) & 0xFF),
            (byte) ((value >> 16) & 0xFF),
            (byte) ((value >> 24) & 0xFF)
        };
    }

    protected byte[] getBytes(int prefix, int value) {
        return new byte[] {
            (byte) (prefix & 0xFF),
            (byte) ((prefix >> 8) & 0xFF),
            (byte) ((prefix >> 16) & 0xFF),
            (byte) ((prefix >> 24) & 0xFF),
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
