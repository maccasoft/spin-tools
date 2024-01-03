/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FileUtilsTest {

    @Test
    void testEolPattern() {
        String text = "line 1\rline 2\r\nline 3\n\nLine 4\r\n\r\nLine 5";
        Assertions.assertEquals(""
            + "line 1\n"
            + "line 2\n"
            + "line 3\n"
            + "\n"
            + "Line 4\n"
            + "\n"
            + "Line 5"
            + "", new String(text).replaceAll(FileUtils.EOL_PATTERN, "\n"));
    }

}
