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

import java.util.Collections;
import java.util.List;

import com.maccasoft.propeller.expressions.Expression;

public abstract class Spin2PAsmInstructionFactory extends Expression {

    public Spin2PAsmInstructionFactory() {
    }

    public List<Spin2PAsmLine> expand(Spin2PAsmLine line) {
        return Collections.singletonList(line);
    }

    public abstract Spin2InstructionObject createObject(Spin2Context context, String condition, List<Spin2PAsmExpression> arguments, String effect);

}
