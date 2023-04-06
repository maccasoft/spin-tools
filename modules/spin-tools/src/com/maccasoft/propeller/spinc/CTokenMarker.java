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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.SourceTokenMarker;
import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.ConstantsNode;
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
import com.maccasoft.propeller.spin1.Spin1Model;
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
    }

    static Map<String, TokenId> spin1Keywords = new CaseInsensitiveMap<>();
    static {
        spin1Keywords.put("@BYTE", TokenId.TYPE);
        spin1Keywords.put("@WORD", TokenId.TYPE);
        spin1Keywords.put("@LONG", TokenId.TYPE);
        spin1Keywords.put("@@BYTE", TokenId.TYPE);
        spin1Keywords.put("@@WORD", TokenId.TYPE);
        spin1Keywords.put("@@LONG", TokenId.TYPE);
        spin1Keywords.put("BYTEFIT", TokenId.TYPE);
        spin1Keywords.put("WORDFIT", TokenId.TYPE);

        spin1Keywords.put("abort", TokenId.KEYWORD);
        spin1Keywords.put("bytefill", TokenId.FUNCTION);
        spin1Keywords.put("bytemove", TokenId.FUNCTION);
        spin1Keywords.put("chipver", TokenId.FUNCTION);
        spin1Keywords.put("clkfreq", TokenId.FUNCTION);
        spin1Keywords.put("_clkfreq", TokenId.CONSTANT);
        spin1Keywords.put("clkmode", TokenId.FUNCTION);
        spin1Keywords.put("_clkmode", TokenId.CONSTANT);
        spin1Keywords.put("clkset", TokenId.FUNCTION);
        spin1Keywords.put("cogid", TokenId.FUNCTION);
        spin1Keywords.put("coginit", TokenId.FUNCTION);
        spin1Keywords.put("cognew", TokenId.FUNCTION);
        spin1Keywords.put("cogstop", TokenId.FUNCTION);
        spin1Keywords.put("constant", TokenId.FUNCTION);
        spin1Keywords.put("float", TokenId.FUNCTION);
        spin1Keywords.put("file", TokenId.KEYWORD);
        spin1Keywords.put("float", TokenId.FUNCTION);
        spin1Keywords.put("lockclr", TokenId.FUNCTION);
        spin1Keywords.put("locknew", TokenId.FUNCTION);
        spin1Keywords.put("lockret", TokenId.FUNCTION);
        spin1Keywords.put("lockset", TokenId.FUNCTION);
        spin1Keywords.put("longfill", TokenId.FUNCTION);
        spin1Keywords.put("longmove", TokenId.FUNCTION);
        spin1Keywords.put("lookdown", TokenId.FUNCTION);
        spin1Keywords.put("lookdownz", TokenId.FUNCTION);
        spin1Keywords.put("lookup", TokenId.FUNCTION);
        spin1Keywords.put("lookupz", TokenId.FUNCTION);
        spin1Keywords.put("reboot", TokenId.KEYWORD);
        spin1Keywords.put("result", TokenId.METHOD_RETURN);
        spin1Keywords.put("@result", TokenId.METHOD_RETURN);
        spin1Keywords.put("strcomp", TokenId.FUNCTION);
        spin1Keywords.put("string", TokenId.FUNCTION);
        spin1Keywords.put("strsize", TokenId.FUNCTION);
        spin1Keywords.put("trunc", TokenId.FUNCTION);
        spin1Keywords.put("waitcnt", TokenId.FUNCTION);
        spin1Keywords.put("waitpeq", TokenId.FUNCTION);
        spin1Keywords.put("waitpne", TokenId.FUNCTION);
        spin1Keywords.put("waitvid", TokenId.FUNCTION);
        spin1Keywords.put("wordfill", TokenId.FUNCTION);
        spin1Keywords.put("wordmove", TokenId.FUNCTION);

        spin1Keywords.put("_CLKFREQ", TokenId.CONSTANT);
        spin1Keywords.put("_CLKMODE", TokenId.CONSTANT);
        spin1Keywords.put("_XINFREQ", TokenId.CONSTANT);
        spin1Keywords.put("_FREE", TokenId.CONSTANT);
        spin1Keywords.put("_STACK", TokenId.CONSTANT);

        spin1Keywords.put("RCFAST", TokenId.CONSTANT);
        spin1Keywords.put("RCSLOW", TokenId.CONSTANT);
        spin1Keywords.put("XINPUT", TokenId.CONSTANT);
        spin1Keywords.put("XTAL1", TokenId.CONSTANT);
        spin1Keywords.put("XTAL2", TokenId.CONSTANT);
        spin1Keywords.put("XTAL3", TokenId.CONSTANT);
        spin1Keywords.put("PLL1X", TokenId.CONSTANT);
        spin1Keywords.put("PLL2X", TokenId.CONSTANT);
        spin1Keywords.put("PLL4X", TokenId.CONSTANT);
        spin1Keywords.put("PLL8X", TokenId.CONSTANT);
        spin1Keywords.put("PLL16X", TokenId.CONSTANT);

        spin1Keywords.put("TRUE", TokenId.CONSTANT);
        spin1Keywords.put("FALSE", TokenId.CONSTANT);
        spin1Keywords.put("POSX", TokenId.CONSTANT);
        spin1Keywords.put("NEGX", TokenId.CONSTANT);
        spin1Keywords.put("PI", TokenId.CONSTANT);

        spin1Keywords.put("DIRA", TokenId.KEYWORD);
        spin1Keywords.put("DIRB", TokenId.KEYWORD);
        spin1Keywords.put("INA", TokenId.KEYWORD);
        spin1Keywords.put("INB", TokenId.KEYWORD);
        spin1Keywords.put("OUTA", TokenId.KEYWORD);
        spin1Keywords.put("OUTB", TokenId.KEYWORD);
        spin1Keywords.put("CNT", TokenId.KEYWORD);
        spin1Keywords.put("CTRA", TokenId.KEYWORD);
        spin1Keywords.put("CTRB", TokenId.KEYWORD);
        spin1Keywords.put("FRQA", TokenId.KEYWORD);
        spin1Keywords.put("FRQB", TokenId.KEYWORD);
        spin1Keywords.put("PHSA", TokenId.KEYWORD);
        spin1Keywords.put("PHSB", TokenId.KEYWORD);
        spin1Keywords.put("VCFG", TokenId.KEYWORD);
        spin1Keywords.put("VSCL", TokenId.KEYWORD);
        spin1Keywords.put("PAR", TokenId.KEYWORD);
        spin1Keywords.put("SPR", TokenId.KEYWORD);
    }

    static Map<String, TokenId> pasmKeywords = new CaseInsensitiveMap<>();
    static {
        pasmKeywords.put("CLKFREQ", TokenId.CONSTANT);
        pasmKeywords.put("CLKMODE", TokenId.CONSTANT);

        pasmKeywords.put("DIRA", TokenId.KEYWORD);
        pasmKeywords.put("DIRB", TokenId.KEYWORD);
        pasmKeywords.put("INA", TokenId.KEYWORD);
        pasmKeywords.put("INB", TokenId.KEYWORD);
        pasmKeywords.put("OUTA", TokenId.KEYWORD);
        pasmKeywords.put("OUTB", TokenId.KEYWORD);
        pasmKeywords.put("CNT", TokenId.KEYWORD);
        pasmKeywords.put("CTRA", TokenId.KEYWORD);
        pasmKeywords.put("CTRB", TokenId.KEYWORD);
        pasmKeywords.put("FRQA", TokenId.KEYWORD);
        pasmKeywords.put("FRQB", TokenId.KEYWORD);
        pasmKeywords.put("PHSA", TokenId.KEYWORD);
        pasmKeywords.put("PHSB", TokenId.KEYWORD);
        pasmKeywords.put("VCFG", TokenId.KEYWORD);
        pasmKeywords.put("VSCL", TokenId.KEYWORD);
        pasmKeywords.put("PAR", TokenId.KEYWORD);
        pasmKeywords.put("SPR", TokenId.KEYWORD);
    }

    static Map<String, TokenId> spin1MethodKeywords = new CaseInsensitiveMap<>();
    static {
        spin1MethodKeywords.put("@BYTE", TokenId.TYPE);
        spin1MethodKeywords.put("@WORD", TokenId.TYPE);
        spin1MethodKeywords.put("@LONG", TokenId.TYPE);
        spin1MethodKeywords.put("@@BYTE", TokenId.TYPE);
        spin1MethodKeywords.put("@@WORD", TokenId.TYPE);
        spin1MethodKeywords.put("@@LONG", TokenId.TYPE);
    }

    static Map<String, TokenId> spin2Keywords = new CaseInsensitiveMap<>();
    static {
        spin2Keywords.put("bytefit", TokenId.TYPE);
        spin2Keywords.put("wordfit", TokenId.TYPE);

        spin2Keywords.put("hubset", TokenId.FUNCTION);
        spin2Keywords.put("clkset", TokenId.FUNCTION);
        spin2Keywords.put("cogspin", TokenId.FUNCTION);
        spin2Keywords.put("coginit", TokenId.FUNCTION);
        spin2Keywords.put("cogstop", TokenId.FUNCTION);
        spin2Keywords.put("cogid", TokenId.FUNCTION);
        spin2Keywords.put("cogchk", TokenId.FUNCTION);
        spin2Keywords.put("locknew", TokenId.FUNCTION);
        spin2Keywords.put("lockret", TokenId.FUNCTION);
        spin2Keywords.put("locktry", TokenId.FUNCTION);
        spin2Keywords.put("lockrel", TokenId.FUNCTION);
        spin2Keywords.put("lockchk", TokenId.FUNCTION);
        spin2Keywords.put("cogatn", TokenId.FUNCTION);
        spin2Keywords.put("pollatn", TokenId.FUNCTION);
        spin2Keywords.put("waitatn", TokenId.FUNCTION);

        spin2Keywords.put("pinw", TokenId.FUNCTION);
        spin2Keywords.put("pinwrite", TokenId.FUNCTION);
        spin2Keywords.put("pinl", TokenId.FUNCTION);
        spin2Keywords.put("pinlow", TokenId.FUNCTION);
        spin2Keywords.put("pinh", TokenId.FUNCTION);
        spin2Keywords.put("pinhigh", TokenId.FUNCTION);
        spin2Keywords.put("pint", TokenId.FUNCTION);
        spin2Keywords.put("pintoggle", TokenId.FUNCTION);
        spin2Keywords.put("pinf", TokenId.FUNCTION);
        spin2Keywords.put("pinfloat", TokenId.FUNCTION);
        spin2Keywords.put("pinr", TokenId.FUNCTION);
        spin2Keywords.put("pinread", TokenId.FUNCTION);
        spin2Keywords.put("pinstart", TokenId.FUNCTION);
        spin2Keywords.put("pinclear", TokenId.FUNCTION);
        spin2Keywords.put("wrpin", TokenId.FUNCTION);
        spin2Keywords.put("wxpin", TokenId.FUNCTION);
        spin2Keywords.put("wypin", TokenId.FUNCTION);
        spin2Keywords.put("akpin", TokenId.FUNCTION);
        spin2Keywords.put("rdpin", TokenId.FUNCTION);
        spin2Keywords.put("rqpin", TokenId.FUNCTION);

        spin2Keywords.put("getct", TokenId.FUNCTION);
        spin2Keywords.put("pollct", TokenId.FUNCTION);
        spin2Keywords.put("waitct", TokenId.FUNCTION);
        spin2Keywords.put("waitus", TokenId.FUNCTION);
        spin2Keywords.put("waitms", TokenId.FUNCTION);
        spin2Keywords.put("getsec", TokenId.FUNCTION);
        spin2Keywords.put("getms", TokenId.FUNCTION);

        spin2Keywords.put("call", TokenId.FUNCTION);
        spin2Keywords.put("regexec", TokenId.FUNCTION);
        spin2Keywords.put("regload", TokenId.FUNCTION);

        spin2Keywords.put("rotxy", TokenId.FUNCTION);
        spin2Keywords.put("polxy", TokenId.FUNCTION);
        spin2Keywords.put("xypol", TokenId.FUNCTION);
        spin2Keywords.put("qsin", TokenId.FUNCTION);
        spin2Keywords.put("qcos", TokenId.FUNCTION);
        spin2Keywords.put("muldiv64", TokenId.FUNCTION);
        spin2Keywords.put("getisn", TokenId.FUNCTION);
        spin2Keywords.put("nan", TokenId.FUNCTION);

        spin2Keywords.put("getregs", TokenId.FUNCTION);
        spin2Keywords.put("setregs", TokenId.FUNCTION);
        spin2Keywords.put("bytemove", TokenId.FUNCTION);
        spin2Keywords.put("wordmove", TokenId.FUNCTION);
        spin2Keywords.put("longmove", TokenId.FUNCTION);
        spin2Keywords.put("bytefill", TokenId.FUNCTION);
        spin2Keywords.put("wordfill", TokenId.FUNCTION);
        spin2Keywords.put("longfill", TokenId.FUNCTION);

        spin2Keywords.put("string", TokenId.FUNCTION);
        spin2Keywords.put("strsize", TokenId.FUNCTION);
        spin2Keywords.put("strcomp", TokenId.FUNCTION);
        spin2Keywords.put("strcopy", TokenId.FUNCTION);
        spin2Keywords.put("getcrc", TokenId.FUNCTION);

        spin2Keywords.put("lookup", TokenId.FUNCTION);
        spin2Keywords.put("lookupz", TokenId.FUNCTION);
        spin2Keywords.put("lookdown", TokenId.FUNCTION);
        spin2Keywords.put("lookdownz", TokenId.FUNCTION);

        spin2Keywords.put("asm", TokenId.KEYWORD);

        spin2Keywords.put("abs", TokenId.KEYWORD);
        spin2Keywords.put("encod", TokenId.KEYWORD);
        spin2Keywords.put("decod", TokenId.KEYWORD);
        spin2Keywords.put("bmask", TokenId.KEYWORD);
        spin2Keywords.put("ones", TokenId.KEYWORD);
        spin2Keywords.put("sqrt", TokenId.KEYWORD);
        spin2Keywords.put("qlog", TokenId.KEYWORD);
        spin2Keywords.put("qexp", TokenId.KEYWORD);

        spin2Keywords.put("sar", TokenId.KEYWORD);
        spin2Keywords.put("ror", TokenId.KEYWORD);
        spin2Keywords.put("rol", TokenId.KEYWORD);
        spin2Keywords.put("rev", TokenId.KEYWORD);
        spin2Keywords.put("zerox", TokenId.KEYWORD);
        spin2Keywords.put("signx", TokenId.KEYWORD);
        spin2Keywords.put("sca", TokenId.KEYWORD);
        spin2Keywords.put("scas", TokenId.KEYWORD);
        spin2Keywords.put("frac", TokenId.KEYWORD);
        spin2Keywords.put("addpins", TokenId.KEYWORD);
        spin2Keywords.put("addbits", TokenId.KEYWORD);

        spin2Keywords.put("_CLKFREQ", TokenId.CONSTANT);
        spin2Keywords.put("_CLKMODE", TokenId.CONSTANT);
        spin2Keywords.put("CLKFREQ", TokenId.CONSTANT);
        spin2Keywords.put("CLKMODE", TokenId.CONSTANT);

        spin2Keywords.put("TRUE", TokenId.CONSTANT);
        spin2Keywords.put("FALSE", TokenId.CONSTANT);
        spin2Keywords.put("POSX", TokenId.CONSTANT);
        spin2Keywords.put("NEGX", TokenId.CONSTANT);
        spin2Keywords.put("PI", TokenId.CONSTANT);

        spin2Keywords.put("P_TRUE_A", TokenId.CONSTANT);
        spin2Keywords.put("P_INVERT_A", TokenId.CONSTANT);

        spin2Keywords.put("P_LOCAL_A", TokenId.CONSTANT);
        spin2Keywords.put("P_PLUS1_A", TokenId.CONSTANT);
        spin2Keywords.put("P_PLUS2_A", TokenId.CONSTANT);
        spin2Keywords.put("P_PLUS3_A", TokenId.CONSTANT);
        spin2Keywords.put("P_OUTBIT_A", TokenId.CONSTANT);
        spin2Keywords.put("P_MINUS3_A", TokenId.CONSTANT);
        spin2Keywords.put("P_MINUS2_A", TokenId.CONSTANT);
        spin2Keywords.put("P_MINUS1_A", TokenId.CONSTANT);
        spin2Keywords.put("P_TRUE_B", TokenId.CONSTANT);
        spin2Keywords.put("P_INVERT_B", TokenId.CONSTANT);
        spin2Keywords.put("P_LOCAL_B", TokenId.CONSTANT);
        spin2Keywords.put("P_PLUS1_B", TokenId.CONSTANT);
        spin2Keywords.put("P_PLUS2_B", TokenId.CONSTANT);
        spin2Keywords.put("P_PLUS3_B", TokenId.CONSTANT);
        spin2Keywords.put("P_OUTBIT_B", TokenId.CONSTANT);
        spin2Keywords.put("P_MINUS3_B", TokenId.CONSTANT);
        spin2Keywords.put("P_MINUS2_B", TokenId.CONSTANT);
        spin2Keywords.put("P_MINUS1_B", TokenId.CONSTANT);
        spin2Keywords.put("P_PASS_AB", TokenId.CONSTANT);
        spin2Keywords.put("P_AND_AB", TokenId.CONSTANT);
        spin2Keywords.put("P_OR_AB", TokenId.CONSTANT);
        spin2Keywords.put("P_XOR_AB", TokenId.CONSTANT);
        spin2Keywords.put("P_FILT0_AB", TokenId.CONSTANT);
        spin2Keywords.put("P_FILT1_AB", TokenId.CONSTANT);
        spin2Keywords.put("P_FILT2_AB", TokenId.CONSTANT);
        spin2Keywords.put("P_FILT3_AB", TokenId.CONSTANT);
        spin2Keywords.put("P_LOGIC_A", TokenId.CONSTANT);
        spin2Keywords.put("P_LOGIC_A_FB", TokenId.CONSTANT);
        spin2Keywords.put("P_LOGIC_B_FB", TokenId.CONSTANT);
        spin2Keywords.put("P_SCHMITT_A", TokenId.CONSTANT);
        spin2Keywords.put("P_SCHMITT_A_FB", TokenId.CONSTANT);
        spin2Keywords.put("P_SCHMITT_B_FB", TokenId.CONSTANT);
        spin2Keywords.put("P_COMPARE_AB", TokenId.CONSTANT);
        spin2Keywords.put("P_COMPARE_AB_FB", TokenId.CONSTANT);
        spin2Keywords.put("P_ADC_GIO", TokenId.CONSTANT);
        spin2Keywords.put("P_ADC_VIO", TokenId.CONSTANT);
        spin2Keywords.put("P_ADC_FLOAT", TokenId.CONSTANT);
        spin2Keywords.put("P_ADC_1X", TokenId.CONSTANT);
        spin2Keywords.put("P_ADC_3X", TokenId.CONSTANT);
        spin2Keywords.put("P_ADC_10X", TokenId.CONSTANT);
        spin2Keywords.put("P_ADC_30X", TokenId.CONSTANT);
        spin2Keywords.put("P_ADC_100X", TokenId.CONSTANT);
        spin2Keywords.put("P_DAC_990R_3V", TokenId.CONSTANT);
        spin2Keywords.put("P_DAC_600R_2V", TokenId.CONSTANT);
        spin2Keywords.put("P_DAC_124R_3V", TokenId.CONSTANT);
        spin2Keywords.put("P_DAC_75R_2V", TokenId.CONSTANT);
        spin2Keywords.put("P_CHANNEL", TokenId.CONSTANT);
        spin2Keywords.put("P_LEVEL_A", TokenId.CONSTANT);
        spin2Keywords.put("P_LEVEL_A_FBN", TokenId.CONSTANT);
        spin2Keywords.put("P_LEVEL_B_FBP", TokenId.CONSTANT);
        spin2Keywords.put("P_LEVEL_B_FBN", TokenId.CONSTANT);
        spin2Keywords.put("P_ASYNC_IO", TokenId.CONSTANT);
        spin2Keywords.put("P_SYNC_IO", TokenId.CONSTANT);
        spin2Keywords.put("P_TRUE_IN", TokenId.CONSTANT);
        spin2Keywords.put("P_INVERT_IN", TokenId.CONSTANT);
        spin2Keywords.put("P_TRUE_OUTPUT", TokenId.CONSTANT);
        spin2Keywords.put("P_INVERT_OUTPUT", TokenId.CONSTANT);
        spin2Keywords.put("P_HIGH_FAST", TokenId.CONSTANT);
        spin2Keywords.put("P_HIGH_1K5", TokenId.CONSTANT);
        spin2Keywords.put("P_HIGH_15K", TokenId.CONSTANT);
        spin2Keywords.put("P_HIGH_150K", TokenId.CONSTANT);
        spin2Keywords.put("P_HIGH_1MA", TokenId.CONSTANT);
        spin2Keywords.put("P_HIGH_100UA", TokenId.CONSTANT);
        spin2Keywords.put("P_HIGH_10UA", TokenId.CONSTANT);
        spin2Keywords.put("P_HIGH_FLOAT", TokenId.CONSTANT);
        spin2Keywords.put("P_LOW_FAST", TokenId.CONSTANT);
        spin2Keywords.put("P_LOW_1K5", TokenId.CONSTANT);
        spin2Keywords.put("P_LOW_15K", TokenId.CONSTANT);
        spin2Keywords.put("P_LOW_150K", TokenId.CONSTANT);
        spin2Keywords.put("P_LOW_1MA", TokenId.CONSTANT);
        spin2Keywords.put("P_LOW_100UA", TokenId.CONSTANT);
        spin2Keywords.put("P_LOW_10UA", TokenId.CONSTANT);
        spin2Keywords.put("P_LOW_FLOAT", TokenId.CONSTANT);
        spin2Keywords.put("P_TT_00", TokenId.CONSTANT);
        spin2Keywords.put("P_TT_01", TokenId.CONSTANT);
        spin2Keywords.put("P_TT_10", TokenId.CONSTANT);
        spin2Keywords.put("P_TT_11", TokenId.CONSTANT);
        spin2Keywords.put("P_OE", TokenId.CONSTANT);
        spin2Keywords.put("P_BITDAC", TokenId.CONSTANT);
        spin2Keywords.put("P_NORMAL", TokenId.CONSTANT);
        spin2Keywords.put("P_REPOSITORY", TokenId.CONSTANT);
        spin2Keywords.put("P_DAC_NOISE", TokenId.CONSTANT);
        spin2Keywords.put("P_DAC_DITHER_RND", TokenId.CONSTANT);
        spin2Keywords.put("P_DAC_DITHER_PWM", TokenId.CONSTANT);
        spin2Keywords.put("P_PULSE", TokenId.CONSTANT);
        spin2Keywords.put("P_TRANSITION", TokenId.CONSTANT);
        spin2Keywords.put("P_NCO_FREQ", TokenId.CONSTANT);
        spin2Keywords.put("P_NCO_DUTY", TokenId.CONSTANT);
        spin2Keywords.put("P_PWM_TRIANGLE", TokenId.CONSTANT);
        spin2Keywords.put("P_PWM_SAWTOOTH", TokenId.CONSTANT);
        spin2Keywords.put("P_PWM_SMPS", TokenId.CONSTANT);
        spin2Keywords.put("P_QUADRATURE", TokenId.CONSTANT);
        spin2Keywords.put("P_REG_UP", TokenId.CONSTANT);
        spin2Keywords.put("P_REG_UP_DOWN", TokenId.CONSTANT);
        spin2Keywords.put("P_COUNT_RISES", TokenId.CONSTANT);
        spin2Keywords.put("P_COUNT_HIGHS", TokenId.CONSTANT);

        spin2Keywords.put("P_STATE_TICKS", TokenId.CONSTANT);
        spin2Keywords.put("P_HIGH_TICKS", TokenId.CONSTANT);
        spin2Keywords.put("P_EVENTS_TICKS", TokenId.CONSTANT);
        spin2Keywords.put("P_PERIODS_TICKS", TokenId.CONSTANT);
        spin2Keywords.put("P_PERIODS_HIGHS", TokenId.CONSTANT);
        spin2Keywords.put("P_COUNTER_TICKS", TokenId.CONSTANT);
        spin2Keywords.put("P_COUNTER_HIGHS", TokenId.CONSTANT);
        spin2Keywords.put("P_COUNTER_PERIODS", TokenId.CONSTANT);

        spin2Keywords.put("P_ADC", TokenId.CONSTANT);
        spin2Keywords.put("P_ADC_EXT", TokenId.CONSTANT);
        spin2Keywords.put("P_ADC_SCOPE", TokenId.CONSTANT);
        spin2Keywords.put("P_USB_PAIR", TokenId.CONSTANT);
        spin2Keywords.put("P_SYNC_TX", TokenId.CONSTANT);
        spin2Keywords.put("P_SYNC_RX", TokenId.CONSTANT);
        spin2Keywords.put("P_ASYNC_TX", TokenId.CONSTANT);
        spin2Keywords.put("P_ASYNC_RX", TokenId.CONSTANT);

        spin2Keywords.put("X_IMM_32X1_LUT", TokenId.CONSTANT);
        spin2Keywords.put("X_IMM_16X2_LUT", TokenId.CONSTANT);
        spin2Keywords.put("X_IMM_8X4_LUT", TokenId.CONSTANT);
        spin2Keywords.put("X_IMM_4X8_LUT", TokenId.CONSTANT);

        spin2Keywords.put("X_IMM_32X1_1DAC1", TokenId.CONSTANT);
        spin2Keywords.put("X_IMM_16X2_2DAC1", TokenId.CONSTANT);
        spin2Keywords.put("X_IMM_16X2_1DAC2", TokenId.CONSTANT);
        spin2Keywords.put("X_IMM_8X4_4DAC1", TokenId.CONSTANT);
        spin2Keywords.put("X_IMM_8X4_2DAC2", TokenId.CONSTANT);
        spin2Keywords.put("X_IMM_8X4_1DAC4", TokenId.CONSTANT);

        spin2Keywords.put("X_IMM_4X8_4DAC2", TokenId.CONSTANT);
        spin2Keywords.put("X_IMM_4X8_2DAC4", TokenId.CONSTANT);
        spin2Keywords.put("X_IMM_4X8_1DAC8", TokenId.CONSTANT);
        spin2Keywords.put("X_IMM_2X16_4DAC4", TokenId.CONSTANT);

        spin2Keywords.put("X_IMM_2X16_2DAC8", TokenId.CONSTANT);
        spin2Keywords.put("X_IMM_1X32_4DAC8", TokenId.CONSTANT);

        spin2Keywords.put("X_RFLONG_32X1_LUT", TokenId.CONSTANT);
        spin2Keywords.put("X_RFLONG_16X2_LUT", TokenId.CONSTANT);
        spin2Keywords.put("X_RFLONG_8X4_LUT", TokenId.CONSTANT);
        spin2Keywords.put("X_RFLONG_4X8_LUT", TokenId.CONSTANT);

        spin2Keywords.put("X_RFBYTE_1P_1DAC1", TokenId.CONSTANT);
        spin2Keywords.put("X_RFBYTE_2P_2DAC1", TokenId.CONSTANT);
        spin2Keywords.put("X_RFBYTE_2P_1DAC2", TokenId.CONSTANT);
        spin2Keywords.put("X_RFBYTE_4P_4DAC1", TokenId.CONSTANT);
        spin2Keywords.put("X_RFBYTE_4P_2DAC2", TokenId.CONSTANT);
        spin2Keywords.put("X_RFBYTE_4P_1DAC4", TokenId.CONSTANT);
        spin2Keywords.put("X_RFBYTE_8P_4DAC2", TokenId.CONSTANT);
        spin2Keywords.put("X_RFBYTE_8P_2DAC4", TokenId.CONSTANT);
        spin2Keywords.put("X_RFBYTE_8P_1DAC8", TokenId.CONSTANT);
        spin2Keywords.put("X_RFWORD_16P_4DAC4", TokenId.CONSTANT);
        spin2Keywords.put("X_RFWORD_16P_2DAC8", TokenId.CONSTANT);
        spin2Keywords.put("X_RFLONG_32P_4DAC8", TokenId.CONSTANT);

        spin2Keywords.put("X_RFBYTE_LUMA8", TokenId.CONSTANT);
        spin2Keywords.put("X_RFBYTE_RGBI8", TokenId.CONSTANT);
        spin2Keywords.put("X_RFBYTE_RGB8", TokenId.CONSTANT);
        spin2Keywords.put("X_RFWORD_RGB16", TokenId.CONSTANT);
        spin2Keywords.put("X_RFLONG_RGB24", TokenId.CONSTANT);

        spin2Keywords.put("X_1P_1DAC1_WFBYTE", TokenId.CONSTANT);
        spin2Keywords.put("X_2P_2DAC1_WFBYTE", TokenId.CONSTANT);
        spin2Keywords.put("X_2P_1DAC2_WFBYTE", TokenId.CONSTANT);

        spin2Keywords.put("X_4P_4DAC1_WFBYTE", TokenId.CONSTANT);
        spin2Keywords.put("X_4P_2DAC2_WFBYTE", TokenId.CONSTANT);
        spin2Keywords.put("X_4P_1DAC4_WFBYTE", TokenId.CONSTANT);

        spin2Keywords.put("X_8P_4DAC2_WFBYTE", TokenId.CONSTANT);
        spin2Keywords.put("X_8P_2DAC4_WFBYTE", TokenId.CONSTANT);
        spin2Keywords.put("X_8P_1DAC8_WFBYTE", TokenId.CONSTANT);

        spin2Keywords.put("X_16P_4DAC4_WFWORD", TokenId.CONSTANT);
        spin2Keywords.put("X_16P_2DAC8_WFWORD", TokenId.CONSTANT);
        spin2Keywords.put("X_32P_4DAC8_WFLONG", TokenId.CONSTANT);

        spin2Keywords.put("X_1ADC8_0P_1DAC8_WFBYTE", TokenId.CONSTANT);
        spin2Keywords.put("X_1ADC8_8P_2DAC8_WFWORD", TokenId.CONSTANT);
        spin2Keywords.put("X_2ADC8_0P_2DAC8_WFWORD", TokenId.CONSTANT);
        spin2Keywords.put("X_2ADC8_16P_4DAC8_WFLONG", TokenId.CONSTANT);
        spin2Keywords.put("X_4ADC8_0P_4DAC8_WFLONG", TokenId.CONSTANT);

        spin2Keywords.put("X_DDS_GOERTZEL_SINC1", TokenId.CONSTANT);
        spin2Keywords.put("X_DDS_GOERTZEL_SINC2", TokenId.CONSTANT);

        spin2Keywords.put("X_DACS_OFF", TokenId.CONSTANT);
        spin2Keywords.put("X_DACS_0_0_0_0", TokenId.CONSTANT);
        spin2Keywords.put("X_DACS_X_X_0_0", TokenId.CONSTANT);
        spin2Keywords.put("X_DACS_0_0_X_X", TokenId.CONSTANT);
        spin2Keywords.put("X_DACS_X_X_X_0", TokenId.CONSTANT);
        spin2Keywords.put("X_DACS_X_X_0_X", TokenId.CONSTANT);
        spin2Keywords.put("X_DACS_X_0_X_X", TokenId.CONSTANT);
        spin2Keywords.put("X_DACS_0_X_X_X", TokenId.CONSTANT);

        spin2Keywords.put("X_DACS_0N0_0N0", TokenId.CONSTANT);
        spin2Keywords.put("X_DACS_X_X_0N0", TokenId.CONSTANT);
        spin2Keywords.put("X_DACS_0N0_X_X", TokenId.CONSTANT);
        spin2Keywords.put("X_DACS_1_0_1_0", TokenId.CONSTANT);
        spin2Keywords.put("X_DACS_X_X_1_0", TokenId.CONSTANT);
        spin2Keywords.put("X_DACS_1_0_X_X", TokenId.CONSTANT);
        spin2Keywords.put("X_DACS_1N1_0N0", TokenId.CONSTANT);
        spin2Keywords.put("X_DACS_3_2_1_0", TokenId.CONSTANT);

        spin2Keywords.put("X_PINS_OFF", TokenId.CONSTANT);
        spin2Keywords.put("X_PINS_ON", TokenId.CONSTANT);
        spin2Keywords.put("X_WRITE_OFF", TokenId.CONSTANT);
        spin2Keywords.put("X_WRITE_ON", TokenId.CONSTANT);
        spin2Keywords.put("X_ALT_OFF", TokenId.CONSTANT);
        spin2Keywords.put("X_ALT_ON", TokenId.CONSTANT);

        spin2Keywords.put("COGEXEC", TokenId.CONSTANT);
        spin2Keywords.put("COGEXEC_NEW", TokenId.CONSTANT);
        spin2Keywords.put("HUBEXEC", TokenId.CONSTANT);
        spin2Keywords.put("HUBEXEC_NEW", TokenId.CONSTANT);
        spin2Keywords.put("COGEXEC_NEW_PAIR", TokenId.CONSTANT);
        spin2Keywords.put("HUBEXEC_NEW_PAIR", TokenId.CONSTANT);

        spin2Keywords.put("NEWCOG", TokenId.CONSTANT);

        spin2Keywords.put("EVENT_INT", TokenId.CONSTANT);
        spin2Keywords.put("INT_OFF", TokenId.CONSTANT);
        spin2Keywords.put("EVENT_CT1", TokenId.CONSTANT);
        spin2Keywords.put("EVENT_CT2", TokenId.CONSTANT);
        spin2Keywords.put("EVENT_CT3", TokenId.CONSTANT);
        spin2Keywords.put("EVENT_SE1", TokenId.CONSTANT);
        spin2Keywords.put("EVENT_SE2", TokenId.CONSTANT);
        spin2Keywords.put("EVENT_SE3", TokenId.CONSTANT);
        spin2Keywords.put("EVENT_SE4", TokenId.CONSTANT);

        spin2Keywords.put("EVENT_PAT", TokenId.CONSTANT);
        spin2Keywords.put("EVENT_FBW", TokenId.CONSTANT);
        spin2Keywords.put("EVENT_XMT", TokenId.CONSTANT);
        spin2Keywords.put("EVENT_XFI", TokenId.CONSTANT);
        spin2Keywords.put("EVENT_XRO", TokenId.CONSTANT);
        spin2Keywords.put("EVENT_XRL", TokenId.CONSTANT);
        spin2Keywords.put("EVENT_ATN", TokenId.CONSTANT);
        spin2Keywords.put("EVENT_QMT", TokenId.CONSTANT);

        spin2Keywords.put("IJMP3", TokenId.PASM_INSTRUCTION);
        spin2Keywords.put("IRET3", TokenId.PASM_INSTRUCTION);
        spin2Keywords.put("IJMP2", TokenId.PASM_INSTRUCTION);
        spin2Keywords.put("IRET2", TokenId.PASM_INSTRUCTION);
        spin2Keywords.put("IJMP1", TokenId.PASM_INSTRUCTION);
        spin2Keywords.put("IRET1", TokenId.PASM_INSTRUCTION);
        spin2Keywords.put("PA", TokenId.PASM_INSTRUCTION);
        spin2Keywords.put("PB", TokenId.PASM_INSTRUCTION);
        spin2Keywords.put("PTRA", TokenId.PASM_INSTRUCTION);
        spin2Keywords.put("PTRB", TokenId.PASM_INSTRUCTION);
        spin2Keywords.put("DIRA", TokenId.PASM_INSTRUCTION);
        spin2Keywords.put("DIRB", TokenId.PASM_INSTRUCTION);
        spin2Keywords.put("OUTA", TokenId.PASM_INSTRUCTION);
        spin2Keywords.put("OUTB", TokenId.PASM_INSTRUCTION);
        spin2Keywords.put("INA", TokenId.PASM_INSTRUCTION);
        spin2Keywords.put("INB", TokenId.PASM_INSTRUCTION);
    }

    static Map<String, TokenId> spin2MethodKeywords = new CaseInsensitiveMap<>();
    static {
        spin2MethodKeywords.put("PR0", TokenId.PASM_INSTRUCTION);
        spin2MethodKeywords.put("PR1", TokenId.PASM_INSTRUCTION);
        spin2MethodKeywords.put("PR2", TokenId.PASM_INSTRUCTION);
        spin2MethodKeywords.put("PR3", TokenId.PASM_INSTRUCTION);
        spin2MethodKeywords.put("PR4", TokenId.PASM_INSTRUCTION);
        spin2MethodKeywords.put("PR5", TokenId.PASM_INSTRUCTION);
        spin2MethodKeywords.put("PR6", TokenId.PASM_INSTRUCTION);
        spin2MethodKeywords.put("PR7", TokenId.PASM_INSTRUCTION);
        spin2MethodKeywords.put("REG", TokenId.TYPE);
        spin2MethodKeywords.put("FIELD", TokenId.TYPE);
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

    final NodeVisitor collectKeywordsVisitor = new NodeVisitor() {

        String lastLabel = "";
        boolean hasTarget;

        @Override
        public void visitDirective(DirectiveNode node) {
            int index = 0;
            if (index < node.getTokenCount()) {
                tokens.add(new TokenMarker(node.getToken(index++), TokenId.DIRECTIVE));
            }
            if (index < node.getTokenCount()) {
                if ("pragma".equals(node.getToken(index).getText())) {
                    tokens.add(new TokenMarker(node.getToken(index++), TokenId.DIRECTIVE));
                    if (index < node.getTokenCount()) {
                        if ("target".equals(node.getToken(index).getText())) {
                            index++;
                            if (index < node.getTokenCount()) {
                                if ("P1".equals(node.getToken(index).getText())) {
                                    if (!p1) {
                                        setP1(true);
                                    }
                                }
                                else if ("P2".equals(node.getToken(index).getText())) {
                                    if (p1) {
                                        setP1(false);
                                    }
                                }
                            }
                            hasTarget = true;
                        }
                    }
                }
                else {
                    tokens.add(new TokenMarker(node.getToken(index++), TokenId.DIRECTIVE));
                }
            }
            if (node instanceof DirectiveNode.IncludeNode) {
                DirectiveNode.IncludeNode include = (DirectiveNode.IncludeNode) node;
                if (include.getFile() != null) {
                    tokens.add(new TokenMarker(include.getFile(), TokenId.STRING));

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
                            else if (child instanceof ConstantsNode) {
                                for (Node n : child.getChilds()) {
                                    ConstantNode constant = (ConstantNode) n;
                                    if (constant.getIdentifier() != null) {
                                        symbols.put(constant.getIdentifier().getText(), TokenId.CONSTANT);
                                    }
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

            if (!hasTarget && p1) {
                setP1(false);
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
            if (p1) {
                visitSpin1DataLine(node);
            }
            else {
                visitSpin2DataLine(node);
            }
        }

        public void visitSpin1DataLine(DataLineNode node) {
            if (node.label != null) {
                String s = node.label.getText();
                if (s.startsWith(":")) {
                    symbols.put(lastLabel + s, TokenId.PASM_LOCAL_LABEL);
                    symbols.put(lastLabel + "@" + s, TokenId.PASM_LOCAL_LABEL);
                    symbols.put(lastLabel + "@@" + s, TokenId.PASM_LOCAL_LABEL);
                    tokens.add(new TokenMarker(node.label, TokenId.PASM_LOCAL_LABEL));
                }
                else {
                    symbols.put(s, TokenId.PASM_LABEL);
                    symbols.put("@" + s, TokenId.PASM_LABEL);
                    symbols.put("@@" + s, TokenId.PASM_LABEL);
                    tokens.add(new TokenMarker(node.label, TokenId.PASM_LABEL));
                    lastLabel = s;
                }
            }
            if (node.condition != null) {
                tokens.add(new TokenMarker(node.condition, TokenId.PASM_CONDITION));
            }
            if (node.instruction != null) {
                TokenId id = spin1Keywords.get(node.instruction.getText());
                if (id == null || id != TokenId.TYPE) {
                    if (Spin1Model.isPAsmInstruction(node.instruction.getText())) {
                        id = TokenId.PASM_INSTRUCTION;
                    }
                }
                tokens.add(new TokenMarker(node.instruction, id));
            }
            if (node.modifier != null) {
                tokens.add(new TokenMarker(node.modifier, TokenId.PASM_MODIFIER));
            }
        }

        public void visitSpin2DataLine(DataLineNode node) {
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
                TokenId id = spin2Keywords.get(node.instruction.getText().toUpperCase());
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

    final NodeVisitor updateSpin1ReferencesVisitor = new NodeVisitor() {

        String lastLabel = "";

        @Override
        public void visitVariable(VariableNode node) {
            markTokens(node, 0, "");
            for (Node child : node.getChilds()) {
                markTokens(child, 0, "");
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

            while (i < list.size()) {
                Token token = list.get(i++);
                if (token.type == Token.NUMBER || token.type == Token.CHAR) {
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
                    TokenId id = keywords.get(token.getText().toUpperCase());
                    if (id == null) {
                        id = spin1Keywords.get(token.getText().toUpperCase());
                    }
                    if (id == null) {
                        id = spin1MethodKeywords.get(token.getText().toUpperCase());
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
            return true;
        }

        @Override
        public void visitDataLine(DataLineNode node) {
            if (node.label != null) {
                String s = node.label.getText();
                if (!s.startsWith(":")) {
                    lastLabel = s;
                }
            }

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
                        if (s.startsWith(":") || s.startsWith("@:") || s.startsWith("@@:")) {
                            s = lastLabel + s;
                        }
                        id = pasmKeywords.get(token.getText());
                        if (id == null) {
                            id = symbols.get(s);
                        }
                        if (id == null) {
                            id = compilerSymbols.get(token.getText());
                        }
                        if (id == null) {
                            id = spin1Keywords.get(token.getText());
                        }
                        if (id != null) {
                            if (id == TokenId.CONSTANT && token.getText().contains("#")) {
                                int dot = token.getText().indexOf('#');
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

    };

    final NodeVisitor updateSpin2ReferencesVisitor = new NodeVisitor() {

        String lastLabel = "";

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
                    TokenId id = keywords.get(token.getText().toUpperCase());
                    if (id == null) {
                        id = spin2Keywords.get(token.getText().toUpperCase());
                    }
                    if (id == null) {
                        id = spin2MethodKeywords.get(token.getText().toUpperCase());
                    }
                    if (id == null && debug) {
                        id = debugKeywords.get(token.getText().toUpperCase());
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
                            id = spin2Keywords.get(token.getText().toUpperCase());
                        }
                        if (inline && id == null) {
                            if (isModcz) {
                                id = modczOperands.get(token.getText().toUpperCase());
                            }
                            if (id == null) {
                                id = spin2MethodKeywords.get(token.getText().toUpperCase());
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

    };

    protected boolean p1;

    CTokenMarker() {
        super(SourceProvider.NULL);
        this.constantSeparator = "";
    }

    public CTokenMarker(SourceProvider sourceProvider) {
        super(sourceProvider);
        this.constantSeparator = "";
    }

    public boolean isP1() {
        return p1;
    }

    public boolean isP2() {
        return !p1;
    }

    protected void setP1(boolean p1) {
        this.p1 = p1;
    }

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
        if (isP1()) {
            root.accept(updateSpin1ReferencesVisitor);
        }
        else {
            root.accept(updateSpin2ReferencesVisitor);
        }
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
        if (isP1()) {
            root.accept(updateSpin1ReferencesVisitor);
        }
        else {
            root.accept(updateSpin2ReferencesVisitor);
        }

        super.refreshCompilerTokens(messages);
    }

}
