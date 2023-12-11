/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1.bytecode;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.spin1.Spin1Bytecode;

public class CallSub extends Spin1Bytecode {

    Method method;
    boolean indexed;

    public CallSub(Context context, Method method) {
        super(context);
        this.method = method;
    }

    public CallSub(Context context, Method method, boolean indexed) {
        super(context);
        this.method = method;
        this.indexed = indexed;
    }

    @Override
    public int getSize() {
        return method.getObjectIndex() == -1 ? 2 : 3;
    }

    @Override
    public byte[] getBytes() {
        int objectIndex = method.getObjectIndex();
        int methodIndex = method.getIndex() + 1;
        if (objectIndex == -1) {
            return new byte[] {
                (byte) 0b00000101,
                (byte) methodIndex
            };
        }
        else {
            return new byte[] {
                (byte) (indexed ? 0b00000111 : 0b00000110),
                (byte) objectIndex,
                (byte) methodIndex
            };
        }
    }

    @Override
    public String toString() {
        int objectIndex = method.getObjectIndex();
        int methodIndex = method.getIndex();
        if (objectIndex == -1) {
            return String.format("CALL_SUB (%d)", methodIndex);
        }
        else {
            if (indexed) {
                return String.format("CALL_OBJ_SUB (%d.%d) (indexed)", objectIndex, methodIndex);
            }
            return String.format("CALL_OBJ_SUB (%d.%d)", objectIndex, methodIndex);
        }
    }

}
