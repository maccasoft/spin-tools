/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.expressions;

import java.util.Objects;

public class NumberLiteral extends Literal {

    public static final NumberLiteral ZERO = new NumberLiteral(0);
    public static final NumberLiteral ONE = new NumberLiteral(1);

    private final Number value;
    private final int base;
    private String text;

    public NumberLiteral(long value) {
        this.value = value;
        this.base = 10;
    }

    public NumberLiteral(double value) {
        this.value = value;
        this.base = 10;
    }

    public NumberLiteral(long value, int base) {
        this.value = value;
        this.base = base;
    }

    public NumberLiteral(String s) {
        this.text = s;
        if (s.startsWith("%%")) {
            s = s.substring(2);
            this.base = 4;
        }
        else if (s.startsWith("%")) {
            s = s.substring(1);
            this.base = 2;
        }
        else if (s.startsWith("$")) {
            s = s.substring(1);
            this.base = 16;
        }
        else if (s.startsWith("0b")) {
            s = s.substring(2);
            this.base = 2;
        }
        else if (s.startsWith("0x")) {
            s = s.substring(2);
            this.base = 16;
        }
        else {
            this.base = 10;
        }
        if (this.base == 10 && (s.contains("e") || s.contains("E"))) {
            this.value = Double.parseDouble(s.replace("_", ""));
        }
        else if (s.contains(".")) {
            this.value = Double.parseDouble(s.replace("_", ""));
        }
        else {
            this.value = signx(Long.parseLong(s.replace("_", ""), this.base));
        }
    }

    public NumberLiteral(Number value) {
        this.value = value;
        this.base = 10;
    }

    private Long signx(Long number) {
        long cl = ~31L & 0x3F;
        long value = number.longValue();
        value = value << cl;
        return value >> cl;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public Number getNumber() {
        return value;
    }

    public int getBase() {
        return base;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NumberLiteral other = (NumberLiteral) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public String toString() {
        if (text == null) {
            switch (base) {
                case 2:
                    text = "%" + Long.toBinaryString(getNumber().longValue());
                    break;
                case 4:
                    text = "%%" + Long.toString(getNumber().longValue(), 4);
                    break;
                case 16:
                    text = "$" + Long.toHexString(getNumber().longValue());
                    break;
                default:
                    text = getNumber().toString();
                    break;
            }
        }
        return text;
    }

}
