/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.ObjectCompiler;
import com.maccasoft.propeller.expressions.Abs;
import com.maccasoft.propeller.expressions.Add;
import com.maccasoft.propeller.expressions.Addbits;
import com.maccasoft.propeller.expressions.Addpins;
import com.maccasoft.propeller.expressions.And;
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.DataVariable;
import com.maccasoft.propeller.expressions.Divide;
import com.maccasoft.propeller.expressions.Equals;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Frac;
import com.maccasoft.propeller.expressions.GreaterOrEquals;
import com.maccasoft.propeller.expressions.GreaterThan;
import com.maccasoft.propeller.expressions.IfElse;
import com.maccasoft.propeller.expressions.LessOrEquals;
import com.maccasoft.propeller.expressions.LessThan;
import com.maccasoft.propeller.expressions.LimitMax;
import com.maccasoft.propeller.expressions.LimitMin;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.LogicalAnd;
import com.maccasoft.propeller.expressions.LogicalNot;
import com.maccasoft.propeller.expressions.LogicalOr;
import com.maccasoft.propeller.expressions.MemoryContextLiteral;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.expressions.Modulo;
import com.maccasoft.propeller.expressions.Multiply;
import com.maccasoft.propeller.expressions.Nan;
import com.maccasoft.propeller.expressions.Negative;
import com.maccasoft.propeller.expressions.Not;
import com.maccasoft.propeller.expressions.NotEquals;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.ObjectContextLiteral;
import com.maccasoft.propeller.expressions.Or;
import com.maccasoft.propeller.expressions.Register;
import com.maccasoft.propeller.expressions.Rev;
import com.maccasoft.propeller.expressions.Rol;
import com.maccasoft.propeller.expressions.Ror;
import com.maccasoft.propeller.expressions.Round;
import com.maccasoft.propeller.expressions.Sca;
import com.maccasoft.propeller.expressions.Scas;
import com.maccasoft.propeller.expressions.ShiftLeft;
import com.maccasoft.propeller.expressions.ShiftRight;
import com.maccasoft.propeller.expressions.SpinObject;
import com.maccasoft.propeller.expressions.Sqrt;
import com.maccasoft.propeller.expressions.Subtract;
import com.maccasoft.propeller.expressions.Trunc;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.expressions.Xor;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin2.Spin2Bytecode;
import com.maccasoft.propeller.spin2.Spin2Compiler;
import com.maccasoft.propeller.spin2.Spin2Method;
import com.maccasoft.propeller.spin2.Spin2Model;
import com.maccasoft.propeller.spin2.Spin2PasmCompiler;
import com.maccasoft.propeller.spin2.Spin2StatementNode;
import com.maccasoft.propeller.spin2.Spin2Struct;
import com.maccasoft.propeller.spin2.Spin2Struct.Spin2StructMember;
import com.maccasoft.propeller.spin2.bytecode.Address;
import com.maccasoft.propeller.spin2.bytecode.BitField;
import com.maccasoft.propeller.spin2.bytecode.Bytecode;
import com.maccasoft.propeller.spin2.bytecode.CallSub;
import com.maccasoft.propeller.spin2.bytecode.Constant;
import com.maccasoft.propeller.spin2.bytecode.MemoryOp;
import com.maccasoft.propeller.spin2.bytecode.RegisterOp;
import com.maccasoft.propeller.spin2.bytecode.StructOp;
import com.maccasoft.propeller.spin2.bytecode.SubAddress;
import com.maccasoft.propeller.spin2.bytecode.VariableOp;

public abstract class Spin2CBytecodeCompiler extends Spin2PasmCompiler {

    public static class FunctionDescriptor {
        public byte[] code;
        public int parameters;
        public int returns;

        public FunctionDescriptor(int b0, int b1, int parameters, int returns) {
            if (b1 == -1) {
                this.code = new byte[] {
                    (byte) b0
                };
            }
            else {
                this.code = new byte[] {
                    (byte) b0,
                    (byte) b1
                };
            }
            this.parameters = parameters;
            this.returns = returns;
        }

        public int getParameters() {
            return parameters;
        }

        public int getReturns() {
            return returns;
        }

    }

