/*
 * Copyright (c) 2021 Marco Maccaferri and others.
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

    String type;
    String name;
    Expression size;
    int offset;

    Set<Object> calledBy = new HashSet<>();

    public Variable(String type, String name, Expression size, int offset) {
        this.type = type;
        this.name = name;
        this.size = size;
        this.offset = offset;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Expression getSize() {
        return size;
    }

    public int getOffset() {
        return offset;
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
