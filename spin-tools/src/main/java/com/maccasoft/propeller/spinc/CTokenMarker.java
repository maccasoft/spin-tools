/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.maccasoft.propeller.SourceTokenMarker;
import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.FunctionNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.RootNode;
import com.maccasoft.propeller.model.SourceProvider;
import com.maccasoft.propeller.model.StructNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenStream;
import com.maccasoft.propeller.model.VariableNode;

public class CTokenMarker extends SourceTokenMarker {

    static final Pattern pragmaTargetPattern = Pattern.compile("/\\*[\\s\\S]*?\\*/|//.*|\"([^\"\\\\]|\\\\.)*\"|(#pragma\\s+target\\s+P1|P2)");

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

        keywords.put("static", TokenId.TYPE);
        keywords.put("struct", TokenId.TYPE);
        keywords.put("sizeof", TokenId.KEYWORD);

        keywords.put("BYTE", TokenId.TYPE);
        keywords.put("WORD", TokenId.TYPE);
        keywords.put("LONG", TokenId.TYPE);
        keywords.put("@BYTE", TokenId.TYPE);
        keywords.put("@WORD", TokenId.TYPE);
        keywords.put("@LONG", TokenId.TYPE);
        keywords.put("@@BYTE", TokenId.TYPE);
        keywords.put("@@WORD", TokenId.TYPE);
        keywords.put("@@LONG", TokenId.TYPE);

        keywords.put("defined", TokenId.DIRECTIVE);

