/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.debug;

import com.maccasoft.propeller.spin2.Spin2InstructionObject;

public class P2Disassembler {

    static final String[] conditions = new String[] {
        "_ret_        ",
        "if_nc_and_nz ",
        "if_nc_and_z  ",
        "if_nc        ",
        "if_c_and_nz  ",
        "if_nz        ",
        "if_c_ne_z    ",
        "if_nc_or_nz  ",
        "if_c_and_z   ",
        "if_c_eq_z    ",
        "if_z         ",
        "if_nc_or_z   ",
        "if_c         ",
        "if_c_or_nz   ",
        "if_c_or_z    ",
        "             " // if_always
    };

    static final String[] mod = new String[] {
        "_clr",
        "_nc_and_nz",
        "_nc_and_z",
        "_nc",
        "_c_and_nz",
        "_nz",
        "_c_ne_z",
        "_nc_or_nz",
        "_c_and_z",
        "_c_eq_z",
        "_z",
        "_nc_or_z",
        "_c",
        "_c_or_nz",
        "_c_or_z",
        "_set",
    };

    static final String[] pointers = new String[] {
        "pa", "pb", "ptra", "ptrb"
    };

    static final String[] effect = new String[] {
        "", " wz", " wc", " wcz"
    };

    static final String[] sfr = {
        "ijmp3", "iret3", "ijmp2", "iret2", "ijmp1", "iret1",
        "pa", "pb", "ptra", "ptrb",
        "dira", "dirb", "outa", "outb", "ina", "inb"
    };

    P2Disassembler() {

    }

