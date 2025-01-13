/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Collection;

import com.maccasoft.propeller.devices.ComPort;
import com.maccasoft.propeller.devices.ComPortException;
import com.maccasoft.propeller.devices.DeviceDescriptor;
import com.maccasoft.propeller.devices.NetworkComPort;
import com.maccasoft.propeller.devices.NetworkUtils;
import com.maccasoft.propeller.devices.SerialComPort;

import jssc.SerialPort;
import jssc.SerialPortList;

public class Propeller1Loader extends PropellerLoader {

    public static final int SHUTDOWN = 0;
    public static final int DOWNLOAD_RUN_BINARY = 1;
    public static final int DOWNLOAD_EEPROM = 2;
    public static final int DOWNLOAD_RUN_EEPROM = 3;
    public static final int DOWNLOAD_SHUTDOWN = 4;

    public static final int HTTP_PORT = 80;
    public static final int TELNET_PORT = 23;
    public static final int DISCOVER_PORT = 32420;

    public static final int CONNECT_TIMEOUT = 3000;
    public static final int RESPONSE_TIMEOUT = 3000;
    public static final int DISCOVER_REPLY_TIMEOUT = 250;
    public static final int DISCOVER_ATTEMPTS = 3;

    static final int DEF_LOADER_BAUDRATE = 115200;
    static final int DEF_FAST_LOADER_BAUDRATE = 921600;

    static final double SSSHTime = 0.0000006;
    static final double SCLHighTime = 0.0000006;
    static final double SCLLowTime = 0.0000013;

    static final int initCallFrame[] = {
        0xFF, 0xFF, 0xF9, 0xFF, 0xFF, 0xFF, 0xF9, 0xFF
    };

    int loaderBaudRate = DEF_LOADER_BAUDRATE;
    int fastLoaderBaudRate = DEF_LOADER_BAUDRATE;
    double floatClockSpeed = 80000000.0;

    ComPort comPort;
    byte LFSR;

    private boolean shared;

    public Propeller1Loader(ComPort serialPort, boolean shared) {
        this.comPort = serialPort;
        this.shared = shared;
    }

    public Propeller1Loader(ComPort serialPort, boolean shared, PropellerLoaderListener listener) {
        super(listener);
        this.comPort = serialPort;
        this.shared = shared;
    }

    public String getPortName() {
        return comPort.getPortName();
    }

