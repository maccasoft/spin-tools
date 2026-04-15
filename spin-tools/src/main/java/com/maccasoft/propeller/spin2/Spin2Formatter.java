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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.maccasoft.propeller.Formatter;
import com.maccasoft.propeller.model.Token;

public class Spin2Formatter extends Formatter {

    static Set<String> builtInConstants = new HashSet<>(Arrays.asList(
        "_CLKMODE",
        "_CLKFREQ",
        "_XTLFREQ",
        "_XINFREQ",
        "_ERRFREQ",

        "_RCSLOW",

        "CLKMODE",
        "CLKFREQ",
        "CLKMODE_",
        "CLKFREQ_",

        "DEBUG_PIN",
        "DEBUG_PIN_RX",
        "DEBUG_PIN_TX",
        "DEBUG_BAUD",
        "DEBUG_DELAY",
        "DEBUG_COGINIT",
        "DEBUG_MAIN",
        "DEBUG_COGS",
        "DEBUG_MASK",

        "P_TRUE_A",
        "P_INVERT_A",

        "P_LOCAL_A",
        "P_PLUS1_A",
        "P_PLUS2_A",
        "P_PLUS3_A",
        "P_OUTBIT_A",
        "P_MINUS3_A",
        "P_MINUS2_A",
        "P_MINUS1_A",
        "P_TRUE_B",
        "P_INVERT_B",
        "P_LOCAL_B",
        "P_PLUS1_B",
        "P_PLUS2_B",
        "P_PLUS3_B",
        "P_OUTBIT_B",
        "P_MINUS3_B",
        "P_MINUS2_B",
        "P_MINUS1_B",
        "P_PASS_AB",
        "P_AND_AB",
        "P_OR_AB",
        "P_XOR_AB",
        "P_FILT0_AB",
        "P_FILT1_AB",
        "P_FILT2_AB",
        "P_FILT3_AB",
        "P_LOGIC_A",
        "P_LOGIC_A_FB",
        "P_LOGIC_B_FB",
        "P_SCHMITT_A",
        "P_SCHMITT_A_FB",
        "P_SCHMITT_B_FB",
        "P_COMPARE_AB",
        "P_COMPARE_AB_FB",
        "P_ADC_GIO",
        "P_ADC_VIO",
        "P_ADC_FLOAT",
        "P_ADC_1X",
        "P_ADC_3X",
        "P_ADC_10x",
        "P_ADC_30x",
        "P_ADC_100x",
        "P_DAC_990R_3V",
        "P_DAC_600R_2V",
        "P_DAC_124R_3V",
        "P_DAC_75R_2V",
        "P_CHANNEL",
        "P_LEVEL_A",
        "P_LEVEL_A_FBN",
        "P_LEVEL_B_FBP",
        "P_LEVEL_B_FBN",
        "P_ASYNC_IO",
        "P_SYNC_IO",
        "P_TRUE_IN",
        "P_INVERT_IN",
        "P_TRUE_OUTPUT",
        "P_TRUE_OUT",
        "P_INVERT_OUTPUT",
        "P_INVERT_OUT",
        "P_HIGH_FAST",
        "P_HIGH_1K5",
        "P_HIGH_15K",
        "P_HIGH_150K",
        "P_HIGH_1MA",
        "P_HIGH_100UA",
        "P_HIGH_10UA",
        "P_HIGH_FLOAT",
        "P_LOW_FAST",
        "P_LOW_1K5",
        "P_LOW_15K",
        "P_LOW_150K",
        "P_LOW_1MA",
        "P_LOW_100UA",
        "P_LOW_10UA",
        "P_LOW_FLOAT",
        "P_TT_00",
        "P_TT_01",
        "P_TT_10",
        "P_TT_11",
        "P_OE",
        "P_BITDAC",
        "P_NORMAL",
        "P_REPOSITORY",
        "P_DAC_NOISE",
        "P_DAC_DITHER_RND",
        "P_DAC_DITHER_PWM",
        "P_PULSE",
        "P_TRANSITION",
        "P_NCO_FREQ",
        "P_NCO_DUTY",
        "P_PWM_TRIANGLE",
        "P_PWM_SAWTOOTH",
        "P_PWM_SMPS",
        "P_QUADRATURE",
        "P_REG_UP",
        "P_REG_UP_DOWN",
        "P_COUNT_RISES",
        "P_COUNT_HIGHS",

        "P_STATE_TICKS",
        "P_HIGH_TICKS",
        "P_EVENTS_TICKS",
        "P_PERIODS_TICKS",
        "P_PERIODS_HIGHS",
        "P_COUNTER_TICKS",
        "P_COUNTER_HIGHS",
        "P_COUNTER_PERIODS",

        "P_ADC",
        "P_ADC_EXT",
        "P_ADC_SCOPE",
        "P_USB_PAIR",
        "P_SYNC_TX",
        "P_SYNC_RX",
        "P_ASYNC_TX",
        "P_ASYNC_RX",

        "X_IMM_32X1_LUT",
        "X_IMM_16X2_LUT",
        "X_IMM_8X4_LUT",
        "X_IMM_4X8_LUT",

        "X_IMM_32X1_1DAC1",
        "X_IMM_16X2_2DAC1",
        "X_IMM_16X2_1DAC2",
        "X_IMM_8X4_4DAC1",
        "X_IMM_8X4_2DAC2",
        "X_IMM_8X4_1DAC4",

        "X_IMM_4X8_4DAC2",
        "X_IMM_4X8_2DAC4",
        "X_IMM_4X8_1DAC8",
        "X_IMM_2X16_4DAC4",

        "X_IMM_2X16_2DAC8",
        "X_IMM_1X32_4DAC8",

        "X_RFLONG_32X1_LUT",
        "X_RFLONG_16X2_LUT",
        "X_RFLONG_8X4_LUT",
        "X_RFLONG_4X8_LUT",

        "X_RFBYTE_1P_1DAC1",
        "X_RFBYTE_2P_2DAC1",
        "X_RFBYTE_2P_1DAC2",
        "X_RFBYTE_4P_4DAC1",
        "X_RFBYTE_4P_2DAC2",
        "X_RFBYTE_4P_1DAC4",
        "X_RFBYTE_8P_4DAC2",
        "X_RFBYTE_8P_2DAC4",
        "X_RFBYTE_8P_1DAC8",
        "X_RFWORD_16P_4DAC4",
        "X_RFWORD_16P_2DAC8",
        "X_RFLONG_32P_4DAC8",

        "X_RFBYTE_LUMA8",
        "X_RFBYTE_RGBI8",
        "X_RFBYTE_RGB8",
        "X_RFWORD_RGB16",
        "X_RFLONG_RGB24",

        "X_1P_1DAC1_WFBYTE",
        "X_2P_2DAC1_WFBYTE",
        "X_2P_1DAC2_WFBYTE",

        "X_4P_4DAC1_WFBYTE",
        "X_4P_2DAC2_WFBYTE",
        "X_4P_1DAC4_WFBYTE",

        "X_8P_4DAC2_WFBYTE",
        "X_8P_2DAC4_WFBYTE",
        "X_8P_1DAC8_WFBYTE",

        "X_16P_4DAC4_WFWORD",
        "X_16P_2DAC8_WFWORD",
        "X_32P_4DAC8_WFLONG",

        "X_1ADC8_0P_1DAC8_WFBYTE",
        "X_1ADC8_8P_2DAC8_WFWORD",
        "X_2ADC8_0P_2DAC8_WFWORD",
        "X_2ADC8_16P_4DAC8_WFLONG",
        "X_4ADC8_0P_4DAC8_WFLONG",

        "X_DDS_GOERTZEL_SINC1",
        "X_DDS_GOERTZEL_SINC2",

        "X_DACS_OFF",
        "X_DACS_0_0_0_0",
        "X_DACS_X_X_0_0",
        "X_DACS_0_0_X_X",
        "X_DACS_X_X_X_0",
        "X_DACS_X_X_0_X",
        "X_DACS_X_0_X_X",
        "X_DACS_0_X_X_X",
        "X_DACS_0N0_0N0",
        "X_DACS_X_X_0N0",
        "X_DACS_0N0_X_X",
        "X_DACS_1_0_1_0",
        "X_DACS_X_X_1_0",
        "X_DACS_1_0_X_X",
        "X_DACS_1N1_0N0",
        "X_DACS_3_2_1_0",

        "X_PINS_OFF",
        "X_PINS_ON",
        "X_WRITE_OFF",
        "X_WRITE_ON",
        "X_ALT_OFF",
        "X_ALT_ON",

        "COGEXEC",
        "COGEXEC_NEW",
        "HUBEXEC",
        "HUBEXEC_NEW",
        "COGEXEC_NEW_PAIR",
        "HUBEXEC_NEW_PAIR",

        "NEWCOG",

        "EVENT_INT",
        "INT_OFF",
        "EVENT_CT1",
        "EVENT_CT2",
        "EVENT_CT3",
        "EVENT_SE1",
        "EVENT_SE2",
        "EVENT_SE3",
        "EVENT_SE4",

        "EVENT_PAT",
        "EVENT_FBW",
        "EVENT_XMT",
        "EVENT_XFI",
        "EVENT_XRO",
        "EVENT_XRL",
        "EVENT_ATN",
        "EVENT_QMT",

        "NEWTASK",
        "THISTASK",

        "FALSE",
        "TRUE",
        "NEGX",
        "POSX",
        "PI"
    ));