    static Map<String, FunctionDescriptor> descriptors = new HashMap<String, FunctionDescriptor>();
    static {
        descriptors.put("hubset", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_hubset, 1, 0));
        descriptors.put("clkset", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_clkset, 2, 0));
        //descriptors.put("clkfreq", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_read_clkfreq, 0, 1));
        ////descriptors.put("cogspin", new Descriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_cogspin, 3));
        descriptors.put("cogchk", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_cogchk, 1, 1));
        descriptors.put("regexec", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_regexec, 1, 0));
        descriptors.put("regload", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_regload, 1, 0));
        descriptors.put("call", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_call, 1, 0));
        descriptors.put("getregs", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_getregs, 3, 0));
        descriptors.put("setregs", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_setregs, 3, 0));
        descriptors.put("bytemove", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_bytemove, 3, 0));
        descriptors.put("bytefill", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_bytefill, 3, 0));
        descriptors.put("wordmove", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_wordmove, 3, 0));
        descriptors.put("wordfill", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_wordfill, 3, 0));
        descriptors.put("longmove", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_longmove, 3, 0));
        descriptors.put("longfill", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_longfill, 3, 0));
        descriptors.put("strsize", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_strsize, 1, 1));
        descriptors.put("strcomp", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_strcomp, 2, 1));
        descriptors.put("strcopy", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_strcopy, 3, 0));
        descriptors.put("getcrc", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_getcrc, 3, 1));
        descriptors.put("waitus", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_waitus, 1, 0));
        descriptors.put("waitms", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_waitms, 1, 0));
        descriptors.put("getms", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_getms, 0, 1));
        descriptors.put("getsec", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_getsec, 0, 1));
        descriptors.put("muldiv64", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_muldiv64, 3, 1));
        descriptors.put("qsin", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_qsin, 3, 1));
        descriptors.put("qcos", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_qcos, 3, 1));
        //descriptors.put("rotxy", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_rotxy, 3, 2));
        //descriptors.put("polxy", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_polxy, 2, 2));
        //descriptors.put("xypol", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_xypol, 2, 2));

        descriptors.put("cogstop", new FunctionDescriptor(Spin2Bytecode.bc_cogstop, -1, 1, 0));
        descriptors.put("cogid", new FunctionDescriptor(Spin2Bytecode.bc_cogid, -1, 0, 1));

        descriptors.put("locknew", new FunctionDescriptor(Spin2Bytecode.bc_locknew, -1, 0, 1));
        descriptors.put("lockret", new FunctionDescriptor(Spin2Bytecode.bc_lockret, -1, 1, 0));
        descriptors.put("locktry", new FunctionDescriptor(Spin2Bytecode.bc_locktry, -1, 1, 1));
        descriptors.put("lockrel", new FunctionDescriptor(Spin2Bytecode.bc_lockrel, -1, 1, 0));
        descriptors.put("lockchk", new FunctionDescriptor(Spin2Bytecode.bc_lockchk, -1, 1, 1));

        descriptors.put("cogatn", new FunctionDescriptor(Spin2Bytecode.bc_cogatn, -1, 1, 0));
        descriptors.put("pollatn", new FunctionDescriptor(Spin2Bytecode.bc_pollatn, -1, 0, 1));
        descriptors.put("waitatn", new FunctionDescriptor(Spin2Bytecode.bc_waitatn, -1, 0, 0));

        descriptors.put("getrnd", new FunctionDescriptor(Spin2Bytecode.bc_getrnd, -1, 0, 1));
        descriptors.put("getct", new FunctionDescriptor(Spin2Bytecode.bc_getct, -1, 0, 1));
        descriptors.put("pollct", new FunctionDescriptor(Spin2Bytecode.bc_pollct, -1, 1, 1));
        descriptors.put("waitct", new FunctionDescriptor(Spin2Bytecode.bc_waitct, -1, 1, 0));

        descriptors.put("pinw", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_pinwrite, 2, 0));
        descriptors.put("pinwrite", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_pinwrite, 2, 0));
        descriptors.put("pinl", new FunctionDescriptor(Spin2Bytecode.bc_pinlow, -1, 1, 0));
        descriptors.put("pinlow", new FunctionDescriptor(Spin2Bytecode.bc_pinlow, -1, 1, 0));
        descriptors.put("pinh", new FunctionDescriptor(Spin2Bytecode.bc_pinhigh, -1, 1, 0));
        descriptors.put("pinhigh", new FunctionDescriptor(Spin2Bytecode.bc_pinhigh, -1, 1, 0));
        descriptors.put("pint", new FunctionDescriptor(Spin2Bytecode.bc_pintoggle, -1, 1, 0));
        descriptors.put("pintoggle", new FunctionDescriptor(Spin2Bytecode.bc_pintoggle, -1, 1, 0));
        descriptors.put("pinf", new FunctionDescriptor(Spin2Bytecode.bc_pinfloat, -1, 1, 0));
        descriptors.put("pinfloat", new FunctionDescriptor(Spin2Bytecode.bc_pinfloat, -1, 1, 0));
        descriptors.put("pinr", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_pinread, 1, 1));
        descriptors.put("pinread", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_pinread, 1, 1));

        descriptors.put("pinstart", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_pinstart, 4, 0));
        descriptors.put("pinclear", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_pinclear, 1, 0));

        descriptors.put("wrpin", new FunctionDescriptor(Spin2Bytecode.bc_wrpin, -1, 2, 0));
        descriptors.put("wxpin", new FunctionDescriptor(Spin2Bytecode.bc_wxpin, -1, 2, 0));
        descriptors.put("wypin", new FunctionDescriptor(Spin2Bytecode.bc_wypin, -1, 2, 0));
        descriptors.put("akpin", new FunctionDescriptor(Spin2Bytecode.bc_akpin, -1, 1, 0));
        descriptors.put("rdpin", new FunctionDescriptor(Spin2Bytecode.bc_rdpin, -1, 1, 1));
        descriptors.put("rqpin", new FunctionDescriptor(Spin2Bytecode.bc_rqpin, -1, 1, 1));

        descriptors.put("encod", new FunctionDescriptor(Spin2Bytecode.bc_encod, -1, 1, 1));
        descriptors.put("decod", new FunctionDescriptor(Spin2Bytecode.bc_decod, -1, 1, 1));
        descriptors.put("bmask", new FunctionDescriptor(Spin2Bytecode.bc_bmask, -1, 1, 1));
        descriptors.put("ones", new FunctionDescriptor(Spin2Bytecode.bc_ones, -1, 1, 1));
        descriptors.put("sqrt", new FunctionDescriptor(Spin2Bytecode.bc_sqrt, -1, 1, 1));
        descriptors.put("qlog", new FunctionDescriptor(Spin2Bytecode.bc_qlog, -1, 1, 1));
        descriptors.put("qexp", new FunctionDescriptor(Spin2Bytecode.bc_qexp, -1, 1, 1));

        descriptors.put("sar", new FunctionDescriptor(Spin2Bytecode.bc_sar, -1, 2, 1));
        descriptors.put("ror", new FunctionDescriptor(Spin2Bytecode.bc_ror, -1, 2, 1));
        descriptors.put("rol", new FunctionDescriptor(Spin2Bytecode.bc_rol, -1, 2, 1));
        descriptors.put("rev", new FunctionDescriptor(Spin2Bytecode.bc_rev, -1, 2, 1));
        descriptors.put("zerox", new FunctionDescriptor(Spin2Bytecode.bc_zerox, -1, 2, 1));
        descriptors.put("signx", new FunctionDescriptor(Spin2Bytecode.bc_signx, -1, 2, 1));
        descriptors.put("max", new FunctionDescriptor(Spin2Bytecode.bc_fge, -1, 2, 1));
        descriptors.put("min", new FunctionDescriptor(Spin2Bytecode.bc_fle, -1, 2, 1));
        descriptors.put("addbits", new FunctionDescriptor(Spin2Bytecode.bc_addbits, -1, 2, 1));
        descriptors.put("addpins", new FunctionDescriptor(Spin2Bytecode.bc_addpins, -1, 2, 1));
        descriptors.put("sca", new FunctionDescriptor(Spin2Bytecode.bc_sca, -1, 2, 1));
        descriptors.put("scas", new FunctionDescriptor(Spin2Bytecode.bc_scas, -1, 2, 1));
        descriptors.put("frac", new FunctionDescriptor(Spin2Bytecode.bc_frac, -1, 2, 1));

        descriptors.put("strlen", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_strsize, 1, 1));
        descriptors.put("strcmp", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_strcomp, 2, 1));
        descriptors.put("strcpy", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_strcopy, 3, 0));
        descriptors.put("memmov", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_bytemove, 3, 0));
        descriptors.put("memset", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_bytefill, 3, 0));

        descriptors.put("tasknext", new FunctionDescriptor(Spin2Bytecode.bc_tasknext, -1, 0, 0));
        descriptors.put("taskstop", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_taskstop, 1, 0));
        descriptors.put("taskhalt", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_taskhalt, 1, 0));
        descriptors.put("taskcont", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_taskcont, 1, 0));
        descriptors.put("taskchk", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_taskchk, 1, 1));
        descriptors.put("taskid", new FunctionDescriptor(Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_taskid, 0, 1));
    }

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

    static Map<String, Descriptor> operators = new HashMap<String, Descriptor>();
    static {
        operators.put("<", new Descriptor(Spin2Bytecode.bc_lt, "LESS_THAN"));
        //operators.put("+<", new Descriptor(Spin2Bytecode.bc_ltu, "LESS_THAN (unsigned)"));
        operators.put("<=", new Descriptor(Spin2Bytecode.bc_lte, "LESS_THAN_OR_EQUAL"));
        //operators.put("+<=", new Descriptor(Spin2Bytecode.bc_lteu, "LESS_THAN_OR_EQUAL (unsigned)"));
        operators.put("==", new Descriptor(Spin2Bytecode.bc_e, "EQUAL"));
        operators.put("!=", new Descriptor(Spin2Bytecode.bc_ne, "NOT_EQUAL"));
        operators.put(">=", new Descriptor(Spin2Bytecode.bc_gte, "GREATER_THAN_OR_EQUAL"));
        //operators.put("+>=", new Descriptor(Spin2Bytecode.bc_gteu, "GREATER_THAN_OR_EQUAL (unsigned)"));
        operators.put(">", new Descriptor(Spin2Bytecode.bc_gt, "GREATER_THAN"));
        //operators.put("+>", new Descriptor(Spin2Bytecode.bc_gtu, "GREATER_THAN (unsigned)"));
        //operators.put("<=>", new Descriptor(Spin2Bytecode.bc_ltegt, "SIGNED_COMPARE"));
        //operators.put(">>", new Descriptor(Spin2Bytecode.bc_shr, "SHIFT_RIGHT"));
        operators.put("<<", new Descriptor(Spin2Bytecode.bc_shl, "SHIFT_LEFT"));
        operators.put(">>", new Descriptor(Spin2Bytecode.bc_sar, "SAR"));
        //operators.put("ROR", new Descriptor(Spin2Bytecode.bc_ror, "ROR"));
        //operators.put("ROL", new Descriptor(Spin2Bytecode.bc_rol, "ROL"));
        //operators.put("REV", new Descriptor(Spin2Bytecode.bc_rev, "REV"));
        //operators.put("ZEROX", new Descriptor(Spin2Bytecode.bc_zerox, "ZEROX"));
        //operators.put("SIGNX", new Descriptor(Spin2Bytecode.bc_signx, "SIGNX"));
        operators.put("+", new Descriptor(Spin2Bytecode.bc_add, "ADD"));
        operators.put("-", new Descriptor(Spin2Bytecode.bc_sub, "SUBTRACT"));
        operators.put("&&", new Descriptor(Spin2Bytecode.bc_logand, "BOOLEAN_AND"));
        //operators.put("AND", new Descriptor(Spin2Bytecode.bc_logand, "BOOLEAN_AND"));
        //operators.put("^^", new Descriptor(Spin2Bytecode.bc_logxor, "BOOLEAN_XOR"));
        //operators.put("XOR", new Descriptor(Spin2Bytecode.bc_logxor, "BOOLEAN_XOR"));
        operators.put("||", new Descriptor(Spin2Bytecode.bc_logor, "BOOLEAN_OR"));
        //operators.put("OR", new Descriptor(Spin2Bytecode.bc_logor, "BOOLEAN_OR"));
        operators.put("&", new Descriptor(Spin2Bytecode.bc_bitand, "BITAND"));
        operators.put("^", new Descriptor(Spin2Bytecode.bc_bitxor, "BITXOR"));
        operators.put("|", new Descriptor(Spin2Bytecode.bc_bitor, "BITOR"));
        //operators.put("#>", new Descriptor(Spin2Bytecode.bc_fge, "LIMIT_MIN"));
        //operators.put("<#", new Descriptor(Spin2Bytecode.bc_fle, "LIMIT_MAX"));
        //operators.put("ADDBITS", new Descriptor(Spin2Bytecode.bc_addbits, "ADDBITS"));
        //operators.put("ADDPINS", new Descriptor(Spin2Bytecode.bc_addpins, "ADDPINS"));
        operators.put("*", new Descriptor(Spin2Bytecode.bc_mul, "MULTIPLY"));
        operators.put("/", new Descriptor(Spin2Bytecode.bc_div, "DIVIDE"));
        //operators.put("+/", new Descriptor(Spin2Bytecode.bc_divu, "DIVIDE (unsigned)"));
        operators.put("%", new Descriptor(Spin2Bytecode.bc_rem, "MODULO"));
        //operators.put("+//", new Descriptor(Spin2Bytecode.bc_remu, "MODULO (unsigned)"));
        //operators.put("SCA", new Descriptor(Spin2Bytecode.bc_sca, "SCA"));
        //operators.put("SCAS", new Descriptor(Spin2Bytecode.bc_scas, "SCAS"));
        //operators.put("FRAC", new Descriptor(Spin2Bytecode.bc_frac, "FRAC"));
    }
    ;

    static Map<String, Descriptor> floatOperators = new HashMap<String, Descriptor>();
    static {
        //floatOperators.put("-.", new Descriptor(new byte[] {
        //    Spin2Bytecode.bc_hub_bytecode, (byte) 0x94
        //}, "FLOAT_NEG"));

        floatOperators.put("+", new Descriptor(new byte[] {
            Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_fadd
        }, "FLOAT_ADD"));
        floatOperators.put("-", new Descriptor(new byte[] {
            Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_fsub
        }, "FLOAT_SUBTRACT"));
        floatOperators.put("*", new Descriptor(new byte[] {
            Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_fmul
        }, "FLOAT_MULTIPLY"));
        floatOperators.put("/", new Descriptor(new byte[] {
            Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_fdiv
        }, "FLOAT_DIVIDE"));
        floatOperators.put("<", new Descriptor(new byte[] {
            Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_flt
        }, "FLOAT_LESS_THAN"));
        floatOperators.put(">", new Descriptor(new byte[] {
            Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_fgt
        }, "FLOAT_GREATER_THAN"));
        floatOperators.put("!=", new Descriptor(new byte[] {
            Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_fne
        }, "FLOAT_NOT_EQUAL"));
        floatOperators.put("==", new Descriptor(new byte[] {
            Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_fe
        }, "FLOAT_EQUAL"));
        floatOperators.put("<=", new Descriptor(new byte[] {
            Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_flte
        }, "FLOAT_LESS_THAN_OR_EQUAL"));
        floatOperators.put(">=", new Descriptor(new byte[] {
            Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_fgte
        }, "FLOAT_GREATER_THAN_OR_EQUAL"));

    }

    static Map<String, Descriptor> unaryOperators = new HashMap<String, Descriptor>();
    static {
        //unaryOperators.put("!!", new Descriptor(bc_lognot, "BOOLEAN_NOT"));
        unaryOperators.put("!", new Descriptor(Spin2Bytecode.bc_lognot, "BOOLEAN_NOT"));
        //unaryOperators.put("!", new Descriptor(bc_bitnot, "BITNOT"));
        ////unary.put("-", new Descriptor(bc_neg, "NEGATE"));
        //unaryOperators.put("ABS", new Descriptor(bc_abs, "ABS"));
        //unaryOperators.put("ENCOD", new Descriptor(bc_encod, "ENCOD"));
        //unaryOperators.put("DECOD", new Descriptor(bc_decod, "DECOD"));
        //unaryOperators.put("BMASK", new Descriptor(bc_bmask, "BMASK"));
        //unaryOperators.put("ONES", new Descriptor(bc_ones, "ONES"));
        //unaryOperators.put("SQRT", new Descriptor(bc_sqrt, "SQRT"));
        //unaryOperators.put("QLOG", new Descriptor(bc_qlog, "QLOG"));
        //unaryOperators.put("QEXP", new Descriptor(bc_qexp, "QEXP"));
    }

    static Map<String, Descriptor> assignOperators = new HashMap<String, Descriptor>();
    static {
        //assignOperators.put("!!=", new Descriptor(0x90, 0xB7, "BOOLEAN_NOT_ASSIGN"));
        //assignOperators.put("NOT=", new Descriptor(0x90, 0xB7, "BOOLEAN_NOT_ASSIGN"));
        //assignOperators.put("!=", new Descriptor(0x91, 0xB8, "BITNOT_ASSIGN"));
        ////assignOperations.put("-=", new Descriptor(0x92, 0xB9, "NEGATE_ASSIGN"));
        //assignOperators.put("ABS=", new Descriptor(0x93, 0xBA, "ABS_ASSIGN"));
        //assignOperators.put("ENCOD=", new Descriptor(0x94, 0xBB, "ENCOD_ASSIGN"));
        //assignOperators.put("DECOD=", new Descriptor(0x95, 0xBC, "DECOD_ASSIGN"));
        //assignOperators.put("BMASK=", new Descriptor(0x96, 0xBD, "BMASK_ASSIGN"));
        //assignOperators.put("ONES=", new Descriptor(0x97, 0xBE, "ONES_ASSIGN"));
        //assignOperators.put("SQRT=", new Descriptor(0x98, 0xBF, "SQRT_ASSIGN"));
        //assignOperators.put("QLOG=", new Descriptor(0x99, 0xC0, "QLOG_ASSIGN"));
        //assignOperators.put("QEXP=", new Descriptor(0x9A, 0xC1, "QEXP_ASSIGN"));
        //assignOperators.put(">>=", new Descriptor(0x9B, 0xC2, "SHIFT_RIGHT_ASSIGN"));
        assignOperators.put("<<=", new Descriptor(Spin2Bytecode.bc_shl_write, Spin2Bytecode.bc_shl_write_push, "SHIFT_LEFT_ASSIGN"));
        assignOperators.put(">>=", new Descriptor(Spin2Bytecode.bc_shr_write, Spin2Bytecode.bc_shr_write_push, "SAR_ASSIGN"));
        //assignOperators.put("ROR=", new Descriptor(0x9E, 0xC5, "ROR_ASSIGN"));
        //assignOperators.put("ROL=", new Descriptor(0x9F, 0xC6, "ROL_ASSIGN"));
        //assignOperators.put("REV=", new Descriptor(0xA0, 0xC7, "REV_ASSIGN"));
        //assignOperators.put("ZEROX=", new Descriptor(0xA1, 0xC8, "ZEROX_ASSIGN"));
        //assignOperators.put("SIGNX=", new Descriptor(0xA2, 0xC9, "SIGNX_ASSIGN"));
        assignOperators.put("+=", new Descriptor(Spin2Bytecode.bc_add_write, Spin2Bytecode.bc_add_write_push, "ADD_ASSIGN"));
        assignOperators.put("-=", new Descriptor(Spin2Bytecode.bc_sub_write, Spin2Bytecode.bc_sub_write_push, "SUBTRACT_ASSIGN"));
        //assignOperators.put("&&=", new Descriptor(0xA5, 0xCC, "BOOLEAN_AND_ASSIGN"));
        //assignOperators.put("AND=", new Descriptor(0xA5, 0xCC, "BOOLEAN_AND_ASSIGN"));
        //assignOperators.put("^^=", new Descriptor(0xA6, 0xCD, "BOOLEAN_XOR_ASSIGN"));
        //assignOperators.put("XOR=", new Descriptor(0xA6, 0xCD, "BOOLEAN_XOR_ASSIGN"));
        //assignOperators.put("||=", new Descriptor(0xA7, 0xCE, "BOOLEAN_OR_ASSIGN"));
        //assignOperators.put("OR=", new Descriptor(0xA7, 0xCE, "BOOLEAN_OR_ASSIGN"));
        assignOperators.put("&=", new Descriptor(Spin2Bytecode.bc_bitand_write, Spin2Bytecode.bc_bitand_write_push, "BITAND_ASSIGN"));
        assignOperators.put("^=", new Descriptor(Spin2Bytecode.bc_bitxor_write, Spin2Bytecode.bc_bitxor_write_push, "BITXOR_ASSIGN"));
        assignOperators.put("|=", new Descriptor(Spin2Bytecode.bc_bitor_write, Spin2Bytecode.bc_bitor_write_push, "BITOR_ASSIGN"));
        //assignOperators.put("#>=", new Descriptor(0xAB, 0xD2, "LIMIT_MIN_ASSIGN"));
        //assignOperators.put("<#=", new Descriptor(0xAC, 0xD3, "LIMIT_MAX_ASSIGN"));
        //assignOperators.put("ADDBITS=", new Descriptor(0xAD, 0xD4, "ADDBITS_ASSIGN"));
        //assignOperators.put("ADDPINS=", new Descriptor(0xAE, 0xD5, "ADDPINS_ASSIGN"));
        assignOperators.put("*=", new Descriptor(Spin2Bytecode.bc_mul_write, Spin2Bytecode.bc_mul_write_push, "MULTIPLY_ASSIGN"));
        assignOperators.put("/=", new Descriptor(Spin2Bytecode.bc_div_write, Spin2Bytecode.bc_div_write_push, "DIVIDE_ASSIGN"));
        //assignOperators.put("+/=", new Descriptor(0xB1, 0xD8, "DIVIDE_ASSIGN (unsigned)"));
        assignOperators.put("%=", new Descriptor(Spin2Bytecode.bc_rem_write, Spin2Bytecode.bc_rem_write_push, "MODULO_ASSIGN"));
        //assignOperators.put("+//=", new Descriptor(0xB3, 0xDA, "MODULO_ASSIGN (unsigned)"));
        //assignOperators.put("SCA=", new Descriptor(0xB4, 0xDB, "SCA_ASSIGN"));
        //assignOperators.put("SCAS=", new Descriptor(0xB5, 0xDC, "SCAS_ASSIGN"));
        //assignOperators.put("FRAC=", new Descriptor(0xB6, 0xDD, "FRAC_ASSIGN"));
    }

    public Spin2CBytecodeCompiler(Context scope, Spin2Compiler compiler, File file) {
        super(scope, compiler, file);
    }

    public Spin2CBytecodeCompiler(Context scope, Spin2Compiler compiler, ObjectCompiler parent, File file) {
        super(scope, compiler, parent, file);
    }

    public List<Spin2Bytecode> compileBytecodeExpression(Context context, Spin2Method method, Spin2StatementNode node, boolean push) {
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        try {
            if (node.isMethod()) {
                FunctionDescriptor desc = descriptors.get(node.getText());
                if (desc != null) {
                    int actual = getArgumentsCount(context, node);
                    if (actual != desc.getParameters()) {
                        throw new RuntimeException("expected " + desc.getParameters() + " argument(s), found " + actual);
                    }
                    for (int i = 0; i < node.getChildCount(); i++) {
                        if (isFloat(context, node.getChild(i))) {
                            logMessage(new CompilerException(CompilerException.WARNING, "float to integer conversion", node.getChild(i).getTokens()));
                            Spin2StatementNode exp = new Spin2StatementNode(new Token(Token.KEYWORD, "round"), true);
                            exp.getChilds().add(node.getChild(i));
                            source.addAll(compileConstantExpression(context, method, exp));
                        }
                        else {
                            source.addAll(compileConstantExpression(context, method, node.getChild(i)));
                        }
                    }
                    source.add(new Bytecode(context, desc.code, node.getText().toUpperCase()));
                }
                else if ("abs".equals(node.getText())) {
                    int actual = getArgumentsCount(context, node);
                    if (actual != 1) {
                        throw new RuntimeException("expected " + 1 + " argument(s), found " + actual);
                    }
                    source.addAll(compileConstantExpression(context, method, node.getChild(0)));
                    if (isFloat(context, node.getChild(0))) {
                        source.add(new Bytecode(context, new byte[] {
                            Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_fabs
                        }, "FLOAT_" + node.getText().toUpperCase()));
                    }
                    else {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_abs, node.getText().toUpperCase()));
                    }
                }
                else if ("sqrt".equals(node.getText())) {
                    int actual = getArgumentsCount(context, node);
                    if (actual != 1) {
                        throw new RuntimeException("expected " + 1 + " argument(s), found " + actual);
                    }
                    source.addAll(compileConstantExpression(context, method, node.getChild(0)));
                    if (isFloat(context, node.getChild(0))) {
                        source.add(new Bytecode(context, new byte[] {
                            Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_fsqrt
                        }, "FLOAT_" + node.getText().toUpperCase()));
                    }
                    else {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_sqrt, node.getText().toUpperCase()));
                    }
                }
                else if ("nan".equals(node.getText())) {
                    int actual = getArgumentsCount(context, node);
                    if (actual != 1) {
                        throw new RuntimeException("expected " + 1 + " argument(s), found " + actual);
                    }
                    if (!isFloat(context, node.getChild(0))) {
                        Spin2StatementNode exp = new Spin2StatementNode(new Token(Token.KEYWORD, "float"), true);
                        exp.getChilds().add(node.getChild(0));
                        source.addAll(compileConstantExpression(context, method, exp));
                    }
                    else {
                        source.addAll(compileConstantExpression(context, method, node.getChild(0)));
                    }
                    source.add(new Bytecode(context, new byte[] {
                        Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_nan
                    }, node.getText().toUpperCase()));
                }
                else if ("round".equals(node.getText())) {
                    int actual = getArgumentsCount(context, node);
                    if (actual != 1) {
                        throw new RuntimeException("expected " + 1 + " argument(s), found " + actual);
                    }
                    source.addAll(compileConstantExpression(context, method, node.getChild(0)));
                    if (isFloat(context, node.getChild(0))) {
                        source.add(new Bytecode(context, new byte[] {
                            Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_round
                        }, node.getText().toUpperCase()));
                    }
                    else {
                        logMessage(new CompilerException(CompilerException.WARNING, "not floating point", node.getChild(0).getTokens()));
                    }
                }
                else if ("trunc".equals(node.getText())) {
                    int actual = getArgumentsCount(context, node);
                    if (actual != 1) {
                        throw new RuntimeException("expected " + 1 + " argument(s), found " + actual);
                    }
                    source.addAll(compileConstantExpression(context, method, node.getChild(0)));
                    if (isFloat(context, node.getChild(0))) {
                        source.add(new Bytecode(context, new byte[] {
                            Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_trunc
                        }, node.getText().toUpperCase()));
                    }
                    else {
                        logMessage(new CompilerException(CompilerException.WARNING, "not floating point", node.getChild(0).getTokens()));
                    }
                }
                else if ("float".equals(node.getText())) {
                    int actual = getArgumentsCount(context, node);
                    if (actual != 1) {
                        throw new RuntimeException("expected " + 1 + " argument(s), found " + actual);
                    }
                    source.addAll(compileConstantExpression(context, method, node.getChild(0)));
                    if (!isFloat(context, node.getChild(0))) {
                        source.add(new Bytecode(context, new byte[] {
                            Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_float
                        }, node.getText().toUpperCase()));
                    }
                    else {
                        logMessage(new CompilerException(CompilerException.WARNING, "already floating point", node.getChild(0).getTokens()));
                    }
                }
                else if ("coginit".equals(node.getText())) {
                    if (node.getChildCount() != 3) {
                        throw new RuntimeException("expected " + 3 + " argument(s), found " + node.getChildCount());
                    }
                    for (int i = 0; i < node.getChildCount(); i++) {
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(i), true));
                    }
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_coginit_push : Spin2Bytecode.bc_coginit, node.getText().toUpperCase()));
                }
                else if ("cognew".equals(node.getText())) {
                    if (node.getChildCount() != 2) {
                        throw new RuntimeException("expected " + 2 + " argument(s), found " + node.getChildCount());
                    }
                    source.add(new Constant(context, new NumberLiteral(16)));
                    for (int i = 0; i < node.getChildCount(); i++) {
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(i), true));
                    }
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_coginit_push : Spin2Bytecode.bc_coginit, node.getText().toUpperCase()));
                }
                else if ("cogspin".equals(node.getText())) {
                    if (node.getChildCount() != 3) {
                        throw new RuntimeException("expected " + 3 + " argument(s), found " + node.getChildCount());
                    }

                    source.addAll(compileConstantExpression(context, method, node.getChild(0)));

                    Spin2StatementNode methodNode = node.getChild(1);
                    Expression expression = context.getLocalSymbol(methodNode.getText());
                    if (!(expression instanceof Method)) {
                        throw new CompilerException("invalid method " + methodNode.getText(), methodNode.getToken());
                    }
                    int actual = getArgumentsCount(context, methodNode);
                    if (actual != ((Method) expression).getArgumentsCount()) {
                        throw new CompilerException("expected " + ((Method) expression).getArgumentsCount() + " argument(s), found " + actual, methodNode.getToken());
                    }
                    for (int i = 0; i < methodNode.getChildCount(); i++) {
                        source.addAll(compileConstantExpression(context, method, methodNode.getChild(i)));
                    }
                    source.add(new SubAddress(context, (Method) expression, false));
                    Spin2Method calledMethod = (Spin2Method) expression.getData(Spin2Method.class.getName());
                    calledMethod.setCalledBy(method);

                    source.addAll(compileConstantExpression(context, method, node.getChild(2)));

                    source.add(new Bytecode(context, new byte[] {
                        Spin2Bytecode.bc_hub_bytecode, Spin2Bytecode.bc_cogspin,
                        (byte) methodNode.getChildCount(), (byte) (push ? Spin2Bytecode.bc_coginit_push : Spin2Bytecode.bc_coginit)
                    }, node.getText().toUpperCase()));
                }
                else if ("taskspin".equals(node.getText())) {
                    if (node.getChildCount() != 3) {
                        throw new RuntimeException("expected " + 3 + " argument(s), found " + node.getChildCount());
                    }

                    source.addAll(compileConstantExpression(context, method, node.getChild(0)));

                    Spin2StatementNode methodNode = node.getChild(1);
                    Expression expression = context.getLocalSymbol(methodNode.getText());
                    if (!(expression instanceof Method)) {
                        throw new CompilerException("invalid method " + methodNode.getText(), methodNode.getToken());
                    }
                    int actual = getArgumentsCount(context, methodNode);
                    if (actual != ((Method) expression).getArgumentsCount()) {
                        throw new CompilerException("expected " + ((Method) expression).getArgumentsCount() + " argument(s), found " + actual, methodNode.getToken());
                    }
                    for (int i = 0; i < methodNode.getChildCount(); i++) {
                        source.addAll(compileConstantExpression(context, method, methodNode.getChild(i)));
                    }
                    source.add(new SubAddress(context, (Method) expression, false));
                    Spin2Method calledMethod = (Spin2Method) expression.getData(Spin2Method.class.getName());
                    calledMethod.setCalledBy(method);

                    source.addAll(compileConstantExpression(context, method, node.getChild(2)));

                    source.add(new Bytecode(context, new byte[] {
                        Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_taskspin, (byte) ((push ? 0x80 : 0x00) | actual)
                    }, node.getText().toUpperCase()));
                }
                else if ("recv".equals(node.getText())) {
                    if (node.getChildCount() != 0) {
                        throw new RuntimeException("expected " + 0 + " argument(s), found " + node.getChildCount());
                    }
                    source.add(new Bytecode(context, Spin2Bytecode.bc_call_recv, node.getText().toUpperCase()));
                }
                else if ("send".equals(node.getText())) {
                    if (node.getChildCount() == 0) {
                        throw new RuntimeException("syntax error");
                    }
                    boolean bytes = true;
                    for (Spin2StatementNode child : node.getChilds()) {
                        if (child.getType() != Token.NUMBER) {
                            bytes = false;
                            break;
                        }
                    }
                    if (bytes) {
                        byte[] code = new byte[node.getChildCount() + 2];
                        code[0] = Spin2Bytecode.bc_call_send_bytes;
                        code[1] = (byte) node.getChildCount();
                        for (int i = 0; i < node.getChildCount(); i++) {
                            code[i + 2] = (byte) new NumberLiteral(node.getChild(i).getText()).getNumber().intValue();
                        }
                        source.add(new Bytecode(context, code, node.getText().toUpperCase()));
                    }
                    else {
                        for (Spin2StatementNode child : node.getChilds()) {
                            source.addAll(compileBytecodeExpression(context, method, child, true));
                            source.add(new Bytecode(context, Spin2Bytecode.bc_call_send, node.getText().toUpperCase()));
                        }
                    }
                }
                else if ("debug".equals(node.getText())) {
                    int stack = 0;
                    for (Spin2StatementNode child : node.getChilds()) {
                        if (child.getType() == Token.STRING) {
                            for (Spin2StatementNode param : child.getChilds()) {
                                source.addAll(compileBytecodeExpression(context, method, param, true));
                                stack += 4;
                            }
                        }
                        else if (Spin2Model.isDebugKeyword(child.getText())) {
                            for (Spin2StatementNode param : child.getChilds()) {
                                source.addAll(compileBytecodeExpression(context, method, param, true));
                                stack += 4;
                            }
                        }
                        else if (child.getText().startsWith("`")) {
                            for (Spin2StatementNode param : child.getChilds()) {
                                source.addAll(compileBytecodeExpression(context, method, param, true));
                                stack += 4;
                            }
                        }
                        else {
                            source.addAll(compileBytecodeExpression(context, method, child, true));
                            stack += 4;
                        }
                    }
                    debug.compileDebugStatement(node);

                    if (isDebugEnabled()) {
                        method.debugNodes.add(node);
                        compiler.debugStatements.add(node);

                        int pop = stack;
                        source.add(new Bytecode(context, Spin2Bytecode.bc_debug, "") {

                            int index;

                            @Override
                            public int resolve(int address) {
                                index = compiler.debugStatements.indexOf(node) + 1;
                                if (index >= 255) {
                                    throw new CompilerException("too much debug statements", node);
                                }
                                return super.resolve(address);
                            }

                            @Override
                            public int getSize() {
                                return index == -1 ? 0 : 3;
                            }

                            @Override
                            public byte[] getBytes() {
                                if (index == -1) {
                                    return new byte[0];
                                }
                                return new byte[] {
                                    Spin2Bytecode.bc_debug, (byte) pop, (byte) index
                                };
                            }

                            @Override
                            public String toString() {
                                if (index == -1) {
                                    return "";
                                }
                                return node.getText().toUpperCase() + " #" + index;
                            }

                        });
                        return source;
                    }
                    return new ArrayList<Spin2Bytecode>();
                }
                else if ("end".equals(node.getText())) {
                    // Ignored
                }
                else if ("lookdown".equals(node.getText()) || "lookdownz".equals(node.getText()) || "lookup".equals(node.getText()) || "lookupz".equals(node.getText())) {
                    if (node.getChildCount() == 0) {
                        throw new RuntimeException("expected argument(s), found none");
                    }
                    Spin2StatementNode argsNode = node.getChild(0);
                    if (!":".equals(argsNode.getText()) || argsNode.getChildCount() < 2) {
                        throw new RuntimeException("invalid argument(s)");
                    }

                    int code = Spin2Bytecode.bc_lookup_value;
                    int code_range = Spin2Bytecode.bc_lookup_range;
                    if ("lookdown".equals(node.getText()) || "lookdownz".equals(node.getText())) {
                        code = Spin2Bytecode.bc_lookdown_value;
                        code_range = Spin2Bytecode.bc_lookdown_range;
                    }

                    Spin2Bytecode end = new Spin2Bytecode(context);
                    source.add(new Address(context, new ContextLiteral(end.getContext())));

                    source.addAll(compileBytecodeExpression(context, method, argsNode.getChild(0), true));

                    source.add(new Constant(context, new NumberLiteral(node.getText().toUpperCase().endsWith("z") ? 0 : 1)));

                    for (int i = 1; i < argsNode.getChildCount(); i++) {
                        Spin2StatementNode arg = argsNode.getChild(i);
                        if ("..".equals(arg.getText())) {
                            source.addAll(compileBytecodeExpression(context, method, arg.getChild(0), true));
                            source.addAll(compileBytecodeExpression(context, method, arg.getChild(1), true));
                            source.add(new Bytecode(context, code_range, node.getText().toUpperCase()));
                        }
                        else if (arg.getType() == Token.STRING) {
                            String s = arg.getText().substring(1, arg.getText().length() - 1);
                            for (int x = 0; x < s.length(); x++) {
                                source.add(new Constant(context, new CharacterLiteral(s.substring(x, x + 1))));
                                source.add(new Bytecode(context, code, node.getText().toUpperCase()));
                            }
                        }
                        else {
                            source.addAll(compileBytecodeExpression(context, method, arg, true));
                            source.add(new Bytecode(context, code, node.getText().toUpperCase()));
                        }
                    }

                    source.add(new Bytecode(context, Spin2Bytecode.bc_look_done, "LOOKDONE"));
                    source.add(end);
                }
                else if ("string".equals(node.getText())) {
                    StringBuilder sb = new StringBuilder();
                    for (Spin2StatementNode child : node.getChilds()) {
                        if (child.getType() == Token.STRING) {
                            String s = child.getText().substring(1);
                            sb.append(s.substring(0, s.length() - 1));
                        }
                        else if (child.getType() == Token.NUMBER) {
                            NumberLiteral expression = new NumberLiteral(child.getText());
                            sb.append((char) expression.getNumber().intValue());
                        }
                        else {
                            try {
                                Expression expression = buildConstantExpression(context, child);
                                if (!expression.isConstant()) {
                                    throw new CompilerException("expression is not constant", child.getToken());
                                }
                                sb.append((char) expression.getNumber().intValue());
                            } catch (Exception e) {
                                throw new CompilerException("expression is not constant", child.getToken());
                            }
                        }
                    }
                    sb.append((char) 0x00);

                    byte[] code = sb.toString().getBytes();
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    os.write(Spin2Bytecode.bc_string);
                    os.write(code.length);
                    os.writeBytes(code);
                    source.add(new Bytecode(context, os.toByteArray(), node.getText().toUpperCase()));
                }
                else if ("sizeof".equals(node.getText())) {
                    if (node.getChildCount() == 0) {
                        throw new CompilerException("syntax error", node.getTokens());
                    }

                    int n = 0;
                    String typeText = node.getChild(n++).getText();
                    boolean pointer = false;

                    if ("struct".equals(typeText)) {
                        if (n >= node.getChildCount()) {
                            throw new CompilerException("syntax error", node.getTokens());
                        }
                        typeText += " " + node.getChild(n++).getText();
                    }

                    if (n < node.getChildCount()) {
                        if (!"*".equals(node.getChild(n++).getText())) {
                            throw new CompilerException("syntax error", node.getTokens());
                        }
                        pointer = true;
                    }

                    if (n < node.getChildCount()) {
                        throw new CompilerException("syntax error", node.getTokens());
                    }

                    if (pointer) {
                        source.add(new Constant(context, new NumberLiteral(4)));
                    }
                    else {
                        Expression expression = null;
                        if ("int".equals(typeText) || "long".equals(typeText) || "float".equals(typeText)) {
                            source.add(new Constant(context, new NumberLiteral(4)));
                        }
                        else if ("word".equals(typeText) || "short".equals(typeText)) {
                            source.add(new Constant(context, new NumberLiteral(2)));
                        }
                        else if ("byte".equals(typeText)) {
                            source.add(new Constant(context, new NumberLiteral(1)));
                        }
                        else {
                            expression = context.getLocalSymbol(typeText);
                            if (expression instanceof Variable) {
                                Variable variable = (Variable) expression;
                                source.add(new Constant(context, new NumberLiteral(variable.getTypeSize() * variable.getSize())));
                            }
                            else {
                                throw new CompilerException("invalid type", node.getChild(0).getTokens());
                            }
                        }
                    }
                }
                else {
                    Expression expression = context.getLocalSymbol(node.getText());
                    if (expression == null || !(expression instanceof Method)) {
                        throw new CompilerException("unknown function " + node.getText(), node.getToken());
                    }
                    source.addAll(compileMethodCall(context, method, expression, node, push, false));
                }
            }
            else if (node.getType() == Token.NUMBER) {
                Expression expression = new NumberLiteral(node.getText());
                source.add(new Constant(context, expression));
            }
            else if (node.getType() == Token.CHAR) {
                String s = node.getText().substring(1, node.getText().length() - 1);
                if (s.length() != 1) {
                    throw new CompilerException("invalid character constant", node.getToken());
                }
                source.add(new Constant(context, new CharacterLiteral(s)));
            }
            else if (node.getType() == Token.STRING) {
                String s = node.getText().substring(1, node.getText().length() - 1);
                StringBuilder sb = new StringBuilder(s);
                sb.append((char) 0x00);

                byte[] code = sb.toString().getBytes();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                os.write(Spin2Bytecode.bc_string);
                os.write(code.length);
                os.writeBytes(code);
                source.add(new Bytecode(context, os.toByteArray(), "STRING"));
            }
            else if ("*".equals(node.getText()) && node.getChildCount() == 1) {
                source.addAll(compilePointerDereference(context, method, node.getChild(0), push ? MemoryOp.Op.Read : MemoryOp.Op.Write));
            }
            else if ("-".equals(node.getText()) && node.getChildCount() == 1) {
                try {
                    Expression expression = buildConstantExpression(context, node);
                    source.add(new Constant(context, expression));
                } catch (Exception e) {
                    if (isFloat(context, node.getChild(0))) {
                        source.add(new Bytecode(context, new byte[] {
                            Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_fneg
                        }, "FLOAT_NEGATE"));
                    }
                    else {
                        source.add(new Bytecode(context, Spin2Bytecode.bc_neg, "NEGATE"));
                    }
                }
            }
            else if ("+".equals(node.getText()) && node.getChildCount() == 1) {
                try {
                    Expression expression = buildConstantExpression(context, node);
                    source.add(new Constant(context, expression));
                } catch (Exception e) {
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                }
            }
            else if ("-=".equals(node.getText()) && node.getChildCount() == 1) {
                source.addAll(leftAssign(context, method, node.getChild(0), true, false));
                if (push) {
                    source.add(new Bytecode(context, Spin2Bytecode.bc_neg_write_push, "NEGATE_ASSIGN (push)"));
                }
                else {
                    source.add(new Bytecode(context, Spin2Bytecode.bc_neg_write, "NEGATE_ASSIGN"));
                }
            }
            else if ("=".equals(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new RuntimeException("expression syntax error");
                }
                boolean leftIsFloat = isFloat(context, node.getChild(0));
                boolean rightIsFloat = isFloat(context, node.getChild(1));

                if (leftIsFloat && !rightIsFloat) {
                    Spin2StatementNode exp = new Spin2StatementNode(new Token(Token.KEYWORD, "float"), true);
                    exp.getChilds().add(node.getChild(1));
                    source.addAll(compileConstantExpression(context, method, exp));
                }
                else if (!leftIsFloat && rightIsFloat) {
                    logMessage(new CompilerException(CompilerException.WARNING, "float to integer conversion", node.getChild(0).getToken()));
                    Spin2StatementNode exp = new Spin2StatementNode(new Token(Token.KEYWORD, "round"), true);
                    exp.getChilds().add(node.getChild(1));
                    source.addAll(compileConstantExpression(context, method, exp));
                }
                else {
                    source.addAll(compileConstantExpression(context, method, node.getChild(1)));
                }
                source.addAll(leftAssign(context, method, node.getChild(0), push, push));
            }
            else if (assignOperators.containsKey(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new CompilerException("expression syntax error", node.getToken());
                }
                boolean leftIsFloat = isFloat(context, node.getChild(0));
                boolean rightIsFloat = isFloat(context, node.getChild(1));

                Descriptor desc = assignOperators.get(node.getText());

                if (leftIsFloat && !rightIsFloat) {
                    Spin2StatementNode exp = new Spin2StatementNode(new Token(Token.KEYWORD, "float"), true);
                    exp.getChilds().add(node.getChild(1));
                    source.addAll(compileConstantExpression(context, method, exp));
                }
                else if (!leftIsFloat && rightIsFloat) {
                    logMessage(new CompilerException(CompilerException.WARNING, "float to integer conversion", node.getChild(0).getToken()));
                    Spin2StatementNode exp = new Spin2StatementNode(new Token(Token.KEYWORD, "round"), true);
                    exp.getChilds().add(node.getChild(1));
                    source.addAll(compileConstantExpression(context, method, exp));
                }
                else {
                    source.addAll(compileConstantExpression(context, method, node.getChild(1)));
                }
                source.addAll(leftAssign(context, method, node.getChild(0), true, false));
                source.add(new Bytecode(context, push ? desc.push_value : desc.value, desc.text + (push ? " (push)" : "")));
            }
            else if (unaryOperators.containsKey(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new CompilerException("expression syntax error", node.getToken());
                }
                Descriptor desc = unaryOperators.get(node.getText());
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                source.add(new Bytecode(context, push ? desc.push_value : desc.value, desc.text + (push ? " (push)" : "")));
            }
            else if (operators.containsKey(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new CompilerException("expression syntax error", node.getToken());
                }
                boolean leftIsFloat = isFloat(context, node.getChild(0));
                boolean rightIsFloat = isFloat(context, node.getChild(1));
                Descriptor desc = (leftIsFloat || rightIsFloat) ? floatOperators.get(node.getText()) : operators.get(node.getText());
                if (desc == null) {
                    throw new CompilerException("invalid operator", node.getToken());
                }

                if (!leftIsFloat && rightIsFloat) {
                    Spin2StatementNode exp = new Spin2StatementNode(new Token(Token.KEYWORD, "float"), true);
                    exp.getChilds().add(node.getChild(0));
                    source.addAll(compileBytecodeExpression(context, method, exp, true));
                }
                else {
                    source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                }

                if (leftIsFloat && !rightIsFloat) {
                    Spin2StatementNode exp = new Spin2StatementNode(new Token(Token.KEYWORD, "float"), true);
                    exp.getChilds().add(node.getChild(1));
                    source.addAll(compileConstantExpression(context, method, exp));
                }
                else {
                    source.addAll(compileConstantExpression(context, method, node.getChild(1)));
                }

                source.add(new Bytecode(context, push ? desc.push_value : desc.value, desc.text));
            }
            else if ("?".equals(node.getText())) {
                if (node.getChildCount() != 2) {
                    throw new CompilerException("expression syntax error", node.getToken());
                }
                if (!":".equals(node.getChild(1).getText())) {
                    throw new CompilerException("expression syntax error", node.getChild(1).getToken());
                }
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                source.addAll(compileBytecodeExpression(context, method, node.getChild(1).getChild(0), true));
                source.addAll(compileBytecodeExpression(context, method, node.getChild(1).getChild(1), true));
                source.add(new Bytecode(context, Spin2Bytecode.bc_ternary, "TERNARY_IF_ELSE"));
            }
            else if ("(".equals(node.getText())) {
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), push));
            }
            else if ("\\".equals(node.getText())) {
                if (node.getChildCount() == 2) {
                    source.addAll(compileConstantExpression(context, method, node.getChild(1)));
                    source.addAll(leftAssign(context, method, node.getChild(0), push, false));
                    source.add(new Bytecode(context, Spin2Bytecode.bc_var_swap, "SWAP"));
                }
                else {
                    if (node.getChildCount() != 1) {
                        throw new RuntimeException("expression syntax error");
                    }
                    Expression expression = context.getLocalSymbol(node.getChild(0).getText());

                    if (expression instanceof Method) {
                        source.addAll(compileMethodCall(context, method, expression, node.getChild(0), push, true));
                    }
                    else if (expression instanceof Variable) {
                        source.addAll(compileMethodCall(context, method, expression, node.getChild(0), push, true));
                    }
                    else if (expression instanceof DataVariable) {
                        source.addAll(compileMethodCall(context, method, expression, node.getChild(0), push, true));
                    }
                    else if ("BYTE".equals(node.getChild(0).getText()) || "WORD".equals(node.getChild(0).getText()) || "LONG".equals(node.getChild(0).getText())) {
                        source.addAll(compileBytecodeExpression(context, method, node.getChild(0), push));
                    }
                    else {
                        throw new CompilerException("symbol " + node.getChild(0).getText() + " is not a method", node.getChild(0).getToken());
                    }
                }
            }
            else if ("++".equals(node.getText()) || "--".equals(node.getText())) {
                if (node.getChildCount() != 1) {
                    throw new CompilerException("expression syntax error", node.getToken());
                }

                Spin2StatementNode childNode = node.getChild(0);
                if ("BYTE".equals(childNode.getText()) || "WORD".equals(childNode.getText()) || "LONG".equals(childNode.getText())) {
                    Spin2StatementNode indexNode = null;
                    Spin2StatementNode bitfieldNode = null;

                    int n = 1;
                    if (n < childNode.getChildCount() && (childNode.getChild(n) instanceof Spin2StatementNode.Index)) {
                        indexNode = childNode.getChild(n++);
                    }
                    if (n < childNode.getChildCount() && ".".equals(childNode.getChild(n).getText())) {
                        n++;
                        if (n >= childNode.getChildCount()) {
                            throw new CompilerException("expected bitfield expression", childNode.getToken());
                        }
                        if (!(childNode.getChild(n) instanceof Spin2StatementNode.Index)) {
                            throw new RuntimeException("syntax error");
                        }
                        bitfieldNode = childNode.getChild(n++);
                    }
                    if (n < childNode.getChildCount()) {
                        throw new RuntimeException("syntax error");
                    }

                    source.addAll(compileBytecodeExpression(context, method, childNode.getChild(0), true));

                    if (indexNode != null) {
                        source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                    }

                    int bitfield = -1;
                    if (bitfieldNode != null) {
                        bitfield = compileBitfield(context, method, bitfieldNode, source);
                    }

                    MemoryOp.Size ss = MemoryOp.Size.Long;
                    if ("BYTE".equals(childNode.getText())) {
                        ss = MemoryOp.Size.Byte;
                    }
                    else if ("WORD".equals(childNode.getText())) {
                        ss = MemoryOp.Size.Word;
                    }
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Setup, indexNode != null));

                    if (bitfieldNode != null) {
                        source.add(new BitField(context, BitField.Op.Setup, bitfield));
                    }
                }
                else {
                    Expression expression = context.getLocalSymbol(childNode.getText());
                    if (expression == null) {
                        throw new CompilerException("unsupported operation on " + childNode.getText(), childNode.getToken());
                    }
                    source.addAll(compileVariableSetup(context, method, expression, childNode));
                }
                if ("++".equals(node.getText())) {
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_preinc_push : Spin2Bytecode.bc_var_inc, "PRE_INC" + (push ? " (push)" : "")));
                }
                else if ("--".equals(node.getText())) {
                    source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_predec_push : Spin2Bytecode.bc_var_dec, "PRE_DEC" + (push ? " (push)" : "")));
                }
            }
            else if ("BYTE".equals(node.getText()) || "WORD".equals(node.getText()) || "LONG".equals(node.getText())
                || "@BYTE".equals(node.getText()) || "@WORD".equals(node.getText()) || "@LONG".equals(node.getText())
                || "@@BYTE".equals(node.getText()) || "@@WORD".equals(node.getText()) || "@@LONG".equals(node.getText())) {
                Spin2StatementNode indexNode = null;
                Spin2StatementNode bitfieldNode = null;
                Spin2StatementNode postEffectNode = null;

                int n = 1;
                if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                    indexNode = node.getChild(n++);
                }
                if (n < node.getChildCount() && ".".equals(node.getChild(n).getText())) {
                    if (node.getText().startsWith("@")) {
                        throw new CompilerException("bitfield expression not allowed", node.getChild(n).getToken());
                    }
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new RuntimeException("expected bitfield expression");
                    }
                    if (!(node.getChild(n) instanceof Spin2StatementNode.Index)) {
                        throw new RuntimeException("syntax error");
                    }
                    bitfieldNode = node.getChild(n++);
                }

                if (node.isMethod()) {
                    if (node.getParent() != null && node.getParent().getText().startsWith("\\")) {
                        source.add(new Bytecode(context, push ? Spin2Bytecode.bc_drop_trap_push : Spin2Bytecode.bc_drop_trap, "ANCHOR_TRAP"));
                    }
                    else {
                        source.add(new Bytecode(context, push ? Spin2Bytecode.bc_drop_push : Spin2Bytecode.bc_drop, "ANCHOR"));
                    }

                    while (n < node.getChildCount()) {
                        if (!(node.getChild(n) instanceof Spin2StatementNode.Argument)) {
                            throw new CompilerException("syntax error", node.getChild(n));
                        }
                        source.addAll(compileConstantExpression(context, method, node.getChild(n++)));
                    }
                }
                else if (node.getText().startsWith("@")) {
                    if (n < node.getChildCount()) {
                        throw new CompilerException("syntax error", node.getChild(n).getToken());
                    }
                }

                if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                    postEffectNode = node.getChild(n++);
                }
                if (n < node.getChildCount()) {
                    throw new RuntimeException("syntax error");
                }

                int bitfield = -1;
                if (bitfieldNode != null) {
                    bitfield = compileBitfield(context, method, bitfieldNode, source);
                }

                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));

                if (indexNode != null) {
                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                }

                MemoryOp.Size ss = MemoryOp.Size.Long;
                if ("BYTE".equalsIgnoreCase(node.getText()) || "@BYTE".equalsIgnoreCase(node.getText()) || "@@BYTE".equalsIgnoreCase(node.getText())) {
                    ss = MemoryOp.Size.Byte;
                }
                else if ("WORD".equalsIgnoreCase(node.getText()) || "@WORD".equalsIgnoreCase(node.getText()) || "@@WORD".equalsIgnoreCase(node.getText())) {
                    ss = MemoryOp.Size.Word;
                }

                if (node.isMethod()) {
                    if (ss != MemoryOp.Size.Long) {
                        throw new RuntimeException("method pointers must be long");
                    }
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Read, indexNode != null));
                    source.add(new Bytecode(context, new byte[] {
                        (byte) Spin2Bytecode.bc_call_ptr,
                    }, "CALL_PTR"));
                }
                else if (node.getText().startsWith("@@")) {
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Read, indexNode != null));
                    source.add(new Bytecode(context, Spin2Bytecode.bc_add_pbase, "ADD_PBASE"));
                }
                else if (node.getText().startsWith("@")) {
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Address, indexNode != null));
                }
                else {
                    MemoryOp.Op op = bitfieldNode == null && postEffectNode == null ? (push ? MemoryOp.Op.Read : MemoryOp.Op.Write) : MemoryOp.Op.Setup;
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, op, indexNode != null));
                }

                if (bitfieldNode != null) {
                    source.add(new BitField(context, postEffectNode == null ? BitField.Op.Read : BitField.Op.Setup, bitfield));
                }

                if (postEffectNode != null) {
                    source.addAll(compilePostEffect(context, postEffectNode, push));
                }
            }
            else if ("REG".equals(node.getText())) {
                Spin2StatementNode indexNode = null;
                Spin2StatementNode bitfieldNode = null;
                Spin2StatementNode postEffectNode = null;

                int n = 1;
                if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                    indexNode = node.getChild(n++);
                }
                if (n < node.getChildCount() && ".".equals(node.getChild(n).getText())) {
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new RuntimeException("expected bitfield expression");
                    }
                    if (!(node.getChild(n) instanceof Spin2StatementNode.Index)) {
                        throw new RuntimeException("syntax error");
                    }
                    bitfieldNode = node.getChild(n++);
                }

                if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                    postEffectNode = node.getChild(n++);
                }
                if (n < node.getChildCount()) {
                    throw new RuntimeException("syntax error");
                }

                int bitfield = -1;
                if (bitfieldNode != null) {
                    bitfield = compileBitfield(context, method, bitfieldNode, source);
                }

                Expression expression = null;
                try {
                    expression = buildConstantExpression(context, node.getChild(0), true);
                } catch (Exception e) {
                    // Do nothing
                }
                if (expression == null) {
                    throw new CompilerException("expected constant expression", node.getChild(0).getToken());
                }

                int index = 0;
                boolean popIndex = indexNode != null;
                if (indexNode != null) {
                    try {
                        Expression exp = buildConstantExpression(context, indexNode);
                        if (exp.isConstant()) {
                            index = exp.getNumber().intValue();
                            popIndex = false;
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }
                    if (popIndex) {
                        source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                    }
                }

                RegisterOp.Op op = bitfieldNode == null && postEffectNode == null ? (push ? RegisterOp.Op.Read : RegisterOp.Op.Write) : RegisterOp.Op.Setup;
                source.add(new RegisterOp(context, op, popIndex, expression, index));

                if (bitfieldNode != null) {
                    source.add(new BitField(context, postEffectNode == null ? BitField.Op.Read : BitField.Op.Setup, bitfield));
                }

                if (postEffectNode != null) {
                    source.addAll(compilePostEffect(context, postEffectNode, push));
                }
            }
            else if ("FIELD".equals(node.getText()) && node.getChildCount() != 0) {
                Spin2StatementNode indexNode = null;
                Spin2StatementNode postEffectNode = null;

                int n = 1;
                if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                    indexNode = node.getChild(n++);
                }
                if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                    postEffectNode = node.getChild(n++);
                }
                if (n < node.getChildCount()) {
                    throw new RuntimeException("syntax error");
                }

                source.addAll(compileConstantExpression(context, method, node.getChild(0)));

                if (indexNode != null) {
                    source.addAll(compileConstantExpression(context, method, indexNode));
                }

                if (postEffectNode == null) {
                    source.add(new Bytecode(context, new byte[] {
                        (byte) (indexNode == null ? Spin2Bytecode.bc_setup_field_p : Spin2Bytecode.bc_setup_field_pi),
                        (byte) (push ? Spin2Bytecode.bc_read : Spin2Bytecode.bc_write)
                    }, "FIELD_" + (push ? "READ" : "WRITE")));
                }
                else {
                    source.add(new Bytecode(context, new byte[] {
                        (byte) (indexNode == null ? Spin2Bytecode.bc_setup_field_p : Spin2Bytecode.bc_setup_field_pi)
                    }, "FIELD_SETUP"));
                }

                if (postEffectNode != null) {
                    source.addAll(compilePostEffect(context, postEffectNode, push));
                }
            }
            else if ("CLKMODE".equalsIgnoreCase(node.getText()) || "CLKFREQ".equalsIgnoreCase(node.getText())) {
                Spin2StatementNode indexNode = null;
                Spin2StatementNode bitfieldNode = null;
                Spin2StatementNode postEffectNode = null;

                int n = 0;
                if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                    indexNode = node.getChild(n++);
                }
                if (n < node.getChildCount() && ".".equals(node.getChild(n).getText())) {
                    if (node.getText().startsWith("@")) {
                        throw new CompilerException("bitfield expression not allowed", node.getChild(n).getToken());
                    }
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new RuntimeException("expected bitfield expression");
                    }
                    if (!(node.getChild(n) instanceof Spin2StatementNode.Index)) {
                        throw new RuntimeException("syntax error");
                    }
                    bitfieldNode = node.getChild(n++);
                }
                if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                    postEffectNode = node.getChild(n++);
                }
                if (n < node.getChildCount()) {
                    throw new RuntimeException("syntax error");
                }

                int bitfield = -1;
                if (bitfieldNode != null) {
                    bitfield = compileBitfield(context, method, bitfieldNode, source);
                }

                if ("CLKMODE".equalsIgnoreCase(node.getText())) {
                    source.add(new Constant(context, new NumberLiteral(0x40, 16)));
                }
                if ("CLKFREQ".equalsIgnoreCase(node.getText())) {
                    source.add(new Constant(context, new NumberLiteral(0x44, 16)));
                }

                if (indexNode != null) {
                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                }

                MemoryOp.Size ss = MemoryOp.Size.Long;
                MemoryOp.Op op = bitfieldNode == null && postEffectNode == null ? (push ? MemoryOp.Op.Read : MemoryOp.Op.Write) : MemoryOp.Op.Setup;
                source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, op, indexNode != null));

                if (bitfieldNode != null) {
                    source.add(new BitField(context, postEffectNode == null ? BitField.Op.Read : BitField.Op.Setup, bitfield));
                }

                if (postEffectNode != null) {
                    compilePostEffect(context, postEffectNode, source, push);
                    node.setReturnLongs(push ? 1 : 0);
                }
                else {
                    if (!push) {
                        logMessage(new CompilerException("expected assignment", node.getTokens()));
                    }
                    node.setReturnLongs(1);
                }

                return source;
            }
            else {
                Expression expression = null;

                String[] ar = node.getText().split("[\\.]");
                if (ar.length >= 2 && ("BYTE".equalsIgnoreCase(ar[ar.length - 1]) || "WORD".equalsIgnoreCase(ar[ar.length - 1]) || "LONG".equalsIgnoreCase(ar[ar.length - 1]))) {
                    int index = 0;
                    boolean popIndex = false;
                    Spin2StatementNode indexNode = null;
                    Spin2StatementNode bitfieldNode = null;
                    Spin2StatementNode postEffectNode = null;

                    if (expression == null && isAbsoluteAddress(ar[0])) {
                        expression = context.getLocalSymbol(ar[0].substring(2));
                    }
                    if (expression == null && isAddress(ar[0])) {
                        expression = context.getLocalSymbol(ar[0].substring(1));
                    }
                    if (expression instanceof ObjectContextLiteral) {
                        expression = context.getLocalSymbol(ar[0].substring(1));
                    }
                    if (expression == null) {
                        throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
                    }

                    int n = 0;
                    if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                        indexNode = node.getChild(n++);
                    }
                    if (n < node.getChildCount() && ".".equals(node.getChild(n).getText())) {
                        if (node.getText().startsWith("@")) {
                            throw new CompilerException("bitfield expression not allowed", node.getChild(n).getToken());
                        }
                        n++;
                        if (n >= node.getChildCount()) {
                            throw new RuntimeException("expected bitfield expression");
                        }
                        if (!(node.getChild(n) instanceof Spin2StatementNode.Index)) {
                            throw new RuntimeException("syntax error");
                        }
                        bitfieldNode = node.getChild(n++);
                    }
                    if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                        postEffectNode = node.getChild(n++);
                    }
                    if (n < node.getChildCount()) {
                        throw new CompilerException("syntax error", node.getChild(n).getToken());
                    }

                    MemoryOp.Size ss = MemoryOp.Size.Long;
                    if ("BYTE".equals(ar[1])) {
                        ss = MemoryOp.Size.Byte;
                    }
                    else if ("WORD".equals(ar[1])) {
                        ss = MemoryOp.Size.Word;
                    }
                    MemoryOp.Base bb = MemoryOp.Base.PBase;
                    if (expression instanceof Variable) {
                        bb = expression instanceof LocalVariable ? MemoryOp.Base.DBase : MemoryOp.Base.VBase;
                        ((Variable) expression).setCalledBy(method);
                    }

                    int bitfield = -1;
                    if (bitfieldNode != null) {
                        bitfield = compileBitfield(context, method, bitfieldNode, source);
                    }

                    if (indexNode != null) {
                        popIndex = true;
                        try {
                            Expression exp = buildConstantExpression(context, indexNode);
                            if (exp.isConstant()) {
                                index = exp.getNumber().intValue();
                                popIndex = false;
                            }
                        } catch (Exception e) {
                            // Do nothing
                        }
                        if (popIndex) {
                            source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                        }
                    }

                    if (ar[0].startsWith("&")) {
                        source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Address, popIndex, expression, index));
                    }
                    else {
                        MemoryOp.Op op = bitfieldNode == null && postEffectNode == null ? (push ? MemoryOp.Op.Read : MemoryOp.Op.Write) : MemoryOp.Op.Setup;
                        source.add(new MemoryOp(context, ss, bb, op, popIndex, expression, index));
                    }

                    if (bitfieldNode != null) {
                        source.add(new BitField(context, postEffectNode == null ? BitField.Op.Read : BitField.Op.Setup, bitfield));
                    }

                    if (postEffectNode != null) {
                        if ("++".equals(postEffectNode.getText())) {
                            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postinc_push : Spin2Bytecode.bc_var_inc, "POST_INC" + (push ? " (push)" : "")));
                        }
                        else if ("--".equals(postEffectNode.getText())) {
                            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postdec_push : Spin2Bytecode.bc_var_dec, "POST_DEC" + (push ? " (push)" : "")));
                        }
                        else {
                            throw new CompilerException("unhandled post effect " + postEffectNode.getText(), postEffectNode.getToken());
                        }
                    }

                }
                else {
                    expression = context.getLocalSymbol(node.getText());
                    if (expression == null && isAbsoluteAddress(node.getText())) {
                        expression = context.getLocalSymbol(node.getText().substring(2));
                    }
                    if (expression == null && isAddress(node.getText())) {
                        expression = context.getLocalSymbol(node.getText().substring(1));
                    }
                    if (expression == null && node.getText().startsWith("^@")) {
                        expression = context.getLocalSymbol(node.getText().substring(2));
                    }
                    if (expression instanceof ObjectContextLiteral) {
                        expression = context.getLocalSymbol(node.getText().substring(1));
                    }

                    if (expression == null && ar.length > 1) {
                        expression = context.getLocalSymbol(ar[0]);
                        if (expression == null && isAddress(ar[0])) {
                            expression = context.getLocalSymbol(ar[0].substring(1));
                        }
                    }
                }

                if (expression == null) {
                    throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
                }

                if (expression instanceof SpinObject) {
                    if (node.getChildCount() != 2) {
                        throw new CompilerException("syntax error", node.getToken());
                    }
                    if (!(node.getChild(0) instanceof Spin2StatementNode.Index)) {
                        throw new CompilerException("syntax error", node.getChild(0).getToken());
                    }

                    String qualifiedName = node.getText() + node.getChild(1).getText();

                    expression = context.getLocalSymbol(qualifiedName);
                    if (expression == null && isAddress(qualifiedName)) {
                        expression = context.getLocalSymbol(qualifiedName.substring(1));
                    }
                    if (!(expression instanceof Method)) {
                        throw new CompilerException("syntax error", node.getToken());
                    }
                    Method methodExpression = (Method) expression;
                    if (isAddress(node.getText())) {
                        source.addAll(compileConstantExpression(context, method, node.getChild(0)));
                        source.add(new SubAddress(context, methodExpression, true));
                        Spin2Method calledMethod = (Spin2Method) methodExpression.getData(Spin2Method.class.getName());
                        calledMethod.setCalledBy(method);
                    }
                    else {
                        Spin2StatementNode childNode = node.getChild(1);

                        int expected = methodExpression.getArgumentsCount();
                        int actual = getArgumentsCount(context, childNode);
                        if (expected != actual) {
                            throw new CompilerException("expected " + expected + " argument(s), found " + actual, node.getToken());
                        }
                        if (push && methodExpression.getReturnLongs() == 0) {
                            throw new RuntimeException("method doesn't return any value");
                        }

                        source.add(new Bytecode(context, push ? Spin2Bytecode.bc_drop_push : Spin2Bytecode.bc_drop, "ANCHOR"));
                        for (int i = 0; i < childNode.getChildCount(); i++) {
                            source.addAll(compileConstantExpression(context, method, childNode.getChild(i)));
                        }
                        source.addAll(compileConstantExpression(context, method, node.getChild(0)));
                        source.add(new CallSub(context, methodExpression, true));
                        Spin2Method calledMethod = (Spin2Method) methodExpression.getData(Spin2Method.class.getName());
                        calledMethod.setCalledBy(method);
                    }
                    return source;
                }
                else if (expression instanceof Method) {
                    if (isAddress(node.getText())) {
                        Method methodExpression = (Method) expression;
                        source.add(new SubAddress(context, methodExpression));
                        Spin2Method calledMethod = (Spin2Method) methodExpression.getData(Spin2Method.class.getName());
                        calledMethod.setCalledBy(method);
                    }
                    else {
                        source.addAll(compileMethodCall(context, method, expression, node, push, false));
                    }
                }
                else if (expression instanceof Variable) {
                    if (isStructure(context, expression)) {
                        return compileStructure(context, method, node, expression, null, MemoryOp.Op.Read, push);
                    }
                    else if (isAddress(node.getText())) {
                        int index = 0;
                        boolean popIndex = false;
                        boolean hasIndex = false;
                        Spin2StatementNode indexNode = null;

                        int n = 0;
                        if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                            indexNode = node.getChild(n++);
                        }
                        if (n < node.getChildCount()) {
                            throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n));
                        }

                        if (indexNode != null) {
                            popIndex = true;
                            try {
                                Expression exp = buildConstantExpression(context, indexNode);
                                if (exp.isConstant()) {
                                    index = exp.getNumber().intValue();
                                    hasIndex = true;
                                    popIndex = false;
                                }
                            } catch (Exception e) {
                                // Do nothing
                            }
                            if (popIndex) {
                                source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                            }
                        }

                        if (node.getText().startsWith("@@")) {
                            source.add(new VariableOp(context, VariableOp.Op.Read, popIndex, (Variable) expression, hasIndex, index));
                            source.add(new Bytecode(context, Spin2Bytecode.bc_add_pbase, "ADD_PBASE"));
                        }
                        else {
                            source.add(new VariableOp(context, VariableOp.Op.Address, popIndex, (Variable) expression, hasIndex, index));
                        }
                    }
                    else {
                        if (node.isMethod()) {
                            source.addAll(compileMethodCall(context, method, expression, node, push, false));
                        }
                        else {
                            source.addAll(compileVariableRead(context, method, expression, node, push));
                        }
                    }
                    ((Variable) expression).setCalledBy(method);
                }
                else {
                    if (isAddress(node.getText())) {
                        int index = 0;
                        boolean popIndex = false;
                        Spin2StatementNode indexNode = null;

                        int n = 0;
                        if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                            indexNode = node.getChild(n++);
                        }
                        if (n < node.getChildCount()) {
                            throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n));
                        }

                        if (indexNode != null) {
                            popIndex = true;
                            try {
                                Expression exp = buildConstantExpression(context, indexNode);
                                if (exp.isConstant()) {
                                    index = exp.getNumber().intValue();
                                    popIndex = false;
                                }
                            } catch (Exception e) {
                                // Do nothing
                            }
                            if (popIndex) {
                                source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                            }
                        }

                        MemoryOp.Size ss = MemoryOp.Size.Long;
                        MemoryOp.Base bb = MemoryOp.Base.PBase;

                        if ((expression instanceof MemoryContextLiteral) && node.getText().startsWith("@@")) {
                            expression = context.getLocalSymbol(node.getText().substring(1));
                        }
                        if (expression instanceof DataVariable) {
                            switch (((DataVariable) expression).getType()) {
                                case "byte":
                                    ss = MemoryOp.Size.Byte;
                                    break;
                                case "word":
                                    ss = MemoryOp.Size.Word;
                                    break;
                            }
                        }
                        else if (expression instanceof ObjectContextLiteral) {
                            switch (((ObjectContextLiteral) expression).getType()) {
                                case "byte":
                                    ss = MemoryOp.Size.Byte;
                                    break;
                                case "word":
                                    ss = MemoryOp.Size.Word;
                                    break;
                            }
                        }
                        if (isAbsoluteAddress(node.getText())) {
                            source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Read, popIndex, expression, index));
                            source.add(new Bytecode(context, Spin2Bytecode.bc_add_pbase, "ADD_PBASE"));
                        }
                        else {
                            source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Address, popIndex, expression, index));
                        }
                    }
                    else if (expression instanceof Register) {
                        source.addAll(compileVariableRead(context, method, expression, node, push));
                    }
                    else if (expression instanceof DataVariable) {
                        if (node.isMethod()) {
                            source.addAll(compileMethodCall(context, method, expression, node, push, false));
                        }
                        else {
                            source.addAll(compileVariableRead(context, method, expression, node, push));
                        }
                    }
                    else if (expression instanceof ContextLiteral) {
                        source.addAll(compileVariableRead(context, method, expression, node, push));
                    }
                    else if (expression.isConstant()) {
                        if (node.getChildCount() != 0) {
                            throw new RuntimeException("syntax error");
                        }
                        source.add(new Constant(context, expression));
                    }
                    else {
                        if (node.getChildCount() != 0) {
                            throw new RuntimeException("syntax error");
                        }
                        source.add(new MemoryOp(context, MemoryOp.Size.Long, MemoryOp.Base.PBase, MemoryOp.Op.Read, expression));
                    }
                }
            }
        } catch (CompilerException e) {
            logMessage(e);
        } catch (Exception e) {
            logMessage(new CompilerException(e, node.getToken()));
        }

        return source;
    }

    protected boolean isAddress(String text) {
        return text.startsWith("&");
    }

    protected boolean isAbsoluteAddress(String text) {
        return false;
    }

    protected int getArgumentsCount(Context context, Spin2StatementNode childNode) {
        int actual = 0;
        for (int i = 0; i < childNode.getChildCount(); i++) {
            Expression child = context.getLocalSymbol(childNode.getChild(i).getText());
            if (child != null && (child instanceof Method) && !childNode.getChild(i).getText().startsWith("@")) {
                actual += ((Method) child).getReturnLongs();
                continue;
            }
            Spin2Bytecode.Descriptor descriptor = Spin2Bytecode.getDescriptor(childNode.getChild(i).getText().toUpperCase());
            if (descriptor != null) {
                actual += descriptor.getReturns();
                continue;
            }
            actual++;
        }
        return actual;
    }

    List<Spin2Bytecode> compileConstantExpression(Context context, Spin2Method method, Spin2StatementNode node) {
        try {
            Expression expression = buildConstantExpression(context, node);
            if (expression.isConstant()) {
                return Collections.singletonList(new Constant(context, expression));
            }
        } catch (Exception e) {
            // Do nothing
        }
        return compileBytecodeExpression(context, method, node, true);
    }

    Expression buildConstantExpression(Context context, Spin2StatementNode node) {
        return buildConstantExpression(context, node, false);
    }

    Expression buildConstantExpression(Context context, Spin2StatementNode node, boolean registerConstant) {
        if (node.getType() == Token.NUMBER) {
            return new NumberLiteral(node.getText());
        }
        else if (node.getType() == Token.CHAR) {
            String s = node.getText().substring(1);
            s = s.substring(0, s.length() - 1);
            if (s.length() == 1) {
                return new CharacterLiteral(s);
            }
            throw new RuntimeException("string not allowed");
        }
        else if (node.getType() == Token.STRING) {
            throw new RuntimeException("string not allowed");
        }

        Expression expression = context.getLocalSymbol(node.getText());
        if (expression != null) {
            if (node.getChildCount() == 0) {
                if (expression.isConstant()) {
                    return expression;
                }
                if (registerConstant && (expression instanceof Register) || (expression instanceof DataVariable)) {
                    return expression;
                }
            }
            throw new RuntimeException("not a constant (" + expression + ")");
        }

        switch (node.getText().toUpperCase()) {
            case ">>":
                return new ShiftRight(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "<<":
                return new ShiftLeft(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "&":
                return new And(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "^":
                return new Xor(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "|":
                return new Or(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "*":
                return new Multiply(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "/":
                return new Divide(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "%":
                return new Modulo(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "+":
                if (node.getChildCount() == 1) {
                    return buildConstantExpression(context, node.getChild(0), registerConstant);
                }
                return new Add(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "-":
                if (node.getChildCount() == 1) {
                    return new Negative(buildConstantExpression(context, node.getChild(0), registerConstant));
                }
                return new Subtract(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "&&":
                return new LogicalAnd(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "||":
                return new LogicalOr(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "<":
                return new LessThan(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "<=":
                return new LessOrEquals(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "==":
                return new Equals(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "!=":
                return new NotEquals(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case ">=":
                return new GreaterOrEquals(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case ">":
                return new GreaterThan(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "!":
                if (node.getChildCount() == 1) {
                    return new LogicalNot(buildConstantExpression(context, node.getChild(0), registerConstant));
                }
                throw new RuntimeException("unary operator with " + node.getChildCount() + " arguments");
            case "~":
                if (node.getChildCount() == 1) {
                    return new Not(buildConstantExpression(context, node.getChild(0), registerConstant));
                }
                throw new RuntimeException("unary operator with " + node.getChildCount() + " arguments");

            case "?": {
                Expression right = buildConstantExpression(context, node.getChild(1), registerConstant);
                if (!(right instanceof IfElse)) {
                    throw new RuntimeException("unsupported operator " + node.getText());
                }
                return new IfElse(buildConstantExpression(context, node.getChild(0), registerConstant), ((IfElse) right).getTrueTerm(), ((IfElse) right).getFalseTerm());
            }
            case ":":
                return new IfElse(null, buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));

            case "TRUNC":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Trunc(buildConstantExpression(context, node.getChild(0), registerConstant));
            case "SQRT":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Sqrt(buildConstantExpression(context, node.getChild(0), registerConstant));
            case "ROUND":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Round(buildConstantExpression(context, node.getChild(0), registerConstant));
            case "FLOAT":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new NumberLiteral(buildConstantExpression(context, node.getChild(0), registerConstant).getNumber().doubleValue());
            case "ABS":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Abs(buildConstantExpression(context, node.getChild(0), registerConstant));
            case "NAN":
                if (node.getChildCount() != 1) {
                    throw new RuntimeException("misplaced unary operator (" + node.getText() + ")");
                }
                return new Nan(buildConstantExpression(context, node.getChild(0), registerConstant));
            case "ADDBITS":
                return new Addbits(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "ADDPINS":
                return new Addpins(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "MIN":
                return new LimitMax(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "MAX":
                return new LimitMin(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "ROR":
                return new Ror(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "ROL":
                return new Rol(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "REV":
                return new Rev(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "SCA":
                return new Sca(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "SCAS":
                return new Scas(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
            case "FRAC":
                return new Frac(buildConstantExpression(context, node.getChild(0), registerConstant), buildConstantExpression(context, node.getChild(1), registerConstant));
        }

        throw new RuntimeException("unknown " + node.getText());
    }

    List<Spin2Bytecode> leftAssign(Context context, Spin2Method method, Spin2StatementNode node, boolean push, boolean write) {
        Spin2StatementNode indexNode = null;
        Spin2StatementNode bitfieldNode = null;
        Spin2StatementNode postEffectNode = null;
        int index = 0;
        boolean popIndex = false;
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        if (node.getText().startsWith("^@")) {
            throw new CompilerException("syntax error", node.getToken());
        }

        if ("*".equals(node.getText()) && node.getChildCount() == 1) {
            source.addAll(compilePointerDereference(context, method, node.getChild(0), push ? MemoryOp.Op.Setup : MemoryOp.Op.Write));
        }
        else if ("_".equals(node.getText())) {
            source.add(new Bytecode(context, Spin2Bytecode.bc_pop, "POP"));
        }
        else if (",".equals(node.getText())) {
            for (int i = node.getChildCount() - 1; i >= 0; i--) {
                source.addAll(leftAssign(context, method, node.getChild(i), push, false));
            }
        }
        else if (node.getType() == Token.OPERATOR) {
            source.addAll(leftAssign(context, method, node.getChild(1), true, true));
            source.addAll(leftAssign(context, method, node.getChild(0), node.getChild(0).getType() == Token.OPERATOR, false));
        }
        else if ("BYTE".equals(node.getText()) || "WORD".equals(node.getText()) || "LONG".equals(node.getText())) {
            int n = 1;
            if (n < node.getChildCount()) {
                if (".".equals(node.getChild(n).getText())) {
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new CompilerException("expected bitfield expression", node.getToken());
                    }
                    bitfieldNode = node.getChild(n++);
                }
                else if (!isPostEffect(node.getChild(n))) {
                    indexNode = node.getChild(n++);
                }
            }
            if (n < node.getChildCount()) {
                if (".".equals(node.getChild(n).getText())) {
                    if (bitfieldNode != null) {
                        throw new CompilerException("syntax error", node.getToken());
                    }
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new CompilerException("expected bitfield expression", node.getToken());
                    }
                    bitfieldNode = node.getChild(n++);
                }
            }
            if (n < node.getChildCount()) {
                if (!isPostEffect(node.getChild(n))) {
                    if (indexNode != null) {
                        throw new CompilerException("syntax error", node.getToken());
                    }
                    indexNode = node.getChild(n++);
                }
            }
            if (n < node.getChildCount()) {
                throw new RuntimeException("syntax error " + node.getText());
            }

            int bitfield = -1;
            if (bitfieldNode != null) {
                bitfield = compileBitfield(context, method, bitfieldNode, source);
            }

            source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
            if (indexNode != null) {
                source.addAll(compileBytecodeExpression(context, method, indexNode, true));
            }

            MemoryOp.Size ss = MemoryOp.Size.Long;
            if ("BYTE".equals(node.getText())) {
                ss = MemoryOp.Size.Byte;
            }
            else if ("WORD".equals(node.getText())) {
                ss = MemoryOp.Size.Word;
            }
            MemoryOp.Op op = push || bitfieldNode != null ? MemoryOp.Op.Setup : MemoryOp.Op.Write;
            source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, op, indexNode != null));

            if (bitfieldNode != null) {
                source.add(new BitField(context, push && !write ? BitField.Op.Setup : BitField.Op.Write, push, bitfield));
            }
            else if (write) {
                source.add(new Bytecode(context, Spin2Bytecode.bc_write_push, "WRITE"));
            }
        }
        else if ("REG".equals(node.getText())) {
            int n = 1;
            if (n < node.getChildCount()) {
                if (".".equals(node.getChild(n).getText())) {
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new CompilerException("expected bitfield expression", node.getToken());
                    }
                    bitfieldNode = node.getChild(n++);
                }
                else if (!isPostEffect(node.getChild(n))) {
                    indexNode = node.getChild(n++);
                }
            }
            if (n < node.getChildCount()) {
                if (".".equals(node.getChild(n).getText())) {
                    if (bitfieldNode != null) {
                        throw new CompilerException("syntax error", node.getToken());
                    }
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new CompilerException("expected bitfield expression", node.getToken());
                    }
                    bitfieldNode = node.getChild(n++);
                }
            }
            if (n < node.getChildCount()) {
                if (!isPostEffect(node.getChild(n))) {
                    if (indexNode != null) {
                        throw new CompilerException("syntax error", node.getToken());
                    }
                    indexNode = node.getChild(n++);
                }
            }
            if (n < node.getChildCount()) {
                throw new RuntimeException("syntax error " + node.getText());
            }

            int bitfield = -1;
            if (bitfieldNode != null) {
                bitfield = compileBitfield(context, method, bitfieldNode, source);
            }

            Expression expression = null;
            try {
                expression = buildConstantExpression(context, node.getChild(0), true);
            } catch (Exception e) {
                // Do nothing
            }
            if (expression == null) {
                throw new CompilerException("expected constant expression", node.getChild(0).getToken());
            }

            index = 0;
            popIndex = indexNode != null;
            if (indexNode != null) {
                try {
                    Expression exp = buildConstantExpression(context, indexNode);
                    if (exp.isConstant()) {
                        index = exp.getNumber().intValue();
                        popIndex = false;
                    }
                } catch (Exception e) {
                    // Do nothing
                }
                if (popIndex) {
                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                }
            }

            source.add(new RegisterOp(context, bitfieldNode != null || push ? RegisterOp.Op.Setup : RegisterOp.Op.Write, popIndex, expression, index));

            if (bitfieldNode != null) {
                source.add(new BitField(context, push && !write ? BitField.Op.Setup : BitField.Op.Write, push, bitfield));
            }
            else if (write) {
                source.add(new Bytecode(context, Spin2Bytecode.bc_write_push, "WRITE"));
            }
        }
        else if ("FIELD".equals(node.getText()) && node.getChildCount() != 0) {
            int n = 1;
            if (n < node.getChildCount()) {
                indexNode = node.getChild(n++);
            }
            if (n < node.getChildCount()) {
                throw new RuntimeException("syntax error " + node.getText());
            }

            source.addAll(compileConstantExpression(context, method, node.getChild(0)));

            if (indexNode != null) {
                source.addAll(compileConstantExpression(context, method, indexNode));
            }

            if (push && !write) {
                source.add(new Bytecode(context, new byte[] {
                    (byte) (indexNode == null ? Spin2Bytecode.bc_setup_field_p : Spin2Bytecode.bc_setup_field_pi),
                }, "FIELD_SETUP"));
            }
            else {
                source.add(new Bytecode(context, new byte[] {
                    (byte) (indexNode == null ? Spin2Bytecode.bc_setup_field_p : Spin2Bytecode.bc_setup_field_pi),
                    (byte) (push ? Spin2Bytecode.bc_write_push : Spin2Bytecode.bc_write)
                }, push ? "FIELD_WRITE (push)" : "FIELD_WRITE"));
            }
        }
        else {
            String[] ar = node.getText().split("[\\.]");

            Expression expression = context.getLocalSymbol(ar[0]);
            if (expression == null) {
                throw new CompilerException("undefined symbol " + node.getText(), node.getToken());
            }

            if (expression instanceof Variable) {
                if (isStructure(context, expression)) {
                    return compileStructure(context, method, node, expression, null, MemoryOp.Op.Write, push);
                }
            }

            if (ar.length >= 2 && ("BYTE".equalsIgnoreCase(ar[ar.length - 1]) || "WORD".equalsIgnoreCase(ar[ar.length - 1]) || "LONG".equalsIgnoreCase(ar[ar.length - 1]))) {
                int n = 0;
                if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
                    indexNode = node.getChild(n++);
                }
                if (n < node.getChildCount() && ".".equals(node.getChild(n).getText())) {
                    n++;
                    if (n >= node.getChildCount()) {
                        throw new RuntimeException("expected bitfield expression");
                    }
                    if (!(node.getChild(n) instanceof Spin2StatementNode.Index)) {
                        throw new RuntimeException("syntax error");
                    }
                    bitfieldNode = node.getChild(n++);
                }
                if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
                    postEffectNode = node.getChild(n++);
                }
                if (n < node.getChildCount()) {
                    throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
                }

                int bitfield = -1;
                if (bitfieldNode != null) {
                    bitfield = compileBitfield(context, method, bitfieldNode, source);
                }

                if (indexNode != null) {
                    popIndex = true;
                    try {
                        Expression exp = buildConstantExpression(context, indexNode);
                        if (exp.isConstant()) {
                            index = exp.getNumber().intValue();
                            popIndex = false;
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }
                    if (popIndex) {
                        source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                    }
                }

                MemoryOp.Size ss = MemoryOp.Size.Long;
                if ("BYTE".equals(ar[1])) {
                    ss = MemoryOp.Size.Byte;
                }
                else if ("WORD".equals(ar[1])) {
                    ss = MemoryOp.Size.Word;
                }
                MemoryOp.Base bb = MemoryOp.Base.PBase;
                if (expression instanceof Variable) {
                    bb = expression instanceof LocalVariable ? MemoryOp.Base.DBase : MemoryOp.Base.VBase;
                    ((Variable) expression).setCalledBy(method);
                }

                MemoryOp.Op op = push || bitfieldNode != null ? MemoryOp.Op.Setup : MemoryOp.Op.Write;
                source.add(new MemoryOp(context, ss, bb, op, popIndex, expression, index));

                if (bitfieldNode != null) {
                    source.add(new BitField(context, postEffectNode == null ? BitField.Op.Write : BitField.Op.Setup, bitfield));
                }

                if (postEffectNode != null) {
                    if ("++".equals(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postinc_push : Spin2Bytecode.bc_var_inc, "POST_INC" + (push ? " (push)" : "")));
                    }
                    else if ("--".equals(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postdec_push : Spin2Bytecode.bc_var_dec, "POST_DEC" + (push ? " (push)" : "")));
                    }
                    else {
                        throw new CompilerException("unhandled post effect " + postEffectNode.getText(), postEffectNode.getToken());
                    }
                }
            }

            if ((expression instanceof Variable) && ((Variable) expression).isPointer() && node.getChildCount() != 0) {
                int n = 0;
                if (n < node.getChildCount()) {
                    if (".".equals(node.getChild(n).getText())) {
                        n++;
                        if (n >= node.getChildCount()) {
                            throw new CompilerException("expected bitfield expression", node.getToken());
                        }
                        bitfieldNode = node.getChild(n++);
                    }
                    else if (!isPostEffect(node.getChild(n))) {
                        indexNode = node.getChild(n++);
                    }
                }
                if (n < node.getChildCount()) {
                    if (".".equals(node.getChild(n).getText())) {
                        if (bitfieldNode != null) {
                            throw new CompilerException("syntax error", node.getToken());
                        }
                        n++;
                        if (n >= node.getChildCount()) {
                            throw new CompilerException("expected bitfield expression", node.getToken());
                        }
                        bitfieldNode = node.getChild(n++);
                    }
                }
                if (n < node.getChildCount()) {
                    if (!isPostEffect(node.getChild(n))) {
                        if (indexNode != null) {
                            throw new CompilerException("syntax error", node.getToken());
                        }
                        indexNode = node.getChild(n++);
                    }
                }
                if (n < node.getChildCount()) {
                    throw new RuntimeException("syntax error " + node.getText());
                }

                int bitfield = -1;
                if (bitfieldNode != null) {
                    bitfield = compileBitfield(context, method, bitfieldNode, source);
                }

                source.add(new VariableOp(context, VariableOp.Op.Read, false, (Variable) expression, false, 0));
                if (indexNode != null) {
                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                }

                MemoryOp.Size ss = MemoryOp.Size.Long;
                if ("byte".equals(((Variable) expression).getPointerType())) {
                    ss = MemoryOp.Size.Byte;
                }
                else if ("word".equals(((Variable) expression).getPointerType())) {
                    ss = MemoryOp.Size.Word;
                }
                MemoryOp.Op op = push || bitfieldNode != null ? MemoryOp.Op.Setup : MemoryOp.Op.Write;
                source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, op, indexNode != null));

                if (bitfieldNode != null) {
                    source.add(new BitField(context, push && !write ? BitField.Op.Setup : BitField.Op.Write, push, bitfield));
                }
            }
            else if (isStructure(context, expression)) {
                return compileStructure(context, method, node, expression, null, MemoryOp.Op.Write, push);
            }
            else {
                boolean hasIndex = false;

                int n = 0;
                if (n < node.getChildCount()) {
                    if (".".equals(node.getChild(n).getText())) {
                        n++;
                        if (n >= node.getChildCount()) {
                            throw new CompilerException("expected bitfield expression", node.getToken());
                        }
                        bitfieldNode = node.getChild(n++);
                    }
                    else {
                        indexNode = node.getChild(n++);
                        popIndex = true;
                        try {
                            Expression exp = buildConstantExpression(context, indexNode);
                            if (exp.isConstant()) {
                                index = exp.getNumber().intValue();
                                hasIndex = true;
                                popIndex = false;
                            }
                        } catch (Exception e) {
                            // Do nothing
                        }
                        if (n < node.getChildCount()) {
                            if (".".equals(node.getChild(n).getText())) {
                                n++;
                                if (n >= node.getChildCount()) {
                                    throw new CompilerException("expected bitfield expression", node.getToken());
                                }
                                bitfieldNode = node.getChild(n++);
                            }
                        }
                    }
                }
                if (n < node.getChildCount()) {
                    throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
                }

                int bitfield = -1;
                if (bitfieldNode != null) {
                    bitfield = compileBitfield(context, method, bitfieldNode, source);
                }

                if (popIndex) {
                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                }

                if (expression instanceof Register) {
                    source.add(new RegisterOp(context, bitfieldNode != null || push ? RegisterOp.Op.Setup : RegisterOp.Op.Write, popIndex, expression, index));
                }
                else if (expression instanceof ContextLiteral) {
                    MemoryOp.Size ss = MemoryOp.Size.Long;
                    MemoryOp.Base bb = MemoryOp.Base.PBase;
                    if (expression instanceof Variable) {
                        bb = expression instanceof LocalVariable ? MemoryOp.Base.DBase : MemoryOp.Base.VBase;
                        ((Variable) expression).setCalledBy(method);
                    }
                    if (expression instanceof DataVariable) {
                        switch (((DataVariable) expression).getType()) {
                            case "byte":
                                ss = MemoryOp.Size.Byte;
                                break;
                            case "word":
                                ss = MemoryOp.Size.Word;
                                break;
                        }
                    }

                    source.add(new MemoryOp(context, ss, bb, bitfieldNode != null || push ? MemoryOp.Op.Setup : MemoryOp.Op.Write, popIndex, expression, index));
                }
                else {
                    source.add(new VariableOp(context, bitfieldNode != null || push ? VariableOp.Op.Setup : VariableOp.Op.Write, popIndex, (Variable) expression, hasIndex, index));
                    ((Variable) expression).setCalledBy(method);
                }

                if (bitfieldNode != null) {
                    source.add(new BitField(context, push && !write ? BitField.Op.Setup : BitField.Op.Write, push, bitfield));
                }
                else if (write) {
                    source.add(new Bytecode(context, Spin2Bytecode.bc_write_push, "WRITE"));
                }
            }
        }

        return source;
    }

    List<Spin2Bytecode> compileMethodCall(Context context, Spin2Method method, Expression symbol, Spin2StatementNode node, boolean push, boolean trap) {
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        if (trap) {
            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_drop_trap_push : Spin2Bytecode.bc_drop_trap, "ANCHOR_TRAP"));
        }
        else {
            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_drop_push : Spin2Bytecode.bc_drop, "ANCHOR"));
        }

        if (symbol instanceof Method) {
            Method methodExpression = (Method) symbol;
            int expected = methodExpression.getArgumentsCount();
            int actual = getArgumentsCount(context, node);
            if (expected != actual) {
                throw new RuntimeException("expected " + expected + " argument(s), found " + actual);
            }
            if (push && !trap && methodExpression.getReturnLongs() == 0) {
                throw new RuntimeException("method doesn't return any value");
            }

            if (methodExpression.getArgumentsCount() != 0) {
                Spin2Method targetMethod = (Spin2Method) methodExpression.getData(Spin2Method.class.getName());

                int p = 0;
                for (int i = 0; i < node.getChildCount(); i++) {
                    LocalVariable parameter = targetMethod.getParameters().get(p++);
                    boolean leftIsFloat = "float".equals(parameter.getType());
                    boolean rightIsFloat = isFloat(context, node.getChild(i));

                    if (leftIsFloat && !rightIsFloat) {
                        Spin2StatementNode exp = new Spin2StatementNode(new Token(Token.KEYWORD, "float"), true);
                        exp.getChilds().add(node.getChild(i));
                        source.addAll(compileConstantExpression(context, method, exp));
                    }
                    else if (!leftIsFloat && rightIsFloat) {
                        logMessage(new CompilerException(CompilerException.WARNING, "float to integer conversion", node.getChild(i).getTokens()));
                        Spin2StatementNode exp = new Spin2StatementNode(new Token(Token.KEYWORD, "round"), true);
                        exp.getChilds().add(node.getChild(i));
                        source.addAll(compileConstantExpression(context, method, exp));
                    }
                    else {
                        source.addAll(compileConstantExpression(context, method, node.getChild(i)));
                    }
                }
            }

            source.add(new CallSub(context, methodExpression));
            Spin2Method calledMethod = (Spin2Method) methodExpression.getData(Spin2Method.class.getName());
            calledMethod.setCalledBy(method);
        }
        else {
            int i = 0;
            Spin2StatementNode indexNode = null;
            if (i < node.getChildCount()) {
                if (node.getChild(i) instanceof Spin2StatementNode.Index) {
                    indexNode = node.getChild(i++);
                }
            }
            while (i < node.getChildCount()) {
                if (node.getChild(i) instanceof Spin2StatementNode.Index) {
                    throw new CompilerException("syntax error", node.getChild(i));
                }
                source.addAll(compileConstantExpression(context, method, node.getChild(i++)));
            }

            int index = 0;
            boolean hasIndex = false;
            boolean popIndex = indexNode != null;

            if (indexNode != null) {
                try {
                    Expression exp = buildConstantExpression(context, indexNode);
                    if (exp.isConstant()) {
                        index = exp.getNumber().intValue();
                        hasIndex = true;
                        popIndex = false;
                    }
                } catch (Exception e) {
                    // Do nothing
                }
                if (popIndex) {
                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                }
            }

            if (symbol instanceof Variable) {
                switch (((Variable) symbol).getType()) {
                    case "BYTE":
                    case "WORD":
                        throw new RuntimeException("method pointers must be long");
                }
                source.add(new VariableOp(context, VariableOp.Op.Read, popIndex, (Variable) symbol, hasIndex, index));
                ((Variable) symbol).setCalledBy(method);
            }
            else if (symbol instanceof DataVariable) {
                switch (((DataVariable) symbol).getType()) {
                    case "BYTE":
                    case "WORD":
                        throw new RuntimeException("method pointers must be long");
                }
                source.add(new MemoryOp(context, MemoryOp.Size.Long, MemoryOp.Base.PBase, MemoryOp.Op.Read, popIndex, symbol, index));
            }
            else {
                throw new CompilerException("unsupported operation on " + node.getText(), node.getToken());
            }

            source.add(new Bytecode(context, new byte[] {
                (byte) Spin2Bytecode.bc_call_ptr,
            }, "CALL_PTR"));
        }

        return source;
    }

    int compileBitfield(Context context, Spin2Method method, Spin2StatementNode node, List<Spin2Bytecode> source) {
        int bitfield = -1;

        if ("..".equals(node.getText())) {
            if (node.getChildCount() != 2) {
                throw new RuntimeException("syntax error");
            }
            try {
                Expression exp1 = buildConstantExpression(context, node.getChild(0));
                Expression exp2 = buildConstantExpression(context, node.getChild(1));
                if (exp1.isConstant() && exp2.isConstant()) {
                    int lowest = Math.min(exp1.getNumber().intValue(), exp2.getNumber().intValue());
                    int highest = Math.max(exp1.getNumber().intValue(), exp2.getNumber().intValue());
                    Expression exp = new Addbits(new NumberLiteral(lowest), new NumberLiteral(highest - lowest));
                    bitfield = exp.getNumber().intValue();
                }
            } catch (Exception e) {
                // Do nothing
            }

            if (bitfield == -1) {
                source.addAll(compileBytecodeExpression(context, method, node.getChild(0), true));
                source.addAll(compileBytecodeExpression(context, method, node.getChild(1), true));
                source.add(new Bytecode(context, new byte[] {
                    (byte) Spin2Bytecode.bc_bitrange, (byte) Spin2Bytecode.bc_addbits
                }, "ADDBITS"));
            }
        }
        else {
            try {
                Expression exp = buildConstantExpression(context, node);
                if (exp.isConstant()) {
                    bitfield = exp.getNumber().intValue();
                }
            } catch (Exception e) {
                // Do nothing
            }

            if (bitfield == -1) {
                source.addAll(compileBytecodeExpression(context, method, node, true));
            }
        }

        return bitfield;
    }

    List<Spin2Bytecode> compileVariableSetup(Context context, Spin2Method method, Expression expression, Spin2StatementNode node) {
        Spin2StatementNode indexNode = null;
        Spin2StatementNode bitfieldNode = null;
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        MemoryOp.Size ss = MemoryOp.Size.Long;
        MemoryOp.Base bb = MemoryOp.Base.PBase;
        if (expression instanceof DataVariable) {
            switch (((DataVariable) expression).getType()) {
                case "BYTE":
                    ss = MemoryOp.Size.Byte;
                    break;
                case "WORD":
                    ss = MemoryOp.Size.Word;
                    break;
            }
        }

        int n = 0;
        if (n < node.getChildCount()) {
            if (!".".equals(node.getChild(n).getText()) && !isPostEffect(node.getChild(n))) {
                indexNode = node.getChild(n++);
                if ("..".equals(indexNode.getText())) {
                    bitfieldNode = indexNode;
                    indexNode = null;
                }
            }
        }
        if (n < node.getChildCount()) {
            if (".".equals(node.getChild(n).getText())) {
                if (bitfieldNode != null) {
                    throw new CompilerException("invalid bitfield expression", node.getToken());
                }
                n++;
                if (n >= node.getChildCount()) {
                    throw new CompilerException("expected bitfield expression", node.getToken());
                }
                bitfieldNode = node.getChild(n++);
            }
        }
        if (n < node.getChildCount()) {
            throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n).getToken());
        }

        int index = 0;
        boolean hasIndex = false;
        boolean popIndex = false;

        if (indexNode != null) {
            popIndex = true;
            try {
                Expression exp = buildConstantExpression(context, indexNode);
                if (exp.isConstant()) {
                    index = exp.getNumber().intValue();
                    hasIndex = true;
                    popIndex = false;
                }
            } catch (Exception e) {
                // Do nothing
            }
        }

        if (bitfieldNode != null) {
            int bitfield = compileBitfield(context, method, bitfieldNode, source);

            if (popIndex) {
                source.addAll(compileBytecodeExpression(context, method, indexNode, true));
            }

            if (expression instanceof Register) {
                source.add(new RegisterOp(context, RegisterOp.Op.Setup, popIndex, expression, index));
            }
            else if (expression instanceof ContextLiteral) {
                source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Setup, popIndex, expression, index));
            }
            else {
                source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, (Variable) expression, hasIndex, index));
                ((Variable) expression).setCalledBy(method);
            }

            source.add(new BitField(context, BitField.Op.Setup, bitfield));
        }
        else {
            if (popIndex) {
                source.addAll(compileBytecodeExpression(context, method, indexNode, true));
            }
            if (expression instanceof Register) {
                source.add(new RegisterOp(context, RegisterOp.Op.Setup, popIndex, expression, index));
            }
            else if (expression instanceof ContextLiteral) {
                source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Setup, popIndex, expression, index));
            }
            else {
                source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, (Variable) expression, hasIndex, index));
                ((Variable) expression).setCalledBy(method);
            }
        }

        return source;
    }

    List<Spin2Bytecode> compileVariableRead(Context context, Spin2Method method, Expression expression, Spin2StatementNode node, boolean push) {
        Spin2StatementNode indexNode = null;
        Spin2StatementNode bitfieldNode = null;
        Spin2StatementNode postEffectNode = null;
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        MemoryOp.Size ss = MemoryOp.Size.Long;
        MemoryOp.Base bb = MemoryOp.Base.PBase;
        if (expression instanceof DataVariable) {
            switch (((DataVariable) expression).getType()) {
                case "byte":
                    ss = MemoryOp.Size.Byte;
                    break;
                case "word":
                    ss = MemoryOp.Size.Word;
                    break;
            }
        }
        else if (expression instanceof Variable) {
            if ("byte".equals(((Variable) expression).getPointerType())) {
                ss = MemoryOp.Size.Byte;
            }
            else if ("word".equals(((Variable) expression).getPointerType())) {
                ss = MemoryOp.Size.Word;
            }
        }

        boolean field = node.getText().startsWith("^@");

        int n = 0;
        if (n < node.getChildCount() && (node.getChild(n) instanceof Spin2StatementNode.Index)) {
            indexNode = node.getChild(n++);
        }
        if (n < node.getChildCount() && ".".equals(node.getChild(n).getText())) {
            n++;
            if (n >= node.getChildCount()) {
                throw new CompilerException("expected bitfield expression", node.getChild(n - 1));
            }
            if (!(node.getChild(n) instanceof Spin2StatementNode.Index)) {
                throw new CompilerException("invalid bitfield expression", node.getChild(n));
            }
            bitfieldNode = node.getChild(n++);
        }
        if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
            postEffectNode = node.getChild(n++);
        }
        if (n < node.getChildCount()) {
            throw new CompilerException("unexpected " + node.getChild(n).getText(), node.getChild(n));
        }

        if ((expression instanceof Variable) && ((Variable) expression).isPointer()) {
            Variable variable = (Variable) expression;

            source.add(new VariableOp(context, VariableOp.Op.Read, variable));

            if (bitfieldNode != null) {
                throw new CompilerException("unsupported bitfield operation", bitfieldNode.getTokens());
            }

            if (indexNode != null) {
                source.addAll(compileConstantExpression(context, method, indexNode));
                if (postEffectNode != null) {
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Setup, true));
                    if ("++".equals(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postinc_push : Spin2Bytecode.bc_var_inc, "POST_INC" + (push ? " (push)" : "")));
                    }
                    else if ("--".equals(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postdec_push : Spin2Bytecode.bc_var_dec, "POST_DEC" + (push ? " (push)" : "")));
                    }
                    else {
                        throw new CompilerException("unsupported post effect " + postEffectNode.getText(), postEffectNode.getToken());
                    }
                }
                else {
                    source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, push ? MemoryOp.Op.Read : MemoryOp.Op.Write, true));
                }
            }
            else if (postEffectNode != null) {
                source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, MemoryOp.Op.Setup, true));
                if (variable.getPointerSize() == 1) {
                    if ("++".equals(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postinc_push : Spin2Bytecode.bc_var_inc, "POST_INC" + (push ? " (push)" : "")));
                    }
                    else if ("--".equals(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postdec_push : Spin2Bytecode.bc_var_dec, "POST_DEC" + (push ? " (push)" : "")));
                    }
                    else {
                        throw new CompilerException("unsupported post effect " + postEffectNode.getText(), postEffectNode.getToken());
                    }
                }
                else {
                    source.add(new Constant(context, new NumberLiteral(variable.getPointerSize())));
                    source.add(new VariableOp(context, VariableOp.Op.Setup, variable));
                    if ("++".equals(postEffectNode.getText())) {
                        Descriptor desc = assignOperators.get("+=");
                        source.add(new Bytecode(context, desc.value, desc.text));
                    }
                    else if ("--".equals(postEffectNode.getText())) {
                        Descriptor desc = assignOperators.get("-=");
                        source.add(new Bytecode(context, desc.value, desc.text));
                    }
                    else {
                        throw new CompilerException("unsupported post effect " + postEffectNode.getText(), postEffectNode.getToken());
                    }
                }
            }

            variable.setCalledBy(method);
        }
        else {
            int index = 0;
            boolean hasIndex = false;
            boolean popIndex = false;

            if (indexNode != null) {
                popIndex = true;
                try {
                    Expression exp = buildConstantExpression(context, indexNode);
                    if (exp.isConstant()) {
                        index = exp.getNumber().intValue();
                        hasIndex = true;
                        popIndex = false;
                    }
                } catch (Exception e) {
                    // Do nothing
                }
            }

            if (bitfieldNode != null) {
                int bitfield = compileBitfield(context, method, bitfieldNode, source);

                if (popIndex) {
                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                }

                if (expression instanceof Register) {
                    source.add(new RegisterOp(context, RegisterOp.Op.Setup, popIndex, expression, index));
                }
                else if (expression instanceof ContextLiteral) {
                    source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Setup, popIndex, expression, index));
                }
                else {
                    source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, (Variable) expression, hasIndex, index));
                    ((Variable) expression).setCalledBy(method);
                }

                BitField.Op op = field ? BitField.Op.Field : (postEffectNode == null ? BitField.Op.Read : BitField.Op.Setup);
                source.add(new BitField(context, op, bitfield));

                if (postEffectNode != null) {
                    source.addAll(compilePostEffect(context, postEffectNode, push));
                }
            }
            else {
                if (popIndex) {
                    source.addAll(compileBytecodeExpression(context, method, indexNode, true));
                }
                if (postEffectNode != null) {
                    if (expression instanceof Register) {
                        source.add(new RegisterOp(context, RegisterOp.Op.Setup, popIndex, expression, index));
                    }
                    else if (expression instanceof ContextLiteral) {
                        source.add(new MemoryOp(context, ss, bb, MemoryOp.Op.Setup, popIndex, expression, index));
                    }
                    else {
                        Variable variable = (Variable) expression;
                        source.add(new VariableOp(context, VariableOp.Op.Setup, popIndex, variable, hasIndex, index));
                        variable.setCalledBy(method);
                    }
                    if ("++".equals(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postinc_push : Spin2Bytecode.bc_var_inc, "POST_INC" + (push ? " (push)" : "")));
                    }
                    else if ("--".equals(postEffectNode.getText())) {
                        source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postdec_push : Spin2Bytecode.bc_var_dec, "POST_DEC" + (push ? " (push)" : "")));
                    }
                    else {
                        throw new CompilerException("unsupported post effect " + postEffectNode.getText(), postEffectNode.getToken());
                    }
                }
                else {
                    if (expression instanceof Register) {
                        source.add(new RegisterOp(context, field ? RegisterOp.Op.Field : RegisterOp.Op.Read, popIndex, expression, index));
                    }
                    else if (expression instanceof ContextLiteral) {
                        source.add(new MemoryOp(context, ss, bb, field ? MemoryOp.Op.Field : MemoryOp.Op.Read, popIndex, expression, index));
                    }
                    else {
                        Variable variable = (Variable) expression;
                        source.add(new VariableOp(context, field ? VariableOp.Op.Field : VariableOp.Op.Read, popIndex, variable, hasIndex, index));
                        variable.setCalledBy(method);
                    }
                }
            }
        }

        return source;
    }

    List<Spin2Bytecode> compilePointerDereference(Context context, Spin2Method method, Spin2StatementNode node, MemoryOp.Op op) {
        Spin2StatementNode postEffectNode = null;
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

        Expression expression = context.getLocalSymbol(node.getText());
        if (expression == null) {
            throw new CompilerException("undefined symbol " + node.getText(), node.getTokens());
        }
        if (!(expression instanceof Variable)) {
            throw new CompilerException("unsupported", node.getTokens());
        }

        Variable variable = (Variable) expression;
        if (!variable.isPointer()) {
            logMessage(new CompilerException(CompilerException.WARNING, "not a pointer", node.getTokens()));
        }
        variable.setCalledBy(method);

        int n = 0;
        if (n < node.getChildCount() && isPostEffect(node.getChild(n))) {
            postEffectNode = node.getChild(n++);
        }
        if (n < node.getChildCount()) {
            throw new CompilerException("syntax error", node.getChild(n).getTokens());
        }

        if (postEffectNode != null && variable.getPointerSize() == 1) {
            source.add(new VariableOp(context, VariableOp.Op.Setup, variable));
            source.addAll(compilePostEffect(context, postEffectNode, true));
        }
        else {
            source.add(new VariableOp(context, VariableOp.Op.Read, variable));
            if (postEffectNode != null) {
                source.add(new Constant(context, new NumberLiteral(variable.getPointerSize())));
                source.add(new VariableOp(context, VariableOp.Op.Setup, variable));
                if ("++".equals(postEffectNode.getText())) {
                    Descriptor desc = assignOperators.get("+=");
                    source.add(new Bytecode(context, desc.value, desc.text));
                }
                else if ("--".equals(postEffectNode.getText())) {
                    Descriptor desc = assignOperators.get("-=");
                    source.add(new Bytecode(context, desc.value, desc.text));
                }
                else {
                    throw new CompilerException("unsupported post effect " + postEffectNode.getText(), postEffectNode.getToken());
                }
            }
        }

        MemoryOp.Size ss = MemoryOp.Size.Long;
        if ("byte".equals(variable.getPointerType())) {
            ss = MemoryOp.Size.Byte;
        }
        else if ("word".equals(variable.getPointerType())) {
            ss = MemoryOp.Size.Word;
        }
        source.add(new MemoryOp(context, ss, MemoryOp.Base.Pop, op, false));

        return source;
    }

    boolean isPostEffect(Spin2StatementNode node) {
        if (node.getChildCount() != 0) {
            return false;
        }
        String s = node.getText();
        return "++".equals(s) || "--".equals(s);
    }

    List<Spin2Bytecode> compilePostEffect(Context context, Spin2StatementNode node, boolean push) {
        List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();
        if ("++".equals(node.getText())) {
            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postinc_push : Spin2Bytecode.bc_var_inc, "POST_INC" + (push ? " (push)" : "")));
        }
        else if ("--".equals(node.getText())) {
            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postdec_push : Spin2Bytecode.bc_var_dec, "POST_DEC" + (push ? " (push)" : "")));
        }
        else {
            throw new CompilerException("unhandled post effect " + node.getText(), node.getToken());
        }
        return source;
    }

    boolean isFloat(Context context, Spin2StatementNode node) {
        boolean result = false;

        if (node.getType() == Token.NUMBER) {
            Expression expression = new NumberLiteral(node.getText());
            if (expression.isNumber() && (expression.getNumber() instanceof Double)) {
                return true;
            }
        }
        if ("nan".equals(node.getText()) || "round".equals(node.getText()) || "trunc".equals(node.getText())) {
            return false;
        }
        if ("float".equals(node.getText())) {
            return true;
        }
        if ("abs".equals(node.getText()) || "sqrt".equals(node.getText())) {
            return isFloat(context, node.getChild(0));
        }
        if (descriptors.containsKey(node.getText())) {
            return false;
        }

        Expression expression = context.getLocalSymbol(node.getText());
        if (expression != null) {
            if (expression.isNumber() && (expression.getNumber() instanceof Double)) {
                return true;
            }
            if (expression instanceof Variable) {
                if ("float".equals(((Variable) expression).getType())) {
                    return true;
                }
            }
            if (expression instanceof Method) {
                Spin2Method method = (Spin2Method) expression.getData(Spin2Method.class.getName());
                if (method.getReturnLongs() == 1 && "float".equals(method.getReturns().get(0).getType())) {
                    return true;
                }
            }
        }

        for (Spin2StatementNode child : node.getChilds()) {
            result |= isFloat(context, child);
        }

        return result;
    }

    List<Spin2Bytecode> compileStructure(Context context, Spin2Method method, Spin2StatementNode node, Expression expression, Spin2StatementNode preEffectNode, MemoryOp.Op op, Boolean push) {
        int offset = 0;
        int index = 0;
        int lastMemberSize = 0;
        String varType;
        boolean pointer;
        Spin2Struct struct = null;
        Spin2StatementNode varNode = node;

        List<Spin2StatementNode> indexNodes = new ArrayList<>();
        List<Integer> indexMultipliers = new ArrayList<>();
        Spin2StatementNode postEffectNode = null;

        List<Spin2Bytecode> source = new ArrayList<>();

        if (isAddress(node.getText())) {
            op = MemoryOp.Op.Address;
        }

        MemoryOp.Base bb;
        if (expression instanceof Variable) {
            pointer = ((Variable) expression).isPointer();
            if (pointer) {
                bb = MemoryOp.Base.Pop;
            }
            else {
                bb = expression instanceof LocalVariable ? MemoryOp.Base.DBase : MemoryOp.Base.VBase;
            }
            varType = ((Variable) expression).getType();
        }
        else {
            pointer = false;
            bb = MemoryOp.Base.PBase;
            varType = ((DataVariable) expression).getType();
        }

        int n = 0;
        while (true) {
            struct = context.getStructureDefinition(varType);
            if (struct == null && varType.startsWith("^")) {
                struct = context.getStructureDefinition(varType.substring(1));
            }
            lastMemberSize = struct.getTypeSize();

            String[] ar = varNode.getText().split("[\\.]");
            for (int i = 1; i < ar.length; i++) {
                Spin2StructMember member = struct.getMember(ar[i]);
                if (member == null) {
                    throw new CompilerException("undefined symbol " + varNode.getText(), varNode.getToken());
                }

                offset += member.getOffset();

                varType = member.getType() != null ? member.getType().getText() : "LONG";
                struct = member.getStructureDefinition();

                if (struct != null) {
                    lastMemberSize = struct.getTypeSize();
                }
                else {
                    switch (varType.toUpperCase()) {
                        case "BYTE":
                            lastMemberSize = 1;
                            break;
                        case "WORD":
                            lastMemberSize = 2;
                            break;
                        default:
                            lastMemberSize = 4;
                            break;
                    }
                }
            }

            if (n < varNode.getChildCount() && (varNode.getChild(n) instanceof Spin2StatementNode.Index)) {
                boolean constantIndex = false;
                Spin2StatementNode idxNode = varNode.getChild(n++);
                try {
                    Expression exp = buildConstantExpression(context, idxNode);
                    if (exp.isConstant()) {
                        index += exp.getNumber().intValue() * lastMemberSize;
                        constantIndex = true;
                    }
                } catch (Exception e) {
                    // Do nothing
                }
                if (!constantIndex) {
                    indexNodes.add(idxNode);
                    indexMultipliers.add(0, lastMemberSize);
                }
            }

            if (n >= varNode.getChildCount()) {
                break;
            }
            if (isPostEffect(varNode.getChild(n))) {
                break;
            }

            if (struct == null) {
                break;
            }

            varNode = varNode.getChild(n);
            n = 0;
        }

        if (varNode.isMethod()) {
            if (push == null) {
                push = varNode.getReturnLongs() != 0;
            }
            if (push) {
                source.add(new Bytecode(context, Spin2Bytecode.bc_drop_push, "ANCHOR (push)"));
            }
            else {
                source.add(new Bytecode(context, Spin2Bytecode.bc_drop, "ANCHOR"));
            }

            while (n < varNode.getChildCount()) {
                if (!(varNode.getChild(n) instanceof Spin2StatementNode.Argument)) {
                    throw new CompilerException("syntax error", varNode.getChild(n).getTokens());
                }
                source.addAll(compileConstantExpression(context, method, varNode.getChild(n++)));
            }
        }
        else if (preEffectNode == null) {
            if (n < varNode.getChildCount() && isPostEffect(varNode.getChild(n))) {
                postEffectNode = varNode.getChild(n++);
            }
        }

        if (n < varNode.getChildCount()) {
            throw new CompilerException("unexpected '" + varNode.getChild(n).getText() + "'", varNode.getChild(n).getTokens());
        }

        if (preEffectNode != null && struct != null) {
            throw new CompilerException("syntax error", varNode.getTokens());
        }

        if (varNode.toString().startsWith("[")) {
            source.add(new VariableOp(context, postEffectNode != null ? VariableOp.Op.Setup : VariableOp.Op.Write, false, (Variable) expression, false, 0));
            if (postEffectNode != null) {
                int structSize = struct.getTypeSize();

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try {
                    if ("++".equalsIgnoreCase(postEffectNode.getText()) || "--".equalsIgnoreCase(postEffectNode.getText())) {
                        os.write(Spin2Bytecode.bc_set_incdec);
                        os.write(Constant.wrVar(structSize));
                        if ("++".equalsIgnoreCase(postEffectNode.getText())) {
                            os.write(Spin2Bytecode.bc_var_inc);
                            source.add(new Bytecode(context, os.toByteArray(), "POST_INC"));
                        }
                        else if ("--".equalsIgnoreCase(postEffectNode.getText())) {
                            os.write(Spin2Bytecode.bc_var_dec);
                            source.add(new Bytecode(context, os.toByteArray(), "POST_DEC"));
                        }
                    }
                    else {
                        os.write(Spin2Bytecode.bc_drop);
                        if ("~".equalsIgnoreCase(postEffectNode.getText())) {
                            os.write(Spin2Bytecode.bc_con_n + 1);
                        }
                        else if ("~~".equalsIgnoreCase(postEffectNode.getText())) {
                            os.write(Spin2Bytecode.bc_con_n);
                        }
                        else {
                            logMessage(new CompilerException("invalid post effect '" + postEffectNode.getText() + "'", postEffectNode.getToken()));
                        }
                        os.write(Constant.wrAuto(structSize));
                        os.write(Spin2Bytecode.bc_hub_bytecode);
                        os.write(Spin2Bytecode.bc_bytefill);
                        source.add(new Bytecode(context, os.toByteArray(), "BYTEFILL"));
                    }
                } catch (Exception e) {
                    // Do nothing
                }

            }

            node.setReturnLongs(postEffectNode == null ? 1 : 0);
            return source;
        }

        MemoryOp.Size ss;
        switch (varType.toUpperCase()) {
            case "BYTE":
                ss = MemoryOp.Size.Byte;
                break;
            case "WORD":
                ss = MemoryOp.Size.Word;
                break;
            default:
                ss = MemoryOp.Size.Long;
                break;
        }

        if (indexNodes.size() > 3) {
            throw new CompilerException("too many nested indexes", varNode.getTokens());
        }

        if (!varNode.isMethod()) {
            node.setReturnLongs(struct != null ? (struct.getTypeSize() + 3) / 4 : 1);
        }

        if (struct != null || indexNodes.size() > 0 || postEffectNode != null || bb == MemoryOp.Base.Pop) {
            for (Spin2StatementNode idxNode : indexNodes) {
                source.addAll(compileBytecodeExpression(context, method, idxNode, true));
            }

            if (bb == MemoryOp.Base.Pop) {
                source.add(new VariableOp(context, VariableOp.Op.Read, false, (Variable) expression, false, 0));
            }

            source.add(new StructOp(context, postEffectNode != null ? MemoryOp.Op.Setup : op, bb, ss, struct, expression, index + offset, indexMultipliers, push));

            if (op == MemoryOp.Op.Address) {
                node.setReturnLongs(1);
            }
        }
        else if (expression instanceof Variable) {
            int address = ((Variable) expression).getOffset() + index + offset;
            if ("LONG".equalsIgnoreCase(varType) && (address % 4) == 0) {
                VariableOp.Op varOp;
                if (op == MemoryOp.Op.Read) {
                    varOp = VariableOp.Op.Read;
                }
                else if (op == MemoryOp.Op.Write) {
                    varOp = VariableOp.Op.Write;
                }
                else if (op == MemoryOp.Op.Address) {
                    varOp = VariableOp.Op.Address;
                }
                else {
                    varOp = VariableOp.Op.Setup;
                }
                source.add(new VariableOp(context, postEffectNode != null ? VariableOp.Op.Setup : varOp, false, (Variable) expression, false, (index + offset) / 4));
            }
            else {
                source.add(new MemoryOp(context, ss, bb, postEffectNode != null ? MemoryOp.Op.Setup : op, expression, index + offset));
            }
        }
        else {
            source.add(new MemoryOp(context, ss, bb, postEffectNode != null ? MemoryOp.Op.Setup : op, expression, index + offset));
        }

        if (varNode.isMethod()) {
            source.add(new Bytecode(context, new byte[] {
                (byte) Spin2Bytecode.bc_call_ptr,
            }, "CALL_PTR"));

            if (push && node.getReturnLongs() == 0) {
                logMessage(new CompilerException("method doesn't return any value", node.getToken()));
            }
            node.setReturnLongs(push ? node.getReturnLongs() : 0);
        }
        else {
            if (postEffectNode != null) {
                if (struct != null) {
                    int structSize = struct.getTypeSize();
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        os.write(Spin2Bytecode.bc_drop);
                        if ("~".equalsIgnoreCase(postEffectNode.getText())) {
                            os.write(Spin2Bytecode.bc_con_n + 1);
                        }
                        else if ("~~".equalsIgnoreCase(postEffectNode.getText())) {
                            os.write(Spin2Bytecode.bc_con_n);
                        }
                        else {
                            logMessage(new CompilerException("invalid post effect '" + postEffectNode.getText() + "'", postEffectNode.getToken()));
                        }
                        os.write(Constant.wrAuto(structSize));
                        os.write(Spin2Bytecode.bc_hub_bytecode);
                        os.write(Spin2Bytecode.bc_bytefill);

                        node.setReturnLongs(0);
                    } catch (Exception e) {

                    }
                    source.add(new Bytecode(context, os.toByteArray(), "BYTEFILL"));
                }
                else {
                    compilePostEffect(context, postEffectNode, source, push);
                    if (!push) {
                        node.setReturnLongs(0);
                    }
                }
            }
        }

        if (expression instanceof Variable) {
            ((Variable) expression).setCalledBy(method);
        }

        return source;
    }

    void compilePostEffect(Context context, Spin2StatementNode node, List<Spin2Bytecode> source, boolean push) {
        if ("~".equalsIgnoreCase(node.getText())) {
            source.add(0, new Constant(context, new NumberLiteral(0)));
            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_swap : Spin2Bytecode.bc_write, push ? "SWAP" : "WRITE"));
        }
        else if ("~~".equalsIgnoreCase(node.getText())) {
            source.add(0, new Constant(context, new NumberLiteral(-1)));
            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_swap : Spin2Bytecode.bc_write, push ? "SWAP" : "WRITE"));
        }
        else if ("++".equalsIgnoreCase(node.getText())) {
            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postinc_push : Spin2Bytecode.bc_var_inc, "POST_INC" + (push ? " (push)" : "")));
        }
        else if ("--".equalsIgnoreCase(node.getText())) {
            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_postdec_push : Spin2Bytecode.bc_var_dec, "POST_DEC" + (push ? " (push)" : "")));
        }
        else if ("!!".equalsIgnoreCase(node.getText())) {
            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_lognot_push : Spin2Bytecode.bc_var_lognot, "POST_LOGICAL_NOT" + (push ? " (push)" : "")));
        }
        else if ("!".equalsIgnoreCase(node.getText())) {
            source.add(new Bytecode(context, push ? Spin2Bytecode.bc_var_bitnot_push : Spin2Bytecode.bc_var_bitnot, "POST_NOT" + (push ? " (push)" : "")));
        }
        else {
            throw new CompilerException("unhandled post effect " + node.getText(), node.getToken());
        }
    }

    boolean isStructure(Context context, Expression expression) {
        String varType;

        if (expression instanceof Variable) {
            varType = ((Variable) expression).getType();
            if (context.hasStructureDefinition(varType)) {
                return true;
            }
        }

        return false;
    }

}
