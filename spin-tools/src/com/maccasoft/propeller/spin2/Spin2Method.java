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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.iterators.ReverseListIterator;
import org.apache.commons.lang3.BitField;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.spin2.bytecode.Constant;

public class Spin2Method {

    public static final BitField address_bit = new BitField(0b0_0000000_0000_11111111111111111111);
    public static final BitField returns_bit = new BitField(0b0_0000000_1111_00000000000000000000);
    public static final BitField parameters_bit = new BitField(0b0_1111111_0000_00000000000000000000);

    Context scope;

    String label;
    List<LocalVariable> parameters;
    List<LocalVariable> returns;
    List<LocalVariable> localVariables;

    int varOffset;

    List<Spin2MethodLine> lines = new ArrayList<>();
    public List<Spin2StatementNode> debugNodes = new ArrayList<>();

    String comment;
    Object data;

    int startAddress;
    int endAddress;
    boolean addressChanged;

    List<Spin2Method> calledBy = new ArrayList<>();
    List<Spin2Method> calls = new ArrayList<>();

    public Spin2Method(Context scope, String label) {
        this.scope = scope;
        this.label = label;
        this.parameters = new ArrayList<>();
        this.returns = new ArrayList<>();
        this.localVariables = new ArrayList<>();
    }

    public Context getScope() {
        return scope;
    }

    public String getLabel() {
        return label;
    }

    public int getVarOffset() {
        return varOffset;
    }

    public void addParameter(LocalVariable var) {
        scope.addSymbol(var.getName(), var);
        parameters.add(var);
        varOffset += (var.getTypeSize() * var.getSize() + 3) & ~3;
    }

    public void addReturnVariable(LocalVariable var) {
        scope.addSymbol(var.getName(), var);
        returns.add(var);
        varOffset += (var.getTypeSize() * var.getSize() + 3) & ~3;
    }

    public void addLocalVariable(LocalVariable var) {
        scope.addSymbol(var.getName(), var);
        localVariables.add(var);
        varOffset += var.getTypeSize() * var.getSize();
    }

    public void alignWord() {
        varOffset = (varOffset + 1) & ~1;
    }

    public void alignLong() {
        varOffset = (varOffset + 3) & ~3;
    }

    public void register() {
        for (Spin2MethodLine line : lines) {
            line.register(scope);
        }
    }

    public int resolve(int address) {
        addressChanged = startAddress != address;
        startAddress = address;

        scope.setAddress(address);
        address++;
        for (Spin2MethodLine line : lines) {
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

    public List<LocalVariable> getReturns() {
        return returns;
    }

    public int getReturnLongs() {
        int count = 0;
        for (LocalVariable var : returns) {
            count += (var.getTypeSize() + 3) & ~3;
        }
        return count / 4;
    }

    public LocalVariable getParameter(int index) {
        return parameters.get(index);
    }

    public List<LocalVariable> getParameters() {
        return parameters;
    }

    public int getParametersCount() {
        return parameters.size();
    }

    public int getParameterLongs() {
        int count = 0;
        for (LocalVariable var : parameters) {
            count += (var.getTypeSize() + 3) & ~3;
        }
        return count / 4;
    }

    public int getMinParameterLongs() {
        int count = getParameterLongs();

        ReverseListIterator<LocalVariable> iter = new ReverseListIterator<>(parameters);
        while (iter.hasPrevious()) {
            LocalVariable var = iter.previous();
            if (var.getValue() == null) {
                break;
            }
            count -= (var.getTypeSize() + 3) / 4;
        }

        return count;
    }

    public List<LocalVariable> getLocalVariables() {
        return localVariables;
    }

    public List<LocalVariable> getAllLocalVariables() {
        List<LocalVariable> list = new ArrayList<LocalVariable>();
        list.addAll(parameters);
        list.addAll(returns);
        list.addAll(localVariables);
        return list;
    }

    public int getLocalVariableLongs() {
        int count = 0;

        for (LocalVariable var : localVariables) {
            count += var.getTypeSize() * var.getSize();
        }

        return (count + 3) >> 2;
    }

    public void addSource(Spin2MethodLine line) {
        lines.add(line);
    }

    public List<Spin2MethodLine> getLines() {
        return lines;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void writeTo(Spin2Object obj) {
        if (comment != null) {
            obj.writeComment(comment);
        }

        obj.writeBytes(Constant.wrVar(getLocalVariableLongs()), "(stack size)");

        for (Spin2MethodLine line : lines) {
            line.writeTo(obj);
        }
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setCalledBy(Spin2Method method) {
        if (method == this) {
            return;
        }
        if (!calledBy.contains(method)) {
            calledBy.add(method);
        }
        if (!method.calls.contains(this)) {
            method.calls.add(this);
        }
    }

    void removeCalledBy(Spin2Method method) {
        calledBy.remove(method);
        if (calledBy.size() == 0) {
            for (Spin2Method ref : calls) {
                ref.removeCalledBy(this);
            }
        }
    }

    public void remove() {
        for (Spin2Method ref : calls) {
            ref.removeCalledBy(this);
        }
    }

    public boolean isReferenced() {
        return calledBy.size() != 0;
    }

}
