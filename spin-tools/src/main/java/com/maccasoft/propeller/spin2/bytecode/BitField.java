/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin2.Spin2Bytecode;
import com.maccasoft.propeller.spin2.bytecode.Bytecode.Op;

public class BitField extends Spin2Bytecode {

    Op op;
    int bitfield = -1;

    public BitField(Context context, Op op, int bitfield) {
        super(context);
        this.op = op;
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
                if (op == Bytecode.Op.Read) {
                    os.write(Spin2Bytecode.bc_read_bfield_pop);
                }
                else if (op == Bytecode.Op.Write) {
                    os.write(Spin2Bytecode.bc_write_bfield_pop);
                }
                else {
                    os.write(Spin2Bytecode.bc_setup_bfield_pop);
                }
            }
            else {
                if (bitfield >= 0 && bitfield <= 31) {
                    if (op == Bytecode.Op.Read) {
                        os.write(Spin2Bytecode.bc_read_bfield_0_31 + bitfield);
                    }
                    else if (op == Bytecode.Op.Write) {
                        os.write(Spin2Bytecode.bc_write_bfield_0_31 + bitfield);
                    }
                    else {
                        os.write(Spin2Bytecode.bc_setup_bfield_0_31 + bitfield);
                    }
                }
                else {
                    if (op == Bytecode.Op.Read) {
                        os.write(Spin2Bytecode.bc_read_bfield_rfvar);
                    }
                    else if (op == Bytecode.Op.Write) {
                        os.write(Spin2Bytecode.bc_write_bfield_rfvar);
                    }
                    else {
                        os.write(Spin2Bytecode.bc_setup_bfield_rfvar);
                    }
                    os.write(Constant.wrVar(bitfield));
                }
            }
            if (op == Bytecode.Op.Field) {
                os.write(Spin2Bytecode.bc_get_field);
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

        return sb.toString();
    }

}
