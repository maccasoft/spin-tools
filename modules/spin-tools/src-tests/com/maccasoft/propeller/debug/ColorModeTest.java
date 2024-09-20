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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ColorModeTest {

    @Test
    void testTranslateRGBI8() {
        Assertions.assertEquals("00000000", String.format("%08X", ColorMode.translateColor(0b000_00000, ColorMode.RGBI8, 0)));
        Assertions.assertEquals("00100800", String.format("%08X", ColorMode.translateColor(0b000_00010, ColorMode.RGBI8, 0)));
        Assertions.assertEquals("00211000", String.format("%08X", ColorMode.translateColor(0b000_00100, ColorMode.RGBI8, 0)));
        Assertions.assertEquals("00311800", String.format("%08X", ColorMode.translateColor(0b000_00110, ColorMode.RGBI8, 0)));
        Assertions.assertEquals("00422100", String.format("%08X", ColorMode.translateColor(0b000_01000, ColorMode.RGBI8, 0)));
        Assertions.assertEquals("00522900", String.format("%08X", ColorMode.translateColor(0b000_01010, ColorMode.RGBI8, 0)));
        Assertions.assertEquals("00633100", String.format("%08X", ColorMode.translateColor(0b000_01100, ColorMode.RGBI8, 0)));
        Assertions.assertEquals("00733900", String.format("%08X", ColorMode.translateColor(0b000_01110, ColorMode.RGBI8, 0)));
        Assertions.assertEquals("00844200", String.format("%08X", ColorMode.translateColor(0b000_10000, ColorMode.RGBI8, 0)));
        Assertions.assertEquals("00944A00", String.format("%08X", ColorMode.translateColor(0b000_10010, ColorMode.RGBI8, 0)));
        Assertions.assertEquals("00A55200", String.format("%08X", ColorMode.translateColor(0b000_10100, ColorMode.RGBI8, 0)));
        Assertions.assertEquals("00B55A00", String.format("%08X", ColorMode.translateColor(0b000_10110, ColorMode.RGBI8, 0)));
        Assertions.assertEquals("00C66300", String.format("%08X", ColorMode.translateColor(0b000_11000, ColorMode.RGBI8, 0)));
        Assertions.assertEquals("00D66B00", String.format("%08X", ColorMode.translateColor(0b000_11010, ColorMode.RGBI8, 0)));
        Assertions.assertEquals("00E77300", String.format("%08X", ColorMode.translateColor(0b000_11100, ColorMode.RGBI8, 0)));
        Assertions.assertEquals("00F77B00", String.format("%08X", ColorMode.translateColor(0b000_11110, ColorMode.RGBI8, 0)));
    }

    @Test
    void testTranslateHSV8() {
        Assertions.assertEquals("00000000", String.format("%08X", ColorMode.translateColor(0, ColorMode.HSV8, 0)));
        Assertions.assertEquals("00110000", String.format("%08X", ColorMode.translateColor(1, ColorMode.HSV8, 0)));
        Assertions.assertEquals("00220000", String.format("%08X", ColorMode.translateColor(2, ColorMode.HSV8, 0)));
        Assertions.assertEquals("00330000", String.format("%08X", ColorMode.translateColor(3, ColorMode.HSV8, 0)));
        Assertions.assertEquals("00440000", String.format("%08X", ColorMode.translateColor(4, ColorMode.HSV8, 0)));
        Assertions.assertEquals("00550000", String.format("%08X", ColorMode.translateColor(5, ColorMode.HSV8, 0)));
        Assertions.assertEquals("00660000", String.format("%08X", ColorMode.translateColor(6, ColorMode.HSV8, 0)));
        Assertions.assertEquals("00770000", String.format("%08X", ColorMode.translateColor(7, ColorMode.HSV8, 0)));
        Assertions.assertEquals("00880000", String.format("%08X", ColorMode.translateColor(8, ColorMode.HSV8, 0)));
        Assertions.assertEquals("00990000", String.format("%08X", ColorMode.translateColor(9, ColorMode.HSV8, 0)));
        Assertions.assertEquals("00AA0000", String.format("%08X", ColorMode.translateColor(10, ColorMode.HSV8, 0)));
        Assertions.assertEquals("00BB0000", String.format("%08X", ColorMode.translateColor(11, ColorMode.HSV8, 0)));
        Assertions.assertEquals("00CC0000", String.format("%08X", ColorMode.translateColor(12, ColorMode.HSV8, 0)));
        Assertions.assertEquals("00DD0000", String.format("%08X", ColorMode.translateColor(13, ColorMode.HSV8, 0)));
        Assertions.assertEquals("00EE0000", String.format("%08X", ColorMode.translateColor(14, ColorMode.HSV8, 0)));
        Assertions.assertEquals("00FF0000", String.format("%08X", ColorMode.translateColor(15, ColorMode.HSV8, 0)));
    }

}
