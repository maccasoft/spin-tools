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

package com.maccasoft.propeller.devices;

public class ComPortEvent {

    final ComPort comPort;

    final boolean rxchar;
    final boolean cts;
    final boolean dsr;

    public ComPortEvent(ComPort comPort, boolean rxchar, boolean cts, boolean dsr) {
        this.comPort = comPort;
        this.rxchar = rxchar;
        this.cts = cts;
        this.dsr = dsr;
    }

    public boolean isRXCHAR() {
        return rxchar;
    }

    public boolean isCTS() {
        return cts;
    }

    public boolean isDSR() {
        return dsr;
    }

    public ComPort getComPort() {
        return comPort;
    }

}
