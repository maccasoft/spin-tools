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
import com.maccasoft.propeller.spin1.Spin1InstructionObject;
import com.maccasoft.propeller.spin1.Spin1PAsmExpression;
import com.maccasoft.propeller.spin1.Spin1PAsmInstructionFactory;

public class Long extends Spin1PAsmInstructionFactory {

    @Override
    public Spin1InstructionObject createObject(Context context, String condition, List<Spin1PAsmExpression> arguments, String effect) {
        return new Long_(context, arguments);
    }

    public class Long_ extends Spin1InstructionObject {

        List<Spin1PAsmExpression> arguments;

        public Long_(Context context, List<Spin1PAsmExpression> arguments) {
            super(context);
            this.arguments = arguments;
        }

        @Override
        public int resolve(int address, int memoryAddress) {
            address = (address + 3) & ~3;
            context.setAddress(address >> 2);
            context.setMemoryAddress(memoryAddress);
            return address + getSize();
        }

        @Override
        public int getSize() {
            int size = 0;

            for (Spin1PAsmExpression exp : arguments) {
                size += exp.getLongSize();
            }

            return size;
        }

        @Override
        public byte[] getBytes() {
            CompilerException msgs = new CompilerException();
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            for (Spin1PAsmExpression exp : arguments) {
                try {
                    byte[] value = exp.getLong();
                    for (int i = 0; i < exp.getCount(); i++) {
                        os.write(value);
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
