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
public class ConsolePreferences {

    public ConsolePreferences() {
        maxLines = 500;
        writeLogFile = true;
    }

    public String font;
    public int maxLines;
    public boolean writeLogFile;
    public boolean resetDeviceOnClose;
    public boolean hideBacktickCommands;

    @Override
    public int hashCode() {
        return Objects.hash(font, maxLines, writeLogFile, resetDeviceOnClose);
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
        ConsolePreferences other = (ConsolePreferences) obj;
        return Objects.equals(font, other.font) && maxLines == other.maxLines && writeLogFile == other.writeLogFile && resetDeviceOnClose == other.resetDeviceOnClose;
    }

}