    public Spin2Formatter() {

    }

    @Override
    public String format(String text) {
        return format(new Spin2TokenStream(text));
    }

    @Override
    protected boolean isBlockStart(Token token) {
        return Spin2Model.isBlockStart(token.getText());
    }

    @Override
    protected boolean pasmCondition(Token token) {
        return Spin2Model.isPAsmCondition(token.getText());
    }

    @Override
    protected boolean pasmInstruction(Token token) {
        if (Spin2Model.isPAsmInstruction(token.getText())) {
            return true;
        }
        if ("debug".equalsIgnoreCase(token.getText())) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean pasmModifier(Token token) {
        return Spin2Model.isPAsmModifier(token.getText());
    }

    @Override
    protected boolean isBuiltInConstant(Token token) {
        return builtInConstants.contains(token.getText().toUpperCase());
    }

    @Override
    protected Token nextPAsmToken() {
        Token token = stream.nextToken();
        if ("@".equals(token.getText()) || "@@".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (".".equals(token.getText()) && token.isAdjacent(nextToken)) {
                token = token.merge(stream.nextToken());
                nextToken = stream.peekNext();
            }
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken());
            }
        }
        else if (".".equals(token.getText())) {
            Token nextToken = stream.peekNext();
            if (token.isAdjacent(nextToken) && nextToken.type != Token.OPERATOR) {
                token = token.merge(stream.nextToken());
            }
        }
        return token;
    }

}
