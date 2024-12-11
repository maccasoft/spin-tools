/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.HashMap;
import java.util.Map;

import com.maccasoft.propeller.expressions.Context;

public class Spin2Bytecode {

    public static final int bc_drop = 0x00; // main bytecodes
    public static final int bc_drop_push = 0x01;
    public static final int bc_drop_trap = 0x02;
    public static final int bc_drop_trap_push = 0x03;

    public static final int bc_return_results = 0x04;
    public static final int bc_return_args = 0x05;

    public static final int bc_abort_0 = 0x06;
    public static final int bc_abort_arg = 0x07;

    public static final int bc_call_obj_sub = 0x08;
    public static final int bc_call_obji_sub = 0x09;
    public static final int bc_call_sub = 0x0A;
    public static final int bc_call_ptr = 0x0B;
    public static final int bc_call_recv = 0x0C;
    public static final int bc_call_send = 0x0D;
    public static final int bc_call_send_bytes = 0x0E;

    public static final int bc_mptr_obj_sub = 0x0F;
    public static final int bc_mptr_obji_sub = 0x10;
    public static final int bc_mptr_sub = 0x11;

    public static final int bc_jmp = 0x12;
    public static final int bc_jz = 0x13;
    public static final int bc_jnz = 0x14;
    public static final int bc_tjz = 0x15;
    public static final int bc_djnz = 0x16;

    public static final int bc_pop = 0x17;
    public static final int bc_pop_rfvar = 0x18;

    public static final int bc_hub_bytecode = 0x19;

    public static final int bc_case_fast_init = 0x1A;
    public static final int bc_case_fast_done = 0x1B;

    public static final int bc_case_value = 0x1C;
    public static final int bc_case_range = 0x1D;
    public static final int bc_case_done = 0x1E;

    public static final int bc_lookup_value = 0x1F;
    public static final int bc_lookdown_value = 0x20;
    public static final int bc_lookup_range = 0x21;
    public static final int bc_lookdown_range = 0x22;
    public static final int bc_look_done = 0x23;

    public static final int bc_add_pbase = 0x24;

    public static final int bc_coginit = 0x25;
    public static final int bc_coginit_push = 0x26;
    public static final int bc_cogstop = 0x27;
    public static final int bc_cogid = 0x28;

    public static final int bc_locknew = 0x29;
    public static final int bc_lockret = 0x2A;
    public static final int bc_locktry = 0x2B;
    public static final int bc_lockrel = 0x2C;
    public static final int bc_lockchk = 0x2D;

    public static final int bc_cogatn = 0x2E;
    public static final int bc_pollatn = 0x2F;
    public static final int bc_waitatn = 0x30;

    public static final int bc_getrnd = 0x31;
    public static final int bc_getct = 0x32;
    public static final int bc_pollct = 0x33;
    public static final int bc_waitct = 0x34;

    public static final int bc_pinlow = 0x35;
    public static final int bc_pinhigh = 0x36;
    public static final int bc_pintoggle = 0x37;
    public static final int bc_pinfloat = 0x38;

    public static final int bc_wrpin = 0x39;
    public static final int bc_wxpin = 0x3A;
    public static final int bc_wypin = 0x3B;
    public static final int bc_akpin = 0x3C;
    public static final int bc_rdpin = 0x3D;
    public static final int bc_rqpin = 0x3E;
    public static final int bc_tasknext = 0x3F;

    public static final int bc_unused_40 = 0x40;

    public static final int bc_debug = 0x41;

    public static final int bc_con_rfbyte = 0x42;
    public static final int bc_con_rfbyte_not = 0x43;
    public static final int bc_con_rfword = 0x44;
    public static final int bc_con_rfword_not = 0x45;
    public static final int bc_con_rflong = 0x46;
    public static final int bc_con_rfbyte_decod = 0x47;
    public static final int bc_con_rfbyte_decod_not = 0x48;
    public static final int bc_con_rfbyte_bmask = 0x49;
    public static final int bc_con_rfbyte_bmask_not = 0x4A;

