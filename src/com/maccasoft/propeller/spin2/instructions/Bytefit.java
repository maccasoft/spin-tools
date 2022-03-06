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

import com.maccasoft.propeller.CompilerMessage;
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.spin2.Spin2Context;
import com.maccasoft.propeller.spin2.Spin2InstructionObject;
import com.maccasoft.propeller.spin2.Spin2PAsmExpression;

public class Bytefit extends Byte {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        return new Bytefit_(context, arguments);
    }

    public class Bytefit_ extends Spin2InstructionObject {

        List<Spin2PAsmExpression> arguments;

        public Bytefit_(Spin2Context context, List<Spin2PAsmExpression> arguments) {
            super(context);
            this.arguments = arguments;
        }

        @Override
        public int getSize() {
            int size = 0;
            for (Spin2PAsmExpression exp : arguments) {
                if (exp.getExpression() instanceof CharacterLiteral) {
                    size += ((CharacterLiteral) exp.getExpression()).getString().length();
                }
                else {
                    size += exp.getCount();
                }
            }
            return size;
        }

        @Override
        public byte[] getBytes() {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                for (Spin2PAsmExpression exp : arguments) {
                    if (exp.getExpression().isString()) {
                        os.write(exp.getExpression().getString().getBytes());
                    }
                    else {
                        int value = exp.getInteger();
                        if (value < -0x80 || value > 0xFF) {
                            throw new CompilerMessage("Byte value must range from -$80 to $FF", exp.getExpression().getData());
                        }
                        for (int i = 0; i < exp.getCount(); i++) {
                            os.write(value);
                        }
                    }
                }
            } catch (CompilerMessage e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return os.toByteArray();
        }

    }

}
