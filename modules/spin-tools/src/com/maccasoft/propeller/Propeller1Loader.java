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

import com.maccasoft.propeller.devices.ComPort;
import com.maccasoft.propeller.devices.ComPortException;
import com.maccasoft.propeller.devices.SerialComPort;

import jssc.SerialPort;
import jssc.SerialPortList;

public class Propeller1Loader extends PropellerLoader {

    public static final int SHUTDOWN = 0;
    public static final int DOWNLOAD_RUN_BINARY = 1;
    public static final int DOWNLOAD_EEPROM = 2;
    public static final int DOWNLOAD_RUN_EEPROM = 3;
    public static final int DOWNLOAD_SHUTDOWN = 4;

    SerialComPort comPort;
    byte LFSR;

    private boolean shared;

    public Propeller1Loader(SerialComPort serialPort, boolean shared) {
        this.comPort = serialPort;
        this.shared = shared;
    }

    public Propeller1Loader(SerialComPort serialPort, boolean shared, PropellerLoaderListener listener) {
        super(listener);
        this.comPort = serialPort;
        this.shared = shared;
    }

    public String getPortName() {
        return comPort.getPortName();
    }

    public SerialComPort getSerialPort() {
        return comPort;
    }

    @Override
    public ComPort detect() {
        if (comPort != null) {
            try {
                if (find() != 0) {
                    return comPort;
                }
            } catch (Exception e) {
                // Do nothing
            }
        }

        String[] portNames = SerialPortList.getPortNames();
        for (int i = 0; i < portNames.length; i++) {
            if (comPort != null && portNames[i].equals(comPort.getPortName())) {
                continue;
            }
            comPort = new SerialComPort(portNames[i]);
            try {
                if (find() != 0) {
                    return comPort;
                }
            } catch (Exception e) {
                // Do nothing
            }
            if (shared) {
                try {
                    comPort.closePort();
                } catch (Exception e) {
                    // Do nothing
                }
            }
        }

        return null;
    }

    public int find() throws Exception {
        int version = 0;

        if (!comPort.isOpened()) {
            comPort.openPort();
        }
        comPort.setParams(
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
                comPort.closePort();
            }
        }

