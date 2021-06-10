/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.maccasoft.propeller.model.ConstantAssignEnumNode;
import com.maccasoft.propeller.model.ConstantAssignNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.ErrorNode;
import com.maccasoft.propeller.model.ExpressionNode;
import com.maccasoft.propeller.model.LocalVariableNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.ParameterNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;
import com.maccasoft.propeller.spin.Spin2TokenStream.Token;

public class Spin2TokenMarker {

    public static enum TokenId {
        NULL,
        COMMENT,
        SECTION,
        NUMBER,
        STRING,
        KEYWORD,
        FUNCTION,
        OPERATOR,
        TYPE,

        CONSTANT,
        VARIABLE,
        OBJECT,

        METHOD_PUB,
        METHOD_PRI,
        METHOD_LOCAL,
        METHOD_RETURN,
        METHOD_PARAMETER,

        PASM_LABEL,
        PASM_LOCAL_LABEL,
        PASM_CONDITION,
        PASM_TYPE,
        PASM_DIRECTIVE,
        PASM_INSTRUCTION,
        PASM_MODIFIER,

        WARNING,
        ERROR
    }

    static Map<String, TokenId> keywords = new HashMap<String, TokenId>();
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
        keywords.put("STRING", TokenId.FUNCTION);

        keywords.put("LOOKUP", TokenId.FUNCTION);
        keywords.put("LOOKUPZ", TokenId.FUNCTION);
        keywords.put("LOOKDOWN", TokenId.FUNCTION);
        keywords.put("LOOKDOWNZ", TokenId.FUNCTION);

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
        keywords.put("UNTIL", TokenId.KEYWORD);
        keywords.put("NEXT", TokenId.KEYWORD);
        keywords.put("QUIT", TokenId.KEYWORD);

        keywords.put("END", TokenId.KEYWORD);

        keywords.put("DEBUG", TokenId.KEYWORD);

        keywords.put("ROUND", TokenId.KEYWORD);
        keywords.put("ADDPINS", TokenId.KEYWORD);
        keywords.put("ADDBITS", TokenId.KEYWORD);

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

