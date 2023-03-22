/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CModel {

    public static Set<String> types = new HashSet<String>(Arrays.asList(new String[] {
        "INT", "SHORT", "LONG", "WORD", "BYTE",
    }));

    public static boolean isType(String token) {
        return types.contains(token.toUpperCase());
    }

    public static String getSpinType(String token) {
        switch (token.toUpperCase()) {
            case "INT":
                return "LONG";
            case "SHORT":
                return "WORD";
            default:
                return token.toUpperCase();
        }
    }

}
