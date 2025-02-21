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
import java.io.IOException;
import java.util.List;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin2.Spin2InstructionObject;
import com.maccasoft.propeller.spin2.Spin2PAsmExpression;
import com.maccasoft.propeller.spin2.Spin2PAsmInstructionFactory;
import com.maccasoft.propeller.spin2.Spin2Struct;

public class DataType extends Spin2PAsmInstructionFactory {

    String type;

    public DataType(String type) {
        this.type = type;
    }

    @Override
    public Spin2InstructionObject createObject(Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        return new DataType_(context, arguments);
    }

    public class DataType_ extends Spin2InstructionObject {

        List<Spin2PAsmExpression> arguments;

        public DataType_(Context context, List<Spin2PAsmExpression> arguments) {
            super(context);
            this.arguments = arguments;
        }

        @Override
        public int getSize() {
            int size = 0;

            for (Spin2PAsmExpression exp : arguments) {
                size += exp.getByteSize();
            }

            Spin2Struct struct = context.getStructureDefinition(type);
            if (struct != null) {
                size = ((size + struct.getTypeSize() - 1) / struct.getTypeSize()) * struct.getTypeSize();
            }

            return size;
        }

        @Override
        public byte[] getBytes() {
            CompilerException msgs = new CompilerException();
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            for (Spin2PAsmExpression exp : arguments) {
                try {
                    byte[] value = exp.getByte();
                    for (int i = 0; i < exp.getCount(); i++) {
                        os.write(value);
                    }
                } catch (IOException e) {
                    // Do nothing
                } catch (CompilerException e) {
                    msgs.addMessage(e);
                } catch (Exception e) {
                    msgs.addMessage(new CompilerException(e.getMessage(), exp.getExpression().getData()));
                }
            }

            Spin2Struct struct = context.getStructureDefinition(type);
            if (struct != null) {
                int size = ((os.size() + struct.getTypeSize() - 1) / struct.getTypeSize()) * struct.getTypeSize();
                if (size - os.size() > 0) {
                    try {
                        os.write(new byte[size - os.size()]);
                    } catch (IOException e) {
                        // Do nothing
                    }
                }
            }

            if (msgs.hasChilds()) {
                throw msgs;
            }

            return os.toByteArray();
        }

    }

}
