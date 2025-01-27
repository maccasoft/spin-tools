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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class Firmware {

    public static final String DEFAULT_P1_TEXT = "P8X32A Firmware";
    public static final String DEFAULT_P2_TEXT = "P2X8C4M64P Rev B/C Firmware";

    @JsonInclude(Include.ALWAYS)
    String description;

    @JsonInclude(Include.ALWAYS)
    int binaryVersion;

    @JsonInclude(Include.ALWAYS)
    byte[] binaryImage;

    public static Firmware fromFile(File file) throws IOException {

        InputStream is = new FileInputStream(file);
        try {
            byte[] binaryImage = new byte[is.available()];
            is.read(binaryImage);

            byte checksum = 0;
            for (int i = 0; i < binaryImage.length; i++) {
                checksum += binaryImage[i];
            }
            int binaryVersion = checksum == 0x14 ? 1 : 2;

            String description = (binaryVersion == 1 ? DEFAULT_P1_TEXT : DEFAULT_P2_TEXT);

            return new Firmware(binaryVersion, binaryImage, description);

        } finally {
            try {
                is.close();
            } catch (Exception e) {

            }
        }
    }

    public Firmware() {

    }

    public Firmware(int binaryVersion, byte[] binaryImage, String description) {
        this.binaryVersion = binaryVersion;
        this.binaryImage = binaryImage;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBinaryVersion() {
        return binaryVersion;
    }

    public void setBinaryVersion(int binaryVersion) {
        this.binaryVersion = binaryVersion;
    }

    public byte[] getBinaryImage() {
        return binaryImage;
    }

    public void setBinaryImage(byte[] binaryImage) {
        this.binaryImage = binaryImage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(binaryImage);
        result = prime * result + Objects.hash(binaryVersion, description);
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
        Firmware other = (Firmware) obj;
        return Arrays.equals(binaryImage, other.binaryImage) && binaryVersion == other.binaryVersion && Objects.equals(description, other.description);
    }

    @Override
    public String toString() {
        return "Firmware [binaryVersion=" + binaryVersion + ", description=" + description + "]";
    }

}