/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.expressions;

import java.util.HashMap;
import java.util.Map;

public class StructureVariable extends Variable {

    int typeSize;
    boolean align;
    Map<String, Variable> elements = new HashMap<>();

    public StructureVariable(String type, String name, int size, int offset, boolean align) {
        super(type, name, size, offset);
        this.align = align;
    }

    public Variable addVariable(String type, String name, int size) {
        int varSize = size;
        if ("WORD".equalsIgnoreCase(type)) {
            varSize = varSize * 2;
            if (align) {
                typeSize = (typeSize + 1) & ~1;
            }
        }
        else if (!"BYTE".equalsIgnoreCase(type)) {
            varSize = varSize * 4;
            if (align) {
                typeSize = (typeSize + 3) & ~3;
            }
        }

        Variable var = new Variable(type, name, size, getOffset() + typeSize) {

            @Override
            public void setCalledBy(Object method) {
                StructureVariable.this.setCalledBy(method);
            }

            @Override
            public void removeCalledBy(Object method) {
                StructureVariable.this.removeCalledBy(method);
            }

            @Override
            public boolean isReferenced() {
                return StructureVariable.this.isReferenced();
            }

        };
        elements.put(name, var);

        typeSize += varSize;

        return var;
    }

    public Variable getVariable(String name) {
        return elements.get(name);
    }

    @Override
    public int getTypeSize() {
        return align ? (typeSize + 3) & ~3 : typeSize;
    }

}