    public static final int bc_setup_field_p = 0x4B;
    public static final int bc_setup_field_pi = 0x4C;

    public static final int bc_setup_reg = 0x4D;
    public static final int bc_setup_reg_pi = 0x4E;

    public static final int bc_setup_byte_pbase = 0x4F;
    public static final int bc_setup_byte_vbase = 0x50;
    public static final int bc_setup_byte_dbase = 0x51;
    public static final int bc_setup_byte_pbase_pi = 0x52;
    public static final int bc_setup_byte_vbase_pi = 0x53;
    public static final int bc_setup_byte_dbase_pi = 0x54;

    public static final int bc_setup_word_pbase = 0x55;
    public static final int bc_setup_word_vbase = 0x56;
    public static final int bc_setup_word_dbase = 0x57;
    public static final int bc_setup_word_pbase_pi = 0x58;
    public static final int bc_setup_word_vbase_pi = 0x59;
    public static final int bc_setup_word_dbase_pi = 0x5A;

    public static final int bc_setup_long_pbase = 0x5B;
    public static final int bc_setup_long_vbase = 0x5C;
    public static final int bc_setup_long_dbase = 0x5D;
    public static final int bc_setup_long_pbase_pi = 0x5E;
    public static final int bc_setup_long_vbase_pi = 0x5F;
    public static final int bc_setup_long_dbase_pi = 0x60;

    public static final int bc_setup_byte_pa = 0x61;
    public static final int bc_setup_word_pa = 0x62;
    public static final int bc_setup_long_pa = 0x63;

    public static final int bc_setup_byte_pb_pi = 0x64;
    public static final int bc_setup_word_pb_pi = 0x65;
    public static final int bc_setup_long_pb_pi = 0x66;

    public static final int bc_setup_struct_pbase = 0x67;
    public static final int bc_setup_struct_vbase = 0x68;
    public static final int bc_setup_struct_dbase = 0x69;
    public static final int bc_setup_struct_pop = 0x6A;

    public static final int bc_ternary = 0x6B;

    public static final int bc_lt = 0x6C;
    public static final int bc_ltu = 0x6D;
    public static final int bc_lte = 0x6E;
    public static final int bc_lteu = 0x6F;
    public static final int bc_e = 0x70;
    public static final int bc_ne = 0x71;
    public static final int bc_gte = 0x72;
    public static final int bc_gteu = 0x73;
    public static final int bc_gt = 0x74;
    public static final int bc_gtu = 0x75;
    public static final int bc_ltegt = 0x76;

    public static final int bc_lognot = 0x77;
    public static final int bc_bitnot = 0x78;
    public static final int bc_neg = 0x79;
    public static final int bc_abs = 0x7A;
    public static final int bc_encod = 0x7B;
    public static final int bc_decod = 0x7C;
    public static final int bc_bmask = 0x7D;
    public static final int bc_ones = 0x7E;
    public static final int bc_sqrt = 0x7F;
    public static final int bc_qlog = 0x80;
    public static final int bc_qexp = 0x81;

    public static final int bc_shr = 0x82;
    public static final int bc_shl = 0x83;
    public static final int bc_sar = 0x84;
    public static final int bc_ror = 0x85;
    public static final int bc_rol = 0x86;
    public static final int bc_rev = 0x87;
    public static final int bc_zerox = 0x88;
    public static final int bc_signx = 0x89;
    public static final int bc_add = 0x8A;
    public static final int bc_sub = 0x8B;

    public static final int bc_logand = 0x8C;
    public static final int bc_logxor = 0x8D;
    public static final int bc_logor = 0x8E;
    public static final int bc_bitand = 0x8F;
    public static final int bc_bitxor = 0x90;
    public static final int bc_bitor = 0x91;
    public static final int bc_fge = 0x92;
    public static final int bc_fle = 0x93;
    public static final int bc_addbits = 0x94;
    public static final int bc_addpins = 0x95;

