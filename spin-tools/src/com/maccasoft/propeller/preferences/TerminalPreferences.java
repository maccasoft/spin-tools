/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.preferences;

import java.util.Arrays;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.maccasoft.propeller.Preferences;

@JsonInclude(Include.NON_DEFAULT)
public class TerminalPreferences {

    public Bounds window;
    public boolean lineInput;
    public boolean localEcho;
    public String[] history;
    public String type;
    public String font;
    public int baudRate;
    public int cursor;

    public TerminalPreferences() {
        lineInput = true;
        localEcho = false;
        type = "pst";
        baudRate = 115200;
        cursor = Preferences.CURSOR_ON | Preferences.CURSOR_FLASH | Preferences.CURSOR_ULINE;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(history);
        result = prime * result + Objects.hash(baudRate, font, lineInput, localEcho, type, window, cursor);
        return result;
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
        TerminalPreferences other = (TerminalPreferences) obj;
        return baudRate == other.baudRate && Objects.equals(font, other.font) && Arrays.equals(history, other.history) && lineInput == other.lineInput && localEcho == other.localEcho
            && type == other.type && Objects.equals(window, other.window);
    }

}
