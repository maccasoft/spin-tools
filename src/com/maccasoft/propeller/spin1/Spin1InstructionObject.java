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

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.BitField;

public abstract class Spin1InstructionObject {

    public static final BitField instr = new BitField(0b111111_0_0_0_0_0000_000000000_000000000); // instruction
    public static final BitField z = new BitField(0b000000_100_0_0000_000000000_000000000); // write z
    public static final BitField c = new BitField(0b000000_010_0_0000_000000000_000000000); // write c
    public static final BitField r = new BitField(0b000000_001_0_0000_000000000_000000000); // write result
    public static final BitField i = new BitField(0b000000_000_1_0000_000000000_000000000); // immediate
    public static final BitField con = new BitField(0b000000_000_0_1111_000000000_000000000); // condition
    public static final BitField d = new BitField(0b000000_000_0_0000_111111111_000000000); // destination
    public static final BitField s = new BitField(0b000000_000_0_0000_000000000_111111111); // source

    public static final BitField zcr = new BitField(0b000000_111_0_0000_000000000_000000000);

    public static int decode(byte[] b) {
        return b[0] | (b[1] << 8) | (b[1] << 16) | (b[1] << 24);
    }

    public static String decodeToString(byte[] b) {
        return decodeToString((b[0] & 0xFF) | ((b[1] & 0xFF) << 8) | ((b[2] & 0xFF) << 16) | ((b[3] & 0xFF) << 24));
    }

    public static String decodeToString(int value) {
        return String.format("%s_%s%s%s%s_%s_%s_%s",
            toBinary(instr.getValue(value), 6),
            toBinary(z.getValue(value), 1),
            toBinary(c.getValue(value), 1),
            toBinary(r.getValue(value), 1),
            toBinary(i.getValue(value), 1),
            toBinary(con.getValue(value), 4),
            toBinary(d.getValue(value), 9),
            toBinary(s.getValue(value), 9));
    }

    public static String toBinary(int value, int size) {
        value &= (1 << size) - 1;
        StringBuilder sb = new StringBuilder(Integer.toBinaryString(value));
        while (sb.length() < size) {
            sb.insert(0, '0');
        }
        return sb.toString();
    }

    // Conditions
    public static Map<String, Integer> conditions = new HashMap<String, Integer>();
    static {
        conditions.put("if_never", 0b0000);
        conditions.put("if_nc_and_nz", 0b0001);
        conditions.put("if_nz_and_nc", 0b0001);
        conditions.put("if_a", 0b0001);
        conditions.put("if_nc_and_z", 0b0010);
        conditions.put("if_z_and_nc", 0b0010);
        conditions.put("if_nc", 0b0011);
        conditions.put("if_ae", 0b0011);
        conditions.put("if_c_and_nz", 0b0100);
        conditions.put("if_nz_and_c", 0b0100);
        conditions.put("if_nz", 0b0101);
        conditions.put("if_ne", 0b0101);
        conditions.put("if_c_ne_z", 0b0110);
        conditions.put("if_z_ne_c", 0b0110);
        conditions.put("if_nc_or_nz", 0b0111);
        conditions.put("if_nz_or_nc", 0b0111);
        conditions.put("if_c_and_z", 0b1000);
        conditions.put("if_z_and_c", 0b1000);
        conditions.put("if_c_eq_z", 0b1001);
        conditions.put("if_z_eq_c", 0b1001);
        conditions.put("if_z", 0b1010);
        conditions.put("if_e", 0b1010);
        conditions.put("if_nc_or_z", 0b1011);
        conditions.put("if_z_or_nc", 0b1011);
        conditions.put("if_c", 0b1100);
        conditions.put("if_b", 0b1100);
        conditions.put("if_c_or_nz", 0b1101);
        conditions.put("if_nz_or_c", 0b1101);
        conditions.put("if_c_or_z", 0b1110);
        conditions.put("if_z_or_c", 0b1110);
        conditions.put("if_be", 0b1110);
        conditions.put("if_always", 0b1111);
    }

    protected final Spin1Context context;

    public Spin1InstructionObject(Spin1Context context) {
        this.context = context;
    }

    public int resolve(int address) {
        context.setAddress(address >> 2);
        return address + getSize();
    }

    public int getSize() {
        return 4;
    }

    protected int encodeEffect(String effect) {
        return encodeEffect(0b001, effect);
    }

    protected int encodeEffect(int result, String effect) {
        if (effect != null) {
            effect = effect.toLowerCase();
            if (effect.contains("wz")) {
                result |= 0b100;
            }
            if (effect.contains("wc")) {
                result |= 0b010;
            }
            if (effect.contains("wr")) {
                result |= 0b001;
            }
            else if (effect.contains("nr")) {
                result &= 0b110;
            }
        }
        return result;
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

}
