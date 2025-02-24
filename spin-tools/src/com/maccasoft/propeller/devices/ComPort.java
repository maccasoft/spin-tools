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

public abstract class ComPort {

    public static final int DEFAULT_RESET_DELAY = 100;

    public static final int P1_RESET_DELAY = 90;
    public static final int P2_RESET_DELAY = 15;

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getPortName();

    public abstract boolean isOpened();

    public abstract boolean openPort() throws ComPortException;

    public abstract boolean setParams(int baudRate, int dataBits, int stopBits, int parity) throws ComPortException;

    public abstract void closePort();

    public void hwreset() {
        hwreset(DEFAULT_RESET_DELAY);
    }

    public abstract void hwreset(int delay);

    public abstract int readByteWithTimeout(int timeout) throws ComPortException;

    public abstract boolean writeInt(int singleInt) throws ComPortException;

    public abstract boolean writeByte(byte singleByte) throws ComPortException;

    public abstract boolean writeBytes(byte[] buffer) throws ComPortException;

    public abstract boolean writeString(String string) throws ComPortException;

    public abstract byte[] readBytes() throws ComPortException;

    public abstract void setEventListener(ComPortEventListener listener) throws ComPortException;

    public abstract void removeEventListener() throws ComPortException;

    public abstract void setRTS(boolean enable) throws ComPortException;

    public abstract void setDTR(boolean enable) throws ComPortException;

    public abstract boolean isCTS() throws ComPortException;

    public abstract boolean isDSR() throws ComPortException;

}
