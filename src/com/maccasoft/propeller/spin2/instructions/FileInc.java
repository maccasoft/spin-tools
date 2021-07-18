/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.instructions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import com.maccasoft.propeller.spin2.Spin2Context;
import com.maccasoft.propeller.spin2.Spin2InstructionObject;
import com.maccasoft.propeller.spin2.Spin2PAsmExpression;
import com.maccasoft.propeller.spin2.Spin2PAsmInstructionFactory;

public class FileInc extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        return new FileInc_(context, arguments.get(0));
    }

    public class FileInc_ extends Spin2InstructionObject {

        Spin2PAsmExpression arg;

        public FileInc_(Spin2Context context, Spin2PAsmExpression arg) {
            super(context);
            this.arg = arg;
        }

        @Override
        public int resolve(int address) {
            context.setAddress(address);
            return address + getSize();
        }

        @Override
        public int getSize() {
            File file = new File(arg.toString());
            return (int) file.length();
        }

        @Override
        public byte[] getBytes() {
            int rc;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                FileInputStream is = new FileInputStream(arg.toString());
                do {
                    rc = is.read(buffer);
                    os.write(buffer, 0, rc);
                } while (rc > 0);
                is.close();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return os.toByteArray();
        }

    }

}
