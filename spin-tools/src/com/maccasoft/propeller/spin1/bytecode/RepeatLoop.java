/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1.bytecode;

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Context;

public class RepeatLoop extends Jmp {

    public RepeatLoop(Context context, boolean step, Expression expression) {
        super(context, step ? 0x06 : 0x02, expression);
    }

    @Override
    public String toString() {
        return toString("REPEAT-JMP");
    }

}
