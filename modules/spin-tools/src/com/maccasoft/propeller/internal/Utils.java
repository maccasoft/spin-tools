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

package com.maccasoft.propeller.internal;

public class Utils {

    public static String makeSkipPattern(String text) {
        StringBuilder pattern = new StringBuilder();

        String[] s = text.split(FileUtils.EOL_PATTERN);

        char target = 0;
        for (int i = 0; i < s.length; i++) {
            if (s[i].length() < 1) {
                if (pattern.length() == 0 || pattern.charAt(0) != '_') {
                    pattern.insert(0, '_');
                }
                continue;
            }
            char ch = s[i].charAt(0);
            if (ch == '-' || ch == '_') {
                pattern.insert(0, ch);
            }
            else if (ch == '|') {
                pattern.insert(0, '1');
            }
            else {
                if (target == 0) {
                    if (ch == ' ') {
                        continue;
                    }
                    target = ch;
                }
                if (ch == target) {
                    pattern.insert(0, '0');
                }
                else {
                    if (pattern.length() == 0 || pattern.charAt(0) != '_') {
                        pattern.insert(0, '_');
                    }
                }
            }
        }

        while (pattern.length() >= 1 && pattern.charAt(0) == '_') {
            pattern.deleteCharAt(0);
        }

        if (pattern.length() > 0) {
            pattern.insert(0, '%');
        }

        return pattern.toString();
    }

}
