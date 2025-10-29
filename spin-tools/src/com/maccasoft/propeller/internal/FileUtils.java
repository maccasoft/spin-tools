/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
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
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    static final String EOL_PATTERN = "\\r\\n|\\r|\\n";

    public static File getAppDir() {
        String appDir = System.getenv("APP_DIR");
        if (appDir == null) {
            return new File("").getAbsoluteFile();
        }
        return new File(appDir).getAbsoluteFile();
    }

    public static String loadFromFile(File file) throws IOException {
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

        String text = new String(data).replaceAll(EOL_PATTERN, System.lineSeparator());

        return replaceTabs(text, 8);
    }

    public static String replaceTabs(String text, int tabs) {
        String spaces = " ".repeat(tabs);

        int i = 0;
        while ((i = text.indexOf('\t', i)) != -1) {
            int s = i;
            while (s > 0) {
                s--;
                if (text.charAt(s) == '\r' || text.charAt(s) == '\n') {
                    s++;
                    break;
                }
            }
            int n = ((i - s) % tabs);
            text = text.substring(0, i) + spaces.substring(0, tabs - n) + text.substring(i + 1);
        }

        return text;
    }

    public static byte[] loadBinaryFromFile(File file) throws Exception {
        InputStream is = new FileInputStream(file);
        try {
            byte[] b = new byte[is.available()];
            is.read(b);
            return b;
        } finally {
            try {
                is.close();
            } catch (Exception e) {

            }
        }
    }

    public static boolean isEditable(File file) {
        String name = file.getName().toLowerCase();

        if (name.endsWith(".spin") || name.endsWith(".p1asm")) {
            return true;
        }
        if (name.endsWith(".spin2") || name.endsWith(".p2asm")) {
            return true;
        }
        if (name.endsWith(".c")) {
            return true;
        }

        return false;
    }

}
