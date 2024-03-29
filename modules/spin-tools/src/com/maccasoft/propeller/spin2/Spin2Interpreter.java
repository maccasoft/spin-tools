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

public class Spin2Interpreter {

    int debugnop0E54; // _debugnop1_     dirh    #63-63                  'write clkfreq to rx pin long repository'
    int debugnop0E58; // _debugnop2_     wxpin   z,#63-63
    int debugnop0E5C; // _debugnop3_     dirl    #63-63                  '(these 3 are NOP'd by compiler if not DEBUG, else fixed with debug_pin_rx)
    byte[] code = new byte[0];

    public Spin2Interpreter() {
        InputStream is = getClass().getResourceAsStream("Spin2_interpreter.binary");
        try {
            code = new byte[is.available()];
            is.read(code);
            writeLong(0x003C, 0x00000100);
            debugnop0E54 = readLong(0x0E54);
            debugnop0E58 = readLong(0x0E58);
            debugnop0E5C = readLong(0x0E5C);
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

    public int getSize() {
        return code.length;
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

    public void setDelay(int delay) {
        int augd = Spin2InstructionObject.e.setValue(0, 0b1111);
        augd = Spin2InstructionObject.o.setValue(augd, 0b1111100);
        augd = Spin2InstructionObject.x.setValue(augd, delay >> 9);
        writeLong(0x0E54, augd);

        int waitx = Spin2InstructionObject.e.setValue(0, 0b1111);
        waitx = Spin2InstructionObject.o.setValue(waitx, 0b1101011);
        waitx = Spin2InstructionObject.i.setBoolean(waitx, true);
        waitx = Spin2InstructionObject.d.setValue(waitx, delay);
        waitx = Spin2InstructionObject.s.setValue(waitx, 0b000011111);
        writeLong(0x0E58, waitx);

        writeLong(0x0E5C, 0x00000000);
    }

    public void setDebugPins(int tx, int rx) {
        writeLong(0x0E54, Spin2InstructionObject.d.setValue(debugnop0E54, rx));
        writeLong(0x0E58, Spin2InstructionObject.s.setValue(debugnop0E58, rx));
        writeLong(0x0E5C, Spin2InstructionObject.d.setValue(debugnop0E5C, rx));
    }

}
