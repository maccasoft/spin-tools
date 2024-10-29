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
import java.util.Objects;

public class CircularBuffer {

    byte[] fifo;
    int head;
    int tail;

    public CircularBuffer(int size) {
        fifo = new byte[size];
        head = tail = 0;
    }

    public void flush() {
        head = tail = 0;
    }

    public int read() throws IOException {
        if (head == tail) {
            return -1;
        }
        int rc = fifo[tail++] & 0xFF;
        if (tail >= fifo.length) {
            tail = 0;
        }
        return rc;
    }

    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte b[], int off, int len) throws IOException {
        Objects.checkFromIndexSize(off, len, b.length);

        int i = off;
        while (len > 0 && head != tail) {
            b[i++] = fifo[tail++];
            if (tail >= fifo.length) {
                tail = 0;
            }
            len--;
        }

        return i - off;
    }

    public int available() throws IOException {
        int rc = head - tail;
        if (rc < 0) {
            rc += fifo.length;
        }
        return rc;
    }

    public void write(int b) throws IOException {
        fifo[head++] = (byte) b;
        if (head >= fifo.length) {
            head = 0;
        }
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        Objects.checkFromIndexSize(off, len, b.length);
        while (len > 0) {
            fifo[head++] = b[off++];
            if (head >= fifo.length) {
                head = 0;
            }
            len--;
        }
    }

}
