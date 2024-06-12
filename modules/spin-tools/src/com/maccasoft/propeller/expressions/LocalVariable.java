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

public class LocalVariable extends Variable {

    Expression value;

    public LocalVariable(String type, String name, int size, int offset) {
        super(type, name, size, offset);
    }

    public LocalVariable(String type, String name, int size, int offset, boolean align) {
        super(type, name, size, offset, align);
    }

    public LocalVariable(String type, String name, Expression value, int size, int offset) {
        super(type, name, size, offset);
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public LocalVariable addMember(String type, String name, int size) {
        int typeSize = members.size() == 0 ? 0 : getTypeSize();

        if (align) {
            if ("WORD".equalsIgnoreCase(type)) {
                typeSize = (typeSize + 1) & ~1;
            }
            else if (!"BYTE".equalsIgnoreCase(type)) {
                typeSize = (typeSize + 3) & ~3;
            }
        }

        LocalVariable var = new LocalVariable(type, name, size, getOffset() + typeSize, align) {

            @Override
            public void setCalledBy(Object method) {
                LocalVariable.this.setCalledBy(method);
            }

            @Override
            public void removeCalledBy(Object method) {
                LocalVariable.this.removeCalledBy(method);
            }

            @Override
            public boolean isReferenced() {
                return LocalVariable.this.isReferenced();
            }

        };
        members.add(var);

        return var;
    }

}