        return version;
    }

    void hwreset() {
        try {
            comPort.setDTR(true);
            comPort.setRTS(true);
            msleep(25);
            comPort.setDTR(false);
            comPort.setRTS(false);
            msleep(25);
            skipIncomingBytes();
            comPort.getSerialPort().purgePort(SerialPort.PURGE_TXABORT |
                SerialPort.PURGE_RXABORT |
                SerialPort.PURGE_TXCLEAR |
                SerialPort.PURGE_RXCLEAR);
        } catch (Exception e) {
            // Do nothing
        }
    }

    private void msleep(int msec) {
        try {
            Thread.sleep(msec);
        } catch (Exception e) {

        }
    }

    protected int hwfind() throws ComPortException {
        int n, ii, jj;
        byte[] buffer;

        // send the calibration pulse
        comPort.writeInt(0xF9);

        // send the magic propeller LFSR byte stream.
        LFSR = 'P';
        buffer = new byte[250];
        for (n = 0; n < 250; n++) {
            buffer[n] = (byte) (iterate() | 0xFE);
            //System.out.print(String.format("%02X ", ii));
        }
        comPort.writeBytes(buffer);
        //System.out.println();

        skipIncomingBytes();

        // Send 258 0xF9 for LFSR and Version ID
        // These bytes clock the LSFR bits and ID from propeller back to us.
        buffer = new byte[258];
        for (n = 0; n < 258; n++) {
            buffer[n] = (byte) (0xF9);
        }
        comPort.writeBytes(buffer);

        // Wait at least 100ms for the first response. Allow some margin.
        // Some chips may respond < 50ms, but there's no guarantee all will.
        // If we don't get it, we can assume the propeller is not there.
        if ((ii = getBit(110)) == -1) {
            return 0;
        }
        //System.out.print(String.format("%02X ", ii));

        // wait for response so we know we have a Propeller
        for (n = 1; n < 250; n++) {
            jj = iterate();
            //System.out.println(String.format("%03d: %d:%d", n, ii, jj));

            if (ii != jj) {
                //System.err.println("Lost HW contact");
                for (n = 0; n < 300; n++) {
                    if (comPort.readByteWithTimeout(50) == -1) {
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
                return 0;
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

    protected int skipIncomingBytes() throws ComPortException {
        int n = 0;
        while (comPort.readByteWithTimeout(50) != -1) {
            n++;
        }
        return n;
    }

    private int getBit(int timeout) throws ComPortException {
        int rx = comPort.readByteWithTimeout(timeout);
        if (rx != -1) {
            return rx & 1;
        }
        return -1;
    }

    @Override
    public void upload(byte[] binaryImage, int type) throws ComPortException {
        if (!comPort.isOpened()) {
            comPort.openPort();
        }
        comPort.setParams(
            SerialPort.BAUDRATE_115200,
            SerialPort.DATABITS_8,
            SerialPort.STOPBITS_1,
            SerialPort.PARITY_NONE,
            false,
            false);

        try {
            hwreset();
            if (hwfind() == 0) {
                throw new ComPortException("No propeller chip on port " + comPort.getPortName());
            }
            bufferUpload(type, binaryImage, "binary image");
        } finally {
            if (!shared) {
                comPort.closePort();
            }
        }
    }

    protected void bufferUpload(int type, byte[] binaryImage, String text) throws ComPortException {
        int n;
        int longcount = binaryImage.length / 4;

        if (listener != null) {
            listener.bufferUpload(type, binaryImage, text);
        }

        // send type
        comPort.writeBytes(makelong(type));
        // send count
        comPort.writeBytes(makelong(longcount));

        for (n = 0; n < binaryImage.length; n += 4) {
            if ((n % 1024) == 0) {
                notifyProgress(n, binaryImage.length);
            }
            int data = (binaryImage[n] & 0xFF) |
                ((binaryImage[n + 1] << 8) & 0xFF00) |
                ((binaryImage[n + 2] << 16) & 0xFF0000) |
                ((binaryImage[n + 3] << 24) & 0xFF000000);
            comPort.writeBytes(makelong(data));
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

    protected void verifyRam() throws ComPortException {
        int n, rc = 0;

        if (listener != null) {
            listener.verifyRam();
        }

        for (n = 0; n < 100; n++) {
            comPort.writeInt(0xF9);
            if ((rc = getBit(110)) != -1) {
                break;
            }
        }

        // Check for a Timeout or Checksum Error
        if (n >= 100) {
            throw new ComPortException("Timeout");
        }

        if (rc != 0) {
            throw new ComPortException("Checksum error");
        }
    }

    protected void eepromWrite() throws ComPortException {
        int n, rc;

        if (listener != null) {
            listener.eepromWrite();
        }

        // Check for EEPROM program finished
        for (n = 0; n < 500; n++) {
            msleep(20);
            comPort.writeInt(0xF9);
            if ((rc = getBit(110)) != -1) {
                if (rc != 0) {
                    throw new ComPortException("EEPROM programming failed");
                }
                break;
            }
        }
        if (n >= 500) {
            throw new ComPortException("EEPROM programming timeout");
        }
    }

    protected void eepromVerify() throws ComPortException {
        int n, rc;

        if (listener != null) {
            listener.eepromVerify();
        }

        // Check for EEPROM program verify
        for (n = 0; n < 500; n++) {
            msleep(20);
            comPort.writeInt(0xF9);
            if ((rc = getBit(110)) != -1) {
                if (rc != 0) {
                    throw new ComPortException("EEPROM verify failed");
                }
                break;
            }
        }
        if (n >= 500) {
            throw new ComPortException("EEPROM verify timeout");
        }
    }

}
