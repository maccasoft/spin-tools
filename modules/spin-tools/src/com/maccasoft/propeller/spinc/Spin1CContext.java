/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin1.Spin1Context;

public class Spin1CContext extends Spin1Context implements CContext {

    Map<String, List<Token>> defines;

    public Spin1CContext() {
        super(true);
        this.defines = new HashMap<>();
    }

    public Spin1CContext(Spin1Context parent) {
        super(parent);
        this.defines = new HashMap<>();
    }

    @Override
    public void addDefinition(String identifier, Expression expression) {
        defines.put(identifier, Collections.emptyList());
        addSymbol(identifier, expression);
    }

    @Override
    public void addDefinition(String identifier, List<Token> definition) {
        defines.put(identifier, definition);
    }

    @Override
    public boolean isDefined(String identifier) {
        return defines.containsKey(identifier);
    }

    @Override
    public List<Token> getDefinition(String identifier) {
        List<Token> result = defines.get(identifier);
        if (result == null && (getParent() instanceof Spin1CContext)) {
            result = ((Spin1CContext) getParent()).getDefinition(identifier);
        }
        return result;
    }

}
