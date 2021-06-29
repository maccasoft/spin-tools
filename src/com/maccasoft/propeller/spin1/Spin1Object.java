/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Spin1Object {

    int size;
    List<DataObject> data = new ArrayList<DataObject>();

    class DataObject {
        byte[] bytes;
        String text;

        public DataObject(byte[] bytes) {
            this.bytes = bytes;
        }

        public DataObject(byte[] bytes, String text) {
            this.bytes = bytes;
            this.text = text;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("   ");

            int i = 0;
            if (bytes != null) {
                while (i < bytes.length && i < 4) {
                    sb.append(String.format(" %02X", bytes[i++]));
                }
            }
            while (i < 5) {
                sb.append("   ");
                i++;
            }
            sb.append(" | ");
            if (text != null) {
                sb.append(text);
            }

            return sb.toString();
        }

    }

    class PAsmDataObject extends DataObject {

        int addr;

        public PAsmDataObject(int addr, byte[] bytes, String text) {
            super(bytes, text);
            this.addr = addr;
        }

        public PAsmDataObject(int addr, byte[] bytes) {
            super(bytes);
            this.addr = addr;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%03X", addr));

            int i = 0;
            if (bytes != null) {
                while (i < bytes.length && i < 4) {
                    sb.append(String.format(" %02X", bytes[i++]));
                }
            }
            while (i < 5) {
                sb.append("   ");
                i++;
            }
            sb.append(" | ");
            if (text != null) {
                sb.append(text);
            }

            return sb.toString();
        }

    }

    public Spin1Object() {

    }

    public void writeLong(int value) {
        data.add(new DataObject(new byte[] {
            (byte) value,
            (byte) (value >> 8),
            (byte) (value >> 16),
            (byte) (value >> 24)
        }));
        size += 4;
    }

    public void writeLong(int value, String text) {
        data.add(new DataObject(new byte[] {
            (byte) value,
            (byte) (value >> 8),
            (byte) (value >> 16),
            (byte) (value >> 24)
        }, text));
        size += 4;
    }

    public void writeWord(int value) {
        data.add(new DataObject(new byte[] {
            (byte) value,
            (byte) (value >> 8),
        }));
        size += 2;
    }

    public void writeWord(int value, String text) {
        data.add(new DataObject(new byte[] {
            (byte) value,
            (byte) (value >> 8),
        }, text));
        size += 2;
    }

    public void writeByte(int value) {
        data.add(new DataObject(new byte[] {
            (byte) value,
        }));
        size += 1;
    }

    public void writeByte(int value, String text) {
        data.add(new DataObject(new byte[] {
            (byte) value,
        }, text));
        size += 1;
    }

    public void writeBytes(byte[] bytes) {
        data.add(new DataObject(bytes));
        size += bytes.length;
    }

    public void writeBytes(byte[] bytes, String text) {
        data.add(new DataObject(bytes, text));
        size += bytes.length;
    }

    public void writeBytes(int addr, byte[] bytes) {
        data.add(new PAsmDataObject(addr, bytes));
        size += bytes.length;
    }

    public void writeBytes(int addr, byte[] bytes, String text) {
        data.add(new PAsmDataObject(addr, bytes, text));
        size += bytes.length;
    }

    public int getSize() {
        return size;
    }

    public void alignToWord() {
        if ((size % 2) != 0) {
            int padding = 2 - (size % 2);
            data.add(new DataObject(new byte[padding], "Padding"));
        }
    }

    public void alignToLong() {
        if ((size % 4) != 0) {
            int padding = 4 - (size % 4);
            data.add(new DataObject(new byte[padding], "Padding"));
        }
    }

    public void generateListing(int address, PrintStream ps) {

        for (DataObject obj : data) {
            ps.print(String.format("%04X ", address));
            ps.println(obj);
            address += obj.bytes.length;
        }
    }

    public void writeBinary(OutputStream os) throws IOException {

        for (DataObject obj : data) {
            os.write(obj.bytes);
        }
    }

}
