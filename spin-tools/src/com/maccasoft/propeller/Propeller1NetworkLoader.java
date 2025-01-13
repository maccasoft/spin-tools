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
import java.util.Iterator;

import com.maccasoft.propeller.devices.ComPort;
import com.maccasoft.propeller.devices.ComPortException;
import com.maccasoft.propeller.devices.DeviceDescriptor;
import com.maccasoft.propeller.devices.NetworkComPort;
import com.maccasoft.propeller.devices.NetworkUtils;

public class Propeller1NetworkLoader extends PropellerLoader {

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

    NetworkComPort comPort;
    boolean shared;

    int loaderBaudRate = DEF_LOADER_BAUDRATE;
    int fastLoaderBaudRate = DEF_LOADER_BAUDRATE;
    double floatClockSpeed = 80000000.0;

    public Propeller1NetworkLoader(NetworkComPort comDevice, boolean shared) {
        this.comPort = comDevice;
        this.shared = shared;
    }

    public Propeller1NetworkLoader(NetworkComPort serialPort, boolean shared, PropellerLoaderListener listener) {
        super(listener);
        this.comPort = serialPort;
        this.shared = shared;
    }

    public ComPort detect() {
        if (comPort != null) {
            try {
                DeviceDescriptor descr = NetworkUtils.probe(comPort.getInetAddr());
                if (descr != null && descr.mac_address.equalsIgnoreCase(comPort.getMacAddress())) {
                    return comPort;
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Do nothing
            }
        }

        Collection<DeviceDescriptor> list = NetworkUtils.getAvailableDevices();
        if (comPort != null) {
            for (DeviceDescriptor descr : list) {
                if (descr.mac_address.equals(comPort.getMacAddress())) {
                    if (descr.inetAddr.equals(comPort.getInetAddr())) {
                        comPort.setResetPin(descr.reset_pin);
                        return comPort;
                    }
                    comPort = new NetworkComPort(descr);
                }
            }
        }
        else {
            Iterator<DeviceDescriptor> iter = list.iterator();
            if (iter.hasNext()) {
                comPort = new NetworkComPort(iter.next());
            }
        }

        return comPort;
    }

    @Override
    public ComPort upload(byte[] binaryImage, int type, boolean discoverDevice) throws ComPortException {
        boolean valid = resolveNetworkPort(comPort);
        if (!valid) {
            throw new ComPortException("Device " + comPort.getPortName() + " not found");
        }

        if (!comPort.isOpened()) {
            comPort.openPort();
        }

        try {
            bufferUpload(type, binaryImage, "binary image");
        } finally {
            if (!shared) {
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

    protected void bufferUpload(int type, byte[] binaryImage, String text) throws ComPortException {
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

    protected void notifyProgress(int sent, int total) {
        // Do nothing
    }

}
