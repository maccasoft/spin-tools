/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.bytecode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Method;

class CallSubTest {

    class MethodMock extends Method {

        int objectIndex;
        int index;

        public MethodMock(int objectIndex, int index) {
            super("test", 0, 0);
            this.objectIndex = objectIndex;
            this.index = index;
        }

        @Override
        public int getObjectIndex() {
            return objectIndex;
        }

        @Override
        public int getIndex() {
            return index;
        }

    }

    @Test
    void testCallSub() {
        CallSub subject = new CallSub(new Context(), new MethodMock(-1, 1));
        byte[] result = subject.getBytes();
        Assertions.assertEquals("0A 01", toString(result));
    }

    @Test
    void testCallSubOver127() {
        CallSub subject = new CallSub(new Context(), new MethodMock(-1, 128));
        byte[] result = subject.getBytes();
        Assertions.assertEquals("0A 80 01", toString(result));
    }

    @Test
    void testCallObjectSub() {
        CallSub subject = new CallSub(new Context(), new MethodMock(1, 2));
        byte[] result = subject.getBytes();
        Assertions.assertEquals("08 01 02", toString(result));
    }

    @Test
    void testCallObjectSubOver127() {
        CallSub subject = new CallSub(new Context(), new MethodMock(1, 128));
        byte[] result = subject.getBytes();
        Assertions.assertEquals("08 01 80 01", toString(result));
    }

    @Test
    void testCallIndexedObjectSub() {
        CallSub subject = new CallSub(new Context(), new MethodMock(1, 2), true);
        byte[] result = subject.getBytes();
        Assertions.assertEquals("09 01 02", toString(result));
    }

    @Test
    void testCallIndexedObjectSubOver127() {
        CallSub subject = new CallSub(new Context(), new MethodMock(1, 128), true);
        byte[] result = subject.getBytes();
        Assertions.assertEquals("09 01 80 01", toString(result));
    }

    String toString(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i != 0) {
                sb.append(" ");
            }
            sb.append(String.format("%02X", array[i]));
        }
        return sb.toString();
    }

}