        keywords.put("__DATE__", TokenId.CONSTANT);
        keywords.put("__TIME__", TokenId.CONSTANT);
        keywords.put("__FILE__", TokenId.CONSTANT);
        keywords.put("__SPINTOOLS__", TokenId.CONSTANT);
        keywords.put("__VERSION__", TokenId.CONSTANT);
        keywords.put("__DEBUG__", TokenId.CONSTANT);
        keywords.put("__propeller__", TokenId.CONSTANT);
    }

    static Map<String, TokenId> spin1Keywords = new CaseInsensitiveMap<>();
    static {
        spin1Keywords.put("result", TokenId.METHOD_RETURN);
        spin1Keywords.put("@result", TokenId.METHOD_RETURN);

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

        spin1Keywords.put("__propeller1__", TokenId.CONSTANT);
    }

    static Map<String, TokenId> spin1Functions = new CaseInsensitiveMap<>();
    static {
        spin1Functions.put("abort", TokenId.FUNCTION);
        spin1Functions.put("bytefill", TokenId.FUNCTION);
        spin1Functions.put("bytemove", TokenId.FUNCTION);
        spin1Functions.put("chipver", TokenId.FUNCTION);
        spin1Functions.put("clkfreq", TokenId.FUNCTION);
        spin1Functions.put("clkmode", TokenId.FUNCTION);
        spin1Functions.put("clkset", TokenId.FUNCTION);
        spin1Functions.put("cogid", TokenId.FUNCTION);
        spin1Functions.put("coginit", TokenId.FUNCTION);
        spin1Functions.put("cognew", TokenId.FUNCTION);
        spin1Functions.put("cogstop", TokenId.FUNCTION);
        spin1Functions.put("constant", TokenId.FUNCTION);
        spin1Functions.put("lockclr", TokenId.FUNCTION);
        spin1Functions.put("locknew", TokenId.FUNCTION);
        spin1Functions.put("lockret", TokenId.FUNCTION);
        spin1Functions.put("lockset", TokenId.FUNCTION);
        spin1Functions.put("longfill", TokenId.FUNCTION);
        spin1Functions.put("longmove", TokenId.FUNCTION);
        spin1Functions.put("lookdown", TokenId.FUNCTION);
        spin1Functions.put("lookdownz", TokenId.FUNCTION);
        spin1Functions.put("lookup", TokenId.FUNCTION);
        spin1Functions.put("lookupz", TokenId.FUNCTION);
        spin1Functions.put("reboot", TokenId.FUNCTION);
        spin1Functions.put("strcomp", TokenId.FUNCTION);
        spin1Functions.put("strsize", TokenId.FUNCTION);
        spin1Functions.put("waitcnt", TokenId.FUNCTION);
        spin1Functions.put("waitpeq", TokenId.FUNCTION);
        spin1Functions.put("waitpne", TokenId.FUNCTION);
        spin1Functions.put("waitvid", TokenId.FUNCTION);
        spin1Functions.put("wordfill", TokenId.FUNCTION);
        spin1Functions.put("wordmove", TokenId.FUNCTION);

        spin1Functions.put("ror", TokenId.FUNCTION);
        spin1Functions.put("rol", TokenId.FUNCTION);
        spin1Functions.put("max", TokenId.FUNCTION);
        spin1Functions.put("min", TokenId.FUNCTION);
        spin1Functions.put("abs", TokenId.FUNCTION);
        spin1Functions.put("rev", TokenId.FUNCTION);
        spin1Functions.put("encod", TokenId.FUNCTION);
        spin1Functions.put("decod", TokenId.FUNCTION);
        spin1Functions.put("sqrt", TokenId.FUNCTION);

        spin1Functions.put("string", TokenId.FUNCTION);
        spin1Functions.put("strlen", TokenId.FUNCTION);
        spin1Functions.put("strcmp", TokenId.FUNCTION);
        spin1Functions.put("memset", TokenId.FUNCTION);
        spin1Functions.put("memmov", TokenId.FUNCTION);
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

    static Map<String, TokenId> spin2Keywords = new CaseInsensitiveMap<>();
    static {
        spin2Keywords.put("asm", TokenId.KEYWORD);

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
        spin2Keywords.put("P_TRUE_OUT", TokenId.CONSTANT);
        spin2Keywords.put("P_INVERT_OUT", TokenId.CONSTANT);
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
        spin2Keywords.put("NEWTASK", TokenId.CONSTANT);
        spin2Keywords.put("THISTASK", TokenId.CONSTANT);

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

        spin2Keywords.put("__propeller1__", TokenId.CONSTANT);
    }

    static Map<String, TokenId> spin2Functions = new CaseInsensitiveMap<>();
    static {
        spin2Functions.put("hubset", TokenId.FUNCTION);
        spin2Functions.put("clkset", TokenId.FUNCTION);
        spin2Functions.put("cogspin", TokenId.FUNCTION);
        spin2Functions.put("coginit", TokenId.FUNCTION);
        spin2Functions.put("cogstop", TokenId.FUNCTION);
        spin2Functions.put("cogid", TokenId.FUNCTION);
        spin2Functions.put("cogchk", TokenId.FUNCTION);
        spin2Functions.put("locknew", TokenId.FUNCTION);
        spin2Functions.put("lockret", TokenId.FUNCTION);
        spin2Functions.put("locktry", TokenId.FUNCTION);
        spin2Functions.put("lockrel", TokenId.FUNCTION);
        spin2Functions.put("lockchk", TokenId.FUNCTION);
        spin2Functions.put("cogatn", TokenId.FUNCTION);
        spin2Functions.put("pollatn", TokenId.FUNCTION);
        spin2Functions.put("waitatn", TokenId.FUNCTION);

        spin2Functions.put("pinw", TokenId.FUNCTION);
        spin2Functions.put("pinwrite", TokenId.FUNCTION);
        spin2Functions.put("pinl", TokenId.FUNCTION);
        spin2Functions.put("pinlow", TokenId.FUNCTION);
        spin2Functions.put("pinh", TokenId.FUNCTION);
        spin2Functions.put("pinhigh", TokenId.FUNCTION);
        spin2Functions.put("pint", TokenId.FUNCTION);
        spin2Functions.put("pintoggle", TokenId.FUNCTION);
        spin2Functions.put("pinf", TokenId.FUNCTION);
        spin2Functions.put("pinfloat", TokenId.FUNCTION);
        spin2Functions.put("pinr", TokenId.FUNCTION);
        spin2Functions.put("pinread", TokenId.FUNCTION);
        spin2Functions.put("pinstart", TokenId.FUNCTION);
        spin2Functions.put("pinclear", TokenId.FUNCTION);
        spin2Functions.put("wrpin", TokenId.FUNCTION);
        spin2Functions.put("wxpin", TokenId.FUNCTION);
        spin2Functions.put("wypin", TokenId.FUNCTION);
        spin2Functions.put("akpin", TokenId.FUNCTION);
        spin2Functions.put("rdpin", TokenId.FUNCTION);
        spin2Functions.put("rqpin", TokenId.FUNCTION);

        spin2Functions.put("getrnd", TokenId.FUNCTION);
        spin2Functions.put("getct", TokenId.FUNCTION);
        spin2Functions.put("pollct", TokenId.FUNCTION);
        spin2Functions.put("waitct", TokenId.FUNCTION);
        spin2Functions.put("waitus", TokenId.FUNCTION);
        spin2Functions.put("waitms", TokenId.FUNCTION);
        spin2Functions.put("getsec", TokenId.FUNCTION);
        spin2Functions.put("getms", TokenId.FUNCTION);

        spin2Functions.put("call", TokenId.FUNCTION);
        spin2Functions.put("regexec", TokenId.FUNCTION);
        spin2Functions.put("regload", TokenId.FUNCTION);

        spin2Functions.put("rotxy", TokenId.FUNCTION);
        spin2Functions.put("polxy", TokenId.FUNCTION);
        spin2Functions.put("xypol", TokenId.FUNCTION);
        spin2Functions.put("qsin", TokenId.FUNCTION);
        spin2Functions.put("qcos", TokenId.FUNCTION);
        spin2Functions.put("muldiv64", TokenId.FUNCTION);
        spin2Functions.put("getisn", TokenId.FUNCTION);
        spin2Functions.put("nan", TokenId.FUNCTION);

        spin2Functions.put("getregs", TokenId.FUNCTION);
        spin2Functions.put("setregs", TokenId.FUNCTION);
        spin2Functions.put("bytemove", TokenId.FUNCTION);
        spin2Functions.put("wordmove", TokenId.FUNCTION);
        spin2Functions.put("longmove", TokenId.FUNCTION);
        spin2Functions.put("bytefill", TokenId.FUNCTION);
        spin2Functions.put("wordfill", TokenId.FUNCTION);
        spin2Functions.put("longfill", TokenId.FUNCTION);

        spin2Functions.put("strsize", TokenId.FUNCTION);
        spin2Functions.put("strcomp", TokenId.FUNCTION);
        spin2Functions.put("strcopy", TokenId.FUNCTION);
        spin2Functions.put("getcrc", TokenId.FUNCTION);

        spin2Functions.put("lookup", TokenId.FUNCTION);
        spin2Functions.put("lookupz", TokenId.FUNCTION);
        spin2Functions.put("lookdown", TokenId.FUNCTION);
        spin2Functions.put("lookdownz", TokenId.FUNCTION);

        spin2Functions.put("taskspin", TokenId.FUNCTION);
        spin2Functions.put("tasknext", TokenId.FUNCTION);
        spin2Functions.put("taskstop", TokenId.FUNCTION);
        spin2Functions.put("taskhalt", TokenId.FUNCTION);
        spin2Functions.put("taskcont", TokenId.FUNCTION);
        spin2Functions.put("taskchk", TokenId.FUNCTION);
        spin2Functions.put("taskid", TokenId.FUNCTION);

        spin2Functions.put("movbyts", TokenId.FUNCTION);
        spin2Functions.put("endianl", TokenId.FUNCTION);
        spin2Functions.put("endianw", TokenId.FUNCTION);

        spin2Functions.put("abs", TokenId.FUNCTION);
        spin2Functions.put("sqrt", TokenId.FUNCTION);
        spin2Functions.put("qlog", TokenId.FUNCTION);
        spin2Functions.put("qexp", TokenId.FUNCTION);

        spin2Functions.put("float", TokenId.FUNCTION);
        spin2Functions.put("round", TokenId.FUNCTION);
        spin2Functions.put("trunc", TokenId.FUNCTION);

        spin2Functions.put("sar", TokenId.FUNCTION);
        spin2Functions.put("ror", TokenId.FUNCTION);
        spin2Functions.put("rol", TokenId.FUNCTION);
        spin2Functions.put("rev", TokenId.FUNCTION);
        spin2Functions.put("zerox", TokenId.FUNCTION);
        spin2Functions.put("signx", TokenId.FUNCTION);
        spin2Functions.put("max", TokenId.FUNCTION);
        spin2Functions.put("min", TokenId.FUNCTION);
        spin2Functions.put("addpins", TokenId.FUNCTION);
        spin2Functions.put("addbits", TokenId.FUNCTION);
        spin2Functions.put("sca", TokenId.FUNCTION);
        spin2Functions.put("scas", TokenId.FUNCTION);
        spin2Functions.put("frac", TokenId.FUNCTION);

        spin2Functions.put("string", TokenId.FUNCTION);
        spin2Functions.put("strlen", TokenId.FUNCTION);
        spin2Functions.put("strcmp", TokenId.FUNCTION);
        spin2Functions.put("strcpy", TokenId.FUNCTION);
        spin2Functions.put("memmov", TokenId.FUNCTION);
        spin2Functions.put("memset", TokenId.FUNCTION);
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

    protected boolean p1;

    public CTokenMarker(SourceProvider sourceProvider) {
        super(sourceProvider);
        this.constantSeparator = "";
        this.localLabelPrefix = ".";
    }

    public boolean isP1() {
        return p1;
    }

    public boolean isP2() {
        return !p1;
    }

    @Override
    public void refreshTokens(String text) {
        Matcher matcher = pragmaTargetPattern.matcher(text);
        while (matcher.find()) {
            if (matcher.group(2) != null) {
                if (matcher.group(2).endsWith("P1")) {
                    p1 = true;
                }
                else if (matcher.group(2).endsWith("P2")) {
                    p1 = false;
                }
                break;
            }
        }

        CParser parser = new CParser(text);

        RootNode root = parser.parse();
        if (this.root == null || this.root.getChildCount() == 0) {
            comments.addAll(root.getComments());
        }
        this.root = root;

        collectTokens(root);
    }

    @Override
    public void setRoot(RootNode root) {
        symbols.clear();
        locals.clear();

        comments.clear();
        comments.addAll(root.getComments());
        collectTokens(root);

        super.setRoot(root);
    }

    void collectTokens(RootNode root) {
        for (Node child : root.getChilds()) {
            switch (child) {
                case DirectiveNode.DefineNode node -> {
                    if (!node.isExclude()) {
                        symbols.put(node.identifier.getText(), TokenId.CONSTANT);
                    }
                }
                case DirectiveNode.IncludeNode node -> {
                    if (node.isExclude()) {
                        break;
                    }
                    String name = node.file.getText();
                    if (name.startsWith("\"") || name.startsWith("<")) {
                        name = name.substring(1);
                    }
                    if (name.endsWith("\"") || name.endsWith("<")) {
                        name = name.substring(0, name.length() - 1);
                    }
                    symbols.put(name, TokenId.TYPE);
                }
                case StructNode node -> {
                    if (node.identifier != null) {
                        symbols.put(node.identifier.getText(), TokenId.TYPE);
                    }
                }
                case VariableNode node -> {
                    if (node.identifier != null) {
                        RootNode objectRoot = root.getObjectRoot(node.identifier.getText());
                        if (objectRoot != null) {
                            collectObjectTokens(node.identifier.getText(), objectRoot);
                            symbols.put(node.type.getText(), TokenId.TYPE);
                            symbols.put(node.identifier.getText(), TokenId.OBJECT);
                        }
                        else {
                            symbols.put(node.type.getText(), TokenId.TYPE);
                            symbols.put(node.identifier.getText(), TokenId.VARIABLE);
                        }
                    }
                }
                case FunctionNode node -> {
                    if (node.identifier != null) {
                        symbols.put(node.identifier.getText(), node.isPublic() ? TokenId.METHOD_PUB : TokenId.METHOD_PRI);
                        Map<String, TokenId> methodLocals = locals.computeIfAbsent(node.identifier.getText(), k -> new HashMap<>());
                        for (FunctionNode.ParameterNode var : node.getParameters()) {
                            if (var.identifier != null) {
                                methodLocals.put(var.identifier.getText(), TokenId.METHOD_PARAMETER);
                            }
                        }
                        for (FunctionNode.LocalVariableNode var : node.getLocalVariables()) {
                            if (var.identifier != null) {
                                methodLocals.put(var.identifier.getText(), TokenId.METHOD_LOCAL);
                            }
                        }
                    }
                }
                default -> {
                }
            }
        }

    }

    void collectObjectTokens(String qualifier, Node root) {
        for (Node child : root.getChilds()) {
            switch (child) {
                case ConstantsNode node -> {
                    collectObjectTokens(qualifier, node);
                }
                case ConstantNode node -> {
                    if (node.identifier != null) {
                        symbols.put(node.identifier.getText(), TokenId.CONSTANT);
                    }
                }
                case MethodNode node -> {
                    if (node.name != null && node.isPublic()) {
                        symbols.put(qualifier + "." + node.name.getText(), TokenId.METHOD_PUB);
                    }
                }
                default -> {
                }
            }
        }
    }

    @Override
    public Collection<TokenMarker> getTokens(int lineIndex, int lineOffset, String lineText) {
        Token token;
        int startIndex = 0;
        List<TokenMarker> markers = new ArrayList<>();

        for (Token blockCommentToken : comments) {
            if (lineOffset >= blockCommentToken.start && lineOffset <= blockCommentToken.stop) {
                int index = blockCommentToken.stop - lineOffset + 1;
                if (index >= lineText.length()) {
                    markers.add(new TokenMarker(0, lineText.length(), TokenId.COMMENT));
                    return markers;
                }
                markers.add(new TokenMarker(0, index, TokenId.COMMENT));
                startIndex = index;
                break;
            }
        }

        Node contextNode = null;
        for (Node node : root.getChilds()) {
            if (node.getTokenCount() != 0 && lineIndex >= node.getStartToken().line) {
                contextNode = node;
            }
        }

        CTokenStream stream = new CTokenStream(lineText, startIndex);
        markTokens(contextNode, stream, markers, null);

        return markers;
    }

    void markTokens(Node contextNode, TokenStream stream, List<TokenMarker> markers, String endMarker) {
        Token token;

        while ((token = stream.nextToken()).type != Token.EOF) {
            String tokentext = token.getText();
            if (tokentext.equals(endMarker)) {
                return;
            }
            if ("#".equals(tokentext)) {
                int from = token.start;
                int to = token.stop;
                while ((token = stream.nextToken()).type != Token.EOF) {
                    if (token.type == Token.COMMENT) {
                        break;
                    }
                    to = token.stop;
                }
                markers.add(new TokenMarker(from, to + 1, TokenId.DIRECTIVE));
                if (token.type != Token.EOF) {
                    markers.add(new TokenMarker(token, TokenId.COMMENT));
                }
                return;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                markers.add(new TokenMarker(token, TokenId.COMMENT));
            }
            else if (token.type == Token.NUMBER) {
                markers.add(new TokenMarker(token, TokenId.NUMBER));
            }
            else if (token.type == Token.STRING) {
                markers.add(new TokenMarker(token, TokenId.STRING));
            }
            else if (token.type != Token.OPERATOR) {
                TokenId id = symbols.get(tokentext);
                if (id == TokenId.OBJECT) {
                    String qualifier = token.getText();
                    markers.add(new TokenMarker(token, id));
                    token = stream.nextToken();
                    if ("[".equals(token.getText())) {
                        markTokens(contextNode, stream, markers, "]");
                        token = stream.nextToken();
                        if (".".equals(token.getText())) {
                            Token nextToken = stream.peekNext();
                            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                                token = stream.nextToken();
                                id = symbols.get(qualifier + "." + token.getText());
                                if (id != null) {
                                    markers.add(new TokenMarker(token, id));
                                }
                                continue;
                            }
                        }
                    }
                    id = null;
                }
                else if (id == TokenId.METHOD_PUB || id == TokenId.CONSTANT) {
                    int dot = tokentext.indexOf('.');
                    if (dot != -1) {
                        markers.add(new TokenMarker(token.start, token.start + dot - 1, TokenId.OBJECT));
                        markers.add(new TokenMarker(token.start + dot + 1, token.stop, id));
                        continue;
                    }
                }
                if (id == null) {
                    if (contextNode instanceof FunctionNode functionNode) {
                        if (functionNode.identifier != null) {
                            Map<String, TokenId> localSymbols = locals.get(functionNode.identifier.getText());
                            if (localSymbols != null) {
                                id = localSymbols.get(tokentext);
                            }
                        }
                    }
                    if (id == null) {
                        id = symbols.get(tokentext);
                        if (id == null) {
                            if (isP1()) {
                                id = spin1Keywords.get(tokentext);
                                if (id == null) {
                                    id = spin1Functions.get(tokentext);
                                }
                            }
                            else {
                                id = spin2Keywords.get(tokentext);
                                if (id == null) {
                                    id = spin2Functions.get(tokentext);
                                }
                            }
                        }
                        if (id == null) {
                            id = keywords.get(tokentext);
                        }
                    }
                }
                if (id != null) {
                    markers.add(new TokenMarker(token, id));
                }
            }
            else if ("[".equals(tokentext)) {
                markTokens(contextNode, stream, markers, "]");
            }
        }
    }

    public TokenId getLineBackgroundId(Node root, int lineOffset) {
        return null;
    }

}