    public static String disassemble(int addr, int ins) {
        StringBuilder sb;

        if (ins == 0) {
            sb = new StringBuilder();
            sb.append(conditions[0b1111]);
            sb.append("nop");
            return sb.toString();
        }

        sb = new StringBuilder();
        sb.append(conditions[Spin2InstructionObject.e.getValue(ins) & 0b1111]);

        int cz = Spin2InstructionObject.cz.getValue(ins);

        int o = Spin2InstructionObject.o.getValue(ins);
        switch (o) {
            case 0b0000000:
                standardInstruction(sb, "ror     ", ins);
                break;
            case 0b0000001:
                standardInstruction(sb, "rol     ", ins);
                break;
            case 0b0000010:
                standardInstruction(sb, "shr     ", ins);
                break;
            case 0b0000011:
                standardInstruction(sb, "shl     ", ins);
                break;
            case 0b0000100:
                standardInstruction(sb, "rcr     ", ins);
                break;
            case 0b0000101:
                standardInstruction(sb, "rcl     ", ins);
                break;
            case 0b0000110:
                standardInstruction(sb, "sar     ", ins);
                break;
            case 0b0000111:
                standardInstruction(sb, "sal     ", ins);
                break;
            case 0b0001000:
                standardInstruction(sb, "add     ", ins);
                break;
            case 0b0001001:
                standardInstruction(sb, "addx    ", ins);
                break;
            case 0b0001010:
                standardInstruction(sb, "adds    ", ins);
                break;
            case 0b0001011:
                standardInstruction(sb, "addsx   ", ins);
                break;
            case 0b0001100:
                standardInstruction(sb, "sub     ", ins);
                break;
            case 0b0001101:
                standardInstruction(sb, "subx    ", ins);
                break;
            case 0b0001110:
                standardInstruction(sb, "subs    ", ins);
                break;
            case 0b0001111:
                standardInstruction(sb, "subsx   ", ins);
                break;

            case 0b0010000:
                standardInstruction(sb, "cmp     ", ins);
                break;
            case 0b0010001:
                standardInstruction(sb, "cmpx    ", ins);
                break;
            case 0b0010010:
                standardInstruction(sb, "cmps    ", ins);
                break;
            case 0b0010011:
                standardInstruction(sb, "cmpsx   ", ins);
                break;
            case 0b0010100:
                standardInstruction(sb, "cmpr    ", ins);
                break;
            case 0b0010101:
                standardInstruction(sb, "cmpm    ", ins);
                break;
            case 0b0010110:
                standardInstruction(sb, "subr    ", ins);
                break;
            case 0b0010111:
                standardInstruction(sb, "cmpsub  ", ins);
                break;
            case 0b0011000:
                standardInstruction(sb, "fge     ", ins);
                break;
            case 0b0011001:
                standardInstruction(sb, "fle     ", ins);
                break;
            case 0b0011010:
                standardInstruction(sb, "fges    ", ins);
                break;
            case 0b0011011:
                standardInstruction(sb, "fles    ", ins);
                break;
            case 0b0011100:
                standardInstruction(sb, "sumc    ", ins);
                break;
            case 0b0011101:
                standardInstruction(sb, "sumnc   ", ins);
                break;
            case 0b0011110:
                standardInstruction(sb, "sumz    ", ins);
                break;
            case 0b0011111:
                standardInstruction(sb, "sumnz   ", ins);
                break;

            case 0b0100000:
                altInstruction1(sb, "testb   ", "bitl    ", ins);
                break;
            case 0b0100001:
                altInstruction1(sb, "testbn  ", "bith    ", ins);
                break;
            case 0b0100010:
                altInstruction1(sb, "testb   ", "bitc    ", ins);
                break;
            case 0b0100011:
                altInstruction1(sb, "testbn  ", "bitnc   ", ins);
                break;
            case 0b0100100:
                altInstruction1(sb, "testb   ", "bitz    ", ins);
                break;
            case 0b0100101:
                altInstruction1(sb, "testbn  ", "bitnz   ", ins);
                break;
            case 0b0100110:
                altInstruction1(sb, "testb   ", "bitrnd  ", ins);
                break;
            case 0b0100111:
                altInstruction1(sb, "testbn  ", "bitnot  ", ins);
                break;

            case 0b0101000:
                standardInstruction(sb, "and     ", ins);
                break;
            case 0b0101001:
                standardInstruction(sb, "andn    ", ins);
                break;
            case 0b0101010:
                standardInstruction(sb, "or      ", ins);
                break;
            case 0b0101011:
                standardInstruction(sb, "xor     ", ins);
                break;
            case 0b0101100:
                standardInstruction(sb, "muxc    ", ins);
                break;
            case 0b0101101:
                standardInstruction(sb, "muxnc   ", ins);
                break;
            case 0b0101110:
                standardInstruction(sb, "muxz    ", ins);
                break;
            case 0b0101111:
                standardInstruction(sb, "muxnz   ", ins);
                break;

            case 0b0110000:
                standardInstruction(sb, "mov     ", ins);
                break;
            case 0b0110001:
                standardInstruction(sb, "not     ", ins, true);
                break;
            case 0b0110010:
                standardInstruction(sb, "abs     ", ins, true);
                break;
            case 0b0110011:
                standardInstruction(sb, "neg     ", ins, true);
                break;
            case 0b0110100:
                standardInstruction(sb, "negc    ", ins, true);
                break;
            case 0b0110101:
                standardInstruction(sb, "negnc   ", ins, true);
                break;
            case 0b0110110:
                standardInstruction(sb, "negz    ", ins, true);
                break;
            case 0b0110111:
                standardInstruction(sb, "negnz   ", ins, true);
                break;
            case 0b0111000:
                standardInstruction(sb, "incmod  ", ins);
                break;
            case 0b0111001:
                standardInstruction(sb, "decmod  ", ins);
                break;
            case 0b0111010:
                standardInstruction(sb, "zerox   ", ins);
                break;
            case 0b0111011:
                standardInstruction(sb, "signx   ", ins);
                break;
            case 0b0111100:
                standardInstruction(sb, "encod   ", ins, true);
                break;
            case 0b0111101:
                standardInstruction(sb, "ones    ", ins, true);
                break;
            case 0b0111110:
                standardInstruction(sb, "test    ", ins, true);
                break;
            case 0b0111111:
                standardInstruction(sb, "testn   ", ins);
                break;

            case 0b1000000:
            case 0b1000001:
                sb.append("setnib  ");
                if (Spin2InstructionObject.nnn.getValue(ins) == 0 && Spin2InstructionObject.d.getValue(ins) == 0) {
                    if (Spin2InstructionObject.i.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                }
                else {
                    sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                    sb.append(", ");
                    if (Spin2InstructionObject.i.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                    sb.append(", ");
                    sb.append(String.format("#%d", Spin2InstructionObject.nnn.getValue(ins)));
                }
                break;
            case 0b1000010:
            case 0b1000011:
                sb.append("getnib  ");
                sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                if (Spin2InstructionObject.nnn.getValue(ins) != 0 || Spin2InstructionObject.s.getValue(ins) != 0) {
                    sb.append(", ");
                    if (Spin2InstructionObject.i.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                    sb.append(", ");
                    sb.append(String.format("#%d", Spin2InstructionObject.nnn.getValue(ins)));
                }
                break;
            case 0b1000100:
            case 0b1000101:
                sb.append("rolnib  ");
                sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                if (Spin2InstructionObject.nnn.getValue(ins) != 0 || Spin2InstructionObject.s.getValue(ins) != 0) {
                    sb.append(", ");
                    if (Spin2InstructionObject.i.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                    sb.append(", ");
                    sb.append(String.format("#%d", Spin2InstructionObject.nnn.getValue(ins)));
                }
                break;
            case 0b1000110:
                sb.append("setbyte ");
                if (Spin2InstructionObject.cz.getValue(ins) == 0 && Spin2InstructionObject.d.getValue(ins) == 0) {
                    if (Spin2InstructionObject.i.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                }
                else {
                    sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                    sb.append(", ");
                    if (Spin2InstructionObject.i.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                    sb.append(", ");
                    sb.append(String.format("#%d", Spin2InstructionObject.cz.getValue(ins)));
                }
                break;
            case 0b1000111:
                sb.append("getbyte ");
                sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                if (Spin2InstructionObject.cz.getValue(ins) != 0 || Spin2InstructionObject.s.getValue(ins) != 0) {
                    sb.append(", ");
                    if (Spin2InstructionObject.i.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                    sb.append(", ");
                    sb.append(String.format("#%d", Spin2InstructionObject.cz.getValue(ins)));
                }
                break;
            case 0b1001000:
                sb.append("rolbyte ");
                sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                if (Spin2InstructionObject.czi.getValue(ins) != 0b000 || Spin2InstructionObject.s.getValue(ins) != 0) {
                    sb.append(", ");
                    if (Spin2InstructionObject.i.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                    sb.append(", ");
                    sb.append(String.format("#%d", Spin2InstructionObject.cz.getValue(ins)));
                }
                break;
            case 0b1001001:
                if (Spin2InstructionObject.c.getValue(ins) == 0) {
                    sb.append("setword ");
                    if (Spin2InstructionObject.z.getValue(ins) == 0 && Spin2InstructionObject.d.getValue(ins) == 0) {
                        if (Spin2InstructionObject.i.getValue(ins) == 1) {
                            sb.append("#");
                        }
                        sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                    }
                    else {
                        sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                        sb.append(", ");
                        if (Spin2InstructionObject.i.getValue(ins) == 1) {
                            sb.append("#");
                        }
                        sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                        sb.append(", ");
                        sb.append(String.format("#%d", Spin2InstructionObject.z.getValue(ins)));
                    }
                }
                else {
                    sb.append("getword ");
                    sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                    if (Spin2InstructionObject.czi.getValue(ins) != 0b100 || Spin2InstructionObject.s.getValue(ins) != 0) {
                        sb.append(", ");
                        if (Spin2InstructionObject.i.getValue(ins) == 1) {
                            sb.append("#");
                        }
                        sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                        sb.append(", ");
                        sb.append(String.format("#%d", Spin2InstructionObject.z.getValue(ins)));
                    }
                }
                break;
            case 0b1001010:
                if (Spin2InstructionObject.c.getValue(ins) == 0) {
                    sb.append("rolword ");
                    sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                    if (Spin2InstructionObject.czi.getValue(ins) != 0b000 || Spin2InstructionObject.s.getValue(ins) != 0) {
                        sb.append(", ");
                        if (Spin2InstructionObject.i.getValue(ins) == 1) {
                            sb.append("#");
                        }
                        sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                        sb.append(", ");
                        sb.append(String.format("#%d", Spin2InstructionObject.z.getValue(ins)));
                    }
                }
                else {
                    if (Spin2InstructionObject.cz.getValue(ins) == 0b10) {
                        sb.append("altsn   ");
                    }
                    else if (Spin2InstructionObject.cz.getValue(ins) == 0b11) {
                        sb.append("altgn   ");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                    if (Spin2InstructionObject.i.getValue(ins) != 1 || Spin2InstructionObject.s.getValue(ins) != 0) {
                        sb.append(", ");
                        if (Spin2InstructionObject.i.getValue(ins) == 1) {
                            sb.append("#");
                        }
                        sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                    }
                }
                break;
            case 0b1001011:
                if (Spin2InstructionObject.cz.getValue(ins) == 0b00) {
                    sb.append("altsb   ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b01) {
                    sb.append("altgb   ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b10) {
                    sb.append("altsw   ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b11) {
                    sb.append("altgw   ");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                if (Spin2InstructionObject.i.getValue(ins) != 1 || Spin2InstructionObject.s.getValue(ins) != 0) {
                    sb.append(", ");
                    if (Spin2InstructionObject.i.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                }
                break;
            case 0b1001100:
                if (Spin2InstructionObject.cz.getValue(ins) == 0b00) {
                    sb.append("altr    ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b01) {
                    sb.append("altd    ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b10) {
                    sb.append("alts    ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b11) {
                    sb.append("altb    ");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                if (Spin2InstructionObject.i.getValue(ins) != 1 || Spin2InstructionObject.s.getValue(ins) != 0) {
                    sb.append(", ");
                    if (Spin2InstructionObject.i.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                }
                break;
            case 0b1001101:
                if (Spin2InstructionObject.cz.getValue(ins) == 0b00) {
                    sb.append("alti    ");
                    sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                    if (Spin2InstructionObject.i.getValue(ins) != 1 || Spin2InstructionObject.s.getValue(ins) != 0b101100100) {
                        sb.append(", ");
                        if (Spin2InstructionObject.i.getValue(ins) == 1) {
                            sb.append("#");
                        }
                        sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                    }
                }
                else {
                    if (Spin2InstructionObject.cz.getValue(ins) == 0b01) {
                        sb.append("setr    ");
                    }
                    else if (Spin2InstructionObject.cz.getValue(ins) == 0b10) {
                        sb.append("setd    ");
                    }
                    else if (Spin2InstructionObject.cz.getValue(ins) == 0b11) {
                        sb.append("sets    ");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                    sb.append(", ");
                    if (Spin2InstructionObject.i.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                }
                break;
            case 0b1001110:
                if (Spin2InstructionObject.cz.getValue(ins) == 0b00) {
                    sb.append("decod   ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b01) {
                    sb.append("bmask   ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b10) {
                    sb.append("crcbit  ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b11) {
                    sb.append("crcnib  ");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                if (Spin2InstructionObject.i.getValue(ins) != 1 || Spin2InstructionObject.s.getValue(ins) != Spin2InstructionObject.d.getValue(ins)) {
                    sb.append(", ");
                    if (Spin2InstructionObject.i.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                }
                break;
            case 0b1001111:
                if (Spin2InstructionObject.cz.getValue(ins) == 0b00) {
                    sb.append("muxnits ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b01) {
                    sb.append("muxnibs ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b10) {
                    sb.append("muxq    ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b11) {
                    sb.append("movbyts ");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                sb.append(", ");
                if (Spin2InstructionObject.i.getValue(ins) == 1) {
                    sb.append("#");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                break;

            case 0b1010000:
                if (Spin2InstructionObject.c.getValue(ins) == 0) {
                    sb.append("mul     ");
                }
                else {
                    sb.append("muls    ");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                sb.append(", ");
                if (Spin2InstructionObject.i.getValue(ins) == 1) {
                    sb.append("#");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                if (Spin2InstructionObject.z.getValue(ins) == 1) {
                    sb.append(" wz");
                }
                break;
            case 0b1010001:
                if (Spin2InstructionObject.c.getValue(ins) == 0) {
                    sb.append("sca     ");
                }
                else {
                    sb.append("scas    ");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                sb.append(", ");
                if (Spin2InstructionObject.i.getValue(ins) == 1) {
                    sb.append("#");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                if (Spin2InstructionObject.z.getValue(ins) == 1) {
                    sb.append(" wz");
                }
                break;
            case 0b1010010:
                if (Spin2InstructionObject.cz.getValue(ins) == 0b00) {
                    sb.append("addpix  ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b01) {
                    sb.append("mulpix  ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b10) {
                    sb.append("blnpix  ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b11) {
                    sb.append("mixpix  ");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                sb.append(", ");
                if (Spin2InstructionObject.i.getValue(ins) == 1) {
                    sb.append("#");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                break;
            case 0b1010011:
                if (Spin2InstructionObject.cz.getValue(ins) == 0b00) {
                    sb.append("addct1  ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b01) {
                    sb.append("addct2  ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b10) {
                    sb.append("addct3  ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b11) {
                    sb.append("wmlong  ");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                sb.append(", ");
                if (Spin2InstructionObject.i.getValue(ins) == 1) {
                    sb.append("#");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                break;
            case 0b1010100:
                if (Spin2InstructionObject.z.getValue(ins) == 0) {
                    sb.append("rqpin   ");
                }
                else {
                    sb.append("rdpin   ");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                sb.append(", ");
                if (Spin2InstructionObject.i.getValue(ins) == 1) {
                    sb.append("#");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                if (Spin2InstructionObject.c.getValue(ins) == 1) {
                    sb.append(" wc");
                }
                break;
            case 0b1010101:
                ptr(sb, "rdlut   ", ins, false);
                sb.append(effect[Spin2InstructionObject.cz.getValue(ins)]);
                break;
            case 0b1010110:
                ptr(sb, "rdbyte  ", ins, false);
                sb.append(effect[Spin2InstructionObject.cz.getValue(ins)]);
                break;
            case 0b1010111:
                ptr(sb, "rdword  ", ins, false);
                sb.append(effect[Spin2InstructionObject.cz.getValue(ins)]);
                break;
            case 0b1011000:
                ptr(sb, "rdlong  ", ins, false);
                sb.append(effect[Spin2InstructionObject.cz.getValue(ins)]);
                break;
            case 0b1011001:
                switch (ins & 0b111_111111111_111111111) {
                    case 0b110_111110000_111110001:
                        sb.append("resi3   ");
                        break;
                    case 0b110_111110010_111110011:
                        sb.append("resi2   ");
                        break;
                    case 0b110_111110100_111110101:
                        sb.append("resi1   ");
                        break;
                    case 0b110_111111110_111111111:
                        sb.append("resi0   ");
                        break;
                    case 0b110_111111111_111110001:
                        sb.append("reti3   ");
                        break;
                    case 0b110_111111111_111110011:
                        sb.append("reti2   ");
                        break;
                    case 0b110_111111111_111110101:
                        sb.append("reti1   ");
                        break;
                    case 0b110_111111111_111111111:
                        sb.append("reti0   ");
                        break;
                    default:
                        standardInstruction(sb, "calld   ", ins);
                        break;
                }
                break;
            case 0b1011010:
                sb.append(Spin2InstructionObject.c.getValue(ins) == 0 ? "callpa  " : "callpb  ");
                if (Spin2InstructionObject.l.getValue(ins) == 1) {
                    sb.append("#");
                }
                sb.append(register(Spin2InstructionObject.d.getValue(ins)));
                sb.append(", ");
                if (Spin2InstructionObject.i.getValue(ins) == 1) {
                    sb.append("#");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                break;
            case 0b1011011:
                if (Spin2InstructionObject.cz.getValue(ins) == 0b00) {
                    sb.append("djz     ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b01) {
                    sb.append("djnz    ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b10) {
                    sb.append("djf     ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b11) {
                    sb.append("djnf    ");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                sb.append(", ");
                if (Spin2InstructionObject.i.getValue(ins) == 1) {
                    sb.append("#");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                break;
            case 0b1011100:
                if (Spin2InstructionObject.cz.getValue(ins) == 0b00) {
                    sb.append("ijz     ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b01) {
                    sb.append("ijnz    ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b10) {
                    sb.append("tjz     ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b11) {
                    sb.append("tjnz    ");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                sb.append(", ");
                if (Spin2InstructionObject.i.getValue(ins) == 1) {
                    sb.append("#");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                break;
            case 0b1011101:
                if (Spin2InstructionObject.cz.getValue(ins) == 0b00) {
                    sb.append("tjf     ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b01) {
                    sb.append("tjnf    ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b10) {
                    sb.append("tjs     ");
                }
                else if (Spin2InstructionObject.cz.getValue(ins) == 0b11) {
                    sb.append("tjns    ");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                sb.append(", ");
                if (Spin2InstructionObject.i.getValue(ins) == 1) {
                    sb.append("#");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                break;
            case 0b1011110:
                if (cz == 0b00) {
                    sb.append("tjv     ");
                    sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                    sb.append(", ");
                    if (Spin2InstructionObject.i.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                }
                else if (cz == 0b01) {
                    switch (Spin2InstructionObject.d.getValue(ins)) {
                        case 0b000000000:
                            sb.append("jint    ");
                            break;
                        case 0b000000001:
                            sb.append("jct1    ");
                            break;
                        case 0b000000010:
                            sb.append("jct2    ");
                            break;
                        case 0b000000011:
                            sb.append("jct3    ");
                            break;
                        case 0b000000100:
                            sb.append("jse1    ");
                            break;
                        case 0b000000101:
                            sb.append("jse2    ");
                            break;
                        case 0b000000110:
                            sb.append("jse3    ");
                            break;
                        case 0b000000111:
                            sb.append("jse4    ");
                            break;
                        case 0b000001000:
                            sb.append("jpat    ");
                            break;
                        case 0b000001001:
                            sb.append("jfbw    ");
                            break;
                        case 0b000001010:
                            sb.append("jxmt    ");
                            break;
                        case 0b000001011:
                            sb.append("jxfi    ");
                            break;
                        case 0b000001100:
                            sb.append("jxro    ");
                            break;
                        case 0b000001101:
                            sb.append("jxrl    ");
                            break;
                        case 0b000001110:
                            sb.append("jatn    ");
                            break;
                        case 0b000001111:
                            sb.append("jqmt    ");
                            break;
                        case 0b000010000:
                            sb.append("jnint   ");
                            break;
                        case 0b000010001:
                            sb.append("jnct1   ");
                            break;
                        case 0b000010010:
                            sb.append("jnct2   ");
                            break;
                        case 0b000010011:
                            sb.append("jnct3   ");
                            break;
                        case 0b000010100:
                            sb.append("jnse1   ");
                            break;
                        case 0b000010101:
                            sb.append("jnse2   ");
                            break;
                        case 0b000010110:
                            sb.append("jnse3   ");
                            break;
                        case 0b000010111:
                            sb.append("jnse4   ");
                            break;
                        case 0b000011000:
                            sb.append("jnpat   ");
                            break;
                        case 0b000011001:
                            sb.append("jnfbw   ");
                            break;
                        case 0b000011010:
                            sb.append("jnxmt   ");
                            break;
                        case 0b000011011:
                            sb.append("jnxfi   ");
                            break;
                        case 0b000011100:
                            sb.append("jnxro   ");
                            break;
                        case 0b000011101:
                            sb.append("jnxrl   ");
                            break;
                        case 0b000011110:
                            sb.append("jnatn   ");
                            break;
                        case 0b000011111:
                            sb.append("jnqmt   ");
                            break;
                    }
                    if (Spin2InstructionObject.i.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                }
                else if (Spin2InstructionObject.c.getValue(ins) == 1) {
                    srcDstOnly(sb, "<empty> ", ins);
                }
                break;
            case 0b1011111:
                if (Spin2InstructionObject.c.getValue(ins) == 0) {
                    srcDstOnly(sb, "<empty> ", ins);
                }
                else {
                    srcDstOnly(sb, "setpat  ", ins);
                }
                break;

            case 0b1100000:
                if (Spin2InstructionObject.cz.getValue(ins) == 0b01) {
                    sb.append("akpin   ");
                    if (Spin2InstructionObject.i.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                }
                else {
                    if (Spin2InstructionObject.c.getValue(ins) == 0) {
                        sb.append("wrpin   ");
                    }
                    else {
                        sb.append("wxpin   ");
                    }
                    if (Spin2InstructionObject.l.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                    sb.append(", ");
                    if (Spin2InstructionObject.i.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                }
                break;
            case 0b1100001:
                if (Spin2InstructionObject.c.getValue(ins) == 0) {
                    sb.append("wypin   ");
                    if (Spin2InstructionObject.l.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                    sb.append(", ");
                    if (Spin2InstructionObject.i.getValue(ins) == 1) {
                        sb.append("#");
                    }
                    sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                }
                else {
                    ptr(sb, "wrlut   ", ins, true);
                }
                break;
            case 0b1100010:
                if (Spin2InstructionObject.c.getValue(ins) == 0) {
                    ptr(sb, "wrbyte  ", ins, true);
                }
                else {
                    ptr(sb, "wrword  ", ins, true);
                }
                break;
            case 0b1100011:
                if (Spin2InstructionObject.c.getValue(ins) == 0) {
                    ptr(sb, "wrlong  ", ins, true);
                }
                else {
                    srcDstOnly(sb, "rdfast  ", ins);
                }
                break;
            case 0b1100100:
                if (Spin2InstructionObject.c.getValue(ins) == 0) {
                    sb.append("wrfast  ");
                }
                else {
                    sb.append("fblock  ");
                }
                if (Spin2InstructionObject.l.getValue(ins) == 1) {
                    sb.append("#");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.d.getValue(ins)));
                sb.append(", ");
                if (Spin2InstructionObject.i.getValue(ins) == 1) {
                    sb.append("#");
                }
                sb.append(String.format("$%03X", Spin2InstructionObject.s.getValue(ins)));
                break;
            case 0b1100101:
                if (Spin2InstructionObject.czi.getValue(ins) == 0b011 && Spin2InstructionObject.d.getValue(ins) == 0 && Spin2InstructionObject.s.getValue(ins) == 0) {
                    sb.append("xstop   ");
                }
                else {
                    if (Spin2InstructionObject.c.getValue(ins) == 0) {
                        srcDstOnly(sb, "xinit   ", ins);
                    }
                    else {
                        srcDstOnly(sb, "xzero   ", ins);
                    }
                }
                break;
            case 0b1100110:
                if (Spin2InstructionObject.c.getValue(ins) == 0) {
                    srcDstOnly(sb, "xcont   ", ins);
                }
                else {
                    srcDstOnly(sb, "rep     ", ins);
                }
                break;
            case 0b1100111:
                srcDstOnly(sb, "coginit ", ins);
                if (Spin2InstructionObject.c.getValue(ins) == 1) {
                    sb.append(" wc");
                }
                break;
            case 0b1101000:
                if (Spin2InstructionObject.c.getValue(ins) == 0) {
                    srcDstOnly(sb, "qmul    ", ins);
                }
                else {
                    srcDstOnly(sb, "qdiv    ", ins);
                }
                break;
            case 0b1101001:
                if (Spin2InstructionObject.c.getValue(ins) == 0) {
                    srcDstOnly(sb, "qfrac   ", ins);
                }
                else {
                    srcDstOnly(sb, "qsqrt   ", ins);
                }
                break;
            case 0b1101010:
                if (Spin2InstructionObject.c.getValue(ins) == 0) {
                    srcDstOnly(sb, "qrotate ", ins);
                }
                else {
                    srcDstOnly(sb, "qvector ", ins);
                }
                break;
            case 0b1101011:
                group1101011(sb, ins);
                break;
            case 0b1101100:
                jmpCall(sb, "jmp     ", ins);
                break;
            case 0b1101101:
                jmpCall(sb, "call    ", ins);
                break;
            case 0b1101110:
                jmpCall(sb, "calla   ", ins);
                break;
            case 0b1101111:
                jmpCall(sb, "callb   ", ins);
                break;

            case 0b1110000:
            case 0b1110001:
            case 0b1110010:
            case 0b1110011:
                sb.append("calld   ");
                sb.append(pointers[Spin2InstructionObject.o.getValue(ins) & 0b11]);
                sb.append(", #");
                sb.append(String.format("$%08X", Spin2InstructionObject.a.getValue(ins)));
                break;
            case 0b1110100:
            case 0b1110101:
            case 0b1110110:
            case 0b1110111:
                sb.append("loc     ");
                sb.append(pointers[Spin2InstructionObject.o.getValue(ins) & 0b11]);
                sb.append(", #");
                sb.append(String.format("$%08X", Spin2InstructionObject.a.getValue(ins)));
                break;
            case 0b1111000:
            case 0b1111001:
            case 0b1111010:
            case 0b1111011:
                sb.append("augs    #");
                sb.append(String.format("$%08X", Spin2InstructionObject.x.getValue(ins) << 9).substring(0, 7));
                sb.append("xx");
                break;
            case 0b1111100:
            case 0b1111101:
            case 0b1111110:
            case 0b1111111:
                sb.append("augd    #");
                sb.append(String.format("$%08X", Spin2InstructionObject.x.getValue(ins) << 9).substring(0, 7));
                sb.append("xx");
                break;
        }

        return sb.toString();
    }

    static void ptr(StringBuilder sb, String mnemonic, int ins, boolean immd) {
        sb.append(mnemonic);
        if (immd && Spin2InstructionObject.l.getValue(ins) == 1) {
            sb.append("#");
        }
        sb.append(register(Spin2InstructionObject.d.getValue(ins)));
        sb.append(", ");

        int s = Spin2InstructionObject.s.getValue(ins);
        if (Spin2InstructionObject.i.getValue(ins) == 1) {
            if ((s & 0b001000000) != 0) {
                int ofs = ((s & 0x1F) << 27) >> 27;
                if ((s & 0b000100000) == 0) {
                    sb.append(ofs < 0 ? "--" : "++");
                }
                sb.append((s & 0b010000000) == 0 ? "ptra" : "ptrb");
                if ((s & 0b000100000) != 0) {
                    sb.append(ofs < 0 ? "--" : "++");
                }
                if (ofs != 0 && ofs != -1 && ofs != 1) {
                    sb.append(String.format("[%d]", Math.abs(ofs)));
                }
            }
            else {
                sb.append((s & 0b010000000) == 0 ? "ptra" : "ptrb");
                if ((s & 0x3F) != 0) {
                    sb.append(String.format("[%d]", ((s & 0x3F) << 26) >> 26));
                }
            }
        }
        else {
            sb.append(register(s));
        }
    }

    static void srcDstOnly(StringBuilder sb, String mnemonic, int ins) {
        sb.append(mnemonic);
        if (Spin2InstructionObject.l.getValue(ins) == 1) {
            sb.append("#");
        }
        sb.append(register(Spin2InstructionObject.d.getValue(ins)));
        sb.append(", ");
        if (Spin2InstructionObject.i.getValue(ins) == 1) {
            sb.append("#");
        }
        sb.append(register(Spin2InstructionObject.s.getValue(ins)));
    }

    static void standardInstruction(StringBuilder sb, String mnemonic, int ins) {
        standardInstruction(sb, mnemonic, ins, false);
    }

    static void standardInstruction(StringBuilder sb, String mnemonic, int ins, boolean canOmitSrc) {
        boolean i = Spin2InstructionObject.i.getValue(ins) == 1;
        int cz = Spin2InstructionObject.cz.getValue(ins);
        int dst = Spin2InstructionObject.d.getValue(ins);
        int src = Spin2InstructionObject.s.getValue(ins);

        sb.append(mnemonic);
        sb.append(register(dst));

        if (!canOmitSrc || src != dst || i) {
            sb.append(", ");
            if (i) {
                sb.append("#");
            }
            sb.append(register(src));
        }

        sb.append(effect[cz]);
    }

    static void altInstruction1(StringBuilder sb, String mnemonic1, String mnemonic2, int ins) {
        boolean i = Spin2InstructionObject.i.getValue(ins) == 1;
        int cz = Spin2InstructionObject.cz.getValue(ins);
        int dst = Spin2InstructionObject.d.getValue(ins);
        int src = Spin2InstructionObject.s.getValue(ins);

        sb.append((cz == 0b00 || cz == 0b11) ? mnemonic2 : mnemonic1);

        sb.append(register(dst));

        sb.append(", ");
        if (i) {
            sb.append("#");
        }
        sb.append(register(src));

        String swc = " wc";
        String swz = " wz";
        switch (Spin2InstructionObject.o.getValue(ins) & 0b111) {
            case 0b010:
            case 0b011:
                swc = " andc";
                swz = " andz";
                break;
            case 0b100:
            case 0b101:
                swc = " orc";
                swz = " orz";
                break;
            case 0b110:
            case 0b111:
                swc = " xorc";
                swz = " xorz";
                break;
        }

        boolean wc = Spin2InstructionObject.c.getValue(ins) == 1;
        boolean wz = Spin2InstructionObject.z.getValue(ins) == 1;
        if (wc && wz) {
            sb.append(" wcz");
        }
        else if (wc) {
            sb.append(swc);
        }
        else if (wz) {
            sb.append(swz);
        }
    }

    static void jmpCall(StringBuilder sb, String mnemonic, int ins) {
        sb.append(mnemonic);

        sb.append("#");
        if (Spin2InstructionObject.r.getValue(ins) == 0) {
            sb.append("\\");
        }
        sb.append(String.format("$%05X", Spin2InstructionObject.a.getValue(ins)));
    }

    static void group1101011(StringBuilder sb, int ins) {
        boolean i = Spin2InstructionObject.i.getValue(ins) == 1;
        int dst = Spin2InstructionObject.d.getValue(ins);
        int cz = Spin2InstructionObject.cz.getValue(ins);

        switch (Spin2InstructionObject.s.getValue(ins)) {
            case 0b000000000:
                sb.append("hubset  ");
                break;
            case 0b000000001:
                sb.append("cogid   ");
                break;
            case 0b000000011:
                sb.append("cogstop ");
                break;
            case 0b000000100:
                sb.append("locknew ");
                break;
            case 0b000000101:
                sb.append("lockret ");
                break;
            case 0b000000110:
                sb.append("locktry ");
                break;
            case 0b000000111:
                sb.append("lockrel ");
                break;

            case 0b000001000:
                sb.append("??? xx1 ");
                break;
            case 0b000001001:
                sb.append("??? xx2 ");
                break;
            case 0b000001010:
                sb.append("??? xx3 ");
                break;
            case 0b000001011:
                sb.append("??? xx4 ");
                break;
            case 0b000001100:
                sb.append("??? xx5 ");
                break;
            case 0b000001101:
                sb.append("??? xx6 ");
                break;
            case 0b000001110:
                sb.append("qlog    ");
                break;
            case 0b000001111:
                sb.append("qexp    ");
                break;

            case 0b000010000:
                sb.append("rfbyte  ");
                break;
            case 0b000010001:
                sb.append("rfword  ");
                break;
            case 0b000010010:
                sb.append("rflong  ");
                break;
            case 0b000010011:
                sb.append("rfvar   ");
                break;
            case 0b000010100:
                sb.append("rfvars  ");
                break;
            case 0b000010101:
                sb.append("wfbyte  ");
                break;
            case 0b000010110:
                sb.append("wfword  ");
                break;
            case 0b000010111:
                sb.append("wflong  ");
                break;

            case 0b000011000:
                sb.append("getqx   ");
                break;
            case 0b000011001:
                sb.append("getqy   ");
                break;
            case 0b000011010:
                sb.append("getct   ");
                break;
            case 0b000011011:
                sb.append("getrnd  ");
                break;
            case 0b000011100:
                sb.append("setdacs ");
                break;
            case 0b000011101:
                sb.append("setxfrq ");
                break;
            case 0b000011110:
                sb.append("getxacc ");
                break;
            case 0b000011111:
                sb.append("waitx   ");
                break;

            case 0b000100000:
                sb.append("setse1  ");
                break;
            case 0b000100001:
                sb.append("setse2  ");
                break;
            case 0b000100010:
                sb.append("setse3  ");
                break;
            case 0b000100011:
                sb.append("setse4  ");
                break;

            case 0b000100100:
                if (Spin2InstructionObject.i.getValue(ins) == 0 && dst <= 0b000011110) {
                    switch (dst) {
                        case 0b000000000:
                            sb.append("pollint ");
                            break;
                        case 0b000000001:
                            sb.append("pollct1 ");
                            break;
                        case 0b000000010:
                            sb.append("pollct2 ");
                            break;
                        case 0b000000011:
                            sb.append("pollct3 ");
                            break;
                        case 0b000000100:
                            sb.append("pollse1 ");
                            break;
                        case 0b000000101:
                            sb.append("pollse2 ");
                            break;
                        case 0b000000110:
                            sb.append("pollse3 ");
                            break;
                        case 0b000000111:
                            sb.append("pollse4 ");
                            break;
                        case 0b000001000:
                            sb.append("pollpat ");
                            break;
                        case 0b000001001:
                            sb.append("pollfbw ");
                            break;
                        case 0b000001010:
                            sb.append("pollxmt ");
                            break;
                        case 0b000001011:
                            sb.append("pollxfi ");
                            break;
                        case 0b000001100:
                            sb.append("pollxro ");
                            break;
                        case 0b000001101:
                            sb.append("pollxrl ");
                            break;
                        case 0b000001110:
                            sb.append("pollatn ");
                            break;
                        case 0b000001111:
                            sb.append("pollqmt ");
                            break;
                        case 0b000010000:
                            sb.append("waitint ");
                            break;
                        case 0b000010001:
                            sb.append("waitct1 ");
                            break;
                        case 0b000010010:
                            sb.append("waitct2 ");
                            break;
                        case 0b000010011:
                            sb.append("waitct3 ");
                            break;
                        case 0b000010100:
                            sb.append("waitse1 ");
                            break;
                        case 0b000010101:
                            sb.append("waitse2 ");
                            break;
                        case 0b000010110:
                            sb.append("waitse3 ");
                            break;
                        case 0b000010111:
                            sb.append("waitse4 ");
                            break;
                        case 0b000011000:
                            sb.append("waitpat ");
                            break;
                        case 0b000011001:
                            sb.append("waitfbw ");
                            break;
                        case 0b000011010:
                            sb.append("waitxmt ");
                            break;
                        case 0b000011011:
                            sb.append("waitxfi ");
                            break;
                        case 0b000011100:
                            sb.append("waitxro ");
                            break;
                        case 0b000011101:
                            sb.append("waitxrl ");
                            break;
                        case 0b000011110:
                            sb.append("waitatn ");
                            break;
                        default:
                            sb.append("??? x13 ");
                            break;
                    }
                    sb.append(effect[cz]);
                    return;
                }
                if (Spin2InstructionObject.czi.getValue(ins) == 0b000) {
                    switch (dst) {
                        case 0b000100000:
                            sb.append("allowi  ");
                            return;
                        case 0b000100001:
                            sb.append("stalli  ");
                            return;
                        case 0b000100010:
                            sb.append("trgint1 ");
                            return;
                        case 0b000100011:
                            sb.append("trgint2 ");
                            return;
                        case 0b000100100:
                            sb.append("trgint3 ");
                            return;
                        case 0b000100101:
                            sb.append("nixint1 ");
                            return;
                        case 0b000100110:
                            sb.append("nixint2 ");
                            return;
                        case 0b000100111:
                            sb.append("nixint3 ");
                            return;
                    }
                }
                sb.append("??? x12 ");
                return;
            case 0b000100101:
                sb.append("setint1 ");
                break;
            case 0b000100110:
                sb.append("setint2 ");
                break;
            case 0b000100111:
                sb.append("setint3 ");
                break;

            case 0b000101000:
                sb.append("setq    ");
                break;
            case 0b000101001:
                sb.append("setq2   ");
                break;
            case 0b000101010:
                sb.append("push    ");
                break;
            case 0b000101011:
                sb.append("pop     ");
                break;
            case 0b000101100:
                sb.append("jmp     ");
                break;
            case 0b000101101:
                sb.append(Spin2InstructionObject.i.getValue(ins) == 0 ? "call    " : "ret     ");
                sb.append(register(dst));
                sb.append(effect[cz]);
                return;
            case 0b000101110:
                sb.append(Spin2InstructionObject.i.getValue(ins) == 0 ? "calla   " : "reta    ");
                sb.append(register(dst));
                sb.append(effect[cz]);
                return;
            case 0b000101111:
                sb.append(Spin2InstructionObject.i.getValue(ins) == 0 ? "callb   " : "retb    ");
                sb.append(register(dst));
                sb.append(effect[cz]);
                return;

            case 0b000110000:
                sb.append("jmprel  ");
                break;
            case 0b000110001:
                sb.append("skip    ");
                break;
            case 0b000110010:
                sb.append("skipf   ");
                break;
            case 0b000110011:
                sb.append("execf   ");
                break;

            case 0b000110100:
                sb.append("getptr  ");
                break;
            case 0b000110101:
                sb.append("cogbrk  ");
                break;
            case 0b000110110:
                sb.append("brk     ");
                break;
            case 0b000110111:
                sb.append("setluts ");
                break;

            case 0b000111000:
                sb.append("setcy   ");
                break;
            case 0b000111001:
                sb.append("setci   ");
                break;
            case 0b000111010:
                sb.append("setcq   ");
                break;
            case 0b000111011:
                sb.append("setcfrq ");
                break;
            case 0b000111100:
                sb.append("setcmod ");
                break;
            case 0b000111101:
                sb.append("setpiv  ");
                break;
            case 0b000111110:
                sb.append("setpix  ");
                break;
            case 0b000111111:
                sb.append("cogatn  ");
                break;

            case 0b001000000:
                altInstruction2(sb, "testp   ", "dirl    ", ins);
                return;
            case 0b001000001:
                altInstruction2(sb, "testpn  ", "dirh    ", ins);
                return;
            case 0b001000010:
                altInstruction2(sb, "testp   ", "dirc    ", ins);
                return;
            case 0b001000011:
                altInstruction2(sb, "testpn  ", "dirnc   ", ins);
                return;
            case 0b001000100:
                altInstruction2(sb, "testp   ", "dirz    ", ins);
                return;
            case 0b001000101:
                altInstruction2(sb, "testpn  ", "dirnz   ", ins);
                return;
            case 0b001000110:
                altInstruction2(sb, "testp   ", "dirrnd  ", ins);
                return;
            case 0b001000111:
                altInstruction2(sb, "testpn  ", "dirnot  ", ins);
                return;

            case 0b001001000:
                sb.append("outl    ");
                break;
            case 0b001001001:
                sb.append("outh    ");
                break;
            case 0b001001010:
                sb.append("outc    ");
                break;
            case 0b001001011:
                sb.append("outnc   ");
                break;
            case 0b001001100:
                sb.append("outz    ");
                break;
            case 0b001001101:
                sb.append("outnz   ");
                break;
            case 0b001001110:
                sb.append("outrnd  ");
                break;
            case 0b001001111:
                sb.append("outnot  ");
                break;

            case 0b001010000:
                sb.append("fltl    ");
                break;
            case 0b001010001:
                sb.append("flth    ");
                break;
            case 0b001010010:
                sb.append("fltc    ");
                break;
            case 0b001010011:
                sb.append("fltnc   ");
                break;
            case 0b001010100:
                sb.append("fltz    ");
                break;
            case 0b001010101:
                sb.append("fltnz   ");
                break;
            case 0b001010110:
                sb.append("fltrnd  ");
                break;
            case 0b001010111:
                sb.append("fltnot  ");
                break;

            case 0b001011000:
                sb.append("drvl    ");
                break;
            case 0b001011001:
                sb.append("drvh    ");
                break;
            case 0b001011010:
                sb.append("drvc    ");
                break;
            case 0b001011011:
                sb.append("drvnc   ");
                break;
            case 0b001011100:
                sb.append("drvz    ");
                break;
            case 0b001011101:
                sb.append("drvnz   ");
                break;
            case 0b001011110:
                sb.append("drvrnd  ");
                break;
            case 0b001011111:
                sb.append("drvnot  ");
                break;

            case 0b001100000:
                sb.append("splitb  ");
                break;
            case 0b001100001:
                sb.append("mergeb  ");
                break;
            case 0b001100010:
                sb.append("splitw  ");
                break;
            case 0b001100011:
                sb.append("mergew  ");
                break;
            case 0b001100100:
                sb.append("seussf  ");
                break;
            case 0b001100101:
                sb.append("seussr  ");
                break;
            case 0b001100110:
                sb.append("rgbsqz  ");
                break;
            case 0b001100111:
                sb.append("rgbexp  ");
                break;

            case 0b001101000:
                sb.append("xoro32  ");
                break;
            case 0b001101001:
                sb.append("rev     ");
                break;
            case 0b001101010:
                sb.append("rczr    ");
                break;
            case 0b001101011:
                sb.append("rczl    ");
                break;
            case 0b001101100:
                sb.append("wrc     ");
                break;
            case 0b001101101:
                sb.append("wrnc    ");
                break;
            case 0b001101110:
                sb.append("wrz     ");
                break;
            case 0b001101111:
                if (i) {
                    if (cz == 0b10) {
                        sb.append("modc    ");
                        sb.append(mod[dst >> 4]);
                    }
                    else if (cz == 0b01) {
                        sb.append("modz    ");
                        sb.append(mod[dst & 0b1111]);
                    }
                    else {
                        sb.append("modcz   ");
                        sb.append(mod[dst >> 4]);
                        sb.append(", ");
                        sb.append(mod[dst & 0b1111]);
                    }
                    sb.append(effect[cz]);
                    return;
                }
                sb.append("wrnz    ");
                break;

            case 0b001110000:
                sb.append("setscp  ");
                break;
            case 0b001110001:
                sb.append("getscp  ");
                break;

            default:
                sb.append("???     ");
                break;
        }

        if (i) {
            sb.append("#");
        }
        sb.append(register(dst));
        sb.append(effect[cz]);
    }

    static void altInstruction2(StringBuilder sb, String mnemonic1, String mnemonic2, int ins) {
        boolean i = Spin2InstructionObject.i.getValue(ins) == 1;
        int cz = Spin2InstructionObject.cz.getValue(ins);
        int dst = Spin2InstructionObject.d.getValue(ins);

        sb.append((cz == 0b00 || cz == 0b11) ? mnemonic2 : mnemonic1);

        if (i) {
            sb.append("#");
        }
        sb.append(register(dst));

        String swc = " wc";
        String swz = " wz";
        switch (Spin2InstructionObject.s.getValue(ins) & 0b110) {
            case 0b010:
            case 0b011:
                swc = " andc";
                swz = " andz";
                break;
            case 0b100:
            case 0b101:
                swc = " orc";
                swz = " orz";
                break;
            case 0b110:
            case 0b111:
                swc = " xorc";
                swz = " xorz";
                break;
        }

        if (cz == 0b10) {
            sb.append(swc);
        }
        else if (cz == 0b01) {
            sb.append(swz);
        }
        else if (cz == 0b11) {
            sb.append(" wcz");
        }
    }

    static String register(int a) {
        if (a >= 0x1F0 && a <= 0x1FF) {
            return sfr[a - 0x1F0];
        }
        return String.format("$%03X", a);
    }

}
