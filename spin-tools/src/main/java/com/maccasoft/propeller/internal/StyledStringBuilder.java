/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.TextStyle;

public class StyledStringBuilder {

    StringBuilder sb;
    List<StyleRange> styles;

    public StyledStringBuilder() {
        sb = new StringBuilder();
        styles = new ArrayList<StyleRange>();
    }

    public void append(String s) {
        sb.append(s);
    }

    public void append(String s, TextStyle style) {
        StyleRange range = new StyleRange(style);
        range.start = sb.length();
        range.length = s.length();

        sb.append(s);
        styles.add(range);
    }

    public void append(Object obj, TextStyle style) {
        append(obj.toString(), style);
    }

    public int length() {
        return sb.length();
    }

    public String getText() {
        return sb.toString();
    }

    public StyleRange[] getTextStyles() {
        return styles.toArray(new StyleRange[styles.size()]);
    }

}