    public static final int bc_mul = 0x96;
    public static final int bc_div = 0x97;
    public static final int bc_divu = 0x98;
    public static final int bc_rem = 0x99;
    public static final int bc_remu = 0x9A;
    public static final int bc_sca = 0x9B;
    public static final int bc_scas = 0x9C;
    public static final int bc_frac = 0x9D;

    public static final int bc_string = 0x9E;
    public static final int bc_bitrange = 0x9F;

    public static final int bc_con_n = 0xA0;
    public static final int bc_setup_reg_1D8_1F8 = 0xB0;
    public static final int bc_setup_var_0_15 = 0xC0;
    public static final int bc_setup_local_0_15 = 0xD0;
    public static final int bc_read_local_0_15 = 0xE0;
    public static final int bc_write_local_0_15 = 0xF0;

    public static final int bc_set_incdec = 0x79;

    public static final int bc_repeat_var_init_n = 0x7A;
    public static final int bc_repeat_var_init_1 = 0x7B;
    public static final int bc_repeat_var_init = 0x7C;
    public static final int bc_repeat_var_loop = 0x7D;

    public static final int bc_get_field = 0x7E;
    public static final int bc_get_addr = 0x7F;
    public static final int bc_read = 0x80;
    public static final int bc_write = 0x81;
    public static final int bc_write_push = 0x82;

    public static final int bc_var_inc = 0x83;
    public static final int bc_var_dec = 0x84;
    public static final int bc_var_preinc_push = 0x85;
    public static final int bc_var_predec_push = 0x86;
    public static final int bc_var_postinc_push = 0x87;
    public static final int bc_var_postdec_push = 0x88;
    public static final int bc_var_lognot = 0x89;
    public static final int bc_var_lognot_push = 0x8A;
    public static final int bc_var_bitnot = 0x8B;
    public static final int bc_var_bitnot_push = 0x8C;
    public static final int bc_var_swap = 0x8D;
    public static final int bc_var_rnd = 0x8E;
    public static final int bc_var_rnd_push = 0x8F;

    public static final int bc_lognot_write = 0x90;
    public static final int bc_bitnot_write = 0x91;
    public static final int bc_neg_write = 0x92;
    public static final int bc_abs_write = 0x93;
    public static final int bc_encod_write = 0x94;
    public static final int bc_decod_write = 0x95;
    public static final int bc_bmask_write = 0x96;
    public static final int bc_ones_write = 0x97;
    public static final int bc_sqrt_write = 0x98;
    public static final int bc_qlog_write = 0x99;
    public static final int bc_qexp_write = 0x9A;

    public static final int bc_shr_write = 0x9B;
    public static final int bc_shl_write = 0x9C;
    public static final int bc_sar_write = 0x9D;
    public static final int bc_ror_write = 0x9E;
    public static final int bc_rol_write = 0x9F;
    public static final int bc_rev_write = 0xA0;
    public static final int bc_zerox_write = 0xA1;
    public static final int bc_signx_write = 0xA2;
    public static final int bc_add_write = 0xA3;
    public static final int bc_sub_write = 0xA4;

    public static final int bc_logand_write = 0xA5;
    public static final int bc_logxor_write = 0xA6;
    public static final int bc_logor_write = 0xA7;
    public static final int bc_bitand_write = 0xA8;
    public static final int bc_bitxor_write = 0xA9;
    public static final int bc_bitor_write = 0xAA;
    public static final int bc_fge_write = 0xAB;
    public static final int bc_fle_write = 0xAC;
    public static final int bc_addbits_write = 0xAD;
    public static final int bc_addpins_write = 0xAE;

