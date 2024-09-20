/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.debug;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class KeywordIteratorTest {

    @Test
    void testTokenizer() {
        KeywordIterator subject = new KeywordIterator("POS 0 1 SIZE 2 3");
        Assertions.assertEquals(6, subject.ar.length);
        Assertions.assertEquals("POS", subject.ar[0]);
        Assertions.assertEquals("0", subject.ar[1]);
        Assertions.assertEquals("1", subject.ar[2]);
        Assertions.assertEquals("SIZE", subject.ar[3]);
        Assertions.assertEquals("2", subject.ar[4]);
        Assertions.assertEquals("3", subject.ar[5]);
    }

    @Test
    void testStringTokenizer() {
        KeywordIterator subject = new KeywordIterator("TITLE 'My Window Title'");
        Assertions.assertEquals(2, subject.ar.length);
        Assertions.assertEquals("TITLE", subject.ar[0]);
        Assertions.assertEquals("'My Window Title'", subject.ar[1]);
    }

    @Test
    void testNextString() {
        KeywordIterator subject = new KeywordIterator("TITLE 'My Window Title'");
        subject.next();
        Assertions.assertTrue(subject.hasNextString());
        Assertions.assertEquals("My Window Title", subject.nextString());
    }

    @Test
    void testNumberList() {
        KeywordIterator subject = new KeywordIterator("1 2 3, 4");
        Assertions.assertEquals(4, subject.ar.length);
        Assertions.assertEquals("1", subject.ar[0]);
        Assertions.assertEquals("2", subject.ar[1]);
        Assertions.assertEquals("3", subject.ar[2]);
        Assertions.assertEquals("4", subject.ar[3]);
    }

}
