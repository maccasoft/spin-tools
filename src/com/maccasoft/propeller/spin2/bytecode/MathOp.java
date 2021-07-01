/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.bytecode;

import java.util.HashMap;
import java.util.Map;

import com.maccasoft.propeller.spin2.Spin2Bytecode;
import com.maccasoft.propeller.spin2.Spin2Context;

public class MathOp extends Spin2Bytecode {

    static class Descriptor {
        int value;
        String text;

        public Descriptor(int value, String text) {
            this.value = value;
            this.text = text;
        }
    }

    static Map<String, Descriptor> operations = new HashMap<String, Descriptor>();
    static {
        operations.put("<", new Descriptor(0x6C, "LESS_THAN"));
        operations.put("+<", new Descriptor(0x6D, "LESS_THAN (unsigned)"));
        operations.put("<=", new Descriptor(0x6E, "LESS_THAN_OR_EQUAL"));
        operations.put("+<=", new Descriptor(0x6F, "LESS_THAN_OR_EQUAL (unsigned)"));
        operations.put("==", new Descriptor(0x70, "EQUAL"));
        operations.put("<>", new Descriptor(0x71, "NOT_EQUAL"));
        operations.put(">=", new Descriptor(0x72, "GREATER_THAN_OR_EQUAL"));
        operations.put("+>=", new Descriptor(0x73, "GREATER_THAN_OR_EQUAL (unsigned)"));
        operations.put(">", new Descriptor(0x74, "GREATER_THAN"));
        operations.put("+>", new Descriptor(0x75, "GREATER_THAN (unsigned)"));
        operations.put("<=>", new Descriptor(0x76, ""));
        operations.put("!!", new Descriptor(0x77, "BOOLEAN_NOT"));
        operations.put("NOT", new Descriptor(0x77, "BOOLEAN_NOT"));
        operations.put("!", new Descriptor(0x78, "BITNOT"));
        //operations.put("-", new Descriptor(0x79, "NEGATE"));
        operations.put("ABS", new Descriptor(0x7A, "ABS"));
        operations.put("ENCOD", new Descriptor(0x7B, "ENCOD"));
        operations.put("DECOD", new Descriptor(0x7C, "DECOD"));
        operations.put("BMASK", new Descriptor(0x7D, "BMASK"));
        operations.put("ONES", new Descriptor(0x7E, "ONES"));
        operations.put("SQRT", new Descriptor(0x7F, "SQRT"));
        operations.put("QLOG", new Descriptor(0x80, "QLOG"));
        operations.put("QEXP", new Descriptor(0x81, "QEXP"));
        operations.put(">>", new Descriptor(0x82, "SHIFT_RIGHT"));
        operations.put("<<", new Descriptor(0x83, "SHIFT_LEFT"));
        operations.put("SAR", new Descriptor(0x84, "SAR"));
        operations.put("ROR", new Descriptor(0x85, "ROR"));
        operations.put("ROL", new Descriptor(0x86, "ROL"));
        operations.put("REV", new Descriptor(0x87, "REV"));
        operations.put("ZEROX", new Descriptor(0x88, "ZEROX"));
        operations.put("SIGNX", new Descriptor(0x89, "SIGNX"));
        operations.put("+", new Descriptor(0x8A, "ADD"));
        operations.put("-", new Descriptor(0x8B, "SUBTRACT"));
        operations.put("&&", new Descriptor(0x8C, "BOOLEAN_AND"));
        operations.put("AND", new Descriptor(0x8C, "BOOLEAN_AND"));
        operations.put("^^", new Descriptor(0x8D, "BOOLEAN_XOR"));
        operations.put("XOR", new Descriptor(0x8D, "BOOLEAN_XOR"));
        operations.put("||", new Descriptor(0x8E, "BOOLEAN_OR"));
        operations.put("OR", new Descriptor(0x8E, "BOOLEAN_OR"));
        operations.put("&", new Descriptor(0x8F, "BITAND"));
        operations.put("^", new Descriptor(0x90, "BITXOR"));
        operations.put("|", new Descriptor(0x91, "BITOR"));
        operations.put("#>", new Descriptor(0x92, "LIMIT_MIN"));
        operations.put("<#", new Descriptor(0x93, "LIMIT_MAX"));
        operations.put("ADDBITS", new Descriptor(0x94, "ADDBITS"));
        operations.put("ADDPINS", new Descriptor(0x95, "ADDPINS"));
        operations.put("*", new Descriptor(0x96, "MULTIPLY"));
        operations.put("/", new Descriptor(0x97, "DIVIDE"));
        operations.put("+/", new Descriptor(0x98, "DIVIDE (unsigned)"));
        operations.put("//", new Descriptor(0x99, "MODULO"));
        operations.put("+//", new Descriptor(0x9A, "MODULO (unsigned)"));
        operations.put("SCA", new Descriptor(0x9B, "SCA"));
        operations.put("SCAS", new Descriptor(0x9C, "SCAS"));
        operations.put("FRAC", new Descriptor(0x9D, "FRAC"));
    }