    @Override
    public ComPort upload(byte[] binaryImage, int type, boolean discoverDevice) throws ComPortException {
        int version = 0;

        try {
            if (comPort instanceof NetworkComPort) {
                boolean valid = resolveNetworkPort((NetworkComPort) comPort);
                if (!valid) {
                    throw new ComPortException("Device " + comPort.getPortName() + " not found");
                }

                if (!comPort.isOpened()) {
                    comPort.openPort();
                }
                comPort.setParams(
                    SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

                bufferUpload((NetworkComPort) comPort, type, binaryImage, "binary image");
            }
            else {
                if (comPort != null) {
                    if (!comPort.isOpened()) {
                        comPort.openPort();
                    }
                    comPort.setParams(
                        SerialPort.BAUDRATE_115200,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
                }

                hwreset();
                version = hwfind();

                if (version == 0) {
                    if (comPort != null && !discoverDevice) {
                        throw new ComPortException("No propeller chip on port " + comPort.getPortName());
                    }
                    SerialComPort discoveredComPort = discover();
                    if (discoveredComPort == null) {
                        throw new ComPortException("No propeller chip found");
                    }
                    if (comPort != null) {
                        comPort.closePort();
                    }
                    comPort = discoveredComPort;
                }
                bufferUpload(type, binaryImage, "binary image");
            }
        } finally {
            if (comPort != null && !shared) {
                comPort.closePort();
            }
        }

        return comPort;
    }

    boolean resolveNetworkPort(NetworkComPort comPort) throws ComPortException {
        boolean valid = false;

        if (comPort.getInetAddr() != null) {
            try {
                DeviceDescriptor descr = NetworkUtils.probe(comPort.getInetAddr());
                if (descr != null && descr.mac_address.equalsIgnoreCase(comPort.getMacAddress())) {
                    valid = true;
                }
            } catch (IOException e) {
                throw new ComPortException(e.getMessage(), e);
            }
        }
        if (!valid) {
            Collection<DeviceDescriptor> list = NetworkUtils.getAvailableDevices();
            for (DeviceDescriptor descr : list) {
                if (descr.mac_address.equals(comPort.getMacAddress())) {
                    comPort.setInetAddr(descr.inetAddr);
                    try {
                        if (comPort.isOpened()) {
                            comPort.closePort();
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                for (DeviceDescriptor descr : list) {
                    if (descr.inetAddr.equals(comPort.getInetAddr())) {
                        comPort.setMacAddress(descr.mac_address);
                        valid = true;
                        break;
                    }
                }
            }
        }

        return valid;
    }

    protected SerialComPort discover() {
        ComPort currentComPort = comPort;
        String[] portNames = SerialPortList.getPortNames();

        for (int i = 0; i < portNames.length; i++) {
            if (comPort != null && portNames[i].equals(comPort.getPortName())) {
                continue;
            }
            SerialComPort serialComPort = new SerialComPort(portNames[i]);
            try {
                serialComPort.openPort();
                serialComPort.setParams(
                    SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

                try {
                    comPort = serialComPort;
                    hwreset();
                    if (hwfind() != 0) {
                        comPort = currentComPort;
                        return serialComPort;
                    }
                } catch (Exception e) {
                    // Do nothing
                }
                serialComPort.closePort();

            } catch (Exception e) {
                // Do nothing
            }
        }

        comPort = currentComPort;

        return null;
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
            if (comPort instanceof SerialComPort) {
                ((SerialComPort) comPort).getSerialPort().purgePort(SerialPort.PURGE_TXABORT |
                    SerialPort.PURGE_RXABORT |
                    SerialPort.PURGE_TXCLEAR |
                    SerialPort.PURGE_RXCLEAR);
            }
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

    protected void bufferUpload(NetworkComPort comPort, int type, byte[] binaryImage, String text) throws ComPortException {
        int rId;
        String body;

        byte[] loaderImage = new byte[LoaderImage.rawLoaderImage.length];
        System.arraycopy(LoaderImage.rawLoaderImage, 0, loaderImage, 0, loaderImage.length);

        int packetId = (binaryImage.length + NetworkComPort.MAX_DATA_SIZE - 1) / NetworkComPort.MAX_DATA_SIZE;
        int initAreaOffset = loaderImage.length + LoaderImage.RAW_LOADER_INIT_OFFSET_FROM_END;

        setLong(loaderImage, initAreaOffset + 4, (int) (floatClockSpeed / loaderBaudRate + 0.5)); // IBitTime
        setLong(loaderImage, initAreaOffset + 8, (int) (floatClockSpeed / fastLoaderBaudRate + 0.5)); // FBitTime
        setLong(loaderImage, initAreaOffset + 12, (int) (1.5 * floatClockSpeed / fastLoaderBaudRate - 23 + 0.5)); // BitTime1_5
        setLong(loaderImage, initAreaOffset + 16, (int) (2.0 * floatClockSpeed / (3 * 4) + 0.5)); // Failsafe
        setLong(loaderImage, initAreaOffset + 20, (int) (2.0 * floatClockSpeed / fastLoaderBaudRate * 10.0 / 12.0 + 0.5)); // EndOfPacket

        setLong(loaderImage, initAreaOffset + 36, packetId);

        byte sum = 0;
        loaderImage[5] = 0;
        for (int i = 0; i < loaderImage.length; i++) {
            sum += loaderImage[i];
        }
        loaderImage[5] = (byte) (0x14 - sum);

        int checksum = 0;
        for (int i = 0; i < binaryImage.length; i++) {
            checksum += binaryImage[i] & 0xFF;
        }
        for (int i = 0; i < initCallFrame.length; i++) {
            checksum += initCallFrame[i];
        }

        if (listener != null) {
            listener.bufferUpload(type, binaryImage, text);
        }

        try {
            StringBuilder sb = new StringBuilder(128);
            sb.append("http://");
            sb.append(comPort.getInetAddr().getHostAddress());
            sb.append("/propeller/load?baud-rate=115200");
            if (comPort.getResetPin() != null && !comPort.getResetPin().isBlank()) {
                sb.append("&reset-pin=" + comPort.getResetPin());
            }
            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(sb.toString())) //
                .POST(BodyPublishers.ofByteArray(loaderImage)) //
                .timeout(Duration.ofMillis(NetworkComPort.RESPONSE_TIMEOUT)) //
                .build();

            HttpResponse<String> httpResponse = comPort.getHttpClient().send(httpRequest, BodyHandlers.ofString());
            body = httpResponse.body();
        } catch (InterruptedException | URISyntaxException | IOException e) {
            throw new ComPortException("Second-stage loader delivery failed", e);
        }

        if (body != null && "OK\r\n".equals(body)) {
            int ofs = 0;
            int remaining = binaryImage.length;

            try {
                rId = comPort.readLong(2000);
                comPort.readLong(2000); // tag
            } catch (InterruptedException e) {
                throw new ComPortException("Second-stage loader start failed (timeout)");
            }
            if (rId != packetId) {
                throw new ComPortException("Second-stage loader start failed");
            }

            while (remaining > 0) {
                int len = 1024;
                if (len > remaining) {
                    len = remaining;
                }
                byte[] buffer = new byte[8 + len];
                System.arraycopy(binaryImage, ofs, buffer, 8, len);
                setLong(buffer, 0, packetId);

                try {
                    comPort.writeBytes(buffer);
                    rId = comPort.readLong(2000);
                    comPort.readLong(2000); // tag
                } catch (InterruptedException e) {
                    throw new ComPortException("Timeout waiting packet response");
                }

                packetId--;
                if (rId != packetId) {
                    throw new ComPortException(String.format("Unexpected packet response (expected %d, received %d)", packetId, rId));
                }

                ofs += len;
                remaining -= len;
                notifyProgress(ofs, binaryImage.length);
            }

            if (remaining == 0) {
                if (listener != null) {
                    listener.verifyRam();
                }
                byte[] buffer = new byte[8 + LoaderImage.verifyRAM.length];
                System.arraycopy(LoaderImage.verifyRAM, 0, buffer, 8, LoaderImage.verifyRAM.length);
                setLong(buffer, 0, packetId);
                comPort.writeBytes(buffer);

                try {
                    rId = comPort.readLong(2000);
                    comPort.readLong(2000); // tag
                } catch (InterruptedException e) {
                    throw new ComPortException("Ram verify failed (timeout)");
                }
                if (rId != -checksum) {
                    throw new ComPortException("Ram verify failed (checksum)");
                }
                packetId = -checksum;

                if ((type & Propeller1Loader.DOWNLOAD_EEPROM) != 0) {
                    if (listener != null) {
                        listener.eepromWrite();
                    }
                    buffer = new byte[8 + LoaderImage.programVerifyEEPROM.length];
                    System.arraycopy(LoaderImage.programVerifyEEPROM, 0, buffer, 8, LoaderImage.programVerifyEEPROM.length);
                    setLong(buffer, 0, packetId);
                    comPort.writeBytes(buffer);

                    try {
                        rId = comPort.readLong(8000);
                        comPort.readLong(2000); // tag
                    } catch (InterruptedException e) {
                        throw new ComPortException("EEprom verify failed (timed)");
                    }
                    if (rId != -checksum * 2) {
                        throw new ComPortException("EEprom verify failed (checksum)");
                    }
                    packetId = -checksum * 2;
                }

                buffer = new byte[8 + LoaderImage.readyToLaunch.length];
                System.arraycopy(LoaderImage.readyToLaunch, 0, buffer, 8, LoaderImage.readyToLaunch.length);
                setLong(buffer, 0, packetId);
                comPort.writeBytes(buffer);

                try {
                    rId = comPort.readLong(2000);
                    comPort.readLong(2000); // tag
                } catch (InterruptedException e) {
                    throw new ComPortException("Ready to launch failed (timeout)");
                }
                if (rId != packetId - 1) {
                    throw new ComPortException("Ready to launch failed (checksum)");
                }
                packetId--;

                buffer = new byte[8 + LoaderImage.launchNow.length];
                System.arraycopy(LoaderImage.launchNow, 0, buffer, 8, LoaderImage.launchNow.length);
                setLong(buffer, 0, packetId);
                comPort.writeBytes(buffer);
            }
        }
    }

    void setLong(byte[] data, int offset, int value) {
        data[offset + 0] = (byte) value;
        data[offset + 1] = (byte) (value >> 8);
        data[offset + 2] = (byte) (value >> 16);
        data[offset + 3] = (byte) (value >> 24);
    }

}
