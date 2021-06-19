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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.BitField;

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.spin1.bytecode.VarAssignAdd;
import com.maccasoft.propeller.spin1.bytecode.VarWrite;

public abstract class Spin1BytecodeFactory {

    public static final BitField xxx = new BitField(0b000_111_00);

    static Map<String, Spin1BytecodeFactory> symbols = new HashMap<String, Spin1BytecodeFactory>();
    static {
        symbols.put(":=", new VarWrite());
        symbols.put("+=", new VarAssignAdd());
    }

    public abstract Spin1BytecodeObject createObject(Spin1Context context, Expression expression);

}
