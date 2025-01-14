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

import java.io.InputStream;

public class Spin2Debugger {

    static final int _clkfrq = 0x00D4;
    static final int _clkmode1 = 0x00D8;
    static final int _clkmode2 = 0x00DC;
    static final int _delay = 0x00E0;
    static final int _appsize = 0x00E4;
    static final int _hubset = 0x00E8;

    static final int _brk_cond = 0x011C;

    static final int _txpin = 0x0140;
    static final int _rxpin = 0x0144;
    static final int _baud = 0x0148;
    static final int _dlyms = 0x014C;

    byte[] code = new byte[0];

    public Spin2Debugger() {
        InputStream is = getClass().getResourceAsStream("Spin2_debugger.binary");
        try {
            code = new byte[is.available()];
            is.read(code);
            writeLong(_baud, 2000000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setTxPin(int txPin) {
        writeLong(_txpin, txPin);
    }

    public void setRxPin(int rxPin) {
        writeLong(_rxpin, rxPin);
    }

    public void setBaud(int baud) {
        writeLong(_baud, baud);
    }

    public void setClkFreq(int freq) {
        writeLong(_clkfrq, freq);
    }

    public void setClkMode1(int mode) {
        writeLong(_clkmode1, mode);
    }

    public void setClkMode2(int mode) {
        writeLong(_clkmode2, mode);
    }

    public void setDelay(int delay) {
        writeLong(_delay, delay);
    }

    public void setAppSize(int size) {
        writeLong(_appsize, size);
    }

    public void setHubset(int set) {
        writeLong(_hubset, set);
    }

    public void setBrkCond(int set) {
        writeLong(_brk_cond, set);
    }

    public void setCogs(int set) {
        code[0xE8] = (byte) set;
    }

    public int getSize() {
        return code.length;
    }

    void writeLong(int index, int value) {
        code[index] = (byte) value;
        code[index + 1] = (byte) (value >> 8);
        code[index + 2] = (byte) (value >> 16);
        code[index + 3] = (byte) (value >> 24);
    }

    int readLong(int index) {
        return (code[index] & 0xFF) | ((code[index + 1] & 0xFF) << 8) | ((code[index + 2] & 0xFF) << 16) | ((code[index + 3] & 0xFF) << 24);
    }

    public byte[] getCode() {
        return code;
    }

}
