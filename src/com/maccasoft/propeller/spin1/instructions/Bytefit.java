/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
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

import com.maccasoft.propeller.CompilerMessage;
import com.maccasoft.propeller.expressions.CharacterLiteral;
import com.maccasoft.propeller.spin1.Spin1Context;
import com.maccasoft.propeller.spin1.Spin1InstructionObject;
import com.maccasoft.propeller.spin1.Spin1PAsmExpression;

public class Bytefit extends Byte {

    @Override
    public Spin1InstructionObject createObject(Spin1Context context, String condition, List<Spin1PAsmExpression> arguments, String effect) {
        return new Bytefit_(context, arguments);
    }

    public class Bytefit_ extends Spin1InstructionObject {

        List<Spin1PAsmExpression> arguments;

        public Bytefit_(Spin1Context context, List<Spin1PAsmExpression> arguments) {
            super(context);
            this.arguments = arguments;
        }

        @Override
        public int getSize() {
            int size = 0;
            for (Spin1PAsmExpression exp : arguments) {
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
                for (Spin1PAsmExpression exp : arguments) {
                    if (exp.getExpression() instanceof CharacterLiteral) {
                        os.write(((CharacterLiteral) exp.getExpression()).getString().getBytes());
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
