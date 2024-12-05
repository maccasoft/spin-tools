/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller;

public class LoaderImage {

    public static final int RAW_LOADER_INIT_OFFSET_FROM_END = -(10 * 4) - 8;

    public static final int BootClkSel = 0x00158;
    static final int ExpectedID = 0x0017C;

    public static final byte[] rawLoaderImage = {
        /* 0000 */ (byte) 0x00,(byte) 0xB4,(byte) 0xC4,(byte) 0x04,(byte) 0x6F,(byte) 0x93,(byte) 0x10,(byte) 0x00,(byte) 0x88,(byte) 0x01,(byte) 0x90,(byte) 0x01,(byte) 0x80,(byte) 0x01,(byte) 0x94,(byte) 0x01,
        /* 0010 */ (byte) 0x78,(byte) 0x01,(byte) 0x02,(byte) 0x00,(byte) 0x70,(byte) 0x01,(byte) 0x00,(byte) 0x00,(byte) 0x4D,(byte) 0xE8,(byte) 0xBF,(byte) 0xA0,(byte) 0x4D,(byte) 0xEC,(byte) 0xBF,(byte) 0xA0,
        /* 0020 */ (byte) 0x51,(byte) 0xB8,(byte) 0xBC,(byte) 0xA1,(byte) 0x01,(byte) 0xB8,(byte) 0xFC,(byte) 0x28,(byte) 0xF1,(byte) 0xB9,(byte) 0xBC,(byte) 0x80,(byte) 0xA0,(byte) 0xB6,(byte) 0xCC,(byte) 0xA0,
        /* 0030 */ (byte) 0x51,(byte) 0xB8,(byte) 0xBC,(byte) 0xF8,(byte) 0xF2,(byte) 0x99,(byte) 0x3C,(byte) 0x61,(byte) 0x05,(byte) 0xB6,(byte) 0xFC,(byte) 0xE4,(byte) 0x59,(byte) 0x24,(byte) 0xFC,(byte) 0x54,
        /* 0040 */ (byte) 0x62,(byte) 0xB4,(byte) 0xBC,(byte) 0xA0,(byte) 0x02,(byte) 0xBC,(byte) 0xFC,(byte) 0xA0,(byte) 0x51,(byte) 0xB8,(byte) 0xBC,(byte) 0xA0,(byte) 0xF1,(byte) 0xB9,(byte) 0xBC,(byte) 0x80,
        /* 0050 */ (byte) 0x04,(byte) 0xBE,(byte) 0xFC,(byte) 0xA0,(byte) 0x08,(byte) 0xC0,(byte) 0xFC,(byte) 0xA0,(byte) 0x51,(byte) 0xB8,(byte) 0xBC,(byte) 0xF8,(byte) 0x4D,(byte) 0xE8,(byte) 0xBF,(byte) 0x64,
        /* 0060 */ (byte) 0x01,(byte) 0xB2,(byte) 0xFC,(byte) 0x21,(byte) 0x51,(byte) 0xB8,(byte) 0xBC,(byte) 0xF8,(byte) 0x4D,(byte) 0xE8,(byte) 0xBF,(byte) 0x70,(byte) 0x12,(byte) 0xC0,(byte) 0xFC,(byte) 0xE4,
        /* 0070 */ (byte) 0x51,(byte) 0xB8,(byte) 0xBC,(byte) 0xF8,(byte) 0x4D,(byte) 0xE8,(byte) 0xBF,(byte) 0x68,(byte) 0x0F,(byte) 0xBE,(byte) 0xFC,(byte) 0xE4,(byte) 0x48,(byte) 0x24,(byte) 0xBC,(byte) 0x80,
        /* 0080 */ (byte) 0x0E,(byte) 0xBC,(byte) 0xFC,(byte) 0xE4,(byte) 0x52,(byte) 0xA2,(byte) 0xBC,(byte) 0xA0,(byte) 0x54,(byte) 0x44,(byte) 0xFC,(byte) 0x50,(byte) 0x61,(byte) 0xB4,(byte) 0xFC,(byte) 0xA0,
        /* 0090 */ (byte) 0x5A,(byte) 0x5E,(byte) 0xBC,(byte) 0x54,(byte) 0x5A,(byte) 0x60,(byte) 0xBC,(byte) 0x54,(byte) 0x5A,(byte) 0x62,(byte) 0xBC,(byte) 0x54,(byte) 0x04,(byte) 0xBE,(byte) 0xFC,(byte) 0xA0,
        /* 00a0 */ (byte) 0x54,(byte) 0xB6,(byte) 0xBC,(byte) 0xA0,(byte) 0x53,(byte) 0xB8,(byte) 0xBC,(byte) 0xA1,(byte) 0x00,(byte) 0xBA,(byte) 0xFC,(byte) 0xA0,(byte) 0x80,(byte) 0xBA,(byte) 0xFC,(byte) 0x72,
        /* 00b0 */ (byte) 0xF2,(byte) 0x99,(byte) 0x3C,(byte) 0x61,(byte) 0x25,(byte) 0xB6,(byte) 0xF8,(byte) 0xE4,(byte) 0x36,(byte) 0x00,(byte) 0x78,(byte) 0x5C,(byte) 0xF1,(byte) 0xB9,(byte) 0xBC,(byte) 0x80,
        /* 00c0 */ (byte) 0x51,(byte) 0xB8,(byte) 0xBC,(byte) 0xF8,(byte) 0xF2,(byte) 0x99,(byte) 0x3C,(byte) 0x61,(byte) 0x00,(byte) 0xBB,(byte) 0xFC,(byte) 0x70,(byte) 0x01,(byte) 0xBA,(byte) 0xFC,(byte) 0x29,
        /* 00d0 */ (byte) 0x2A,(byte) 0x00,(byte) 0x4C,(byte) 0x5C,(byte) 0xFF,(byte) 0xC2,(byte) 0xFC,(byte) 0x64,(byte) 0x5D,(byte) 0xC2,(byte) 0xBC,(byte) 0x68,(byte) 0x08,(byte) 0xC2,(byte) 0xFC,(byte) 0x20,
        /* 00e0 */ (byte) 0x55,(byte) 0x44,(byte) 0xFC,(byte) 0x50,(byte) 0x22,(byte) 0xBE,(byte) 0xFC,(byte) 0xE4,(byte) 0x01,(byte) 0xB4,(byte) 0xFC,(byte) 0x80,(byte) 0x1E,(byte) 0x00,(byte) 0x7C,(byte) 0x5C,
        /* 00f0 */ (byte) 0x22,(byte) 0xB6,(byte) 0xBC,(byte) 0xA0,(byte) 0xFF,(byte) 0xB7,(byte) 0xFC,(byte) 0x60,(byte) 0x54,(byte) 0xB6,(byte) 0x7C,(byte) 0x86,(byte) 0x00,(byte) 0x8E,(byte) 0x68,(byte) 0x0C,
        /* 0100 */ (byte) 0x59,(byte) 0xC2,(byte) 0x3C,(byte) 0xC2,(byte) 0x09,(byte) 0x00,(byte) 0x54,(byte) 0x5C,(byte) 0x01,(byte) 0xB2,(byte) 0xFC,(byte) 0xC1,(byte) 0x63,(byte) 0x00,(byte) 0x70,(byte) 0x5C,
        /* 0110 */ (byte) 0x63,(byte) 0xB4,(byte) 0xFC,(byte) 0x84,(byte) 0x45,(byte) 0xC6,(byte) 0x3C,(byte) 0x08,(byte) 0x04,(byte) 0x8A,(byte) 0xFC,(byte) 0x80,(byte) 0x48,(byte) 0x7E,(byte) 0xBC,(byte) 0x80,
        /* 0120 */ (byte) 0x3F,(byte) 0xB4,(byte) 0xFC,(byte) 0xE4,(byte) 0x63,(byte) 0x7E,(byte) 0xFC,(byte) 0x54,(byte) 0x09,(byte) 0x00,(byte) 0x7C,(byte) 0x5C,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
        /* 0130 */ (byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x80,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x02,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x80,(byte) 0x00,(byte) 0x00,
        /* 0140 */ (byte) 0xFF,(byte) 0xFF,(byte) 0xF9,(byte) 0xFF,(byte) 0x10,(byte) 0xC0,(byte) 0x07,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x80,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x40,
        /* 0150 */ (byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x20,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x10,(byte) 0x07,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0xB6,(byte) 0x02,(byte) 0x00,(byte) 0x00,
        /* 0160 */ (byte) 0x56,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x82,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x55,(byte) 0x73,(byte) 0xCB,(byte) 0x00,(byte) 0x18,(byte) 0x51,(byte) 0x00,(byte) 0x00,
        /* 0170 */ (byte) 0x30,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x30,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x68,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
        /* 0350 */ (byte) 0x35,(byte) 0xC7,(byte) 0x08,(byte) 0x35,(byte) 0x2C,(byte) 0x32,(byte) 0x00,(byte) 0x00
    };

    public static final byte[] verifyRAM = {
        /* 0184 */ (byte) 0x49, (byte) 0xBC, (byte) 0xBC, (byte) 0xA0, (byte) 0x45, (byte) 0xBC, (byte) 0xBC, (byte) 0x84, (byte) 0x02, (byte) 0xBC, (byte) 0xFC, (byte) 0x2A, (byte) 0x45, (byte) 0x8C, (byte) 0x14, (byte) 0x08,
        /* 0194 */ (byte) 0x04, (byte) 0x8A, (byte) 0xD4, (byte) 0x80, (byte) 0x66, (byte) 0xBC, (byte) 0xD4, (byte) 0xE4, (byte) 0x0A, (byte) 0xBC, (byte) 0xFC, (byte) 0x04, (byte) 0x04, (byte) 0xBC, (byte) 0xFC, (byte) 0x84,
        /* 01a4 */ (byte) 0x5E, (byte) 0x94, (byte) 0x3C, (byte) 0x08, (byte) 0x04, (byte) 0xBC, (byte) 0xFC, (byte) 0x84, (byte) 0x5E, (byte) 0x94, (byte) 0x3C, (byte) 0x08, (byte) 0x01, (byte) 0x8A, (byte) 0xFC, (byte) 0x84,
        /* 01b4 */ (byte) 0x45, (byte) 0xBE, (byte) 0xBC, (byte) 0x00, (byte) 0x5F, (byte) 0x8C, (byte) 0xBC, (byte) 0x80, (byte) 0x6E, (byte) 0x8A, (byte) 0x7C, (byte) 0xE8, (byte) 0x46, (byte) 0xB2, (byte) 0xBC, (byte) 0xA4,
        /* 01c4 */ (byte) 0x09, (byte) 0x00, (byte) 0x7C, (byte) 0x5C
    };

    public static final byte[] programVerifyEEPROM = {
        /* 01cc */ (byte) 0x03, (byte) 0x8C, (byte) 0xFC, (byte) 0x2C, (byte) 0x4F, (byte) 0xEC, (byte) 0xBF, (byte) 0x68, (byte) 0x82, (byte) 0x18, (byte) 0xFD, (byte) 0x5C, (byte) 0x40, (byte) 0xBE, (byte) 0xFC, (byte) 0xA0,
        /* 01dc */ (byte) 0x45, (byte) 0xBA, (byte) 0xBC, (byte) 0x00, (byte) 0xA0, (byte) 0x62, (byte) 0xFD, (byte) 0x5C, (byte) 0x79, (byte) 0x00, (byte) 0x70, (byte) 0x5C, (byte) 0x01, (byte) 0x8A, (byte) 0xFC, (byte) 0x80,
        /* 01ec */ (byte) 0x67, (byte) 0xBE, (byte) 0xFC, (byte) 0xE4, (byte) 0x8F, (byte) 0x3E, (byte) 0xFD, (byte) 0x5C, (byte) 0x49, (byte) 0x8A, (byte) 0x3C, (byte) 0x86, (byte) 0x65, (byte) 0x00, (byte) 0x54, (byte) 0x5C,
        /* 01fc */ (byte) 0x00, (byte) 0x8A, (byte) 0xFC, (byte) 0xA0, (byte) 0x49, (byte) 0xBE, (byte) 0xBC, (byte) 0xA0, (byte) 0x7D, (byte) 0x02, (byte) 0xFD, (byte) 0x5C, (byte) 0xA3, (byte) 0x62, (byte) 0xFD, (byte) 0x5C,
        /* 020c */ (byte) 0x45, (byte) 0xC0, (byte) 0xBC, (byte) 0x00, (byte) 0x5D, (byte) 0xC0, (byte) 0x3C, (byte) 0x86, (byte) 0x79, (byte) 0x00, (byte) 0x54, (byte) 0x5C, (byte) 0x01, (byte) 0x8A, (byte) 0xFC, (byte) 0x80,
        /* 021c */ (byte) 0x72, (byte) 0xBE, (byte) 0xFC, (byte) 0xE4, (byte) 0x01, (byte) 0x8C, (byte) 0xFC, (byte) 0x28, (byte) 0x8F, (byte) 0x3E, (byte) 0xFD, (byte) 0x5C, (byte) 0x01, (byte) 0x8C, (byte) 0xFC, (byte) 0x28,
        /* 022c */ (byte) 0x46, (byte) 0xB2, (byte) 0xBC, (byte) 0xA4, (byte) 0x09, (byte) 0x00, (byte) 0x7C, (byte) 0x5C, (byte) 0x82, (byte) 0x18, (byte) 0xFD, (byte) 0x5C, (byte) 0xA1, (byte) 0xBA, (byte) 0xFC, (byte) 0xA0,
        /* 023c */ (byte) 0x8D, (byte) 0x62, (byte) 0xFD, (byte) 0x5C, (byte) 0x79, (byte) 0x00, (byte) 0x70, (byte) 0x5C, (byte) 0x00, (byte) 0x00, (byte) 0x7C, (byte) 0x5C, (byte) 0xFF, (byte) 0xBD, (byte) 0xFC, (byte) 0xA0,
        /* 024c */ (byte) 0xA0, (byte) 0xBA, (byte) 0xFC, (byte) 0xA0, (byte) 0x8D, (byte) 0x62, (byte) 0xFD, (byte) 0x5C, (byte) 0x83, (byte) 0xBC, (byte) 0xF0, (byte) 0xE4, (byte) 0x45, (byte) 0xBA, (byte) 0x8C, (byte) 0xA0,
        /* 025c */ (byte) 0x08, (byte) 0xBA, (byte) 0xCC, (byte) 0x28, (byte) 0xA0, (byte) 0x62, (byte) 0xCD, (byte) 0x5C, (byte) 0x45, (byte) 0xBA, (byte) 0x8C, (byte) 0xA0, (byte) 0xA0, (byte) 0x62, (byte) 0xCD, (byte) 0x5C,
        /* 026c */ (byte) 0x79, (byte) 0x00, (byte) 0x70, (byte) 0x5C, (byte) 0x00, (byte) 0x00, (byte) 0x7C, (byte) 0x5C, (byte) 0x47, (byte) 0x8E, (byte) 0x3C, (byte) 0x62, (byte) 0x90, (byte) 0x00, (byte) 0x7C, (byte) 0x5C,
        /* 027c */ (byte) 0x47, (byte) 0x8E, (byte) 0x3C, (byte) 0x66, (byte) 0x09, (byte) 0xC0, (byte) 0xFC, (byte) 0xA0, (byte) 0x58, (byte) 0xB8, (byte) 0xBC, (byte) 0xA0, (byte) 0xF1, (byte) 0xB9, (byte) 0xBC, (byte) 0x80,
        /* 028c */ (byte) 0x4F, (byte) 0xE8, (byte) 0xBF, (byte) 0x64, (byte) 0x4E, (byte) 0xEC, (byte) 0xBF, (byte) 0x78, (byte) 0x56, (byte) 0xB8, (byte) 0xBC, (byte) 0xF8, (byte) 0x4F, (byte) 0xE8, (byte) 0xBF, (byte) 0x68,
        /* 029c */ (byte) 0xF2, (byte) 0x9D, (byte) 0x3C, (byte) 0x61, (byte) 0x56, (byte) 0xB8, (byte) 0xBC, (byte) 0xF8, (byte) 0x4E, (byte) 0xEC, (byte) 0xBB, (byte) 0x7C, (byte) 0x00, (byte) 0xB8, (byte) 0xF8, (byte) 0xF8,
        /* 02ac */ (byte) 0xF2, (byte) 0x9D, (byte) 0x28, (byte) 0x61, (byte) 0x91, (byte) 0xC0, (byte) 0xCC, (byte) 0xE4, (byte) 0x79, (byte) 0x00, (byte) 0x44, (byte) 0x5C, (byte) 0x7B, (byte) 0x00, (byte) 0x48, (byte) 0x5C,
        /* 02bc */ (byte) 0x00, (byte) 0x00, (byte) 0x68, (byte) 0x5C, (byte) 0x01, (byte) 0xBA, (byte) 0xFC, (byte) 0x2C, (byte) 0x01, (byte) 0xBA, (byte) 0xFC, (byte) 0x68, (byte) 0xA4, (byte) 0x00, (byte) 0x7C, (byte) 0x5C,
        /* 02cc */ (byte) 0xFE, (byte) 0xBB, (byte) 0xFC, (byte) 0xA0, (byte) 0x09, (byte) 0xC0, (byte) 0xFC, (byte) 0xA0, (byte) 0x58, (byte) 0xB8, (byte) 0xBC, (byte) 0xA0, (byte) 0xF1, (byte) 0xB9, (byte) 0xBC, (byte) 0x80,
        /* 02dc */ (byte) 0x4F, (byte) 0xE8, (byte) 0xBF, (byte) 0x64, (byte) 0x00, (byte) 0xBB, (byte) 0x7C, (byte) 0x62, (byte) 0x01, (byte) 0xBA, (byte) 0xFC, (byte) 0x34, (byte) 0x4E, (byte) 0xEC, (byte) 0xBF, (byte) 0x78,
        /* 02ec */ (byte) 0x57, (byte) 0xB8, (byte) 0xBC, (byte) 0xF8, (byte) 0x4F, (byte) 0xE8, (byte) 0xBF, (byte) 0x68, (byte) 0xF2, (byte) 0x9D, (byte) 0x3C, (byte) 0x61, (byte) 0x58, (byte) 0xB8, (byte) 0xBC, (byte) 0xF8,
        /* 02fc */ (byte) 0xA7, (byte) 0xC0, (byte) 0xFC, (byte) 0xE4, (byte) 0xFF, (byte) 0xBA, (byte) 0xFC, (byte) 0x60, (byte) 0x00, (byte) 0x00, (byte) 0x7C, (byte) 0x5C
    };

    public static final byte[] readyToLaunch = {
        /* 030c */ (byte) 0xB8, (byte) 0x72, (byte) 0xFC, (byte) 0x58, (byte) 0x66, (byte) 0x72, (byte) 0xFC, (byte) 0x50, (byte) 0x09, (byte) 0x00, (byte) 0x7C, (byte) 0x5C, (byte) 0x06, (byte) 0xBE, (byte) 0xFC, (byte) 0x04,
        /* 031c */ (byte) 0x10, (byte) 0xBE, (byte) 0x7C, (byte) 0x86, (byte) 0x00, (byte) 0x8E, (byte) 0x54, (byte) 0x0C, (byte) 0x04, (byte) 0xBE, (byte) 0xFC, (byte) 0x00, (byte) 0x78, (byte) 0xBE, (byte) 0xFC, (byte) 0x60,
        /* 032c */ (byte) 0x50, (byte) 0xBE, (byte) 0xBC, (byte) 0x68, (byte) 0x00, (byte) 0xBE, (byte) 0x7C, (byte) 0x0C, (byte) 0x40, (byte) 0xAE, (byte) 0xFC, (byte) 0x2C, (byte) 0x6E, (byte) 0xAE, (byte) 0xFC, (byte) 0xE4,
        /* 033c */ (byte) 0x04, (byte) 0xBE, (byte) 0xFC, (byte) 0x00, (byte) 0x00, (byte) 0xBE, (byte) 0x7C, (byte) 0x0C, (byte) 0x02, (byte) 0x96, (byte) 0x7C, (byte) 0x0C
    };

    public static final byte[] launchNow = {
        /* 034c */ 0x66, 0x00, 0x7C, 0x5C
    };

}
