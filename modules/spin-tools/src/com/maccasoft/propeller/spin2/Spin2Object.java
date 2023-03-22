/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.commons.lang3.BitField;

import com.maccasoft.propeller.Propeller2Loader;
import com.maccasoft.propeller.SpinObject;

public class Spin2Object extends SpinObject {

    public static BitField cm_pll = new BitField(0b0000_000_1_000000_0000000000_0000_00_00);
    public static BitField cm_xi_div = new BitField(0b0000_000_0_111111_0000000000_0000_00_00);
    public static BitField cm_vco_mul = new BitField(0b0000_000_0_000000_1111111111_0000_00_00);
    public static BitField cm_vco_div = new BitField(0b0000_000_0_000000_0000000000_1111_00_00);
    public static BitField cm_cc = new BitField(0b0000_000_0_000000_0000000000_0000_11_00);
    public static BitField cm_ss = new BitField(0b0000_000_0_000000_0000000000_0000_00_11);

    public static class LinkDataObject extends DataObject {

        public Object object;
        public long offset;
        public long varOffset;

        public LinkDataObject(Object object, long offset, long varOffset) {
            super(new byte[] {
                (byte) offset,
                (byte) (offset >> 8),
                (byte) (offset >> 16),
                (byte) (offset >> 24)
            });
            this.object = object;
            this.offset = offset;
            this.varOffset = varOffset;
        }

        public long getOffset() {
            return offset;
        }

        public void setOffset(long offset) {
            this.bytes = new byte[] {
                (byte) offset,
                (byte) (offset >> 8),
                (byte) (offset >> 16),
                (byte) (offset >> 24)
            };
            this.offset = offset;
        }

        public long getVarOffset() {
            return varOffset;
        }

        public Object getObject() {
            return object;
        }

    }

    public Spin2Interpreter interpreter;
    public Spin2Debugger debugger;

    public Spin2Object debugData;
    public boolean clockSetter;

    public int debugTxPin = 62;
    public int debugRxPin = 63;
    public int debugBaud = Propeller2Loader.UPLOAD_BAUD_RATE;

    public Spin2Object() {

    }

    public void setClockSetter(boolean clockSetter) {
        this.clockSetter = clockSetter;
    }

    public Spin2Interpreter getInterpreter() {
        return interpreter;
    }

    public void setInterpreter(Spin2Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public Spin2Debugger getDebugger() {
        return debugger;
    }

    public void setDebugger(Spin2Debugger debugger) {
        this.debugger = debugger;
    }

    public int getDebugTxPin() {
        return debugTxPin;
    }

    public void setDebugTxPin(int debugTxPin) {
        this.debugTxPin = debugTxPin;
    }

    public int getDebugRxPin() {
        return debugRxPin;
    }

    public void setDebugRxPin(int debugRxPin) {
        this.debugRxPin = debugRxPin;
    }

    public int getDebugBaud() {
        return debugBaud;
    }

    public void setDebugBaud(int debugBaud) {
        this.debugBaud = debugBaud;
    }

    public Spin2Object getDebugData() {
        return debugData;
    }

    public void setDebugData(Spin2Object debugData) {
        this.debugData = debugData;
    }

    @Override
    public void generateBinary(OutputStream os) throws IOException {

        if (debugger != null) {
            debugger.setClkFreq(getClkFreq());
            debugger.setClkMode1(getClkMode() & ~3);
            debugger.setClkMode2(getClkMode());

            int appSize = getSize();
            if (interpreter != null) {
                appSize += interpreter.getSize();
            }
            debugger.setAppSize(appSize);

            debugger.setDelay(getClkFreq() / 10);

            debugger.setRxPin(debugRxPin);
            debugger.setTxPin(debugTxPin);
            debugger.setBaud(debugBaud);

            os.write(debugger.getCode());
        }
        if (debugData != null) {
            debugData.generateBinary(os);
        }
        if (interpreter != null) {
            interpreter.setClkFreq(getClkFreq());
            interpreter.setClkMode(getClkMode());
            if (debugger == null) {
                interpreter.setDelay(getClkFreq() / 10);
            }
            else {
                interpreter.setDebugPins(debugTxPin, debugRxPin);
            }
            os.write(interpreter.getCode());
        }
        if (clockSetter && interpreter == null && debugger == null) {
            writeClockSetter(os);
        }
        super.generateBinary(os);
    }

    @Override
    public void generateListing(PrintStream ps) {
        int offset = 0;
        if (interpreter != null) {
            offset = interpreter.getPBase();
        }
        generateListing(offset, ps);
        if (debugData != null) {
            int size = debugger != null ? debugger.getSize() : 0;
            debugData.generateListing(size, ps);
        }
    }

    void writeClockSetter(OutputStream os) throws IOException {
        int clkMode = getClkMode();

        if (clkMode != 0) {
            InputStream is = getClass().getResourceAsStream("clock_setter.binary");
            try {
                byte[] code = new byte[is.available()];
                is.read(code);

                if (clkMode == 0b01) {
                    writeLong(code, 0x000, 0);
                    writeLong(code, 0x004, 0);
                    writeLong(code, 0x008, 0);
                }
                else {
                    writeLong(code, 0x028, 0);
                }
                writeLong(code, 0x034, clkMode & ~0b11);
                writeLong(code, 0x038, clkMode);

                int programSize = getSize();
                writeLong(code, 0x03C, (programSize + 2048) >> (9 + 2));

                os.write(code);

            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void writeLong(byte[] code, int index, int value) {
        code[index] = (byte) value;
        code[index + 1] = (byte) (value >> 8);
        code[index + 2] = (byte) (value >> 16);
        code[index + 3] = (byte) (value >> 24);
    }

}
