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

import java.util.ArrayList;
import java.util.List;

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

    public static String[] splitArguments(String args) {
        List<String> result = new ArrayList<>();
        int s = 0, e, nested = 0;

        while (s < args.length()) {
            if (args.charAt(s) == ' ') {
                s++;
                continue;
            }
            e = s;
            while (e < args.length()) {
                if (nested == 0) {
                    if (args.charAt(e) == ' ') {
                        break;
                    }
                    if (args.charAt(e) == '"' || args.charAt(e) == '\'') {
                        nested++;
                    }
                }
                else if (args.charAt(e) == '"' || args.charAt(e) == '\'') {
                    nested--;
                }
                e++;
            }
            String str = args.substring(s, e);
            if (str.startsWith("\"") && str.endsWith("\"")) {
                str = str.substring(1, str.length() - 1);
            }
            result.add(str);
            s = e + 1;
        }

        return result.toArray(new String[result.size()]);
    }

}
