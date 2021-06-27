/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1.bytecode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.LocalVariable;
import com.maccasoft.propeller.expressions.Register;
import com.maccasoft.propeller.spin1.Spin1BytecodeExpression;
import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionFactory;
import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionObject;
import com.maccasoft.propeller.spin1.Spin1BytecodeLine;
import com.maccasoft.propeller.spin1.Spin1Context;

public class Assign extends Spin1BytecodeInstructionFactory {

    @Override
    public Spin1BytecodeInstructionObject createObject(Spin1BytecodeLine line) {
        Spin1BytecodeExpression arg = line.getArgument(0);
        Expression expression = line.getScope().getSymbol(arg.getText());
        if (expression instanceof Register) {
            return new RegAssign_(line.getScope(), expression);
        }
        if (expression instanceof LocalVariable) {
            return new LocalVarAssign_(line.getScope(), expression);
        }
        return new VarAssign_(line.getScope(), expression);
    }

    @Override
    public List<Spin1BytecodeLine> expand(Spin1BytecodeLine line) {
        List<Spin1BytecodeLine> list = new ArrayList<Spin1BytecodeLine>();

        for (int i = 1; i < line.getArgumentCount(); i++) {
            Spin1BytecodeExpression expression = line.getArgument(i);
            Spin1BytecodeLine newLine = new Spin1BytecodeLine(new Spin1Context(line.getScope()), null, expression.getText(), expression.getChilds());
            list.addAll(newLine.expand());
        }

        list.add(new Spin1BytecodeLine(line.getScope(), line.getLabel(), line.getMnemonic(), Collections.singletonList(line.getArgument(0))));

        return list;
    }

    class RegAssign_ extends Spin1BytecodeInstructionObject {

        Expression expression;

        public RegAssign_(Spin1Context context, Expression expression) {
            super(context);
            this.expression = expression;
        }

        @Override
        public byte[] getBytes() {
            //                                                            ++-------- 00 read, 01 write, 10 assign
            //                                                            || +++++-- register offset from 0x1E0
            //                                                            || |||||
            return new byte[] {
                (byte) 0x3F,
                (byte) ((expression.getNumber().intValue() - 0x1E0) | 0b1_01_00000),
            };
        }

        @Override
        public String toString() {
            return String.format("REG_WRITE %03X", expression.getNumber().intValue());
        }
    }

    class LocalVarAssign_ extends Spin1BytecodeInstructionObject {

        Expression expression;

        public LocalVarAssign_(Spin1Context context, Expression expression) {
            super(context);
            this.expression = expression;
        }

        @Override
        public byte[] getBytes() {
            int address = expression.getNumber().intValue();
            if ((address / 4) < 8) {
                //                         ++----------- variable op
                //                         || +--------- 0 vbase, 1 dbase
                //                         || | +++----- offset (xxx)
                //                         || | ||| ++-- 00 read, 01 write, 10 assign, 11 address
                //                         || | ||| ||
                int value = xxx.setValue(0b01_1_000_01, address / 4);
                return new byte[] {
                    (byte) value
                };
            }
            else {
                //               +------------- memory op
                //               | ++---------- 00 byte, 01 word, 10 long
                //               | || +-------- 0 none, 1 pop
                //               | || | ++----- 00 pop, 01 pbase, 10 vbase, 11 dbase
                //               | || | || ++-- 00 read, 01 write, 10 assign, 11 address
                //               | || | || ||
                if (address < 127) {
                    return new byte[] {
                        (byte) 0b1_10_0_11_01,
                        (byte) address,
                    };
                }
                else {
                    return new byte[] {
                        (byte) 0b1_10_0_11_01,
                        (byte) (0x80 | (address >> 8)),
                        (byte) address,
                    };
                }
            }
        }

        @Override
        public String toString() {
            int address = expression.getNumber().intValue();
            if ((address / 4) < 8) {
                return "MEM_WRITE LONG DBASE+" + String.format("$%04X", expression.getNumber().intValue()) + " (short)";
            }
            else {
                return "MEM_WRITE LONG DBASE+" + String.format("$%04X", expression.getNumber().intValue()) + " (long)";
            }
        }

    }

    class VarAssign_ extends Spin1BytecodeInstructionObject {

        Expression expression;

        public VarAssign_(Spin1Context context, Expression expression) {
            super(context);
            this.expression = expression;
        }

        @Override
        public byte[] getBytes() {
            int address = expression.getNumber().intValue();
            if ((address / 4) < 8) {
                //                         ++----------- variable op
                //                         || +--------- 0 vbase, 1 dbase
                //                         || | +++----- offset (xxx)
                //                         || | ||| ++-- 00 read, 01 write, 10 assign, 11 address
                //                         || | ||| ||
                int value = xxx.setValue(0b01_0_000_01, address / 4);
                return new byte[] {
                    (byte) value
                };
            }
            else {
                //               +------------- memory op
                //               | ++---------- 00 byte, 01 word, 10 long
                //               | || +-------- 0 none, 1 pop
                //               | || | ++----- 00 pop, 01 pbase, 10 vbase, 11 dbase
                //               | || | || ++-- 00 read, 01 write, 10 assign, 11 address
                //               | || | || ||
                if (address < 127) {
                    return new byte[] {
                        (byte) 0b1_10_0_10_01,
                        (byte) address,
                    };
                }
                else {
                    return new byte[] {
                        (byte) 0b1_10_0_10_01,
                        (byte) (0x80 | (address >> 8)),
                        (byte) address,
                    };
                }
            }
        }

        @Override
        public String toString() {
            int address = expression.getNumber().intValue();
            if ((address / 4) < 8) {
                return "MEM_WRITE LONG DBASE+" + String.format("$%04X", expression.getNumber().intValue()) + " (short)";
            }
            else {
                return "MEM_WRITE LONG DBASE+" + String.format("$%04X", expression.getNumber().intValue()) + " (long)";
            }
        }

    }

}
