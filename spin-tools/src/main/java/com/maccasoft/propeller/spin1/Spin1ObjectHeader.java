/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.maccasoft.propeller.SpinObject.ByteDataObject;
import com.maccasoft.propeller.SpinObject.DataObject;
import com.maccasoft.propeller.SpinObject.LongDataObject;
import com.maccasoft.propeller.SpinObject.WordDataObject;

public class Spin1ObjectHeader {

    public final LongDataObject clkfreq;
    public final ByteDataObject clkmode;
    public final ByteDataObject checksum;

    public final WordDataObject pbase;
    public final WordDataObject vbase;
    public final WordDataObject dbase;

    public final WordDataObject pcurr;
    public final WordDataObject dcurr;

    List<DataObject> data = new ArrayList<>();

    public Spin1ObjectHeader() {
        data.add(clkfreq = new LongDataObject(0, "CLKFREQ"));
        data.add(clkmode = new ByteDataObject(0, "CLKMODE"));

        data.add(checksum = new ByteDataObject(0, "Checksum"));

        data.add(pbase = new WordDataObject(16, "PBASE"));
        data.add(vbase = new WordDataObject(0, "VBASE"));
        data.add(dbase = new WordDataObject(0, "DBASE"));

        data.add(pcurr = new WordDataObject(0, "PCURR"));
        data.add(dcurr = new WordDataObject(0, "DCURR"));
    }

    public int getSize() {
        return 16;
    }

    public void generateListing(PrintStream ps) {
        int address = 0;

        for (DataObject obj : data) {
            obj.generateListing(address, 0, ps);
            address += obj.size();
        }
    }

    public byte[] getBinary() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        for (DataObject obj : data) {
            try {
                os.write(obj.getBytes());
            } catch (Exception ignored) {
                // Do nothing
            }
        }

        return os.toByteArray();
    }

    public int setBytes(byte[] bytes, int index) {
        for (DataObject obj : data) {
            index = obj.setBytes(bytes, index);
        }
        return index;
    }

}
