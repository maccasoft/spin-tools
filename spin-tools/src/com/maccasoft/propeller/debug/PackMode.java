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

public class PackMode {
    public static PackMode NONE() {
        return new PackMode(0xFFFFFFFF, 1, 32, false, false);
    }

    public static final PackMode LONGS_1BIT(boolean alt, boolean signx) {
        return new PackMode(0b1, 32, 1, alt, signx);
    }

    public static final PackMode LONGS_2BIT(boolean alt, boolean signx) {
        return new PackMode(0b11, 16, 2, alt, signx);
    }

    public static final PackMode LONGS_4BIT(boolean alt, boolean signx) {
        return new PackMode(0b1111, 8, 4, alt, signx);
    }

    public static final PackMode LONGS_8BIT(boolean alt, boolean signx) {
        return new PackMode(0b11111111, 4, 8, alt, signx);
    }

    public static final PackMode LONGS_16BIT(boolean alt, boolean signx) {
        return new PackMode(0b1111111111111111, 2, 16, alt, signx);
    }

    public static final PackMode WORDS_1BIT(boolean alt, boolean signx) {
        return new PackMode(0b1, 16, 1, alt, signx);
    }

    public static final PackMode WORDS_2BIT(boolean alt, boolean signx) {
        return new PackMode(0b11, 8, 2, alt, signx);
    }

    public static final PackMode WORDS_4BIT(boolean alt, boolean signx) {
        return new PackMode(0b1111, 4, 4, alt, signx);
    }

    public static final PackMode WORDS_8BIT(boolean alt, boolean signx) {
        return new PackMode(0b11111111, 2, 8, alt, signx);
    }

    public static final PackMode BYTES_1BIT(boolean alt, boolean signx) {
        return new PackMode(0b1, 8, 1, alt, signx);
    }

    public static final PackMode BYTES_2BIT(boolean alt, boolean signx) {
        return new PackMode(0b11, 4, 2, alt, signx);
    }

    public static final PackMode BYTES_4BIT(boolean alt, boolean signx) {
        return new PackMode(0b1111, 2, 4, alt, signx);
    }

    final int mask;
    final int size;
    final int shift;

    final boolean alt;
    final boolean signx;

    int data;

    PackMode(int mask, int size, int shift, boolean alt, boolean signx) {
        this.mask = mask;
        this.size = size;
        this.shift = shift;
        this.alt = alt;
        this.signx = signx;
    }

    public void newPack(int data) {
        if (alt) {
            if (shift <= 1) {
                data = ((data >> 1) & 0x55555555) | ((data << 1) & 0xAAAAAAAA);
            }
            if (shift <= 2) {
                data = ((data >> 2) & 0x33333333) | ((data << 2) & 0xCCCCCCCC);
            }
            if (shift <= 4) {
                data = ((data >> 4) & 0x0F0F0F0F) | ((data << 4) & 0xF0F0F0F0);
            }
        }
        this.data = data;
    }

    public int unpack() {
        int rc = data & mask;
        if (signx && ((rc >> (shift - 1) & 1) == 1)) {
            rc = rc | (0xFFFFFFFF ^ mask);
        }
        data = (data >> shift) & (0xFFFFFFFF ^ (mask << (32 - shift)));
        return rc;
    }

    public int count() {
        return size;
    }

}
