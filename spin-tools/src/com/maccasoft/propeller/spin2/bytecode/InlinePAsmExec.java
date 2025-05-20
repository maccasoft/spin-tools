/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.bytecode;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin2.Spin2Bytecode;

public class InlinePAsmExec extends Spin2Bytecode {

    int org;
    boolean orgh;

    int size;

    public InlinePAsmExec(Context context, int org, boolean orgh) {
        super(context);
        this.org = org;
        this.orgh = orgh;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int getSize() {
        return getBytes().length;
    }

    @Override
    public byte[] getBytes() {
        if (orgh) {
            return new byte[] {
                Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_orgh,
                (byte) (org >> 8),
                (byte) org
            };
        }
        return new byte[] {
            Spin2Bytecode.bc_hub_bytecode, (byte) Spin2Bytecode.bc_org,
            (byte) org,
            (byte) (org >> 8),
            (byte) size,
            (byte) (size >> 8),
        };
    }

    @Override
    public String toString() {
        if (orgh) {
            return String.format("INLINE-EXEC ORGH=$%03x", org);
        }
        return String.format("INLINE-EXEC ORG=$%03x, %d", org, size + 1);
    }

}
