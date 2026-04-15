/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.debug;

public enum ColorMode {

    LUT1,
    LUT2,
    LUT4,
    LUT8,
    LUMA8,
    LUMA8W,
    LUMA8X,
    HSV8,
    HSV8W,
    HSV8X,
    RGBI8,
    RGBI8W,
    RGBI8X,
    HSV16,
    HSV16W,
    HSV16X,
    RGB8,
    RGB16,
    RGB24;

    static final int[] polarColors = new int[] {
        0x00FF0000, 0x00FF0000, 0x00FF0000, 0x00FF0000, 0x00FF0000, 0x00FF0000, 0x00FF0000, 0x00FF0000,
        0x00FF0500, 0x00FF0B00, 0x00FF1100, 0x00FF1700, 0x00FF1D00, 0x00FF2300, 0x00FF2900, 0x00FF2F00,
        0x00FF3500, 0x00FF3B00, 0x00FF4100, 0x00FF4700, 0x00FF4C00, 0x00FF5200, 0x00FF5800, 0x00FF5E00,
        0x00FF6400, 0x00FF6A00, 0x00FF7000, 0x00FF7600, 0x00FF7C00, 0x00FF8200, 0x00FF8800, 0x00FF8E00,
        0x00FF9400, 0x00FF9A00, 0x00FFA000, 0x00FFA600, 0x00FFAC00, 0x00FFB200, 0x00FFB800, 0x00FFBE00,
        0x00FFC400, 0x00FFCA00, 0x00FFD000, 0x00FFD600, 0x00FFDC00, 0x00FFE200, 0x00FFE800, 0x00FFEE00,
        0x00FFF400, 0x00FFFA00, 0x00FEFF00, 0x00F8FF00, 0x00F2FF00, 0x00ECFF00, 0x00E6FF00, 0x00E0FF00,
        0x00DAFF00, 0x00D4FF00, 0x00CEFF00, 0x00C8FF00, 0x00C2FF00, 0x00BCFF00, 0x00B6FF00, 0x00B1FF00,
        0x00ABFF00, 0x00A5FF00, 0x009FFF00, 0x0099FF00, 0x0093FF00, 0x008DFF00, 0x0087FF00, 0x0081FF00,
        0x007BFF00, 0x0075FF00, 0x006FFF00, 0x0069FF00, 0x0063FF00, 0x005DFF00, 0x0057FF00, 0x0051FF00,
        0x004BFF00, 0x0045FF00, 0x003FFF00, 0x0039FF00, 0x0033FF00, 0x002DFF00, 0x0027FF00, 0x0021FF00,
        0x001BFF00, 0x0015FF00, 0x000FFF00, 0x0009FF00, 0x0003FF00, 0x0000FF03, 0x0000FF09, 0x0000FF0F,
        0x0000FF15, 0x0000FF1B, 0x0000FF21, 0x0000FF27, 0x0000FF2D, 0x0000FF33, 0x0000FF39, 0x0000FF3F,
        0x0000FF45, 0x0000FF4B, 0x0000FF50, 0x0000FF56, 0x0000FF5C, 0x0000FF62, 0x0000FF68, 0x0000FF6E,
        0x0000FF74, 0x0000FF7A, 0x0000FF80, 0x0000FF86, 0x0000FF8C, 0x0000FF92, 0x0000FF98, 0x0000FF9E,
        0x0000FFA4, 0x0000FFAA, 0x0000FFB0, 0x0000FFB6, 0x0000FFBC, 0x0000FFC2, 0x0000FFC8, 0x0000FFCE,
        0x0000FFD4, 0x0000FFDA, 0x0000FFE0, 0x0000FFE6, 0x0000FFEC, 0x0000FFF2, 0x0000FFF8, 0x0000FFFE,
        0x0000FAFF, 0x0000F4FF, 0x0000EEFF, 0x0000E8FF, 0x0000E2FF, 0x0000DCFF, 0x0000D6FF, 0x0000D0FF,
        0x0000CAFF, 0x0000C4FF, 0x0000BEFF, 0x0000B8FF, 0x0000B3FF, 0x0000ADFF, 0x0000A7FF, 0x0000A1FF,
        0x00009BFF, 0x000095FF, 0x00008FFF, 0x000089FF, 0x000083FF, 0x00007DFF, 0x000077FF, 0x000071FF,
        0x00006BFF, 0x000065FF, 0x00005FFF, 0x000059FF, 0x000053FF, 0x00004DFF, 0x000047FF, 0x000041FF,
        0x00003BFF, 0x000035FF, 0x00002FFF, 0x000029FF, 0x000023FF, 0x00001DFF, 0x000017FF, 0x000011FF,
        0x00000BFF, 0x000005FF, 0x000100FF, 0x000700FF, 0x000D00FF, 0x001300FF, 0x001900FF, 0x001F00FF,
        0x002500FF, 0x002B00FF, 0x003100FF, 0x003700FF, 0x003D00FF, 0x004300FF, 0x004900FF, 0x004E00FF,
        0x005400FF, 0x005A00FF, 0x006000FF, 0x006600FF, 0x006C00FF, 0x007200FF, 0x007800FF, 0x007E00FF,
        0x008400FF, 0x008A00FF, 0x009000FF, 0x009600FF, 0x009C00FF, 0x00A200FF, 0x00A800FF, 0x00AE00FF,
        0x00B400FF, 0x00BA00FF, 0x00C000FF, 0x00C600FF, 0x00CC00FF, 0x00D200FF, 0x00D800FF, 0x00DE00FF,
        0x00E400FF, 0x00EA00FF, 0x00F000FF, 0x00F600FF, 0x00FC00FF, 0x00FF00FC, 0x00FF00F6, 0x00FF00F0,
        0x00FF00EA, 0x00FF00E4, 0x00FF00DE, 0x00FF00D8, 0x00FF00D2, 0x00FF00CC, 0x00FF00C6, 0x00FF00C0,
        0x00FF00BA, 0x00FF00B4, 0x00FF00AF, 0x00FF00A9, 0x00FF00A3, 0x00FF009D, 0x00FF0097, 0x00FF0091,
        0x00FF008B, 0x00FF0085, 0x00FF007F, 0x00FF0079, 0x00FF0073, 0x00FF006D, 0x00FF0067, 0x00FF0061,
        0x00FF005B, 0x00FF0055, 0x00FF004F, 0x00FF0049, 0x00FF0043, 0x00FF003D, 0x00FF0037, 0x00FF0031
    };

