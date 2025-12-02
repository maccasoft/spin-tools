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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;

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
        return splitArguments(args, !"win32".equals(SWT.getPlatform()));
    }

    public static String[] splitArguments(String args, boolean stripQuotes) {
        List<String> result = new ArrayList<>();
        int i = 0, nested = 0;

        StringBuilder sb = new StringBuilder();
        while (i < args.length()) {
            char ch = args.charAt(i++);
            if (ch == '\\') {
                if (i < args.length()) {
                    char ch1 = args.charAt(i);
                    if (ch1 == '"') {
                        sb.append(ch1);
                        i++;
                        continue;
                    }
                }
                sb.append(ch);
            }
            else if (nested == 0) {
                if (ch == ' ') {
                    if (sb.length() != 0) {
                        result.add(sb.toString());
                        sb = new StringBuilder();
                    }
                }
                else if (ch == '"' || ch == '\'') {
                    if (nested == 0 && !stripQuotes) {
                        sb.append(ch);
                    }
                    nested++;
                }
                else {
                    sb.append(ch);
                }
            }
            else {
                if (ch == '"' || ch == '\'') {
                    nested--;
                    if (nested == 0) {
                        if (!stripQuotes) {
                            sb.append(ch);
                        }
                        if (sb.length() != 0) {
                            result.add(sb.toString());
                            sb = new StringBuilder();
                        }
                    }
                }
                else {
                    sb.append(ch);
                }
            }
        }

        if (sb.length() != 0) {
            result.add(sb.toString());
        }

        return result.toArray(new String[result.size()]);
    }

    public boolean equals(String text1, String text2, boolean caseSensitive) {
        if (text1 != null) {
            return caseSensitive ? text1.equals(text2) : text1.equalsIgnoreCase(text2);
        }
        return (text1 == text2);
    }

}
