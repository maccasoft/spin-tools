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
import java.io.IOException;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin2.Spin2Bytecode;

public class BitField extends Spin2Bytecode {

    public static enum Op {
        Read, Write, Setup, Field
    }

    Op op;
    boolean push;
    int bitfield = -1;

    public BitField(Context context, Op op, int bitfield) {
        super(context);
        this.op = op;
        this.bitfield = bitfield;
    }

    public BitField(Context context, Op op, boolean push, int bitfield) {
        super(context);
        this.op = op;
        this.push = push;
        this.bitfield = bitfield;
    }

    @Override
    public int getSize() {
        try {
            return getBytes().length;
        } catch (Exception e) {
            // Do nothing
        }
        return 5;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            if (bitfield == -1) {
                os.write(Spin2Bytecode.bc_setup_bfield_pop); // Read (pop)
            }
            else {
                if (bitfield >= 0 && bitfield <= 31) {
                    os.write(Spin2Bytecode.bc_setup_bfield_0_31 + bitfield);
                }
                else {
                    os.write(Spin2Bytecode.bc_setup_bfield_rfvar);
                    os.write(Constant.wrVar(bitfield));
                }
            }

            if (op == Op.Field) {
                os.write(Spin2Bytecode.bc_get_field);
            }
            else if (op == Op.Read) {
                os.write(Spin2Bytecode.bc_read);
            }
            else if (op == Op.Write) {
                os.write(push ? Spin2Bytecode.bc_write_push : Spin2Bytecode.bc_write);
            }
        } catch (IOException e) {
            // Do nothing
        }

        return os.toByteArray();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("BITFIELD_");

        switch (op) {
            case Read:
                sb.append("READ");
                break;
            case Write:
                sb.append("WRITE");
                break;
            case Setup:
                sb.append("SETUP");
                break;
            case Field:
                sb.append("PTR");
                break;
        }

        if (bitfield == -1) {
            sb.append(" (pop)");
        }
        else {
            if (bitfield >= 0 && bitfield <= 31) {
                sb.append(" (short)");
            }
        }

        if (op == Op.Write && push) {
            sb.append(" (push)");
        }

        return sb.toString();
    }

}
