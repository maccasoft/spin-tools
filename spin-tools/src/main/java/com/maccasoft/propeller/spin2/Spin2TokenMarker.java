/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.maccasoft.propeller.SourceTokenMarker;
import com.maccasoft.propeller.model.ConstantNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.DirectiveNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.RootNode;
import com.maccasoft.propeller.model.SourceProvider;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.StructNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TokenStream;
import com.maccasoft.propeller.model.TokenStream.Position;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;

public class Spin2TokenMarker extends SourceTokenMarker {

    static Map<String, TokenId> keywords = new CaseInsensitiveMap<>();
    static {
        keywords.put("CON", TokenId.SECTION);
        keywords.put("VAR", TokenId.SECTION);
        keywords.put("OBJ", TokenId.SECTION);
        keywords.put("PUB", TokenId.SECTION);
        keywords.put("PRI", TokenId.SECTION);
        keywords.put("DAT", TokenId.SECTION);

        keywords.put("BYTE", TokenId.TYPE);
        keywords.put("WORD", TokenId.TYPE);
        keywords.put("LONG", TokenId.TYPE);
        keywords.put("BYTEFIT", TokenId.TYPE);
        keywords.put("WORDFIT", TokenId.TYPE);

        keywords.put("^BYTE", TokenId.TYPE);
        keywords.put("^WORD", TokenId.TYPE);
        keywords.put("^LONG", TokenId.TYPE);

        keywords.put("HUBSET", TokenId.FUNCTION);
        keywords.put("CLKSET", TokenId.FUNCTION);
        keywords.put("COGSPIN", TokenId.FUNCTION);
        keywords.put("COGINIT", TokenId.FUNCTION);
        keywords.put("COGSTOP", TokenId.FUNCTION);
        keywords.put("COGID", TokenId.FUNCTION);
        keywords.put("COGCHK", TokenId.FUNCTION);
        keywords.put("LOCKNEW", TokenId.FUNCTION);
        keywords.put("LOCKRET", TokenId.FUNCTION);
        keywords.put("LOCKTRY", TokenId.FUNCTION);
        keywords.put("LOCKREL", TokenId.FUNCTION);
        keywords.put("LOCKCHK", TokenId.FUNCTION);
        keywords.put("COGATN", TokenId.FUNCTION);
        keywords.put("POLLATN", TokenId.FUNCTION);
        keywords.put("WAITATN", TokenId.FUNCTION);

        keywords.put("PINW", TokenId.FUNCTION);
        keywords.put("PINWRITE", TokenId.FUNCTION);
        keywords.put("PINL", TokenId.FUNCTION);
        keywords.put("PINLOW", TokenId.FUNCTION);
        keywords.put("PINH", TokenId.FUNCTION);
        keywords.put("PINHIGH", TokenId.FUNCTION);
        keywords.put("PINT", TokenId.FUNCTION);
        keywords.put("PINTOGGLE", TokenId.FUNCTION);
        keywords.put("PINF", TokenId.FUNCTION);
        keywords.put("PINFLOAT", TokenId.FUNCTION);
        keywords.put("PINR", TokenId.FUNCTION);
        keywords.put("PINREAD", TokenId.FUNCTION);
        keywords.put("PINSTART", TokenId.FUNCTION);
        keywords.put("PINCLEAR", TokenId.FUNCTION);
        keywords.put("WRPIN", TokenId.FUNCTION);
        keywords.put("WXPIN", TokenId.FUNCTION);
        keywords.put("WYPIN", TokenId.FUNCTION);
        keywords.put("AKPIN", TokenId.FUNCTION);
        keywords.put("RDPIN", TokenId.FUNCTION);
        keywords.put("RQPIN", TokenId.FUNCTION);

        keywords.put("GETCT", TokenId.FUNCTION);
        keywords.put("POLLCT", TokenId.FUNCTION);
        keywords.put("WAITCT", TokenId.FUNCTION);
        keywords.put("WAITUS", TokenId.FUNCTION);
        keywords.put("WAITMS", TokenId.FUNCTION);
        keywords.put("GETSEC", TokenId.FUNCTION);
        keywords.put("GETMS", TokenId.FUNCTION);

        keywords.put("CALL", TokenId.FUNCTION);
        keywords.put("REGEXEC", TokenId.FUNCTION);
        keywords.put("REGLOAD", TokenId.FUNCTION);

        keywords.put("ROTXY", TokenId.FUNCTION);
        keywords.put("POLXY", TokenId.FUNCTION);
        keywords.put("XYPOL", TokenId.FUNCTION);
        keywords.put("QSIN", TokenId.FUNCTION);
        keywords.put("QCOS", TokenId.FUNCTION);
        keywords.put("MULDIV64", TokenId.FUNCTION);
        keywords.put("GETRND", TokenId.FUNCTION);
        keywords.put("NAN", TokenId.FUNCTION);

        keywords.put("GETREGS", TokenId.FUNCTION);
        keywords.put("SETREGS", TokenId.FUNCTION);
        keywords.put("BYTEMOVE", TokenId.FUNCTION);
        keywords.put("WORDMOVE", TokenId.FUNCTION);
        keywords.put("LONGMOVE", TokenId.FUNCTION);
        keywords.put("BYTEFILL", TokenId.FUNCTION);
        keywords.put("WORDFILL", TokenId.FUNCTION);
        keywords.put("LONGFILL", TokenId.FUNCTION);

        keywords.put("STRSIZE", TokenId.FUNCTION);
        keywords.put("STRCOMP", TokenId.FUNCTION);
        keywords.put("STRCOPY", TokenId.FUNCTION);
        keywords.put("STRING", TokenId.FUNCTION);
        keywords.put("LSTRING", TokenId.FUNCTION);
        keywords.put("GETCRC", TokenId.FUNCTION);

        keywords.put("LOOKUP", TokenId.FUNCTION);
        keywords.put("LOOKUPZ", TokenId.FUNCTION);
        keywords.put("LOOKDOWN", TokenId.FUNCTION);
        keywords.put("LOOKDOWNZ", TokenId.FUNCTION);

        keywords.put("TASKSPIN", TokenId.FUNCTION);
        keywords.put("TASKNEXT", TokenId.FUNCTION);
        keywords.put("TASKSTOP", TokenId.FUNCTION);
        keywords.put("TASKHALT", TokenId.FUNCTION);
        keywords.put("TASKCONT", TokenId.FUNCTION);
        keywords.put("TASKCHK", TokenId.FUNCTION);
        keywords.put("TASKID", TokenId.FUNCTION);

        keywords.put("MOVBYTS", TokenId.FUNCTION);
        keywords.put("ENDIANL", TokenId.FUNCTION);
        keywords.put("ENDIANW", TokenId.FUNCTION);

        keywords.put("BYTECODE", TokenId.FUNCTION);

        keywords.put("NOT", TokenId.KEYWORD);
        keywords.put("OR", TokenId.KEYWORD);
        keywords.put("AND", TokenId.KEYWORD);
        keywords.put("XOR", TokenId.KEYWORD);

        keywords.put("ABORT", TokenId.KEYWORD);
        keywords.put("SEND", TokenId.KEYWORD);
        keywords.put("RECV", TokenId.KEYWORD);
        keywords.put("IF", TokenId.KEYWORD);
        keywords.put("IFNOT", TokenId.KEYWORD);
        keywords.put("ELSEIF", TokenId.KEYWORD);
        keywords.put("ELSEIFNOT", TokenId.KEYWORD);
        keywords.put("ELSE", TokenId.KEYWORD);
        keywords.put("CASE", TokenId.KEYWORD);
        keywords.put("CASE_FAST", TokenId.KEYWORD);
        keywords.put("OTHER", TokenId.KEYWORD);
        keywords.put("REPEAT", TokenId.KEYWORD);
        keywords.put("FROM", TokenId.KEYWORD);
        keywords.put("TO", TokenId.KEYWORD);
        keywords.put("STEP", TokenId.KEYWORD);
        keywords.put("WHILE", TokenId.KEYWORD);
        keywords.put("WITH", TokenId.KEYWORD);
        keywords.put("UNTIL", TokenId.KEYWORD);
        keywords.put("NEXT", TokenId.KEYWORD);
        keywords.put("QUIT", TokenId.KEYWORD);
        keywords.put("RETURN", TokenId.KEYWORD);

        keywords.put("DEBUG", TokenId.KEYWORD);

        keywords.put("ABS", TokenId.KEYWORD);
        keywords.put("ENCOD", TokenId.KEYWORD);
        keywords.put("DECOD", TokenId.KEYWORD);
        keywords.put("BMASK", TokenId.KEYWORD);
        keywords.put("ONES", TokenId.KEYWORD);
        keywords.put("SQRT", TokenId.KEYWORD);
        keywords.put("QLOG", TokenId.KEYWORD);
        keywords.put("QEXP", TokenId.KEYWORD);

        keywords.put("SAR", TokenId.KEYWORD);
        keywords.put("ROR", TokenId.KEYWORD);
        keywords.put("ROL", TokenId.KEYWORD);
        keywords.put("REV", TokenId.KEYWORD);
        keywords.put("ZEROX", TokenId.KEYWORD);
        keywords.put("SIGNX", TokenId.KEYWORD);
        keywords.put("SCA", TokenId.KEYWORD);
        keywords.put("SCAS", TokenId.KEYWORD);
        keywords.put("FRAC", TokenId.KEYWORD);
        keywords.put("ADDPINS", TokenId.KEYWORD);
        keywords.put("ADDBITS", TokenId.KEYWORD);

        keywords.put("FLOAT", TokenId.KEYWORD);
        keywords.put("TRUNC", TokenId.KEYWORD);
        keywords.put("ROUND", TokenId.KEYWORD);
        keywords.put("FABS", TokenId.KEYWORD);
        keywords.put("FSQRT", TokenId.KEYWORD);
        keywords.put("LOG2", TokenId.KEYWORD);
        keywords.put("LOG10", TokenId.KEYWORD);
        keywords.put("LOG", TokenId.KEYWORD);
        keywords.put("EXP2", TokenId.KEYWORD);
        keywords.put("EXP10", TokenId.KEYWORD);
        keywords.put("EXP", TokenId.KEYWORD);
        keywords.put("POW", TokenId.KEYWORD);

        keywords.put("_CLKFREQ", TokenId.CONSTANT);
        keywords.put("_CLKMODE", TokenId.CONSTANT);
        keywords.put("CLKFREQ", TokenId.CONSTANT);
        keywords.put("CLKMODE", TokenId.CONSTANT);
        keywords.put("VARBASE", TokenId.CONSTANT);

        keywords.put("TRUE", TokenId.CONSTANT);
        keywords.put("FALSE", TokenId.CONSTANT);
        keywords.put("POSX", TokenId.CONSTANT);
        keywords.put("NEGX", TokenId.CONSTANT);
        keywords.put("PI", TokenId.CONSTANT);
        keywords.put("SIZEOF", TokenId.KEYWORD);
        keywords.put("OFFSETOF", TokenId.KEYWORD);

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
        keywords.put("P_TRUE_OUT", TokenId.CONSTANT);
        keywords.put("P_INVERT_OUT", TokenId.CONSTANT);
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
        keywords.put("NEWTASK", TokenId.CONSTANT);
        keywords.put("THISTASK", TokenId.CONSTANT);

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

        keywords.put("END", TokenId.PASM_INSTRUCTION);

        keywords.put("defined", TokenId.DIRECTIVE);

        keywords.put("DEBUG_END_SESSION", TokenId.CONSTANT);

        keywords.put("__DATE__", TokenId.CONSTANT);
        keywords.put("__TIME__", TokenId.CONSTANT);
        keywords.put("__FILE__", TokenId.CONSTANT);
        keywords.put("__P2__", TokenId.CONSTANT);
        keywords.put("__SPINTOOLS__", TokenId.CONSTANT);
        keywords.put("__VERSION__", TokenId.CONSTANT);
        keywords.put("__DEBUG__", TokenId.CONSTANT);
        keywords.put("__propeller__", TokenId.CONSTANT);
        keywords.put("__propeller2__", TokenId.CONSTANT);
    }

