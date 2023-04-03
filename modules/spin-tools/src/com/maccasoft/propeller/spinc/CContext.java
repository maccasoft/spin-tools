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

import java.util.List;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.model.Token;

public interface CContext extends Context {

    public abstract void addDefinition(String identifier, Expression expression);

    public abstract void addDefinition(String identifier, List<Token> definition);

    public abstract boolean isDefined(String identifier);

    public abstract List<Token> getDefinition(String identifier);

}
