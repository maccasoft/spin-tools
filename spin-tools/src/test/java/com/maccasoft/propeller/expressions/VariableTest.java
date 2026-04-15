/*
 * Copyright (c) 2021-2024 Marco Maccaferri and others.
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

class VariableTest {

    @Test
    void testSimpleType() {
        Variable subject = new Variable("BYTE", "a", 1, 0, false);
        Assertions.assertEquals(1, subject.getTypeSize());

        subject = new Variable("WORD", "a", 1, 0, false);
        Assertions.assertEquals(2, subject.getTypeSize());
        subject = new Variable("SHORT", "a", 1, 0, false);
        Assertions.assertEquals(2, subject.getTypeSize());

        subject = new Variable("LONG", "a", 1, 0, false);
        Assertions.assertEquals(4, subject.getTypeSize());
        subject = new Variable("INT", "a", 1, 0, false);
        Assertions.assertEquals(4, subject.getTypeSize());
    }

    @Test
    void testAlignedDefinition() {
        Variable subject = new Variable("struct", "data", 1, 0, true);
        subject.addMember("INT", "a", 1);
        subject.addMember("WORD", "b", 1);
        subject.addMember("WORD", "c", 1);
        subject.addMember("BYTE", "d", 1);
        subject.addMember("BYTE", "e", 1);
        subject.addMember("BYTE", "f", 1);
        subject.addMember("BYTE", "g", 1);

        Assertions.assertEquals(0, subject.getOffset());
        Assertions.assertEquals(12, subject.getTypeSize());

        Assertions.assertEquals(0, subject.getMember("a").getOffset());
        Assertions.assertEquals(4, subject.getMember("b").getOffset());
        Assertions.assertEquals(6, subject.getMember("c").getOffset());
        Assertions.assertEquals(8, subject.getMember("d").getOffset());
        Assertions.assertEquals(9, subject.getMember("e").getOffset());
        Assertions.assertEquals(10, subject.getMember("f").getOffset());
        Assertions.assertEquals(11, subject.getMember("g").getOffset());
    }

    @Test
    void testUnalignedDefinition() {
        Variable subject = new Variable("struct", "data", 1, 0, true);
        subject.addMember("BYTE", "a", 1);
        subject.addMember("BYTE", "b", 1);
        subject.addMember("INT", "c", 1);
        subject.addMember("WORD", "d", 1);
        subject.addMember("BYTE", "e", 1);
        subject.addMember("WORD", "f", 1);
        subject.addMember("BYTE", "g", 1);

        Assertions.assertEquals(0, subject.getOffset());
        Assertions.assertEquals(15, subject.getTypeSize());

        Assertions.assertEquals(0, subject.getMember("a").getOffset());
        Assertions.assertEquals(1, subject.getMember("b").getOffset());
        Assertions.assertEquals(4, subject.getMember("c").getOffset());
        Assertions.assertEquals(8, subject.getMember("d").getOffset());
        Assertions.assertEquals(10, subject.getMember("e").getOffset());
        Assertions.assertEquals(12, subject.getMember("f").getOffset());
        Assertions.assertEquals(14, subject.getMember("g").getOffset());
    }

    @Test
    void testElementsOffset() {
        Variable subject = new Variable("struct", "data", 1, 10, false);
        subject.addMember("INT", "a", 1);
        subject.addMember("WORD", "b", 1);
        subject.addMember("WORD", "c", 1);

        Assertions.assertEquals(10, subject.getOffset());
        Assertions.assertEquals(8, subject.getTypeSize());

        Assertions.assertEquals(10 + 0, subject.getMember("a").getOffset());
        Assertions.assertEquals(10 + 4, subject.getMember("b").getOffset());
        Assertions.assertEquals(10 + 6, subject.getMember("c").getOffset());
    }

    @Test
    void testArrayElementsOffset() {
        Variable subject = new Variable("struct", "data", 2, 10, false);
        subject.addMember("INT", "a", 1);
        subject.addMember("WORD", "b", 1);
        subject.addMember("WORD", "c", 1);

        Assertions.assertEquals(10, subject.getOffset());
        Assertions.assertEquals(8, subject.getTypeSize());

        Assertions.assertEquals(10 + 0, subject.getMember("a").getOffset());
        Assertions.assertEquals(10 + 4, subject.getMember("b").getOffset());
        Assertions.assertEquals(10 + 6, subject.getMember("c").getOffset());
    }

}
