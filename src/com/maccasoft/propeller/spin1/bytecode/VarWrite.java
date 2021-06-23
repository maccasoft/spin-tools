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
import java.util.Collections;
import java.util.List;

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.spin1.Spin1BytecodeExpression;
import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionFactory;
import com.maccasoft.propeller.spin1.Spin1BytecodeInstructionObject;
import com.maccasoft.propeller.spin1.Spin1BytecodeLine;
import com.maccasoft.propeller.spin1.Spin1Context;

public class VarWrite extends Spin1BytecodeInstructionFactory {

    @Override
    public Spin1BytecodeInstructionObject createObject(Spin1BytecodeLine line) {
        return new VarWrite_(line.getScope(), new com.maccasoft.propeller.expressions.Identifier(line.getArgument(0).getText(), line.getScope()));
    }

    @Override
    public List<Spin1BytecodeLine> expand(Spin1BytecodeLine line) {
        List<Spin1BytecodeLine> list = new ArrayList<Spin1BytecodeLine>();

        for (int i = 1; i < line.getArgumentCount(); i++) {
            Spin1BytecodeExpression expression = line.getArgument(i);
            Spin1BytecodeLine newLine = new Spin1BytecodeLine(new Spin1Context(line.getScope()), null, expression.getText(), expression.getChilds());
            list.addAll(newLine.expand());
        }

        list.add(new Spin1BytecodeLine(line.getScope(), line.getLabel(), line.getMnemonic(), Collections.singletonList(line.getArgument(0))));

        return list;
    }

    class VarWrite_ extends Spin1BytecodeInstructionObject {

        Expression expression;

        public VarWrite_(Spin1Context context, Expression expression) {
            super(context);
            this.expression = expression;
        }

        @Override
        public byte[] getBytes() {
            int value = xxx.setValue(0b01_1_000_01, expression.getNumber().intValue() / 4);
            return new byte[] {
                (byte) value
            };
        }

        @Override
        public String toString() {
            return "MEM_WRITE LONG DBASE+" + String.format("$%04X", expression.getNumber().intValue()) + " (short)";
        }

    }

}
