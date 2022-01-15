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

import org.apache.commons.lang3.BitField;

import com.maccasoft.propeller.expressions.LocalVariable;

public class Spin1Method {

    public static final BitField address = new BitField(0b0000000000000000_1111111111111111);
    public static final BitField locals = new BitField(0b1111111111111111_0000000000000000);

    Spin1Context scope;

    String label;
    List<LocalVariable> parameters;
    List<LocalVariable> returns;
    List<LocalVariable> localVariables;

    List<Spin1MethodLine> lines = new ArrayList<Spin1MethodLine>();

    String comment;

    int startAddress;
    int endAddress;
    boolean addressChanged;

    public Spin1Method(Spin1Context scope, String label, List<LocalVariable> parameters, List<LocalVariable> returns, List<LocalVariable> localVariables) {
        this.scope = scope;
        this.label = label;
        this.parameters = parameters;
        this.returns = returns;
        this.localVariables = localVariables;
    }

    public Spin1Context getScope() {
        return scope;
    }

    public String getLabel() {
        return label;
    }

    public void register() {
        for (Spin1MethodLine line : lines) {
            line.register(scope);
        }
    }

    public int resolve(int address) {
        addressChanged = startAddress != address;
        startAddress = address;

        scope.setAddress(address);
        for (Spin1MethodLine line : lines) {
            address = line.resolve(address);
            addressChanged |= line.isAddressChanged();
        }

        addressChanged |= endAddress != address;
        endAddress = address;

        return address;
    }

    public boolean isAddressChanged() {
        return addressChanged;
    }

    public int getReturnsCount() {
        return returns.size();
    }

    public int getParametersCount() {
        return parameters.size();
    }

    public int getLocalsSize() {
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

    public int getStackSize() {
        return parameters.size() * 4 + getLocalsSize();
    }

    public void addSource(Spin1MethodLine line) {
        lines.add(line);
    }

    public List<Spin1MethodLine> getLines() {
        return lines;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void writeTo(Spin1Object obj) {
        if (comment != null) {
            obj.writeComment(comment);
        }

        for (Spin1MethodLine line : lines) {
            line.writeTo(obj);
        }
    }

}
