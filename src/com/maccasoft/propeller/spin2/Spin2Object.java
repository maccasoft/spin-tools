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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import com.maccasoft.propeller.SpinObject;

public class Spin2Object extends SpinObject {

    Spin2Interpreter interpreter;

    public Spin2Object() {

    }

    @Override
    public void setClkFreq(int clkfreq) {
        if (interpreter != null) {
            interpreter.setClkFreq(clkfreq);
        }
        super.setClkFreq(clkfreq);
    }

    @Override
    public void setClkMode(int _clkmode) {
        if (interpreter != null) {
            interpreter.setClkMode(_clkmode);
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

    @Override
    public void generateBinary(OutputStream os) throws IOException {
        if (interpreter != null) {
            os.write(interpreter.code);
        }
        super.generateBinary(os);
    }

    @Override
    public void generateListing(PrintStream ps) {
        int offset = interpreter != null ? interpreter.getPBase() : 0;
        generateListing(offset, ps);
    }

}
