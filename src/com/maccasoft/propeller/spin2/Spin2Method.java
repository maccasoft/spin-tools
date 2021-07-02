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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BitField;

import com.maccasoft.propeller.expressions.LocalVariable;

public class Spin2Method {

    public static final BitField address_bit = new BitField(0b0_0000000_0000_11111111111111111111);
    public static final BitField returns_bit = new BitField(0b0_0000000_1111_00000000000000000000);
    public static final BitField parameters_bit = new BitField(0b0_1111111_0000_00000000000000000000);

    Spin2Context scope;

    String label;
    List<LocalVariable> parameters;
    List<LocalVariable> returns;
    List<LocalVariable> localVariables;

    List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

    public Spin2Method(Spin2Context scope, String label, List<LocalVariable> parameters, List<LocalVariable> returns, List<LocalVariable> localVariables) {
        this.scope = scope;
        this.label = label;
        this.parameters = parameters;
        this.returns = returns;
        this.localVariables = localVariables;
    }

    public Spin2Context getScope() {
        return scope;
    }

    public String getLabel() {
        return label;
    }

    public int resolve(int address) {
        scope.setAddress(address);
        return address;
    }

    public int getReturnsCount() {
        return returns.size();
    }

    public int getParametersCount() {
        return parameters.size();
    }

    public int getStackSize() {
        int count = 0;

        for (LocalVariable var : localVariables) {
            int size = 4;
            if ("WORD".equalsIgnoreCase(var.getType())) {
                size = 2;
            }
            else if ("BYTE".equalsIgnoreCase(var.getType())) {
                size = 1;
            }
            if (var.getSize() != null) {
                size = size * var.getSize().getNumber().intValue();
            }
            count += size;
        }

        return count;
    }

    public void addSource(Spin2Bytecode line) {
        source.add(line);
    }

    public List<Spin2Bytecode> getSource() {
        return source;
    }

    public void writeTo(Spin2Object obj) {
        obj.writeByte(getStackSize(), "(stack size)");
        for (Spin2Bytecode bc : getSource()) {
            bc.getContext().setAddress(obj.getSize());
            obj.writeBytes(bc.getBytes(), bc.toString());
        }
    }

}
