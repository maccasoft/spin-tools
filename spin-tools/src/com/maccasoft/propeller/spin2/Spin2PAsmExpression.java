/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.Type;
import com.maccasoft.propeller.spin2.bytecode.Constant;

public class Spin2PAsmExpression {

    final String prefix;
    final Expression expression;
    final Expression count;

    public static class PtrExpression extends Expression {

        String pre;
        String ptr;
        String post;
        boolean immediate;
        Expression index;

        public PtrExpression(String pre, String ptr, String post, boolean immediate, Expression index) {
            this.pre = pre;
            this.ptr = ptr;
            this.post = post;
            this.immediate = immediate;
            this.index = index;
        }

        public boolean isImmediate() {
            return immediate;
        }

        public Expression getIndex() {
            return index;
        }

        @Override
        public Number getNumber() {
            int result = immediate ? 0b000000000 : 0b100000000;

            if (immediate) {
                result |= 0b100000000000000000000000;
                if (ptr.equalsIgnoreCase("PTRB")) {
                    result |= 0b10000000000000000000000;
                }
                if (pre != null || post != null) {
                    result |= 0b001000000000000000000000;
                }
                if (post != null) {
                    result |= 0b000100000000000000000000;
                }
            }
            else {
                if (ptr.equalsIgnoreCase("PTRB")) {
                    result |= 0b010000000;
                }
                if (pre != null || post != null) {
                    result |= 0b001000000;
                }
                if (post != null) {
                    result |= 0b000100000;
                }
            }

            if (index != null) {
                int o = index.getNumber().intValue();
                if (immediate) {
                    if ("--".equals(pre) || "--".equals(post)) {
                        o = -o;
                    }
                    result |= o & 0b000011111111111111111111;
                }
                else {
                    if (o < -32 || o > 31) {
                        throw new CompilerException("constant out of range (-32 to 31)", index.getData());
                    }
                    if ("++".equals(pre) || "++".equals(post)) {
                        if (o < 0) {
                            o = 32 + o;
                        }
                        result |= o & 0x1F;
                    }
                    if ("--".equals(pre) || "--".equals(post)) {
                        o = -o;
                        if (o < 0) {
                            o = 32 + o;
                        }
                        result |= o & 0x1F;
                    }
                    else {
                        if (o < 0) {
                            o = 64 + o;
                        }
                        result |= o & 0x3F;
                    }
                }
            }
            else {
                if ("++".equals(pre) || "++".equals(post)) {
                    result |= 0b000000001;
                }
                else if ("--".equals(pre) || "--".equals(post)) {
                    result |= 0b000011111;
                }
            }

            return result;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (pre != null) {
                sb.append(pre);
            }
            sb.append(ptr);
            if (post != null) {
                sb.append(post);
            }
            if (index != null) {
                sb.append("[");
                if (immediate) {
                    sb.append("##");
                }
                sb.append(index.toString());
                sb.append("]");
            }
            return sb.toString();
        }

    }

