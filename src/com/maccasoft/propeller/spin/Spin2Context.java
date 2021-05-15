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

import java.util.HashMap;
import java.util.Map;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Expression;

public class Spin2Context implements Context {

    Spin2Context parent;

    Map<String, Expression> symbols = new HashMap<String, Expression>();

    int address;
    boolean set;

    public Spin2Context() {
    }

    public Spin2Context(Spin2Context parent) {
        this.parent = parent;
        addSymbol("$", new ContextLiteral(this));
    }

    public void addSymbol(String name, Expression value) {
        symbols.put(name.toLowerCase(), value);
    }

    @Override
    public Expression getSymbol(String name) {
        Expression exp = symbols.get(name.toLowerCase());
        if (exp == null && parent != null) {
            exp = parent.getSymbol(name);
        }
        return exp;
    }

    @Override
    public boolean hasSymbol(String name) {
        boolean result = symbols.containsKey(name.toLowerCase());
        if (result == false && parent != null) {
            result = parent.hasSymbol(name);
        }
        return result;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    @Override
    public int getAddress() {
        return address;
    }

}