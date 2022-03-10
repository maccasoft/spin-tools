/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.maccasoft.propeller.CompilerMessage;
import com.maccasoft.propeller.SourceTokenMarker;
import com.maccasoft.propeller.model.ConstantAssignEnumNode;
import com.maccasoft.propeller.model.ConstantAssignNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.ExpressionNode;
import com.maccasoft.propeller.model.LocalVariableNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.NodeVisitor;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.ParameterNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;

public class Spin2TokenMarker extends SourceTokenMarker {

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
        keywords.put("BYTEFIT", TokenId.TYPE);
        keywords.put("WORDFIT", TokenId.TYPE);

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
        keywords.put("NAN", TokenId.KEYWORD);
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

        keywords.put("FDEC", TokenId.FUNCTION);
        keywords.put("FDEC_REG_ARRAY", TokenId.FUNCTION);
        keywords.put("FDEC_ARRAY", TokenId.FUNCTION);

        keywords.put("UDEC", TokenId.FUNCTION);
        keywords.put("UDEC_BYTE", TokenId.FUNCTION);
        keywords.put("UDEC_WORD", TokenId.FUNCTION);
        keywords.put("UDEC_LONG", TokenId.FUNCTION);
        keywords.put("SDEC", TokenId.FUNCTION);
        keywords.put("SDEC_BYTE", TokenId.FUNCTION);
        keywords.put("SDEC_WORD", TokenId.FUNCTION);
        keywords.put("SDEC_LONG", TokenId.FUNCTION);
        keywords.put("UHEX", TokenId.FUNCTION);
        keywords.put("UHEX_BYTE", TokenId.FUNCTION);
        keywords.put("UHEX_WORD", TokenId.FUNCTION);
        keywords.put("UHEX_LONG", TokenId.FUNCTION);
        keywords.put("SHEX", TokenId.FUNCTION);
        keywords.put("SHEX_BYTE", TokenId.FUNCTION);
        keywords.put("SHEX_WORD", TokenId.FUNCTION);
        keywords.put("SHEX_LONG", TokenId.FUNCTION);
        keywords.put("UBIN", TokenId.FUNCTION);
        keywords.put("UBIN_BYTE", TokenId.FUNCTION);
        keywords.put("UBIN_WORD", TokenId.FUNCTION);
        keywords.put("UBIN_LONG", TokenId.FUNCTION);
        keywords.put("SBIN", TokenId.FUNCTION);
        keywords.put("SBIN_BYTE", TokenId.FUNCTION);
        keywords.put("SBIN_WORD", TokenId.FUNCTION);
        keywords.put("SBIN_LONG", TokenId.FUNCTION);

        keywords.put("UDEC_", TokenId.FUNCTION);
        keywords.put("UDEC_BYTE_", TokenId.FUNCTION);
        keywords.put("UDEC_WORD_", TokenId.FUNCTION);
        keywords.put("UDEC_LONG_", TokenId.FUNCTION);
        keywords.put("SDEC_", TokenId.FUNCTION);
        keywords.put("SDEC_BYTE_", TokenId.FUNCTION);
        keywords.put("SDEC_WORD_", TokenId.FUNCTION);
        keywords.put("SDEC_LONG_", TokenId.FUNCTION);
        keywords.put("UHEX_", TokenId.FUNCTION);
        keywords.put("UHEX_BYTE_", TokenId.FUNCTION);
        keywords.put("UHEX_WORD_", TokenId.FUNCTION);
        keywords.put("UHEX_LONG_", TokenId.FUNCTION);
        keywords.put("SHEX_", TokenId.FUNCTION);
        keywords.put("SHEX_BYTE_", TokenId.FUNCTION);
        keywords.put("SHEX_WORD_", TokenId.FUNCTION);
        keywords.put("SHEX_LONG_", TokenId.FUNCTION);
        keywords.put("UBIN_", TokenId.FUNCTION);
        keywords.put("UBIN_BYTE_", TokenId.FUNCTION);
        keywords.put("UBIN_WORD_", TokenId.FUNCTION);
        keywords.put("UBIN_LONG_", TokenId.FUNCTION);
        keywords.put("SBIN_", TokenId.FUNCTION);
        keywords.put("SBIN_BYTE_", TokenId.FUNCTION);
        keywords.put("SBIN_WORD_", TokenId.FUNCTION);
        keywords.put("SBIN_LONG_", TokenId.FUNCTION);

