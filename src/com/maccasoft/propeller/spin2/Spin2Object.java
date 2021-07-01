/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Spin2Object {

    int size;
    List<DataObject> data = new ArrayList<DataObject>();

    public static class DataObject {
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
                while (i < bytes.length && i < 5) {
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

    public static class LongDataObject extends DataObject {

        long value;

        public LongDataObject(long value) {
            super(new byte[] {
                (byte) value,
                (byte) (value >> 8),
                (byte) (value >> 16),
                (byte) (value >> 24)
            });
            this.value = value;
        }

        public LongDataObject(long value, String text) {
            super(new byte[] {
                (byte) value,
                (byte) (value >> 8),
                (byte) (value >> 16),
                (byte) (value >> 24)
            }, text);
            this.value = value;
        }

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.bytes = new byte[] {
                (byte) value,
                (byte) (value >> 8),
                (byte) (value >> 16),
                (byte) (value >> 24)
            };
            this.value = value;
        }
    }

    public static class WordDataObject extends DataObject {

        int value;

        public WordDataObject(int value) {
            super(new byte[] {
                (byte) value,
                (byte) (value >> 8)
            });
            this.value = value & 0xFFFF;
        }

        public WordDataObject(int value, String text) {
            super(new byte[] {
                (byte) value,
                (byte) (value >> 8)
            }, text);
            this.value = value & 0xFFFF;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.bytes = new byte[] {
                (byte) value,
                (byte) (value >> 8)
            };
            this.value = value & 0xFFFF;
        }
    }

    public static class ByteDataObject extends DataObject {

        int value;

        public ByteDataObject(int value) {
            super(new byte[] {
                (byte) value
            });
            this.value = value & 0xFF;
        }

        public ByteDataObject(int value, String text) {
            super(new byte[] {
                (byte) value
            }, text);
            this.value = value & 0xFF;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.bytes = new byte[] {
                (byte) value
            };
            this.value = value & 0xFF;
        }
    }

    public static class PAsmDataObject extends DataObject {

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

    public Spin2Object() {

    }

    public LongDataObject writeLong(long value) {
        LongDataObject rc = new LongDataObject(value);
        data.add(rc);
        size += 4;
        return rc;
    }

    public LongDataObject writeLong(long value, String text) {
        LongDataObject rc = new LongDataObject(value, text);
        data.add(rc);
        size += 4;
        return rc;
    }

    public WordDataObject writeWord(int value) {
        WordDataObject rc = new WordDataObject(value);
        data.add(rc);
        size += 2;
        return rc;
    }

    public WordDataObject writeWord(int value, String text) {
        WordDataObject rc = new WordDataObject(value, text);
        data.add(rc);
        size += 2;
        return rc;
    }

    public ByteDataObject writeByte(int value) {
        ByteDataObject rc = new ByteDataObject(value);
        data.add(rc);
        size += 1;
        return rc;
    }

    public ByteDataObject writeByte(int value, String text) {
        ByteDataObject rc = new ByteDataObject(value, text);
        data.add(rc);
        size += 1;
        return rc;
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
