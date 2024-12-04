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
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.Variable;
import com.maccasoft.propeller.spin2.Spin2Bytecode;

public class VariableOp extends Spin2Bytecode {

    public enum Size {
        Byte, Word, Long
    };

    public static enum Op {
        Read, Write, Setup, Address, PBaseAddress, Field
    }

    public Size ss;
    public Op op;
    public boolean indexed;
    public boolean push;
    public Variable variable;
    public boolean hasIndex;
    public int index;

    public VariableOp(Context context, Op op, boolean indexed, Variable variable) {
        super(context);
        this.op = op;
        this.indexed = indexed;
        this.variable = variable;

        this.ss = Size.Long;
        if (!variable.isPointer()) {
            if ("BYTE".equalsIgnoreCase(variable.getType())) {
                this.ss = Size.Byte;
            }
            else if ("WORD".equalsIgnoreCase(variable.getType())) {
                this.ss = Size.Word;
            }
        }
    }

    public VariableOp(Context context, Op op, Variable variable) {
        this(context, op, false, variable, false, 0);
    }

    public VariableOp(Context context, Op op, boolean indexed, Variable variable, boolean hasIndex, int index) {
        super(context);
        this.op = op;
        this.indexed = indexed;
        this.variable = variable;
        this.hasIndex = hasIndex;
        this.index = index;

        if (variable.isPointer()) {
            this.ss = Size.Long;
            this.index = index * 4;
        }
        else {
            if ("BYTE".equalsIgnoreCase(variable.getType())) {
                this.ss = Size.Byte;
            }
            else if ("WORD".equalsIgnoreCase(variable.getType())) {
                this.ss = Size.Word;
                this.index = index * 2;
            }
            else {
                this.ss = Size.Long;
                this.index = index * 4;
            }
        }
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
                        os.write(bc_read_local_0_15 + offset);
                    }
                    else if (op == Op.Write) {
                        os.write(bc_write_local_0_15 + offset);
                    }
                    else {
                        os.write(bc_setup_local_0_15 + offset);
                        if (op == Op.Address) {
                            os.write(bc_get_addr);
                        }
                        else if (op == Op.Field) {
                            os.write(bc_get_field);
                        }
                    }
                }
                else {
                    os.write(bc_setup_var_0_15 + offset);
                    if (op == Op.Read || op == Op.PBaseAddress) {
                        os.write(bc_read);
                    }
                    else if (op == Op.Write) {
                        os.write(bc_write);
                    }
                    else if (op == Op.Address) {
                        os.write(bc_get_addr);
                    }
                    else if (op == Op.Field) {
                        os.write(bc_get_field);
                    }
                }
            }
            else {
                if (indexed) {
                    switch (ss) {
                        case Byte:
                            os.write((variable instanceof LocalVariable) ? bc_setup_byte_dbase_pi : bc_setup_byte_vbase_pi);
                            break;
                        case Word:
                            os.write((variable instanceof LocalVariable) ? bc_setup_word_dbase_pi : bc_setup_word_vbase_pi);
                            break;
                        case Long:
                            os.write((variable instanceof LocalVariable) ? bc_setup_long_dbase_pi : bc_setup_long_vbase_pi);
                            break;
                    }
                }
                else {
                    switch (ss) {
                        case Byte:
                            os.write((variable instanceof LocalVariable) ? bc_setup_byte_dbase : bc_setup_byte_vbase);
                            break;
                        case Word:
                            os.write((variable instanceof LocalVariable) ? bc_setup_word_dbase : bc_setup_word_vbase);
                            break;
                        case Long:
                            os.write((variable instanceof LocalVariable) ? bc_setup_long_dbase : bc_setup_long_vbase);
                            break;
                    }
                }

                os.write(Constant.wrVar(offset));

                if (op == Op.Field) {
                    os.write(bc_get_field);
                }
                else if (op == Op.Address) {
                    os.write(bc_get_addr);
                }
                else if (op == Op.Read || op == Op.PBaseAddress) {
                    os.write(bc_read);
                }
                else if (op == Op.Write) {
                    os.write(bc_write);
                }
            }
            if (op == Op.PBaseAddress) {
                os.write(bc_add_pbase);
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
        else if (op == Op.Field) {
            sb.append("BITFIELD_PTR");
        }

        if (indexed) {
            sb.append("_INDEXED");
        }

        if (op != Op.Address && op != Op.PBaseAddress) {
            sb.append(" ");
            if (variable.getType().startsWith("^")) {
                sb.append("LONG");
                sb.append(" (" + variable.getType() + ")");
            }
            else {
                sb.append(variable.getType());
            }
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