        keywords.put("UDEC_REG_ARRAY", TokenId.FUNCTION);
        keywords.put("UDEC_BYTE_ARRAY", TokenId.FUNCTION);
        keywords.put("UDEC_WORD_ARRAY", TokenId.FUNCTION);
        keywords.put("UDEC_LONG_ARRAY", TokenId.FUNCTION);
        keywords.put("SDEC_REG_ARRAY", TokenId.FUNCTION);
        keywords.put("SDEC_BYTE_ARRAY", TokenId.FUNCTION);
        keywords.put("SDEC_WORD_ARRAY", TokenId.FUNCTION);
        keywords.put("SDEC_LONG_ARRAY", TokenId.FUNCTION);
        keywords.put("UHEX_REG_ARRAY", TokenId.FUNCTION);
        keywords.put("UHEX_BYTE_ARRAY", TokenId.FUNCTION);
        keywords.put("UHEX_WORD_ARRAY", TokenId.FUNCTION);
        keywords.put("UHEX_LONG_ARRAY", TokenId.FUNCTION);
        keywords.put("SHEX_REG_ARRAY", TokenId.FUNCTION);
        keywords.put("SHEX_BYTE_ARRAY", TokenId.FUNCTION);
        keywords.put("SHEX_WORD_ARRAY", TokenId.FUNCTION);
        keywords.put("SHEX_LONG_ARRAY", TokenId.FUNCTION);
        keywords.put("UBIN_REG_ARRAY", TokenId.FUNCTION);
        keywords.put("UBIN_BYTE_ARRAY", TokenId.FUNCTION);
        keywords.put("UBIN_WORD_ARRAY", TokenId.FUNCTION);
        keywords.put("UBIN_LONG_ARRAY", TokenId.FUNCTION);
        keywords.put("SBIN_REG_ARRAY", TokenId.FUNCTION);
        keywords.put("SBIN_BYTE_ARRAY", TokenId.FUNCTION);
        keywords.put("SBIN_WORD_ARRAY", TokenId.FUNCTION);
        keywords.put("SBIN_LONG_ARRAY", TokenId.FUNCTION);

        keywords.put("UDEC_REG_ARRAY_", TokenId.FUNCTION);
        keywords.put("UDEC_BYTE_ARRAY_", TokenId.FUNCTION);
        keywords.put("UDEC_WORD_ARRAY_", TokenId.FUNCTION);
        keywords.put("UDEC_LONG_ARRAY_", TokenId.FUNCTION);
        keywords.put("SDEC_REG_ARRAY_", TokenId.FUNCTION);
        keywords.put("SDEC_BYTE_ARRAY_", TokenId.FUNCTION);
        keywords.put("SDEC_WORD_ARRAY_", TokenId.FUNCTION);
        keywords.put("SDEC_LONG_ARRAY_", TokenId.FUNCTION);
        keywords.put("UHEX_REG_ARRAY_", TokenId.FUNCTION);
        keywords.put("UHEX_BYTE_ARRAY_", TokenId.FUNCTION);
        keywords.put("UHEX_WORD_ARRAY_", TokenId.FUNCTION);
        keywords.put("UHEX_LONG_ARRAY_", TokenId.FUNCTION);
        keywords.put("SHEX_REG_ARRAY_", TokenId.FUNCTION);
        keywords.put("SHEX_BYTE_ARRAY_", TokenId.FUNCTION);
        keywords.put("SHEX_WORD_ARRAY_", TokenId.FUNCTION);
        keywords.put("SHEX_LONG_ARRAY_", TokenId.FUNCTION);
        keywords.put("UBIN_REG_ARRAY_", TokenId.FUNCTION);
        keywords.put("UBIN_BYTE_ARRAY_", TokenId.FUNCTION);
        keywords.put("UBIN_WORD_ARRAY_", TokenId.FUNCTION);
        keywords.put("UBIN_LONG_ARRAY_", TokenId.FUNCTION);
        keywords.put("SBIN_REG_ARRAY_", TokenId.FUNCTION);
        keywords.put("SBIN_BYTE_ARRAY_", TokenId.FUNCTION);
        keywords.put("SBIN_WORD_ARRAY_", TokenId.FUNCTION);
        keywords.put("SBIN_LONG_ARRAY_", TokenId.FUNCTION);

        keywords.put("DLY", TokenId.FUNCTION);
        keywords.put("ZSTR", TokenId.FUNCTION);
        keywords.put("LSTR", TokenId.FUNCTION);

