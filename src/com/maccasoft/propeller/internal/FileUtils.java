/*
 * Copyright (c) 2022 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.internal;

import java.io.File;
import java.io.FileInputStream;

public class FileUtils {

    public static String loadFromFile(File file) throws Exception {
        FileInputStream is = new FileInputStream(file);
        byte[] data = new byte[is.available()];
        is.read(data);
        is.close();

        if ((data[0] == (byte) 0xFF && data[1] == (byte) 0xFE && data[2] == 0x00 && data[3] == 0x00) || (data[0] == 0x00 && data[1] == 0x00 && data[2] == (byte) 0xFE && data[3] == (byte) 0xFF)) {
            String s = new String(data, "UTF-32");
            data = s.getBytes("UTF-8");
        }
        if ((data[0] == (byte) 0xFF && data[1] == (byte) 0xFE) || (data[0] == (byte) 0xFE && data[1] == (byte) 0xFF)) {
            String s = new String(data, "UTF-16");
            data = s.getBytes("UTF-8");
        }

        return new String(data).replace("\r\n", "\n").replace("\r", "\n");
    }

}
