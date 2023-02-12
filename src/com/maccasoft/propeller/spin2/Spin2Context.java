/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.ContextLiteral;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.MemoryContextLiteral;
import com.maccasoft.propeller.expressions.ObjectContextLiteral;

public class Spin2Context implements Context {

    final Spin2Context parent;
    final boolean caseSensitive;

    Map<String, Expression> symbols = new HashMap<>();
    Map<String, Expression> caseInsensitivesymbols = new CaseInsensitiveMap<>();

    Integer address;
    Integer objectAddress;
    Integer memoryAddress;

    public Spin2Context() {
        this(null, false);
    }

    public Spin2Context(boolean caseSensitive) {
        this(null, caseSensitive);
    }

    public Spin2Context(Spin2Context parent) {
        this(parent, parent.isCaseSensitive());
    }

    Spin2Context(Spin2Context parent, boolean caseSensitive) {
        this.parent = parent;
        this.caseSensitive = caseSensitive;
        caseInsensitivesymbols.put("$", new ContextLiteral(this));
        caseInsensitivesymbols.put("@$", new ObjectContextLiteral(this));
        caseInsensitivesymbols.put("@@$", new MemoryContextLiteral(this));
    }

    public Spin2Context getParent() {
        return parent;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void addBuiltinSymbol(String name, Expression value) {
        if (caseInsensitivesymbols.containsKey(name)) {
            throw new RuntimeException("symbol " + name + " already defined");
        }
        caseInsensitivesymbols.put(name, value);
    }

    public void addSymbol(String name, Expression value) {
        if (caseInsensitivesymbols.containsKey(name)) {
            throw new RuntimeException("symbol " + name + " already defined");
        }
        if (caseSensitive) {
            if (symbols.containsKey(name)) {
                throw new RuntimeException("symbol " + name + " already defined");
            }
            symbols.put(name, value);
        }
        else {
            caseInsensitivesymbols.put(name, value);
        }
    }

    public void addOrUpdateSymbol(String name, Expression value) {
        if (caseSensitive) {
            symbols.put(name, value);
        }
        else {
            caseInsensitivesymbols.put(name, value);
        }
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
        Expression exp = caseInsensitivesymbols.get(name);
        if (exp == null && caseSensitive) {
            exp = symbols.get(name);
        }
        if (exp == null && parent != null) {
            exp = parent.getLocalSymbol(name);
        }
        return exp;
    }

    @Override
    public boolean hasSymbol(String name) {
        boolean result = caseInsensitivesymbols.containsKey(name);
        if (result == false && caseSensitive) {
            result = symbols.containsKey(name);
        }
        if (result == false && parent != null) {
            result = parent.hasSymbol(name);
        }
        return result;
    }

    public int getInteger(String name) {
        Expression result = getSymbol(name);
        return result.getNumber().intValue();
    }

    public void setAddress(int address) {
        this.address = address;
    }

    @Override
    public boolean isAddressSet() {
        return address != null;
    }

    @Override
    public int getAddress() {
        if (address == null) {
            throw new RuntimeException("address not set");
        }
        return address;
    }

    @Override
    public int getObjectAddress() {
        if (objectAddress == null) {
            throw new RuntimeException("object address not set");
        }
        return objectAddress;
    }

    public void setObjectAddress(int address) {
        this.objectAddress = address;
    }

    @Override
    public int getMemoryAddress() {
        if (memoryAddress == null) {
            throw new RuntimeException("memory address not set");
        }
        return memoryAddress;
    }

    public void setMemoryAddress(int address) {
        this.memoryAddress = address;
    }

}
