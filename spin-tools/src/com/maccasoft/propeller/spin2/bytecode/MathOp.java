/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
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

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin2.Spin2Bytecode;

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
        operations.put("<", new Descriptor(bc_lt, "LESS_THAN"));
        operations.put("+<", new Descriptor(bc_ltu, "LESS_THAN (unsigned)"));
        operations.put("<=", new Descriptor(bc_lte, "LESS_THAN_OR_EQUAL"));
        operations.put("+<=", new Descriptor(bc_lteu, "LESS_THAN_OR_EQUAL (unsigned)"));
        operations.put("==", new Descriptor(bc_e, "EQUAL"));
        operations.put("<>", new Descriptor(bc_ne, "NOT_EQUAL"));
        operations.put(">=", new Descriptor(bc_gte, "GREATER_THAN_OR_EQUAL"));
        operations.put("+>=", new Descriptor(bc_gteu, "GREATER_THAN_OR_EQUAL (unsigned)"));
        operations.put(">", new Descriptor(bc_gt, "GREATER_THAN"));
        operations.put("+>", new Descriptor(bc_gtu, "GREATER_THAN (unsigned)"));
        operations.put("<=>", new Descriptor(bc_ltegt, "SIGNED_COMPARE"));
        operations.put(">>", new Descriptor(bc_shr, "SHIFT_RIGHT"));
        operations.put("<<", new Descriptor(bc_shl, "SHIFT_LEFT"));
        operations.put("SAR", new Descriptor(bc_sar, "SAR"));
        operations.put("ROR", new Descriptor(bc_ror, "ROR"));
        operations.put("ROL", new Descriptor(bc_rol, "ROL"));
        operations.put("REV", new Descriptor(bc_rev, "REV"));
        operations.put("ZEROX", new Descriptor(bc_zerox, "ZEROX"));
        operations.put("SIGNX", new Descriptor(bc_signx, "SIGNX"));
        operations.put("+", new Descriptor(bc_add, "ADD"));
        operations.put("-", new Descriptor(bc_sub, "SUBTRACT"));
        operations.put("&&", new Descriptor(bc_logand, "BOOLEAN_AND"));
        operations.put("AND", new Descriptor(bc_logand, "BOOLEAN_AND"));
        operations.put("^^", new Descriptor(bc_logxor, "BOOLEAN_XOR"));
        operations.put("XOR", new Descriptor(bc_logxor, "BOOLEAN_XOR"));
        operations.put("||", new Descriptor(bc_logor, "BOOLEAN_OR"));
        operations.put("OR", new Descriptor(bc_logor, "BOOLEAN_OR"));
        operations.put("&", new Descriptor(bc_bitand, "BITAND"));
        operations.put("^", new Descriptor(bc_bitxor, "BITXOR"));
        operations.put("|", new Descriptor(bc_bitor, "BITOR"));
        operations.put("#>", new Descriptor(bc_fge, "LIMIT_MIN"));
        operations.put("<#", new Descriptor(bc_fle, "LIMIT_MAX"));
        operations.put("ADDBITS", new Descriptor(bc_addbits, "ADDBITS"));
        operations.put("ADDPINS", new Descriptor(bc_addpins, "ADDPINS"));
        operations.put("*", new Descriptor(bc_mul, "MULTIPLY"));
        operations.put("/", new Descriptor(bc_div, "DIVIDE"));
        operations.put("+/", new Descriptor(bc_divu, "DIVIDE (unsigned)"));
        operations.put("//", new Descriptor(bc_rem, "MODULO"));
        operations.put("+//", new Descriptor(bc_remu, "MODULO (unsigned)"));
        operations.put("SCA", new Descriptor(bc_sca, "SCA"));
        operations.put("SCAS", new Descriptor(bc_scas, "SCAS"));
        operations.put("FRAC", new Descriptor(bc_frac, "FRAC"));
        operations.put("..", new Descriptor(new byte[] {
            (byte) bc_bitrange, (byte) bc_addpins
        }, "ADDPINS_RANGE"));

        //operations.put("-.", new Descriptor(new byte[] {
        //    bc_hub_bytecode, (byte) bc_fneg
        //}, "FLOAT_NEG"));

        operations.put("+.", new Descriptor(new byte[] {
            bc_hub_bytecode, (byte) bc_fadd
        }, "FLOAT_ADD"));
        operations.put("-.", new Descriptor(new byte[] {
            bc_hub_bytecode, (byte) bc_fsub
        }, "FLOAT_SUBTRACT"));
        operations.put("*.", new Descriptor(new byte[] {
            bc_hub_bytecode, (byte) bc_fmul
        }, "FLOAT_MULTIPLY"));
        operations.put("/.", new Descriptor(new byte[] {
            bc_hub_bytecode, (byte) bc_fdiv
        }, "FLOAT_DIVIDE"));
        operations.put("<.", new Descriptor(new byte[] {
            bc_hub_bytecode, (byte) bc_flt
        }, "FLOAT_LESS_THAN"));
        operations.put(">.", new Descriptor(new byte[] {
            bc_hub_bytecode, (byte) bc_fgt
        }, "FLOAT_GREATER_THAN"));
        operations.put("<>.", new Descriptor(new byte[] {
            bc_hub_bytecode, (byte) bc_fne
        }, "FLOAT_NOT_EQUAL"));
        operations.put("==.", new Descriptor(new byte[] {
            bc_hub_bytecode, (byte) bc_fe
        }, "FLOAT_EQUAL"));
        operations.put("<=.", new Descriptor(new byte[] {
            bc_hub_bytecode, (byte) bc_flte
        }, "FLOAT_LESS_THAN_OR_EQUAL"));
        operations.put(">=.", new Descriptor(new byte[] {
            bc_hub_bytecode, (byte) bc_fgte
        }, "FLOAT_GREATER_THAN_OR_EQUAL"));
        operations.put("POW", new Descriptor(new byte[] {
            bc_hub_bytecode, (byte) bc_pow
        }, "POW"));
    }

    public static boolean isMathOp(String s) {
        return operations.containsKey(s.toUpperCase());
    }

    static Map<String, Descriptor> unary = new HashMap<String, Descriptor>();
    static {
        unary.put("!!", new Descriptor(bc_lognot, "BOOLEAN_NOT"));
        unary.put("NOT", new Descriptor(bc_lognot, "BOOLEAN_NOT"));
        unary.put("!", new Descriptor(bc_bitnot, "BITNOT"));
        //unary.put("-", new Descriptor(bc_neg, "NEGATE"));
        unary.put("ABS", new Descriptor(bc_abs, "ABS"));
        unary.put("ENCOD", new Descriptor(bc_encod, "ENCOD"));
        unary.put("DECOD", new Descriptor(bc_decod, "DECOD"));
        unary.put("BMASK", new Descriptor(bc_bmask, "BMASK"));
        unary.put("ONES", new Descriptor(bc_ones, "ONES"));
        unary.put("SQRT", new Descriptor(bc_sqrt, "SQRT"));
        unary.put("QLOG", new Descriptor(bc_qlog, "QLOG"));
        unary.put("QEXP", new Descriptor(bc_qexp, "QEXP"));
        unary.put("LOG2", new Descriptor(new byte[] {
            bc_hub_bytecode, (byte) bc_log2
        }, "LOG2"));
        unary.put("LOG10", new Descriptor(new byte[] {
            bc_hub_bytecode, (byte) bc_log10
        }, "LOG10"));
        unary.put("LOG", new Descriptor(new byte[] {
            bc_hub_bytecode, (byte) bc_log
        }, "LOG"));
        unary.put("EXP2", new Descriptor(new byte[] {
            bc_hub_bytecode, (byte) bc_exp2
        }, "EXP2"));
        unary.put("EXP10", new Descriptor(new byte[] {
            bc_hub_bytecode, (byte) bc_exp10
        }, "EXP10"));
        unary.put("EXP", new Descriptor(new byte[] {
            bc_hub_bytecode, (byte) bc_exp
        }, "EXP"));
    }

    public static boolean isUnaryMathOp(String s) {
        return unary.containsKey(s.toUpperCase());
    }

    static Map<String, Descriptor> assignOperations = new HashMap<String, Descriptor>();
    static {
        assignOperations.put("!!=", new Descriptor(bc_lognot_write, bc_lognot_write_push, "BOOLEAN_NOT_ASSIGN"));
        assignOperations.put("NOT=", new Descriptor(bc_lognot_write, bc_lognot_write_push, "BOOLEAN_NOT_ASSIGN"));
        assignOperations.put("!=", new Descriptor(bc_bitnot_write, bc_bitnot_write_push, "BITNOT_ASSIGN"));
        //assignOperations.put("-=", new Descriptor(bc_neg_write, bc_neg_write_push, "NEGATE_ASSIGN"));
        assignOperations.put("ABS=", new Descriptor(bc_abs_write, bc_abs_write_push, "ABS_ASSIGN"));
        assignOperations.put("ENCOD=", new Descriptor(bc_encod_write, bc_encod_write_push, "ENCOD_ASSIGN"));
        assignOperations.put("DECOD=", new Descriptor(bc_decod_write, bc_decod_write_push, "DECOD_ASSIGN"));
        assignOperations.put("BMASK=", new Descriptor(bc_bmask_write, bc_bmask_write_push, "BMASK_ASSIGN"));
        assignOperations.put("ONES=", new Descriptor(bc_ones_write, bc_ones_write_push, "ONES_ASSIGN"));
        assignOperations.put("SQRT=", new Descriptor(bc_sqrt_write, bc_sqrt_write_push, "SQRT_ASSIGN"));
        assignOperations.put("QLOG=", new Descriptor(bc_qlog_write, bc_qlog_write_push, "QLOG_ASSIGN"));
        assignOperations.put("QEXP=", new Descriptor(bc_qexp_write, bc_qexp_write_push, "QEXP_ASSIGN"));
        assignOperations.put(">>=", new Descriptor(bc_shr_write, bc_shr_write_push, "SHIFT_RIGHT_ASSIGN"));
        assignOperations.put("<<=", new Descriptor(bc_shl_write, bc_shl_write_push, "SHIFT_LEFT_ASSIGN"));
        assignOperations.put("SAR=", new Descriptor(bc_sar_write, bc_sar_write_push, "SAR_ASSIGN"));
        assignOperations.put("ROR=", new Descriptor(bc_ror_write, bc_ror_write_push, "ROR_ASSIGN"));
        assignOperations.put("ROL=", new Descriptor(bc_rol_write, bc_rol_write_push, "ROL_ASSIGN"));
        assignOperations.put("REV=", new Descriptor(bc_rev_write, bc_rev_write_push, "REV_ASSIGN"));
        assignOperations.put("ZEROX=", new Descriptor(bc_zerox_write, bc_zerox_write_push, "ZEROX_ASSIGN"));
        assignOperations.put("SIGNX=", new Descriptor(bc_signx_write, bc_signx_write_push, "SIGNX_ASSIGN"));
        assignOperations.put("+=", new Descriptor(bc_add_write, bc_add_write_push, "ADD_ASSIGN"));
        assignOperations.put("-=", new Descriptor(bc_sub_write, bc_sub_write_push, "SUBTRACT_ASSIGN"));
        assignOperations.put("&&=", new Descriptor(bc_logand_write, bc_logand_write_push, "BOOLEAN_AND_ASSIGN"));
        assignOperations.put("AND=", new Descriptor(bc_logand_write, bc_logand_write_push, "BOOLEAN_AND_ASSIGN"));
        assignOperations.put("^^=", new Descriptor(bc_logxor_write, bc_logxor_write_push, "BOOLEAN_XOR_ASSIGN"));
        assignOperations.put("XOR=", new Descriptor(bc_logxor_write, bc_logxor_write_push, "BOOLEAN_XOR_ASSIGN"));
        assignOperations.put("||=", new Descriptor(bc_logor_write, bc_logor_write_push, "BOOLEAN_OR_ASSIGN"));
        assignOperations.put("OR=", new Descriptor(bc_logor_write, bc_logor_write_push, "BOOLEAN_OR_ASSIGN"));
        assignOperations.put("&=", new Descriptor(bc_bitand_write, bc_bitand_write_push, "BITAND_ASSIGN"));
        assignOperations.put("^=", new Descriptor(bc_bitxor_write, bc_bitxor_write_push, "BITXOR_ASSIGN"));
        assignOperations.put("|=", new Descriptor(bc_bitor_write, bc_bitor_write_push, "BITOR_ASSIGN"));
        assignOperations.put("#>=", new Descriptor(bc_fge_write, bc_fge_write_push, "LIMIT_MIN_ASSIGN"));
        assignOperations.put("<#=", new Descriptor(bc_fle_write, bc_fle_write_push, "LIMIT_MAX_ASSIGN"));
        assignOperations.put("ADDBITS=", new Descriptor(bc_addbits_write, bc_addbits_write_push, "ADDBITS_ASSIGN"));
        assignOperations.put("ADDPINS=", new Descriptor(bc_addpins_write, bc_addpins_write_push, "ADDPINS_ASSIGN"));
        assignOperations.put("*=", new Descriptor(bc_mul_write, bc_mul_write_push, "MULTIPLY_ASSIGN"));
        assignOperations.put("/=", new Descriptor(bc_div_write, bc_div_write_push, "DIVIDE_ASSIGN"));
        assignOperations.put("+/=", new Descriptor(bc_divu_write, bc_divu_write_push, "DIVIDE_ASSIGN (unsigned)"));
        assignOperations.put("//=", new Descriptor(bc_rem_write, bc_rem_write_push, "MODULO_ASSIGN"));
        assignOperations.put("+//=", new Descriptor(bc_remu_write, bc_remu_write_push, "MODULO_ASSIGN (unsigned)"));
        assignOperations.put("SCA=", new Descriptor(bc_sca_write, bc_sca_write_push, "SCA_ASSIGN"));
        assignOperations.put("SCAS=", new Descriptor(bc_scas_write, bc_scas_write_push, "SCAS_ASSIGN"));
        assignOperations.put("FRAC=", new Descriptor(bc_frac_write, bc_frac_write_push, "FRAC_ASSIGN"));
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
        return push ? op.push_value.length : op.value.length;
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
