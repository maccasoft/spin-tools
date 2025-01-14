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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.commons.lang3.BitField;

import com.maccasoft.propeller.SpinObject;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;

public class Spin2Object extends SpinObject {

    public static BitField cm_pll = new BitField(0b0000_000_1_000000_0000000000_0000_00_00);
    public static BitField cm_xi_div = new BitField(0b0000_000_0_111111_0000000000_0000_00_00);
    public static BitField cm_vco_mul = new BitField(0b0000_000_0_000000_1111111111_0000_00_00);
    public static BitField cm_vco_div = new BitField(0b0000_000_0_000000_0000000000_1111_00_00);
    public static BitField cm_cc = new BitField(0b0000_000_0_000000_0000000000_0000_11_00);
    public static BitField cm_ss = new BitField(0b0000_000_0_000000_0000000000_0000_00_11);

    public static class Spin2LinkDataObject extends LinkDataObject {

        public Spin2LinkDataObject(Object object, long varSize) {
            super(object, varSize);
        }

        @Override
        public void setOffset(long offset) {
            this.bytes = new byte[] {
                (byte) offset,
                (byte) (offset >> 8),
                (byte) (offset >> 16),
                (byte) (offset >> 24)
            };
            super.setOffset(offset);
        }

    }

    public Spin2Interpreter interpreter;
    public Spin2Debugger debugger;

    public Spin2Object debugData;
    public boolean clockSetter;

    public boolean compress;

    public int debugTxPin = 62;
    public int debugRxPin = 63;
    public int debugBaud = 2000000;
    public Integer debugCogs;
    public Integer debugBrkCond;
    public Integer debugDelay;

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

    public void setDebugBrkCond(int debugBrkCond) {
        this.debugBrkCond = debugBrkCond;
    }

    public void setDebugCogs(Integer debugCogs) {
        this.debugCogs = debugCogs;
    }

    public void setDebugDelay(int debugDelay) {
        this.debugDelay = debugDelay;
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

            debugger.setDelay(debugDelay != null ? debugDelay : (getClkFreq() / 10));

            debugger.setRxPin(debugRxPin);
            debugger.setTxPin(debugTxPin);
            debugger.setBaud(debugBaud);
            if (debugBrkCond != null) {
                debugger.setBrkCond(debugBrkCond);
            }
            if (debugCogs != null) {
                debugger.setCogs(debugCogs);
            }

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

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    @Override
    public byte[] getBinary() throws IOException {
        byte[] code = super.getBinary();
        if (compress) {
            code = compressBinary(code);
        }
        return code;
    }

    byte[] compressBinary(byte[] data) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        LZ4Factory factory = LZ4Factory.nativeInstance();
        LZ4Compressor compressor = factory.highCompressor(12); // same as p2crunch

        byte[] compressed = new byte[512 * 1024];
        int compressedLength = compressor.compress(data, 0, data.length, compressed, 0, compressed.length);

        InputStream is = Spin2Object.class.getResourceAsStream("lz4stub.binary");
        try {
            byte[] stub = new byte[is.available()];
            is.read(stub);

            os.write(stub, 0, stub.length - 4);
            os.write(compressedLength & 0xFF);
            os.write((compressedLength >> 8) & 0xFF);
            os.write((compressedLength >> 16) & 0xFF);
            os.write((compressedLength >> 24) & 0xFF);

            os.write(compressed, 0, compressedLength);

            while ((os.size() % 4) != 0) {
                os.write(0x00);
            }

        } finally {
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return os.toByteArray();
    }

    @Override
    public byte[] getEEPromBinary() throws IOException {
        InputStream is = getClass().getResourceAsStream("flash_bootloader.binary");
        byte[] loader = new byte[is.available()];
        is.read(loader);
        is.close();

        byte[] code = getBinary();

        int appLongs = code.length / 4;
        loader[0x80] = loader[0x84] = (byte) appLongs;
        loader[0x81] = loader[0x85] = (byte) (appLongs >> 8);
        loader[0x82] = loader[0x86] = (byte) (appLongs >> 16);
        loader[0x83] = loader[0x87] = (byte) (appLongs >> 24);

        int sum = 0;
        for (int n = 0; n < code.length; n += 4) {
            int data = code[n] & 0xFF;
            if ((n + 1) < code.length) {
                data |= (code[n + 1] << 8) & 0xFF00;
                if ((n + 2) < code.length) {
                    data |= (code[n + 2] << 16) & 0xFF0000;
                    if ((n + 3) < code.length) {
                        data |= (code[n + 3] << 24) & 0xFF000000;
                    }
                }
            }
            sum -= data;
        }
        loader[0x88] = (byte) sum;
        loader[0x89] = (byte) (sum >> 8);
        loader[0x8A] = (byte) (sum >> 16);
        loader[0x8B] = (byte) (sum >> 24);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(loader);
        os.write(code);
        byte[] binaryImage = os.toByteArray();

        int loaderSum = 0;
        for (int n = 0; n < binaryImage.length && n < 0x400; n += 4) {
            int data = binaryImage[n] & 0xFF;
            if ((n + 1) < binaryImage.length) {
                data |= (binaryImage[n + 1] << 8) & 0xFF00;
                if ((n + 2) < binaryImage.length) {
                    data |= (binaryImage[n + 2] << 16) & 0xFF0000;
                    if ((n + 3) < binaryImage.length) {
                        data |= (binaryImage[n + 3] << 24) & 0xFF000000;
                    }
                }
            }
            loaderSum -= data;
        }
        binaryImage[0x8C] = (byte) loaderSum;
        binaryImage[0x8D] = (byte) (loaderSum >> 8);
        binaryImage[0x8E] = (byte) (loaderSum >> 16);
        binaryImage[0x8F] = (byte) (loaderSum >> 24);

        return binaryImage;
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

    @Override
    public byte[] getRAM() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        generateBinary(os);

        int count = (512 * 1024) - os.size();
        if (count > 0) {
            os.write(new byte[count]);
        }

        return os.toByteArray();
    }

}
