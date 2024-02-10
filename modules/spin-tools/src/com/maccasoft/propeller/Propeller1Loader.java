/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
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
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;

public class Propeller1Loader {

    public static final int SHUTDOWN = 0;
    public static final int DOWNLOAD_RUN_BINARY = 1;
    public static final int DOWNLOAD_EEPROM = 2;
    public static final int DOWNLOAD_RUN_EEPROM = 3;
    public static final int DOWNLOAD_SHUTDOWN = 4;

    SerialPort serialPort;
    byte LFSR;

    private boolean shared;

    public Propeller1Loader(SerialPort serialPort, boolean shared) {
        this.serialPort = serialPort;
        this.shared = shared;
    }

    public String getPortName() {
        return serialPort.getPortName();
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public SerialPort detect() {
        if (serialPort != null) {
            try {
                if (find() != 0) {
                    return serialPort;
                }
            } catch (Exception e) {
                // Do nothing
            }
        }

        String[] portNames = SerialPortList.getPortNames();
        for (int i = 0; i < portNames.length; i++) {
            if (serialPort != null && portNames[i].equals(serialPort.getPortName())) {
                continue;
            }
            serialPort = new SerialPort(portNames[i]);
            try {
                if (find() != 0) {
                    return serialPort;
                }
            } catch (Exception e) {
                // Do nothing
            }
            if (shared) {
                try {
                    serialPort.closePort();
                } catch (Exception e) {
                    // Do nothing
                }
            }
        }

        return null;
    }

    public int find() throws Exception {
        int version = 0;

        if (!serialPort.isOpened()) {
            serialPort.openPort();
        }
        serialPort.setParams(
            SerialPort.BAUDRATE_115200,
            SerialPort.DATABITS_8,
            SerialPort.STOPBITS_1,
            SerialPort.PARITY_NONE,
            false,
            false);

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
        serialPort.setDTR(true);
        serialPort.setRTS(true);
        msleep(25);
        serialPort.setDTR(false);
        serialPort.setRTS(false);
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
        int n, ii, jj;
        byte[] buffer;

        // send the calibration pulse
        serialPort.writeInt(0xF9);

        // send the magic propeller LFSR byte stream.
        LFSR = 'P';
        buffer = new byte[250];
        for (n = 0; n < 250; n++) {
            buffer[n] = (byte) (iterate() | 0xFE);
            //System.out.print(String.format("%02X ", ii));
        }
        serialPort.writeBytes(buffer);
        //System.out.println();

        skipIncomingBytes();

        // Send 258 0xF9 for LFSR and Version ID
        // These bytes clock the LSFR bits and ID from propeller back to us.
        buffer = new byte[258];
        for (n = 0; n < 258; n++) {
            buffer[n] = (byte) (0xF9);
        }
        serialPort.writeBytes(buffer);

        // Wait at least 100ms for the first response. Allow some margin.
        // Some chips may respond < 50ms, but there's no guarantee all will.
        // If we don't get it, we can assume the propeller is not there.
        if ((ii = getBit(110)) == -1) {
            throw new IOException("Timeout waiting for first response bit");
        }
        //System.out.print(String.format("%02X ", ii));

        // wait for response so we know we have a Propeller
        for (n = 1; n < 250; n++) {
            jj = iterate();
            //System.out.println(String.format("%03d: %d:%d", n, ii, jj));

            if (ii != jj) {
                //System.err.println("Lost HW contact");
                for (n = 0; n < 300; n++) {
                    if (readByteWithTimeout(50) == -1) {
                        break;
                    }
                }
                return 0;
            }

            int to = 0;
            do {
                if ((ii = getBit(110)) != -1) {
                    //System.out.print(String.format("%02X ", ii));
                    break;
                }
            } while (to++ < 100);

            if (to > 100) {
                throw new IOException("Timeout waiting for response bit");
            }
        }
        //System.out.println();

        int rc = 0;
        for (n = 0; n < 8; n++) {
            rc >>= 1;
            if ((ii = getBit(110)) != -1) {
                //System.out.print(String.format("%02X ", ii));
                rc += (ii != 0) ? 0x80 : 0;
            }
        }
        //System.out.println();

        return rc;
    }

    int iterate() {
        int bit = LFSR & 1;
        LFSR = (byte) ((LFSR << 1) | (((LFSR >> 7) ^ (LFSR >> 5) ^ (LFSR >> 4) ^ (LFSR >> 1)) & 1));
        return bit;
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

    private int getBit(int timeout) throws SerialPortException {
        int[] rx;
        try {
            rx = serialPort.readIntArray(1, timeout);
            return rx[0] & 1;
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
        }
        serialPort.setParams(
            SerialPort.BAUDRATE_115200,
            SerialPort.DATABITS_8,
            SerialPort.STOPBITS_1,
            SerialPort.PARITY_NONE,
            false,
            false);

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
        }
        serialPort.setParams(
            SerialPort.BAUDRATE_115200,
            SerialPort.DATABITS_8,
            SerialPort.STOPBITS_1,
            SerialPort.PARITY_NONE,
            false,
            false);

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
        int longcount = binaryImage.length / 4;

        // send type
        serialPort.writeBytes(makelong(type));
        // send count
        serialPort.writeBytes(makelong(longcount));

        for (n = 0; n < binaryImage.length; n += 4) {
            if ((n % 1024) == 0) {
                notifyProgress(n, binaryImage.length);
            }
            int data = (binaryImage[n] & 0xFF) |
                ((binaryImage[n + 1] << 8) & 0xFF00) |
                ((binaryImage[n + 2] << 16) & 0xFF0000) |
                ((binaryImage[n + 3] << 24) & 0xFF000000);
            serialPort.writeBytes(makelong(data));
        }
        notifyProgress(n, binaryImage.length);

        // give propeller time to calculate checksum match 32K/12M sec = 32ms
        msleep(100);

        verifyRam();

        if ((type & DOWNLOAD_EEPROM) != 0) {
            eepromWrite();
            eepromVerify();
        }
    }

    static byte[] makelong(int data) {
        int n = 0;
        byte[] buff = new byte[11];

        for (; n < 10; n++) {
            buff[n] = (byte) (0x92 | (data & 1) | ((data & 2) << 2) | ((data & 4) << 4));
            data >>= 3;
        }
        buff[n] = (byte) (0xf2 | (data & 1) | ((data & 2) << 2));

        return buff;
    }

    protected void notifyProgress(int sent, int total) {
        // Do nothing
    }

    protected void verifyRam() throws SerialPortException, IOException {
        int n, rc = 0;

        for (n = 0; n < 100; n++) {
            serialPort.writeInt(0xF9);
            if ((rc = getBit(110)) != -1) {
                break;
            }
        }

        // Check for a Timeout or Checksum Error
        if (n >= 100) {
            throw new IOException("Timeout");
        }

        if (rc != 0) {
            throw new IOException("Checksum error");
        }
    }

    protected void eepromWrite() throws SerialPortException, IOException {
        int n, rc;

        // Check for EEPROM program finished
        for (n = 0; n < 500; n++) {
            msleep(20);
            serialPort.writeInt(0xF9);
            if ((rc = getBit(110)) != -1) {
                if (rc != 0) {
                    throw new IOException("EEPROM programming failed");
                }
                break;
            }
        }
        if (n >= 500) {
            throw new IOException("EEPROM programming timeout");
        }
    }

    protected void eepromVerify() throws SerialPortException, IOException {
        int n, rc;

        // Check for EEPROM program verify
        for (n = 0; n < 500; n++) {
            msleep(20);
            serialPort.writeInt(0xF9);
            if ((rc = getBit(110)) != -1) {
                if (rc != 0) {
                    throw new IOException("EEPROM verify failed");
                }
                break;
            }
        }
        if (n >= 500) {
            throw new IOException("EEPROM verify timeout");
        }
    }

    protected void writeFile(File file) throws SerialPortException, SerialPortTimeoutException, IOException {
        // Do nothing
    }
}
