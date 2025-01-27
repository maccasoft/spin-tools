/*
 * Copyright (c) 2025 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class FirmwarePack {

    @JsonInclude(Include.ALWAYS)
    List<Firmware> firmwareList;

    @JsonInclude(Include.ALWAYS)
    boolean enableLocal;
    @JsonInclude(Include.ALWAYS)
    boolean enableNetwork;

    public FirmwarePack() {
        enableLocal = true;
        firmwareList = new ArrayList<>();
    }

    public boolean isEnableLocal() {
        return enableLocal;
    }

    public void setEnableLocal(boolean enableLocal) {
        this.enableLocal = enableLocal;
    }

    public boolean isEnableNetwork() {
        return enableNetwork;
    }

    public void setEnableNetwork(boolean enableNetwork) {
        this.enableNetwork = enableNetwork;
    }

    public List<Firmware> getFirmwareList() {
        return firmwareList;
    }

    public void addFirmware(Firmware firmware) {
        this.firmwareList.add(firmware);
    }

    public void removeFirmware(Firmware firmware) {
        this.firmwareList.remove(firmware);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enableLocal, enableNetwork, firmwareList);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FirmwarePack other = (FirmwarePack) obj;
        return enableLocal == other.enableLocal && enableNetwork == other.enableNetwork && Objects.equals(firmwareList, other.firmwareList);
    }

}
