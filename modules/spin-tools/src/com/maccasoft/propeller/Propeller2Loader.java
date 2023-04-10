/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Base64.Encoder;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;

public class Propeller2Loader {

    public static final int UPLOAD_BAUD_RATE = 2000000;

    public static final int DOWNLOAD_RUN_RAM = 0;
    public static final int DOWNLOAD_RUN_FLASH = 1;

    SerialPort serialPort;
    int portBaudRate = UPLOAD_BAUD_RATE;

    boolean shared;

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

    public int getPortBaudRate() {
        return portBaudRate;
    }

    public void setPortBaudRate(int portBaudRate) {
        this.portBaudRate = portBaudRate;
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
            portBaudRate,
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
        msleep(25);
        serialPort.setDTR(false);
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
        }
        serialPort.setParams(
            portBaudRate,
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
            portBaudRate,
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

    void hexUpload(byte[] binaryImage) throws SerialPortException {
        int n;

        serialPort.writeString("> Prop_Hex 0 0 0 0");

        int sum = 0;
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
    }

    void base64Upload(byte[] binaryImage) throws SerialPortException {
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

        serialPort.writeString("> Prop_Txt 0 0 0 0");

        String encodedImage = encoder.encodeToString(binaryImage);
        for (n = 0, sent = 0; n < encodedImage.length(); n += 64, sent += 48) {
            serialPort.writeString("\r> ");
            serialPort.writeString(encodedImage.substring(n, n + Math.min(64, encodedImage.length() - n)));
            notifyProgress(sent, binaryImage.length);
        }
        notifyProgress(sent, binaryImage.length);

        serialPort.writeString(" ?");
    }

    protected void notifyProgress(int sent, int total) {
        // Do nothing
    }

    protected void flashWrite() throws SerialPortException, IOException {

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
