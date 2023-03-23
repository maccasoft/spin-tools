/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import com.maccasoft.propeller.SpinObject;

public class Spin1Object extends SpinObject {

    public static class Spin1LinkDataObject extends LinkDataObject {

        public Spin1LinkDataObject(Object object, long offset, long varOffset) {
            super(object, offset, varOffset);
        }

        @Override
        public void setOffset(long offset) {
            this.bytes = new byte[] {
                (byte) offset,
                (byte) (offset >> 8),
                (byte) getVarOffset(),
                (byte) (getVarOffset() >> 8)
            };
            super.setOffset(offset);
        }

        @Override
        public void setVarOffset(long varOffset) {
            this.bytes = new byte[] {
                (byte) getOffset(),
                (byte) (getOffset() >> 8),
                (byte) varOffset,
                (byte) (varOffset >> 8)
            };
            super.setVarOffset(varOffset);
        }

    }

    int dcurr;

    public Spin1Object() {

    }

    public int getDcurr() {
        return dcurr;
    }

    public void setDcurr(int dcurr) {
        this.dcurr = dcurr;
    }

}
