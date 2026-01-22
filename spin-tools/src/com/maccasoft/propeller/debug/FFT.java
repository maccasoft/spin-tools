/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.debug;

public class FFT {

    public static final int FFT_MAX = 1 << 11;
    public static final int DEFAULT = 512;

    int FFTexp;
    int FFTfirst;
    int FFTlast;

    long[] FFTsin = new long[FFT_MAX];
    long[] FFTcos = new long[FFT_MAX];
    long[] FFTwin = new long[FFT_MAX];
    long[] FFTreal = new long[FFT_MAX];
    long[] FFTimag = new long[FFT_MAX];

    public FFT(int FFTexp) {
        this.FFTexp = FFTexp;

        for (int i = 0; i < 1 << FFTexp; i++) {
            double Tf = (double) Rev32(i) / 0x100000000L * Math.PI;
            double Yf = Math.sin(Tf);
            double Xf = Math.cos(Tf);
            FFTsin[i] = Math.round(Yf * 0x1000);
            FFTcos[i] = Math.round(Xf * 0x1000);
            FFTwin[i] = Math.round((1.0 - Math.cos(((double) i / (1 << FFTexp)) * Math.PI * 2)) * 0x1000);
        }
    }

    public void performFFT(int FFTmag, int[] FFTsamp, int[] FFTpower) {
        int i1, i2, i3, i4, c1, c2, th, ptra, ptrb;
        long ax, ay, bx, by, rx, ry;

        // Load samples into (real,imag) with Hanning window applied
        for (i1 = 0; i1 < 1 << FFTexp; i1++) {
            FFTreal[i1] = FFTsamp[i1] * FFTwin[i1];
            FFTimag[i1] = 0;
        }

        // Perform FFT on (real,imag)
        i1 = 1 << (FFTexp - 1);
        i2 = 1;
        while (i1 != 0) {
            th = 0;
            i3 = 0;
            i4 = i1;
            c1 = i2;
            //System.out.println(String.format("i1=%d, i2=%d", i1, i2));
            while (c1 != 0) {
                ptra = i3;
                ptrb = ptra + i1;
                c2 = i4 - i3;
                //System.out.println(String.format("  ptra=%d, ptrb=%d, c2=%d", ptra, ptrb, c2));
                while (c2 != 0) {
                    ax = FFTreal[ptra];
                    ay = FFTimag[ptra];
                    bx = FFTreal[ptrb];
                    by = FFTimag[ptrb];
                    rx = (bx * FFTcos[th] - by * FFTsin[th]) / 0x1000;
                    ry = (bx * FFTsin[th] + by * FFTcos[th]) / 0x1000;
                    FFTreal[ptra] = ax + rx;
                    FFTimag[ptra] = ay + ry;
                    FFTreal[ptrb] = ax - rx;
                    FFTimag[ptrb] = ay - ry;
                    ptra = ptra + 1;
                    ptrb = ptrb + 1;
                    c2 = c2 - 1;
                }
                th = th + 1;
                i3 = i3 + (i1 << 1);
                i4 = i4 + (i1 << 1);
                c1 = c1 - 1;
            }
            i1 = i1 >> 1;
            i2 = i2 << 1;
        }

        // Convert (real,imag) to (power,angle)
        for (i1 = 0; i1 < 1 << (FFTexp - 1); i1++) {
            i2 = (int) (Rev32(i1) >> (32 - FFTexp));
            rx = FFTreal[i2];
            ry = FFTimag[i2];
            FFTpower[i1] = (int) (Math.round(Math.hypot(rx, ry) / ((0x800 << FFTexp) >> FFTmag)));
            //FFTangle[i1] = (int) (Math.round(Math.atan2(rx, ry) / (Math.PI * 2) * 0x100000000L) & 0xFFFFFFFFL);
            //System.out.println("i1=" + i1 + ", FFTpower[i1]=" + FFTpower[i1]);
        }
    }

    static long Rev32(int i) {
        return Integer.reverse(i) & 0xFFFFFFFFL;
    }

}
