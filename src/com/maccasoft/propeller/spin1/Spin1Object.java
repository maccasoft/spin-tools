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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Spin1Object {

    int size;
    List<DataObject> data = new ArrayList<DataObject>();

    int clkfreq;
    int clkmode;
    int varSize;

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

        public void setText(String text) {
            this.text = text;
        }

    }

    public static class CommentDataObject extends DataObject {

        public CommentDataObject(String text) {
            super(null, text);
        }
    }

    public static class ObjectDataObject extends DataObject {

        Spin1Object object;

        public ObjectDataObject(Spin1Object object) {
            super(null, null);
            this.object = object;
        }

        public Spin1Object getObject() {
            return object;
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

    }

    public Spin1Object() {

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

    public void writeObject(Spin1Object object) {
        data.add(new ObjectDataObject(object));
        size += object.size;
    }

    public void writeComment(String text) {
        data.add(new CommentDataObject(text));
    }

    public int getSize() {
        return size;
    }

    public void alignToWord() {
        if ((size % 2) != 0) {
            int padding = 2 - (size % 2);
            data.add(new DataObject(new byte[padding], "Padding"));
            size += padding;
        }
    }

    public void alignToLong() {
        if ((size % 4) != 0) {
            int padding = 4 - (size % 4);
            data.add(new DataObject(new byte[padding], "Padding"));
            size += padding;
        }
    }

    public int getClkFreq() {
        return clkfreq;
    }

    public void setClkFreq(int clkfreq) {
        this.clkfreq = clkfreq;
    }

    public int getClkMode() {
        return clkmode;
    }

    public void setClkMode(int _clkmode) {
        this.clkmode = _clkmode;
    }

    public int getVarSize() {
        return varSize;
    }

    public void setVarSize(int varSize) {
        this.varSize = varSize;
    }

    public void generateBinary(OutputStream os) throws IOException {
        for (DataObject obj : data) {
            if (obj.bytes != null) {
                os.write(obj.bytes);
            }
        }
    }

    public byte[] getBinary() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        for (DataObject obj : data) {
            if (obj.bytes != null) {
                os.write(obj.bytes);
            }
        }
        return os.toByteArray();
    }

    public void generateListing(PrintStream ps) {
        int offset = 0;
        generateListing(offset, ps);
    }

    protected int generateListing(int offset, PrintStream ps) {
        int address = 0;

        for (DataObject obj : data) {
            if (obj instanceof ObjectDataObject) {
                address += ((ObjectDataObject) obj).getObject().generateListing(address + offset, ps);
            }
            else if (obj.bytes != null) {
                if (obj instanceof PAsmDataObject) {
                    int cogAddr = ((PAsmDataObject) obj).addr;

                    ps.print(String.format("%05X %05X   ", address + offset, address));
                    ps.print(cogAddr < 0x400 ? String.format("%03X", cogAddr) : "   ");

                    int i = 0;
                    while (i < obj.bytes.length) {
                        if (i > 0 && (i % 4) == 0) {
                            ps.print("   ");
                            if (i == 4) {
                                if (obj.text != null) {
                                    ps.print(" " + obj.text);
                                }
                            }
                            ps.println();
                            cogAddr++;
                            ps.print(String.format("%05X %05X   ", address + offset, address));
                            ps.print(cogAddr < 0x400 ? String.format("%03X", cogAddr) : "   ");
                        }
                        ps.print(String.format(" %02X", obj.bytes[i++]));
                        address++;
                    }
                    while (i < 4 || (i % 4) != 0) {
                        ps.print("   ");
                        i++;
                    }
                    ps.print("   ");
                    if (i == 4) {
                        if (obj.text != null) {
                            ps.print(" " + obj.text);
                        }
                    }
                    ps.println();
                }
                else if (obj.bytes.length != 0) {
                    ps.print(String.format("%05X %05X      ", address + offset, address));

                    int i = 0;
                    while (i < obj.bytes.length) {
                        if (i > 0 && (i % 5) == 0) {
                            if (i == 5) {
                                if (obj.text != null) {
                                    ps.print(" " + obj.text);
                                }
                            }
                            ps.println();
                            ps.print(String.format("%05X %05X      ", address + offset, address));
                        }
                        ps.print(String.format(" %02X", obj.bytes[i++]));
                        address++;
                    }
                    while (i < 5 || (i % 5) != 0) {
                        ps.print("   ");
                        i++;
                    }
                    if (i == 5) {
                        if (obj.text != null) {
                            ps.print(" " + obj.text);
                        }
                    }
                    ps.println();
                }
            }
            else if (obj.text != null) {
                ps.println("' " + obj.text);
            }
        }

        return address;
    }

}