    public Spin2PAsmExpression(String prefix, Expression expression, Expression count) {
        this.prefix = prefix;
        this.expression = expression;
        this.count = count;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isLiteral() {
        if (expression instanceof PtrExpression) {
            return false;
        }
        return prefix != null && prefix.startsWith("#");
    }

    public boolean isLongLiteral() {
        if (expression instanceof PtrExpression) {
            return ((PtrExpression) expression).isImmediate();
        }
        return prefix != null && prefix.startsWith("##");
    }

    public boolean isAbsolute() {
        return prefix != null && prefix.endsWith("\\");
    }

    public boolean isPtr() {
        return expression instanceof PtrExpression;
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
        int[] value = getValue();

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
                case "FVAR": {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        for (int i = 0; i < value.length; i++) {
                            os.write(Constant.wrVar(value[i]));
                        }
                    } catch (IOException e) {
                        // Do nothing
                    }
                    return os.toByteArray();
                }
                case "FVARS":
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        for (int i = 0; i < value.length; i++) {
                            os.write(Constant.wrVars(value[i]));
                        }
                    } catch (IOException e) {
                        // Do nothing
                    }
                    return os.toByteArray();
            }
        }

        byte[] r = new byte[value.length];
        for (int s = 0, d = 0; s < value.length; s++) {
            r[d++] = (byte) value[s];
        }
        return r;
    }

    public int getByteSize() {
        int[] value;
        try {
            value = getValue();
        } catch (Exception e) {
            value = new int[1];
        }

        int size = value.length;

        if (expression instanceof Type) {
            switch (((Type) expression).getType().toUpperCase()) {
                case "WORD":
                    size = value.length * 2;
                    break;

                case "LONG":
                    size = value.length * 4;
                    break;

                case "FVAR": {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        for (int i = 0; i < value.length; i++) {
                            os.write(Constant.wrVar(value[i]));
                        }
                        size = os.size();
                    } catch (IOException e) {
                        // Do nothing
                    }
                    break;
                }
                case "FVARS":
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        for (int i = 0; i < value.length; i++) {
                            os.write(Constant.wrVars(value[i]));
                        }
                        size = os.size();
                    } catch (IOException e) {
                        // Do nothing
                    }
                    break;
            }
        }

        int count;
        try {
            count = getCount();
        } catch (Exception e) {
            count = 1;
        }

        return size * count;
    }

    public byte[] getWord() {
        int[] value = getValue();

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
                case "FVAR": {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        for (int i = 0; i < value.length; i++) {
                            os.write(Constant.wrVar(value[i]));
                        }
                    } catch (IOException e) {
                        // Do nothing
                    }
                    return os.toByteArray();
                }
                case "FVARS":
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        for (int i = 0; i < value.length; i++) {
                            os.write(Constant.wrVars(value[i]));
                        }
                    } catch (IOException e) {
                        // Do nothing
                    }
                    return os.toByteArray();
            }
        }

        byte[] r = new byte[value.length * 2];
        for (int s = 0, d = 0; s < value.length; s++) {
            r[d++] = (byte) value[s];
            r[d++] = (byte) (value[s] >> 8);
        }
        return r;
    }

    public int getWordSize() {
        int[] value;
        try {
            value = getValue();
        } catch (Exception e) {
            value = new int[1];
        }

        int size = value.length * 2;

        if (expression instanceof Type) {
            switch (((Type) expression).getType().toUpperCase()) {
                case "LONG":
                    size = value.length * 4;
                    break;

                case "FVAR": {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        for (int i = 0; i < value.length; i++) {
                            os.write(Constant.wrVar(value[i]));
                        }
                        size = os.size();
                    } catch (IOException e) {
                        // Do nothing
                    }
                    break;
                }
                case "FVARS":
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        for (int i = 0; i < value.length; i++) {
                            os.write(Constant.wrVars(value[i]));
                        }
                        size = os.size();
                    } catch (IOException e) {
                        // Do nothing
                    }
                    break;
            }
        }

        int count;
        try {
            count = getCount();
        } catch (Exception e) {
            count = 1;
        }

        return size * count;
    }

    public byte[] getLong() {
        int[] value = getValue();

        if (expression instanceof Type) {
            switch (((Type) expression).getType().toUpperCase()) {
                case "FVAR": {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        for (int i = 0; i < value.length; i++) {
                            os.write(Constant.wrVar(value[i]));
                        }
                    } catch (IOException e) {
                        // Do nothing
                    }
                    return os.toByteArray();
                }
                case "FVARS":
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        for (int i = 0; i < value.length; i++) {
                            os.write(Constant.wrVars(value[i]));
                        }
                    } catch (IOException e) {
                        // Do nothing
                    }
                    return os.toByteArray();
            }
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

    public int getLongSize() {
        int[] value;
        try {
            value = getValue();
        } catch (Exception e) {
            value = new int[1];
        }

        int size = value.length * 4;

        if (expression instanceof Type) {
            switch (((Type) expression).getType().toUpperCase()) {
                case "FVAR": {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        for (int i = 0; i < value.length; i++) {
                            os.write(Constant.wrVar(value[i]));
                        }
                        size = os.size();
                    } catch (IOException e) {
                        // Do nothing
                    }
                    break;
                }
                case "FVARS":
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    try {
                        for (int i = 0; i < value.length; i++) {
                            os.write(Constant.wrVars(value[i]));
                        }
                        size = os.size();
                    } catch (IOException e) {
                        // Do nothing
                    }
                    break;
            }
        }

        int count;
        try {
            count = getCount();
        } catch (Exception e) {
            count = 1;
        }

        return size * count;
    }

    int[] getValue() {
        if (expression.isString()) {
            return expression.getStringValues();
        }
        if (expression.getNumber() instanceof Double) {
            return new int[] {
                Float.floatToIntBits(expression.getNumber().floatValue())
            };
        }
        return new int[] {
            expression.getNumber().intValue()
        };
    }

    public String getString() {
        return expression.getString();
    }

    @Override
    public String toString() {
        return (prefix != null ? prefix : "") + expression.toString() + (count != null ? "[" + count.toString() + "]" : "");
    }

}
