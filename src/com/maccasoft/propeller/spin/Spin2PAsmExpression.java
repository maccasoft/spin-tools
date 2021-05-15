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

import com.maccasoft.propeller.expressions.Expression;

public class Spin2PAsmExpression {

    final String prefix;
    final Expression expression;

    public Spin2PAsmExpression(String prefix, Expression expression) {
        this.prefix = prefix;
        this.expression = expression;
    }

    public String getPrefix() {
        return prefix;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return (prefix != null ? prefix : "") + expression.toString();
    }

}
