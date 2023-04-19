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
import com.maccasoft.propeller.expressions.Context;

public class MathOp extends Spin2Bytecode {

    static class Descriptor {
        byte[] value;
        byte[] push_value;
        String text;

        public Descriptor(int value, String text) {
            this.value = new byte[] {
                (byte) value
            };
            this.push_value = new byte[] {
                (byte) value
            };
            this.text = text;
        }

        public Descriptor(byte[] value, String text) {
            this.value = value;
            this.push_value = value;
            this.text = text;
        }

        public Descriptor(int value, int push_value, String text) {
            this.value = new byte[] {
                (byte) value
            };
            this.push_value = new byte[] {
                (byte) push_value
            };
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

        //operations.put("-.", new Descriptor(new byte[] {
        //    0x19, (byte) 0x94
        //}, "FLOAT_NEG"));

        operations.put("+.", new Descriptor(new byte[] {
            0x19, (byte) 0x9A
        }, "FLOAT_ADD"));
        operations.put("-.", new Descriptor(new byte[] {
            0x19, (byte) 0x9C
        }, "FLOAT_SUBTRACT"));
        operations.put("*.", new Descriptor(new byte[] {
            0x19, (byte) 0x9E
        }, "FLOAT_MULTIPLY"));
        operations.put("/.", new Descriptor(new byte[] {
            0x19, (byte) 0xA0
        }, "FLOAT_DIVIDE"));
        operations.put("<.", new Descriptor(new byte[] {
            0x19, (byte) 0xA2
        }, "FLOAT_LESS_THAN"));
        operations.put(">.", new Descriptor(new byte[] {
            0x19, (byte) 0xA4
        }, "FLOAT_GREATER_THAN"));
        operations.put("<>.", new Descriptor(new byte[] {
            0x19, (byte) 0xA6
        }, "FLOAT_NOT_EQUAL"));
        operations.put("==.", new Descriptor(new byte[] {
            0x19, (byte) 0xA8
        }, "FLOAT_EQUAL"));
        operations.put("<=.", new Descriptor(new byte[] {
            0x19, (byte) 0xAA
        }, "FLOAT_LESS_THAN_OR_EQUAL"));
        operations.put(">=.", new Descriptor(new byte[] {
            0x19, (byte) 0xAC
        }, "FLOAT_GREATER_THAN_OR_EQUAL"));

    }

    public static boolean isMathOp(String s) {
        return operations.containsKey(s.toUpperCase());
    }

    static Map<String, Descriptor> unary = new HashMap<String, Descriptor>();
    static {
        unary.put("!!", new Descriptor(0x77, "BOOLEAN_NOT"));
        unary.put("NOT", new Descriptor(0x77, "BOOLEAN_NOT"));
        unary.put("!", new Descriptor(0x78, "BITNOT"));
        //unary.put("-", new Descriptor(0x79, "NEGATE"));
        unary.put("ABS", new Descriptor(0x7A, "ABS"));
        unary.put("ENCOD", new Descriptor(0x7B, "ENCOD"));
        unary.put("DECOD", new Descriptor(0x7C, "DECOD"));
        unary.put("BMASK", new Descriptor(0x7D, "BMASK"));
        unary.put("ONES", new Descriptor(0x7E, "ONES"));
        unary.put("SQRT", new Descriptor(0x7F, "SQRT"));
        unary.put("QLOG", new Descriptor(0x80, "QLOG"));
        unary.put("QEXP", new Descriptor(0x81, "QEXP"));
    }

    public static boolean isUnaryMathOp(String s) {
        return unary.containsKey(s.toUpperCase());
    }

