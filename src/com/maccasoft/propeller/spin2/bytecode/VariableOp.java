/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.bytecode;

import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.spin2.Spin2Bytecode;
import com.maccasoft.propeller.spin2.Spin2Context;

public class VariableOp extends Spin2Bytecode {

    public static enum Op {
        Read,
        Write,
        Setup,
        Address
    }

    public Op op;
    public Variable variable;

    public VariableOp(Spin2Context context, String label, Op op, Variable variable) {
        super(context, label);
        this.op = op;
        this.variable = variable;
    }

    @Override
    public int getSize() {
        int value = variable.getNumber().intValue();

        if ("LONG".equalsIgnoreCase(variable.getType()) && (value / 4) <= 15) {
            value /= 4;
            if (variable instanceof LocalVariable) {
                return 1;
            }
            else {
                return 2;
            }
        }

        return Constant.wrVarSize(value) + 2;
    }

    @Override
    public byte[] getBytes() {
        int value = variable.getNumber().intValue();

        if ("LONG".equalsIgnoreCase(variable.getType()) && (value / 4) <= 15) {
            value /= 4;
            if (variable instanceof LocalVariable) {
                int opcode = 0xD0;
                if (op == Op.Read) {
                    opcode = 0xE0;
                }
                else if (op == Op.Write) {
                    opcode = 0xF0;
                }
                else if (op == Op.Address) {
                    return new byte[] {
                        (byte) (opcode + value),
                        (byte) 0x7F
                    };
                }
                return new byte[] {
                    (byte) (opcode + value)
                };
            }
            else {
                if (op == Op.Address) {
                    return new byte[] {
                        (byte) (0xC0 + value),
                        (byte) 0x7F
                    };
                }
                return new byte[] {
                    (byte) (0xC0 + value),
                    (byte) (op == Op.Read ? 0x80 : 0x81)
                };
            }
        }

        byte[] v = Constant.wrVar(value);
        byte[] b = new byte[v.length + 2];

        if (variable instanceof LocalVariable) {
            if ("WORD".equalsIgnoreCase(variable.getType())) {
                b[0] = 0x58;
            }
            else if ("BYTE".equalsIgnoreCase(variable.getType())) {
                b[0] = 0x52;
            }
            else {
                b[0] = 0x5E;
            }
        }
        else {
            if ("WORD".equalsIgnoreCase(variable.getType())) {
                b[0] = 0x57;
            }
            else if ("BYTE".equalsIgnoreCase(variable.getType())) {
                b[0] = 0x51;
            }
            else {
                b[0] = 0x5D;
            }
        }
        for (int i = 0; i < v.length; i++) {
            b[i + 1] = v[i];
        }
        if (op == Op.Address) {
            b[b.length - 1] = (byte) 0x7F;
        }
        else {
            b[b.length - 1] = (byte) (op == Op.Read ? 0x80 : 0x81);
        }

        return b;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("VAR_");
        if (op == Op.Read) {
            sb.append("READ ");
        }
        else if (op == Op.Write) {
            sb.append("WRITE ");
        }
        else if (op == Op.Address) {
            sb.append("ADDRESS ");
        }
        sb.append(variable.getType());
        if (variable instanceof LocalVariable) {
            sb.append(" DBASE");
        }
        else if (variable instanceof Variable) {
            sb.append(" VBASE");
        }
        int value = variable.getNumber().intValue();
        if ("LONG".equalsIgnoreCase(variable.getType()) && (value / 4) <= 15) {
            sb.append(String.format("+$%05X", value / 4));
            sb.append(" (short)");
        }
        else {
            sb.append(String.format("+$%05X", value));
        }
        return sb.toString();
    }

}
