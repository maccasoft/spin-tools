/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.instructions;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin2.Spin2InstructionObject;
import com.maccasoft.propeller.spin2.Spin2PAsmExpression;
import com.maccasoft.propeller.spin2.Spin2PAsmInstructionFactory;

public class Byte extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        return new Byte_(context, arguments);
    }

    public class Byte_ extends Spin2InstructionObject {

        List<Spin2PAsmExpression> arguments;

        public Byte_(Context context, List<Spin2PAsmExpression> arguments) {
            super(context);
            this.arguments = arguments;
        }

        @Override
        public int getSize() {
            int size = 0;
            try {
                size = getBytes().length;
            } catch (Exception e) {

            }
            return size;
        }

        @Override
        public byte[] getBytes() {
            CompilerException msgs = new CompilerException();
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            for (Spin2PAsmExpression exp : arguments) {
                try {
                    if (exp.getExpression().isString()) {
                        os.write(exp.getExpression().getString().getBytes());
                    }
                    else {
                        byte[] value = exp.getByte();
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
