/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.maccasoft.propeller.expressions.Expression;

public abstract class Spin2PAsmInstructionFactory extends Expression {

    public Spin2PAsmInstructionFactory() {
    }

    public List<Spin2PAsmLine> expand(Spin2PAsmLine line) {
        return Collections.singletonList(line);
    }

    protected List<Spin2PAsmLine> augsExpand(Spin2PAsmLine line, Spin2PAsmExpression argument) {
        List<Spin2PAsmLine> list = new ArrayList<Spin2PAsmLine>();

        if (argument.isLongLiteral()) {
            list.add(new Spin2PAsmLine(
                line.getScope(), line.getLabel(), line.getCondition(), "augs",
                Collections.singletonList(argument),
                null));
        }
        list.add(line);

        return list;
    }

    protected List<Spin2PAsmLine> augdExpand(Spin2PAsmLine line, Spin2PAsmExpression argument) {
        List<Spin2PAsmLine> list = new ArrayList<Spin2PAsmLine>();

        if (argument.isLongLiteral()) {
            list.add(new Spin2PAsmLine(
                line.getScope(), line.getLabel(), line.getCondition(), "augd",
                Collections.singletonList(argument),
                null));
        }
        list.add(line);

        return list;
    }

    public abstract Spin2InstructionObject createObject(Spin2Context context, List<Spin2PAsmExpression> arguments);

}
