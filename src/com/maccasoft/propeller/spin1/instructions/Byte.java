/*
 * Copyright (c) 2021 Marco Maccaferri and others.
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

import com.maccasoft.propeller.spin1.Spin1Context;
import com.maccasoft.propeller.spin1.Spin1InstructionObject;
import com.maccasoft.propeller.spin1.Spin1PAsmExpression;
import com.maccasoft.propeller.spin1.Spin1PAsmInstructionFactory;

public class Byte extends Spin1PAsmInstructionFactory {

    @Override
    public Spin1InstructionObject createObject(Spin1Context context, String condition, List<Spin1PAsmExpression> arguments, String effect) {
        return new Byte_(context, arguments);
    }

    public class Byte_ extends Spin1InstructionObject {

        List<Spin1PAsmExpression> arguments;

        public Byte_(Spin1Context context, List<Spin1PAsmExpression> arguments) {
            super(context);
            this.arguments = arguments;
        }

        @Override
        public int getSize() {
            int size = 0;
            for (Spin1PAsmExpression exp : arguments) {
                size += exp.getCount();
            }
            return size;
        }

        @Override
        public byte[] getBytes() {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                for (Spin1PAsmExpression exp : arguments) {
                    int value = exp.getInteger();
                    for (int i = 0; i < exp.getCount(); i++) {
                        os.write(value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return os.toByteArray();
        }

    }

}
