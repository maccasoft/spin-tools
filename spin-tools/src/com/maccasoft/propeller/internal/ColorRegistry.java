/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorRegistry {

    public static final String LIST_BACKGROUND = "LIST_BACKGROUND";
    public static final String LIST_FOREGROUND = "LIST_FOREGROUND";
    public static final String WIDGET_BACKGROUND = "WIDGET_BACKGROUND";
    public static final String WIDGET_FOREGROUND = "WIDGET_FOREGROUND";

    private static Map<String, Color> colorMap = new HashMap<>();
    private static Map<RGB, Color> map = new HashMap<>();

    ColorRegistry() {

    }

    public static void initSystemDefaults() {
        colorMap.put(LIST_BACKGROUND, Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        colorMap.put(LIST_FOREGROUND, Display.getDefault().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
        colorMap.put(WIDGET_BACKGROUND, Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        colorMap.put(WIDGET_FOREGROUND, Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
    }

    public static Color getColor(String key) {
        return colorMap.get(key);
    }

    public static Color getColor(int r, int g, int b) {
        return getColor(new RGB(r, g, b));
    }

    public static Color getColor(int r, int g, int b, int percent) {
        r += (int) (r / 100.0 * percent);
        g += (int) (g / 100.0 * percent);
        b += (int) (b / 100.0 * percent);
        return getColor(new RGB(r, g, b));
    }

    public static Color getColor(RGB rgb) {
        Color result = map.get(rgb);
        if (result == null) {
            result = new Color(rgb);
            map.put(rgb, result);
        }
        return result;
    }

    public static Color getDimColor(Color color, int percent) {
        int r = color.getRed() + (int) (color.getRed() / 100.0 * percent);
        if (r < 0) {
            r = 0;
        }
        if (r > 255) {
            r = 255;
        }
        int g = color.getGreen() + (int) (color.getGreen() / 100.0 * percent);
        if (g < 0) {
            g = 0;
        }
        if (g > 255) {
            g = 255;
        }
        int b = color.getBlue() + (int) (color.getBlue() / 100.0 * percent);
        if (b < 0) {
            b = 0;
        }
        if (b > 255) {
            b = 255;
        }
        return getColor(r, g, b);
    }

}