    static Map<String, Descriptor> assignOperations = new HashMap<String, Descriptor>();
    static {
        assignOperations.put("!!=", new Descriptor(0x90, 0xB7, "BOOLEAN_NOT_ASSIGN"));
        assignOperations.put("NOT=", new Descriptor(0x90, 0xB7, "BOOLEAN_NOT_ASSIGN"));
        assignOperations.put("!=", new Descriptor(0x91, 0xB8, "BITNOT_ASSIGN"));
        //assignOperations.put("-=", new Descriptor(0x92, 0xB9, "NEGATE_ASSIGN"));
        assignOperations.put("ABS=", new Descriptor(0x93, 0xBA, "ABS_ASSIGN"));
        assignOperations.put("ENCOD=", new Descriptor(0x94, 0xBB, "ENCOD_ASSIGN"));
        assignOperations.put("DECOD=", new Descriptor(0x95, 0xBC, "DECOD_ASSIGN"));
        assignOperations.put("BMASK=", new Descriptor(0x96, 0xBD, "BMASK_ASSIGN"));
        assignOperations.put("ONES=", new Descriptor(0x97, 0xBE, "ONES_ASSIGN"));
        assignOperations.put("SQRT=", new Descriptor(0x98, 0xBF, "SQRT_ASSIGN"));
        assignOperations.put("QLOG=", new Descriptor(0x99, 0xC0, "QLOG_ASSIGN"));
        assignOperations.put("QEXP=", new Descriptor(0x9A, 0xC1, "QEXP_ASSIGN"));
        assignOperations.put(">>=", new Descriptor(0x9B, 0xC2, "SHIFT_RIGHT_ASSIGN"));
        assignOperations.put("<<=", new Descriptor(0x9C, 0xC3, "SHIFT_LEFT_ASSIGN"));
        assignOperations.put("SAR=", new Descriptor(0x9D, 0xC4, "SAR_ASSIGN"));
        assignOperations.put("ROR=", new Descriptor(0x9E, 0xC5, "ROR_ASSIGN"));
        assignOperations.put("ROL=", new Descriptor(0x9F, 0xC6, "ROL_ASSIGN"));
        assignOperations.put("REV=", new Descriptor(0xA0, 0xC7, "REV_ASSIGN"));
        assignOperations.put("ZEROX=", new Descriptor(0xA1, 0xC8, "ZEROX_ASSIGN"));
        assignOperations.put("SIGNX=", new Descriptor(0xA2, 0xC9, "SIGNX_ASSIGN"));
        assignOperations.put("+=", new Descriptor(0xA3, 0xCA, "ADD_ASSIGN"));
        assignOperations.put("-=", new Descriptor(0xA4, 0xCB, "SUBTRACT_ASSIGN"));
        assignOperations.put("&&=", new Descriptor(0xA5, 0xCC, "BOOLEAN_AND_ASSIGN"));
        assignOperations.put("AND=", new Descriptor(0xA5, 0xCC, "BOOLEAN_AND_ASSIGN"));
        assignOperations.put("^^=", new Descriptor(0xA6, 0xCD, "BOOLEAN_XOR_ASSIGN"));
        assignOperations.put("XOR=", new Descriptor(0xA6, 0xCD, "BOOLEAN_XOR_ASSIGN"));
        assignOperations.put("||=", new Descriptor(0xA7, 0xCE, "BOOLEAN_OR_ASSIGN"));
        assignOperations.put("OR=", new Descriptor(0xA7, 0xCE, "BOOLEAN_OR_ASSIGN"));
        assignOperations.put("&=", new Descriptor(0xA8, 0xCF, "BITAND_ASSIGN"));
        assignOperations.put("^=", new Descriptor(0xA9, 0xD0, "BITXOR_ASSIGN"));
        assignOperations.put("|=", new Descriptor(0xAA, 0xD1, "BITOR_ASSIGN"));
        assignOperations.put("#>=", new Descriptor(0xAB, 0xD2, "LIMIT_MIN_ASSIGN"));
        assignOperations.put("<#=", new Descriptor(0xAC, 0xD3, "LIMIT_MAX_ASSIGN"));
        assignOperations.put("ADDBITS=", new Descriptor(0xAD, 0xD4, "ADDBITS_ASSIGN"));
        assignOperations.put("ADDPINS=", new Descriptor(0xAE, 0xD5, "ADDPINS_ASSIGN"));
        assignOperations.put("*=", new Descriptor(0xAF, 0xD6, "MULTIPLY_ASSIGN"));
        assignOperations.put("/=", new Descriptor(0xB0, 0xD7, "DIVIDE_ASSIGN"));
        assignOperations.put("+/=", new Descriptor(0xB1, 0xD8, "DIVIDE_ASSIGN (unsigned)"));
        assignOperations.put("//=", new Descriptor(0xB2, 0xD9, "MODULO_ASSIGN"));
        assignOperations.put("+//=", new Descriptor(0xB3, 0xDA, "MODULO_ASSIGN (unsigned)"));
        assignOperations.put("SCA=", new Descriptor(0xB4, 0xDB, "SCA_ASSIGN"));
        assignOperations.put("SCAS=", new Descriptor(0xB5, 0xDC, "SCAS_ASSIGN"));
        assignOperations.put("FRAC=", new Descriptor(0xB6, 0xDD, "FRAC_ASSIGN"));
    }

    public static boolean isAssignMathOp(String s) {
        return assignOperations.containsKey(s.toUpperCase());
    }

    Descriptor op;
    boolean push;

    public MathOp(Context context, String op, boolean push) {
        super(context);
        this.op = operations.get(op.toUpperCase());
        if (this.op == null) {
            this.op = unary.get(op.toUpperCase());
        }
        if (this.op == null) {
            this.op = assignOperations.get(op.toUpperCase());
        }
        this.push = push;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public byte[] getBytes() {
        return push ? op.push_value : op.value;
    }

    @Override
    public String toString() {
        return op.text + (push ? " (push)" : "");
    }

}