    public static final int bc_mul_write = 0xAF;
    public static final int bc_div_write = 0xB0;
    public static final int bc_divu_write = 0xB1;
    public static final int bc_rem_write = 0xB2;
    public static final int bc_remu_write = 0xB3;
    public static final int bc_sca_write = 0xB4;
    public static final int bc_scas_write = 0xB5;
    public static final int bc_frac_write = 0xB6;

    public static final int bc_lognot_write_push = 0xB7;
    public static final int bc_bitnot_write_push = 0xB8;
    public static final int bc_neg_write_push = 0xB9;
    public static final int bc_abs_write_push = 0xBA;
    public static final int bc_encod_write_push = 0xBB;
    public static final int bc_decod_write_push = 0xBC;
    public static final int bc_bmask_write_push = 0xBD;
    public static final int bc_ones_write_push = 0xBE;
    public static final int bc_sqrt_write_push = 0xBF;
    public static final int bc_qlog_write_push = 0xC0;
    public static final int bc_qexp_write_push = 0xC1;

    public static final int bc_shr_write_push = 0xC2;
    public static final int bc_shl_write_push = 0xC3;
    public static final int bc_sar_write_push = 0xC4;
    public static final int bc_ror_write_push = 0xC5;
    public static final int bc_rol_write_push = 0xC6;
    public static final int bc_rev_write_push = 0xC7;
    public static final int bc_zerox_write_push = 0xC8;
    public static final int bc_signx_write_push = 0xC9;
    public static final int bc_add_write_push = 0xCA;
    public static final int bc_sub_write_push = 0xCB;

    public static final int bc_logand_write_push = 0xCC;
    public static final int bc_logxor_write_push = 0xCD;
    public static final int bc_logor_write_push = 0xCE;
    public static final int bc_bitand_write_push = 0xCF;
    public static final int bc_bitxor_write_push = 0xD0;
    public static final int bc_bitor_write_push = 0xD1;
    public static final int bc_fge_write_push = 0xD2;
    public static final int bc_fle_write_push = 0xD3;
    public static final int bc_addbits_write_push = 0xD4;
    public static final int bc_addpins_write_push = 0xD5;

    public static final int bc_mul_write_push = 0xD6;
    public static final int bc_div_write_push = 0xD7;
    public static final int bc_divu_write_push = 0xD8;
    public static final int bc_rem_write_push = 0xD9;
    public static final int bc_remu_write_push = 0xDA;
    public static final int bc_sca_write_push = 0xDB;
    public static final int bc_scas_write_push = 0xDC;
    public static final int bc_frac_write_push = 0xDD;

    public static final int bc_setup_bfield_pop = 0xDE;
    public static final int bc_setup_bfield_rfvar = 0xDF;
    public static final int bc_setup_bfield_0_31 = 0xE0; //,32

    public static final int bc_hubset = 0x54; // hub bytecodes, miscellaneous (step by 2)
    public static final int bc_clkset = 0x56;
    public static final int bc_read_clkfreq = 0x58;
    public static final int bc_cogspin = 0x5A;
    public static final int bc_cogchk = 0x5C;
    public static final int bc_inline = 0x5E;
    public static final int bc_regexec = 0x60;
    public static final int bc_regload = 0x62;
    public static final int bc_call = 0x64;
    public static final int bc_getregs = 0x66;
    public static final int bc_setregs = 0x68;
    public static final int bc_bytefill = 0x6A;
    public static final int bc_bytemove = 0x6C;
    public static final int bc_byteswap = 0x6E;
    public static final int bc_bytecomp = 0x70;
    public static final int bc_wordfill = 0x72;
    public static final int bc_wordmove = 0x74;
    public static final int bc_wordswap = 0x76;
    public static final int bc_wordcomp = 0x78;
    public static final int bc_longfill = 0x7A;
    public static final int bc_longmove = 0x7C;
    public static final int bc_longswap = 0x7E;
    public static final int bc_longcomp = 0x80;
    public static final int bc_strsize = 0x82;
    public static final int bc_strcomp = 0x84;
    public static final int bc_strcopy = 0x86;
    public static final int bc_getcrc = 0x88;
    public static final int bc_waitus = 0x8A;
    public static final int bc_waitms = 0x8C;
    public static final int bc_getms = 0x8E;
    public static final int bc_getsec = 0x90;
    public static final int bc_muldiv64 = 0x92;
    public static final int bc_qsin = 0x94;
    public static final int bc_qcos = 0x96;
    public static final int bc_rotxy = 0x98;
    public static final int bc_polxy = 0x9A;
    public static final int bc_xypol = 0x9C;

