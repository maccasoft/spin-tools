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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.Formatter;

class Spin1FormatterTest {

    @Test
    void testPAsmLocalLabel1() {
        Formatter subject = new Spin1Formatter();
        String text = subject.format(""
            + "DAT\n"
            + "label\n"
            + ":l1 nop\n"
            + " mov a, #:l1\n"
            + "");
        Assertions.assertEquals(""
            + "DAT\n"
            + "label\n"
            + ":l1             nop\n"
            + "                mov     a, #:l1\n"
            + "", text);
    }

}
