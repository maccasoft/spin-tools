/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
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
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Type;
import com.maccasoft.propeller.spin1.Spin1InstructionObject;
import com.maccasoft.propeller.spin1.Spin1PAsmExpression;
import com.maccasoft.propeller.spin1.Spin1PAsmInstructionFactory;

public class Word extends Spin1PAsmInstructionFactory {

    @Override
    public Spin1InstructionObject createObject(Context context, String condition, List<Spin1PAsmExpression> arguments, String effect) {
        return new Word_(context, arguments);
    }

    public class Word_ extends Spin1InstructionObject {

        List<Spin1PAsmExpression> arguments;

        public Word_(Context context, List<Spin1PAsmExpression> arguments) {
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
                if (exp.getExpression().isString()) {
                    size += 2 * ((CharacterLiteral) exp.getExpression()).getString().length();
                }
                else {
                    int typeSize = 2;
                    if (exp.getExpression() instanceof Type) {
                        switch (((Type) exp.getExpression()).getType().toUpperCase()) {
                            case "LONG":
                                typeSize = 4;
                                break;
                        }
                    }
                    size += exp.getCount() * typeSize;
                }
            }
            return size;
        }

        @Override
        public byte[] getBytes() {
            CompilerException msgs = new CompilerException();
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            for (Spin1PAsmExpression exp : arguments) {
                try {
                    if (exp.getExpression().isString()) {
                        byte[] b = exp.getExpression().getString().getBytes();
                        for (int i = 0; i < b.length; i++) {
                            os.write(b[i]);
                            os.write(0);
                        }
                    }
                    else {
                        byte[] value = exp.getWord();
                        byte[] buffer = new byte[value.length * exp.getCount()];
                        for (int i = 0; i < buffer.length; i += value.length) {
                            System.arraycopy(value, 0, buffer, i, value.length);
                        }
                        os.write(buffer);
                    }
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