    public static final int bc_float = 0x9E; // hub bytecodes, floating point
    public static final int bc_trunc = 0xA0;
    public static final int bc_round = 0xA2;
    public static final int bc_fneg = 0xA4;
    public static final int bc_fabs = 0xA6;
    public static final int bc_fsqrt = 0xA8;
    public static final int bc_fadd = 0xAA;
    public static final int bc_fsub = 0xAC;
    public static final int bc_fmul = 0xAE;
    public static final int bc_fdiv = 0xB0;
    public static final int bc_flt = 0xB2;
    public static final int bc_fgt = 0xB4;
    public static final int bc_fne = 0xB6;
    public static final int bc_fe = 0xB8;
    public static final int bc_flte = 0xBA;
    public static final int bc_fgte = 0xBC;
    public static final int bc_nan = 0xBE;

    public static final int bc_pinread = 0xC0;
    public static final int bc_pinwrite = 0xC2;
    public static final int bc_pinstart = 0xC4;
    public static final int bc_pinclear = 0xC6;

    public static final int bc_taskspin = 0xC8;
    public static final int bc_taskstop = 0xCA;
    public static final int bc_taskhalt = 0xCC;
    public static final int bc_taskcont = 0xCE;
    public static final int bc_taskchk = 0xD0;
    public static final int bc_taskid = 0xD2;
    public static final int bc_top_return = 0xD4;

    public static class Descriptor {
        public byte[] code;
        public int parameters;
        public int returns;

