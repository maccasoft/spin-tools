/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1.instructions;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Type;
import com.maccasoft.propeller.spin1.Spin1InstructionObject;
import com.maccasoft.propeller.spin1.Spin1PAsmExpression;

public class Wordfit extends Word {

    @Override
    public Spin1InstructionObject createObject(Context context, String condition, List<Spin1PAsmExpression> arguments, String effect) {
        return new Wordfit_(context, arguments);
    }

    public class Wordfit_ extends Spin1InstructionObject {

        List<Spin1PAsmExpression> arguments;

        public Wordfit_(Context context, List<Spin1PAsmExpression> arguments) {
            super(context);
            this.arguments = arguments;
        }

        @Override
        public int resolve(int address, int memoryAddress) {
            while ((address % 2) != 0) {
                address++;
            }
            context.setAddress(address >> 2);
            context.setMemoryAddress(memoryAddress);
            return address + getSize();
        }

        @Override
        public int getSize() {
            int size = 0;

            for (Spin1PAsmExpression exp : arguments) {
                size += exp.getWordSize();
            }

            return size;
        }

        @Override
        public byte[] getBytes() {
            CompilerException msgs = new CompilerException();
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            for (Spin1PAsmExpression exp : arguments) {
                try {
                    if (exp.getExpression() instanceof Type) {
                        switch (((Type) exp.getExpression()).getType().toUpperCase()) {
                            case "BYTE":
                                if (exp.getInteger() < -0x80 || exp.getInteger() > 0xFF) {
                                    throw new CompilerException("Byte value must range from -$80 to $FF", exp.getExpression().getData());
                                }
                                break;
                            case "LONG":
                                break;
                            default:
                                if (exp.getInteger() < -0x8000 || exp.getInteger() > 0xFFFF) {
                                    throw new CompilerException("Word value must range from -$8000 to $FFFF", exp.getExpression().getData());
                                }
                                break;
                        }
                    }
                    else if (exp.getInteger() < -0x8000 || exp.getInteger() > 0xFFFF) {
                        throw new CompilerException("Word value must range from -$8000 to $FFFF", exp.getExpression().getData());
                    }

                    byte[] value = exp.getWord();

                    int v = 0, d = 0;
                    byte[] data = new byte[value.length * exp.getCount()];
                    while (d < data.length) {
                        data[d++] = value[v];
                        v = (v + 1) % value.length;
                    }
                    os.write(data);

                } catch (CompilerException e) {
                    msgs.addMessage(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

            if (msgs.hasChilds()) {
                throw msgs;
            }

            return os.toByteArray();
        }

    }

}