    public static boolean isMathOp(String s) {
        return operations.containsKey(s.toUpperCase());
    }

    static Map<String, Descriptor> assignOperations = new HashMap<String, Descriptor>();
    static {
        operations.put("!!=", new Descriptor(0x90, "BOOLEAN_NOT_ASSIGN"));
        operations.put("NOT=", new Descriptor(0x90, "BOOLEAN_NOT_ASSIGN"));
        operations.put("!=", new Descriptor(0x91, "BITNOT_ASSIGN"));
        //operations.put("-=", new Descriptor(0x92, "NEGATE_ASSIGN"));
        operations.put("ABS=", new Descriptor(0x93, "ABS_ASSIGN"));
        operations.put("ENCOD=", new Descriptor(0x94, "ENCOD_ASSIGN"));
        operations.put("DECOD=", new Descriptor(0x95, "DECOD_ASSIGN"));
        operations.put("BMASK=", new Descriptor(0x96, "BMASK_ASSIGN"));
        operations.put("ONES=", new Descriptor(0x97, "ONES_ASSIGN"));
        operations.put("SQRT=", new Descriptor(0x98, "SQRT_ASSIGN"));
        operations.put("QLOG=", new Descriptor(0x99, "QLOG_ASSIGN"));
        operations.put("QEXP=", new Descriptor(0x9A, "QEXP_ASSIGN"));
        operations.put(">>=", new Descriptor(0x9B, "SHIFT_RIGHT_ASSIGN"));
        operations.put("<<=", new Descriptor(0x9C, "SHIFT_LEFT_ASSIGN"));
        operations.put("SAR=", new Descriptor(0x9D, "SAR_ASSIGN"));
        operations.put("ROR=", new Descriptor(0x9E, "ROR_ASSIGN"));
        operations.put("ROL=", new Descriptor(0x9F, "ROL_ASSIGN"));
        operations.put("REV=", new Descriptor(0xA0, "REV_ASSIGN"));
        operations.put("ZEROX=", new Descriptor(0xA1, "ZEROX_ASSIGN"));
        operations.put("SIGNX=", new Descriptor(0xA2, "SIGNX_ASSIGN"));
        operations.put("+=", new Descriptor(0xA3, "ADD_ASSIGN"));
        operations.put("-=", new Descriptor(0xA4, "SUBTRACT_ASSIGN"));
        operations.put("&&=", new Descriptor(0xA5, "BOOLEAN_AND_ASSIGN"));
        operations.put("AND=", new Descriptor(0xA5, "BOOLEAN_AND_ASSIGN"));
        operations.put("^^=", new Descriptor(0xA6, "BOOLEAN_XOR_ASSIGN"));
        operations.put("XOR=", new Descriptor(0xA6, "BOOLEAN_XOR_ASSIGN"));
        operations.put("||=", new Descriptor(0xA7, "BOOLEAN_OR_ASSIGN"));
        operations.put("OR=", new Descriptor(0xA7, "BOOLEAN_OR_ASSIGN"));
        operations.put("&=", new Descriptor(0xA8, "BITAND_ASSIGN"));
        operations.put("^=", new Descriptor(0xA9, "BITXOR_ASSIGN"));
        operations.put("|=", new Descriptor(0xAA, "BITOR_ASSIGN"));
        operations.put("#>=", new Descriptor(0xAB, "LIMIT_MIN_ASSIGN"));
        operations.put("<#=", new Descriptor(0xAC, "LIMIT_MAX_ASSIGN"));
        operations.put("ADDBITS=", new Descriptor(0xAD, "ADDBITS_ASSIGN"));
        operations.put("ADDPINS=", new Descriptor(0xAE, "ADDPINS_ASSIGN"));
        operations.put("*=", new Descriptor(0xAF, "MULTIPLY_ASSIGN"));
        operations.put("/=", new Descriptor(0xB0, "DIVIDE_ASSIGN"));
        operations.put("+/=", new Descriptor(0xB1, "DIVIDE_ASSIGN (unsigned)"));
        operations.put("//=", new Descriptor(0xB2, "MODULO_ASSIGN"));
        operations.put("+//=", new Descriptor(0xB3, "MODULO_ASSIGN (unsigned)"));
        operations.put("SCA=", new Descriptor(0xB4, "SCA_ASSIGN"));
        operations.put("SCAS=", new Descriptor(0xB5, "SCAS_ASSIGN"));
        operations.put("FRAC=", new Descriptor(0xB6, "FRAC_ASSIGN"));
    }

    public static boolean isAssignMathOp(String s) {
        return assignOperations.containsKey(s.toUpperCase());
    }

    Descriptor op;

    public MathOp(Spin2Context context, String label, String op) {
        super(context, label);
        this.op = operations.get(op.toUpperCase());
        if (this.op == null) {
            this.op = assignOperations.get(op.toUpperCase());
        }
    }

    @Override
    public byte[] getBytes() {
        return new byte[] {
            (byte) op.value
        };
    }

    @Override
    public String toString() {
        return op.text;
    }

}
