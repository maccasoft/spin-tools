/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.Register;

public class Spin2GlobalContext extends Spin2Context {

    public Spin2GlobalContext() {

        // Registers

        addBuiltinSymbol("ijmp3", new Register(0x1F0));
        addBuiltinSymbol("iret3", new Register(0x1F1));
        addBuiltinSymbol("ijmp2", new Register(0x1F2));
        addBuiltinSymbol("iret2", new Register(0x1F3));
        addBuiltinSymbol("ijmp1", new Register(0x1F4));
        addBuiltinSymbol("iret1", new Register(0x1F5));
        addBuiltinSymbol("pa", new Register(0x1F6));
        addBuiltinSymbol("pb", new Register(0x1F7));
        addBuiltinSymbol("ptra", new Register(0x1F8));
        addBuiltinSymbol("ptrb", new Register(0x1F9));
        addBuiltinSymbol("dira", new Register(0x1FA));
        addBuiltinSymbol("dirb", new Register(0x1FB));
        addBuiltinSymbol("outa", new Register(0x1FC));
        addBuiltinSymbol("outb", new Register(0x1FD));
        addBuiltinSymbol("ina", new Register(0x1FE));
        addBuiltinSymbol("inb", new Register(0x1FF));

        // Smart-pin constants

        addBuiltinSymbol("p_true_a", 0b0000_0000_000_0000000000000_00_00000_0);
        addBuiltinSymbol("p_invert_a", 0b1000_0000_000_0000000000000_00_00000_0);

        addBuiltinSymbol("p_local_a", 0b0000_0000_000_0000000000000_00_00000_0);
        addBuiltinSymbol("p_plus1_a", 0b0001_0000_000_0000000000000_00_00000_0);
        addBuiltinSymbol("p_plus2_a", 0b0010_0000_000_0000000000000_00_00000_0);
        addBuiltinSymbol("p_plus3_a", 0x30000000);
        addBuiltinSymbol("p_outbit_a", 0x40000000);
        addBuiltinSymbol("p_minus3_a", 0x50000000);
        addBuiltinSymbol("p_minus2_a", 0x60000000);
        addBuiltinSymbol("p_minus1_a", 0x70000000);
        addBuiltinSymbol("p_true_b", 0x0);
        addBuiltinSymbol("p_invert_b", 0x8000000);
        addBuiltinSymbol("p_local_b", 0x0);
        addBuiltinSymbol("p_plus1_b", 0x1000000);
        addBuiltinSymbol("p_plus2_b", 0x2000000);
        addBuiltinSymbol("p_plus3_b", 0x3000000);
        addBuiltinSymbol("p_outbit_b", 0x4000000);
        addBuiltinSymbol("p_minus3_b", 0x5000000);
        addBuiltinSymbol("p_minus2_b", 0x6000000);
        addBuiltinSymbol("p_minus1_b", 0x7000000);
        addBuiltinSymbol("p_pass_ab", 0x0);
        addBuiltinSymbol("p_and_ab", 0x200000);
        addBuiltinSymbol("p_or_ab", 0x400000);
        addBuiltinSymbol("p_xor_ab", 0x600000);
        addBuiltinSymbol("p_filt0_ab", 0x800000);
        addBuiltinSymbol("p_filt1_ab", 0xa00000);
        addBuiltinSymbol("p_filt2_ab", 0xc00000);
        addBuiltinSymbol("p_filt3_ab", 0xe00000);
        addBuiltinSymbol("p_logic_a", 0x0);
        addBuiltinSymbol("p_logic_a_fb", 0x20000);
        addBuiltinSymbol("p_logic_b_fb", 0x40000);
        addBuiltinSymbol("p_schmitt_a", 0x60000);
        addBuiltinSymbol("p_schmitt_a_fb", 0x80000);
        addBuiltinSymbol("p_schmitt_b_fb", 0xa0000);
        addBuiltinSymbol("p_compare_ab", 0xc0000);
        addBuiltinSymbol("p_compare_ab_fb", 0xe0000);
        addBuiltinSymbol("p_adc_gio", 0x100000);
        addBuiltinSymbol("p_adc_vio", 0x108000);
        addBuiltinSymbol("p_adc_float", 0x110000);
        addBuiltinSymbol("p_adc_1x", 0x118000);
        addBuiltinSymbol("p_adc_3x", 0x120000);
        addBuiltinSymbol("p_adc_10x", 0x128000);
        addBuiltinSymbol("p_adc_30x", 0x130000);
        addBuiltinSymbol("p_adc_100x", 0x138000);
        addBuiltinSymbol("p_dac_990r_3v", 0b0000_0000_000_1010000000000_00_00000_0);
        addBuiltinSymbol("p_dac_600r_2v", 0b0000_0000_000_1010100000000_00_00000_0);
        addBuiltinSymbol("p_dac_124r_3v", 0b0000_0000_000_1011000000000_00_00000_0);
        addBuiltinSymbol("p_dac_75r_2v", 0b0000_0000_000_1011100000000_00_00000_0);
        addBuiltinSymbol("p_channel", 0b0000_0000_000_0000000000000_01_00000_0);
        addBuiltinSymbol("p_level_a", 0x180000);
        addBuiltinSymbol("p_level_a_fbn", 0x1a0000);
        addBuiltinSymbol("p_level_b_fbp", 0x1c0000);
        addBuiltinSymbol("p_level_b_fbn", 0x1e0000);
        addBuiltinSymbol("p_async_io", 0x0);
        addBuiltinSymbol("p_sync_io", 0x10000);
        addBuiltinSymbol("p_true_in", 0x0);
        addBuiltinSymbol("p_invert_in", 0x8000);
        addBuiltinSymbol("p_true_output", 0x0);
        addBuiltinSymbol("p_invert_output", 0x4000);
        addBuiltinSymbol("p_high_fast", 0x0);
        addBuiltinSymbol("p_high_1k5", 0x800);
        addBuiltinSymbol("p_high_15k", 0x1000);
        addBuiltinSymbol("p_high_150k", 0x1800);
        addBuiltinSymbol("p_high_1ma", 0x2000);
        addBuiltinSymbol("p_high_100ua", 0x2800);
        addBuiltinSymbol("p_high_10ua", 0x3000);
        addBuiltinSymbol("p_high_float", 0x3800);
        addBuiltinSymbol("p_low_fast", 0x0);
        addBuiltinSymbol("p_low_1k5", 0x100);
        addBuiltinSymbol("p_low_15k", 0x200);
        addBuiltinSymbol("p_low_150k", 0x300);
        addBuiltinSymbol("p_low_1ma", 0x400);
        addBuiltinSymbol("p_low_100ua", 0x500);
        addBuiltinSymbol("p_low_10ua", 0x600);
        addBuiltinSymbol("p_low_float", 0x700);
        addBuiltinSymbol("p_tt_00", 0x0);
        addBuiltinSymbol("p_tt_01", 0x40);
        addBuiltinSymbol("p_tt_10", 0x80);
        addBuiltinSymbol("p_tt_11", 0xc0);
        addBuiltinSymbol("p_oe", 0x40);
        addBuiltinSymbol("p_bitdac", 0x80);
        addBuiltinSymbol("p_normal", 0x0);
        addBuiltinSymbol("p_repository", 0x2);
        addBuiltinSymbol("p_dac_noise", 0x2);
        addBuiltinSymbol("p_dac_dither_rnd", 0x4);
        addBuiltinSymbol("p_dac_dither_pwm", 0x6);
        addBuiltinSymbol("p_pulse", 0x8);
        addBuiltinSymbol("p_transition", 0xa);
        addBuiltinSymbol("p_nco_freq", 0xc);
        addBuiltinSymbol("p_nco_duty", 0xe);
        addBuiltinSymbol("p_pwm_triangle", 0x10);
        addBuiltinSymbol("p_pwm_sawtooth", 0x12);
        addBuiltinSymbol("p_pwm_smps", 0x14);
        addBuiltinSymbol("p_quadrature", 0x16);
        addBuiltinSymbol("p_reg_up", 0x18);
        addBuiltinSymbol("p_reg_up_down", 0x1a);
        addBuiltinSymbol("p_count_rises", 0x1c);
        addBuiltinSymbol("p_count_highs", 0x1e);

        addBuiltinSymbol("p_state_ticks", 0x20);
        addBuiltinSymbol("p_high_ticks", 0x22);
        addBuiltinSymbol("p_events_ticks", 0x24);
        addBuiltinSymbol("p_periods_ticks", 0x26);
        addBuiltinSymbol("p_periods_highs", 0x28);
        addBuiltinSymbol("p_counter_ticks", 0x2a);
        addBuiltinSymbol("p_counter_highs", 0x2c);
        addBuiltinSymbol("p_counter_periods", 0x2e);

        addBuiltinSymbol("p_adc", 0b0000_0000_000_0000000000000_00_11000_0);
        addBuiltinSymbol("p_adc_ext", 0b0000_0000_000_0000000000000_00_11001_0);
        addBuiltinSymbol("p_adc_scope", 0b0000_0000_000_0000000000000_00_11010_0);
        addBuiltinSymbol("p_usb_pair", 0b0000_0000_000_0000000000000_00_11011_0);
        addBuiltinSymbol("p_sync_tx", 0b0000_0000_000_0000000000000_00_11100_0);
        addBuiltinSymbol("p_sync_rx", 0b0000_0000_000_0000000000000_00_11101_0);
        addBuiltinSymbol("p_async_tx", 0b0000_0000_000_0000000000000_00_11110_0);
        addBuiltinSymbol("p_async_rx", 0b0000_0000_000_0000000000000_00_11111_0);

        // Streamer modes

        addBuiltinSymbol("x_imm_32x1_lut", 0x0000 << 16);
        addBuiltinSymbol("x_imm_16x2_lut", 0x1000 << 16);
        addBuiltinSymbol("x_imm_8x4_lut", 0x2000 << 16);
        addBuiltinSymbol("x_imm_4x8_lut", 0x3000 << 16);

        addBuiltinSymbol("x_imm_32x1_1dac1", 0x4000 << 16);
        addBuiltinSymbol("x_imm_16x2_2dac1", 0x5000 << 16);
        addBuiltinSymbol("x_imm_16x2_1dac2", 0x5002 << 16);
        addBuiltinSymbol("x_imm_8x4_4dac1", 0x6000 << 16);
        addBuiltinSymbol("x_imm_8x4_2dac2", 0x6002 << 16);
        addBuiltinSymbol("x_imm_8x4_1dac4", 0x6004 << 16);

        addBuiltinSymbol("x_imm_4x8_4dac2", 0x6006 << 16);
        addBuiltinSymbol("x_imm_4x8_2dac4", 0x6007 << 16);
        addBuiltinSymbol("x_imm_4x8_1dac8", 0x600e << 16);
        addBuiltinSymbol("x_imm_2x16_4dac4", 0x600f << 16);

        addBuiltinSymbol("x_imm_2x16_2dac8", 0x7000 << 16);
        addBuiltinSymbol("x_imm_1x32_4dac8", 0x7001 << 16);

        addBuiltinSymbol("x_rflong_32x1_lut", 0x7002 << 16);
        addBuiltinSymbol("x_rflong_16x2_lut", 0x7004 << 16);
        addBuiltinSymbol("x_rflong_8x4_lut", 0x7006 << 16);
        addBuiltinSymbol("x_rflong_4x8_lut", 0x7008 << 16);

        addBuiltinSymbol("x_rfbyte_1p_1dac1", 0x8000 << 16);
        addBuiltinSymbol("x_rfbyte_2p_2dac1", 0x9000 << 16);
        addBuiltinSymbol("x_rfbyte_2p_1dac2", 0x9002 << 16);
        addBuiltinSymbol("x_rfbyte_4p_4dac1", 0xa000 << 16);
        addBuiltinSymbol("x_rfbyte_4p_2dac2", 0xa002 << 16);
        addBuiltinSymbol("x_rfbyte_4p_1dac4", 0xa004 << 16);
        addBuiltinSymbol("x_rfbyte_8p_4dac2", 0xa006 << 16);
        addBuiltinSymbol("x_rfbyte_8p_2dac4", 0xa007 << 16);
        addBuiltinSymbol("x_rfbyte_8p_1dac8", 0xa00e << 16);
        addBuiltinSymbol("x_rfword_16p_4dac4", 0xa00f << 16);
        addBuiltinSymbol("x_rfword_16p_2dac8", 0xb000 << 16);
        addBuiltinSymbol("x_rflong_32p_4dac8", 0xb001 << 16);

        addBuiltinSymbol("x_rfbyte_luma8", 0xb002 << 16);
        addBuiltinSymbol("x_rfbyte_rgbi8", 0xb003 << 16);
        addBuiltinSymbol("x_rfbyte_rgb8", 0xb004 << 16);
        addBuiltinSymbol("x_rfword_rgb16", 0xb005 << 16);
        addBuiltinSymbol("x_rflong_rgb24", 0xb006 << 16);

        addBuiltinSymbol("x_1p_1dac1_wfbyte", 0xc000 << 16);
        addBuiltinSymbol("x_2p_2dac1_wfbyte", 0xd000 << 16);
        addBuiltinSymbol("x_2p_1dac2_wfbyte", 0xd002 << 16);

        addBuiltinSymbol("x_4p_4dac1_wfbyte", 0xe000 << 16);
        addBuiltinSymbol("x_4p_2dac2_wfbyte", 0xe002 << 16);
        addBuiltinSymbol("x_4p_1dac4_wfbyte", 0xe004 << 16);

        addBuiltinSymbol("x_8p_4dac2_wfbyte", 0xe006 << 16);
        addBuiltinSymbol("x_8p_2dac4_wfbyte", 0xe007 << 16);
        addBuiltinSymbol("x_8p_1dac8_wfbyte", 0xe00e << 16);

        addBuiltinSymbol("x_16p_4dac4_wfword", 0xe00f << 16);
        addBuiltinSymbol("x_16p_2dac8_wfword", 0xf000 << 16);
        addBuiltinSymbol("x_32p_4dac8_wflong", 0xf001 << 16);

        addBuiltinSymbol("x_1adc8_0p_1dac8_wfbyte", 0xf002 << 16);
        addBuiltinSymbol("x_1adc8_8p_2dac8_wfword", 0xf003 << 16);
        addBuiltinSymbol("x_2adc8_0p_2dac8_wfword", 0xf004 << 16);
        addBuiltinSymbol("x_2adc8_16p_4dac8_wflong", 0xf005 << 16);
        addBuiltinSymbol("x_4adc8_0p_4dac8_wflong", 0xf006 << 16);

        addBuiltinSymbol("x_dds_goertzel_sinc1", 0xf007 << 16);
        addBuiltinSymbol("x_dds_goertzel_sinc2", 0xf087 << 16);

        addBuiltinSymbol("x_dacs_off", 0x0000 << 16);
        addBuiltinSymbol("x_dacs_0_0_0_0", 0x0100 << 16);
        addBuiltinSymbol("x_dacs_x_x_0_0", 0x0200 << 16);
        addBuiltinSymbol("x_dacs_0_0_x_x", 0x0300 << 16);
        addBuiltinSymbol("x_dacs_x_x_x_0", 0x0400 << 16);
        addBuiltinSymbol("x_dacs_x_x_0_x", 0x0500 << 16);
        addBuiltinSymbol("x_dacs_x_0_x_x", 0x0600 << 16);
        addBuiltinSymbol("x_dacs_0_x_x_x", 0x0700 << 16);

        addBuiltinSymbol("x_dacs_0n0_0n0", 0x0800 << 16);
        addBuiltinSymbol("x_dacs_x_x_0n0", 0x0900 << 16);
        addBuiltinSymbol("x_dacs_0n0_x_x", 0x0a00 << 16);
        addBuiltinSymbol("x_dacs_1_0_1_0", 0x0b00 << 16);
        addBuiltinSymbol("x_dacs_x_x_1_0", 0x0c00 << 16);
        addBuiltinSymbol("x_dacs_1_0_x_x", 0x0d00 << 16);
        addBuiltinSymbol("x_dacs_1n1_0n0", 0x0e00 << 16);
        addBuiltinSymbol("x_dacs_3_2_1_0", 0x0f00 << 16);

        addBuiltinSymbol("x_pins_off", 0x0000 << 16);
        addBuiltinSymbol("x_pins_on", 0x0080 << 16);
        addBuiltinSymbol("x_write_off", 0x0000 << 16);
        addBuiltinSymbol("x_write_on", 0x0080 << 16);
        addBuiltinSymbol("x_alt_off", 0x0000 << 16);
        addBuiltinSymbol("x_alt_on", 0x0001 << 16);

        addBuiltinSymbol("cogexec", 0);
        addBuiltinSymbol("cogexec_new", 0x10);
        addBuiltinSymbol("hubexec", 0x20);
        addBuiltinSymbol("hubexec_new", 0x30);
        addBuiltinSymbol("cogexec_new_pair", 0x11);
        addBuiltinSymbol("hubexec_new_pair", 0x31);

        addBuiltinSymbol("newcog", 0x10);

        addBuiltinSymbol("event_int", 0);
        addBuiltinSymbol("int_off", 0);
        addBuiltinSymbol("event_ct1", 1);
        addBuiltinSymbol("event_ct2", 2);
        addBuiltinSymbol("event_ct3", 3);
        addBuiltinSymbol("event_se1", 4);
        addBuiltinSymbol("event_se2", 5);
        addBuiltinSymbol("event_se3", 6);
        addBuiltinSymbol("event_se4", 7);

        addBuiltinSymbol("event_pat", 8);
        addBuiltinSymbol("event_fbw", 9);
        addBuiltinSymbol("event_xmt", 10);
        addBuiltinSymbol("event_xfi", 11);
        addBuiltinSymbol("event_xro", 12);
        addBuiltinSymbol("event_xrl", 13);
        addBuiltinSymbol("event_atn", 14);
        addBuiltinSymbol("event_qmt", 15);

        // Predefined constants

        addBuiltinSymbol("false", 0);
        addBuiltinSymbol("true", -1);
        addBuiltinSymbol("negx", Integer.MIN_VALUE);
        addBuiltinSymbol("posx", Integer.MAX_VALUE);
        addBuiltinSymbol("pi", Math.PI);
    }

    void addBuiltinSymbol(String name, Expression value) {
        symbols.put(name.toLowerCase(), value);
        symbols.put(name.toUpperCase(), value);
    }

    void addBuiltinSymbol(String name, long value) {
        symbols.put(name.toLowerCase(), new NumberLiteral(value));
        symbols.put(name.toUpperCase(), new NumberLiteral(value));
    }

    void addBuiltinSymbol(String name, double value) {
        symbols.put(name.toLowerCase(), new NumberLiteral(value));
        symbols.put(name.toUpperCase(), new NumberLiteral(value));
    }

}
