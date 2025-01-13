/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Type;

public class Spin1PAsmExpression {

    final String prefix;
    final Expression expression;
    final Expression count;

    public Spin1PAsmExpression(String prefix, Expression expression, Expression count) {
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
        if (expression.getNumber() instanceof Double) {
            return Float.floatToIntBits(expression.getNumber().floatValue());
        }
        return expression.getNumber().intValue();
    }

    public byte[] getByte() {
        int[] value;
        if (expression.isString()) {
            value = expression.getStringValues();
        }
        else if (expression.getNumber() instanceof Double) {
            value = new int[] {
                Float.floatToIntBits(expression.getNumber().floatValue())
            };
        }
        else {
            value = new int[] {
                expression.getNumber().intValue()
            };
        }
        if (expression instanceof Type) {
            switch (((Type) expression).getType().toUpperCase()) {
                case "WORD": {
                    byte[] r = new byte[value.length * 2];
                    for (int s = 0, d = 0; s < value.length; s++) {
                        r[d++] = (byte) value[s];
                        r[d++] = (byte) (value[s] >> 8);
                    }
                    return r;
                }
                case "LONG": {
                    byte[] r = new byte[value.length * 4];
                    for (int s = 0, d = 0; s < value.length; s++) {
                        r[d++] = (byte) value[s];
                        r[d++] = (byte) (value[s] >> 8);
                        r[d++] = (byte) (value[s] >> 16);
                        r[d++] = (byte) (value[s] >> 24);
                    }
                    return r;
                }
            }
        }
        byte[] r = new byte[value.length];
        for (int s = 0, d = 0; s < value.length; s++) {
            r[d++] = (byte) value[s];
        }
        return r;
    }

    public byte[] getWord() {
        int[] value;
        if (expression.isString()) {
            value = expression.getStringValues();
        }
        else if (expression.getNumber() instanceof Double) {
            value = new int[] {
                Float.floatToIntBits(expression.getNumber().floatValue())
            };
        }
        else {
            value = new int[] {
                expression.getNumber().intValue()
            };
        }
        if (expression instanceof Type) {
            switch (((Type) expression).getType().toUpperCase()) {
                case "LONG": {
                    byte[] r = new byte[value.length * 4];
                    for (int s = 0, d = 0; s < value.length; s++) {
                        r[d++] = (byte) value[s];
                        r[d++] = (byte) (value[s] >> 8);
                        r[d++] = (byte) (value[s] >> 16);
                        r[d++] = (byte) (value[s] >> 24);
                    }
                    return r;
                }
            }
        }

        byte[] r = new byte[value.length * 2];
        for (int s = 0, d = 0; s < value.length; s++) {
            r[d++] = (byte) value[s];
            r[d++] = (byte) (value[s] >> 8);
        }
        return r;
    }

    public byte[] getLong() {
        int[] value;
        if (expression.isString()) {
            value = expression.getStringValues();
        }
        else if (expression.getNumber() instanceof Double) {
            value = new int[] {
                Float.floatToIntBits(expression.getNumber().floatValue())
            };
        }
        else {
            value = new int[] {
                expression.getNumber().intValue()
            };
        }
        byte[] r = new byte[value.length * 4];
        for (int s = 0, d = 0; s < value.length; s++) {
            r[d++] = (byte) value[s];
            r[d++] = (byte) (value[s] >> 8);
            r[d++] = (byte) (value[s] >> 16);
            r[d++] = (byte) (value[s] >> 24);
        }
        return r;
    }

    public String getString() {
        return expression.getString();
    }

    @Override
    public String toString() {
        return (prefix != null ? prefix : "") + expression.toString() + (count != null ? "[" + count.toString() + "]" : "");
    }

}
