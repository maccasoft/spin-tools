/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BitField;

import com.maccasoft.propeller.SpinObject;
import com.maccasoft.propeller.spin2.Spin2ObjectCompiler.ObjectInfo;

public class Spin2Object extends SpinObject {

    public static BitField cm_pll = new BitField(0b0000_000_1_000000_0000000000_0000_00_00);
    public static BitField cm_xi_div = new BitField(0b0000_000_0_111111_0000000000_0000_00_00);
    public static BitField cm_vco_mul = new BitField(0b0000_000_0_000000_1111111111_0000_00_00);
    public static BitField cm_vco_div = new BitField(0b0000_000_0_000000_0000000000_1111_00_00);
    public static BitField cm_cc = new BitField(0b0000_000_0_000000_0000000000_0000_11_00);
    public static BitField cm_ss = new BitField(0b0000_000_0_000000_0000000000_0000_00_11);

    public static class LinkDataObject extends DataObject {

        ObjectInfo object;
        long offset;
        long varOffset;

        public LinkDataObject(ObjectInfo object, long offset, long varOffset) {
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

    Spin2Interpreter interpreter;
    Spin2Debugger debugger;
    List<LinkDataObject> links = new ArrayList<LinkDataObject>();

    Spin2Object debugData;

    public Spin2Object() {

    }

    @Override
    public void setClkFreq(int clkfreq) {
        if (interpreter != null) {
            interpreter.setClkFreq(clkfreq);
        }
        if (debugger != null) {
            debugger.setClkFreq(clkfreq);
        }
        super.setClkFreq(clkfreq);
    }

    @Override
    public void setClkMode(int _clkmode) {
        if (interpreter != null) {
            interpreter.setClkMode(_clkmode);
        }
        if (debugger != null) {
            debugger.setClkMode1(_clkmode & ~3);
            debugger.setClkMode2(_clkmode);
        }
        super.setClkMode(_clkmode);
    }

    public Spin2Interpreter getInterpreter() {
        return interpreter;
    }

    public void setInterpreter(Spin2Interpreter interpreter) {
        this.interpreter = interpreter;
        this.interpreter.setClkFreq(getClkFreq());
        this.interpreter.setClkMode(getClkMode());
    }

    public Spin2Debugger getDebugger() {
        return debugger;
    }

    public void setDebugger(Spin2Debugger debugger) {
        this.debugger = debugger;
        this.debugger.setClkFreq(getClkFreq());
        this.debugger.setClkMode1(getClkMode() & ~3);
        this.debugger.setClkMode2(getClkMode());

        int appSize = getSize();
        if (this.interpreter != null) {
            appSize += this.interpreter.getSize();
        }
        this.debugger.setAppSize(appSize);

        this.debugger.setDelay(200);
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
            os.write(debugger.getCode());
        }
        if (debugData != null) {
            debugData.generateBinary(os);
        }
        if (interpreter != null) {
            os.write(interpreter.getCode());
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

}
