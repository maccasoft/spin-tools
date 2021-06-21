/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import com.maccasoft.propeller.expressions.Expression;

public class Spin1ByteCodeLine {

    Spin1Context scope;
    Expression expression;

    Spin1BytecodeFactory instructionFactory;
    Spin1BytecodeObject instructionObject;

    public Spin1ByteCodeLine(Spin1Context scope) {
        this.scope = scope;
    }

    public Spin1Context getScope() {
        return scope;
    }

    public Spin1BytecodeFactory getInstructionFactory() {
        return instructionFactory;
    }

    public int resolve(int address) {
        if (instructionObject == null && instructionFactory != null) {
            instructionObject = instructionFactory.createObject(scope, expression);
        }
        if (instructionObject != null) {
            return instructionObject.resolve(address);
        }
        return address;
    }

    public Spin1BytecodeObject getInstructionObject() {
        return instructionObject;
    }

}
