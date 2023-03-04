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
        Read, Write, Setup, Address, PBaseAddress
    }

    public Size ss;
    public Op op;
    public boolean indexed;
    public boolean push;
    public Variable variable;
    public boolean hasIndex;
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

    public VariableOp(Spin2Context context, Op op, boolean indexed, Variable variable, boolean hasIndex, int index) {
        super(context);
        this.op = op;
        this.indexed = indexed;
        this.variable = variable;
        this.hasIndex = hasIndex;
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
        return getBytes().length;
    }

    @Override
    public byte[] getBytes() {
        int offset = variable.getOffset() + index;
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            if (!indexed && !hasIndex && ss == Size.Long && (offset % 4) == 0 && (offset >> 2) <= 15) {
                offset >>= 2;

                if (variable instanceof LocalVariable) {
                    if (op == Op.Read || op == Op.PBaseAddress) {
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
                    if (op == Op.Read || op == Op.PBaseAddress) {
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
                            os.write((variable instanceof LocalVariable) ? 0x56 : 0x55);
                            break;
                        case Word:
                            os.write((variable instanceof LocalVariable) ? 0x5C : 0x5B);
                            break;
                        case Long:
                            os.write((variable instanceof LocalVariable) ? 0x62 : 0x61);
                            break;
                    }
                }
                else {
                    switch (ss) {
                        case Byte:
                            os.write((variable instanceof LocalVariable) ? 0x53 : 0x52);
                            break;
                        case Word:
                            os.write((variable instanceof LocalVariable) ? 0x59 : 0x58);
                            break;
                        case Long:
                            os.write((variable instanceof LocalVariable) ? 0x5F : 0x5E);
                            break;
                    }
                }

                os.write(Constant.wrVar(offset));

                if (op == Op.Address) {
                    os.write(0x7F);
                }
                else if (op == Op.Read || op == Op.PBaseAddress) {
                    os.write(0x80);
                }
                else if (op == Op.Write) {
                    os.write(0x81);
                }
            }
            if (op == Op.PBaseAddress) {
                os.write(0x24);
            }
        } catch (IOException e) {
            // Do nothing
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
        else if (op == Op.Address || op == Op.PBaseAddress) {
            sb.append("ADDRESS");
        }

        if (indexed) {
            sb.append("_INDEXED");
        }

        if (op != Op.Address && op != Op.PBaseAddress) {
            sb.append(" ");
            sb.append(variable.getType());
        }
        sb.append(" ");

        if (op == Op.PBaseAddress) {
            sb.append("PBASE+");
        }

        if (variable instanceof LocalVariable) {
            sb.append("DBASE");
        }
        else if (variable instanceof Variable) {
            sb.append("VBASE");
        }

        int offset = variable.getOffset();
        if (!indexed && ss == Size.Long && (offset % 4) == 0 && (offset >> 2) <= 15) {
            sb.append(String.format("+$%05X", offset >> 2));
            sb.append(" (short)");
        }
        else {
            sb.append(String.format("+$%05X", offset));
        }

        return sb.toString();
    }

}
