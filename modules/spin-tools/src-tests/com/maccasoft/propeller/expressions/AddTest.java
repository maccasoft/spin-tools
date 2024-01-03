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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AddTest {

    @Test
    void testStringNumberValues() {
        Expression subject = new Add(new CharacterLiteral("1234"), new NumberLiteral(0x180));
        Assertions.assertEquals("00000031 00000032 00000033 000001B4", str(subject.getStringValues()));
    }

    @Test
    void testNumberStringValues() {
        Expression subject = new Add(new NumberLiteral(0x180), new CharacterLiteral("1234"));
        Assertions.assertEquals("000001B1 00000032 00000033 00000034", str(subject.getStringValues()));
    }

    @Test
    void testStringStringValues() {
        Expression subject = new Add(new CharacterLiteral("1234"), new CharacterLiteral("5678"));
        Assertions.assertEquals("00000031 00000032 00000033 00000069 00000036 00000037 00000038", str(subject.getStringValues()));
    }

    String str(int[] b) {
        StringBuilder sb = new StringBuilder();

        int i = 0;
        if (i < b.length) {
            sb.append(String.format("%08X", b[i++]));
            while (i < b.length) {
                sb.append(String.format(" %08X", b[i++]));
            }
        }

        return sb.toString();
    }

}
