/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.util.ArrayList;
import java.util.List;

public class Spin1Method {

    Spin1Context scope;

    String label;
    int localSize;

    List<Spin1BytecodeInstruction> source = new ArrayList<Spin1BytecodeInstruction>();

    public Spin1Method(Spin1Context scope, String label) {
        this.scope = scope;
        this.label = label;
    }

    public Spin1Context getScope() {
        return scope;
    }

    public String getLabel() {
        return label;
    }

    public void addBytecodeInstruction(Spin1BytecodeInstruction instruction) {
        source.add(instruction);
    }

    public List<Spin1BytecodeInstruction> getSource() {
        return source;
    }

    public int resolve(int address) {
        scope.setAddress(address);
        return address;
    }

    public void setLocalSize(int localSize) {
        this.localSize = localSize;
    }

    public int getLocalSize() {
        return localSize;
    }

    public int getSize() {
        int rc = 0;

        for (Spin1BytecodeInstruction instruction : source) {
            rc += instruction.getSize();
        }

        return rc;
    }

}