        public Descriptor(int b0, int b1, int parameters, int returns) {
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

    static Map<String, Descriptor> descriptors = new HashMap<String, Descriptor>();
    static {
        descriptors.put("HUBSET", new Descriptor(bc_hub_bytecode, bc_hubset, 1, 0));
        descriptors.put("CLKSET", new Descriptor(bc_hub_bytecode, bc_clkset, 2, 0));
        //descriptors.put("CLKFREQ", new Descriptor(bc_hub_bytecode, bc_read_clkfreq, 0, 1));
        //descriptors.put("COGSPIN", new Descriptor(bc_hub_bytecode, bc_cogspin, 3));
        descriptors.put("COGCHK", new Descriptor(bc_hub_bytecode, bc_cogchk, 1, 1));
        descriptors.put("REGEXEC", new Descriptor(bc_hub_bytecode, bc_regexec, 1, 0));
        descriptors.put("REGLOAD", new Descriptor(bc_hub_bytecode, bc_regload, 1, 0));
        descriptors.put("CALL", new Descriptor(bc_hub_bytecode, bc_call, 1, 0));
        descriptors.put("GETREGS", new Descriptor(bc_hub_bytecode, bc_getregs, 3, 0));
        descriptors.put("SETREGS", new Descriptor(bc_hub_bytecode, bc_setregs, 3, 0));
        descriptors.put("BYTEMOVE", new Descriptor(bc_hub_bytecode, bc_bytemove, 3, 0));
        descriptors.put("BYTEFILL", new Descriptor(bc_hub_bytecode, bc_bytefill, 3, 0));
        descriptors.put("BYTECOMP", new Descriptor(bc_hub_bytecode, bc_bytecomp, 3, 1));
        descriptors.put("BYTESWAP", new Descriptor(bc_hub_bytecode, bc_byteswap, 3, 0));
        descriptors.put("WORDMOVE", new Descriptor(bc_hub_bytecode, bc_wordmove, 3, 0));
        descriptors.put("WORDFILL", new Descriptor(bc_hub_bytecode, bc_wordfill, 3, 0));
        descriptors.put("WORDCOMP", new Descriptor(bc_hub_bytecode, bc_wordcomp, 3, 1));
        descriptors.put("WORDSWAP", new Descriptor(bc_hub_bytecode, bc_wordswap, 3, 0));
        descriptors.put("LONGMOVE", new Descriptor(bc_hub_bytecode, bc_longmove, 3, 0));
        descriptors.put("LONGFILL", new Descriptor(bc_hub_bytecode, bc_longfill, 3, 0));
        descriptors.put("LONGCOMP", new Descriptor(bc_hub_bytecode, bc_longcomp, 3, 1));
        descriptors.put("LONGSWAP", new Descriptor(bc_hub_bytecode, bc_longswap, 3, 0));
        descriptors.put("STRSIZE", new Descriptor(bc_hub_bytecode, bc_strsize, 1, 1));
        descriptors.put("STRCOMP", new Descriptor(bc_hub_bytecode, bc_strcomp, 2, 1));
        descriptors.put("STRCOPY", new Descriptor(bc_hub_bytecode, bc_strcopy, 3, 0));
        descriptors.put("GETCRC", new Descriptor(bc_hub_bytecode, bc_getcrc, 3, 1));
        descriptors.put("WAITUS", new Descriptor(bc_hub_bytecode, bc_waitus, 1, 0));
        descriptors.put("WAITMS", new Descriptor(bc_hub_bytecode, bc_waitms, 1, 0));
        descriptors.put("GETMS", new Descriptor(bc_hub_bytecode, bc_getms, 0, 1));
        descriptors.put("GETSEC", new Descriptor(bc_hub_bytecode, bc_getsec, 0, 1));
        descriptors.put("MULDIV64", new Descriptor(bc_hub_bytecode, bc_muldiv64, 3, 1));
        descriptors.put("QSIN", new Descriptor(bc_hub_bytecode, bc_qsin, 3, 1));
        descriptors.put("QCOS", new Descriptor(bc_hub_bytecode, bc_qcos, 3, 1));
        descriptors.put("ROTXY", new Descriptor(bc_hub_bytecode, bc_rotxy, 3, 2));
        descriptors.put("POLXY", new Descriptor(bc_hub_bytecode, bc_polxy, 2, 2));
        descriptors.put("XYPOL", new Descriptor(bc_hub_bytecode, bc_xypol, 2, 2));

        descriptors.put("NAN", new Descriptor(bc_hub_bytecode, bc_nan, 1, 1));
        descriptors.put("FABS", new Descriptor(bc_hub_bytecode, bc_fabs, 1, 1));
        descriptors.put("FSQRT", new Descriptor(bc_hub_bytecode, bc_fsqrt, 1, 1));
        descriptors.put("ROUND", new Descriptor(bc_hub_bytecode, bc_round, 1, 1));
        descriptors.put("TRUNC", new Descriptor(bc_hub_bytecode, bc_trunc, 1, 1));
        descriptors.put("FLOAT", new Descriptor(bc_hub_bytecode, bc_float, 1, 1));

        descriptors.put("COGSTOP", new Descriptor(bc_cogstop, -1, 1, 0));
        descriptors.put("COGID", new Descriptor(bc_cogid, -1, 0, 1));

        descriptors.put("LOCKNEW", new Descriptor(bc_locknew, -1, 0, 1));
        descriptors.put("LOCKRET", new Descriptor(bc_lockret, -1, 1, 0));
        descriptors.put("LOCKTRY", new Descriptor(bc_locktry, -1, 1, 1));
        descriptors.put("LOCKREL", new Descriptor(bc_lockrel, -1, 1, 0));
        descriptors.put("LOCKCHK", new Descriptor(bc_lockchk, -1, 1, 1));

        descriptors.put("COGATN", new Descriptor(bc_cogatn, -1, 1, 0));
        descriptors.put("POLLATN", new Descriptor(bc_pollatn, -1, 0, 1));
        descriptors.put("WAITATN", new Descriptor(bc_waitatn, -1, 0, 0));

        descriptors.put("GETRND", new Descriptor(bc_getrnd, -1, 0, 1));
        descriptors.put("GETCT", new Descriptor(bc_getct, -1, 0, 1));
        descriptors.put("POLLCT", new Descriptor(bc_pollct, -1, 1, 1));
        descriptors.put("WAITCT", new Descriptor(bc_waitct, -1, 1, 0));

        descriptors.put("PINW", new Descriptor(bc_hub_bytecode, bc_pinwrite, 2, 0));
        descriptors.put("PINWRITE", new Descriptor(bc_hub_bytecode, bc_pinwrite, 2, 0));
        descriptors.put("PINL", new Descriptor(bc_pinlow, -1, 1, 0));
        descriptors.put("PINLOW", new Descriptor(bc_pinlow, -1, 1, 0));
        descriptors.put("PINH", new Descriptor(bc_pinhigh, -1, 1, 0));
        descriptors.put("PINHIGH", new Descriptor(bc_pinhigh, -1, 1, 0));
        descriptors.put("PINT", new Descriptor(bc_pintoggle, -1, 1, 0));
        descriptors.put("PINTOGGLE", new Descriptor(bc_pintoggle, -1, 1, 0));
        descriptors.put("PINF", new Descriptor(bc_pinfloat, -1, 1, 0));
        descriptors.put("PINFLOAT", new Descriptor(bc_pinfloat, -1, 1, 0));
        descriptors.put("PINR", new Descriptor(bc_hub_bytecode, bc_pinread, 1, 1));
        descriptors.put("PINREAD", new Descriptor(bc_hub_bytecode, bc_pinread, 1, 1));

        descriptors.put("PINSTART", new Descriptor(bc_hub_bytecode, bc_pinstart, 4, 0));
        descriptors.put("PINCLEAR", new Descriptor(bc_hub_bytecode, bc_pinclear, 1, 0));

        descriptors.put("WRPIN", new Descriptor(bc_wrpin, -1, 2, 0));
        descriptors.put("WXPIN", new Descriptor(bc_wxpin, -1, 2, 0));
        descriptors.put("WYPIN", new Descriptor(bc_wypin, -1, 2, 0));
        descriptors.put("AKPIN", new Descriptor(bc_akpin, -1, 1, 0));
        descriptors.put("RDPIN", new Descriptor(bc_rdpin, -1, 1, 1));
        descriptors.put("RQPIN", new Descriptor(bc_rqpin, -1, 1, 1));

        descriptors.put("TASKNEXT", new Descriptor(bc_tasknext, -1, 0, 0));
        descriptors.put("TASKSTOP", new Descriptor(bc_hub_bytecode, bc_taskstop, 1, 0));
        descriptors.put("TASKHALT", new Descriptor(bc_hub_bytecode, bc_taskhalt, 1, 0));
        descriptors.put("TASKCONT", new Descriptor(bc_hub_bytecode, bc_taskcont, 1, 0));
        descriptors.put("TASKCHK", new Descriptor(bc_hub_bytecode, bc_taskchk, 1, 1));
        descriptors.put("TASKID", new Descriptor(bc_hub_bytecode, bc_taskid, 0, 1));
    }

    public static Descriptor getDescriptor(String s) {
        return descriptors.get(s.toUpperCase());
    }

    protected Context context;
    String text;

    public Spin2Bytecode(Context context) {
        this.context = new Context(context);
    }

    public Spin2Bytecode(Context context, String text) {
        this.context = new Context(context);
        this.text = text;
    }

    public Context getContext() {
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
