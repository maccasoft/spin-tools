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

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CircularBufferTest {

    @Test
    void testWrite() throws IOException {
        CircularBuffer subject = new CircularBuffer(10);
        Assertions.assertEquals(0, subject.head);
        Assertions.assertEquals(0, subject.tail);
        subject.write('A');
        Assertions.assertEquals(1, subject.head);
        Assertions.assertEquals(0, subject.tail);
    }

    @Test
    void testRead() throws IOException {
        CircularBuffer subject = new CircularBuffer(10);
        subject.fifo[subject.head++] = 'A';
        Assertions.assertEquals('A', subject.read());
        Assertions.assertEquals(1, subject.head);
        Assertions.assertEquals(1, subject.tail);
    }

    @Test
    void testReadBuffer() throws IOException {
        CircularBuffer subject = new CircularBuffer(10);

        String text = "ABCDEF";
        subject.write(text.getBytes());

        byte[] rc = new byte[100];
        int size = subject.read(rc);
        Assertions.assertEquals(text.length(), size);

        Assertions.assertEquals(text, new String(rc, 0, size));
    }

    @Test
    void testWriteWrap() throws IOException {
        CircularBuffer subject = new CircularBuffer(5);

        subject.write("ABCDEF".getBytes());

        byte[] rc = new byte[100];
        int size = subject.read(rc);
        Assertions.assertEquals(1, size);

        Assertions.assertEquals("F", new String(rc, 0, size));
    }

    @Test
    void testAvailable() throws IOException {
        CircularBuffer subject = new CircularBuffer(10);
        Assertions.assertEquals(0, subject.available());
        subject.write("ABCDEF".getBytes());
        Assertions.assertEquals(6, subject.available());
    }

}