    static Map<String, TokenId> debugKeywords = new CaseInsensitiveMap<>();
    static {
        debugKeywords.put("DEBUG", TokenId.KEYWORD);

        debugKeywords.put("FDEC", TokenId.FUNCTION);
        debugKeywords.put("FDEC_REG_ARRAY", TokenId.FUNCTION);
        debugKeywords.put("FDEC_ARRAY", TokenId.FUNCTION);

        debugKeywords.put("FDEC_", TokenId.FUNCTION);
        debugKeywords.put("FDEC_REG_ARRAY_", TokenId.FUNCTION);
        debugKeywords.put("FDEC_ARRAY_", TokenId.FUNCTION);

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

        debugKeywords.put("BOOL", TokenId.FUNCTION);
        debugKeywords.put("BOOL_", TokenId.FUNCTION);
        debugKeywords.put("C_Z", TokenId.FUNCTION);

        debugKeywords.put("DLY", TokenId.FUNCTION);
        debugKeywords.put("ZSTR", TokenId.FUNCTION);
        debugKeywords.put("ZSTR_", TokenId.FUNCTION);
        debugKeywords.put("LSTR", TokenId.FUNCTION);
        debugKeywords.put("LSTR_", TokenId.FUNCTION);

        debugKeywords.put("IF", TokenId.FUNCTION);
        debugKeywords.put("IFNOT", TokenId.FUNCTION);

        debugKeywords.put("PC_KEY", TokenId.FUNCTION);
        debugKeywords.put("PC_MOUSE", TokenId.FUNCTION);

        debugKeywords.put("SIZEOF", TokenId.KEYWORD);
        debugKeywords.put("OFFSETOF", TokenId.KEYWORD);
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
        spinKeywords.put("TASKHLT", TokenId.PASM_INSTRUCTION);
        spinKeywords.put("@BYTE", TokenId.TYPE);
        spinKeywords.put("@WORD", TokenId.TYPE);
        spinKeywords.put("@LONG", TokenId.TYPE);
        spinKeywords.put("@@BYTE", TokenId.TYPE);
        spinKeywords.put("@@WORD", TokenId.TYPE);
        spinKeywords.put("@@LONG", TokenId.TYPE);
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

    private static final Set<String> preprocessor = new HashSet<>(Arrays.asList(new String[] {
        "define", "ifdef", "elifdef", "elseifdef", "ifndef", "elifndef", "elseifndef", "else", "if", "elif", "elseif", "endif",
        "error", "warning", "pragma", "undef"
    }));

    public Spin2TokenMarker(SourceProvider sourceProvider) {
        super(sourceProvider);
        this.constantSeparator = ".";
        this.localLabelPrefix = ".";
    }

    @Override
    public void refreshTokens(String text) {
        Spin2Parser parser = new Spin2Parser(text);

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

        rootNodes.clear();
        rootNodes.putAll(root.getObjectRoots());

        collectTokens(root);

        super.setRoot(root);
    }

    void collectTokens(Node root) {
        for (Node child : root.getChilds()) {
            switch (child) {
                case DirectiveNode.DefineNode node -> {
                    if (!node.isExclude()) {
                        symbols.put(node.identifier.getText(), TokenId.CONSTANT);
                    }
                }
                case ConstantsNode node -> {
                    collectTokens(node);
                }
                case ConstantNode node -> {
                    if (node.identifier != null) {
                        symbols.put(node.identifier.getText(), TokenId.CONSTANT);
                    }
                }
                case StructNode node -> {
                    if (node.identifier != null) {
                        symbols.put(node.identifier.getText(), TokenId.TYPE);
                    }
                }
                case VariablesNode node -> {
                    collectTokens(node);
                }
                case VariableNode node -> {
                    if (node.identifier != null) {
                        symbols.put(node.identifier.getText(), TokenId.VARIABLE);
                    }
                }
                case ObjectsNode node -> {
                    collectTokens(node);
                }
                case ObjectNode node -> {
                    if (node.name != null) {
                        String name = node.name.getText();
                        symbols.put(name, TokenId.OBJECT);
                        RootNode objectRoot = node.getRoot().getObjectRoot(name);
                        if (objectRoot != null) {
                            collectObjectTokens(name, objectRoot);
                        }
                    }
                }
                case MethodNode node -> {
                    if (node.name != null) {
                        symbols.put(node.name.getText(), node.isPublic() ? TokenId.METHOD_PUB : TokenId.METHOD_PRI);
                        Map<String, TokenId> methodLocals = locals.computeIfAbsent(node.name.getText(), k -> isCaseSensitive() ? new HashMap<>() : new CaseInsensitiveMap<>());
                        for (MethodNode.ParameterNode var : node.getParameters()) {
                            if (var.identifier != null) {
                                methodLocals.put(var.identifier.getText(), TokenId.METHOD_PARAMETER);
                            }
                        }
                        for (MethodNode.ReturnNode var : node.getReturnVariables()) {
                            if (var.identifier != null) {
                                methodLocals.put(var.identifier.getText(), TokenId.METHOD_RETURN);
                            }
                        }
                        for (MethodNode.LocalVariableNode var : node.getLocalVariables()) {
                            if (var.identifier != null) {
                                methodLocals.put(var.identifier.getText(), TokenId.METHOD_LOCAL);
                            }
                        }
                    }
                }
                case DataNode node -> {
                    collectTokens(node);
                }
                case DataLineNode node -> {
                    if (node.label != null && !node.label.getText().startsWith(".") && !node.label.getText().startsWith(":")) {
                        symbols.put(node.label.getText(), TokenId.PASM_LABEL);
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
                        symbols.put(qualifier + "." + node.identifier.getText(), TokenId.CONSTANT);
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

        Node contextNode = getSectionAtLine(lineIndex);
        Spin2TokenStream stream = new Spin2TokenStream(lineText, startIndex);

        while ((token = stream.peekNext()).type != Token.EOF) {
            if ("#".equals(token.getText())) {
                Position pos = stream.mark();
                Token preprocessorToken = stream.nextToken(); // #
                Token directiveToken = stream.nextToken();
                if (preprocessor.contains(directiveToken.getText())) {
                    while ((token = stream.nextToken()).type != Token.EOF) {
                        if (token.type == Token.NL) {
                            break;
                        }
                    }
                    markers.add(new TokenMarker(preprocessorToken.start, token.stop, TokenId.DIRECTIVE));
                    continue;
                }
                stream.restore(pos);
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                markers.add(new TokenMarker(stream.nextToken(), TokenId.COMMENT));
            }
            else if (token.type == Token.NUMBER) {
                markers.add(new TokenMarker(stream.nextToken(), TokenId.NUMBER));
            }
            else if (token.type == Token.STRING) {
                markers.add(new TokenMarker(stream.nextToken(), TokenId.STRING));
            }
            else if ("CON".equalsIgnoreCase(token.getText())) {
                markers.add(new TokenMarker(stream.nextToken(), TokenId.SECTION));
                parseConstant(stream, markers);
            }
            else if ("VAR".equalsIgnoreCase(token.getText())) {
                markers.add(new TokenMarker(stream.nextToken(), TokenId.SECTION));
                parseVariable(stream, contextNode, markers);
            }
            else if ("OBJ".equalsIgnoreCase(token.getText())) {
                markers.add(new TokenMarker(stream.nextToken(), TokenId.SECTION));
                parseObject(stream, markers);
            }
            else if ("PUB".equalsIgnoreCase(token.getText())) {
                markers.add(new TokenMarker(stream.nextToken(), TokenId.METHOD_PUB));
                parseMethod(TokenId.METHOD_PUB, stream, markers);
            }
            else if ("PRI".equalsIgnoreCase(token.getText())) {
                markers.add(new TokenMarker(stream.nextToken(), TokenId.METHOD_PRI));
                parseMethod(TokenId.METHOD_PRI, stream, markers);
            }
            else if ("DAT".equalsIgnoreCase(token.getText())) {
                markers.add(new TokenMarker(stream.nextToken(), TokenId.SECTION));
                parseDatLine(stream, "-", markers, Collections.emptyMap());
            }
            else {
                if (contextNode instanceof VariablesNode) {
                    parseVariable(stream, contextNode, markers);
                }
                else if (contextNode instanceof ObjectsNode) {
                    parseObject(stream, markers);
                }
                else if (contextNode instanceof MethodNode methodNode) {
                    Map<String, TokenId> localSymbols = locals.get(methodNode.getName().getText());
                    if (localSymbols == null) {
                        localSymbols = Collections.emptyMap();
                    }
                    Node statement = getStatementAtLine(contextNode, lineIndex);
                    if (statement instanceof DataLineNode) {
                        String lastLabel = getLastPAsmLabel(statement.getParent(), lineOffset + lineText.length());
                        parseDatLine(stream, lastLabel, markers, localSymbols);
                    }
                    else {
                        boolean debug = "debug".equalsIgnoreCase(token.getText());
                        markTokens(methodNode, stream, debug, markers, localSymbols, null);
                    }
                }
                else if (contextNode instanceof DataNode) {
                    String lastLabel = getLastPAsmLabel(contextNode, lineOffset + lineText.length());
                    parseDatLine(stream, lastLabel, markers, Collections.emptyMap());
                }
                else {
                    parseConstant(stream, markers);
                }
            }
        }

        return markers;
    }

    Node getStatementAtLine(Node parent, int lineIndex) {
        Node result = null;

        for (Node node : parent.getChilds()) {
            if (node.getTokenCount() != 0) {
                if (lineIndex < node.getStartToken().line) {
                    break;
                }
                if (node instanceof StatementNode) {
                    Node child = getStatementAtLine(node, lineIndex);
                    result = child != null ? child : node;
                }
                else {
                    result = node;
                }
            }
        }

        return result;
    }

    String getLastPAsmLabel(Node parent, int lineEndOffset) {
        String lastLabel = "-";

        for (Node node : parent.getChilds()) {
            if (node.getStartIndex() > lineEndOffset) {
                break;
            }
            if (node instanceof DataLineNode dataLineNode) {
                if (dataLineNode.label != null) {
                    String s = dataLineNode.label.getText();
                    if (!s.startsWith(":") && !s.startsWith(".")) {
                        lastLabel = s;
                    }
                }
            }
        }

        return lastLabel;
    }

    void parseConstant(TokenStream stream, List<TokenMarker> markers) {
        Token token;
        Token identifier = null;
        int state = 1;

        while ((token = stream.nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                markers.add(new TokenMarker(token, TokenId.COMMENT));
                continue;
            }

            switch (state) {
                case 0, 5, 11:
                    if (",".equals(token.getText())) {
                        state = 1;
                        break;
                    }
                    markConstantToken(token, markers);
                    break;
                case 1:
                    if ("#".equals(token.getText())) {
                        state = 2;
                        break;
                    }
                    if ("struct".equalsIgnoreCase(token.getText())) {
                        markers.add(new TokenMarker(token, TokenId.TYPE));
                        identifier = null;
                        state = 7;
                        break;
                    }
                    markers.add(new TokenMarker(token, TokenId.CONSTANT));
                    state = 4;
                    break;
                case 2:
                    if (",".equals(token.getText())) {
                        state = 1;
                        break;
                    }
                    if ("[".equals(token.getText())) {
                        state = 3;
                        break;
                    }
                    markConstantToken(token, markers);
                    break;
                case 3:
                    if (",".equals(token.getText())) {
                        state = 1;
                        break;
                    }
                    if ("]".equals(token.getText())) {
                        state = 0;
                        break;
                    }
                    markConstantToken(token, markers);
                    break;
                case 4:
                    if ("(".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    if (",".equals(token.getText())) {
                        state = 1;
                        break;
                    }
                    if ("=".equals(token.getText())) {
                        state = 5;
                        break;
                    }
                    if ("[".equals(token.getText())) {
                        state = 6;
                        break;
                    }
                    break;
                case 6:
                    if ("]".equals(token.getText())) {
                        state = 0;
                        break;
                    }
                    markConstantToken(token, markers);
                    break;
                case 7:
                    markers.add(new TokenMarker(token, TokenId.TYPE));
                    state = 8;
                    break;

                case 8:
                    if ("(".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    if ("=".equals(token.getText())) {
                        state = 11;
                        break;
                    }
                    markConstantToken(token, markers);
                    break;
                case 9:
                    if (")".equals(token.getText())) {
                        state = 0;
                        break;
                    }
                    identifier = token;
                    state = 10;
                    break;
                case 10:
                    if (")".equals(token.getText())) {
                        markers.add(new TokenMarker(identifier, TokenId.VARIABLE));
                        state = 0;
                        break;
                    }
                    if (",".equals(token.getText())) {
                        markers.add(new TokenMarker(identifier, TokenId.VARIABLE));
                        state = 9;
                        break;
                    }
                    if (token.type == Token.KEYWORD) {
                        markers.add(new TokenMarker(identifier, TokenId.TYPE));
                        identifier = token;
                        break;
                    }
                    markConstantToken(token, markers);
                    break;
            }
        }
    }

    void parseVariable(TokenStream stream, Node contextNode, List<TokenMarker> markers) {
        Token token;
        int state = 1;

        while ((token = stream.nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                markers.add(new TokenMarker(token, TokenId.COMMENT));
                continue;
            }

            switch (state) {
                case 1:
                    TokenId id = symbols.get(token.getText());
                    if (id != TokenId.TYPE) {
                        id = keywords.get(token.getText());
                    }
                    if (id == TokenId.TYPE) {
                        markers.add(new TokenMarker(token, id));
                        state = 2;
                        break;
                    }
                    int dot = token.getText().indexOf('.');
                    if (dot > 0) {
                        id = symbols.get(token.getText().substring(0, dot));
                        if (id == TokenId.OBJECT) {
                            markers.add(new TokenMarker(token.start, token.start + dot - 1, id));
                        }
                        state = 2;
                        break;
                    }
                    // fall-through
                case 2:
                    markers.add(new TokenMarker(token, TokenId.VARIABLE));
                    state = 3;
                    break;

                case 3:
                    if (",".equals(token.getText())) {
                        state = 1;
                        break;
                    }
                    if ("[".equals(token.getText())) {
                        markTokens(contextNode, stream, false, markers, Collections.emptyMap(), "]");
                        break;
                    }
                    break;
            }
        }
    }

    void parseObject(TokenStream stream, List<TokenMarker> markers) {
        Token token;
        int state = 1;

        while ((token = stream.nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                markers.add(new TokenMarker(token, TokenId.COMMENT));
                continue;
            }

            TokenId id = null;

            switch (state) {
                case 1:
                    id = TokenId.OBJECT;
                    state = 2;
                    break;
                case 2:
                    if ("[".equals(token.getText())) {
                        state = 5;
                        break;
                    }
                    // fall-through
                case 3:
                    if (":".equals(token.getText())) {
                        state = 4;
                        break;
                    }
                    break;
                case 4:
                    state = 6;
                    break;

                case 5:
                    if ("]".equals(token.getText())) {
                        state = 3;
                        break;
                    }
                    break;

                case 6:
                    if ("|".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    state = 10;
                    break;

                case 7:
                    id = TokenId.CONSTANT;
                    state = 8;
                    break;

                case 8:
                    if ("=".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    state = 10;
                    break;

                case 9:
                    if (",".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    break;

                case 10:
                    break;
            }

            if (id == null) {
                if (token.type == Token.NUMBER) {
                    id = TokenId.NUMBER;
                }
                else if (token.type == Token.STRING) {
                    id = token.getText().length() > 3 ? TokenId.STRING : TokenId.NUMBER;
                }
                else {
                    id = symbols.get(token.getText());
                    if (id == null) {
                        id = keywords.get(token.getText());
                        if (id == null) {
                            id = spinKeywords.get(token.getText());
                        }
                    }
                }
            }
            if (id != null) {
                markers.add(new TokenMarker(token, id));
            }
        }
    }

    void parseMethod(TokenId type, TokenStream stream, List<TokenMarker> markers) {
        Token token;
        int state = 1;

        while ((token = stream.nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                markers.add(new TokenMarker(token, TokenId.COMMENT));
                continue;
            }

            if (state == 4 || state == 7 || state == 9) {
                if ("^".equals(token.getText())) {
                    Token nextToken = stream.peekNext();
                    if (nextToken != null && token.isAdjacent(nextToken) && nextToken.type == Token.KEYWORD) {
                        token = token.merge(stream.nextToken());
                        token.type = Token.KEYWORD;
                    }
                }
            }

            TokenId id = null;

            switch (state) {
                case 1:
                    id = type;
                    state = 2;
                    break;
                case 2:
                    if ("(".equals(token.getText())) {
                        state = 4;
                        break;
                    }
                    if (":".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    if ("|".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    break;

                case 4:
                    if (",".equals(token.getText())) {
                        break;
                    }
                    if (")".equals(token.getText())) {
                        state = 6;
                        break;
                    }
                    if (Spin2Model.isType(token.getText())) {
                        id = TokenId.TYPE;
                        break;
                    }
                    if (token.type == Token.KEYWORD) {
                        Token next = stream.peekNext();
                        if (next.type == Token.KEYWORD) {
                            id = TokenId.TYPE;
                            break;
                        }
                        id = TokenId.METHOD_PARAMETER;
                        break;
                    }
                    if ("=".equals(token.getText())) {
                        state = 5;
                    }
                    break;
                case 5:
                    if (",".equals(token.getText())) {
                        state = 4;
                        break;
                    }
                    if (")".equals(token.getText())) {
                        state = 6;
                        break;
                    }
                    break;

                case 6:
                    if (":".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    if ("|".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    break;

                case 7:
                    id = TokenId.METHOD_RETURN;
                    state = 8;
                    break;
                case 8:
                    if (",".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    else if ("|".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    break;

                case 9:
                    if ("alignw".equalsIgnoreCase(token.getText()) || "alignl".equalsIgnoreCase(token.getText())) {
                        id = TokenId.KEYWORD;
                        break;
                    }
                    if (Spin2Model.isType(token.getText())) {
                        id = TokenId.TYPE;
                        state = 10;
                        break;
                    }
                    if (token.type == Token.KEYWORD) {
                        Token next = stream.peekNext();
                        if (next != null && next.type == Token.KEYWORD) {
                            id = TokenId.TYPE;
                            state = 10;
                            break;
                        }
                    }
                    // fall-through
                case 10:
                    id = TokenId.METHOD_LOCAL;
                    state = 11;
                    break;
                case 11:
                    if (",".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    if (":".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    if ("[".equals(token.getText())) {
                        state = 12;
                        break;
                    }
                    break;
                case 12:
                    if ("]".equals(token.getText())) {
                        state = 11;
                        break;
                    }
                    break;
            }

            if (token.type == Token.NUMBER) {
                id = TokenId.NUMBER;
            }
            else if (token.type == Token.STRING) {
                id = token.getText().length() > 3 ? TokenId.STRING : TokenId.NUMBER;
            }
            else if (id == null) {
                id = symbols.get(token.getText());
                if (id == null) {
                    id = keywords.get(token.getText());
                    if (id == null) {
                        id = spinKeywords.get(token.getText());
                    }
                }
            }
            if (id != null) {
                markers.add(new TokenMarker(token, id));
            }
        }
    }

    void parseDatLine(TokenStream stream, String lastLabel, List<TokenMarker> markers, Map<String, TokenId> localSymbols) {
        Token token;
        boolean debug = false;
        boolean modcX = false;
        int state = 1;

        while ((token = stream.nextToken()).type != Token.EOF) {
            if (token.type == Token.NL) {
                break;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                markers.add(new TokenMarker(token, TokenId.COMMENT));
                continue;
            }

            TokenId id = null;

            if (state == 4) {
                if ("@".equals(token.getText()) || "@@".equals(token.getText()) || "@@@".equals(token.getText())) {
                    Token nextToken = stream.peekNext();
                    if ((":".equals(nextToken.getText()) || ".".equals(nextToken.getText())) && token.isAdjacent(nextToken)) {
                        token = token.merge(stream.nextToken());
                        nextToken = stream.peekNext();
                    }
                    if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                        token = token.merge(stream.nextToken());
                    }
                }
            }
            if (state == 1 || state == 4) {
                if (":".equals(token.getText()) || ".".equals(token.getText())) {
                    Token nextToken = stream.peekNext();
                    if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                        token = token.merge(stream.nextToken());
                    }
                }
            }

            switch (state) {
                case 1:
                    if (Spin2Model.isPAsmCondition(token.getText())) {
                        id = TokenId.PASM_CONDITION;
                        state = 3;
                        break;
                    }
                    if ("debug".equals(token.getText())) {
                        id = TokenId.KEYWORD;
                        state = 5;
                        break;
                    }
                    if (Spin2Model.isPAsmInstruction(token.getText())) {
                        modcX = modcz.contains(token.getText().toUpperCase());
                        id = TokenId.PASM_INSTRUCTION;
                        state = 4;
                        break;
                    }
                    id = token.getText().startsWith(":") || token.getText().startsWith(".") ? TokenId.PASM_LOCAL_LABEL : TokenId.PASM_LABEL;
                    state = 2;
                    break;
                case 2:
                    if (Spin2Model.isPAsmCondition(token.getText())) {
                        id = TokenId.PASM_CONDITION;
                        state = 3;
                        break;
                    }
                    // fall-through
                case 3:
                    if ("debug".equals(token.getText())) {
                        id = TokenId.KEYWORD;
                        state = 5;
                        break;
                    }
                    if (Spin2Model.isPAsmInstruction(token.getText())) {
                        modcX = modcz.contains(token.getText().toUpperCase());
                        id = TokenId.PASM_INSTRUCTION;
                    }
                    state = 4;
                    break;
                case 4:
                    if (Spin2Model.isPAsmModifier(token.getText())) {
                        id = TokenId.PASM_MODIFIER;
                        break;
                    }
                    if (modcX) {
                        id = modczOperands.get(token.getText());
                    }
                    break;
                case 5:
                    id = debugKeywords.get(token.getText());
                    break;
            }

            if (id == null) {
                if (token.type == Token.NUMBER) {
                    id = TokenId.NUMBER;
                }
                else if (token.type == Token.STRING) {
                    id = token.getText().length() > 3 ? TokenId.STRING : TokenId.NUMBER;
                }
                else if (token.getText().startsWith(":") || token.getText().startsWith(".")) {
                    id = TokenId.PASM_LOCAL_LABEL;
                }
                else {
                    id = symbols.get(token.getText());
                    if (id == null) {
                        int index = token.getText().indexOf('.');
                        if (index != -1) {
                            String left = token.getText().substring(0, index);
                            id = symbols.get(left);
                            if (id == TokenId.OBJECT) {
                                markers.add(new TokenMarker(token.start, token.start + index - 1, id));
                                //markers.add(new TokenMarker(token.start + index + 1, token.stop, TokenId.CONSTANT));
                            }
                        }
                    }
                    if (id == null) {
                        id = localSymbols.get(token.getText());
                        if (id == null) {
                            id = keywords.get(token.getText());
                            if (id == null) {
                                id = spinKeywords.get(token.getText());
                            }
                        }
                    }
                }
            }
            if (id != null) {
                markers.add(new TokenMarker(token, id));
            }
        }
    }

    void markConstantToken(Token token, List<TokenMarker> markers) {
        if (token.type == Token.NUMBER) {
            markers.add(new TokenMarker(token, TokenId.NUMBER));
        }
        else if (token.type == Token.STRING) {
            markers.add(new TokenMarker(token, token.getText().length() > 3 ? TokenId.STRING : TokenId.NUMBER));
        }
        else {
            TokenId id = symbols.get(token.getText());
            if (id == null) {
                id = keywords.get(token.getText());
            }
            if (id == null && token.type == Token.KEYWORD) {
                int index = token.getText().indexOf('.');
                if (index > 0) {
                    String left = token.getText().substring(0, index);
                    id = symbols.get(left);
                    if (id == TokenId.OBJECT) {
                        markers.add(new TokenMarker(token.start, token.start + index - 1, id));
                        //markers.add(new TokenMarker(token.start + index + 1, token.stop, TokenId.CONSTANT));
                        return;
                    }
                }
            }
            if (id != null) {
                markers.add(new TokenMarker(token, id));
            }
        }
    }

    @Override
    public boolean hasLineContinuation(int lineIndex, int lineOffset, String lineText) {
        Token token;

        if (root == null) {
            return false;
        }

        for (Token blockCommentToken : root.getComments()) {
            if (lineOffset >= blockCommentToken.start && lineOffset <= blockCommentToken.stop) {
                int index = blockCommentToken.stop - lineOffset + 1;
                if (index >= lineText.length()) {
                    return false;
                }
                lineText = lineText.substring(index);
                break;
            }
        }

        Spin2TokenStream stream = new Spin2TokenStream(lineText);
        while ((token = stream.nextToken()).type != Token.EOF) {
            if (token.type == Token.NEXT_LINE) {
                return true;
            }
        }

        return false;
    }

    void markTokens(Node contextNode, TokenStream stream, boolean debug, List<TokenMarker> markers, Map<String, TokenId> localSymbols, String endMarker) {
        Token token;

        while ((token = stream.nextToken()).type != Token.EOF) {
            String tokenText = token.getText();
            if (tokenText.equals(endMarker)) {
                return;
            }
            if (token.type == Token.COMMENT || token.type == Token.BLOCK_COMMENT || token.type == Token.NEXT_LINE) {
                markers.add(new TokenMarker(token, TokenId.COMMENT));
            }
            else if (token.type == Token.NUMBER) {
                markers.add(new TokenMarker(token, TokenId.NUMBER));
            }
            else if (token.type == Token.STRING) {
                markers.add(new TokenMarker(token, token.getText().length() > 3 ? TokenId.STRING : TokenId.NUMBER));
            }
            else if (token.type != Token.OPERATOR) {
                TokenId id = symbols.get(tokenText);
                if (id == TokenId.OBJECT) {
                    String qualifier = token.getText();
                    markers.add(new TokenMarker(token, id));
                    token = stream.nextToken();
                    if ("[".equals(token.getText())) {
                        markTokens(contextNode, stream, debug, markers, localSymbols, "]");
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
                    int dot = tokenText.indexOf('.');
                    if (dot != -1) {
                        markers.add(new TokenMarker(token.start, token.start + dot - 1, TokenId.OBJECT));
                        markers.add(new TokenMarker(token.start + dot + 1, token.stop, id));
                        continue;
                    }
                }
                if (id == null) {
                    id = localSymbols.get(tokenText);
                    if (id == null) {
                        id = keywords.get(tokenText);
                        if (id == null) {
                            id = spinKeywords.get(tokenText);
                            if (id == null && debug) {
                                id = debugKeywords.get(tokenText);
                            }
                        }
                    }
                }
                if (id != null) {
                    markers.add(new TokenMarker(token, id));
                }
            }
            else if ("[".equals(tokenText)) {
                markTokens(contextNode, stream, debug, markers, localSymbols, "]");
            }
        }
    }

}
