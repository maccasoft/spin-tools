/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Spin2InstructionHelp {

    private static final String BUNDLE_NAME = Spin2InstructionHelp.class.getName();
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private static final int MAX_RECURSION = 5;
    private static Pattern pattern = Pattern.compile("\\$\\{([\\w\\.\\-]+)\\}");

    public static String getString(String key) {
        try {
            return translateMessage(key, MAX_RECURSION);
        } catch (MissingResourceException e) {
            return null;
        }
    }

    private static String translateMessage(String key, int iteration) {
        String message = RESOURCE_BUNDLE.getString(key);
        if (message != null) {
            StringBuffer sb = new StringBuffer();
            Matcher matcher = pattern.matcher(message);
            while (matcher.find() && iteration > 0) {
                // the magic
                matcher.appendReplacement(sb, translateMessage(matcher.group(1), iteration - 1));
            }
            matcher.appendTail(sb);
            return sb.toString();
        }
        return null;
    }

    public static String[] getKeys() {
        List<String> l = new ArrayList<String>();
        Enumeration<String> e = RESOURCE_BUNDLE.getKeys();
        while (e.hasMoreElements()) {
            l.add(e.nextElement());
        }
        return l.toArray(new String[l.size()]);
    }
}
