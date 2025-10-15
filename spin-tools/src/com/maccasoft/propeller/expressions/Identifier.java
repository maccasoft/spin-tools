/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.expressions;

import com.maccasoft.propeller.CompilerException;

public class Identifier extends Expression {

    String name;
    Context context;
    Expression defaultValue;

    private boolean evaluating;

    public Identifier(String name, Context context) {
        this.name = name;
        this.context = context;
    }

    public Identifier(String name, Context context, Number defaultValue) {
        this.name = name;
        this.context = context;
        this.defaultValue = defaultValue != null ? new NumberLiteral(defaultValue) : null;
    }

    public String getName() {
        return name;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public boolean isConstant() {
        if (evaluating) {
            throw new CompilerException("illegal circular reference", getData());
        }
        try {
            evaluating = true;
            return resolve().isConstant();
        } finally {
            evaluating = false;
        }
    }

    @Override
    public boolean isNumber() {
        if (evaluating) {
            throw new CompilerException("illegal circular reference", getData());
        }
        try {
            evaluating = true;
            return resolve().isNumber();
        } finally {
            evaluating = false;
        }
    }

    @Override
    public Number getNumber() {
        if (evaluating) {
            throw new CompilerException("illegal circular reference", getData());
        }
        try {
            evaluating = true;
            return resolve().getNumber();
        } finally {
            evaluating = false;
        }
    }

    @Override
    public boolean isString() {
        if (evaluating) {
            throw new CompilerException("illegal circular reference", getData());
        }
        try {
            evaluating = true;
            return resolve().isString();
        } finally {
            evaluating = false;
        }
    }

    @Override
    public String getString() {
        if (evaluating) {
            throw new CompilerException("illegal circular reference", getData());
        }
        try {
            evaluating = true;
            return resolve().getString();
        } finally {
            evaluating = false;
        }
    }

    @Override
    public Expression resolve() {
        if (!context.hasSymbol(name)) {
            if (defaultValue != null) {
                return defaultValue;
            }
            throw new CompilerException("symbol not found", getData());
        }
        return context.getSymbol(name);
    }

    @Override
    public String toString() {
        return name;
    }

}
