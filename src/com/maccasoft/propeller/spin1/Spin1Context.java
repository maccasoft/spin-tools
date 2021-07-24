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

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.HubContextLiteral;

public class Spin1Context implements Context {

    Spin1Context parent;

    Map<String, Expression> symbols = new HashMap<String, Expression>();

    int address;
    boolean set;

    int hubAddress;
    int objectOffset;

    public Spin1Context() {

    }

    public Spin1Context(Spin1Context parent) {
        this.parent = parent;
        symbols.put("$", new ContextLiteral(this));
        symbols.put("@$", new HubContextLiteral(this));
    }

    public Spin1Context getParent() {
        return parent;
    }

    public void addSymbol(String name, Expression value) {
        if (symbols.containsKey(name)) {
            throw new RuntimeException("symbol " + name + " already defined");
        }
        symbols.put(name, value);
    }

    @Override
    public Expression getSymbol(String name) {
        Expression exp = getLocalSymbol(name);
        if (exp == null) {
            throw new RuntimeException("symbol " + name + " not found!");
        }
        return exp;
    }

    public Expression getLocalSymbol(String name) {
        Expression exp = symbols.get(name);
        if (exp == null && parent != null) {
            exp = parent.getLocalSymbol(name);
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
        return result.getNumber().intValue();
    }

    @Override
    public boolean isAddressSet() {
        return set;
    }

    public void setAddress(int address) {
        this.address = address;
        this.set = true;
    }

    @Override
    public int getAddress() {
        return address;
    }

    @Override
    public int getHubAddress() {
        return hubAddress;
    }

    public void setHubAddress(int hubAddress) {
        this.hubAddress = hubAddress;
    }

    @Override
    public int getObjectOffset() {
        return objectOffset;
    }

    public void setObjectOffset(int objectOffset) {
        this.objectOffset = objectOffset;
    }

}
