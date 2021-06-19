/*
 * Copyright (c) 2016 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Based on the PLoadLib.c code written by John Steven Denson
 * with modifications by David Michael Betz
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class Propeller2Loader {

    SerialPort serialPort;
    int portSpeed = 2000000;

    boolean shared;

    public Propeller2Loader(String port) {
        this.serialPort = new SerialPort(port);
    }

    public Propeller2Loader(SerialPort serialPort, boolean shared) {
        this.serialPort = serialPort;
        this.shared = shared;
    }

    public String getPortName() {
        return serialPort.getPortName();
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public int find() throws Exception {
        int version = 0;

        if (!serialPort.isOpened()) {
            serialPort.openPort();
            serialPort.setParams(
                portSpeed,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE,
                true,
                true);
        }

        try {
            hwreset();
            version = hwfind();
        } finally {
            if (!shared) {
                serialPort.closePort();
            }
        }

        return version;
    }

    void hwreset() throws SerialPortException {
        serialPort.setDTR(false);
        msleep(25);
        serialPort.setDTR(true);
        msleep(25);
        skipIncomingBytes();
        serialPort.purgePort(SerialPort.PURGE_TXABORT |
            SerialPort.PURGE_RXABORT |
            SerialPort.PURGE_TXCLEAR |
            SerialPort.PURGE_RXCLEAR);
    }

    private void msleep(int msec) {
        try {
            Thread.sleep(msec);
        } catch (Exception e) {

        }
    }

    protected int hwfind() throws SerialPortException, IOException {
        String result = new String();

        serialPort.writeString("> \r");
        msleep(1);

        serialPort.writeString("> Prop_Chk 0 0 0 0\r");

        try {
            while (result.length() < 11 || !result.endsWith("\r\n")) {
                byte[] b = serialPort.readBytes(1, 20);
                if (b != null && b.length == 1) {
                    result += new String(b);
                }
            }
            if (result.startsWith("\r\nProp_Ver ")) {
                return 2;
            }
        } catch (SerialPortTimeoutException e) {
            return 0;
        }

        return 0;
    }

    protected int skipIncomingBytes() throws SerialPortException {
        int n = 0;
        while (readByteWithTimeout(50) != -1) {
            n++;
        }
        return n;
    }

    private int readByteWithTimeout(int timeout) throws SerialPortException {
        int[] rx;
        try {
            rx = serialPort.readIntArray(1, timeout);
            return rx[0];
        } catch (SerialPortTimeoutException e) {

        }
        return -1;
    }

    public void upload(File binaryFile, int type) throws SerialPortException, IOException, SerialPortTimeoutException {
        byte[] imageBuffer = new byte[(int) binaryFile.length()];

        FileInputStream is = new FileInputStream(binaryFile);
        is.read(imageBuffer);
        is.close();

        if (!serialPort.isOpened()) {
            serialPort.openPort();
            serialPort.setParams(
                portSpeed,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE,
                true,
                true);
        }

        try {
            hwreset();
            if (hwfind() != 0) {
                bufferUpload(type, imageBuffer, "binary image");
            }
        } finally {
            if (!shared) {
                serialPort.closePort();
            }
        }
    }

    public void upload(byte[] binaryImage, int type) throws SerialPortException, IOException {
        if (!serialPort.isOpened()) {
            serialPort.openPort();
            serialPort.setParams(
                portSpeed,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE,
                true,
                true);
        }

        try {
            hwreset();
            if (hwfind() != 0) {
                bufferUpload(type, binaryImage, "binary image");
            }
        } finally {
            if (!shared) {
                serialPort.closePort();
            }
        }
    }

    protected void bufferUpload(int type, byte[] binaryImage, String text) throws SerialPortException, IOException {
        int n;
        //Encoder encoder = Base64.getEncoder();
        //byte[] encodedImage = encoder.encode(binaryImage);

        serialPort.writeString("> Prop_Hex 0 0 0 0");

        long sum = 0;
        for (n = 0; n < binaryImage.length; n += 4) {
            if (n > 0 && (n % 64) == 0) {
                serialPort.writeString("\r>");
                notifyProgress(n, binaryImage.length);
            }
            int data = binaryImage[n] & 0xFF;
            if ((n + 1) < binaryImage.length) {
                data |= (binaryImage[n + 1] << 8) & 0xFF00;
                if ((n + 2) < binaryImage.length) {
                    data |= (binaryImage[n + 2] << 16) & 0xFF0000;
                    if ((n + 3) < binaryImage.length) {
                        data |= (binaryImage[n + 3] << 24) & 0xFF000000;
                    }
                }
            }
            serialPort.writeString(String.format(" %x %x %x %x", data & 0xFF, (data >> 8) & 0xFF, (data >> 16) & 0xFF, (data >> 24) & 0xFF));
            sum += data;
        }
        notifyProgress(n, binaryImage.length);

        sum = 0x706F7250 - sum;
        serialPort.writeString(String.format(" %x %x %x %x ?", sum & 0xFF, (sum >> 8) & 0xFF, (sum >> 16) & 0xFF, (sum >> 24) & 0xFF));

        verifyRam();
    }

    protected void notifyProgress(int sent, int total) {
        // Do nothing
    }

    protected void verifyRam() throws SerialPortException, IOException {
        int rc = readByteWithTimeout(1000);

        if (rc == -1) {
            throw new IOException("Timeout");
        }

        if (rc != '.') {
            throw new IOException("Checksum error");
        }
    }

    protected void writeFile(File file) throws SerialPortException, SerialPortTimeoutException, IOException {
        // Do nothing
    }
}