        keywords.put("IF", TokenId.FUNCTION);
        keywords.put("IFNOT", TokenId.FUNCTION);
    }

    static Map<String, TokenId> spinKeywords = new HashMap<String, TokenId>();
    static {
        spinKeywords.put("PR0", TokenId.PASM_INSTRUCTION);
        spinKeywords.put("PR1", TokenId.PASM_INSTRUCTION);
        spinKeywords.put("PR2", TokenId.PASM_INSTRUCTION);
        spinKeywords.put("PR3", TokenId.PASM_INSTRUCTION);
        spinKeywords.put("PR4", TokenId.PASM_INSTRUCTION);
        spinKeywords.put("PR5", TokenId.PASM_INSTRUCTION);
        spinKeywords.put("PR6", TokenId.PASM_INSTRUCTION);
        spinKeywords.put("PR7", TokenId.PASM_INSTRUCTION);
    }

    static Set<String> modcz = new HashSet<String>();
    static {
        modcz.add("MODC");
        modcz.add("MODZ");
        modcz.add("MODCZ");
    }

    static Map<String, TokenId> modczOperands = new HashMap<String, TokenId>();
    static {
        modczOperands.put("_CLR", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NC_AND_Z", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_Z_AND_NC", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_GT", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NC_AND_Z", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_Z_AND_NC", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NC", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_GE", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_C_AND_NZ", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NZ_AND_C", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NZ", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NE", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_C_NE_NZ", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NZ_NE_C", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_C_OR_NZ", TokenId.PASM_INSTRUCTION);
        modczOperands.put("_NZ_OR_C", TokenId.PASM_INSTRUCTION);
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
            symbols.put(node.getIdentifier().getText(), TokenId.CONSTANT);
            tokens.add(new TokenMarker(node.getIdentifier(), TokenId.CONSTANT));
        }

        @Override
        public void visitConstantAssignEnum(ConstantAssignEnumNode node) {
            symbols.put(node.getIdentifier().getText(), TokenId.CONSTANT);
            tokens.add(new TokenMarker(node.getIdentifier(), TokenId.CONSTANT));
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

            if (node.getIdentifier() != null) {
                String identifier = node.getIdentifier().getText();
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
        public void visitObject(ObjectNode objectNode) {
            if (objectNode.name == null || objectNode.file == null) {
                return;
            }

            symbols.put(objectNode.name.getText(), TokenId.OBJECT);
            tokens.add(new TokenMarker(objectNode.name, TokenId.OBJECT));
            if (objectNode.file != null) {
                tokens.add(new TokenMarker(objectNode.file, TokenId.STRING));
            }

            String fileName = objectNode.file.getText().substring(1, objectNode.file.getText().length() - 1);
            Node objectRoot = getObjectTree(fileName);
            if (objectRoot != null) {
                objectRoot.accept(new NodeVisitor() {

                    @Override
                    public void visitConstantAssign(ConstantAssignNode node) {
                        symbols.put(objectNode.name.getText() + "." + node.getIdentifier().getText(), TokenId.CONSTANT);
                    }

                    @Override
                    public void visitConstantAssignEnum(ConstantAssignEnumNode node) {
                        symbols.put(objectNode.name.getText() + "." + node.getIdentifier().getText(), TokenId.CONSTANT);
                    }

                    @Override
                    public void visitMethod(MethodNode methodNode) {
                        if (methodNode.name == null) {
                            return;
                        }
                        if ("PUB".equalsIgnoreCase(methodNode.type.getText())) {
                            symbols.put(objectNode.name.getText() + "." + methodNode.name.getText(), TokenId.METHOD_PUB);
                        }
                    }

                });
            }
        }

        @Override
        public void visitMethod(MethodNode node) {
            TokenId id = TokenId.METHOD_PUB;
            if ("PRI".equalsIgnoreCase(node.getType().getText())) {
                id = TokenId.METHOD_PRI;
            }
            tokens.add(new TokenMarker(node.getType(), id));

            if (node.getName() != null) {
                symbols.put(node.getName().getText(), id);
                tokens.add(new TokenMarker(node.getName(), id));
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
                    symbols.put(lastLabel + s, TokenId.PASM_LOCAL_LABEL);
                    symbols.put(lastLabel + "@" + s, TokenId.PASM_LOCAL_LABEL);
                    tokens.add(new TokenMarker(node.label, TokenId.PASM_LOCAL_LABEL));
                }
                else {
                    TokenId id = symbols.get(s);
                    if (id == null) {
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
                TokenId id = keywords.get(node.instruction.getText().toUpperCase());
                if (id == null || id != TokenId.TYPE) {
                    id = TokenId.PASM_INSTRUCTION;
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
                            id = keywords.get(token.getText().toUpperCase());
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
        Map<String, TokenId> locals = new HashMap<String, TokenId>();

        @Override
        public void visitObjects(ObjectsNode node) {
            tokens.add(new TokenMarker(node.getTokens().get(0), TokenId.SECTION));
            for (Node child : node.getChilds()) {
                ObjectNode obj = (ObjectNode) child;
                if (obj.count != null) {
                    markTokens(obj.count);
                }
            }
        }

        @Override
        public void visitMethod(MethodNode node) {
            locals.clear();

            for (Node child : node.getParameters()) {
                locals.put(child.getText(), TokenId.METHOD_LOCAL);
                locals.put("@" + child.getText(), TokenId.METHOD_LOCAL);
            }
            for (Node child : node.getReturnVariables()) {
                locals.put(child.getText(), TokenId.METHOD_RETURN);
                locals.put("@" + child.getText(), TokenId.METHOD_RETURN);
            }

            for (LocalVariableNode child : node.getLocalVariables()) {
                if (child.type != null) {
                    tokens.add(new TokenMarker(child.type, TokenId.TYPE));
                }
                if (child.identifier != null) {
                    locals.put(child.identifier.getText(), TokenId.METHOD_LOCAL);
                    locals.put("@" + child.identifier.getText(), TokenId.METHOD_LOCAL);
                    tokens.add(new TokenMarker(child.identifier, TokenId.METHOD_LOCAL));
                }
            }

            for (Node child : node.getChilds()) {
                if (child instanceof StatementNode) {
                    markTokens(child);
                }
                else if (child instanceof DataLineNode) {
                    markDataTokens((DataLineNode) child, true);
                }
            }
        }

        void markTokens(Node node) {
            for (Token token : node.getTokens()) {
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
                        id = spinKeywords.get(token.getText().toUpperCase());
                    }
                    if (id == null) {
                        id = locals.get(token.getText());
                    }
                    if (id == null) {
                        id = symbols.get(token.getText());
                    }
                    if (id == null) {
                        id = compilerSymbols.get(token.getText());
                    }
                    if (id != null) {
                        if ((id == TokenId.METHOD_PUB || id == TokenId.CONSTANT)) {
                            int dot = token.getText().indexOf('.');
                            if (dot != -1) {
                                tokens.add(new TokenMarker(token.start, token.start + dot - 1, TokenId.OBJECT));
                                tokens.add(new TokenMarker(token.start + dot + 1, token.stop, id));
                            }
                            else {
                                tokens.add(new TokenMarker(token, id));
                            }
                        }
                        else {
                            tokens.add(new TokenMarker(token, id));
                        }
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
            for (Node child : node.getChilds()) {
                markDataTokens((DataLineNode) child, false);
            }
        }

        public void markDataTokens(DataLineNode node, boolean inline) {
            if (node.label != null) {
                String s = node.label.getText();
                if (!s.startsWith(".")) {
                    lastLabel = s;
                }
            }

            boolean isModcz = node.instruction != null && modcz.contains(node.instruction.getText().toUpperCase());

            for (ParameterNode parameter : node.parameters) {
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
                            if (s.startsWith(".") || s.startsWith("@.")) {
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

        @Override
        public void visitExpression(ExpressionNode node) {
            for (Token token : node.getTokens()) {
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

    Map<String, TokenId> symbols = new HashMap<String, TokenId>();
    Map<String, TokenId> compilerSymbols = new HashMap<String, TokenId>();

    public Spin2TokenMarker() {

    }

    @Override
    public void refreshTokens(String text) {
        tokens.clear();
        symbols.clear();
        compilerTokens.clear();

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

    @Override
    public void refreshCompilerTokens(List<CompilerMessage> messages) {
        symbols.clear();
        compilerTokens.clear();

        Iterator<TokenMarker> iter = tokens.iterator();
        while (iter.hasNext()) {
            if (iter.next().getId() != TokenId.COMMENT) {
                iter.remove();
            }
        }

        root.accept(collectKeywordsVisitor);
        root.accept(new NodeVisitor() {

            @Override
            public void visitObject(ObjectNode objectNode) {
                if (objectNode.name == null || objectNode.file == null) {
                    return;
                }

                String fileName = objectNode.file.getText().substring(1, objectNode.file.getText().length() - 1);
                Node objectRoot = getObjectTree(fileName);
                if (objectRoot != null) {
                    objectRoot.accept(new NodeVisitor() {

                        @Override
                        public void visitConstantAssign(ConstantAssignNode node) {
                            compilerSymbols.put(objectNode.name.getText() + "." + node.getIdentifier().getText(), TokenId.CONSTANT);
                        }

                        @Override
                        public void visitConstantAssignEnum(ConstantAssignEnumNode node) {
                            compilerSymbols.put(objectNode.name.getText() + "." + node.getIdentifier().getText(), TokenId.CONSTANT);
                        }

                        @Override
                        public void visitMethod(MethodNode node) {
                            if (node.name == null) {
                                return;
                            }
                            if ("PUB".equalsIgnoreCase(node.type.getText())) {
                                compilerSymbols.put(objectNode.name.getText() + "." + node.name.getText(), TokenId.METHOD_PUB);
                            }
                        }

                    });
                }
            }

        });

        root.accept(updateReferencesVisitor);

        super.refreshCompilerTokens(messages);
    }

}