        keywords.put("PR0", TokenId.PASM_INSTRUCTION);
        keywords.put("PR1", TokenId.PASM_INSTRUCTION);
        keywords.put("PR2", TokenId.PASM_INSTRUCTION);
        keywords.put("PR3", TokenId.PASM_INSTRUCTION);
        keywords.put("PR4", TokenId.PASM_INSTRUCTION);
        keywords.put("PR5", TokenId.PASM_INSTRUCTION);
        keywords.put("PR6", TokenId.PASM_INSTRUCTION);
        keywords.put("PR7", TokenId.PASM_INSTRUCTION);

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
    }

    static Map<String, TokenId> pasmKeywords = new HashMap<String, TokenId>();
    static {
        pasmKeywords.put("IJMP3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("IRET3", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("IJMP2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("IRET2", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("IJMP1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("IRET1", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PTRA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("PTRB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DIRA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("DIRB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("OUTA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("OUTB", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("INA", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("INB", TokenId.PASM_INSTRUCTION);

        pasmKeywords.put("_CLR", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NC_AND_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z_AND_NC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_GT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NC_AND_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z_AND_NC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_GE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_AND_NZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NZ_AND_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_NE_NZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NZ_NE_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_OR_NZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NZ_OR_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_AND_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z_AND_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_EQ_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z_EQ_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_E", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NC_OR_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z_OR_NC", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_LT", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_OR_NZ", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_NZ_OR_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_C_OR_Z", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_Z_OR_C", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_LE", TokenId.PASM_INSTRUCTION);
        pasmKeywords.put("_SET", TokenId.PASM_INSTRUCTION);
    }

    public static class TokenMarker implements Comparable<TokenMarker> {

        int start;
        int stop;
        TokenId id;

        String error;

        public TokenMarker(Token token, TokenId id) {
            this.start = token.start;
            this.stop = token.stop;
            this.id = id;
        }

        public TokenMarker(Node node, TokenId id) {
            this.start = node.getStartIndex();
            this.stop = node.getStopIndex();
            this.id = id;
        }

        public TokenMarker(Token startToken, Token stopToken, TokenId id) {
            this.start = startToken.start;
            this.stop = stopToken.stop;
            this.id = id;
        }

        public TokenMarker(int start, int stop, TokenId id) {
            this.start = start;
            this.stop = stop;
            this.id = id;
        }

        public TokenMarker(int start) {
            this.start = start;
            this.stop = start;
        }

        public int getStart() {
            return start;
        }

        public int getStop() {
            return stop;
        }

        public TokenId getId() {
            return id;
        }

        @Override
        public int compareTo(TokenMarker o) {
            return Integer.compare(start, o.start);
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

    }

    Node root;
    TreeSet<TokenMarker> tokens = new TreeSet<TokenMarker>();

    Map<String, TokenId> symbols = new HashMap<String, TokenId>();

    final NodeVisitor collectKeywordsVisitor = new NodeVisitor() {

        String lastLabel = "";

        @Override
        public void visitConstants(ConstantsNode node) {
            if (node.getTextToken() != null) {
                tokens.add(new TokenMarker(node.getTextToken(), TokenId.SECTION));
            }
        }

        @Override
        public void visitConstantAssign(ConstantAssignNode node) {
            if (symbols.containsKey(node.getIdentifier().getText())) {
                tokens.add(new TokenMarker(node.getIdentifier(), TokenId.ERROR));
            }
            else {
                symbols.put(node.getIdentifier().getText(), TokenId.CONSTANT);
                tokens.add(new TokenMarker(node.getIdentifier(), TokenId.CONSTANT));
            }
        }

        @Override
        public void visitConstantAssignEnum(ConstantAssignEnumNode node) {
            if (symbols.containsKey(node.getIdentifier().getText())) {
                tokens.add(new TokenMarker(node.getIdentifier(), TokenId.ERROR));
            }
            else {
                symbols.put(node.getIdentifier().getText(), TokenId.CONSTANT);
                tokens.add(new TokenMarker(node.getIdentifier(), TokenId.CONSTANT));
            }
        }

        @Override
        public void visitVariables(VariablesNode node) {
            tokens.add(new TokenMarker(node.getTokens().get(0), TokenId.SECTION));
        }

        @Override
        public void visitVariable(VariableNode node) {
            if (node.getType() != null) {
                tokens.add(new TokenMarker(node.getType(), TokenId.TYPE));
            }

            String identifier = node.getIdentifier().getText();
            if (symbols.containsKey(identifier)) {
                TokenMarker marker = new TokenMarker(node.getIdentifier(), TokenId.ERROR);
                marker.setError("Symbol already defined");
                tokens.add(marker);
            }
            else {
                symbols.put(identifier, TokenId.VARIABLE);
                symbols.put("@" + identifier, TokenId.VARIABLE);
                tokens.add(new TokenMarker(node.getIdentifier(), TokenId.VARIABLE));
            }
        }

        @Override
        public void visitObjects(ObjectsNode node) {
            tokens.add(new TokenMarker(node.getTokens().get(0), TokenId.SECTION));
        }

        @Override
        public void visitMethod(MethodNode node) {
            if ("PRI".equalsIgnoreCase(node.getType().getText())) {
                tokens.add(new TokenMarker(node.getType(), TokenId.METHOD_PRI));
                if (node.getName() != null) {
                    symbols.put(node.getName().getText(), TokenId.METHOD_PRI);
                    tokens.add(new TokenMarker(node.getName(), TokenId.METHOD_PRI));
                }
            }
            else {
                tokens.add(new TokenMarker(node.getType(), TokenId.METHOD_PUB));
                if (node.getName() != null) {
                    symbols.put(node.getName().getText(), TokenId.METHOD_PUB);
                    tokens.add(new TokenMarker(node.getName(), TokenId.METHOD_PUB));
                }
            }

            for (Node child : node.getParameters()) {
                tokens.add(new TokenMarker(child, TokenId.METHOD_LOCAL));
            }

            for (Node child : node.getReturnVariables()) {
                tokens.add(new TokenMarker(child, TokenId.METHOD_RETURN));
            }
        }

        @Override
        public void visitData(DataNode node) {
            lastLabel = "";
            tokens.add(new TokenMarker(node.getTokens().get(0), TokenId.SECTION));
        }

        @Override
        public void visitDataLine(DataLineNode node) {
            if (node.label != null) {
                String s = node.label.getText();
                if (s.startsWith(".")) {
                    if (symbols.containsKey(lastLabel + s)) {
                        TokenMarker marker = new TokenMarker(node.label, TokenId.ERROR);
                        marker.setError("Symbol already defined");
                        tokens.add(marker);
                    }
                    else {
                        symbols.put(lastLabel + s, TokenId.PASM_LOCAL_LABEL);
                        symbols.put(lastLabel + "@" + s, TokenId.PASM_LOCAL_LABEL);
                        tokens.add(new TokenMarker(node.label, TokenId.PASM_LOCAL_LABEL));
                    }
                }
                else {
                    TokenId id = symbols.get(s);
                    if (id == null) {
                        id = pasmKeywords.get(s);
                    }
                    if (id != null) {
                        TokenMarker marker = new TokenMarker(node.label, TokenId.ERROR);
                        marker.setError("Symbol already defined");
                        tokens.add(marker);
                    }
                    else {
                        symbols.put(s, TokenId.PASM_LABEL);
                        symbols.put("@" + s, TokenId.PASM_LABEL);
                        tokens.add(new TokenMarker(node.label, TokenId.PASM_LABEL));
                    }
                    lastLabel = s;
                }
            }
            if (node.condition != null) {
                tokens.add(new TokenMarker(node.condition, TokenId.PASM_CONDITION));
            }
            if (node.instruction != null) {
                tokens.add(new TokenMarker(node.instruction, TokenId.PASM_INSTRUCTION));
            }
            if (node.modifier != null) {
                tokens.add(new TokenMarker(node.modifier, TokenId.PASM_MODIFIER));
            }
        }

    };

    final NodeVisitor updateReferencesVisitor = new NodeVisitor() {

        String lastLabel = "";
        Map<String, TokenId> locals = new HashMap<String, TokenId>();

        @Override
        public void visitMethod(MethodNode node) {
            locals.clear();

            for (Node child : node.getParameters()) {
                locals.put(child.getText(), TokenId.METHOD_LOCAL);
            }
            for (Node child : node.getReturnVariables()) {
                locals.put(child.getText(), TokenId.METHOD_RETURN);
            }

            for (LocalVariableNode child : node.getLocalVariables()) {
                if (child.type != null) {
                    tokens.add(new TokenMarker(child.type, TokenId.TYPE));
                }
                locals.put(child.identifier.getText(), TokenId.METHOD_LOCAL);
                if (symbols.containsKey(child.identifier.getText())) {
                    TokenMarker marker = new TokenMarker(child.identifier, TokenId.ERROR);
                    marker.setError("Symbol already defined");
                    tokens.add(marker);
                }
                else {
                    tokens.add(new TokenMarker(child.identifier, TokenId.METHOD_LOCAL));
                }
            }

            for (Node child : node.getChilds()) {
                if (child instanceof StatementNode) {
                    markTokens(child);
                }
            }
        }

        void markTokens(Node node) {
            for (Token token : node.getTokens()) {
                if (token.type == Spin2TokenStream.NUMBER) {
                    tokens.add(new TokenMarker(token, TokenId.NUMBER));
                }
                else if (token.type == Spin2TokenStream.OPERATOR) {
                    tokens.add(new TokenMarker(token, TokenId.OPERATOR));
                }
                else if (token.type == Spin2TokenStream.STRING) {
                    tokens.add(new TokenMarker(token, TokenId.STRING));
                }
                else {
                    TokenId id = keywords.get(token.getText().toUpperCase());
                    if (id == null) {
                        id = locals.get(token.getText());
                    }
                    if (id == null) {
                        id = symbols.get(token.getText());
                    }
                    if (id == null) {
                        TokenMarker marker = new TokenMarker(token, TokenId.ERROR);
                        marker.setError("Symbol is undefined");
                        tokens.add(marker);
                    }
                    else {
                        tokens.add(new TokenMarker(token, id));
                    }
                }
            }
            for (Node child : node.getChilds()) {
                markTokens(child);
            }
        }

        @Override
        public void visitData(DataNode node) {
            lastLabel = "";
        }

        @Override
        public void visitDataLine(DataLineNode node) {
            if (node.label != null) {
                String s = node.label.getText();
                if (!s.startsWith(".")) {
                    lastLabel = s;
                }
            }

            for (ParameterNode parameter : node.parameters) {
                for (Token token : parameter.getTokens()) {
                    TokenId id = null;
                    if (token.type == Spin2TokenStream.NUMBER) {
                        tokens.add(new TokenMarker(token, TokenId.NUMBER));
                    }
                    else if (token.type == Spin2TokenStream.OPERATOR) {
                        tokens.add(new TokenMarker(token, TokenId.OPERATOR));
                    }
                    else if (token.type == Spin2TokenStream.STRING) {
                        tokens.add(new TokenMarker(token, TokenId.STRING));
                    }
                    else {
                        String s = token.getText();
                        if (s.startsWith(".") || s.startsWith("@.")) {
                            s = lastLabel + s;
                        }
                        id = symbols.get(s);
                        if (id == null) {
                            id = pasmKeywords.get(token.getText().toUpperCase());
                        }
                        if (id == null) {
                            TokenMarker marker = new TokenMarker(token, TokenId.ERROR);
                            marker.setError("Symbol is undefined");
                            tokens.add(marker);
                        }
                        else {
                            tokens.add(new TokenMarker(token, id));
                        }
                    }
                }
            }
        }

        @Override
        public void visitExpression(ExpressionNode node) {
            for (Token token : node.getTokens()) {
                if (token.type == Spin2TokenStream.NUMBER) {
                    tokens.add(new TokenMarker(token, TokenId.NUMBER));
                }
                else if (token.type == Spin2TokenStream.OPERATOR) {
                    tokens.add(new TokenMarker(token, TokenId.OPERATOR));
                }
                else if (token.type == Spin2TokenStream.STRING) {
                    tokens.add(new TokenMarker(token, TokenId.STRING));
                }
                else {
                    TokenId id = keywords.get(token.getText().toUpperCase());
                    if (id == null) {
                        id = symbols.get(token.getText());
                    }
                    if (id == null) {
                        TokenMarker marker = new TokenMarker(token, TokenId.ERROR);
                        marker.setError("Symbol is undefined");
                        tokens.add(marker);
                    }
                    else {
                        tokens.add(new TokenMarker(token, id));
                    }
                }
            }
        }

        @Override
        public void visitError(ErrorNode node) {
            TokenMarker marker = new TokenMarker(node.getStartToken(), node.getStopToken(), TokenId.ERROR);
            if (node.getDescription() != null) {
                marker.setError(node.getDescription());
            }
            tokens.add(marker);
        }

    };

    public Spin2TokenMarker() {

    }

    public void refreshTokens(String text) {
        tokens.clear();
        symbols.clear();

        Spin2TokenStream stream = new Spin2TokenStream(text);
        Spin2Parser subject = new Spin2Parser(stream);
        root = subject.parse();

        // Comments are hidden from the parser
        for (Token token : stream.getHiddenTokens()) {
            tokens.add(new TokenMarker(token, TokenId.COMMENT));
        }

        // Collect known keywords and symbols
        root.accept(collectKeywordsVisitor);

        // Update symbols references from expressions
        root.accept(updateReferencesVisitor);
    }

    public Set<TokenMarker> getLineTokens(int lineStart, String lineText) {
        return getLineTokens(lineStart, lineStart + lineText.length());
    }

    public TokenMarker getMarkerAtOffset(int offset) {
        for (TokenMarker marker : tokens) {
            if (offset >= marker.start && offset <= marker.stop) {
                return marker;
            }
        }
        return null;
    }

    public Set<TokenMarker> getLineTokens(int lineStart, int lineStop) {
        Set<TokenMarker> result = new TreeSet<TokenMarker>();

        if (tokens.size() == 0) {
            return result;
        }

        TokenMarker firstMarker = tokens.floor(new TokenMarker(lineStart, lineStop, null));
        if (firstMarker == null) {
            firstMarker = tokens.first();
        }

        for (TokenMarker entry : tokens.tailSet(firstMarker)) {
            int start = entry.getStart();
            int stop = entry.getStop();
            if ((lineStart >= start && lineStart <= stop) || (lineStop >= start && lineStop <= stop)) {
                result.add(entry);
            }
            else if (stop >= lineStart && stop <= lineStop) {
                result.add(entry);
            }
            if (start >= lineStop) {
                break;
            }
        }

        return result;
    }

    public Node getContextAt(int index) {
        if (root == null) {
            return null;
        }

        List<Node> allNodes = new ArrayList<Node>();
        root.accept(new NodeVisitor() {

            @Override
            public void visitConstants(ConstantsNode node) {
                allNodes.add(node);
            }

            @Override
            public void visitVariables(VariablesNode node) {
                allNodes.add(node);
            }

            @Override
            public void visitObjects(ObjectsNode node) {
                allNodes.add(node);
            }

            @Override
            public void visitStatement(StatementNode node) {
                allNodes.add(node);
            }

            @Override
            public void visitData(DataNode node) {
                allNodes.add(node);
            }

            @Override
            public void visitDataLine(DataLineNode node) {
                allNodes.add(node);
            }

        });
        for (int i = 0; i < allNodes.size() - 1; i++) {
            int nodeStart = allNodes.get(i).getStartIndex();
            int nodeStop = allNodes.get(i + 1).getStartIndex();
            if (index >= nodeStart && index < nodeStop) {
                return allNodes.get(i);
            }
        }
        if (allNodes.size() != 0) {
            return allNodes.get(allNodes.size() - 1);
        }
        return null;
    }

    public Node getContextAt(Node node, int index) {
        for (Node child : node.getChilds()) {
            if (child instanceof DataLineNode) {
                if (index >= child.getStartIndex() && index <= child.getStopIndex()) {
                    return child;
                }
            }
            if (child instanceof StatementNode) {
                Node result = getContextAt(child, index);
                if (result != null) {
                    return result;
                }
            }
        }

        if (index >= node.getStartIndex() && index <= node.getStopIndex()) {
            return node;
        }

        return null;
    }

    public Token getTokenAt(int index) {
        if (root == null) {
            return null;
        }
        return getTokenAt(root, index);
    }

    public Token getTokenAt(Node node, int index) {
        for (Token token : node.getTokens()) {
            if (index >= token.start && index <= token.stop) {
                return token;
            }
        }
        for (Node child : node.getChilds()) {
            Token token = getTokenAt(child, index);
            if (token != null) {
                return token;
            }
        }

        return null;
    }

    public String getMethod(String symbol) {
        StringBuilder sb = new StringBuilder();

        root.accept(new NodeVisitor() {

            @Override
            public void visitConstantAssign(ConstantAssignNode node) {
                if (node.getIdentifier() == null) {
                    return;
                }
                if (!symbol.equals(node.getIdentifier().getText())) {
                    return;
                }
                sb.append("<b>");
                sb.append(node.getText());
                sb.append("</b>");
            }

            @Override
            public void visitMethod(MethodNode node) {
                if (node.getName() == null) {
                    return;
                }
                if (!symbol.equals(node.getName().getText())) {
                    return;
                }

                sb.append("<b>");
                sb.append(node.getType().getText());
                sb.append(" ");
                if (node.getName() != null) {
                    sb.append(node.getName().getText());
                }

                sb.append("(");
                for (Node child : node.getParameters()) {
                    if (sb.charAt(sb.length() - 1) != '(') {
                        sb.append(", ");
                    }
                    sb.append(child.getText());
                    tokens.add(new TokenMarker(child, TokenId.METHOD_LOCAL));
                }
                sb.append(")");

                if (node.getReturnVariables().size() != 0) {
                    sb.append(" : ");
                    for (Node child : node.getReturnVariables()) {
                        if (sb.charAt(sb.length() - 2) != ':') {
                            sb.append(", ");
                        }
                        sb.append(child.getText());
                        tokens.add(new TokenMarker(child, TokenId.METHOD_RETURN));
                    }
                }
                sb.append("</b>");
            }

        });

        return sb.length() != 0 ? sb.toString() : null;
    }

    public Node getRoot() {
        return root;
    }
}
