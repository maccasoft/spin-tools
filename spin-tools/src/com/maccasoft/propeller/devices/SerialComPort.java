/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller.devices;

import org.eclipse.swt.internal.Platform;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class SerialComPort extends ComPort {

    final SerialPort serialPort;

    public SerialComPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public SerialComPort(String portName) {
        this.serialPort = new SerialPort(portName);
    }

    @Override
    public String getName() {
        return serialPort.getPortName();
    }

    @Override
    public String getDescription() {
        return serialPort.getPortName();
    }

    @Override
    public String getPortName() {
        return serialPort.getPortName();
    }

    @Override
    public boolean isOpened() {
        return serialPort.isOpened();
    }

    @Override
    public boolean openPort() throws ComPortException {
        try {
            return serialPort.openPort();
        } catch (SerialPortException e) {
            throw new ComPortException(e.getExceptionType(), e);
        }
    }

    @Override
    public boolean setParams(int baudRate, int dataBits, int stopBits, int parity) throws ComPortException {
        try {
            boolean lineState = "win32".equals(Platform.PLATFORM) ? false : true;
            return serialPort.setParams(baudRate, dataBits, stopBits, parity, lineState, lineState);
        } catch (SerialPortException e) {
            throw new ComPortException(e.getExceptionType(), e);
        }
    }

    @Override
    public void closePort() {
        try {
            serialPort.closePort();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hwreset(Control control, int delay) {
        try {
            if (control == Control.DtrRts || control == Control.Dtr) {
                serialPort.setDTR(true);
            }
            if (control == Control.DtrRts || control == Control.Rts) {
                serialPort.setRTS(true);
            }
            Thread.sleep(5);
            if (control == Control.DtrRts || control == Control.Dtr) {
                serialPort.setDTR(false);
            }
            if (control == Control.DtrRts || control == Control.Rts) {
                serialPort.setRTS(false);
            }
            if (delay != 0) {
                Thread.sleep(delay);
            }
            serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
        } catch (Exception e) {
            // Do nothing
        }
    }

    @Override
    public int readByteWithTimeout(int timeout) throws ComPortException {
        int[] rx;
        try {
            rx = serialPort.readIntArray(1, timeout);
            return rx[0];
        } catch (SerialPortTimeoutException e) {
            // Do nothing;
        } catch (SerialPortException e) {
            throw new ComPortException(e.getExceptionType(), e);
        }
        return -1;
    }

    @Override
    public boolean writeInt(int singleInt) throws ComPortException {
        try {
            return serialPort.writeInt(singleInt);
        } catch (SerialPortException e) {
            throw new ComPortException(e.getExceptionType(), e);
        }
    }

    @Override
    public boolean writeByte(byte singleByte) throws ComPortException {
        try {
            return serialPort.writeByte(singleByte);
        } catch (SerialPortException e) {
            throw new ComPortException(e.getExceptionType(), e);
        }
    }

    @Override
    public boolean writeBytes(byte[] buffer) throws ComPortException {
        try {
            return serialPort.writeBytes(buffer);
        } catch (SerialPortException e) {
            throw new ComPortException(e.getExceptionType(), e);
        }
    }

    @Override
    public byte[] readBytes() throws ComPortException {
        try {
            return serialPort.readBytes();
        } catch (SerialPortException e) {
            throw new ComPortException(e.getExceptionType(), e);
        }
    }

    public byte[] readBytes(int byteCount, int timeout) throws ComPortException, SerialPortTimeoutException {
        try {
            return serialPort.readBytes(byteCount, timeout);
        } catch (SerialPortException e) {
            throw new ComPortException(e.getExceptionType(), e);
        }
    }

    @Override
    public void setEventListener(ComPortEventListener listener) throws ComPortException {
        try {
            SerialPortEventListener serialEventListener = new SerialPortEventListener() {

                @Override
                public void serialEvent(SerialPortEvent serialPortEvent) {
                    ComPortEvent event = new ComPortEvent(SerialComPort.this, serialPortEvent.isRXCHAR(), serialPortEvent.isCTS(), serialPortEvent.isDSR());
                    listener.serialEvent(event);
                }

            };
            serialPort.addEventListener(serialEventListener, SerialPort.MASK_RXCHAR | SerialPort.MASK_CTS | SerialPort.MASK_DSR);
        } catch (SerialPortException e) {
            throw new ComPortException(e.getExceptionType(), e);
        }
    }

    @Override
    public void removeEventListener() throws ComPortException {
        try {
            serialPort.removeEventListener();
        } catch (SerialPortException e) {
            throw new ComPortException(e.getExceptionType(), e);
        }
    }

    @Override
    public void setRTS(boolean enable) throws ComPortException {
        try {
            serialPort.setRTS(enable);
        } catch (SerialPortException e) {
            throw new ComPortException(e.getExceptionType(), e);
        }
    }

    @Override
    public void setDTR(boolean enable) throws ComPortException {
        try {
            serialPort.setDTR(enable);
        } catch (SerialPortException e) {
            throw new ComPortException(e.getExceptionType(), e);
        }
    }

    @Override
    public boolean isCTS() throws ComPortException {
        try {
            return serialPort.isCTS();
        } catch (SerialPortException e) {
            throw new ComPortException(e.getExceptionType(), e);
        }
    }

    @Override
    public boolean isDSR() throws ComPortException {
        try {
            return serialPort.isDSR();
        } catch (SerialPortException e) {
            throw new ComPortException(e.getExceptionType(), e);
        }
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    @Override
    public boolean writeString(String string) throws ComPortException {
        return writeBytes(string.getBytes());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SerialComPort)) {
            return false;
        }
        SerialComPort other = (SerialComPort) o;
        return serialPort.getPortName().equals(other.serialPort.getPortName());
    }

}
