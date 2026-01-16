/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.debug;

import java.util.ArrayList;
import java.util.List;

public class KeywordIterator {

    final String[] ar;
    int index;

    public KeywordIterator(String s) {
        int idx = 0, state = 0, start = 0;
        List<String> list = new ArrayList<>();

        if (idx < s.length() && s.charAt(idx) == '`') {
            idx++;
        }

        while (idx < s.length()) {
            char ch = s.charAt(idx);
            switch (state) {
                case 0:
                    if (ch == ',' || ch == ' ' || ch == '\t') {
                        break;
                    }
                    start = idx;
                    if (ch == '\'') {
                        state = 2;
                        break;
                    }
                    state = 1;
                    break;
                case 1:
                    if (ch == ',' || ch == ' ' || ch == '\t') {
                        list.add(s.substring(start, idx));
                        state = 0;
                        break;
                    }
                    break;
                case 2:
                    if (ch == '\'') {
                        list.add(s.substring(start, idx + 1));
                        state = 0;
                        break;
                    }
                    break;
            }
            idx++;
        }
        if (state != 0) {
            list.add(s.substring(start, idx));
        }

        ar = list.toArray(new String[list.size()]);
    }

    public boolean hasNext() {
        return index < ar.length;
    }

    public String next() {
        return ar[index++];
    }

    public String peekNext() {
        return ar[index];
    }

    public boolean hasNextString() {
        if (index >= ar.length) {
            return false;
        }
        return ar[index].startsWith("'") && ar[index].endsWith("'");
    }

    public String nextString() {
        String s = ar[index++];
        return s.substring(1, s.length() - 1);
    }

    public boolean hasNextNumber() {
        try {
            if (index < ar.length) {
                getNextNumber();
                return true;
            }
        } catch (Exception e) {
            // Do nothing
        }
        return false;
    }

    public int nextNumber() {
        try {
            int value = getNextNumber();
            index++;
            return value;
        } catch (Exception e) {
            return 0;
        }
    }

    int getNextNumber() {
        String s = ar[index].replace("_", "");
        if (s.startsWith("$")) {
            return (int) Long.parseLong(s.substring(1), 16);
        }
        if (s.startsWith("%%")) {
            return (int) Long.parseLong(s.substring(2), 4);
        }
        if (s.startsWith("%")) {
            return (int) Long.parseLong(s.substring(1), 2);
        }
        return (int) Long.parseLong(s, 10);
    }

    public void skip() {
        if (index < ar.length) {
            index++;
        }
    }

    public void back() {
        if (index > 0) {
            index--;
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
