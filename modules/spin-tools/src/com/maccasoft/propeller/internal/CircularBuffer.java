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

    static final int TIMEOUT = 500;

    int head;
    int tail;
    byte[] buffer;

    public CircularBuffer(int size) {
        head = tail = 0;
        buffer = new byte[size];
    }

    public void flush() {
        head = tail = 0;
    }

    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte b[], int off, int len) throws IOException {
        Objects.checkFromIndexSize(off, len, b.length);

        int i = off;
        while (len > 0 && head != tail) {
            b[i++] = buffer[tail++];
            if (tail >= buffer.length) {
                tail = 0;
            }
            len--;
        }

        return i - off;
    }

    public int available() throws IOException {
        int rc = head - tail;
        if (rc < 0) {
            rc += buffer.length;
        }
        return rc;
    }

    public void write(int b) throws IOException {
        buffer[head++] = (byte) b;
        if (head >= buffer.length) {
            head = 0;
        }
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        Objects.checkFromIndexSize(off, len, b.length);
        while (len > 0) {
            buffer[head++] = b[off++];
            if (head >= buffer.length) {
                head = 0;
            }
            len--;
        }
    }

    public void writeWord(int b) throws IOException {
        write(b & 0xFF);
        write((b >> 8) & 0xFF);
    }

    public void writeLong(int b) throws IOException {
        write(b & 0xFF);
        write((b >> 8) & 0xFF);
        write((b >> 16) & 0xFF);
        write((b >> 24) & 0xFF);
    }

    public int read() throws IOException, InterruptedException {
        if (head == tail) {
            long now = System.currentTimeMillis();
            do {
                if ((System.currentTimeMillis() - now) > TIMEOUT) {
                    throw new InterruptedException();
                }
                Thread.sleep(1);
            } while (head == tail);
        }
        int rc = buffer[tail++] & 0xFF;
        if (tail >= buffer.length) {
            tail = 0;
        }
        return rc;
    }

    public int readWord() throws IOException, InterruptedException {
        if (available() < 2) {
            long now = System.currentTimeMillis();
            do {
                if ((System.currentTimeMillis() - now) > TIMEOUT) {
                    throw new InterruptedException();
                }
                Thread.sleep(1);
            } while (available() < 2);
        }
        return read() | (read() << 8);
    }

    public int readLong() throws IOException, InterruptedException {
        if (available() < 4) {
            long now = System.currentTimeMillis();
            do {
                if ((System.currentTimeMillis() - now) > TIMEOUT) {
                    throw new InterruptedException();
                }
                Thread.sleep(1);
            } while (available() < 4);
        }
        return read() | (read() << 8) | (read() << 16) | (read() << 24);
    }

}
