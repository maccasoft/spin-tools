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

    public static class LinkDataObject extends DataObject {

        Object object;
        long offset;
        long varOffset;

        public LinkDataObject(Object object, long offset, long varOffset) {
            super(new byte[] {
                (byte) offset,
                (byte) (offset >> 8),
                (byte) varOffset,
                (byte) (varOffset >> 8)
            });
            this.object = object;
            this.offset = offset;
            this.varOffset = varOffset;
        }

        public long getOffset() {
            return offset;
        }

        public void setOffset(long offset) {
            this.bytes = new byte[] {
                (byte) offset,
                (byte) (offset >> 8),
                (byte) varOffset,
                (byte) (varOffset >> 8)
            };
            this.offset = offset;
        }

        public long getVarOffset() {
            return varOffset;
        }

        public void setVarOffset(long varOffset) {
            this.bytes = new byte[] {
                (byte) offset,
                (byte) (offset >> 8),
                (byte) varOffset,
                (byte) (varOffset >> 8)
            };
            this.varOffset = varOffset;
        }

        public Object getObject() {
            return object;
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
