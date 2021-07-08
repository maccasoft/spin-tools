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

public class Spin2Interpreter {

    byte[] code = new byte[0];

    public Spin2Interpreter() {
        InputStream is = getClass().getResourceAsStream("Spin2_interpreter.binary");
        try {
            code = new byte[is.available()];
            is.read(code);
            writeLong(0x3C, 0x00000100);
            setPBase(code.length);
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

    public void setPBase(int value) {
        writeLong(0x30, value);
    }

    public int getPBase() {
        return readLong(0x30);
    }

    public void setVBase(int value) {
        writeLong(0x34, value);
    }

    public int getVBase() {
        return readLong(0x34);
    }

    public void setDBase(int value) {
        writeLong(0x38, value);
    }

    public void setClearLongs(int value) {
        writeLong(0x3C, value);
    }

    public int getDBase() {
        return readLong(0x38);
    }

    public void setClkMode(int mode) {
        writeLong(0x40, mode);
    }

    public void setClkFreq(int value) {
        writeLong(0x44, value);
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
