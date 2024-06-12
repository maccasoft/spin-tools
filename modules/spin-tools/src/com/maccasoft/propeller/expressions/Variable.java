/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.expressions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Variable extends Expression {

    private String type;
    private String name;
    private int size;
    private int offset;

    protected boolean align;
    protected List<Variable> members = new ArrayList<>();

    private Set<Object> calledBy = new HashSet<>();

    public Variable(String type, String name, int size) {
        this.type = type.toUpperCase();
        this.name = name;
        this.size = size;
    }

    public Variable(String type, String name, int size, int offset) {
        this.type = type.toUpperCase();
        this.name = name;
        this.size = size;
        this.offset = offset;
    }

    public Variable(String type, String name, int size, int offset, boolean align) {
        this.type = type.toUpperCase();
        this.name = name;
        this.size = size;
        this.offset = offset;
        this.align = align;
    }

    public Variable addMember(String type, String name, int size) {
        int typeSize = members.size() == 0 ? 0 : getTypeSize();

        if (align) {
            if ("WORD".equalsIgnoreCase(type)) {
                typeSize = (typeSize + 1) & ~1;
            }
            else if (!"BYTE".equalsIgnoreCase(type)) {
                typeSize = (typeSize + 3) & ~3;
            }
        }

        Variable var = new Variable(type, name, size, getOffset() + typeSize, align) {

            @Override
            public void setCalledBy(Object method) {
                Variable.this.setCalledBy(method);
            }

            @Override
            public void removeCalledBy(Object method) {
                Variable.this.removeCalledBy(method);
            }

            @Override
            public boolean isReferenced() {
                return Variable.this.isReferenced();
            }

        };
        members.add(var);

        return var;
    }

    public boolean hasMembers() {
        return members.size() != 0;
    }

    public Variable getMember(String name) {
        for (Variable var : members) {
            if (name.equals(var.getName())) {
                return var;
            }
        }
        return null;
    }

    public String getType() {
        if (type.endsWith("*")) {
            return "LONG";
        }
        if ("SHORT".equals(type)) {
            return "WORD";
        }
        if ("INT".equals(type)) {
            return "LONG";
        }
        return type;
    }

    public int getTypeSize() {
        if (members.size() == 0) {
            if ("SHORT".equals(type) || "WORD".equals(type)) {
                return 2;
            }
            else if ("BYTE".equals(type)) {
                return 1;
            }
            return 4;
        }
        else {
            int typeSize = 0;

            for (Variable var : members) {
                if (align) {
                    if ("WORD".equalsIgnoreCase(var.getType())) {
                        typeSize = (typeSize + 1) & ~1;
                    }
                    else if (!"BYTE".equalsIgnoreCase(var.getType())) {
                        typeSize = (typeSize + 3) & ~3;
                    }
                }
                typeSize += var.getTypeSize() * var.getSize();
            }

            return typeSize;
        }
    }

    public boolean isPointer() {
        return type.endsWith("*");
    }

    public String getPointerType() {
        if (!type.endsWith("*")) {
            return null;
        }
        return type.substring(0, type.indexOf(' ')).toUpperCase();
    }

    public int getPointerSize() {
        if (type.startsWith("BYTE")) {
            return 1;
        }
        if (type.startsWith("WORD") || type.startsWith("SHORT")) {
            return 2;
        }
        return 4;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    public void setCalledBy(Object method) {
        if (!calledBy.contains(method)) {
            calledBy.add(method);
        }
    }

    public void removeCalledBy(Object method) {
        calledBy.remove(method);
    }

    public boolean isReferenced() {
        return calledBy.size() != 0;
    }

    @Override
    public String toString() {
        return name;
    }

}
