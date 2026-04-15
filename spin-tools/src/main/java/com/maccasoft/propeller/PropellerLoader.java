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

package com.maccasoft.propeller;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.maccasoft.propeller.devices.ComPort;
import com.maccasoft.propeller.devices.ComPortException;

public abstract class PropellerLoader {

    protected PropellerLoaderListener listener;

    ComPort.Control resetControl;
    Set<String> blacklistedPorts = new HashSet<>();

    public PropellerLoader(ComPort.Control resetControl) {
        this.resetControl = resetControl;
    }

    public PropellerLoader(PropellerLoaderListener listener) {
        this.listener = listener;
    }

    public void setListener(PropellerLoaderListener listener) {
        this.listener = listener;
    }

    public ComPort upload(byte[] binaryImage, int type) throws ComPortException {
        return upload(binaryImage, type, false);
    }

    public abstract ComPort upload(byte[] binaryImage, int type, boolean discoverDevice) throws ComPortException;

    public ComPort.Control getResetControl() {
        return resetControl;
    }

    public void setResetControl(ComPort.Control resetControl) {
        this.resetControl = resetControl;
    }

    public void setBlacklistedPorts(Collection<String> blacklistedPorts) {
        this.blacklistedPorts = new HashSet<>(blacklistedPorts);
    }

    protected boolean isBlacklisted(String portName) {
        return blacklistedPorts.contains(portName);
    }

}
