/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.bytecode;

import java.io.ByteArrayOutputStream;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Method;
import com.maccasoft.propeller.spin2.Spin2Bytecode;

public class SubAddress extends Spin2Bytecode {

    Method method;
    boolean indexed;

    public SubAddress(Context context, Method method) {
        super(context);
        this.method = method;
    }

    public SubAddress(Context context, Method method, boolean indexed) {
        super(context);
        this.method = method;
        this.indexed = indexed;
    }

    @Override
    public int getSize() {
        int objectIndex = method.getObjectIndex();
        int methodIndex = method.getIndex();
        if (objectIndex == -1) {
            return 1 + Constant.wrVar(methodIndex).length;
        }
        else {
            return 1 + Constant.wrVar(objectIndex).length + Constant.wrVar(methodIndex).length;
        }
    }

    @Override
    public byte[] getBytes() {
        int objectIndex = method.getObjectIndex();
        int methodIndex = method.getIndex();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            if (objectIndex == -1) {
                os.write(Spin2Bytecode.bc_mptr_sub);
            }
            else {
                os.write(indexed ? Spin2Bytecode.bc_mptr_obji_sub : Spin2Bytecode.bc_mptr_obj_sub);
                os.write(Constant.wrVar(objectIndex));
            }
            os.write(Constant.wrVar(methodIndex));

        } catch (Exception e) {
            // Do nothing
        }
        return os.toByteArray();
    }

    @Override
    public String toString() {
        int objectIndex = method.getObjectIndex();
        int methodIndex = method.getIndex();
        if (objectIndex == -1) {
            return String.format("SUB_ADDRESS (%d)", methodIndex);
        }
        else {
            if (indexed) {
                return String.format("OBJ_SUB_ADDRESS (%d.%d) (indexed)", objectIndex, methodIndex);
            }
            return String.format("OBJ_SUB_ADDRESS (%d.%d)", objectIndex, methodIndex);
        }
    }

}
