/*
 * Copyright (c) 2015-2016 Marco Maccaferri and others.
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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorRegistry {

    private static Map<RGB, Color> map = new HashMap<RGB, Color>();

    ColorRegistry() {
    }

    public static Color getColor(int r, int g, int b) {
        return getColor(new RGB(r, g, b));
    }

    public static Color getColor(RGB rgb) {
        Color result = map.get(rgb);
        if (result == null) {
            result = new Color(Display.getDefault(), rgb);
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

    public static void dispose() {
        for (Color color : map.values()) {
            color.dispose();
        }
        map.clear();
    }
}
