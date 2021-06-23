/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1.bytecode;

import java.util.ArrayList;
import java.util.List;

import com.maccasoft.propeller.spin1.Spin1BytecodeExpression;
import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionFactory;
import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionObject;
import com.maccasoft.propeller.spin1.Spin1BytecodeLine;
import com.maccasoft.propeller.spin1.Spin1Context;

public class Empty extends Spin1BytecodeInstructionFactory {

    public static final Spin1BytecodeInstructionFactory instance = new Empty();

    @Override
    public Spin1BytecodeInstructionObject createObject(Spin1BytecodeLine line) {
        return new Empty_(line.getScope());
    }

    public List<Spin1BytecodeLine> expand1(Spin1BytecodeLine line) {
        List<Spin1BytecodeLine> list = new ArrayList<Spin1BytecodeLine>();

        for (int i = 0; i < line.getArgumentCount(); i++) {
            Spin1BytecodeExpression expression = line.getArgument(i);
            Spin1BytecodeLine newLine = new Spin1BytecodeLine(line.getScope(), null, expression.getText(), expression.getChilds());
            list.addAll(newLine.expand());
        }

        return list;
    }

    class Empty_ extends Spin1BytecodeInstructionObject {

        public Empty_(Spin1Context context) {
            super(context);
        }

        @Override
        public byte[] getBytes() {
            return new byte[0];
        }

    }
}
