/*
 * Copyright (c) 2021 Marco Maccaferri and others.
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

    byte[] code = new byte[0];

    public Spin2Debugger() {
        InputStream is = getClass().getResourceAsStream("Spin2_debugger.binary");
        try {
            code = new byte[is.available()];
            is.read(code);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        writeLong(0x108, 0x00500007);
        writeLong(0x10C, 0x000270FA);
    }

    public void setClkMode1(int mode) {
        writeLong(0xA0, mode);
    }

    public void setClkMode2(int mode) {
        writeLong(0xA4, mode);
    }

    public void setDelay(int delay) {
        writeLong(0xA8, delay);
    }

    public void setAppSize(int size) {
        writeLong(0xAC, size);
    }

    public void setHubset(int set) {
        writeLong(0xB0, set);
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
