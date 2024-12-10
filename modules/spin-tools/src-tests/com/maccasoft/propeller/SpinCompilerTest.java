package com.maccasoft.propeller;

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

}
