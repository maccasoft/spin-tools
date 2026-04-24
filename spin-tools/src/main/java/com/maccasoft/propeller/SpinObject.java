/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class SpinObject {

    public static class DataObject {

        protected byte[] bytes;
        protected String text;

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

        public byte[] getBytes() {
            return bytes;
        }

        public int setBytes(byte[] bytes, int index) {
            if (this.bytes == null) {
                return index;
            }
            int n = Math.min(bytes.length - index, this.bytes.length);
            for (int i = 0; i < n; i++, index++) {
                if (this.bytes[i] != bytes[index]) {
                    text = "";
                }
                this.bytes[i] = bytes[index];
            }
            return index;
        }

        public int size() {
            return bytes != null ? bytes.length : 0;
        }

        public void generateListing(int address, int offset, PrintStream ps) {
            if (bytes != null && bytes.length != 0) {
                ps.printf("%05X %05X      ", address + offset, address);

                int i = 0;
                while (i < bytes.length) {
                    if (i > 0 && (i % 5) == 0) {
                        if (i == 5) {
                            if (text != null) {
                                ps.print(" " + text);
                            }
                        }
                        ps.println();
                        ps.printf("%05X %05X      ", address + offset, address);
                    }
                    ps.printf(" %02X", bytes[i++]);
                    address++;
                }
                while (i < 5) {
                    ps.print("   ");
                    i++;
                }
                if (i == 5) {
                    if (text != null) {
                        ps.print(" " + text);
                    }
                }
                ps.println();
            }
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(bytes);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            DataObject other = (DataObject) obj;
            return Arrays.equals(bytes, other.bytes);
        }

    }

    public static class CommentDataObject extends DataObject {

        public CommentDataObject(String text) {
            super(null, text);
        }

        @Override
        public void generateListing(int address, int offset, PrintStream ps) {
            ps.println("' " + text);
        }

    }

    public static class ObjectDataObject extends DataObject {

        SpinObject object;

        public ObjectDataObject(SpinObject object) {
            super(null, null);
            this.object = object;
        }

        @Override
        public int size() {
            return object.getSize();
        }

        public SpinObject getObject() {
            return object;
        }

        @Override
        public int setBytes(byte[] bytes, int index) {
            return object.setBytes(bytes, index);
        }

        @Override
        public void generateListing(int address, int offset, PrintStream ps) {
            object.generateListing(address + offset, ps);
        }

    }

    public static abstract class LinkDataObject extends DataObject {

        int offset;
        int varOffset;
        int varSize;

        ObjectCompiler objectCompiler;

        public LinkDataObject(ObjectCompiler objectCompiler, int varSize) {
            super(new byte[] {
                (byte) 0,
                (byte) 0,
                (byte) 0,
                (byte) 0
            });
            this.objectCompiler = objectCompiler;
            this.varSize = varSize;
        }

        public int getVarSize() {
            return varSize;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public int getVarOffset() {
            return varOffset;
        }

        public void setVarOffset(int varOffset) {
            this.varOffset = varOffset;
        }

        public boolean isObjectCompiler(ObjectCompiler other) {
            return this.objectCompiler == other;
        }

    }

    public static class LongDataObject extends DataObject {

        int value;

        public LongDataObject(int value) {
            super(new byte[] {
                (byte) value,
                (byte) (value >> 8),
                (byte) (value >> 16),
                (byte) (value >> 24)
            });
            this.value = value;
        }

        public LongDataObject(int value, String text) {
            super(new byte[] {
                (byte) value,
                (byte) (value >> 8),
                (byte) (value >> 16),
                (byte) (value >> 24)
            }, text);
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
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
        boolean hubMode;

        public PAsmDataObject(int addr, boolean hubMode, byte[] bytes, String text) {
            super(bytes, text);
            this.addr = addr;
            this.hubMode = hubMode;
        }

        public PAsmDataObject(int addr, boolean hubMode, byte[] bytes) {
            super(bytes);
            this.addr = addr;
            this.hubMode = hubMode;
        }

        @Override
        public void generateListing(int address, int offset, PrintStream ps) {
            int cogAddr = addr;

            ps.printf("%05X %05X ", address + offset, address);
            ps.print(hubMode ? String.format("%05X", cogAddr) : String.format("  %03X", cogAddr));

            int i = 0;
            while (i < bytes.length) {
                if (i > 0 && (i % 4) == 0) {
                    ps.print("   ");
                    if (i == 4) {
                        if (text != null) {
                            ps.print(" " + text);
                        }
                    }
                    ps.println();
                    cogAddr += hubMode ? 4 : 1;
                    ps.printf("%05X %05X ", address + offset, address);
                    ps.print(hubMode ? String.format("%05X", cogAddr) : String.format("  %03X", cogAddr));
                }
                ps.printf(" %02X", bytes[i++]);
                address++;
            }
            while (i < 5) {
                ps.print("   ");
                i++;
            }
            if (i == 5) {
                if (text != null) {
                    ps.print(" " + text);
                }
            }
            ps.println();
        }

    }

    File file;
    int address;

    int clkfreq;
    int clkmode;
    int varSize;

    int size;
    List<DataObject> data = new ArrayList<>();

    List<SpinObject> childObjects = new ArrayList<>();

    public SpinObject() {

    }

    public SpinObject(File file, int address) {
        this.file = file;
        this.address = address;
    }

    public File getFile() {
        return file;
    }

    public int getAddress() {
        return address;
    }

    public void addChildObject(SpinObject object) {
        for (SpinObject child : childObjects) {
            if (child == object) {
                return;
            }
        }
        childObjects.add(object);
    }

    public List<SpinObject> getChildObjects() {
        return childObjects;
    }

    public LongDataObject writeLong(int value) {
        LongDataObject rc = new LongDataObject(value);
        data.add(rc);
        size += 4;
        return rc;
    }

    public LongDataObject writeLong(int value, String text) {
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
        data.add(new DataObject(bytes.clone()));
        size += bytes.length;
    }

    public void writeBytes(byte[] bytes, String text) {
        data.add(new DataObject(bytes.clone(), text));
        size += bytes.length;
    }

    public void writeBytes(int addr, byte[] bytes) {
        data.add(new PAsmDataObject(addr, false, bytes.clone()));
        size += bytes.length;
    }

    public void writeBytes(int addr, byte[] bytes, String text) {
        data.add(new PAsmDataObject(addr, false, bytes.clone(), text));
        size += bytes.length;
    }

    public void writeBytes(int addr, boolean hubMode, byte[] bytes) {
        data.add(new PAsmDataObject(addr, hubMode, bytes.clone()));
        size += bytes.length;
    }

    public void writeBytes(int addr, boolean hubMode, byte[] bytes, String text) {
        data.add(new PAsmDataObject(addr, hubMode, bytes.clone(), text));
        size += bytes.length;
    }

    public void writeObject(SpinObject object) {
        data.add(new ObjectDataObject(object));
        size += object.size;
    }

    public void writeComment(String text) {
        data.add(new CommentDataObject(text));
    }

    public void write(DataObject object) {
        data.add(object);
        if (object.bytes != null) {
            size += object.bytes.length;
        }
    }

    public DataObject getObject(int index) {
        if (index < 0 || index >= data.size()) {
            return null;
        }
        return data.get(index);
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
            if (obj instanceof ObjectDataObject) {
                ((ObjectDataObject) obj).getObject().generateBinary(os);
            }
            else if (obj.bytes != null) {
                os.write(obj.bytes);
            }
        }
    }

    public byte[] getBinary() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        generateBinary(os);
        return os.toByteArray();
    }

    public byte[] getEEPromBinary() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        generateBinary(os);
        return os.toByteArray();
    }

    public void generateDatBinary(OutputStream os) throws IOException {
        for (DataObject obj : data) {
            if (obj instanceof ObjectDataObject) {
                byte[] bytes = ((ObjectDataObject) obj).getObject().getDatBinary();
                os.write(bytes);
            }
            else if (obj instanceof PAsmDataObject) {
                os.write(obj.bytes);
            }
        }
    }

    public byte[] getDatBinary() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        generateDatBinary(os);
        return os.toByteArray();
    }

    public byte[] getRAM() throws IOException {
        return getBinary();
    }

    public void generateListing(PrintStream ps) {
        generateListing(0, ps);
    }

    public int generateListing(int offset, PrintStream ps) {
        int address = 0;

        for (DataObject obj : data) {
            obj.generateListing(address, offset, ps);
            address += obj.size();
        }

        return address;
    }

    public int setBytes(byte[] bytes, int index) {
        for (DataObject obj : data) {
            index = obj.setBytes(bytes, index);
        }
        return index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, varSize);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SpinObject other = (SpinObject) obj;
        return Objects.equals(data, other.data) && varSize == other.varSize;
    }

    public List<LinkDataObject> getObjectLinks() {
        List<LinkDataObject> list = new ArrayList<>();

        for (DataObject obj : data) {
            if (obj instanceof LinkDataObject linkDataObjects) {
                list.add(linkDataObjects);
            }
        }

        return list;
    }

}