    public static int translateColor(int p, ColorMode mode, int colorTune) {
        int v;
        boolean w;

        switch (mode) {
            case RGB8:
                return (((p & 0xE0) * 0x1236E) & 0xFF0000) |
                    (((p & 0x1C) * 0x91C) & 0x00FF00) |
                    (((p & 0x03) * 0x55) & 0x0000FF);
            case RGB16:
                return (((p & 0xF800) << 8) | ((p & 0xE000) << 3)) |
                    (((p & 0x07E0) << 5) | ((p & 0x0600) >> 1)) |
                    (((p & 0x001F) << 3) | ((p & 0x001C) >> 2));
            case RGB24:
                return p & 0xFFFFFF;

            case LUMA8:
            case LUMA8W:
            case LUMA8X:
            case RGBI8:
            case RGBI8W:
            case RGBI8X:
                if (mode == ColorMode.LUMA8 || mode == ColorMode.LUMA8W || mode == ColorMode.LUMA8X) {
                    v = colorTune & 7;
                    p &= 0xFF;
                }
                else {
                    v = (p >> 5) & 7;
                    p = ((p & 0x1F) << 3) | ((p & 0x1C) >> 2);
                }
                w = (mode == ColorMode.LUMA8W || mode == ColorMode.RGBI8W) || ((mode == ColorMode.LUMA8X || mode == ColorMode.RGBI8X) && v != 7 && p >= 0x80);
                if ((mode == ColorMode.LUMA8X || mode == ColorMode.RGBI8X) && v != 7) {
                    if (p >= 0x80) {
                        p = ((p ^ 0xFF) & 0x7F) << 1;
                    }
                    else {
                        p = p << 1;
                    }
                }
                if (w) { // from white to color
                    if (v == 0) {
                        return (((p << 7) & 0x007F00) | p) ^ 0xFFFFFF; // orange
                    }
                    else {
                        if (v != 7) {
                            v ^= 7;
                        }
                        return ((((v >> 2) & 1) * (p << 16)) |
                            (((v >> 1) & 1) * (p << 8)) |
                            (((v >> 0) & 1) * (p << 0))) ^ 0xFFFFFF;
                    }
                }
                else { // from black to color
                    if (v == 0) {
                        return ((p << 16) | (p << 7) & 0x007F00); // orange
                    }
                    else {
                        return (((v >> 2) & 1) * (p << 16)) |
                            (((v >> 1) & 1) * (p << 8)) |
                            (((v >> 0) & 1) * (p << 0));
                    }
                }

            case HSV8:
            case HSV8W:
            case HSV8X:
                p = ((p & 0xF0) * 0x110) | ((p & 0x0F) * 0x11);
            case HSV16:
            case HSV16W:
            case HSV16X:
                v = polarColors[(p >> 8 + colorTune) & 0xFF];
                p &= 0xFF;
                w = (mode == ColorMode.HSV8W || mode == ColorMode.HSV16W) || ((mode == ColorMode.HSV8X || mode == ColorMode.HSV16X) && p >= 0x80);
                if (mode == ColorMode.HSV8X || mode == ColorMode.HSV16X) {
                    if (p >= 0x80) {
                        p = ((p & 0x7F) << 1) ^ 0xFE;
                    }
                    else {
                        p = p << 1;
                    }
                }
                if (w) {
                    v ^= 0xFFFFFF;
                }
                p = (((((v >> 16) & 0xFF) * p + 0xFF) >> 8) << 16) |
                    (((((v >> 8) & 0xFF) * p + 0xFF) >> 8) << 8) |
                    (((((v >> 0) & 0xFF) * p + 0xFF) >> 8) << 0);
                return w ? (p ^ 0xFFFFFF) : p;

            default:
                return p & 0xFFFFFF;
        }
    }

    public int translateColor(int p, int colorTune) {
        return translateColor(p, this, colorTune);
    }

}
