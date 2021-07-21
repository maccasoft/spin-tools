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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.spin2.Spin2Bytecode;
import com.maccasoft.propeller.spin2.Spin2Context;

public class VariableOp extends Spin2Bytecode {

    public enum Size {
        Byte, Word, Long
    };

    public static enum Op {
        Read, Write, Setup, Address
    }

    public Size ss;
    public Op op;
    public boolean indexed;
    public boolean push;
    public Variable variable;
    public int index;

    public VariableOp(Spin2Context context, Op op, boolean indexed, Variable variable) {
        super(context);
        this.op = op;
        this.indexed = indexed;
        this.variable = variable;

        this.ss = Size.Long;
        if ("BYTE".equalsIgnoreCase(variable.getType())) {
            this.ss = Size.Byte;
        }
        else if ("WORD".equalsIgnoreCase(variable.getType())) {
            this.ss = Size.Word;
        }
    }

    public VariableOp(Spin2Context context, Op op, boolean indexed, Variable variable, int index) {
        super(context);
        this.op = op;
        this.indexed = indexed;
        this.variable = variable;
        this.index = index;

        if ("LONG".equalsIgnoreCase(variable.getType())) {
            this.ss = Size.Long;
            this.index = index * 4;
        }
        else if ("WORD".equalsIgnoreCase(variable.getType())) {
            this.ss = Size.Word;
            this.index = index * 2;
        }
        else {
            this.ss = Size.Byte;
        }
    }

    public VariableOp(Spin2Context context, Size ss, Op op, boolean indexed, Variable variable) {
        super(context);
        this.ss = ss;
        this.op = op;
        this.indexed = indexed;
        this.variable = variable;
    }

    @Override
    public int getSize() {
        int size = 0;
        int offset = variable.getOffset() + index;

        if (!indexed && index == 0 && ss == Size.Long && (offset >> 2) <= 15) {
            if (variable instanceof LocalVariable) {
                if (op == Op.Read) {
                    size++;
                }
                else if (op == Op.Write) {
                    size++;
                }
                else {
                    size++;
                    if (op == Op.Address) {
                        size++;
                    }
                }
            }
            else {
                size++;
                if (op == Op.Read) {
                    size++;
                }
                else if (op == Op.Write) {
                    size++;
                }
                else if (op == Op.Address) {
                    size++;
                }
            }
        }
        else {
            size++;
            size += Constant.wrVarSize(offset);

            if (op == Op.Address) {
                size++;
            }
            else if (op == Op.Read) {
                size++;
            }
            else if (op == Op.Write) {
                size++;
            }
        }

        return size;
    }

    @Override
    public byte[] getBytes() {
        int offset = variable.getOffset() + index;
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        if (!indexed && index == 0 && ss == Size.Long && (offset >> 2) <= 15) {
            offset >>= 2;

            if (variable instanceof LocalVariable) {
                if (op == Op.Read) {
                    os.write(0xE0 + offset);
                }
                else if (op == Op.Write) {
                    os.write(0xF0 + offset);
                }
                else {
                    os.write(0xD0 + offset);
                    if (op == Op.Address) {
                        os.write(0x7F);
                    }
                }
            }
            else {
                os.write(0xC0 + offset);
                if (op == Op.Read) {
                    os.write(0x80);
                }
                else if (op == Op.Write) {
                    os.write(0x81);
                }
                else if (op == Op.Address) {
                    os.write(0x7F);
                }
            }
        }
        else {
            if (indexed) {
                switch (ss) {
                    case Byte:
                        os.write((variable instanceof LocalVariable) ? 0x55 : 0x54);
                        break;
                    case Word:
                        os.write((variable instanceof LocalVariable) ? 0x5B : 0x5A);
                        break;
                    case Long:
                        os.write((variable instanceof LocalVariable) ? 0x61 : 0x60);
                        break;
                }
            }
            else {
                switch (ss) {
                    case Byte:
                        os.write((variable instanceof LocalVariable) ? 0x52 : 0x51);
                        break;
                    case Word:
                        os.write((variable instanceof LocalVariable) ? 0x58 : 0x57);
                        break;
                    case Long:
                        os.write((variable instanceof LocalVariable) ? 0x5E : 0x5D);
                        break;
                }
            }

            try {
                os.write(Constant.wrVar(offset));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (op == Op.Address) {
                os.write(0x7F);
            }
            else if (op == Op.Read) {
                os.write(0x80);
            }
            else if (op == Op.Write) {
                os.write(0x81);
            }
        }

        return os.toByteArray();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("VAR_");

        if (op == Op.Read) {
            sb.append("READ");
        }
        else if (op == Op.Write) {
            sb.append("WRITE");
        }
        else if (op == Op.Setup) {
            sb.append("SETUP");
        }
        else if (op == Op.Address) {
            sb.append("ADDRESS");
        }

        if (indexed) {
            sb.append("_INDEXED");
        }

        if (op != Op.Address) {
            sb.append(" ");
            sb.append(variable.getType());
        }

        if (variable instanceof LocalVariable) {
            sb.append(" DBASE");
        }
        else if (variable instanceof Variable) {
            sb.append(" VBASE");
        }

        int offset = variable.getOffset();
        if (!indexed && ss == Size.Long && (offset >> 2) <= 15) {
            sb.append(String.format("+$%05X", offset >> 2));
            sb.append(" (short)");
        }
        else {
            sb.append(String.format("+$%05X", offset));
        }

        return sb.toString();
    }

}
