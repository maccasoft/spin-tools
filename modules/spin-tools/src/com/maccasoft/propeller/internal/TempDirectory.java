/*
 * Copyright (c) 2016 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller.internal;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.SecureRandom;

public class TempDirectory {

    private static final String prefix = "spin_tools_";

    // file name generation
    private static final SecureRandom random;

    // temporary directory location
    private static File systemTmpdir = new File(System.getProperty("java.io.tmpdir"));
    private static File tmpdir;

    private TempDirectory() {
    }

    static {
        random = new SecureRandom();
        try {
            tmpdir = generateFile(prefix, "", systemTmpdir);
        } catch (Exception e) {
            e.printStackTrace();
            tmpdir = systemTmpdir;
        }
    }

    public static File location() {
        return tmpdir;
    }

    public static File generateFile(String prefix, String suffix, File dir) throws IOException {
        long n = random.nextLong();
        if (n == Long.MIN_VALUE) {
            n = 0; // corner case
        }
        else {
            n = Math.abs(n);
        }

        // Use only the file name from the supplied prefix
        prefix = new File(prefix).getName();

        String name = prefix + Long.toString(n) + suffix;
        File f = new File(dir, name);
        if (!name.equals(f.getName())) {
            throw new IOException("Unable to create temporary file, " + f);
        }
        return f;
    }

    public static void clean() {
        if (!tmpdir.exists() || !tmpdir.getName().startsWith(prefix) || tmpdir.equals(systemTmpdir)) {
            return;
        }
        clean(tmpdir);
        tmpdir.delete();
    }

    private static void clean(File folder) {
        String[] files = folder.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return !"..".equals(name) && !".".equals(name);
            }
        });
        for (int i = 0; i < files.length; i++) {
            File f = new File(folder, files[i]);
            if (f.isDirectory()) {
                clean(f);
            }
            f.delete();
        }
    }
}
