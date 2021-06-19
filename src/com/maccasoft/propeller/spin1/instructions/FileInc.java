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
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import com.maccasoft.propeller.spin1.Spin1Context;
import com.maccasoft.propeller.spin1.Spin1InstructionObject;
import com.maccasoft.propeller.spin1.Spin1PAsmExpression;
import com.maccasoft.propeller.spin1.Spin1PAsmInstructionFactory;

public class FileInc extends Spin1PAsmInstructionFactory {

    @Override
    public Spin1InstructionObject createObject(Spin1Context context, String condition, List<Spin1PAsmExpression> arguments, String effect) {
        return new FileInc_(context, arguments.get(0));
    }

    public class FileInc_ extends Spin1InstructionObject {

        Spin1PAsmExpression arg;

        public FileInc_(Spin1Context context, Spin1PAsmExpression arg) {
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
                e.printStackTrace();
            }
            return os.toByteArray();
        }

    }

}
