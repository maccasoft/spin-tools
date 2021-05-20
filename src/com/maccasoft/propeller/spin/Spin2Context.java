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
import com.maccasoft.propeller.expressions.NumberLiteral;

public class Spin2Context implements Context {

    Spin2Context parent;

    Map<String, Expression> symbols = new HashMap<String, Expression>();

    int address;
    boolean set;

    public Spin2Context() {
    }

    public Spin2Context(Spin2Context parent) {
        this.parent = parent;
        symbols.put("$", new ContextLiteral(this));
    }

    public Spin2Context getParent() {
        return parent;
    }

    public void addSymbol(String name, Expression value) {
        if (symbols.containsKey(name)) {
            throw new RuntimeException("Symbol " + name + " already defined");
        }
        symbols.put(name, value);
    }

    @Override
    public Expression getSymbol(String name) {
        Expression exp = symbols.get(name);
        if (exp == null && parent != null) {
            exp = parent.getSymbol(name);
        }
        if (exp == null) {
            System.out.println("Symbol " + name + " not found!");
            exp = new NumberLiteral(0);
        }
        return exp;
    }

    @Override
    public boolean hasSymbol(String name) {
        boolean result = symbols.containsKey(name);
        if (result == false && parent != null) {
            result = parent.hasSymbol(name);
        }
        return result;
    }

    public int getInteger(String name) {
        Expression result = getSymbol(name);
        return result != null ? result.getNumber().intValue() : 0;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    @Override
    public int getAddress() {
        return address;
    }

}
