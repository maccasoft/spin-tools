/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

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
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.SourceProvider;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.TypeDefinitionNode;
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
        keywords.put("STRUCT", TokenId.TYPE);

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

        keywords.put("END", TokenId.KEYWORD);

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

        keywords.put("defined", TokenId.DIRECTIVE);
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

    boolean collectLinkedObjects;

    public Spin2TokenMarker(SourceProvider sourceProvider) {
        super(sourceProvider);
        this.constantSeparator = ".";
        this.localLabelPrefix = ".";
    }

    @Override
    public void refreshTokens(String text) {
        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser subject = new Spin2Parser(stream);
        root = subject.parse();

        tokens.clear();
        for (Token token : root.getComments()) {
            if (token.type == Token.NEXT_LINE) {
                tokens.add(new TokenMarker(token.substring(0, 2), TokenId.OPERATOR));
                tokens.add(new TokenMarker(token.substring(3), TokenId.COMMENT));
            }
            else {
                tokens.add(new TokenMarker(token, TokenId.COMMENT));
            }
        }
        tokens.addAll(excludedNodes);

        collectLinkedObjects = false;

        collectKeywords(root);
        updateReferences(root);
    }

    @Override
    public void refreshCompilerTokens(List<CompilerException> messages) {
        tokens.clear();
        symbols.clear();
        externals.clear();
        cache.clear();

        excludedNodes.clear();

        for (Token token : root.getComments()) {
            if (token.type == Token.NEXT_LINE) {
                tokens.add(new TokenMarker(token.substring(0, 2), TokenId.OPERATOR));
                tokens.add(new TokenMarker(token.substring(3), TokenId.COMMENT));
            }
            else {
                tokens.add(new TokenMarker(token, TokenId.COMMENT));
            }
        }

        collectLinkedObjects = true;

        collectKeywords(root);
        updateReferences(root);

        super.refreshCompilerTokens(messages);
    }

    NodeVisitor collectKeywordsVisitor = new NodeVisitor() {

        String lastLabel = "";

        @Override
        public void visitDirective(DirectiveNode node) {
            if (!node.isExclude()) {
                collectTokens(node);
            }
            else {
                addToExcluded(node);
            }
        }

        void collectTokens(DirectiveNode node) {
            if (node instanceof DirectiveNode.DefineNode) {
                Token identifier = ((DirectiveNode.DefineNode) node).getIdentifier();
                if (identifier != null) {
                    symbols.put(identifier.getText(), TokenId.CONSTANT);
                }
            }
            if (node.getTokenCount() != 0) {
                tokens.add(new TokenMarker(node.getStartIndex(), node.getStopIndex(), TokenId.DIRECTIVE));
            }
        }

        @Override
        public boolean visitConstants(ConstantsNode node) {
            if (!node.isExclude()) {
                if (node.getTextToken() != null) {
                    tokens.add(new TokenMarker(node.getTextToken(), TokenId.SECTION));
                }
            }
            else {
                addToExcluded(node);
            }
            return true;
        }

        @Override
        public void visitConstant(ConstantNode node) {
            if (!node.isExclude()) {
                if (node.getIdentifier() != null) {
                    symbols.put(node.getIdentifier().getText(), TokenId.CONSTANT);
                    tokens.add(new TokenMarker(node.getIdentifier(), TokenId.CONSTANT));
                }
            }
            else {
                addToExcluded(node);
            }
        }

        @Override
        public void visitTypeDefinition(TypeDefinitionNode node) {
            if (!node.isExclude()) {
                if (node.getType() != null) {
                    tokens.add(new TokenMarker(node.getType(), TokenId.TYPE));
                }
                if (node.getIdentifier() != null) {
                    symbols.put(node.getIdentifier().getText(), TokenId.TYPE);
                    symbols.put("^" + node.getIdentifier().getText(), TokenId.TYPE);
                    tokens.add(new TokenMarker(node.getIdentifier(), TokenId.CONSTANT));
                }
            }
            else {
                addToExcluded(node);
            }
        }

        @Override
        public boolean visitVariables(VariablesNode node) {
            if (!node.isExclude()) {
                if (!(node.getParent() instanceof VariablesNode) && node.getTokenCount() != 0) {
                    tokens.add(new TokenMarker(node.getStartToken(), TokenId.SECTION));
                }
            }
            else {
                addToExcluded(node);
            }
            return true;
        }

        @Override
        public void visitVariable(VariableNode node) {
            if (!node.isExclude()) {
                if (node.getIdentifier() != null) {
                    String identifier = node.getIdentifier().getText();
                    symbols.put(identifier, TokenId.VARIABLE);
                    symbols.put("@" + identifier, TokenId.VARIABLE);
                    symbols.put("@@" + identifier, TokenId.VARIABLE);
                    tokens.add(new TokenMarker(node.getIdentifier(), TokenId.VARIABLE));
                }
            }
            else {
                addToExcluded(node);
            }
        }

        @Override
        public boolean visitObjects(ObjectsNode node) {
            if (!node.isExclude()) {
                tokens.add(new TokenMarker(node.getTokens().get(0), TokenId.SECTION));
            }
            else {
                addToExcluded(node);
            }
            return true;
        }

        @Override
        public void visitObject(ObjectNode node) {
            if (!node.isExclude()) {
                collectTokens(node);
            }
            else {
                addToExcluded(node);
            }
        }

        void collectTokens(ObjectNode node) {
            if (node.name == null || node.file == null) {
                return;
            }

            symbols.put(node.name.getText(), TokenId.OBJECT);
            tokens.add(new TokenMarker(node.name, TokenId.OBJECT));
            if (node.file != null) {
                tokens.add(new TokenMarker(node.file, TokenId.STRING));
            }

            for (ObjectNode.ParameterNode param : node.parameters) {
                if (param.identifier != null) {
                    tokens.add(new TokenMarker(param.identifier, TokenId.CONSTANT));
                }
            }

            if (!collectLinkedObjects) {
                return;
            }

            Node root = getObjectTree(node.getFileName());
            if (root != null) {
                String qualifier = node.name.getText();
                root.accept(new NodeVisitor() {

                    @Override
                    public void visitConstant(ConstantNode node) {
                        if (node.getIdentifier() != null) {
                            externals.put(qualifier + constantSeparator + node.getIdentifier().getText(), TokenId.CONSTANT);
                        }
                    }

                    @Override
                    public void visitTypeDefinition(TypeDefinitionNode node) {
                        if (!node.isExclude()) {
                            if (node.getIdentifier() != null) {
                                externals.put(qualifier + "." + node.getIdentifier().getText(), TokenId.TYPE);
                            }
                        }
                    }

                    @Override
                    public boolean visitVariables(VariablesNode node) {
                        return false;
                    }

                    @Override
                    public boolean visitObjects(ObjectsNode node) {
                        return false;
                    }

                    @Override
                    public boolean visitMethod(MethodNode node) {
                        if (!node.isExclude()) {
                            if ("PUB".equalsIgnoreCase(node.type.getText())) {
                                externals.put(qualifier + "." + node.name.getText(), TokenId.METHOD_PUB);
                            }
                        }
                        return false;
                    }

                    @Override
                    public boolean visitData(DataNode node) {
                        return false;
                    }

                });
            }
        }

        @Override
        public boolean visitMethod(MethodNode node) {
            if (!node.isExclude()) {
                collectTokens(node);
            }
            else {
                addToExcluded(node);
            }
            return true;
        }

        void collectTokens(MethodNode node) {
            TokenId id = TokenId.METHOD_PUB;
            if ("PRI".equalsIgnoreCase(node.getType().getText())) {
                id = TokenId.METHOD_PRI;
            }
            tokens.add(new TokenMarker(node.getType(), id));

            if (node.getName() != null) {
                symbols.put(node.getName().getText(), id);
                tokens.add(new TokenMarker(node.getName(), id));
            }

            for (MethodNode.ParameterNode child : node.getParameters()) {
                tokens.add(new TokenMarker(child.getIdentifier(), TokenId.METHOD_LOCAL));
            }

            for (MethodNode.ReturnNode child : node.getReturnVariables()) {
                tokens.add(new TokenMarker(child.getIdentifier(), TokenId.METHOD_RETURN));
            }
        }

        @Override
        public boolean visitStatement(StatementNode node) {
            if (node.isExclude()) {
                addToExcluded(node);
            }
            return true;
        }

        @Override
        public boolean visitData(DataNode node) {
            if (!node.isExclude()) {
                lastLabel = "";
                tokens.add(new TokenMarker(node.getTokens().get(0), TokenId.SECTION));
            }
            else {
                addToExcluded(node);
            }
            return true;
        }

        @Override
        public void visitDataLine(DataLineNode node) {
            if (!node.isExclude()) {
                collectTokens(node);
            }
            else {
                addToExcluded(node);
            }
        }

        void collectTokens(DataLineNode node) {
            if (node.label != null) {
                String s = node.label.getText();
                if (s.startsWith(".")) {
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
                if (Spin2Model.isPAsmInstruction(node.instruction.getText())) {
                    tokens.add(new TokenMarker(node.instruction, TokenId.PASM_INSTRUCTION));
                }
                else if ("debug".equalsIgnoreCase(node.instruction.getText())) {
                    tokens.add(new TokenMarker(node.instruction, TokenId.KEYWORD));
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
                            TokenId id;
                            String s = token.getText().toUpperCase();
                            id = debugKeywords.get(s);
                            if (id == null && s.startsWith("`")) {
                                id = debugKeywords.get(s.substring(1));
                            }
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

    void collectKeywords(Node root) {
        root.accept(collectKeywordsVisitor);
    }

    NodeVisitor updateReferencesVisitor = new NodeVisitor() {

        String lastLabel = "";

        @Override
        public void visitConstant(ConstantNode node) {
            if (!node.isExclude()) {
                if (node.getStart() != null) {
                    markTokens(node.getStart(), 0, null);
                }
                if (node.getStep() != null) {
                    markTokens(node.getStep(), 0, null);
                }
                if (node.getExpression() != null) {
                    markTokens(node.getExpression(), 0, null);
                }
                if (node.getMultiplier() != null) {
                    markTokens(node.getMultiplier(), 0, null);
                }
            }
        }

        @Override
        public void visitTypeDefinition(TypeDefinitionNode node) {
            if (!node.isExclude()) {
                updateTokens(node);
            }
        }

        void updateTokens(TypeDefinitionNode node) {
            List<Token> list = node.getTokens();

            int i = 1;
            if (node.getType() != null) {
                i++;
            }
            while (i < list.size()) {
                Token token = list.get(i++);
                if (token.type == Token.NUMBER) {
                    tokens.add(new TokenMarker(token, TokenId.NUMBER));
                }
                else if (token.type == Token.OPERATOR) {
                    tokens.add(new TokenMarker(token, TokenId.OPERATOR));
                }
                else {
                    TokenId id = keywords.get(token.getText());
                    if (id == null || id != TokenId.TYPE) {
                        id = symbols.get(token.getText());
                        if (id != null && id == TokenId.TYPE) {
                            id = TokenId.CONSTANT;
                        }
                        else if (id == null || id != TokenId.TYPE) {
                            id = TokenId.VARIABLE;
                        }
                    }
                    tokens.add(new TokenMarker(token, id));
                }
            }
        }

        @Override
        public void visitVariable(VariableNode node) {
            if (!node.isExclude()) {
                if (node.type != null) {
                    TokenId id = symbols.get(node.type.getText());
                    if (id == null) {
                        id = keywords.get(node.type.getText());
                    }
                    if (id != null && id == TokenId.TYPE) {
                        tokens.add(new TokenMarker(node.type, id));
                    }
                    else if (id == null) {
                        id = externals.get(node.type.getText());
                        if (id != null) {
                            int dot = node.type.getText().indexOf('.');
                            tokens.add(new TokenMarker(node.type.start, node.type.start + dot - 1, TokenId.OBJECT));
                            tokens.add(new TokenMarker(node.type.start + dot + 1, node.type.stop, id));
                        }
                    }
                }
                if (node.getSize() != null) {
                    markTokens(node.getSize(), 0, null);
                }
            }
        }

        @Override
        public void visitDirective(DirectiveNode node) {
            if (!node.isExclude()) {
                markTokens(node, 2, null);
            }
        }

        @Override
        public void visitObject(ObjectNode node) {
            if (!node.isExclude()) {
                if (node.count != null) {
                    markTokens(node.count, 0, null);
                }
                for (ObjectNode.ParameterNode param : node.parameters) {
                    if (param.expression != null) {
                        markTokens(param.expression, 0, null);
                    }
                }
            }
        }

        @Override
        public boolean visitMethod(MethodNode node) {
            if (!node.isExclude()) {
                updateTokens(node);
            }

            node.accept(new NodeVisitor() {

                @Override
                public void visitDirective(DirectiveNode node) {
                    updateReferencesVisitor.visitDirective(node);
                }

                @Override
                public boolean visitStatement(StatementNode node) {
                    if (!node.isExclude()) {
                        markTokens(node, 0, null);
                    }
                    else {
                        tokens.add(new TokenMarker(node.getStartIndex(), node.getStopIndex(), TokenId.COMMENT));
                    }
                    return true;
                }

                @Override
                public void visitDataLine(DataLineNode node) {
                    if (!node.isExclude()) {
                        updateTokens(node, true);
                    }
                    else {
                        tokens.add(new TokenMarker(node.getStartIndex(), node.getStopIndex(), TokenId.COMMENT));
                    }
                }

            });

            return false;
        }

        void updateTokens(MethodNode node) {
            locals.clear();

            for (MethodNode.ParameterNode child : node.getParameters()) {
                if (child.type != null) {
                    TokenId id = symbols.get(child.type.getText());
                    if (id == null) {
                        id = keywords.get(child.type.getText());
                    }
                    if (id != null && id == TokenId.TYPE) {
                        tokens.add(new TokenMarker(child.type, id));
                    }
                    else if (id == null) {
                        id = externals.get(child.type.getText());
                        if (id != null) {
                            int dot = child.type.getText().indexOf('.');
                            tokens.add(new TokenMarker(child.type.start, child.type.start + dot - 1, TokenId.OBJECT));
                            tokens.add(new TokenMarker(child.type.start + dot + 1, child.type.stop, id));
                        }
                    }
                }
                if (child.identifier != null) {
                    locals.put(child.identifier.getText(), TokenId.METHOD_LOCAL);
                    locals.put("@" + child.identifier.getText(), TokenId.METHOD_LOCAL);
                    locals.put("@@" + child.identifier.getText(), TokenId.METHOD_LOCAL);
                }
            }
            for (MethodNode.ReturnNode child : node.getReturnVariables()) {
                if (child.type != null) {
                    TokenId id = symbols.get(child.type.getText());
                    if (id != null && id == TokenId.TYPE) {
                        tokens.add(new TokenMarker(child.type, id));
                    }
                    else if (id == null) {
                        id = externals.get(child.type.getText());
                        if (id != null) {
                            int dot = child.type.getText().indexOf('.');
                            tokens.add(new TokenMarker(child.type.start, child.type.start + dot - 1, TokenId.OBJECT));
                            tokens.add(new TokenMarker(child.type.start + dot + 1, child.type.stop, id));
                        }
                    }
                }
                if (child.identifier != null) {
                    locals.put(child.identifier.getText(), TokenId.METHOD_RETURN);
                    locals.put("@" + child.identifier.getText(), TokenId.METHOD_RETURN);
                    locals.put("@@" + child.identifier.getText(), TokenId.METHOD_RETURN);
                }
            }

            for (MethodNode.ParameterNode child : node.getParameters()) {
                if (child.defaultValue != null) {
                    markTokens(child, 1, null);
                }
            }

            for (MethodNode.LocalVariableNode child : node.getLocalVariables()) {
                if (child.type != null) {
                    TokenId id = symbols.get(child.type.getText());
                    if (id == null) {
                        id = keywords.get(child.type.getText());
                    }
                    if (id != null && id == TokenId.TYPE) {
                        tokens.add(new TokenMarker(child.type, id));
                    }
                    else if (id == null) {
                        id = externals.get(child.type.getText());
                        if (id != null) {
                            int dot = child.type.getText().indexOf('.');
                            tokens.add(new TokenMarker(child.type.start, child.type.start + dot - 1, TokenId.OBJECT));
                            tokens.add(new TokenMarker(child.type.start + dot + 1, child.type.stop, id));
                        }
                    }
                }
                if (child.identifier != null) {
                    locals.put(child.identifier.getText(), TokenId.METHOD_LOCAL);
                    locals.put("@" + child.identifier.getText(), TokenId.METHOD_LOCAL);
                    locals.put("@@" + child.identifier.getText(), TokenId.METHOD_LOCAL);
                    tokens.add(new TokenMarker(child.identifier, TokenId.METHOD_LOCAL));
                }
                if (child.size != null) {
                    markTokens(child.size, 0, null);
                }
            }
        }

        @Override
        public boolean visitData(DataNode node) {
            lastLabel = "";
            return true;
        }

        @Override
        public void visitDataLine(DataLineNode node) {
            if (!node.isExclude()) {
                updateTokens(node, false);
            }
            else {
                tokens.add(new TokenMarker(node.getStartIndex(), node.getStopIndex(), TokenId.COMMENT));
            }
        }

        void updateTokens(DataLineNode node, boolean inline) {
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
                        if (s.startsWith(".") || s.startsWith("@.") || s.startsWith("@@.")) {
                            s = lastLabel + s;
                        }
                        id = symbols.get(s);
                        if (id == null) {
                            id = externals.get(s);
                        }
                        if (id == null && isModcz) {
                            id = modczOperands.get(token.getText());
                        }
                        if (id == null) {
                            id = keywords.get(token.getText());
                        }
                        if (id == null && ("fvar".equalsIgnoreCase(token.getText()) || "fvars".equalsIgnoreCase(token.getText()))) {
                            id = TokenId.TYPE;
                        }
                        if (id == null && inline) {
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

    };

    void updateReferences(Node root) {
        root.accept(updateReferencesVisitor);
    }

    void markTokens(Node node, int i, String endMarker) {
        List<Token> list = node.getTokens();
        boolean debug = list.size() != 0 && "debug".equalsIgnoreCase(list.get(0).getText());

        if (!node.isExclude()) {
            markTokens(list, i, endMarker, debug);
        }
        else {
            tokens.add(new TokenMarker(node.getStartIndex(), node.getStopIndex(), TokenId.COMMENT));
        }
    }

    int markTokens(List<Token> list, int i, String endMarker, boolean debug) {

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
                    i = markTokens(list, i, "]", debug);
                    if (i < list.size()) {
                        token = list.get(i);
                    }
                }
                if (token.getText().equals("(")) {
                    i = markTokens(list, i, ")", debug);
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
                TokenId id = locals.get(token.getText());
                if (id == null) {
                    id = symbols.get(token.getText());
                    if (id == null && token.getText().startsWith("@")) {
                        id = symbols.get(token.getText().substring(1));
                    }
                }
                if (id == null) {
                    id = externals.get(token.getText());
                    if (id == null && token.getText().startsWith("@")) {
                        id = externals.get(token.getText().substring(1));
                    }
                }
                if (debug && id == null) {
                    String s = token.getText().toUpperCase();
                    id = debugKeywords.get(s);
                    if (id == null && s.startsWith("`")) {
                        id = debugKeywords.get(s.substring(1));
                    }
                }
                if (id == null) {
                    id = keywords.get(token.getText());
                    if (id == null) {
                        id = spinKeywords.get(token.getText());
                    }
                }
                if (id == null && dot != -1) {
                    String left = token.getText().substring(0, dot);
                    TokenId leftId = locals.get(left);
                    if (leftId == null) {
                        leftId = symbols.get(left);
                        if (leftId == null && left.startsWith("@")) {
                            leftId = symbols.get(left.substring(1));
                        }
                    }
                    if (leftId == null) {
                        TokenId specialId = keywords.get(left);
                        if (specialId == TokenId.CONSTANT) {
                            leftId = specialId;
                        }
                    }
                    if (leftId != null) {
                        tokens.add(new TokenMarker(token.start, token.start + dot, leftId));
                    }

                    dot = token.getText().lastIndexOf('.');
                    switch (token.getText().substring(dot + 1).toUpperCase()) {
                        case "LONG":
                        case "WORD":
                        case "BYTE":
                            tokens.add(new TokenMarker(token.start + dot + 1, token.stop, TokenId.TYPE));
                            break;
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
                                i = markTokens(list, i + 1, "]", debug);
                                if (i < list.size()) {
                                    token = list.get(i);
                                }
                            }
                            if (token.getText().startsWith(".")) {
                                String qualifiedName = objToken.getText() + token.getText();
                                id = symbols.get(qualifiedName);
                                if (id == null && qualifiedName.startsWith("@")) {
                                    id = symbols.get(qualifiedName.substring(1));
                                }
                                if (id == null) {
                                    id = externals.get(qualifiedName);
                                    if (id == null && qualifiedName.startsWith("@")) {
                                        id = externals.get(qualifiedName.substring(1));
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

        return i;
    }

}
