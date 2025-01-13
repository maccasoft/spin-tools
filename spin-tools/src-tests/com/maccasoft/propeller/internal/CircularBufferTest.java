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

class CircularBufferTest {

    @Test
    void testWrite() throws Exception {
        CircularBuffer subject = new CircularBuffer(10);
        Assertions.assertEquals(0, subject.head);
        Assertions.assertEquals(0, subject.tail);
        subject.write('A');
        Assertions.assertEquals(1, subject.head);
        Assertions.assertEquals(0, subject.tail);
    }

    @Test
    void testRead() throws Exception {
        CircularBuffer subject = new CircularBuffer(10);
        subject.buffer[subject.head++] = 'A';
        Assertions.assertEquals('A', subject.read());
        Assertions.assertEquals(1, subject.head);
        Assertions.assertEquals(1, subject.tail);
    }

    @Test
    void testReadBuffer() throws Exception {
        CircularBuffer subject = new CircularBuffer(10);

        String text = "ABCDEF";
        subject.write(text.getBytes());

        byte[] rc = new byte[100];
        int size = subject.read(rc);
        Assertions.assertEquals(text.length(), size);
        Assertions.assertEquals(text, new String(rc, 0, size));
    }

    @Test
    void testWriteWrap() throws Exception {
        CircularBuffer subject = new CircularBuffer(10);
        subject.head = subject.tail = 8;

        subject.write("ABCDEF".getBytes());

        byte[] rc = new byte[100];
        int size = subject.read(rc);
        Assertions.assertEquals(6, size);
        Assertions.assertEquals("ABCDEF", new String(rc, 0, size));
    }

    @Test
    void testAvailable() throws Exception {
        CircularBuffer subject = new CircularBuffer(10);
        Assertions.assertEquals(0, subject.available());

        subject.write("ABCDEF".getBytes());

        Assertions.assertEquals(6, subject.available());
    }

    @Test
    void testWrapAvailable() throws Exception {
        CircularBuffer subject = new CircularBuffer(10);
        subject.head = subject.tail = 8;
        Assertions.assertEquals(0, subject.available());

        subject.write("ABCDEF".getBytes());

        Assertions.assertEquals(6, subject.available());
    }

}
