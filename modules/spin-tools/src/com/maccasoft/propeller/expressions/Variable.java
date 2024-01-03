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

import java.util.HashSet;
import java.util.Set;

public class Variable extends Expression {

    private String type;
    private String name;
    private int size;
    private int offset;

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
        if (type.endsWith("*")) {
            return 4;
        }
        if ("SHORT".equals(type) || "WORD".equals(type)) {
            return 2;
        }
        if ("BYTE".equals(type)) {
            return 1;
        }
        return 4;
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
