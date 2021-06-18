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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.maccasoft.propeller.expressions.And;
import com.maccasoft.propeller.expressions.Divide;
import com.maccasoft.propeller.expressions.Identifier;
import com.maccasoft.propeller.expressions.Not;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.spin2.Spin2Context;
import com.maccasoft.propeller.spin2.Spin2InstructionObject;
import com.maccasoft.propeller.spin2.Spin2PAsmExpression;
import com.maccasoft.propeller.spin2.Spin2PAsmInstructionFactory;
import com.maccasoft.propeller.spin2.Spin2PAsmLine;

public class AsmClk extends Spin2PAsmInstructionFactory {

    @Override
    public Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect) {
        throw new RuntimeException("Invalid arguments");
    }

    @Override
    public List<Spin2PAsmLine> expand(Spin2PAsmLine line) {
        List<Spin2PAsmLine> list = new ArrayList<Spin2PAsmLine>();

        list.add(new Spin2PAsmLine(
            new Spin2Context(line.getScope()), line.getLabel(), line.getCondition(), "hubset",
            Collections.singletonList(new Spin2PAsmExpression("##",
                new And(
                    new Identifier("clkmode_", line.getScope()),
                    new Not(new NumberLiteral(0b11, 2))),
                null)),
            null));

        list.add(new Spin2PAsmLine(
            new Spin2Context(line.getScope()), null, line.getCondition(), "waitx",
            Collections.singletonList(new Spin2PAsmExpression("##",
                new Divide(
                    new NumberLiteral(20000000),
                    new NumberLiteral(100)),
                null)),
            null));

        list.add(new Spin2PAsmLine(
            new Spin2Context(line.getScope()), null, line.getCondition(), "hubset",
            Collections.singletonList(new Spin2PAsmExpression("##", new Identifier("clkmode_", line.getScope()), null)),
            null));

        return list;
    }
}
