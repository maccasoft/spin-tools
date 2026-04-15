/*
 * Copyright (c) 26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SpinCompilerTest {

    @Test
    void testIPAddressPattern() {
        Assertions.assertTrue(Pattern.matches(SpinCompiler.ipAddressPattern, "192.168.1.55"));
        Assertions.assertFalse(Pattern.matches(SpinCompiler.ipAddressPattern, "18:fe:34:f9:ed:dd"));

        Assertions.assertFalse(Pattern.matches(SpinCompiler.ipAddressPattern, "192.168.1"));
        Assertions.assertFalse(Pattern.matches(SpinCompiler.ipAddressPattern, "192.168"));
        Assertions.assertFalse(Pattern.matches(SpinCompiler.ipAddressPattern, "192"));

        Assertions.assertFalse(Pattern.matches(SpinCompiler.ipAddressPattern, "COM3"));
        Assertions.assertFalse(Pattern.matches(SpinCompiler.ipAddressPattern, "/dev/ttyUSB0"));
        Assertions.assertFalse(Pattern.matches(SpinCompiler.ipAddressPattern, "/dev/tty.usbserial-P8fl2in6"));
    }

    @Test
    void testMACAddressPattern() {
        Assertions.assertFalse(Pattern.matches(SpinCompiler.macAddressPattern, "192.168.1.55"));
        Assertions.assertTrue(Pattern.matches(SpinCompiler.macAddressPattern, "18:fe:34:f9:ed:dd"));

        Assertions.assertFalse(Pattern.matches(SpinCompiler.macAddressPattern, "18:fe:34:f9:ed"));
        Assertions.assertFalse(Pattern.matches(SpinCompiler.macAddressPattern, "18:fe:34:f9"));
        Assertions.assertFalse(Pattern.matches(SpinCompiler.macAddressPattern, "18:fe:34"));
        Assertions.assertFalse(Pattern.matches(SpinCompiler.macAddressPattern, "18:fe"));
        Assertions.assertFalse(Pattern.matches(SpinCompiler.macAddressPattern, "18"));

        Assertions.assertFalse(Pattern.matches(SpinCompiler.macAddressPattern, "COM3"));
        Assertions.assertFalse(Pattern.matches(SpinCompiler.macAddressPattern, "/dev/ttyUSB0"));
        Assertions.assertFalse(Pattern.matches(SpinCompiler.macAddressPattern, "/dev/tty.usbserial-P8fl2in6"));
    }

    public static void main(String[] args) {
        String codice =
            "/* #pragma target P1 */ \n" +               // Commento multilinea (Ignora)
                "// #pragma target P1 \n" +                 // Commento linea (Ignora)
                "printf(\"Testo #pragma target P1\"); \n" + // Stringa letterale (Ignora)
                "#pragma target P3";                        // VERO TARGET

        // Il target è nel Gruppo 2
        String regex = "/\\*[\\s\\S]*?\\*/|//.*|\"([^\"\\\\]|\\\\.)*\"|(#pragma\\s+target\\s+P1|P2)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(codice);

        while (matcher.find()) {
            // Controlliamo se il Gruppo 2 ha trovato qualcosa
            if (matcher.group(2) != null) {
                System.out.println("Vera occorrenza trovata a: " + matcher.start(2));
                break;
            }
        }
    }

}
