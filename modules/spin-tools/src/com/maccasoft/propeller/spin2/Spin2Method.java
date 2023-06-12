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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BitField;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Expression;
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

    public LocalVariable addParameter(String type, String name, Expression value) {
        LocalVariable var = new LocalVariable(type, name, value, 1, 0) {

            @Override
            public int getOffset() {
                return parameters.indexOf(this) * 4;
            }

        };
        scope.addSymbol(name, var);
        parameters.add(var);
        return var;
    }

    public LocalVariable addReturnVariable(String type, String name) {
        LocalVariable var = new LocalVariable(type, name, 1, 0) {

            @Override
            public int getOffset() {
                return (parameters.size() + returns.indexOf(this)) * 4;
            }

        };
        scope.addSymbol(name, var);
        returns.add(var);
        return var;
    }

    public LocalVariable addLocalVariable(String type, String name, int size) {
        LocalVariable var = new LocalVariable(type, name, size, 0) {

            @Override
            public int getOffset() {
                int offset = (parameters.size() + returns.size()) * 4;

                for (LocalVariable var : localVariables) {
                    if (var == this) {
                        break;
                    }
                    int typeSize = 4;
                    if ("WORD".equalsIgnoreCase(var.getType())) {
                        typeSize = 2;
                    }
                    else if ("BYTE".equalsIgnoreCase(var.getType())) {
                        typeSize = 1;
                    }
                    offset += typeSize * var.getSize();
                }

                return offset;
            }

        };
        scope.addSymbol(name, var);
        localVariables.add(var);
        return var;
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

    public int getReturnsCount() {
        return returns.size();
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

    public int getMinParameters() {
        int count = parameters.size();
        while (count > 0 && parameters.get(count - 1).getValue() != null) {
            count--;
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

    public int getStackSize() {
        int count = 0;

        for (LocalVariable var : localVariables) {
            int typeSize = 4;
            if ("WORD".equalsIgnoreCase(var.getType())) {
                typeSize = 2;
            }
            else if ("BYTE".equalsIgnoreCase(var.getType())) {
                typeSize = 1;
            }
            count += typeSize * var.getSize();
        }

        return (count + 3) >> 2;
    }

    public int getLocalVarSize() {
        int count = parameters.size() * 4 + returns.size() * 4;

        for (LocalVariable var : localVariables) {
            int typeSize = 4;
            if ("WORD".equalsIgnoreCase(var.getType())) {
                typeSize = 2;
            }
            else if ("BYTE".equalsIgnoreCase(var.getType())) {
                typeSize = 1;
            }
            count += typeSize * var.getSize();
        }

        return count;
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

        obj.writeBytes(Constant.wrVar(getStackSize()), "(stack size)");

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
