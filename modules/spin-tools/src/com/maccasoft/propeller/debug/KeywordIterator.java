/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
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
        int idx = 0, state = 0;
        List<String> list = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        while (idx < s.length()) {
            char ch = s.charAt(idx++);
            switch (state) {
                case 0:
                    if (ch == ',' || ch == ' ' || ch == '\t') {
                        break;
                    }
                    sb.append(ch);
                    if (ch == '\'') {
                        state = 2;
                        break;
                    }
                    state = 1;
                    break;
                case 1:
                    if (ch == ',' || ch == ' ' || ch == '\t') {
                        list.add(sb.toString());
                        sb = new StringBuilder();
                        state = 0;
                        break;
                    }
                    sb.append(ch);
                    break;
                case 2:
                    sb.append(ch);
                    if (ch == '\'') {
                        list.add(sb.toString());
                        sb = new StringBuilder();
                        state = 0;
                        break;
                    }
                    break;
            }
        }
        if (sb.length() != 0) {
            list.add(sb.toString());
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
        if (index < ar.length) {
            char ch = ar[index].charAt(0);
            return (ch == '$' || ch == '%' || ch == '-' || ch == '+' || Character.isDigit(ch));
        }
        return false;
    }

    public int nextNumber() {
        String s = ar[index++].replace("_", "");
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
