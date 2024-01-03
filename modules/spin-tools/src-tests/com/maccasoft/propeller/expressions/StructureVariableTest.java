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

class StructureVariableTest {

    @Test
    void testAlignedDefinition() {
        StructureVariable subject = new StructureVariable("struct", "data", 1, 0, true);
        subject.addVariable("INT", "a", 1);
        subject.addVariable("WORD", "b", 1);
        subject.addVariable("WORD", "c", 1);
        subject.addVariable("BYTE", "d", 1);
        subject.addVariable("BYTE", "e", 1);
        subject.addVariable("BYTE", "f", 1);
        subject.addVariable("BYTE", "g", 1);

        Assertions.assertEquals(0, subject.getOffset());
        Assertions.assertEquals(12, subject.getTypeSize());

        Assertions.assertEquals(0, subject.getVariable("a").getOffset());
        Assertions.assertEquals(4, subject.getVariable("b").getOffset());
        Assertions.assertEquals(6, subject.getVariable("c").getOffset());
        Assertions.assertEquals(8, subject.getVariable("d").getOffset());
        Assertions.assertEquals(9, subject.getVariable("e").getOffset());
        Assertions.assertEquals(10, subject.getVariable("f").getOffset());
        Assertions.assertEquals(11, subject.getVariable("g").getOffset());
    }

    @Test
    void testUnalignedDefinition() {
        StructureVariable subject = new StructureVariable("struct", "data", 1, 0, true);
        subject.addVariable("BYTE", "a", 1);
        subject.addVariable("BYTE", "b", 1);
        subject.addVariable("INT", "c", 1);
        subject.addVariable("WORD", "d", 1);
        subject.addVariable("BYTE", "e", 1);
        subject.addVariable("WORD", "f", 1);
        subject.addVariable("BYTE", "g", 1);

        Assertions.assertEquals(0, subject.getOffset());
        Assertions.assertEquals(16, subject.getTypeSize());

        Assertions.assertEquals(0, subject.getVariable("a").getOffset());
        Assertions.assertEquals(1, subject.getVariable("b").getOffset());
        Assertions.assertEquals(4, subject.getVariable("c").getOffset());
        Assertions.assertEquals(8, subject.getVariable("d").getOffset());
        Assertions.assertEquals(10, subject.getVariable("e").getOffset());
        Assertions.assertEquals(12, subject.getVariable("f").getOffset());
        Assertions.assertEquals(14, subject.getVariable("g").getOffset());
    }

}
