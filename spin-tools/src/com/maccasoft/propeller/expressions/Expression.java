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

import java.util.HashMap;
import java.util.Map;

public abstract class Expression {

    protected Object data;
    protected Map<String, Object> keyedData = new HashMap<String, Object>();

    public static Expression fold(Expression expression) {
        if (expression instanceof NumberLiteral) {
            return expression;
        }

        try {
            if (expression instanceof BinaryOperator) {
                Expression term1 = ((BinaryOperator) expression).getTerm1();
                if (!(term1 instanceof NumberLiteral)) {
                    term1 = fold(term1);
                }
                Expression term2 = ((BinaryOperator) expression).getTerm2();
                if (!(term2 instanceof NumberLiteral)) {
                    term2 = fold(term2);
                }
                if (expression instanceof Add) {
                    Expression result = new Add(term1, term2);
                    if ((term1 instanceof NumberLiteral) && (term2 instanceof NumberLiteral)) {
                        return new NumberLiteral(result.getNumber());
                    }
                    return result;
                }
                else if (expression instanceof Multiply) {
                    Expression result = new Multiply(term1, term2);
                    if ((term1 instanceof NumberLiteral) && (term2 instanceof NumberLiteral)) {
                        return new NumberLiteral(result.getNumber());
                    }
                    return result;
                }
            }
        } catch (Exception e) {
            // Do nothing, fall-through
        }

        return expression;
    }

    public Expression resolve() {
        return this;
    }

    public boolean isConstant() {
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public Number getNumber() {
        throw new RuntimeException("not a number");
    }

    public byte[] getByte() {
        return new byte[] {
            getNumber().byteValue()
        };
    }

    public byte[] getWord() {
        int value = getNumber().intValue();
        return new byte[] {
            (byte) (value & 0xFF),
            (byte) ((value >> 8) & 0xFF)
        };
    }

    public byte[] getLong() {
        int value = getNumber().intValue();
        return new byte[] {
            (byte) (value & 0xFF),
            (byte) ((value >> 8) & 0xFF),
            (byte) ((value >> 16) & 0xFF),
            (byte) ((value >> 24) & 0xFF)
        };
    }

    public boolean isString() {
        return false;
    }

    public String getString() {
        throw new RuntimeException("not a string");
    }

    public int[] getStringValues() {
        throw new RuntimeException("unsupported operation");
    }

    public Expression getElement() {
        return getElement(0);
    }

    public Expression getElement(int index) {
        return index == 0 ? this : null;
    }

    public Expression getNext() {
        return null;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData(String key) {
        return keyedData.get(key);
    }

    public void setData(String key, Object data) {
        this.keyedData.put(key, data);
    }

}
