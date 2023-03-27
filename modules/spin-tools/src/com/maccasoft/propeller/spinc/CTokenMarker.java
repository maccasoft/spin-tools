/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.SourceTokenMarker;
import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.FunctionNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.SourceProvider;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.spin2.Spin2Model;

public class CTokenMarker extends SourceTokenMarker {

    static Map<String, TokenId> keywords = new CaseInsensitiveMap<>();
    static {
        keywords.put("void", TokenId.TYPE);
        keywords.put("int", TokenId.TYPE);
        keywords.put("float", TokenId.TYPE);
        keywords.put("byte", TokenId.TYPE);
        keywords.put("word", TokenId.TYPE);
        keywords.put("long", TokenId.TYPE);
        keywords.put("bytefit", TokenId.TYPE);
        keywords.put("wordfit", TokenId.TYPE);

        keywords.put("hubset", TokenId.FUNCTION);
        keywords.put("clkset", TokenId.FUNCTION);
        keywords.put("cogspin", TokenId.FUNCTION);
        keywords.put("coginit", TokenId.FUNCTION);
        keywords.put("cogstop", TokenId.FUNCTION);
        keywords.put("cogid", TokenId.FUNCTION);
        keywords.put("cogchk", TokenId.FUNCTION);
        keywords.put("locknew", TokenId.FUNCTION);
        keywords.put("lockret", TokenId.FUNCTION);
        keywords.put("locktry", TokenId.FUNCTION);
        keywords.put("lockrel", TokenId.FUNCTION);
        keywords.put("lockchk", TokenId.FUNCTION);
        keywords.put("cogatn", TokenId.FUNCTION);
        keywords.put("pollatn", TokenId.FUNCTION);
        keywords.put("waitatn", TokenId.FUNCTION);

        keywords.put("pinw", TokenId.FUNCTION);
        keywords.put("pinwrite", TokenId.FUNCTION);
        keywords.put("pinl", TokenId.FUNCTION);
        keywords.put("pinlow", TokenId.FUNCTION);
        keywords.put("pinh", TokenId.FUNCTION);
        keywords.put("pinhigh", TokenId.FUNCTION);
        keywords.put("pint", TokenId.FUNCTION);
        keywords.put("pintoggle", TokenId.FUNCTION);
        keywords.put("pinf", TokenId.FUNCTION);
        keywords.put("pinfloat", TokenId.FUNCTION);
        keywords.put("pinr", TokenId.FUNCTION);
        keywords.put("pinread", TokenId.FUNCTION);
        keywords.put("pinstart", TokenId.FUNCTION);
        keywords.put("pinclear", TokenId.FUNCTION);
        keywords.put("wrpin", TokenId.FUNCTION);
        keywords.put("wxpin", TokenId.FUNCTION);
        keywords.put("wypin", TokenId.FUNCTION);
        keywords.put("akpin", TokenId.FUNCTION);
        keywords.put("rdpin", TokenId.FUNCTION);
        keywords.put("rqpin", TokenId.FUNCTION);

        keywords.put("getct", TokenId.FUNCTION);
        keywords.put("pollct", TokenId.FUNCTION);
        keywords.put("waitct", TokenId.FUNCTION);
        keywords.put("waitus", TokenId.FUNCTION);
        keywords.put("waitms", TokenId.FUNCTION);
        keywords.put("getsec", TokenId.FUNCTION);
        keywords.put("getms", TokenId.FUNCTION);

        keywords.put("call", TokenId.FUNCTION);
        keywords.put("regexec", TokenId.FUNCTION);
        keywords.put("regload", TokenId.FUNCTION);

        keywords.put("rotxy", TokenId.FUNCTION);
        keywords.put("polxy", TokenId.FUNCTION);
        keywords.put("xypol", TokenId.FUNCTION);
        keywords.put("qsin", TokenId.FUNCTION);
        keywords.put("qcos", TokenId.FUNCTION);
        keywords.put("muldiv64", TokenId.FUNCTION);
        keywords.put("getisn", TokenId.FUNCTION);
        keywords.put("nan", TokenId.FUNCTION);

        keywords.put("getregs", TokenId.FUNCTION);
        keywords.put("setregs", TokenId.FUNCTION);
        keywords.put("bytemove", TokenId.FUNCTION);
        keywords.put("wordmove", TokenId.FUNCTION);
        keywords.put("longmove", TokenId.FUNCTION);
        keywords.put("bytefill", TokenId.FUNCTION);
        keywords.put("wordfill", TokenId.FUNCTION);
        keywords.put("longfill", TokenId.FUNCTION);

        keywords.put("string", TokenId.FUNCTION);
        keywords.put("strsize", TokenId.FUNCTION);
        keywords.put("strcomp", TokenId.FUNCTION);
        keywords.put("strcopy", TokenId.FUNCTION);
        keywords.put("getcrc", TokenId.FUNCTION);

        keywords.put("lookup", TokenId.FUNCTION);
        keywords.put("lookupz", TokenId.FUNCTION);
        keywords.put("lookdown", TokenId.FUNCTION);
        keywords.put("lookdownz", TokenId.FUNCTION);

        keywords.put("asm", TokenId.KEYWORD);
        keywords.put("break", TokenId.KEYWORD);
        keywords.put("case", TokenId.KEYWORD);
        keywords.put("continue", TokenId.KEYWORD);
        keywords.put("default", TokenId.KEYWORD);
        keywords.put("do", TokenId.KEYWORD);
        keywords.put("else", TokenId.KEYWORD);
        keywords.put("for", TokenId.KEYWORD);
        keywords.put("if", TokenId.KEYWORD);
        keywords.put("return", TokenId.KEYWORD);
        keywords.put("select", TokenId.KEYWORD);
        keywords.put("until", TokenId.KEYWORD);
        keywords.put("while", TokenId.KEYWORD);

        keywords.put("abs", TokenId.KEYWORD);
        keywords.put("encod", TokenId.KEYWORD);
        keywords.put("decod", TokenId.KEYWORD);
        keywords.put("bmask", TokenId.KEYWORD);
        keywords.put("ones", TokenId.KEYWORD);
        keywords.put("sqrt", TokenId.KEYWORD);
        keywords.put("qlog", TokenId.KEYWORD);
        keywords.put("qexp", TokenId.KEYWORD);

        keywords.put("sar", TokenId.KEYWORD);
        keywords.put("ror", TokenId.KEYWORD);
        keywords.put("rol", TokenId.KEYWORD);
        keywords.put("rev", TokenId.KEYWORD);
        keywords.put("zerox", TokenId.KEYWORD);
        keywords.put("signx", TokenId.KEYWORD);
        keywords.put("sca", TokenId.KEYWORD);
        keywords.put("scas", TokenId.KEYWORD);
        keywords.put("frac", TokenId.KEYWORD);
        keywords.put("addpins", TokenId.KEYWORD);
        keywords.put("addbits", TokenId.KEYWORD);

        keywords.put("_CLKFREQ", TokenId.CONSTANT);
        keywords.put("_CLKMODE", TokenId.CONSTANT);
        keywords.put("CLKFREQ", TokenId.CONSTANT);
        keywords.put("CLKMODE", TokenId.CONSTANT);

        keywords.put("TRUE", TokenId.CONSTANT);
        keywords.put("FALSE", TokenId.CONSTANT);
        keywords.put("POSX", TokenId.CONSTANT);
        keywords.put("NEGX", TokenId.CONSTANT);
        keywords.put("PI", TokenId.CONSTANT);

        keywords.put("P_TRUE_A", TokenId.CONSTANT);
        keywords.put("P_INVERT_A", TokenId.CONSTANT);

        keywords.put("P_LOCAL_A", TokenId.CONSTANT);
        keywords.put("P_PLUS1_A", TokenId.CONSTANT);
        keywords.put("P_PLUS2_A", TokenId.CONSTANT);
        keywords.put("P_PLUS3_A", TokenId.CONSTANT);
        keywords.put("P_OUTBIT_A", TokenId.CONSTANT);
        keywords.put("P_MINUS3_A", TokenId.CONSTANT);
        keywords.put("P_MINUS2_A", TokenId.CONSTANT);
        keywords.put("P_MINUS1_A", TokenId.CONSTANT);
        keywords.put("P_TRUE_B", TokenId.CONSTANT);
        keywords.put("P_INVERT_B", TokenId.CONSTANT);
        keywords.put("P_LOCAL_B", TokenId.CONSTANT);
        keywords.put("P_PLUS1_B", TokenId.CONSTANT);
        keywords.put("P_PLUS2_B", TokenId.CONSTANT);
        keywords.put("P_PLUS3_B", TokenId.CONSTANT);
        keywords.put("P_OUTBIT_B", TokenId.CONSTANT);
        keywords.put("P_MINUS3_B", TokenId.CONSTANT);
        keywords.put("P_MINUS2_B", TokenId.CONSTANT);
        keywords.put("P_MINUS1_B", TokenId.CONSTANT);
        keywords.put("P_PASS_AB", TokenId.CONSTANT);
        keywords.put("P_AND_AB", TokenId.CONSTANT);
        keywords.put("P_OR_AB", TokenId.CONSTANT);
        keywords.put("P_XOR_AB", TokenId.CONSTANT);
        keywords.put("P_FILT0_AB", TokenId.CONSTANT);
        keywords.put("P_FILT1_AB", TokenId.CONSTANT);
        keywords.put("P_FILT2_AB", TokenId.CONSTANT);
        keywords.put("P_FILT3_AB", TokenId.CONSTANT);
        keywords.put("P_LOGIC_A", TokenId.CONSTANT);
        keywords.put("P_LOGIC_A_FB", TokenId.CONSTANT);
        keywords.put("P_LOGIC_B_FB", TokenId.CONSTANT);
        keywords.put("P_SCHMITT_A", TokenId.CONSTANT);
        keywords.put("P_SCHMITT_A_FB", TokenId.CONSTANT);
        keywords.put("P_SCHMITT_B_FB", TokenId.CONSTANT);
        keywords.put("P_COMPARE_AB", TokenId.CONSTANT);
        keywords.put("P_COMPARE_AB_FB", TokenId.CONSTANT);
        keywords.put("P_ADC_GIO", TokenId.CONSTANT);
        keywords.put("P_ADC_VIO", TokenId.CONSTANT);
        keywords.put("P_ADC_FLOAT", TokenId.CONSTANT);
        keywords.put("P_ADC_1X", TokenId.CONSTANT);
        keywords.put("P_ADC_3X", TokenId.CONSTANT);
        keywords.put("P_ADC_10X", TokenId.CONSTANT);
        keywords.put("P_ADC_30X", TokenId.CONSTANT);
        keywords.put("P_ADC_100X", TokenId.CONSTANT);
        keywords.put("P_DAC_990R_3V", TokenId.CONSTANT);
        keywords.put("P_DAC_600R_2V", TokenId.CONSTANT);
        keywords.put("P_DAC_124R_3V", TokenId.CONSTANT);
        keywords.put("P_DAC_75R_2V", TokenId.CONSTANT);
        keywords.put("P_CHANNEL", TokenId.CONSTANT);
        keywords.put("P_LEVEL_A", TokenId.CONSTANT);
        keywords.put("P_LEVEL_A_FBN", TokenId.CONSTANT);
        keywords.put("P_LEVEL_B_FBP", TokenId.CONSTANT);
        keywords.put("P_LEVEL_B_FBN", TokenId.CONSTANT);
        keywords.put("P_ASYNC_IO", TokenId.CONSTANT);
        keywords.put("P_SYNC_IO", TokenId.CONSTANT);
        keywords.put("P_TRUE_IN", TokenId.CONSTANT);
        keywords.put("P_INVERT_IN", TokenId.CONSTANT);
        keywords.put("P_TRUE_OUTPUT", TokenId.CONSTANT);
        keywords.put("P_INVERT_OUTPUT", TokenId.CONSTANT);
        keywords.put("P_HIGH_FAST", TokenId.CONSTANT);
        keywords.put("P_HIGH_1K5", TokenId.CONSTANT);
        keywords.put("P_HIGH_15K", TokenId.CONSTANT);
        keywords.put("P_HIGH_150K", TokenId.CONSTANT);
        keywords.put("P_HIGH_1MA", TokenId.CONSTANT);
        keywords.put("P_HIGH_100UA", TokenId.CONSTANT);
        keywords.put("P_HIGH_10UA", TokenId.CONSTANT);
        keywords.put("P_HIGH_FLOAT", TokenId.CONSTANT);
        keywords.put("P_LOW_FAST", TokenId.CONSTANT);
        keywords.put("P_LOW_1K5", TokenId.CONSTANT);
        keywords.put("P_LOW_15K", TokenId.CONSTANT);
        keywords.put("P_LOW_150K", TokenId.CONSTANT);
        keywords.put("P_LOW_1MA", TokenId.CONSTANT);
        keywords.put("P_LOW_100UA", TokenId.CONSTANT);
        keywords.put("P_LOW_10UA", TokenId.CONSTANT);
        keywords.put("P_LOW_FLOAT", TokenId.CONSTANT);
        keywords.put("P_TT_00", TokenId.CONSTANT);
        keywords.put("P_TT_01", TokenId.CONSTANT);
        keywords.put("P_TT_10", TokenId.CONSTANT);
        keywords.put("P_TT_11", TokenId.CONSTANT);
        keywords.put("P_OE", TokenId.CONSTANT);
        keywords.put("P_BITDAC", TokenId.CONSTANT);
        keywords.put("P_NORMAL", TokenId.CONSTANT);
        keywords.put("P_REPOSITORY", TokenId.CONSTANT);
        keywords.put("P_DAC_NOISE", TokenId.CONSTANT);
        keywords.put("P_DAC_DITHER_RND", TokenId.CONSTANT);
        keywords.put("P_DAC_DITHER_PWM", TokenId.CONSTANT);
        keywords.put("P_PULSE", TokenId.CONSTANT);
        keywords.put("P_TRANSITION", TokenId.CONSTANT);
        keywords.put("P_NCO_FREQ", TokenId.CONSTANT);
        keywords.put("P_NCO_DUTY", TokenId.CONSTANT);
        keywords.put("P_PWM_TRIANGLE", TokenId.CONSTANT);
        keywords.put("P_PWM_SAWTOOTH", TokenId.CONSTANT);
        keywords.put("P_PWM_SMPS", TokenId.CONSTANT);
        keywords.put("P_QUADRATURE", TokenId.CONSTANT);
        keywords.put("P_REG_UP", TokenId.CONSTANT);
        keywords.put("P_REG_UP_DOWN", TokenId.CONSTANT);
        keywords.put("P_COUNT_RISES", TokenId.CONSTANT);
        keywords.put("P_COUNT_HIGHS", TokenId.CONSTANT);

        keywords.put("P_STATE_TICKS", TokenId.CONSTANT);
        keywords.put("P_HIGH_TICKS", TokenId.CONSTANT);
        keywords.put("P_EVENTS_TICKS", TokenId.CONSTANT);
        keywords.put("P_PERIODS_TICKS", TokenId.CONSTANT);
        keywords.put("P_PERIODS_HIGHS", TokenId.CONSTANT);
        keywords.put("P_COUNTER_TICKS", TokenId.CONSTANT);
        keywords.put("P_COUNTER_HIGHS", TokenId.CONSTANT);
        keywords.put("P_COUNTER_PERIODS", TokenId.CONSTANT);

        keywords.put("P_ADC", TokenId.CONSTANT);
        keywords.put("P_ADC_EXT", TokenId.CONSTANT);
        keywords.put("P_ADC_SCOPE", TokenId.CONSTANT);
        keywords.put("P_USB_PAIR", TokenId.CONSTANT);
        keywords.put("P_SYNC_TX", TokenId.CONSTANT);
        keywords.put("P_SYNC_RX", TokenId.CONSTANT);
        keywords.put("P_ASYNC_TX", TokenId.CONSTANT);
        keywords.put("P_ASYNC_RX", TokenId.CONSTANT);

        keywords.put("X_IMM_32X1_LUT", TokenId.CONSTANT);
        keywords.put("X_IMM_16X2_LUT", TokenId.CONSTANT);
        keywords.put("X_IMM_8X4_LUT", TokenId.CONSTANT);
        keywords.put("X_IMM_4X8_LUT", TokenId.CONSTANT);

        keywords.put("X_IMM_32X1_1DAC1", TokenId.CONSTANT);
        keywords.put("X_IMM_16X2_2DAC1", TokenId.CONSTANT);
        keywords.put("X_IMM_16X2_1DAC2", TokenId.CONSTANT);
        keywords.put("X_IMM_8X4_4DAC1", TokenId.CONSTANT);
        keywords.put("X_IMM_8X4_2DAC2", TokenId.CONSTANT);
        keywords.put("X_IMM_8X4_1DAC4", TokenId.CONSTANT);

        keywords.put("X_IMM_4X8_4DAC2", TokenId.CONSTANT);
        keywords.put("X_IMM_4X8_2DAC4", TokenId.CONSTANT);
        keywords.put("X_IMM_4X8_1DAC8", TokenId.CONSTANT);
        keywords.put("X_IMM_2X16_4DAC4", TokenId.CONSTANT);

        keywords.put("X_IMM_2X16_2DAC8", TokenId.CONSTANT);
        keywords.put("X_IMM_1X32_4DAC8", TokenId.CONSTANT);

        keywords.put("X_RFLONG_32X1_LUT", TokenId.CONSTANT);
        keywords.put("X_RFLONG_16X2_LUT", TokenId.CONSTANT);
        keywords.put("X_RFLONG_8X4_LUT", TokenId.CONSTANT);
        keywords.put("X_RFLONG_4X8_LUT", TokenId.CONSTANT);

        keywords.put("X_RFBYTE_1P_1DAC1", TokenId.CONSTANT);
        keywords.put("X_RFBYTE_2P_2DAC1", TokenId.CONSTANT);
        keywords.put("X_RFBYTE_2P_1DAC2", TokenId.CONSTANT);
        keywords.put("X_RFBYTE_4P_4DAC1", TokenId.CONSTANT);
        keywords.put("X_RFBYTE_4P_2DAC2", TokenId.CONSTANT);
        keywords.put("X_RFBYTE_4P_1DAC4", TokenId.CONSTANT);
        keywords.put("X_RFBYTE_8P_4DAC2", TokenId.CONSTANT);
        keywords.put("X_RFBYTE_8P_2DAC4", TokenId.CONSTANT);
        keywords.put("X_RFBYTE_8P_1DAC8", TokenId.CONSTANT);
        keywords.put("X_RFWORD_16P_4DAC4", TokenId.CONSTANT);
        keywords.put("X_RFWORD_16P_2DAC8", TokenId.CONSTANT);
        keywords.put("X_RFLONG_32P_4DAC8", TokenId.CONSTANT);

        keywords.put("X_RFBYTE_LUMA8", TokenId.CONSTANT);
        keywords.put("X_RFBYTE_RGBI8", TokenId.CONSTANT);
        keywords.put("X_RFBYTE_RGB8", TokenId.CONSTANT);
        keywords.put("X_RFWORD_RGB16", TokenId.CONSTANT);
        keywords.put("X_RFLONG_RGB24", TokenId.CONSTANT);

        keywords.put("X_1P_1DAC1_WFBYTE", TokenId.CONSTANT);
        keywords.put("X_2P_2DAC1_WFBYTE", TokenId.CONSTANT);
        keywords.put("X_2P_1DAC2_WFBYTE", TokenId.CONSTANT);

        keywords.put("X_4P_4DAC1_WFBYTE", TokenId.CONSTANT);
        keywords.put("X_4P_2DAC2_WFBYTE", TokenId.CONSTANT);
        keywords.put("X_4P_1DAC4_WFBYTE", TokenId.CONSTANT);

        keywords.put("X_8P_4DAC2_WFBYTE", TokenId.CONSTANT);
        keywords.put("X_8P_2DAC4_WFBYTE", TokenId.CONSTANT);
        keywords.put("X_8P_1DAC8_WFBYTE", TokenId.CONSTANT);

        keywords.put("X_16P_4DAC4_WFWORD", TokenId.CONSTANT);
        keywords.put("X_16P_2DAC8_WFWORD", TokenId.CONSTANT);
        keywords.put("X_32P_4DAC8_WFLONG", TokenId.CONSTANT);

        keywords.put("X_1ADC8_0P_1DAC8_WFBYTE", TokenId.CONSTANT);
        keywords.put("X_1ADC8_8P_2DAC8_WFWORD", TokenId.CONSTANT);
        keywords.put("X_2ADC8_0P_2DAC8_WFWORD", TokenId.CONSTANT);
        keywords.put("X_2ADC8_16P_4DAC8_WFLONG", TokenId.CONSTANT);
        keywords.put("X_4ADC8_0P_4DAC8_WFLONG", TokenId.CONSTANT);

        keywords.put("X_DDS_GOERTZEL_SINC1", TokenId.CONSTANT);
        keywords.put("X_DDS_GOERTZEL_SINC2", TokenId.CONSTANT);

        keywords.put("X_DACS_OFF", TokenId.CONSTANT);
        keywords.put("X_DACS_0_0_0_0", TokenId.CONSTANT);
        keywords.put("X_DACS_X_X_0_0", TokenId.CONSTANT);
        keywords.put("X_DACS_0_0_X_X", TokenId.CONSTANT);
        keywords.put("X_DACS_X_X_X_0", TokenId.CONSTANT);
        keywords.put("X_DACS_X_X_0_X", TokenId.CONSTANT);
        keywords.put("X_DACS_X_0_X_X", TokenId.CONSTANT);
        keywords.put("X_DACS_0_X_X_X", TokenId.CONSTANT);

        keywords.put("X_DACS_0N0_0N0", TokenId.CONSTANT);
        keywords.put("X_DACS_X_X_0N0", TokenId.CONSTANT);
        keywords.put("X_DACS_0N0_X_X", TokenId.CONSTANT);
        keywords.put("X_DACS_1_0_1_0", TokenId.CONSTANT);
        keywords.put("X_DACS_X_X_1_0", TokenId.CONSTANT);
        keywords.put("X_DACS_1_0_X_X", TokenId.CONSTANT);
        keywords.put("X_DACS_1N1_0N0", TokenId.CONSTANT);
        keywords.put("X_DACS_3_2_1_0", TokenId.CONSTANT);

        keywords.put("X_PINS_OFF", TokenId.CONSTANT);
        keywords.put("X_PINS_ON", TokenId.CONSTANT);
        keywords.put("X_WRITE_OFF", TokenId.CONSTANT);
        keywords.put("X_WRITE_ON", TokenId.CONSTANT);
        keywords.put("X_ALT_OFF", TokenId.CONSTANT);
        keywords.put("X_ALT_ON", TokenId.CONSTANT);

        keywords.put("COGEXEC", TokenId.CONSTANT);
        keywords.put("COGEXEC_NEW", TokenId.CONSTANT);
        keywords.put("HUBEXEC", TokenId.CONSTANT);
        keywords.put("HUBEXEC_NEW", TokenId.CONSTANT);
        keywords.put("COGEXEC_NEW_PAIR", TokenId.CONSTANT);
        keywords.put("HUBEXEC_NEW_PAIR", TokenId.CONSTANT);

        keywords.put("NEWCOG", TokenId.CONSTANT);

        keywords.put("EVENT_INT", TokenId.CONSTANT);
        keywords.put("INT_OFF", TokenId.CONSTANT);
        keywords.put("EVENT_CT1", TokenId.CONSTANT);
        keywords.put("EVENT_CT2", TokenId.CONSTANT);
        keywords.put("EVENT_CT3", TokenId.CONSTANT);
        keywords.put("EVENT_SE1", TokenId.CONSTANT);
        keywords.put("EVENT_SE2", TokenId.CONSTANT);
        keywords.put("EVENT_SE3", TokenId.CONSTANT);
        keywords.put("EVENT_SE4", TokenId.CONSTANT);

        keywords.put("EVENT_PAT", TokenId.CONSTANT);
        keywords.put("EVENT_FBW", TokenId.CONSTANT);
        keywords.put("EVENT_XMT", TokenId.CONSTANT);
        keywords.put("EVENT_XFI", TokenId.CONSTANT);
        keywords.put("EVENT_XRO", TokenId.CONSTANT);
        keywords.put("EVENT_XRL", TokenId.CONSTANT);
        keywords.put("EVENT_ATN", TokenId.CONSTANT);
        keywords.put("EVENT_QMT", TokenId.CONSTANT);

        keywords.put("IJMP3", TokenId.PASM_INSTRUCTION);
        keywords.put("IRET3", TokenId.PASM_INSTRUCTION);
        keywords.put("IJMP2", TokenId.PASM_INSTRUCTION);
        keywords.put("IRET2", TokenId.PASM_INSTRUCTION);
        keywords.put("IJMP1", TokenId.PASM_INSTRUCTION);
        keywords.put("IRET1", TokenId.PASM_INSTRUCTION);
        keywords.put("PA", TokenId.PASM_INSTRUCTION);
        keywords.put("PB", TokenId.PASM_INSTRUCTION);
        keywords.put("PTRA", TokenId.PASM_INSTRUCTION);
        keywords.put("PTRB", TokenId.PASM_INSTRUCTION);
        keywords.put("DIRA", TokenId.PASM_INSTRUCTION);
        keywords.put("DIRB", TokenId.PASM_INSTRUCTION);
        keywords.put("OUTA", TokenId.PASM_INSTRUCTION);
        keywords.put("OUTB", TokenId.PASM_INSTRUCTION);
        keywords.put("INA", TokenId.PASM_INSTRUCTION);
        keywords.put("INB", TokenId.PASM_INSTRUCTION);
    }

