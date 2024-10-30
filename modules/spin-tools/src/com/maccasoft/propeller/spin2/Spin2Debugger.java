/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.InputStream;

import com.maccasoft.propeller.Propeller2Loader;

public class Spin2Debugger {

    byte[] code = new byte[0];

    public Spin2Debugger() {
        InputStream is = getClass().getResourceAsStream("Spin2_debugger.binary");
        try {
            code = new byte[is.available()];
            is.read(code);
            writeLong(0x0148, Propeller2Loader.UPLOAD_BAUD_RATE);
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
        writeLong(0x0140, txPin);
    }

    public void setRxPin(int rxPin) {
        writeLong(0x0144, rxPin);
    }

    public void setBaud(int baud) {
        writeLong(0x0148, baud);
    }

    public void setClkFreq(int freq) {
        writeLong(0xD4, freq);
    }

    public void setClkMode1(int mode) {
        writeLong(0xD8, mode);
    }

    public void setClkMode2(int mode) {
        writeLong(0xDC, mode);
    }

    public void setDelay(int delay) {
        writeLong(0xE0, delay);
    }

    public void setAppSize(int size) {
        writeLong(0xE4, size);
    }

    public void setHubset(int set) {
        writeLong(0xE8, set);
    }

    public void setBrkCond(int set) {
        writeLong(0x11C, set);
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
