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

    static final int _pbase_init = 0x0030;
    static final int _vbase_init = 0x0034;
    static final int _dbase_init = 0x0038;
    static final int _var_longs = 0x003C;

    static final int _clkmode_hub = 0x0040;
    static final int _clkfreq_hub = 0x0044;

    static final int _debugnop1 = 0x0E54;
    static final int _debugnop2 = 0x0E58;
    static final int _debugnop3 = 0x0E5C;

    int[] debugnop = new int[3];

    byte[] code = new byte[0];

    public Spin2Interpreter() {
        InputStream is = getClass().getResourceAsStream("Spin2_interpreter.binary");
        try {
            code = new byte[is.available()];
            is.read(code);
            writeLong(_var_longs, 0x00000100);
            debugnop[0] = readLong(_debugnop1);
            debugnop[1] = readLong(_debugnop2);
            debugnop[2] = readLong(_debugnop3);
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
        writeLong(_pbase_init, value);
    }

    public int getPBase() {
        return readLong(_pbase_init);
    }

    public void setVBase(int value) {
        writeLong(_vbase_init, value);
    }

    public int getVBase() {
        return readLong(_vbase_init);
    }

    public void setDBase(int value) {
        writeLong(_dbase_init, value);
    }

    public int getDBase() {
        return readLong(_dbase_init);
    }

    public void setClearLongs(int value) {
        writeLong(_var_longs, value);
    }

    public void setClkMode(int mode) {
        writeLong(_clkmode_hub, mode);
    }

    public void setClkFreq(int value) {
        writeLong(_clkfreq_hub, value);
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
        writeLong(_debugnop1, augd);

        int waitx = Spin2InstructionObject.e.setValue(0, 0b1111);
        waitx = Spin2InstructionObject.o.setValue(waitx, 0b1101011);
        waitx = Spin2InstructionObject.i.setBoolean(waitx, true);
        waitx = Spin2InstructionObject.d.setValue(waitx, delay);
        waitx = Spin2InstructionObject.s.setValue(waitx, 0b000011111);
        writeLong(_debugnop2, waitx);

        writeLong(_debugnop3, 0x00000000);
    }

    public void setDebugPins(int tx, int rx) {
        writeLong(_debugnop1, Spin2InstructionObject.d.setValue(debugnop[0], rx));
        writeLong(_debugnop2, Spin2InstructionObject.s.setValue(debugnop[1], rx));
        writeLong(_debugnop3, Spin2InstructionObject.d.setValue(debugnop[2], rx));
    }

}
