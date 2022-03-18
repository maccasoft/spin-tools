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

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.spin1.Spin1Context;
import com.maccasoft.propeller.spin1.Spin1InstructionObject;
import com.maccasoft.propeller.spin1.Spin1PAsmExpression;

public class Wordfit extends Word {

    @Override
    public Spin1InstructionObject createObject(Spin1Context context, String condition, List<Spin1PAsmExpression> arguments, String effect) {
        return new Wordfit_(context, arguments);
    }

    public class Wordfit_ extends Spin1InstructionObject {

        List<Spin1PAsmExpression> arguments;

        public Wordfit_(Spin1Context context, List<Spin1PAsmExpression> arguments) {
            super(context);
            this.arguments = arguments;
        }

        @Override
        public int resolve(int address) {
            while ((address % 2) != 0) {
                address++;
            }
            context.setAddress(address >> 2);
            return address + getSize();
        }

        @Override
        public int getSize() {
            int size = 0;
            for (Spin1PAsmExpression exp : arguments) {
                size += 2 * exp.getCount();
            }
            return size;
        }

        @Override
        public byte[] getBytes() {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                for (Spin1PAsmExpression exp : arguments) {
                    if (exp.getInteger() < -0x8000 || exp.getInteger() > 0xFFFF) {
                        throw new CompilerException("Word value must range from -$8000 to $FFFF", exp.getExpression().getData());
                    }
                    byte[] value = getBytes(exp.getInteger());
                    for (int i = 0; i < exp.getCount(); i++) {
                        os.write(value[0]);
                        os.write(value[1]);
                    }
                }
            } catch (CompilerException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return os.toByteArray();
        }

    }

}