    static Map<String, TokenId> spinKeywords = new CaseInsensitiveMap<>();
    static {
        spinKeywords.put("PR0", TokenId.PASM_INSTRUCTION);
        spinKeywords.put("PR1", TokenId.PASM_INSTRUCTION);
        spinKeywords.put("PR2", TokenId.PASM_INSTRUCTION);
        spinKeywords.put("PR3", TokenId.PASM_INSTRUCTION);
        spinKeywords.put("PR4", TokenId.PASM_INSTRUCTION);
        spinKeywords.put("PR5", TokenId.PASM_INSTRUCTION);
        spinKeywords.put("PR6", TokenId.PASM_INSTRUCTION);
        spinKeywords.put("PR7", TokenId.PASM_INSTRUCTION);
        spinKeywords.put("REG", TokenId.TYPE);
        spinKeywords.put("FIELD", TokenId.TYPE);
    }

    static Set<String> modcz = new HashSet<String>();
    static {
        modcz.add("MODC");
        modcz.add("MODZ");
        modcz.add("MODCZ");
    }

    static Map<String, TokenId> modczOperands = new CaseInsensitiveMap<>();
    static {
        modczOperands.put("_CLR", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NC_AND_NZ", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NZ_AND_NC", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_GT", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NC_AND_Z", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_Z_AND_NC", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NC", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_GE", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_C_AND_NZ", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NZ_AND_C", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NZ", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NE", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_C_NE_Z", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_Z_NE_C", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NC_OR_NZ", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NZ_OR_NC", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_C_AND_Z", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_Z_AND_C", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_C_EQ_Z", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_Z_EQ_C", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_Z", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_E", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NC_OR_Z", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_Z_OR_NC", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_C", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_LT", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_C_OR_NZ", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NZ_OR_C", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_C_OR_Z", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_Z_OR_C", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_LE", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_SET", TokenId.PASM_INSTRUCTION);
    }

    static Map<String, TokenId> debugKeywords = new CaseInsensitiveMap<>();
    static {
        debugKeywords.put("DEBUG", TokenId.KEYWORD);

        debugKeywords.put("FDEC", TokenId.FUNCTION);
        debugKeywords.put("FDEC_REG_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("FDEC_ARRAY", TokenId.FUNCTION);

        debugKeywords.put("UDEC", TokenId.FUNCTION);
        debugKeywords.put("UDEC_BYTE", TokenId.FUNCTION);
        debugKeywords.put("UDEC_WORD", TokenId.FUNCTION);
        debugKeywords.put("UDEC_LONG", TokenId.FUNCTION);
        debugKeywords.put("SDEC", TokenId.FUNCTION);
        debugKeywords.put("SDEC_BYTE", TokenId.FUNCTION);
        debugKeywords.put("SDEC_WORD", TokenId.FUNCTION);
        debugKeywords.put("SDEC_LONG", TokenId.FUNCTION);
        debugKeywords.put("UHEX", TokenId.FUNCTION);
        debugKeywords.put("UHEX_BYTE", TokenId.FUNCTION);
        debugKeywords.put("UHEX_WORD", TokenId.FUNCTION);
        debugKeywords.put("UHEX_LONG", TokenId.FUNCTION);
        debugKeywords.put("SHEX", TokenId.FUNCTION);
        debugKeywords.put("SHEX_BYTE", TokenId.FUNCTION);
        debugKeywords.put("SHEX_WORD", TokenId.FUNCTION);
        debugKeywords.put("SHEX_LONG", TokenId.FUNCTION);
        debugKeywords.put("UBIN", TokenId.FUNCTION);
        debugKeywords.put("UBIN_BYTE", TokenId.FUNCTION);
        debugKeywords.put("UBIN_WORD", TokenId.FUNCTION);
        debugKeywords.put("UBIN_LONG", TokenId.FUNCTION);
        debugKeywords.put("SBIN", TokenId.FUNCTION);
        debugKeywords.put("SBIN_BYTE", TokenId.FUNCTION);
        debugKeywords.put("SBIN_WORD", TokenId.FUNCTION);
        debugKeywords.put("SBIN_LONG", TokenId.FUNCTION);

        debugKeywords.put("UDEC_", TokenId.FUNCTION);
        debugKeywords.put("UDEC_BYTE_", TokenId.FUNCTION);
        debugKeywords.put("UDEC_WORD_", TokenId.FUNCTION);
        debugKeywords.put("UDEC_LONG_", TokenId.FUNCTION);
        debugKeywords.put("SDEC_", TokenId.FUNCTION);
        debugKeywords.put("SDEC_BYTE_", TokenId.FUNCTION);
        debugKeywords.put("SDEC_WORD_", TokenId.FUNCTION);
        debugKeywords.put("SDEC_LONG_", TokenId.FUNCTION);
        debugKeywords.put("UHEX_", TokenId.FUNCTION);
        debugKeywords.put("UHEX_BYTE_", TokenId.FUNCTION);
        debugKeywords.put("UHEX_WORD_", TokenId.FUNCTION);
        debugKeywords.put("UHEX_LONG_", TokenId.FUNCTION);
        debugKeywords.put("SHEX_", TokenId.FUNCTION);
        debugKeywords.put("SHEX_BYTE_", TokenId.FUNCTION);
        debugKeywords.put("SHEX_WORD_", TokenId.FUNCTION);
        debugKeywords.put("SHEX_LONG_", TokenId.FUNCTION);
        debugKeywords.put("UBIN_", TokenId.FUNCTION);
        debugKeywords.put("UBIN_BYTE_", TokenId.FUNCTION);
        debugKeywords.put("UBIN_WORD_", TokenId.FUNCTION);
        debugKeywords.put("UBIN_LONG_", TokenId.FUNCTION);
        debugKeywords.put("SBIN_", TokenId.FUNCTION);
        debugKeywords.put("SBIN_BYTE_", TokenId.FUNCTION);
        debugKeywords.put("SBIN_WORD_", TokenId.FUNCTION);
        debugKeywords.put("SBIN_LONG_", TokenId.FUNCTION);

        debugKeywords.put("UDEC_REG_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("UDEC_BYTE_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("UDEC_WORD_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("UDEC_LONG_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("SDEC_REG_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("SDEC_BYTE_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("SDEC_WORD_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("SDEC_LONG_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("UHEX_REG_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("UHEX_BYTE_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("UHEX_WORD_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("UHEX_LONG_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("SHEX_REG_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("SHEX_BYTE_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("SHEX_WORD_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("SHEX_LONG_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("UBIN_REG_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("UBIN_BYTE_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("UBIN_WORD_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("UBIN_LONG_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("SBIN_REG_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("SBIN_BYTE_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("SBIN_WORD_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("SBIN_LONG_ARRAY", TokenId.FUNCTION);

        debugKeywords.put("UDEC_REG_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("UDEC_BYTE_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("UDEC_WORD_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("UDEC_LONG_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("SDEC_REG_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("SDEC_BYTE_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("SDEC_WORD_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("SDEC_LONG_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("UHEX_REG_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("UHEX_BYTE_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("UHEX_WORD_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("UHEX_LONG_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("SHEX_REG_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("SHEX_BYTE_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("SHEX_WORD_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("SHEX_LONG_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("UBIN_REG_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("UBIN_BYTE_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("UBIN_WORD_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("UBIN_LONG_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("SBIN_REG_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("SBIN_BYTE_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("SBIN_WORD_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("SBIN_LONG_ARRAY_", TokenId.FUNCTION);

        debugKeywords.put("DLY", TokenId.FUNCTION);
        debugKeywords.put("ZSTR", TokenId.FUNCTION);
        debugKeywords.put("LSTR", TokenId.FUNCTION);

        debugKeywords.put("IF", TokenId.FUNCTION);
        debugKeywords.put("IFNOT", TokenId.FUNCTION);
    }

    Map<String, String> alias = new HashMap<>();

    CTokenMarker() {
        super(SourceProvider.NULL);
        this.constantSeparator = "";
    }

    public CTokenMarker(SourceProvider sourceProvider) {
        super(sourceProvider);
        this.constantSeparator = "";
    }

    final NodeVisitor collectKeywordsVisitor = new NodeVisitor() {

        String lastLabel = "";

        @Override
        public void visitDirective(DirectiveNode node) {
            int index = 0;
            if (index < node.getTokenCount()) {
                tokens.add(new TokenMarker(node.getToken(index++), TokenId.DIRECTIVE));
            }
            if (index < node.getTokenCount()) {
                tokens.add(new TokenMarker(node.getToken(index++), TokenId.DIRECTIVE));
            }
            if (node instanceof DirectiveNode.IncludeNode) {
                DirectiveNode.IncludeNode include = (DirectiveNode.IncludeNode) node;
                if (include.getFile() != null) {
                    String name = include.getFile().getText().substring(1, include.getFile().getText().length() - 1);
                    if (name.toLowerCase().endsWith(".spin2") || name.toLowerCase().endsWith(".c")) {
                        name = name.substring(0, name.lastIndexOf('.'));
                    }

                    String fileName = include.getFile().getText().substring(1, include.getFile().getText().length() - 1);
                    Node objectRoot = getObjectTree(fileName);
                    if (objectRoot != null) {
                        for (Node child : objectRoot.getChilds()) {
                            if (child instanceof DirectiveNode.DefineNode) {
                                DirectiveNode.DefineNode define = (DirectiveNode.DefineNode) node;
                                if (define.getIdentifier() != null) {
                                    symbols.put(define.getIdentifier().getText(), TokenId.CONSTANT);
                                }
                            }
                            else if (child instanceof ConstantNode) {
                                ConstantNode constant = (ConstantNode) child;
                                if (constant.getIdentifier() != null) {
                                    symbols.put(constant.getIdentifier().getText(), TokenId.CONSTANT);
                                }
                            }
                        }
                    }
                }
            }
            else if (node instanceof DirectiveNode.DefineNode) {
                DirectiveNode.DefineNode define = (DirectiveNode.DefineNode) node;
                if (define.getIdentifier() != null) {
                    symbols.put(define.getIdentifier().getText(), TokenId.CONSTANT);
                    tokens.add(new TokenMarker(define.getIdentifier(), TokenId.CONSTANT));
                }
            }
        }

        @Override
        public void visitVariable(VariableNode node) {
            if (node.getModifier() != null) {
                tokens.add(new TokenMarker(node.getModifier(), TokenId.TYPE));
            }
            if (node.getType() != null) {
                tokens.add(new TokenMarker(node.getType(), TokenId.TYPE));
            }

            if (node.getIdentifier() != null) {
                String identifier = node.getIdentifier().getText();
                symbols.put(identifier, TokenId.VARIABLE);
                tokens.add(new TokenMarker(node.getIdentifier(), TokenId.VARIABLE));
            }

            String objectName = node.getType().getText();
            if (symbols.get(objectName) == null) {
                Node objectRoot = getObjectTree(objectName);
                if (objectRoot != null) {
                    symbols.put(objectName, TokenId.TYPE);
                    String objectPrefix = objectName + ".";
                    for (Node child : objectRoot.getChilds()) {
                        if (child instanceof MethodNode) {
                            MethodNode methodNode = (MethodNode) child;
                            if ("PUB".equalsIgnoreCase(methodNode.type.getText())) {
                                if (methodNode.name != null) {
                                    symbols.put(objectPrefix + methodNode.name.getText(), TokenId.METHOD_PUB);
                                }
                            }
                        }
                        else if (child instanceof FunctionNode) {
                            FunctionNode functionNode = (FunctionNode) child;
                            if (functionNode.getModifier() == null || !"static".equals(functionNode.getModifier().getText())) {
                                if (functionNode.getIdentifier() != null) {
                                    symbols.put(objectPrefix + functionNode.getIdentifier().getText(), TokenId.METHOD_PUB);
                                }
                            }
                        }
                    }
                }
            }

            if (node.getType() != null && node.getIdentifier() != null) {
                if (symbols.get(node.getType().getText()) == TokenId.TYPE) {
                    alias.put(node.getIdentifier().getText(), node.getType().getText());
                }
            }
        }

        @Override
        public boolean visitFunction(FunctionNode node) {
            TokenId id = TokenId.METHOD_PUB;
            if (!node.isPublic()) {
                id = TokenId.METHOD_PRI;
            }
            tokens.add(new TokenMarker(node.getType(), id));

            if (node.getIdentifier() != null) {
                symbols.put(node.getIdentifier().getText(), id);
                tokens.add(new TokenMarker(node.getIdentifier(), id));
            }

            for (Node child : node.getParameters()) {
                FunctionNode.ParameterNode param = (FunctionNode.ParameterNode) child;
                if (param.type != null) {
                    tokens.add(new TokenMarker(param.type, TokenId.TYPE));
                }
                if (param.identifier != null) {
                    tokens.add(new TokenMarker(param.identifier, TokenId.METHOD_LOCAL));
                    symbols.put(param.identifier.getText(), TokenId.METHOD_LOCAL);
                }
            }

            return true;
        }

        @Override
        public void visitDataLine(DataLineNode node) {
            if (node.label != null) {
                String s = node.label.getText();
                if (s.startsWith(".")) {
                    symbols.put(lastLabel + s, TokenId.PASM_LOCAL_LABEL);
                    symbols.put(lastLabel + "@" + s, TokenId.PASM_LOCAL_LABEL);
                    symbols.put(lastLabel + "@@" + s, TokenId.PASM_LOCAL_LABEL);
                    tokens.add(new TokenMarker(node.label, TokenId.PASM_LOCAL_LABEL));
                }
                else {
                    TokenId id = symbols.get(s);
                    if (id == null) {
                        symbols.put(s, TokenId.PASM_LABEL);
                        symbols.put("@" + s, TokenId.PASM_LABEL);
                        symbols.put("@@" + s, TokenId.PASM_LABEL);
                        tokens.add(new TokenMarker(node.label, TokenId.PASM_LABEL));
                    }
                    lastLabel = s;
                }
            }
            if (node.condition != null) {
                tokens.add(new TokenMarker(node.condition, TokenId.PASM_CONDITION));
            }
            if (node.instruction != null) {
                TokenId id = keywords.get(node.instruction.getText().toUpperCase());
                if (id == null || id != TokenId.TYPE) {
                    if (Spin2Model.isPAsmInstruction(node.instruction.getText())) {
                        id = TokenId.PASM_INSTRUCTION;
                    }
                }
                if ("debug".equalsIgnoreCase(node.instruction.getText())) {
                    id = TokenId.KEYWORD;
                }
                tokens.add(new TokenMarker(node.instruction, id));
                if ("debug".equalsIgnoreCase(node.instruction.getText())) {
                    for (int i = 1; i < node.getTokens().size(); i++) {
                        Token token = node.getToken(i);
                        if (token.type == Token.NUMBER) {
                            tokens.add(new TokenMarker(token, TokenId.NUMBER));
                        }
                        else if (token.type == Token.OPERATOR) {
                            tokens.add(new TokenMarker(token, TokenId.OPERATOR));
                        }
                        else if (token.type == Token.STRING) {
                            tokens.add(new TokenMarker(token, TokenId.STRING));
                        }
                        else {
                            id = debugKeywords.get(token.getText().toUpperCase());
                            if (id != null) {
                                tokens.add(new TokenMarker(token, id));
                            }
                        }
                    }
                }
            }
            if (node.modifier != null) {
                tokens.add(new TokenMarker(node.modifier, TokenId.PASM_MODIFIER));
            }
        }

    };

    final NodeVisitor updateReferencesVisitor = new NodeVisitor() {

        String lastLabel = "";

        @Override
        public void visitDirective(DirectiveNode node) {
            Iterator<Token> iter = node.getTokens().iterator();
            iter.next();
            if (iter.hasNext()) {
                Token directive = iter.next();
                if (!"define".equals(directive.getText())) {
                    markExpression(iter);
                }
            }
        }

        @Override
        public void visitVariable(VariableNode node) {
            markTokens(node, 0, "");
            for (Node child : node.getChilds()) {
                markTokens(child, 0, "");
            }
        }

        @Override
        public void visitObject(ObjectNode node) {
            if (node.count != null) {
                markTokens(node.count, 0, "");
            }
        }

        @Override
        public boolean visitFunction(FunctionNode node) {
            locals.clear();

            for (Node child : node.getParameters()) {
                locals.put(child.getText(), TokenId.METHOD_LOCAL);
            }
            for (Node child : node.getReturnVariables()) {
                locals.put(child.getText(), TokenId.METHOD_RETURN);
            }

            for (FunctionNode.LocalVariableNode child : node.getLocalVariables()) {
                if (child.type != null) {
                    tokens.add(new TokenMarker(child.type, TokenId.TYPE));
                }
                if (child.identifier != null) {
                    locals.put(child.identifier.getText(), TokenId.METHOD_LOCAL);
                    tokens.add(new TokenMarker(child.identifier, TokenId.METHOD_LOCAL));
                }
            }

            for (Node child : node.getChilds()) {
                markTokens(child, 0, "");
            }

            return false;
        }

        int markTokens(Node node, int i, String endMarker) {
            List<Token> list = node.getTokens();
            boolean debug = list.size() != 0 && "debug".equalsIgnoreCase(list.get(0).getText());

            while (i < list.size()) {
                Token token = list.get(i++);
                if (token.type == Token.NUMBER) {
                    tokens.add(new TokenMarker(token, TokenId.NUMBER));
                }
                else if (token.type == Token.OPERATOR) {
                    tokens.add(new TokenMarker(token, TokenId.OPERATOR));
                    if (token.getText().equals(endMarker)) {
                        return i;
                    }
                    if (token.getText().equals("[")) {
                        i = markTokens(node, i, "]");
                        if (i < list.size()) {
                            token = list.get(i);
                        }
                    }
                    if (token.getText().equals("(")) {
                        i = markTokens(node, i, ")");
                        if (i < list.size()) {
                            token = list.get(i);
                        }
                    }
                }
                else if (token.type == Token.STRING) {
                    tokens.add(new TokenMarker(token, TokenId.STRING));
                }
                else {
                    int dot = token.getText().indexOf('.');
                    TokenId id = debug ? debugKeywords.get(token.getText().toUpperCase()) : keywords.get(token.getText().toUpperCase());
                    if (id == null) {
                        id = spinKeywords.get(token.getText().toUpperCase());
                    }
                    if (id == null) {
                        id = locals.get(token.getText());
                        if (id == null && token.getText().startsWith("&")) {
                            id = locals.get(token.getText().substring(1));
                        }
                    }
                    if (id == null) {
                        id = symbols.get(token.getText());
                        if (id == null && token.getText().startsWith("&")) {
                            id = symbols.get(token.getText().substring(1));
                        }
                    }
                    if (id == null) {
                        id = compilerSymbols.get(token.getText());
                        if (id == null && token.getText().startsWith("&")) {
                            id = compilerSymbols.get(token.getText().substring(1));
                        }
                    }
                    if (id == null && dot != -1) {
                        String type = token.getText().substring(0, dot);
                        String object = alias.get(type);
                        if (object != null) {
                            id = symbols.get(object + token.getText().substring(dot));
                        }
                    }
                    if (id != null) {
                        if ((id == TokenId.METHOD_PUB || id == TokenId.CONSTANT) && dot != -1) {
                            tokens.add(new TokenMarker(token.start, token.start + dot - 1, TokenId.OBJECT));
                            tokens.add(new TokenMarker(token.start + dot + 1, token.stop, id));
                        }
                        else {
                            tokens.add(new TokenMarker(token, id));
                            if (id == TokenId.OBJECT && i < list.size()) {
                                Token objToken = token;
                                token = list.get(i);
                                if (token.getText().equals("[")) {
                                    tokens.add(new TokenMarker(token, TokenId.OPERATOR));
                                    i = markTokens(node, i + 1, "]");
                                    if (i < list.size()) {
                                        token = list.get(i);
                                    }
                                }
                                if (token.getText().startsWith(".")) {
                                    String qualifiedName = objToken.getText() + token.getText();
                                    id = symbols.get(qualifiedName);
                                    if (id == null && qualifiedName.startsWith("&")) {
                                        id = symbols.get(qualifiedName.substring(1));
                                    }
                                    if (id == null) {
                                        id = compilerSymbols.get(qualifiedName);
                                        if (id == null && qualifiedName.startsWith("&")) {
                                            id = compilerSymbols.get(qualifiedName.substring(1));
                                        }
                                    }
                                    if (id != null) {
                                        tokens.add(new TokenMarker(token, id));
                                    }
                                    i++;
                                }
                            }
                        }
                    }
                }
            }
            for (Node child : node.getChilds()) {
                markTokens(child, 0, "");
            }
            return i;
        }

        @Override
        public boolean visitData(DataNode node) {
            lastLabel = "";
            for (Node child : node.getChilds()) {
                markDataTokens((DataLineNode) child, false);
            }
            return true;
        }

        public void markDataTokens(DataLineNode node, boolean inline) {
            if (node.label != null) {
                String s = node.label.getText();
                if (!s.startsWith(".")) {
                    lastLabel = s;
                }
            }

            boolean isModcz = node.instruction != null && modcz.contains(node.instruction.getText().toUpperCase());

            for (DataLineNode.ParameterNode parameter : node.parameters) {
                for (Token token : parameter.getTokens()) {
                    TokenId id = null;
                    if (token.type == Token.NUMBER) {
                        tokens.add(new TokenMarker(token, TokenId.NUMBER));
                    }
                    else if (token.type == Token.OPERATOR) {
                        tokens.add(new TokenMarker(token, TokenId.OPERATOR));
                    }
                    else if (token.type == Token.STRING) {
                        tokens.add(new TokenMarker(token, TokenId.STRING));
                    }
                    else {
                        String s = token.getText();
                        if (isModcz) {
                            id = modczOperands.get(token.getText().toUpperCase());
                        }
                        if (id == null) {
                            if (s.startsWith(".") || s.startsWith("@.") || s.startsWith("@@.")) {
                                s = lastLabel + s;
                            }
                            id = symbols.get(s);
                        }
                        if (id == null) {
                            id = compilerSymbols.get(token.getText());
                        }
                        if (id == null) {
                            id = keywords.get(token.getText().toUpperCase());
                        }
                        if (inline && id == null) {
                            if (isModcz) {
                                id = modczOperands.get(token.getText().toUpperCase());
                            }
                            if (id == null) {
                                id = spinKeywords.get(token.getText().toUpperCase());
                            }
                            if (id == null) {
                                id = locals.get(token.getText());
                            }
                        }
                        if (id != null) {
                            if (id == TokenId.CONSTANT && token.getText().contains(".")) {
                                int dot = token.getText().indexOf('.');
                                tokens.add(new TokenMarker(token.start, token.start + dot - 1, TokenId.OBJECT));
                                tokens.add(new TokenMarker(token.start + dot + 1, token.stop, id));
                            }
                            else {
                                tokens.add(new TokenMarker(token, id));
                            }
                        }
                    }
                }
            }
        }

        void markExpression(Iterator<Token> iter) {
            while (iter.hasNext()) {
                Token token = iter.next();
                if (token.type == Token.NUMBER) {
                    tokens.add(new TokenMarker(token, TokenId.NUMBER));
                }
                else if (token.type == Token.OPERATOR) {
                    tokens.add(new TokenMarker(token, TokenId.OPERATOR));
                }
                else if (token.type == Token.STRING) {
                    tokens.add(new TokenMarker(token, TokenId.STRING));
                }
                else {
                    TokenId id = keywords.get(token.getText().toUpperCase());
                    if (id == null) {
                        id = symbols.get(token.getText());
                    }
                    if (id == null) {
                        id = compilerSymbols.get(token.getText());
                    }
                    if (id != null) {
                        if (id == TokenId.CONSTANT && token.getText().contains(".")) {
                            int dot = token.getText().indexOf('.');
                            tokens.add(new TokenMarker(token.start, token.start + dot - 1, TokenId.OBJECT));
                            tokens.add(new TokenMarker(token.start + dot + 1, token.stop, id));
                        }
                        else {
                            tokens.add(new TokenMarker(token, id));
                        }
                    }
                }
            }
        }

    };

    @Override
    public void refreshTokens(String text) {
        tokens.clear();
        symbols.clear();
        compilerTokens.clear();

        CTokenStream stream = new CTokenStream(text);
        CParser subject = new CParser(stream);
        root = subject.parse();

        for (Token token : root.getComments()) {
            tokens.add(new TokenMarker(token, TokenId.COMMENT));
        }

        root.accept(collectKeywordsVisitor);
        root.accept(updateReferencesVisitor);
    }

    @Override
    public void refreshCompilerTokens(List<CompilerException> messages) {
        tokens.clear();
        symbols.clear();
        compilerTokens.clear();

        for (Token token : root.getComments()) {
            tokens.add(new TokenMarker(token, TokenId.COMMENT));
        }

        root.accept(collectKeywordsVisitor);
        root.accept(updateReferencesVisitor);

        super.refreshCompilerTokens(messages);
    }

}
