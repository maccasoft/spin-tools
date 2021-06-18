/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import com.maccasoft.propeller.expressions.Expression;

public class Spin2PAsmExpression {

    final String prefix;
    final Expression expression;
    final Expression count;

    public Spin2PAsmExpression(String prefix, Expression expression, Expression count) {
        this.prefix = prefix;
        this.expression = expression;
        this.count = count;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isLiteral() {
        return prefix != null && prefix.startsWith("#");
    }

    public boolean isLongLiteral() {
        return prefix != null && prefix.startsWith("##");
    }

    public boolean isAbsolute() {
        return prefix != null && prefix.endsWith("\\");
    }

    public Expression getExpression() {
        return expression;
    }

    public int getCount() {
        if (count == null) {
            return 1;
        }
        return count.getNumber().intValue();
    }

    public int getInteger() {
        return expression.getNumber().intValue();
    }

    public byte[] getByte() {
        int value = expression.getNumber().intValue();
        return new byte[] {
            (byte) (value & 0xFF)
        };
    }

    public byte[] getWord() {
        int value = expression.getNumber().intValue();
        return new byte[] {
            (byte) (value & 0xFF),
            (byte) ((value >> 8) & 0xFF)
        };
    }

    public byte[] getLong() {
        int value;
        if (expression.getNumber() instanceof Double) {
            value = Float.floatToIntBits(expression.getNumber().floatValue());
        }
        else {
            value = expression.getNumber().intValue();
        }
        return new byte[] {
            (byte) (value & 0xFF),
            (byte) ((value >> 8) & 0xFF),
            (byte) ((value >> 16) & 0xFF),
            (byte) ((value >> 24) & 0xFF)
        };
    }

    @Override
    public String toString() {
        return (prefix != null ? prefix : "") + expression.toString() + (count != null ? "[" + count.toString() + "]" : "");
    }

}
