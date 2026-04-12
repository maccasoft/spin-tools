/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.preferences;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_DEFAULT)
public class RemoteDevice {

    public String name = "";
    public String ip = "";
    public String mac = "";
    public String reset_pin = "";

    public RemoteDevice() {

    }

    public RemoteDevice(RemoteDevice other) {
        this.name = other.name;
        this.ip = other.ip;
        this.mac = other.mac;
        this.reset_pin = other.reset_pin;
    }

    public RemoteDevice(String name, String ip, String mac, String reset_pin) {
        this.name = name;
        this.ip = ip;
        this.mac = mac;
        this.reset_pin = reset_pin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getResetPin() {
        return reset_pin;
    }

    public void setResetPin(String reset_pin) {
        this.reset_pin = reset_pin;
    }

    public String getPortName() {
        if (mac != null && !mac.isBlank()) {
            return mac;
        }
        if (ip != null && !ip.isBlank()) {
            return ip;
        }
        return "";
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, mac, name, reset_pin);
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
        RemoteDevice other = (RemoteDevice) obj;
        return Objects.equals(ip, other.ip) && Objects.equals(mac, other.mac) && Objects.equals(name, other.name) && Objects.equals(reset_pin, other.reset_pin);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" (");
        if (ip != null && !ip.isBlank()) {
            sb.append("ip=");
            sb.append(ip);
        }
        if (mac != null && !mac.isBlank()) {
            if (ip != null && !ip.isBlank()) {
                sb.append(", ");
            }
            sb.append("mac=");
            sb.append(mac);
        }
        sb.append(")");
        return sb.toString();
    }

}
