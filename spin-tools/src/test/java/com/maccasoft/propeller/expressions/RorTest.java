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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RorTest {

    @Test
    void testBit1() {
        NumberLiteral term1 = new NumberLiteral(1);
        NumberLiteral term2 = new NumberLiteral(4);
        Ror subject = new Ror(term1, term2);
        Assertions.assertEquals(1 << (31 - 3), subject.getNumber().longValue());
    }

    @Test
    void testBit31() {
        NumberLiteral term1 = new NumberLiteral(1 << 31);
        NumberLiteral term2 = new NumberLiteral(4);
        Ror subject = new Ror(term1, term2);
        Assertions.assertEquals(1 << 27, subject.getNumber().longValue());
    }

}
