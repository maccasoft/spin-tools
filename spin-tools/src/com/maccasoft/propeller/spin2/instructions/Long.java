/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
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

public class Long extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        return new Long_(context, arguments);
    }

    public class Long_ extends Spin2InstructionObject {

        List<Spin2PAsmExpression> arguments;

        public Long_(Context context, List<Spin2PAsmExpression> arguments) {
            super(context);
            this.arguments = arguments;
        }

        @Override
        public int getSize() {
            int size = 0;

            for (Spin2PAsmExpression exp : arguments) {
                size += exp.getLongSize();
            }

            return size;
        }

        @Override
        public byte[] getBytes() {
            CompilerException msgs = new CompilerException();
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            for (Spin2PAsmExpression exp : arguments) {
                try {
                    byte[] value = exp.getLong();

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
                    msgs.addMessage(new CompilerException(e.getMessage(), exp.getExpression().getData()));
                }
            }

            if (msgs.hasChilds()) {
                throw msgs;
            }

            return os.toByteArray();
        }

    }

}
