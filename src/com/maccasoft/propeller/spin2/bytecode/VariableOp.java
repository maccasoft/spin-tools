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
    public boolean push;
    public Variable variable;

    public VariableOp(Spin2Context context, Op op, Variable variable) {
        super(context);
        this.op = op;
        this.variable = variable;
    }

    public VariableOp(Spin2Context context, Op op, boolean push, Variable variable) {
        super(context);
        this.op = op;
        this.push = push;
        this.variable = variable;
    }

    @Override
    public int getSize() {
        int value = variable.getOffset();

        if ("LONG".equalsIgnoreCase(variable.getType()) && (value / 4) <= 15) {
            if (variable instanceof LocalVariable) {
                if (op == Op.Write) {
                    if (push) {
                        return 2;
                    }
                }
                else if (op == Op.Address) {
                    return 2;
                }
                return 1;
            }
            else {
                if (op == Op.Address) {
                    return 2;
                }
                return 2;
            }
        }

        return Constant.wrVarSize(value) + 2;
    }

    @Override
    public byte[] getBytes() {
        int value = variable.getOffset();

        if ("LONG".equalsIgnoreCase(variable.getType()) && (value / 4) <= 15) {
            value /= 4;
            if (variable instanceof LocalVariable) {
                int opcode = 0xD0;
                if (op == Op.Read) {
                    opcode = 0xE0;
                }
                else if (op == Op.Write) {
                    if (push) {
                        return new byte[] {
                            (byte) (opcode + value),
                            (byte) 0x82
                        };
                    }
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

        int index = 0;
        if (variable instanceof LocalVariable) {
            if ("WORD".equalsIgnoreCase(variable.getType())) {
                b[index] = 0x58;
            }
            else if ("BYTE".equalsIgnoreCase(variable.getType())) {
                b[index] = 0x52;
            }
            else {
                b[index] = 0x5E;
            }
        }
        else {
            if ("WORD".equalsIgnoreCase(variable.getType())) {
                b[index] = 0x57;
            }
            else if ("BYTE".equalsIgnoreCase(variable.getType())) {
                b[index] = 0x51;
            }
            else {
                b[index] = 0x5D;
            }
        }
        index++;
        for (int i = 0; i < v.length; i++) {
            b[index++] = v[i];
        }
        if (op == Op.Address) {
            b[index] = (byte) 0x7F;
        }
        else if (op == Op.Read) {
            b[index] = (byte) 0x80;
        }
        else {
            b[index] = (byte) (push ? 0x82 : 0x81);
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
        int value = variable.getOffset();
        if ("LONG".equalsIgnoreCase(variable.getType()) && (value / 4) <= 15) {
            sb.append(String.format("+$%05X", value / 4));
            sb.append(" (short)");
        }
        else {
            sb.append(String.format("+$%05X", value));
        }
        if (op == Op.Write && push) {
            sb.append(" (push)");
        }
        return sb.toString();
    }

}
