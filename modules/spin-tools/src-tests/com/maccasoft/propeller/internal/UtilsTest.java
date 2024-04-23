/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UtilsTest {

    @Test
    void testSimplePattern() {
        String subject = "" +
            "a\n" +
            "a\n" +
            "|\n" +
            "a\n" +
            " \n" +
            "|\n" +
            "a\n" +
            "";
        Assertions.assertEquals("%01_0100", Utils.makeSkipPattern(subject));
    }

    @Test
    void testSkipMultipleSpaces() {
        String subject = "" +
            "a\n" +
            "a\n" +
            "|\n" +
            "a\n" +
            " \n" +
            " \n" +
            " \n" +
            "|\n" +
            "a\n" +
            "";
        Assertions.assertEquals("%01_0100", Utils.makeSkipPattern(subject));
    }

    @Test
    void testSkipLeadingAndTrailingSpaces() {
        String subject = "" +
            " \n" +
            " \n" +
            "a\n" +
            "a\n" +
            "|\n" +
            "a\n" +
            " \n" +
            "|\n" +
            "a\n" +
            " \n" +
            " \n" +
            "";
        Assertions.assertEquals("%01_0100", Utils.makeSkipPattern(subject));
    }

    @Test
    void testSplitArguments() {
        String[] result = Utils.splitArguments("-2 -L/home/marco/lib source.spin2");
        Assertions.assertEquals(3, result.length);
        Assertions.assertEquals("-2", result[0]);
        Assertions.assertEquals("-L/home/marco/lib", result[1]);
        Assertions.assertEquals("source.spin2", result[2]);
    }

    @Test
    void testSplitStringDelimitedArguments() {
        String[] result = Utils.splitArguments("-2 -L \"/home/marco/lib\" source.spin2");
        Assertions.assertEquals(4, result.length);
        Assertions.assertEquals("-2", result[0]);
        Assertions.assertEquals("-L", result[1]);
        Assertions.assertEquals("/home/marco/lib", result[2]);
        Assertions.assertEquals("source.spin2", result[3]);
    }

}
