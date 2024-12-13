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

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.expressions.Register;

public class Spin2GlobalContext extends Context {

    public Spin2GlobalContext() {
        this(false);
    }

    public Spin2GlobalContext(boolean caseSensitive) {
        super(caseSensitive);

        // Registers

        addBuiltinSymbol("IJMP3", new Register(0x1F0));
        addBuiltinSymbol("IRET3", new Register(0x1F1));
        addBuiltinSymbol("IJMP2", new Register(0x1F2));
        addBuiltinSymbol("IRET2", new Register(0x1F3));
        addBuiltinSymbol("IJMP1", new Register(0x1F4));
        addBuiltinSymbol("IRET1", new Register(0x1F5));
        addBuiltinSymbol("PA", new Register(0x1F6));
        addBuiltinSymbol("PB", new Register(0x1F7));
        addBuiltinSymbol("PTRA", new Register(0x1F8));
        addBuiltinSymbol("PTRB", new Register(0x1F9));
        addBuiltinSymbol("DIRA", new Register(0x1FA));
        addBuiltinSymbol("DIRB", new Register(0x1FB));
        addBuiltinSymbol("OUTA", new Register(0x1FC));
        addBuiltinSymbol("OUTB", new Register(0x1FD));
        addBuiltinSymbol("INA", new Register(0x1FE));
        addBuiltinSymbol("INB", new Register(0x1FF));

        addBuiltinSymbol("PR0", new Register(0x1D8));
        addBuiltinSymbol("PR1", new Register(0x1D9));
        addBuiltinSymbol("PR2", new Register(0x1DA));
        addBuiltinSymbol("PR3", new Register(0x1DB));
        addBuiltinSymbol("PR4", new Register(0x1DC));
        addBuiltinSymbol("PR5", new Register(0x1DD));
        addBuiltinSymbol("PR6", new Register(0x1DE));
        addBuiltinSymbol("PR7", new Register(0x1DF));

        addBuiltinSymbol("#PR0", new NumberLiteral(0x1D8, 16));
        addBuiltinSymbol("#PR1", new NumberLiteral(0x1D9, 16));
        addBuiltinSymbol("#PR2", new NumberLiteral(0x1DA, 16));
        addBuiltinSymbol("#PR3", new NumberLiteral(0x1DB, 16));
        addBuiltinSymbol("#PR4", new NumberLiteral(0x1DC, 16));
        addBuiltinSymbol("#PR5", new NumberLiteral(0x1DD, 16));
        addBuiltinSymbol("#PR6", new NumberLiteral(0x1DE, 16));
        addBuiltinSymbol("#PR7", new NumberLiteral(0x1DF, 16));

        // Smart-pin constants

        addBuiltinSymbol("P_TRUE_A", 0b0000_0000_000_0000000000000_00_00000_0);
        addBuiltinSymbol("P_INVERT_A", 0b1000_0000_000_0000000000000_00_00000_0);

        addBuiltinSymbol("P_LOCAL_A", 0b0000_0000_000_0000000000000_00_00000_0);
        addBuiltinSymbol("P_PLUS1_A", 0b0001_0000_000_0000000000000_00_00000_0);
        addBuiltinSymbol("P_PLUS2_A", 0b0010_0000_000_0000000000000_00_00000_0);
        addBuiltinSymbol("P_PLUS3_A", 0x30000000);
        addBuiltinSymbol("P_OUTBIT_A", 0x40000000);
        addBuiltinSymbol("P_MINUS3_A", 0x50000000);
        addBuiltinSymbol("P_MINUS2_A", 0x60000000);
        addBuiltinSymbol("P_MINUS1_A", 0x70000000);
        addBuiltinSymbol("P_TRUE_B", 0x0);
        addBuiltinSymbol("P_INVERT_B", 0x8000000);
        addBuiltinSymbol("P_LOCAL_B", 0x0);
        addBuiltinSymbol("P_PLUS1_B", 0x1000000);
        addBuiltinSymbol("P_PLUS2_B", 0x2000000);
        addBuiltinSymbol("P_PLUS3_B", 0x3000000);
        addBuiltinSymbol("P_OUTBIT_B", 0x4000000);
        addBuiltinSymbol("P_MINUS3_B", 0x5000000);
        addBuiltinSymbol("P_MINUS2_B", 0x6000000);
        addBuiltinSymbol("P_MINUS1_B", 0x7000000);
        addBuiltinSymbol("P_PASS_AB", 0x0);
        addBuiltinSymbol("P_AND_AB", 0x200000);
        addBuiltinSymbol("P_OR_AB", 0x400000);
        addBuiltinSymbol("P_XOR_AB", 0x600000);
        addBuiltinSymbol("P_FILT0_AB", 0x800000);
        addBuiltinSymbol("P_FILT1_AB", 0xA00000);
        addBuiltinSymbol("P_FILT2_AB", 0xC00000);
        addBuiltinSymbol("P_FILT3_AB", 0xE00000);
        addBuiltinSymbol("P_LOGIC_A", 0x0);
        addBuiltinSymbol("P_LOGIC_A_FB", 0x20000);
        addBuiltinSymbol("P_LOGIC_B_FB", 0x40000);
        addBuiltinSymbol("P_SCHMITT_A", 0x60000);
        addBuiltinSymbol("P_SCHMITT_A_FB", 0x80000);
        addBuiltinSymbol("P_SCHMITT_B_FB", 0xA0000);
        addBuiltinSymbol("P_COMPARE_AB", 0xC0000);
        addBuiltinSymbol("P_COMPARE_AB_FB", 0xE0000);
        addBuiltinSymbol("P_ADC_GIO", 0x100000);
        addBuiltinSymbol("P_ADC_VIO", 0x108000);
        addBuiltinSymbol("P_ADC_FLOAT", 0x110000);
        addBuiltinSymbol("P_ADC_1X", 0x118000);
        addBuiltinSymbol("P_ADC_3X", 0x120000);
        addBuiltinSymbol("P_ADC_10x", 0x128000);
        addBuiltinSymbol("P_ADC_30x", 0x130000);
        addBuiltinSymbol("P_ADC_100x", 0x138000);
        addBuiltinSymbol("P_DAC_990R_3V", 0b0000_0000_000_1010000000000_00_00000_0);
        addBuiltinSymbol("P_DAC_600R_2V", 0b0000_0000_000_1010100000000_00_00000_0);
        addBuiltinSymbol("P_DAC_124R_3V", 0b0000_0000_000_1011000000000_00_00000_0);
        addBuiltinSymbol("P_DAC_75R_2V", 0b0000_0000_000_1011100000000_00_00000_0);
        addBuiltinSymbol("P_CHANNEL", 0b0000_0000_000_0000000000000_01_00000_0);
        addBuiltinSymbol("P_LEVEL_A", 0x180000);
        addBuiltinSymbol("P_LEVEL_A_FBN", 0x1A0000);
        addBuiltinSymbol("P_LEVEL_B_FBP", 0x1C0000);
        addBuiltinSymbol("P_LEVEL_B_FBN", 0x1E0000);
        addBuiltinSymbol("P_ASYNC_IO", 0x0);
        addBuiltinSymbol("P_SYNC_IO", 0x10000);
        addBuiltinSymbol("P_TRUE_IN", 0x0);
        addBuiltinSymbol("P_INVERT_IN", 0x8000);
        addBuiltinSymbol("P_TRUE_OUTPUT", 0x0);
        addBuiltinSymbol("P_INVERT_OUTPUT", 0x4000);
        addBuiltinSymbol("P_HIGH_FAST", 0x0);
        addBuiltinSymbol("P_HIGH_1K5", 0x800);
        addBuiltinSymbol("P_HIGH_15K", 0x1000);
        addBuiltinSymbol("P_HIGH_150K", 0x1800);
        addBuiltinSymbol("P_HIGH_1MA", 0x2000);
        addBuiltinSymbol("P_HIGH_100UA", 0x2800);
        addBuiltinSymbol("P_HIGH_10UA", 0x3000);
        addBuiltinSymbol("P_HIGH_FLOAT", 0x3800);
        addBuiltinSymbol("P_LOW_FAST", 0x0);
        addBuiltinSymbol("P_LOW_1K5", 0x100);
        addBuiltinSymbol("P_LOW_15K", 0x200);
        addBuiltinSymbol("P_LOW_150K", 0x300);
        addBuiltinSymbol("P_LOW_1MA", 0x400);
        addBuiltinSymbol("P_LOW_100UA", 0x500);
        addBuiltinSymbol("P_LOW_10UA", 0x600);
        addBuiltinSymbol("P_LOW_FLOAT", 0x700);
        addBuiltinSymbol("P_TT_00", 0x0);
        addBuiltinSymbol("P_TT_01", 0x40);
        addBuiltinSymbol("P_TT_10", 0x80);
        addBuiltinSymbol("P_TT_11", 0xC0);
        addBuiltinSymbol("P_OE", 0x40);
        addBuiltinSymbol("P_BITDAC", 0x80);
        addBuiltinSymbol("P_NORMAL", 0x0);
        addBuiltinSymbol("P_REPOSITORY", 0x2);
        addBuiltinSymbol("P_DAC_NOISE", 0x2);
        addBuiltinSymbol("P_DAC_DITHER_RND", 0x4);
        addBuiltinSymbol("P_DAC_DITHER_PWM", 0x6);
        addBuiltinSymbol("P_PULSE", 0x8);
        addBuiltinSymbol("P_TRANSITION", 0xA);
        addBuiltinSymbol("P_NCO_FREQ", 0xC);
        addBuiltinSymbol("P_NCO_DUTY", 0xE);
        addBuiltinSymbol("P_PWM_TRIANGLE", 0x10);
        addBuiltinSymbol("P_PWM_SAWTOOTH", 0x12);
        addBuiltinSymbol("P_PWM_SMPS", 0x14);
        addBuiltinSymbol("P_QUADRATURE", 0x16);
        addBuiltinSymbol("P_REG_UP", 0x18);
        addBuiltinSymbol("P_REG_UP_DOWN", 0x1A);
        addBuiltinSymbol("P_COUNT_RISES", 0x1C);
        addBuiltinSymbol("P_COUNT_HIGHS", 0x1E);

        addBuiltinSymbol("P_STATE_TICKS", 0x20);
        addBuiltinSymbol("P_HIGH_TICKS", 0x22);
        addBuiltinSymbol("P_EVENTS_TICKS", 0x24);
        addBuiltinSymbol("P_PERIODS_TICKS", 0x26);
        addBuiltinSymbol("P_PERIODS_HIGHS", 0x28);
        addBuiltinSymbol("P_COUNTER_TICKS", 0x2A);
        addBuiltinSymbol("P_COUNTER_HIGHS", 0x2C);
        addBuiltinSymbol("P_COUNTER_PERIODS", 0x2E);

        addBuiltinSymbol("P_ADC", 0b0000_0000_000_0000000000000_00_11000_0);
        addBuiltinSymbol("P_ADC_EXT", 0b0000_0000_000_0000000000000_00_11001_0);
        addBuiltinSymbol("P_ADC_SCOPE", 0b0000_0000_000_0000000000000_00_11010_0);
        addBuiltinSymbol("P_USB_PAIR", 0b0000_0000_000_0000000000000_00_11011_0);
        addBuiltinSymbol("P_SYNC_TX", 0b0000_0000_000_0000000000000_00_11100_0);
        addBuiltinSymbol("P_SYNC_RX", 0b0000_0000_000_0000000000000_00_11101_0);
        addBuiltinSymbol("P_ASYNC_TX", 0b0000_0000_000_0000000000000_00_11110_0);
        addBuiltinSymbol("P_ASYNC_RX", 0b0000_0000_000_0000000000000_00_11111_0);

        // Streamer modes

        addBuiltinSymbol("X_IMM_32X1_LUT", 0x0000 << 16);
        addBuiltinSymbol("X_IMM_16X2_LUT", 0x1000 << 16);
        addBuiltinSymbol("X_IMM_8X4_LUT", 0x2000 << 16);
        addBuiltinSymbol("X_IMM_4X8_LUT", 0x3000 << 16);

        addBuiltinSymbol("X_IMM_32X1_1DAC1", 0x4000 << 16);
        addBuiltinSymbol("X_IMM_16X2_2DAC1", 0x5000 << 16);
        addBuiltinSymbol("X_IMM_16X2_1DAC2", 0x5002 << 16);
        addBuiltinSymbol("X_IMM_8X4_4DAC1", 0x6000 << 16);
        addBuiltinSymbol("X_IMM_8X4_2DAC2", 0x6002 << 16);
        addBuiltinSymbol("X_IMM_8X4_1DAC4", 0x6004 << 16);

        addBuiltinSymbol("X_IMM_4X8_4DAC2", 0x6006 << 16);
        addBuiltinSymbol("X_IMM_4X8_2DAC4", 0x6007 << 16);
        addBuiltinSymbol("X_IMM_4X8_1DAC8", 0x600E << 16);
        addBuiltinSymbol("X_IMM_2X16_4DAC4", 0x600F << 16);

        addBuiltinSymbol("X_IMM_2X16_2DAC8", 0x7000 << 16);
        addBuiltinSymbol("X_IMM_1X32_4DAC8", 0x7001 << 16);

        addBuiltinSymbol("X_RFLONG_32X1_LUT", 0x7002 << 16);
        addBuiltinSymbol("X_RFLONG_16X2_LUT", 0x7004 << 16);
        addBuiltinSymbol("X_RFLONG_8X4_LUT", 0x7006 << 16);
        addBuiltinSymbol("X_RFLONG_4X8_LUT", 0x7008 << 16);

        addBuiltinSymbol("X_RFBYTE_1P_1DAC1", 0x8000 << 16);
        addBuiltinSymbol("X_RFBYTE_2P_2DAC1", 0x9000 << 16);
        addBuiltinSymbol("X_RFBYTE_2P_1DAC2", 0x9002 << 16);
        addBuiltinSymbol("X_RFBYTE_4P_4DAC1", 0xA000 << 16);
        addBuiltinSymbol("X_RFBYTE_4P_2DAC2", 0xA002 << 16);
        addBuiltinSymbol("X_RFBYTE_4P_1DAC4", 0xA004 << 16);
        addBuiltinSymbol("X_RFBYTE_8P_4DAC2", 0xA006 << 16);
        addBuiltinSymbol("X_RFBYTE_8P_2DAC4", 0xA007 << 16);
        addBuiltinSymbol("X_RFBYTE_8P_1DAC8", 0xA00E << 16);
        addBuiltinSymbol("X_RFWORD_16P_4DAC4", 0xA00F << 16);
        addBuiltinSymbol("X_RFWORD_16P_2DAC8", 0xB000 << 16);
        addBuiltinSymbol("X_RFLONG_32P_4DAC8", 0xB001 << 16);

        addBuiltinSymbol("X_RFBYTE_LUMA8", 0xB002 << 16);
        addBuiltinSymbol("X_RFBYTE_RGBI8", 0xB003 << 16);
        addBuiltinSymbol("X_RFBYTE_RGB8", 0xB004 << 16);
        addBuiltinSymbol("X_RFWORD_RGB16", 0xB005 << 16);
        addBuiltinSymbol("X_RFLONG_RGB24", 0xB006 << 16);

        addBuiltinSymbol("X_1P_1DAC1_WFBYTE", 0xC000 << 16);
        addBuiltinSymbol("X_2P_2DAC1_WFBYTE", 0xD000 << 16);
        addBuiltinSymbol("X_2P_1DAC2_WFBYTE", 0xD002 << 16);

        addBuiltinSymbol("X_4P_4DAC1_WFBYTE", 0xE000 << 16);
        addBuiltinSymbol("X_4P_2DAC2_WFBYTE", 0xE002 << 16);
        addBuiltinSymbol("X_4P_1DAC4_WFBYTE", 0xE004 << 16);

        addBuiltinSymbol("X_8P_4DAC2_WFBYTE", 0xE006 << 16);
        addBuiltinSymbol("X_8P_2DAC4_WFBYTE", 0xE007 << 16);
        addBuiltinSymbol("X_8P_1DAC8_WFBYTE", 0xE00E << 16);

        addBuiltinSymbol("X_16P_4DAC4_WFWORD", 0xE00F << 16);
        addBuiltinSymbol("X_16P_2DAC8_WFWORD", 0xF000 << 16);
        addBuiltinSymbol("X_32P_4DAC8_WFLONG", 0xF001 << 16);

        addBuiltinSymbol("X_1ADC8_0P_1DAC8_WFBYTE", 0xF002 << 16);
        addBuiltinSymbol("X_1ADC8_8P_2DAC8_WFWORD", 0xF003 << 16);
        addBuiltinSymbol("X_2ADC8_0P_2DAC8_WFWORD", 0xF004 << 16);
        addBuiltinSymbol("X_2ADC8_16P_4DAC8_WFLONG", 0xF005 << 16);
        addBuiltinSymbol("X_4ADC8_0P_4DAC8_WFLONG", 0xF006 << 16);

        addBuiltinSymbol("X_DDS_GOERTZEL_SINC1", 0xF007 << 16);
        addBuiltinSymbol("X_DDS_GOERTZEL_SINC2", 0xF087 << 16);

        addBuiltinSymbol("X_DACS_OFF", 0x0000 << 16);
        addBuiltinSymbol("X_DACS_0_0_0_0", 0x0100 << 16);
        addBuiltinSymbol("X_DACS_X_X_0_0", 0x0200 << 16);
        addBuiltinSymbol("X_DACS_0_0_X_X", 0x0300 << 16);
        addBuiltinSymbol("X_DACS_X_X_X_0", 0x0400 << 16);
        addBuiltinSymbol("X_DACS_X_X_0_X", 0x0500 << 16);
        addBuiltinSymbol("X_DACS_X_0_X_X", 0x0600 << 16);
        addBuiltinSymbol("X_DACS_0_X_X_X", 0x0700 << 16);

        addBuiltinSymbol("X_DACS_0N0_0N0", 0x0800 << 16);
        addBuiltinSymbol("X_DACS_X_X_0N0", 0x0900 << 16);
        addBuiltinSymbol("X_DACS_0N0_X_X", 0x0A00 << 16);
        addBuiltinSymbol("X_DACS_1_0_1_0", 0x0B00 << 16);
        addBuiltinSymbol("X_DACS_X_X_1_0", 0x0C00 << 16);
        addBuiltinSymbol("X_DACS_1_0_X_X", 0x0D00 << 16);
        addBuiltinSymbol("X_DACS_1N1_0N0", 0x0E00 << 16);
        addBuiltinSymbol("X_DACS_3_2_1_0", 0x0F00 << 16);

        addBuiltinSymbol("X_PINS_OFF", 0x0000 << 16);
        addBuiltinSymbol("X_PINS_ON", 0x0080 << 16);
        addBuiltinSymbol("X_WRITE_OFF", 0x0000 << 16);
        addBuiltinSymbol("X_WRITE_ON", 0x0080 << 16);
        addBuiltinSymbol("X_ALT_OFF", 0x0000 << 16);
        addBuiltinSymbol("X_ALT_ON", 0x0001 << 16);

        addBuiltinSymbol("COGEXEC", 0);
        addBuiltinSymbol("COGEXEC_NEW", 0x10);
        addBuiltinSymbol("HUBEXEC", 0x20);
        addBuiltinSymbol("HUBEXEC_NEW", 0x30);
        addBuiltinSymbol("COGEXEC_NEW_PAIR", 0x11);
        addBuiltinSymbol("HUBEXEC_NEW_PAIR", 0x31);

        addBuiltinSymbol("NEWCOG", 0x10);

        addBuiltinSymbol("EVENT_INT", 0);
        addBuiltinSymbol("INT_OFF", 0);
        addBuiltinSymbol("EVENT_CT1", 1);
        addBuiltinSymbol("EVENT_CT2", 2);
        addBuiltinSymbol("EVENT_CT3", 3);
        addBuiltinSymbol("EVENT_SE1", 4);
        addBuiltinSymbol("EVENT_SE2", 5);
        addBuiltinSymbol("EVENT_SE3", 6);
        addBuiltinSymbol("EVENT_SE4", 7);

        addBuiltinSymbol("EVENT_PAT", 8);
        addBuiltinSymbol("EVENT_FBW", 9);
        addBuiltinSymbol("EVENT_XMT", 10);
        addBuiltinSymbol("EVENT_XFI", 11);
        addBuiltinSymbol("EVENT_XRO", 12);
        addBuiltinSymbol("EVENT_XRL", 13);
        addBuiltinSymbol("EVENT_ATN", 14);
        addBuiltinSymbol("EVENT_QMT", 15);

        addBuiltinSymbol("NEWTASK", -1);
        addBuiltinSymbol("THISTASK", -1);

        // Predefined constants

        addBuiltinSymbol("FALSE", 0);
        addBuiltinSymbol("TRUE", -1);
        addBuiltinSymbol("NEGX", Integer.MIN_VALUE);
        addBuiltinSymbol("POSX", Integer.MAX_VALUE);
        addBuiltinSymbol("PI", Math.PI);
    }

    private void addBuiltinSymbol(String name, long value) {
        addBuiltinSymbol(name, new NumberLiteral(value));
    }

    private void addBuiltinSymbol(String name, double value) {
        addBuiltinSymbol(name, new NumberLiteral(value));
    }

}
