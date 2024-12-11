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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Collection;

import com.maccasoft.propeller.devices.ComPort;
import com.maccasoft.propeller.devices.ComPortException;
import com.maccasoft.propeller.devices.DeviceDescriptor;
import com.maccasoft.propeller.devices.NetworkComPort;
import com.maccasoft.propeller.devices.NetworkUtils;
import com.maccasoft.propeller.devices.SerialComPort;
import com.maccasoft.propeller.internal.FileUtils;

import jssc.SerialPort;
import jssc.SerialPortList;

public class Propeller2Loader extends PropellerLoader {

    public static final int UPLOAD_BAUD_RATE = 2000000;

    public static final int DOWNLOAD_RUN_RAM = 0;
    public static final int DOWNLOAD_RUN_FLASH = 1;

    ComPort comPort;
    int portBaudRate = UPLOAD_BAUD_RATE;

    boolean shared;

    public Propeller2Loader(ComPort serialPort, boolean shared) {
        this.comPort = serialPort;
        this.shared = shared;
    }

    public Propeller2Loader(ComPort serialPort, boolean shared, PropellerLoaderListener listener) {
        super(listener);
        this.comPort = serialPort;
        this.shared = shared;
    }

    public String getPortName() {
        return comPort.getPortName();
    }

    public ComPort getSerialPort() {
        return comPort;
    }

    public int getPortBaudRate() {
        return portBaudRate;
    }

    public void setPortBaudRate(int portBaudRate) {
        this.portBaudRate = portBaudRate;
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

    public int find() throws ComPortException {
        int version = 0;

        if (!comPort.isOpened()) {
            comPort.openPort();
        }
        comPort.setParams(
            portBaudRate,
            SerialPort.DATABITS_8,
            SerialPort.STOPBITS_1,
            SerialPort.PARITY_NONE);

        try {
            comPort.hwreset();
            version = hwfind();
        } finally {
            if (!shared) {
                comPort.closePort();
            }
        }

        return version;
    }

    protected int hwfind() throws ComPortException {

        for (int i = 0; i < 3; i++) {
            comPort.hwreset();
            comPort.writeString("> \r");
            comPort.writeString("> Prop_Chk 0 0 0 0\r");

            try {
                String result = new String();
                while (result.length() < 11 || !result.endsWith("\r\n")) {
                    int b = comPort.readByteWithTimeout(200);
                    if (b == -1) {
                        break;
                    }
                    result += (char) b;
                }
                if (result.startsWith("\r\nProp_Ver ")) {
                    return 2;
                }
            } catch (ComPortException e) {
                return 0;
            }
        }

        return 0;
    }

    @Override
    public void upload(byte[] binaryImage, int type) throws ComPortException {
        if (comPort instanceof NetworkComPort) {
            boolean valid = resolveNetworkPort((NetworkComPort) comPort);
            if (!valid) {
                throw new ComPortException("Device " + comPort.getPortName() + " not found");
            }
        }

        if (!comPort.isOpened()) {
            comPort.openPort();
        }
        comPort.setParams(
            portBaudRate,
            SerialPort.DATABITS_8,
            SerialPort.STOPBITS_1,
            SerialPort.PARITY_NONE);

        try {
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
        int n;

        if (listener != null) {
            listener.bufferUpload(type, binaryImage, text);
        }

        if (type == DOWNLOAD_RUN_FLASH) {
            InputStream is = getClass().getResourceAsStream("flash_loader.binary");
            try {
                byte[] loader = new byte[is.available()];
                is.read(loader);

                byte[] loaderImage = new byte[loader.length + ((binaryImage.length + 3) & ~3)];
                System.arraycopy(loader, 0, loaderImage, 0, loader.length);
                System.arraycopy(binaryImage, 0, loaderImage, loader.length, binaryImage.length);
                binaryImage = loaderImage;

                binaryImage[8] = (byte) binaryImage.length;
                binaryImage[9] = (byte) (binaryImage.length >> 8);
                binaryImage[10] = (byte) (binaryImage.length >> 16);
                binaryImage[11] = (byte) (binaryImage.length >> 24);

                int sum = 0;
                for (n = 0; n < binaryImage.length; n += 4) {
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
                    sum -= data;
                }

                binaryImage[4] = (byte) sum;
                binaryImage[5] = (byte) (sum >> 8);
                binaryImage[6] = (byte) (sum >> 16);
                binaryImage[7] = (byte) (sum >> 24);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        base64Upload(binaryImage);
        verifyRam();

        if (type == DOWNLOAD_RUN_FLASH) {
            flashWrite();
        }
    }

    void hexUpload(byte[] binaryImage) throws ComPortException {
        int n;

        comPort.writeString("> Prop_Hex 0 0 0 0");

        int sum = 0;
        for (n = 0; n < binaryImage.length; n += 4) {
            if (n > 0 && (n % 64) == 0) {
                comPort.writeString("\r>");
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
            comPort.writeString(String.format(" %x %x %x %x", data & 0xFF, (data >> 8) & 0xFF, (data >> 16) & 0xFF, (data >> 24) & 0xFF));
            sum += data;
        }
        notifyProgress(n, binaryImage.length);

        sum = 0x706F7250 - sum;
        comPort.writeString(String.format(" %x %x %x %x ?", sum & 0xFF, (sum >> 8) & 0xFF, (sum >> 16) & 0xFF, (sum >> 24) & 0xFF));
    }

    void base64Upload(byte[] binaryImage) throws ComPortException {
        int n, sent;
        Encoder encoder = Base64.getEncoder();

        int sum = 0;
        for (n = 0; n < binaryImage.length; n += 4) {
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
            sum += data;
        }
        sum = 0x706F7250 - sum;

        byte[] image = new byte[(binaryImage.length + 7) & ~3];
        System.arraycopy(binaryImage, 0, image, 0, binaryImage.length);
        image[image.length - 4] = (byte) sum;
        image[image.length - 3] = (byte) (sum >> 8);
        image[image.length - 2] = (byte) (sum >> 16);
        image[image.length - 1] = (byte) (sum >> 24);
        binaryImage = image;

        comPort.writeString("> Prop_Txt 0 0 0 0");

        String encodedImage = encoder.encodeToString(binaryImage);
        for (n = 0, sent = 0; n < encodedImage.length(); n += 64, sent += 48) {
            comPort.writeString("\r> ");
            comPort.writeString(encodedImage.substring(n, n + Math.min(64, encodedImage.length() - n)));
            notifyProgress(sent, binaryImage.length);
        }
        notifyProgress(sent, binaryImage.length);

        comPort.writeString(" ?");
    }

    protected void notifyProgress(int sent, int total) {
        // Do nothing
    }

    protected void flashWrite() throws ComPortException {

        if (listener != null) {
            listener.eepromWrite();
        }

    }

    protected void verifyRam() throws ComPortException {

        if (listener != null) {
            listener.verifyRam();
        }

        int rc = comPort.readByteWithTimeout(10_000);

        if (rc == -1) {
            throw new ComPortException("Timeout");
        }

        if (rc != '.') {
            throw new ComPortException("Checksum error");
        }
    }

    public static void main(String[] args) {
        try {
            byte[] binaryImage = FileUtils.loadBinaryFromFile(new File("/home/marco/workspace/spin-tools-ide/examples/P2", "jm_i2c_devices.binary"));

            NetworkComPort comPort = new NetworkComPort(InetAddress.getByName("192.168.1.55"));
            Propeller2Loader loader = new Propeller2Loader(comPort, false);
            loader.upload(binaryImage, DOWNLOAD_RUN_RAM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
