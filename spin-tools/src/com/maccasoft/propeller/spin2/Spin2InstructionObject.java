/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.BitField;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Context;

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

    // Conditions
    public static Map<String, Integer> conditions = new HashMap<String, Integer>();
    static {
        conditions.put("_ret_", 0b0000);
        conditions.put("if_nc_and_nz", 0b0001);
        conditions.put("if_nz_and_nc", 0b0001);
        conditions.put("if_gt", 0b0001);
        conditions.put("if_a", 0b0001);
        conditions.put("if_00", 0b0001);
        conditions.put("if_nc_and_z", 0b0010);
        conditions.put("if_z_and_nc", 0b0010);
        conditions.put("if_01", 0b0010);
        conditions.put("if_nc", 0b0011);
        conditions.put("if_ge", 0b0011);
        conditions.put("if_ae", 0b0011);
        conditions.put("if_0x", 0b0011);
        conditions.put("if_c_and_nz", 0b0100);
        conditions.put("if_nz_and_c", 0b0100);
        conditions.put("if_10", 0b0100);
        conditions.put("if_nz", 0b0101);
        conditions.put("if_ne", 0b0101);
        conditions.put("if_x0", 0b0101);
        conditions.put("if_c_ne_z", 0b0110);
        conditions.put("if_z_ne_c", 0b0110);
        conditions.put("if_diff", 0b0110);
        conditions.put("if_nc_or_nz", 0b0111);
        conditions.put("if_nz_or_nc", 0b0111);
        conditions.put("if_not_11", 0b0111);
        conditions.put("if_c_and_z", 0b1000);
        conditions.put("if_z_and_c", 0b1000);
        conditions.put("if_11", 0b1000);
        conditions.put("if_c_eq_z", 0b1001);
        conditions.put("if_z_eq_c", 0b1001);
        conditions.put("if_same", 0b1001);
        conditions.put("if_z", 0b1010);
        conditions.put("if_e", 0b1010);
        conditions.put("if_x1", 0b1010);
        conditions.put("if_nc_or_z", 0b1011);
        conditions.put("if_z_or_nc", 0b1011);
        conditions.put("if_not_10", 0b1011);
        conditions.put("if_c", 0b1100);
        conditions.put("if_lt", 0b1100);
        conditions.put("if_b", 0b1100);
        conditions.put("if_1x", 0b1100);
        conditions.put("if_c_or_nz", 0b1101);
        conditions.put("if_nz_or_c", 0b1101);
        conditions.put("if_not_01", 0b1101);
        conditions.put("if_c_or_z", 0b1110);
        conditions.put("if_z_or_c", 0b1110);
        conditions.put("if_le", 0b1110);
        conditions.put("if_be", 0b1110);
        conditions.put("if_not_00", 0b1110);
        conditions.put("if_always", 0b1111);
    }

    // Mod
    public static Map<String, Integer> mod = new HashMap<String, Integer>();
    static {
        mod.put("_clr", 0b0000);
        mod.put("_nc_and_nz", 0b0001);
        mod.put("_nz_and_nc", 0b0001);
        mod.put("_gt", 0b0001);
        mod.put("_nc_and_z", 0b0010);
        mod.put("_z_and_nc", 0b0010);
        mod.put("_nc", 0b0011);
        mod.put("_ge", 0b0011);
        mod.put("_c_and_nz", 0b0100);
        mod.put("_nz_and_c", 0b0100);
        mod.put("_nz", 0b0101);
        mod.put("_ne", 0b0101);
        mod.put("_c_ne_z", 0b0110);
        mod.put("_z_ne_c", 0b0110);
        mod.put("_nc_or_nz", 0b0111);
        mod.put("_nz_or_nc", 0b0111);
        mod.put("_c_and_z", 0b1000);
        mod.put("_z_and_c", 0b1000);
        mod.put("_c_eq_z", 0b1001);
        mod.put("_z_eq_c", 0b1001);
        mod.put("_z", 0b1010);
        mod.put("_e", 0b1010);
        mod.put("_nc_or_z", 0b1011);
        mod.put("_z_or_nc", 0b1011);
        mod.put("_c", 0b1100);
        mod.put("_lt", 0b1100);
        mod.put("_c_or_nz", 0b1101);
        mod.put("_nz_or_c", 0b1101);
        mod.put("_c_or_z", 0b1110);
        mod.put("_z_or_c", 0b1110);
        mod.put("_le", 0b1110);
        mod.put("_set", 0b1111);
    }

    public static Set<String> ptrInstructions = new HashSet<>();
    static {
        ptrInstructions.add("rdbyte");
        ptrInstructions.add("rdword");
        ptrInstructions.add("rdlong");
        ptrInstructions.add("rdlut");
        ptrInstructions.add("wmlong");
        ptrInstructions.add("wrbyte");
        ptrInstructions.add("wrword");
        ptrInstructions.add("wrlong");
        ptrInstructions.add("wrlut");
    }

    protected final Context context;

    public Spin2InstructionObject(Context context) {
        this.context = context;
    }

    public int resolve(int address, boolean hubMode) {
        context.setAddress(hubMode ? address : address >> 2);
        return address + getSize();
    }

    public int getSize() {
        return 4;
    }

    protected int encodeEffect(String effect) {
        int result = 0b00;

        if (effect != null) {
            effect = effect.toLowerCase();
            if ("wcz".equals(effect)) {
                result = 0b11;
            }
            else if ("andc".equals(effect) || "orc".equals(effect) || "xorc".equals(effect)) {
                result = 0b10;
            }
            else if ("andz".equals(effect) || "orz".equals(effect) || "xorz".equals(effect)) {
                result = 0b01;
            }
            else {
                if (effect.contains("wc")) {
                    result |= 0b10;
                }
                if (effect.contains("wz")) {
                    result |= 0b01;
                }
            }
        }

        return result;
    }

    public void generateObjectCode(OutputStream output) throws IOException {
        byte[] object = getBytes();
        output.write(object, 0, object.length);
    }

    protected int encodeAugs(String condition, int number) {
        int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
        if (e.getValue(value) == 0b0000) {
            value = e.setValue(value, 0b1111);
        }
        value = o.setValue(value, 0b1111000);
        return x.setValue(value, number >> 9);
    }

    protected int encodeAugd(String condition, int number) {
        int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
        if (e.getValue(value) == 0b0000) {
            value = e.setValue(value, 0b1111);
        }
        value = o.setValue(value, 0b1111100);
        return x.setValue(value, number >> 9);
    }

    protected int encodeInstructionParameters(String condition, Spin2PAsmExpression dst, Spin2PAsmExpression src, String effect) {
        CompilerException msgs = new CompilerException();

        int value = e.setValue(0, condition == null ? 0b1111 : conditions.get(condition.toLowerCase()));
        value = cz.setValue(value, encodeEffect(effect));
        value = i.setBoolean(value, src.isLiteral());
        try {
            if (dst.getInteger() > 0x1FF) {
                msgs.addMessage(new CompilerException("destination register cannot exceed $1FF", dst.getExpression().getData()));
            }
            value = d.setValue(value, dst.getInteger());
        } catch (Exception e) {
            msgs.addMessage(new CompilerException(e.getMessage(), dst.getExpression().getData()));
        }
        try {
            if (!src.isLongLiteral() && src.getInteger() > 0x1FF) {
                msgs.addMessage(new CompilerException("source register/constant cannot exceed $1FF", src.getExpression().getData()));
            }
            value = s.setValue(value, src.getInteger());
        } catch (Exception e) {
            msgs.addMessage(new CompilerException(e.getMessage(), src.getExpression().getData()));
        }

        if (msgs.hasChilds()) {
            throw msgs;
        }

        return value;
    }

    protected byte[] encodeRelativeJump(int value, String condition, Spin2PAsmExpression src) {
        if (src.isLiteral()) {
            int addr = src.getInteger();
            int ours = context.getSymbol("$").getNumber().intValue();
            if ((ours < 0x400 && addr >= 0x400) || (ours >= 0x400 && addr < 0x400)) {
                throw new CompilerException("relative addresses cann't cross between cog and hub domains", src.getExpression().getData());
            }
            if (addr >= 0x400 && (addr & 0x3) != 0) {
                throw new CompilerException("addresses not aligned with instruction", src.getExpression().getData());
            }
            int offset = (addr < 0x400 ? (addr - ours) : (addr - ours) >> 2) - 1;
            if (src.isLongLiteral()) {
                offset -= 1;
            }
            if (!src.isLongLiteral() && (offset < -256 || offset > 255)) {
                throw new CompilerException("relative offset out of range", src.getExpression().getData());
            }
            offset = addr >= 0x400 ? (offset & 0x3FFFF) : (offset & 0xFFFFF);
            value = s.setValue(value, offset);
            if (src.isLongLiteral()) {
                return getBytes(encodeAugs(condition, offset), value);
            }
        }
        else {
            value = s.setValue(value, src.getInteger());
        }
        return getBytes(value);
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
