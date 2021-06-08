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

    public static void dispose() {
        for (Color color : map.values()) {
            color.dispose();
        }
        map.clear();
    }
}
