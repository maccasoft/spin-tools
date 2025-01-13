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

public class CharacterLiteral extends Literal {

    private final String str;

    public CharacterLiteral(String str) {
        this.str = str;
    }

    @Override
    public boolean isString() {
        return str.length() > 1;
    }

    @Override
    public String getString() {
        return str;
    }

    @Override
    public int[] getStringValues() {
        byte[] b = str.getBytes();

        int[] r = new int[b.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = b[i] & 0xFF;
        }

        return r;
    }

    @Override
    public boolean isNumber() {
        return str.length() == 1;
    }

    @Override
    public Number getNumber() {
        return Long.valueOf(str.charAt(0));
    }

    @Override
    public String toString() {
        String escaped = str;
        escaped = escaped.replace("\\", "\\\\");
        escaped = escaped.replace("\'", "\\\'");
        escaped = escaped.replace("\0", "\\0");
        escaped = escaped.replace("\7", "\\a");
        escaped = escaped.replace("\t", "\\t");
        escaped = escaped.replace("\n", "\\n");
        escaped = escaped.replace("\f", "\\f");
        escaped = escaped.replace("\r", "\\r");
        escaped = escaped.replace("\33", "\\e");
        return "\"" + escaped + "\"";
    }

}
